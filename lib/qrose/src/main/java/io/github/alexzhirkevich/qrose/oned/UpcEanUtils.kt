package io.github.alexzhirkevich.qrose.oned

internal object UpcEan {

    val NUMSYS_AND_CHECK_DIGIT_PATTERNS = arrayOf(
        intArrayOf(0x38, 0x34, 0x32, 0x31, 0x2C, 0x26, 0x23, 0x2A, 0x29, 0x25),
        intArrayOf(0x07, 0x0B, 0x0D, 0x0E, 0x13, 0x19, 0x1C, 0x15, 0x16, 0x1A)
    )

    /**
     * Start/end guard pattern.
     */
    val START_END_PATTERN by lazy {
        intArrayOf(1, 1, 1)
    }

    /**
     * Pattern marking the middle of a UPC/EAN pattern, separating the two halves.
     */
    val MIDDLE_PATTERN by lazy {
        intArrayOf(1, 1, 1, 1, 1)
    }

    /**
     * end guard pattern.
     */
    val END_PATTERN by lazy {
        intArrayOf(1, 1, 1, 1, 1, 1)
    }

    /**
     * "Odd", or "L" patterns used to encode UPC/EAN digits.
     */
    val L_PATTERNS by lazy {
        arrayOf(
            intArrayOf(3, 2, 1, 1),
            intArrayOf(2, 2, 2, 1),
            intArrayOf(2, 1, 2, 2),
            intArrayOf(1, 4, 1, 1),
            intArrayOf(1, 1, 3, 2),
            intArrayOf(1, 2, 3, 1),
            intArrayOf(1, 1, 1, 4),
            intArrayOf(1, 3, 1, 2),
            intArrayOf(1, 2, 1, 3),
            intArrayOf(3, 1, 1, 2)
        )
    }

    val L_AND_G_PATTERNS by lazy {
        buildList(20) {
            addAll(L_PATTERNS)

            for (i in 10..19) {
                val widths: IntArray = L_PATTERNS[i - 10]
                val reversedWidths = IntArray(widths.size)
                for (j in widths.indices) {
                    reversedWidths[j] = widths[widths.size - j - 1]
                }
                add(reversedWidths)
            }
        }
    }

    val FIRST_DIGIT_ENCODINGS by lazy {
        intArrayOf(
            0x00, 0x0B, 0x0D, 0xE, 0x13, 0x19, 0x1C, 0x15, 0x16, 0x1A
        )
    }
}

internal fun String.requireNumeric() = require(all { it.isDigit() }) {
    "Input should only contain digits 0-9"
}

internal fun convertUPCEtoUPCA(upce: String): String {
    val upceChars = upce.toCharArray(1, 7)
    val result = StringBuilder(12)
    result.append(upce[0])
    val lastChar = upceChars[5]
    when (lastChar) {
        '0', '1', '2' -> {
            result.appendRange(upceChars, 0, 0 + 2)
            result.append(lastChar)
            result.append("0000")
            result.appendRange(upceChars, 2, 3)
        }

        '3' -> {
            result.appendRange(upceChars, 0, 3)
            result.append("00000")
            result.appendRange(upceChars, 3, 2)
        }

        '4' -> {
            result.appendRange(upceChars, 0, 4)
            result.append("00000")
            result.append(upceChars[4])
        }

        else -> {
            result.appendRange(upceChars, 0, 5)
            result.append("0000")
            result.append(lastChar)
        }
    }
    // Only append check digit in conversion if supplied
    if (upce.length >= 8) {
        result.append(upce[7])
    }
    return result.toString()
}

internal fun checkStandardUPCEANChecksum(s: CharSequence): Boolean {
    val length = s.length
    if (length == 0) {
        return false
    }
    val check = s[length - 1].digitToIntOrNull() ?: -1
    return getStandardUPCEANChecksum(s.subSequence(0, length - 1)) == check
}

internal fun getStandardUPCEANChecksum(s: CharSequence): Int {
    val length = s.length
    var sum = 0
    run {
        var i = length - 1
        while (i >= 0) {
            val digit = s[i].code - '0'.code
            if (digit < 0 || digit > 9) {
                throw IllegalStateException("Illegal contents")
            }
            sum += digit
            i -= 2
        }
    }
    sum *= 3
    var i = length - 2
    while (i >= 0) {
        val digit = s[i].code - '0'.code
        if (digit < 0 || digit > 9) {
            throw IllegalStateException("Illegal contents")
        }
        sum += digit
        i -= 2
    }
    return (1000 - sum) % 10
}