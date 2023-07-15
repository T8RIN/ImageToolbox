package com.radzivon.bartoshyk.avif.coder

import androidx.annotation.Keep

@Keep
class HardwareBitmapIsNotImplementedException :
    Exception("Hardware bitmaps currently are not implemented")