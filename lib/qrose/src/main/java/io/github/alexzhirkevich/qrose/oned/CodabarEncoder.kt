package io.github.alexzhirkevich.qrose.oned

internal object CodabarEncoder : BarcodeEncoder {

    override fun encode(contents: String): BooleanArray {
        var actualContents = contents
        if (actualContents.length < 2) {
            // Can't have a start/end guard, so tentatively add default guards
            actualContents = DEFAULT_GUARD.toString() + actualContents + DEFAULT_GUARD
        } else {
            // Verify input and calculate decoded length.
            val firstChar = actualContents[0].uppercaseChar()
            val lastChar = actualContents[actualContents.length - 1].uppercaseChar()
            val startsNormal: Boolean = firstChar in START_END_CHARS
            val endsNormal: Boolean = lastChar in START_END_CHARS
            val startsAlt: Boolean = firstChar in ALT_START_END_CHARS
            val endsAlt: Boolean = lastChar in ALT_START_END_CHARS
            if (startsNormal) {
                if (!endsNormal) {
                    throw IllegalArgumentException("Invalid start/end guards: $actualContents")
                }
                // else already has valid start/end
            } else if (startsAlt) {
                if (!endsAlt) {
                    throw IllegalArgumentException("Invalid start/end guards: $actualContents")
                }
                // else already has valid start/end
            } else {
                // Doesn't start with a guard
                if (endsNormal || endsAlt) {
                    throw IllegalArgumentException("Invalid start/end guards: $actualContents")
                }
                // else doesn't end with guard either, so add a default
                actualContents = DEFAULT_GUARD.toString() + actualContents + DEFAULT_GUARD
            }
        }

        // The start character and the end character are decoded to 10 length each.
        var resultLength = 20
        for (i in 1 until actualContents.length - 1) {
            resultLength += if (actualContents[i].isDigit() || actualContents[i] == '-' || actualContents[i] == '$') {
                9
            } else if (actualContents[i] in CHARS_WHICH_ARE_TEN_LENGTH_EACH_AFTER_DECODED) {
                10
            } else {
                throw IllegalArgumentException("Cannot encode : '" + actualContents[i] + '\'')
            }
        }
        // A blank is placed between each character.
        resultLength += actualContents.length - 1
        val result = BooleanArray(resultLength)
        var position = 0
        for (index in actualContents.indices) {
            var c = actualContents[index].uppercaseChar()
            if (index == 0 || index == actualContents.length - 1) {
                // The start/end chars are not in the CodaBarReader.ALPHABET.
                when (c) {
                    'T' -> c = 'A'
                    'N' -> c = 'B'
                    '*' -> c = 'C'
                    'E' -> c = 'D'
                }
            }
            var code = 0
            for (i in ALPHABET.indices) {
                // Found any, because I checked above.
                if (c == ALPHABET[i]) {
                    code = CHARACTER_ENCODINGS[i]
                    break
                }
            }
            var color = true
            var counter = 0
            var bit = 0
            while (bit < 7) { // A character consists of 7 digit.
                result[position] = color
                position++
                if (code shr 6 - bit and 1 == 0 || counter == 1) {
                    color = !color // Flip the color.
                    bit++
                    counter = 0
                } else {
                    counter++
                }
            }
            if (index < actualContents.length - 1) {
                result[position] = false
                position++
            }
        }
        return result
    }

    private val START_END_CHARS = charArrayOf('A', 'B', 'C', 'D')
    private val ALT_START_END_CHARS = charArrayOf('T', 'N', '*', 'E')
    private val CHARS_WHICH_ARE_TEN_LENGTH_EACH_AFTER_DECODED = charArrayOf('/', ':', '+', '.')
    private val DEFAULT_GUARD = START_END_CHARS[0]

    private const val ALPHABET_STRING = "0123456789-$:/.+ABCD"
    private val ALPHABET = ALPHABET_STRING.toCharArray()

    /**
     * These represent the encodings of characters, as patterns of wide and narrow bars. The 7 least-significant bits of
     * each int correspond to the pattern of wide and narrow, with 1s representing "wide" and 0s representing narrow.
     */
    private val CHARACTER_ENCODINGS = intArrayOf(
        0x003, 0x006, 0x009, 0x060, 0x012, 0x042, 0x021, 0x024, 0x030, 0x048,  // 0-9
        0x00c, 0x018, 0x045, 0x051, 0x054, 0x015, 0x01A, 0x029, 0x00B, 0x00E
    )
}