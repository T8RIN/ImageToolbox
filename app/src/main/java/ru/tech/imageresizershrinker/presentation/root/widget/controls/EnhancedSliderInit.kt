package ru.tech.imageresizershrinker.presentation.root.widget.controls

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun EnhancedSliderInit() {
    var initialized by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(initialized) {
        if (!initialized) {
            kotlin.runCatching {
                Class.forName("androidx.compose.material3.SliderKt")
                    .getDeclaredField("TrackHeight").apply {
                        isAccessible.let {
                            isAccessible = true
                            set(this, 32f)
                            isAccessible = it
                        }
                    }
            }
            initialized = true
        }
    }
}