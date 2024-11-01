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
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalHapticFeedback
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.push
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.core.crash.components.GlobalExceptionHandler
import ru.tech.imageresizershrinker.core.resources.emoji.Emoji
import ru.tech.imageresizershrinker.core.settings.presentation.model.toUiState
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalEditPresetsController
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import ru.tech.imageresizershrinker.core.settings.presentation.provider.rememberEditPresetsController
import ru.tech.imageresizershrinker.core.ui.shapes.IconShapeDefaults
import ru.tech.imageresizershrinker.core.ui.theme.ImageToolboxTheme
import ru.tech.imageresizershrinker.core.ui.utils.confetti.ConfettiHost
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.rememberConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.core.ui.utils.helper.ReviewHandler
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalImageLoader
import ru.tech.imageresizershrinker.core.ui.widget.UpdateSheet
import ru.tech.imageresizershrinker.core.ui.widget.haptics.rememberCustomHapticFeedback
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

    val currentDestination = component.childStack.subscribeAsState().value.backStack
    LaunchedEffect(currentDestination) {
        delay(200L) // Delay for transition
        randomEmojiKey++
    }

    val settingsState = component.settingsState.toUiState(
        allEmojis = Emoji.allIcons(),
        allIconShapes = IconShapeDefaults.shapes,
        randomEmojiKey = randomEmojiKey,
        getEmojiColorTuple = component::getColorTupleFromEmoji
    )

    val editPresetsController = rememberEditPresetsController()

    CompositionLocalProvider(
        LocalToastHostState provides component.toastHostState,
        LocalSettingsState provides settingsState,
        LocalSimpleSettingsInteractor provides component.getSettingsInteractor(),
        LocalEditPresetsController provides editPresetsController,
        LocalConfettiHostState provides rememberConfettiHostState(),
        LocalImageLoader provides component.imageLoader,
        LocalHapticFeedback provides rememberCustomHapticFeedback(component.settingsState.hapticsStrength)
    ) {
        SecureModeHandler()

        var showSelectSheet by rememberSaveable(component.showSelectDialog) {
            mutableStateOf(component.showSelectDialog)
        }
        var showUpdateSheet by rememberSaveable(component.showUpdateDialog) {
            mutableStateOf(component.showUpdateDialog)
        }
        LaunchedEffect(settingsState) {
            GlobalExceptionHandler.setAllowCollectCrashlytics(settingsState.allowCollectCrashlytics)
            GlobalExceptionHandler.setAnalyticsCollectionEnabled(settingsState.allowCollectAnalytics)
        }

        LaunchedEffect(showSelectSheet) {
            if (!showSelectSheet) {
                delay(600)
                component.hideSelectDialog()
                component.updateUris(null)
            }
        }
        LaunchedEffect(showUpdateSheet) {
            if (!showUpdateSheet) {
                delay(600)
                component.cancelledUpdate()
            }
        }
        ImageToolboxTheme {
            val tiramisu = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

            if (!tiramisu) {
                BackHandler {
                    if (component.shouldShowDialog) showExitDialog = true
                    else context.finishAffinity()
                }
            }

            Surface(Modifier.fillMaxSize()) {
                ScreenSelector(
                    component = component,
                    onRegisterScreenOpen = GlobalExceptionHandler.Companion::registerScreenOpen
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
                    visible = showSelectSheet,
                    onDismiss = { showSelectSheet = it },
                    onNavigate = { screen ->
                        GlobalExceptionHandler.registerScreenOpen(screen)
                        component.navController.push(screen)
                        showSelectSheet = false
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
                visible = showUpdateSheet,
                onDismiss = {
                    showUpdateSheet = false
                }
            )

            ConfettiHost()

            ToastHost(
                hostState = LocalToastHostState.current
            )

            SideEffect {
                component.tryGetUpdate(
                    isInstalledFromMarket = context.isInstalledFromPlayStore()
                )
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

            if (component.showGithubReviewDialog) {
                GithubReviewDialog(
                    onDismiss = component::hideReviewDialog,
                    onNotShowAgain = {
                        ReviewHandler.notShowReviewAgain(context)
                    },
                    isNotShowAgainButtonVisible = ReviewHandler.showNotShowAgainButton
                )
            }

            if (component.showTelegramGroupDialog) {
                TelegramGroupDialog(
                    onDismiss = component::hideTelegramGroupDialog,
                    onRedirected = component::registerTelegramGroupOpen
                )
            }
        }
    }

}