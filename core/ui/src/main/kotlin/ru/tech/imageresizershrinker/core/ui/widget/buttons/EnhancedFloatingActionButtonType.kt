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

package ru.tech.imageresizershrinker.core.ui.widget.buttons

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed class EnhancedFloatingActionButtonType(val size: Dp, val shape: Shape) {
    data object Small : EnhancedFloatingActionButtonType(
        size = 40.dp,
        shape = RoundedCornerShape(12.dp)
    )

    data object Primary : EnhancedFloatingActionButtonType(
        size = 56.dp,
        shape = RoundedCornerShape(16.dp)
    )

    data object Large : EnhancedFloatingActionButtonType(
        size = 96.dp,
        shape = RoundedCornerShape(28.dp)
    )

    class Custom(
        size: Dp,
        shape: Shape
    ) : EnhancedFloatingActionButtonType(
        size = size,
        shape = shape
    )
}