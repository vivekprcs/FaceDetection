package com.vivek.facedetection

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.vivek.facedetection.ui.theme.FaceDetectionTheme
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import com.vivek.facedetection.ui.GalleryScreen
import com.vivek.facedetection.ui.PermissionDeniedScreen
import com.vivek.facedetection.viewmodel.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.RuntimeException

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: GalleryViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //TEST MEDIAPIPE FACE DETECTOR INITIALIZATION
         testFaceDetector()
        setContent {
            FaceDetectionTheme {
                val context = LocalContext.current
                val isAndroid13Plus = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

                // Determine which permission to request
                val permissionToRequest = if (isAndroid13Plus) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }

                // Check if permission is already granted
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    permissionToRequest
                ) == PackageManager.PERMISSION_GRANTED

                var permissionState by remember { mutableStateOf(hasPermission) }

                // Launcher to request permission
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    permissionState = isGranted
                    if (isGranted) {
                        viewModel.loadPhotos()
                    }
                }

                LaunchedEffect(key1 = permissionState) {
                    if (!permissionState) {
                        launcher.launch(permissionToRequest)
                    } else {
                        viewModel.loadPhotos()
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Face Detection") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    },
                    content = { padding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                        ) {
                            if (permissionState) {
                                GalleryScreen(viewModel = viewModel)
                            } else {
                                PermissionDeniedScreen(
                                    onRetry = {
                                        launcher.launch(permissionToRequest)
                                    }
                                )
                            }
                        }
                    }
                )

            }
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        FaceDetectionTheme {
            Greeting("Android")
        }
    }

    private fun testFaceDetector() {
        try {
            val applicationContext = applicationContext

            // Setting up BaseOptions for the FaceDetector
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath("face_detection_short_range.tflite")
                .build()

            val faceDetectorOptions = FaceDetector.FaceDetectorOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.IMAGE)
                .build()

            Log.d("FaceDetectionTest", "Creating FaceDetector with options: $faceDetectorOptions")

            // Create FaceDetector from assets
            val faceDetector = FaceDetector.createFromOptions(applicationContext, faceDetectorOptions)

            Log.d("FaceDetectionTest", "FaceDetector created: $faceDetector")

        } catch (e: IllegalStateException) {
            Log.e("FaceDetectionTest", "TFLite failed to load model with error: ${e.message}")
        } catch (e: RuntimeException) {
            Log.e("FaceDetectionTest", "Face detector failed to load model with error: ${e.message}")
        } catch (e: Exception) {
            Log.e("FaceDetectionTest", "An unexpected error occurred: ${e.message}")
        }
    }

}