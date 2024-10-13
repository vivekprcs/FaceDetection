package com.vivek.facedetection.repository

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import com.google.mediapipe.tasks.vision.facedetector.FaceDetectorResult
import com.vivek.facedetection.model.Photo
import com.vivek.facedetection.utils.ImageUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val faceDetector: FaceDetector
) : IPhotoRepository {

    override suspend fun getPhotos(): List<Photo> = withContext(Dispatchers.IO) {
        val photos = mutableListOf<Photo>()
        val maxPhotos = 100

        queryPhotos(maxPhotos).forEach { (id, name, dateAdded) ->
            val contentUri = getPhotoUri(id)

            ImageUtils.getBitmap(contentUri,context)?.let { bitmap ->
                if (containsFace(bitmap)) {
                    photos.add(Photo(id, contentUri, contentUri))
                }
            }
        }

        Log.d("FaceDetection", "Photos: $photos")
        photos
    }

    private fun queryPhotos(maxPhotos: Int): List<Triple<Long, String, Long>> {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf("Camera")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        return context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            mutableListOf<Triple<Long, String, Long>>().apply {
                var photoCount = 0
                while (cursor.moveToNext() && photoCount < maxPhotos) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn)
                    add(Triple(id, name, dateAdded))
                    photoCount++
                }
            }
        } ?: emptyList()
    }

    private fun getPhotoUri(id: Long): Uri {
        return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
    }

    private fun containsFace(bitmap: Bitmap): Boolean {
        val argbBitmap = convertToARGB8888(bitmap)
        return try {
            val inputImage = BitmapImageBuilder(argbBitmap).build()
            faceDetector.detect(inputImage)?.detections()?.isNotEmpty() ?: false
        } catch (e: Exception) {
            Log.e("PhotoRepository", "Error detecting face", e)
            false
        }
    }

    private fun convertToARGB8888(bitmap: Bitmap): Bitmap {
        return if (bitmap.config != Bitmap.Config.ARGB_8888) {
            bitmap.copy(Bitmap.Config.ARGB_8888, true)
        } else {
            bitmap
        }
    }

    override suspend fun detectFaces(bitmap: Bitmap): FaceDetectorResult? {
        return withContext(Dispatchers.IO) {
            val argbBitmap = convertToARGB8888(bitmap)
            val inputImage = BitmapImageBuilder(argbBitmap).build()
            faceDetector.detect(inputImage)
        }
    }
}
