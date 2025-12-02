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

package com.t8rin.imagetoolbox.feature.erase_background.di

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.feature.erase_background.data.AndroidAutoBackgroundRemover
import com.t8rin.imagetoolbox.feature.erase_background.data.AndroidAutoBackgroundRemoverBackendFactory
import com.t8rin.imagetoolbox.feature.erase_background.domain.AutoBackgroundRemover
import com.t8rin.imagetoolbox.feature.erase_background.domain.AutoBackgroundRemoverBackendFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface EraseBackgroundModule {

    @Binds
    @Singleton
    fun provideBackgroundRemover(
        remover: AndroidAutoBackgroundRemover
    ): AutoBackgroundRemover<Bitmap>

    @Binds
    @Singleton
    fun provideBackendFactory(
        backend: AndroidAutoBackgroundRemoverBackendFactory
    ): AutoBackgroundRemoverBackendFactory<Bitmap>

}