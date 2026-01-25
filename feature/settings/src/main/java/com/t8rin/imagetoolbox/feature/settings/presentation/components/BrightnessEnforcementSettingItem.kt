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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrightnessHigh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalResourceManager
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCheckbox
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem


@Composable
fun BrightnessEnforcementSettingItem(
    onValueChange: (Screen) -> Unit,
    shape: Shape = ShapeDefaults.top,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    val settingsScreenList = settingsState.screenListWithMaxBrightnessEnforcement
    val screenList by remember(settingsScreenList) {
        derivedStateOf {
            settingsScreenList.mapNotNull {
                Screen.entries.find { s -> s.id == it }
            }
        }
    }

    var showPickerSheet by rememberSaveable { mutableStateOf(false) }

    val context = LocalResourceManager.current

    val subtitle by remember(screenList, context) {
        derivedStateOf {
            screenList.joinToString(separator = ", ") {
                context.getString(it.title)
            }.ifEmpty {
                context.getString(R.string.disabled)
            }
        }
    }

    PreferenceItem(
        shape = shape,
        modifier = modifier,
        onClick = {
            showPickerSheet = true
        },
        startIcon = Icons.Outlined.BrightnessHigh,
        title = stringResource(R.string.brightness_enforcement),
        subtitle = subtitle,
        endIcon = Icons.Rounded.MiniEdit
    )

    EnhancedModalBottomSheet(
        visible = showPickerSheet,
        onDismiss = {
            showPickerSheet = it
        },
        title = {
            TitleItem(
                text = stringResource(R.string.brightness),
                icon = Icons.Outlined.BrightnessHigh
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { showPickerSheet = false }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
        sheetContent = {
            Box {
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = Screen.entries,
                        key = { it.id }
                    ) { screen ->
                        val checked by remember(screen, screenList) {
                            derivedStateOf {
                                screenList.fastAny { it::class.isInstance(screen) }
                            }
                        }
                        PreferenceItemOverload(
                            modifier = Modifier.fillMaxWidth(),
                            title = stringResource(screen.title),
                            subtitle = stringResource(screen.subtitle),
                            startIcon = {
                                screen.icon?.let {
                                    Icon(
                                        imageVector = it,
                                        contentDescription = null
                                    )
                                }
                            },
                            endIcon = {
                                EnhancedCheckbox(
                                    checked = checked,
                                    onCheckedChange = {
                                        onValueChange(screen)
                                    }
                                )
                            },
                            onClick = {
                                onValueChange(screen)
                            }
                        )
                    }
                }
            }
        }
    )
}