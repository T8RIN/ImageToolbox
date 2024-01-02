package ru.tech.imageresizershrinker.coreui.utils.navigation

import androidx.compose.runtime.compositionLocalOf
import dev.olshevski.navigation.reimagined.NavController

val LocalNavController =
    compositionLocalOf<NavController<Screen>> { error("NavController not present") }