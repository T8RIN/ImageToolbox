package ru.tech.imageresizershrinker.data.di

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.exifinterface.media.ExifInterface
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.imageLoader
import com.github.awxkee.avifcoil.HeifDecoder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.tech.imageresizershrinker.data.image.AndroidImageManager
import ru.tech.imageresizershrinker.data.image.draw.AndroidImageDrawApplier
import ru.tech.imageresizershrinker.data.image.filters.provider.AndroidFilterProvider
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.image.draw.ImageDrawApplier
import ru.tech.imageresizershrinker.domain.image.filters.provider.FilterProvider
import ru.tech.imageresizershrinker.domain.saving.FileController
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
        @ApplicationContext context: Context,
        imageManager: ImageManager<Bitmap, ExifInterface>
    ): ImageDrawApplier<Bitmap, Path, Color> = AndroidImageDrawApplier(
        context = context,
        imageManager = imageManager
    )

}