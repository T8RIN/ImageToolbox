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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.model

import android.os.Parcelable
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class UiDrawPathMode : Parcelable {
    @Parcelize
    data object Free : UiDrawPathMode()

    @Parcelize
    data object Line : UiDrawPathMode()

    @Parcelize
    data class PointingArrow(
        val sizeScale: Float = 3f,
        val angle: Float = 150f
    ) : UiDrawPathMode()

    @Parcelize
    data class DoublePointingArrow(
        val sizeScale: Float = 3f,
        val angle: Float = 150f
    ) : UiDrawPathMode()

    @Parcelize
    data class LinePointingArrow(
        val sizeScale: Float = 3f,
        val angle: Float = 150f
    ) : UiDrawPathMode()

    @Parcelize
    data class DoubleLinePointingArrow(
        val sizeScale: Float = 3f,
        val angle: Float = 150f
    ) : UiDrawPathMode()

    @Parcelize
    data object Lasso : UiDrawPathMode()

    @Parcelize
    data class OutlinedRect(
        val rotationDegrees: Int = 0,
        val cornerRadius: Float = 0f
    ) : UiDrawPathMode()

    @Parcelize
    data object OutlinedOval : UiDrawPathMode()

    @Parcelize
    data class Rect(
        val rotationDegrees: Int = 0,
        val cornerRadius: Float = 0f
    ) : UiDrawPathMode()

    @Parcelize
    data object Oval : UiDrawPathMode()

    @Parcelize
    data object Triangle : UiDrawPathMode()

    @Parcelize
    data object OutlinedTriangle : UiDrawPathMode()

    @Parcelize
    data class Polygon(
        val vertices: Int = 5,
        val rotationDegrees: Int = 0,
        val isRegular: Boolean = false
    ) : UiDrawPathMode()

    @Parcelize
    data class OutlinedPolygon(
        val vertices: Int = 5,
        val rotationDegrees: Int = 0,
        val isRegular: Boolean = false
    ) : UiDrawPathMode()

    @Parcelize
    data class Star(
        val vertices: Int = 5,
        val rotationDegrees: Int = 0,
        val innerRadiusRatio: Float = 0.5f,
        val isRegular: Boolean = false
    ) : UiDrawPathMode()

    @Parcelize
    data class OutlinedStar(
        val vertices: Int = 5,
        val rotationDegrees: Int = 0,
        val innerRadiusRatio: Float = 0.5f,
        val isRegular: Boolean = false
    ) : UiDrawPathMode()

    @Parcelize
    data class FloodFill(
        val tolerance: Float = 0.25f
    ) : UiDrawPathMode()

    @Parcelize
    data class Spray(
        val density: Int = 50,
        val pixelSize: Float = 1f,
        val isSquareShaped: Boolean = false
    ) : UiDrawPathMode()
}

fun DrawPathMode.toUi(): UiDrawPathMode = when (this) {
    is DrawPathMode.DoubleLinePointingArrow -> UiDrawPathMode.DoubleLinePointingArrow(
        sizeScale = sizeScale,
        angle = angle
    )

    is DrawPathMode.DoublePointingArrow -> UiDrawPathMode.DoublePointingArrow(
        sizeScale = sizeScale,
        angle = angle
    )

    DrawPathMode.Free -> UiDrawPathMode.Free

    DrawPathMode.Lasso -> UiDrawPathMode.Lasso

    DrawPathMode.Line -> UiDrawPathMode.Line

    is DrawPathMode.LinePointingArrow -> UiDrawPathMode.LinePointingArrow(
        sizeScale = sizeScale,
        angle = angle
    )

    DrawPathMode.OutlinedOval -> UiDrawPathMode.OutlinedOval

    is DrawPathMode.OutlinedPolygon -> UiDrawPathMode.OutlinedPolygon(
        vertices = vertices,
        rotationDegrees = rotationDegrees,
        isRegular = isRegular
    )

    is DrawPathMode.OutlinedRect -> UiDrawPathMode.OutlinedRect(
        rotationDegrees = rotationDegrees,
        cornerRadius = cornerRadius
    )

    is DrawPathMode.OutlinedStar -> UiDrawPathMode.OutlinedStar(
        vertices = vertices,
        rotationDegrees = rotationDegrees,
        isRegular = isRegular,
        innerRadiusRatio = innerRadiusRatio
    )

    DrawPathMode.OutlinedTriangle -> UiDrawPathMode.OutlinedTriangle

    DrawPathMode.Oval -> UiDrawPathMode.Oval

    is DrawPathMode.PointingArrow -> UiDrawPathMode.PointingArrow(
        sizeScale = sizeScale,
        angle = angle
    )

    is DrawPathMode.Polygon -> UiDrawPathMode.Polygon(
        vertices = vertices,
        rotationDegrees = rotationDegrees,
        isRegular = isRegular
    )

    is DrawPathMode.Rect -> UiDrawPathMode.Rect(
        rotationDegrees = rotationDegrees,
        cornerRadius = cornerRadius
    )

    is DrawPathMode.Star -> UiDrawPathMode.Star(
        vertices = vertices,
        rotationDegrees = rotationDegrees,
        isRegular = isRegular,
        innerRadiusRatio = innerRadiusRatio
    )

    DrawPathMode.Triangle -> UiDrawPathMode.Triangle

    is DrawPathMode.FloodFill -> UiDrawPathMode.FloodFill(tolerance)

    is DrawPathMode.Spray -> UiDrawPathMode.Spray(
        density = density,
        pixelSize = pixelSize,
        isSquareShaped = isSquareShaped
    )
}

fun UiDrawPathMode.toDomain(): DrawPathMode = when (this) {
    is UiDrawPathMode.DoubleLinePointingArrow -> DrawPathMode.DoubleLinePointingArrow(
        sizeScale = sizeScale,
        angle = angle
    )

    is UiDrawPathMode.DoublePointingArrow -> DrawPathMode.DoublePointingArrow(
        sizeScale = sizeScale,
        angle = angle
    )

    UiDrawPathMode.Free -> DrawPathMode.Free

    UiDrawPathMode.Lasso -> DrawPathMode.Lasso

    UiDrawPathMode.Line -> DrawPathMode.Line

    is UiDrawPathMode.LinePointingArrow -> DrawPathMode.LinePointingArrow(
        sizeScale = sizeScale,
        angle = angle
    )

    UiDrawPathMode.OutlinedOval -> DrawPathMode.OutlinedOval

    is UiDrawPathMode.OutlinedPolygon -> DrawPathMode.OutlinedPolygon(
        vertices = vertices,
        rotationDegrees = rotationDegrees,
        isRegular = isRegular
    )

    is UiDrawPathMode.OutlinedRect -> DrawPathMode.OutlinedRect(
        rotationDegrees = rotationDegrees,
        cornerRadius = cornerRadius
    )

    is UiDrawPathMode.OutlinedStar -> DrawPathMode.OutlinedStar(
        vertices = vertices,
        rotationDegrees = rotationDegrees,
        isRegular = isRegular,
        innerRadiusRatio = innerRadiusRatio
    )

    UiDrawPathMode.OutlinedTriangle -> DrawPathMode.OutlinedTriangle

    UiDrawPathMode.Oval -> DrawPathMode.Oval

    is UiDrawPathMode.PointingArrow -> DrawPathMode.PointingArrow(
        sizeScale = sizeScale,
        angle = angle
    )

    is UiDrawPathMode.Polygon -> DrawPathMode.Polygon(
        vertices = vertices,
        rotationDegrees = rotationDegrees,
        isRegular = isRegular
    )

    is UiDrawPathMode.Rect -> DrawPathMode.Rect(
        rotationDegrees = rotationDegrees,
        cornerRadius = cornerRadius
    )

    is UiDrawPathMode.Star -> DrawPathMode.Star(
        vertices = vertices,
        rotationDegrees = rotationDegrees,
        isRegular = isRegular,
        innerRadiusRatio = innerRadiusRatio
    )

    UiDrawPathMode.Triangle -> DrawPathMode.Triangle

    is UiDrawPathMode.FloodFill -> DrawPathMode.FloodFill(tolerance)

    is UiDrawPathMode.Spray -> DrawPathMode.Spray(density)
}