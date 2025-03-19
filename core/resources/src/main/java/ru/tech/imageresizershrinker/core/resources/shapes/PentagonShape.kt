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

val PentagonShape: Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val baseWidth = 1224.1858f
        val baseHeight = 1137.3882f

        val path = Path().apply {
            moveTo(521.133f, 28.588f)
            cubicTo(575.7829f, -9.5293f, 648.4028f, -9.5293f, 703.0528f, 28.588f)
            lineTo(1156.1332f, 344.6029f)
            cubicTo(1214.148f, 385.0671f, 1238.4572f, 458.9902f, 1215.7808f, 525.9892f)
            lineTo(1045.41f, 1029.3625f)
            cubicTo(1023.5555f, 1093.9333f, 962.9716f, 1137.3882f, 894.8026f, 1137.3882f)
            lineTo(329.3832f, 1137.3882f)
            cubicTo(261.2142f, 1137.3882f, 200.6302f, 1093.9333f, 178.7757f, 1029.3625f)
            lineTo(8.405f, 525.9893f)
            cubicTo(-14.2714f, 458.9903f, 10.0377f, 385.0671f, 68.0526f, 344.6029f)
            lineTo(521.133f, 28.588f)
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