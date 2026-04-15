/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.markup_layers.domain

import com.t8rin.imagetoolbox.core.domain.model.toColorModel
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode

internal val MarkupLayerShapeModes: List<DrawPathMode> by lazy {
    listOf(
        DrawPathMode.Line,
        DrawPathMode.PointingArrow(),
        DrawPathMode.DoublePointingArrow(),
        DrawPathMode.LinePointingArrow(),
        DrawPathMode.DoubleLinePointingArrow(),
        DrawPathMode.Rect(),
        DrawPathMode.OutlinedRect(),
        DrawPathMode.Oval,
        DrawPathMode.OutlinedOval(),
        DrawPathMode.Triangle,
        DrawPathMode.OutlinedTriangle(),
        DrawPathMode.Polygon(),
        DrawPathMode.OutlinedPolygon(),
        DrawPathMode.Star(),
        DrawPathMode.OutlinedStar()
    )
}

internal fun DrawPathMode.isMarkupLayerShapeMode(): Boolean = when (this) {
    DrawPathMode.Line,
    is DrawPathMode.PointingArrow,
    is DrawPathMode.DoublePointingArrow,
    is DrawPathMode.LinePointingArrow,
    is DrawPathMode.DoubleLinePointingArrow,
    is DrawPathMode.Rect,
    is DrawPathMode.OutlinedRect,
    DrawPathMode.Oval,
    is DrawPathMode.OutlinedOval,
    DrawPathMode.Triangle,
    is DrawPathMode.OutlinedTriangle,
    is DrawPathMode.Polygon,
    is DrawPathMode.OutlinedPolygon,
    is DrawPathMode.Star,
    is DrawPathMode.OutlinedStar -> true

    else -> false
}

internal fun DrawPathMode.sanitizeForMarkupLayerShape(): DrawPathMode =
    takeIf(DrawPathMode::isMarkupLayerShapeMode) ?: DrawPathMode.OutlinedRect()

internal fun DrawPathMode.isOutlinedShapeMode(): Boolean = this is DrawPathMode.Outlined

internal fun DrawPathMode.isFilledShapeMode(): Boolean = when (this) {
    is DrawPathMode.Rect,
    DrawPathMode.Oval,
    DrawPathMode.Triangle,
    is DrawPathMode.Polygon,
    is DrawPathMode.Star,
    is DrawPathMode.PointingArrow,
    is DrawPathMode.DoublePointingArrow -> true

    else -> false
}

internal fun DrawPathMode.usesStrokeWidth(): Boolean = when (this) {
    DrawPathMode.Line,
    is DrawPathMode.LinePointingArrow,
    is DrawPathMode.DoubleLinePointingArrow,
    is DrawPathMode.Outlined -> true

    else -> false
}

internal fun DrawPathMode.outlinedFillColorInt(): Int? = when (this) {
    is DrawPathMode.OutlinedOval -> fillColor?.colorInt
    is DrawPathMode.OutlinedPolygon -> fillColor?.colorInt
    is DrawPathMode.OutlinedRect -> fillColor?.colorInt
    is DrawPathMode.OutlinedStar -> fillColor?.colorInt
    is DrawPathMode.OutlinedTriangle -> fillColor?.colorInt
    else -> null
}

internal fun DrawPathMode.withOutlinedFillColor(color: Int?): DrawPathMode = when (this) {
    is DrawPathMode.OutlinedOval -> copy(fillColor = color?.toColorModel())
    is DrawPathMode.OutlinedPolygon -> copy(fillColor = color?.toColorModel())
    is DrawPathMode.OutlinedRect -> copy(fillColor = color?.toColorModel())
    is DrawPathMode.OutlinedStar -> copy(fillColor = color?.toColorModel())
    is DrawPathMode.OutlinedTriangle -> copy(fillColor = color?.toColorModel())
    else -> this
}

internal fun DrawPathMode.arrowSizeScale(): Float = when (this) {
    is DrawPathMode.PointingArrow -> sizeScale
    is DrawPathMode.LinePointingArrow -> sizeScale
    is DrawPathMode.DoublePointingArrow -> sizeScale
    is DrawPathMode.DoubleLinePointingArrow -> sizeScale
    else -> 3f
}

internal fun DrawPathMode.arrowAngle(): Float = when (this) {
    is DrawPathMode.PointingArrow -> angle
    is DrawPathMode.LinePointingArrow -> angle
    is DrawPathMode.DoublePointingArrow -> angle
    is DrawPathMode.DoubleLinePointingArrow -> angle
    else -> 150f
}

internal fun DrawPathMode.vertices(): Int = when (this) {
    is DrawPathMode.Polygon -> vertices
    is DrawPathMode.OutlinedPolygon -> vertices
    is DrawPathMode.Star -> vertices
    is DrawPathMode.OutlinedStar -> vertices
    else -> 5
}

internal fun DrawPathMode.rotationDegrees(): Int = when (this) {
    is DrawPathMode.Polygon -> rotationDegrees
    is DrawPathMode.OutlinedPolygon -> rotationDegrees
    is DrawPathMode.Star -> rotationDegrees
    is DrawPathMode.OutlinedStar -> rotationDegrees
    is DrawPathMode.Rect -> rotationDegrees
    is DrawPathMode.OutlinedRect -> rotationDegrees
    else -> 0
}

internal fun DrawPathMode.cornerRadius(): Float = when (this) {
    is DrawPathMode.Rect -> cornerRadius
    is DrawPathMode.OutlinedRect -> cornerRadius
    else -> 0f
}

internal fun DrawPathMode.isRegular(): Boolean = when (this) {
    is DrawPathMode.Polygon -> isRegular
    is DrawPathMode.OutlinedPolygon -> isRegular
    is DrawPathMode.Star -> isRegular
    is DrawPathMode.OutlinedStar -> isRegular
    else -> false
}

internal fun DrawPathMode.innerRadiusRatio(): Float = when (this) {
    is DrawPathMode.Star -> innerRadiusRatio
    is DrawPathMode.OutlinedStar -> innerRadiusRatio
    else -> 0.5f
}

internal fun DrawPathMode.updateArrow(
    sizeScale: Float? = null,
    angle: Float? = null
): DrawPathMode = when (this) {
    is DrawPathMode.PointingArrow -> copy(
        sizeScale = sizeScale ?: this.sizeScale,
        angle = angle ?: this.angle
    )

    is DrawPathMode.LinePointingArrow -> copy(
        sizeScale = sizeScale ?: this.sizeScale,
        angle = angle ?: this.angle
    )

    is DrawPathMode.DoublePointingArrow -> copy(
        sizeScale = sizeScale ?: this.sizeScale,
        angle = angle ?: this.angle
    )

    is DrawPathMode.DoubleLinePointingArrow -> copy(
        sizeScale = sizeScale ?: this.sizeScale,
        angle = angle ?: this.angle
    )

    else -> this
}

internal fun DrawPathMode.updateRect(
    rotationDegrees: Int? = null,
    cornerRadius: Float? = null
): DrawPathMode = when (this) {
    is DrawPathMode.Rect -> copy(
        rotationDegrees = rotationDegrees ?: this.rotationDegrees,
        cornerRadius = cornerRadius ?: this.cornerRadius
    )

    is DrawPathMode.OutlinedRect -> copy(
        rotationDegrees = rotationDegrees ?: this.rotationDegrees,
        cornerRadius = cornerRadius ?: this.cornerRadius
    )

    else -> this
}

internal fun DrawPathMode.updatePolygon(
    vertices: Int? = null,
    rotationDegrees: Int? = null,
    isRegular: Boolean? = null
): DrawPathMode = when (this) {
    is DrawPathMode.Polygon -> copy(
        vertices = vertices ?: this.vertices,
        rotationDegrees = rotationDegrees ?: this.rotationDegrees,
        isRegular = isRegular ?: this.isRegular
    )

    is DrawPathMode.OutlinedPolygon -> copy(
        vertices = vertices ?: this.vertices,
        rotationDegrees = rotationDegrees ?: this.rotationDegrees,
        isRegular = isRegular ?: this.isRegular
    )

    else -> this
}

internal fun DrawPathMode.updateStar(
    vertices: Int? = null,
    innerRadiusRatio: Float? = null,
    rotationDegrees: Int? = null,
    isRegular: Boolean? = null
): DrawPathMode = when (this) {
    is DrawPathMode.Star -> copy(
        vertices = vertices ?: this.vertices,
        innerRadiusRatio = innerRadiusRatio ?: this.innerRadiusRatio,
        rotationDegrees = rotationDegrees ?: this.rotationDegrees,
        isRegular = isRegular ?: this.isRegular
    )

    is DrawPathMode.OutlinedStar -> copy(
        vertices = vertices ?: this.vertices,
        innerRadiusRatio = innerRadiusRatio ?: this.innerRadiusRatio,
        rotationDegrees = rotationDegrees ?: this.rotationDegrees,
        isRegular = isRegular ?: this.isRegular
    )

    else -> this
}

internal fun DrawPathMode.withSavedStateFrom(previous: DrawPathMode): DrawPathMode =
    when (previous) {
        is DrawPathMode.Rect if this is DrawPathMode.OutlinedRect -> copy(
            rotationDegrees = previous.rotationDegrees,
            cornerRadius = previous.cornerRadius
        )

        is DrawPathMode.OutlinedRect if this is DrawPathMode.Rect -> copy(
            rotationDegrees = previous.rotationDegrees,
            cornerRadius = previous.cornerRadius
        )

        is DrawPathMode.Polygon if this is DrawPathMode.OutlinedPolygon -> copy(
            vertices = previous.vertices,
            rotationDegrees = previous.rotationDegrees,
            isRegular = previous.isRegular
        )

        is DrawPathMode.OutlinedPolygon if this is DrawPathMode.Polygon -> copy(
            vertices = previous.vertices,
            rotationDegrees = previous.rotationDegrees,
            isRegular = previous.isRegular
        )

        is DrawPathMode.Star if this is DrawPathMode.OutlinedStar -> copy(
            vertices = previous.vertices,
            innerRadiusRatio = previous.innerRadiusRatio,
            rotationDegrees = previous.rotationDegrees,
            isRegular = previous.isRegular
        )

        is DrawPathMode.OutlinedStar if this is DrawPathMode.Star -> copy(
            vertices = previous.vertices,
            innerRadiusRatio = previous.innerRadiusRatio,
            rotationDegrees = previous.rotationDegrees,
            isRegular = previous.isRegular
        )

        is DrawPathMode.PointingArrow if this is DrawPathMode.LinePointingArrow -> copy(
            sizeScale = previous.sizeScale,
            angle = previous.angle
        )

        is DrawPathMode.LinePointingArrow if this is DrawPathMode.PointingArrow -> copy(
            sizeScale = previous.sizeScale,
            angle = previous.angle
        )

        is DrawPathMode.PointingArrow if this is DrawPathMode.DoublePointingArrow -> copy(
            sizeScale = previous.sizeScale,
            angle = previous.angle
        )

        is DrawPathMode.DoublePointingArrow if this is DrawPathMode.PointingArrow -> copy(
            sizeScale = previous.sizeScale,
            angle = previous.angle
        )

        is DrawPathMode.PointingArrow if this is DrawPathMode.DoubleLinePointingArrow -> copy(
            sizeScale = previous.sizeScale,
            angle = previous.angle
        )

        is DrawPathMode.DoubleLinePointingArrow if this is DrawPathMode.PointingArrow -> copy(
            sizeScale = previous.sizeScale,
            angle = previous.angle
        )

        is DrawPathMode.LinePointingArrow if this is DrawPathMode.DoublePointingArrow -> copy(
            sizeScale = previous.sizeScale,
            angle = previous.angle
        )

        is DrawPathMode.DoublePointingArrow if this is DrawPathMode.LinePointingArrow -> copy(
            sizeScale = previous.sizeScale,
            angle = previous.angle
        )

        is DrawPathMode.LinePointingArrow if this is DrawPathMode.DoubleLinePointingArrow -> copy(
            sizeScale = previous.sizeScale,
            angle = previous.angle
        )

        is DrawPathMode.DoubleLinePointingArrow if this is DrawPathMode.LinePointingArrow -> copy(
            sizeScale = previous.sizeScale,
            angle = previous.angle
        )

        is DrawPathMode.DoublePointingArrow if this is DrawPathMode.DoubleLinePointingArrow -> copy(
            sizeScale = previous.sizeScale,
            angle = previous.angle
        )

        is DrawPathMode.DoubleLinePointingArrow if this is DrawPathMode.DoublePointingArrow -> copy(
            sizeScale = previous.sizeScale,
            angle = previous.angle
        )

        else -> this
    }.let { updated ->
        if (previous is DrawPathMode.Outlined && updated is DrawPathMode.Outlined) {
            updated.withOutlinedFillColor(previous.fillColor?.colorInt)
        } else updated
    }
