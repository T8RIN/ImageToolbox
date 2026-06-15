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

package com.t8rin.neural_tools.scans

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.scale
import com.t8rin.neural_tools.NeuralTool
import java.nio.FloatBuffer
import kotlin.math.floor

object UvDocUnwarper : NeuralTool() {

    private const val TAG = "UvDocUnwarper"
    private const val INPUT_WIDTH = 496
    private const val INPUT_HEIGHT = 720
    private const val CHANNELS = 3

    fun unwarp(
        image: Bitmap,
        session: OrtSession
    ): Bitmap? = runCatching {
        val inputImage = image.scale(INPUT_WIDTH, INPUT_HEIGHT)
        val inputTensor = bitmapToTensor(inputImage)

        try {
            session.run(mapOf(session.inputNames.first() to inputTensor)).use { result ->
                val outputTensor = result[0] as? OnnxTensor
                    ?: throw IllegalStateException("Model output is not OnnxTensor")

                applyGrid(
                    source = image,
                    grid = outputTensorToGrid(outputTensor)
                )
            }
        } finally {
            inputTensor.close()
            if (inputImage !== image) inputImage.recycle()
        }
    }.onFailure {
        Log.e(TAG, "unwarp failure", it)
    }.getOrNull()

    private fun bitmapToTensor(
        bitmap: Bitmap
    ): OnnxTensor {
        val pixels = IntArray(INPUT_WIDTH * INPUT_HEIGHT)
        bitmap.getPixels(pixels, 0, INPUT_WIDTH, 0, 0, INPUT_WIDTH, INPUT_HEIGHT)

        val planeSize = INPUT_WIDTH * INPUT_HEIGHT
        val input = FloatArray(planeSize * CHANNELS)

        pixels.forEachIndexed { index, color ->
            input[index] = Color.red(color) / 255f
            input[planeSize + index] = Color.green(color) / 255f
            input[planeSize * 2 + index] = Color.blue(color) / 255f
        }

        return OnnxTensor.createTensor(
            OrtEnvironment.getEnvironment(),
            FloatBuffer.wrap(input),
            longArrayOf(1, CHANNELS.toLong(), INPUT_HEIGHT.toLong(), INPUT_WIDTH.toLong())
        )
    }

    private fun outputTensorToGrid(
        tensor: OnnxTensor
    ): Grid {
        val shape = tensor.info.shape
        val height = shape[2].toInt()
        val width = shape[3].toInt()
        val planeSize = width * height
        val data = FloatArray(tensor.floatBuffer.capacity())
        tensor.floatBuffer.get(data)

        return Grid(
            x = Array(height) { row ->
                FloatArray(width) { column ->
                    data[row * width + column]
                }
            },
            y = Array(height) { row ->
                FloatArray(width) { column ->
                    data[planeSize + row * width + column]
                }
            }
        )
    }

    private fun applyGrid(
        source: Bitmap,
        grid: Grid
    ): Bitmap {
        val width = source.width
        val height = source.height
        val sourcePixels = IntArray(width * height)
        val resultPixels = IntArray(width * height)
        source.getPixels(sourcePixels, 0, width, 0, 0, width, height)

        for (y in 0 until height) {
            val gridRow = if (height > 1) {
                y.toFloat() / (height - 1) * (grid.x.size - 1)
            } else {
                0f
            }

            for (x in 0 until width) {
                val gridColumn = if (width > 1) {
                    x.toFloat() / (width - 1) * (grid.x[0].size - 1)
                } else {
                    0f
                }
                val normalizedX = sampleGrid(grid.x, gridColumn, gridRow)
                val normalizedY = sampleGrid(grid.y, gridColumn, gridRow)
                val sourceX = (normalizedX + 1f) * 0.5f * (width - 1)
                val sourceY = (normalizedY + 1f) * 0.5f * (height - 1)

                resultPixels[y * width + x] = sampleSourcePixel(
                    pixels = sourcePixels,
                    width = width,
                    height = height,
                    x = sourceX,
                    y = sourceY
                )
            }
        }

        return Bitmap.createBitmap(
            resultPixels,
            width,
            height,
            Bitmap.Config.ARGB_8888
        )
    }

    private fun sampleGrid(
        grid: Array<FloatArray>,
        x: Float,
        y: Float
    ): Float {
        val x0 = floor(x).toInt().coerceIn(0, grid[0].lastIndex)
        val y0 = floor(y).toInt().coerceIn(0, grid.lastIndex)
        val x1 = (x0 + 1).coerceAtMost(grid[0].lastIndex)
        val y1 = (y0 + 1).coerceAtMost(grid.lastIndex)
        val tx = x - x0
        val ty = y - y0

        val top = grid[y0][x0] * (1f - tx) + grid[y0][x1] * tx
        val bottom = grid[y1][x0] * (1f - tx) + grid[y1][x1] * tx
        return top * (1f - ty) + bottom * ty
    }

    private fun sampleSourcePixel(
        pixels: IntArray,
        width: Int,
        height: Int,
        x: Float,
        y: Float
    ): Int {
        val safeX = x.coerceIn(0f, (width - 1).toFloat())
        val safeY = y.coerceIn(0f, (height - 1).toFloat())
        val x0 = floor(safeX).toInt()
        val y0 = floor(safeY).toInt()
        val x1 = (x0 + 1).coerceAtMost(width - 1)
        val y1 = (y0 + 1).coerceAtMost(height - 1)
        val tx = safeX - x0
        val ty = safeY - y0

        val topLeft = pixels[y0 * width + x0]
        val topRight = pixels[y0 * width + x1]
        val bottomLeft = pixels[y1 * width + x0]
        val bottomRight = pixels[y1 * width + x1]

        return Color.argb(
            bilinearChannel(topLeft, topRight, bottomLeft, bottomRight, tx, ty, Color::alpha),
            bilinearChannel(topLeft, topRight, bottomLeft, bottomRight, tx, ty, Color::red),
            bilinearChannel(topLeft, topRight, bottomLeft, bottomRight, tx, ty, Color::green),
            bilinearChannel(topLeft, topRight, bottomLeft, bottomRight, tx, ty, Color::blue)
        )
    }

    private fun bilinearChannel(
        topLeft: Int,
        topRight: Int,
        bottomLeft: Int,
        bottomRight: Int,
        tx: Float,
        ty: Float,
        channel: (Int) -> Int
    ): Int {
        val top = channel(topLeft) * (1f - tx) + channel(topRight) * tx
        val bottom = channel(bottomLeft) * (1f - tx) + channel(bottomRight) * tx
        return (top * (1f - ty) + bottom * ty).toInt().coerceIn(0, 255)
    }

    private data class Grid(
        val x: Array<FloatArray>,
        val y: Array<FloatArray>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Grid

            if (!x.contentDeepEquals(other.x)) return false
            if (!y.contentDeepEquals(other.y)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = x.contentDeepHashCode()
            result = 31 * result + y.contentDeepHashCode()
            return result
        }
    }
}