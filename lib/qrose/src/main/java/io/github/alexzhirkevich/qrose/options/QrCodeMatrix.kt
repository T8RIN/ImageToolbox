package io.github.alexzhirkevich.qrose.options


class QrCodeMatrix(val size: Int, initialFill: PixelType = PixelType.Background) {

    constructor(list: List<List<PixelType>>) : this(list.size) {
        types = list.flatten().toMutableList()
    }

    enum class PixelType { DarkPixel, LightPixel, Background, Logo }

    private var types = MutableList(size * size) {
        initialFill
    }

    operator fun get(i: Int, j: Int): PixelType {

        val outOfBound = when {
            i !in 0 until size -> i
            j !in 0 until size -> j
            else -> null
        }

        if (outOfBound != null)
            throw IndexOutOfBoundsException(
                "Index $outOfBound is out of 0..${size - 1} matrix bound"
            )

        return types[i + j * size]
    }

    operator fun set(i: Int, j: Int, type: PixelType) {

        val outOfBound = when {
            i !in 0 until size -> i
            j !in 0 until size -> j
            else -> null
        }

        if (outOfBound != null)
            throw IndexOutOfBoundsException(
                "Index $outOfBound is out of 0..${size - 1} matrix bound"
            )

        types[i + j * size] = type
    }

    fun copy(): QrCodeMatrix = QrCodeMatrix(size).apply {
        types = ArrayList(this@QrCodeMatrix.types)
    }
}

internal fun QrCodeMatrix.neighbors(i: Int, j: Int): Neighbors {

    fun cmp(i2: Int, j2: Int) = kotlin.runCatching {
        this[i2, j2] == this[i, j]
    }.getOrDefault(false)

    return Neighbors(
        topLeft = cmp(i - 1, j - 1),
        topRight = cmp(i + 1, j - 1),
        left = cmp(i - 1, j),
        top = cmp(i, j - 1),
        right = cmp(i + 1, j),
        bottomLeft = cmp(i - 1, j + 1),
        bottom = cmp(i, j + 1),
        bottomRight = cmp(i + 1, j + 1)
    )
}

