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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.JAVA_FORMAT_SPECIFICATION
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.saving.FilenameCreator
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Date
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Extension
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Height
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.OriginalName
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Prefix
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.PresetInfo
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Rand
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.ScaleMode
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Sequence
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Suffix
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Width
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.settings.domain.model.FilenameBehavior
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.text.PatternHighlightTransformation
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
fun FilenamePatternSettingItem(
    onValueChange: (String) -> Unit,
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp),
    filenameCreator: FilenameCreator
) {
    val settingsState = LocalSettingsState.current
    var showEditDialog by rememberSaveable { mutableStateOf(false) }

    val exampleSaveTarget = remember {
        val originalUri = "file:///android_asset/svg/emotions/aasparkles.svg"
        ImageSaveTarget(
            imageInfo = ImageInfo(
                width = 123,
                height = 456,
                originalUri = originalUri
            ),
            originalUri = originalUri,
            sequenceNumber = 1,
            data = ByteArray(1),
            presetInfo = Preset.Percentage(95)
        )
    }

    var value by remember(showEditDialog, settingsState) {
        mutableStateOf(
            settingsState.filenamePattern?.takeIf { it.isNotBlank() }
                ?: if (settingsState.addOriginalFilename) {
                    FilenamePattern.ForOriginal
                } else {
                    FilenamePattern.Default
                }
        )
    }

    val exampleFilename by remember(filenameCreator, value, settingsState) {
        derivedStateOf {
            filenameCreator.constructImageFilename(
                saveTarget = exampleSaveTarget,
                oneTimePrefix = null,
                forceNotAddSizeInFilename = false,
                pattern = value
            )
        }
    }

    PreferenceItem(
        shape = shape,
        onClick = {
            showEditDialog = true
        },
        enabled = settingsState.filenameBehavior is FilenameBehavior.None,
        title = stringResource(R.string.filename_format),
        subtitle = exampleFilename,
        endIcon = Icons.Rounded.MiniEdit,
        startIcon = Icons.Outlined.Description,
        modifier = modifier.fillMaxWidth()
    )

    EnhancedModalBottomSheet(
        visible = showEditDialog,
        onDismiss = { showEditDialog = it },
        title = {
            TitleItem(
                icon = Icons.Outlined.Description,
                text = stringResource(R.string.filename_format)
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                    alpha = if (settingsState.isNightMode) 0.5f
                    else 1f
                ),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = {
                    onValueChange(value.trim())
                    showEditDialog = false
                },
                borderColor = MaterialTheme.colorScheme.outlineVariant(
                    onTopOf = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(stringResource(R.string.save))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clearFocusOnTap()
                .enhancedVerticalScroll(rememberScrollState())
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.CenterVertically
            ),
        ) {
            PreferenceItem(
                title = stringResource(R.string.filename),
                subtitle = exampleFilename,
                modifier = Modifier
            )

            RoundedTextField(
                modifier = Modifier
                    .container(
                        shape = MaterialTheme.shapes.large,
                        resultPadding = 8.dp
                    ),
                value = value,
                textStyle = MaterialTheme.typography.titleMedium,
                onValueChange = {
                    value = it
                },
                maxLines = Int.MAX_VALUE,
                singleLine = false,
                hint = { Text(stringResource(R.string.format_pattern)) },
                label = null,
                visualTransformation = PatternHighlightTransformation.default()
            )

            Spacer(Modifier.height(4.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.CenterVertically
                ),
            ) {
                val linkHandler = LocalUriHandler.current

                FilenamePattern.entries.forEachIndexed { index, pattern ->
                    PreferenceItemOverload(
                        title = when (pattern) {
                            Date -> pattern.value + "{pattern}"
                            Rand -> pattern.value + "{count}"
                            else -> pattern.value
                        },
                        subtitle = when (pattern) {
                            Prefix -> stringResource(R.string.prefix_pattern_description)
                            OriginalName -> stringResource(R.string.original_filename_pattern_description)
                            Width -> stringResource(R.string.width_pattern_description)
                            Height -> stringResource(R.string.height_pattern_description)
                            Date -> stringResource(R.string.formatted_timestamp_pattern_description)
                            Rand -> stringResource(R.string.random_numbers_pattern_description)
                            Sequence -> stringResource(R.string.sequence_number_pattern_description)
                            PresetInfo -> stringResource(R.string.preset_info_pattern_description)
                            ScaleMode -> stringResource(R.string.scale_mode_pattern_description)
                            Suffix -> stringResource(R.string.suffix_pattern_description)
                            Extension -> stringResource(R.string.extension_pattern_description)
                            else -> null
                        },
                        badge = if (pattern.hasUpper()) {
                            {
                                EnhancedBadge(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(horizontal = 4.dp, vertical = 2.dp),
                                    content = { Text("A/a") },
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        } else null,
                        containerColor = takeColorFromScheme {
                            if (pattern.value in value) {
                                secondaryContainer
                            } else {
                                SafeLocalContainerColor
                            }
                        },
                        contentColor = takeColorFromScheme {
                            if (pattern.value in value) {
                                onSecondaryContainer
                            } else {
                                onSurface
                            }
                        },
                        endIcon = if (pattern == Date) {
                            {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null
                                )
                            }
                        } else null,
                        onClick = if (pattern == Date) {
                            {
                                linkHandler.openUri(JAVA_FORMAT_SPECIFICATION)
                            }
                        } else null,
                        shape = ShapeDefaults.byIndex(
                            index = index,
                            size = FilenamePattern.entries.size
                        ),
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun Preview() = ImageToolboxThemeForPreview(
    isDarkTheme = true
) {
    Surface {
        FilenamePatternSettingItem(
            onValueChange = {},
            filenameCreator = object : FilenameCreator {
                override fun constructImageFilename(
                    saveTarget: ImageSaveTarget,
                    oneTimePrefix: String?,
                    forceNotAddSizeInFilename: Boolean,
                    pattern: String?
                ): String = "Not yet implemented"

                override fun constructRandomFilename(
                    extension: String,
                    length: Int
                ): String = "Not yet implemented"

                override fun getFilename(uri: String): String = "Not yet implemented"
            }
        )
    }
}