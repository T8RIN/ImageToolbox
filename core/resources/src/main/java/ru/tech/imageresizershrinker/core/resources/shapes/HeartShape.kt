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

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

val HeartShape: Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val height = size.height
            val width = size.width
            moveTo(0.5f * width, 0.25f * height)
            cubicTo(
                0.5f * width,
                0.225f * height,
                0.45833334f * width,
                0.125f * height,
                0.29166666f * width,
                0.125f * height
            )
            cubicTo(
                0.041666668f * width,
                0.125f * height,
                0.041666668f * width,
                0.4f * height,
                0.041666668f * width,
                0.4f * height
            )
            cubicTo(
                0.041666668f * width,
                0.5833333f * height,
                0.20833333f * width,
                0.76666665f * height,
                0.5f * width,
                0.9166667f * height
            )
            cubicTo(
                0.7916667f * width,
                0.76666665f * height,
                0.9583333f * width,
                0.5833333f * height,
                0.9583333f * width,
                0.4f * height
            )
            cubicTo(
                0.9583333f * width,
                0.4f * height,
                0.9583333f * width,
                0.125f * height,
                0.7083333f * width,
                0.125f * height
            )
            cubicTo(
                0.5833333f * width,
                0.125f * height,
                0.5f * width,
                0.225f * height,
                0.5f * width,
                0.25f * height
            )
            close()
        }


        return Outline.Generic(path)
    }
}