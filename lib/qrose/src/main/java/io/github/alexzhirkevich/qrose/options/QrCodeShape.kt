package io.github.alexzhirkevich.qrose.options

import androidx.compose.runtime.Immutable
import io.github.alexzhirkevich.qrose.options.QrCodeShape.Companion.Default
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random


/**
 * Shape of the QR-code pattern.
 *
 * Limitations:
 * - The [Default] shape is the smallest available.
 * Any custom shapes must have bigger size.
 * - QR code pattern must always be at the center of the matrix.
 * - QR code matrix is always square.
 * */
interface QrCodeShape {

    /**
     * How much was QR code size increased in fraction related to default size (1.0).
     * */
    val shapeSizeIncrease: Float

    /**
     * Transform receiver matrix or create new with bigger size.
     * */
    fun QrCodeMatrix.transform(): QrCodeMatrix

    companion object {
        val Default = object : QrCodeShape {
            override val shapeSizeIncrease: Float = 1f

            override fun QrCodeMatrix.transform(): QrCodeMatrix = this
        }
    }
}


fun QrCodeShape.Companion.circle(
    padding: Float = 1.1f,
    precise: Boolean = true,
    random: Random = Random(233),
): QrCodeShape = Circle(
    padding = padding,
    random = random,
    precise = precise
)

fun QrCodeShape.Companion.hexagon(
    rotation: Float = 30f,
    precise: Boolean = true,
    random: Random = Random(233),
): QrCodeShape = Hexagon(
    rotationDegree = rotation,
    random = random,
    precise = precise
)


@Immutable
private class Circle(
    private val padding: Float,
    private val random: Random,
    private val precise: Boolean
) : QrCodeShape {

    override val shapeSizeIncrease: Float =
        1 + (padding * sqrt(2.0) - 1).toFloat()

    override fun QrCodeMatrix.transform(): QrCodeMatrix {

        val padding = padding.coerceIn(1f, 2f)
        val added = (((size * padding * sqrt(2.0)) - size) / 2).roundToInt()

        val newSize = size + 2 * added
        val newMatrix = QrCodeMatrix(newSize)

        val center = newSize / 2f


        for (i in 0 until newSize) {
            for (j in 0 until newSize) {

                val notInSquare = (i <= added - 1 ||
                        j <= added - 1 ||
                        i >= added + size ||
                        j >= added + size)

                val inLargeCircle = isInCircle(center, i.toFloat(), j.toFloat(), center)
                if (notInSquare && inLargeCircle) {

                    val inSmallCircle = !precise || isInCircle(
                        center,
                        i.toFloat(),
                        j.toFloat(),
                        center - 1
                    )
                    newMatrix[i, j] = if (!inSmallCircle || random.nextBoolean())
                        QrCodeMatrix.PixelType.DarkPixel
                    else QrCodeMatrix.PixelType.LightPixel
                }
            }
        }

        for (i in 0 until size) {
            for (j in 0 until size) {
                newMatrix[added + i, added + j] = this[i, j]
            }
        }
        return newMatrix
    }

    fun isInCircle(center: Float, i: Float, j: Float, radius: Float): Boolean {
        return sqrt((center - i) * (center - i) + (center - j) * (center - j)) < radius
    }
}

@Immutable
private class Hexagon(
    rotationDegree: Float,
    private val random: Random,
    private val precise: Boolean
) : QrCodeShape {

    override val shapeSizeIncrease: Float = 1.6f

    override fun QrCodeMatrix.transform(): QrCodeMatrix {

        val a = sqrt(size * size / 1.268 / 1.268)

        val newSize = (1.575f * size).toInt()

        val newMatrix = QrCodeMatrix(newSize)

        val (x1, y1) = rotate(newSize / 2, newSize / 2)

        repeat(newSize) { i ->
            repeat(newSize) { j ->
                val (x, y) = rotate(i, j)

                val inLarge = isInHexagon(x, y, x1, y1, a)

                if (inLarge) {
                    val inSmall = !precise || isInHexagon(x, y, x1, y1, a - 1.1)

                    newMatrix[i, j] = if (!inSmall || random.nextBoolean())
                        QrCodeMatrix.PixelType.DarkPixel else QrCodeMatrix.PixelType.LightPixel
                }
            }
        }

        val diff = (newSize - size) / 2

        repeat(size) { i ->
            repeat(size) { j ->
                newMatrix[diff + i, diff + j] = this[i, j]
            }
        }

        return newMatrix
    }

    private fun isInHexagon(x1: Double, y1: Double, x2: Double, y2: Double, z: Double): Boolean {
        val x = abs(x1 - x2)
        val y = abs(y1 - y2)
        val py1 = z * 0.86602540378
        val px2 = z * 0.5088190451
        val py2 = z * 0.8592582628

        val p_angle_01 = -x * (py1 - y) - x * y
        val p_angle_20 = -y * (px2 - x) + x * (py2 - y)
        val p_angle_03 = y * z
        val p_angle_12 = -x * (py2 - y) - (px2 - x) * (py1 - y)
        val p_angle_32 = (z - x) * (py2 - y) + y * (px2 - x)
        val is_inside_1 = (p_angle_01 * p_angle_12 >= 0) && (p_angle_12 * p_angle_20 >= 0)
        val is_inside_2 = (p_angle_03 * p_angle_32 >= 0) && (p_angle_32 * p_angle_20 >= 0)

        return is_inside_1 || is_inside_2
    }

    private val rad = rotationDegree * 0.0174533

    private val si = sin(rad)
    private val co = cos(rad)

    private val isModulo60 = rotationDegree.roundToInt() % 60 == 0

    private fun rotate(x: Int, y: Int): Pair<Double, Double> {

        if (isModulo60) {
            return x.toDouble() to y.toDouble()
        }
        return (x * co - y * si) to (x * si + y * co)
    }

}