package io.github.alexzhirkevich.qrose.options

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path


@Immutable
internal class SquareShape(
    val size: Float = 1f
) : QrShapeModifier {

    override fun Path.path(size: Float, neighbors: Neighbors): Path = apply {
        val s = size * this@SquareShape.size.coerceIn(0f, 1f)
        val offset = (size - s) / 2

        addRect(
            Rect(
                Offset(offset, offset),
                Size(s, s)
            )
        )
    }
}

@Immutable
internal class CircleShape(
    val size: Float
) : QrShapeModifier {

    override fun Path.path(size: Float, neighbors: Neighbors): Path = apply {
        val s = size * this@CircleShape.size.coerceIn(0f, 1f)
        val offset = (size - s) / 2

        addOval(
            Rect(
                Offset(offset, offset),
                Size(s, s)
            )
        )
    }
}

@Immutable
internal class RoundCornersShape(
    val cornerRadius: Float,
    val withNeighbors: Boolean,
    val topLeft: Boolean = true,
    val bottomLeft: Boolean = true,
    val topRight: Boolean = true,
    val bottomRight: Boolean = true,
) : QrShapeModifier {


    override fun Path.path(size: Float, neighbors: Neighbors): Path = apply {

        val corner = (cornerRadius.coerceIn(0f, .5f) * size).let { CornerRadius(it, it) }

        addRoundRect(
            RoundRect(
                Rect(0f, 0f, size, size),
                topLeft = if (topLeft && (withNeighbors.not() || neighbors.top.not() && neighbors.left.not()))
                    corner else CornerRadius.Zero,
                topRight = if (topRight && (withNeighbors.not() || neighbors.top.not() && neighbors.right.not()))
                    corner else CornerRadius.Zero,
                bottomRight = if (bottomRight && (withNeighbors.not() || neighbors.bottom.not() && neighbors.right.not()))
                    corner else CornerRadius.Zero,
                bottomLeft = if (bottomLeft && (withNeighbors.not() || neighbors.bottom.not() && neighbors.left.not()))
                    corner else CornerRadius.Zero
            )
        )
    }

}

@Immutable
internal class VerticalLinesShape(
    private val width: Float
) : QrShapeModifier {

    override fun Path.path(size: Float, neighbors: Neighbors): Path = apply {

        val padding = (size * (1 - width.coerceIn(0f, 1f)))

        if (neighbors.top) {
            addRect(Rect(Offset(padding, 0f), Size(size - padding * 2, size / 2)))
        } else {
            addArc(Rect(Offset(padding, 0f), Size(size - padding * 2, size)), 180f, 180f)
        }

        if (neighbors.bottom) {
            addRect(Rect(Offset(padding, size / 2), Size(size - padding * 2, size / 2)))
        } else {
            addArc(Rect(Offset(padding, 0f), Size(size - padding * 2, size)), 0f, 180f)
        }
    }
}

@Immutable
internal class HorizontalLinesShape(
    private val width: Float
) : QrShapeModifier {

    override fun Path.path(size: Float, neighbors: Neighbors): Path = apply {

        val padding = (size * (1 - width.coerceIn(0f, 1f)))

        if (neighbors.left) {
            addRect(Rect(Offset(0f, padding), Size(size / 2, size - padding * 2)))
        } else {
            addArc(Rect(Offset(0f, padding), Size(size, size - padding * 2)), 90f, 180f)

        }

        if (neighbors.right) {
            addRect(Rect(Offset(size / 2, padding), Size(size / 2, size - padding * 2)))
        } else {
            addArc(Rect(Offset(0f, padding), Size(size, size - padding * 2)), -90f, 180f)
        }
    }
}
