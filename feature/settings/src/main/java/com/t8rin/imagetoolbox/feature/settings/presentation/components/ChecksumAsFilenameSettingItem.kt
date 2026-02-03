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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.HashingType
import com.t8rin.imagetoolbox.core.domain.utils.safeCast
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.model.FilenameBehavior
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop

@Composable
fun ChecksumAsFilenameSettingItem(
    onValueChange: (HashingType?) -> Unit,
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    var checkedState by remember {
        mutableStateOf(settingsState.filenameBehavior is FilenameBehavior.Checksum)
    }
    LaunchedEffect(onValueChange, settingsState) {
        snapshotFlow { checkedState }
            .drop(1)
            .collectLatest {
                onValueChange(
                    if (it) {
                        settingsState.filenameBehavior.safeCast<FilenameBehavior.Checksum>()?.hashingType
                            ?: HashingType.entries.first()
                    } else {
                        null
                    }
                )
            }
    }

    PreferenceRowSwitch(
        shape = shape,
        modifier = modifier,
        enabled = settingsState.filenameBehavior is FilenameBehavior.None || settingsState.filenameBehavior is FilenameBehavior.Checksum,
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
                DataSelector(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    value = settingsState.filenameBehavior.safeCast<FilenameBehavior.Checksum>()?.hashingType
                        ?: HashingType.entries.first(),
                    onValueChange = onValueChange,
                    entries = HashingType.entries,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shape = shape,
                    title = stringResource(R.string.algorithms),
                    titleIcon = null,
                    badgeContent = {
                        Text(HashingType.entries.size.toString())
                    },
                    itemContentText = {
                        it.name
                    }
                )
            }
        }
    )
}