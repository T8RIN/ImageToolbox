package io.github.alexzhirkevich.qrose.options

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.painter.Painter

/**
 * Logo (middle image) of the QR code.
 *
 * @param painter middle image.
 * @param size image size in fraction relative to QR code size
 * @param padding style and size of the QR code padding.
 * Can be used without [painter] if you want to place a logo manually.
 * @param shape shape of the logo padding
 * */
@Immutable
data class QrLogo(
    val painter: Painter? = null,
    val size: Float = 0.25f,
    val padding: QrLogoPadding = QrLogoPadding.Empty,
    val shape: QrLogoShape = QrLogoShape.Default,
)

