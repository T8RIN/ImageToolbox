package com.awxkee.jxlcoder

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.util.Size
import androidx.annotation.FloatRange
import androidx.annotation.Keep

@Keep
class JxlCoder {

    fun decode(byteArray: ByteArray): Bitmap {
        return decodeImpl(byteArray)
    }

    fun decodeSampled(byteArray: ByteArray, width: Int, height: Int): Bitmap {
        return decodeSampledImpl(byteArray, width, height)
    }

    /**
     * @param lossyLevel Sets the distance level for lossy compression: target max butteraugli
     *  * distance, lower = higher quality. Range: 0 .. 15.
     *  * 0.0 = mathematically lossless (however, use JxlEncoderSetFrameLossless
     *  * instead to use true lossless, as setting distance to 0 alone is not the only
     *  * requirement). 1.0 = visually lossless. Recommended range: 0.5 .. 3.0. Default
     *  * value: 1.0.
     */
    fun encode(
        bitmap: Bitmap,
        colorSpace: JxlColorSpace = JxlColorSpace.RGBA,
        compressionOption: JxlCompressionOption = JxlCompressionOption.LOSSY,
        @FloatRange(from = 0.0, to = 15.0) lossyLevel: Float = 1.0f,
    ): ByteArray {
        return encodeImpl(bitmap, colorSpace.cValue, compressionOption.cValue, lossyLevel)
    }

    fun getSize(byteArray: ByteArray): Size? {
        return getSizeImpl(byteArray)
    }

    private external fun getSizeImpl(byteArray: ByteArray): Size?

    private external fun decodeImpl(
        byteArray: ByteArray
    ): Bitmap

    private external fun decodeSampledImpl(byteArray: ByteArray, width: Int, height: Int): Bitmap

    private external fun encodeImpl(
        bitmap: Bitmap,
        colorSpace: Int,
        compressionOption: Int,
        lossyLevel: Float,
    ): ByteArray

    @SuppressLint("ObsoleteSdkInt")
    companion object {

        private val MAGIC_1 = byteArrayOf(0xFF.toByte(), 0x0A)
        private val MAGIC_2 = byteArrayOf(
            0x0.toByte(),
            0x0.toByte(),
            0x0.toByte(),
            0x0C.toByte(),
            0x4A,
            0x58,
            0x4C,
            0x20,
            0x0D,
            0x0A,
            0x87.toByte(),
            0x0A
        )

        init {
            if (Build.VERSION.SDK_INT >= 24) {
                System.loadLibrary("jxlcoder")
            }
        }

        fun isJXL(byteArray: ByteArray): Boolean {
            if (byteArray.size < MAGIC_2.size) {
                return false
            }
            val sample1 = byteArray.copyOfRange(0, 2)
            val sample2 = byteArray.copyOfRange(0, MAGIC_2.size)
            return sample1.contentEquals(MAGIC_1) || sample2.contentEquals(MAGIC_2)
        }
    }
}