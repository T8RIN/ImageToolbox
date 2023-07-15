package com.radzivon.bartoshyk.avif.coder

import androidx.annotation.Keep

@Keep
class CorruptedBitDepthException : Exception("Invalid bit depth in the image")