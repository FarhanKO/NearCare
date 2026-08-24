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


fun cropBitmap(bitmap: android.graphics.Bitmap, rotation: Int, viewW: Int, viewH: Int, density: Float): android.graphics.Bitmap {
    val rotated = if (rotation != 0) {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(rotation.toFloat())
        android.graphics.Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }
    
    val paddingX = (40 * density).toInt()
    val paddingYTop = (120 * density).toInt()
    val paddingYBottom = (220 * density).toInt()
    
    val vfWidth = viewW - (paddingX * 2)
    val vfHeight = viewH - paddingYTop - paddingYBottom
    
    val scaleX = rotated.width.toFloat() / viewW
    val scaleY = rotated.height.toFloat() / viewH
    
    val cropX = (paddingX * scaleX).toInt().coerceIn(0, rotated.width)
    val cropY = (paddingYTop * scaleY).toInt().coerceIn(0, rotated.height)
    val cropW = (vfWidth * scaleX).toInt().coerceAtMost(rotated.width - cropX)
    val cropH = (vfHeight * scaleY).toInt().coerceAtMost(rotated.height - cropY)
    
    return if (cropW > 0 && cropH > 0) {
        android.graphics.Bitmap.createBitmap(rotated, cropX, cropY, cropW, cropH)
    } else {
        rotated
    }
}

@Composable
fun AnimatedHeader(text: String) {
    val animProgress = remember { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = androidx.compose.animation.core.tween(800, easing = androidx.compose.animation.core.FastOutSlowInEasing))
    }
    
    val progress = animProgress.value
    val blurRadius = (16 * (1f - progress)).dp
    val alphaVal = progress
    
    Text(
        text = text,
        color = Color(0xFF0F172A),
        fontSize = 24.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier
            .graphicsLayer { alpha = alphaVal }
            .blur(blurRadius)
    )
}

@Composable
fun AnimatedRevealedLine(text: String) {
    val animProgress = remember { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = androidx.compose.animation.core.tween(600, easing = androidx.compose.animation.core.LinearOutSlowInEasing))
    }
    
    val progress = animProgress.value
    val blurRadius = (10 * (1f - progress)).dp
    val offsetY = (18 * (1f - progress)).dp
    val alphaVal = progress
    
    Row(
        modifier = Modifier
            .graphicsLayer {
                alpha = alphaVal
                translationY = offsetY.toPx()
            }
            .blur(blurRadius)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(Color(0xFF10B981), CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Color(0xFF1E293B),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionScanner(
    onDismiss: () -> Unit,
    onCaptured: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    var flashEnabled by remember { mutableStateOf(false) }
    var capturedBitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    
    var triggerCaptureAnimation by remember { mutableStateOf<Bitmap?>(null) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    
    var isAnalyzing by remember { mutableStateOf(false) }
    var isThinking by remember { mutableStateOf(false) }
    var ocrFailure by remember { mutableStateOf<String?>(null) }
    val revealedList = remember { mutableStateListOf<String>() }
    
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val thumbnailScale = remember { Animatable(1f) }
    var isFlashOn by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val bitmaps = uris.mapNotNull { uri ->
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    android.graphics.BitmapFactory.decodeStream(inputStream)
                } catch (e: Exception) {
                    null
                }
            }
            capturedBitmaps = capturedBitmaps + bitmaps
        }
    }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
             val bitmaps = uris.mapNotNull { uri ->
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    android.graphics.BitmapFactory.decodeStream(inputStream)
                } catch (e: Exception) {
                    null
                }
            }
            capturedBitmaps = capturedBitmaps + bitmaps
        }
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val widthDp = maxWidth
        val heightDp = maxHeight
        val screenWidthPx = constraints.maxWidth
        val screenHeightPx = constraints.maxHeight
        
        val thumbnailRight = 16.dp
        val thumbnailWidth = 48.dp
        val thumbnailHeight = 68.dp
        val thumbnailTop = heightDp - 160.dp - 28.dp - thumbnailHeight

        AnimatedContent(
            targetState = isAnalyzing,
            transitionSpec = {
                (fadeIn(animationSpec = tween(500)) + slideInHorizontally(initialOffsetX = { 80 }, animationSpec = tween(500))).togetherWith(
                    fadeOut(animationSpec = tween(400))
                )
            },
            label = "ScannerStateTransition"
        ) { analyzing ->
            if (analyzing) {
                // REAL extraction: run on-device OCR over the captured photos and
                // reveal what was actually read. Nothing is pre-seeded — if the
                // photo has no readable text, nothing is reported.
                LaunchedEffect(capturedBitmaps) {
                    ocrFailure = null
                    isThinking = true
                    revealedList.clear()

                    val result = withContext(Dispatchers.Default) {
                        PrescriptionOcr.extract(capturedBitmaps)
                    }
                    val found = result.all
                    isThinking = false

                    if (found.isEmpty()) {
                        ocrFailure = if (result.hasText) {
                            "Text was read, but no known test or medicine matched.\nTry a sharper, well-lit photo."
                        } else {
                            "No readable text found in the photo.\nTry again with better lighting and focus."
                        }
                        return@LaunchedEffect
                    }

                    // Same staged reveal cadence as before, now over real findings.
                    val total = found.size
                    val batchSize = when {
                        total <= 6 -> 1
                        total <= 12 -> 2
                        else -> 4
                    }
                    val delayMs = when {
                        total <= 6 -> 800L
                        total <= 12 -> 600L
                        else -> 450L
                    }

                    var index = 0
                    while (index < total) {
                        delay(delayMs)
                        revealedList.addAll(found.subList(index, minOf(index + batchSize, total)))
                        index += batchSize
                    }
                    delay(1200)
                    onCaptured(found.joinToString(", "))
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFF3F7FA),
                                    Color(0xFFE3EDF7)
                                )
                            )
                        )
                        .statusBarsPadding()
                        .padding(horizontal = 36.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    val targetSpacerHeight = when {
                        revealedList.size <= 3 -> heightDp * 0.28f
                        revealedList.size <= 6 -> heightDp * 0.15f
                        else -> heightDp * 0.05f
                    }
                    val animatedSpacerHeight by animateDpAsState(
                        targetValue = targetSpacerHeight,
                        animationSpec = tween(600, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                        label = "SpacerHeight"
                    )

                    Spacer(modifier = Modifier.height(animatedSpacerHeight))

                    if (isThinking) {
                        // KokonutUI-style cycling "thinking" text while OCR runs.
                        AiTextLoading(
                            texts = listOf(
                                "Thinking...",
                                "Reading your prescription...",
                                "Extracting text...",
                                "Matching tests & medicines...",
                                "Almost..."
                            )
                        )
                    } else {
                        AnimatedHeader(
                            text = if (ocrFailure != null) "Nothing detected"
                            else "Found on your prescription"
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    ocrFailure?.let { message ->
                        Text(
                            text = message,
                            fontSize = 13.sp,
                            color = TextSecondaryDark,
                            lineHeight = 19.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                ocrFailure = null
                                capturedBitmaps = emptyList()
                                revealedList.clear()
                                isAnalyzing = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Scan again", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Column(
                        modifier = Modifier.padding(start = 12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        revealedList.forEach { test ->
                            AnimatedRevealedLine(text = test)
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (cameraPermissionState.status.isGranted) {
                        AndroidView(
                            factory = { ctx ->
                                val previewView = PreviewView(ctx)
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = androidx.camera.core.Preview.Builder().build().also {
                                        it.setSurfaceProvider(previewView.surfaceProvider)
                                    }
                                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                    try {
                                        cameraProvider.unbindAll()
                                        val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
                                        camera.cameraControl.enableTorch(flashEnabled)
                                    } catch (exc: Exception) {
                                        Log.e("Scanner", "Use case binding failed", exc)
                                    }
                                }, ContextCompat.getMainExecutor(ctx))
                                previewView
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                        
                        LaunchedEffect(flashEnabled) {
                            try {
                                val cameraProvider = ProcessCameraProvider.getInstance(context).get()
                                cameraProvider.unbindAll()
                                val preview = androidx.camera.core.Preview.Builder().build()
                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
                                camera.cameraControl.enableTorch(flashEnabled)
                            } catch (e: Exception) {
                                Log.e("Scanner", "Torch toggle failed", e)
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Camera permission is required to scan prescriptions.", color = Color.White, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                                Text("Grant Permission")
                            }
                        }
                    }

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = 2.5.dp.toPx()
                        val color = Color(0xFF10B981)
                        
                        val paddingX = 40.dp.toPx()
                        val paddingYTop = 120.dp.toPx()
                        val paddingYBottom = 220.dp.toPx() 
                        val cornerLen = 40.dp.toPx()
                        
                        drawLine(color, Offset(paddingX, paddingYTop), Offset(paddingX + cornerLen, paddingYTop), stroke)
                        drawLine(color, Offset(paddingX, paddingYTop), Offset(paddingX, paddingYTop + cornerLen), stroke)
                        drawLine(color, Offset(size.width - paddingX, paddingYTop), Offset(size.width - paddingX - cornerLen, paddingYTop), stroke)
                        drawLine(color, Offset(size.width - paddingX, paddingYTop), Offset(size.width - paddingX, paddingYTop + cornerLen), stroke)
                        drawLine(color, Offset(paddingX, size.height - paddingYBottom), Offset(paddingX + cornerLen, size.height - paddingYBottom), stroke)
                        drawLine(color, Offset(paddingX, size.height - paddingYBottom), Offset(paddingX, size.height - paddingYBottom - cornerLen), stroke)
                        drawLine(color, Offset(size.width - paddingX, size.height - paddingYBottom), Offset(size.width - paddingX - cornerLen, size.height - paddingYBottom), stroke)
                        drawLine(color, Offset(size.width - paddingX, size.height - paddingYBottom), Offset(size.width - paddingX, size.height - paddingYBottom - cornerLen), stroke)
                        
                        drawLine(color.copy(alpha = 0.3f), Offset(0f, paddingYTop), Offset(size.width, paddingYTop), 1.dp.toPx())
                        drawLine(color.copy(alpha = 0.3f), Offset(0f, size.height - paddingYBottom), Offset(size.width, size.height - paddingYBottom), 1.dp.toPx())
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                        
                        IconButton(onClick = { flashEnabled = !flashEnabled }) {
                            Icon(
                                if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                contentDescription = "Flash",
                                tint = Color.White
                            )
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Prescription Scanner",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(bottom = 32.dp, top = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Scan",
                            color = Color(0xFF10B981),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(84.dp)
                                .padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            
                            // Left Control Transition
                            Box(modifier = Modifier.width(115.dp), contentAlignment = Alignment.Center) {
                                AnimatedContent(
                                    targetState = when {
                                        capturedBitmaps.isEmpty() -> 0
                                        capturedBitmaps.size == 1 -> 1
                                        else -> 2
                                    },
                                    transitionSpec = {
                                        (fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f, animationSpec = tween(300))).togetherWith(fadeOut(animationSpec = tween(300)))
                                    },
                                    label = "LeftControlTransition"
                                ) { state ->
                                    when(state) {
                                        0 -> {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.clickable { 
                                                    fileLauncher.launch(arrayOf("application/pdf", "image/*"))
                                                }
                                            ) {
                                                Icon(Icons.Default.InsertDriveFile, contentDescription = "Files", tint = Color.White, modifier = Modifier.size(28.dp))
                                                Text("Files", color = Color.White, fontSize = 11.sp)
                                            }
                                        }
                                        1 -> {
                                            Button(
                                                onClick = { capturedBitmaps = emptyList() },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA4335)),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                modifier = Modifier.height(36.dp).fillMaxWidth()
                                            ) {
                                                Text("Retake", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        2 -> {
                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Button(
                                                    onClick = { capturedBitmaps = capturedBitmaps.dropLast(1) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                                                    modifier = Modifier.height(34.dp).fillMaxWidth()
                                                ) {
                                                    Text("Retake Last", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                                }
                                                Button(
                                                    onClick = { capturedBitmaps = emptyList() },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA4335)),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                                                    modifier = Modifier.height(34.dp).fillMaxWidth()
                                                ) {
                                                    Text("Retake All", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Center: Shutter Button
                            Box(
                                modifier = Modifier
                                    .size(74.dp)
                                    .border(3.5.dp, Color(0xFF10B981), CircleShape)
                                    .padding(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .clickable {
                                        val densityVal = context.resources.displayMetrics.density
                                        
                                        imageCapture.takePicture(
                                            ContextCompat.getMainExecutor(context),
                                            object : ImageCapture.OnImageCapturedCallback() {
                                                override fun onCaptureSuccess(image: ImageProxy) {
                                                    val rotation = image.imageInfo.rotationDegrees
                                                    val bitmap = image.toBitmap()
                                                    image.close()
                                                    
                                                    val cropped = cropBitmap(bitmap, rotation, screenWidthPx, screenHeightPx, densityVal)
                                                    
                                                    triggerCaptureAnimation = cropped
                                                    capturedBitmaps = capturedBitmaps + cropped
                                                    
                                                    isFlashOn = true
                                                    coroutineScope.launch {
                                                        delay(150)
                                                        isFlashOn = false
                                                    }
                                                }
                                                override fun onError(exception: ImageCaptureException) {
                                                    Log.e("Scanner", "Capture failed", exception)
                                                }
                                            }
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Box(modifier = Modifier.size(52.dp).background(Color.White, CircleShape).border(1.dp, Color.Black, CircleShape))
                            }

                            // Right Control Transition
                            Box(modifier = Modifier.width(115.dp), contentAlignment = Alignment.Center) {
                                AnimatedContent(
                                    targetState = capturedBitmaps.isEmpty(),
                                    transitionSpec = {
                                        (fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f)).togetherWith(fadeOut(animationSpec = tween(300)))
                                    },
                                    label = "RightControlTransition"
                                ) { isNoCaptures ->
                                    if (isNoCaptures) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.clickable { 
                                                galleryLauncher.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                            }
                                        ) {
                                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = Color.White, modifier = Modifier.size(28.dp))
                                            Text("Gallery", color = Color.White, fontSize = 11.sp)
                                        }
                                    } else {
                                        Button(
                                            onClick = { isAnalyzing = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(36.dp).fillMaxWidth()
                                        ) {
                                            Text("Complete", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Thumbnail landing spot (Bottom Right)
                    if (capturedBitmaps.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .offset(x = widthDp - thumbnailWidth - thumbnailRight, y = thumbnailTop)
                                .size(thumbnailWidth, thumbnailHeight)
                                .graphicsLayer {
                                    scaleX = thumbnailScale.value
                                    scaleY = thumbnailScale.value
                                }
                                .zIndex(20f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.DarkGray)
                                    .border(1.5.dp, Color.White, RoundedCornerShape(8.dp))
                            ) {
                                Image(
                                    bitmap = capturedBitmaps.last().asImageBitmap(),
                                    contentDescription = "Last captured photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-8).dp)
                                    .size(22.dp)
                                    .background(Color.Red, CircleShape)
                                    .border(1.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = capturedBitmaps.size.toString(),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                    
                    if (isFlashOn) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(heightDp - 160.dp)
                                .background(Color.White.copy(alpha = 0.35f))
                                .zIndex(30f)
                        )
                    }
                }
            }
        }

        triggerCaptureAnimation?.let { bitmap ->
            val animProgress = remember { Animatable(0f) }
            LaunchedEffect(bitmap) {
                animProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(800, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                )
                triggerCaptureAnimation = null
                
                thumbnailScale.animateTo(
                    targetValue = 1.25f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioHighBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                thumbnailScale.animateTo(1f)
            }
            
            val progress = animProgress.value
            val arcOffset = with(density) { -100.dp.toPx() } * kotlin.math.sin(kotlin.math.PI * progress).toFloat()
            
            val startSizeW = with(density) { (widthDp - 80.dp).toPx() }
            val startSizeH = with(density) { (heightDp - 340.dp).toPx() }
            val endSizeW = with(density) { thumbnailWidth.toPx() }
            val endSizeH = with(density) { thumbnailHeight.toPx() }
            
            val startX = with(density) { 40.dp.toPx() }
            val startY = with(density) { 120.dp.toPx() }
            
            val endX = with(density) { (widthDp - thumbnailWidth - thumbnailRight).toPx() }
            val endY = with(density) { thumbnailTop.toPx() }
            
            val currX = startX + (endX - startX) * progress
            val currY = startY + (endY - startY) * progress + arcOffset
            
            val currW = startSizeW + (endSizeW - startSizeW) * progress
            val currH = startSizeH + (endSizeH - startSizeH) * progress
            
            val currAlpha = 1f - 0.5f * progress
            val currCorner = with(density) { 0.dp.toPx() } + (with(density) { 8.dp.toPx() } - with(density) { 0.dp.toPx() }) * progress
            val currRotation = -14f * progress

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .offset { IntOffset(currX.toInt(), currY.toInt()) }
                    .size(
                        width = with(density) { currW.toDp() },
                        height = with(density) { currH.toDp() }
                    )
                    .graphicsLayer {
                        rotationZ = currRotation
                        alpha = currAlpha
                    }
                    .clip(RoundedCornerShape(with(density) { currCorner.toDp() }))
                    .border(2.dp * progress, Color.White, RoundedCornerShape(with(density) { currCorner.toDp() }))
                    .zIndex(50f),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        }
    }
}
