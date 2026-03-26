package io.github.alexzhirkevich.qrose.options

import androidx.compose.runtime.Immutable


/**
 * Colors of QR code elements
 *
 * @param dark brush of the dark QR code pixels
 * @param light brush of the light QR code pixels
 * @param ball brush of the QR code eye balls
 * @param frame brush of the QR code eye frames
 */
@Immutable
data class QrColors(
    val dark: QrBrush = QrBrush.Default,
    val light: QrBrush = QrBrush.Unspecified,
    val ball: QrBrush = QrBrush.Unspecified,
    val frame: QrBrush = QrBrush.Unspecified,
    val background: QrBrush = QrBrush.Unspecified
)