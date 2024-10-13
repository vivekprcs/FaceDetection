package com.vivek.facedetection.navigation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vivek.facedetection.ui.GalleryScreen
import com.vivek.facedetection.ui.PermissionDeniedScreen
import com.vivek.facedetection.ui.FullScreenImageScreen
import com.vivek.facedetection.viewmodel.GalleryViewModel

@Composable
fun NavigationGraph(navController: NavHostController, viewModel: GalleryViewModel) {
    val context = LocalContext.current
    val isAndroid13Plus = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    val permissionToRequest = if (isAndroid13Plus) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var permissionState by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, permissionToRequest) == PackageManager.PERMISSION_GRANTED) }
    var shouldShowRationale by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionState = isGranted

        if (isGranted) {
            viewModel.loadPhotos()
            navController.navigate("gallery"){
                popUpTo("permissionHandler") { inclusive = true }
            }

        } else {
            shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permissionToRequest)
            if (!shouldShowRationale) {
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${context.packageName}")
                context.startActivity(intent)
            } else {
                navController.navigate("permissionDenied")
            }
        }
    }

    LaunchedEffect(Unit) {
        permissionState = ContextCompat.checkSelfPermission(context, permissionToRequest) == PackageManager.PERMISSION_GRANTED
        if (permissionState) {
            viewModel.loadPhotos()
            navController.navigate("gallery") {
                popUpTo("permissionHandler") { inclusive = true }
            }

        } else {
            navController.navigate("permissionHandler")
        }
    }

    NavHost(navController, startDestination = "permissionHandler") {
        composable("permissionHandler") {
            Box {
                if (permissionState) {
                    viewModel.loadPhotos()
                    navController.navigate("gallery") {
                        popUpTo("permissionHandler") { inclusive = true }
                    }

                } else {
                    navController.navigate("permissionDenied")
                }
            }
        }
        composable("gallery") {
            GalleryScreen(viewModel = viewModel, navController = navController)
        }
        composable("permissionDenied") {
            PermissionDeniedScreen(
                onRetry = {
                    launcher.launch(permissionToRequest)
                }
            )
        }

        composable("fullScreen/{photoId}") { backStackEntry ->
            val photoId = backStackEntry.arguments?.getString("photoId")
            val photo = viewModel.getPhotoById(photoId)
            if (photo != null) {
                FullScreenImageScreen(photo, viewModel = viewModel)
            }
        }
    }
}


