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

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.t8rin.imagetoolbox.core.data.json.MoshiParser
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.json.JsonParser
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface JsonModule {

    @Binds
    @Singleton
    fun parser(
        impl: MoshiParser,
    ): JsonParser

    companion object {

        @Provides
        @Singleton
        fun moshi(): Moshi = Moshi.Builder()
            .add(
                PolymorphicJsonAdapterFactory.of(Quality::class.java, "Quality")
                    .withSubtype(Quality.Jxl::class.java, "Jxl")
                    .withSubtype(Quality.Avif::class.java, "Avif")
                    .withSubtype(Quality.PngLossy::class.java, "PngLossy")
                    .withSubtype(Quality.Tiff::class.java, "Tiff")
                    .withSubtype(Quality.Base::class.java, "Base")
                    .withDefaultValue(Quality.Base())
            )
            .addLast(KotlinJsonAdapterFactory())
            .build()

    }

}