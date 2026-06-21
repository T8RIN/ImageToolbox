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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedDropdownMenu
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.aspectRatios
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealDirection
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealValue
import com.t8rin.imagetoolbox.core.ui.widget.other.SwipeToReveal
import com.t8rin.imagetoolbox.core.ui.widget.other.rememberRevealState
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
internal fun LazyItemScope.ImagePresetItem(
    index: Int,
    profilesCount: Int,
    item: ImageExportProfile,
    selected: Boolean,
    onApplyProfile: (ImageExportProfile) -> Unit,
    onExportProfile: (ImageExportProfile, Uri) -> Unit,
    onShareProfile: (ImageExportProfile) -> Unit,
    onWantDelete: (ImageExportProfile) -> Unit
) {
    val scope = rememberCoroutineScope()
    val state = rememberRevealState()
    val shape = ShapeDefaults.byIndex(index, profilesCount)

    SwipeToReveal(
        state = state,
        directions = setOf(RevealDirection.EndToStart),
        modifier = Modifier.animateItem(),
        revealedContentEnd = {
            Box(
                Modifier
                    .fillMaxSize()
                    .container(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = shape,
                        autoShadowElevation = 0.dp,
                        resultPadding = 0.dp
                    )
                    .hapticsClickable {
                        scope.launch {
                            state.animateTo(RevealValue.Default)
                        }
                        onWantDelete(item)
                    }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(end = 8.dp)
                        .align(Alignment.CenterEnd),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        swipeableContent = {
            PreferenceItemOverload(
                title = item.name,
                subtitle = item.subtitle(),
                onClick = {
                    onApplyProfile(item)
                },
                drawStartIconContainer = false,
                modifier = Modifier.fillMaxWidth(),
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
                        modifier = Modifier.offset(x = 8.dp)
                    )
                },
                shape = shape,
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
    )
}

@Composable
internal fun AddImagePresetBlock(
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
private fun ImagePresetItemMenu(
    preset: ImageExportProfile,
    onExportProfile: (ImageExportProfile, Uri) -> Unit,
    onShareProfile: (ImageExportProfile) -> Unit,
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
        }
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