package com.vivek.facedetection.repository

import com.vivek.facedetection.model.Photo

interface IPhotoRepository {
    suspend fun getPhotos(): List<Photo>

}