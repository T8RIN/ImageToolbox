package io.github.alexzhirkevich.qrose.oned

internal object Code39Encoder : BarcodeEncoder {

    override fun encode(contents: String): BooleanArray {
        var actualContent = contents
        var length = actualContent.length
        if (length > 80) {
            throw IllegalArgumentException(
                "Requested contents should be less than 80 digits long, but got $length"
            )
        }
        for (i in 0 until length) {
            val indexInString: Int = ALPHABET_STRING.indexOf(actualContent[i])
            if (indexInString < 0) {
                actualContent = tryToConvertToExtendedMode(actualContent)
                length = actualContent.length
                if (length > 80) {
                    throw IllegalArgumentException(
                        "Requested contents should be less than 80 digits long, but got " +
                                length + " (extended full ASCII mode)"
                    )
                }
                break
            }
        }
        val widths = IntArray(9)
        val codeWidth = 24 + 1 + (13 * length)
        val result = BooleanArray(codeWidth)
        toIntArray(ASTERISK_ENCODING, widths)
        var pos = appendPattern(result, 0, widths, true)
        val narrowWhite = intArrayOf(1)
        pos += appendPattern(result, pos, narrowWhite, false)
        //append next character to byte matrix
        for (i in 0 until length) {
            val indexInString: Int = ALPHABET_STRING.indexOf(actualContent[i])
            toIntArray(CHARACTER_ENCODINGS[indexInString], widths)
            pos += appendPattern(result, pos, widths, true)
            pos += appendPattern(result, pos, narrowWhite, false)
        }
        toIntArray(ASTERISK_ENCODING, widths)
        appendPattern(result, pos, widths, true)
        return result
    }


    const val ALPHABET_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. \$/+%"

    val CHARACTER_ENCODINGS by lazy {
        intArrayOf(
            0x034, 0x121, 0x061, 0x160, 0x031, 0x130, 0x070, 0x025, 0x124, 0x064, // 0-9
            // 0-9
            0x109, 0x049, 0x148, 0x019, 0x118, 0x058, 0x00D, 0x10C, 0x04C, 0x01C, // A-J
            // A-J
            0x103, 0x043, 0x142, 0x013, 0x112, 0x052, 0x007, 0x106, 0x046, 0x016, // K-T
            // K-T
            0x181, 0x0C1, 0x1C0, 0x091, 0x190, 0x0D0, 0x085, 0x184, 0x0C4, 0x0A8, // U-$
            // U-$
            0x0A2, 0x08A, 0x02A // /-%
            // /-%
        )
    }

    val ASTERISK_ENCODING = 0x094

    private fun toIntArray(a: Int, toReturn: IntArray) {
        for (i in 0..8) {
            val temp = a and (1 shl (8 - i))
            toReturn[i] = if (temp == 0) 1 else 2
        }
    }

    private fun tryToConvertToExtendedMode(contents: String): String {
        val length = contents.length
        val extendedContent = StringBuilder()
        for (i in 0 until length) {
            val character = contents[i]
            when (character) {
                '\u0000' -> extendedContent.append("%U")
                ' ', '-', '.' -> extendedContent.append(character)
                '@' -> extendedContent.append("%V")
                '`' -> extendedContent.append("%W")
                else -> if (character.code <= 26) {
                    extendedContent.append('$')
                    extendedContent.append(('A'.code + (character.code - 1)).toChar())
                } else if (character < ' ') {
                    extendedContent.append('%')
                    extendedContent.append(('A'.code + (character.code - 27)).toChar())
                } else if ((character <= ',') || (character == '/') || (character == ':')) {
                    extendedContent.append('/')
                    extendedContent.append(('A'.code + (character.code - 33)).toChar())
                } else if (character <= '9') {
                    extendedContent.append(('0'.code + (character.code - 48)).toChar())
                } else if (character <= '?') {
                    extendedContent.append('%')
                    extendedContent.append(('F'.code + (character.code - 59)).toChar())
                } else if (character <= 'Z') {
                    extendedContent.append(('A'.code + (character.code - 65)).toChar())
                } else if (character <= '_') {
                    extendedContent.append('%')
                    extendedContent.append(('K'.code + (character.code - 91)).toChar())
                } else if (character <= 'z') {
                    extendedContent.append('+')
                    extendedContent.append(('A'.code + (character.code - 97)).toChar())
                } else if (character.code <= 127) {
                    extendedContent.append('%')
                    extendedContent.append(('P'.code + (character.code - 123)).toChar())
                } else {
                    throw IllegalArgumentException(
                        "Requested content contains a non-encodable character: '" + contents[i] + "'"
                    )
                }
            }
        }
        return extendedContent.toString()
    }
}

