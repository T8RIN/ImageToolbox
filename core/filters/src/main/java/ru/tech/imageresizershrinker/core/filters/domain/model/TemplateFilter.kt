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

data class TemplateFilter<Image>(
    val name: String,
    val filters: List<Filter<Image, *>>
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TemplateFilter<*>

        if (other.name != name) return false
        if (other.filters.size != filters.size) return false
        val filters1 = other.filters.sortedBy { it::class.simpleName }
        val filters2 = filters.sortedBy { it::class.simpleName }
        filters1.forEachIndexed { index, filter1 ->
            val filter2 = filters2[index]
            if (filter1::class.simpleName != filter2::class.simpleName) return false
            if (filter1.value != filter2.value) return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + filters.hashCode()
        return result
    }


}