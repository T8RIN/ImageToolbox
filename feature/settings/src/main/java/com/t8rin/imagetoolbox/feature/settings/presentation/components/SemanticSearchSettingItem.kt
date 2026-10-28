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

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ImageSearch
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.permission.PermissionStatus
import com.t8rin.imagetoolbox.core.ui.utils.permission.PermissionUtils.askUserToRequestPermissionExplicitly
import com.t8rin.imagetoolbox.core.ui.utils.permission.PermissionUtils.checkPermissions
import com.t8rin.imagetoolbox.core.ui.utils.permission.PermissionUtils.hasPermissionAllowed
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalComponentActivity
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCancellableCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchState

@Composable
internal fun SemanticSearchSettingItem(
    state: SemanticSearchState,
    onValueChange: (Boolean) -> Unit,
    shape: Shape = ShapeDefaults.default,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    val context = LocalComponentActivity.current
    val downloadProgress = rememberUpdatedState(state.downloadProgress)
    val onValueChangeState by rememberUpdatedState(onValueChange)
    val downloadContent: @Composable () -> Unit = remember {
        {
            EnhancedCancellableCircularProgressIndicator(
                progress = { downloadProgress.value },
                modifier = Modifier.size(24.dp),
                trackColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                strokeWidth = 3.dp,
                onCancel = { onValueChangeState(false) }
            )
        }
    }
    val permissions = remember {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(
                Manifest.permission.READ_MEDIA_IMAGES
            )

            else -> listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.any { it }) {
            onValueChange(true)
        } else {
            AppToastHost.showToast(
                message = R.string.gallery_search_permission,
                icon = Icons.Outlined.ImageSearch
            )
        }
    }

    PreferenceRowSwitch(
        modifier = modifier,
        shape = shape,
        title = stringResource(R.string.gallery_search_auto),
        subtitle = stringResource(R.string.gallery_search_auto_sub),
        checked = settingsState.isSemanticSearchEnabled,
        contentInsteadOfSwitch = downloadContent.takeIf {
            settingsState.isSemanticSearchEnabled && state.isDownloading
        },
        onClick = { enabled ->
            when {
                !enabled || permissions.any { context.hasPermissionAllowed(it) } -> onValueChange(
                    enabled
                )

                context.checkPermissions(permissions).finalStatus == PermissionStatus.DENIED_PERMANENTLY -> {
                    context.askUserToRequestPermissionExplicitly()
                    AppToastHost.showToast(
                        message = R.string.grant_permission_manual,
                        icon = Icons.Outlined.ImageSearch
                    )
                }

                else -> permissionLauncher.launch(permissions.toTypedArray())
            }
        },
        startIcon = Icons.Outlined.ImageSearch
    )
}