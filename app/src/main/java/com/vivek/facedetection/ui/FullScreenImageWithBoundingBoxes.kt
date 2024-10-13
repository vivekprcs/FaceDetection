package com.vivek.facedetection.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import com.google.mediapipe.tasks.components.containers.Detection

@Composable
fun FullScreenImageWithBoundingBoxes(
    bitmap: Bitmap,
    faceDetections: List<Detection>
) {
    Box {
        DrawBoundingBoxes(bitmap, faceDetections)
    }
}

@Composable
fun DrawBoundingBoxes(bitmap: Bitmap, faceDetections: List<Detection>) {
    val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

    val canvas = Canvas(mutableBitmap)
    val paint = Paint().apply {
        color = 0xFFFF0000.toInt()
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

    for (detection in faceDetections) {
        val bbox = detection.boundingBox()
        val rect = RectF(bbox.left, bbox.top, bbox.right, bbox.bottom)
        canvas.drawRect(rect, paint)
    }

    Image(bitmap = mutableBitmap.asImageBitmap(), contentDescription = null)
}
