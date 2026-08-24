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
fun AiTextLoading(
    texts: List<String> = listOf(
        "Thinking...",
        "Processing...",
        "Analyzing...",
        "Computing...",
        "Almost..."
    ),
    intervalMs: Long = 1500,
    fontSize: TextUnit = 26.sp,
    baseColor: Color = TextPrimaryDark,
    highlightColor: Color = Color(0xFFB6C2D1)
) {
    var index by remember { mutableIntStateOf(0) }

    LaunchedEffect(texts, intervalMs) {
        while (true) {
            kotlinx.coroutines.delay(intervalMs)
            index = (index + 1) % texts.size
        }
    }

    val shimmer = rememberInfiniteTransition(label = "aiTextShimmer")
    val sweep by shimmer.animateFloat(
        initialValue = -600f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(animation = tween(2500), repeatMode = RepeatMode.Restart),
        label = "sweep"
    )

    AnimatedContent(
        targetState = index,
        transitionSpec = {
            (fadeIn(animationSpec = tween(300)) +
                slideInVertically(animationSpec = tween(300)) { it / 2 })
                .togetherWith(
                    fadeOut(animationSpec = tween(300)) +
                        slideOutVertically(animationSpec = tween(300)) { -it / 2 }
                )
        },
        label = "aiTextCycle"
    ) { i ->
        Text(
            text = texts[i],
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            style = androidx.compose.ui.text.TextStyle(
                brush = Brush.linearGradient(
                    colors = listOf(baseColor, highlightColor, baseColor),
                    start = Offset(sweep, 0f),
                    end = Offset(sweep + 600f, 0f)
                )
            )
        )
    }
}

/**
 * In-app voice capture for the symptom field.
 *
 * Adapted from KokonutUI's "AI Voice" (React/Tailwind) to Compose: mic button,
 * MM:SS recording timer, pulsing waveform bars and a listening state.
 *
 * Unlike the reference component the bars are NOT random — Android's
 * SpeechRecognizer reports live mic amplitude via onRmsChanged, so the waveform
 * actually reacts to the user's voice, and partial transcripts stream in as they
 * speak instead of handing off to the opaque system dialog.
 */
@Composable
fun AiVoiceDialog(
    accent: Color,
    onDismiss: () -> Unit,
    onAccept: (String) -> Unit
) {
    val context = LocalContext.current

    var isListening by remember { mutableStateOf(false) }
    var seconds by remember { mutableIntStateOf(0) }
    var level by remember { mutableStateOf(0f) }          // smoothed 0..1 mic amplitude
    var transcript by remember { mutableStateOf("") }
    var isFinal by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var isAnalysing by remember { mutableStateOf(false) }
    val matchedSpecialties = remember { mutableStateListOf<String>() }

    // After a final transcript, show the thinking state and then reveal the
    // specialties the triage actually derived from the spoken words.
    LaunchedEffect(isFinal, transcript) {
        matchedSpecialties.clear()
        if (!isFinal || transcript.isBlank()) {
            isAnalysing = false
            return@LaunchedEffect
        }
        isAnalysing = true
        kotlinx.coroutines.delay(1400)
        val found = SymptomTriage.analyze(transcript).map { it.label }
        isAnalysing = false
        found.forEach {
            kotlinx.coroutines.delay(450)
            matchedSpecialties.add(it)
        }
    }

    val recognitionAvailable = remember { SpeechRecognizer.isRecognitionAvailable(context) }
    val recognizer = remember {
        if (recognitionAvailable) SpeechRecognizer.createSpeechRecognizer(context) else null
    }

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var pendingStart by remember { mutableStateOf(false) }

    fun beginListening() {
        val r = recognizer ?: return
        errorText = null
        transcript = ""
        isFinal = false
        level = 0f
        r.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { isListening = true }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {
                // SpeechRecognizer reports roughly -2..10 dB; smooth it so the bars glide.
                val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                level = level * 0.6f + normalized * 0.4f
            }
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { isListening = false }
            override fun onError(error: Int) {
                isListening = false
                level = 0f
                errorText = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "Didn't catch that — try again."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech heard — try again."
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission denied."
                    SpeechRecognizer.ERROR_NETWORK,
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network problem during recognition."
                    else -> "Couldn't recognise speech — try again."
                }
            }
            override fun onResults(results: Bundle?) {
                isListening = false
                level = 0f
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                if (!text.isNullOrBlank()) {
                    transcript = text
                    isFinal = true
                } else {
                    errorText = "Didn't catch that — try again."
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {
                partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    ?.let { if (it.isNotBlank()) transcript = it }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        try {
            r.startListening(intent)
            isListening = true
        } catch (e: Exception) {
            errorText = "Couldn't start the microphone."
        }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
        if (granted && pendingStart) beginListening()
        else if (!granted) errorText = "Microphone permission is needed to listen."
        pendingStart = false
    }

    DisposableEffect(Unit) {
        onDispose {
            try { recognizer?.stopListening() } catch (_: Exception) {}
            try { recognizer?.cancel() } catch (_: Exception) {}
            recognizer?.destroy()
        }
    }

    // Recording timer
    LaunchedEffect(isListening) {
        if (isListening) {
            seconds = 0
            while (true) {
                kotlinx.coroutines.delay(1000)
                seconds += 1
            }
        }
    }

    val waveTransition = rememberInfiniteTransition(label = "aiVoiceWave")
    val phase by waveTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(animation = tween(1400), repeatMode = RepeatMode.Restart),
        label = "wavePhase"
    )
    val spin by waveTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(3000), repeatMode = RepeatMode.Restart),
        label = "spin"
    )

    fun toggle() {
        if (isListening) {
            try { recognizer?.stopListening() } catch (_: Exception) {}
            isListening = false
            return
        }
        if (!recognitionAvailable) {
            errorText = "Speech recognition isn't available on this device."
            return
        }
        if (hasMicPermission) beginListening()
        else {
            pendingStart = true
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Describe your symptoms",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "Speak naturally — e.g. \"chest pain and fever\"",
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Mic / stop button — 64dp, no fill, matching the reference component.
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { toggle() },
                contentAlignment = Alignment.Center
            ) {
                if (isListening) {
                    // Slowly spinning square stands in for the mic while recording.
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer { rotationZ = spin }
                            .clip(RoundedCornerShape(2.dp))
                            .background(accent)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Start listening",
                        tint = accent.copy(alpha = 0.9f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = String.format(
                    java.util.Locale.US, "%02d:%02d", seconds / 60, seconds % 60
                ),
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                color = TextPrimaryDark.copy(alpha = if (isListening) 0.7f else 0.3f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Waveform — 48 bars, 2dp wide on a 2dp gap, in a 256dp track.
            // The reference randomises bar heights; here they follow the real mic
            // amplitude so the wave belongs to the voice that is actually speaking.
            val listeningProgress by animateFloatAsState(
                targetValue = if (isListening) 1f else 0f,
                animationSpec = tween(300),
                label = "aiVoiceListening"
            )
            Row(
                modifier = Modifier
                    .width(256.dp)
                    .height(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally)
            ) {
                repeat(48) { i ->
                    val wave = Math.abs(Math.sin((phase + i * 0.35f).toDouble())).toFloat()
                    val activeFraction = (0.2f + level * (0.25f + 0.75f * wave)).coerceIn(0.2f, 1f)
                    val barHeight = 4.dp + (16.dp * activeFraction - 4.dp) * listeningProgress
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(barHeight)
                            .clip(CircleShape)
                            .background(
                                androidx.compose.ui.graphics.lerp(
                                    TextPrimaryDark.copy(alpha = 0.1f),
                                    accent.copy(alpha = 0.5f),
                                    listeningProgress
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isAnalysing) {
                AiTextLoading(
                    texts = listOf(
                        "Thinking...",
                        "Reading your symptoms...",
                        "Matching specialties...",
                        "Almost..."
                    ),
                    fontSize = 18.sp
                )
            } else {
                Text(
                    text = when {
                        isListening -> "Listening..."
                        isFinal -> "Got it"
                        else -> "Tap to speak"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isListening) accent else TextPrimaryDark.copy(alpha = 0.7f)
                )
            }

            // Specialties actually derived from the spoken words, revealed one by one.
            if (matchedSpecialties.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    matchedSpecialties.forEach { sp ->
                        AnimatedRevealedLine(text = sp)
                    }
                }
            }

            if (transcript.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        transcript,
                        fontSize = 13.sp,
                        color = TextPrimaryDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            errorText?.let {
                Spacer(modifier = Modifier.height(10.dp))
                Text(it, fontSize = 11.sp, color = CoralAccent, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel", fontSize = 12.sp, color = TextSecondaryDark)
                }
                Button(
                    onClick = { onAccept(transcript.trim()) },
                    enabled = transcript.isNotBlank() && !isListening,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = accent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Use this", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/** Legend colour for an estimated stock percentage. */
