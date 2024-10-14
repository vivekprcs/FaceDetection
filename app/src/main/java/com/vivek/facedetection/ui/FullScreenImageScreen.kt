package com.vivek.facedetection.ui

import FullScreenImageWithBoundingBoxes
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.vivek.facedetection.model.Photo
import com.vivek.facedetection.model.TaggedDetection
import com.vivek.facedetection.utils.ImageUtils
import com.vivek.facedetection.viewmodel.GalleryViewModel

@Composable
fun FullScreenImageScreen(photo: Photo, viewModel: GalleryViewModel) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var faceDetections by remember { mutableStateOf(emptyList<TaggedDetection>()) }
    val context = LocalContext.current

    LaunchedEffect(photo.uri) {
        bitmap = ImageUtils.getBitmap(photo.uri, context)
        bitmap?.let {
            viewModel.detectFacesInPhoto(it) { detections ->
                faceDetections = detections?.detections()?.map { detection ->
                    TaggedDetection(detection)
                } ?: emptyList()
                if(faceDetections.isEmpty()){
                   Log.d("FullScreenImageScreen", "No faces detected")
               }else{
                   Log.d("FullScreenImageScreen", "Faces detected: ${faceDetections.size}")
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            bitmap?.recycle()
            bitmap = null
        }
    }

    bitmap?.let { bmp ->
        FullScreenImageWithBoundingBoxes(bmp, faceDetections){
            index, newTag ->
           faceDetections[index].tag = newTag
        }
    } ?: run {
        Image(
            painter = rememberAsyncImagePainter(photo.uri),
            contentDescription = "Loading Image...",
            modifier = Modifier.fillMaxSize()
        )
    }
}
