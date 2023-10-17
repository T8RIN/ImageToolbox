package ru.tech.imageresizershrinker.presentation.root.widget.utils

import androidx.compose.runtime.compositionLocalOf
import coil.ImageLoader

val LocalImageLoader = compositionLocalOf<ImageLoader> { error("No ImageLoader provided") }