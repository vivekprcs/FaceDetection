package com.vivek.facedetection.di

import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FaceDetectorModule {

//    @Provides
//    @Singleton
//    fun provideFaceDetector(@ApplicationContext context: Context): FaceDetector {
//        return try {
//            val baseOptions = BaseOptions.builder()
//                .setModelAssetPath("face_detection_short_range.tflite")
//                .build()
//            val faceDetectorOptions = FaceDetector.FaceDetectorOptions.builder()
//                .setBaseOptions(baseOptions)
//                .setRunningMode(RunningMode.IMAGE).build()
//
//            Log.d("FaceDetectionModule", "Creating FaceDetector with options: $faceDetectorOptions")
//            FaceDetector.createFromOptions(context, faceDetectorOptions).also {
//                Log.d("FaceDetectionModule", "FaceDetector created successfully")
//            } ?: run {
//                Log.e("FaceDetectionModule", "Failed to create FaceDetector")
//                throw IllegalStateException("Failed to create FaceDetector")
//            }
//        } catch (e: Exception) {
//            Log.e("FaceDetectionModule", "Error creating FaceDetector", e)
//            throw e
//        }
//    }
}
