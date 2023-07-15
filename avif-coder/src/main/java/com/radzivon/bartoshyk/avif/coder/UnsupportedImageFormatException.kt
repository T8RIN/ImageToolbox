package com.radzivon.bartoshyk.avif.coder

import androidx.annotation.Keep

@Keep
class UnsupportedImageFormatException :
    Exception("Currently support only RGBA_8888, RGB_565, RGBA_F16, RGBA_1010102 image format")