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

sealed class NightMode(val ordinal: Int) {
    data object Light : NightMode(0)
    data object Dark : NightMode(1)
    data object System : NightMode(2)

    companion object {
        fun fromOrdinal(int: Int?): NightMode? = when (int) {
            0 -> Light
            1 -> Dark
            2 -> System
            else -> null
        }
    }
}