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

package com.t8rin.imagetoolbox.core.data.di

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.data.image.AndroidImageCompressor
import com.t8rin.imagetoolbox.core.data.image.AndroidImageGetter
import com.t8rin.imagetoolbox.core.data.image.AndroidImagePreviewCreator
import com.t8rin.imagetoolbox.core.data.image.AndroidImageScaler
import com.t8rin.imagetoolbox.core.data.image.AndroidImageTransformer
import com.t8rin.imagetoolbox.core.data.image.AndroidShareProvider
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImagePreviewCreator
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.ImageTransformer
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface ImageModule {

    @Singleton
    @Binds
    fun provideImageManager(
        transformer: AndroidImageTransformer
    ): ImageTransformer<Bitmap>

    @Singleton
    @Binds
    fun provideImageScaler(
        scaler: AndroidImageScaler
    ): ImageScaler<Bitmap>

    @Singleton
    @Binds
    fun provideImageCompressor(
        compressor: AndroidImageCompressor
    ): ImageCompressor<Bitmap>

    @Singleton
    @Binds
    fun provideImageGetter(
        getter: AndroidImageGetter
    ): ImageGetter<Bitmap>

    @Singleton
    @Binds
    fun provideImagePreviewCreator(
        creator: AndroidImagePreviewCreator
    ): ImagePreviewCreator<Bitmap>

    @Singleton
    @Binds
    fun provideShareProvider(
        provider: AndroidShareProvider
    ): ShareProvider

    @Singleton
    @Binds
    fun provideImageShareProvider(
        provider: AndroidShareProvider
    ): ImageShareProvider<Bitmap>

}