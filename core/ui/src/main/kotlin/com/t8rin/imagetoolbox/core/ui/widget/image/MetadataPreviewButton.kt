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

package com.t8rin.imagetoolbox.core.ui.widget.image

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.image.toMap
import com.t8rin.imagetoolbox.core.domain.utils.humanFileSize
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Exif
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.theme.onSecondaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.theme.secondaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.localizedName
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberImageMetadataAsState
import com.t8rin.imagetoolbox.core.ui.widget.buttons.SupportingButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextFieldColors
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.utils.dateAdded
import com.t8rin.imagetoolbox.core.utils.fileSize
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.core.utils.lastModified
import com.t8rin.imagetoolbox.core.utils.path

@Composable
fun MetadataPreviewButton(
    uri: Uri?,
    dateModified: (Uri) -> Long? = { it.lastModified() },
    dateAdded: (Uri) -> Long? = { it.dateAdded() },
    path: (Uri) -> String? = { it.path() },
    name: (Uri) -> String? = { it.filename() },
    fileSize: (Uri) -> String? = { humanFileSize(it.fileSize() ?: 0L, 2) }
) {
    AnimatedContent(
        targetState = uri
    ) { uri ->
        val metadata by rememberImageMetadataAsState(
            uri ?: return@AnimatedContent
        )
        val tagMap by remember(metadata) {
            derivedStateOf {
                metadata?.toMap().orEmpty().toList()
                    .filter { it.second.isNotBlank() }
            }
        }
        val info by remember(uri, dateModified, dateAdded, path, name, fileSize) {
            derivedStateOf {
                UriInfo(
                    dateModified = dateModified(uri),
                    dateAdded = dateAdded(uri),
                    path = path(uri),
                    name = name(uri),
                    fileSize = fileSize(uri)
                )
            }
        }
        if (tagMap.isNotEmpty() || info.data.isNotEmpty()) {
            var showExif by rememberSaveable {
                mutableStateOf(false)
            }
            SupportingButton(
                onClick = { showExif = true },
                contentColor = MaterialTheme.colorScheme.onSecondaryContainerFixed,
                containerColor = MaterialTheme.colorScheme.secondaryContainerFixed,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(20.dp),
                iconPadding = 2.dp
            )
            EnhancedModalBottomSheet(
                visible = showExif,
                onDismiss = { showExif = false },
                title = {
                    TitleItem(
                        text = stringResource(R.string.exif),
                        icon = Icons.Rounded.Exif
                    )
                },
                confirmButton = {
                    EnhancedButton(
                        onClick = { showExif = false }
                    ) {
                        Text(text = stringResource(R.string.close))
                    }
                },
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    itemsIndexed(info.data) { index, (name, value) ->
                        ValueField(
                            label = stringResource(name),
                            value = value,
                            shape = ShapeDefaults.byIndex(
                                index = index,
                                size = info.data.size
                            )
                        )
                    }

                    if (info.data.isNotEmpty() && tagMap.isNotEmpty()) {
                        item { Spacer(Modifier.height(4.dp)) }
                    }

                    itemsIndexed(tagMap) { index, (tag, value) ->
                        ValueField(
                            label = tag.localizedName,
                            value = value,
                            shape = ShapeDefaults.byIndex(
                                index = index,
                                size = tagMap.size
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ValueField(
    label: String,
    value: String,
    shape: Shape
) {
    RoundedTextField(
        onValueChange = {},
        readOnly = true,
        value = value,
        label = label,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        ),
        colors = RoundedTextFieldColors(
            isError = false,
            containerColor = EnhancedBottomSheetDefaults.contentContainerColor,
            unfocusedIndicatorColor = Color.Transparent
        ).copy(
            unfocusedLabelColor = MaterialTheme.colorScheme
                .surfaceVariant.inverse({ 0.3f })
        ),
        singleLine = false,
        maxLines = Int.MAX_VALUE,
        shape = shape,
        modifier = Modifier
            .fillMaxWidth()
            .container(
                color = EnhancedBottomSheetDefaults.contentContainerColor,
                shape = shape,
                resultPadding = 0.dp
            )
    )
}

private data class UriInfo(
    val dateModified: Long?,
    val dateAdded: Long?,
    val path: String?,
    val name: String?,
    val fileSize: String?
) {
    val data: List<Pair<Int, String>> = buildList {
        name?.takeIf { it.isNotBlank() }?.let {
            add(R.string.filename to it)
        }

        fileSize?.takeIf { it.isNotBlank() }?.let {
            add(R.string.file_size to it)
        }

        val dateAddedFormatted = dateAdded?.takeIf { it > 0 }?.let {
            timestamp(
                format = "d MMMM, yyyy • HH:mm",
                date = it
            )
        }

        val dateModifiedFormatted = dateModified?.takeIf { it > 0 }?.let {
            timestamp(
                format = "d MMMM, yyyy • HH:mm",
                date = it
            )
        }

        if (dateModifiedFormatted != dateAddedFormatted) {
            dateModifiedFormatted?.let {
                add(R.string.sort_by_date_modified to it)
            }
        }

        dateAddedFormatted?.let {
            add(R.string.sort_by_date_added to it)
        }

        path?.takeIf { it.isNotBlank() }
            ?.removeSuffix("/$name")
            ?.removeSuffix("/${name?.substringBeforeLast('.')}")
            ?.let {
                add(R.string.path to it)
            }
    }
}