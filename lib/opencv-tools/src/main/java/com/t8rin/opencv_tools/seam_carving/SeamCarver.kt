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

package com.t8rin.opencv_tools.seam_carving

import android.graphics.Bitmap
import com.t8rin.opencv_tools.utils.OpenCV
import com.t8rin.opencv_tools.utils.toBitmap
import com.t8rin.opencv_tools.utils.toMat
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

object SeamCarver : OpenCV() {

    private const val PROTECTED_MASK_ENERGY = 1_000_000_000.0
    private const val REMOVAL_MASK_ENERGY = -1_000_000_000.0
    private const val INSERTION_BATCH_RATIO = 0.15

    fun distort(
        bitmap: Bitmap,
        distortionPercent: Float,
        maxInputSide: Int = 512
    ): Bitmap {
        val amount = distortionPercent.coerceIn(0f, 95f)
        if (amount <= 0f) return bitmap

        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        val targetScale = 1f - amount / 100f

        val originalMat = bitmap.toMat()
        var mat = originalMat.resizeToFit(maxInputSide)
        if (mat !== originalMat) {
            originalMat.release()
        }

        val targetWidth = (mat.cols() * targetScale).roundToInt().coerceAtLeast(1)
        val targetHeight = (mat.rows() * targetScale).roundToInt().coerceAtLeast(1)

        mat = carveMat(
            mat = mat,
            desiredWidth = targetWidth,
            desiredHeight = targetHeight
        )

        val restored = Mat()
        Imgproc.resize(
            mat,
            restored,
            Size(originalWidth.toDouble(), originalHeight.toDouble()),
            0.0,
            0.0,
            Imgproc.INTER_CUBIC
        )
        mat.release()

        return restored.toBitmap()
    }

    /**
     * Main entry:
     * input: bitmap, desiredWidth, desiredHeight
     * returns: processed Bitmap. Smaller dimensions are reached by removing seams, larger dimensions
     * by inserting seams.
     */
    fun carve(
        bitmap: Bitmap,
        desiredWidth: Int,
        desiredHeight: Int,
        protectionMask: Bitmap? = null,
        useBackwardEnergy: Boolean = false,
        useMaskAsRemoval: Boolean = false,
    ): Bitmap {
        val mat = bitmap.toMat()
        val maskMat = protectionMask?.toMat()?.resizeTo(
            width = mat.cols(),
            height = mat.rows()
        )

        return carveMat(
            mat = mat,
            desiredWidth = desiredWidth,
            desiredHeight = desiredHeight,
            protectionMask = maskMat,
            useBackwardEnergy = useBackwardEnergy,
            useMaskAsRemoval = useMaskAsRemoval,
        ).toBitmap()
    }

    private fun carveMat(
        mat: Mat,
        desiredWidth: Int,
        desiredHeight: Int,
        protectionMask: Mat? = null,
        useBackwardEnergy: Boolean = false,
        useMaskAsRemoval: Boolean = false,
    ): Mat {
        var current = mat
        var currentMask = protectionMask
        val targetW = desiredWidth.coerceAtLeast(1)
        val targetH = desiredHeight.coerceAtLeast(1)

        if (useMaskAsRemoval) {
            currentMask?.takeIf(::hasMaskPixels)?.let {
                removeMaskedArea(
                    src = current,
                    mask = it,
                    useBackwardEnergy = useBackwardEnergy
                ).let { result ->
                    current = result.image
                    result.mask.release()
                    currentMask = null
                }
            }
        }

        if (targetW > current.cols() || targetH > current.rows()) {
            return resizeWithAspectRetargeting(
                src = current,
                targetWidth = targetW,
                targetHeight = targetH,
                mask = currentMask,
                useBackwardEnergy = useBackwardEnergy
            )
        }

        resizeWidthWithSeams(
            src = current,
            targetWidth = targetW,
            mask = currentMask,
            useBackwardEnergy = useBackwardEnergy
        ).let {
            current = it.image
            currentMask = it.mask
        }

        var transposed = transposeMat(current)
        current.release()
        current = transposed

        currentMask = currentMask?.let {
            val transposedMask = transposeMat(it)
            it.release()
            transposedMask
        }

        resizeWidthWithSeams(
            src = current,
            targetWidth = targetH,
            mask = currentMask,
            useBackwardEnergy = useBackwardEnergy
        ).let {
            current = it.image
            currentMask = it.mask
        }
        transposed = transposeMat(current)
        current.release()
        currentMask?.release()

        return transposed
    }

    private fun resizeWithAspectRetargeting(
        src: Mat,
        targetWidth: Int,
        targetHeight: Int,
        mask: Mat?,
        useBackwardEnergy: Boolean
    ): Mat {
        var current = src
        var currentMask = mask
        val intermediateSize = chooseAspectRetargetSize(
            src = current,
            targetWidth = targetWidth,
            targetHeight = targetHeight,
            mask = currentMask
        )

        resizeWidthWithSeams(
            src = current,
            targetWidth = intermediateSize.width,
            mask = currentMask,
            useBackwardEnergy = useBackwardEnergy
        ).let {
            current = it.image
            currentMask = it.mask
        }

        var transposed = transposeMat(current)
        current.release()
        current = transposed

        currentMask = currentMask?.let {
            val transposedMask = transposeMat(it)
            it.release()
            transposedMask
        }

        resizeWidthWithSeams(
            src = current,
            targetWidth = intermediateSize.height,
            mask = currentMask,
            useBackwardEnergy = useBackwardEnergy
        ).let {
            current = it.image
            currentMask = it.mask
        }

        transposed = transposeMat(current)
        current.release()
        currentMask?.release()

        if (transposed.cols() == targetWidth && transposed.rows() == targetHeight) {
            return transposed
        }

        val scaled = Mat()
        Imgproc.resize(
            transposed,
            scaled,
            Size(targetWidth.toDouble(), targetHeight.toDouble()),
            0.0,
            0.0,
            Imgproc.INTER_CUBIC
        )
        transposed.release()

        return scaled
    }

    private fun chooseAspectRetargetSize(
        src: Mat,
        targetWidth: Int,
        targetHeight: Int,
        mask: Mat?
    ): RetargetSize {
        val targetAspectRatio = targetWidth.toDouble() / targetHeight
        val widthForCurrentHeight = (src.rows() * targetAspectRatio)
            .roundToInt()
            .coerceAtLeast(1)
        val heightForCurrentWidth = (src.cols() / targetAspectRatio)
            .roundToInt()
            .coerceAtLeast(1)
        val maskBounds = mask?.let(::findMaskBounds)
        val candidates = listOf(
            RetargetSize(
                width = widthForCurrentHeight,
                height = src.rows()
            ),
            RetargetSize(
                width = src.cols(),
                height = heightForCurrentWidth
            )
        )

        return candidates
            .filter { it.canKeepMask(src, maskBounds) }
            .minByOrNull { it.scoreAgainst(src) }
            ?: candidates.minBy { it.scoreAgainst(src) }
    }

    private fun resizeWidthWithSeams(
        src: Mat,
        targetWidth: Int,
        mask: Mat?,
        useBackwardEnergy: Boolean
    ): SeamResizeResult {
        var current = src
        var currentMask = mask

        while (current.cols() > targetWidth) {
            val seam = findVerticalSeam(
                src = current,
                protectionMask = currentMask,
                useBackwardEnergy = useBackwardEnergy,
                useMaskAsRemoval = false
            )

            val resized = removeVerticalSeam(current, seam)
            current.release()
            current = resized

            currentMask = currentMask?.let {
                val resizedMask = removeVerticalSeam(it, seam)
                it.release()
                resizedMask
            }
        }

        while (current.cols() < targetWidth) {
            val remaining = targetWidth - current.cols()
            val insertCount = current.maxInsertionBatchSize()
                .coerceAtMost(remaining)
            val seams = findVerticalSeamsForInsertion(
                src = current,
                mask = currentMask,
                count = insertCount,
                useBackwardEnergy = useBackwardEnergy
            )

            val resized = insertVerticalSeams(current, seams)
            current.release()
            current = resized

            currentMask = currentMask?.let {
                val resizedMask = insertVerticalSeams(it, seams)
                it.release()
                resizedMask
            }
        }

        return SeamResizeResult(
            image = current,
            mask = currentMask
        )
    }

    private fun Mat.maxInsertionBatchSize(): Int {
        if (cols() <= 1) return 1

        return (cols() * INSERTION_BATCH_RATIO)
            .roundToInt()
            .coerceIn(1, cols() - 1)
    }

    private fun findVerticalSeamsForInsertion(
        src: Mat,
        mask: Mat?,
        count: Int,
        useBackwardEnergy: Boolean
    ): List<IntArray> {
        if (count <= 0) return emptyList()
        if (src.cols() <= 1) {
            return List(count) { IntArray(src.rows()) }
        }

        var working = src.clone()
        var workingMask = mask?.clone()
        val columnIndexes = Array(src.rows()) {
            MutableList(src.cols()) { column -> column }
        }
        val seams = mutableListOf<IntArray>()

        repeat(count.coerceAtMost(src.cols() - 1)) {
            val seam = findVerticalSeam(
                src = working,
                protectionMask = workingMask,
                useBackwardEnergy = useBackwardEnergy,
                useMaskAsRemoval = false
            )
            seams += IntArray(src.rows()) { row ->
                columnIndexes[row][seam[row]]
            }

            for (row in 0 until src.rows()) {
                columnIndexes[row].removeAt(seam[row])
            }

            val resized = removeVerticalSeam(working, seam)
            working.release()
            working = resized

            workingMask = workingMask?.let {
                val resizedMask = removeVerticalSeam(it, seam)
                it.release()
                resizedMask
            }
        }

        working.release()
        workingMask?.release()

        return seams
    }

    private fun removeMaskedArea(
        src: Mat,
        mask: Mat,
        useBackwardEnergy: Boolean,
    ): SeamMaskRemovalResult {
        val bounds = findMaskBounds(mask) ?: return SeamMaskRemovalResult(src, mask)
        val removeVertically = bounds.width <= bounds.height || src.rows() <= 1

        if (removeVertically || src.cols() <= 1) {
            return removeMaskWithVerticalSeams(
                src = src,
                mask = mask,
                useBackwardEnergy = useBackwardEnergy
            )
        }

        val transposedSrc = transposeMat(src)
        src.release()
        val transposedMask = transposeMat(mask)
        mask.release()

        val result = removeMaskWithVerticalSeams(
            src = transposedSrc,
            mask = transposedMask,
            useBackwardEnergy = useBackwardEnergy
        )

        val restoredImage = transposeMat(result.image)
        result.image.release()

        val restoredMask = transposeMat(result.mask)
        result.mask.release()

        return SeamMaskRemovalResult(
            image = restoredImage,
            mask = restoredMask
        )
    }

    private fun removeMaskWithVerticalSeams(
        src: Mat,
        mask: Mat,
        useBackwardEnergy: Boolean,
    ): SeamMaskRemovalResult {
        var current = src
        var currentMask = mask

        while (current.cols() > 1 && hasMaskPixels(currentMask)) {
            val seam = findVerticalSeam(
                src = current,
                protectionMask = currentMask,
                useBackwardEnergy = useBackwardEnergy,
                useMaskAsRemoval = true,
            )

            if (!seamHitsMask(currentMask, seam)) break

            val resized = removeVerticalSeam(current, seam)
            current.release()
            current = resized

            val resizedMask = removeVerticalSeam(currentMask, seam)
            currentMask.release()
            currentMask = resizedMask
        }

        return SeamMaskRemovalResult(
            image = current,
            mask = currentMask
        )
    }

    private fun Mat.resizeTo(width: Int, height: Int): Mat {
        if (cols() == width && rows() == height) return this

        val resized = Mat()
        Imgproc.resize(
            this,
            resized,
            Size(width.toDouble(), height.toDouble()),
            0.0,
            0.0,
            Imgproc.INTER_NEAREST
        )
        release()
        return resized
    }

    private fun Mat.resizeToFit(maxSide: Int): Mat {
        val currentMaxSide = max(cols(), rows())
        if (maxSide !in 1..<currentMaxSide) return this

        val scale = maxSide.toDouble() / currentMaxSide
        val resized = Mat()
        Imgproc.resize(
            this,
            resized,
            Size(
                (cols() * scale).roundToInt().coerceAtLeast(1).toDouble(),
                (rows() * scale).roundToInt().coerceAtLeast(1).toDouble()
            ),
            0.0,
            0.0,
            Imgproc.INTER_AREA
        )
        return resized
    }

    private fun transposeMat(src: Mat): Mat {
        val dst = Mat()
        Core.transpose(src, dst)
        return dst
    }

    private fun findVerticalSeam(
        src: Mat,
        protectionMask: Mat?,
        useBackwardEnergy: Boolean,
        useMaskAsRemoval: Boolean,
    ): IntArray {
        if (src.cols() <= 1) return IntArray(src.rows())

        return if (useBackwardEnergy) {
            val energy = computeBackwardEnergy(src)
            protectionMask?.let { mask ->
                if (useMaskAsRemoval) {
                    applyRemovalMaskEnergy(energy, mask)
                } else {
                    addProtectionMaskEnergy(energy, mask)
                }
            }
            findLowestEnergyVerticalSeam(energy).also {
                energy.release()
            }
        } else {
            findForwardVerticalSeam(
                src = src,
                protectionMask = protectionMask,
                useMaskAsRemoval = useMaskAsRemoval
            )
        }
    }

    /**
     * Backward energy map using gradient magnitude (Sobel).
     */
    private fun computeBackwardEnergy(src: Mat): Mat {
        val gray = src.toGray()
        gray.convertTo(gray, CvType.CV_64F)

        val gradX = Mat()
        val gradY = Mat()
        Imgproc.Sobel(gray, gradX, CvType.CV_64F, 1, 0, 3, 1.0, 0.0, Core.BORDER_DEFAULT)
        Imgproc.Sobel(gray, gradY, CvType.CV_64F, 0, 1, 3, 1.0, 0.0, Core.BORDER_DEFAULT)

        val gradXSq = Mat()
        val gradYSq = Mat()
        Core.multiply(gradX, gradX, gradXSq)
        Core.multiply(gradY, gradY, gradYSq)

        val energy = Mat()
        Core.add(gradXSq, gradYSq, energy)
        Core.sqrt(energy, energy)

        gray.release()
        gradX.release()
        gradY.release()
        gradXSq.release()
        gradYSq.release()

        return energy
    }

    private fun findForwardVerticalSeam(
        src: Mat,
        protectionMask: Mat?,
        useMaskAsRemoval: Boolean,
    ): IntArray {
        val gray = src.toGray()
        gray.convertTo(gray, CvType.CV_64F)

        val rows = gray.rows()
        val cols = gray.cols()
        val pixels = DoubleArray(rows * cols)
        gray.get(0, 0, pixels)
        gray.release()

        val maskEnergy = protectionMask?.createMaskEnergy(useMaskAsRemoval)
        var previous = DoubleArray(cols) { col ->
            maskEnergy?.get(col) ?: 0.0
        }
        var current = DoubleArray(cols)
        val backtrack = Array(rows) { IntArray(cols) }

        fun pixelAt(row: Int, col: Int): Double {
            val safeRow = row.coerceIn(0, rows - 1)
            val safeCol = col.coerceIn(0, cols - 1)
            return pixels[safeRow * cols + safeCol]
        }

        for (row in 1 until rows) {
            for (col in 0 until cols) {
                val up = pixelAt(row - 1, col)
                val left = pixelAt(row, col - 1)
                val right = pixelAt(row, col + 1)

                val costUp = abs(right - left)
                val costLeft = abs(up - left) + costUp
                val costRight = abs(up - right) + costUp

                var best = previous[col] + costUp
                var bestColumn = col

                if (col > 0) {
                    val leftTotal = previous[col - 1] + costLeft
                    if (leftTotal < best) {
                        best = leftTotal
                        bestColumn = col - 1
                    }
                }

                if (col < cols - 1) {
                    val rightTotal = previous[col + 1] + costRight
                    if (rightTotal < best) {
                        best = rightTotal
                        bestColumn = col + 1
                    }
                }

                current[col] = best + (maskEnergy?.get(row * cols + col) ?: 0.0)
                backtrack[row][col] = bestColumn
            }
            val swap = previous
            previous = current
            current = swap
        }

        var minCol = 0
        var minVal = previous[0]
        for (col in 1 until cols) {
            if (previous[col] < minVal) {
                minVal = previous[col]
                minCol = col
            }
        }

        val seam = IntArray(rows)
        var column = minCol
        for (row in rows - 1 downTo 0) {
            seam[row] = column
            if (row > 0) column = backtrack[row][column]
        }

        return seam
    }

    private fun addProtectionMaskEnergy(energy: Mat, protectionMask: Mat) {
        val grayMask = toBinaryMask(protectionMask)
        grayMask.convertTo(grayMask, CvType.CV_64F, PROTECTED_MASK_ENERGY / 255.0)
        Core.add(energy, grayMask, energy)
        grayMask.release()
    }

    private fun applyRemovalMaskEnergy(energy: Mat, removalMask: Mat) {
        val grayMask = toBinaryMask(removalMask)
        val selector = Mat()
        grayMask.convertTo(selector, CvType.CV_64F, 1.0 / 255.0)

        val removalValues = Mat()
        selector.convertTo(removalValues, CvType.CV_64F, REMOVAL_MASK_ENERGY)

        val ones = Mat(energy.size(), CvType.CV_64F, Scalar(1.0))
        val inverseSelector = Mat()
        Core.subtract(ones, selector, inverseSelector)

        val keptEnergy = Mat()
        Core.multiply(energy, inverseSelector, keptEnergy)
        Core.add(keptEnergy, removalValues, energy)

        grayMask.release()
        selector.release()
        removalValues.release()
        ones.release()
        inverseSelector.release()
        keptEnergy.release()
    }

    private fun toBinaryMask(mask: Mat): Mat {
        val grayMask = mask.toGray()
        Imgproc.threshold(grayMask, grayMask, 0.0, 255.0, Imgproc.THRESH_BINARY)
        return grayMask
    }

    private fun Mat.createMaskEnergy(useMaskAsRemoval: Boolean): DoubleArray {
        val grayMask = toBinaryMask(this)
        val rows = grayMask.rows()
        val cols = grayMask.cols()
        val rowPixels = ByteArray(cols)
        val energy = DoubleArray(rows * cols)
        val selectedEnergy = if (useMaskAsRemoval) {
            REMOVAL_MASK_ENERGY
        } else {
            PROTECTED_MASK_ENERGY
        }

        for (row in 0 until rows) {
            grayMask.get(row, 0, rowPixels)
            for (col in 0 until cols) {
                if ((rowPixels[col].toInt() and 0xFF) > 0) {
                    energy[row * cols + col] = selectedEnergy
                }
            }
        }
        grayMask.release()

        return energy
    }

    private fun Mat.toGray(): Mat {
        val gray = Mat()
        when (channels()) {
            1 -> copyTo(gray)
            3 -> Imgproc.cvtColor(this, gray, Imgproc.COLOR_RGB2GRAY)
            else -> Imgproc.cvtColor(this, gray, Imgproc.COLOR_RGBA2GRAY)
        }
        return gray
    }

    private fun hasMaskPixels(mask: Mat): Boolean {
        val grayMask = toBinaryMask(mask)
        val hasPixels = Core.countNonZero(grayMask) > 0
        grayMask.release()
        return hasPixels
    }

    private fun seamHitsMask(mask: Mat, seam: IntArray): Boolean {
        val grayMask = toBinaryMask(mask)
        val value = ByteArray(1)
        for (row in 0 until grayMask.rows()) {
            grayMask.get(row, seam[row], value)
            if ((value[0].toInt() and 0xFF) > 0) {
                grayMask.release()
                return true
            }
        }
        grayMask.release()
        return false
    }

    private fun findMaskBounds(mask: Mat): MaskBounds? {
        val grayMask = toBinaryMask(mask)
        val cols = grayMask.cols()
        val rowPixels = ByteArray(cols)
        var left = cols
        var right = -1
        var top = grayMask.rows()
        var bottom = -1

        for (row in 0 until grayMask.rows()) {
            grayMask.get(row, 0, rowPixels)
            for (col in 0 until cols) {
                if ((rowPixels[col].toInt() and 0xFF) > 0) {
                    if (col < left) left = col
                    if (col > right) right = col
                    if (row < top) top = row
                    if (row > bottom) bottom = row
                }
            }
        }
        grayMask.release()

        if (right < left || bottom < top) return null

        return MaskBounds(
            width = right - left + 1,
            height = bottom - top + 1
        )
    }

    /**
     * Find vertical seam with minimum cumulative energy.
     */
    private fun findLowestEnergyVerticalSeam(energy: Mat): IntArray {
        val rows = energy.rows()
        val cols = energy.cols()

        var previous = DoubleArray(cols)
        var current = DoubleArray(cols)
        val rowEnergy = DoubleArray(cols)
        val backtrack = Array(rows) { IntArray(cols) }

        energy.get(0, 0, previous)

        for (r in 1 until rows) {
            energy.get(r, 0, rowEnergy)
            for (c in 0 until cols) {
                var minPrev = previous[c]
                var idx = c
                if (c > 0 && previous[c - 1] < minPrev) {
                    minPrev = previous[c - 1]
                    idx = c - 1
                }
                if (c < cols - 1 && previous[c + 1] < minPrev) {
                    minPrev = previous[c + 1]
                    idx = c + 1
                }
                current[c] = rowEnergy[c] + minPrev
                backtrack[r][c] = idx
            }
            val swap = previous
            previous = current
            current = swap
        }

        var minCol = 0
        var minVal = previous[0]
        for (c in 1 until cols) {
            if (previous[c] < minVal) {
                minVal = previous[c]
                minCol = c
            }
        }

        val seam = IntArray(rows)
        var cur = minCol
        for (r in rows - 1 downTo 0) {
            seam[r] = cur
            if (r > 0) cur = backtrack[r][cur]
        }
        return seam
    }

    /**
     * Remove vertical seam from src and return new Mat with cols-1.
     */
    private fun removeVerticalSeam(src: Mat, seam: IntArray): Mat {
        val rows = src.rows()
        val cols = src.cols()
        val dst = Mat(rows, cols - 1, src.type())

        val rowBuf = ByteArray(cols * src.channels())
        val outBuf = ByteArray((cols - 1) * src.channels())

        val channels = src.channels()

        for (r in 0 until rows) {
            src.get(r, 0, rowBuf)
            val skipCol = seam[r]
            var dstIdx = 0
            var srcIdx = 0
            for (c in 0 until cols) {
                if (c == skipCol) {
                    srcIdx += channels
                    continue
                }
                repeat(channels) {
                    outBuf[dstIdx++] = rowBuf[srcIdx++]
                }
            }
            dst.put(r, 0, outBuf)
        }
        return dst
    }

    private fun insertVerticalSeams(src: Mat, seams: List<IntArray>): Mat {
        val rows = src.rows()
        val cols = src.cols()
        val dst = Mat(rows, cols + seams.size, src.type())

        val channels = src.channels()
        val rowBuf = ByteArray(cols * channels)
        val outBuf = ByteArray((cols + seams.size) * channels)

        for (row in 0 until rows) {
            val rowSeams = seams
                .map { seam -> seam[row].coerceIn(0, cols - 1) }
                .sorted()
            src.get(row, 0, rowBuf)
            var dstIdx = 0
            var seamIndex = 0

            for (col in 0 until cols) {
                val srcIdx = col * channels

                repeat(channels) { ch ->
                    outBuf[dstIdx++] = rowBuf[srcIdx + ch]
                }

                while (seamIndex < rowSeams.size && rowSeams[seamIndex] == col) {
                    val neighborCol = if (col < cols - 1) {
                        col + 1
                    } else {
                        (col - 1).coerceAtLeast(0)
                    }
                    val neighborIdx = neighborCol * channels

                    repeat(channels) { ch ->
                        outBuf[dstIdx++] = averageByte(
                            first = rowBuf[srcIdx + ch],
                            second = rowBuf[neighborIdx + ch]
                        )
                    }
                    seamIndex++
                }
            }

            dst.put(row, 0, outBuf)
        }
        return dst
    }

    private fun averageByte(first: Byte, second: Byte): Byte {
        val firstValue = first.toInt() and 0xFF
        val secondValue = second.toInt() and 0xFF

        return ((firstValue + secondValue) / 2).toByte()
    }

    private data class SeamResizeResult(
        val image: Mat,
        val mask: Mat?
    )

    private data class SeamMaskRemovalResult(
        val image: Mat,
        val mask: Mat
    )

    private data class MaskBounds(
        val width: Int,
        val height: Int
    )

    private data class RetargetSize(
        val width: Int,
        val height: Int
    ) {
        fun scoreAgainst(src: Mat): Double {
            val removed = (src.cols() - width).coerceAtLeast(0) +
                    (src.rows() - height).coerceAtLeast(0)
            val inserted = (width - src.cols()).coerceAtLeast(0) +
                    (height - src.rows()).coerceAtLeast(0)

            return removed + inserted * 2.0
        }

        fun canKeepMask(src: Mat, maskBounds: MaskBounds?): Boolean {
            if (maskBounds == null) return true

            val minProtectedWidth = (maskBounds.width * 1.1).roundToInt().coerceAtLeast(1)
            val minProtectedHeight = (maskBounds.height * 1.1).roundToInt().coerceAtLeast(1)

            return (width >= src.cols() || width >= minProtectedWidth) &&
                    (height >= src.rows() || height >= minProtectedHeight)
        }
    }
}
