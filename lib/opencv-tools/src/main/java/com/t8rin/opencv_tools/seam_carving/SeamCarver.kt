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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
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

    suspend fun distort(
        bitmap: Bitmap,
        distortionPercent: Float,
        maxInputSide: Int = 512
    ): Bitmap = coroutineScope {
        ensureActive()
        val amount = distortionPercent.coerceIn(0f, 95f)
        if (amount <= 0f) return@coroutineScope bitmap

        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        val targetScale = 1f - amount / 100f

        var mat = bitmap.toMat()
        val restored = Mat()
        try {
            val resized = mat.resizeToFit(maxInputSide)
            if (resized !== mat) mat.release()
            mat = resized
            val targetWidth = (mat.cols() * targetScale).roundToInt().coerceAtLeast(1)
            val targetHeight = (mat.rows() * targetScale).roundToInt().coerceAtLeast(1)
            ensureActive()
            mat = carveMat(mat, targetWidth, targetHeight)
            Imgproc.resize(
                mat, restored,
                Size(originalWidth.toDouble(), originalHeight.toDouble()),
                0.0, 0.0, Imgproc.INTER_CUBIC
            )
            restored.toBitmap()
        } finally {
            mat.release()
            restored.release()
        }
    }

    /**
     * Main entry:
     * input: bitmap, desiredWidth, desiredHeight
     * returns: processed Bitmap. Smaller dimensions are reached by removing seams, larger dimensions
     * by inserting seams.
     */
    suspend fun carve(
        bitmap: Bitmap,
        desiredWidth: Int,
        desiredHeight: Int,
        protectionMask: Bitmap? = null,
        useBackwardEnergy: Boolean = false,
        useMaskAsRemoval: Boolean = false,
    ): Bitmap = coroutineScope {
        ensureActive()
        val mat = bitmap.toMat()
        var maskMat: Mat? = null
        try {
            maskMat = protectionMask?.toMat()?.resizeTo(mat.cols(), mat.rows())
            carveMat(
                mat = mat,
                desiredWidth = desiredWidth,
                desiredHeight = desiredHeight,
                protectionMask = maskMat,
                useBackwardEnergy = useBackwardEnergy,
                useMaskAsRemoval = useMaskAsRemoval,
            ).toBitmap()
        } finally {
            mat.release()
            maskMat?.release()
        }
    }

    private suspend fun carveMat(
        mat: Mat,
        desiredWidth: Int,
        desiredHeight: Int,
        protectionMask: Mat? = null,
        useBackwardEnergy: Boolean = false,
        useMaskAsRemoval: Boolean = false,
    ): Mat = coroutineScope {
        var current = mat
        var currentMask = protectionMask
        val targetW = desiredWidth.coerceAtLeast(1)
        val targetH = desiredHeight.coerceAtLeast(1)

        try {
            if (useMaskAsRemoval) {
                currentMask?.takeIf { hasMaskPixels(it) }?.let {
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

            transposed
        } catch (t: Throwable) {
            current.release()
            currentMask?.release()
            throw t
        }
    }

    private suspend fun resizeWidthWithSeams(
        src: Mat,
        targetWidth: Int,
        mask: Mat?,
        useBackwardEnergy: Boolean
    ): SeamResizeResult = coroutineScope {
        var current = src
        var currentMask = mask

        try {
            while (current.cols() > targetWidth) {
                ensureActive()
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
                ensureActive()
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

            SeamResizeResult(
                image = current,
                mask = currentMask
            )
        } catch (t: Throwable) {
            current.release()
            currentMask?.release()
            throw t
        }
    }

    private fun Mat.maxInsertionBatchSize(): Int {
        if (cols() <= 1) return 1

        return (cols() * INSERTION_BATCH_RATIO)
            .roundToInt()
            .coerceIn(1, cols() - 1)
    }

    private suspend fun findVerticalSeamsForInsertion(
        src: Mat,
        mask: Mat?,
        count: Int,
        useBackwardEnergy: Boolean
    ): List<IntArray> = coroutineScope {
        if (count <= 0) return@coroutineScope emptyList()
        if (src.cols() <= 1) {
            return@coroutineScope List(count) { IntArray(src.rows()) }
        }

        var working = src.clone()
        var workingMask = mask?.clone()
        val columnIndexes = Array(src.rows()) {
            MutableList(src.cols()) { column -> column }
        }
        val seams = mutableListOf<IntArray>()

        try {
            repeat(count.coerceAtMost(src.cols() - 1)) {
                ensureActive()
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
                    ensureActive()
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

            seams
        } finally {
            working.release()
            workingMask?.release()
        }
    }

    private suspend fun removeMaskedArea(
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

        var current = src
        var currentMask = mask
        try {
            val transposedSrc = transposeMat(current)
            current.release()
            current = transposedSrc
            val transposedMask = transposeMat(currentMask)
            currentMask.release()
            currentMask = transposedMask
            val result = removeMaskWithVerticalSeams(current, currentMask, useBackwardEnergy)
            current = result.image
            currentMask = result.mask
            val restoredImage = transposeMat(current)
            current.release()
            current = restoredImage
            val restoredMask = transposeMat(currentMask)
            currentMask.release()
            currentMask = restoredMask
            return SeamMaskRemovalResult(current, currentMask)
        } catch (t: Throwable) {
            current.release()
            currentMask.release()
            throw t
        }
    }

    private suspend fun removeMaskWithVerticalSeams(
        src: Mat,
        mask: Mat,
        useBackwardEnergy: Boolean,
    ): SeamMaskRemovalResult = coroutineScope {
        var current = src
        var currentMask = mask

        try {
            while (current.cols() > 1 && hasMaskPixels(currentMask)) {
                ensureActive()
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

            SeamMaskRemovalResult(
                image = current,
                mask = currentMask
            )
        } catch (t: Throwable) {
            current.release()
            currentMask.release()
            throw t
        }
    }

    private fun Mat.resizeTo(width: Int, height: Int): Mat {
        if (cols() == width && rows() == height) return this

        val resized = Mat()
        try {
            Imgproc.resize(
                this,
                resized,
                Size(width.toDouble(), height.toDouble()),
                0.0,
                0.0,
                Imgproc.INTER_NEAREST
            )
            return resized
        } catch (t: Throwable) {
            resized.release()
            throw t
        } finally {
            release()
        }
    }

    private fun Mat.resizeToFit(maxSide: Int): Mat {
        val currentMaxSide = max(cols(), rows())
        if (maxSide !in 1..<currentMaxSide) return this

        val scale = maxSide.toDouble() / currentMaxSide
        val resized = Mat()
        try {
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
        } catch (t: Throwable) {
            resized.release()
            throw t
        }
    }

    private fun transposeMat(src: Mat): Mat {
        val dst = Mat()
        try {
            Core.transpose(src, dst)
            return dst
        } catch (t: Throwable) {
            dst.release()
            throw t
        }
    }

    private suspend fun findVerticalSeam(
        src: Mat,
        protectionMask: Mat?,
        useBackwardEnergy: Boolean,
        useMaskAsRemoval: Boolean,
    ): IntArray {
        if (src.cols() <= 1) return IntArray(src.rows())

        return if (useBackwardEnergy) {
            val energy = computeBackwardEnergy(src)

            try {
                if (useMaskAsRemoval) {
                    protectionMask?.let { applyRemovalMaskEnergy(energy, it) }
                    findLowestEnergyVerticalSeam(energy)
                } else {
                    protectionMask?.let {
                        findLowestEnergyVerticalSeamAvoidingMask(
                            energy = energy,
                            protectionMask = it
                        )
                    } ?: run {
                        protectionMask?.let { addProtectionMaskEnergy(energy, it) }
                        findLowestEnergyVerticalSeam(energy)
                    }
                }
            } finally {
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

    private suspend fun findLowestEnergyVerticalSeamAvoidingMask(
        energy: Mat,
        protectionMask: Mat
    ): IntArray? = coroutineScope {
        val rows = energy.rows()
        val cols = energy.cols()
        val protectedPixels = protectionMask.toBinaryMaskPixels()

        fun isProtected(row: Int, col: Int): Boolean {
            return (protectedPixels[row * cols + col].toInt() and 0xFF) > 0
        }

        var previous = DoubleArray(cols)
        var current = DoubleArray(cols)
        val rowEnergy = DoubleArray(cols)
        val backtrack = ByteArray(rows * cols)

        energy.get(0, 0, rowEnergy)
        for (col in 0 until cols) {
            ensureActive()
            previous[col] = if (isProtected(0, col)) {
                Double.POSITIVE_INFINITY
            } else {
                rowEnergy[col]
            }
        }

        for (row in 1 until rows) {
            ensureActive()
            energy.get(row, 0, rowEnergy)

            for (col in 0 until cols) {
                if (isProtected(row, col)) {
                    current[col] = Double.POSITIVE_INFINITY
                    backtrack[row * cols + col] = 0
                    continue
                }

                var minPrev = previous[col]
                var bestColumn = col

                if (col > 0 && previous[col - 1] < minPrev) {
                    minPrev = previous[col - 1]
                    bestColumn = col - 1
                }

                if (col < cols - 1 && previous[col + 1] < minPrev) {
                    minPrev = previous[col + 1]
                    bestColumn = col + 1
                }

                current[col] = if (minPrev.isInfinite()) {
                    Double.POSITIVE_INFINITY
                } else {
                    rowEnergy[col] + minPrev
                }

                backtrack[row * cols + col] = (bestColumn - col).toByte()
            }

            val swap = previous
            previous = current
            current = swap
        }

        var minCol = -1
        var minVal = Double.POSITIVE_INFINITY

        for (col in 0 until cols) {
            ensureActive()
            if (previous[col] < minVal) {
                minVal = previous[col]
                minCol = col
            }
        }

        if (minCol < 0 || minVal.isInfinite()) return@coroutineScope null

        val seam = IntArray(rows)
        var column = minCol

        for (row in rows - 1 downTo 0) {
            ensureActive()
            seam[row] = column
            if (row > 0) column += backtrack[row * cols + column]
        }

        seam
    }

    /**
     * Backward energy map using gradient magnitude (Sobel).
     */
    private fun computeBackwardEnergy(src: Mat): Mat {
        val gray = src.toGray()
        val gradX = Mat()
        val gradY = Mat()
        val gradXSq = Mat()
        val gradYSq = Mat()
        val energy = Mat()
        try {
            gray.convertTo(gray, CvType.CV_64F)
            Imgproc.Sobel(gray, gradX, CvType.CV_64F, 1, 0, 3, 1.0, 0.0, Core.BORDER_DEFAULT)
            Imgproc.Sobel(gray, gradY, CvType.CV_64F, 0, 1, 3, 1.0, 0.0, Core.BORDER_DEFAULT)
            Core.multiply(gradX, gradX, gradXSq)
            Core.multiply(gradY, gradY, gradYSq)
            Core.add(gradXSq, gradYSq, energy)
            Core.sqrt(energy, energy)
            return energy
        } catch (t: Throwable) {
            energy.release()
            throw t
        } finally {
            gray.release()
            gradX.release()
            gradY.release()
            gradXSq.release()
            gradYSq.release()
        }
    }

    private suspend fun findForwardVerticalSeam(
        src: Mat,
        protectionMask: Mat?,
        useMaskAsRemoval: Boolean,
    ): IntArray {
        val hardProtectedSeam = if (!useMaskAsRemoval && protectionMask != null) {
            findForwardVerticalSeamInternal(
                src = src,
                protectionMask = protectionMask,
                useMaskAsRemoval = false,
                forbidProtectionMask = true
            )
        } else {
            null
        }

        return hardProtectedSeam ?: findForwardVerticalSeamInternal(
            src = src,
            protectionMask = protectionMask,
            useMaskAsRemoval = useMaskAsRemoval,
            forbidProtectionMask = false
        ) ?: IntArray(src.rows())
    }

    private suspend fun findForwardVerticalSeamInternal(
        src: Mat,
        protectionMask: Mat?,
        useMaskAsRemoval: Boolean,
        forbidProtectionMask: Boolean,
    ): IntArray? = coroutineScope {
        ensureActive()
        val gray = src.toGray()
        val rows = gray.rows()
        val cols = gray.cols()
        val pixels = ByteArray(rows * cols)
        try {
            gray.get(0, 0, pixels)
        } finally {
            gray.release()
        }

        val maskEnergy = if (forbidProtectionMask) null
        else protectionMask?.createMaskEnergy(useMaskAsRemoval)
        val protectedPixels = if (forbidProtectionMask) {
            protectionMask?.toBinaryMaskPixels()
        } else {
            null
        }

        fun pixelAt(row: Int, col: Int): Double {
            val safeRow = row.coerceIn(0, rows - 1)
            val safeCol = col.coerceIn(0, cols - 1)
            return (pixels[safeRow * cols + safeCol].toInt() and 0xFF).toDouble()
        }

        fun isProtected(row: Int, col: Int): Boolean {
            return protectedPixels?.let {
                (it[row * cols + col].toInt() and 0xFF) > 0
            } ?: false
        }

        var previous = DoubleArray(cols) { col ->
            if (isProtected(0, col)) {
                Double.POSITIVE_INFINITY
            } else {
                maskEnergy?.get(col) ?: 0.0
            }
        }

        var current = DoubleArray(cols)
        val backtrack = ByteArray(rows * cols)

        for (row in 1 until rows) {
            ensureActive()
            for (col in 0 until cols) {
                if (isProtected(row, col)) {
                    current[col] = Double.POSITIVE_INFINITY
                    backtrack[row * cols + col] = 0
                    continue
                }

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

                current[col] = if (best.isInfinite()) {
                    Double.POSITIVE_INFINITY
                } else {
                    best + (maskEnergy?.get(row * cols + col) ?: 0.0)
                }

                backtrack[row * cols + col] = (bestColumn - col).toByte()
            }

            val swap = previous
            previous = current
            current = swap
        }

        var minCol = -1
        var minVal = Double.POSITIVE_INFINITY

        for (col in 0 until cols) {
            ensureActive()
            if (previous[col] < minVal) {
                minVal = previous[col]
                minCol = col
            }
        }

        if (minCol < 0 || minVal.isInfinite()) return@coroutineScope null

        val seam = IntArray(rows)
        var column = minCol

        for (row in rows - 1 downTo 0) {
            ensureActive()
            seam[row] = column
            if (row > 0) column += backtrack[row * cols + column]
        }

        seam
    }

    private suspend fun Mat.toBinaryMaskPixels(): ByteArray = coroutineScope {
        val binaryMask = toBinaryMask(this@toBinaryMaskPixels)
        try {
            ensureActive()
            val pixels = ByteArray(binaryMask.rows() * binaryMask.cols())
            binaryMask.get(0, 0, pixels)
            pixels
        } finally {
            binaryMask.release()
        }

    }

    private fun addProtectionMaskEnergy(energy: Mat, protectionMask: Mat) {
        val grayMask = toBinaryMask(protectionMask)
        try {
            Core.add(energy, Scalar(PROTECTED_MASK_ENERGY), energy, grayMask)
        } finally {
            grayMask.release()
        }
    }

    private fun applyRemovalMaskEnergy(energy: Mat, removalMask: Mat) {
        val grayMask = toBinaryMask(removalMask)
        try {
            energy.setTo(Scalar(REMOVAL_MASK_ENERGY), grayMask)
        } finally {
            grayMask.release()
        }
    }

    private fun toBinaryMask(mask: Mat): Mat {
        val grayMask = mask.toGray()
        try {
            Imgproc.threshold(grayMask, grayMask, 0.0, 255.0, Imgproc.THRESH_BINARY)
            return grayMask
        } catch (t: Throwable) {
            grayMask.release()
            throw t
        }
    }

    private suspend fun Mat.createMaskEnergy(
        useMaskAsRemoval: Boolean
    ): DoubleArray = coroutineScope {
        val grayMask = toBinaryMask(this@createMaskEnergy)
        try {
            val rows = grayMask.rows()
            val cols = grayMask.cols()
            val rowPixels = ByteArray(cols)
            val energy = DoubleArray(rows * cols)
            val selectedEnergy = if (useMaskAsRemoval) REMOVAL_MASK_ENERGY
            else PROTECTED_MASK_ENERGY
            for (row in 0 until rows) {
                ensureActive()
                grayMask.get(row, 0, rowPixels)
                for (col in 0 until cols) {
                    if ((rowPixels[col].toInt() and 0xFF) > 0) {
                        energy[row * cols + col] = selectedEnergy
                    }
                }
            }
            energy
        } finally {
            grayMask.release()
        }
    }

    private fun Mat.toGray(): Mat {
        val gray = Mat()
        try {
            when (channels()) {
                1 -> copyTo(gray)
                3 -> Imgproc.cvtColor(this, gray, Imgproc.COLOR_RGB2GRAY)
                else -> Imgproc.cvtColor(this, gray, Imgproc.COLOR_RGBA2GRAY)
            }
            return gray
        } catch (t: Throwable) {
            gray.release()
            throw t
        }
    }

    private fun hasMaskPixels(mask: Mat): Boolean {
        val grayMask = toBinaryMask(mask)
        try {
            return Core.countNonZero(grayMask) > 0
        } finally {
            grayMask.release()
        }
    }

    private suspend fun seamHitsMask(mask: Mat, seam: IntArray): Boolean = coroutineScope {
        val grayMask = toBinaryMask(mask)
        try {
            val value = ByteArray(1)
            for (row in 0 until grayMask.rows()) {
                ensureActive()
                grayMask.get(row, seam[row], value)
                if ((value[0].toInt() and 0xFF) > 0) return@coroutineScope true
            }
            false
        } finally {
            grayMask.release()
        }
    }

    private suspend fun findMaskBounds(mask: Mat): MaskBounds? = coroutineScope {
        val grayMask = toBinaryMask(mask)
        try {
            val cols = grayMask.cols()
            val rowPixels = ByteArray(cols)
            var left = cols
            var right = -1
            var top = grayMask.rows()
            var bottom = -1
            for (row in 0 until grayMask.rows()) {
                ensureActive()
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
            if (right < left || bottom < top) return@coroutineScope null
            MaskBounds(width = right - left + 1, height = bottom - top + 1)
        } finally {
            grayMask.release()
        }
    }

    /**
     * Find vertical seam with minimum cumulative energy.
     */
    private suspend fun findLowestEnergyVerticalSeam(energy: Mat): IntArray = coroutineScope {
        val rows = energy.rows()
        val cols = energy.cols()

        var previous = DoubleArray(cols)
        var current = DoubleArray(cols)
        val rowEnergy = DoubleArray(cols)
        val backtrack = ByteArray(rows * cols)

        energy.get(0, 0, previous)

        for (r in 1 until rows) {
            ensureActive()
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
                backtrack[r * cols + c] = (idx - c).toByte()
            }
            val swap = previous
            previous = current
            current = swap
        }

        var minCol = 0
        var minVal = previous[0]
        for (c in 1 until cols) {
            ensureActive()
            if (previous[c] < minVal) {
                minVal = previous[c]
                minCol = c
            }
        }

        val seam = IntArray(rows)
        var cur = minCol
        for (r in rows - 1 downTo 0) {
            ensureActive()
            seam[r] = cur
            if (r > 0) cur += backtrack[r * cols + cur]
        }

        seam
    }

    /**
     * Remove vertical seam from src and return new Mat with cols-1.
     */
    private suspend fun removeVerticalSeam(src: Mat, seam: IntArray): Mat = coroutineScope {
        val rows = src.rows()
        val cols = src.cols()
        val dst = Mat(rows, cols - 1, src.type())

        val channels = src.channels()
        val rowBuf = ByteArray(cols * channels)
        val outBuf = ByteArray((cols - 1) * channels)
        try {
            for (r in 0 until rows) {
                ensureActive()
                src.get(r, 0, rowBuf)
                val skip = seam[r] * channels
                rowBuf.copyInto(outBuf, endIndex = skip)
                rowBuf.copyInto(outBuf, destinationOffset = skip, startIndex = skip + channels)
                dst.put(r, 0, outBuf)
            }
            dst
        } catch (t: Throwable) {
            dst.release()
            throw t
        }
    }

    private suspend fun insertVerticalSeams(
        src: Mat,
        seams: List<IntArray>
    ): Mat = coroutineScope {
        val rows = src.rows()
        val cols = src.cols()
        val dst = Mat(rows, cols + seams.size, src.type())
        val channels = src.channels()
        val rowBuf = ByteArray(cols * channels)
        val outBuf = ByteArray((cols + seams.size) * channels)
        val rowSeams = IntArray(seams.size)
        try {
            for (row in 0 until rows) {
                ensureActive()
                seams.forEachIndexed { index, seam ->
                    rowSeams[index] = seam[row].coerceIn(0, cols - 1)
                }
                rowSeams.sort()
                src.get(row, 0, rowBuf)
                var srcOffset = 0
                var dstOffset = 0
                for (col in rowSeams) {
                    val end = (col + 1) * channels
                    rowBuf.copyInto(outBuf, dstOffset, srcOffset, end)
                    dstOffset += end - srcOffset
                    srcOffset = end
                    val neighbor = if (col < cols - 1) col + 1 else (col - 1).coerceAtLeast(0)
                    repeat(channels) { channel ->
                        outBuf[dstOffset++] = averageByte(
                            rowBuf[col * channels + channel],
                            rowBuf[neighbor * channels + channel]
                        )
                    }
                }
                rowBuf.copyInto(outBuf, dstOffset, srcOffset)
                dst.put(row, 0, outBuf)
            }
            dst
        } catch (t: Throwable) {
            dst.release()
            throw t
        }
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

}
