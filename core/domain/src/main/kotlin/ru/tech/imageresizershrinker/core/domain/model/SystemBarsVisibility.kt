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

sealed class SystemBarsVisibility(
    val ordinal: Int
) {

    data object Auto : SystemBarsVisibility(0)

    data object ShowAll : SystemBarsVisibility(1)

    data object HideAll : SystemBarsVisibility(2)

    data object HideNavigationBar : SystemBarsVisibility(3)

    data object HideStatusBar : SystemBarsVisibility(4)

    companion object {

        val entries: List<SystemBarsVisibility> by lazy {
            listOf(
                Auto,
                ShowAll,
                HideAll,
                HideNavigationBar,
                HideStatusBar
            )
        }

        fun fromOrdinal(ordinal: Int?): SystemBarsVisibility? = ordinal?.let {
            entries[ordinal]
        }
    }

}