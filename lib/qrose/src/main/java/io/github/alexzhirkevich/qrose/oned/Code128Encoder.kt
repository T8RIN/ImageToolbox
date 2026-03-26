package io.github.alexzhirkevich.qrose.oned

internal object Code128Encoder : BarcodeEncoder {
    // Results of minimal lookahead for code C
    private enum class CType {
        UNCODABLE,
        ONE_DIGIT,
        TWO_DIGITS,
        FNC_1
    }

    override fun encode(data: String): BooleanArray {
        return encode(contents = data)
    }

    fun encode(
        contents: String,
        compact: Boolean = true,
        codeSet: Code128Type? = null
    ): BooleanArray {
        val forcedCodeSet = check(contents, codeSet)

        return if (compact) {
            MinimalEncoder().encode(contents)
        } else {
            encodeFast(
                contents,
                forcedCodeSet
            )
        }
    }

    /**
     * Encodes minimally using Divide-And-Conquer with Memoization
     */
    private class MinimalEncoder {
        private enum class Charset {
            A,
            B,
            C,
            NONE
        }

        private enum class Latch {
            A,
            B,
            C,
            SHIFT,
            NONE
        }

        private var memoizedCost: Array<IntArray>? = null
        private var minPath: Array<Array<Latch?>>? = null

        fun encode(contents: String): BooleanArray {
            memoizedCost = Array(4) { IntArray(contents.length) }
            minPath = Array(4) {
                arrayOfNulls(
                    contents.length
                )
            }
            encode(contents, Charset.NONE, 0)
            val patterns: MutableCollection<IntArray> = ArrayList<IntArray>()
            val checkSum = intArrayOf(0)
            val checkWeight = intArrayOf(1)
            val length = contents.length
            var charset = Charset.NONE
            var i = 0
            while (i < length) {
                val latch = minPath!![charset.ordinal][i]
                when (latch) {
                    Latch.A -> {
                        charset = Charset.A
                        addPattern(
                            patterns,
                            if (i == 0) CODE_START_A else CODE_CODE_A,
                            checkSum,
                            checkWeight,
                            i
                        )
                    }

                    Latch.B -> {
                        charset = Charset.B
                        addPattern(
                            patterns,
                            if (i == 0) CODE_START_B else CODE_CODE_B,
                            checkSum,
                            checkWeight,
                            i
                        )
                    }

                    Latch.C -> {
                        charset = Charset.C
                        addPattern(
                            patterns,
                            if (i == 0) CODE_START_C else CODE_CODE_C,
                            checkSum,
                            checkWeight,
                            i
                        )
                    }

                    Latch.SHIFT -> addPattern(patterns, CODE_SHIFT, checkSum, checkWeight, i)
                    else -> {}
                }
                if (charset == Charset.C) {
                    if (contents[i] == ESCAPE_FNC_1) {
                        addPattern(patterns, CODE_FNC_1, checkSum, checkWeight, i)
                    } else {
                        addPattern(
                            patterns,
                            contents.substring(i, i + 2).toInt(),
                            checkSum,
                            checkWeight,
                            i
                        )
                        require(i + 1 < length) //the algorithm never leads to a single trailing digit in character set C

                        if (i + 1 < length) {
                            i++
                        }
                    }
                } else { // charset A or B
                    var patternIndex: Int
                    patternIndex =
                        when (contents[i]) {
                            ESCAPE_FNC_1 -> CODE_FNC_1
                            ESCAPE_FNC_2 -> CODE_FNC_2
                            ESCAPE_FNC_3 -> CODE_FNC_3
                            ESCAPE_FNC_4 -> if (charset == Charset.A && latch != Latch.SHIFT ||
                                charset == Charset.B && latch == Latch.SHIFT
                            ) {
                                CODE_FNC_4_A
                            } else {
                                CODE_FNC_4_B
                            }

                            else -> contents[i].code - ' '.code
                        }
                    if ((charset == Charset.A && latch != Latch.SHIFT ||
                                charset == Charset.B && latch == Latch.SHIFT) &&
                        patternIndex < 0
                    ) {
                        patternIndex += '`'.code
                    }
                    addPattern(patterns, patternIndex, checkSum, checkWeight, i)
                }
                i++
            }
            memoizedCost = null
            minPath = null
            return produceResult(patterns, checkSum[0])
        }

        private fun canEncode(contents: CharSequence, charset: Charset, position: Int): Boolean {
            val c = contents[position]
            return when (charset) {
                Charset.A -> c == ESCAPE_FNC_1 || c == ESCAPE_FNC_2 || c == ESCAPE_FNC_3 || c == ESCAPE_FNC_4 || A.indexOf(
                    c
                ) >= 0

                Charset.B -> c == ESCAPE_FNC_1 || c == ESCAPE_FNC_2 || c == ESCAPE_FNC_3 || c == ESCAPE_FNC_4 || B.indexOf(
                    c
                ) >= 0

                Charset.C -> c == ESCAPE_FNC_1 || position + 1 < contents.length &&
                        isDigit(c) && isDigit(contents[position + 1])

                else -> false
            }
        }

        /**
         * Encode the string starting at position position starting with the character set charset
         */
        private fun encode(contents: CharSequence, charset: Charset, position: Int): Int {
            require(position < contents.length)
            val mCost = memoizedCost!![charset.ordinal][position]
            if (mCost > 0) {
                return mCost
            }
            var minCost = Int.MAX_VALUE
            var minLatch: Latch? = Latch.NONE
            val atEnd = position + 1 >= contents.length
            val sets = arrayOf(Charset.A, Charset.B)
            for (i in 0..1) {
                if (canEncode(contents, sets[i], position)) {
                    var cost = 1
                    var latch = Latch.NONE
                    if (charset != sets[i]) {
                        cost++
                        latch = Latch.valueOf(
                            sets[i].toString()
                        )
                    }
                    if (!atEnd) {
                        cost += encode(contents, sets[i], position + 1)
                    }
                    if (cost < minCost) {
                        minCost = cost
                        minLatch = latch
                    }
                    cost = 1
                    if (charset == sets[(i + 1) % 2]) {
                        cost++
                        latch = Latch.SHIFT
                        if (!atEnd) {
                            cost += encode(contents, charset, position + 1)
                        }
                        if (cost < minCost) {
                            minCost = cost
                            minLatch = latch
                        }
                    }
                }
            }
            if (canEncode(contents, Charset.C, position)) {
                var cost = 1
                var latch = Latch.NONE
                if (charset != Charset.C) {
                    cost++
                    latch = Latch.C
                }
                val advance = if (contents[position] == ESCAPE_FNC_1) 1 else 2
                if (position + advance < contents.length) {
                    cost += encode(contents, Charset.C, position + advance)
                }
                if (cost < minCost) {
                    minCost = cost
                    minLatch = latch
                }
            }
            if (minCost == Int.MAX_VALUE) {
                throw IllegalArgumentException("Bad character in input: ASCII value=" + contents[position].code)
            }
            memoizedCost!![charset.ordinal][position] = minCost
            minPath!![charset.ordinal][position] = minLatch
            return minCost
        }

        companion object {
            const val A =
                " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_\u0000\u0001\u0002" +
                        "\u0003\u0004\u0005\u0006\u0007\u0008\u0009\n\u000B\u000C\r\u000E\u000F\u0010\u0011" +
                        "\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C\u001D\u001E\u001F" +
                        "\u00FF"
            const val B =
                " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqr" +
                        "stuvwxyz{|}~\u007F\u00FF"
            private const val CODE_SHIFT = 98

            private fun addPattern(
                patterns: MutableCollection<IntArray>,
                patternIndex: Int,
                checkSum: IntArray,
                checkWeight: IntArray,
                position: Int,
            ) {
                patterns.add(CODE_PATTERNS.get(patternIndex))
                if (position != 0) {
                    checkWeight[0]++
                }
                checkSum[0] += patternIndex * checkWeight[0]
            }

            private fun isDigit(c: Char): Boolean {
                return c in '0'..'9'
            }
        }
    }


    private const val CODE_START_A = 103
    private const val CODE_START_B = 104
    private const val CODE_START_C = 105
    internal const val CODE_CODE_A = 101
    internal const val CODE_CODE_B = 100
    internal const val CODE_CODE_C = 99
    private const val CODE_STOP = 106

    // Dummy characters used to specify control characters in input
    private const val ESCAPE_FNC_1 = '\u00f1'
    private const val ESCAPE_FNC_2 = '\u00f2'
    private const val ESCAPE_FNC_3 = '\u00f3'
    private const val ESCAPE_FNC_4 = '\u00f4'
    private const val CODE_FNC_1 = 102 // Code A, Code B, Code C
    private const val CODE_FNC_2 = 97 // Code A, Code B
    private const val CODE_FNC_3 = 96 // Code A, Code B
    private const val CODE_FNC_4_A = 101 // Code A
    private const val CODE_FNC_4_B = 100 // Code B

    private fun check(contents: String, codeSet: Code128Type?): Int {

        // Check content
        val length = contents.length
        for (i in 0 until length) {
            val c = contents[i]
            when (c) {
                ESCAPE_FNC_1, ESCAPE_FNC_2, ESCAPE_FNC_3, ESCAPE_FNC_4 -> {}
                else -> if (c.code > 127) {
                    // no full Latin-1 character set available at the moment
                    // shift and manual code change are not supported
                    throw IllegalArgumentException("Bad character in input: ASCII value=" + c.code)
                }
            }
            when (codeSet) {
                Code128Type.A ->           // allows no ascii above 95 (no lower caps, no special symbols)
                    if (c.code in 96..127) {
                        throw IllegalArgumentException("Bad character in input for forced code set A: ASCII value=" + c.code)
                    }

                Code128Type.B ->           // allows no ascii below 32 (terminal symbols)
                    if (c.code < 32) {
                        throw IllegalArgumentException("Bad character in input for forced code set B: ASCII value=" + c.code)
                    }

                Code128Type.C ->           // allows only numbers and no FNC 2/3/4
                    if (c.code < 48 || c.code in 58..127 || c == ESCAPE_FNC_2 || c == ESCAPE_FNC_3 || c == ESCAPE_FNC_4) {
                        throw IllegalArgumentException("Bad character in input for forced code set C: ASCII value=" + c.code)
                    }

                else -> {}
            }
        }
        return codeSet?.v ?: -1
    }

    private fun encodeFast(contents: String, forcedCodeSet: Int): BooleanArray {
        val length = contents.length
        val patterns: MutableCollection<IntArray> = ArrayList() // temporary storage for patterns
        var checkSum = 0
        var checkWeight = 1
        var codeSet = 0 // selected code (CODE_CODE_B or CODE_CODE_C)
        var position = 0 // position in contents
        while (position < length) {
            //Select code to use
            var newCodeSet: Int
            newCodeSet = if (forcedCodeSet == -1) {
                chooseCode(contents, position, codeSet)
            } else {
                forcedCodeSet
            }

            //Get the pattern index
            var patternIndex: Int
            if (newCodeSet == codeSet) {
                // Encode the current character
                // First handle escapes
                when (contents[position]) {
                    ESCAPE_FNC_1 -> patternIndex = CODE_FNC_1
                    ESCAPE_FNC_2 -> patternIndex = CODE_FNC_2
                    ESCAPE_FNC_3 -> patternIndex = CODE_FNC_3
                    ESCAPE_FNC_4 -> patternIndex = if (codeSet == CODE_CODE_A) {
                        CODE_FNC_4_A
                    } else {
                        CODE_FNC_4_B
                    }

                    else -> when (codeSet) {
                        CODE_CODE_A -> {
                            patternIndex = contents[position].code - ' '.code
                            if (patternIndex < 0) {
                                // everything below a space character comes behind the underscore in the code patterns table
                                patternIndex += '`'.code
                            }
                        }

                        CODE_CODE_B -> patternIndex = contents[position].code - ' '.code
                        else -> {
                            // CODE_CODE_C
                            if (position + 1 == length) {
                                // this is the last character, but the encoding is C, which always encodes two characers
                                throw IllegalArgumentException("Bad number of characters for digit only encoding.")
                            }
                            patternIndex = contents.substring(position, position + 2).toInt()
                            position++ // Also incremented below
                        }
                    }
                }
                position++
            } else {
                // Should we change the current code?
                // Do we have a code set?
                patternIndex = if (codeSet == 0) {
                    // No, we don't have a code set
                    when (newCodeSet) {
                        CODE_CODE_A -> CODE_START_A
                        CODE_CODE_B -> CODE_START_B
                        else -> CODE_START_C
                    }
                } else {
                    // Yes, we have a code set
                    newCodeSet
                }
                codeSet = newCodeSet
            }

            // Get the pattern
            patterns.add(CODE_PATTERNS[patternIndex])

            // Compute checksum
            checkSum += patternIndex * checkWeight
            if (position != 0) {
                checkWeight++
            }
        }
        return produceResult(patterns, checkSum)
    }

    fun produceResult(patterns: MutableCollection<IntArray>, checkSum: Int): BooleanArray {
        // Compute and append checksum
        val checkSumMod = checkSum % 103
        if (checkSumMod < 0) {
            throw IllegalArgumentException("Unable to compute a valid input checksum")
        }
        patterns.add(CODE_PATTERNS[checkSumMod])

        // Append stop code
        patterns.add(CODE_PATTERNS[CODE_STOP])

        // Compute code width
        var codeWidth = 0
        for (pattern in patterns) {
            for (width in pattern) {
                codeWidth += width
            }
        }

        // Compute result
        val result = BooleanArray(codeWidth)
        var pos = 0
        for (pattern in patterns) {
            pos += appendPattern(result, pos, pattern, true)
        }
        return result
    }

    private fun findCType(value: CharSequence, start: Int): CType {
        val last = value.length
        if (start >= last) {
            return CType.UNCODABLE
        }
        var c = value[start]
        if (c == ESCAPE_FNC_1) {
            return CType.FNC_1
        }
        if (c < '0' || c > '9') {
            return CType.UNCODABLE
        }
        if (start + 1 >= last) {
            return CType.ONE_DIGIT
        }
        c = value[start + 1]
        return if (c < '0' || c > '9') {
            CType.ONE_DIGIT
        } else CType.TWO_DIGITS
    }

    private fun chooseCode(value: CharSequence, start: Int, oldCode: Int): Int {
        var lookahead = findCType(value, start)
        if (lookahead == CType.ONE_DIGIT) {
            return if (oldCode == CODE_CODE_A) {
                CODE_CODE_A
            } else CODE_CODE_B
        }
        if (lookahead == CType.UNCODABLE) {
            if (start < value.length) {
                val c = value[start]
                if (c < ' ' || oldCode == CODE_CODE_A && (c < '`' || c >= ESCAPE_FNC_1 && c <= ESCAPE_FNC_4)) {
                    // can continue in code A, encodes ASCII 0 to 95 or FNC1 to FNC4
                    return CODE_CODE_A
                }
            }
            return CODE_CODE_B // no choice
        }
        if (oldCode == CODE_CODE_A && lookahead == CType.FNC_1) {
            return CODE_CODE_A
        }
        if (oldCode == CODE_CODE_C) { // can continue in code C
            return CODE_CODE_C
        }
        if (oldCode == CODE_CODE_B) {
            if (lookahead == CType.FNC_1) {
                return CODE_CODE_B // can continue in code B
            }
            // Seen two consecutive digits, see what follows
            lookahead = findCType(value, start + 2)
            if (lookahead == CType.UNCODABLE || lookahead == CType.ONE_DIGIT) {
                return CODE_CODE_B // not worth switching now
            }
            if (lookahead == CType.FNC_1) { // two digits, then FNC_1...
                lookahead = findCType(value, start + 3)
                return if (lookahead == CType.TWO_DIGITS) { // then two more digits, switch
                    CODE_CODE_C
                } else {
                    CODE_CODE_B // otherwise not worth switching
                }
            }
            // At this point, there are at least 4 consecutive digits.
            // Look ahead to choose whether to switch now or on the next round.
            var index = start + 4
            while (findCType(value, index).also { lookahead = it } == CType.TWO_DIGITS) {
                index += 2
            }
            return if (lookahead == CType.ONE_DIGIT) { // odd number of digits, switch later
                CODE_CODE_B
            } else CODE_CODE_C
            // even number of digits, switch now
        }
        // Here oldCode == 0, which means we are choosing the initial code
        if (lookahead == CType.FNC_1) { // ignore FNC_1
            lookahead = findCType(value, start + 1)
        }
        return if (lookahead == CType.TWO_DIGITS) { // at least two digits, start in code C
            CODE_CODE_C
        } else CODE_CODE_B
    }
}

internal fun appendPattern(
    target: BooleanArray,
    pos: Int,
    pattern: IntArray,
    startColor: Boolean,
): Int {
    var pos = pos
    var color = startColor
    var numAdded = 0
    for (len in pattern) {
        for (j in 0 until len) {
            target[pos++] = color
        }
        numAdded += len
        color = !color // flip color after each segment
    }
    return numAdded
}

private val CODE_PATTERNS by lazy {
    arrayOf(
        intArrayOf(2, 1, 2, 2, 2, 2),
        intArrayOf(2, 2, 2, 1, 2, 2),
        intArrayOf(2, 2, 2, 2, 2, 1),
        intArrayOf(1, 2, 1, 2, 2, 3),
        intArrayOf(1, 2, 1, 3, 2, 2),
        intArrayOf(1, 3, 1, 2, 2, 2),
        intArrayOf(1, 2, 2, 2, 1, 3),
        intArrayOf(1, 2, 2, 3, 1, 2),
        intArrayOf(1, 3, 2, 2, 1, 2),
        intArrayOf(2, 2, 1, 2, 1, 3),
        intArrayOf(2, 2, 1, 3, 1, 2),
        intArrayOf(2, 3, 1, 2, 1, 2),
        intArrayOf(1, 1, 2, 2, 3, 2),
        intArrayOf(1, 2, 2, 1, 3, 2),
        intArrayOf(1, 2, 2, 2, 3, 1),
        intArrayOf(1, 1, 3, 2, 2, 2),
        intArrayOf(1, 2, 3, 1, 2, 2),
        intArrayOf(1, 2, 3, 2, 2, 1),
        intArrayOf(2, 2, 3, 2, 1, 1),
        intArrayOf(2, 2, 1, 1, 3, 2),
        intArrayOf(2, 2, 1, 2, 3, 1),
        intArrayOf(2, 1, 3, 2, 1, 2),
        intArrayOf(2, 2, 3, 1, 1, 2),
        intArrayOf(3, 1, 2, 1, 3, 1),
        intArrayOf(3, 1, 1, 2, 2, 2),
        intArrayOf(3, 2, 1, 1, 2, 2),
        intArrayOf(3, 2, 1, 2, 2, 1),
        intArrayOf(3, 1, 2, 2, 1, 2),
        intArrayOf(3, 2, 2, 1, 1, 2),
        intArrayOf(3, 2, 2, 2, 1, 1),
        intArrayOf(2, 1, 2, 1, 2, 3),
        intArrayOf(2, 1, 2, 3, 2, 1),
        intArrayOf(2, 3, 2, 1, 2, 1),
        intArrayOf(1, 1, 1, 3, 2, 3),
        intArrayOf(1, 3, 1, 1, 2, 3),
        intArrayOf(1, 3, 1, 3, 2, 1),
        intArrayOf(1, 1, 2, 3, 1, 3),
        intArrayOf(1, 3, 2, 1, 1, 3),
        intArrayOf(1, 3, 2, 3, 1, 1),
        intArrayOf(2, 1, 1, 3, 1, 3),
        intArrayOf(2, 3, 1, 1, 1, 3),
        intArrayOf(2, 3, 1, 3, 1, 1),
        intArrayOf(1, 1, 2, 1, 3, 3),
        intArrayOf(1, 1, 2, 3, 3, 1),
        intArrayOf(1, 3, 2, 1, 3, 1),
        intArrayOf(1, 1, 3, 1, 2, 3),
        intArrayOf(1, 1, 3, 3, 2, 1),
        intArrayOf(1, 3, 3, 1, 2, 1),
        intArrayOf(3, 1, 3, 1, 2, 1),
        intArrayOf(2, 1, 1, 3, 3, 1),
        intArrayOf(2, 3, 1, 1, 3, 1),
        intArrayOf(2, 1, 3, 1, 1, 3),
        intArrayOf(2, 1, 3, 3, 1, 1),
        intArrayOf(2, 1, 3, 1, 3, 1),
        intArrayOf(3, 1, 1, 1, 2, 3),
        intArrayOf(3, 1, 1, 3, 2, 1),
        intArrayOf(3, 3, 1, 1, 2, 1),
        intArrayOf(3, 1, 2, 1, 1, 3),
        intArrayOf(3, 1, 2, 3, 1, 1),
        intArrayOf(3, 3, 2, 1, 1, 1),
        intArrayOf(3, 1, 4, 1, 1, 1),
        intArrayOf(2, 2, 1, 4, 1, 1),
        intArrayOf(4, 3, 1, 1, 1, 1),
        intArrayOf(1, 1, 1, 2, 2, 4),
        intArrayOf(1, 1, 1, 4, 2, 2),
        intArrayOf(1, 2, 1, 1, 2, 4),
        intArrayOf(1, 2, 1, 4, 2, 1),
        intArrayOf(1, 4, 1, 1, 2, 2),
        intArrayOf(1, 4, 1, 2, 2, 1),
        intArrayOf(1, 1, 2, 2, 1, 4),
        intArrayOf(1, 1, 2, 4, 1, 2),
        intArrayOf(1, 2, 2, 1, 1, 4),
        intArrayOf(1, 2, 2, 4, 1, 1),
        intArrayOf(1, 4, 2, 1, 1, 2),
        intArrayOf(1, 4, 2, 2, 1, 1),
        intArrayOf(2, 4, 1, 2, 1, 1),
        intArrayOf(2, 2, 1, 1, 1, 4),
        intArrayOf(4, 1, 3, 1, 1, 1),
        intArrayOf(2, 4, 1, 1, 1, 2),
        intArrayOf(1, 3, 4, 1, 1, 1),
        intArrayOf(1, 1, 1, 2, 4, 2),
        intArrayOf(1, 2, 1, 1, 4, 2),
        intArrayOf(1, 2, 1, 2, 4, 1),
        intArrayOf(1, 1, 4, 2, 1, 2),
        intArrayOf(1, 2, 4, 1, 1, 2),
        intArrayOf(1, 2, 4, 2, 1, 1),
        intArrayOf(4, 1, 1, 2, 1, 2),
        intArrayOf(4, 2, 1, 1, 1, 2),
        intArrayOf(4, 2, 1, 2, 1, 1),
        intArrayOf(2, 1, 2, 1, 4, 1),
        intArrayOf(2, 1, 4, 1, 2, 1),
        intArrayOf(4, 1, 2, 1, 2, 1),
        intArrayOf(1, 1, 1, 1, 4, 3),
        intArrayOf(1, 1, 1, 3, 4, 1),
        intArrayOf(1, 3, 1, 1, 4, 1),
        intArrayOf(1, 1, 4, 1, 1, 3),
        intArrayOf(1, 1, 4, 3, 1, 1),
        intArrayOf(4, 1, 1, 1, 1, 3),
        intArrayOf(4, 1, 1, 3, 1, 1),
        intArrayOf(1, 1, 3, 1, 4, 1),
        intArrayOf(1, 1, 4, 1, 3, 1),
        intArrayOf(3, 1, 1, 1, 4, 1),
        intArrayOf(4, 1, 1, 1, 3, 1),
        intArrayOf(2, 1, 1, 4, 1, 2),
        intArrayOf(2, 1, 1, 2, 1, 4),
        intArrayOf(2, 1, 1, 2, 3, 2),
        intArrayOf(2, 3, 3, 1, 1, 1, 2)
    )
}