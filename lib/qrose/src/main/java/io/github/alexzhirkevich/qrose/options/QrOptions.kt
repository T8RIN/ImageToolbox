package io.github.alexzhirkevich.qrose.options

import androidx.compose.runtime.Immutable
import io.github.alexzhirkevich.qrose.options.dsl.InternalQrOptionsBuilderScope
import io.github.alexzhirkevich.qrose.options.dsl.QrOptionsBuilderScope
import io.github.alexzhirkevich.qrose.qrcode.MaskPattern

fun QrOptions(block: QrOptionsBuilderScope.() -> Unit): QrOptions {
    val builder = QrOptions.Builder()
    InternalQrOptionsBuilderScope(builder).apply(block)
    return builder.build()
}

/**
 * Styling options of the QR code
 *
 * @param shapes shapes of the QR code pattern and its parts
 * @param colors colors of the QR code parts
 * @param logo middle image
 * @param errorCorrectionLevel level of error correction
 * @param fourEyed enable fourth eye
 * */
@Immutable
data class QrOptions(
    val shapes: QrShapes = QrShapes(),
    val colors: QrColors = QrColors(),
    val logo: QrLogo = QrLogo(),
    val errorCorrectionLevel: QrErrorCorrectionLevel = QrErrorCorrectionLevel.Auto,
    val maskPattern: MaskPattern? = null,
    val fourEyed: Boolean = false,
) {

    internal class Builder {

        var shapes: QrShapes = QrShapes()
        var colors: QrColors = QrColors()
        var logo: QrLogo = QrLogo()
        var errorCorrectionLevel: QrErrorCorrectionLevel = QrErrorCorrectionLevel.Auto
        var fourthEyeEnabled: Boolean = false
        var maskPattern: MaskPattern? = null

        fun build(): QrOptions = QrOptions(
            shapes = shapes,
            colors = colors,
            logo = logo,
            errorCorrectionLevel = errorCorrectionLevel,
            maskPattern = maskPattern,
            fourEyed = fourthEyeEnabled
        )
    }

}

