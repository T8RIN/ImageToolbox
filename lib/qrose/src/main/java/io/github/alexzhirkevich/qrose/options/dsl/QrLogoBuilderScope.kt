package io.github.alexzhirkevich.qrose.options.dsl

import androidx.compose.ui.graphics.painter.Painter
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrLogoShape

/**
 * Logo (middle image) of the QR code.
 *
 * @property painter Middle image.
 * @property size Image size in fraction relative to QR code size
 * @property padding Style and size of the QR code padding.
 * Can be used without [painter] if you want to place a logo manually.
 * @property shape Shape of the logo padding
 * */
sealed interface QrLogoBuilderScope {
    var painter: Painter?
    var size: Float
    var padding: QrLogoPadding
    var shape: QrLogoShape
}

