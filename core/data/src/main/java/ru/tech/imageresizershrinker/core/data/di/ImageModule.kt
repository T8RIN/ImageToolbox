package ru.tech.imageresizershrinker.core.data.di

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.exifinterface.media.ExifInterface
import coil.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.tech.imageresizershrinker.core.data.image.AndroidImageManager
import ru.tech.imageresizershrinker.core.data.image.draw.AndroidImageDrawApplier
import ru.tech.imageresizershrinker.core.data.image.filters.applier.AndroidFilterMaskApplier
import ru.tech.imageresizershrinker.core.data.image.filters.provider.AndroidFilterProvider
import ru.tech.imageresizershrinker.core.domain.image.ImageManager
import ru.tech.imageresizershrinker.core.domain.image.draw.ImageDrawApplier
import ru.tech.imageresizershrinker.core.domain.image.filters.FilterMaskApplier
import ru.tech.imageresizershrinker.core.domain.image.filters.provider.FilterProvider
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageModule {

    @Singleton
    @Provides
    fun provideImageManager(
        @ApplicationContext context: Context,
        fileController: FileController,
        imageLoader: ImageLoader,
        filterProvider: FilterProvider<Bitmap>
    ): ImageManager<Bitmap, ExifInterface> = AndroidImageManager(
        context = context,
        fileController = fileController,
        imageLoader = imageLoader,
        filterProvider = filterProvider
    )

    @Singleton
    @Provides
    fun provideFilterProvider(
        @ApplicationContext context: Context,
    ): FilterProvider<Bitmap> = AndroidFilterProvider(context)

    @Singleton
    @Provides
    fun provideImageDrawApplier(
        imageManager: ImageManager<Bitmap, ExifInterface>
    ): ImageDrawApplier<Bitmap, Path, Color> = AndroidImageDrawApplier(
        imageManager = imageManager
    )

    @Singleton
    @Provides
    fun provideFilterMaskApplier(
        imageManager: ImageManager<Bitmap, ExifInterface>
    ): FilterMaskApplier<Bitmap, Path, Color> = AndroidFilterMaskApplier(
        imageManager = imageManager
    )

}