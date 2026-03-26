package io.github.alexzhirkevich.qrose.options.dsl

import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrOptions

internal class InternalQrColorsBuilderScope(
    private val builder: QrOptions.Builder
) : QrColorsBuilderScope {

    override var dark: QrBrush
        get() = builder.colors.dark
        set(value) = with(builder) {
            colors = colors.copy(
                dark = value
            )
        }

    override var light: QrBrush
        get() = builder.colors.light
        set(value) = with(builder) {
            colors = colors.copy(
                light = value
            )
        }

    override var frame: QrBrush
        get() = builder.colors.frame
        set(value) = with(builder) {
            colors = colors.copy(
                frame = value
            )
        }


    override var ball: QrBrush
        get() = builder.colors.ball
        set(value) = with(builder) {
            colors = colors.copy(
                ball = value
            )
        }
}