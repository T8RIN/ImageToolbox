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

package ru.tech.imageresizershrinker.feature.cipher.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.model.CipherType
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.ShieldKey
import ru.tech.imageresizershrinker.core.resources.icons.ShieldOpen
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberFilePicker
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.image.FileNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.modifier.scaleOnTap
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee
import ru.tech.imageresizershrinker.feature.cipher.domain.WrongKeyException
import ru.tech.imageresizershrinker.feature.cipher.presentation.components.CipherControls
import ru.tech.imageresizershrinker.feature.cipher.presentation.components.CipherTipSheet
import ru.tech.imageresizershrinker.feature.cipher.presentation.screenLogic.CipherComponent


@Composable
fun CipherContent(
    component: CipherComponent
) {
    val context = LocalContext.current

    val showTip = component.showTip

    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val canGoBack = component.canGoBack
    val key = component.key

    val onBack = {
        if (!canGoBack) showExitDialog = true
        else component.onGoBack()
    }

    val filePicker = rememberFilePicker(onSuccess = component::setUri)

    AutoFilePicker(
        onAutoPick = filePicker::pickFile,
        isPickedAlready = component.initialUri != null
    )

    val focus = LocalFocusManager.current

    val isPortrait by isPortraitOrientationAsState()

    AdaptiveLayoutScreen(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.marquee()
            ) {
                AnimatedContent(
                    targetState = component.uri to component.isEncrypt,
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) { (uri, isEncrypt) ->
                    Text(
                        if (uri == null) {
                            stringResource(R.string.cipher)
                        } else {
                            listOf(
                                stringResource(R.string.encryption),
                                stringResource(R.string.decryption)
                            )[if (isEncrypt) 0 else 1]
                        },
                        textAlign = TextAlign.Center
                    )
                }
                Badge(
                    content = {
                        Text(
                            text = CipherType.entries.size.toString()
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .padding(bottom = 12.dp)
                        .scaleOnTap {
                            showConfetti()
                        }
                )
            }
        },
        onGoBack = onBack,
        shouldDisableBackHandler = canGoBack,
        topAppBarPersistentActions = {
            if (!isPortrait || component.uri == null) TopAppBarEmoji()
        },
        actions = {},
        buttons = {
            BottomButtonsBlock(
                isNoData = component.uri == null,
                onSecondaryButtonClick = filePicker::pickFile,
                secondaryButtonIcon = Icons.Rounded.FileOpen,
                secondaryButtonText = stringResource(R.string.pick_file),
                onPrimaryButtonClick = {
                    focus.clearFocus()
                    component.startCryptography {
                        if (it is WrongKeyException) {
                            essentials.showToast(
                                message = context.getString(R.string.invalid_password_or_not_encrypted),
                                icon = Icons.Rounded.ErrorOutline
                            )
                        } else if (it != null) {
                            essentials.showFailureToast(
                                throwable = it
                            )
                        }
                    }
                },
                primaryButtonIcon = if (component.isEncrypt) {
                    Icons.Rounded.ShieldKey
                } else {
                    Icons.Rounded.ShieldOpen
                },
                primaryButtonText = if (isPortrait) {
                    if (component.isEncrypt) {
                        stringResource(R.string.encrypt)
                    } else {
                        stringResource(R.string.decrypt)
                    }
                } else "",
                isPrimaryButtonEnabled = key.isNotEmpty(),
                actions = {
                    if (isPortrait) {
                        Spacer(Modifier.width(12.dp))
                        TopAppBarEmoji()
                    }
                }
            )
        },
        canShowScreenData = component.uri != null,
        noDataControls = {
            FileNotPickedWidget(onPickFile = filePicker::pickFile)
        },
        controls = {
            CipherControls(component)
        },
        imagePreview = {},
        showImagePreviewAsStickyHeader = false,
        placeImagePreview = false,
        showActionsInTopAppBar = false
    )

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    CipherTipSheet(
        visible = showTip,
        onDismiss = component::hideTip
    )

    LoadingDialog(
        visible = component.isSaving,
        onCancelLoading = component::cancelSaving
    )

}