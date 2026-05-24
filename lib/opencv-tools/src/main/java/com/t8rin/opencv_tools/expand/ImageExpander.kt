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

@file:Suppress("ConstPropertyName")

package com.t8rin.opencv_tools.expand

import android.graphics.Bitmap
import com.t8rin.opencv_tools.utils.OpenCV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object ImageExpander : OpenCV() {

    suspend fun expand(
        bitmap: Bitmap,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ): Bitmap = coroutineScope {
        require(left >= 0 && top >= 0 && right >= 0 && bottom >= 0)
        val outputWidth = bitmap.width.toLong() + left + right
        val outputHeight = bitmap.height.toLong() + top + bottom
        require(outputWidth in 1..Int.MAX_VALUE)
        require(outputHeight in 1..Int.MAX_VALUE)

        withContext(Dispatchers.Default) {
            if (left == 0 && top == 0 && right == 0 && bottom == 0) {
                return@withContext bitmap.copy(Bitmap.Config.ARGB_8888, false)
            }

            val source = bitmap.copy(Bitmap.Config.ARGB_8888, false)
            val sourceWidth = source.width
            val sourceHeight = source.height
            val resultWidth = outputWidth.toInt()
            val resultHeight = outputHeight.toInt()
            val sourcePixels = IntArray(sourceWidth * sourceHeight)
            val resultPixels = IntArray(resultWidth * resultHeight)
            val knownPixels = BooleanArray(resultPixels.size)

            source.getPixels(
                sourcePixels,
                0,
                sourceWidth,
                0,
                0,
                sourceWidth,
                sourceHeight
            )

            copySourceToResult(
                sourcePixels = sourcePixels,
                resultPixels = resultPixels,
                knownPixels = knownPixels,
                sourceWidth = sourceWidth,
                sourceHeight = sourceHeight,
                resultWidth = resultWidth,
                left = left,
                top = top
            )

            if (sourceWidth == 1 && sourceHeight == 1) {
                fillUnknownWithProjection(
                    sourcePixels = sourcePixels,
                    resultPixels = resultPixels,
                    knownPixels = knownPixels,
                    sourceWidth = sourceWidth,
                    sourceHeight = sourceHeight,
                    resultWidth = resultWidth,
                    left = left,
                    top = top
                )
            } else {
                val config = SynthesisConfig.forImage(
                    sourceWidth = sourceWidth,
                    sourceHeight = sourceHeight,
                    maxExpansion = max(max(left, right), max(top, bottom))
                )

                synthesizeTexture(
                    sourcePixels = sourcePixels,
                    resultPixels = resultPixels,
                    knownPixels = knownPixels,
                    sourceWidth = sourceWidth,
                    sourceHeight = sourceHeight,
                    resultWidth = resultWidth,
                    resultHeight = resultHeight,
                    sourceLeft = left,
                    sourceTop = top,
                    config = config
                )
                fillUnknownWithProjection(
                    sourcePixels = sourcePixels,
                    resultPixels = resultPixels,
                    knownPixels = knownPixels,
                    sourceWidth = sourceWidth,
                    sourceHeight = sourceHeight,
                    resultWidth = resultWidth,
                    left = left,
                    top = top
                )
            }

            Bitmap.createBitmap(
                resultPixels,
                resultWidth,
                resultHeight,
                Bitmap.Config.ARGB_8888
            )
        }
    }

    private suspend fun synthesizeTexture(
        sourcePixels: IntArray,
        resultPixels: IntArray,
        knownPixels: BooleanArray,
        sourceWidth: Int,
        sourceHeight: Int,
        resultWidth: Int,
        resultHeight: Int,
        sourceLeft: Int,
        sourceTop: Int,
        config: SynthesisConfig
    ) = coroutineScope {
        val random = Random(
            sourceWidth * SeedSourceWidthFactor +
                    sourceHeight * SeedSourceHeightFactor +
                    resultWidth * SeedResultWidthFactor +
                    resultHeight * SeedResultHeightFactor
        )
        val placements = buildPatchPlacements(
            resultWidth = resultWidth,
            resultHeight = resultHeight,
            sourceLeft = sourceLeft,
            sourceTop = sourceTop,
            sourceWidth = sourceWidth,
            sourceHeight = sourceHeight,
            step = config.step,
            patchSize = config.patchSize
        )
        var remaining = knownPixels.count { !it }
        var requiredOverlap = config.requiredOverlap

        repeat(config.maxPasses) { pass ->
            ensureActive()
            if (remaining == 0) return@coroutineScope

            var filledThisPass = 0
            val relaxedOverlap = if (pass >= config.maxPasses - RelaxedPassCount) {
                1
            } else {
                requiredOverlap
            }

            placements.forEach { placement ->
                ensureActive()
                val bounds = PatchBounds.fromCenter(
                    centerX = placement.centerX,
                    centerY = placement.centerY,
                    patchSize = config.patchSize,
                    imageWidth = resultWidth,
                    imageHeight = resultHeight
                )

                if (!bounds.hasUnknown(knownPixels, resultWidth)) return@forEach

                val overlap = bounds.knownCount(knownPixels, resultWidth, config.sampleStride)
                if (overlap < relaxedOverlap) return@forEach

                val sourcePatch = findBestSourcePatch(
                    targetBounds = bounds,
                    sourcePixels = sourcePixels,
                    resultPixels = resultPixels,
                    knownPixels = knownPixels,
                    sourceWidth = sourceWidth,
                    sourceHeight = sourceHeight,
                    resultWidth = resultWidth,
                    sourceLeft = sourceLeft,
                    sourceTop = sourceTop,
                    config = config,
                    random = random
                )
                val filled = copyUnknownPatch(
                    sourcePatch = sourcePatch,
                    targetBounds = bounds,
                    sourcePixels = sourcePixels,
                    resultPixels = resultPixels,
                    knownPixels = knownPixels,
                    sourceWidth = sourceWidth,
                    sourceHeight = sourceHeight,
                    resultWidth = resultWidth,
                    resultHeight = resultHeight,
                    sourceLeft = sourceLeft,
                    sourceTop = sourceTop,
                    blendWidth = config.blendWidth
                )

                filledThisPass += filled
                remaining -= filled
            }

            if (filledThisPass == 0) {
                requiredOverlap = (requiredOverlap / 2).coerceAtLeast(1)
            }
        }
    }

    private suspend fun copySourceToResult(
        sourcePixels: IntArray,
        resultPixels: IntArray,
        knownPixels: BooleanArray,
        sourceWidth: Int,
        sourceHeight: Int,
        resultWidth: Int,
        left: Int,
        top: Int
    ) = coroutineScope {
        for (y in 0 until sourceHeight) {
            ensureActive()
            val sourceIndex = y * sourceWidth
            val resultIndex = (top + y) * resultWidth + left
            sourcePixels.copyInto(
                destination = resultPixels,
                destinationOffset = resultIndex,
                startIndex = sourceIndex,
                endIndex = sourceIndex + sourceWidth
            )

            for (x in 0 until sourceWidth) {
                ensureActive()
                knownPixels[resultIndex + x] = true
            }
        }
    }

    private suspend fun buildPatchPlacements(
        resultWidth: Int,
        resultHeight: Int,
        sourceLeft: Int,
        sourceTop: Int,
        sourceWidth: Int,
        sourceHeight: Int,
        step: Int,
        patchSize: Int
    ): List<PatchPlacement> {
        val xs = steppedPositions(resultWidth, step)
        val ys = steppedPositions(resultHeight, step)
        val sourceRight = sourceLeft + sourceWidth - 1
        val sourceBottom = sourceTop + sourceHeight - 1

        return ys.flatMap { y ->
            xs.mapNotNull patch@{ x ->
                val bounds = PatchBounds.fromCenter(
                    centerX = x,
                    centerY = y,
                    patchSize = patchSize,
                    imageWidth = resultWidth,
                    imageHeight = resultHeight
                )

                if (bounds.isInsideSource(sourceLeft, sourceTop, sourceWidth, sourceHeight)) {
                    return@patch null
                }

                val dx = when {
                    x < sourceLeft -> sourceLeft - x
                    x > sourceRight -> x - sourceRight
                    else -> 0
                }
                val dy = when {
                    y < sourceTop -> sourceTop - y
                    y > sourceBottom -> y - sourceBottom
                    else -> 0
                }

                PatchPlacement(
                    centerX = x,
                    centerY = y,
                    distance = dx * dx + dy * dy
                )
            }
        }
            .sortedBy(PatchPlacement::distance)
    }

    private suspend fun steppedPositions(
        size: Int,
        step: Int
    ): List<Int> = coroutineScope {
        buildSet {
            var position = 0

            while (position < size) {
                ensureActive()
                add(position)
                position += step
            }

            add(size - 1)
        }.sorted()
    }

    private suspend fun findBestSourcePatch(
        targetBounds: PatchBounds,
        sourcePixels: IntArray,
        resultPixels: IntArray,
        knownPixels: BooleanArray,
        sourceWidth: Int,
        sourceHeight: Int,
        resultWidth: Int,
        sourceLeft: Int,
        sourceTop: Int,
        config: SynthesisConfig,
        random: Random
    ): SourcePatch {
        val allowed = SourceSearchArea.forTarget(
            targetBounds = targetBounds,
            sourceLeft = sourceLeft,
            sourceTop = sourceTop,
            sourceWidth = sourceWidth,
            sourceHeight = sourceHeight,
            bandSize = config.sourceBandSize
        )
        val anchor = allowed.anchorPatch(
            targetBounds = targetBounds,
            sourceLeft = sourceLeft,
            sourceTop = sourceTop,
            sourceWidth = sourceWidth,
            sourceHeight = sourceHeight
        )

        var bestPatch = anchor
        var bestError = targetBounds.matchError(
            sourcePatch = anchor,
            sourcePixels = sourcePixels,
            resultPixels = resultPixels,
            knownPixels = knownPixels,
            sourceWidth = sourceWidth,
            resultWidth = resultWidth,
            sampleStride = config.sampleStride
        )

        repeat(config.candidateCount) {
            val candidate = allowed.randomPatch(
                random = random
            )
            val error = targetBounds.matchError(
                sourcePatch = candidate,
                sourcePixels = sourcePixels,
                resultPixels = resultPixels,
                knownPixels = knownPixels,
                sourceWidth = sourceWidth,
                resultWidth = resultWidth,
                sampleStride = config.sampleStride
            )

            if (error < bestError) {
                bestError = error
                bestPatch = candidate
            }
        }

        return bestPatch
    }

    private suspend fun copyUnknownPatch(
        sourcePatch: SourcePatch,
        targetBounds: PatchBounds,
        sourcePixels: IntArray,
        resultPixels: IntArray,
        knownPixels: BooleanArray,
        sourceWidth: Int,
        sourceHeight: Int,
        resultWidth: Int,
        resultHeight: Int,
        sourceLeft: Int,
        sourceTop: Int,
        blendWidth: Int
    ): Int = coroutineScope {
        var filled = 0
        val shouldBlendLeft = targetBounds.hasKnownColumn(knownPixels, resultWidth, 0)
        val shouldBlendTop = targetBounds.hasKnownRow(knownPixels, resultWidth, 0)
        val shouldBlendRight = targetBounds.hasKnownColumn(
            knownPixels = knownPixels,
            imageWidth = resultWidth,
            localX = targetBounds.width - 1
        )
        val shouldBlendBottom = targetBounds.hasKnownRow(
            knownPixels = knownPixels,
            imageWidth = resultWidth,
            localY = targetBounds.height - 1
        )

        for (y in 0 until targetBounds.height) {
            ensureActive()
            val resultIndex = (targetBounds.top + y) * resultWidth + targetBounds.left
            val sourceIndex = (sourcePatch.top + y) * sourceWidth + sourcePatch.left

            for (x in 0 until targetBounds.width) {
                ensureActive()
                val index = resultIndex + x
                val resultX = targetBounds.left + x
                val resultY = targetBounds.top + y
                val incoming = sourcePixels[sourceIndex + x]

                if (!knownPixels[index]) {
                    resultPixels[index] = incoming
                    knownPixels[index] = true
                    filled++
                } else {
                    val insideOriginal = isInsideOriginalSource(
                        x = resultX,
                        y = resultY,
                        sourceLeft = sourceLeft,
                        sourceTop = sourceTop,
                        sourceWidth = sourceWidth,
                        sourceHeight = sourceHeight
                    )
                    val blend = minOf(
                        if (shouldBlendLeft && x < blendWidth) {
                            smoothStep(x, blendWidth)
                        } else {
                            1f
                        },
                        if (shouldBlendTop && y < blendWidth) {
                            smoothStep(y, blendWidth)
                        } else {
                            1f
                        },
                        if (shouldBlendRight && targetBounds.width - x - 1 < blendWidth) {
                            smoothStep(targetBounds.width - x - 1, blendWidth)
                        } else {
                            1f
                        },
                        if (shouldBlendBottom && targetBounds.height - y - 1 < blendWidth) {
                            smoothStep(targetBounds.height - y - 1, blendWidth)
                        } else {
                            1f
                        }
                    )

                    if (insideOriginal) {
                        val edgeBlend = originalEdgeBlendAmount(
                            x = resultX,
                            y = resultY,
                            sourceLeft = sourceLeft,
                            sourceTop = sourceTop,
                            sourceWidth = sourceWidth,
                            sourceHeight = sourceHeight,
                            resultWidth = resultWidth,
                            resultHeight = resultHeight,
                            blendWidth = blendWidth
                        )
                        val amount = minOf(blend, edgeBlend)

                        if (amount > 0f) {
                            resultPixels[index] = mixColors(
                                from = resultPixels[index],
                                to = incoming,
                                amount = amount
                            )
                        }
                    } else if (blend < 1f) {
                        resultPixels[index] = mixColors(
                            from = resultPixels[index],
                            to = incoming,
                            amount = blend
                        )
                    }
                }
            }
        }

        filled
    }

    private suspend fun fillUnknownWithProjection(
        sourcePixels: IntArray,
        resultPixels: IntArray,
        knownPixels: BooleanArray,
        sourceWidth: Int,
        sourceHeight: Int,
        resultWidth: Int,
        left: Int,
        top: Int
    ) = coroutineScope {
        for (index in knownPixels.indices) {
            ensureActive()
            if (knownPixels[index]) continue

            val x = index % resultWidth
            val y = index / resultWidth
            val sourceX = (x - left).coerceIn(0, sourceWidth - 1)
            val sourceY = (y - top).coerceIn(0, sourceHeight - 1)

            resultPixels[index] = sourcePixels[sourceY * sourceWidth + sourceX]
            knownPixels[index] = true
        }
    }

    private data class SynthesisConfig(
        val patchSize: Int,
        val step: Int,
        val sampleStride: Int,
        val requiredOverlap: Int,
        val candidateCount: Int,
        val sourceBandSize: Int,
        val blendWidth: Int,
        val maxPasses: Int
    ) {
        companion object {
            fun forImage(
                sourceWidth: Int,
                sourceHeight: Int,
                maxExpansion: Int
            ): SynthesisConfig {
                val minSide = min(sourceWidth, sourceHeight)
                val patchSize = min(minSide, (minSide / PatchDivisor).coerceAtLeast(MinPatchSize))
                    .coerceAtMost(MaxPatchSize)
                    .toOdd()
                    .coerceAtLeast(1)
                val step = (patchSize / 2).coerceAtLeast(1)
                val stride = when {
                    patchSize >= LargePatchSize -> 4
                    patchSize >= MediumPatchSize -> 2
                    else -> 1
                }
                val sampledArea = ((patchSize + stride - 1) / stride).let { it * it }
                val passes =
                    (maxExpansion / step + ExtraPassCount).coerceIn(MinPassCount, MaxPassCount)

                return SynthesisConfig(
                    patchSize = patchSize,
                    step = step,
                    sampleStride = stride,
                    requiredOverlap = (sampledArea / RequiredOverlapDivisor).coerceAtLeast(1),
                    candidateCount = when {
                        sourceWidth * sourceHeight < SmallImageArea -> SmallCandidateCount
                        patchSize >= LargePatchSize -> LargeCandidateCount
                        else -> DefaultCandidateCount
                    },
                    sourceBandSize = (patchSize * SourceBandPatchMultiplier)
                        .coerceAtMost(max(sourceWidth, sourceHeight)),
                    blendWidth = (patchSize - step).coerceAtLeast(1),
                    maxPasses = passes
                )
            }
        }
    }

    private data class PatchPlacement(
        val centerX: Int,
        val centerY: Int,
        val distance: Int
    )

    private data class PatchBounds(
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int
    ) {
        val width: Int get() = right - left
        val height: Int get() = bottom - top

        suspend fun hasUnknown(
            knownPixels: BooleanArray,
            imageWidth: Int
        ): Boolean = coroutineScope {
            for (y in top until bottom) {
                ensureActive()
                val index = y * imageWidth + left

                for (x in 0 until width) {
                    ensureActive()
                    if (!knownPixels[index + x]) return@coroutineScope true
                }
            }

            return@coroutineScope false
        }

        suspend fun knownCount(
            knownPixels: BooleanArray,
            imageWidth: Int,
            sampleStride: Int
        ): Int = coroutineScope {
            var count = 0

            for (y in top until bottom step sampleStride) {
                ensureActive()
                val index = y * imageWidth + left

                for (x in 0 until width step sampleStride) {
                    ensureActive()
                    if (knownPixels[index + x]) count++
                }
            }

            return@coroutineScope count
        }

        suspend fun hasKnownColumn(
            knownPixels: BooleanArray,
            imageWidth: Int,
            localX: Int
        ): Boolean = coroutineScope {
            for (y in top until bottom) {
                ensureActive()
                if (knownPixels[y * imageWidth + left + localX]) return@coroutineScope true
            }

            return@coroutineScope false
        }

        suspend fun hasKnownRow(
            knownPixels: BooleanArray,
            imageWidth: Int,
            localY: Int
        ): Boolean = coroutineScope {
            val index = (top + localY) * imageWidth + left

            for (x in 0 until width) {
                ensureActive()
                if (knownPixels[index + x]) return@coroutineScope true
            }

            return@coroutineScope false
        }

        suspend fun matchError(
            sourcePatch: SourcePatch,
            sourcePixels: IntArray,
            resultPixels: IntArray,
            knownPixels: BooleanArray,
            sourceWidth: Int,
            resultWidth: Int,
            sampleStride: Int
        ): Long = coroutineScope {
            var error = 0L
            var samples = 0

            for (y in 0 until height step sampleStride) {
                ensureActive()
                val resultIndex = (top + y) * resultWidth + left
                val sourceIndex = (sourcePatch.top + y) * sourceWidth + sourcePatch.left

                for (x in 0 until width step sampleStride) {
                    ensureActive()
                    if (knownPixels[resultIndex + x]) {
                        error += colorDistance(
                            resultPixels[resultIndex + x],
                            sourcePixels[sourceIndex + x]
                        )
                        samples++
                    }
                }
            }

            return@coroutineScope if (samples == 0) Long.MAX_VALUE else error / samples
        }

        fun isInsideSource(
            sourceLeft: Int,
            sourceTop: Int,
            sourceWidth: Int,
            sourceHeight: Int
        ): Boolean = left >= sourceLeft &&
                top >= sourceTop &&
                right <= sourceLeft + sourceWidth &&
                bottom <= sourceTop + sourceHeight

        companion object {
            fun fromCenter(
                centerX: Int,
                centerY: Int,
                patchSize: Int,
                imageWidth: Int,
                imageHeight: Int
            ): PatchBounds {
                val radius = patchSize / 2
                val left = (centerX - radius).coerceAtLeast(0)
                val top = (centerY - radius).coerceAtLeast(0)

                return PatchBounds(
                    left = left,
                    top = top,
                    right = (left + patchSize).coerceAtMost(imageWidth),
                    bottom = (top + patchSize).coerceAtMost(imageHeight)
                )
            }
        }
    }

    private data class SourcePatch(
        val left: Int,
        val top: Int
    )

    private data class SourceSearchArea(
        val minLeft: Int,
        val maxLeft: Int,
        val minTop: Int,
        val maxTop: Int
    ) {
        fun anchorPatch(
            targetBounds: PatchBounds,
            sourceLeft: Int,
            sourceTop: Int,
            sourceWidth: Int,
            sourceHeight: Int
        ): SourcePatch {
            val anchorX = (targetBounds.left + targetBounds.width / 2 - sourceLeft)
                .coerceIn(0, sourceWidth - 1)
            val anchorY = (targetBounds.top + targetBounds.height / 2 - sourceTop)
                .coerceIn(0, sourceHeight - 1)

            return SourcePatch(
                left = (anchorX - targetBounds.width / 2).coerceIn(minLeft, maxLeft),
                top = (anchorY - targetBounds.height / 2).coerceIn(minTop, maxTop)
            )
        }

        fun randomPatch(random: Random): SourcePatch = SourcePatch(
            left = random.nextIntInclusive(minLeft, maxLeft),
            top = random.nextIntInclusive(minTop, maxTop)
        )

        companion object {
            fun forTarget(
                targetBounds: PatchBounds,
                sourceLeft: Int,
                sourceTop: Int,
                sourceWidth: Int,
                sourceHeight: Int,
                bandSize: Int
            ): SourceSearchArea {
                val sourceRight = sourceLeft + sourceWidth - 1
                val sourceBottom = sourceTop + sourceHeight - 1
                val maxPatchLeft = sourceWidth - targetBounds.width
                val maxPatchTop = sourceHeight - targetBounds.height
                val targetCenterX = targetBounds.left + targetBounds.width / 2
                val targetCenterY = targetBounds.top + targetBounds.height / 2
                val horizontalRange = when {
                    targetCenterX < sourceLeft -> 0..bandSize.coerceAtMost(maxPatchLeft)
                    targetCenterX > sourceRight -> (maxPatchLeft - bandSize)
                        .coerceAtLeast(0)..maxPatchLeft

                    else -> 0..maxPatchLeft
                }
                val verticalRange = when {
                    targetCenterY < sourceTop -> 0..bandSize.coerceAtMost(maxPatchTop)
                    targetCenterY > sourceBottom -> (maxPatchTop - bandSize)
                        .coerceAtLeast(0)..maxPatchTop

                    else -> 0..maxPatchTop
                }

                return SourceSearchArea(
                    minLeft = horizontalRange.first,
                    maxLeft = horizontalRange.last,
                    minTop = verticalRange.first,
                    maxTop = verticalRange.last
                )
            }
        }
    }

    private fun colorDistance(
        first: Int,
        second: Int
    ): Long {
        val alpha = (first ushr 24 and 0xFF) - (second ushr 24 and 0xFF)
        val red = (first ushr 16 and 0xFF) - (second ushr 16 and 0xFF)
        val green = (first ushr 8 and 0xFF) - (second ushr 8 and 0xFF)
        val blue = (first and 0xFF) - (second and 0xFF)

        return (red * red * RedWeight +
                green * green * GreenWeight +
                blue * blue * BlueWeight +
                alpha * alpha * AlphaWeight).toLong()
    }

    private fun Int.toOdd(): Int = if (this % 2 == 0) this - 1 else this

    private fun Random.nextIntInclusive(
        from: Int,
        to: Int
    ): Int = if (from >= to) from else nextInt(from, to + 1)

    private fun isInsideOriginalSource(
        x: Int,
        y: Int,
        sourceLeft: Int,
        sourceTop: Int,
        sourceWidth: Int,
        sourceHeight: Int
    ): Boolean = x in sourceLeft until sourceLeft + sourceWidth &&
            y in sourceTop until sourceTop + sourceHeight

    private fun originalEdgeBlendAmount(
        x: Int,
        y: Int,
        sourceLeft: Int,
        sourceTop: Int,
        sourceWidth: Int,
        sourceHeight: Int,
        resultWidth: Int,
        resultHeight: Int,
        blendWidth: Int
    ): Float {
        var amount = 0f
        val sourceRight = sourceLeft + sourceWidth - 1
        val sourceBottom = sourceTop + sourceHeight - 1

        if (sourceLeft > 0) {
            val distance = x - sourceLeft
            if (distance in 0 until blendWidth) {
                amount = max(amount, 1f - smoothStep(distance, blendWidth))
            }
        }

        if (sourceTop > 0) {
            val distance = y - sourceTop
            if (distance in 0 until blendWidth) {
                amount = max(amount, 1f - smoothStep(distance, blendWidth))
            }
        }

        if (sourceRight < resultWidth - 1) {
            val distance = sourceRight - x
            if (distance in 0 until blendWidth) {
                amount = max(amount, 1f - smoothStep(distance, blendWidth))
            }
        }

        if (sourceBottom < resultHeight - 1) {
            val distance = sourceBottom - y
            if (distance in 0 until blendWidth) {
                amount = max(amount, 1f - smoothStep(distance, blendWidth))
            }
        }

        return amount
    }

    private fun smoothStep(
        position: Int,
        length: Int
    ): Float {
        val x = (position.toFloat() / (length - 1).coerceAtLeast(1)).coerceIn(0f, 1f)
        return x * x * (3f - 2f * x)
    }

    private fun mixColors(
        from: Int,
        to: Int,
        amount: Float
    ): Int {
        val keep = 1f - amount
        val alpha = (keep * (from ushr 24 and 0xFF) + amount * (to ushr 24 and 0xFF)).toInt()
        val red = (keep * (from ushr 16 and 0xFF) + amount * (to ushr 16 and 0xFF)).toInt()
        val green = (keep * (from ushr 8 and 0xFF) + amount * (to ushr 8 and 0xFF)).toInt()
        val blue = (keep * (from and 0xFF) + amount * (to and 0xFF)).toInt()

        return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
    }

    private const val MinPatchSize = 7
    private const val MaxPatchSize = 120
    private const val MediumPatchSize = 15
    private const val LargePatchSize = 25
    private const val PatchDivisor = 8
    private const val RequiredOverlapDivisor = 5
    private const val ExtraPassCount = 6
    private const val MinPassCount = 8
    private const val MaxPassCount = 48
    private const val RelaxedPassCount = 2
    private const val SmallImageArea = 128 * 128
    private const val SmallCandidateCount = 32
    private const val DefaultCandidateCount = 64
    private const val LargeCandidateCount = 64
    private const val SourceBandPatchMultiplier = 3
    private const val RedWeight = 3
    private const val GreenWeight = 4
    private const val BlueWeight = 2
    private const val AlphaWeight = 2
    private const val SeedSourceWidthFactor = 73856093
    private const val SeedSourceHeightFactor = 19349663
    private const val SeedResultWidthFactor = 83492791
    private const val SeedResultHeightFactor = 265443576

}
