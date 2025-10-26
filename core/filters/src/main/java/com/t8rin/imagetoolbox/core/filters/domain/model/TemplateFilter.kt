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

package com.t8rin.imagetoolbox.core.filters.domain.model

data class TemplateFilter(
    val name: String,
    val filters: List<Filter<*>>
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TemplateFilter) return false

        if (name != other.name) return false
        if (filters.size != other.filters.size) return false

        val filters1 = filters.sortedBy { it::class.simpleName }
        val filters2 = other.filters.sortedBy { it::class.simpleName }

        filters1.forEachIndexed { index, filter1 ->
            val filter2 = filters2[index]
            val filter1Name = filter1::class.simpleName
            val filter2Name = filter2::class.simpleName

            if (filter1Name != filter2Name) return false

            val v1 = filter1.value
            val v2 = filter2.value

            when {
                v1 is FloatArray && v2 is FloatArray -> {
                    if (!v1.contentEquals(v2)) return false
                }

                v1 is IntArray && v2 is IntArray -> {
                    if (!v1.contentEquals(v2)) return false
                }

                v1 is DoubleArray && v2 is DoubleArray -> {
                    if (!v1.contentEquals(v2)) return false
                }

                else -> if (v1 != v2) return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        filters.forEach { filter ->
            result = 31 * result + filter::class.simpleName.hashCode()
            val v = filter.value
            result = 31 * result + when (v) {
                is FloatArray -> v.contentHashCode()
                is IntArray -> v.contentHashCode()
                is DoubleArray -> v.contentHashCode()
                else -> v.hashCode()
            }
        }
        return result
    }
}