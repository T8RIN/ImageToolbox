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

@file:Suppress("SameParameterValue")

package ru.tech.imageresizershrinker.core.domain.model

import kotlin.math.max

data class IntegerSize(
    val width: Int,
    val height: Int
) {
    val aspectRatio: Float
        get() = runCatching {
            val value = width.toFloat() / height
            if (value.isNaN()) throw IllegalArgumentException()

            value
        }.getOrNull() ?: 1f

    val safeAspectRatio: Float
        get() = aspectRatio
            .coerceAtLeast(0.005f)
            .coerceAtMost(1000f)

    operator fun times(i: Float): IntegerSize = IntegerSize(
        width = (width * i).toInt(),
        height = (height * i).toInt()
    ).coerceAtLeast(0, 0)

    private fun coerceAtLeast(
        minWidth: Int,
        minHeight: Int
    ): IntegerSize = IntegerSize(
        width = width.coerceAtLeast(minWidth),
        height = height.coerceAtLeast(minHeight)
    )

    fun isZero(): Boolean = width == 0 || height == 0

    fun isDefined(): Boolean = this != Undefined

    companion object {
        val Undefined by lazy {
            IntegerSize(-1, -1)
        }

        val Zero by lazy {
            IntegerSize(0, 0)
        }
    }
}

fun max(size: IntegerSize): Int = maxOf(size.width, size.height)

infix fun Int.sizeTo(int: Int): IntegerSize = IntegerSize(this, int)

fun IntegerSize.flexibleResize(
    w: Int,
    h: Int
): IntegerSize {
    val max = max(w, h)
    return runCatching {
        if (width > w) {
            val aspectRatio = width.toDouble() / height.toDouble()
            val targetHeight = w / aspectRatio
            return@runCatching IntegerSize(w, targetHeight.toInt())
        }

        if (height >= width) {
            val aspectRatio = width.toDouble() / height.toDouble()
            val targetWidth = (max * aspectRatio).toInt()
            IntegerSize(targetWidth, max)
        } else {
            val aspectRatio = height.toDouble() / width.toDouble()
            val targetHeight = (max * aspectRatio).toInt()
            IntegerSize(max, targetHeight)
        }
    }.getOrNull() ?: this
}