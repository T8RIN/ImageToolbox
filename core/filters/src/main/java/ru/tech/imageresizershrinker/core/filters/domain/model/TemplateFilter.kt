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

package ru.tech.imageresizershrinker.core.filters.domain.model

data class TemplateFilter(
    val name: String,
    val filters: List<Filter<*>>
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TemplateFilter

        if (other.name != name) return false
        if (other.filters.size != filters.size) return false
        val filters1 = other.filters.sortedBy { it::class.simpleName }
        val filters2 = filters.sortedBy { it::class.simpleName }

        filters1.forEachIndexed { index, filter1 ->
            val filter2 = filters2[index]
            val filter1Name = filter1::class.simpleName
            val filter2Name = filter2::class.simpleName

            val filter1Value = filter1.value
            val filter2Value = filter2.value

            if (filter1Name != filter2Name) return false
            if (filter1Value is FloatArray) {
                if (filter2Value !is FloatArray) return false
                if (filter1Value.joinToString() != filter2Value.joinToString()) return false
            }

            if (filter1Value != filter2Value) return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + filters.hashCode()
        return result
    }


}