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

package ru.tech.imageresizershrinker.feature.root.presentation

import android.content.ClipData
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.core.crash.components.GlobalExceptionHandler
import ru.tech.imageresizershrinker.core.resources.emoji.Emoji
import ru.tech.imageresizershrinker.core.settings.presentation.model.toUiState
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalEditPresetsController
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.IconShapeDefaults
import ru.tech.imageresizershrinker.core.ui.theme.ImageToolboxTheme
import ru.tech.imageresizershrinker.core.ui.utils.confetti.ConfettiHost
import ru.tech.imageresizershrinker.core.ui.utils.helper.ReviewHandler
import ru.tech.imageresizershrinker.core.ui.utils.provider.ImageToolboxCompositionLocals
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.widget.UpdateSheet
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.SecureModeHandler
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastHost
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.feature.root.presentation.components.AppExitDialog
import ru.tech.imageresizershrinker.feature.root.presentation.components.EditPresetsSheet
import ru.tech.imageresizershrinker.feature.root.presentation.components.FirstLaunchSetupDialog
import ru.tech.imageresizershrinker.feature.root.presentation.components.GithubReviewDialog
import ru.tech.imageresizershrinker.feature.root.presentation.components.PermissionDialog
import ru.tech.imageresizershrinker.feature.root.presentation.components.ScreenSelector
import ru.tech.imageresizershrinker.feature.root.presentation.components.TelegramGroupDialog
import ru.tech.imageresizershrinker.feature.root.presentation.screenLogic.RootComponent
import ru.tech.imageresizershrinker.feature.settings.presentation.components.additional.DonateDialog

@Composable
fun RootContent(
    component: RootComponent
) {
    val context = LocalComponentActivity.current

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    var randomEmojiKey by remember {
        mutableIntStateOf(0)
    }

    val currentDestination = component.childStack.subscribeAsState().value.items
    LaunchedEffect(currentDestination) {
        delay(200L) // Delay for transition
        randomEmojiKey++
    }

    ImageToolboxCompositionLocals(
        settingsState = component.settingsState.toUiState(
            allEmojis = Emoji.allIcons(),
            allIconShapes = IconShapeDefaults.shapes,
            randomEmojiKey = randomEmojiKey,
            getEmojiColorTuple = component::getColorTupleFromEmoji
        ),
        toastHostState = component.toastHostState,
        simpleSettingsInteractor = component.simpleSettingsInteractor
    ) {
        SecureModeHandler()

        val settingsState = LocalSettingsState.current

        LaunchedEffect(settingsState) {
            GlobalExceptionHandler.setAllowCollectCrashlytics(settingsState.allowCollectCrashlytics)
            GlobalExceptionHandler.setAnalyticsCollectionEnabled(settingsState.allowCollectAnalytics)
        }

        ImageToolboxTheme {
            val editPresetsController = LocalEditPresetsController.current

            val tiramisu = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

            if (!tiramisu) {
                BackHandler {
                    if (component.shouldShowDialog) showExitDialog = true
                    else context.finishAffinity()
                }
            }

            Surface(Modifier.fillMaxSize()) {
                ScreenSelector(
                    component = component
                )

                EditPresetsSheet(
                    visible = editPresetsController.isVisible,
                    onDismiss = editPresetsController::close,
                    onUpdatePresets = component::setPresets
                )

                val clipboardManager = LocalClipboardManager.current.nativeClipboard
                ProcessImagesPreferenceSheet(
                    uris = component.uris ?: emptyList(),
                    extraImageType = component.extraImageType,
                    visible = component.showSelectDialog,
                    onDismiss = component::hideSelectDialog,
                    onNavigate = { screen ->
                        component.navigateTo(screen)
                        component.hideSelectDialog()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            runCatching {
                                clipboardManager.clearPrimaryClip()
                            }.onFailure {
                                clipboardManager.setPrimaryClip(
                                    ClipData.newPlainText(null, "")
                                )
                            }
                        } else {
                            clipboardManager.setPrimaryClip(
                                ClipData.newPlainText(null, "")
                            )
                        }
                    }
                )
            }

            AppExitDialog(
                onDismiss = { showExitDialog = false },
                visible = showExitDialog && !tiramisu
            )

            UpdateSheet(
                tag = component.tag,
                changelog = component.changelog,
                visible = component.showUpdateDialog,
                onDismiss = component::cancelledUpdate
            )

            ConfettiHost()

            ToastHost(
                hostState = LocalToastHostState.current
            )

            SideEffect {
                component.tryGetUpdate()
            }

            FirstLaunchSetupDialog(
                toggleShowUpdateDialog = component::toggleShowUpdateDialog,
                toggleAllowBetas = component::toggleAllowBetas,
                adjustPerformance = component::adjustPerformance
            )

            DonateDialog(
                onRegisterDonateDialogOpen = component::registerDonateDialogOpen,
                onNotShowDonateDialogAgain = component::notShowDonateDialogAgain
            )

            PermissionDialog()

            GithubReviewDialog(
                visible = component.showGithubReviewDialog,
                onDismiss = component::hideReviewDialog,
                onNotShowAgain = {
                    ReviewHandler.notShowReviewAgain(context)
                },
                isNotShowAgainButtonVisible = ReviewHandler.showNotShowAgainButton
            )

            TelegramGroupDialog(
                visible = component.showTelegramGroupDialog,
                onDismiss = component::hideTelegramGroupDialog,
                onRedirected = component::registerTelegramGroupOpen
            )
        }
    }

}