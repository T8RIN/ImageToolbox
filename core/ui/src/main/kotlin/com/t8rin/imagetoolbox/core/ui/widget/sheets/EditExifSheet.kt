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

package com.t8rin.imagetoolbox.core.ui.widget.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.image.Metadata
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.image.toMap
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.DeleteSweep
import com.t8rin.imagetoolbox.core.resources.icons.Exif
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.localizedName
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
fun EditExifSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    exif: Metadata?,
    onClearExif: () -> Unit,
    onUpdateTag: (MetadataTag, String) -> Unit,
    onRemoveTag: (MetadataTag) -> Unit
) {
    var showClearExifDialog by rememberSaveable { mutableStateOf(false) }
    val showAddExifDialog = rememberSaveable { mutableStateOf(false) }

    var exifMap by remember(exif) {
        mutableStateOf(exif?.toMap())
    }

    EnhancedModalBottomSheet(
        confirmButton = {
            val count = remember(exifMap) {
                MetadataTag.entries.count {
                    it !in (exifMap?.keys ?: emptyList())
                }
            }
            Row(
                modifier = Modifier.offset(x = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(exifMap?.isEmpty() == false) {
                    EnhancedIconButton(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        onClick = {
                            showClearExifDialog = true
                        },
                        forceMinimumInteractiveComponentSize = false
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteSweep,
                            contentDescription = null
                        )
                    }
                }
                AnimatedVisibility(count > 0) {
                    EnhancedIconButton(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = { showAddExifDialog.value = true }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddCircleOutline,
                            contentDescription = null
                        )
                    }
                }
                Spacer(Modifier.width(4.dp))
                EnhancedButton(
                    onClick = onDismiss
                ) {
                    AutoSizeText(stringResource(R.string.close))
                }
            }
        },
        title = {
            TitleItem(
                text = stringResource(id = R.string.edit_exif),
                icon = Icons.Rounded.Exif,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 8.dp, end = 16.dp)
            )
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
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    itemsIndexed(
                        items = data,
                        key = { _, t -> t.first.key }
                    ) { index, (tag, value) ->
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .container(
                                    color = EnhancedBottomSheetDefaults.contentContainerColor,
                                    shape = ShapeDefaults.byIndex(
                                        index = index,
                                        size = data.size
                                    )
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.no_exif),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                )
                Icon(
                    imageVector = Icons.Outlined.Exif,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(2f)
                        .sizeIn(maxHeight = 140.dp, maxWidth = 140.dp)
                        .fillMaxSize()
                )
                Spacer(Modifier.weight(1f))
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
        EnhancedAlertDialog(
            visible = showClearExifDialog,
            onDismissRequest = { showClearExifDialog = false },
            title = {
                Text(stringResource(R.string.clear_exif))
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.DeleteSweep,
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