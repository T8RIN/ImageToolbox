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

sealed interface ShapeMode {
    val kind: Kind

    enum class Kind {
        Line,
        Arrow,
        DoubleArrow,
        LineArrow,
        DoubleLineArrow,
        Rect,
        OutlinedRect,
        Oval,
        OutlinedOval,
        Triangle,
        OutlinedTriangle,
        Polygon,
        OutlinedPolygon,
        Star,
        OutlinedStar
    }

    object Line : ShapeMode {
        override val kind: Kind = Kind.Line
    }

    data class Arrow(
        val sizeScale: Float = DEFAULT_ARROW_SIZE_SCALE,
        val angle: Float = DEFAULT_ARROW_ANGLE
    ) : ShapeMode {
        override val kind: Kind = Kind.Arrow
    }

    data class DoubleArrow(
        val sizeScale: Float = DEFAULT_ARROW_SIZE_SCALE,
        val angle: Float = DEFAULT_ARROW_ANGLE
    ) : ShapeMode {
        override val kind: Kind = Kind.DoubleArrow
    }

    data class LineArrow(
        val sizeScale: Float = DEFAULT_ARROW_SIZE_SCALE,
        val angle: Float = DEFAULT_ARROW_ANGLE
    ) : ShapeMode {
        override val kind: Kind = Kind.LineArrow
    }

    data class DoubleLineArrow(
        val sizeScale: Float = DEFAULT_ARROW_SIZE_SCALE,
        val angle: Float = DEFAULT_ARROW_ANGLE
    ) : ShapeMode {
        override val kind: Kind = Kind.DoubleLineArrow
    }

    data class Rect(
        val rotationDegrees: Int = 0,
        val cornerRadius: Float = DEFAULT_RECT_CORNER_RADIUS
    ) : ShapeMode {
        override val kind: Kind = Kind.Rect
    }

    data class OutlinedRect(
        val rotationDegrees: Int = 0,
        val cornerRadius: Float = DEFAULT_RECT_CORNER_RADIUS,
        val fillColor: Int? = null
    ) : ShapeMode {
        override val kind: Kind = Kind.OutlinedRect
    }

    object Oval : ShapeMode {
        override val kind: Kind = Kind.Oval
    }

    data class OutlinedOval(
        val fillColor: Int? = null
    ) : ShapeMode {
        override val kind: Kind = Kind.OutlinedOval
    }

    object Triangle : ShapeMode {
        override val kind: Kind = Kind.Triangle
    }

    data class OutlinedTriangle(
        val fillColor: Int? = null
    ) : ShapeMode {
        override val kind: Kind = Kind.OutlinedTriangle
    }

    data class Polygon(
        val vertices: Int = DEFAULT_VERTICES,
        val rotationDegrees: Int = 0,
        val isRegular: Boolean = false
    ) : ShapeMode {
        override val kind: Kind = Kind.Polygon
    }

    data class OutlinedPolygon(
        val vertices: Int = DEFAULT_VERTICES,
        val rotationDegrees: Int = 0,
        val isRegular: Boolean = false,
        val fillColor: Int? = null
    ) : ShapeMode {
        override val kind: Kind = Kind.OutlinedPolygon
    }

    data class Star(
        val vertices: Int = DEFAULT_VERTICES,
        val rotationDegrees: Int = 0,
        val innerRadiusRatio: Float = DEFAULT_INNER_RADIUS_RATIO,
        val isRegular: Boolean = false
    ) : ShapeMode {
        override val kind: Kind = Kind.Star
    }

    data class OutlinedStar(
        val vertices: Int = DEFAULT_VERTICES,
        val rotationDegrees: Int = 0,
        val innerRadiusRatio: Float = DEFAULT_INNER_RADIUS_RATIO,
        val isRegular: Boolean = false,
        val fillColor: Int? = null
    ) : ShapeMode {
        override val kind: Kind = Kind.OutlinedStar
    }
}

internal val MarkupLayerShapeModes: List<ShapeMode> by lazy {
    listOf(
        ShapeMode.Line,
        ShapeMode.Arrow(),
        ShapeMode.DoubleArrow(),
        ShapeMode.LineArrow(),
        ShapeMode.DoubleLineArrow(),
        ShapeMode.Rect(),
        ShapeMode.OutlinedRect(),
        ShapeMode.Oval,
        ShapeMode.OutlinedOval(),
        ShapeMode.Triangle,
        ShapeMode.OutlinedTriangle(),
        ShapeMode.Polygon(),
        ShapeMode.OutlinedPolygon(),
        ShapeMode.Star(),
        ShapeMode.OutlinedStar()
    )
}

internal val ShapeMode.ordinal: Int
    get() = MarkupLayerShapeModes.indexOfFirst { it.kind == kind }
        .takeIf { it >= 0 }
        ?: 0

internal fun ShapeMode.Kind.defaultMode(): ShapeMode = when (this) {
    ShapeMode.Kind.Line -> ShapeMode.Line
    ShapeMode.Kind.Arrow -> ShapeMode.Arrow()
    ShapeMode.Kind.DoubleArrow -> ShapeMode.DoubleArrow()
    ShapeMode.Kind.LineArrow -> ShapeMode.LineArrow()
    ShapeMode.Kind.DoubleLineArrow -> ShapeMode.DoubleLineArrow()
    ShapeMode.Kind.Rect -> ShapeMode.Rect()
    ShapeMode.Kind.OutlinedRect -> ShapeMode.OutlinedRect()
    ShapeMode.Kind.Oval -> ShapeMode.Oval
    ShapeMode.Kind.OutlinedOval -> ShapeMode.OutlinedOval()
    ShapeMode.Kind.Triangle -> ShapeMode.Triangle
    ShapeMode.Kind.OutlinedTriangle -> ShapeMode.OutlinedTriangle()
    ShapeMode.Kind.Polygon -> ShapeMode.Polygon()
    ShapeMode.Kind.OutlinedPolygon -> ShapeMode.OutlinedPolygon()
    ShapeMode.Kind.Star -> ShapeMode.Star()
    ShapeMode.Kind.OutlinedStar -> ShapeMode.OutlinedStar()
}

internal fun resolveMarkupLayerShapeMode(
    modeName: String?,
    modeOrdinal: Int?
): ShapeMode {
    val modeByName = modeName
        ?.let { raw -> ShapeMode.Kind.entries.firstOrNull { it.name == raw } }
        ?.defaultMode()

    return modeByName
        ?: modeOrdinal?.let(MarkupLayerShapeModes::getOrNull)
        ?: ShapeMode.Kind.OutlinedRect.defaultMode()
}

internal fun ShapeMode.isOutlinedShapeMode(): Boolean = when (this) {
    is ShapeMode.OutlinedRect,
    is ShapeMode.OutlinedOval,
    is ShapeMode.OutlinedTriangle,
    is ShapeMode.OutlinedPolygon,
    is ShapeMode.OutlinedStar -> true

    else -> false
}

internal fun ShapeMode.isFilledShapeMode(): Boolean = when (this) {
    is ShapeMode.Rect,
    ShapeMode.Oval,
    ShapeMode.Triangle,
    is ShapeMode.Polygon,
    is ShapeMode.Star,
    is ShapeMode.Arrow,
    is ShapeMode.DoubleArrow -> true

    else -> false
}

internal fun ShapeMode.usesStrokeWidth(): Boolean = when (this) {
    ShapeMode.Line,
    is ShapeMode.LineArrow,
    is ShapeMode.DoubleLineArrow,
    is ShapeMode.OutlinedRect,
    is ShapeMode.OutlinedOval,
    is ShapeMode.OutlinedTriangle,
    is ShapeMode.OutlinedPolygon,
    is ShapeMode.OutlinedStar -> true

    else -> false
}

internal fun ShapeMode.outlinedFillColorInt(): Int? = when (this) {
    is ShapeMode.OutlinedRect -> fillColor
    is ShapeMode.OutlinedOval -> fillColor
    is ShapeMode.OutlinedTriangle -> fillColor
    is ShapeMode.OutlinedPolygon -> fillColor
    is ShapeMode.OutlinedStar -> fillColor
    else -> null
}

internal fun ShapeMode.withOutlinedFillColor(color: Int?): ShapeMode = when (this) {
    is ShapeMode.OutlinedRect -> copy(fillColor = color)
    is ShapeMode.OutlinedOval -> copy(fillColor = color)
    is ShapeMode.OutlinedTriangle -> copy(fillColor = color)
    is ShapeMode.OutlinedPolygon -> copy(fillColor = color)
    is ShapeMode.OutlinedStar -> copy(fillColor = color)
    else -> this
}

internal fun ShapeMode.arrowSizeScale(): Float = when (this) {
    is ShapeMode.Arrow -> sizeScale
    is ShapeMode.DoubleArrow -> sizeScale
    is ShapeMode.LineArrow -> sizeScale
    is ShapeMode.DoubleLineArrow -> sizeScale
    else -> DEFAULT_ARROW_SIZE_SCALE
}

internal fun ShapeMode.arrowAngle(): Float = when (this) {
    is ShapeMode.Arrow -> angle
    is ShapeMode.DoubleArrow -> angle
    is ShapeMode.LineArrow -> angle
    is ShapeMode.DoubleLineArrow -> angle
    else -> DEFAULT_ARROW_ANGLE
}

internal fun ShapeMode.vertices(): Int = when (this) {
    is ShapeMode.Polygon -> vertices
    is ShapeMode.OutlinedPolygon -> vertices
    is ShapeMode.Star -> vertices
    is ShapeMode.OutlinedStar -> vertices
    else -> DEFAULT_VERTICES
}

internal fun ShapeMode.rotationDegrees(): Int = when (this) {
    is ShapeMode.Rect -> rotationDegrees
    is ShapeMode.OutlinedRect -> rotationDegrees
    is ShapeMode.Polygon -> rotationDegrees
    is ShapeMode.OutlinedPolygon -> rotationDegrees
    is ShapeMode.Star -> rotationDegrees
    is ShapeMode.OutlinedStar -> rotationDegrees
    else -> 0
}

internal fun ShapeMode.cornerRadius(): Float = when (this) {
    is ShapeMode.Rect -> cornerRadius
    is ShapeMode.OutlinedRect -> cornerRadius
    else -> DEFAULT_RECT_CORNER_RADIUS
}

internal fun ShapeMode.isRegular(): Boolean = when (this) {
    is ShapeMode.Polygon -> isRegular
    is ShapeMode.OutlinedPolygon -> isRegular
    is ShapeMode.Star -> isRegular
    is ShapeMode.OutlinedStar -> isRegular
    else -> false
}

internal fun ShapeMode.innerRadiusRatio(): Float = when (this) {
    is ShapeMode.Star -> innerRadiusRatio
    is ShapeMode.OutlinedStar -> innerRadiusRatio
    else -> DEFAULT_INNER_RADIUS_RATIO
}

internal fun ShapeMode.updateArrow(
    sizeScale: Float? = null,
    angle: Float? = null
): ShapeMode = when (this) {
    is ShapeMode.Arrow -> copy(
        sizeScale = sizeScale ?: this.sizeScale,
        angle = angle ?: this.angle
    )

    is ShapeMode.DoubleArrow -> copy(
        sizeScale = sizeScale ?: this.sizeScale,
        angle = angle ?: this.angle
    )

    is ShapeMode.LineArrow -> copy(
        sizeScale = sizeScale ?: this.sizeScale,
        angle = angle ?: this.angle
    )

    is ShapeMode.DoubleLineArrow -> copy(
        sizeScale = sizeScale ?: this.sizeScale,
        angle = angle ?: this.angle
    )

    else -> this
}

internal fun ShapeMode.updateRect(
    rotationDegrees: Int? = null,
    cornerRadius: Float? = null
): ShapeMode = when (this) {
    is ShapeMode.Rect -> copy(
        rotationDegrees = rotationDegrees ?: this.rotationDegrees,
        cornerRadius = cornerRadius ?: this.cornerRadius
    )

    is ShapeMode.OutlinedRect -> copy(
        rotationDegrees = rotationDegrees ?: this.rotationDegrees,
        cornerRadius = cornerRadius ?: this.cornerRadius
    )

    else -> this
}

internal fun ShapeMode.updatePolygon(
    vertices: Int? = null,
    rotationDegrees: Int? = null,
    isRegular: Boolean? = null
): ShapeMode = when (this) {
    is ShapeMode.Polygon -> copy(
        vertices = vertices ?: this.vertices,
        rotationDegrees = rotationDegrees ?: this.rotationDegrees,
        isRegular = isRegular ?: this.isRegular
    )

    is ShapeMode.OutlinedPolygon -> copy(
        vertices = vertices ?: this.vertices,
        rotationDegrees = rotationDegrees ?: this.rotationDegrees,
        isRegular = isRegular ?: this.isRegular
    )

    else -> this
}

internal fun ShapeMode.updateStar(
    vertices: Int? = null,
    innerRadiusRatio: Float? = null,
    rotationDegrees: Int? = null,
    isRegular: Boolean? = null
): ShapeMode = when (this) {
    is ShapeMode.Star -> copy(
        vertices = vertices ?: this.vertices,
        innerRadiusRatio = innerRadiusRatio ?: this.innerRadiusRatio,
        rotationDegrees = rotationDegrees ?: this.rotationDegrees,
        isRegular = isRegular ?: this.isRegular
    )

    is ShapeMode.OutlinedStar -> copy(
        vertices = vertices ?: this.vertices,
        innerRadiusRatio = innerRadiusRatio ?: this.innerRadiusRatio,
        rotationDegrees = rotationDegrees ?: this.rotationDegrees,
        isRegular = isRegular ?: this.isRegular
    )

    else -> this
}

internal fun ShapeMode.withSavedStateFrom(previous: ShapeMode): ShapeMode {
    val previousArrow = previous.takeIf {
        it is ShapeMode.Arrow ||
                it is ShapeMode.DoubleArrow ||
                it is ShapeMode.LineArrow ||
                it is ShapeMode.DoubleLineArrow
    }
    if (previousArrow != null) {
        val current = updateArrow(
            sizeScale = previousArrow.arrowSizeScale(),
            angle = previousArrow.arrowAngle()
        )
        if (current !== this) return current
    }

    return when (previous) {
        is ShapeMode.Rect,
        is ShapeMode.OutlinedRect -> updateRect(
            rotationDegrees = previous.rotationDegrees(),
            cornerRadius = previous.cornerRadius()
        )

        is ShapeMode.Polygon,
        is ShapeMode.OutlinedPolygon -> updatePolygon(
            vertices = previous.vertices(),
            rotationDegrees = previous.rotationDegrees(),
            isRegular = previous.isRegular()
        )

        is ShapeMode.Star,
        is ShapeMode.OutlinedStar -> updateStar(
            vertices = previous.vertices(),
            innerRadiusRatio = previous.innerRadiusRatio(),
            rotationDegrees = previous.rotationDegrees(),
            isRegular = previous.isRegular()
        )

        else -> this
    }
}

private const val DEFAULT_ARROW_SIZE_SCALE = 3f
private const val DEFAULT_ARROW_ANGLE = 150f
private const val DEFAULT_VERTICES = 5
private const val DEFAULT_INNER_RADIUS_RATIO = 0.5f
private const val DEFAULT_RECT_CORNER_RADIUS = 0f
