package ru.tech.imageresizershrinker.presentation.filters_screen.components

import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.presentation.root.icons.material.CreateAlt
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun MaskItem(
    mask: UiFilterMask,
    modifier: Modifier = Modifier,
    titleText: String,
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
    val showEditMaskSheet = rememberSaveable { mutableStateOf(false) }
    val settingsState = LocalSettingsState.current
    Row(
        modifier = modifier
            .container(color = backgroundColor, shape = MaterialTheme.shapes.extraLarge)
            .animateContentSize()
            .then(
                onLongPress?.let {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { it() }
                        )
                    }
                } ?: Modifier
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showDragHandle) {
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Rounded.DragHandle, null)
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .height(32.dp)
                    .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant())
                    .padding(start = 20.dp)
            )
        }
        Column(
            Modifier
                .weight(1f)
                .alpha(if (previewOnly) 0.5f else 1f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Icon(Icons.Rounded.RemoveCircleOutline, null)
                    }
                    Text(
                        text = titleText,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(
                                top = 8.dp,
                                end = 8.dp,
                                start = 16.dp
                            )
                            .weight(1f)
                    )
                    IconButton(
                        onClick = { showEditMaskSheet.value = true },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(Icons.Rounded.CreateAlt, null)
                    }
                }
            }
        }
    }
}