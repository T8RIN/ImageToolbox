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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.Metadata
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.image.toMap
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.utils.humanFileSize
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.DataObject
import com.t8rin.imagetoolbox.core.resources.icons.EditCalendar
import com.t8rin.imagetoolbox.core.resources.icons.Event
import com.t8rin.imagetoolbox.core.resources.icons.Exif
import com.t8rin.imagetoolbox.core.resources.icons.FolderOpen
import com.t8rin.imagetoolbox.core.resources.icons.ImageResize
import com.t8rin.imagetoolbox.core.resources.icons.Info
import com.t8rin.imagetoolbox.core.resources.icons.Straighten
import com.t8rin.imagetoolbox.core.resources.icons.TextSearch
import com.t8rin.imagetoolbox.core.ui.theme.onSecondaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.theme.secondaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.localizedName
import com.t8rin.imagetoolbox.core.ui.utils.provider.ProvideContainerDefaults
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberImageMetadataAsState
import com.t8rin.imagetoolbox.core.ui.widget.buttons.SupportingButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.other.ExpandableItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.utils.dateAdded
import com.t8rin.imagetoolbox.core.utils.fileSize
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.core.utils.imageSize
import com.t8rin.imagetoolbox.core.utils.lastModified
import com.t8rin.imagetoolbox.core.utils.mimeType
import com.t8rin.imagetoolbox.core.utils.path
import java.util.Locale

@Composable
fun MetadataPreviewButton(
    uri: Uri?,
    dateModified: (Uri) -> Long? = { it.lastModified() },
    dateAdded: (Uri) -> Long? = { it.dateAdded() },
    path: (Uri) -> String? = { it.path() },
    name: (Uri) -> String? = { it.filename() },
    fileSize: (Uri) -> String? = { humanFileSize(it.fileSize() ?: 0L) },
    imageSize: (Uri) -> IntegerSize? = { it.imageSize() },
    mimeType: (Uri) -> String? = { it.mimeType() }
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
        val metadataImageSize by remember(metadata) {
            derivedStateOf {
                metadata?.imageSize
            }
        }
        val info by remember(
            uri,
            dateModified,
            dateAdded,
            path,
            name,
            fileSize,
            imageSize,
            mimeType,
            metadataImageSize
        ) {
            derivedStateOf {
                UriInfo(
                    dateModified = dateModified(uri),
                    dateAdded = dateAdded(uri),
                    path = path(uri),
                    name = name(uri),
                    fileSize = fileSize(uri),
                    imageSize = metadataImageSize ?: imageSize(uri),
                    mimeType = mimeType(uri)
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
                    .size(24.dp),
                iconPadding = 3.dp
            )
            EnhancedModalBottomSheet(
                visible = showExif,
                onDismiss = { showExif = false },
                title = {
                    TitleItem(
                        text = stringResource(R.string.image_info),
                        icon = Icons.Rounded.Info
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
                val hasExif = tagMap.isNotEmpty()

                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    itemsIndexed(info.data) { index, (name, value, icon) ->
                        ValueField(
                            title = stringResource(name),
                            value = value,
                            icon = icon,
                            shape = ShapeDefaults.byIndex(
                                index = index,
                                size = info.data.size + if (hasExif) 1 else 0
                            )
                        )
                    }

                    if (hasExif) {
                        item {
                            ExpandableItem(
                                initialState = true,
                                visibleContent = {
                                    TitleItem(
                                        icon = Icons.Outlined.Exif,
                                        text = stringResource(R.string.metadata),
                                        iconEndPadding = 14.dp,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                },
                                expandableContent = {
                                    ProvideContainerDefaults(
                                        color = MaterialTheme.colorScheme.surfaceContainerLow
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        ) {
                                            tagMap.forEachIndexed { index, (tag, value) ->
                                                ValueField(
                                                    title = tag.localizedName,
                                                    value = value,
                                                    icon = null,
                                                    shape = ShapeDefaults.byIndex(
                                                        index = index,
                                                        size = tagMap.size,
                                                        roundedCorner = 12.dp
                                                    )
                                                )
                                            }
                                        }
                                    }
                                },
                                shape = ShapeDefaults.bottom
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ValueField(
    title: String,
    value: String,
    icon: ImageVector?,
    shape: Shape
) {
    PreferenceItem(
        title = title,
        subtitle = value,
        startIcon = icon,
        shape = shape,
        subtitleFontStyle = PreferenceItemDefaults.SubtitleFontStyleMedium,
        onClick = { Clipboard.copy(value) },
        modifier = Modifier.fillMaxWidth()
    )
}

private data class UriInfo(
    val dateModified: Long?,
    val dateAdded: Long?,
    val path: String?,
    val name: String?,
    val fileSize: String?,
    val imageSize: IntegerSize?,
    val mimeType: String?
) {
    val data: List<Triple<Int, String, ImageVector>> = buildList {
        name?.takeIf { it.isNotBlank() }?.let {
            add(
                Triple(
                    first = R.string.filename,
                    second = it,
                    third = Icons.Outlined.TextSearch
                )
            )
        }

        fileSize?.takeIf { it.isNotBlank() }?.let {
            add(
                Triple(
                    first = R.string.file_size,
                    second = it,
                    third = Icons.Outlined.Straighten
                )
            )
        }

        imageSize?.formatResolution()?.let {
            add(
                Triple(
                    first = R.string.resolution,
                    second = it,
                    third = Icons.Outlined.ImageResize
                )
            )
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
                add(
                    Triple(
                        first = R.string.sort_by_date_modified,
                        second = it,
                        third = Icons.Outlined.EditCalendar
                    )
                )
            }
        }

        dateAddedFormatted?.let {
            add(
                Triple(
                    first = R.string.sort_by_date_added,
                    second = it,
                    third = Icons.Outlined.Event
                )
            )
        }

        path?.takeIf { it.isNotBlank() }
            ?.removeSuffix("/$name")
            ?.removeSuffix("/${name?.substringBeforeLast('.')}")
            ?.let {
                add(
                    Triple(
                        first = R.string.path,
                        second = it,
                        third = Icons.Outlined.FolderOpen
                    )
                )
            }

        mimeType?.takeIf { it.isNotBlank() }?.let {
            add(
                Triple(
                    first = R.string.mime_type,
                    second = it,
                    third = Icons.Outlined.DataObject
                )
            )
        }
    }
}

private val Metadata.imageSize: IntegerSize?
    get() {
        val width = getAttribute(MetadataTag.PixelXDimension)?.toIntOrNull()
        val height = getAttribute(MetadataTag.PixelYDimension)?.toIntOrNull()
        return IntegerSize(width ?: 0, height ?: 0).takeIf { it.width > 0 && it.height > 0 }
    }

private fun IntegerSize.formatResolution(): String? = takeIf {
    it.width > 0 && it.height > 0
}?.run {
    val megapixels = width.toLong() * height / 1_000_000f
    "$width × $height • ${String.format(Locale.US, "%.1f", megapixels)} MP"
}?.let {
    if ("0.0" !in it) it else it.substringBeforeLast("•").trim()
}