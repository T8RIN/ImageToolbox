package com.radzivon.bartoshyk.avif.coder

import androidx.annotation.Keep

@Keep
class HeifCantScaleException(
    override val message: String?
) : Exception("HEIF wasn't able to scale image due to $message")