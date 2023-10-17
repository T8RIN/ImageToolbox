package ru.tech.imageresizershrinker.data.di

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
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
import ru.tech.imageresizershrinker.domain.image.ImageManager
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
        imageLoader: ImageLoader
    ): ImageManager<Bitmap, ExifInterface> = AndroidImageManager(
        context = context,
        fileController = fileController,
        imageLoader = imageLoader
    )

    @Provides
    fun provideImageLoader(
        @ApplicationContext context: Context
    ): ImageLoader = context.imageLoader.newBuilder().components {
        if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
        else add(GifDecoder.Factory())
        add(SvgDecoder.Factory())
        if (Build.VERSION.SDK_INT >= 24) add(HeifDecoder.Factory(context))
    }.allowHardware(false).build()
}