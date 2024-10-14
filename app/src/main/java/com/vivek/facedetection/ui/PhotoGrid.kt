package com.vivek.facedetection.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vivek.facedetection.model.Photo
import com.vivek.facedetection.utils.DimensionUtils

@Composable
fun PhotoGrid(photos: List<Photo>, onPhotoClick: (Photo) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(DimensionUtils.GRID_CELLS),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(DimensionUtils.smallPadding),
        verticalArrangement = Arrangement.spacedBy(DimensionUtils.smallPadding),
        horizontalArrangement = Arrangement.spacedBy(DimensionUtils.smallPadding)
    ) {
        items(photos) { photo ->
            PhotoItem(photo = photo, onClick = onPhotoClick)
        }
    }
}