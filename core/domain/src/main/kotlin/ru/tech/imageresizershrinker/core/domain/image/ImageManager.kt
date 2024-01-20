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

package ru.tech.imageresizershrinker.core.domain.image

import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.domain.image.filters.provider.FilterProvider
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.Preset

interface ImageManager<I> {

    fun getFilterProvider(): FilterProvider<I>

    suspend fun transform(
        image: I,
        transformations: List<Transformation<I>>,
        originalSize: Boolean = true
    ): I?

    suspend fun filter(
        image: I,
        filters: List<Filter<I, *>>,
        originalSize: Boolean = true
    ): I?

    suspend fun transform(
        image: I,
        transformations: List<Transformation<I>>,
        size: IntegerSize
    ): I?

    suspend fun filter(
        image: I,
        filters: List<Filter<I, *>>,
        size: IntegerSize
    ): I?

    fun rotate(image: I, degrees: Float): I

    fun flip(image: I, isFlipped: Boolean): I

    fun applyPresetBy(
        image: I?,
        preset: Preset,
        currentInfo: ImageInfo
    ): ImageInfo

}