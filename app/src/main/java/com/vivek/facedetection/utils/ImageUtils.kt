package com.vivek.facedetection.utils
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import java.io.IOException

object ImageUtils {
    fun getBitmap(uri: Uri, context: Context): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        } catch (e: IOException) {
            Log.e("ImageUtils", "IOException while retrieving bitmap from URI: $uri", e)
            null
        } catch (e: IllegalArgumentException) {
            Log.e("ImageUtils", "Invalid URI provided for bitmap retrieval: $uri", e)
            null
        } catch (e: Exception) {
            Log.e("ImageUtils", "Unexpected error while retrieving bitmap from URI: $uri", e)
            null
        }
    }
}
