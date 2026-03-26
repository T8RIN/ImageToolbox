@file:Suppress("UNUSED")

package io.github.alexzhirkevich.qrose.options

import androidx.compose.runtime.Immutable
import io.github.alexzhirkevich.qrose.qrcode.ErrorCorrectionLevel


/**
 * QR code allows you to read encoded information even if a
 * part of the QR code image is damaged. It also allows to have logo
 * inside the code as a part of "damage".
 * */
@Immutable
enum class QrErrorCorrectionLevel(
    internal val lvl: ErrorCorrectionLevel
) {

    /**
     * Minimum possible level will be used.
     * */
    Auto(ErrorCorrectionLevel.L),

    /**
     * ~7% of QR code can be damaged (or used as logo).
     * */
    Low(ErrorCorrectionLevel.L),

    /**
     * ~15% of QR code can be damaged (or used as logo).
     * */
    Medium(ErrorCorrectionLevel.M),

    /**
     * ~25% of QR code can be damaged (or used as logo).
     * */
    MediumHigh(ErrorCorrectionLevel.Q),

    /**
     * ~30% of QR code can be damaged (or used as logo).
     * */
    High(ErrorCorrectionLevel.H)
}
