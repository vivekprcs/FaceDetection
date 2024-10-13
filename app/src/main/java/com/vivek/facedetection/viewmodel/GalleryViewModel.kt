package com.vivek.facedetection.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mediapipe.tasks.vision.facedetector.FaceDetectorResult
import com.vivek.facedetection.model.Photo
import com.vivek.facedetection.repository.IPhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val photoRepository: IPhotoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun getPhotoById(photoId: String?): Photo? {
        return photoId?.toLongOrNull()?.let { id ->
            _uiState.value.photos.find { it.id == id }
        }
    }

    init {
        loadPhotos()
        observeProcessedCount()
    }

    fun loadPhotos() {
        Log.d("GalleryViewModel", "loadPhotos called")
        viewModelScope.launch {
            photoRepository.getPhotosFlow()
                .onStart {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            error = "Failed to load photos: ${e.message}",
                            isLoading = false
                        )
                    }
                }
                .collect { photo ->
                    _uiState.update { currentState ->
                        Log.d("GalleryViewModel", "Emitting photo with ID: ${photo.id}")

                        val updatedPhotos = if (!currentState.photos.any { it.id == photo.id }) {
                            currentState.photos + photo
                        } else {
                            currentState.photos
                        }

                        if (updatedPhotos.isNotEmpty() && currentState.photos.isEmpty()) {
                            _uiState.update { it.copy(isLoading = false) }
                        }

                        currentState.copy(photos = updatedPhotos)
                    }
                }
        }
    }

    fun detectFacesInPhoto(bitmap: Bitmap, onResult: (FaceDetectorResult?) -> Unit) {
        viewModelScope.launch {
            val faceDetections = photoRepository.detectFaces(bitmap)
            onResult(faceDetections)
        }
    }

    private fun observeProcessedCount() {
        viewModelScope.launch {
            photoRepository.getProcessedCountFlow()
                .collect { (processedCount, totalCount) ->
                    Log.d("GalleryViewModel", "Photo Count Processed Count: $processedCount, Total Count: $totalCount")
                    _uiState.update { currentState ->
                        currentState.copy(processedCount = processedCount, totalCount = totalCount)
                    }
                }
        }
    }

    // UI State data class
    data class UiState(
        val photos: List<Photo> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val processedCount: Int = 0,
        val totalCount: Int = 0
    )
}