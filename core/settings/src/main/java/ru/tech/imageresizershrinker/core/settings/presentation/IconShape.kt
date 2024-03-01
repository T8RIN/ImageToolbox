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

package ru.tech.imageresizershrinker.core.settings.presentation

import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class IconShape(
    val shape: Shape,
    val padding: Dp = 4.dp,
    val iconSize: Dp = 24.dp
) {
    fun takeOrElseFrom(
        iconShapesList: List<IconShape>
    ): IconShape = if (this == Random) iconShapesList
        .filter { it != Random }
        .random()
    else this

    companion object {
        val Random by lazy {
            IconShape(
                shape = RectangleShape,
                padding = 0.dp,
                iconSize = 0.dp
            )
        }
    }
}