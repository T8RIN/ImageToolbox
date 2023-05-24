package ru.tech.imageresizershrinker.widget

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun KeepScreenOn(flag: Boolean = true) {
    if (flag) AndroidView({ View(it).apply { keepScreenOn = true } })
}