package ru.tech.imageresizershrinker.feature.recognize.text.di

import android.content.Context
import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.tech.imageresizershrinker.core.domain.image.ImageManager
import ru.tech.imageresizershrinker.feature.recognize.text.data.AndroidImageTextReader
import ru.tech.imageresizershrinker.feature.recognize.text.domain.ImageTextReader
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object RecognizeTextModule {

    @Singleton
    @Provides
    fun provideImageTextReader(
        @ApplicationContext context: Context,
        imageManager: ImageManager<Bitmap, ExifInterface>
    ): ImageTextReader<Bitmap> = AndroidImageTextReader(
        imageManager = imageManager,
        context = context
    )

}