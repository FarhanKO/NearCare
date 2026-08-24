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


@Composable
fun SelectedItemInsights(
    enhanced: EnhancedCenter,
    allCenters: List<EnhancedCenter>,
    appMode: AppMode,
    selectedTest: String,
    medicineList: String,
    isBD: Boolean,
    currencySymbol: String
) {
    val accent = when (appMode) {
        AppMode.DIAGNOSTIC -> MedicalBlue
        AppMode.PHARMACY -> PharmacyPrimary
        AppMode.DOCTOR -> DoctorPrimary
    }
    val center = enhanced.center
    val ctx = LocalContext.current
    val priceScope = rememberCoroutineScope()
    var loadingPrices by remember(center.id) { mutableStateOf(false) }
    var priceResult by remember(center.id) {
        mutableStateOf<com.example.data.api.PriceOutcome?>(null)
    }
    val n = allCenters.size

    // Section header
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.QueryStats, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Insights", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF2C3E50))
        Spacer(modifier = Modifier.width(6.dp))
        if (n > 1) Text("compared to $n nearby", fontSize = 10.sp, color = Color.Gray)
    }
    Spacer(modifier = Modifier.height(8.dp))

    // Quick stat tiles (all modes)
    val travel = enhanced.travelTimeMinutes ?: (enhanced.distanceKm * 2).toInt().coerceAtLeast(1)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatTile(
            Modifier.weight(1f), accent, Icons.Default.Star,
            if (center.reviewsCount > 0)
                String.format(java.util.Locale.US, "%.1f", center.rating) else "—",
            if (center.reviewsCount > 0) "${center.reviewsCount} reviews" else "no rating"
        )
        StatTile(Modifier.weight(1f), accent, Icons.Default.LocationOn,
            "${String.format(java.util.Locale.US, "%.1f", enhanced.distanceKm)}km", "$travel min drive")
        StatTile(Modifier.weight(1f), accent, Icons.Outlined.Timer,
            "${center.estimatedWaitMinutes}m", "avg wait")
        StatTile(Modifier.weight(1f), accent, Icons.Default.QueryStats,
            "${enhanced.matchScore}%", "match")
    }

    // ── Price / fee, resolved through the cheapest-first cascade ──
    // cache -> curated rate card -> the place's own website -> grounded AI search.
    // Published figures carry a real source link; anything approximate is tagged EST.
    run {
        val requested: List<String> = when (appMode) {
            AppMode.DIAGNOSTIC -> selectedTest.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            AppMode.PHARMACY -> MedicineCatalog.parseList(medicineList)
            AppMode.DOCTOR -> listOf("Consultation fee")
        }
        val kind = when (appMode) {
            AppMode.DIAGNOSTIC -> "diagnostic_centre"
            AppMode.PHARMACY -> "pharmacy"
            AppMode.DOCTOR -> "doctor"
        }
        val heading = when (appMode) {
            AppMode.DIAGNOSTIC -> "Test prices"
            AppMode.PHARMACY -> "Medicine prices"
            AppMode.DOCTOR -> "Consultation fee"
        }

        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.MedicalServices,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(heading, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = TextPrimaryDark)
                Spacer(modifier = Modifier.weight(1f))
                (priceResult as? com.example.data.api.PriceOutcome.Found)?.let { f ->
                    Text(f.origin.label, fontSize = 8.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (priceResult == null && !loadingPrices) {
                Text(
                    if (requested.isEmpty())
                        "Add what you need on the search screen, then look up real prices."
                    else
                        "We do not invent prices. Look up what this place actually charges.",
                    fontSize = 10.sp,
                    color = TextSecondaryDark,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (loadingPrices) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AiTextLoading(
                        texts = listOf(
                            "Checking saved results...",
                            "Reading their website...",
                            "Searching the web...",
                            "Almost..."
                        ),
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            when (val result = priceResult) {
                is com.example.data.api.PriceOutcome.Found -> {
                    result.items.forEach { line ->
                        val published = line.isPublished
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (published) Color(0xFF15803D) else Color(0xFF92400E))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    line.item,
                                    fontSize = 12.sp,
                                    color = TextPrimaryDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (!published && line.basisNote.isNotBlank()) {
                                    Text(line.basisNote, fontSize = 8.sp, color = Color.Gray, maxLines = 2)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            if (!published) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(Color(0xFF92400E).copy(alpha = 0.14f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text("EST", fontSize = 7.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF92400E))
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                line.price,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (published) Color(0xFF15803D) else Color(0xFF92400E)
                            )
                        }
                        if (published && line.source.isNotBlank()) {
                            Text(
                                text = line.source,
                                fontSize = 8.sp,
                                color = MedicalBlue,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, bottom = 4.dp)
                                    .clickable {
                                        runCatching {
                                            ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(line.source)))
                                        }
                                    }
                            )
                        }
                    }
                    if (result.note.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(result.note, fontSize = 9.sp, color = Color.Gray, lineHeight = 13.sp)
                    }
                }

                is com.example.data.api.PriceOutcome.NotFound -> {
                    Text(result.reason, fontSize = 10.sp, color = TextSecondaryDark, lineHeight = 14.sp)
                }

                is com.example.data.api.PriceOutcome.Failed -> {
                    Text(
                        "Couldn't check right now: " + result.message,
                        fontSize = 10.sp,
                        color = CoralAccent,
                        lineHeight = 14.sp
                    )
                }

                null -> Unit
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (center.websiteUri.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            runCatching {
                                ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(center.websiteUri)))
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Outlined.Language, contentDescription = null, modifier = Modifier.size(13.dp), tint = accent)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Website", fontSize = 10.sp, color = accent, fontWeight = FontWeight.Bold)
                    }
                }

                // Always-free fallback: hand the real web search to the user.
                OutlinedButton(
                    onClick = {
                        val query = "\"" + center.name + "\" " + requested.joinToString(" ") + " price"
                        runCatching {
                            ctx.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.google.com/search?q=" + Uri.encode(query))
                                )
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(13.dp), tint = accent)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Web", fontSize = 10.sp, color = accent, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        if (loadingPrices) return@Button
                        loadingPrices = true
                        priceResult = null
                        priceScope.launch {
                            priceResult = com.example.data.api.PriceRepository.getPrices(
                                context = ctx,
                                placeId = center.id.toString(),
                                placeName = center.name,
                                area = center.address,
                                websiteUri = center.websiteUri,
                                kind = kind,
                                items = requested
                            )
                            loadingPrices = false
                        }
                    },
                    modifier = Modifier.weight(1.2f),
                    enabled = !loadingPrices,
                    colors = ButtonDefaults.buttonColors(containerColor = accent),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        if (priceResult == null) "Find prices" else "Refresh",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // "How it ranks" chips
    if (n >= 2) {
        val ratingRank = 1 + allCenters.count { it.center.rating > center.rating }
        val distRank = 1 + allCenters.count { it.distanceKm < enhanced.distanceKm }
        val priceRank = 1 + allCenters.count { it.testPrice in 0.0001..(enhanced.testPrice - 0.0001) }

        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (appMode == AppMode.DIAGNOSTIC && enhanced.testPrice > 0.0) {
                RankChip(Modifier.weight(1f), accent, "cheapest", priceRank, n)
            }
            RankChip(Modifier.weight(1f), accent, "top rated", ratingRank, n)
            RankChip(Modifier.weight(1f), accent, "closest", distRank, n)
        }
    }

}

/** Pharmacy stock outlook — shown under the map on the detail sheet. */
@Composable
fun PharmacyStockOutlook(
    center: com.example.data.model.DiagnosticCenter,
    medicineList: String
) {
    val meds = MedicineCatalog.parseList(medicineList)
    val overallPct = MedicineCatalog.availabilityForAll(meds, center.tierLabel)

    Text("Stock outlook:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF2C3E50))
    Spacer(modifier = Modifier.height(6.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PharmacyLight)
            .border(1.dp, PharmacyPrimary.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "~$overallPct%",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = getStockColor(overallPct)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(getStockColor(overallPct).copy(alpha = 0.14f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    MedicineCatalog.stockLabel(overallPct),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = getStockColor(overallPct)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text("estimate", fontSize = 9.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            if (meds.isEmpty()) {
                "Type: ${center.tierLabel.ifEmpty { "Pharmacy" }}. Add your medicines on the search screen for a per-medicine estimate."
            } else {
                "Chance of getting ALL ${meds.size} item(s) here — set by the hardest one to find. Type: ${center.tierLabel.ifEmpty { "Pharmacy" }}."
            },
            fontSize = 10.sp,
            color = TextSecondaryDark
        )

        if (meds.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Per medicine", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            meds.forEach { med ->
                val rarity = MedicineCatalog.rarityOf(med)
                val pct = MedicineCatalog.availability(rarity, center.tierLabel)
                val known = MedicineCatalog.isKnown(med)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(getStockColor(pct))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(med, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        Text(
                            if (known) "${rarity.label} — ${rarity.note}"
                            else "Not in catalogue — assumed ${rarity.label.lowercase()}",
                            fontSize = 8.sp,
                            color = Color.Gray
                        )
                    }
                    Text(
                        "~$pct%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = getStockColor(pct)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Estimated from each medicine's rarity and this pharmacy's size — not live stock. Call ahead before travelling.",
                fontSize = 9.sp,
                color = Color.Gray
            )
        }
    }
}

/** Busyness gauge — shown under the map on the detail sheet. */
@Composable
fun BusynessGauge(
    center: com.example.data.model.DiagnosticCenter,
    accent: Color
) {
    val crowdPct = center.crowdPercentage.coerceIn(0, 100)
    if (crowdPct <= 0) return

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Outlined.Group,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            "Busyness — ${center.crowdLevel}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C3E50)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text("$crowdPct%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accent)
    }
    Spacer(modifier = Modifier.height(6.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color(0xFFE2E8F0))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth((crowdPct / 100f).coerceIn(0.02f, 1f))
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(
                    when {
                        crowdPct < 40 -> Color(0xFF15803D)
                        crowdPct < 70 -> Color(0xFFF59E0B)
                        else -> CoralAccent
                    }
                )
        )
    }
}

@Composable
private fun StatTile(
    modifier: Modifier,
    accent: Color,
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.height(3.dp))
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = TextPrimaryDark, maxLines = 1)
        Text(label, fontSize = 8.sp, color = Color.Gray, maxLines = 1, textAlign = TextAlign.Center)
    }
}

@Composable
private fun RankChip(
    modifier: Modifier,
    accent: Color,
    label: String,
    rank: Int,
    total: Int
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(alpha = 0.08f))
            .border(1.dp, accent.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("#$rank", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = accent)
        Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, maxLines = 1)
        Text("of $total", fontSize = 8.sp, color = Color.Gray)
    }
}
