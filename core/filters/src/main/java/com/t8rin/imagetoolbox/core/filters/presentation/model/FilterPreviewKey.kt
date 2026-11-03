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

package com.t8rin.imagetoolbox.core.filters.presentation.model

import com.t8rin.imagetoolbox.core.filters.domain.model.Filter

data class FilterPreviewKey(
    val filterType: String?,
    val value: Any?,
    val isVisible: Boolean
)

fun Filter<*>.previewKey(): FilterPreviewKey = FilterPreviewKey(
    filterType = this::class.simpleName,
    value = if (this is Filter.RawGmic) {
        value to auxiliaryImages.toList()
    } else {
        value
    },
    isVisible = isVisible
)

fun UiFilter<*>.hasSameValue(value: Any?): Boolean {
    return this.value == value
}

fun UiFilter<*>.hasSameState(other: UiFilter<*>): Boolean {
    return previewKey() == other.previewKey()
}
