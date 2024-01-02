package ru.tech.imageresizershrinker.coreui.widget.utils

import androidx.compose.runtime.compositionLocalOf
import coil.ImageLoader

val LocalImageLoader = compositionLocalOf<ImageLoader> { error("No ImageLoader provided") }