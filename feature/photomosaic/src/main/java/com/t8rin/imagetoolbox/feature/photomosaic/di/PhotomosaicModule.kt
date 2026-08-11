/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.t8rin.imagetoolbox.feature.photomosaic.di

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.feature.photomosaic.data.AndroidPhotomosaicMaker
import com.t8rin.imagetoolbox.feature.photomosaic.domain.PhotomosaicMaker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface PhotomosaicModule {

    @Binds
    @Singleton
    fun bindPhotomosaicMaker(impl: AndroidPhotomosaicMaker): PhotomosaicMaker<Bitmap>
}
