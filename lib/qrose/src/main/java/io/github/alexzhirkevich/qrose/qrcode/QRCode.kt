package io.github.alexzhirkevich.qrose.qrcode

import io.github.alexzhirkevich.qrose.options.QrCodeMatrix
import io.github.alexzhirkevich.qrose.qrcode.QRCodeDataType.DEFAULT
import io.github.alexzhirkevich.qrose.qrcode.QRCodeDataType.NUMBERS
import io.github.alexzhirkevich.qrose.qrcode.QRCodeDataType.UPPER_ALPHA_NUM
import io.github.alexzhirkevich.qrose.qrcode.internals.BitBuffer
import io.github.alexzhirkevich.qrose.qrcode.internals.Polynomial
import io.github.alexzhirkevich.qrose.qrcode.internals.QR8BitByte
import io.github.alexzhirkevich.qrose.qrcode.internals.QRAlphaNum
import io.github.alexzhirkevich.qrose.qrcode.internals.QRCodeSetup.applyMaskPattern
import io.github.alexzhirkevich.qrose.qrcode.internals.QRCodeSetup.setupBottomLeftPositionProbePattern
import io.github.alexzhirkevich.qrose.qrcode.internals.QRCodeSetup.setupPositionAdjustPattern
import io.github.alexzhirkevich.qrose.qrcode.internals.QRCodeSetup.setupTimingPattern
import io.github.alexzhirkevich.qrose.qrcode.internals.QRCodeSetup.setupTopLeftPositionProbePattern
import io.github.alexzhirkevich.qrose.qrcode.internals.QRCodeSetup.setupTopRightPositionProbePattern
import io.github.alexzhirkevich.qrose.qrcode.internals.QRCodeSetup.setupTypeInfo
import io.github.alexzhirkevich.qrose.qrcode.internals.QRCodeSetup.setupTypeNumber
import io.github.alexzhirkevich.qrose.qrcode.internals.QRCodeSquare
import io.github.alexzhirkevich.qrose.qrcode.internals.QRData
import io.github.alexzhirkevich.qrose.qrcode.internals.QRNumber
import io.github.alexzhirkevich.qrose.qrcode.internals.QRUtil
import io.github.alexzhirkevich.qrose.qrcode.internals.RSBlock

internal class QRCode @JvmOverloads constructor(
    private val data: String,
    private val errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.M,
    private val dataType: QRCodeDataType = QRUtil.getDataType(data)
) {
    private val qrCodeData: QRData = when (dataType) {
        NUMBERS -> QRNumber(data)
        UPPER_ALPHA_NUM -> QRAlphaNum(data)
        DEFAULT -> QR8BitByte(data)
    }

    companion object {
        const val DEFAULT_CELL_SIZE = 1
        private const val PAD0 = 0xEC
        private const val PAD1 = 0x11

        /**
         * Calculates a suitable value for the [dataType] field for you.
         */
        @JvmStatic
        @JvmOverloads
        fun typeForDataAndECL(
            data: String,
            errorCorrectionLevel: ErrorCorrectionLevel,
            dataType: QRCodeDataType = QRUtil.getDataType(data)
        ): Int {
            val qrCodeData = when (dataType) {
                NUMBERS -> QRNumber(data)
                UPPER_ALPHA_NUM -> QRAlphaNum(data)
                DEFAULT -> QR8BitByte(data)
            }
            val dataLength = qrCodeData.length()

            for (typeNum in 1 until errorCorrectionLevel.maxTypeNum) {
                if (dataLength <= QRUtil.getMaxLength(typeNum, dataType, errorCorrectionLevel)) {
                    return typeNum
                }
            }

            return 40
        }
    }


    @JvmOverloads
    fun encode(
        type: Int = 0,
        maskPattern: MaskPattern?
    ): QrCodeMatrix {
        val finalType = if (type > 0) type else typeForDataAndECL(data, errorCorrectionLevel)

        val maskPattern = if (maskPattern == null) {
            var bestScore = Int.MAX_VALUE
            var bestPattern = MaskPattern.PATTERN000

            for (pattern in MaskPattern.entries) {
                val matrix = encode(type, pattern)
                val score = calculateMaskPenalty(matrix)
                if (score < bestScore) {
                    bestScore = score
                    bestPattern = pattern
                }
            }

            bestPattern
        } else maskPattern

        val moduleCount = finalType * 4 + 17
        val modules: Array<Array<QRCodeSquare?>> =
            Array(moduleCount) { Array(moduleCount) { null } }

        setupTopLeftPositionProbePattern(modules)
        setupTopRightPositionProbePattern(modules)
        setupBottomLeftPositionProbePattern(modules)

        setupPositionAdjustPattern(finalType, modules)
        setupTimingPattern(moduleCount, modules)
        setupTypeInfo(errorCorrectionLevel, maskPattern, moduleCount, modules)

        if (finalType >= 7) {
            setupTypeNumber(finalType, moduleCount, modules)
        }

        val data = try {
            createData(finalType)
        } catch (e: IllegalArgumentException) {
            if (finalType < 40) {
                return encode(finalType + 1, maskPattern)
            } else {
                throw e
            }
        }

        applyMaskPattern(data, maskPattern, moduleCount, modules)

        return QrCodeMatrix(
            modules.map { row ->
                row.map { pixel ->
                    if (pixel?.dark == true)
                        QrCodeMatrix.PixelType.DarkPixel
                    else QrCodeMatrix.PixelType.LightPixel
                }
            }
        )
    }

    private fun calculateMaskPenalty(matrix: QrCodeMatrix): Int {
        val size = matrix.size
        var penalty = 0

        // Rule 1: длинные ряды одинакового цвета (по горизонтали)
        for (y in 0 until size) {
            var runColor = matrix[0, y]
            var runLength = 1
            for (x in 1 until size) {
                val color = matrix[x, y]
                if (color == runColor) {
                    runLength++
                } else {
                    if (runLength >= 5) penalty += 3 + (runLength - 5)
                    runColor = color
                    runLength = 1
                }
            }
            if (runLength >= 5) penalty += 3 + (runLength - 5)
        }

        // Rule 1 (вертикали)
        for (x in 0 until size) {
            var runColor = matrix[x, 0]
            var runLength = 1
            for (y in 1 until size) {
                val color = matrix[x, y]
                if (color == runColor) {
                    runLength++
                } else {
                    if (runLength >= 5) penalty += 3 + (runLength - 5)
                    runColor = color
                    runLength = 1
                }
            }
            if (runLength >= 5) penalty += 3 + (runLength - 5)
        }

        // Rule 2: квадраты 2x2 одного цвета
        for (y in 0 until size - 1) {
            for (x in 0 until size - 1) {
                val color = matrix[x, y]
                if (color == matrix[x + 1, y] &&
                    color == matrix[x, y + 1] &&
                    color == matrix[x + 1, y + 1]
                ) {
                    penalty += 3
                }
            }
        }

        // Rule 3: паттерны типа 1011101 (finder-like)
        for (y in 0 until size) {
            for (x in 0 until size - 6) {
                if (matrix[x, y] == QrCodeMatrix.PixelType.DarkPixel &&
                    matrix[x + 1, y] == QrCodeMatrix.PixelType.LightPixel &&
                    matrix[x + 2, y] == QrCodeMatrix.PixelType.DarkPixel &&
                    matrix[x + 3, y] == QrCodeMatrix.PixelType.DarkPixel &&
                    matrix[x + 4, y] == QrCodeMatrix.PixelType.DarkPixel &&
                    matrix[x + 5, y] == QrCodeMatrix.PixelType.LightPixel &&
                    matrix[x + 6, y] == QrCodeMatrix.PixelType.DarkPixel
                ) {
                    penalty += 40
                }
            }
        }

        for (x in 0 until size) {
            for (y in 0 until size - 6) {
                if (matrix[x, y] == QrCodeMatrix.PixelType.DarkPixel &&
                    matrix[x, y + 1] == QrCodeMatrix.PixelType.LightPixel &&
                    matrix[x, y + 2] == QrCodeMatrix.PixelType.DarkPixel &&
                    matrix[x, y + 3] == QrCodeMatrix.PixelType.DarkPixel &&
                    matrix[x, y + 4] == QrCodeMatrix.PixelType.DarkPixel &&
                    matrix[x, y + 5] == QrCodeMatrix.PixelType.LightPixel &&
                    matrix[x, y + 6] == QrCodeMatrix.PixelType.DarkPixel
                ) {
                    penalty += 40
                }
            }
        }

        // Rule 4: баланс тёмных и светлых (должно быть около 50%)
        var darkCount = 0
        for (y in 0 until size) {
            for (x in 0 until size) {
                if (matrix[x, y] == QrCodeMatrix.PixelType.DarkPixel) darkCount++
            }
        }
        val total = size * size
        val percent = darkCount * 100 / total
        penalty += (kotlin.math.abs(percent - 50) / 5) * 10

        return penalty
    }

    private fun createData(type: Int): IntArray {
        val rsBlocks = RSBlock.getRSBlocks(type, errorCorrectionLevel)
        val buffer = BitBuffer()

        buffer.put(qrCodeData.dataType.value, 4)
        buffer.put(qrCodeData.length(), qrCodeData.getLengthInBits(type))
        qrCodeData.write(buffer)

        val totalDataCount = rsBlocks.sumOf { it.dataCount } * 8

        if (buffer.lengthInBits > totalDataCount) {
            throw IllegalArgumentException("Code length overflow (${buffer.lengthInBits} > $totalDataCount)")
        }

        if (buffer.lengthInBits + 4 <= totalDataCount) {
            buffer.put(0, 4)
        }

        while (buffer.lengthInBits % 8 != 0) {
            buffer.put(false)
        }

        while (true) {
            if (buffer.lengthInBits >= totalDataCount) {
                break
            }

            buffer.put(PAD0, 8)

            if (buffer.lengthInBits >= totalDataCount) {
                break
            }

            buffer.put(PAD1, 8)
        }

        return createBytes(buffer, rsBlocks)
    }

    private fun createBytes(buffer: BitBuffer, rsBlocks: Array<RSBlock>): IntArray {
        var offset = 0
        var maxDcCount = 0
        var maxEcCount = 0
        var totalCodeCount = 0
        val dcData = Array(rsBlocks.size) { IntArray(0) }
        val ecData = Array(rsBlocks.size) { IntArray(0) }

        rsBlocks.forEachIndexed { i, it ->
            val dcCount = it.dataCount
            val ecCount = it.totalCount - dcCount

            totalCodeCount += it.totalCount
            maxDcCount = maxDcCount.coerceAtLeast(dcCount)
            maxEcCount = maxEcCount.coerceAtLeast(ecCount)

            // Init dcData[i]
            dcData[i] = IntArray(dcCount) { idx -> 0xff and buffer.buffer[idx + offset] }
            offset += dcCount

            // Init ecData[i]
            val rsPoly = QRUtil.getErrorCorrectPolynomial(ecCount)
            val rawPoly = Polynomial(dcData[i], rsPoly.len() - 1)
            val modPoly = rawPoly.mod(rsPoly)
            val ecDataSize = rsPoly.len() - 1

            ecData[i] = IntArray(ecDataSize) { idx ->
                val modIndex = idx + modPoly.len() - ecDataSize
                if ((modIndex >= 0)) modPoly[modIndex] else 0
            }
        }

        var index = 0
        val data = IntArray(totalCodeCount)

        for (i in 0 until maxDcCount) {
            for (r in rsBlocks.indices) {
                if (i < dcData[r].size) {
                    data[index++] = dcData[r][i]
                }
            }
        }

        for (i in 0 until maxEcCount) {
            for (r in rsBlocks.indices) {
                if (i < ecData[r].size) {
                    data[index++] = ecData[r][i]
                }
            }
        }

        return data
    }

    override fun toString(): String =
        "QRCode(data=$data" +
                ", errorCorrectionLevel=$errorCorrectionLevel" +
                ", dataType=$dataType" +
                ", qrCodeData=${qrCodeData::class.simpleName}" +
                ")"
}

