package ru.tech.imageresizershrinker.presentation.root.widget.modifier

import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant

fun Modifier.alertDialogBorder() = composed {
    navigationBarsPadding()
        .statusBarsPadding()
        .displayCutoutPadding()
        .autoElevatedBorder(
            color = MaterialTheme.colorScheme.outlineVariant(
                luminance = 0.3f,
                onTopOf = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
            ),
            shape = AlertDialogDefaults.shape,
            autoElevation = 16.dp
        )
}