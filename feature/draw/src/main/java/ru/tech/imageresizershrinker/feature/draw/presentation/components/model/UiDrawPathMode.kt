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

package ru.tech.imageresizershrinker.feature.draw.presentation.components.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode

@Parcelize
sealed class UiDrawPathMode : Parcelable {
    @Parcelize
    data object Free : UiDrawPathMode()

    @Parcelize
    data object Line : UiDrawPathMode()

    @Parcelize
    data object PointingArrow : UiDrawPathMode()

    @Parcelize
    data object DoublePointingArrow : UiDrawPathMode()

    @Parcelize
    data object LinePointingArrow : UiDrawPathMode()

    @Parcelize
    data object DoubleLinePointingArrow : UiDrawPathMode()

    @Parcelize
    data object Lasso : UiDrawPathMode()

    @Parcelize
    data class OutlinedRect(
        val rotationDegrees: Int = 0
    ) : UiDrawPathMode()

    @Parcelize
    data object OutlinedOval : UiDrawPathMode()

    @Parcelize
    data class Rect(
        val rotationDegrees: Int = 0
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
}

fun DrawPathMode.toUi(): UiDrawPathMode = when (this) {
    DrawPathMode.DoubleLinePointingArrow -> UiDrawPathMode.DoubleLinePointingArrow
    DrawPathMode.DoublePointingArrow -> UiDrawPathMode.DoublePointingArrow
    DrawPathMode.Free -> UiDrawPathMode.Free
    DrawPathMode.Lasso -> UiDrawPathMode.Lasso
    DrawPathMode.Line -> UiDrawPathMode.Line
    DrawPathMode.LinePointingArrow -> UiDrawPathMode.LinePointingArrow
    DrawPathMode.OutlinedOval -> UiDrawPathMode.OutlinedOval
    is DrawPathMode.OutlinedPolygon -> UiDrawPathMode.OutlinedPolygon(
        vertices = vertices,
        rotationDegrees = rotationDegrees,
        isRegular = isRegular
    )

    is DrawPathMode.OutlinedRect -> UiDrawPathMode.OutlinedRect(rotationDegrees)
    is DrawPathMode.OutlinedStar -> UiDrawPathMode.OutlinedStar(
        vertices = vertices,
        rotationDegrees = rotationDegrees,
        isRegular = isRegular,
        innerRadiusRatio = innerRadiusRatio
    )

    DrawPathMode.OutlinedTriangle -> UiDrawPathMode.OutlinedTriangle
    DrawPathMode.Oval -> UiDrawPathMode.Oval
    DrawPathMode.PointingArrow -> UiDrawPathMode.PointingArrow
    is DrawPathMode.Polygon -> UiDrawPathMode.Polygon(
        vertices = vertices,
        rotationDegrees = rotationDegrees,
        isRegular = isRegular
    )

    is DrawPathMode.Rect -> UiDrawPathMode.Rect(rotationDegrees)
    is DrawPathMode.Star -> UiDrawPathMode.Star(
        vertices = vertices,
        rotationDegrees = rotationDegrees,
        isRegular = isRegular,
        innerRadiusRatio = innerRadiusRatio
    )

    DrawPathMode.Triangle -> UiDrawPathMode.Triangle
}

fun UiDrawPathMode.toDomain(): DrawPathMode = when (this) {
    UiDrawPathMode.DoubleLinePointingArrow -> DrawPathMode.DoubleLinePointingArrow
    UiDrawPathMode.DoublePointingArrow -> DrawPathMode.DoublePointingArrow
    UiDrawPathMode.Free -> DrawPathMode.Free
    UiDrawPathMode.Lasso -> DrawPathMode.Lasso
    UiDrawPathMode.Line -> DrawPathMode.Line
    UiDrawPathMode.LinePointingArrow -> DrawPathMode.LinePointingArrow
    UiDrawPathMode.OutlinedOval -> DrawPathMode.OutlinedOval
    is UiDrawPathMode.OutlinedPolygon -> DrawPathMode.OutlinedPolygon(
        vertices = vertices,
        rotationDegrees = rotationDegrees,
        isRegular = isRegular
    )

    is UiDrawPathMode.OutlinedRect -> DrawPathMode.OutlinedRect(rotationDegrees)
    is UiDrawPathMode.OutlinedStar -> DrawPathMode.OutlinedStar(
        vertices = vertices,
        rotationDegrees = rotationDegrees,
        isRegular = isRegular,
        innerRadiusRatio = innerRadiusRatio
    )

    UiDrawPathMode.OutlinedTriangle -> DrawPathMode.OutlinedTriangle
    UiDrawPathMode.Oval -> DrawPathMode.Oval
    UiDrawPathMode.PointingArrow -> DrawPathMode.PointingArrow
    is UiDrawPathMode.Polygon -> DrawPathMode.Polygon(
        vertices = vertices,
        rotationDegrees = rotationDegrees,
        isRegular = isRegular
    )

    is UiDrawPathMode.Rect -> DrawPathMode.Rect(rotationDegrees)
    is UiDrawPathMode.Star -> DrawPathMode.Star(
        vertices = vertices,
        rotationDegrees = rotationDegrees,
        isRegular = isRegular,
        innerRadiusRatio = innerRadiusRatio
    )

    UiDrawPathMode.Triangle -> DrawPathMode.Triangle
}