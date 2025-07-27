/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.ui.widget.sheets

import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileDownloadOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.isInstalledFromPlayStore
import com.t8rin.imagetoolbox.core.ui.widget.other.LocalToastHostState
import kotlinx.coroutines.launch

@Composable
internal fun UpdateSheetImpl(
    changelog: String,
    tag: String,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val toastHostState = LocalToastHostState.current

    if (context.isInstalledFromPlayStore()) {
        LaunchedEffect(visible) {
            if (visible) {
                runCatching {
                    val appUpdateManager = AppUpdateManagerFactory.create(context)

                    val appUpdateInfoTask = appUpdateManager.appUpdateInfo

                    appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                        ) {
                            appUpdateManager.startUpdateFlow(
                                appUpdateInfo,
                                context as Activity,
                                AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE)
                            )
                        } else {
                            scope.launch {
                                toastHostState.showToast(
                                    icon = Icons.Rounded.FileDownloadOff,
                                    message = context.getString(R.string.no_updates)
                                )
                            }
                        }
                    }
                }.onFailure {
                    scope.launch {
                        toastHostState.showToast(
                            icon = Icons.Rounded.FileDownloadOff,
                            message = context.getString(R.string.no_updates)
                        )
                    }
                }
            }
        }
    } else {
        DefaultUpdateSheet(
            changelog = changelog,
            tag = tag,
            visible = visible,
            onDismiss = onDismiss
        )
    }
}