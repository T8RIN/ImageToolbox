/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

@file:Suppress("FunctionName")

package ru.tech.imageresizershrinker.core.resources.shapes


import android.graphics.Matrix
import android.graphics.RectF
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

internal fun PathShape(pathData: String): Shape = PathShapeImpl(pathData)

private class PathShapeImpl(private val pathData: String) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(path = drawPath(size))
    }

    private fun drawPath(size: Size): Path {
        return Path().apply {
            reset()
            addPath(pathData.toPath(size))
            close()
        }
    }
}

private fun String.toPath(
    size: Size
): Path {
    if (isNotEmpty()) {
        val scaleMatrix = Matrix()
        val rectF = RectF()
        val path = PathParser().parsePathString(this).toPath()
        val rectPath = path.getBounds().toAndroidRectF()
        val scaleXFactor = size.width / rectPath.width()
        val scaleYFactor = size.height / rectPath.height()
        val androidPath = path.asAndroidPath()
        scaleMatrix.setScale(scaleXFactor, scaleYFactor, rectF.centerX(), rectF.centerY())
        @Suppress("DEPRECATION")
        androidPath.computeBounds(rectF, true)
        androidPath.transform(scaleMatrix)
        return androidPath.asComposePath()
    }
    return Path()
}