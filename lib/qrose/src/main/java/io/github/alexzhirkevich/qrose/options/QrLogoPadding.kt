package io.github.alexzhirkevich.qrose.options

import androidx.compose.runtime.Immutable

/**
 * Type of padding applied to the logo.
 * Helps to highlight the logo inside the QR code pattern.
 * Padding can be added regardless of the presence of a logo.
 * */
sealed interface QrLogoPadding {

    /**
     * Padding size relatively to the size of logo
     * */
    val size: Float


    /**
     * Logo will be drawn on top of QR code without any padding.
     * QR code pixels might be visible through transparent logo.
     *
     * Prefer empty padding if your qr code encodes large amount of data
     * to avoid performance issues.
     * */
    @Immutable
    data object Empty : QrLogoPadding {
        override val size: Float get() = 0f
    }


    /**
     * Padding will be applied precisely according to the shape of logo
     * */
    @Immutable
    class Accurate(override val size: Float) : QrLogoPadding


    /**
     * Works like [Accurate] but all clipped pixels will be removed.
     *
     * WARNING: this padding can cause performance issues
     * for QR codes with large amount out data
     * */
    @Immutable
    class Natural(override val size: Float) : QrLogoPadding
}