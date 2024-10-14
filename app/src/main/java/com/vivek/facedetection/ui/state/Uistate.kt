package com.vivek.facedetection.ui.state

import com.vivek.facedetection.model.Photo

// UI State data class
data class UiState(
    val photos: List<Photo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val processedCount: Int = 0,
    val totalCount: Int = 0
)
