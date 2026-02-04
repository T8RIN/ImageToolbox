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

package com.t8rin.imagetoolbox.feature.settings.presentation.components.additional

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.VolunteerActivism
import com.t8rin.imagetoolbox.core.settings.presentation.model.isFirstLaunch
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import kotlinx.coroutines.delay

@Composable
fun DonateDialog(
    onNotShowDonateDialogAgain: () -> Unit,
    onRegisterDonateDialogOpen: () -> Unit
) {
    val settings = LocalSettingsState.current
    var isClosed by rememberSaveable {
        mutableStateOf(false)
    }
    val showDialog = settings.appOpenCount % 12 == 0
            && !settings.isFirstLaunch(false) && !isClosed
            && settings.donateDialogOpenCount != null


    var isOpenRegistered by rememberSaveable(showDialog) {
        mutableStateOf(false)
    }
    if (showDialog) {
        LaunchedEffect(isOpenRegistered) {
            if (!isOpenRegistered) {
                delay(1000)
                onRegisterDonateDialogOpen()
                isOpenRegistered = true
            }
        }
    }

    val isNotShowAgainButtonVisible = (settings.donateDialogOpenCount ?: 0) > 2

    EnhancedAlertDialog(
        visible = showDialog,
        onDismissRequest = { },
        icon = {
            Icon(
                imageVector = Icons.Rounded.VolunteerActivism,
                contentDescription = null
            )
        },
        title = { Text(stringResource(R.string.donation)) },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fadingEdges(
                        isVertical = true,
                        scrollableState = scrollState
                    )
                    .enhancedVerticalScroll(scrollState)
            ) {
                DonateContainerContent()
            }
        },
        dismissButton = {
            if (isNotShowAgainButtonVisible) {
                EnhancedButton(
                    onClick = {
                        onNotShowDonateDialogAgain()
                        isClosed = true
                    },
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(stringResource(id = R.string.dismiss_forever))
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    isClosed = true
                },
                containerColor = takeColorFromScheme {
                    if (isNotShowAgainButtonVisible) tertiaryContainer
                    else secondaryContainer
                }
            ) {
                Text(stringResource(id = R.string.close))
            }
        }
    )
}