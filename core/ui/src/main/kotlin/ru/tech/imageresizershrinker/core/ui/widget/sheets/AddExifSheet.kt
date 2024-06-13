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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.image.model.MetadataTag
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.Exif
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.localizedName
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import java.util.Locale

@Composable
fun AddExifSheet(
    visible: Boolean,
    onDismiss: (Boolean) -> Unit,
    selectedTags: List<MetadataTag>,
    onTagSelected: (MetadataTag) -> Unit,
    isTagsRemovable: Boolean = false
) {
    val tags by remember(selectedTags, isTagsRemovable) {
        derivedStateOf {
            if (isTagsRemovable) {
                val addedTags = MetadataTag.entries.filter {
                    it in selectedTags
                }.sorted()
                val notAddedTags = (MetadataTag.entries - addedTags.toSet()).sorted()

                addedTags + notAddedTags
            } else {
                MetadataTag.entries.filter {
                    it !in selectedTags
                }.sorted()
            }
        }
    }
    if (tags.isEmpty()) {
        SideEffect {
            onDismiss(false)
        }
    }
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    val list by remember(tags, query) {
        derivedStateOf {
            tags.filter {
                it.localizedName(context).contains(query, true)
                        || it.localizedName(context, Locale.ENGLISH).contains(query, true)
            }
        }
    }
    SimpleSheet(
        visible = visible,
        onDismiss = onDismiss,
        dragHandle = {
            SimpleDragHandle {
                Column {
                    RoundedTextField(
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Start
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(30),
                        label = stringResource(R.string.search_here),
                        onValueChange = { query = it },
                        value = query
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = { onDismiss(false) }
            ) {
                AutoSizeText(stringResource(R.string.ok))
            }
        },
        title = {
            TitleItem(
                text = stringResource(R.string.add_tag),
                icon = Icons.Rounded.Exif
            )
        },
        sheetContent = {
            Column {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 8.dp),
                    modifier = Modifier.weight(1f, false),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        Spacer(Modifier.height(4.dp))
                    }
                    itemsIndexed(list) { index, tag ->
                        val isSelected by remember(isTagsRemovable, tag, selectedTags) {
                            derivedStateOf {
                                isTagsRemovable && tag in selectedTags
                            }
                        }
                        val endIcon by remember(isSelected) {
                            derivedStateOf {
                                if (isSelected) {
                                    Icons.Rounded.RemoveCircleOutline
                                } else Icons.Rounded.AddCircleOutline
                            }
                        }
                        PreferenceItem(
                            title = tag.localizedName,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            endIcon = endIcon,
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                animateFloatAsState(if (isSelected) 0.35f else 0.1f).value
                            ),
                            shape = ContainerShapeDefaults.shapeForIndex(
                                index = index,
                                size = list.size
                            ),
                            onClick = {
                                onTagSelected(tag)
                            }
                        )
                    }
                    if (list.isEmpty()) {
                        item {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = 16.dp,
                                        bottom = 16.dp,
                                        start = 24.dp,
                                        end = 24.dp
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.nothing_found_by_search),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}