package com.vivek.facedetection.repository

import android.graphics.Bitmap
import com.google.mediapipe.tasks.vision.facedetector.FaceDetectorResult
import com.vivek.facedetection.model.Photo
import kotlinx.coroutines.flow.Flow

interface IPhotoRepository {
    fun getPhotosFlow(): Flow<Photo>
    suspend fun  detectFaces(bitmap: Bitmap): FaceDetectorResult?
    suspend fun getProcessedCountFlow(): Flow<Pair<Int, Int>>
}