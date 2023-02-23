package ru.tech.imageresizershrinker.utils

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass> { error("SizeClass not present") }

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun Activity.provideWindowSizeClass(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalWindowSizeClass provides calculateWindowSizeClass(this),
        content = content
    )
}

fun ComponentActivity.setContentWithWindowSizeClass(
    content: @Composable () -> Unit
) = setContent {
    provideWindowSizeClass(content = content)
}
