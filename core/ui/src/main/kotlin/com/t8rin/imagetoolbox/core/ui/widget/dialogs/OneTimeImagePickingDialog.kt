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

package com.t8rin.imagetoolbox.core.ui.widget.dialogs

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.model.PicturePickerMode
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.ImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.saver.PicturePickerModeSaver

@Composable
fun OneTimeImagePickingDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    picker: Picker,
    imagePicker: ImagePicker
) {
    val settingsState = LocalSettingsState.current

    var selectedPickerMode by rememberSaveable(stateSaver = PicturePickerModeSaver) {
        mutableStateOf(settingsState.picturePickerMode)
    }

    EnhancedAlertDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        confirmButton = {
            EnhancedButton(
                onClick = {
                    onDismiss()
                    imagePicker.pickImageWithMode(
                        picker = picker,
                        picturePickerMode = selectedPickerMode
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text(text = stringResource(id = R.string.pick))
            }
        },
        dismissButton = {
            EnhancedButton(
                onClick = onDismiss,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(text = stringResource(id = R.string.close))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.ImageSearch,
                contentDescription = stringResource(id = R.string.image_source)
            )
        },
        title = {
            Text(text = stringResource(id = R.string.image_source))
        },
        text = {
            val scrollState = rememberScrollState()
            ProvideTextStyle(LocalTextStyle.current.copy(textAlign = TextAlign.Start)) {
                Column(
                    modifier = Modifier
                        .fadingEdges(
                            scrollableState = scrollState,
                            isVertical = true
                        )
                        .enhancedVerticalScroll(scrollState)
                        .padding(vertical = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val data = remember {
                        PicturePickerMode.entries
                    }

                    data.forEachIndexed { index, mode ->
                        val selected = selectedPickerMode.ordinal == mode.ordinal

                        val shape = ShapeDefaults.byIndex(
                            index = index,
                            size = data.size
                        )
                        PreferenceItem(
                            shape = shape,
                            onClick = { selectedPickerMode = mode },
                            title = stringResource(mode.title),
                            startIcon = mode.icon,
                            containerColor = takeColorFromScheme {
                                if (selected) secondaryContainer.copy(0.7f)
                                else SafeLocalContainerColor
                            },
                            endIcon = if (selected) {
                                Icons.Rounded.RadioButtonChecked
                            } else Icons.Rounded.RadioButtonUnchecked,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .border(
                                    width = settingsState.borderWidth,
                                    color = animateColorAsState(
                                        if (selected) {
                                            MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                                0.5f
                                            )
                                        } else Color.Transparent
                                    ).value,
                                    shape = shape
                                )
                        )
                    }
                }
            }
        }
    )
}