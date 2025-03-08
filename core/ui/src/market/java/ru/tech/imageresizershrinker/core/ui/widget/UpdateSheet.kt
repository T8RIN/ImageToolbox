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

package ru.tech.imageresizershrinker.core.ui.widget

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileDownloadOff
import androidx.compose.material.icons.rounded.NewReleases
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.APP_RELEASES
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalBottomSheet
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalSheetDragHandle
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.HtmlText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@Composable
fun UpdateSheet(
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
        EnhancedModalBottomSheet(
            visible = visible,
            onDismiss = {
                if (!it) onDismiss()
            },
            title = {},
            dragHandle = {
                EnhancedModalSheetDragHandle {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor.provides(MaterialTheme.colorScheme.onSurface),
                            LocalTextStyle.provides(MaterialTheme.typography.bodyLarge)
                        ) {
                            TitleItem(
                                text = stringResource(R.string.new_version, tag),
                                icon = Icons.Rounded.NewReleases
                            )
                        }
                    }
                }
            },
            sheetContent = {
                ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
                    Box {
                        Column(
                            modifier = Modifier.align(Alignment.TopCenter),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                val linkHandler = LocalUriHandler.current
                                HtmlText(
                                    html = changelog.trimIndent(),
                                    modifier = Modifier.padding(
                                        start = 24.dp,
                                        end = 24.dp,
                                        top = 24.dp
                                    ),
                                    onHyperlinkClick = linkHandler::openUri
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                val linkHandler = LocalUriHandler.current
                EnhancedButton(
                    onClick = {
                        linkHandler.openUri("$APP_RELEASES/tag/${tag}")
                    }
                ) {
                    AutoSizeText(stringResource(id = R.string.update))
                }
            }
        )
    }
}