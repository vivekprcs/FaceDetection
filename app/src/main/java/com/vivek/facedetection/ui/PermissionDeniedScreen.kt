package com.vivek.facedetection.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vivek.facedetection.utils.DimensionUtils

@Composable
fun PermissionDeniedScreen(onRetry: () -> Unit = {}) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(DimensionUtils.largePadding)
        ) {
            Text(
                text = "Permission Denied",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(DimensionUtils.mediumPadding))
            Text(
                text = "Unable to display photos without the required permissions.",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(DimensionUtils.largePadding))
            Button(onClick = onRetry) {
                Text(text = "Retry")
            }
            Spacer(modifier = Modifier.height(DimensionUtils.largePadding))
        }
    }
}