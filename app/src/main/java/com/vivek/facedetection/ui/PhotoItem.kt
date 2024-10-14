package com.vivek.facedetection.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.platform.testTag
import com.vivek.facedetection.model.Photo
import com.vivek.facedetection.utils.DimensionUtils

@Composable
fun PhotoItem(photo: Photo,  onClick: (Photo) -> Unit) {
    Card(
        shape = RoundedCornerShape(DimensionUtils.smallPadding),
        modifier = Modifier
            .aspectRatio(DimensionUtils.ASPECT_RATIO)
            .testTag("PhotoItem")
            .clickable { onClick(photo) }
    ) {
        Image(
            painter = rememberAsyncImagePainter(photo.uri),
            contentDescription = "Photo",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(DimensionUtils.smallPadding))
        )
    }
}