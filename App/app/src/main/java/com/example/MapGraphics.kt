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


data class RankColors(
    val badgeBg: Color,
    val badgeBorder: Color,
    val badgeText: Color,
    val cardBg: Color,
    val cardBorder: Color,
    val pinColor: Color,
    val pinBorder: Color,
    val ratingBg: Color,
    val ratingText: Color,
    val textColor: Color
)

/**
 * Animated "thinking" indicator.
 *
 * Adapted from KokonutUI's "AI Text Loading" (React/Motion) to Compose: cycles
 * status messages, each entering with a fade + slide, over a shimmering gradient
 * sweep across the glyphs (Brush.linearGradient on the text, animated offset —
 * the Compose equivalent of the animated `background-position` + `bg-clip-text`).
 */

fun getStockColor(percent: Int): Color = when {
    percent >= 80 -> Color(0xFF15803D) // Very likely
    percent >= 60 -> Color(0xFF65A30D) // Likely
    percent >= 35 -> Color(0xFFF59E0B) // Uncertain
    else -> Color(0xFFDC2626)          // Unlikely
}

fun getRankColors(score: Int): RankColors {
    return when {
        // Excellent (Score >= 85) - 🔵
        score >= 85 -> RankColors(
            badgeBg = Color(0xFFE8F0FE),
            badgeBorder = Color(0xFFADCCF9),
            badgeText = Color(0xFF4285F4),
            cardBg = Color(0xFFF8FAFF),
            cardBorder = Color(0xFFD2E3FC),
            pinColor = Color(0xFF4285F4),
            pinBorder = Color(0xFF1967D2),
            ratingBg = Color(0xFF4285F4),
            ratingText = Color.White,
            textColor = Color(0xFF174EA6)
        )
        // Good (Score >= 70) - 🟢
        score >= 70 -> RankColors(
            badgeBg = Color(0xFFE6F4EA),
            badgeBorder = Color(0xFFCEEAD6),
            badgeText = Color(0xFF34A853),
            cardBg = Color(0xFFF1F8F3),
            cardBorder = Color(0xFFCEEAD6),
            pinColor = Color(0xFF34A853),
            pinBorder = Color(0xFF137333),
            ratingBg = Color(0xFF34A853),
            ratingText = Color.White,
            textColor = Color(0xFF0D652D)
        )
        // Average (Score >= 50) - 🟡
        score >= 50 -> RankColors(
            badgeBg = Color(0xFFFFF8E1),
            badgeBorder = Color(0xFFFFE082),
            badgeText = Color(0xFFFBBC05),
            cardBg = Color(0xFFFFFDE7),
            cardBorder = Color(0xFFFFF176),
            pinColor = Color(0xFFFBBC05),
            pinBorder = Color(0xFFF9AB00),
            ratingBg = Color(0xFFFBBC05),
            ratingText = Color.White,
            textColor = Color(0xFFE37400)
        )
        // Poor (Score < 50) - 🔴
        else -> RankColors(
            badgeBg = Color(0xFFFCE8E6),
            badgeBorder = Color(0xFFF9ABAF),
            badgeText = Color(0xFFEA4335),
            cardBg = Color(0xFFFFF5F5),
            cardBorder = Color(0xFFF9ABAF),
            pinColor = Color(0xFFEA4335),
            pinBorder = Color(0xFFC5221F),
            ratingBg = Color(0xFFEA4335),
            ratingText = Color.White,
            textColor = Color(0xFFA50E0E)
        )
    }
}

fun createBlueDotIcon(context: Context): BitmapDescriptor {
    val size = 76
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = android.graphics.Paint().apply {
        isAntiAlias = true
    }

    // Outer glow/shadow
    paint.color = android.graphics.Color.parseColor("#404285F4")
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

    // White border
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(size / 2f, size / 2f, size * 0.4f, paint)

    // Blue core
    paint.color = android.graphics.Color.parseColor("#4285F4")
    canvas.drawCircle(size / 2f, size / 2f, size * 0.32f, paint)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun createMarkerIcon(context: Context, percentage: Int, color: Color): BitmapDescriptor {
    val size = 100
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = android.graphics.Paint().apply {
        isAntiAlias = true
        this.color = color.toArgb()
    }
    
    // Draw circle
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
    
    // Draw white border
    paint.style = android.graphics.Paint.Style.STROKE
    paint.color = android.graphics.Color.WHITE
    paint.strokeWidth = 6f
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 3f, paint)
    
    // Draw text
    paint.style = android.graphics.Paint.Style.FILL
    paint.textSize = 32f
    paint.textAlign = android.graphics.Paint.Align.CENTER
    paint.typeface = Typeface.DEFAULT_BOLD
    val text = "$percentage%"
    val textBounds = Rect()
    paint.getTextBounds(text, 0, text.length, textBounds)
    canvas.drawText(text, size / 2f, size / 2f - textBounds.centerY(), paint)
    
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun createTimeMarkerIcon(context: Context, text: String, color: Color): BitmapDescriptor {
    val paint = android.graphics.Paint().apply {
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    }
    
    paint.textSize = 28f
    val textBounds = Rect()
    paint.getTextBounds(text, 0, text.length, textBounds)
    
    val paddingX = 20
    val paddingY = 12
    val width = textBounds.width() + paddingX * 2
    val height = textBounds.height() + paddingY * 2
    
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    
    // Draw background rounded rectangle (white)
    paint.style = android.graphics.Paint.Style.FILL
    paint.color = android.graphics.Color.WHITE
    val rectF = android.graphics.RectF(0f, 0f, width.toFloat(), height.toFloat())
    canvas.drawRoundRect(rectF, 12f, 12f, paint)
    
    // Draw border
    paint.style = android.graphics.Paint.Style.STROKE
    paint.color = color.toArgb()
    paint.strokeWidth = 4f
    canvas.drawRoundRect(rectF, 12f, 12f, paint)
    
    // Draw text inside
    paint.style = android.graphics.Paint.Style.FILL
    paint.color = android.graphics.Color.BLACK
    paint.textAlign = android.graphics.Paint.Align.CENTER
    canvas.drawText(text, width / 2f, height / 2f - textBounds.centerY(), paint)
    
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun calculateLatitudeOffset(
    userLat: Double,
    zoom: Float,
    density: Float,
    offsetPx: Float
): Double {
    val scale = (256.0 * density * Math.pow(2.0, zoom.toDouble())) / 360.0
    val cosLat = Math.cos(Math.toRadians(userLat))
    val pixelsPerDegree = scale / cosLat
    return offsetPx / pixelsPerDegree
}

// Fixed dimensions representation helpers

fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val lat1Rad = Math.toRadians(lat1)
    val lat2Rad = Math.toRadians(lat2)
    val dLon = Math.toRadians(lon2 - lon1)
    
    val y = Math.sin(dLon) * Math.cos(lat2Rad)
    val x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(dLon)
    
    val brng = Math.atan2(y, x)
    return ((Math.toDegrees(brng) + 360) % 360).toFloat()
}
