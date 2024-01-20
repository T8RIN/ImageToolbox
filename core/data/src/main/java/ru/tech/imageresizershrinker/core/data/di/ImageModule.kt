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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.exifinterface.media.ExifInterface
import coil.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.tech.imageresizershrinker.core.data.image.AndroidImageCompressor
import ru.tech.imageresizershrinker.core.data.image.AndroidImageGetter
import ru.tech.imageresizershrinker.core.data.image.AndroidImageManager
import ru.tech.imageresizershrinker.core.data.image.AndroidImagePreviewCreator
import ru.tech.imageresizershrinker.core.data.image.AndroidImageScaler
import ru.tech.imageresizershrinker.core.data.image.AndroidShareProvider
import ru.tech.imageresizershrinker.core.data.image.draw.AndroidImageDrawApplier
import ru.tech.imageresizershrinker.core.data.image.filters.applier.AndroidFilterMaskApplier
import ru.tech.imageresizershrinker.core.data.image.filters.provider.AndroidFilterProvider
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImageManager
import ru.tech.imageresizershrinker.core.domain.image.ImagePreviewCreator
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.image.draw.ImageDrawApplier
import ru.tech.imageresizershrinker.core.domain.image.filters.FilterMaskApplier
import ru.tech.imageresizershrinker.core.domain.image.filters.provider.FilterProvider
import ru.tech.imageresizershrinker.core.domain.repository.SettingsRepository
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageModule {

    @Singleton
    @Provides
    fun provideImageManager(
        @ApplicationContext context: Context,
        imageLoader: ImageLoader,
        filterProvider: FilterProvider<Bitmap>
    ): ImageManager<Bitmap> = AndroidImageManager(
        context = context,
        imageLoader = imageLoader,
        filterProvider = filterProvider
    )

    @Singleton
    @Provides
    fun provideImageScaler(
        settingsRepository: SettingsRepository,
        imageCompressor: ImageCompressor<Bitmap>,
        imageManager: ImageManager<Bitmap>
    ): ImageScaler<Bitmap> = AndroidImageScaler(
        settingsRepository = settingsRepository,
        imageCompressor = imageCompressor,
        imageManager = imageManager
    )

    @Singleton
    @Provides
    fun provideImageCompressor(
        @ApplicationContext context: Context,
        imageManager: ImageManager<Bitmap>,
        settingsRepository: SettingsRepository
    ): ImageCompressor<Bitmap> = AndroidImageCompressor(
        context = context,
        imageManager = imageManager,
        settingsRepository = settingsRepository
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
    fun provideFilterProvider(
        @ApplicationContext context: Context,
    ): FilterProvider<Bitmap> = AndroidFilterProvider(context)

    @Singleton
    @Provides
    fun provideImageDrawApplier(
        imageManager: ImageManager<Bitmap>,
        imageGetter: ImageGetter<Bitmap, ExifInterface>
    ): ImageDrawApplier<Bitmap, Path, Color> = AndroidImageDrawApplier(
        imageManager = imageManager,
        imageGetter = imageGetter
    )

    @Singleton
    @Provides
    fun provideImagePreviewCreator(
        imageManager: ImageManager<Bitmap>,
        imageGetter: ImageGetter<Bitmap, ExifInterface>,
        imageCompressor: ImageCompressor<Bitmap>,
        imageScaler: ImageScaler<Bitmap>
    ): ImagePreviewCreator<Bitmap> = AndroidImagePreviewCreator(
        imageManager = imageManager,
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

    @Singleton
    @Provides
    fun provideFilterMaskApplier(
        imageGetter: ImageGetter<Bitmap, ExifInterface>,
        imageManager: ImageManager<Bitmap>
    ): FilterMaskApplier<Bitmap, Path, Color> = AndroidFilterMaskApplier(
        imageGetter = imageGetter,
        imageManager = imageManager
    )

}