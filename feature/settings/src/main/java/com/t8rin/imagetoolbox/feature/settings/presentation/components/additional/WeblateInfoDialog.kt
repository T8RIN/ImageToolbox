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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.domain.WEBLATE_LINK
import com.t8rin.imagetoolbox.core.domain.WEBLATE_TITLE
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Cancel
import com.t8rin.imagetoolbox.core.resources.icons.Weblate
import com.t8rin.imagetoolbox.core.settings.presentation.model.isFirstLaunch
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton

@Composable
fun WeblateInfoDialog(
    onNotShowWeblateInfoDialogAgain: () -> Unit,
    onRegisterWeblateInfoDialogOpen: () -> Unit
) {
    val settings = LocalSettingsState.current
    val linkHandler = LocalUriHandler.current
    var isClosed by rememberSaveable {
        mutableStateOf(false)
    }
    val showDialog = settings.appOpenCount % 20 == 0
            && !settings.isFirstLaunch(false) && !isClosed
            && settings.weblateInfoDialogOpenCount != null

    var isOpenRegistered by rememberSaveable(showDialog) {
        mutableStateOf(false)
    }
    if (showDialog) {
        LaunchedEffect(isOpenRegistered) {
            if (!isOpenRegistered) {
                onRegisterWeblateInfoDialogOpen()
                isOpenRegistered = true
            }
        }
    }

    val isNotShowAgainButtonVisible = (settings.weblateInfoDialogOpenCount ?: 0) > 0

    EnhancedAlertDialog(
        visible = showDialog,
        onDismissRequest = { isClosed = true },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Weblate,
                contentDescription = null
            )
        },
        title = {
            Text(WEBLATE_TITLE)
        },
        text = {
            Text(stringResource(R.string.weblate_info_sub))
        },
        dismissButton = {
            AnimatedVisibility(isNotShowAgainButtonVisible) {
                EnhancedIconButton(
                    onClick = {
                        onNotShowWeblateInfoDialogAgain()
                        isClosed = true
                    },
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Cancel,
                        contentDescription = stringResource(R.string.dismiss_forever)
                    )
                }
            }
            EnhancedButton(
                onClick = { isClosed = true },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(R.string.understood))
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    isClosed = true
                    linkHandler.openUri(WEBLATE_LINK)
                }
            ) {
                Text(stringResource(R.string.open))
            }
        }
    )
}