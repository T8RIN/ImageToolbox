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

package com.t8rin.imagetoolbox.feature.root.presentation.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NewReleases
import androidx.compose.material.icons.rounded.SystemSecurityUpdate
import androidx.compose.material.icons.rounded.Webhook
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.PerformanceClass
import com.t8rin.imagetoolbox.core.domain.utils.Flavor
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Beta
import com.t8rin.imagetoolbox.core.settings.presentation.model.isFirstLaunch
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.performanceClass
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.saver.OneTimeEffect
import com.t8rin.imagetoolbox.core.utils.appContext

@Composable
internal fun FirstLaunchSetupDialog(
    toggleAllowBetas: () -> Unit,
    toggleShowUpdateDialog: () -> Unit,
    adjustPerformance: (PerformanceClass) -> Unit
) {
    val settingsState = LocalSettingsState.current
    var updateOnFirstOpen by rememberSaveable {
        mutableStateOf(false)
    }

    OneTimeEffect {
        updateOnFirstOpen = settingsState.isFirstLaunch(false)

        if (updateOnFirstOpen) {
            adjustPerformance(appContext.performanceClass)
        }
    }

    EnhancedAlertDialog(
        visible = updateOnFirstOpen,
        onDismissRequest = {},
        icon = {
            Icon(
                imageVector = Icons.Rounded.SystemSecurityUpdate,
                contentDescription = null
            )
        },
        title = {
            Text(stringResource(R.string.updates))
        },
        text = {
            val essentials = rememberLocalEssentials()
            val state = rememberScrollState()
            ProvideTextStyle(value = LocalTextStyle.current.copy(textAlign = TextAlign.Left)) {
                Column(
                    modifier = Modifier
                        .fadingEdges(
                            isVertical = true,
                            scrollableState = state,
                            scrollFactor = 1.1f
                        )
                        .verticalScroll(state)
                        .padding(2.dp)
                ) {
                    if (Flavor.isFoss()) {
                        PreferenceItem(
                            title = stringResource(id = R.string.attention),
                            subtitle = stringResource(R.string.foss_update_checker_warning),
                            startIcon = Icons.Rounded.Webhook,
                            shape = ShapeDefaults.default,
                            modifier = Modifier.padding(bottom = 8.dp),
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                    }
                    PreferenceRowSwitch(
                        shape = if (!essentials.isInstalledFromPlayStore()) {
                            ShapeDefaults.top
                        } else ShapeDefaults.default,
                        modifier = Modifier,
                        title = stringResource(R.string.check_updates),
                        subtitle = stringResource(R.string.check_updates_sub),
                        checked = settingsState.showUpdateDialogOnStartup,
                        onClick = {
                            toggleShowUpdateDialog()
                        },
                        startIcon = Icons.Rounded.NewReleases
                    )
                    if (!essentials.isInstalledFromPlayStore()) {
                        Spacer(Modifier.height(4.dp))
                        PreferenceRowSwitch(
                            modifier = Modifier,
                            shape = ShapeDefaults.bottom,
                            title = stringResource(R.string.allow_betas),
                            subtitle = stringResource(R.string.allow_betas_sub),
                            checked = settingsState.allowBetas,
                            onClick = {
                                toggleAllowBetas()
                            },
                            startIcon = Icons.Rounded.Beta
                        )
                    }
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = { updateOnFirstOpen = false }
            ) {
                Text(stringResource(id = R.string.ok))
            }
        }
    )
}