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

val CloverShape: Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val baseWidth = 200f
        val baseHeight = 200f

        val path = Path().apply {
            moveTo(12f, 100f)
            cubicTo(12f, 76f, 0f, 77.6142f, 0f, 50f)
            cubicTo(0f, 22.3858f, 22.3858f, 0f, 50f, 0f)
            cubicTo(77.6142f, 0f, 76f, 12f, 100f, 12f)
            cubicTo(124f, 12f, 122.3858f, 0f, 150f, 0f)
            cubicTo(177.6142f, 0f, 200f, 22.3858f, 200f, 50f)
            cubicTo(200f, 77.6142f, 188f, 76f, 188f, 100f)
            cubicTo(188f, 124f, 200f, 122.3858f, 200f, 150f)
            cubicTo(200f, 177.6142f, 177.6142f, 200f, 150f, 200f)
            cubicTo(122.3858f, 200f, 124f, 188f, 100f, 188f)
            cubicTo(76f, 188f, 77.6142f, 200f, 50f, 200f)
            cubicTo(22.3858f, 200f, 0f, 177.6142f, 0f, 150f)
            cubicTo(0f, 122.3858f, 12f, 124f, 12f, 100f)
            close()
        }

        return Outline.Generic(
            path
                .asAndroidPath()
                .apply {
                    transform(Matrix().apply {
                        setScale(size.width / baseWidth, size.height / baseHeight)
                    })
                }
                .asComposePath()
        )
    }
}