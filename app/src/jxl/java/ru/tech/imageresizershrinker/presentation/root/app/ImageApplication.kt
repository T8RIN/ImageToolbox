package ru.tech.imageresizershrinker.presentation.root.app

import android.app.Application
import android.os.Build
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.imageLoader
import com.github.awxkee.avifcoil.HeifDecoder
import com.google.mlkit.common.sdkinternal.MlKitContext
import com.t8rin.jxlcoder_coil.JxlDecoder
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class ImageApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MlKitContext.initializeIfNeeded(this)
        loader = {
            imageLoader.newBuilder().components {
                if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                else add(GifDecoder.Factory())
                add(SvgDecoder.Factory())
                if (Build.VERSION.SDK_INT >= 24) add(HeifDecoder.Factory())
                add(JxlDecoder.Factory())
            }.allowHardware(false).build()
        }
    }

    companion object {
        lateinit var loader: () -> ImageLoader
            private set
    }
}