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

package ru.tech.imageresizershrinker.core.resources.shapes

import android.graphics.Matrix
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

val SimpleHeartShape: Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val baseWidth = 24f
        val baseHeight = 24f

        val path = Path().apply {
            moveTo(12.0f, 21.35f)
            relativeLineTo(-1.45f, -1.32f)
            cubicTo(5.4f, 15.36f, 2.0f, 12.28f, 2.0f, 8.5f)
            cubicTo(2.0f, 5.42f, 4.42f, 3.0f, 7.5f, 3.0f)
            relativeCubicTo(1.74f, 0.0f, 3.41f, 0.81f, 4.5f, 2.09f)
            cubicTo(13.09f, 3.81f, 14.76f, 3.0f, 16.5f, 3.0f)
            cubicTo(19.58f, 3.0f, 22.0f, 5.42f, 22.0f, 8.5f)
            relativeCubicTo(0.0f, 3.78f, -3.4f, 6.86f, -8.55f, 11.54f)
            lineTo(12.0f, 21.35f)
            close()
        }

        return Outline.Generic(
            path
                .asAndroidPath()
                .apply {
                    transform(
                        Matrix().apply {
                            setScale(size.width / baseWidth, size.height / baseHeight)
                        }
                    )
                }
                .asComposePath()
        )
    }
}