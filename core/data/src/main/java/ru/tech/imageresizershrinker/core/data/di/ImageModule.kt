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
import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import coil.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.tech.imageresizershrinker.core.data.image.AndroidImageCompressor
import ru.tech.imageresizershrinker.core.data.image.AndroidImageGetter
import ru.tech.imageresizershrinker.core.data.image.AndroidImagePreviewCreator
import ru.tech.imageresizershrinker.core.data.image.AndroidImageScaler
import ru.tech.imageresizershrinker.core.data.image.AndroidImageTransformer
import ru.tech.imageresizershrinker.core.data.image.AndroidShareProvider
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImagePreviewCreator
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.core.settings.domain.SettingsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ImageModule {

    @Singleton
    @Provides
    fun provideImageManager(
        @ApplicationContext context: Context,
        imageLoader: ImageLoader
    ): ImageTransformer<Bitmap> = AndroidImageTransformer(
        context = context,
        imageLoader = imageLoader
    )

    @Singleton
    @Provides
    fun provideImageScaler(
        settingsRepository: SettingsRepository,
        imageTransformer: ImageTransformer<Bitmap>,
        filterProvider: FilterProvider<Bitmap>
    ): ImageScaler<Bitmap> = AndroidImageScaler(
        settingsRepository = settingsRepository,
        imageTransformer = imageTransformer,
        filterProvider = filterProvider
    )

    @Singleton
    @Provides
    fun provideImageCompressor(
        @ApplicationContext context: Context,
        imageTransformer: ImageTransformer<Bitmap>,
        imageScaler: ImageScaler<Bitmap>
    ): ImageCompressor<Bitmap> = AndroidImageCompressor(
        context = context,
        imageTransformer = imageTransformer,
        imageScaler = imageScaler
    )

    @Singleton
    @Provides
    fun provideImageGetter(
        imageLoader: ImageLoader,
        @ApplicationContext context: Context,
    ): ImageGetter<Bitmap, ExifInterface> = AndroidImageGetter(
        imageLoader = imageLoader,
        context = context
    )

    @Singleton
    @Provides
    fun provideImagePreviewCreator(
        imageTransformer: ImageTransformer<Bitmap>,
        imageGetter: ImageGetter<Bitmap, ExifInterface>,
        imageCompressor: ImageCompressor<Bitmap>,
        imageScaler: ImageScaler<Bitmap>
    ): ImagePreviewCreator<Bitmap> = AndroidImagePreviewCreator(
        imageTransformer = imageTransformer,
        imageGetter = imageGetter,
        imageCompressor = imageCompressor,
        imageScaler = imageScaler
    )

    @Singleton
    @Provides
    fun provideShareProvider(
        @ApplicationContext context: Context,
        imageCompressor: ImageCompressor<Bitmap>,
        imageGetter: ImageGetter<Bitmap, ExifInterface>,
        fileController: FileController
    ): ShareProvider<Bitmap> = AndroidShareProvider(
        context = context,
        imageGetter = imageGetter,
        imageCompressor = imageCompressor,
        fileController = fileController
    )

}