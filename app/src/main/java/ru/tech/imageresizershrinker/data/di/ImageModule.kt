package ru.tech.imageresizershrinker.data.di

import android.content.Context
import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.tech.imageresizershrinker.data.image.AndroidImageManager
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.repository.CipherRepository
import ru.tech.imageresizershrinker.domain.saving.FileController
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageModule {

    @Singleton
    @Provides
    fun provideImageManager(
        @ApplicationContext context: Context,
        fileController: FileController
    ): ImageManager<Bitmap, ExifInterface> = AndroidImageManager(
        context = context,
        fileController = fileController
    )

}