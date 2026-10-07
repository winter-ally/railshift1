package com.railshift.passenger.ui.screens.scanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.R
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.railshift.passenger.ui.theme.RailshiftTheme
import java.util.concurrent.Executors

@Composable
fun QrScannerScreen(
    onBackClick: () -> Unit,
    onCodeScanned: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = RailshiftTheme.colors

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var scannedCode by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (hasCameraPermission) {
            // Camera Preview with ML Kit
            CameraPreviewWithScanner(
                onCodeDetected = { code ->
                    if (scannedCode == null) {
                        scannedCode = code
                        onCodeScanned(code)
                    }
                }
            )

            // Viewfinder Overlay
            ViewfinderOverlay(
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Permission Rationale / Denied State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(colors.surface2),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = null,
                        tint = colors.accent,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.qr_scanner_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.qr_permission_rationale),
                    fontSize = 14.sp,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_grant_camera_permission")
                ) {
                    Text(
                        text = stringResource(R.string.btn_grant_permission),
                        fontWeight = FontWeight.Bold,
                        color = colors.onAccent
                    )
                }
            }
        }

        // Top App Bar with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("scanner_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.qr_scanner_title),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Quick Scan / Fallback Bar at Bottom for Testing & Stations
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.qr_scanner_caption),
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Test scan station buttons (useful in emulator without a physical QR sheet)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onCodeScanned("BZA") },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.surface.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("sim_scan_bza")
                ) {
                    Text("Scan BZA", fontSize = 12.sp, color = colors.text, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { onCodeScanned("MAS") },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.surface.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("sim_scan_mas")
                ) {
                    Text("Scan MAS", fontSize = 12.sp, color = colors.text, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { onCodeScanned("73205184") },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("sim_scan_ticket")
                ) {
                    Text("Scan Ticket", fontSize = 12.sp, color = colors.onAccent, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
private fun CameraPreviewWithScanner(
    onCodeDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )
                        barcodeScanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    val raw = barcode.rawValue
                                    if (!raw.isNullOrBlank()) {
                                        onCodeDetected(raw)
                                        break
                                    }
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun ViewfinderOverlay(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scannerLine")
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scannerProgress"
    )

    Canvas(modifier = modifier) {
        val frameWidth = size.width * 0.72f
        val frameHeight = frameWidth
        val left = (size.width - frameWidth) / 2
        val top = (size.height - frameHeight) / 2.3f
        val bracketLen = 36.dp.toPx()
        val stroke = 4.dp.toPx()
        val cornerRadius = 12.dp.toPx()
        val accent = Color(0xFF1B6EF3)

        // Semi-transparent darkened background outside frame
        drawRect(Color.Black.copy(alpha = 0.5f), Offset.Zero, Size(size.width, top))
        drawRect(Color.Black.copy(alpha = 0.5f), Offset(0f, top + frameHeight), Size(size.width, size.height - (top + frameHeight)))
        drawRect(Color.Black.copy(alpha = 0.5f), Offset(0f, top), Size(left, frameHeight))
        drawRect(Color.Black.copy(alpha = 0.5f), Offset(left + frameWidth, top), Size(size.width - (left + frameWidth), frameHeight))

        // Top-Left corner
        drawLine(accent, Offset(left, top + cornerRadius), Offset(left, top + bracketLen), stroke)
        drawLine(accent, Offset(left + cornerRadius, top), Offset(left + bracketLen, top), stroke)
        drawArc(
            color = accent,
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(left, top),
            size = Size(cornerRadius * 2, cornerRadius * 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(stroke)
        )

        // Top-Right corner
        val right = left + frameWidth
        drawLine(accent, Offset(right, top + cornerRadius), Offset(right, top + bracketLen), stroke)
        drawLine(accent, Offset(right - bracketLen, top), Offset(right - cornerRadius, top), stroke)
        drawArc(
            color = accent,
            startAngle = 270f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(right - cornerRadius * 2, top),
            size = Size(cornerRadius * 2, cornerRadius * 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(stroke)
        )

        // Bottom-Left corner
        val bottom = top + frameHeight
        drawLine(accent, Offset(left, bottom - bracketLen), Offset(left, bottom - cornerRadius), stroke)
        drawLine(accent, Offset(left + cornerRadius, bottom), Offset(left + bracketLen, bottom), stroke)
        drawArc(
            color = accent,
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(left, bottom - cornerRadius * 2),
            size = Size(cornerRadius * 2, cornerRadius * 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(stroke)
        )

        // Bottom-Right corner
        drawLine(accent, Offset(right, bottom - bracketLen), Offset(right, bottom - cornerRadius), stroke)
        drawLine(accent, Offset(right - bracketLen, bottom), Offset(right - cornerRadius, bottom), stroke)
        drawArc(
            color = accent,
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(right - cornerRadius * 2, bottom - cornerRadius * 2),
            size = Size(cornerRadius * 2, cornerRadius * 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(stroke)
        )

        // Animated laser line
        val lineY = top + (frameHeight * scanProgress)
        drawLine(
            color = accent,
            start = Offset(left + 10.dp.toPx(), lineY),
            end = Offset(right - 10.dp.toPx(), lineY),
            strokeWidth = 2.5.dp.toPx()
        )
    }
}
