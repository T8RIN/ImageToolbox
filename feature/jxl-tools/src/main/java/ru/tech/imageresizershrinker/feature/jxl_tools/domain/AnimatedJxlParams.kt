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

package ru.tech.imageresizershrinker.feature.jxl_tools.domain

import ru.tech.imageresizershrinker.core.domain.image.model.Quality
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize


data class AnimatedJxlParams(
    val size: IntegerSize?,
    val repeatCount: Int,
    val delay: Int,
    val quality: Quality,
    val isLossy: Boolean
) {
    companion object {
        val Default by lazy {
            AnimatedJxlParams(
                size = null,
                repeatCount = 1,
                delay = 1000,
                quality = Quality.Jxl(),
                isLossy = true
            )
        }
    }
}