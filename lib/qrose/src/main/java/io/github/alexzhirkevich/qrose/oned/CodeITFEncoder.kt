package io.github.alexzhirkevich.qrose.oned


internal object CodeITFEncoder : BarcodeEncoder {

    override fun encode(contents: String): BooleanArray {
        val length = contents.length
        if (length % 2 != 0) {
            throw IllegalArgumentException("The length of the input should be even")
        }
        if (length > 80) {
            throw IllegalArgumentException(
                "Requested contents should be less than 80 digits long, but got $length"
            )
        }
        contents.requireNumeric()
        val result = BooleanArray(9 + 9 * length)
        var pos = appendPattern(result, 0, START_PATTERN, true)
        var i = 0
        while (i < length) {
            val one = contents[i].digitToIntOrNull() ?: -1
            val two = contents[i + 1].digitToIntOrNull() ?: -1
            val encoding = IntArray(10)
            for (j in 0..4) {
                encoding[2 * j] = PATTERNS[one][j]
                encoding[2 * j + 1] = PATTERNS[two][j]
            }
            pos += appendPattern(result, pos, encoding, true)
            i += 2
        }
        appendPattern(result, pos, END_PATTERN, true)
        return result
    }

    private val START_PATTERN = intArrayOf(1, 1, 1, 1)
    private val END_PATTERN = intArrayOf(3, 1, 1)
    private const val W = 3 // Pixel width of a 3x wide line
    private const val N = 1 // Pixed width of a narrow line

    // See ITFReader.PATTERNS
    private val PATTERNS = arrayOf(
        intArrayOf(N, N, W, W, N),
        intArrayOf(W, N, N, N, W),
        intArrayOf(
            N, W, N, N, W
        ),
        intArrayOf(W, W, N, N, N),
        intArrayOf(N, N, W, N, W),
        intArrayOf(W, N, W, N, N),
        intArrayOf(
            N, W, W, N, N
        ),
        intArrayOf(N, N, N, W, W),
        intArrayOf(W, N, N, W, N),
        intArrayOf(N, W, N, W, N)
    )
}