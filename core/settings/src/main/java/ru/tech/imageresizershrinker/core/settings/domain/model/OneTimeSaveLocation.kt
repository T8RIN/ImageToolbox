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

package ru.tech.imageresizershrinker.core.settings.domain.model

data class OneTimeSaveLocation(
    val uri: String,
    val date: Long?,
    val count: Int
) {

    override fun toString(): String {
        return listOf(uri, date, count).joinToString(delimiter)
    }

    companion object {
        fun fromString(string: String): OneTimeSaveLocation? {
            val data = string.split(delimiter)
            val uri = data.getOrNull(0) ?: return null
            val date = data.getOrNull(1)?.toLongOrNull()
            val count = data.getOrNull(2)?.toIntOrNull() ?: 0

            return OneTimeSaveLocation(uri, date, count)
        }
    }

}

private const val delimiter = "\n"