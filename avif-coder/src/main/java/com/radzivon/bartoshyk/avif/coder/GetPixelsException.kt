package com.radzivon.bartoshyk.avif.coder

import androidx.annotation.Keep

@Keep
class GetPixelsException : Exception("Can't get pixels from android bitmap")