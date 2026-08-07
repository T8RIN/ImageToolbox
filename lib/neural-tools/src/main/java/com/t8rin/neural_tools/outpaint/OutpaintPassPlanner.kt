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

package com.t8rin.neural_tools.outpaint

import kotlin.math.min

data class OutpaintRect(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
) {
    val width: Int get() = right - left
    val height: Int get() = bottom - top
    val isEmpty: Boolean get() = width <= 0 || height <= 0
}

data class OutpaintPass(
    val knownBefore: OutpaintRect,
    val left: OutpaintRect?,
    val right: OutpaintRect?,
    val top: OutpaintRect?,
    val bottom: OutpaintRect?,
    val knownAfter: OutpaintRect
) {
    val regions: List<OutpaintRect>
        get() = listOfNotNull(left, right, top, bottom)
}

class OutpaintPassPlanner(
    private val maxDepth: Int = 128
) {
    init {
        require(maxDepth > 0) { "maxDepth must be positive" }
    }

    fun plan(
        targetWidth: Int,
        targetHeight: Int,
        sourceWidth: Int,
        sourceHeight: Int,
        offsetX: Int,
        offsetY: Int
    ): List<OutpaintPass> {
        require(targetWidth > 0 && targetHeight > 0) { "Target dimensions must be positive" }
        require(sourceWidth > 0 && sourceHeight > 0) { "Source dimensions must be positive" }
        require(targetWidth >= sourceWidth && targetHeight >= sourceHeight) {
            "Target must not be smaller than source"
        }
        require(offsetX >= 0 && offsetY >= 0) { "Source offset must not be negative" }
        require(offsetX + sourceWidth <= targetWidth && offsetY + sourceHeight <= targetHeight) {
            "Source must fit inside target"
        }

        val result = mutableListOf<OutpaintPass>()
        var known = OutpaintRect(
            left = offsetX,
            top = offsetY,
            right = offsetX + sourceWidth,
            bottom = offsetY + sourceHeight
        )

        while (known.left > 0 || known.top > 0 ||
            known.right < targetWidth || known.bottom < targetHeight
        ) {
            val leftDepth = min(maxDepth, known.left)
            val rightDepth = min(maxDepth, targetWidth - known.right)
            val expandedLeft = known.left - leftDepth
            val expandedRight = known.right + rightDepth
            val topDepth = min(maxDepth, known.top)
            val bottomDepth = min(maxDepth, targetHeight - known.bottom)

            val left = OutpaintRect(expandedLeft, known.top, known.left, known.bottom)
                .takeUnless(OutpaintRect::isEmpty)
            val right = OutpaintRect(known.right, known.top, expandedRight, known.bottom)
                .takeUnless(OutpaintRect::isEmpty)
            val expandedTop = known.top - topDepth
            val expandedBottom = known.bottom + bottomDepth
            val top = OutpaintRect(expandedLeft, expandedTop, expandedRight, known.top)
                .takeUnless(OutpaintRect::isEmpty)
            val bottom = OutpaintRect(expandedLeft, known.bottom, expandedRight, expandedBottom)
                .takeUnless(OutpaintRect::isEmpty)
            val next = OutpaintRect(expandedLeft, expandedTop, expandedRight, expandedBottom)

            result += OutpaintPass(known, left, right, top, bottom, next)
            known = next
        }

        return result
    }
}
