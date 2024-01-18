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

import ru.tech.imageresizershrinker.core.domain.Domain

sealed class ResizeType : Domain {
    data object Explicit : ResizeType()
    data object Flexible : ResizeType()

    data class CenterCrop(
        val canvasColor: Int? = 0,
        val blurRadius: Int = 35
    ) : ResizeType()

    sealed class Limits(val autoRotateLimitBox: Boolean) : ResizeType() {

        fun copy(autoRotateLimitBox: Boolean) = when (this) {
            is Recode -> Recode(autoRotateLimitBox)
            is Skip -> Skip(autoRotateLimitBox)
            is Zoom -> Zoom(autoRotateLimitBox)
        }

        class Skip(
            autoRotateLimitBox: Boolean = false
        ) : Limits(autoRotateLimitBox)

        class Recode(
            autoRotateLimitBox: Boolean = false
        ) : Limits(autoRotateLimitBox)

        class Zoom(
            autoRotateLimitBox: Boolean = false
        ) : Limits(autoRotateLimitBox)
    }

    companion object {
        val entries by lazy {
            listOf(
                Explicit,
                Flexible,
                CenterCrop()
            )
        }
        val limitsEntries by lazy {
            listOf(
                Limits.Skip(),
                Limits.Recode(),
                Limits.Zoom()
            )
        }
    }
}