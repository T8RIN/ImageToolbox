package io.github.alexzhirkevich.qrose.qrcode.internals

import io.github.alexzhirkevich.qrose.qrcode.internals.QRMath.gexp
import io.github.alexzhirkevich.qrose.qrcode.internals.QRMath.glog

/**
 * Rewritten in Kotlin from the [original (GitHub)](https://github.com/kazuhikoarase/qrcode-generator/blob/master/java/src/main/java/com/d_project/qrcode/Polynomial.java)
 *
 * @author Rafael Lins - g0dkar
 * @author Kazuhiko Arase - kazuhikoarase
 */
internal class Polynomial(num: IntArray, shift: Int = 0) {
    private val data: IntArray

    init {
        val offset = num.indexOfFirst { it != 0 }.coerceAtLeast(0)
        this.data = IntArray(num.size - offset + shift) { 0 }
        arraycopy(num, offset, this.data, 0, num.size - offset)
    }

    private fun arraycopy(from: IntArray, fromPos: Int, to: IntArray, toPos: Int, length: Int) {
        for (i in 0 until length) {
            to[toPos + i] = from[fromPos + i]
        }
    }

    operator fun get(i: Int) = data[i]

    fun len(): Int = data.size

    fun multiply(other: Polynomial): Polynomial =
        IntArray(len() + other.len() - 1) { 0 }
            .let {
                for (i in 0 until len()) {
                    for (j in 0 until other.len()) {
                        it[i + j] = it[i + j] xor gexp(glog(this[i]) + glog(other[j]))
                    }
                }

                Polynomial(it)
            }

    fun mod(other: Polynomial): Polynomial =
        if (len() - other.len() < 0) {
            this
        } else {
            val ratio = glog(this[0]) - glog(other[0])
            val result = data.copyOf()

            other.data.forEachIndexed { i, it ->
                result[i] = result[i] xor gexp(glog(it) + ratio)
            }

            Polynomial(result).mod(other)
        }
}
