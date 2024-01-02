package ru.tech.imageresizershrinker.coredata.di

import android.content.Context
import android.os.Build
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

@Module
@InstallIn(SingletonComponent::class)
object ImageLoaderModule {

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