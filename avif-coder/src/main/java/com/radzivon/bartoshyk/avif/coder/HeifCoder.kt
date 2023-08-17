@file:Suppress("unused")

package com.radzivon.bartoshyk.avif.coder

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.util.Size
import androidx.annotation.Keep

@Keep
class HeifCoder {

    fun isAvif(byteArray: ByteArray): Boolean {
        return isAvifImageImpl(byteArray)
    }

    fun isHeif(byteArray: ByteArray): Boolean {
        return isHeifImageImpl(byteArray)
    }

    fun isSupportedImage(byteArray: ByteArray): Boolean {
        return isSupportedImageImpl(byteArray)
    }

    fun getSize(bytes: ByteArray): Size? {
        return getSizeImpl(bytes)
    }

    fun decode(byteArray: ByteArray): Bitmap {
        return decodeImpl(byteArray, 0, 0)
    }

    fun decodeSampled(byteArray: ByteArray, scaledWidth: Int, scaledHeight: Int): Bitmap {
        return decodeImpl(byteArray, scaledWidth, scaledHeight)
    }

    fun encodeAvif(bitmap: Bitmap, quality: Int): ByteArray {
        return encodeAvifImpl(bitmap, quality)
    }

    fun encodeHeic(bitmap: Bitmap, quality: Int): ByteArray {
        return encodeHeicImpl(bitmap, quality)
    }


    private external fun getSizeImpl(byteArray: ByteArray): Size?
    private external fun isHeifImageImpl(byteArray: ByteArray): Boolean
    private external fun isAvifImageImpl(byteArray: ByteArray): Boolean
    private external fun isSupportedImageImpl(byteArray: ByteArray): Boolean
    private external fun decodeImpl(
        byteArray: ByteArray,
        scaledWidth: Int,
        scaledHeight: Int
    ): Bitmap

    private external fun encodeAvifImpl(bitmap: Bitmap, quality: Int): ByteArray
    private external fun encodeHeicImpl(bitmap: Bitmap, quality: Int): ByteArray

    @SuppressLint("ObsoleteSdkInt")
    companion object {
        init {
            if (Build.VERSION.SDK_INT >= 24) {
                System.loadLibrary("coder")
            }
        }
    }
}