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

package ru.tech.imageresizershrinker.core.ui.widget.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.exifinterface.media.ExifInterface
import ru.tech.imageresizershrinker.core.domain.image.model.MetadataTag
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.Exif
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.localizedName
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.toMap
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.alertDialogBorder
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@Composable
fun EditExifSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    exif: ExifInterface?,
    onClearExif: () -> Unit,
    onUpdateTag: (MetadataTag, String) -> Unit,
    onRemoveTag: (MetadataTag) -> Unit
) {
    var showClearExifDialog by rememberSaveable { mutableStateOf(false) }
    val showAddExifDialog = rememberSaveable { mutableStateOf(false) }

    var exifMap by remember(exif) {
        mutableStateOf(exif?.toMap())
    }

    SimpleSheet(
        confirmButton = {
            EnhancedButton(
                onClick = onDismiss
            ) {
                AutoSizeText(stringResource(R.string.ok))
            }
        },
        title = {
            val count = remember(exifMap) {
                MetadataTag.entries.count {
                    it !in (exifMap?.keys ?: emptyList())
                }
            }
            Row {
                if (exifMap?.isEmpty() == false) {
                    EnhancedButton(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(0.2f),
                        contentColor = MaterialTheme.colorScheme.onErrorContainer.copy(0.7f),
                        onClick = {
                            showClearExifDialog = true
                        }
                    ) {
                        Text(stringResource(R.string.clear))
                    }
                    Spacer(Modifier.width(8.dp))
                }
                if (count > 0) {
                    EnhancedButton(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = { showAddExifDialog.value = true }
                    ) {
                        Text(stringResource(R.string.add_tag))
                    }
                }
            }
        },
        dragHandle = {
            SimpleDragHandle {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TitleItem(
                        text = stringResource(id = R.string.edit_exif),
                        icon = Icons.Rounded.Exif
                    )
                }
            }
        },
        visible = visible,
        onDismiss = {
            if (!it) onDismiss()
        }
    ) {
        val data by remember(exifMap) {
            derivedStateOf {
                exifMap!!.toList()
            }
        }
        if (exifMap?.isEmpty() == false) {
            Box {
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(data) { index, (tag, value) ->
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .container(
                                    color = SimpleSheetDefaults.contentContainerColor,
                                    shape = ContainerShapeDefaults.shapeForIndex(
                                        index = index,
                                        size = data.size
                                    ),
                                )
                        ) {
                            Row {
                                Text(
                                    text = tag.localizedName,
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .weight(1f),
                                    textAlign = TextAlign.Start
                                )
                                EnhancedIconButton(
                                    containerColor = Color.Transparent,
                                    contentColor = LocalContentColor.current,
                                    enableAutoShadowAndBorder = false,
                                    onClick = {
                                        onRemoveTag(tag)
                                        exifMap = exifMap?.toMutableMap()
                                            ?.apply { remove(tag) }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.RemoveCircleOutline,
                                        contentDescription = stringResource(R.string.remove)
                                    )
                                }
                            }
                            OutlinedTextField(
                                onValueChange = {
                                    onUpdateTag(tag, it)
                                    exifMap = exifMap?.toMutableMap()
                                        ?.apply {
                                            this[tag] = it
                                        }
                                },
                                value = value,
                                textStyle = LocalTextStyle.current.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Next,
                                    autoCorrectEnabled = null
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        } else {
            Box {
                Text(
                    stringResource(R.string.no_exif),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        AddExifSheet(
            visible = showAddExifDialog.value,
            onDismiss = {
                showAddExifDialog.value = it
            },
            selectedTags = (exifMap?.keys?.toList() ?: emptyList()),
            onTagSelected = { tag ->
                onRemoveTag(tag)
                exifMap = exifMap?.toMutableMap()
                    ?.apply { this[tag] = "" }
            }
        )
        if (showClearExifDialog) {
            AlertDialog(
                modifier = Modifier.alertDialogBorder(),
                onDismissRequest = { showClearExifDialog = false },
                title = {
                    Text(stringResource(R.string.clear_exif))
                },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = null
                    )
                },
                confirmButton = {
                    EnhancedButton(
                        onClick = { showClearExifDialog = false }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                },
                dismissButton = {
                    EnhancedButton(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = {
                            showClearExifDialog = false
                            onClearExif()
                            exifMap = emptyMap()
                        }
                    ) {
                        Text(stringResource(R.string.clear))
                    }
                },
                text = {
                    Text(stringResource(R.string.clear_exif_sub))
                }
            )
        }
    }
}