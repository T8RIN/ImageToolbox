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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.model.Setting
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseFileSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalContainerShape
import ru.tech.imageresizershrinker.core.ui.utils.provider.ProvideContainerDefaults
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.feature.settings.presentation.screenLogic.SettingsComponent

@Composable
internal fun SettingItem(
    setting: Setting,
    component: SettingsComponent,
    onTryGetUpdate: (
        isNewRequest: Boolean,
        onNoUpdates: () -> Unit,
    ) -> Unit,
    onNavigateToEasterEgg: () -> Unit,
    onNavigateToSettings: () -> Boolean,
    isUpdateAvailable: Boolean,
    containerColor: Color = MaterialTheme.colorScheme.surface,
) {
    val context = LocalComponentActivity.current
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
        },
    ) = onTryGetUpdate(isNewRequest, onNoUpdates)

    ProvideContainerDefaults(
        color = containerColor,
        shape = LocalContainerShape.current
    ) {
        when (setting) {
            Setting.AddFileSize -> {
                AddFileSizeSettingItem(onClick = component::toggleAddFileSize)
            }

            Setting.AddOriginalFilename -> {
                AddOriginalFilenameSettingItem(onClick = component::toggleAddOriginalFilename)
            }

            Setting.AllowBetas -> {
                if (!context.isInstalledFromPlayStore()) {
                    AllowBetasSettingItem(
                        onClick = {
                            component.toggleAllowBetas()
                            tryGetUpdate(
                                onNoUpdates = {}
                            )
                        }
                    )
                }
            }

            Setting.AllowImageMonet -> {
                AllowImageMonetSettingItem(onClick = component::toggleAllowImageMonet)
            }

            Setting.AmoledMode -> {
                AmoledModeSettingItem(onClick = component::toggleAmoledMode)
            }

            Setting.Analytics -> {
                AnalyticsSettingItem(onClick = component::toggleAllowCollectAnalytics)
            }

            Setting.Author -> {
                AuthorSettingItem()
            }

            Setting.AutoCacheClear -> {
                AutoCacheClearSettingItem(onClick = component::toggleClearCacheOnLaunch)
            }

            Setting.AutoCheckUpdates -> {
                AutoCheckUpdatesSettingItem(onClick = component::toggleShowUpdateDialog)
            }

            Setting.Backup -> {
                BackupSettingItem(
                    onCreateBackupFilename = component::createBackupFilename,
                    onCreateBackup = { uri ->
                        component.createBackup(
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
                BorderThicknessSettingItem(onValueChange = component::setBorderWidth)
            }

            Setting.ChangeFont -> {
                ChangeFontSettingItem(
                    onValueChange = { font ->
                        component.setFont(font.asDomain())
                        context.recreate()
                    }
                )
            }

            Setting.ChangeLanguage -> {
                ChangeLanguageSettingItem()
            }

            Setting.ClearCache -> {
                ClearCacheSettingItem(
                    value = component.getReadableCacheSize(),
                    onClearCache = component::clearCache
                )
            }

            Setting.ColorScheme -> {
                ColorSchemeSettingItem(
                    onToggleInvertColors = component::toggleInvertColors,
                    onSetThemeStyle = component::setThemeStyle,
                    onUpdateThemeContrast = component::updateThemeContrast,
                    onUpdateColorTuple = component::setColorTuple,
                    onUpdateColorTuples = component::updateColorTuples,
                    onToggleUseEmojiAsPrimaryColor = component::toggleUseEmojiAsPrimaryColor
                )
            }

            Setting.Crashlytics -> {
                CrashlyticsSettingItem(onClick = component::toggleAllowCollectCrashlytics)
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
                DynamicColorsSettingItem(onClick = component::toggleDynamicColors)
            }

            Setting.Emoji -> {
                EmojiSettingItem(
                    onAddColorTupleFromEmoji = component::addColorTupleFromEmoji,
                    selectedEmojiIndex = component.settingsState.selectedEmoji ?: 0,
                    onUpdateEmoji = component::setEmoji
                )
            }

            Setting.EmojisCount -> {
                EmojisCountSettingItem(onValueChange = component::setEmojisCount)
            }

            Setting.FabAlignment -> {
                FabAlignmentSettingItem(onValueChange = component::setAlignment)
            }

            Setting.FilenamePrefix -> {
                FilenamePrefixSettingItem(onValueChange = component::setFilenamePrefix)
            }

            Setting.FilenameSuffix -> {
                FilenameSuffixSettingItem(onValueChange = component::setFilenameSuffix)
            }

            Setting.FontScale -> {
                FontScaleSettingItem(
                    onValueChange = {
                        component.onUpdateFontScale(it)
                        context.recreate()
                    }
                )
            }

            Setting.GroupOptions -> {
                GroupOptionsSettingItem(onClick = component::toggleGroupOptionsByType)
            }

            Setting.HelpTranslate -> {
                HelpTranslateSettingItem()
            }

            Setting.ImagePickerMode -> {
                ImagePickerModeSettingItemGroup(onValueChange = component::setImagePickerMode)
            }

            Setting.IssueTracker -> {
                IssueTrackerSettingItem()
            }

            Setting.LockDrawOrientation -> {
                LockDrawOrientationSettingItem(onClick = component::toggleLockDrawOrientation)
            }

            Setting.NightMode -> {
                NightModeSettingItemGroup(
                    value = component.settingsState.nightMode,
                    onValueChange = component::setNightMode
                )
            }

            Setting.Presets -> {
                PresetsSettingItem()
            }

            Setting.RandomizeFilename -> {
                RandomizeFilenameSettingItem(onClick = component::toggleRandomizeFilename)
            }

            Setting.ReplaceSequenceNumber -> {
                ReplaceSequenceNumberSettingItem(onClick = component::toggleAddSequenceNumber)
            }

            Setting.OverwriteFiles -> {
                OverwriteFilesSettingItem(onClick = component::toggleOverwriteFiles)
            }

            Setting.Reset -> {
                ResetSettingsSettingItem(onReset = component::resetSettings)
            }

            Setting.Restore -> {
                RestoreSettingItem(
                    onObtainBackupFile = {
                        component.restoreBackupFrom(
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
                SavingFolderSettingItemGroup(onValueChange = component::updateSaveFolderUri)
            }

            Setting.ScreenOrder -> {
                ScreenOrderSettingItem(onValueChange = component::updateOrder)
            }

            Setting.ScreenSearch -> {
                ScreenSearchSettingItem(onClick = component::toggleScreenSearchEnabled)
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
                ContainerShadowsSettingItem(onClick = component::toggleDrawContainerShadows)
            }

            Setting.ButtonShadows -> {
                ButtonShadowsSettingItem(onClick = component::toggleDrawButtonShadows)
            }

            Setting.FABShadows -> {
                FabShadowsSettingItem(onClick = component::toggleDrawFabShadows)
            }

            Setting.SliderShadows -> {
                SliderShadowsSettingItem(onClick = component::toggleDrawSliderShadows)
            }

            Setting.SwitchShadows -> {
                SwitchShadowsSettingItem(onClick = component::toggleDrawSwitchShadows)
            }

            Setting.AppBarShadows -> {
                AppBarShadowsSettingItem(onClick = component::toggleDrawAppBarShadows)
            }

            Setting.AutoPinClipboard -> {
                AutoPinClipboardSettingItem(onClick = component::toggleAutoPinClipboard)
            }

            Setting.AutoPinClipboardOnlyClip -> {
                AutoPinClipboardOnlyClipSettingItem(onClick = component::toggleAutoPinClipboardOnlyClip)
            }

            Setting.VibrationStrength -> {
                VibrationStrengthSettingItem(onValueChange = component::setVibrationStrength)
            }

            Setting.DefaultScaleMode -> {
                DefaultScaleModeSettingItem(onValueChange = component::setDefaultImageScaleMode)
            }

            Setting.SwitchType -> {
                SwitchTypeSettingItem(onValueChange = component::setSwitchType)
            }

            Setting.Magnifier -> {
                MagnifierSettingItem(onClick = component::toggleMagnifierEnabled)
            }

            Setting.ExifWidgetInitialState -> {
                ExifWidgetInitialStateSettingItem(onClick = component::toggleExifWidgetInitialState)
            }

            Setting.BrightnessEnforcement -> {
                BrightnessEnforcementSettingItem(onValueChange = component::updateBrightnessEnforcementScreens)
            }

            Setting.Confetti -> {
                ConfettiSettingItem(onClick = component::toggleConfettiEnabled)
            }

            Setting.SecureMode -> {
                SecureModeSettingItem(onClick = component::toggleSecureMode)
            }

            Setting.UseRandomEmojis -> {
                UseRandomEmojisSettingItem(onClick = component::toggleUseRandomEmojis)
            }

            Setting.IconShape -> {
                IconShapeSettingItem(
                    value = component.settingsState.iconShape,
                    onValueChange = component::setIconShape
                )
            }

            Setting.DragHandleWidth -> {
                DragHandleWidthSettingItem(onValueChange = component::setDragHandleWidth)
            }

            Setting.ConfettiType -> {
                ConfettiTypeSettingItem(onValueChange = component::setConfettiType)
            }

            Setting.AllowAutoClipboardPaste -> {
                AllowAutoClipboardPasteSettingItem(onClick = component::toggleAllowAutoClipboardPaste)
            }

            Setting.ConfettiHarmonizer -> {
                ConfettiHarmonizerSettingItem(onValueChange = component::setConfettiHarmonizer)
            }

            Setting.ConfettiHarmonizationLevel -> {
                ConfettiHarmonizationLevelSettingItem(onValueChange = component::setConfettiHarmonizationLevel)
            }

            Setting.GeneratePreviews -> {
                GeneratePreviewsSettingItem(onClick = component::toggleGeneratePreviews)
            }

            Setting.SkipFilePicking -> {
                SkipImagePickingSettingItem(onClick = component::toggleSkipImagePicking)
            }

            Setting.ShowSettingsInLandscape -> {
                ShowSettingsInLandscapeSettingItem(onClick = component::toggleShowSettingsInLandscape)
            }

            Setting.UseFullscreenSettings -> {
                UseFullscreenSettingsSettingItem(
                    onClick = component::toggleUseFullscreenSettings,
                    onNavigateToSettings = onNavigateToSettings
                )
            }

            Setting.DefaultDrawLineWidth -> {
                DefaultDrawLineWidthSettingItem(onValueChange = component::setDefaultDrawLineWidth)
            }

            Setting.OpenEditInsteadOfPreview -> {
                OpenEditInsteadOfPreviewSettingItem(onClick = component::toggleOpenEditInsteadOfPreview)
            }

            Setting.CanEnterPresetsByTextField -> {
                CanEnterPresetsByTextFieldSettingItem(onClick = component::toggleCanEnterPresetsByTextField)
            }

            Setting.ColorBlindScheme -> {
                ColorBlindSchemeSettingItem(onValueChange = component::setColorBlindScheme)
            }

            Setting.EnableLinksPreview -> {
                EnableLinksPreviewSettingItem(onClick = component::toggleIsLinksPreviewEnabled)
            }

            Setting.DefaultDrawColor -> {
                DefaultDrawColorSettingItem(onValueChange = component::setDefaultDrawColor)
            }

            Setting.DefaultDrawPathMode -> {
                DefaultDrawPathModeSettingItem(onValueChange = component::setDefaultDrawPathMode)
            }

            Setting.AddTimestampToFilename -> {
                AddTimestampToFilenameSettingItem(onClick = component::toggleAddTimestampToFilename)
            }

            Setting.UseFormattedFilenameTimestamp -> {
                UseFormattedFilenameTimestampSettingItem(onClick = component::toggleUseFormattedFilenameTimestamp)
            }

            Setting.OneTimeSaveLocation -> {
                OneTimeSaveLocationSettingItem()
            }

            Setting.DefaultResizeType -> {
                DefaultResizeTypeSettingItem(onValueChange = component::setDefaultResizeType)
            }

            Setting.SystemBarsVisibility -> {
                SystemBarsVisibilitySettingItem(onValueChange = component::setSystemBarsVisibility)
            }

            Setting.ShowSystemBarsBySwipe -> {
                ShowSystemBarsBySwipeSettingItem(onClick = component::toggleIsSystemBarsVisibleBySwipe)
            }

            Setting.UseCompactSelectors -> {
                UseCompactSelectorsSettingItem(onClick = component::toggleUseCompactSelectors)
            }

            Setting.MainScreenTitle -> {
                MainScreenTitleSettingItem(onValueChange = component::setMainScreenTitle)
            }

            Setting.SliderType -> {
                SliderTypeSettingItem(onValueChange = component::setSliderType)
            }
        }
    }
}