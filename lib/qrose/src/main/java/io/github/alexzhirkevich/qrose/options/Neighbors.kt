package io.github.alexzhirkevich.qrose.options

import androidx.compose.runtime.Immutable


/**
 * Status of the neighbor QR code pixels or eyes
 * */

@Immutable
class Neighbors(
    val topLeft: Boolean = false,
    val topRight: Boolean = false,
    val left: Boolean = false,
    val top: Boolean = false,
    val right: Boolean = false,
    val bottomLeft: Boolean = false,
    val bottom: Boolean = false,
    val bottomRight: Boolean = false,
) {

    companion object {
        val Empty = Neighbors()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Neighbors

        if (topLeft != other.topLeft) return false
        if (topRight != other.topRight) return false
        if (left != other.left) return false
        if (top != other.top) return false
        if (right != other.right) return false
        if (bottomLeft != other.bottomLeft) return false
        if (bottom != other.bottom) return false
        if (bottomRight != other.bottomRight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = topLeft.hashCode()
        result = 31 * result + topRight.hashCode()
        result = 31 * result + left.hashCode()
        result = 31 * result + top.hashCode()
        result = 31 * result + right.hashCode()
        result = 31 * result + bottomLeft.hashCode()
        result = 31 * result + bottom.hashCode()
        result = 31 * result + bottomRight.hashCode()
        return result
    }
}

val Neighbors.hasAny: Boolean
    get() = topLeft || topRight || left || top ||
            right || bottomLeft || bottom || bottomRight

val Neighbors.hasAllNearest
    get() = top && bottom && left && right

val Neighbors.hasAll: Boolean
    get() = topLeft && topRight && left && top &&
            right && bottomLeft && bottom && bottomRight

