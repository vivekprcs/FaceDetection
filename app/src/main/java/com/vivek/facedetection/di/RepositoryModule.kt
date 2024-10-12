package com.vivek.facedetection.di

import com.vivek.facedetection.repository.IPhotoRepository
import com.vivek.facedetection.repository.PhotoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindPhotoRepository(impl: PhotoRepository): IPhotoRepository

}