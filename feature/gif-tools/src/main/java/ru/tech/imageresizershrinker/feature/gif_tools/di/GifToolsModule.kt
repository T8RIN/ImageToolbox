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

package ru.tech.imageresizershrinker.feature.gif_tools.di

import android.content.Context
import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.feature.gif_tools.data.AndroidGifConverter
import ru.tech.imageresizershrinker.feature.gif_tools.domain.GifConverter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GifToolsModule {

    @Provides
    @Singleton
    fun provideConverter(
        imageGetter: ImageGetter<Bitmap, ExifInterface>,
        imageShareProvider: ShareProvider<Bitmap>,
        @ApplicationContext context: Context
    ): GifConverter = AndroidGifConverter(
        imageGetter = imageGetter,
        imageShareProvider = imageShareProvider,
        context = context
    )

}