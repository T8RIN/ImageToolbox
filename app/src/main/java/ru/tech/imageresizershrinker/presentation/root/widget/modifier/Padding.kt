package ru.tech.imageresizershrinker.presentation.root.widget.modifier

import android.content.res.Configuration
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

fun Modifier.navBarsPaddingOnlyIfTheyAtTheEnd(enabled: Boolean = true) = composed {
    if (WindowInsets.navigationBars.asPaddingValues()
            .calculateBottomPadding() == 0.dp && enabled
    ) Modifier
        .navigationBarsPadding()
        .displayCutoutPadding()
    else Modifier
}

fun Modifier.navBarsLandscapePadding(enabled: Boolean = true) = composed {
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE && enabled) Modifier
        .navigationBarsPadding()
        .displayCutoutPadding()
    else Modifier
}

fun Modifier.navBarsPaddingOnlyIfTheyAtTheBottom(enabled: Boolean = true) = composed {
    if (WindowInsets.navigationBars.asPaddingValues()
            .calculateBottomPadding() != 0.dp && enabled
    ) Modifier
        .navigationBarsPadding()
        .displayCutoutPadding()
    else Modifier
}