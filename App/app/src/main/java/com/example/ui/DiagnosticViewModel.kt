package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.DiagnosticCenter
import com.example.data.repository.DiagnosticRepository
import com.example.data.api.GeminiManager
import com.example.BuildConfig
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class SortMode {
    BEST_MATCH,
    DISTANCE,
    PRICE_LOW_TO_HIGH,
    HI_RATING,
    SHORT_WAIT,
    TRAVEL_TIME
}

enum class AppMode {
    DIAGNOSTIC,
    PHARMACY,
    DOCTOR
}

/**
 * Inferred pharmacy size tiers. These are HEURISTICS derived from the name,
 * review volume and distance to the nearest hospital — no map API returns a
 * pharmacy's real size or stock, so the UI always labels these "inferred".
 */
object PharmacyTier {
    const val SMALL = "Small"
    const val MID = "Mid-sized"
    const val BRAND = "Brand / chain"
    const val HOSPITAL = "Hospital pharmacy"

    /** Rough breadth-of-stock signal (0..100) used by pharmacy ranking. */
    fun breadth(tier: String): Double = when (tier) {
        HOSPITAL -> 100.0
        BRAND -> 85.0
        MID -> 60.0
        else -> 35.0
    }

    /** Projected chance the searched medicines are in stock (0..100). */
    fun projectedAvailability(tier: String): Int = breadth(tier).toInt()
}

enum class DoctorSpecialty(val label: String) {
    GENERAL("General"),
    CARDIOLOGIST("Cardiologist"),
    DERMATOLOGIST("Dermatologist"),
    ORTHOPEDIC("Orthopedic"),
    ENT("ENT"),
    GYNECOLOGIST("Gynecologist"),
    PEDIATRICIAN("Pediatrician"),
    NEUROLOGIST("Neurologist"),
    PSYCHIATRIST("Psychiatrist"),
    OPHTHALMOLOGIST("Ophthalmologist"),
    DENTIST("Dentist"),
    UROLOGIST("Urologist")
}

data class FilterState(
    val appMode: AppMode = AppMode.DIAGNOSTIC,
    val testName: String = "CBC",
    val medicineList: String = "",
    val symptoms: String = "",
    val doctorSpecialty: DoctorSpecialty = DoctorSpecialty.GENERAL,
    val manualQuery: String = "",
    val sortMode: SortMode = SortMode.BEST_MATCH,
    val userLatitude: Double = 0.0,
    val userLongitude: Double = 0.0,
    val myLocationLabel: String = ""
)

data class EnhancedCenter(
    val center: DiagnosticCenter,
    val distanceKm: Double,
    val testPrice: Double,
    val matchScore: Int,
    val travelTimeMinutes: Int? = null,
    val routePoints: List<LatLng>? = null
)

data class LocationSuggestion(
    val label: String,
    val placeId: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

class DiagnosticViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "DiagnosticViewModel"

    /** Widen the search radius only until at least this many results are found. */
    private val MIN_NEARBY_RESULTS = 10
    
    private val GOOGLE_MAPS_API_KEY = com.example.AppConfig.getMapsApiKey()
    private val repository: DiagnosticRepository
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val webSyncStatus = MutableStateFlow<String>("IDLE")
    val webSyncMessage = MutableStateFlow<String>("")

    /** Specialties inferred from typed symptoms (Doctor mode) — shown as chips. */
    val inferredSpecialties = MutableStateFlow<List<DoctorSpecialty>>(emptyList())

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DiagnosticRepository(database.diagnosticDao())
        // Caching: We no longer clear all data on startup to persist previous searches
    }

    fun fetchCentersFromWeb() {
        val state = filterState.value
        performSmartGlobalSearch(state.userLatitude, state.userLongitude)
    }

    private fun performSmartGlobalSearch(lat: Double, lon: Double) {
        val state = filterState.value
        val mode = state.appMode
        viewModelScope.launch(Dispatchers.IO) {
            webSyncStatus.value = "SYNCING"
            webSyncMessage.value = when (mode) {
                AppMode.DIAGNOSTIC -> "Consulting Google medical database..."
                AppMode.PHARMACY -> "Finding nearby pharmacies..."
                AppMode.DOCTOR -> "Finding nearby clinics & specialists..."
            }

            try {
                // Pharmacy ranking needs hospital anchors to infer size tier.
                val hospitalsJob =
                    if (mode == AppMode.PHARMACY) async { fetchHospitalAnchors(lat, lon) } else null

                // Every mode searches nearest-first and only widens when there
                // genuinely aren't enough places close by. `best` is never
                // overwritten by a smaller set, so a failing wider call can't
                // discard results a tighter one already found.
                var best = emptyList<DiagnosticCenter>()

                if (mode == AppMode.PHARMACY) {
                    for (radiusKm in listOf(5.0, 10.0, 15.0)) {
                        webSyncMessage.value = "Searching pharmacies within ${radiusKm.toInt()} km..."
                        val found = fetchNearbyByType(
                            lat, lon,
                            listOf("pharmacy", "drugstore"),
                            radiusKm * 1000.0,
                            mode
                        )
                        if (found.size > best.size) best = found
                        if (best.size >= MIN_NEARBY_RESULTS) break
                    }
                } else {
                    val queries = buildQueries(state)
                    val label = if (mode == AppMode.DIAGNOSTIC) "diagnostic centers" else "clinics"
                    for (radiusKm in listOf(5.0, 12.0, 25.0)) {
                        webSyncMessage.value = "Searching $label within ${radiusKm.toInt()} km..."
                        val found = queries.map { q ->
                            async {
                                fetchNearbyFromGoogle(
                                    lat, lon, q.keyword, mode, q.specialtyLabel, q.includedType, radiusKm
                                )
                            }
                        }.awaitAll().flatten()
                        if (found.size > best.size) best = found
                        if (best.size >= MIN_NEARBY_RESULTS) break
                    }
                }

                val allResults = best

                val hospitals = hospitalsJob?.await() ?: emptyList()

                var mergedResults = mergeAndDeduplicate(allResults, lat, lon)

                if (mode == AppMode.PHARMACY) {
                    mergedResults = mergedResults.map { p ->
                        p.copy(tierLabel = inferPharmacyTier(p, hospitals))
                    }
                }

                if (mergedResults.isEmpty()) {
                    webSyncStatus.value = "ERROR"
                    // Always overwrite: the "Searching within N km..." progress text is
                    // never empty, so the old isEmpty() guard meant this message never
                    // appeared and a zero-result search looked like it was still loading.
                    webSyncMessage.value = when (mode) {
                        AppMode.DIAGNOSTIC -> "No diagnostic centers found in your immediate area."
                        AppMode.PHARMACY -> "No pharmacies found in your immediate area."
                        AppMode.DOCTOR -> "No clinics or doctors found in your immediate area."
                    }
                } else {
                    // Replace this mode's previous rows (favorites survive) so a new
                    // search never mixes with stale results from another area.
                    repository.clearCategory(mode.name)
                    repository.insertAll(mergedResults)
                    fetchTravelTimesForTopCenters(lat, lon, mergedResults)
                    webSyncStatus.value = "SUCCESS"
                    webSyncMessage.value = when (mode) {
                        AppMode.DIAGNOSTIC -> "Found ${mergedResults.size} centers near you!"
                        AppMode.PHARMACY -> "Found ${mergedResults.size} pharmacies near you!"
                        AppMode.DOCTOR -> "Found ${mergedResults.size} clinics & specialists near you!"
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Google Places error", e)
                webSyncStatus.value = "ERROR"
                webSyncMessage.value = "Search failed. Check your API key or connection."
            }
        }
    }

    /**
     * Builds the Places queries for the current mode as (keyword, specialtyLabel).
     * specialtyLabel is only meaningful for Doctor mode.
     */
    private fun buildQueries(state: FilterState): List<PlaceQuery> {
        return when (state.appMode) {
            AppMode.DIAGNOSTIC -> listOf(
                PlaceQuery("diagnostic center"),
                PlaceQuery("medical lab"),
                PlaceQuery("diagnostic laboratory")
            )
            // Pharmacy never reaches here — it uses fetchNearbyByType(), a real
            // radius search pinned to Google's "pharmacy"/"drugstore" place types.
            AppMode.PHARMACY -> emptyList()
            AppMode.DOCTOR -> {
                val inferred = SymptomTriage.analyze(state.symptoms)
                inferredSpecialties.value = inferred

                // An explicitly chosen specialty leads; symptom-inferred ones follow.
                val chosen = LinkedHashSet<DoctorSpecialty>()
                if (state.doctorSpecialty != DoctorSpecialty.GENERAL) {
                    chosen.add(state.doctorSpecialty)
                }
                inferred.take(2).forEach { chosen.add(it) }
                if (chosen.isEmpty()) chosen.add(state.doctorSpecialty)

                chosen.take(3).map {
                    PlaceQuery(SymptomTriage.searchKeyword(it), specialtyLabel = it.label)
                }
            }
        }
    }

    /** One Places lookup for the active mode. */
    private data class PlaceQuery(
        val keyword: String,
        val specialtyLabel: String = "",
        /** Google place type to restrict results to (null = free text search). */
        val includedType: String? = null
    )

    /**
     * Actual pharmacy CHAIN names used to infer the "brand" tier. Deliberately
     * excludes generic words like "pharma"/"chemist"/"medical" — almost every
     * corner shop is called "<name> Pharma", and matching those promoted tiny
     * shops to brand tier and inflated their stock estimate.
     */
    private val pharmacyBrands = listOf(
        "lazz", "wellbeing", "guardian", "arogga", "tamanna",
        "apollo", "square", "popular", "cvs", "walgreens", "boots"
    )

    private fun inferPharmacyTier(p: DiagnosticCenter, hospitals: List<Pair<Double, Double>>): String {
        val name = p.name.lowercase()
        val nearestHospitalKm = hospitals.minOfOrNull { (hLat, hLon) ->
            calculateDistance(p.latitude, p.longitude, hLat, hLon)
        }
        if (name.contains("hospital") || (nearestHospitalKm != null && nearestHospitalKm <= 0.25)) {
            return PharmacyTier.HOSPITAL
        }
        if (pharmacyBrands.any { name.contains(it) }) return PharmacyTier.BRAND
        if (p.reviewsCount >= 80) return PharmacyTier.MID
        return PharmacyTier.SMALL
    }

    /** Minimal hospital sweep (locations only) used to infer pharmacy tier. */
    private suspend fun fetchHospitalAnchors(lat: Double, lon: Double): List<Pair<Double, Double>> {
        val url = "https://places.googleapis.com/v1/places:searchText"
        val jsonRequest = JSONObject().apply {
            put("textQuery", "hospital")
            put("maxResultCount", 20)
            put("rankPreference", "DISTANCE")
            put("locationRestriction", JSONObject().apply {
                put("rectangle", JSONObject().apply {
                    put("low", JSONObject().apply {
                        put("latitude", lat - 0.4)
                        put("longitude", lon - 0.4)
                    })
                    put("high", JSONObject().apply {
                        put("latitude", lat + 0.4)
                        put("longitude", lon + 0.4)
                    })
                })
            })
        }
        val body = jsonRequest.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .header("X-Goog-Api-Key", GOOGLE_MAPS_API_KEY)
            .header("X-Goog-FieldMask", "places.location")
            .header("X-Android-Package", "com.aistudio.diagnosticfinder.zkwpqd")
            .header("X-Android-Cert", "A55E38E3CC22590D6028A3B17FAA1D60866F7BAB")
            .build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            if (!response.isSuccessful) return emptyList()
            val results = JSONObject(responseBody).optJSONArray("places") ?: JSONArray()
            val out = mutableListOf<Pair<Double, Double>>()
            for (i in 0 until results.length()) {
                val loc = results.getJSONObject(i).optJSONObject("location") ?: continue
                out.add(loc.optDouble("latitude") to loc.optDouble("longitude"))
            }
            out
        } catch (e: Exception) {
            Log.e(TAG, "Hospital anchor fetch failed", e)
            emptyList()
        }
    }

    private suspend fun fetchNearbyFromGoogle(
        lat: Double,
        lon: Double,
        keyword: String,
        mode: AppMode,
        specialtyLabel: String,
        includedType: String? = null,
        radiusKm: Double = 12.0
    ): List<DiagnosticCenter> {
        val url = "https://places.googleapis.com/v1/places:searchText"

        // searchText only accepts a rectangle for locationRestriction, so convert the
        // requested radius into a box around the user. The old fixed ±0.4° box was
        // ~88km wide, which is why "nearest" results could be tens of km away.
        val latDelta = radiusKm / 111.32
        val lonDelta = radiusKm / (111.32 * Math.cos(Math.toRadians(lat)).coerceAtLeast(0.01))

        val jsonRequest = JSONObject().apply {
            put("textQuery", keyword)
            put("maxResultCount", 20)
            put("rankPreference", "DISTANCE")
            // Restrict to a real Google place type where we have one, so free text
            // can't drag in unrelated businesses (e.g. a medical book shop).
            if (includedType != null) {
                put("includedType", includedType)
                put("strictTypeFiltering", true)
            }
            put("locationRestriction", JSONObject().apply {
                put("rectangle", JSONObject().apply {
                    put("low", JSONObject().apply {
                        put("latitude", lat - latDelta)
                        put("longitude", lon - lonDelta)
                    })
                    put("high", JSONObject().apply {
                        put("latitude", lat + latDelta)
                        put("longitude", lon + lonDelta)
                    })
                })
            })
        }

        val body = jsonRequest.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .header("X-Goog-Api-Key", GOOGLE_MAPS_API_KEY)
            .header("X-Goog-FieldMask", "places.id,places.displayName,places.formattedAddress,places.location,places.rating,places.userRatingCount,places.internationalPhoneNumber,places.types,places.websiteUri")
            .header("X-Android-Package", "com.aistudio.diagnosticfinder.zkwpqd")
            .header("X-Android-Cert", "A55E38E3CC22590D6028A3B17FAA1D60866F7BAB")
            .build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            if (response.isSuccessful) {
                parsePlacesResponse(responseBody, lat, lon, mode, specialtyLabel)
            } else {
                val json = JSONObject(responseBody)
                val errorMsg = json.optJSONObject("error")?.optString("message", "Unknown error") ?: "Unknown error"
                webSyncMessage.value = "Google API Error: $errorMsg"
                Log.e(TAG, "Places Search Error: $responseBody")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network Error", e)
            emptyList()
        }
    }

    /** Shared Places (New) response parser for both text and nearby search. */
    private fun parsePlacesResponse(
        responseBody: String,
        fallbackLat: Double,
        fallbackLon: Double,
        mode: AppMode,
        specialtyLabel: String
    ): List<DiagnosticCenter> {
        val results = JSONObject(responseBody).optJSONArray("places") ?: JSONArray()
        val list = mutableListOf<DiagnosticCenter>()

        for (i in 0 until results.length()) {
            val place = results.getJSONObject(i)
            val name = place.optJSONObject("displayName")?.optString("text", "Medical Facility")
                ?: "Medical Facility"
            val loc = place.optJSONObject("location") ?: continue

            // Never let a non-pharmacy (book shop, general store, …) into Pharmacy mode.
            val typesArray = place.optJSONArray("types")
            val types = buildList {
                if (typesArray != null) {
                    for (t in 0 until typesArray.length()) add(typesArray.optString(t))
                }
            }
            val typeAllowed = mode != AppMode.PHARMACY || types.isEmpty() ||
                types.any { it == "pharmacy" || it == "drugstore" }
            if (!typeAllowed) continue

            list.add(
                createProceduralCenterFromGoogle(
                    name,
                    loc.optDouble("latitude", fallbackLat),
                    loc.optDouble("longitude", fallbackLon),
                    place.optString("formattedAddress", "Nearby Location"),
                    // No invented ratings: a place Google has no rating for enters as
                    // 0/0 and the Bayesian prior treats it as neutral. The UI shows
                    // "No rating" rather than a made-up 4.0.
                    place.optDouble("rating", 0.0).toFloat(),
                    place.optInt("userRatingCount", 0),
                    place.optString("internationalPhoneNumber", "Tap to call"),
                    mode,
                    specialtyLabel,
                    place.optString("websiteUri", "")
                )
            )
        }
        return list
    }

    /**
     * True "near me" lookup: a real radius around the user, ranked by distance —
     * the same thing Google Maps does for "pharmacy near me".
     *
     * The old path text-searched a ~88km-wide rectangle, which is why the closest
     * result could be 8-9km away. A radius search cannot return anything outside it.
     */
    private suspend fun fetchNearbyByType(
        lat: Double,
        lon: Double,
        types: List<String>,
        radiusMeters: Double,
        mode: AppMode
    ): List<DiagnosticCenter> {
        val url = "https://places.googleapis.com/v1/places:searchNearby"

        val jsonRequest = JSONObject().apply {
            put("includedTypes", JSONArray(types))
            put("maxResultCount", 20)
            put("rankPreference", "DISTANCE")
            put("locationRestriction", JSONObject().apply {
                put("circle", JSONObject().apply {
                    put("center", JSONObject().apply {
                        put("latitude", lat)
                        put("longitude", lon)
                    })
                    put("radius", radiusMeters)
                })
            })
        }

        val body = jsonRequest.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .header("X-Goog-Api-Key", GOOGLE_MAPS_API_KEY)
            .header(
                "X-Goog-FieldMask",
                "places.id,places.displayName,places.formattedAddress,places.location,places.rating,places.userRatingCount,places.internationalPhoneNumber,places.types,places.websiteUri"
            )
            .header("X-Android-Package", "com.aistudio.diagnosticfinder.zkwpqd")
            .header("X-Android-Cert", "A55E38E3CC22590D6028A3B17FAA1D60866F7BAB")
            .build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            if (response.isSuccessful) {
                parsePlacesResponse(responseBody, lat, lon, mode, "")
            } else {
                val errorMsg = JSONObject(responseBody).optJSONObject("error")
                    ?.optString("message", "Unknown error") ?: "Unknown error"
                webSyncMessage.value = "Google API Error: $errorMsg"
                Log.e(TAG, "Nearby Search Error: $responseBody")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Nearby search network error", e)
            emptyList()
        }
    }

    private fun createProceduralCenterFromGoogle(
        name: String,
        lat: Double,
        lon: Double,
        address: String,
        rating: Float,
        reviews: Int,
        phone: String = "Tap to call",
        mode: AppMode = AppMode.DIAGNOSTIC,
        specialtyLabel: String = "",
        websiteUri: String = ""
    ): DiagnosticCenter {
        val stableId = (name + lat.toString() + lon.toString()).hashCode()
        val hash = name.hashCode()
        val waitTime = 10 + Math.abs(hash % 45)
        val basePrice = 12.0 + Math.abs(hash % 25)

        // Test prices and lab certification are diagnostic-only concepts. Attaching
        // them to a pharmacy or a clinic is what made every mode look like a
        // diagnostic finder, so non-diagnostic rows carry neither.
        val isDiagnostic = mode == AppMode.DIAGNOSTIC

        return DiagnosticCenter(
            id = stableId,
            name = name,
            address = address,
            suburb = "Nearby",
            latitude = lat,
            longitude = lon,
            rating = rating,
            reviewsCount = reviews,
            crowdLevel = if (waitTime < 20) "Low" else if (waitTime < 35) "Moderate" else "High",
            crowdPercentage = (waitTime * 2).coerceIn(10, 100),
            estimatedWaitMinutes = waitTime,
            phone = phone,
            timing = "08:00 AM - 08:30 PM",
            certified = isDiagnostic && hash % 3 == 0,
            testsJson = if (isDiagnostic) {
                """{"CBC": ${String.format("%.2f", basePrice)}, "MRI Brain": ${String.format("%.2f", basePrice * 10)}}"""
            } else "{}",
            category = mode.name,
            specialtyLabel = specialtyLabel,
            websiteUri = websiteUri
        )
    }

    private fun mergeAndDeduplicate(combined: List<DiagnosticCenter>, lat: Double, lon: Double): List<DiagnosticCenter> {
        val result = mutableListOf<DiagnosticCenter>()
        val seenIds = mutableSetOf<Int>()

        for (candidate in combined) {
            // Same place returned by more than one keyword -> identical stable id.
            if (!seenIds.add(candidate.id)) continue

            // Genuinely different businesses often sit metres apart (pharmacies
            // especially cluster on the same street). The old blanket 100m rule
            // deleted most of them, so only near-identical names at nearly the
            // same spot count as duplicates now.
            val isDuplicate = result.any { existing ->
                existing.name.equals(candidate.name, ignoreCase = true) &&
                    calculateDistance(
                        candidate.latitude, candidate.longitude,
                        existing.latitude, existing.longitude
                    ) < 0.05
            }
            if (!isDuplicate) result.add(candidate)
        }
        return result.sortedBy {
            calculateDistance(lat, lon, it.latitude, it.longitude)
        }
    }

    private val routeDetails = MutableStateFlow<Map<Int, Pair<Int?, List<LatLng>?>>>(emptyMap())

    private fun fetchTravelTimesForTopCenters(userLat: Double, userLon: Double, centers: List<DiagnosticCenter>? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val centersToProcess = if (centers != null) {
                // If centers are provided, calculate their scores and take the top ones
                centers.map { center ->
                    val dist = calculateDistance(userLat, userLon, center.latitude, center.longitude)
                    val prices = center.getTestPrices()
                    val dbKey = getDatabaseTestKey(filterState.value.testName)
                    val price = prices[dbKey] ?: 25.0
                    // Use the same mode-aware scorer as the list, so travel times are
                    // fetched for the results the user will actually see on top.
                    val score = scoreFor(center, dist, price, filterState.value)
                    EnhancedCenter(center, dist, price, score)
                }.sortedByDescending { it.matchScore }.take(15)
            } else {
                enhancedCenters.value.take(15)
            }
            
            if (centersToProcess.isEmpty()) return@launch
            
            val newRouteDetails = routeDetails.value.toMutableMap()
            
            centersToProcess.forEach { ec ->
                if (newRouteDetails.containsKey(ec.center.id)) return@forEach

                val url = "https://router.project-osrm.org/route/v1/driving/$userLon,$userLat;${ec.center.longitude},${ec.center.latitude}?overview=full&geometries=polyline"
                val request = Request.Builder().url(url).header("User-Agent", "DiagnosticFinder/1.0").build()
                
                try {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val json = JSONObject(response.body?.string() ?: "")
                        val routes = json.optJSONArray("routes") ?: JSONArray()
                        if (routes.length() > 0) {
                            val route = routes.getJSONObject(0)
                            val duration = (route.optDouble("duration", 0.0) / 60.0).toInt()
                            val geometry = route.optString("geometry", "")
                            if (geometry.isNotEmpty()) {
                                val points = decodePolyline(geometry)
                                newRouteDetails[ec.center.id] = duration to points
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "OSRM error for center ${ec.center.id}", e)
                }
            }
            
            routeDetails.value = newRouteDetails
        }
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(p)
        }
        return poly
    }

    val filterState = MutableStateFlow(FilterState())

    val locationSuggestions = MutableStateFlow<List<LocationSuggestion>>(emptyList())
    val isSearchingLocation = MutableStateFlow(false)
    private var searchJob: Job? = null

    fun searchPlaces(query: String) {
        searchJob?.cancel()
        val cleaned = query.trim()
        if (cleaned.length < 1) {
            locationSuggestions.value = emptyList()
            webSyncMessage.value = "" 
            return
        }

        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(200) 
            isSearchingLocation.value = true
            try {
                // Use Places Autocomplete (New)
                val url = "https://places.googleapis.com/v1/places:autocomplete"

                val jsonRequest = JSONObject().apply {
                    put("input", cleaned)
                    put("includedRegionCodes", JSONArray(listOf("BD")))
                }

                val postBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())
                
                val request = Request.Builder()
                    .url(url)
                    .post(postBody)
                    .header("X-Goog-Api-Key", GOOGLE_MAPS_API_KEY)
                    .header("X-Goog-FieldMask", "suggestions.placePrediction.text,suggestions.placePrediction.placeId")
                    .header("X-Android-Package", "com.aistudio.diagnosticfinder.zkwpqd")
                    .header("X-Android-Cert", "A55E38E3CC22590D6028A3B17FAA1D60866F7BAB")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""
                
                if (response.isSuccessful) {
                    val rootJson = JSONObject(responseBody)
                    val suggestions = rootJson.optJSONArray("suggestions") ?: JSONArray()
                    
                    if (suggestions.length() > 0) {
                        val results = mutableListOf<LocationSuggestion>()
                        for (i in 0 until suggestions.length()) {
                            val suggestion = suggestions.getJSONObject(i)
                            val placePrediction = suggestion.optJSONObject("placePrediction")
                            val placeId = placePrediction?.optString("placeId", "") ?: ""
                            val text = placePrediction?.optJSONObject("text")
                            val description = text?.optString("text") ?: ""
                            results.add(LocationSuggestion(description, placeId))
                        }
                        locationSuggestions.value = results
                        webSyncMessage.value = "" 
                    } else {
                        // Fallback logic remains same or handle zero results
                        locationSuggestions.value = emptyList()
                    }
                } else {
                    val json = JSONObject(responseBody)
                    val errorDetail = json.optJSONObject("error")?.optString("message", "No specific details.") ?: "No details"
                    webSyncMessage.value = "Google API Error (New): $errorDetail"
                    Log.e(TAG, "Autocomplete (New) failed: $errorDetail")
                    locationSuggestions.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Location Search Error", e)
                webSyncMessage.value = "App Error: ${e.localizedMessage}"
            } finally {
                isSearchingLocation.value = false
            }
        }
    }

    suspend fun getPlaceCoordinates(placeId: String): Pair<Double, Double>? = withContext(Dispatchers.IO) {
        val url = "https://places.googleapis.com/v1/places/$placeId"
        val request = Request.Builder()
            .url(url)
            .get()
            .header("X-Goog-Api-Key", GOOGLE_MAPS_API_KEY)
            .header("X-Goog-FieldMask", "location,displayName")
            .header("X-Android-Package", "com.aistudio.diagnosticfinder.zkwpqd")
            .header("X-Android-Cert", "A55E38E3CC22590D6028A3B17FAA1D60866F7BAB")
            .build()
        
        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            if (response.isSuccessful) {
                val json = JSONObject(responseBody)
                val loc = json.optJSONObject("location")
                if (loc != null) {
                    return@withContext loc.optDouble("latitude") to loc.optDouble("longitude")
                }
            } else {
                Log.e(TAG, "Place Details failed: $responseBody")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Place Details error", e)
        }
        null
    }

    val enhancedCenters: StateFlow<List<EnhancedCenter>> = combine(
        repository.allCenters,
        filterState,
        routeDetails
    ) { centers, filters, routes ->
        // Scope results to the active mode. Without this, pharmacy/doctor searches
        // render on top of the cached diagnostic pool (all modes share one table).
        centers.filter { it.category == filters.appMode.name }.map { center ->
            val dist = calculateDistance(filters.userLatitude, filters.userLongitude, center.latitude, center.longitude)
            
            // Dynamic wait calculation based on current hour of day
            val calendar = java.util.Calendar.getInstance()
            val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
            val waitFactor = when (hour) {
                in 8..11 -> 1.4 // Morning peak
                in 12..15 -> 0.75 // Afternoon lull
                in 17..19 -> 1.3 // Evening rush
                else -> 0.9
            }
            val dynamicWait = (center.estimatedWaitMinutes * waitFactor).toInt().coerceIn(5, 90)
            val dynamicCrowdLevel = when {
                dynamicWait < 20 -> "Low"
                dynamicWait < 35 -> "Moderate"
                else -> "High"
            }
            val dynamicCrowdPercentage = (dynamicWait * 2).coerceIn(10, 100)
            
            val updatedCenter = center.copy(
                estimatedWaitMinutes = dynamicWait,
                crowdLevel = dynamicCrowdLevel,
                crowdPercentage = dynamicCrowdPercentage
            )

            // Dynamic test price summation:
            val totalPrice = calculateTotalPriceForQuery(updatedCenter, filters.testName)

            // Mode-aware match score (see scoreFor).
            val score = scoreFor(updatedCenter, dist, totalPrice, filters)

            val routeInfo = routes[updatedCenter.id]
            val userLatLng = LatLng(filters.userLatitude, filters.userLongitude)
            val centerLatLng = LatLng(updatedCenter.latitude, updatedCenter.longitude)
            val routePoints = routeInfo?.second ?: generateProceduralRoute(userLatLng, centerLatLng)
            val travelTime = routeInfo?.first ?: (dist * 2.0).toInt().coerceAtLeast(1)
            
            val isBD = filters.myLocationLabel.contains("Bangladesh", ignoreCase = true) ||
                       filters.myLocationLabel.contains("Dhaka", ignoreCase = true) ||
                       filters.myLocationLabel.contains("BD", ignoreCase = true) ||
                       (filters.userLatitude in 20.0..27.0 && filters.userLongitude in 88.0..93.0)
            val finalPrice = if (isBD) totalPrice * 120.0 else totalPrice
            
            EnhancedCenter(updatedCenter, dist, finalPrice, score, travelTime, routePoints)
        }.filter { enhanced ->
            // Locality Filter: Only show centers within 40km of current search point
            // This prevents "Cached" results from other cities or countries (like India) appearing.
            val isNearby = enhanced.distanceKm < 40.0

            // DECISION ENGINE: test-availability filter is DIAGNOSTIC-only logic.
            // Applying it to pharmacies/clinics rejected real pharmacies (no "lab"/
            // "clinic" in the name) while admitting things like "… Medical Book House".
            val isCompatible = filters.appMode != AppMode.DIAGNOSTIC ||
                checkTestCompatibility(enhanced.center, filters.testName)
            
            isNearby && isCompatible && (filters.manualQuery.isEmpty() ||
                enhanced.center.name.contains(filters.manualQuery, ignoreCase = true))
        }.sortedWith { a, b ->
            when (filters.sortMode) {
                SortMode.BEST_MATCH -> b.matchScore.compareTo(a.matchScore)
                SortMode.DISTANCE -> a.distanceKm.compareTo(b.distanceKm)
                SortMode.PRICE_LOW_TO_HIGH -> a.testPrice.compareTo(b.testPrice)
                SortMode.HI_RATING -> b.center.rating.compareTo(a.center.rating)
                SortMode.SHORT_WAIT -> a.center.estimatedWaitMinutes.compareTo(b.center.estimatedWaitMinutes)
                SortMode.TRAVEL_TIME -> (a.travelTimeMinutes ?: Int.MAX_VALUE).compareTo(b.travelTimeMinutes ?: Int.MAX_VALUE)
            }
        }.take(15).let { list ->
            if (list.size < 2) return@let list
            val maxS = list.maxOf { it.matchScore }.toDouble()
            val minS = list.minOf { it.matchScore }.toDouble()
            val range = (maxS - minS).coerceAtLeast(1.0)
            
            list.map { ec ->
                // Rank-based Spectrum: Ensures we see Blue, Green, Yellow, and Red within the top 15.
                // We map the highest score to ~96% and the lowest to ~42%
                val normalized = 42 + (((ec.matchScore - minS) / range) * 54).toInt()
                ec.copy(matchScore = normalized)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private fun checkTestCompatibility(center: DiagnosticCenter, testName: String): Boolean {
        val n = center.name.lowercase()
        val t = testName.lowercase()
        
        // Expert Rules (Relaxed):
        if (t.contains("mri") || t.contains("scan") || t.contains("ray") || t.contains("ct")) {
            // Must be an imaging/radiology center or a hospital or generic clinical center
            return n.contains("imaging") || n.contains("scan") || n.contains("radiology") || 
                   n.contains("hospital") || n.contains("digital") || n.contains("diagnostic") || 
                   n.contains("center") || n.contains("clinic")
        }
        if (t.contains("cbc") || t.contains("lipid") || t.contains("glucose") || t.contains("blood")) {
            // Must be a lab or clinic
            return n.contains("lab") || n.contains("pathology") || n.contains("clinic") || 
                   n.contains("hospital") || n.contains("medical") || n.contains("diagnostic") || 
                   n.contains("center") || n.contains("care")
        }
        return true // Generic fallback
    }

    val aiResponse = MutableStateFlow<String?>("")
    val loadingAi = MutableStateFlow(false)

    fun updateTest(testName: String) {
        filterState.value = filterState.value.copy(testName = testName)
    }

    fun updateManualQuery(query: String) {
        filterState.value = filterState.value.copy(manualQuery = query)
    }

    fun updateSortMode(mode: SortMode) {
        filterState.value = filterState.value.copy(sortMode = mode)
    }

    fun updateAppMode(mode: AppMode) {
        if (filterState.value.appMode == mode) return
        filterState.value = filterState.value.copy(appMode = mode)
        // Routes belong to the previous mode's results.
        routeDetails.value = emptyMap()
        inferredSpecialties.value = emptyList()
        webSyncMessage.value = ""

        // Switching mode must actually go and look for that kind of place.
        val state = filterState.value
        if (state.userLatitude != 0.0 || state.userLongitude != 0.0) {
            performSmartGlobalSearch(state.userLatitude, state.userLongitude)
        }
    }

    fun updateSymptoms(symptoms: String) {
        filterState.value = filterState.value.copy(symptoms = symptoms)
    }

    fun updateDoctorSpecialty(specialty: DoctorSpecialty) {
        filterState.value = filterState.value.copy(doctorSpecialty = specialty)
    }

    fun updateMedicineList(list: String) {
        filterState.value = filterState.value.copy(medicineList = list)
    }

    fun updateLocation(latitude: Double, longitude: Double, label: String) {
        filterState.value = filterState.value.copy(
            userLatitude = latitude,
            userLongitude = longitude,
            myLocationLabel = label
        )
        // Reset routes when location changes
        routeDetails.value = emptyMap()
        fetchTravelTimesForTopCenters(latitude, longitude)
        performSmartGlobalSearch(latitude, longitude)
    }

    fun toggleFavorite(centerId: Int, currentFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(centerId, !currentFav)
        }
    }

    fun triggerAiAdvisor(patientQuery: String) {
        val currentCenters = enhancedCenters.value
        if (currentCenters.isEmpty()) return
        loadingAi.value = true
        viewModelScope.launch {
            val response = GeminiManager.askAdvisor(patientQuery, "Centers: ${currentCenters.take(3).map { it.center.name }}")
            aiResponse.value = response
            loadingAi.value = false
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c 
    }

    private fun calculateMatchScore(center: DiagnosticCenter, distance: Double, price: Double, dbKey: String, userTestName: String): Int {
        // Higher discrimination logic to ensure not everyone is "Excellent" (Blue)
        
        // Proximity discrimination: 0km = 100, 8km = 0 (Steeper drop)
        val proximityScore = ((8.0 - distance).coerceIn(0.0, 8.0) / 8.0) * 100.0
        
        // Price discrimination: $12 = 100, $40 = 0 (Based on createProceduralCenterFromGoogle price range)
        val costScore = ((40.0 - price).coerceIn(0.0, 40.0) / 28.0) * 100.0
        
        // Rating discrimination: 5.0 = 100, 3.5 = 0 (Only high ratings score well)
        val ratingScore = ((center.rating - 3.5f).coerceAtLeast(0f) / 1.5f) * 100.0
        
        var specializationBonus = 0.0
        val n = center.name.lowercase()
        val ut = userTestName.lowercase()
        if (ut.isNotEmpty() && (n.contains("lab") || n.contains("imaging") || n.contains("diagnostic"))) {
            specializationBonus = 20.0
        }

        // Spread the weights to favor varied outcomes
        val composite = (proximityScore * 0.35) + (costScore * 0.25) + (ratingScore * 0.30) + (specializationBonus * 0.10)
        
        // Force more variation by adding a small stable "random" jitter based on center ID
        val jitter = (center.id % 5).toDouble() 
        
        return (composite + jitter).coerceIn(0.0, 100.0).toInt()
    }

    /**
     * Mode-aware match score (0..100).
     *
     * Each mode scores on the signals that actually mean something for it:
     *  - DIAGNOSTIC: proximity + test cost + rating + lab-name specialization
     *  - PHARMACY:   proximity + rating + inferred size tier (stock breadth)
     *  - DOCTOR:     proximity + rating + specialty-name match (no price exists)
     *
     * Rating uses a Bayesian average so a 5.0 backed by 3 reviews does not beat a
     * 4.6 backed by 800.
     */
    private fun scoreFor(
        center: DiagnosticCenter,
        dist: Double,
        totalPrice: Double,
        filters: FilterState
    ): Int {
        // Proximity: 0km = 100, 8km = 0
        val proximityScore = ((8.0 - dist).coerceIn(0.0, 8.0) / 8.0) * 100.0

        val priorCount = 20.0
        val priorMean = 3.5
        val bayesRating = (center.reviewsCount * center.rating + priorCount * priorMean) /
                (center.reviewsCount + priorCount)
        // Map 3.0..5.0 onto 0..100 so ratings still discriminate.
        val ratingScore = (((bayesRating - 3.0) / 2.0).coerceIn(0.0, 1.0)) * 100.0

        val name = center.name.lowercase()

        val composite = when (filters.appMode) {
            AppMode.DIAGNOSTIC -> {
                val expectedBase =
                    if (filters.testName.contains("MRI", true) || filters.testName.contains("CT", true)) 300.0 else 25.0
                val numTests = filters.testName.split(",").map { it.trim() }
                    .filter { it.isNotEmpty() }.size.coerceAtLeast(1)
                val maxPrice = expectedBase * numTests * 1.4
                val minPrice = expectedBase * numTests * 0.6
                val costRange = (maxPrice - minPrice).coerceAtLeast(10.0)
                val costScore = ((maxPrice - totalPrice).coerceIn(0.0, costRange) / costRange) * 100.0
                val spec = if (name.contains("lab") || name.contains("imaging") || name.contains("diagnostic")) 100.0 else 0.0
                (proximityScore * 0.35) + (costScore * 0.25) + (ratingScore * 0.30) + (spec * 0.10)
            }
            AppMode.PHARMACY -> {
                // Stock likelihood depends on BOTH the medicine and the pharmacy:
                // Napa is everywhere, Lenva realistically only at hospital pharmacies.
                // With no medicines typed we fall back to generic tier breadth.
                val meds = MedicineCatalog.parseList(filters.medicineList)
                val stockScore = if (meds.isEmpty()) {
                    PharmacyTier.breadth(center.tierLabel)
                } else {
                    MedicineCatalog.availabilityForAll(meds, center.tierLabel).toDouble()
                }
                (proximityScore * 0.35) + (ratingScore * 0.25) + (stockScore * 0.40)
            }
            AppMode.DOCTOR -> {
                val rowSpecialty = DoctorSpecialty.entries
                    .firstOrNull { it.label == center.specialtyLabel } ?: filters.doctorSpecialty
                val hints = SymptomTriage.nameHints(rowSpecialty)
                val nameMatch = if (hints.any { name.contains(it) }) 100.0 else 40.0
                (proximityScore * 0.40) + (ratingScore * 0.40) + (nameMatch * 0.20)
            }
        }

        // Small stable spread so scores aren't all identical.
        val jitter = (center.id % 5).toDouble()
        return (composite + jitter).coerceIn(0.0, 100.0).toInt()
    }

    private fun calculateTotalPriceForQuery(center: DiagnosticCenter, query: String): Double {
        val prices = center.getTestPrices()
        val tests = query.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (tests.isEmpty()) {
            val dbKey = getDatabaseTestKey(query)
            return prices[dbKey] ?: 25.0
        }
        return tests.sumOf { t ->
            val dbKey = getDatabaseTestKey(t)
            prices[dbKey] ?: 25.0
        }
    }

    companion object {
        fun getDatabaseTestKey(uiName: String): String {
            return when {
                uiName.contains("CBC", ignoreCase = true) -> "CBC"
                uiName.contains("Lipid", ignoreCase = true) -> "Lipid"
                uiName.contains("MRI", ignoreCase = true) -> "MRI Brain"
                uiName.contains("X-Ray", ignoreCase = true) -> "X-Ray Chest"
                uiName.contains("Glucose", ignoreCase = true) -> "Glucose"
                uiName.contains("CT Scan", ignoreCase = true) -> "CT Scan Abdomen"
                else -> "CBC"
            }
        }
    }

    private fun generateProceduralRoute(start: LatLng, end: LatLng): List<LatLng> {
        val points = mutableListOf<LatLng>()
        points.add(start)
        
        val lat1 = start.latitude
        val lon1 = start.longitude
        val lat2 = end.latitude
        val lon2 = end.longitude
        
        val p1 = LatLng(lat1, lon1 + (lon2 - lon1) * 0.25)
        val p2 = LatLng(lat1 + (lat2 - lat1) * 0.4, lon1 + (lon2 - lon1) * 0.25)
        val p3 = LatLng(lat1 + (lat2 - lat1) * 0.4, lon1 + (lon2 - lon1) * 0.75)
        val p4 = LatLng(lat1 + (lat2 - lat1) * 0.8, lon1 + (lon2 - lon1) * 0.75)
        val p5 = LatLng(lat1 + (lat2 - lat1) * 0.8, lon2)
        
        points.add(p1)
        points.add(p2)
        points.add(p3)
        points.add(p4)
        points.add(p5)
        points.add(end)
        
        return points
    }
}
