package com.vivek.facedetection.repository

import android.graphics.Bitmap
import com.google.mediapipe.tasks.vision.facedetector.FaceDetectorResult
import com.vivek.facedetection.model.Photo

interface IPhotoRepository {
    suspend fun getPhotos(): List<Photo>
    suspend fun  detectFaces(bitmap: Bitmap): FaceDetectorResult?
}