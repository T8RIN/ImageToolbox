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
import coil.ComponentRegistry
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.imageLoader
import coil.util.DebugLogger
import coil.util.Logger
import com.awxkee.jxlcoder.coil.AnimatedJxlDecoder
import com.github.awxkee.avifcoil.decoder.HeifDecoder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import oupson.apng.coil.AnimatedPngDecoder
import ru.tech.imageresizershrinker.core.data.utils.TimeMeasureInterceptor
import ru.tech.imageresizershrinker.core.resources.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
internal object ImageLoaderModule {

    @Provides
    fun provideImageLoader(
        @ApplicationContext context: Context,
        logger: Logger?,
        componentRegistry: ComponentRegistry
    ): ImageLoader = context.imageLoader.newBuilder()
        .components(componentRegistry)
        .allowHardware(false)
        .logger(logger)
        .build()

    @Provides
    fun provideCoilLogger(): Logger? = if (BuildConfig.DEBUG) DebugLogger()
    else null

    @Provides
    fun provideComponentRegistry(
        @ApplicationContext context: Context,
        interceptor: TimeMeasureInterceptor
    ): ComponentRegistry = ComponentRegistry.Builder()
        .apply {
            add(AnimatedPngDecoder.Factory(context))
            add(
                if (Build.VERSION.SDK_INT >= 28) ImageDecoderDecoder.Factory()
                else GifDecoder.Factory()
            )
            add(SvgDecoder.Factory())
            if (Build.VERSION.SDK_INT >= 24) add(HeifDecoder.Factory(context))
            add(AnimatedJxlDecoder.Factory(context))

            if (BuildConfig.DEBUG) add(interceptor)
        }
        .build()

}