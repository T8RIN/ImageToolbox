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

package ru.tech.imageresizershrinker.feature.settings.presentation.components

import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileDownloadOff
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.model.Setting
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseFileSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalContainerShape
import ru.tech.imageresizershrinker.core.ui.utils.provider.ProvideContainerDefaults
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.feature.settings.presentation.viewModel.SettingsViewModel

@Composable
internal fun SettingItem(
    setting: Setting,
    viewModel: SettingsViewModel,
    onTryGetUpdate: (
        isNewRequest: Boolean,
        onNoUpdates: () -> Unit
    ) -> Unit,
    onNavigateToEasterEgg: () -> Unit,
    onNavigateToSettings: () -> Boolean,
    isUpdateAvailable: Boolean,
    containerColor: Color = MaterialTheme.colorScheme.surface
) {
    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHostState.current
    val scope = rememberCoroutineScope()
    val confettiHostState = LocalConfettiHostState.current

    fun tryGetUpdate(
        isNewRequest: Boolean = true,
        onNoUpdates: () -> Unit = {
            scope.launch {
                toastHostState.showToast(
                    icon = Icons.Rounded.FileDownloadOff,
                    message = context.getString(R.string.no_updates)
                )
            }
        }
    ) = onTryGetUpdate(isNewRequest, onNoUpdates)

    ProvideContainerDefaults(
        color = containerColor,
        shape = LocalContainerShape.current
    ) {
        when (setting) {
            Setting.AddFileSize -> {
                AddFileSizeSettingItem(onClick = viewModel::toggleAddFileSize)
            }

            Setting.AddOriginalFilename -> {
                AddOriginalFilenameSettingItem(onClick = viewModel::toggleAddOriginalFilename)
            }

            Setting.AllowBetas -> {
                if (!context.isInstalledFromPlayStore()) {
                    AllowBetasSettingItem(
                        onClick = {
                            viewModel.toggleAllowBetas()
                            tryGetUpdate(
                                onNoUpdates = {}
                            )
                        }
                    )
                }
            }

            Setting.AllowImageMonet -> {
                AllowImageMonetSettingItem(onClick = viewModel::toggleAllowImageMonet)
            }

            Setting.AmoledMode -> {
                AmoledModeSettingItem(onClick = viewModel::toggleAmoledMode)
            }

            Setting.Analytics -> {
                AnalyticsSettingItem(onClick = viewModel::toggleAllowCollectAnalytics)
            }

            Setting.Author -> {
                AuthorSettingItem()
            }

            Setting.AutoCacheClear -> {
                AutoCacheClearSettingItem(onClick = viewModel::toggleClearCacheOnLaunch)
            }

            Setting.AutoCheckUpdates -> {
                AutoCheckUpdatesSettingItem(onClick = viewModel::toggleShowUpdateDialog)
            }

            Setting.Backup -> {
                BackupSettingItem(
                    onCreateBackupFilename = viewModel::createBackupFilename,
                    onCreateBackup = { uri ->
                        viewModel.createBackup(
                            uri = uri,
                            onResult = { result ->
                                context.parseFileSaveResult(
                                    saveResult = result,
                                    onSuccess = {
                                        confettiHostState.showConfetti()
                                    },
                                    toastHostState = toastHostState,
                                    scope = scope
                                )
                            }
                        )
                    }
                )
            }

            Setting.BorderThickness -> {
                BorderThicknessSettingItem(onValueChange = viewModel::setBorderWidth)
            }

            Setting.ChangeFont -> {
                ChangeFontSettingItem(
                    onValueChange = { font ->
                        viewModel.setFont(font.asDomain())
                        context.recreate()
                    }
                )
            }

            Setting.ChangeLanguage -> {
                ChangeLanguageSettingItem()
            }

            Setting.ClearCache -> {
                ClearCacheSettingItem(
                    value = viewModel.getReadableCacheSize(),
                    onClearCache = viewModel::clearCache
                )
            }

            Setting.ColorScheme -> {
                ColorSchemeSettingItem(
                    onToggleInvertColors = viewModel::toggleInvertColors,
                    onSetThemeStyle = viewModel::setThemeStyle,
                    onUpdateThemeContrast = viewModel::updateThemeContrast,
                    onUpdateColorTuple = viewModel::setColorTuple,
                    onUpdateColorTuples = viewModel::updateColorTuples,
                    onToggleUseEmojiAsPrimaryColor = viewModel::toggleUseEmojiAsPrimaryColor
                )
            }

            Setting.Crashlytics -> {
                CrashlyticsSettingItem(onClick = viewModel::toggleAllowCollectCrashlytics)
            }

            Setting.CurrentVersionCode -> {
                var clicks by rememberSaveable {
                    mutableIntStateOf(0)
                }
                LaunchedEffect(clicks) {
                    if (clicks >= 3) {
                        onNavigateToEasterEgg()
                        clicks = 0
                    }

                    delay(500L) //debounce

                    if (clicks == 0) return@LaunchedEffect

                    toastHostState.currentToastData?.dismiss()
                    if (clicks == 1) {
                        tryGetUpdate()
                    }
                }

                CurrentVersionCodeSettingItem(
                    isUpdateAvailable = isUpdateAvailable,
                    onClick = {
                        clicks++
                    }
                )
            }

            Setting.Donate -> {
                DonateSettingItem()
            }

            Setting.DynamicColors -> {
                DynamicColorsSettingItem(onClick = viewModel::toggleDynamicColors)
            }

            Setting.Emoji -> {
                EmojiSettingItem(
                    onAddColorTupleFromEmoji = viewModel::addColorTupleFromEmoji,
                    selectedEmojiIndex = viewModel.settingsState.selectedEmoji ?: 0,
                    onUpdateEmoji = viewModel::setEmoji
                )
            }

            Setting.EmojisCount -> {
                EmojisCountSettingItem(onValueChange = viewModel::setEmojisCount)
            }

            Setting.FabAlignment -> {
                FabAlignmentSettingItem(onValueChange = viewModel::setAlignment)
            }

            Setting.FilenamePrefix -> {
                FilenamePrefixSettingItem(onValueChange = viewModel::setFilenamePrefix)
            }

            Setting.FilenameSuffix -> {
                FilenameSuffixSettingItem(onValueChange = viewModel::setFilenameSuffix)
            }

            Setting.FontScale -> {
                FontScaleSettingItem(
                    onValueChange = {
                        viewModel.onUpdateFontScale(it)
                        context.recreate()
                    }
                )
            }

            Setting.GroupOptions -> {
                GroupOptionsSettingItem(onClick = viewModel::toggleGroupOptionsByType)
            }

            Setting.HelpTranslate -> {
                HelpTranslateSettingItem()
            }

            Setting.ImagePickerMode -> {
                ImagePickerModeSettingItemGroup(onValueChange = viewModel::setImagePickerMode)
            }

            Setting.IssueTracker -> {
                IssueTrackerSettingItem()
            }

            Setting.LockDrawOrientation -> {
                LockDrawOrientationSettingItem(onClick = viewModel::toggleLockDrawOrientation)
            }

            Setting.NightMode -> {
                NightModeSettingItemGroup(
                    value = viewModel.settingsState.nightMode,
                    onValueChange = viewModel::setNightMode
                )
            }

            Setting.Presets -> {
                PresetsSettingItem()
            }

            Setting.RandomizeFilename -> {
                RandomizeFilenameSettingItem(onClick = viewModel::toggleRandomizeFilename)
            }

            Setting.ReplaceSequenceNumber -> {
                ReplaceSequenceNumberSettingItem(onClick = viewModel::toggleAddSequenceNumber)
            }

            Setting.OverwriteFiles -> {
                OverwriteFilesSettingItem(onClick = viewModel::toggleOverwriteFiles)
            }

            Setting.Reset -> {
                ResetSettingsSettingItem(onReset = viewModel::resetSettings)
            }

            Setting.Restore -> {
                RestoreSettingItem(
                    onObtainBackupFile = {
                        viewModel.restoreBackupFrom(
                            uri = it,
                            onSuccess = {
                                scope.launch {
                                    confettiHostState.showConfetti()
                                    //Wait for confetti to appear, then trigger font scale adjustment
                                    delay(300L)
                                    context.recreate()
                                }
                                scope.launch {
                                    toastHostState.showToast(
                                        context.getString(R.string.settings_restored),
                                        Icons.Rounded.Save
                                    )
                                }
                            },
                            onFailure = {
                                scope.launch {
                                    toastHostState.showError(context, it)
                                }
                            }
                        )
                    }
                )
            }

            Setting.SavingFolder -> {
                SavingFolderSettingItemGroup(onValueChange = viewModel::updateSaveFolderUri)
            }

            Setting.ScreenOrder -> {
                ScreenOrderSettingItem(onValueChange = viewModel::updateOrder)
            }

            Setting.ScreenSearch -> {
                ScreenSearchSettingItem(onClick = viewModel::toggleScreenSearchEnabled)
            }

            Setting.SourceCode -> {
                SourceCodeSettingItem()
            }

            Setting.TelegramGroup -> {
                TelegramGroupSettingItem()
            }

            Setting.TelegramChannel -> {
                TelegramChannelSettingItem()
            }

            Setting.CheckUpdatesButton -> {
                CheckUpdatesButtonSettingItem(
                    onClick = ::tryGetUpdate
                )
            }

            Setting.ContainerShadows -> {
                ContainerShadowsSettingItem(onClick = viewModel::toggleDrawContainerShadows)
            }

            Setting.ButtonShadows -> {
                ButtonShadowsSettingItem(onClick = viewModel::toggleDrawButtonShadows)
            }

            Setting.FABShadows -> {
                FabShadowsSettingItem(onClick = viewModel::toggleDrawFabShadows)
            }

            Setting.SliderShadows -> {
                SliderShadowsSettingItem(onClick = viewModel::toggleDrawSliderShadows)
            }

            Setting.SwitchShadows -> {
                SwitchShadowsSettingItem(onClick = viewModel::toggleDrawSwitchShadows)
            }

            Setting.AppBarShadows -> {
                AppBarShadowsSettingItem(onClick = viewModel::toggleDrawAppBarShadows)
            }

            Setting.AutoPinClipboard -> {
                AutoPinClipboardSettingItem(onClick = viewModel::toggleAutoPinClipboard)
            }

            Setting.AutoPinClipboardOnlyClip -> {
                AutoPinClipboardOnlyClipSettingItem(onClick = viewModel::toggleAutoPinClipboardOnlyClip)
            }

            Setting.VibrationStrength -> {
                VibrationStrengthSettingItem(onValueChange = viewModel::setVibrationStrength)
            }

            Setting.DefaultScaleMode -> {
                DefaultScaleModeSettingItem(onValueChange = viewModel::setDefaultImageScaleMode)
            }

            Setting.SwitchType -> {
                SwitchTypeSettingItem(onValueChange = viewModel::setSwitchType)
            }

            Setting.Magnifier -> {
                MagnifierSettingItem(onClick = viewModel::toggleMagnifierEnabled)
            }

            Setting.ExifWidgetInitialState -> {
                ExifWidgetInitialStateSettingItem(onClick = viewModel::toggleExifWidgetInitialState)
            }

            Setting.BrightnessEnforcement -> {
                BrightnessEnforcementSettingItem(onValueChange = viewModel::updateBrightnessEnforcementScreens)
            }

            Setting.Confetti -> {
                ConfettiSettingItem(onClick = viewModel::toggleConfettiEnabled)
            }

            Setting.SecureMode -> {
                SecureModeSettingItem(onClick = viewModel::toggleSecureMode)
            }

            Setting.UseRandomEmojis -> {
                UseRandomEmojisSettingItem(onClick = viewModel::toggleUseRandomEmojis)
            }

            Setting.IconShape -> {
                IconShapeSettingItem(
                    value = viewModel.settingsState.iconShape,
                    onValueChange = viewModel::setIconShape
                )
            }

            Setting.DragHandleWidth -> {
                DragHandleWidthSettingItem(onValueChange = viewModel::setDragHandleWidth)
            }

            Setting.ConfettiType -> {
                ConfettiTypeSettingItem(onValueChange = viewModel::setConfettiType)
            }

            Setting.AllowAutoClipboardPaste -> {
                AllowAutoClipboardPasteSettingItem(onClick = viewModel::toggleAllowAutoClipboardPaste)
            }

            Setting.ConfettiHarmonizer -> {
                ConfettiHarmonizerSettingItem(onValueChange = viewModel::setConfettiHarmonizer)
            }

            Setting.ConfettiHarmonizationLevel -> {
                ConfettiHarmonizationLevelSettingItem(onValueChange = viewModel::setConfettiHarmonizationLevel)
            }

            Setting.GeneratePreviews -> {
                GeneratePreviewsSettingItem(onClick = viewModel::toggleGeneratePreviews)
            }

            Setting.SkipFilePicking -> {
                SkipImagePickingSettingItem(onClick = viewModel::toggleSkipImagePicking)
            }

            Setting.ShowSettingsInLandscape -> {
                ShowSettingsInLandscapeSettingItem(onClick = viewModel::toggleShowSettingsInLandscape)
            }

            Setting.UseFullscreenSettings -> {
                UseFullscreenSettingsSettingItem(
                    onClick = viewModel::toggleUseFullscreenSettings,
                    onNavigateToSettings = onNavigateToSettings
                )
            }

            Setting.DefaultDrawLineWidth -> {
                DefaultDrawLineWidthSettingItem(onValueChange = viewModel::setDefaultDrawLineWidth)
            }

            Setting.OpenEditInsteadOfPreview -> {
                OpenEditInsteadOfPreviewSettingItem(onClick = viewModel::toggleOpenEditInsteadOfPreview)
            }

            Setting.CanEnterPresetsByTextField -> {
                CanEnterPresetsByTextFieldSettingItem(onClick = viewModel::toggleCanEnterPresetsByTextField)
            }

            Setting.ColorBlindScheme -> {
                ColorBlindSchemeSettingItem(onValueChange = viewModel::setColorBlindScheme)
            }

            Setting.EnableLinksPreview -> {
                EnableLinksPreviewSettingItem(onClick = viewModel::toggleIsLinksPreviewEnabled)
            }

            Setting.DefaultDrawColor -> {
                DefaultDrawColorSettingItem(onValueChange = viewModel::setDefaultDrawColor)
            }

            Setting.DefaultDrawPathMode -> {
                DefaultDrawPathModeSettingItem(onValueChange = viewModel::setDefaultDrawPathMode)
            }

            Setting.AddTimestampToFilename -> {
                AddTimestampToFilenameSettingItem(onClick = viewModel::toggleAddTimestampToFilename)
            }

            Setting.UseFormattedFilenameTimestamp -> {
                UseFormattedFilenameTimestampSettingItem(onClick = viewModel::toggleUseFormattedFilenameTimestamp)
            }

            Setting.OneTimeSaveLocation -> {
                OneTimeSaveLocationSettingItem()
            }

            Setting.DefaultResizeType -> {
                DefaultResizeTypeSettingItem(onValueChange = viewModel::setDefaultResizeType)
            }

            Setting.SystemBarsVisibility -> {
                SystemBarsVisibilitySettingItem(onValueChange = viewModel::setSystemBarsVisibility)
            }

            Setting.ShowSystemBarsBySwipe -> {
                ShowSystemBarsBySwipeSettingItem(onClick = viewModel::toggleIsSystemBarsVisibleBySwipe)
            }
        }
    }
}