package ru.tech.imageresizershrinker.utils.coil.filters

import android.content.Context
import com.commit451.coiltransformations.gpu.BrightnessFilterTransformation

fun Context.BrightnessTransformation(
    brightness: Float = 0f
) = BrightnessFilterTransformation(this, brightness)