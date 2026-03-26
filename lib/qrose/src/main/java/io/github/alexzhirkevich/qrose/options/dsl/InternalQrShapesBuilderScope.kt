package io.github.alexzhirkevich.qrose.options.dsl

import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrCodeShape
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrOptions
import io.github.alexzhirkevich.qrose.options.QrPixelShape


internal class InternalQrShapesBuilderScope(
    private val builder: QrOptions.Builder,
    centralSymmetry: Boolean,
) : QrShapesBuilderScope {

    init {
        builder.shapes = builder.shapes.copy(
            centralSymmetry = centralSymmetry
        )
    }

    override var pattern: QrCodeShape
        get() = builder.shapes.code
        set(value) = with(builder) {
            shapes = shapes.copy(
                code = value
            )
        }


    override var darkPixel: QrPixelShape
        get() = builder.shapes.darkPixel
        set(value) = with(builder) {
            shapes = shapes.copy(
                darkPixel = value
            )
        }

    override var lightPixel: QrPixelShape
        get() = builder.shapes.lightPixel
        set(value) = with(builder) {
            shapes = shapes.copy(
                lightPixel = value
            )
        }

    override var ball: QrBallShape
        get() = builder.shapes.ball
        set(value) = with(builder) {
            shapes = shapes.copy(
                ball = value
            )
        }

    override var frame: QrFrameShape
        get() = builder.shapes.frame
        set(value) = with(builder) {
            shapes = shapes.copy(
                frame = value
            )
        }
}