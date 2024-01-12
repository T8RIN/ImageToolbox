package ru.tech.imageresizershrinker.feature.recognize.text.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Segment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.material.CreateAlt
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.feature.recognize.text.domain.SegmentationMode

@Composable
fun ModelTypeSelector(
    value: SegmentationMode,
    onValueChange: (SegmentationMode) -> Unit
) {
    val haptics = LocalHapticFeedback.current

    var showSelectionSheet by remember {
        mutableStateOf(false)
    }
    val subtitle by remember(value) {
        derivedStateOf {
            value.normalize()
        }
    }
    PreferenceItem(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(id = R.string.segmentation_mode),
        subtitle = subtitle,
        onClick = {
            showSelectionSheet = true
        },
        shape = RoundedCornerShape(24.dp),
        icon = Icons.AutoMirrored.Outlined.Segment,
        endIcon = Icons.Rounded.CreateAlt
    )

    SimpleSheet(
        visible = showSelectionSheet,
        onDismiss = {
            showSelectionSheet = it
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    showSelectionSheet = false
                }
            ) {
                Text(stringResource(R.string.close))
            }
        },
        title = {
            TitleItem(
                text = stringResource(id = R.string.segmentation_mode),
                icon = Icons.AutoMirrored.Outlined.Segment
            )
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(SegmentationMode.entries) { index, mode ->
                PreferenceItem(
                    modifier = Modifier.fillMaxWidth(),
                    title = mode.normalize(),
                    onClick = {
                        haptics.performHapticFeedback(
                            HapticFeedbackType.LongPress
                        )
                        onValueChange(mode)
                    },
                    color = animateColorAsState(
                        if (value == mode) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.surfaceContainerLow
                    ).value,
                    shape = ContainerShapeDefaults.shapeForIndex(
                        index = index,
                        size = SegmentationMode.entries.size
                    )
                )
            }
        }
    }
}

private fun SegmentationMode.normalize(): String {
    return this.name.removePrefix("PSM_").split("_").let { data ->
        val firstPart = data[0].lowercase().replaceFirstChar {
            it.uppercase()
        }
        val lastPart = data.getOrNull(1)?.lowercase()?.replaceFirstChar {
            it.uppercase()
        }?.let { " $it" } ?: ""

        "$firstPart$lastPart"
    }
}
