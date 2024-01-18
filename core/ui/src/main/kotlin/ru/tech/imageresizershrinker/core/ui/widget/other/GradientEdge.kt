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

package ru.tech.imageresizershrinker.core.ui.widget.other

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun GradientEdge(
    modifier: Modifier,
    orientation: Orientation = Orientation.Vertical,
    startColor: Color,
    endColor: Color
) {
    when (orientation) {
        Orientation.Vertical -> {
            Box(
                modifier = modifier
                    .background(
                        brush = Brush.verticalGradient(
                            0f to startColor,
                            0.7f to startColor,
                            1f to endColor
                        )
                    )
            )
        }

        Orientation.Horizontal -> {
            Box(
                modifier = modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            0f to startColor, 0.7f to startColor, 1f to endColor
                        )
                    )
            )
        }
    }
}