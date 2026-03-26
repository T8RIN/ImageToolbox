package io.github.alexzhirkevich.qrose.options.dsl

import androidx.compose.ui.graphics.painter.Painter
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrLogoShape
import io.github.alexzhirkevich.qrose.options.QrOptions

internal class InternalQrLogoBuilderScope(
    private val builder: QrOptions.Builder,
) : QrLogoBuilderScope {

    override var painter: Painter?
        get() = builder.logo.painter
        set(value) = with(builder) {
            logo = logo.copy(painter = value)
        }
    override var size: Float
        get() = builder.logo.size
        set(value) = with(builder) {
            logo = logo.copy(size = value)
        }

    override var padding: QrLogoPadding
        get() = builder.logo.padding
        set(value) = with(builder) {
            logo = logo.copy(padding = value)
        }
    override var shape: QrLogoShape
        get() = builder.logo.shape
        set(value) = with(builder) {
            logo = logo.copy(shape = value)
        }
}