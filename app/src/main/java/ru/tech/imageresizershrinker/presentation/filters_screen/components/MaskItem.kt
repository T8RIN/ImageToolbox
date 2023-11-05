package ru.tech.imageresizershrinker.presentation.filters_screen.components

import android.net.Uri
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun MaskItem(
    mask: UiFilterMask,
    modifier: Modifier = Modifier,
    onMaskChange: (UiFilterMask) -> Unit,
    previewOnly: Boolean = false,
    backgroundColor: Color = MaterialTheme.colorScheme
        .surfaceContainer,
    showDragHandle: Boolean,
    onLongPress: (() -> Unit)? = null,
    onRemove: () -> Unit,
    imageUri: Uri? = null,
    previousMasks: List<UiFilterMask> = emptyList()
) {
    TODO("Not yet implemented")
}