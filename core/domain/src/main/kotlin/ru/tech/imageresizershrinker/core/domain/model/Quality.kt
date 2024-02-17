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

package ru.tech.imageresizershrinker.core.domain.model

import androidx.annotation.IntRange

sealed class Quality(
    open val qualityValue: Int
) {
    data class Jxl(
        @IntRange(from = 0, to = 100)
        override val qualityValue: Int = 100,
        @IntRange(from = 1, to = 9)
        val effort: Int = 7,
        @IntRange(from = 0, to = 5)
        val speed: Int = 2
    ) : Quality(qualityValue)

    data class PngLossy(
        @IntRange(from = 1, to = 1024)
        val maxColors: Int = 512,
        @IntRange(from = 0, to = 9)
        val compressionLevel: Int = 7,
    ) : Quality(compressionLevel)

    data class Base(
        override val qualityValue: Int = 100
    ) : Quality(qualityValue)
}