package ru.tech.imageresizershrinker.utils.coil.filters


import android.content.Context
import com.commit451.coiltransformations.gpu.ContrastFilterTransformation

fun Context.ContrastTransformation(
    contrast: Float = 1f
) = ContrastFilterTransformation(this, contrast)