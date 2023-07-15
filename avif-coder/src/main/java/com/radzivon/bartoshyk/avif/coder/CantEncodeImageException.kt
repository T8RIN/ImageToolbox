package com.radzivon.bartoshyk.avif.coder

import androidx.annotation.Keep

@Keep
class CantEncodeImageException(
    override val message: String?
) : Exception("Can't encode image: $message")