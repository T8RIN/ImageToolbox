package io.github.alexzhirkevich.qrose.oned


internal object CodeEAN13Encoder : BarcodeEncoder {

    private val CODE_WIDTH = 3 + 7 * 6 +  // left bars
            5 + 7 * 6 +  // right bars
            3 // end guard


    override fun encode(contents: String): BooleanArray {
        var actualContents = contents
        val length = actualContents.length
        when (length) {
            12 -> {
                actualContents += getStandardUPCEANChecksum(actualContents)
            }

            13 -> if (!checkStandardUPCEANChecksum(actualContents)) {
                throw IllegalArgumentException("Contents do not pass checksum")
            }

            else -> throw IllegalArgumentException(
                "Requested contents should be 12 or 13 digits long, but got $length"
            )
        }
        actualContents.requireNumeric()
        val firstDigit = actualContents[0].digitToIntOrNull() ?: -1
        val parities: Int = UpcEan.FIRST_DIGIT_ENCODINGS[firstDigit]
        val result = BooleanArray(CODE_WIDTH)
        var pos = 0
        pos += appendPattern(result, pos, UpcEan.START_END_PATTERN, true)

        // See EAN13Reader for a description of how the first digit & left bars are encoded
        for (i in 1..6) {
            var digit = actualContents[i].digitToIntOrNull() ?: -1
            if (parities shr 6 - i and 1 == 1) {
                digit += 10
            }
            pos += appendPattern(
                result,
                pos,
                UpcEan.L_AND_G_PATTERNS.get(digit),
                false
            )
        }
        pos += appendPattern(result, pos, UpcEan.MIDDLE_PATTERN, false)
        for (i in 7..12) {
            val digit = actualContents[i].digitToIntOrNull() ?: -1
            pos += appendPattern(result, pos, UpcEan.L_PATTERNS[digit], true)
        }
        appendPattern(result, pos, UpcEan.START_END_PATTERN, true)
        return result
    }

}