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
fun MinimalistLandingPage(
    filterState: FilterState,
    locationSuggestions: List<com.example.ui.LocationSuggestion>,
    isSearchingLocation: Boolean,
    apiErrorMessage: String,
    onSearchPlaces: (String) -> Unit,
    onStartSearch: (test: String, lat: Double, lon: Double, label: String, sortMode: SortMode) -> Unit,
    onTriggerLocationPermission: () -> Unit
) {
    var areaText by remember { mutableStateOf("") }
    var testText by remember { mutableStateOf("") }
    var medicineText by remember { mutableStateOf("") }
    var symptomText by remember { mutableStateOf("") }
    var selectedSortMode by remember { mutableStateOf(filterState.sortMode) }
    var isAreaFocused by remember { mutableStateOf(false) }

    var selectedLocationCoords by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var selectedLocationLabel by remember { mutableStateOf<String?>(null) }

    var showScannerDialog by remember { mutableStateOf(false) }
    var showVoiceDialog by remember { mutableStateOf(false) }
    var isResolvingLocation by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val viewModel: com.example.ui.DiagnosticViewModel = viewModel()

    // --- SPEECH RECOGNITION LAUNCHER ---
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (spokenText != null) {
                symptomText = spokenText
                viewModel.updateSymptoms(spokenText)
            }
        }
    }

    // --- MODE COLOR ANIMATIONS ---
    val primaryColor by animateColorAsState(
        targetValue = when (filterState.appMode) {
            AppMode.DIAGNOSTIC -> MedicalBlue
            AppMode.PHARMACY -> PharmacyPrimary
            AppMode.DOCTOR -> DoctorPrimary
        },
        animationSpec = tween<Color>(500),
        label = "primaryColor"
    )

    val lightAccentColor by animateColorAsState(
        targetValue = when (filterState.appMode) {
            AppMode.DIAGNOSTIC -> TealLight
            AppMode.PHARMACY -> PharmacyLight
            AppMode.DOCTOR -> DoctorLight
        },
        animationSpec = tween<Color>(500),
        label = "lightAccentColor"
    )

    val gradientColors = when (filterState.appMode) {
        AppMode.DIAGNOSTIC -> listOf(Color(0xFF061B3B), Color(0xFF0D4EA3), Color(0xFF0A101C))
        AppMode.PHARMACY -> listOf(PharmacyGradientStart, PharmacyGradientMid, PharmacyGradientEnd)
        AppMode.DOCTOR -> listOf(DoctorGradientStart, DoctorGradientMid, DoctorGradientEnd)
    }

    // Auto-update area input text when the GPS syncing produces a new location
    androidx.compose.runtime.LaunchedEffect(filterState.myLocationLabel, filterState.userLatitude, filterState.userLongitude) {
        if (filterState.myLocationLabel.isNotEmpty()) {
            areaText = filterState.myLocationLabel
            selectedLocationLabel = filterState.myLocationLabel
            selectedLocationCoords = filterState.userLatitude to filterState.userLongitude
        }
    }

    var triggerAnimate by remember { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        triggerAnimate = true
    }

    val headerAlpha by animateFloatAsState(
        targetValue = if (triggerAnimate) 1f else 0f,
        animationSpec = tween(durationMillis = 650, delayMillis = 100, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "headerAlpha"
    )

    val titleAlpha by animateFloatAsState(
        targetValue = if (triggerAnimate) 1f else 0f,
        animationSpec = tween(durationMillis = 750, delayMillis = 240, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "titleAlpha"
    )
    val titleOffsetY by animateFloatAsState(
        targetValue = if (triggerAnimate) 0f else -45f,
        animationSpec = tween(durationMillis = 750, delayMillis = 240, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "titleOffsetY"
    )
    val titleScale by animateFloatAsState(
        targetValue = if (triggerAnimate) 1f else 1.38f,
        animationSpec = tween(durationMillis = 950, delayMillis = 240, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "titleScale"
    )
    val titleBlur by animateFloatAsState(
        targetValue = if (triggerAnimate) 0f else 18f,
        animationSpec = tween(durationMillis = 850, delayMillis = 240, easing = androidx.compose.animation.core.LinearOutSlowInEasing),
        label = "titleBlur"
    )

    val cardAlpha by animateFloatAsState(
        targetValue = if (triggerAnimate) 1f else 0f,
        animationSpec = tween(durationMillis = 850, delayMillis = 380, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "cardAlpha"
    )
    val cardOffsetY by animateFloatAsState(
        targetValue = if (triggerAnimate) 0f else 45f,
        animationSpec = tween(durationMillis = 850, delayMillis = 380, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "cardOffsetY"
    )
    val cardScale by animateFloatAsState(
        targetValue = if (triggerAnimate) 1f else 0.95f,
        animationSpec = tween(durationMillis = 850, delayMillis = 380, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "cardScale"
    )

    val badge1Alpha by animateFloatAsState(
        targetValue = if (triggerAnimate) 1f else 0f,
        animationSpec = tween(durationMillis = 550, delayMillis = 520, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "badge1Alpha"
    )
    val badge1OffsetY by animateFloatAsState(
        targetValue = if (triggerAnimate) 0f else 20f,
        animationSpec = tween(durationMillis = 550, delayMillis = 520, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "badge1OffsetY"
    )

    val badge2Alpha by animateFloatAsState(
        targetValue = if (triggerAnimate) 1f else 0f,
        animationSpec = tween(durationMillis = 550, delayMillis = 640, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "badge2Alpha"
    )
    val badge2OffsetY by animateFloatAsState(
        targetValue = if (triggerAnimate) 0f else 20f,
        animationSpec = tween(durationMillis = 550, delayMillis = 640, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "badge2OffsetY"
    )

    val badge3Alpha by animateFloatAsState(
        targetValue = if (triggerAnimate) 1f else 0f,
        animationSpec = tween(durationMillis = 550, delayMillis = 760, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "badge3Alpha"
    )
    val badge3OffsetY by animateFloatAsState(
        targetValue = if (triggerAnimate) 0f else 20f,
        animationSpec = tween(durationMillis = 550, delayMillis = 760, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "badge3OffsetY"
    )

    val infiniteTransitionBadge = rememberInfiniteTransition(label = "badge_float")
    val badgeFloater by infiniteTransitionBadge.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badgeFloat"
    )

    val landingGradient = Brush.verticalGradient(colors = gradientColors)

    // Main layout with vertical centering constraints
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(landingGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 40.dp, horizontal = 18.dp)
                .widthIn(max = 415.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elegant Centered branding with staggered character-by-character left-to-right blur-in reveal
            Row(
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .graphicsLayer {
                        alpha = headerAlpha
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val brandText = "NearCare"
                brandText.forEachIndexed { index, char ->
                    val charAlpha by animateFloatAsState(
                        targetValue = if (triggerAnimate) 1f else 0f,
                        animationSpec = tween(
                            durationMillis = 650,
                            delayMillis = 80 + (index * 70),
                            easing = androidx.compose.animation.core.FastOutSlowInEasing
                        ),
                        label = "charAlpha_$index"
                    )
                    val charOffsetY by animateFloatAsState(
                        targetValue = if (triggerAnimate) 0f else -35f,
                        animationSpec = tween(
                            durationMillis = 650,
                            delayMillis = 80 + (index * 70),
                            easing = androidx.compose.animation.core.FastOutSlowInEasing
                        ),
                        label = "charOffsetY_$index"
                    )
                    val charScale by animateFloatAsState(
                        targetValue = if (triggerAnimate) 1f else 1.35f,
                        animationSpec = tween(
                            durationMillis = 800,
                            delayMillis = 80 + (index * 70),
                            easing = androidx.compose.animation.core.FastOutSlowInEasing
                        ),
                        label = "charScale_$index"
                    )
                    val charBlur by animateFloatAsState(
                        targetValue = if (triggerAnimate) 0f else 14f,
                        animationSpec = tween(
                            durationMillis = 750,
                            delayMillis = 80 + (index * 70),
                            easing = androidx.compose.animation.core.LinearOutSlowInEasing
                        ),
                        label = "charBlur_$index"
                    )

                    val charColor = if (index < 4) {
                        val baseColor = primaryColor
                        baseColor.copy(alpha = 0.8f)
                    } else Color.White

                    Text(
                        text = char.toString(),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp,
                        color = charColor,
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = charAlpha
                                translationY = charOffsetY.dp.toPx()
                                scaleX = charScale
                                scaleY = charScale
                            }
                            .blur(if (charBlur > 0.1f) charBlur.dp else 0.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Jargon-free display title - white contrast with dynamic animation
            val titleText = when (filterState.appMode) {
                AppMode.DIAGNOSTIC -> "Find the Best Diagnostic Center Near You"
                AppMode.PHARMACY -> "Find a Nearby Pharmacy for Your Medicine"
                AppMode.DOCTOR -> "Find the Right Doctor Near You"
            }

            Text(
                text = titleText,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .graphicsLayer {
                        alpha = titleAlpha
                        translationY = titleOffsetY.dp.toPx()
                        scaleX = titleScale
                        scaleY = titleScale
                    }
                    .blur(if (titleBlur > 0.1f) titleBlur.dp else 0.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- MODE SELECTOR PILL ---
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(4.dp)
                    .graphicsLayer { alpha = cardAlpha },
                horizontalArrangement = Arrangement.Center
            ) {
                AppMode.entries.forEach { mode ->
                    val isSelected = filterState.appMode == mode
                    
                    val backgroundTransitionColor by animateColorAsState(
                        targetValue = if (isSelected) Color.White else Color.Transparent,
                        animationSpec = tween(400),
                        label = "selectorBg"
                    )

                    val modeColor = when (mode) {
                        AppMode.DIAGNOSTIC -> MedicalBlue
                        AppMode.PHARMACY -> PharmacyPrimary
                        AppMode.DOCTOR -> DoctorPrimary
                    }
                    val label = when (mode) {
                        AppMode.DIAGNOSTIC -> "🏥 Diagnostic"
                        AppMode.PHARMACY -> "💊 Pharmacy"
                        AppMode.DOCTOR -> "🩺 Doctor"
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(backgroundTransitionColor)
                            .clickable { viewModel.updateAppMode(mode) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) modeColor else Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main options Card (does NOT float, stays perfectly grounded for sharp, stable typing input)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = cardAlpha
                        translationY = cardOffsetY.dp.toPx()
                        scaleX = cardScale
                        scaleY = cardScale
                    }
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(width = 1.dp, color = Color(0xFFF1F5F9))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    // Option 1: Area Selection TextField
                    Text(
                        text = "1. ENTER YOUR CLINICAL AREA",
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .zIndex(10f)
                    ) {
                        OutlinedTextField(
                            value = areaText,
                            onValueChange = {
                                areaText = it
                                isAreaFocused = true
                                onSearchPlaces(it)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { isAreaFocused = it.isFocused }
                                .testTag("landing_area_search"),
                            placeholder = { Text("Enter area...", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Area Locator Pin",
                                    tint = primaryColor
                                )
                            },
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isSearchingLocation) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = primaryColor,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            onTriggerLocationPermission()
                                            isAreaFocused = false
                                        },
                                        modifier = Modifier.testTag("landing_gps_trigger")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MyLocation,
                                            contentDescription = "Trigger GPS permission and sync location",
                                            tint = primaryColor
                                        )
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF1E293B),
                                unfocusedTextColor = Color(0xFF1E293B),
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedContainerColor = SoftGreyBg,
                                unfocusedContainerColor = SoftGreyBg
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Floating suggestions dropdown menu
                        if (isAreaFocused && areaText.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 62.dp) // Adjusted to ensure it's below the text field
                                    .shadow(16.dp, RoundedCornerShape(12.dp))
                                    .zIndex(20f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    if (apiErrorMessage.startsWith("Google API Error")) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = null,
                                                tint = Color.Red,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = apiErrorMessage,
                                                color = Color.Red,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    if (isSearchingLocation) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                color = primaryColor,
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "Searching places in Bangladesh...",
                                                color = Color.Gray,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    if (locationSuggestions.isNotEmpty()) {
                                        locationSuggestions.forEach { suggestion ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        areaText = suggestion.label
                                                        selectedLocationLabel = suggestion.label
                                                        isAreaFocused = false
                                                        
                                                        // Fetch precise coordinates for the selected place
                                                        isResolvingLocation = true
                                                        coroutineScope.launch {
                                                            try {
                                                                val coords = viewModel.getPlaceCoordinates(suggestion.placeId)
                                                                if (coords != null) {
                                                                    selectedLocationCoords = coords
                                                                }
                                                            } catch (e: Exception) {
                                                                Log.e("LandingPage", "Error resolving location", e)
                                                            } finally {
                                                                isResolvingLocation = false
                                                            }
                                                        }
                                                    }
                                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.LocationOn,
                                                    contentDescription = null,
                                                    tint = primaryColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = suggestion.label,
                                                    color = Color(0xFF1E293B),
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    } else if (!isSearchingLocation) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Info,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "No results found. Try typing a city name.",
                                                color = Color.Gray,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // --- SMOOTH CONTENT CROSSFADE ---
                    Crossfade(targetState = filterState.appMode, label = "CardContentFade") { mode ->
                        Column {
                            val option2Label = when (mode) {
                                AppMode.DIAGNOSTIC -> "2. SEARCH TEST OR SCAN REPORT PHOTO"
                                AppMode.PHARMACY -> "2. UPLOAD RX OR TYPE MEDICINE LIST"
                                AppMode.DOCTOR -> "2. DESCRIBE YOUR SYMPTOMS"
                            }
                            val option2Placeholder = when (mode) {
                                AppMode.DIAGNOSTIC -> "Search test..."
                                AppMode.PHARMACY -> "Medicine list (e.g. Napa, Sergel)..."
                                AppMode.DOCTOR -> "e.g. High fever, chest pain..."
                            }

                            Text(
                                text = option2Label,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            if (mode == AppMode.DOCTOR) {
                                OutlinedTextField(
                                    value = symptomText,
                                    onValueChange = { 
                                        symptomText = it
                                        viewModel.updateSymptoms(it)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("landing_symptom_search"),
                                    placeholder = { Text(option2Placeholder, fontSize = 13.sp) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.MedicalServices,
                                            contentDescription = "Symptom Icon",
                                            tint = primaryColor
                                        )
                                    },
                                    trailingIcon = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(onClick = { showVoiceDialog = true }) {
                                                Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = primaryColor)
                                            }
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B),
                                        focusedBorderColor = primaryColor,
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedContainerColor = SoftGreyBg,
                                        unfocusedContainerColor = SoftGreyBg
                                    ),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            } else {
                                val textFieldValue = if (mode == AppMode.DIAGNOSTIC) testText else medicineText
                                OutlinedTextField(
                                    value = textFieldValue,
                                    onValueChange = { 
                                        if (mode == AppMode.DIAGNOSTIC) {
                                            testText = it
                                            viewModel.updateTest(it)
                                        } else {
                                            medicineText = it
                                            viewModel.updateMedicineList(it)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("landing_main_search"),
                                    placeholder = { Text(option2Placeholder, fontSize = 13.sp) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (mode == AppMode.DIAGNOSTIC) Icons.Default.Search else Icons.Default.MedicalServices,
                                            contentDescription = "Search Icon",
                                            tint = primaryColor
                                        )
                                    },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = { showScannerDialog = true },
                                            modifier = Modifier.testTag("landing_camera_btn")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PhotoCamera,
                                                contentDescription = "Capture prescription",
                                                tint = if (mode == AppMode.DIAGNOSTIC) CoralAccent else primaryColor
                                            )
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B),
                                        focusedBorderColor = primaryColor,
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedContainerColor = SoftGreyBg,
                                        unfocusedContainerColor = SoftGreyBg
                                    ),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Option 3: Mode-specific criteria
                            if (mode == AppMode.DOCTOR) {
                                Text(
                                    text = "3. SELECT SPECIALTY",
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    DoctorSpecialty.entries.forEach { specialty ->
                                        val isSelected = filterState.doctorSpecialty == specialty
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isSelected) primaryColor else SoftGreyBg)
                                                .border(
                                                    width = 1.dp,
                                                    color = if (isSelected) primaryColor else Color(0xFFCBD5E1),
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .clickable { viewModel.updateDoctorSpecialty(specialty) }
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = specialty.label,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else TextSecondaryDark
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "3. OPTIMIZER METRIC CRITERIA",
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Price sorting only means something where prices exist
                                    // (diagnostic tests). Pharmacy rows carry no prices.
                                    val sortModes = if (mode == AppMode.DIAGNOSTIC) {
                                        SortMode.entries.toList()
                                    } else {
                                        SortMode.entries.filter { it != SortMode.PRICE_LOW_TO_HIGH }
                                    }
                                    sortModes.forEach { sm ->
                                        val isSelected = selectedSortMode == sm
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isSelected) primaryColor else SoftGreyBg)
                                                .border(
                                                    width = 1.dp,
                                                    color = if (isSelected) primaryColor else Color(0xFFCBD5E1),
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .clickable { 
                                                    selectedSortMode = sm 
                                                    viewModel.updateSortMode(sm)
                                                }
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = getSortModeLabel(sm),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else TextSecondaryDark
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Mode-specific search button
                    val searchBtnLabel = when (filterState.appMode) {
                        AppMode.DIAGNOSTIC -> "Find Best Centers"
                        AppMode.PHARMACY -> "Find Nearby Pharmacies"
                        AppMode.DOCTOR -> "Find Nearby Doctors"
                    }

                    Button(
                        onClick = {
                            val finalQuery = when (filterState.appMode) {
                                AppMode.DIAGNOSTIC -> testText
                                AppMode.PHARMACY -> medicineText
                                AppMode.DOCTOR -> symptomText
                            }
                            if (selectedLocationCoords != null) {
                                onStartSearch(finalQuery, selectedLocationCoords!!.first, selectedLocationCoords!!.second, selectedLocationLabel ?: areaText, selectedSortMode)
                            } else {
                                isResolvingLocation = true
                                coroutineScope.launch {
                                    val result = fetchCoordinatesFromGoogle(areaText)
                                    isResolvingLocation = false
                                    if (result != null) {
                                        onStartSearch(finalQuery, result.first, result.second, areaText, selectedSortMode)
                                    } else {
                                        Toast.makeText(context, "Could not locate '$areaText'. Please try again.", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("landing_search_btn"),
                        enabled = areaText.isNotEmpty() && !isResolvingLocation
                    ) {
                        if (isResolvingLocation) {
                             CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search results",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = searchBtnLabel,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Professional clinical metrics statistics grid at bottom of landing page - glass style with staggered pop up animations and subtle bobbing float effects
            val stats = when (filterState.appMode) {
                AppMode.DIAGNOSTIC -> listOf("500+" to "LAB CENTERS", "35+" to "CLINICAL TESTS", "10K+" to "PATIENTS SERVED")
                AppMode.PHARMACY -> listOf("800+" to "PHARMACIES", "50K+" to "MEDICINES", "24/7" to "AVAILABILITY")
                AppMode.DOCTOR -> listOf("1200+" to "DOCTORS", "25+" to "SPECIALTIES", "15K+" to "CONSULTATIONS")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LandingStatsBadge(
                    bigText = stats[0].first,
                    subText = stats[0].second,
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            alpha = badge1Alpha
                            translationY = (badge1OffsetY + badgeFloater).dp.toPx()
                        }
                )
                LandingStatsBadge(
                    bigText = stats[1].first,
                    subText = stats[1].second,
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            alpha = badge2Alpha
                            translationY = (badge2OffsetY - badgeFloater).dp.toPx()
                        }
                )
                LandingStatsBadge(
                    bigText = stats[2].first,
                    subText = stats[2].second,
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            alpha = badge3Alpha
                            translationY = (badge3OffsetY + badgeFloater * 0.8f).dp.toPx()
                        }
                )
            }
        }
    }

    // Full-screen Prescription Scanner Overlay
    AnimatedVisibility(
        visible = showScannerDialog,
        enter = fadeIn() + slideInHorizontally { it },
        exit = fadeOut() + slideOutHorizontally { it }
    ) {
        PrescriptionScanner(
            onDismiss = { showScannerDialog = false },
            onCaptured = { capturedTests ->
                testText = capturedTests
                showScannerDialog = false
                Toast.makeText(context, "Prescription scanned successfully!", Toast.LENGTH_LONG).show()
            }
        )
    }

    // In-app voice capture for symptoms (Doctor mode)
    if (showVoiceDialog) {
        AiVoiceDialog(
            accent = primaryColor,
            onDismiss = { showVoiceDialog = false },
            onAccept = { spoken ->
                if (spoken.isNotBlank()) {
                    symptomText = spoken
                    viewModel.updateSymptoms(spoken)
                }
                showVoiceDialog = false
            }
        )
    }
}

suspend fun fetchCoordinatesFromGoogle(query: String): Pair<Double, Double>? = withContext(Dispatchers.IO) {
    try {
        val apiKey = AppConfig.getMapsApiKey()
        val client = OkHttpClient()
        val url = "https://maps.googleapis.com/maps/api/geocode/json" +
                "?address=${android.net.Uri.encode(query)}" +
                "&region=BD" +
                "&components=country:BD" +
                "&key=$apiKey"
                
        val request = Request.Builder()
            .url(url)
            .header("X-Android-Package", "com.aistudio.diagnosticfinder.zkwpqd")
            .header("X-Android-Cert", "A55E38E3CC22590D6028A3B17FAA1D60866F7BAB")
            .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val body = response.body?.string() ?: ""
            val json = JSONObject(body)
            if (json.optString("status") == "OK") {
                val results = json.optJSONArray("results")
                if (results != null && results.length() > 0) {
                    val first = results.getJSONObject(0)
                    val geom = first.optJSONObject("geometry")
                    val loc = geom?.optJSONObject("location")
                    if (loc != null) {
                        return@withContext Pair(loc.optDouble("lat", 0.0), loc.optDouble("lng", 0.0))
                    }
                }
            }
        }
    } catch (e: Exception) {
        Log.e("Geocoding", "Google Geocode error", e)
    }
    null
}

@Composable
fun LandingStatsBadge(bigText: String, subText: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
        border = androidx.compose.foundation.BorderStroke(width = 1.6.dp, color = Color.White.copy(alpha = 0.22f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = bigText,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subText,
                color = Color(0xFFE2F0FF),
                fontSize = 8.sp,
                lineHeight = 10.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
