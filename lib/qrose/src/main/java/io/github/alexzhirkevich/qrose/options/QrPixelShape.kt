package io.github.alexzhirkevich.qrose.options

/**
 * Style of the qr-code pixels.
 * */
fun interface QrPixelShape : QrShapeModifier {

    companion object {
        val Default: QrPixelShape = square()
    }
}

fun QrPixelShape.Companion.square(size: Float = 1f): QrPixelShape =
    object : QrPixelShape, QrShapeModifier by SquareShape(size) {}

fun QrPixelShape.Companion.circle(size: Float = 1f): QrPixelShape =
    object : QrPixelShape, QrShapeModifier by CircleShape(size) {}

fun QrPixelShape.Companion.roundCorners(radius: Float = .5f): QrPixelShape =
    object : QrPixelShape, QrShapeModifier by RoundCornersShape(radius, true) {}

fun QrPixelShape.Companion.verticalLines(width: Float = 1f): QrPixelShape =
    object : QrPixelShape, QrShapeModifier by VerticalLinesShape(width) {}

fun QrPixelShape.Companion.horizontalLines(width: Float = 1f): QrPixelShape =
    object : QrPixelShape, QrShapeModifier by HorizontalLinesShape(width) {}