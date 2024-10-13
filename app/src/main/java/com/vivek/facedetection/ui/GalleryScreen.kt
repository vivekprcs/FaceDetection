package com.vivek.facedetection.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vivek.facedetection.viewmodel.GalleryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(viewModel: GalleryViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Progress Bar Section
        Column(modifier = Modifier.fillMaxSize()) {
            // Display the progress bar
            LinearProgressIndicator(
                progress = { if (uiState.totalCount > 0) uiState.processedCount.toFloat() / uiState.totalCount.toFloat() else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary // Change color based on theme
            )

            // Display the counts
            Text(
                text = "Processed: ${uiState.processedCount} / ${uiState.totalCount}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: "Unknown Error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                else -> {
                    PhotoGrid(
                        photos = uiState.photos,
                        onPhotoClick = { photo ->
                            navController.navigate("fullScreen/${photo.id}")
                        }
                    )
                }
            }
        }
    }
}
