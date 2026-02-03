/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.t8rin.imagetoolbox.core.ui.widget.other

import android.graphics.Bitmap
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.core.graphics.createBitmap
import coil3.compose.asPainter
import coil3.imageLoader
import coil3.request.ImageRequest
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.resources.shapes.ArrowShape
import com.t8rin.imagetoolbox.core.resources.shapes.BookmarkShape
import com.t8rin.imagetoolbox.core.resources.shapes.BurgerShape
import com.t8rin.imagetoolbox.core.resources.shapes.CloverShape
import com.t8rin.imagetoolbox.core.resources.shapes.DropletShape
import com.t8rin.imagetoolbox.core.resources.shapes.EggShape
import com.t8rin.imagetoolbox.core.resources.shapes.ExplosionShape
import com.t8rin.imagetoolbox.core.resources.shapes.MapShape
import com.t8rin.imagetoolbox.core.resources.shapes.MaterialStarShape
import com.t8rin.imagetoolbox.core.resources.shapes.OctagonShape
import com.t8rin.imagetoolbox.core.resources.shapes.OvalShape
import com.t8rin.imagetoolbox.core.resources.shapes.PentagonShape
import com.t8rin.imagetoolbox.core.resources.shapes.PillShape
import com.t8rin.imagetoolbox.core.resources.shapes.ShieldShape
import com.t8rin.imagetoolbox.core.resources.shapes.ShurikenShape
import com.t8rin.imagetoolbox.core.resources.shapes.SmallMaterialStarShape
import com.t8rin.imagetoolbox.core.resources.shapes.SquircleShape
import com.t8rin.imagetoolbox.core.settings.presentation.model.IconShape
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.utils.toShape
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.applyPadding
import com.t8rin.imagetoolbox.core.ui.utils.painter.centerCrop
import com.t8rin.imagetoolbox.core.ui.utils.painter.roundCorners
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shimmer
import com.t8rin.imagetoolbox.core.ui.widget.other.QrCodeParams.BallShape.Shaped
import com.t8rin.imagetoolbox.core.utils.appContext
import io.github.alexzhirkevich.qrose.QrCodePainter
import io.github.alexzhirkevich.qrose.options.Neighbors
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrColors
import io.github.alexzhirkevich.qrose.options.QrErrorCorrectionLevel
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrLogo
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrLogoShape
import io.github.alexzhirkevich.qrose.options.QrOptions
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.QrShapes
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.cutCorners
import io.github.alexzhirkevich.qrose.options.horizontalLines
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.options.square
import io.github.alexzhirkevich.qrose.options.verticalLines
import io.github.alexzhirkevich.qrose.qrcode.MaskPattern
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import io.github.alexzhirkevich.qrose.toImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Creates a [Painter] that draws a QR code for the given [content].
 * The [size] parameter defines the size of the QR code in dp.
 * The [padding] parameter defines the padding of the QR code in dp.
 */
@Composable
private fun rememberBarcodePainter(
    content: String,
    params: BarcodeParams,
    onLoading: () -> Unit = {},
    onSuccess: () -> Unit = {},
    onFailure: (Throwable) -> Unit = {}
): Painter = when (params) {
    is BarcodeParams.Barcode -> {
        val width = params.width
        val height = params.height
        val foregroundColor = params.foregroundColor
        val backgroundColor = params.backgroundColor
        val type = params.type

        val density = LocalDensity.current
        val widthPx = with(density) { width.roundToPx() }
        val heightPx = with(density) { height.roundToPx() }
        val paddingPx = with(density) { 0.dp.roundToPx() }

        val bitmapState = remember(content) {
            mutableStateOf<Bitmap?>(null)
        }

        // Use dependency on 'content' to re-trigger the effect when content changes
        LaunchedEffect(
            content,
            type,
            widthPx,
            heightPx,
            foregroundColor,
            backgroundColor
        ) {
            onLoading()
            delay(50)

            if (content.isNotEmpty()) {
                bitmapState.value = runSuspendCatching {
                    generateQrBitmap(
                        content = content,
                        widthPx = widthPx,
                        heightPx = heightPx,
                        paddingPx = paddingPx,
                        foregroundColor = foregroundColor,
                        backgroundColor = backgroundColor,
                        format = type.zxingFormat
                    )
                }.onFailure(onFailure).getOrNull()
            } else {
                bitmapState.value = null
            }

            if (bitmapState.value != null) onSuccess()
        }

        val bitmap = bitmapState.value ?: createDefaultBitmap(widthPx, heightPx)

        remember(bitmap) {
            bitmap?.asImageBitmap()?.let {
                BitmapPainter(it)
            } ?: EmptyPainter(widthPx, heightPx)
        }
    }

    is BarcodeParams.Qr -> {
        rememberQrCodePainter(
            data = content,
            options = params.options,
            onSuccess = onSuccess,
            onFailure = {
                onLoading()
                onFailure(it)
            }
        )
    }
}

/**
 * Generates a QR code bitmap for the given [content].
 * The [widthPx] parameter defines the size of the QR code in pixels.
 * The [paddingPx] parameter defines the padding of the QR code in pixels.
 * Returns null if the QR code could not be generated.
 * This function is suspendable and should be called from a coroutine is thread-safe.
 */
private suspend fun generateQrBitmap(
    content: String,
    widthPx: Int,
    heightPx: Int,
    paddingPx: Int,
    foregroundColor: Color = Color.Black,
    backgroundColor: Color = Color.White,
    format: BarcodeFormat = BarcodeFormat.QR_CODE
): Bitmap = withContext(Dispatchers.IO) {
    val encodeHints = mutableMapOf<EncodeHintType, Any?>()
        .apply {
            this[EncodeHintType.CHARACTER_SET] = "utf-8"
            if (format == BarcodeFormat.QR_CODE) {
                this[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
            }
            this[EncodeHintType.MARGIN] = paddingPx
        }

    val bitmapMatrix =
        MultiFormatWriter().encode(content, format, widthPx, heightPx, encodeHints)

    val matrixWidth = bitmapMatrix.width
    val matrixHeight = bitmapMatrix.height

    val colors = IntArray(matrixWidth * matrixHeight) { index ->
        val x = index % matrixWidth
        val y = index / matrixWidth
        val shouldColorPixel = bitmapMatrix.get(x, y)
        if (shouldColorPixel) foregroundColor.toArgb() else backgroundColor.toArgb()
    }

    Bitmap.createBitmap(colors, matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888)
}

private sealed interface BarcodeParams {
    data class Qr(
        val options: QrOptions
    ) : BarcodeParams

    data class Barcode(
        val width: Dp = 150.dp,
        val height: Dp = width,
        val type: BarcodeType,
        val foregroundColor: Color,
        val backgroundColor: Color,
    ) : BarcodeParams {
        init {
            check(width >= 0.dp && height >= 0.dp) { "Size must be non negative" }
        }
    }
}

private class EmptyPainter(
    private val widthPx: Int,
    private val heightPx: Int
) : Painter() {
    override val intrinsicSize: Size
        get() = Size(widthPx.toFloat(), heightPx.toFloat())

    override fun DrawScope.onDraw() = Unit
}

/**
 * Creates a default bitmap with the given [widthPx], [heightPx].
 * The bitmap is transparent.
 * This is used as a fallback if the QR code could not be generated.
 * The bitmap is created on the UI thread.
 */
private fun createDefaultBitmap(
    widthPx: Int,
    heightPx: Int
): Bitmap? {
    return if (widthPx > 0 && heightPx > 0) {
        createBitmap(widthPx, heightPx).apply {
            eraseColor(Color.Transparent.toArgb())
        }
    } else null
}

data class QrCodeParams(
    val foregroundColor: Color? = null,
    val backgroundColor: Color? = null,
    val logo: Any? = null,
    val logoPadding: Float = 0.25f,
    val logoSize: Float = 0.2f,
    val logoCorners: Float = 0f,
    val pixelShape: PixelShape = PixelShape.Square,
    val frameShape: FrameShape = FrameShape.Square,
    val ballShape: BallShape = BallShape.Square,
    val errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.Auto,
    val maskPattern: MaskPattern = MaskPattern.Auto
) {
    sealed interface PixelShape {
        sealed interface Predefined : PixelShape

        data object Square : Predefined
        data object RoundSquare : Predefined
        data object Circle : Predefined
        data object Vertical : Predefined
        data object Horizontal : Predefined

        data object Random : PixelShape
        data class Shaped(val shape: Shape) : PixelShape

        companion object {
            val entries by lazy {
                listOf(
                    Random,
                    Square,
                    RoundSquare,
                    Circle,
                    Vertical,
                    Horizontal,
                ) + IconShape.entriesNoRandom.map { Shaped(it.shape) }
            }
        }
    }

    sealed interface FrameShape {
        data class Corners(
            val percent: Float,
            val sides: List<CornerSide> = CornerSide.entries,
            val isCut: Boolean = false
        ) : FrameShape {
            enum class CornerSide {
                TopLeft, TopRight, BottomRight, BottomLeft
            }

            val topLeft = CornerSide.TopLeft in sides
            val topRight = CornerSide.TopRight in sides
            val bottomLeft = CornerSide.BottomLeft in sides
            val bottomRight = CornerSide.BottomRight in sides
        }

        companion object {
            val Square = Corners(0.00f)
            val Circle = Corners(0.50f)

            val entries: List<FrameShape> by lazy {
                listOf(
                    Square,
                    Corners(0.25f),
                    Circle,
                    Corners(0.05f),
                    Corners(0.10f),
                    Corners(0.15f),
                    Corners(0.20f),
                    Corners(0.30f),
                    Corners(0.35f),
                    Corners(0.40f),
                    Corners(0.45f),
                    Corners(
                        percent = 0.05f,
                        isCut = true
                    ),
                    Corners(
                        percent = 0.10f,
                        isCut = true
                    ),
                    Corners(
                        percent = 0.15f,
                        isCut = true
                    ),
                    Corners(
                        percent = 0.20f,
                        isCut = true
                    ),
                    Corners(
                        percent = 0.25f,
                        isCut = true
                    ),
                    Corners(
                        percent = 0.30f,
                        isCut = true
                    ),
                    Corners(
                        percent = 0.35f,
                        isCut = true
                    ),
                )
            }
        }
    }

    sealed interface BallShape {
        sealed interface Predefined : BallShape

        data object Square : Predefined
        data object Circle : Predefined

        data class Shaped(val shape: Shape) : BallShape

        companion object {
            val entries by lazy {
                listOf(
                    Square,
                    Circle,
                ) + listOf(
                    Shaped(SquircleShape),
                    Shaped(CutCornerShape(25)),
                    Shaped(CutCornerShape(35)),
                    Shaped(CutCornerShape(50)),
                    Shaped(RoundedCornerShape(15)),
                    Shaped(RoundedCornerShape(25)),
                    Shaped(RoundedCornerShape(35)),
                    Shaped(RoundedCornerShape(45)),
                    Shaped(CloverShape),
                    Shaped(MaterialStarShape),
                    Shaped(SmallMaterialStarShape),
                    Shaped(BookmarkShape),
                    Shaped(PillShape),
                    Shaped(BurgerShape),
                    Shaped(OvalShape),
                    Shaped(ShieldShape),
                    Shaped(EggShape),
                    Shaped(DropletShape),
                    Shaped(ArrowShape),
                    Shaped(PentagonShape),
                    Shaped(OctagonShape),
                    Shaped(ShurikenShape),
                    Shaped(ExplosionShape),
                    Shaped(MapShape),
                ) + listOf(
                    MaterialShapes.Slanted,
                    MaterialShapes.Arch,
                    MaterialShapes.Oval,
                    MaterialShapes.Diamond,
                    MaterialShapes.Gem,
                    MaterialShapes.Sunny,
                    MaterialShapes.VerySunny,
                    MaterialShapes.Cookie4Sided,
                    MaterialShapes.Cookie6Sided,
                    MaterialShapes.Cookie9Sided,
                    MaterialShapes.Cookie12Sided,
                    MaterialShapes.Ghostish,
                    MaterialShapes.Clover4Leaf,
                    MaterialShapes.Clover8Leaf,
                    MaterialShapes.Burst,
                    MaterialShapes.SoftBurst,
                    MaterialShapes.SoftBoom,
                    MaterialShapes.Flower,
                    MaterialShapes.Puffy,
                    MaterialShapes.PuffyDiamond,
                    MaterialShapes.PixelCircle
                ).map {
                    Shaped(it.toShape())
                }
            }
        }
    }

    enum class ErrorCorrectionLevel {
        Auto, L, M, Q, H
    }

    enum class MaskPattern {
        Auto,
        P_000,
        P_001,
        P_010,
        P_011,
        P_100,
        P_101,
        P_110,
        P_111
    }
}

@Composable
fun defaultQrColors(): Pair<Color, Color> {
    val settingsState = LocalSettingsState.current

    val backgroundColor = if (settingsState.isNightMode) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }

    val foregroundColor = if (settingsState.isNightMode) {
        MaterialTheme.colorScheme.surfaceContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    return remember(backgroundColor, foregroundColor) {
        backgroundColor to foregroundColor
    }
}

@Composable
fun QrCode(
    content: String,
    modifier: Modifier,
    cornerRadius: Dp = 4.dp,
    heightRatio: Float = 2f,
    qrParams: QrCodeParams,
    type: BarcodeType = BarcodeType.QR_CODE,
    onFailure: (Throwable) -> Unit = {},
    onSuccess: () -> Unit = {},
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val width = min(this.maxWidth, maxHeight)
        val height = animateDpAsState(
            if (type.isSquare && type != BarcodeType.DATA_MATRIX) width else width / heightRatio
        ).value

        val (bg, fg) = defaultQrColors()

        val backgroundColor = qrParams.backgroundColor ?: bg

        val foregroundColor = qrParams.foregroundColor ?: fg

        var isLoading by remember {
            mutableStateOf(true)
        }

        var logoPainterRaw by remember(qrParams.logo) {
            mutableStateOf<Painter?>(null)
        }

        val logoPainter = remember(logoPainterRaw, qrParams.logoCorners) {
            logoPainterRaw?.roundCorners(qrParams.logoCorners)
        }

        LaunchedEffect(qrParams.logo) {
            logoPainterRaw = appContext.imageLoader.execute(
                ImageRequest.Builder(appContext)
                    .data(qrParams.logo)
                    .size(1024)
                    .build()
            ).image?.asPainter(appContext)?.centerCrop()
        }

        val density = LocalDensity.current

        val params by remember(
            width,
            height,
            type,
            foregroundColor,
            backgroundColor,
            qrParams,
            logoPainter,
            density
        ) {
            derivedStateOf {
                when (type) {
                    BarcodeType.QR_CODE -> {
                        BarcodeParams.Qr(
                            QrOptions(
                                colors = QrColors(
                                    dark = QrBrush.solid(foregroundColor),
                                    light = QrBrush.solid(backgroundColor),
                                    ball = QrBrush.solid(foregroundColor),
                                    frame = QrBrush.solid(foregroundColor),
                                    background = QrBrush.solid(backgroundColor),
                                ),
                                logo = logoPainter?.let {
                                    QrLogo(
                                        painter = logoPainter,
                                        shape = QrLogoShape.roundCorners(qrParams.logoCorners),
                                        padding = QrLogoPadding.Natural(qrParams.logoPadding),
                                        size = qrParams.logoSize
                                    )
                                } ?: QrLogo(),
                                shapes = QrShapes(
                                    darkPixel = qrParams.pixelShape.toLib(density),
                                    frame = qrParams.frameShape.toLib(),
                                    ball = qrParams.ballShape.toLib(density)
                                ),
                                errorCorrectionLevel = qrParams.errorCorrectionLevel.toLib(),
                                maskPattern = qrParams.maskPattern.toLib()
                            )
                        )
                    }

                    else -> {
                        BarcodeParams.Barcode(
                            width = width,
                            height = height,
                            foregroundColor = foregroundColor,
                            backgroundColor = backgroundColor,
                            type = type,
                        )
                    }
                }
            }
        }

        val painter = rememberBarcodePainter(
            content = content,
            params = params,
            onLoading = {
                isLoading = true
            },
            onSuccess = {
                isLoading = false
                onSuccess()
            },
            onFailure = onFailure
        )

        val padding = 16.dp

        Picture(
            model = painter,
            modifier = Modifier
                .size(
                    width = width,
                    height = height
                )
                .clip(AutoCornersShape(cornerRadius))
                .background(backgroundColor)
                .padding(padding)
                .alpha(
                    animateFloatAsState(
                        targetValue = if (isLoading) 0f else 1f,
                        animationSpec = tween(500)
                    ).value
                ),
            contentDescription = null
        )

        Box(
            modifier = Modifier
                .size(
                    width = width,
                    height = height
                )
                .clip(AutoCornersShape((cornerRadius - 1.dp).coerceAtLeast(0.dp)))
                .shimmer(isLoading)
        )
    }
}

suspend fun QrCodeParams.renderAsQr(
    content: String,
    type: BarcodeType
): Bitmap? = coroutineScope {
    val widthPx = 1500
    val heightPx = 1500
    val paddingPx = 100

    val logoPainter: Painter? = logo?.let {
        appContext.imageLoader.execute(
            ImageRequest.Builder(appContext)
                .data(logo)
                .size(1024)
                .build()
        ).image?.asPainter(appContext)?.centerCrop()?.roundCorners(logoCorners)
    }

    val options = QrOptions(
        colors = QrColors(
            dark = QrBrush.solid(foregroundColor ?: Color.Black),
            light = QrBrush.solid(backgroundColor ?: Color.White),
            ball = QrBrush.solid(foregroundColor ?: Color.Black),
            frame = QrBrush.solid(foregroundColor ?: Color.Black),
            background = QrBrush.solid(backgroundColor ?: Color.White),
        ),
        logo = logoPainter?.let {
            QrLogo(
                painter = logoPainter,
                shape = QrLogoShape.roundCorners(logoCorners),
                padding = QrLogoPadding.Natural(logoPadding),
                size = logoSize
            )
        } ?: QrLogo(),
        shapes = QrShapes(
            darkPixel = pixelShape.toLib(Density(1f)),
            frame = frameShape.toLib(),
            ball = ballShape.toLib(Density(1f))
        ),
        errorCorrectionLevel = errorCorrectionLevel.toLib(),
        maskPattern = maskPattern.toLib()
    )

    runSuspendCatching {
        when (type) {
            BarcodeType.QR_CODE -> {
                QrCodePainter(
                    data = content,
                    options = options,
                    onSuccess = {},
                    onFailure = {}
                ).toImageBitmap(widthPx, heightPx).asAndroidBitmap().applyPadding(paddingPx)
            }

            else -> {
                generateQrBitmap(
                    content = content,
                    widthPx = widthPx,
                    heightPx = heightPx,
                    paddingPx = paddingPx,
                    foregroundColor = foregroundColor ?: Color.Black,
                    backgroundColor = backgroundColor ?: Color.White,
                    format = type.zxingFormat
                )
            }
        }
    }.getOrNull()
}

private fun pixelShape(
    density: Density,
    shape: () -> Shape
) = QrPixelShape { size, _ ->
    apply {
        addOutline(
            shape().createOutline(
                size = Size(size, size),
                layoutDirection = LayoutDirection.Ltr,
                density = density
            )
        )
    }
}

private fun Shape.toBallShape(density: Density) = object : QrBallShape {
    override fun Path.path(
        size: Float,
        neighbors: Neighbors
    ): Path = apply {
        addOutline(
            createOutline(
                size = Size(size, size),
                layoutDirection = LayoutDirection.Ltr,
                density = density
            )
        )
    }
}

private fun QrCodeParams.BallShape.toLib(density: Density): QrBallShape = when (this) {
    QrCodeParams.BallShape.Square -> QrBallShape.square()
    QrCodeParams.BallShape.Circle -> QrBallShape.circle()
    is Shaped -> shape.toBallShape(density)
}

private fun QrCodeParams.FrameShape.toLib(): QrFrameShape = when (this) {
    is QrCodeParams.FrameShape.Corners -> {
        if (isCut) {
            QrFrameShape.cutCorners(
                corner = percent,
                topLeft = topLeft,
                topRight = topRight,
                bottomLeft = bottomLeft,
                bottomRight = bottomRight
            )
        } else {
            QrFrameShape.roundCorners(
                corner = percent,
                topLeft = topLeft,
                topRight = topRight,
                bottomLeft = bottomLeft,
                bottomRight = bottomRight
            )
        }
    }
}

private fun QrCodeParams.PixelShape.toLib(density: Density): QrPixelShape = when (this) {
    QrCodeParams.PixelShape.Square -> QrPixelShape.square()
    QrCodeParams.PixelShape.RoundSquare -> QrPixelShape.roundCorners()
    QrCodeParams.PixelShape.Circle -> QrPixelShape.circle()
    QrCodeParams.PixelShape.Vertical -> QrPixelShape.verticalLines()
    QrCodeParams.PixelShape.Horizontal -> QrPixelShape.horizontalLines()
    QrCodeParams.PixelShape.Random -> pixelShape(density) { IconShape.entriesNoRandom.random().shape }
    is QrCodeParams.PixelShape.Shaped -> pixelShape(density) { shape }
}

private fun QrCodeParams.ErrorCorrectionLevel.toLib(): QrErrorCorrectionLevel = when (this) {
    QrCodeParams.ErrorCorrectionLevel.Auto -> QrErrorCorrectionLevel.Auto
    QrCodeParams.ErrorCorrectionLevel.L -> QrErrorCorrectionLevel.Low
    QrCodeParams.ErrorCorrectionLevel.M -> QrErrorCorrectionLevel.Medium
    QrCodeParams.ErrorCorrectionLevel.Q -> QrErrorCorrectionLevel.MediumHigh
    QrCodeParams.ErrorCorrectionLevel.H -> QrErrorCorrectionLevel.High
}

private fun QrCodeParams.MaskPattern.toLib(): MaskPattern? = when (this) {
    QrCodeParams.MaskPattern.Auto -> null
    QrCodeParams.MaskPattern.P_000 -> MaskPattern.PATTERN000
    QrCodeParams.MaskPattern.P_001 -> MaskPattern.PATTERN001
    QrCodeParams.MaskPattern.P_010 -> MaskPattern.PATTERN010
    QrCodeParams.MaskPattern.P_011 -> MaskPattern.PATTERN011
    QrCodeParams.MaskPattern.P_100 -> MaskPattern.PATTERN100
    QrCodeParams.MaskPattern.P_101 -> MaskPattern.PATTERN101
    QrCodeParams.MaskPattern.P_110 -> MaskPattern.PATTERN110
    QrCodeParams.MaskPattern.P_111 -> MaskPattern.PATTERN111
}


enum class BarcodeType(
    internal val zxingFormat: BarcodeFormat,
    val isSquare: Boolean
) {
    QR_CODE(BarcodeFormat.QR_CODE, true),
    AZTEC(BarcodeFormat.AZTEC, true),
    CODABAR(BarcodeFormat.CODABAR, false),
    CODE_39(BarcodeFormat.CODE_39, false),
    CODE_93(BarcodeFormat.CODE_93, false),
    CODE_128(BarcodeFormat.CODE_128, false),
    DATA_MATRIX(BarcodeFormat.DATA_MATRIX, true),
    EAN_8(BarcodeFormat.EAN_8, false),
    EAN_13(BarcodeFormat.EAN_13, false),
    ITF(BarcodeFormat.ITF, false),
    PDF_417(BarcodeFormat.PDF_417, false),
    UPC_A(BarcodeFormat.UPC_A, false),
    UPC_E(BarcodeFormat.UPC_E, false)
}