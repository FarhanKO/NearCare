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
fun DiagnosticCenterCard(
    enhanced: EnhancedCenter,
    selectedTest: String,
    appMode: AppMode,
    medicineList: String = "",
    currencySymbol: String = "$",
    onClick: () -> Unit,
    onFavToggle: () -> Unit
) {
    val center = enhanced.center
    val rColors = getRankColors(enhanced.matchScore)
    val context = LocalContext.current
    val modeAccent = when (appMode) {
        AppMode.DIAGNOSTIC -> MedicalBlue
        AppMode.PHARMACY -> PharmacyPrimary
        AppMode.DOCTOR -> DoctorPrimary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("clinic_card_${center.id}")
            .border(width = 1.2.dp, color = rColors.cardBorder, shape = RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = rColors.cardBg),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // First row: Name, rating, certificate badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(rColors.badgeBg)
                            .border(width = 1.dp, color = rColors.badgeBorder, shape = RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${getRankLabel(enhanced.matchScore)} • ${enhanced.matchScore}% MATCH",
                            color = rColors.badgeText,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = center.name,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = TextPrimaryDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (center.certified && appMode == AppMode.DIAGNOSTIC) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = "Certified",
                                tint = TealPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = center.address,
                        fontSize = 11.sp,
                        color = TextSecondaryDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Rating Badge
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(rColors.ratingBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (center.reviewsCount > 0)
                                    String.format("%.1f", center.rating) else "—",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Text(
                        text = if (center.reviewsCount > 0) "${center.reviewsCount} reviews" else "No rating yet",
                        fontSize = 9.sp,
                        color = TextSecondaryDark,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Core details panel — each mode surfaces what actually matters for it.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (appMode) {
                    AppMode.DIAGNOSTIC -> {
                        DetailInfoItem(
                            label = getSimpleTestName(selectedTest).uppercase(),
                            value = "${currencySymbol}${String.format("%.2f", enhanced.testPrice)}",
                            icon = Icons.Default.MedicalServices,
                            modifier = Modifier.weight(1f)
                        )
                        DetailInfoItem(
                            label = "DISTANCE",
                            value = String.format("%.1f km", enhanced.distanceKm),
                            icon = Icons.Default.Directions,
                            modifier = Modifier.weight(1f)
                        )
                        DetailInfoItem(
                            label = "EST. WAIT",
                            value = "${center.estimatedWaitMinutes}m",
                            icon = Icons.Outlined.Timer,
                            valueColor = getWaitColor(center.estimatedWaitMinutes),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    AppMode.PHARMACY -> {
                        // Stock likelihood is INFERRED from the medicine's rarity and the
                        // pharmacy's size tier — never real inventory. "est." keeps it honest.
                        val meds = MedicineCatalog.parseList(medicineList)
                        val stockPct = MedicineCatalog.availabilityForAll(meds, center.tierLabel)
                        DetailInfoItem(
                            label = "STOCK (EST.)",
                            value = "~$stockPct%",
                            icon = Icons.Default.MedicalServices,
                            valueColor = getStockColor(stockPct),
                            modifier = Modifier.weight(1f)
                        )
                        DetailInfoItem(
                            label = "DISTANCE",
                            value = String.format("%.1f km", enhanced.distanceKm),
                            icon = Icons.Default.Directions,
                            modifier = Modifier.weight(1f)
                        )
                        DetailInfoItem(
                            label = "TYPE (INFERRED)",
                            value = center.tierLabel.ifEmpty { "Pharmacy" },
                            icon = Icons.Outlined.Group,
                            modifier = Modifier.weight(1.3f)
                        )
                    }
                    AppMode.DOCTOR -> {
                        DetailInfoItem(
                            label = "SPECIALTY",
                            value = center.specialtyLabel.ifEmpty { "Clinic" },
                            icon = Icons.Default.MedicalServices,
                            valueColor = DoctorPrimary,
                            modifier = Modifier.weight(1.4f)
                        )
                        DetailInfoItem(
                            label = "DISTANCE",
                            value = String.format("%.1f km", enhanced.distanceKm),
                            icon = Icons.Default.Directions,
                            modifier = Modifier.weight(1f)
                        )
                        DetailInfoItem(
                            label = "MATCH",
                            value = "${enhanced.matchScore}%",
                            icon = Icons.Default.QueryStats,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Call Button
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
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = modeAccent),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Directions Button
                Button(
                    onClick = {
                        val mapUri = Uri.parse("geo:${center.latitude},${center.longitude}?q=${Uri.encode(center.name)}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    },
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = modeAccent.copy(alpha = 0.82f)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Directions", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Favorite Toggle
                IconButton(
                    onClick = onFavToggle,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(rColors.badgeBg)
                ) {
                    Icon(
                        imageVector = if (center.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (center.isFavorite) CoralAccent else TextSecondaryDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailInfoItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    valueColor: Color = TextPrimaryDark
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.7f))
            .border(width = 0.5.dp, color = Color(0xFFE2E8F0), shape = RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(10.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    color = TextSecondaryDark,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = valueColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
        }
    }
}
