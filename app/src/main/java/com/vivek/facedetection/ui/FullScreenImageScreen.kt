package com.vivek.facedetection.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.google.mediapipe.tasks.components.containers.Detection
import com.vivek.facedetection.model.Photo
import com.vivek.facedetection.utils.ImageUtils
import com.vivek.facedetection.viewmodel.GalleryViewModel

@Composable
fun FullScreenImageScreen(photo: Photo, viewModel: GalleryViewModel) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var faceDetections by remember { mutableStateOf(emptyList<Detection>()) }
    val context = LocalContext.current

    LaunchedEffect(photo.uri) {
        bitmap = ImageUtils.getBitmap(photo.uri, context)
        bitmap?.let {
            viewModel.detectFacesInPhoto(it) { detections ->
                faceDetections = detections?.detections() ?: emptyList()
               if(faceDetections.isEmpty()){
                   Log.d("FullScreenImageScreen", "No faces detected")
               }else{
                   Log.d("FullScreenImageScreen", "Faces detected: ${faceDetections.size}")
                }
            }
        }
    }

    bitmap?.let { bmp ->
        FullScreenImageWithBoundingBoxes(bmp, faceDetections)
    } ?: run {
        Image(
            painter = rememberAsyncImagePainter(photo.uri),
            contentDescription = "Loading Image...",
            modifier = Modifier.fillMaxSize()
        )
    }
}
