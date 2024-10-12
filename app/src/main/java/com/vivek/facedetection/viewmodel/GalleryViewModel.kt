package com.vivek.facedetection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        loadPhotos()
    }

     fun loadPhotos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val photoList = photoRepository.getPhotos()
                _uiState.update { it.copy(photos = photoList, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to load photos: ${e.message}", isLoading = false) }
            }
        }
        }

    // UI State data class
    data class UiState(
        val photos: List<Photo> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )
}