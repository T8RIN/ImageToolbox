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

@file:Suppress("NOTHING_TO_INLINE")

package ru.tech.imageresizershrinker.core.domain.model

import kotlin.math.min

private const val NORMALIZATION_FACTOR = 500

@JvmInline
value class Pt(val value: Float) {
    fun toPx(
        size: IntegerSize
    ): Float = min(
        size.width * (value / NORMALIZATION_FACTOR),
        size.height * (value / NORMALIZATION_FACTOR)
    )


    /**
     * Add two [Pt]s together.
     */
    inline operator fun plus(other: Pt) = Pt(this.value + other.value)

    /**
     * Subtract a Pt from another one.
     */
    inline operator fun minus(other: Pt) = Pt(this.value - other.value)

    /**
     * This is the same as multiplying the Pt by -1.0.
     */
    inline operator fun unaryMinus() = Pt(-value)

    /**
     * Divide a Pt by a scalar.
     */
    inline operator fun div(other: Float): Pt = Pt(value / other)


    inline operator fun div(other: Int): Pt = Pt(value / other)

    /**
     * Divide by another Pt to get a scalar.
     */
    inline operator fun div(other: Pt): Float = value / other.value

    /**
     * Multiply a Pt by a scalar.
     */
    inline operator fun times(other: Float): Pt = Pt(value * other)


    inline operator fun times(other: Int): Pt = Pt(value * other)

    /**
     * Support comparing Dimensions with comparison operators.
     */
    inline operator fun compareTo(other: Pt) = value.compareTo(other.value)

    companion object {
        val Zero = Pt(0f)
    }
}

inline val Float.pt: Pt get() = Pt(this)
inline val Int.pt: Pt get() = Pt(this.toFloat())

inline fun Pt.coerceIn(
    min: Pt,
    max: Pt
) = Pt(value.coerceIn(min.value, max.value))