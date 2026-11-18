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

package com.t8rin.imagetoolbox.core.ui.widget.sheets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.FontDownload
import com.t8rin.imagetoolbox.core.resources.icons.Link
import com.t8rin.imagetoolbox.core.settings.domain.FontLibrary
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalFontLibrary
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberHumanFileSize
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCancellableCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealDirection
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealValue
import com.t8rin.imagetoolbox.core.ui.widget.other.SwipeToReveal
import com.t8rin.imagetoolbox.core.ui.widget.other.rememberRevealState
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import kotlinx.coroutines.launch

@Composable
internal fun FontPackCard(
    onDisabled: () -> Unit,
    modifier: Modifier = Modifier
) {
    val library = LocalFontLibrary.current ?: return
    val fonts by library.fonts.collectAsState()
    val progress by library.progress.collectAsState()
    val enabled = LocalSettingsState.current.fontPackEnabled
    val interactor = LocalSimpleSettingsInteractor.current
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val revealState = rememberRevealState()
    val shape = ShapeDefaults.default
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }
    val installed = fonts.isNotEmpty()
    val downloadProgress = rememberUpdatedState(progress?.currentPercent ?: 0f)
    val downloadContent: @Composable () -> Unit = remember(library) {
        {
            EnhancedCancellableCircularProgressIndicator(
                progress = { downloadProgress.value },
                modifier = Modifier.size(24.dp),
                trackColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                strokeWidth = 3.dp,
                onCancel = library::cancelDownload
            )
        }
    }

    EnhancedAlertDialog(
        visible = showDeleteDialog,
        onDismissRequest = { showDeleteDialog = false },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null
            )
        },
        title = { Text(stringResource(R.string.delete)) },
        text = { Text(stringResource(R.string.delete_font_pack_sub)) },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    showDeleteDialog = false
                    scope.launch {
                        runSuspendCatching {
                            interactor.deleteFontPack()
                            onDisabled()
                        }.onFailure(AppToastHost::showFailureToast)
                    }
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { showDeleteDialog = false }
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
    SwipeToReveal(
        state = revealState,
        modifier = modifier,
        directions = if (installed) {
            setOf(RevealDirection.StartToEnd, RevealDirection.EndToStart)
        } else {
            setOf(RevealDirection.StartToEnd)
        },
        revealedContentStart = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .container(
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(0.5f),
                        shape = shape,
                        autoShadowElevation = 0.dp,
                        resultPadding = 0.dp
                    )
                    .hapticsClickable {
                        scope.launch { revealState.animateTo(RevealValue.Default) }
                        uriHandler.openUri(FontLibrary.ARCHIVE_PAGE)
                    }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Link,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(start = 8.dp)
                        .align(Alignment.CenterStart)
                )
            }
        },
        revealedContentEnd = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .container(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = shape,
                        autoShadowElevation = 0.dp,
                        resultPadding = 0.dp
                    )
                    .hapticsClickable {
                        scope.launch { revealState.animateTo(RevealValue.Default) }
                        showDeleteDialog = true
                    }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(end = 8.dp)
                        .align(Alignment.CenterEnd)
                )
            }
        },
        swipeableContent = {
            PreferenceRowSwitch(
                title = stringResource(R.string.font_pack),
                subtitle = when {
                    progress != null -> stringResource(R.string.downloading) + " · " +
                            rememberHumanFileSize(progress?.currentTotalSize ?: 0)

                    installed -> stringResource(R.string.font_pack_installed, fonts.size)
                    else -> stringResource(
                        R.string.font_pack_sub,
                        FontLibrary.FONT_COUNT,
                        rememberHumanFileSize(FontLibrary.ARCHIVE_SIZE)
                    )
                },
                startIcon = Icons.Rounded.FontDownload,
                shape = shape,
                checked = if (installed) enabled else progress != null,
                modifier = Modifier.fillMaxWidth(),
                onClick = { checked ->
                    scope.launch {
                        runSuspendCatching {
                            interactor.setFontPackEnabled(checked)
                            if (!checked) onDisabled()
                            if (!installed && checked) library.download()
                        }.onFailure(AppToastHost::showFailureToast)
                    }
                },
                contentInsteadOfSwitch = downloadContent.takeIf { progress != null }
            )
        }
    )
}
