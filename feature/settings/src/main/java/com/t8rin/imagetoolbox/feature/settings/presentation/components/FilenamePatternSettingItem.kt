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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.saving.FilenameCreator
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Description
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.settings.domain.model.FilenameBehavior
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.FilenamePatternEditSheet

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

    FilenamePatternEditSheet(
        visible = showEditDialog,
        onDismiss = { showEditDialog = false },
        value = value,
        onValueChange = { value = it },
        onValueChangeFinished = onValueChange,
        exampleFilename = {
            PreferenceItem(
                title = stringResource(R.string.filename),
                subtitle = exampleFilename,
                modifier = Modifier
            )
        }
    )
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