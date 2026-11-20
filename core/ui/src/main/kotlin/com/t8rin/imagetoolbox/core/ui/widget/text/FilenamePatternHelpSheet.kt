/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.ui.widget.text

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Date
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.FileSize
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Geometry
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.OriginalName
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Rand
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Sequence
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.sheets.InfoSheetCard

internal val FilenamePattern.helpTitle: Int?
    get() = when (this) {
        OriginalName -> R.string.filename_pattern_title_name
        Geometry -> R.string.caption_image_dimensions
        Date -> R.string.filename_pattern_title_date
        Rand -> R.string.filename_pattern_title_random
        Sequence -> R.string.filename_pattern_title_sequence
        FileSize -> R.string.file_size
        else -> null
    }

@Composable
fun DateFormatHelpSheet(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    FilenamePatternHelpSheet(
        visible = visible,
        pattern = Date,
        onDismiss = onDismiss,
        dateFormatOnly = true
    )
}

@Composable
internal fun FilenamePatternHelpSheet(
    visible: Boolean,
    pattern: FilenamePattern,
    onDismiss: () -> Unit,
    dateFormatOnly: Boolean = false,
    showSaveSetting: Boolean = false
) {
    val title = pattern.helpTitle ?: return
    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = { if (!it) onDismiss() },
        title = { TitleItem(text = stringResource(title)) },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onDismiss
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        }
    ) {
        val groups = remember(pattern, dateFormatOnly) { pattern.examples(dateFormatOnly) }
        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            flingBehavior = enhancedFlingBehavior()
        ) {
            itemsIndexed(groups) { index, group ->
                InfoSheetCard(
                    title = stringResource(group.title),
                    icon = group.icon,
                    shape = ShapeDefaults.byIndex(index, groups.size)
                ) {
                    Text(stringResource(group.description))
                    if (showSaveSetting && index == 0) {
                        val setting = when (pattern) {
                            OriginalName -> R.string.add_original_filename
                            Geometry -> R.string.add_file_size
                            Sequence -> R.string.replace_sequence_number
                            else -> null
                        }
                        setting?.let {
                            Text(
                                stringResource(
                                    R.string.filename_pattern_save_setting,
                                    stringResource(it)
                                )
                            )
                        }
                    }
                    PatternExamples(group.examples)
                }
            }
        }
    }
}

@Composable
private fun PatternExamples(examples: List<Pair<String, String>>) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val maxResultWidth = maxWidth / 2
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            examples.forEachIndexed { index, (pattern, result) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .container(
                            color = MaterialTheme.colorScheme.surface,
                            shape = ShapeDefaults.byIndex(
                                index = index,
                                size = examples.size,
                                roundedCorner = 12.dp
                            ),
                            resultPadding = 10.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = pattern,
                        modifier = Modifier
                            .weight(1f)
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    Clipboard.copy(pattern)
                                }
                            },
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = result,
                        modifier = Modifier.widthIn(max = maxResultWidth),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}