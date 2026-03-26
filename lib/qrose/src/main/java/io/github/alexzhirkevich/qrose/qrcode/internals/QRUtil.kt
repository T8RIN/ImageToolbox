package io.github.alexzhirkevich.qrose.qrcode.internals

import io.github.alexzhirkevich.qrose.qrcode.ErrorCorrectionLevel
import io.github.alexzhirkevich.qrose.qrcode.MaskPattern
import io.github.alexzhirkevich.qrose.qrcode.MaskPattern.PATTERN000
import io.github.alexzhirkevich.qrose.qrcode.MaskPattern.PATTERN001
import io.github.alexzhirkevich.qrose.qrcode.MaskPattern.PATTERN010
import io.github.alexzhirkevich.qrose.qrcode.MaskPattern.PATTERN011
import io.github.alexzhirkevich.qrose.qrcode.MaskPattern.PATTERN100
import io.github.alexzhirkevich.qrose.qrcode.MaskPattern.PATTERN101
import io.github.alexzhirkevich.qrose.qrcode.MaskPattern.PATTERN110
import io.github.alexzhirkevich.qrose.qrcode.MaskPattern.PATTERN111
import io.github.alexzhirkevich.qrose.qrcode.QRCodeDataType

/**
 * Rewritten in Kotlin from the [original (GitHub)](https://github.com/kazuhikoarase/qrcode-generator/blob/master/java/src/main/java/com/d_project/qrcode/QRUtil.java)
 *
 * @author Rafael Lins - g0dkar
 * @author Kazuhiko Arase - kazuhikoarase
 */
internal object QRUtil {
    fun getPatternPosition(typeNumber: Int): IntArray = PATTERN_POSITION_TABLE[typeNumber - 1]

    private val PATTERN_POSITION_TABLE = arrayOf(
        intArrayOf(),
        intArrayOf(6, 18),
        intArrayOf(6, 22),
        intArrayOf(6, 26),
        intArrayOf(6, 30),
        intArrayOf(6, 34),
        intArrayOf(6, 22, 38),
        intArrayOf(6, 24, 42),
        intArrayOf(6, 26, 46),
        intArrayOf(6, 28, 50),
        intArrayOf(6, 30, 54),
        intArrayOf(6, 32, 58),
        intArrayOf(6, 34, 62),
        intArrayOf(6, 26, 46, 66),
        intArrayOf(6, 26, 48, 70),
        intArrayOf(6, 26, 50, 74),
        intArrayOf(6, 30, 54, 78),
        intArrayOf(6, 30, 56, 82),
        intArrayOf(6, 30, 58, 86),
        intArrayOf(6, 34, 62, 90),
        intArrayOf(6, 28, 50, 72, 94),
        intArrayOf(6, 26, 50, 74, 98),
        intArrayOf(6, 30, 54, 78, 102),
        intArrayOf(6, 28, 54, 80, 106),
        intArrayOf(6, 32, 58, 84, 110),
        intArrayOf(6, 30, 58, 86, 114),
        intArrayOf(6, 34, 62, 90, 118),
        intArrayOf(6, 26, 50, 74, 98, 122),
        intArrayOf(6, 30, 54, 78, 102, 126),
        intArrayOf(6, 26, 52, 78, 104, 130),
        intArrayOf(6, 30, 56, 82, 108, 134),
        intArrayOf(6, 34, 60, 86, 112, 138),
        intArrayOf(6, 30, 58, 86, 114, 142),
        intArrayOf(6, 34, 62, 90, 118, 146),
        intArrayOf(6, 30, 54, 78, 102, 126, 150),
        intArrayOf(6, 24, 50, 76, 102, 128, 154),
        intArrayOf(6, 28, 54, 80, 106, 132, 158),
        intArrayOf(6, 32, 58, 84, 110, 136, 162),
        intArrayOf(6, 26, 54, 82, 110, 138, 166),
        intArrayOf(6, 30, 58, 86, 114, 142, 170)
    )

    private val MAX_LENGTH = arrayOf(
        arrayOf(
            intArrayOf(41, 25, 17, 10),
            intArrayOf(34, 20, 14, 8),
            intArrayOf(27, 16, 11, 7),
            intArrayOf(17, 10, 7, 4)
        ),
        arrayOf(
            intArrayOf(77, 47, 32, 20),
            intArrayOf(63, 38, 26, 16),
            intArrayOf(48, 29, 20, 12),
            intArrayOf(34, 20, 14, 8)
        ),
        arrayOf(
            intArrayOf(127, 77, 53, 32),
            intArrayOf(101, 61, 42, 26),
            intArrayOf(77, 47, 32, 20),
            intArrayOf(58, 35, 24, 15)
        ),
        arrayOf(
            intArrayOf(187, 114, 78, 48),
            intArrayOf(149, 90, 62, 38),
            intArrayOf(111, 67, 46, 28),
            intArrayOf(82, 50, 34, 21)
        ),
        arrayOf(
            intArrayOf(255, 154, 106, 65),
            intArrayOf(202, 122, 84, 52),
            intArrayOf(144, 87, 60, 37),
            intArrayOf(106, 64, 44, 27)
        ),
        arrayOf(
            intArrayOf(322, 195, 134, 82),
            intArrayOf(255, 154, 106, 65),
            intArrayOf(178, 108, 74, 45),
            intArrayOf(139, 84, 58, 36)
        ),
        arrayOf(
            intArrayOf(370, 224, 154, 95),
            intArrayOf(293, 178, 122, 75),
            intArrayOf(207, 125, 86, 53),
            intArrayOf(154, 93, 64, 39)
        ),
        arrayOf(
            intArrayOf(461, 279, 192, 118),
            intArrayOf(365, 221, 152, 93),
            intArrayOf(259, 157, 108, 66),
            intArrayOf(202, 122, 84, 52)
        ),
        arrayOf(
            intArrayOf(552, 335, 230, 141),
            intArrayOf(432, 262, 180, 111),
            intArrayOf(312, 189, 130, 80),
            intArrayOf(235, 143, 98, 60)
        ),
        arrayOf(
            intArrayOf(652, 395, 271, 167),
            intArrayOf(513, 311, 213, 131),
            intArrayOf(364, 221, 151, 93),
            intArrayOf(288, 174, 119, 74)
        ),
        arrayOf(
            intArrayOf(772, 468, 321, 198),
            intArrayOf(604, 366, 251, 155),
            intArrayOf(427, 259, 177, 109),
            intArrayOf(331, 200, 137, 85)
        ),
        arrayOf(
            intArrayOf(883, 535, 367, 226),
            intArrayOf(691, 419, 287, 177),
            intArrayOf(489, 296, 203, 125),
            intArrayOf(374, 227, 155, 96)
        ),
        arrayOf(
            intArrayOf(1022, 619, 425, 262),
            intArrayOf(796, 483, 331, 204),
            intArrayOf(580, 352, 241, 149),
            intArrayOf(427, 259, 177, 109)
        ),
        arrayOf(
            intArrayOf(1101, 667, 458, 282),
            intArrayOf(871, 528, 362, 223),
            intArrayOf(621, 376, 258, 159),
            intArrayOf(468, 283, 194, 120)
        ),
        arrayOf(
            intArrayOf(1250, 758, 520, 320),
            intArrayOf(991, 600, 412, 254),
            intArrayOf(703, 426, 292, 180),
            intArrayOf(530, 321, 220, 136)
        ),
        arrayOf(
            intArrayOf(1408, 854, 586, 361),
            intArrayOf(1082, 656, 450, 277),
            intArrayOf(775, 470, 322, 198),
            intArrayOf(602, 365, 250, 154)
        ),
        arrayOf(
            intArrayOf(1548, 938, 644, 397),
            intArrayOf(1212, 734, 504, 310),
            intArrayOf(876, 531, 364, 224),
            intArrayOf(674, 408, 280, 173)
        ),
        arrayOf(
            intArrayOf(1725, 1046, 718, 442),
            intArrayOf(1346, 816, 560, 345),
            intArrayOf(948, 574, 394, 243),
            intArrayOf(746, 452, 310, 191)
        ),
        arrayOf(
            intArrayOf(1903, 1153, 792, 488),
            intArrayOf(1500, 909, 624, 384),
            intArrayOf(1063, 644, 442, 272),
            intArrayOf(813, 493, 338, 208)
        ),
        arrayOf(
            intArrayOf(2061, 1249, 858, 528),
            intArrayOf(1600, 970, 666, 410),
            intArrayOf(1159, 702, 482, 297),
            intArrayOf(919, 557, 382, 235)
        ),
        arrayOf(
            intArrayOf(2232, 1352, 929, 572),
            intArrayOf(1708, 1035, 711, 438),
            intArrayOf(1224, 742, 509, 314),
            intArrayOf(969, 587, 403, 248)
        ),
        arrayOf(
            intArrayOf(2409, 1460, 1003, 618),
            intArrayOf(1872, 1134, 779, 480),
            intArrayOf(1358, 823, 565, 348),
            intArrayOf(1056, 640, 439, 270)
        ),
        arrayOf(
            intArrayOf(2620, 1588, 1091, 672),
            intArrayOf(2059, 1248, 857, 528),
            intArrayOf(1468, 890, 611, 376),
            intArrayOf(1108, 672, 461, 284)
        ),
        arrayOf(
            intArrayOf(2812, 1704, 1171, 721),
            intArrayOf(2188, 1326, 911, 561),
            intArrayOf(1588, 963, 661, 407),
            intArrayOf(1228, 744, 511, 315)
        ),
        arrayOf(
            intArrayOf(3057, 1853, 1273, 784),
            intArrayOf(2395, 1451, 997, 614),
            intArrayOf(1718, 1041, 715, 440),
            intArrayOf(1286, 779, 535, 330)
        ),
        arrayOf(
            intArrayOf(3283, 1990, 1367, 842),
            intArrayOf(2544, 1542, 1059, 652),
            intArrayOf(1804, 1094, 751, 462),
            intArrayOf(1425, 864, 593, 365)
        ),
        arrayOf(
            intArrayOf(3517, 2132, 1465, 902),
            intArrayOf(2701, 1637, 1125, 692),
            intArrayOf(1933, 1172, 805, 496),
            intArrayOf(1501, 910, 625, 385)
        ),
        arrayOf(
            intArrayOf(3669, 2223, 1528, 940),
            intArrayOf(2857, 1732, 1190, 732),
            intArrayOf(2085, 1263, 868, 534),
            intArrayOf(1581, 958, 658, 405)
        ),
        arrayOf(
            intArrayOf(3909, 2369, 1628, 1002),
            intArrayOf(3035, 1839, 1264, 778),
            intArrayOf(2181, 1322, 908, 559),
            intArrayOf(1677, 1016, 698, 430)
        ),
        arrayOf(
            intArrayOf(4158, 2520, 1732, 1066),
            intArrayOf(3289, 1994, 1370, 843),
            intArrayOf(2358, 1429, 982, 604),
            intArrayOf(1782, 1080, 742, 457)
        ),
        arrayOf(
            intArrayOf(4417, 2677, 1840, 1132),
            intArrayOf(3486, 2113, 1452, 894),
            intArrayOf(2473, 1499, 1030, 634),
            intArrayOf(1897, 1150, 790, 486)
        ),
        arrayOf(
            intArrayOf(4686, 2840, 1952, 1201),
            intArrayOf(3693, 2238, 1538, 947),
            intArrayOf(2670, 1618, 1112, 684),
            intArrayOf(2022, 1226, 842, 518)
        ),
        arrayOf(
            intArrayOf(4965, 3009, 2068, 1273),
            intArrayOf(3909, 2369, 1628, 1002),
            intArrayOf(2805, 1700, 1168, 719),
            intArrayOf(2157, 1307, 898, 553)
        ),
        arrayOf(
            intArrayOf(5253, 3183, 2188, 1347),
            intArrayOf(4134, 2506, 1722, 1060),
            intArrayOf(2949, 1787, 1228, 756),
            intArrayOf(2301, 1394, 958, 590)
        )
    )

    fun getMaxLength(
        typeNumber: Int,
        dataType: QRCodeDataType,
        errorCorrectionLevel: ErrorCorrectionLevel
    ): Int =
        MAX_LENGTH[typeNumber - 1][errorCorrectionLevel.ordinal][dataType.ordinal]

    fun getErrorCorrectPolynomial(errorCorrectLength: Int): Polynomial {
        var a = Polynomial(intArrayOf(1))
        for (i in 0 until errorCorrectLength) {
            a = a.multiply(Polynomial(intArrayOf(1, QRMath.gexp(i))))
        }
        return a
    }

    /**
     * Each Mask Pattern [applies a different formula](https://www.thonky.com/qr-code-tutorial/mask-patterns).
     */
    fun getMask(maskPattern: MaskPattern, i: Int, j: Int): Boolean =
        when (maskPattern) {
            PATTERN000 -> (i + j) % 2 == 0
            PATTERN001 -> i % 2 == 0
            PATTERN010 -> j % 3 == 0
            PATTERN011 -> (i + j) % 3 == 0
            PATTERN100 -> (i / 2 + j / 3) % 2 == 0
            PATTERN101 -> (i * j) % 2 + (i * j) % 3 == 0
            PATTERN110 -> ((i * j) % 2 + (i * j) % 3) % 2 == 0
            PATTERN111 -> ((i * j) % 3 + (i + j) % 2) % 2 == 0
        }

    /**
     * Returns a suitable [QRCodeDataType] to the given input String based on a simple matching.
     *
     * @see QRCodeDataType
     */
    fun getDataType(s: String): QRCodeDataType =
        if (isAlphaNum(s)) {
            if (isNumber(s)) {
                QRCodeDataType.NUMBERS
            } else {
                QRCodeDataType.UPPER_ALPHA_NUM
            }
        } else {
            QRCodeDataType.DEFAULT
        }

    private fun isNumber(s: String) = s.matches(Regex("^\\d+$"))
    private fun isAlphaNum(s: String) = s.matches(Regex("^[0-9A-Z $%*+\\-./:]+$"))

    private const val G15 = (
            1 shl 10 or (1 shl 8) or (1 shl 5)
                    or (1 shl 4) or (1 shl 2) or (1 shl 1) or (1 shl 0)
            )
    private const val G18 = (
            1 shl 12 or (1 shl 11) or (1 shl 10)
                    or (1 shl 9) or (1 shl 8) or (1 shl 5) or (1 shl 2) or (1 shl 0)
            )
    private const val G15_MASK = (
            1 shl 14 or (1 shl 12) or (1 shl 10)
                    or (1 shl 4) or (1 shl 1)
            )

    fun getBCHTypeInfo(data: Int): Int {
        var d = data shl 10
        while (getBCHDigit(d) - getBCHDigit(G15) >= 0) {
            d = d xor (G15 shl getBCHDigit(d) - getBCHDigit(G15))
        }
        return data shl 10 or d xor G15_MASK
    }

    fun getBCHTypeNumber(data: Int): Int {
        var d = data shl 12
        while (getBCHDigit(d) - getBCHDigit(G18) >= 0) {
            d = d xor (G18 shl getBCHDigit(d) - getBCHDigit(G18))
        }
        return data shl 12 or d
    }

    private fun getBCHDigit(data: Int): Int {
        var i = data
        var digit = 0
        while (i != 0) {
            digit++
            i = i ushr 1
        }
        return digit
    }
}
