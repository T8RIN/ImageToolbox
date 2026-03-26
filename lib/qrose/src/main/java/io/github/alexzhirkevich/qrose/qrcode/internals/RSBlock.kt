package io.github.alexzhirkevich.qrose.qrcode.internals

import io.github.alexzhirkevich.qrose.qrcode.ErrorCorrectionLevel

/**
 * Rewritten in Kotlin from the [original (GitHub)](https://github.com/kazuhikoarase/qrcode-generator/blob/master/java/src/main/java/com/d_project/qrcode/RSBlock.java)
 *
 * @author Rafael Lins - g0dkar
 * @author Kazuhiko Arase - kazuhikoarase
 */
internal class RSBlock(val totalCount: Int, val dataCount: Int) {
    companion object {
        private val RS_BLOCK_TABLE = arrayOf(
            intArrayOf(1, 26, 19),
            intArrayOf(1, 26, 16),
            intArrayOf(1, 26, 13),
            intArrayOf(1, 26, 9),
            intArrayOf(1, 44, 34),
            intArrayOf(1, 44, 28),
            intArrayOf(1, 44, 22),
            intArrayOf(1, 44, 16),
            intArrayOf(1, 70, 55),
            intArrayOf(1, 70, 44),
            intArrayOf(2, 35, 17),
            intArrayOf(2, 35, 13),
            intArrayOf(1, 100, 80),
            intArrayOf(2, 50, 32),
            intArrayOf(2, 50, 24),
            intArrayOf(4, 25, 9),
            intArrayOf(1, 134, 108),
            intArrayOf(2, 67, 43),
            intArrayOf(2, 33, 15, 2, 34, 16),
            intArrayOf(2, 33, 11, 2, 34, 12),
            intArrayOf(2, 86, 68),
            intArrayOf(4, 43, 27),
            intArrayOf(4, 43, 19),
            intArrayOf(4, 43, 15),
            intArrayOf(2, 98, 78),
            intArrayOf(4, 49, 31),
            intArrayOf(2, 32, 14, 4, 33, 15),
            intArrayOf(4, 39, 13, 1, 40, 14),
            intArrayOf(2, 121, 97),
            intArrayOf(2, 60, 38, 2, 61, 39),
            intArrayOf(4, 40, 18, 2, 41, 19),
            intArrayOf(4, 40, 14, 2, 41, 15),
            intArrayOf(2, 146, 116),
            intArrayOf(3, 58, 36, 2, 59, 37),
            intArrayOf(4, 36, 16, 4, 37, 17),
            intArrayOf(4, 36, 12, 4, 37, 13),
            intArrayOf(2, 86, 68, 2, 87, 69),
            intArrayOf(4, 69, 43, 1, 70, 44),
            intArrayOf(6, 43, 19, 2, 44, 20),
            intArrayOf(6, 43, 15, 2, 44, 16),
            intArrayOf(4, 101, 81),
            intArrayOf(1, 80, 50, 4, 81, 51),
            intArrayOf(4, 50, 22, 4, 51, 23),
            intArrayOf(3, 36, 12, 8, 37, 13),
            intArrayOf(2, 116, 92, 2, 117, 93),
            intArrayOf(6, 58, 36, 2, 59, 37),
            intArrayOf(4, 46, 20, 6, 47, 21),
            intArrayOf(7, 42, 14, 4, 43, 15),
            intArrayOf(4, 133, 107),
            intArrayOf(8, 59, 37, 1, 60, 38),
            intArrayOf(8, 44, 20, 4, 45, 21),
            intArrayOf(12, 33, 11, 4, 34, 12),
            intArrayOf(3, 145, 115, 1, 146, 116),
            intArrayOf(4, 64, 40, 5, 65, 41),
            intArrayOf(11, 36, 16, 5, 37, 17),
            intArrayOf(11, 36, 12, 5, 37, 13),
            intArrayOf(5, 109, 87, 1, 110, 88),
            intArrayOf(5, 65, 41, 5, 66, 42),
            intArrayOf(5, 54, 24, 7, 55, 25),
            intArrayOf(11, 36, 12, 7, 37, 13),
            intArrayOf(5, 122, 98, 1, 123, 99),
            intArrayOf(7, 73, 45, 3, 74, 46),
            intArrayOf(15, 43, 19, 2, 44, 20),
            intArrayOf(3, 45, 15, 13, 46, 16),
            intArrayOf(1, 135, 107, 5, 136, 108),
            intArrayOf(10, 74, 46, 1, 75, 47),
            intArrayOf(1, 50, 22, 15, 51, 23),
            intArrayOf(2, 42, 14, 17, 43, 15),
            intArrayOf(5, 150, 120, 1, 151, 121),
            intArrayOf(9, 69, 43, 4, 70, 44),
            intArrayOf(17, 50, 22, 1, 51, 23),
            intArrayOf(2, 42, 14, 19, 43, 15),
            intArrayOf(3, 141, 113, 4, 142, 114),
            intArrayOf(3, 70, 44, 11, 71, 45),
            intArrayOf(17, 47, 21, 4, 48, 22),
            intArrayOf(9, 39, 13, 16, 40, 14),
            intArrayOf(3, 135, 107, 5, 136, 108),
            intArrayOf(3, 67, 41, 13, 68, 42),
            intArrayOf(15, 54, 24, 5, 55, 25),
            intArrayOf(15, 43, 15, 10, 44, 16),
            intArrayOf(4, 144, 116, 4, 145, 117),
            intArrayOf(17, 68, 42),
            intArrayOf(17, 50, 22, 6, 51, 23),
            intArrayOf(19, 46, 16, 6, 47, 17),
            intArrayOf(2, 139, 111, 7, 140, 112),
            intArrayOf(17, 74, 46),
            intArrayOf(7, 54, 24, 16, 55, 25),
            intArrayOf(34, 37, 13),
            intArrayOf(4, 151, 121, 5, 152, 122),
            intArrayOf(4, 75, 47, 14, 76, 48),
            intArrayOf(11, 54, 24, 14, 55, 25),
            intArrayOf(16, 45, 15, 14, 46, 16),
            intArrayOf(6, 147, 117, 4, 148, 118),
            intArrayOf(6, 73, 45, 14, 74, 46),
            intArrayOf(11, 54, 24, 16, 55, 25),
            intArrayOf(30, 46, 16, 2, 47, 17),
            intArrayOf(8, 132, 106, 4, 133, 107),
            intArrayOf(8, 75, 47, 13, 76, 48),
            intArrayOf(7, 54, 24, 22, 55, 25),
            intArrayOf(22, 45, 15, 13, 46, 16),
            intArrayOf(10, 142, 114, 2, 143, 115),
            intArrayOf(19, 74, 46, 4, 75, 47),
            intArrayOf(28, 50, 22, 6, 51, 23),
            intArrayOf(33, 46, 16, 4, 47, 17),
            intArrayOf(8, 152, 122, 4, 153, 123),
            intArrayOf(22, 73, 45, 3, 74, 46),
            intArrayOf(8, 53, 23, 26, 54, 24),
            intArrayOf(12, 45, 15, 28, 46, 16),
            intArrayOf(3, 147, 117, 10, 148, 118),
            intArrayOf(3, 73, 45, 23, 74, 46),
            intArrayOf(4, 54, 24, 31, 55, 25),
            intArrayOf(11, 45, 15, 31, 46, 16),
            intArrayOf(7, 146, 116, 7, 147, 117),
            intArrayOf(21, 73, 45, 7, 74, 46),
            intArrayOf(1, 53, 23, 37, 54, 24),
            intArrayOf(19, 45, 15, 26, 46, 16),
            intArrayOf(5, 145, 115, 10, 146, 116),
            intArrayOf(19, 75, 47, 10, 76, 48),
            intArrayOf(15, 54, 24, 25, 55, 25),
            intArrayOf(23, 45, 15, 25, 46, 16),
            intArrayOf(13, 145, 115, 3, 146, 116),
            intArrayOf(2, 74, 46, 29, 75, 47),
            intArrayOf(42, 54, 24, 1, 55, 25),
            intArrayOf(23, 45, 15, 28, 46, 16),
            intArrayOf(17, 145, 115),
            intArrayOf(10, 74, 46, 23, 75, 47),
            intArrayOf(10, 54, 24, 35, 55, 25),
            intArrayOf(19, 45, 15, 35, 46, 16),
            intArrayOf(17, 145, 115, 1, 146, 116),
            intArrayOf(14, 74, 46, 21, 75, 47),
            intArrayOf(29, 54, 24, 19, 55, 25),
            intArrayOf(11, 45, 15, 46, 46, 16),
            intArrayOf(13, 145, 115, 6, 146, 116),
            intArrayOf(14, 74, 46, 23, 75, 47),
            intArrayOf(44, 54, 24, 7, 55, 25),
            intArrayOf(59, 46, 16, 1, 47, 17),
            intArrayOf(12, 151, 121, 7, 152, 122),
            intArrayOf(12, 75, 47, 26, 76, 48),
            intArrayOf(39, 54, 24, 14, 55, 25),
            intArrayOf(22, 45, 15, 41, 46, 16),
            intArrayOf(6, 151, 121, 14, 152, 122),
            intArrayOf(6, 75, 47, 34, 76, 48),
            intArrayOf(46, 54, 24, 10, 55, 25),
            intArrayOf(2, 45, 15, 64, 46, 16),
            intArrayOf(17, 152, 122, 4, 153, 123),
            intArrayOf(29, 74, 46, 14, 75, 47),
            intArrayOf(49, 54, 24, 10, 55, 25),
            intArrayOf(24, 45, 15, 46, 46, 16),
            intArrayOf(4, 152, 122, 18, 153, 123),
            intArrayOf(13, 74, 46, 32, 75, 47),
            intArrayOf(48, 54, 24, 14, 55, 25),
            intArrayOf(42, 45, 15, 32, 46, 16),
            intArrayOf(20, 147, 117, 4, 148, 118),
            intArrayOf(40, 75, 47, 7, 76, 48),
            intArrayOf(43, 54, 24, 22, 55, 25),
            intArrayOf(10, 45, 15, 67, 46, 16),
            intArrayOf(19, 148, 118, 6, 149, 119),
            intArrayOf(18, 75, 47, 31, 76, 48),
            intArrayOf(34, 54, 24, 34, 55, 25),
            intArrayOf(20, 45, 15, 61, 46, 16)
        )

        fun getRSBlocks(
            typeNumber: Int,
            errorCorrectionLevel: ErrorCorrectionLevel
        ): Array<RSBlock> =
            RS_BLOCK_TABLE[(typeNumber - 1) * 4 + errorCorrectionLevel.ordinal]
                .let { rsBlock ->
                    if (rsBlock.size == 3) {
                        val block = RSBlock(rsBlock[1], rsBlock[2])
                        Array(rsBlock[0]) { block }
                    } else {
                        val blocksSize = rsBlock[0] + rsBlock[3]
                        val firstBlock = RSBlock(rsBlock[1], rsBlock[2])
                        val secondBlock = RSBlock(rsBlock[4], rsBlock[5])

                        Array(blocksSize) {
                            if (it < rsBlock[0]) {
                                firstBlock
                            } else {
                                secondBlock
                            }
                        }
                    }
                }
    }
}
