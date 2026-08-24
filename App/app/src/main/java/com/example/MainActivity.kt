package com.example

import android.Manifest
import android.app.Activity
import android.util.Log
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.ui.unit.TextUnit
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import android.webkit.WebView
import android.webkit.WebViewClient
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import android.graphics.Bitmap
import android.location.LocationManager
import android.location.Geocoder
import android.location.Address
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.animation.core.Animatable
import androidx.compose.ui.platform.LocalDensity
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.data.model.DiagnosticCenter
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.Crossfade
import android.speech.RecognizerIntent
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.window.Dialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material.icons.filled.Mic
import com.example.ui.DiagnosticViewModel
import com.example.ui.EnhancedCenter
import com.example.ui.FilterState
import com.example.ui.SortMode
import com.example.ui.AppMode
import com.example.ui.DoctorSpecialty
import com.example.ui.PharmacyTier
import com.example.ui.PrescriptionOcr
import com.example.ui.SymptomTriage
import com.example.ui.MedicineCatalog
import com.example.ui.MedicineRarity
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import android.graphics.Typeface
import android.graphics.Rect
import androidx.compose.ui.graphics.toArgb
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.CropRotate
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.EditNote

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.ImageCaptureException
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Undo


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase App Check: proves calls come from this genuine app on a real
        // device, which is what lets us reach Gemini without shipping a raw key.
        // Debug builds use the debug provider (register the token it logs in
        // Firebase Console -> App Check -> Manage debug tokens).
        try {
            com.google.firebase.FirebaseApp.initializeApp(this)
            com.google.firebase.appcheck.FirebaseAppCheck.getInstance()
                .installAppCheckProviderFactory(
                    if (BuildConfig.DEBUG) {
                        com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory.getInstance()
                    } else {
                        com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory.getInstance()
                    }
                )
        } catch (e: Exception) {
            Log.e("AppCheck", "Firebase App Check init failed", e)
        }

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DiagnosticAppEntryPoint()
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun DiagnosticAppEntryPoint() {
    val context = LocalContext.current
    val viewModel: DiagnosticViewModel = viewModel()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val enhancedCenters by viewModel.enhancedCenters.collectAsStateWithLifecycle()
    val locationSuggestions by viewModel.locationSuggestions.collectAsStateWithLifecycle()
    val isSearchingLocation by viewModel.isSearchingLocation.collectAsStateWithLifecycle()
    val apiErrorMessage by viewModel.webSyncMessage.collectAsStateWithLifecycle()
    val inferredSpecialties by viewModel.inferredSpecialties.collectAsStateWithLifecycle()

    // Accent for the results screen, matching the active mode.
    val listAccent by animateColorAsState(
        targetValue = when (filterState.appMode) {
            AppMode.DIAGNOSTIC -> MedicalBlue
            AppMode.PHARMACY -> PharmacyPrimary
            AppMode.DOCTOR -> DoctorPrimary
        },
        animationSpec = tween(500),
        label = "listAccent"
    )

    var showLandingPage by remember { mutableStateOf(true) }
    var selectedCenterForDetail by remember { mutableStateOf<EnhancedCenter?>(null) }
    var showGpsSettingsDialog by remember { mutableStateOf(false) }
    var isMapLoaded by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val config = LocalConfiguration.current
    val screenHeightPx = with(density) { config.screenHeightDp.dp.toPx() }
    var topPaddingPx by remember { mutableStateOf(0f) }
    var boxHeightPx by remember { mutableStateOf(0f) }
    
    val boxHeight = if (boxHeightPx > 0f) boxHeightPx else (screenHeightPx - topPaddingPx)
    val expandedOffset = with(density) { 12.dp.toPx() }
    val defaultOffset = boxHeight * 0.48f
    val collapsedOffset = boxHeight - with(density) { 72.dp.toPx() }
    
    val sheetOffsetY = remember { Animatable(defaultOffset) }

    LaunchedEffect(boxHeight) {
        if (boxHeightPx > 0f) {
            sheetOffsetY.snapTo(defaultOffset)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(filterState.userLatitude, filterState.userLongitude),
            13f
        )
    }

    // Keep "my location" in the exact centre of the VISIBLE map area — the strip
    // above the bottom sheet — and re-centre live as the sheet is dragged.
    // Identical behaviour in all three modes (diagnostic, pharmacy, doctor).
    //
    // The zoom adapts to the farthest shown result so the ranked pins still fit
    // on screen; a fixed zoom used to push distant results out of view.
    androidx.compose.runtime.LaunchedEffect(
        filterState.userLatitude,
        filterState.userLongitude,
        isMapLoaded,
        topPaddingPx,
        sheetOffsetY.value,
        enhancedCenters.map { it.center.id }
    ) {
        if (!isMapLoaded || topPaddingPx <= 0f) return@LaunchedEffect
        try {
            // Roughly: 1km -> z15, 5km -> z13, 10km -> z12.
            val farthestKm = enhancedCenters.take(15).maxOfOrNull { it.distanceKm } ?: 3.0
            val zoomVal = (15.3 - Math.log(farthestKm.coerceAtLeast(0.4)) / Math.log(2.0))
                .toFloat().coerceIn(11f, 15.5f)

            // The sheet covers (boxHeight - sheetOffsetY) of the map. Shifting the
            // camera target south by half of that puts the user dead centre of the
            // strip that is actually visible.
            val offsetPx = (boxHeight - sheetOffsetY.value) / 2
            val latOffset = calculateLatitudeOffset(
                filterState.userLatitude,
                zoomVal,
                density.density,
                offsetPx
            )
            cameraPositionState.animate(
                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                    LatLng(filterState.userLatitude - latOffset, filterState.userLongitude),
                    zoomVal
                )
            )
        } catch (e: Exception) {
            Log.e("Map", "Failed to centre map on user", e)
        }
    }

    if (showGpsSettingsDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showGpsSettingsDialog = false },
            title = { Text(text = "Turn On Location Services (GPS)") },
            text = { Text(text = "Your device's location/GPS services are currently disabled. Please enable them to allow the app to detect your precise real location.") },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        showGpsSettingsDialog = false
                        try {
                            val intent = android.content.Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Cannot open settings. Please enable GPS manually.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue)
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showGpsSettingsDialog = false }
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    val coroutineScope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                                   locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isLocationEnabled) {
                showGpsSettingsDialog = true
            } else {
                Toast.makeText(context, "Acquiring GPS position...", Toast.LENGTH_SHORT).show()
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            fetchGpsLocationAddress(context, location.latitude, location.longitude) { addressLabel ->
                                viewModel.updateLocation(location.latitude, location.longitude, addressLabel)
                                Toast.makeText(context, "GPS synced: $addressLabel", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            fusedLocationClient.getCurrentLocation(
                                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                                null
                            ).addOnSuccessListener { freshLocation ->
                                if (freshLocation != null) {
                                    fetchGpsLocationAddress(context, freshLocation.latitude, freshLocation.longitude) { addressLabel ->
                                        viewModel.updateLocation(freshLocation.latitude, freshLocation.longitude, addressLabel)
                                        Toast.makeText(context, "GPS synced: $addressLabel", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "GPS coordinates unavailable.", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("GPS", "Failed to extract location", e)
                }
            }
        } else {
            Toast.makeText(context, "GPS permission denied. Utilizing simulated test presets.", Toast.LENGTH_SHORT).show()
        }
    }

    AnimatedContent(
        targetState = showLandingPage,
        transitionSpec = {
            if (targetState) {
                // Back to Landing: slide from left to right
                (slideInHorizontally(animationSpec = tween(500)) { -it } + fadeIn(animationSpec = tween(500)))
                    .togetherWith(slideOutHorizontally(animationSpec = tween(500)) { it } + fadeOut(animationSpec = tween(500)))
            } else {
                // Forward to search dashboard: slide from right to left
                (slideInHorizontally(animationSpec = tween(500)) { it } + fadeIn(animationSpec = tween(500)))
                    .togetherWith(slideOutHorizontally(animationSpec = tween(500)) { -it } + fadeOut(animationSpec = tween(500)))
            }
        },
        label = "pageTransition"
    ) { isLanding ->
        if (isLanding) {
            MinimalistLandingPage(
                filterState = filterState,
                locationSuggestions = locationSuggestions,
                isSearchingLocation = isSearchingLocation,
                apiErrorMessage = apiErrorMessage,
                onSearchPlaces = { viewModel.searchPlaces(it) },
                onStartSearch = { query, lat, lon, label, sortMode ->
                    when (filterState.appMode) {
                        AppMode.DIAGNOSTIC -> viewModel.updateTest(query)
                        AppMode.PHARMACY -> viewModel.updateMedicineList(query)
                        AppMode.DOCTOR -> viewModel.updateSymptoms(query)
                    }
                    viewModel.updateLocation(lat, lon, label)
                    viewModel.updateSortMode(sortMode)
                    showLandingPage = false
                },
                onTriggerLocationPermission = {
                    val fineCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    val coarseCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    if (fineCheck == PackageManager.PERMISSION_GRANTED || coarseCheck == PackageManager.PERMISSION_GRANTED) {
                        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                                               locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                        if (!isLocationEnabled) {
                            showGpsSettingsDialog = true
                        } else {
                            Toast.makeText(context, "Acquiring GPS position...", Toast.LENGTH_SHORT).show()
                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                if (location != null) {
                                    fetchGpsLocationAddress(context, location.latitude, location.longitude) { addressLabel ->
                                        viewModel.updateLocation(location.latitude, location.longitude, addressLabel)
                                        Toast.makeText(context, "GPS synced: $addressLabel", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    try {
                                        fusedLocationClient.getCurrentLocation(
                                            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                                            null
                                        ).addOnSuccessListener { freshLocation ->
                                            if (freshLocation != null) {
                                                fetchGpsLocationAddress(context, freshLocation.latitude, freshLocation.longitude) { addressLabel ->
                                                    viewModel.updateLocation(freshLocation.latitude, freshLocation.longitude, addressLabel)
                                                    Toast.makeText(context, "GPS synced: $addressLabel", Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                val tz = java.util.TimeZone.getDefault().id
                                                if (tz.contains("Dhaka") || tz.contains("Asia/Dhaka") || tz.contains("Calcutta") || tz.contains("Kolkata") || tz.contains("Karachi")) {
                                                    viewModel.updateLocation(23.8103, 90.4125, "Dhaka (Fallback GPS)")
                                                    Toast.makeText(context, "GPS unavailable. Matches TimeZone: Dhaka, Bangladesh fallback.", Toast.LENGTH_LONG).show()
                                                } else {
                                                    Toast.makeText(context, "GPS coordinates unavailable, using default region.", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    } catch (e: SecurityException) {
                                        Toast.makeText(context, "GPS permission denied.", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "GPS coordinates unavailable.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }
            )
        } else {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                topBar = {
                    HeaderSection(
                        filterState = filterState,
                        onTriggerLocationPermission = {
                            val fineCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                            val coarseCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            if (fineCheck == PackageManager.PERMISSION_GRANTED || coarseCheck == PackageManager.PERMISSION_GRANTED) {
                                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                                val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                                                       locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                                if (!isLocationEnabled) {
                                    showGpsSettingsDialog = true
                                } else {
                                    Toast.makeText(context, "Acquiring GPS position...", Toast.LENGTH_SHORT).show()
                                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                        if (location != null) {
                                            fetchGpsLocationAddress(context, location.latitude, location.longitude) { addressLabel ->
                                                viewModel.updateLocation(location.latitude, location.longitude, addressLabel)
                                                Toast.makeText(context, "GPS synced: $addressLabel", Toast.LENGTH_SHORT).show()

                                                // Animate camera to fresh GPS location
                                                coroutineScope.launch {
                                                    if (isMapLoaded) {
                                                        try {
                                                            val zoomVal = 14f
                                                            val mapHeight = boxHeight
                                                            val offsetPx = (mapHeight - sheetOffsetY.value) / 2
                                                            val latOffset = calculateLatitudeOffset(location.latitude, zoomVal, density.density, offsetPx)
                                                            cameraPositionState.animate(
                                                                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                                                                    LatLng(location.latitude - latOffset, location.longitude),
                                                                    zoomVal
                                                                )
                                                            )
                                                        } catch (e: Exception) {
                                                            Log.e("Map", "Failed to animate camera", e)
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            try {
                                                fusedLocationClient.getCurrentLocation(
                                                    com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                                                    null
                                                ).addOnSuccessListener { freshLocation ->
                                                    if (freshLocation != null) {
                                                        fetchGpsLocationAddress(context, freshLocation.latitude, freshLocation.longitude) { addressLabel ->
                                                            viewModel.updateLocation(freshLocation.latitude, freshLocation.longitude, addressLabel)
                                                            Toast.makeText(context, "GPS synced: $addressLabel", Toast.LENGTH_SHORT).show()
                                                            
                                                            // Animate camera to fresh GPS location
                                                            coroutineScope.launch {
                                                                if (isMapLoaded) {
                                                                    try {
                                                                        val zoomVal = 14f
                                                                        val mapHeight = boxHeight
                                                                        val offsetPx = (mapHeight - sheetOffsetY.value) / 2
                                                                        val latOffset = calculateLatitudeOffset(freshLocation.latitude, zoomVal, density.density, offsetPx)
                                                                        cameraPositionState.animate(
                                                                            com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                                                                                LatLng(freshLocation.latitude - latOffset, freshLocation.longitude), zoomVal
                                                                            )
                                                                        )
                                                                    } catch (e: Exception) {
                                                                        Log.e("Map", "Failed to animate camera", e)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        Toast.makeText(context, "Could not fetch GPS lock.", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "GPS coordinates unavailable.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            } else {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        onBackToLanding = {
                            showLandingPage = true
                        }
                    )
                },
                contentWindowInsets = WindowInsets.navigationBars
            ) { paddingValues ->
                val topPadding = paddingValues.calculateTopPadding()
                androidx.compose.runtime.LaunchedEffect(topPadding) {
                    topPaddingPx = with(density) { topPadding.toPx() }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(softGreyBgColor())
                        .onGloballyPositioned { coordinates ->
                            boxHeightPx = coordinates.size.height.toFloat()
                        }
                ) {
                    // Map area (Full screen behind the sheet)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            properties = MapProperties(isMyLocationEnabled = false), // Hide default to use custom blue dot
                            uiSettings = MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = false),
                            onMapLoaded = { isMapLoaded = true }
                        ) {
                            val userLatLng = LatLng(filterState.userLatitude, filterState.userLongitude)
                            
                            // Custom Blue Dot Marker.
                            // zIndex keeps it above result pins — pharmacies often sit
                            // metres from the user and the larger pin was covering it.
                            Marker(
                                state = MarkerState(position = userLatLng),
                                title = "My Location",
                                icon = createBlueDotIcon(context),
                                zIndex = 10f
                            )

                            val isPharmacyMode = filterState.appMode == AppMode.PHARMACY
                            val searchedMeds = MedicineCatalog.parseList(filterState.medicineList)

                            enhancedCenters.take(15).forEach { ec ->
                                val rankColors = getRankColors(ec.matchScore)

                                // In pharmacy mode the legend reads "stock chance", so the
                                // pin must show the real estimated stock % — not the
                                // rank-normalised match score, which disagreed with the card.
                                val stockPct = if (isPharmacyMode) {
                                    MedicineCatalog.availabilityForAll(searchedMeds, ec.center.tierLabel)
                                } else 0
                                val pinValue = if (isPharmacyMode) stockPct else ec.matchScore
                                val pinColor =
                                    if (isPharmacyMode) getStockColor(stockPct) else rankColors.pinColor

                                val centerLatLng = LatLng(ec.center.latitude, ec.center.longitude)
                                val isSelected = selectedCenterForDetail?.center?.id == ec.center.id
                                val shouldShowMarker = selectedCenterForDetail == null || isSelected

                                if (shouldShowMarker) {
                                    Marker(
                                        state = MarkerState(position = centerLatLng),
                                        title = ec.center.name,
                                        snippet = if (isPharmacyMode) {
                                            "~$stockPct% in stock (est.)"
                                        } else "${ec.matchScore}% Match",
                                        icon = createMarkerIcon(context, pinValue, pinColor),
                                        onClick = {
                                            selectedCenterForDetail = ec
                                            false
                                        }
                                    )

                                    if (ec.routePoints != null && isSelected) {
                                        Polyline(
                                            points = ec.routePoints,
                                            color = rankColors.pinColor,
                                            width = 12f
                                        )
                                        
                                        if (ec.travelTimeMinutes != null && ec.routePoints.isNotEmpty()) {
                                            val midpoint = ec.routePoints[ec.routePoints.size / 2]
                                            val labelText = "${ec.travelTimeMinutes} min (${String.format(java.util.Locale.US, "%.1f", ec.distanceKm)} km)"
                                            Marker(
                                                state = MarkerState(position = midpoint),
                                                title = "Travel Details",
                                                icon = createTimeMarkerIcon(context, labelText, rankColors.pinColor),
                                                flat = true
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Map Legend in Top Start
                        MapLegend(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp),
                            appMode = filterState.appMode
                        )

                        // 4. Recenter Map Button (Top Right) - Transparent and simple
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .size(44.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null // Remove ripple for truly transparent feel
                                ) {
                                    coroutineScope.launch {
                                        if (isMapLoaded) {
                                            try {
                                                val zoomVal = 14f
                                                val mapHeight = boxHeight
                                                val offsetPx = (mapHeight - sheetOffsetY.value) / 2
                                                val latOffset = calculateLatitudeOffset(filterState.userLatitude, zoomVal, density.density, offsetPx)
                                                val userLatLng = LatLng(filterState.userLatitude - latOffset, filterState.userLongitude)
                                                cameraPositionState.animate(
                                                    com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(userLatLng, zoomVal)
                                                )
                                            } catch (e: Exception) {
                                                Log.e("Map", "Failed to animate camera", e)
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Recenter to my location",
                                tint = MedicalBlue.copy(alpha = 0.6f),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // 2. Sliding recommended centers sheet: now with professional fluid dragging!
                    
                    val draggableState = rememberDraggableState { delta ->
                        coroutineScope.launch {
                            sheetOffsetY.snapTo((sheetOffsetY.value + delta).coerceIn(expandedOffset, collapsedOffset))
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset { IntOffset(0, sheetOffsetY.value.toInt()) }
                            .shadow(16.dp, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                            .draggable(
                                orientation = Orientation.Vertical,
                                state = draggableState,
                                onDragStopped = { velocity ->
                                    // Fling behavior: if user swipes hard, animate to ends. 
                                    // Otherwise, stay exactly where it is (sticky behavior)
                                    if (Math.abs(velocity) > 1000f) {
                                        val target = if (velocity < 0) expandedOffset else collapsedOffset
                                        coroutineScope.launch {
                                            sheetOffsetY.animateTo(target, spring(stiffness = Spring.StiffnessLow))
                                        }
                                    }
                                }
                            ),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(width = 1.dp, color = Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Expand/Collapse drag pill & trigger bar
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { 
                                        coroutineScope.launch {
                                            // Toggle between Expanded and Collapsed
                                            val target = if (sheetOffsetY.value > (expandedOffset + collapsedOffset) / 2) expandedOffset else collapsedOffset
                                            sheetOffsetY.animateTo(target, spring(stiffness = Spring.StiffnessLow))
                                        }
                                    }
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(42.dp)
                                        .height(5.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFCBD5E1))
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (filterState.appMode) {
                                            AppMode.DIAGNOSTIC -> Icons.Default.Star
                                            AppMode.PHARMACY -> Icons.Default.MedicalServices
                                            AppMode.DOCTOR -> Icons.Default.LocalHospital
                                        },
                                        contentDescription = null,
                                        tint = listAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = when (filterState.appMode) {
                                            AppMode.DIAGNOSTIC -> "Recommended Diagnostic Centers"
                                            AppMode.PHARMACY -> "Nearby Pharmacies"
                                            AppMode.DOCTOR -> "Doctors & Clinics Nearby"
                                        },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimaryDark
                                    )
                                }

                                // Doctor mode: show which specialties the typed symptoms
                                // were routed to, and be explicit that it is not a diagnosis.
                                if (filterState.appMode == AppMode.DOCTOR && inferredSpecialties.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            "Suggested:",
                                            fontSize = 10.sp,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.Bold
                                        )
                                        inferredSpecialties.forEach { sp ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(DoctorLight)
                                                    .border(1.dp, DoctorPrimary.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    sp.label,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = DoctorPrimary
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Guidance only — not a medical diagnosis.",
                                        fontSize = 9.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                            // Centers list inside the sliding sheet
                            if (enhancedCenters.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.LocalHospital,
                                            contentDescription = "No centers matched",
                                            tint = Color.LightGray,
                                            modifier = Modifier.size(52.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            when (filterState.appMode) {
                                                AppMode.DIAGNOSTIC -> "No diagnostic centers match your criteria."
                                                AppMode.PHARMACY -> "No pharmacies found nearby."
                                                AppMode.DOCTOR -> "No clinics or doctors found nearby."
                                            },
                                            color = Color.Gray, fontSize = 13.sp
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp),
                                    contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(enhancedCenters, key = { it.center.id }) { ec ->
                                        val isBD = filterState.myLocationLabel.contains("Bangladesh", ignoreCase = true) ||
                                                   filterState.myLocationLabel.contains("Dhaka", ignoreCase = true) ||
                                                   filterState.myLocationLabel.contains("BD", ignoreCase = true) ||
                                                   (filterState.userLatitude in 20.0..27.0 && filterState.userLongitude in 88.0..93.0)
                                        val currencySymbol = if (isBD) "৳" else "$"
                                        DiagnosticCenterCard(
                                            enhanced = ec,
                                            selectedTest = filterState.testName,
                                            appMode = filterState.appMode,
                                            medicineList = filterState.medicineList,
                                            currencySymbol = currencySymbol,
                                            onClick = { selectedCenterForDetail = ec },
                                            onFavToggle = { viewModel.toggleFavorite(ec.center.id, ec.center.isFavorite) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                }
                
                // 3. Popup Overlay Card Detail Panel
                AnimatedVisibility(
                    visible = selectedCenterForDetail != null,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(durationMillis = 400)
                    ) + fadeIn(animationSpec = tween(400)),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(durationMillis = 400)
                    ) + fadeOut(animationSpec = tween(400)),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding())
                ) {
                    selectedCenterForDetail?.let { ec ->
                        DetailedCenterBottomSheet(
                            enhanced = ec,
                            allCenters = enhancedCenters,
                            filterState = filterState,
                            onClose = { selectedCenterForDetail = null },
                            onFavoriteToggle = {
                                viewModel.toggleFavorite(ec.center.id, ec.center.isFavorite)
                                selectedCenterForDetail = ec.copy(
                                    center = ec.center.copy(isFavorite = !ec.center.isFavorite)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MapLegend(modifier: Modifier = Modifier, appMode: AppMode = AppMode.DIAGNOSTIC) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.85f))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        if (appMode == AppMode.PHARMACY) {
            // Pharmacy pins rank by how likely the searched medicines are in stock.
            Text(
                "STOCK CHANCE",
                fontSize = 7.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF64748B),
                letterSpacing = 0.4.sp
            )
            LegendItem("🔵", "Very likely")
            LegendItem("🟢", "Likely")
            LegendItem("🟡", "Uncertain")
            LegendItem("🔴", "Unlikely")
        } else {
            LegendItem("🔵", "Excellent")
            LegendItem("🟢", "Good")
            LegendItem("🟡", "Average")
            LegendItem("🔴", "Poor")
        }
    }
}

@Composable
private fun LegendItem(emoji: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = emoji, fontSize = 9.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label, 
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold, 
            color = Color(0xFF1E293B) // Dark slate
        )
    }
}
