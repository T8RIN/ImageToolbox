package ru.tech.imageresizershrinker.utils

import androidx.compose.runtime.compositionLocalOf
import dev.olshevski.navigation.reimagined.NavController

val LocalNavController =
    compositionLocalOf<NavController<Screen>> { error("NavController not present") }