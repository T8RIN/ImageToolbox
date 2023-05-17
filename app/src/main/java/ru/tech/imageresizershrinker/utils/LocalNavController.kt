package ru.tech.imageresizershrinker.utils

import androidx.compose.runtime.compositionLocalOf
import dev.olshevski.navigation.reimagined.NavController
import ru.tech.imageresizershrinker.main_screen.components.Screen

val LocalNavController = compositionLocalOf<NavController<Screen>> { error("NavController not present") }