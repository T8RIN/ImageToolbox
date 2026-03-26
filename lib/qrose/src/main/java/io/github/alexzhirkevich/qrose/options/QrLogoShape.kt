package io.github.alexzhirkevich.qrose.options

interface QrLogoShape : QrShapeModifier {

    companion object {
        val Default: QrLogoShape = object : QrLogoShape, QrShapeModifier by SquareShape() {}
    }

}

fun QrLogoShape.Companion.circle(): QrLogoShape =
    object : QrLogoShape, QrShapeModifier by CircleShape(1f) {}

fun QrLogoShape.Companion.roundCorners(radius: Float): QrLogoShape =
    object : QrLogoShape, QrShapeModifier by RoundCornersShape(radius, false) {}
