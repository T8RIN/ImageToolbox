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

package ru.tech.imageresizershrinker.feature.settings.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.model.ChecksumType
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedChip
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.fadingEdges
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
fun ChecksumAsFilenameSettingItem(
    onValueChange: (ChecksumType?) -> Unit,
    shape: Shape = ContainerShapeDefaults.centerShape,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    var checkedState by remember {
        mutableStateOf(settingsState.checksumTypeForFilename != null)
    }
    LaunchedEffect(checkedState) {
        onValueChange(
            if (checkedState) {
                settingsState.checksumTypeForFilename ?: ChecksumType.entries.first()
            } else {
                null
            }
        )
    }

    PreferenceRowSwitch(
        shape = shape,
        modifier = modifier,
        enabled = !settingsState.overwriteFiles && !settingsState.randomizeFilename,
        onClick = {
            checkedState = it
        },
        title = stringResource(R.string.checksum_as_filename),
        subtitle = stringResource(R.string.checksum_as_filename_sub),
        checked = checkedState,
        startIcon = Icons.Rounded.Tag,
        additionalContent = {
            AnimatedVisibility(
                visible = checkedState,
                modifier = Modifier.fillMaxWidth()
            ) {
                val state = rememberScrollState()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fadingEdges(state)
                        .horizontalScroll(state)
                ) {
                    ChecksumType.entries.forEach { type ->
                        val selected by remember(type, settingsState.checksumTypeForFilename) {
                            derivedStateOf {
                                settingsState.checksumTypeForFilename == type
                            }
                        }

                        EnhancedChip(
                            selected = selected,
                            onClick = {
                                onValueChange(type)
                            },
                            selectedColor = MaterialTheme.colorScheme.tertiary,
                            contentPadding = PaddingValues(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            ),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = type.digest
                            )
                        }
                    }
                }
            }
        }
    )
}