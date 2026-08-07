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

import ai.onnxruntime.OnnxJavaType
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.TensorInfo
import android.graphics.Bitmap
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min

object MiganOutpaintProcessor {
    private const val OVERLAP = 64
    private val mutex = Mutex()

    private const val TILE_SIZE = 512

    suspend fun process(
        session: OrtSession,
        source: Bitmap,
        targetWidth: Int,
        targetHeight: Int,
        offsetX: Int,
        offsetY: Int,
        onProgress: (completed: Int, total: Int) -> Unit = { _, _ -> }
    ): Bitmap = mutex.withLock {
        validateSession(session)
        val passes = OutpaintPassPlanner().plan(
            targetWidth = targetWidth,
            targetHeight = targetHeight,
            sourceWidth = source.width,
            sourceHeight = source.height,
            offsetX = offsetX,
            offsetY = offsetY
        )
        val pixels = IntArray(targetWidth * targetHeight)
        val sourcePixels = IntArray(source.width * source.height)
        source.getPixels(sourcePixels, 0, source.width, 0, 0, source.width, source.height)
        copySource(sourcePixels, source.width, source.height, pixels, targetWidth, offsetX, offsetY)

        val known = ByteArray(pixels.size)
        markKnown(
            known,
            targetWidth,
            OutpaintRect(offsetX, offsetY, offsetX + source.width, offsetY + source.height)
        )
        val total = passes.sumOf { it.regions.size }
        var completed = 0
        onProgress(completed, total)

        passes.forEach { pass ->
            currentCoroutineContext().ensureActive()
            val sideResults = listOfNotNull(
                pass.left?.let { OutpaintSide.Left to it },
                pass.right?.let { OutpaintSide.Right to it }
            ).map { (side, region) ->
                generateRegion(
                    session = session,
                    pixels = pixels,
                    known = known,
                    canvasWidth = targetWidth,
                    canvasHeight = targetHeight,
                    region = region,
                    knownBounds = pass.knownBefore,
                    side = side
                )
            }
            sideResults.forEach { it.commit(pixels, known, targetWidth) }
            completed += sideResults.size
            onProgress(completed, total)

            val verticalKnownBounds = OutpaintRect(
                left = pass.knownAfter.left,
                top = pass.knownBefore.top,
                right = pass.knownAfter.right,
                bottom = pass.knownBefore.bottom
            )
            val verticalResults = listOfNotNull(
                pass.top?.let { OutpaintSide.Top to it },
                pass.bottom?.let { OutpaintSide.Bottom to it }
            ).map { (side, region) ->
                generateRegion(
                    session = session,
                    pixels = pixels,
                    known = known,
                    canvasWidth = targetWidth,
                    canvasHeight = targetHeight,
                    region = region,
                    knownBounds = verticalKnownBounds,
                    side = side
                )
            }
            verticalResults.forEach { it.commit(pixels, known, targetWidth) }
            completed += verticalResults.size
            onProgress(completed, total)
        }

        copySource(sourcePixels, source.width, source.height, pixels, targetWidth, offsetX, offsetY)
        Bitmap.createBitmap(pixels, targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
    }

    private fun validateSession(session: OrtSession) {
        require(session.inputInfo.size == 2) { "MI-GAN model must have exactly two inputs" }
        require(session.outputInfo.size == 1) { "MI-GAN model must have exactly one output" }
        val image = session.inputInfo["image"]?.info as? TensorInfo
            ?: error("MI-GAN model has no image tensor")
        val mask = session.inputInfo["mask"]?.info as? TensorInfo
            ?: error("MI-GAN model has no mask tensor")
        val output = session.outputInfo.values.single().info as? TensorInfo
            ?: error("MI-GAN output is not a tensor")
        require(image.type == OnnxJavaType.UINT8 && image.shape.size == 4 && image.shape[1] == 3L) {
            "MI-GAN image input must be uint8 NCHW with 3 channels"
        }
        require(mask.type == OnnxJavaType.UINT8 && mask.shape.size == 4 && mask.shape[1] == 1L) {
            "MI-GAN mask input must be uint8 NCHW with 1 channel"
        }
        require(output.type == OnnxJavaType.UINT8 && output.shape.size == 4 && output.shape[1] == 3L) {
            "MI-GAN output must be uint8 NCHW with 3 channels"
        }
    }

    private suspend fun generateRegion(
        session: OrtSession,
        pixels: IntArray,
        known: ByteArray,
        canvasWidth: Int,
        canvasHeight: Int,
        region: OutpaintRect,
        knownBounds: OutpaintRect,
        side: OutpaintSide
    ): RegionResult {
        val horizontal = side == OutpaintSide.Top || side == OutpaintSide.Bottom
        val positions = tilePositions(
            start = if (horizontal) region.left else region.top,
            end = if (horizontal) region.right else region.bottom,
            tileSize = TILE_SIZE,
            overlap = OVERLAP
        )
        val result = RegionResult(region)

        for (position in positions) {
            currentCoroutineContext().ensureActive()
            val tileLeft = if (horizontal) {
                position
            } else {
                contextOrigin(side, region.left, region.right, canvasWidth)
            }
            val tileTop = if (horizontal) {
                contextOrigin(side, region.top, region.bottom, canvasHeight)
            } else {
                position
            }
            val tile = runTile(
                session = session,
                pixels = pixels,
                known = known,
                canvasWidth = canvasWidth,
                canvasHeight = canvasHeight,
                tileLeft = tileLeft,
                tileTop = tileTop,
                region = region,
                knownBounds = knownBounds
            )
            result.accumulate(tile, tileLeft, tileTop)
        }
        return result
    }

    private fun contextOrigin(side: OutpaintSide, start: Int, end: Int, limit: Int): Int {
        val origin = when (side) {
            OutpaintSide.Left, OutpaintSide.Top -> start
            OutpaintSide.Right, OutpaintSide.Bottom -> end - TILE_SIZE
        }
        return origin.coerceIn(0, max(0, limit - TILE_SIZE))
    }

    private fun tilePositions(start: Int, end: Int, tileSize: Int, overlap: Int): List<Int> {
        val length = end - start
        if (length <= tileSize) return listOf(max(0, end - tileSize))
        val step = tileSize - overlap
        val result = mutableListOf(start)
        while (result.last() + tileSize < end) {
            result += min(result.last() + step, end - tileSize)
        }
        return result.distinct()
    }

    private fun runTile(
        session: OrtSession,
        pixels: IntArray,
        known: ByteArray,
        canvasWidth: Int,
        canvasHeight: Int,
        tileLeft: Int,
        tileTop: Int,
        region: OutpaintRect,
        knownBounds: OutpaintRect
    ): ByteArray {
        val size = TILE_SIZE
        val area = size * size
        val tilePixels = IntArray(area)
        val tileKnown = ByteArray(area)
        for (y in 0 until size) {
            for (x in 0 until size) {
                val actualX = tileLeft + x
                val actualY = tileTop + y
                val inCanvas = actualX in 0 until canvasWidth && actualY in 0 until canvasHeight
                val canvasIndex = if (inCanvas) actualY * canvasWidth + actualX else -1
                val isAlreadyKnown = inCanvas && known[canvasIndex].toInt() != 0
                val isTarget = inCanvas && !isAlreadyKnown && region.contains(actualX, actualY)
                val tileIndex = y * size + x

                if (isAlreadyKnown) {
                    tilePixels[tileIndex] = pixels[canvasIndex]
                    tileKnown[tileIndex] = 1
                } else if (isTarget) {
                    tileKnown[tileIndex] = 0
                } else {
                    val sampleX = actualX.coerceIn(knownBounds.left, knownBounds.right - 1)
                    val sampleY = actualY.coerceIn(knownBounds.top, knownBounds.bottom - 1)
                    tilePixels[tileIndex] = pixels[sampleY * canvasWidth + sampleX]
                    tileKnown[tileIndex] = 1
                }
            }
        }
        val imageBuffer = ByteBuffer.allocateDirect(area * 3).order(ByteOrder.nativeOrder())
        val maskBuffer = ByteBuffer.allocateDirect(area).order(ByteOrder.nativeOrder())
        for (channel in 0..2) {
            for (y in 0 until size) {
                for (x in 0 until size) {
                    val pixel = tilePixels[y * size + x]
                    imageBuffer.put(
                        when (channel) {
                            0 -> (pixel shr 16).toByte()
                            1 -> (pixel shr 8).toByte()
                            else -> pixel.toByte()
                        }
                    )
                }
            }
        }
        tileKnown.forEach { maskBuffer.put(if (it.toInt() != 0) 0xFF.toByte() else 0) }
        imageBuffer.rewind()
        maskBuffer.rewind()
        val shape = longArrayOf(1, 3, size.toLong(), size.toLong())
        val maskShape = longArrayOf(1, 1, size.toLong(), size.toLong())
        val environment = OrtEnvironment.getEnvironment()
        OnnxTensor.createTensor(environment, imageBuffer, shape, OnnxJavaType.UINT8).use { image ->
            OnnxTensor.createTensor(environment, maskBuffer, maskShape, OnnxJavaType.UINT8)
                .use { mask ->
                    session.run(mapOf("image" to image, "mask" to mask)).use { output ->
                        val tensor =
                            output[0] as? OnnxTensor ?: error("MI-GAN output is not a tensor")
                        val buffer = tensor.byteBuffer
                        return ByteArray(buffer.remaining()).also(buffer::get)
                    }
                }
        }
    }

    private fun OutpaintRect.contains(x: Int, y: Int): Boolean {
        return x in left until right && y in top until bottom
    }

    private enum class OutpaintSide {
        Left,
        Right,
        Top,
        Bottom
    }

    private class RegionResult(private val region: OutpaintRect) {
        private val red = FloatArray(region.width * region.height)
        private val green = FloatArray(red.size)
        private val blue = FloatArray(red.size)
        private val weights = FloatArray(red.size)

        fun accumulate(tile: ByteArray, tileLeft: Int, tileTop: Int) {
            val size = TILE_SIZE
            val area = size * size
            val left = max(region.left, tileLeft)
            val right = min(region.right, tileLeft + size)
            val top = max(region.top, tileTop)
            val bottom = min(region.bottom, tileTop + size)
            for (y in top until bottom) {
                for (x in left until right) {
                    val tx = x - tileLeft
                    val ty = y - tileTop
                    val tileIndex = ty * size + tx
                    val index = (y - region.top) * region.width + x - region.left
                    val weight = hann(tx, size) * hann(ty, size)
                    red[index] += (tile[tileIndex].toInt() and 0xFF) * weight
                    green[index] += (tile[area + tileIndex].toInt() and 0xFF) * weight
                    blue[index] += (tile[2 * area + tileIndex].toInt() and 0xFF) * weight
                    weights[index] += weight
                }
            }
        }

        fun commit(pixels: IntArray, known: ByteArray, canvasWidth: Int) {
            for (y in region.top until region.bottom) {
                for (x in region.left until region.right) {
                    val index = (y - region.top) * region.width + x - region.left
                    val weight = weights[index]
                    check(weight > 0f) { "Outpaint region contains an uncovered pixel" }
                    val r = (red[index] / weight).toInt().coerceIn(0, 255)
                    val g = (green[index] / weight).toInt().coerceIn(0, 255)
                    val b = (blue[index] / weight).toInt().coerceIn(0, 255)
                    val canvasIndex = y * canvasWidth + x
                    pixels[canvasIndex] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
                    known[canvasIndex] = 1
                }
            }
        }

        private fun hann(position: Int, size: Int): Float {
            val value = 0.5f - 0.5f * cos(2f * PI.toFloat() * position / (size - 1))
            return max(0.001f, value)
        }
    }

    private fun copySource(
        source: IntArray,
        sourceWidth: Int,
        sourceHeight: Int,
        target: IntArray,
        targetWidth: Int,
        offsetX: Int,
        offsetY: Int
    ) {
        repeat(sourceHeight) { y ->
            source.copyInto(
                destination = target,
                destinationOffset = (offsetY + y) * targetWidth + offsetX,
                startIndex = y * sourceWidth,
                endIndex = (y + 1) * sourceWidth
            )
        }
    }

    private fun markKnown(known: ByteArray, width: Int, rect: OutpaintRect) {
        for (y in rect.top until rect.bottom) {
            known.fill(1, y * width + rect.left, y * width + rect.right)
        }
    }
}
