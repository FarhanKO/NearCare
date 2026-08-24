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

// UNUSED / LEGACY SCREENS
//
// Every composable in this file has ZERO callers (verified by reference search):
// MedicalTabs, PresetChip, ComparisonDashboard, RadarSatelliteView, AIChatAdvisor,
// PrescriptionCard, size16, CustomProximityMap, StatsBadge, SimulatedStreetMap,
// GoogleMapView, GoogleOverviewMapView, NativeClinicalRadarMapView.
//
// They are quarantined here rather than deleted so nothing is lost silently.
// Safe to delete this whole file.

@Composable
fun MedicalTabs(activeTab: Int, onTabSelected: (Int) -> Unit) {
    TabRow(
        selectedTabIndex = activeTab,
        containerColor = Color.White,
        contentColor = TealPrimary,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                color = TealPrimary
            )
        },
        modifier = Modifier.fillMaxWidth().shadow(2.dp)
    ) {
        Tab(
            selected = activeTab == 0,
            onClick = { onTabSelected(0) },
            text = { Text("Compare List", fontWeight = FontWeight.SemiBold, fontSize = 14.sp) },
            icon = { Icon(Icons.Default.MedicalServices, contentDescription = "Comparison List", modifier = Modifier.size(20.dp)) },
            modifier = Modifier.testTag("tab_compare_list")
        )
        Tab(
            selected = activeTab == 1,
            onClick = { onTabSelected(1) },
            text = { Text("Radar Map", fontWeight = FontWeight.SemiBold, fontSize = 14.sp) },
            icon = { Icon(Icons.Default.Map, contentDescription = "Radar Map Grid", modifier = Modifier.size(20.dp)) },
            modifier = Modifier.testTag("tab_radar_map")
        )
        Tab(
            selected = activeTab == 2,
            onClick = { onTabSelected(2) },
            text = { Text("AI Advisor", fontWeight = FontWeight.SemiBold, fontSize = 14.sp) },
            icon = { Icon(Icons.Default.SmartToy, contentDescription = "AI Recommender Bot", modifier = Modifier.size(20.dp)) },
            modifier = Modifier.testTag("tab_ai_advisor")
        )
    }
}

@Composable
fun PresetChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) GoldAccent else Color.White)
            .border(width = 1.dp, color = if (isSelected) TealPrimary else Color(0xFFCAC4D0), shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) TealPrimary else TextSecondaryDark,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonDashboard(
    viewModel: DiagnosticViewModel,
    filterState: FilterState,
    centers: List<EnhancedCenter>,
    onCenterClick: (EnhancedCenter) -> Unit
) {
    val testList = listOf(
        "Complete Blood Count (CBC)",
        "Lipid Profile (Cholesterols)",
        "MRI Brain (Magnetic Resonance)",
        "X-Ray Chest (Digital Chest)",
        "Glucose Fasting (Diabetes)",
        "CT Scan Abdomen (Full Scan)"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Step 1: Lab Test Selector (Scrollable horizontally)
        Text(
            text = "Select Prescribed Test:",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C3E50),
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            testList.forEach { testName ->
                val shortName = getSimpleTestName(testName)
                val isSelected = filterState.testName == shortName
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) TealLight else Color.White)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) TealPrimary else Color(0xFFDFE6E9),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { viewModel.updateTest(shortName) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("test_chip_$shortName")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when {
                                shortName.contains("CBC") || shortName.contains("Glucose") -> Icons.Default.MedicalServices
                                shortName.contains("MRI") || shortName.contains("CT") -> Icons.Default.QueryStats
                                else -> Icons.Default.LocalHospital
                            },
                            contentDescription = null,
                            tint = if (isSelected) TealPrimary else Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = testName,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) TealPrimary else Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Step 2: Search Input and Filter details
        OutlinedTextField(
            value = filterState.manualQuery,
            onValueChange = { viewModel.updateManualQuery(it) },
            prefix = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray) },
            placeholder = { Text("Filter centers by name, suburb...", fontSize = 13.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("input_search_lab"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF1E293B),
                unfocusedTextColor = Color(0xFF1E293B),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = TealPrimary,
                unfocusedBorderColor = Color(0xFFDFE6E9)
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Step 3: Sort Options Selector Flow
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.FilterList, contentDescription = "Sorting filters", modifier = Modifier.size(16.dp), tint = Color.Gray)
            Text("Sort by:", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)

            SortMode.entries.forEach { mode ->
                val isSelected = filterState.sortMode == mode
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.updateSortMode(mode) },
                    label = {
                        Text(
                            text = getSortModeLabel(mode),
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = Color.Black
                    ),
                    modifier = Modifier.testTag("sort_chip_${mode.name}")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Match Quality description
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Results: ${centers.size} Centers Found",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.SemiBold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(TealSecondary))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Optimized Match Algorithm active", fontSize = 10.sp, color = Color.Gray)
            }
        }

        // Centers results list
        if (centers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.LocalHospital,
                        contentDescription = "No centers available",
                        tint = Color.LightGray,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No diagnostic centers match search.", color = Color.Gray)
                    Text("Try relaxing search filter keywords.", color = Color.LightGray, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(centers, key = { it.center.id }) { ec ->
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
                        onClick = { onCenterClick(ec) },
                        onFavToggle = { viewModel.toggleFavorite(ec.center.id, ec.center.isFavorite) }
                    )
                }
            }
        }
    }
}

@Composable
fun RadarSatelliteView(
    filterState: FilterState,
    centers: List<EnhancedCenter>,
    onPinSelected: (EnhancedCenter) -> Unit
) {
    val pulseSizeState = rememberInfiniteTransition(label = "Radar Ring Pulse")
    val pulseSize by pulseSizeState.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "Pulse width animated"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Active Clinical Radar (Geocentric Mapping)",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF2C3E50)
        )
        Text(
            text = "Proximity Map surrounding current GPS frame (${filterState.myLocationLabel})",
            fontSize = 10.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Interlocking geographic radar map canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE8F2F4))
                .border(2.dp, TealSecondary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize().testTag("satellite_map_radar")) {
                val centerOffset = Offset(size.width / 2.0f, size.height / 2.0f)
                val maxDim = size.minDimension / 2.0f - 30.dp.toPx()

                // Draw radar rings represents distance bounds (3km, 6km, 9km)
                drawCircle(color = TealPrimary.copy(alpha = 0.15f), radius = maxDim * 0.33f, center = centerOffset, style = Stroke(2f))
                drawCircle(color = TealPrimary.copy(alpha = 0.15f), radius = maxDim * 0.66f, center = centerOffset, style = Stroke(2f))
                drawCircle(color = TealPrimary.copy(alpha = 0.15f), radius = maxDim, center = centerOffset, style = Stroke(2f))

                // Compass overlay coordinates
                drawLine(color = TealPrimary.copy(alpha = 0.08f), start = Offset(0f, centerOffset.y), end = Offset(size.width, centerOffset.y), strokeWidth = 3f)
                drawLine(color = TealPrimary.copy(alpha = 0.08f), start = Offset(centerOffset.x, 0f), end = Offset(centerOffset.x, size.height), strokeWidth = 3f)

                // Draw central patient locus pulsating
                drawCircle(color = TealSecondary, radius = 7.dp.toPx(), center = centerOffset)
                drawCircle(color = TealSecondary.copy(alpha = 1.0f - pulseSize), radius = 8.dp.toPx() + (pulseSize * 22.dp.toPx()), center = centerOffset)
            }

            // Pins positioned on top of the geocentric coordinate space as elements
            centers.forEach { ec ->
                val centerLat = filterState.userLatitude
                val centerLon = filterState.userLongitude

                // Latitude delta -> Y axis (invert since Screen coordinates 0 at top)
                // Longitude delta -> X axis
                val latDelta = ec.center.latitude - centerLat
                val lonDelta = ec.center.longitude - centerLon

                // Scale factor: 10km corresponds to full maxDim bounds
                val scaleFactor = 15000.0 // Scaling delta degree to DP offsets
                val xOffsetDp = (lonDelta * scaleFactor).toFloat().coerceIn(-130f, 130f).dp
                val yOffsetDp = (-latDelta * scaleFactor).toFloat().coerceIn(-130f, 130f).dp

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = xOffsetDp * 2, y = yOffsetDp * 2)
                        .testTag("map_pin_${ec.center.id}")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onPinSelected(ec) }
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(TealPrimary)
                                .border(1.dp, Color.White, RoundedCornerShape(6.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "$${String.format("%.0f", ec.testPrice)}",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Diagnostic Center Anchor",
                            tint = if (ec.center.isFavorite) CoralAccent else TealSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = ec.center.name.split(" ").firstOrNull() ?: "",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E50),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Radar Map instruction tips
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CompassCalibration,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Interactive Medical Radar", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF2C3E50))
                    Text(
                        "Click pins to view catalog, call clinic, or launch driving directions natively.",
                        fontSize = 9.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun AIChatAdvisor(
    viewModel: DiagnosticViewModel,
    filterState: FilterState,
    aiMessage: String?,
    loading: Boolean
) {
    var patientPromptInput by remember { mutableStateOf("") }
    val presetQuestions = listOf(
        "Which tests require 12 hours of fasting?",
        "Why are MRI scan prices so different in Central Park vs Hudson River?",
        "Compare wait times of Metro Care and Brooklyn Health.",
        "When is fasting mandatory for diabetes glucose tests?"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // AI Header card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TealLight)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.SmartToy,
                    contentDescription = "AI Assistant Logo",
                    tint = TealPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        "NearCare AI Advisor",
                        fontWeight = FontWeight.Bold,
                        color = MedicalBlue,
                        fontSize = 14.sp
                    )
                    Text(
                        "Ask about medical test fasting protocols, lab compare, and price reasons.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main chatbot response scrollable box
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(width = 1.dp, color = Color(0xFFDFE6E9))
                .padding(12.dp)
        ) {
            if (loading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = TealPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "NearCare AI Advisor formulating advice...",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else if (aiMessage.isNullOrEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Suggested Questions to Ask:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    presetQuestions.forEach { question ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SoftGreyBg)
                                .clickable {
                                    patientPromptInput = question
                                    viewModel.triggerAiAdvisor(question)
                                }
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Send,
                                    contentDescription = null,
                                    tint = TealSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(question, fontSize = 11.sp, color = Color.Black)
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = aiMessage,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Input footer bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = patientPromptInput,
                onValueChange = { patientPromptInput = it },
                placeholder = { Text("Ask NearCare advisor regarding tests...", fontSize = 12.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_ai_query")
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1E293B),
                    unfocusedTextColor = Color(0xFF1E293B),
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = Color(0xFFDFE6E9),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                maxLines = 1,
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (patientPromptInput.isNotEmpty()) {
                        viewModel.triggerAiAdvisor(patientPromptInput)
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(TealPrimary)
                    .testTag("submit_ai_query")
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send advice query",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

data class PrescriptionPage(val title: String, val tests: List<String>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionCard(page: PrescriptionPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalHospital,
                    contentDescription = null,
                    tint = Color(0xFF0D4EA3),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CLINICAL RX",
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    color = Color(0xFF0D4EA3),
                    letterSpacing = 1.sp
                )
            }
            Text(
                text = "ID: #0829-X",
                fontSize = 8.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.5.dp)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Dr. Sarah Jenkins, MD",
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "Senior General Practitioner",
                    fontSize = 7.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = "Date: 2026-07-01",
                fontSize = 7.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Prescribed Lab Orders:",
            fontSize = 8.sp,
            color = Color.Gray,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        page.tests.forEach { test ->
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color(0xFF0D4EA3),
                        modifier = Modifier.size(10.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = test,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(1.5.dp)
                    .background(Color(0xFFCBD5E1))
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "S. Jenkins",
                    fontStyle = FontStyle.Italic,
                    fontSize = 10.sp,
                    color = Color(0xFF0D4EA3),
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(1.dp)
                        .background(Color(0xFF0D4EA3).copy(alpha = 0.5f))
                )
                Text(
                    text = "Signature",
                    fontSize = 6.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

fun size16() = 16.dp

@Composable
fun CustomProximityMap(
    filterState: FilterState,
    centers: List<EnhancedCenter>,
    onPinClick: (EnhancedCenter) -> Unit
) {
    val pulseSizeState = rememberInfiniteTransition(label = "Radar Ring Pulse")
    val pulseSize by pulseSizeState.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "Pulse width animated"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE6F3FF)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().testTag("custom_proximity_map_canvas")) {
            val centerOffset = Offset(size.width / 2.0f, size.height / 2.0f)
            val maxDim = size.minDimension / 2.0f - 20.dp.toPx()

            // Draw radar concentric rings
            drawCircle(color = MedicalBlue.copy(alpha = 0.08f), radius = maxDim * 0.33f, center = centerOffset, style = Stroke(1.5f))
            drawCircle(color = MedicalBlue.copy(alpha = 0.08f), radius = maxDim * 0.66f, center = centerOffset, style = Stroke(1.5f))
            drawCircle(color = MedicalBlue.copy(alpha = 0.08f), radius = maxDim, center = centerOffset, style = Stroke(1.5f))

            // Pulse central location marker (Patient / Current location)
            drawCircle(color = MedicalBlue, radius = 6.dp.toPx(), center = centerOffset)
            drawCircle(color = MedicalBlue.copy(alpha = 1.0f - pulseSize), radius = 7.dp.toPx() + (pulseSize * 15.dp.toPx()), center = centerOffset)
        }

        // Standard patient pin label
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-20).dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MedicalBlue)
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "You",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Centers mapping coordinates
        centers.forEach { ec ->
            val centerLat = filterState.userLatitude
            val centerLon = filterState.userLongitude

            val latDelta = ec.center.latitude - centerLat
            val lonDelta = ec.center.longitude - centerLon

            // Scale delta degree to offset
            val scaleFactor = 16000.0
            val xOffsetDp = (lonDelta * scaleFactor).toFloat().coerceIn(-130f, 130f).dp
            val yOffsetDp = (-latDelta * scaleFactor).toFloat().coerceIn(-130f, 130f).dp

            val rankColors = getRankColors(ec.matchScore)

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = xOffsetDp * 2, y = yOffsetDp * 2)
                    .testTag("proximity_pin_${ec.center.id}")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onPinClick(ec) }
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(rankColors.pinColor)
                            .border(1.dp, Color.White, RoundedCornerShape(6.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$${String.format("%.0f", ec.testPrice)}",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Diagnostic Center",
                        tint = rankColors.pinColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatsBadge(bigText: String, subText: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TealLight),
        border = androidx.compose.foundation.BorderStroke(width = 1.dp, color = Color(0xFFD0E3FF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = bigText,
                color = MedicalBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subText,
                color = TextSecondaryDark,
                fontSize = 8.sp,
                lineHeight = 10.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SimulatedStreetMap(
    centerLatitude: Double,
    centerLongitude: Double,
    labelText: String,
    modifier: Modifier = Modifier
) {
    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by pulseTransition.animateFloat(
        initialValue = 6f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                drawRect(color = Color(0xFFF1F5F9))

                // draw clinical green park areas
                drawRect(
                    color = Color(0xFFDCFCE7),
                    size = androidx.compose.ui.geometry.Size(w * 0.25f, h * 0.3f),
                    topLeft = Offset(w * 0.1f, h * 0.15f)
                )
                drawRect(
                    color = Color(0xFFDCFCE7),
                    size = androidx.compose.ui.geometry.Size(w * 0.3f, h * 0.3f),
                    topLeft = Offset(w * 0.62f, h * 0.6f)
                )

                // water bodies
                drawRect(
                    color = Color(0xFFE0F2FE),
                    size = androidx.compose.ui.geometry.Size(w * 0.2f, h),
                    topLeft = Offset(w * 0.85f, 0f)
                )

                // roads
                drawRect(
                    color = Color.White,
                    size = androidx.compose.ui.geometry.Size(28.dp.toPx(), h),
                    topLeft = Offset(w * 0.45f, 0f)
                )
                drawRect(
                    color = Color.White,
                    size = androidx.compose.ui.geometry.Size(w, 20.dp.toPx()),
                    topLeft = Offset(0f, h * 0.43f)
                )
                drawRect(
                    color = Color.White,
                    size = androidx.compose.ui.geometry.Size(w, 16.dp.toPx()),
                    topLeft = Offset(0f, h * 0.78f)
                )

                drawLine(color = Color(0xFFCBD5E1), start = Offset(w * 0.25f, 0f), end = Offset(w * 0.25f, h), strokeWidth = 1f)
                drawLine(color = Color(0xFFCBD5E1), start = Offset(w * 0.72f, 0f), end = Offset(w * 0.72f, h), strokeWidth = 1f)

                val centerOffset = Offset(w * 0.5f + (centerLongitude % 0.05).toFloat() * 1000f, h * 0.5f - (centerLatitude % 0.05).toFloat() * 1000f)
                val boundCenter = Offset(
                    centerOffset.x.coerceIn(40.dp.toPx(), w - 40.dp.toPx()),
                    centerOffset.y.coerceIn(40.dp.toPx(), h - 40.dp.toPx())
                )

                drawCircle(color = MedicalBlue.copy(alpha = pulseAlpha), radius = pulseRadius.dp.toPx(), center = boundCenter)
                drawCircle(color = MedicalBlue, radius = 6.dp.toPx(), center = boundCenter)
                drawCircle(color = Color.White, radius = 3.dp.toPx(), center = boundCenter)

                // draw clinic markers
                drawCircle(color = CoralAccent, radius = 5.dp.toPx(), center = boundCenter + Offset(-40.dp.toPx(), -25.dp.toPx()))
                drawCircle(color = CoralAccent, radius = 5.dp.toPx(), center = boundCenter + Offset(55.dp.toPx(), 40.dp.toPx()))
                drawCircle(color = CoralAccent, radius = 5.dp.toPx(), center = boundCenter + Offset(-30.dp.toPx(), 50.dp.toPx()))
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.92f))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Column {
                    Text(
                        text = "LIVE GOOGLE MAP PREVIEW",
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = TextSecondaryDark,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Centering on: $labelText",
                        color = MedicalBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Coords: ${String.format("%.4f", centerLatitude)}, ${String.format("%.4f", centerLongitude)}",
                        color = Color.Gray,
                        fontSize = 9.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.95f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CompassCalibration,
                    contentDescription = "Compass North Indicator",
                    tint = MedicalBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun GoogleMapView(
    latitude: Double,
    longitude: Double,
    name: String,
    modifier: Modifier = Modifier
) {
    var isInteractiveMode by remember { mutableStateOf(true) }

    Box(modifier = modifier) {
        val htmlContent = remember(latitude, longitude, name, isInteractiveMode) {
            val latStr = String.format(java.util.Locale.US, "%.6f", latitude)
            val lngStr = String.format(java.util.Locale.US, "%.6f", longitude)
            val escapedName = name.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("\n", " ")
                .replace("\r", " ")
            """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no, shrink-to-fit=no" />
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.css" />
                <script src="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.js"></script>
                <style>
                    html, body {
                        width: 100% !important;
                        height: 100% !important;
                        margin: 0;
                        padding: 0;
                        overflow: hidden;
                        background-color: ${if (isInteractiveMode) "#f1f5f9" else "#0b0f19"};
                    }
                    #map {
                        width: 100% !important;
                        height: 100% !important;
                        position: absolute;
                        top: 0;
                        left: 0;
                        right: 0;
                        bottom: 0;
                    }
                    .clinic-pin-container {
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    }
                    .clinic-pin {
                        width: 18px;
                        height: 18px;
                        background-color: ${if (isInteractiveMode) "#EF4444" else "#10B981"};
                        border: 2px solid white;
                        border-radius: 50%;
                        box-shadow: 0 0 12px ${if (isInteractiveMode) "rgba(239, 68, 68, 0.8)" else "rgba(16, 185, 129, 0.9)"};
                        animation: beat 1.6s infinite ease-in-out;
                    }
                    @keyframes beat {
                        0% { transform: scale(0.95); opacity: 0.95; }
                        50% { transform: scale(1.15); opacity: 1; box-shadow: 0 0 16px ${if (isInteractiveMode) "rgba(239, 68, 68, 0.9)" else "rgba(16, 185, 129, 1)"}; }
                        100% { transform: scale(0.95); opacity: 0.95; }
                    }
                    .leaflet-tooltip {
                        background: #1e293b !important;
                        color: #ffffff !important;
                        border: none !important;
                        border-radius: 8px !important;
                        font-size: 11px !important;
                        font-family: inherit !important;
                        font-weight: bold !important;
                        padding: 6px 10px !important;
                        box-shadow: 0 4px 12px rgba(0,0,0,0.18) !important;
                    }
                    .zoom-controls {
                        position: absolute;
                        bottom: 16px;
                        right: 16px;
                        display: flex;
                        flex-direction: column;
                        gap: 6px;
                        z-index: 1000;
                    }
                    .zoom-controls button {
                        width: 36px;
                        height: 36px;
                        background: ${if (isInteractiveMode) "white" else "#1e293b"};
                        border: 1px solid ${if (isInteractiveMode) "rgba(0,0,0,0.1)" else "rgba(255,255,255,0.15)"};
                        border-radius: 50%;
                        font-size: 18px;
                        font-weight: bold;
                        color: ${if (isInteractiveMode) "#1e293b" else "#f8fafc"};
                        box-shadow: 0 2px 8px rgba(0,0,0,0.2);
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        cursor: pointer;
                    }
                    .zoom-controls button:active {
                        background: ${if (isInteractiveMode) "#f1f5f9" else "#334155"};
                    }
                    
                    /* Dynamic Radar Sweep Effects on Overlay */
                    .radar-sweep-overlay {
                        position: absolute;
                        top: 0;
                        left: 0;
                        width: 100% !important;
                        height: 100% !important;
                        pointer-events: none;
                        z-index: 999;
                        border-radius: inherit;
                        overflow: hidden;
                        display: ${if (isInteractiveMode) "none" else "block"};
                    }
                    .radar-sweep-line {
                        position: absolute;
                        top: 50%;
                        left: 50%;
                        width: 150%;
                        height: 150%;
                        margin-top: -75%;
                        margin-left: -75%;
                        background: conic-gradient(from 0deg, rgba(16, 185, 129, 0.12) 0deg, rgba(16, 185, 129, 0.02) 60deg, transparent 360deg);
                        animation: sweep 6s infinite linear;
                        transform-origin: center center;
                        pointer-events: none;
                    }
                    @keyframes sweep {
                        0% { transform: rotate(0deg); }
                        100% { transform: rotate(360deg); }
                    }
                </style>
            </head>
            <body>
                <div id="map"></div>
                
                <div class="radar-sweep-overlay">
                    <div class="radar-sweep-line"></div>
                </div>

                <div class="zoom-controls">
                    <button onclick="map.zoomIn()">+</button>
                    <button onclick="map.zoomOut()">−</button>
                </div>
                <script>
                    var map = L.map('map', {
                        zoomControl: false,
                        attributionControl: false,
                        dragging: true,
                        touchZoom: true,
                        doubleClickZoom: true,
                        boxZoom: true,
                        keyboard: true,
                        tap: true
                    }).setView([$latStr, $lngStr], 15);

                    L.tileLayer(${if (isInteractiveMode) "'https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png'" else "'https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png'"}, {
                        maxZoom: 19
                    }).addTo(map);

                    var clinicIcon = L.divIcon({
                        className: 'clinic-pin-container',
                        html: '<div class="clinic-pin"></div>',
                        iconSize: [24, 24],
                        iconAnchor: [12, 12]
                    });

                    L.marker([$latStr, $lngStr], { icon: clinicIcon }).addTo(map)
                        .bindTooltip("$escapedName", { permanent: true, direction: 'top', offset: [0, -10] });

                    // Recalculate size securely when laid out
                    new ResizeObserver(function() {
                        map.invalidateSize();
                    }).observe(document.getElementById('map'));

                    window.onload = function() {
                        setTimeout(function() { map.invalidateSize(); }, 150);
                        setTimeout(function() { map.invalidateSize(); }, 600);
                    };
                </script>
            </body>
            </html>
            """.trimIndent()
        }

        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewClient = WebViewClient()
                    webChromeClient = android.webkit.WebChromeClient()
                    settings.apply {
                        javaScriptEnabled = true
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36 ClinicalLocatorApp/1.0"
                    }
                }
            },
            update = { webView ->
                val oldHtml = webView.tag as? String
                if (oldHtml != htmlContent) {
                    webView.tag = htmlContent
                    webView.loadDataWithBaseURL("https://localhost/", htmlContent, "text/html", "UTF-8", null)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Action map toggler card
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0F172A).copy(alpha = 0.88f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                .clickable { isInteractiveMode = !isInteractiveMode }
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isInteractiveMode) "🗺️ Map" else "📡 Radar",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GoogleOverviewMapView(
    filterState: FilterState,
    centers: List<EnhancedCenter>,
    onPinClick: (EnhancedCenter) -> Unit,
    modifier: Modifier = Modifier
) {
    val mainHandler = remember { android.os.Handler(android.os.Looper.getMainLooper()) }
    var isWebMode by remember { mutableStateOf(true) }

    val userLat = filterState.userLatitude
    val userLon = filterState.userLongitude

    // Re-generate HTML content whenever centers, user location coordinates, or map mode shifts
    val htmlContent = remember(userLat, userLon, centers, isWebMode) {
        val userLatStr = String.format(java.util.Locale.US, "%.6f", userLat)
        val userLonStr = String.format(java.util.Locale.US, "%.6f", userLon)
        val top5Ids = centers.sortedByDescending { it.matchScore }.take(5).map { it.center.id }.toSet()
        buildString {
            append("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no, shrink-to-fit=no" />
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.css" />
                <script src="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.js"></script>
                <style>
                    html, body {
                        width: 100% !important;
                        height: 100% !important;
                        margin: 0;
                        padding: 0;
                        overflow: hidden;
                        background-color: ${if (isWebMode) "#f1f5f9" else "#0b0f19"};
                    }
                    #map {
                        width: 100% !important;
                        height: 100% !important;
                        position: absolute;
                        top: 0;
                        left: 0;
                        right: 0;
                        bottom: 0;
                    }
                    .user-pulse-container {
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        width: 24px !important;
                        height: 24px !important;
                    }
                    .user-pulse-core {
                        width: 12px;
                        height: 12px;
                        background: ${if (isWebMode) "#3B82F6" else "#10B981"};
                        border: 2px solid white;
                        border-radius: 50%;
                        box-shadow: 0 0 10px ${if (isWebMode) "rgba(59, 130, 246, 0.8)" else "rgba(16, 185, 129, 0.9)"};
                        position: absolute;
                        z-index: 10;
                    }
                    .user-pulse-ring {
                        width: 28px;
                        height: 28px;
                        background: ${if (isWebMode) "rgba(59, 130, 246, 0.35)" else "rgba(16, 185, 129, 0.35)"};
                        border-radius: 50%;
                        animation: mapPulse 1.8s infinite ease-out;
                        position: absolute;
                    }
                    @keyframes mapPulse {
                        0% { transform: scale(0.3); opacity: 1; }
                        100% { transform: scale(1.4); opacity: 0; }
                    }
                    .clinic-badge-container {
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    }
                    .clinic-badge {
                        background: ${if (isWebMode) "white" else "#0f172a"};
                        border: 2.5px solid;
                        border-radius: 12px;
                        padding: 3px 8px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        gap: 3px;
                        font-family: inherit;
                        font-size: 10.5px;
                        font-weight: bold;
                        box-shadow: ${if (isWebMode) "0 3px 6px rgba(0,0,0,0.16)" else "0 0 12px rgba(16, 185, 129, 0.4)"};
                        white-space: nowrap;
                    }
                    .clinic-badge .star {
                        font-size: 10px;
                        font-weight: bold;
                    }
                    .leaflet-tooltip {
                        background: #1e293b !important;
                        color: #ffffff !important;
                        border: none !important;
                        border-radius: 6px !important;
                        font-size: 10px !important;
                        font-weight: bold !important;
                        padding: 4px 8px !important;
                        box-shadow: 0 3px 8px rgba(0,0,0,0.15) !important;
                    }
                    .zoom-controls {
                        position: absolute;
                        bottom: 24px;
                        right: 16px;
                        display: flex;
                        flex-direction: column;
                        gap: 6px;
                        z-index: 1000;
                    }
                    .zoom-controls button {
                        width: 36px;
                        height: 36px;
                        background: ${if (isWebMode) "white" else "#1e293b"};
                        border: 1px solid ${if (isWebMode) "rgba(0,0,0,0.1)" else "rgba(255,255,255,0.15)"};
                        border-radius: 50%;
                        font-size: 18px;
                        font-weight: bold;
                        color: ${if (isWebMode) "#1e293b" else "#f8fafc"};
                        box-shadow: 0 3px 8px rgba(0,0,0,0.2);
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        cursor: pointer;
                    }
                    .zoom-controls button:active {
                        background: ${if (isWebMode) "#f1f5f9" else "#334155"};
                    }
                    
                    /* Dynamic Radar Sweep Effects on Overlay */
                    .radar-sweep-overlay {
                        position: absolute;
                        top: 0;
                        left: 0;
                        width: 100% !important;
                        height: 100% !important;
                        pointer-events: none;
                        z-index: 999;
                        border-radius: inherit;
                        overflow: hidden;
                        display: ${if (isWebMode) "none" else "block"};
                    }
                    .radar-sweep-line {
                        position: absolute;
                        top: 50%;
                        left: 50%;
                        width: 150%;
                        height: 150%;
                        margin-top: -75%;
                        margin-left: -75%;
                        background: conic-gradient(from 0deg, rgba(16, 185, 129, 0.12) 0deg, rgba(16, 185, 129, 0.02) 60deg, transparent 360deg);
                        animation: sweep 6s infinite linear;
                        transform-origin: center center;
                        pointer-events: none;
                    }
                    @keyframes sweep {
                        0% { transform: rotate(0deg); }
                        100% { transform: rotate(360deg); }
                    }
                </style>
            </head>
            <body>
                <div id="map"></div>
                
                <div class="radar-sweep-overlay">
                    <div class="radar-sweep-line"></div>
                </div>

                <div class="zoom-controls">
                    <button onclick="map.zoomIn()">+</button>
                    <button onclick="map.zoomOut()">−</button>
                </div>
                <script>
                    var map = L.map('map', {
                        zoomControl: false,
                        attributionControl: false,
                        dragging: true,
                        touchZoom: true,
                        doubleClickZoom: true,
                        boxZoom: true,
                        keyboard: true,
                        tap: true
                    }).setView([$userLatStr, $userLonStr], 13);

                    L.tileLayer(${if (isWebMode) "'https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png'" else "'https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png'"}, {
                        maxZoom: 19
                    }).addTo(map);

                    // Glowing user location dot with pulse
                    var userIcon = L.divIcon({
                        className: 'user-pulse-container',
                        html: '<div class="user-pulse-ring"></div><div class="user-pulse-core"></div>',
                        iconSize: [24, 24],
                        iconAnchor: [12, 12]
                    });
                    L.marker([$userLatStr, $userLonStr], { icon: userIcon }).addTo(map)
                        .bindTooltip("You Are Here", { permanent: true, direction: 'top', offset: [0, -10] });

                    function onPinTapped(id) {
                        try {
                            if (window.AndroidBridge) {
                                window.AndroidBridge.postMessage(id);
                            }
                        } catch(e) {}
                    }
            """)

            centers.forEach { ec ->
                val score = ec.matchScore
                // In radar mode, use green/cyan neon colors. In map mode, use premium medical colors.
                val hexColor = if (isWebMode) {
                    when {
                        score >= 85 -> "#1D4ED8" // Deep Clinical Blue
                        score >= 70 -> "#0284C7" // Friendly Sky Blue
                        score >= 50 -> "#64748B" // Cool Slate Semi-Ash
                        else -> "#9CA3AF"        // Ash Gray
                    }
                } else {
                    when {
                        score >= 85 -> "#10B981" // Radiant Green
                        score >= 70 -> "#06B6D4" // Neon Cyan
                        score >= 50 -> "#F59E0B" // Amber
                        else -> "#64748B"        // Slate Gray
                    }
                }

                val label = ec.center.name.replace("\\", "\\\\")
                    .replace("\"" , "\\\"")
                    .replace("'", "\\'")
                    .replace("\n", " ")
                    .replace("\r", " ")
                val id = ec.center.id
                val clLat = ec.center.latitude
                val clLon = ec.center.longitude
                val clLatStr = String.format(java.util.Locale.US, "%.6f", clLat)
                val clLonStr = String.format(java.util.Locale.US, "%.6f", clLon)
                val isTop5 = id in top5Ids

                append("""
                    var clinicIcon$id = L.divIcon({
                        className: 'clinic-badge-container',
                        html: '<div class="clinic-badge" style="border-color: $hexColor; color: $hexColor;"><span class="star">★</span><span>$score%</span></div>',
                        iconSize: [46, 20],
                        iconAnchor: [23, 10]
                    });
                    
                    var m$id = L.marker([$clLatStr, $clLonStr], { icon: clinicIcon$id }).addTo(map);
                    
                    m$id.bindTooltip("$label", { permanent: false, direction: 'top', offset: [0, -10] });
                    m$id.on('click', function() {
                        onPinTapped("$id");
                    });
                """)

                if (isTop5) {
                    val pathWeight = if (score >= 85) 3.5 else 1.8
                    val pathOpacity = if (score >= 85) 0.8 else 0.5
                    val dashArray = if (score >= 85) "" else (if (isWebMode) "4, 6" else "2, 4")
                    append("""
                        // Route to top 5
                        L.polyline([
                            [$userLatStr, $userLonStr],
                            [$clLatStr, $clLonStr]
                        ], {
                            color: '$hexColor',
                            weight: $pathWeight,
                            opacity: $pathOpacity,
                            dashArray: '$dashArray'
                        }).addTo(map);
                    """)
                }
            }

            append("""
                    // Invalidate size on observers
                    new ResizeObserver(function() {
                        map.invalidateSize();
                    }).observe(document.getElementById('map'));

                    window.onload = function() {
                        setTimeout(function() { map.invalidateSize(); }, 150);
                        setTimeout(function() { map.invalidateSize(); }, 600);
                        setTimeout(function() { map.invalidateSize(); }, 1500);
                    };
                </script>
            </body>
            </html>
            """)
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewClient = WebViewClient()
                    webChromeClient = android.webkit.WebChromeClient()
                    settings.apply {
                        javaScriptEnabled = true
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36 ClinicalLocatorApp/1.0"
                    }
                    addJavascriptInterface(object {
                        @android.webkit.JavascriptInterface
                        fun postMessage(centerId: String) {
                            val matchingCenter = centers.find { it.center.id.toString() == centerId }
                            if (matchingCenter != null) {
                                mainHandler.post {
                                    onPinClick(matchingCenter)
                                }
                            }
                        }
                    }, "AndroidBridge")
                }
            },
            update = { webView ->
                val oldHtml = webView.tag as? String
                if (oldHtml != htmlContent) {
                    webView.tag = htmlContent
                    webView.loadDataWithBaseURL("https://localhost/", htmlContent, "text/html", "UTF-8", null)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Sleek Custom Map Toggle capsules over the top-right corner
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0xFF0F172A).copy(alpha = 0.88f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(30.dp))
                .padding(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(if (isWebMode) MedicalBlue else Color.Transparent)
                    .clickable { isWebMode = true }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "🗺️ Map",
                    color = if (isWebMode) Color.White else Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(if (!isWebMode) MedicalBlue else Color.Transparent)
                    .clickable { isWebMode = false }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "📡 Radar",
                    color = if (!isWebMode) Color.White else Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun NativeClinicalRadarMapView(
    userLat: Double,
    userLon: Double,
    userLabel: String,
    centers: List<EnhancedCenter>,
    onPinClick: (EnhancedCenter) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val infiniteTransition = rememberInfiniteTransition(label = "RadarPulse")
    val pulseRatio by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    BoxWithConstraints(
        modifier = modifier
            .background(Color(0xFF0F172A)) // Sleek dark slate radar canvas
    ) {
        val width = this.constraints.maxWidth
        val height = this.constraints.maxHeight
        val wPx = width.toFloat()
        val hPx = height.toFloat()
        val centerOffset = Offset(wPx / 2f, hPx / 2f)

        // Find scale factor dynamically to fit close items nicely
        val localClinics = centers.filter { it.distanceKm < 150.0 }
        val maxDelta = if (localClinics.isNotEmpty()) {
            localClinics.maxOf { ec ->
                maxOf(
                    kotlin.math.abs(ec.center.latitude - userLat),
                    kotlin.math.abs(ec.center.longitude - userLon)
                )
            }.coerceIn(0.003, 0.15)
        } else {
            0.015
        }

        val scale = (minOf(wPx, hPx) * 0.40f) / maxDelta.toFloat()

        // Radar coordinate grid Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val maxRadius = minOf(size.width, size.height) * 0.42f

            // Draw grid concentric circular rings
            drawCircle(color = Color(0xFF1E293B), radius = maxRadius * 0.33f, center = centerOffset, style = Stroke(1.5f))
            drawCircle(color = Color(0xFF1E293B), radius = maxRadius * 0.66f, center = centerOffset, style = Stroke(1.5f))
            drawCircle(color = Color(0xFF1E293B), radius = maxRadius, center = centerOffset, style = Stroke(1.5f))

            // Compass radial guidelines
            drawLine(color = Color(0xFF1E293B), start = Offset(0f, centerOffset.y), end = Offset(size.width, centerOffset.y), strokeWidth = 1f)
            drawLine(color = Color(0xFF1E293B), start = Offset(centerOffset.x, 0f), end = Offset(centerOffset.x, size.height), strokeWidth = 1f)

            // Draw line coordinates to clinics
            localClinics.take(8).forEach { ec ->
                val dLat = ec.center.latitude - userLat
                val dLon = ec.center.longitude - userLon
                val targetOffset = Offset(
                    centerOffset.x + (dLon * scale).toFloat(),
                    centerOffset.y - (dLat * scale).toFloat()
                )
                // Match score-based path colors matching the getRankColors system
                val rankColors = getRankColors(ec.matchScore)
                val pathColor = rankColors.pinColor.copy(alpha = 0.4f)
                drawLine(
                    color = pathColor,
                    start = centerOffset,
                    end = targetOffset,
                    strokeWidth = 2f,
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }

            // Draw central patient locus radar circle pulse
            drawCircle(color = MedicalBlue.copy(alpha = 1f - pulseRatio), radius = 24.dp.toPx() * pulseRatio, center = centerOffset)
            drawCircle(color = Color.White, radius = 6.dp.toPx(), center = centerOffset)
            drawCircle(color = MedicalBlue, radius = 4.dp.toPx(), center = centerOffset)
        }

        // Display user badge
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-16).dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF0F172A))
                .border(0.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = "YOU",
                color = Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }

        // Overlay interactive markers
        localClinics.forEach { ec ->
            val dLat = ec.center.latitude - userLat
            val dLon = ec.center.longitude - userLon

            val xDp = with(density) { (centerOffset.x + dLon * scale).toFloat().toDp() - 14.dp }
            val yDp = with(density) { (centerOffset.y - dLat * scale).toFloat().toDp() - 14.dp }

            val rankColors = getRankColors(ec.matchScore)
            val pinColor = rankColors.pinColor

            Box(
                modifier = Modifier
                    .offset(x = xDp, y = yDp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, pinColor, CircleShape)
                    .clickable { onPinClick(ec) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${ec.matchScore}%",
                    color = pinColor,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Map radar distance label at the bottom right
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            val distLabel = if (localClinics.isNotEmpty()) {
                val maxDist = localClinics.maxOf { it.distanceKm }
                val labelKm = if (maxDist > 1.5) maxDist else 5.0
                "Grid Range: ~${String.format("%.1f", labelKm.coerceIn(1.5, 150.0))} km"
            } else {
                "Grid Range: ~5.0 km"
            }
            Text(
                text = distLabel,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 8.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}
