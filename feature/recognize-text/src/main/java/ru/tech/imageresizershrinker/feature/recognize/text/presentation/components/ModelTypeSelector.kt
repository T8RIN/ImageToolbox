/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

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
import ru.tech.imageresizershrinker.core.ui.icons.material.MiniEdit
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
    PreferenceItem(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(id = R.string.segmentation_mode),
        subtitle = stringResource(id = value.title),
        onClick = {
            showSelectionSheet = true
        },
        shape = RoundedCornerShape(24.dp),
        startIcon = Icons.AutoMirrored.Outlined.Segment,
        endIcon = Icons.Rounded.MiniEdit
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
                    title = stringResource(id = mode.title),
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

private inline val SegmentationMode.title: Int
    get() = when (this) {
        SegmentationMode.PSM_OSD_ONLY -> R.string.segmentation_mode_osd_only
        SegmentationMode.PSM_AUTO_OSD -> R.string.segmentation_mode_auto_osd
        SegmentationMode.PSM_AUTO_ONLY -> R.string.segmentation_mode_auto_only
        SegmentationMode.PSM_AUTO -> R.string.segmentation_mode_auto
        SegmentationMode.PSM_SINGLE_COLUMN -> R.string.segmentation_mode_single_column
        SegmentationMode.PSM_SINGLE_BLOCK_VERT_TEXT -> R.string.segmentation_mode_single_block_vert_text
        SegmentationMode.PSM_SINGLE_BLOCK -> R.string.segmentation_mode_single_block
        SegmentationMode.PSM_SINGLE_LINE -> R.string.segmentation_mode_single_line
        SegmentationMode.PSM_SINGLE_WORD -> R.string.segmentation_mode_single_word
        SegmentationMode.PSM_CIRCLE_WORD -> R.string.segmentation_mode_circle_word
        SegmentationMode.PSM_SINGLE_CHAR -> R.string.segmentation_mode_single_char
        SegmentationMode.PSM_SPARSE_TEXT -> R.string.segmentation_mode_sparse_text
        SegmentationMode.PSM_SPARSE_TEXT_OSD -> R.string.segmentation_mode_sparse_text_osd
        SegmentationMode.PSM_RAW_LINE -> R.string.segmentation_mode_raw_line
    }
