package com.awxkee.jxlcoder

import androidx.annotation.Keep

@Keep
class InvalidSizeParameterException: Exception("All images sizes expected to be > 0") {
}