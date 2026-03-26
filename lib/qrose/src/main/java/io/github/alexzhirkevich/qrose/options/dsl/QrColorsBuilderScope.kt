package io.github.alexzhirkevich.qrose.options.dsl

import io.github.alexzhirkevich.qrose.options.QrBrush

/**
 * Colors of QR code elements
 *
 * @property dark Brush of the dark QR code pixels
 * @property light Brush of the light QR code pixels
 * @property ball Brush of the QR code eye balls
 * @property frame Brush of the QR code eye frames
 */
sealed interface QrColorsBuilderScope {
    var dark: QrBrush
    var light: QrBrush
    var frame: QrBrush
    var ball: QrBrush
}
