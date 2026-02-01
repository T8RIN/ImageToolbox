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

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.t8rin.imagetoolbox.core.data.json.MoshiParser
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.json.JsonParser
import com.t8rin.imagetoolbox.core.settings.domain.model.FilenameBehavior
import com.t8rin.imagetoolbox.core.settings.domain.model.ShapeType
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
                PolymorphicJsonAdapterFactory.of(Quality::class.java, "quality_type")
                    .withSubtype(Quality.Jxl::class.java, "jxl")
                    .withSubtype(Quality.Avif::class.java, "avif")
                    .withSubtype(Quality.PngLossy::class.java, "png")
                    .withSubtype(Quality.Tiff::class.java, "tiff")
                    .withSubtype(Quality.Base::class.java, "base")
                    .withDefaultValue(Quality.Base())
            )
            .add(
                PolymorphicJsonAdapterFactory.of(ShapeType::class.java, "shape_type")
                    .withSubtype(ShapeType.Rounded::class.java, "rounded")
                    .withSubtype(ShapeType.Cut::class.java, "cut")
                    .withSubtype(ShapeType.Squircle::class.java, "squircle")
                    .withSubtype(ShapeType.Smooth::class.java, "smooth")
                    .withDefaultValue(ShapeType.Rounded())
            )
            .add(
                PolymorphicJsonAdapterFactory.of(FilenameBehavior::class.java, "filename_type")
                    .withSubtype(FilenameBehavior.None::class.java, "none")
                    .withSubtype(FilenameBehavior.Overwrite::class.java, "overwrite")
                    .withSubtype(FilenameBehavior.Checksum::class.java, "checksum")
                    .withSubtype(FilenameBehavior.Random::class.java, "random")
                    .withDefaultValue(FilenameBehavior.None())
            )
            .addLast(KotlinJsonAdapterFactory())
            .build()

    }

}