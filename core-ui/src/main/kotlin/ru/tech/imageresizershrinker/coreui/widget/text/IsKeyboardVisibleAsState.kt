package ru.tech.imageresizershrinker.coreui.widget.text

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun isKeyboardVisibleAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

@Composable
fun KeyboardFocusHandler() {
    val focus = LocalFocusManager.current
    val isKeyboardVisible by isKeyboardVisibleAsState()

    LaunchedEffect(isKeyboardVisible) {
        if (!isKeyboardVisible) focus.clearFocus()
    }
}