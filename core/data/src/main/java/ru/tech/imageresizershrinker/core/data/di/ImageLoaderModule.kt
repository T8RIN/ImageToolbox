/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.core.data.di

import android.content.Context
import android.os.Build
import coil.Coil
import coil.ComponentRegistry
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.imageLoader
import coil.util.DebugLogger
import coil.util.Logger
import com.awxkee.jxlcoder.coil.AnimatedJxlDecoder
import com.gemalto.jp2.coil.Jpeg2000Decoder
import com.t8rin.awebp.coil.AnimatedWebPDecoder
import com.t8rin.djvu_coder.coil.DjvuDecoder
import com.t8rin.psd.coil.PsdDecoder
import com.t8rin.qoi_coder.coil.QoiDecoder
import com.t8rin.tiff.TiffDecoder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import oupson.apng.coil.AnimatedPngDecoder
import ru.tech.imageresizershrinker.core.data.coil.HeifDecoderCoil2
import ru.tech.imageresizershrinker.core.data.coil.TimeMeasureInterceptor
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.resources.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
internal object ImageLoaderModule {

    @Provides
    fun provideImageLoader(
        @ApplicationContext context: Context,
        logger: Logger?,
        componentRegistry: ComponentRegistry,
        dispatchersHolder: DispatchersHolder
    ): ImageLoader = context.imageLoader.newBuilder()
        .components(componentRegistry)
        .decoderDispatcher(dispatchersHolder.decodingDispatcher)
        .fetcherDispatcher(dispatchersHolder.ioDispatcher)
        .transformationDispatcher(dispatchersHolder.defaultDispatcher)
        .allowHardware(false)
        .logger(logger)
        .build()
        .also(Coil::setImageLoader)

    @Provides
    fun provideCoilLogger(): Logger? = if (BuildConfig.DEBUG) DebugLogger()
    else null

    @Provides
    fun provideComponentRegistry(
        @ApplicationContext context: Context,
        interceptor: TimeMeasureInterceptor
    ): ComponentRegistry = ComponentRegistry.Builder()
        .apply {
            add(AnimatedPngDecoder.Factory())
            if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
            else {
                add(GifDecoder.Factory())
                add(AnimatedWebPDecoder.Factory())
            }
            add(SvgDecoder.Factory())
            if (Build.VERSION.SDK_INT >= 24) {
                add(HeifDecoderCoil2.Factory())
            }
            add(AnimatedJxlDecoder.Factory(context))
            add(Jpeg2000Decoder.Factory())
            add(TiffDecoder.Factory())
            add(QoiDecoder.Factory())
            add(PsdDecoder.Factory())
            add(DjvuDecoder.Factory())
            if (BuildConfig.DEBUG) add(interceptor)
        }
        .build()

}