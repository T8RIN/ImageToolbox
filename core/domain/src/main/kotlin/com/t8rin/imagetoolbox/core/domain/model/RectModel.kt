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

package com.t8rin.imagetoolbox.core.domain.model

import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

data class RectModel(
    /** The OffsetModel of the left edge of this rectangle from the x axis. */
    val left: Float,

    /** The OffsetModel of the top edge of this rectangle from the y axis. */
    val top: Float,

    /** The OffsetModel of the right edge of this rectangle from the x axis. */
    val right: Float,

    /** The OffsetModel of the bottom edge of this rectangle from the y axis. */
    val bottom: Float,
) {

    companion object {
        /** A rectangle with left, top, right, and bottom edges all at zero. */
        val Zero: RectModel = RectModel(0.0f, 0.0f, 0.0f, 0.0f)
    }

    /** The distance between the left and right edges of this rectangle. */

    inline val width: Float
        get() = right - left

    /** The distance between the top and bottom edges of this rectangle. */

    inline val height: Float
        get() = bottom - top

    /** The distance between the upper-left corner and the lower-right corner of this rectangle. */

    val size: FloatSize
        get() = FloatSize(width, height)

    /** Whether any of the coordinates of this rectangle are equal to positive infinity. */
    // included for consistency with OffsetModel and Size

    val isInfinite: Boolean
        get() =
            (left == Float.POSITIVE_INFINITY) or
                    (top == Float.POSITIVE_INFINITY) or
                    (right == Float.POSITIVE_INFINITY) or
                    (bottom == Float.POSITIVE_INFINITY)

    /** Whether all coordinates of this rectangle are finite. */

    val isFinite: Boolean
        get() =
            ((left.toRawBits() and 0x7fffffff) < FloatInfinityBase) and
                    ((top.toRawBits() and 0x7fffffff) < FloatInfinityBase) and
                    ((right.toRawBits() and 0x7fffffff) < FloatInfinityBase) and
                    ((bottom.toRawBits() and 0x7fffffff) < FloatInfinityBase)

    /** Whether this rectangle encloses a non-zero area. Negative areas are considered empty. */

    val isEmpty: Boolean
        get() = (left >= right) or (top >= bottom)

    /**
     * Returns a new rectangle translated by the given OffsetModel.
     *
     * To translate a rectangle by separate x and y components rather than by an [offset], consider
     * [translate].
     */

    fun translate(offset: OffsetModel): RectModel {
        return RectModel(
            left + offset.x,
            top + offset.y,
            right + offset.x,
            bottom + offset.y
        )
    }

    /**
     * Returns a new rectangle with translateX added to the x components and translateY added to the
     * y components.
     */

    fun translate(translateX: Float, translateY: Float): RectModel {
        return RectModel(
            left + translateX,
            top + translateY,
            right + translateX,
            bottom + translateY
        )
    }

    /** Returns a new rectangle with edges moved outwards by the given delta. */

    fun inflate(delta: Float): RectModel {
        return RectModel(left - delta, top - delta, right + delta, bottom + delta)
    }

    /** Returns a new rectangle with edges moved inwards by the given delta. */
    fun deflate(delta: Float): RectModel = inflate(-delta)

    /**
     * Returns a new rectangle that is the intersection of the given rectangle and this rectangle.
     * The two rectangles must overlap for this to be meaningful. If the two rectangles do not
     * overlap, then the resulting RectModel will have a negative width or height.
     */

    fun intersect(other: RectModel): RectModel {
        return RectModel(
            max(left, other.left),
            max(top, other.top),
            min(right, other.right),
            min(bottom, other.bottom),
        )
    }

    /**
     * Returns a new rectangle that is the intersection of the given rectangle and this rectangle.
     * The two rectangles must overlap for this to be meaningful. If the two rectangles do not
     * overlap, then the resulting RectModel will have a negative width or height.
     */

    fun intersect(
        otherLeft: Float,
        otherTop: Float,
        otherRight: Float,
        otherBottom: Float
    ): RectModel {
        return RectModel(
            max(left, otherLeft),
            max(top, otherTop),
            min(right, otherRight),
            min(bottom, otherBottom),
        )
    }

    /** Whether `other` has a nonzero area of overlap with this rectangle. */
    fun overlaps(other: RectModel): Boolean {
        return (left < other.right) and
                (other.left < right) and
                (top < other.bottom) and
                (other.top < bottom)
    }

    /** The lesser of the magnitudes of the [width] and the [height] of this rectangle. */
    val minDimension: Float
        get() = min(width.absoluteValue, height.absoluteValue)

    /** The greater of the magnitudes of the [width] and the [height] of this rectangle. */
    val maxDimension: Float
        get() = max(width.absoluteValue, height.absoluteValue)

    /** The OffsetModel to the intersection of the top and left edges of this rectangle. */
    val topLeft: OffsetModel
        get() = OffsetModel(left, top)

    /** The OffsetModel to the center of the top edge of this rectangle. */
    val topCenter: OffsetModel
        get() = OffsetModel(left + width / 2.0f, top)

    /** The OffsetModel to the intersection of the top and right edges of this rectangle. */
    val topRight: OffsetModel
        get() = OffsetModel(right, top)

    /** The OffsetModel to the center of the left edge of this rectangle. */
    val centerLeft: OffsetModel
        get() = OffsetModel(left, top + height / 2.0f)

    /**
     * The OffsetModel to the point halfway between the left and right and the top and bottom edges of
     * this rectangle.
     *
     * See also [Size.center].
     */
    val center: OffsetModel
        get() = OffsetModel(left + width / 2.0f, top + height / 2.0f)

    /** The OffsetModel to the center of the right edge of this rectangle. */
    val centerRight: OffsetModel
        get() = OffsetModel(right, top + height / 2.0f)

    /** The OffsetModel to the intersection of the bottom and left edges of this rectangle. */
    val bottomLeft: OffsetModel
        get() = OffsetModel(left, bottom)

    /** The OffsetModel to the center of the bottom edge of this rectangle. */
    val bottomCenter: OffsetModel
        get() {
            return OffsetModel(left + width / 2.0f, bottom)
        }

    /** The OffsetModel to the intersection of the bottom and right edges of this rectangle. */
    val bottomRight: OffsetModel
        get() {
            return OffsetModel(right, bottom)
        }

    /**
     * Whether the point specified by the given OffsetModel (which is assumed to be relative to the
     * origin) lies between the left and right and the top and bottom edges of this rectangle.
     *
     * Rectangles include their top and left edges but exclude their bottom and right edges.
     */
    operator fun contains(offset: OffsetModel): Boolean {
        val x = offset.x
        val y = offset.y
        return (x >= left) and (x < right) and (y >= top) and (y < bottom)
    }
}

internal const val FloatInfinityBase = 0x7f800000