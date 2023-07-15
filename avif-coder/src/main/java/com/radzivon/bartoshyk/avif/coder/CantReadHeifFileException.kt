package com.radzivon.bartoshyk.avif.coder

import androidx.annotation.Keep

@Keep
class CantReadHeifFileException : Exception("Can't read heif file exception")