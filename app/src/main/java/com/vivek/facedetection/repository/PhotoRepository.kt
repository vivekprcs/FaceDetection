package com.vivek.facedetection.repository

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector.FaceDetectorOptions
import com.vivek.facedetection.model.Photo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class PhotoRepository @Inject constructor(
    @ApplicationContext private val context: Context) : IPhotoRepository   {

    override suspend fun getPhotos() = withContext(Dispatchers.IO) {
        val photos = mutableListOf<Photo>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )

        // Adjust the bucket name based on actual device directories
        val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf("Camera")

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        context.contentResolver.query(
            queryUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val dateAdded = cursor.getLong(dateAddedColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

            /*    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, contentUri))
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, contentUri)
                }

                if (bitmap != null && containsFace(bitmap)) {
                    val photo = Photo(
                        id = id,
                        uri = contentUri,
                        thumbnailUri = contentUri
                    )
                    photos.add(photo)
                }*/

                val photo = Photo(
                    id = id,
                    uri = contentUri,
                    thumbnailUri = contentUri // Consider using separate thumbnail URIs if needed
                )

                photos.add(photo)
            }
        }
        Log.d("FaceDetection", "Photos: $photos")
        photos
    }
   /* private fun containsFace(bitmap: Bitmap): Boolean {
            val inputImage = BitmapImageBuilder(bitmap).build()
            val result = faceDetector.detect(inputImage)
            return result?.detections()?.isNotEmpty() ?:false
        }*/
}

