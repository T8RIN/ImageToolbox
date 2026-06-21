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

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageExportProfile
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.image.model.title
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.domain.utils.trimTrailingZero
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.DownloadFile
import com.t8rin.imagetoolbox.core.resources.icons.Loyalty
import com.t8rin.imagetoolbox.core.resources.icons.MoreVert
import com.t8rin.imagetoolbox.core.resources.icons.RadioButtonChecked
import com.t8rin.imagetoolbox.core.resources.icons.RadioButtonUnchecked
import com.t8rin.imagetoolbox.core.resources.icons.Save
import com.t8rin.imagetoolbox.core.resources.icons.Share
import com.t8rin.imagetoolbox.core.resources.icons.UploadFile
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedDropdownMenu
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.image.aspectRatios
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlin.math.abs

@Composable
fun ImageExportProfileSelector(
    profiles: List<ImageExportProfile>,
    selectedProfile: ImageExportProfile?,
    imageInfo: ImageInfo,
    preset: Preset,
    onApplyProfile: (ImageExportProfile) -> Unit,
    onSaveProfile: (String) -> Unit,
    onDeleteProfile: (ImageExportProfile) -> Unit,
    onExportProfile: (ImageExportProfile, Uri) -> Unit,
    onShareProfile: (ImageExportProfile) -> Unit,
    onImportProfile: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    val importPicker = rememberFilePicker(
        mimeType = MimeType.All,
        onSuccess = onImportProfile
    )

    EnhancedChip(
        selected = selectedProfile != null,
        onClick = { showSheet = true },
        selectedColor = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.Loyalty,
            contentDescription = stringResource(R.string.export_profiles)
        )
    }

    EnhancedModalBottomSheet(
        visible = showSheet,
        onDismiss = { showSheet = it },
        title = {
            TitleItem(
                icon = Icons.Rounded.Loyalty,
                text = stringResource(R.string.export_profiles)
            )
        },
        confirmButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                EnhancedIconButton(
                    onClick = importPicker::pickFile,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Outlined.UploadFile,
                        contentDescription = stringResource(R.string.import_word)
                    )
                }
                EnhancedButton(
                    onClick = {
                        showSheet = false
                    }
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier
                .animateContentSizeNoClip()
                .clearFocusOnTap()
        ) {
            if (selectedProfile == null || profiles.isEmpty()) {
                item("AddImagePresetBlock") {
                    AddImagePresetBlock(
                        preset = preset,
                        imageInfo = imageInfo,
                        onSave = onSaveProfile,
                        modifier = Modifier
                            .padding(
                                bottom = 4.dp
                            )
                            .animateItem()
                    )
                }
            }
            itemsIndexed(
                items = profiles,
                key = { _, item -> item.name }
            ) { index, item ->
                val selected = item == selectedProfile
                PreferenceItemOverload(
                    title = item.name,
                    subtitle = item.subtitle(),
                    onClick = {
                        onApplyProfile(item)
                    },
                    drawStartIconContainer = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(),
                    startIcon = {
                        Icon(
                            imageVector = if (selected) {
                                Icons.Rounded.RadioButtonChecked
                            } else {
                                Icons.Rounded.RadioButtonUnchecked
                            },
                            contentDescription = null
                        )
                    },
                    endIcon = {
                        ImagePresetItemMenu(
                            preset = item,
                            onExportProfile = onExportProfile,
                            onShareProfile = onShareProfile,
                            onDeleteProfile = onDeleteProfile,
                            modifier = Modifier.offset(x = 8.dp)
                        )
                    },
                    shape = ShapeDefaults.byIndex(index, profiles.size),
                    containerColor = if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        Color.Unspecified
                    },
                    contentColor = if (selected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        Color.Unspecified
                    }
                )
            }
        }
    }
}

@Composable
private fun ImagePresetItemMenu(
    preset: ImageExportProfile,
    onExportProfile: (ImageExportProfile, Uri) -> Unit,
    onShareProfile: (ImageExportProfile) -> Unit,
    onDeleteProfile: (ImageExportProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by rememberSaveable(preset.name) { mutableStateOf(false) }
    val exportPicker = rememberFileCreator(
        mimeType = MimeType.All,
        onSuccess = { uri ->
            onExportProfile(preset, uri)
        }
    )

    Box(
        modifier = modifier
    ) {
        EnhancedIconButton(onClick = { showMenu = true }) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = null
            )
        }
        EnhancedDropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            ImagePresetMenuAction(
                title = R.string.export,
                icon = Icons.Outlined.DownloadFile,
                onClick = {
                    showMenu = false
                    exportPicker.make(preset.fileName)
                }
            )
            ImagePresetMenuAction(
                title = R.string.share,
                icon = Icons.Rounded.Share,
                onClick = {
                    showMenu = false
                    onShareProfile(preset)
                }
            )
            ImagePresetMenuAction(
                title = R.string.delete,
                icon = Icons.Rounded.Delete,
                onClick = {
                    showMenu = false
                    onDeleteProfile(preset)
                }
            )
        }
    }
}

@Composable
private fun AddImagePresetBlock(
    preset: Preset,
    imageInfo: ImageInfo,
    onSave: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by rememberSaveable { mutableStateOf("") }
    val canSave = name.isNotBlank()

    Column(
        modifier = modifier
            .container(
                shape = ShapeDefaults.default,
                resultPadding = 16.dp
            )
    ) {
        TitleItem(
            icon = Icons.Outlined.Loyalty,
            text = stringResource(R.string.save_export_profile),
            subtitle = imageInfo.summary(preset),
            modifier = Modifier
        )

        Spacer(Modifier.height(12.dp))

        RoundedTextField(
            value = name,
            onValueChange = { name = it },
            label = stringResource(R.string.name),
            endIcon = {
                EnhancedIconButton(
                    onClick = {
                        onSave(name)
                        name = ""
                    },
                    enabled = canSave
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Save,
                        contentDescription = null
                    )
                }
            },
            singleLine = true
        )
    }
}

@Composable
private fun ImagePresetMenuAction(
    title: Int,
    icon: ImageVector,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(stringResource(title)) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        onClick = onClick
    )
}

@Composable
private fun ImageExportProfile.subtitle(): String = imageInfo.summary(preset)

private val ImageExportProfile.fileName: String
    get() = "${name.safeFileName()}.itpreset"

@Composable
private fun ImageInfo.summary(preset: Preset): String = listOfNotNull(
    preset.targetLabel(this),
    imageFormatLabel(),
    resizeType.title(),
    imageScaleMode.titleOrNull()
).joinToString(SummarySeparator)

@Composable
private fun Preset.targetLabel(imageInfo: ImageInfo): String = when (this) {
    is Preset.AspectRatio -> buildString {
        append(stringResource(R.string.aspect_ratio))
        append(" ")
        append(ratio.aspectRatioLabel())
        if (isFit) {
            append(", ")
            append(stringResource(R.string.fit))
        }
    }

    Preset.None -> imageInfo.sizeLabel()
    is Preset.Percentage -> "$value%"
    Preset.Telegram -> stringResource(R.string.telegram)
}

private fun ImageInfo.sizeLabel(): String = "${width}x$height"

private fun ImageInfo.imageFormatLabel(): String {
    val quality = quality.qualityLabel(
        showQuality = imageFormat.canChangeCompressionValue
    )
    return listOfNotNull(imageFormat.title, quality).joinToString(" ")
}

private fun Quality.qualityLabel(showQuality: Boolean): String? {
    if (!showQuality) return null

    return when (this) {
        is Quality.Avif -> "${qualityValue}%, E$effort"
        is Quality.Base -> "${qualityValue}%"
        is Quality.Jxl -> "${qualityValue}%, E$effort"
        is Quality.PngLossy -> "L$compressionLevel, ${maxColors}c"
        is Quality.PngQuant -> "${quality}%, ${maxColors}c"
        is Quality.Tiff -> "C$compressionScheme"
    }
}

@Composable
private fun ResizeType.title(): String = stringResource(
    when (this) {
        ResizeType.Explicit -> R.string.explicit
        is ResizeType.Flexible -> R.string.flexible
        is ResizeType.CenterCrop -> R.string.crop
        is ResizeType.Fit -> R.string.fit
    }
)

@Composable
private fun ImageScaleMode.titleOrNull(): String? =
    if (this == ImageScaleMode.Base || this == ImageScaleMode.NotPresent) {
        null
    } else {
        stringResource(title)
    }

@Composable
private fun Float.aspectRatioLabel(): String {
    val ratios = aspectRatios()

    val ratio = remember(ratios, this) {
        ratios.filterIsInstance<DomainAspectRatio.Numeric>().firstOrNull {
            abs(it.value - this) < 0.001f
        }
    } ?: return this.toString().trimTrailingZero()

    val width = ratio.widthProportion.toString()
        .trimTrailingZero()
    val height = ratio.heightProportion.toString()
        .trimTrailingZero()

    return "$width:$height"
}

private const val SummarySeparator = " / "

private fun String.safeFileName(): String = trim()
    .replace(Regex("""[^\w.-]+"""), "_")
    .trim('_')
    .ifBlank { "preset" }

@Preview
@Composable
private fun Preview() = ImageToolboxThemeForPreview(true) {
    Button(onClick = {}) { }

    AddImagePresetBlock(
        preset = Preset.Telegram,
        imageInfo = ImageInfo(),
        onSave = {}
    )
}