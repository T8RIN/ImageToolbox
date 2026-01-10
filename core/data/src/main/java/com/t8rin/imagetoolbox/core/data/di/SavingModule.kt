/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

import com.t8rin.imagetoolbox.core.data.saving.AndroidFileController
import com.t8rin.imagetoolbox.core.data.saving.AndroidFilenameCreator
import com.t8rin.imagetoolbox.core.data.saving.AndroidKeepAliveService
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.FilenameCreator
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SavingModule {

    @Singleton
    @Binds
    fun provideFileController(
        impl: AndroidFileController
    ): FileController

    @Singleton
    @Binds
    fun filenameCreator(
        impl: AndroidFilenameCreator
    ): FilenameCreator

    @Singleton
    @Binds
    fun service(
        impl: AndroidKeepAliveService
    ): KeepAliveService

}