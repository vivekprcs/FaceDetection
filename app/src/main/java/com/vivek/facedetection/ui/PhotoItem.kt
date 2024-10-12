package com.vivek.facedetection.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.platform.testTag
import com.vivek.facedetection.model.Photo

@Composable
fun PhotoItem(photo: Photo) {
    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .aspectRatio(1f)
            .testTag("PhotoItem") // For testing purposes
    ) {
        Image(
            painter = rememberAsyncImagePainter(photo.uri),
            contentDescription = "Photo",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(4.dp))
        )
    }
}