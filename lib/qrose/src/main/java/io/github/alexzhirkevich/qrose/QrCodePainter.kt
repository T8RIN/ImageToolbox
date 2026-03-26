package io.github.alexzhirkevich.qrose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import io.github.alexzhirkevich.qrose.options.Neighbors
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrBrushMode
import io.github.alexzhirkevich.qrose.options.QrCodeMatrix
import io.github.alexzhirkevich.qrose.options.QrColors
import io.github.alexzhirkevich.qrose.options.QrErrorCorrectionLevel
import io.github.alexzhirkevich.qrose.options.QrLogo
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrOptions
import io.github.alexzhirkevich.qrose.options.QrShapeModifier
import io.github.alexzhirkevich.qrose.options.QrShapes
import io.github.alexzhirkevich.qrose.options.dsl.QrOptionsBuilderScope
import io.github.alexzhirkevich.qrose.options.isSpecified
import io.github.alexzhirkevich.qrose.options.neighbors
import io.github.alexzhirkevich.qrose.options.newPath
import io.github.alexzhirkevich.qrose.qrcode.ErrorCorrectionLevel
import io.github.alexzhirkevich.qrose.qrcode.QRCode
import kotlin.math.roundToInt

/**
 * Create and remember QR code painter
 *
 * @param data QR code payload
 * @param keys keys of the [options] block. QR code will be re-generated when any key is changed.
 * @param options [QrOptions] builder block
 * */
@Composable
fun rememberQrCodePainter(
    data: String,
    vararg keys: Any?,
    onSuccess: () -> Unit = {},
    onFailure: (Throwable) -> Unit = {},
    options: QrOptionsBuilderScope.() -> Unit
): QrCodePainter = rememberQrCodePainter(
    data = data,
    options = remember(keys) { QrOptions(options) },
    onFailure = onFailure,
    onSuccess = onSuccess
)

/**
 * Create and remember QR code painter
 *
 * @param data QR code payload
 * @param options QR code styling options
 * */
@Composable
fun rememberQrCodePainter(
    data: String,
    options: QrOptions,
    onSuccess: () -> Unit = {},
    onFailure: (Throwable) -> Unit = {}
): QrCodePainter = remember(data, options, onFailure, onSuccess) {
    QrCodePainter(
        data = data,
        options = options,
        onFailure = onFailure,
        onSuccess = onSuccess
    )
}

@Composable
fun rememberQrCodePainter(
    data: String,
    shapes: QrShapes = QrShapes(),
    colors: QrColors = QrColors(),
    logo: QrLogo = QrLogo(),
    errorCorrectionLevel: QrErrorCorrectionLevel = QrErrorCorrectionLevel.Auto,
    fourEyed: Boolean = false,
    onFailure: (Throwable) -> Unit = {},
    onSuccess: () -> Unit = {},
): QrCodePainter = rememberQrCodePainter(
    data = data,
    options = remember(shapes, colors, logo, errorCorrectionLevel, fourEyed) {
        QrOptions(
            shapes = shapes,
            colors = colors,
            logo = logo,
            errorCorrectionLevel = errorCorrectionLevel,
            fourEyed = fourEyed
        )
    },
    onFailure = onFailure,
    onSuccess = onSuccess
)

/**
 * Encodes [data] payload and renders it into the compose [Painter] using styling [options]
 * */
@Immutable
class QrCodePainter(
    val data: String,
    val options: QrOptions = QrOptions(),
    val onSuccess: () -> Unit,
    val onFailure: (Throwable) -> Unit
) : CachedPainter() {
    private val initialMatrixSize: Int

    private val actualCodeMatrix = options.shapes.code.run {

        val initialMatrix = runCatching {
            QRCode(
                data = data,
                errorCorrectionLevel =
                    if (options.errorCorrectionLevel == QrErrorCorrectionLevel.Auto)
                        options.errorCorrectionLevel.fit(options).lvl
                    else options.errorCorrectionLevel.lvl
            ).encode(maskPattern = options.maskPattern)
        }.onFailure(onFailure).onSuccess {
            if (it.size == 0) onFailure(IllegalArgumentException("Failed to generate QR code"))
            else onSuccess()
        }.getOrDefault(QrCodeMatrix(1))

        initialMatrixSize = initialMatrix.size

        initialMatrix.transform()
    }

    private var codeMatrix = actualCodeMatrix

    override val intrinsicSize: Size = Size(
        codeMatrix.size.toFloat() * 10f,
        codeMatrix.size.toFloat() * 10f
    )


    private val shapeIncrease = (codeMatrix.size - initialMatrixSize) / 2

    private val balls = mutableListOf(
        2 + shapeIncrease to 2 + shapeIncrease,
        2 + shapeIncrease to initialMatrixSize - 5 + shapeIncrease,
        initialMatrixSize - 5 + shapeIncrease to 2 + shapeIncrease
    ).apply {
        if (options.fourEyed)
            this += initialMatrixSize - 5 + shapeIncrease to initialMatrixSize - 5 + shapeIncrease
    }.toList()

    private val frames = mutableListOf(
        shapeIncrease to shapeIncrease,
        shapeIncrease to initialMatrixSize - 7 + shapeIncrease,
        initialMatrixSize - 7 + shapeIncrease to shapeIncrease
    ).apply {
        if (options.fourEyed) {
            this += initialMatrixSize - 7 + shapeIncrease to initialMatrixSize - 7 + shapeIncrease
        }
    }.toList()

    private val shouldSeparateDarkPixels
        get() = options.colors.dark.mode == QrBrushMode.Separate

    private val shouldSeparateLightPixels
        get() = options.colors.light.mode == QrBrushMode.Separate

    private val shouldSeparateFrames
        get() = options.colors.frame.isSpecified || shouldSeparateDarkPixels

    private val shouldSeparateBalls
        get() = options.colors.ball.isSpecified || shouldSeparateDarkPixels

    override fun toString(): String {
        return "QrCodePainter(data = $data)"
    }

    override fun hashCode(): Int {
        return data.hashCode() * 31 + options.hashCode()
    }

    private val DrawScope.logoSize
        get() = size * options.logo.size

    private val DrawScope.logoPaddingSize
        get() = logoSize.width * (1 + options.logo.padding.size)

    private val DrawScope.pixelSize: Float
        get() = minOf(size.width, size.height) / codeMatrix.size


    override fun DrawScope.onCache() {
        draw()
    }

    private fun DrawScope.draw() {
        if (actualCodeMatrix.size > 1) {
            runCatching {
                drawRect(
                    options.colors.background.brush(
                        size = maxOf(size.width, size.height),
                        neighbors = Neighbors.Empty
                    )
                )

                val pixelSize = pixelSize

                prepareLogo(pixelSize)

                val (dark, light) = createMainElements(pixelSize)

                if (shouldSeparateDarkPixels || shouldSeparateLightPixels) {
                    drawSeparatePixels(pixelSize)
                }

                if (!shouldSeparateLightPixels) {
                    drawPath(
                        path = light,
                        brush = options.colors.light
                            .brush(pixelSize * codeMatrix.size, Neighbors.Empty),
                    )
                }

                if (!shouldSeparateDarkPixels) {
                    drawPath(
                        path = dark,
                        brush = options.colors.dark
                            .brush(pixelSize * codeMatrix.size, Neighbors.Empty),
                    )
                }

                if (shouldSeparateFrames) {
                    drawFrames(pixelSize)
                }

                if (shouldSeparateBalls) {
                    drawBalls(pixelSize)
                }

                drawLogo()
            }.onFailure(onFailure)
        }
    }

    private fun DrawScope.drawSeparatePixels(
        pixelSize: Float,
    ) {
        val darkPaint = darkPaintFactory(pixelSize)
        val lightPaint = lightPaintFactory(pixelSize)

        val darkPixelPath = darkPixelPathFactory(pixelSize)
        val lightPixelPath = lightPixelPathFactory(pixelSize)

        repeat(codeMatrix.size) { i ->
            repeat(codeMatrix.size) inner@{ j ->
                if (isInsideFrameOrBall(i, j))
                    return@inner

                translate(
                    left = i * pixelSize,
                    top = j * pixelSize
                ) {
                    if (shouldSeparateDarkPixels && codeMatrix[i, j] == QrCodeMatrix.PixelType.DarkPixel) {
                        val n = codeMatrix.neighbors(i, j)
                        drawPath(
                            path = darkPixelPath.next(n),
                            brush = darkPaint.next(n),
                        )
                    }
                    if (shouldSeparateLightPixels && codeMatrix[i, j] == QrCodeMatrix.PixelType.LightPixel) {
                        val n = codeMatrix.neighbors(i, j)

                        drawPath(
                            path = lightPixelPath.next(n),
                            brush = lightPaint.next(n),
                        )
                    }
                }
            }
        }
    }


    private fun DrawScope.prepareLogo(pixelSize: Float) {

        val ps = logoPaddingSize

        if (options.logo.padding is QrLogoPadding.Natural) {
            val logoPath = options.logo.shape.newPath(
                size = ps,
                neighbors = Neighbors.Empty
            ).apply {
                translate(
                    Offset(
                        (size.width - ps) / 2f,
                        (size.height - ps) / 2f,
                    )
                )
            }

            val darkPathF = darkPixelPathFactory(pixelSize)
            val lightPathF = lightPixelPathFactory(pixelSize)

            val logoPixels = (codeMatrix.size *
                    options.logo.size.coerceIn(0f, 1f) *
                    (1 + options.logo.padding.size.coerceIn(0f, 1f))).roundToInt() + 1

            val xRange =
                (codeMatrix.size - logoPixels) / 2 until (codeMatrix.size + logoPixels) / 2
            val yRange =
                (codeMatrix.size - logoPixels) / 2 until (codeMatrix.size + logoPixels) / 2

            for (x in xRange) {
                for (y in yRange) {
                    val neighbors = codeMatrix.neighbors(x, y)
                    val offset = Offset(x * pixelSize, y * pixelSize)

                    val darkPath = darkPathF.next(neighbors).apply {
                        translate(offset)
                    }
                    val lightPath = lightPathF.next(neighbors).apply {
                        translate(offset)
                    }

                    if (
                        codeMatrix[x, y] == QrCodeMatrix.PixelType.DarkPixel &&
                        logoPath.intersects(darkPath) ||
                        codeMatrix[x, y] == QrCodeMatrix.PixelType.LightPixel &&
                        logoPath.intersects(lightPath)
                    ) {
                        codeMatrix[x, y] = QrCodeMatrix.PixelType.Logo
                    }
                }
            }
        }
    }

    private fun DrawScope.drawLogo() {

        val ps = logoPaddingSize

        if (options.logo.padding is QrLogoPadding.Accurate) {
            val path = options.logo.shape.newPath(
                size = ps,
                neighbors = Neighbors.Empty
            )

            translate(
                left = center.x - ps / 2,
                top = center.y - ps / 2
            ) {
                drawPath(path, Color.Black, blendMode = BlendMode.Clear)
            }
        }

        options.logo.painter?.let {
            it.run {
                translate(
                    left = center.x - logoSize.width / 2,
                    top = center.y - logoSize.height / 2
                ) {
                    draw(logoSize)
                }
            }
        }
    }


    private fun DrawScope.drawBalls(
        pixelSize: Float
    ) {
        val brush by ballBrushFactory(pixelSize)
        val path by ballShapeFactory(pixelSize)

        balls.forEach {

            translate(
                it.first * pixelSize,
                it.second * pixelSize
            ) {
                drawPath(
                    path = path,
                    brush = brush,
                )
            }
        }
    }


    private fun DrawScope.drawFrames(
        pixelSize: Float
    ) {
        val ballBrush by frameBrushFactory(pixelSize)
        val ballPath by frameShapeFactory(pixelSize)

        frames.forEach {

            translate(
                it.first * pixelSize,
                it.second * pixelSize
            ) {
                drawPath(
                    path = ballPath,
                    brush = ballBrush,
                )
            }
        }
    }

    private fun createMainElements(
        pixelSize: Float
    ): Pair<Path, Path> {

        val darkPath = Path().apply {
            fillType = PathFillType.EvenOdd
        }
        val lightPath = Path().apply {
            fillType = PathFillType.EvenOdd
        }

        val rotatedFramePath by frameShapeFactory(pixelSize)
        val rotatedBallPath by ballShapeFactory(pixelSize)

        val darkPixelPathFactory = darkPixelPathFactory(pixelSize)
        val lightPixelPathFactory = lightPixelPathFactory(pixelSize)

        for (x in 0 until codeMatrix.size) {
            for (y in 0 until codeMatrix.size) {

                val neighbors = codeMatrix.neighbors(x, y)

                when {
                    !shouldSeparateFrames && isFrameStart(x, y) ->
                        darkPath
                            .addPath(
                                path = rotatedFramePath,
                                offset = Offset(x * pixelSize, y * pixelSize)
                            )


                    !shouldSeparateBalls && isBallStart(x, y) ->
                        darkPath
                            .addPath(
                                path = rotatedBallPath,
                                offset = Offset(x * pixelSize, y * pixelSize)
                            )

                    isInsideFrameOrBall(x, y) -> Unit

                    !shouldSeparateDarkPixels && codeMatrix[x, y] == QrCodeMatrix.PixelType.DarkPixel ->
                        darkPath
                            .addPath(
                                path = darkPixelPathFactory.next(neighbors),
                                offset = Offset(x * pixelSize, y * pixelSize)
                            )

                    !shouldSeparateLightPixels && codeMatrix[x, y] == QrCodeMatrix.PixelType.LightPixel ->
                        lightPath
                            .addPath(
                                path = lightPixelPathFactory.next(neighbors),
                                offset = Offset(x * pixelSize, y * pixelSize)
                            )

                }
            }
        }
        return darkPath to lightPath
    }


    private fun isFrameStart(x: Int, y: Int) =
        x - shapeIncrease == 0 && y - shapeIncrease == 0 ||
                x - shapeIncrease == 0 && y - shapeIncrease == initialMatrixSize - 7 ||
                x - shapeIncrease == initialMatrixSize - 7 && y - shapeIncrease == 0 ||
                options.fourEyed && x - shapeIncrease == initialMatrixSize - 7 && y - shapeIncrease == initialMatrixSize - 7

    private fun isBallStart(x: Int, y: Int) =
        x - shapeIncrease == 2 && y - shapeIncrease == initialMatrixSize - 5 ||
                x - shapeIncrease == initialMatrixSize - 5 && y - shapeIncrease == 2 ||
                x - shapeIncrease == 2 && y - shapeIncrease == 2 ||
                options.fourEyed && x - shapeIncrease == initialMatrixSize - 5 && y - shapeIncrease == initialMatrixSize - 5

    private fun isInsideFrameOrBall(x: Int, y: Int): Boolean {
        return x - shapeIncrease in -1..7 && y - shapeIncrease in -1..7 ||
                x - shapeIncrease in -1..7 && y - shapeIncrease in initialMatrixSize - 8 until initialMatrixSize + 1 ||
                x - shapeIncrease in initialMatrixSize - 8 until initialMatrixSize + 1 && y - shapeIncrease in -1..7 ||
                options.fourEyed && x - shapeIncrease in initialMatrixSize - 8 until initialMatrixSize + 1 && y - shapeIncrease in initialMatrixSize - 8 until initialMatrixSize + 1
    }

    private fun darkPaintFactory(pixelSize: Float) =
        pixelBrushFactory(
            brush = options.colors.dark,
            separate = shouldSeparateDarkPixels,
            pixelSize = pixelSize
        )

    private fun lightPaintFactory(pixelSize: Float) =
        pixelBrushFactory(
            brush = options.colors.light,
            separate = shouldSeparateLightPixels,
            pixelSize = pixelSize
        )

    private fun ballBrushFactory(pixelSize: Float) =
        eyeBrushFactory(brush = options.colors.ball, pixelSize = pixelSize)

    private fun frameBrushFactory(pixelSize: Float) =
        eyeBrushFactory(brush = options.colors.frame, pixelSize = pixelSize)


    private fun ballShapeFactory(pixelSize: Float): Lazy<Path> =
        rotatedPathFactory(
            shape = options.shapes.ball,
            shapeSize = pixelSize * BALL_SIZE
        )

    private fun frameShapeFactory(pixelSize: Float): Lazy<Path> =
        rotatedPathFactory(
            shape = options.shapes.frame,
            shapeSize = pixelSize * FRAME_SIZE
        )

    private fun darkPixelPathFactory(pixelSize: Float) =
        pixelPathFactory(
            shape = options.shapes.darkPixel,
            pixelSize = pixelSize
        )

    private fun lightPixelPathFactory(pixelSize: Float) =
        pixelPathFactory(
            shape = options.shapes.lightPixel,
            pixelSize = pixelSize
        )

    private fun pixelPathFactory(
        shape: QrShapeModifier,
        pixelSize: Float
    ): NeighborsBasedFactory<Path> {
        val path = Path()

        return NeighborsBasedFactory {
            path.rewind()
            path.apply {
                shape.run {
                    path(pixelSize, it)
                }
            }
            path
        }
    }

    private fun rotatedPathFactory(
        shape: QrShapeModifier,
        shapeSize: Float,
    ): Lazy<Path> {

        var number = 0

        val path = Path()

        val factory = NeighborsBasedFactory {
            path.apply {
                rewind()
                fillType = PathFillType.EvenOdd
                shape.run { path(shapeSize, it) }
            }
        }

        return Recreating {
            factory.next(Neighbors.forEyeWithNumber(number, options.fourEyed)).apply {
                if (options.shapes.centralSymmetry) {
                    val angle = when (number) {
                        0 -> 0f
                        1 -> -90f
                        2 -> 90f
                        else -> 180f
                    }
                    rotate(angle, Offset(shapeSize / 2, shapeSize / 2))
                }
            }.also {
                number = (number + 1) % if (options.fourEyed) 4 else 3
            }
        }
    }


    private fun eyeBrushFactory(
        brush: QrBrush,
        pixelSize: Float
    ): Lazy<Brush> {
        val b = brush
            .takeIf { it.isSpecified }
            ?: QrBrush.Default

        var number = 0

        val factory = {
            b.brush(
                size = pixelSize,
                neighbors = Neighbors.forEyeWithNumber(number, options.fourEyed)
            ).also {
                number = (number + 1) % if (options.fourEyed) 4 else 3
            }
        }

        return Recreating(factory)
    }

    private fun pixelBrushFactory(
        brush: QrBrush,
        separate: Boolean,
        pixelSize: Float,
    ): NeighborsBasedFactory<Brush> {

        val size = if (separate)
            pixelSize
        else codeMatrix.size * pixelSize

        val joinBrush by lazy { brush.brush(size, Neighbors.Empty) }

        return NeighborsBasedFactory {
            if (separate)
                brush.brush(size, it)
            else joinBrush
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as QrCodePainter

        if (data != other.data) return false
        if (options != other.options) return false

        return true
    }
}

private const val BALL_SIZE = 3
private const val FRAME_SIZE = 7


private fun Path.rotate(degree: Float, pivot: Offset) {
    translate(-pivot)
    transform(Matrix().apply { rotateZ(degree) })
    translate(pivot)
}

private fun Path.intersects(other: Path) =
    Path.combine(
        operation = PathOperation.Intersect,
        path1 = this,
        path2 = other
    ).isEmpty.not()

private class Recreating<T>(
    private val factory: () -> T
) : Lazy<T> {
    override val value: T
        get() = factory()

    override fun isInitialized(): Boolean = true
}

private fun Neighbors.Companion.forEyeWithNumber(
    number: Int,
    fourthEyeEnabled: Boolean
): Neighbors {
    return when (number) {
        0 -> Neighbors(bottom = true, right = true, bottomRight = fourthEyeEnabled)
        1 -> Neighbors(bottom = fourthEyeEnabled, left = true, bottomLeft = true)
        2 -> Neighbors(top = true, topRight = true, right = fourthEyeEnabled)
        3 -> Neighbors(top = true, left = true, topLeft = true)

        else -> throw IllegalStateException("Incorrect eye number: $number")
    }
}

private fun interface NeighborsBasedFactory<T> {
    fun next(neighbors: Neighbors): T
}


private fun QrErrorCorrectionLevel.fit(
    options: QrOptions
): QrErrorCorrectionLevel {

    val logoSize = options.logo.size *
            (1 + options.logo.padding.size) //*
//            options.shapes.code.shapeSizeIncrease

    val hasLogo = options.logo.padding != QrLogoPadding.Empty

    return if (this == QrErrorCorrectionLevel.Auto)
        when {
            !hasLogo -> QrErrorCorrectionLevel.Low
            logoSize > .3 -> QrErrorCorrectionLevel.High
            logoSize in .2.. .3 && lvl < ErrorCorrectionLevel.Q ->
                QrErrorCorrectionLevel.MediumHigh

            logoSize > .05f && lvl < ErrorCorrectionLevel.M ->
                QrErrorCorrectionLevel.Medium

            else -> this
        } else this
}