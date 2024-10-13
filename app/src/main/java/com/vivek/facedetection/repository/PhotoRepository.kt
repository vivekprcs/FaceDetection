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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val faceDetector: FaceDetector
) : IPhotoRepository {

    private var processedCount = 0 // Count for all processed images
    private var totalCount = 0 // Count for all processed images
    private var queryCount =0
    private var emittingCount =0
    private var methodCalledCount =0


    override fun getPhotosFlow(): Flow<Photo> = flow {
        methodCalledCount++
        Log.d("FaceDetection", "PHOTO COUNT Querying photos from ViewModel $methodCalledCount")
        queryPhotos().forEach { (id, name, dateAdded) ->
             queryCount++
            Log.d("FaceDetection", "PHOTO COUNT QUERY COUNT $queryCount")

            val contentUri = getPhotoUri(id)
            val bitmap = withContext(Dispatchers.IO) {
                ImageUtils.getBitmap(contentUri, context)
            }

            bitmap?.let {
                val hasFace = withContext(Dispatchers.Default) {
                    containsFace(it)
                }

                if (hasFace) {
                    val photo = Photo(id, contentUri, contentUri)
                    emittingCount++
                    Log.d("FaceDetection", "PHOTO COUNT Photos emission with count $emittingCount")
                    emit(photo)
                }

                it.recycle()
            }
        }

        Log.d("FaceDetection", "Photos emission completed.")
    }.flowOn(Dispatchers.IO)

    private fun queryPhotos(): List<Triple<Long, String, Long>> {
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
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn)
                    add(Triple(id, name, dateAdded))
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
            val hasFace = faceDetector.detect(inputImage)?.detections()?.isNotEmpty() ?: false
             processedCount++

            hasFace
        } catch (e: Exception) {
            Log.e("PhotoRepository", "Error detecting face", e)
            processedCount++
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

    override suspend fun getProcessedCountFlow(): Flow<Pair<Int,Int>> = flow {
        totalCount = getTotalPhotoCount()
        Log.d("FaceDetection", "Total photo count: $totalCount")
        while (processedCount < totalCount) {
            Log.d("FaceDetection", "PHOTO COUNT CURRENT COUNT $processedCount totalCounts- $totalCount")
            emit(Pair(processedCount, totalCount))
            delay(1000)
        }
        emit(Pair(processedCount,totalCount))
    }

    private fun getTotalPhotoCount(): Int {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf("Camera")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        val totalCount = cursor?.count ?: 0
        cursor?.close()

        return totalCount
    }


}
