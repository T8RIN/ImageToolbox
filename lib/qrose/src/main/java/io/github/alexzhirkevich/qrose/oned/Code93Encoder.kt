package io.github.alexzhirkevich.qrose.oned


internal object Code93Encoder : BarcodeEncoder {

    override fun encode(contents: String): BooleanArray {
        var actualContents = contents
        var length = actualContents.length
        if (length > 80) {
            throw IllegalArgumentException(
                "Requested contents should be less than 80 digits long, but got $length"
            )
        }
        for (i in 0 until length) {
            val indexInString: Int = Code39Encoder.ALPHABET_STRING.indexOf(actualContents[i])
            if (indexInString < 0) {
                actualContents = tryToConvertToExtendedMode(actualContents)
                length = actualContents.length
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
        toIntArray(Code39Encoder.ASTERISK_ENCODING, widths)
        var pos = appendPattern(result, 0, widths, true)
        val narrowWhite = intArrayOf(1)
        pos += appendPattern(result, pos, narrowWhite, false)
        //append next character to byte matrix
        for (i in 0 until length) {
            val indexInString: Int = Code39Encoder.ALPHABET_STRING.indexOf(actualContents[i])
            toIntArray(Code39Encoder.CHARACTER_ENCODINGS.get(indexInString), widths)
            pos += appendPattern(result, pos, widths, true)
            pos += appendPattern(result, pos, narrowWhite, false)
        }
        toIntArray(Code39Encoder.ASTERISK_ENCODING, widths)
        appendPattern(result, pos, widths, true)
        return result
    }

    private fun toIntArray(a: Int, toReturn: IntArray) {
        for (i in 0..8) {
            val temp = a and (1 shl (8 - i))
            toReturn[i] = if (temp == 0) 1 else 2
        }
    }

    private fun tryToConvertToExtendedMode(contents: String): String {
        val length = contents.length
        val extendedContent: StringBuilder = StringBuilder()
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