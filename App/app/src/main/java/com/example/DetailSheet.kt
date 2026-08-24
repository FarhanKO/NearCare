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
fun DetailedCenterBottomSheet(
    enhanced: EnhancedCenter,
    allCenters: List<EnhancedCenter>,
    filterState: FilterState,
    onClose: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    val center = enhanced.center
    val context = LocalContext.current
    
    val isBD = filterState.myLocationLabel.contains("Bangladesh", ignoreCase = true) ||
               filterState.myLocationLabel.contains("Dhaka", ignoreCase = true) ||
               filterState.myLocationLabel.contains("BD", ignoreCase = true) ||
               (filterState.userLatitude in 20.0..27.0 && filterState.userLongitude in 88.0..93.0)
    val currencySymbol = if (isBD) "৳" else "$"

    val swipeOffsetY = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(0, swipeOffsetY.value.coerceAtLeast(0f).toInt()) }
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(SoftGreyBg)
            .border(width = 1.dp, color = Color(0xFFCAC4D0), shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Drag handle & Non-scrollable header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            coroutineScope.launch {
                                swipeOffsetY.snapTo(swipeOffsetY.value + delta)
                            }
                        },
                        onDragStopped = { velocity ->
                            if (swipeOffsetY.value > 300f || velocity > 1000f) {
                                coroutineScope.launch {
                                    swipeOffsetY.animateTo(screenHeightPx, spring(stiffness = Spring.StiffnessMedium))
                                    onClose()
                                }
                            } else {
                                coroutineScope.launch {
                                    swipeOffsetY.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
                                }
                            }
                        }
                    )
                    .padding(top = 12.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Drag handle pill
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCBD5E1))
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = center.name,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = TextPrimaryDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onFavoriteToggle,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (center.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (center.isFavorite) CoralAccent else TextSecondaryDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(TealLight)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close details panel",
                            tint = TextPrimaryDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                Text(center.address, fontSize = 12.sp, color = TextSecondaryDark)
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)

            // Scrollable Content Column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding()
            ) {

            // Certification alert (diagnostic labs only)
            if (filterState.appMode == AppMode.DIAGNOSTIC) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (center.certified) TealLight else Color(0xFFFFF9E6))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (center.certified) Icons.Outlined.CheckCircle else Icons.Outlined.Info,
                    contentDescription = null,
                    tint = if (center.certified) TealPrimary else GoldAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (center.certified) "NABL Accredited & Certified" else "State Board Approved Medical Lab",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (center.certified) TealPrimary else Color(0xFFB7791F)
                    )
                    Text(
                        text = if (center.certified) "High precision testing standards matching international WHO clinical accuracy standards." 
                               else "Meets standard healthcare regulations for diagnostic screenings.",
                        fontSize = 9.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            }

            // Call / Navigate action grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Call Buttons
                Button(
                    onClick = {
                        val cleanPhone = center.phone.trim()
                        if (cleanPhone.isEmpty() || cleanPhone.equals("Tap to call", ignoreCase = true) || !cleanPhone.any { it.isDigit() }) {
                            Toast.makeText(context, "Phone contact number not available for this center", Toast.LENGTH_SHORT).show()
                        } else {
                            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${cleanPhone}"))
                            context.startActivity(dialIntent)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        when (filterState.appMode) {
                            AppMode.DIAGNOSTIC -> "Call Lab"
                            AppMode.PHARMACY -> "Call Pharmacy"
                            AppMode.DOCTOR -> "Call Clinic"
                        },
                        fontSize = 12.sp
                    )
                }

                // Open maps navigation link
                Button(
                    onClick = {
                        val mapUri = Uri.parse("geo:${center.latitude},${center.longitude}?q=${Uri.encode(center.name)}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            // general web map browser backup
                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${center.latitude},${center.longitude}"))
                            context.startActivity(webIntent)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = TealSecondary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Directions, contentDescription = "Get directions", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Directions", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // --- Insights / EDA panel (mode-aware) ---
            SelectedItemInsights(
                enhanced = enhanced,
                allCenters = allCenters,
                appMode = filterState.appMode,
                selectedTest = filterState.testName,
                medicineList = filterState.medicineList,
                isBD = isBD,
                currencySymbol = currencySymbol
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Pharmacy stock outlook and the busyness gauge are rendered BELOW the
            // map section further down.

            // Doctor: which specialty this place matched, plus a clear disclaimer
            if (filterState.appMode == AppMode.DOCTOR) {
                Text("Specialty match:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF2C3E50))
                Spacer(modifier = Modifier.height(6.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DoctorLight)
                        .border(1.dp, DoctorPrimary.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        center.specialtyLabel.ifEmpty { "General Clinic" },
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = DoctorPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "A clinic or practice matching this specialty. Individual doctor names, chamber hours and availability are not available from maps data — please call to confirm.",
                        fontSize = 10.sp,
                        color = TextSecondaryDark
                    )
                    if (filterState.symptoms.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Matched from your symptoms: \"${filterState.symptoms}\"",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DoctorPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Guidance only — not a medical diagnosis. In an emergency, call your local emergency number.",
                            fontSize = 9.sp,
                            color = Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // The old "All the Test Prices" catalogue was removed: it listed only two
            // entries (CBC and MRI Brain) generated from a hash of the place name, so
            // it was invented data rather than anything the centre actually charges.

            // Doctor: consultation fee. No maps or public API exposes physician fees,
            // so we never invent a number — we say so and offer to call.
            if (filterState.appMode == AppMode.DOCTOR) {
                Text("Consultation fee:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF2C3E50))
                Spacer(modifier = Modifier.height(6.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Not published",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = TextPrimaryDark
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DoctorPrimary.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                "Call to confirm",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = DoctorPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Consultation fees vary by doctor, chamber and visit type (new vs follow-up), " +
                            "and are not available from maps data. Please call the chamber to confirm " +
                            "before you travel.",
                        fontSize = 10.sp,
                        color = TextSecondaryDark,
                        lineHeight = 15.sp
                    )
                    if (center.phone.trim().any { it.isDigit() }) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val dialIntent = Intent(
                                    Intent.ACTION_DIAL,
                                    Uri.parse("tel:${center.phone.trim()}")
                                )
                                context.startActivity(dialIntent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DoctorPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ask about the fee", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    tint = MedicalBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Map Location:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF2C3E50)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            ) {
                val centerLatLng = LatLng(center.latitude, center.longitude)
                val userLatLng = LatLng(filterState.userLatitude, filterState.userLongitude)
                var isInnerMapLoaded by remember { mutableStateOf(false) }
                
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(centerLatLng, 14f)
                }

                val routePoints = enhanced.routePoints ?: listOf(userLatLng, centerLatLng)
                val travelTime = enhanced.travelTimeMinutes ?: (enhanced.distanceKm * 2.0).toInt().coerceAtLeast(1)

                LaunchedEffect(isInnerMapLoaded) {
                    if (isInnerMapLoaded) {
                        try {
                            val midpoint = LatLng(
                                (userLatLng.latitude + centerLatLng.latitude) / 2,
                                (userLatLng.longitude + centerLatLng.longitude) / 2
                            )
                            val bearing = calculateBearing(
                                              userLatLng.latitude, userLatLng.longitude,
                                              centerLatLng.latitude, centerLatLng.longitude
                                            )
                            val zoom = (15.3f - Math.log(enhanced.distanceKm.coerceAtLeast(0.1)) / Math.log(2.0)).toFloat().coerceIn(10f, 18.5f)
                                            
                            val cameraPosition = com.google.android.gms.maps.model.CameraPosition.builder()
                                .target(midpoint)
                                .zoom(zoom)
                                .bearing(bearing + 90f)
                                .build()
                                            
                            cameraPositionState.animate(
                                com.google.android.gms.maps.CameraUpdateFactory.newCameraPosition(cameraPosition)
                            )
                        } catch (e: Exception) {
                            Log.e("Map", "Failed to fit inner map bounds", e)
                        }
                    }
                }
                
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = false),
                    uiSettings = MapUiSettings(zoomControlsEnabled = false),
                    onMapLoaded = { isInnerMapLoaded = true }
                ) {
                    val rankColors = getRankColors(enhanced.matchScore)
                    
                    Marker(
                        state = MarkerState(position = userLatLng),
                        title = "My Location",
                        icon = createBlueDotIcon(context)
                    )

                    Marker(
                        state = MarkerState(position = centerLatLng),
                        title = center.name,
                        icon = createMarkerIcon(context, enhanced.matchScore, rankColors.pinColor)
                    )

                    Polyline(
                        points = routePoints,
                        color = rankColors.pinColor,
                        width = 10f
                    )

                    val midpoint = routePoints[routePoints.size / 2]
                    val labelText = "$travelTime min (${String.format(java.util.Locale.US, "%.1f", enhanced.distanceKm)} km)"
                    Marker(
                        state = MarkerState(position = midpoint),
                        title = "Travel Details",
                        icon = createTimeMarkerIcon(context, labelText, rankColors.pinColor),
                        flat = true
                    )
                }

                // Obscure the Google logo watermark at the bottom-left corner of the map
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 0.dp, bottom = 0.dp)
                        .width(82.dp)
                        .height(26.dp)
                        .background(Color(0xFFEAEEF2), RoundedCornerShape(topEnd = 6.dp))
                )
            }

            // --- Below the map ---

            if (filterState.appMode == AppMode.PHARMACY) {
                Spacer(modifier = Modifier.height(18.dp))
                PharmacyStockOutlook(
                    center = center,
                    medicineList = filterState.medicineList
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            BusynessGauge(
                center = center,
                accent = when (filterState.appMode) {
                    AppMode.DIAGNOSTIC -> MedicalBlue
                    AppMode.PHARMACY -> PharmacyPrimary
                    AppMode.DOCTOR -> DoctorPrimary
                }
            )
        }
    }
}
}

/**
 * Mode-aware insight / EDA panel shown under a selected result. All figures are
 * computed on-device from the current result set (allCenters) — percentiles,
 * rank, and deviation from the nearby average — so nothing new is fabricated;
 * it just summarizes what's already on screen.
 */
