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

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrowserUpdated
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.domain.model.CopyToClipboardMode
import ru.tech.imageresizershrinker.core.settings.presentation.model.Setting
import ru.tech.imageresizershrinker.core.ui.theme.mixedContainer
import ru.tech.imageresizershrinker.core.ui.theme.onMixedContainer
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseFileSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalContainerShape
import ru.tech.imageresizershrinker.core.ui.utils.provider.ProvideContainerDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.feature.settings.presentation.viewModel.SettingsViewModel

@Composable
internal fun SettingItem(
    setting: Setting,
    viewModel: SettingsViewModel,
    onTryGetUpdate: (
        newRequest: Boolean,
        installedFromMarket: Boolean,
        onNoUpdates: () -> Unit
    ) -> Unit,
    onNavigateToEasterEgg: () -> Unit,
    onNavigateToSettings: () -> Boolean,
    updateAvailable: Boolean,
    color: Color = MaterialTheme.colorScheme.surface
) {
    fun tryGetUpdate(
        newRequest: Boolean = false,
        installedFromMarket: Boolean,
        onNoUpdates: () -> Unit = {}
    ) = onTryGetUpdate(newRequest, installedFromMarket, onNoUpdates)

    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHostState.current
    val scope = rememberCoroutineScope()
    val confettiHostState = LocalConfettiHostState.current

    ProvideContainerDefaults(
        color = color,
        shape = LocalContainerShape.current
    ) {
        when (setting) {
            Setting.AddFileSize -> {
                AddFileSizeSettingItem(
                    onClick = { viewModel.toggleAddFileSize() }
                )
            }

            Setting.AddOriginalFilename -> {
                AddOriginalFilenameSettingItem(
                    onClick = { viewModel.toggleAddOriginalFilename() }
                )
            }

            Setting.AllowBetas -> {
                if (!context.isInstalledFromPlayStore()) {
                    AllowBetasSettingItem(
                        onClick = {
                            viewModel.toggleAllowBetas()
                            tryGetUpdate(
                                true, context.isInstalledFromPlayStore()
                            )
                        }
                    )
                }
            }

            Setting.AllowImageMonet -> {
                AllowImageMonetSettingItem(onClick = { viewModel.toggleAllowImageMonet() })
            }

            Setting.AmoledMode -> {
                AmoledModeSettingItem(
                    onClick = { viewModel.toggleAmoledMode() }
                )
            }

            Setting.Analytics -> {
                AnalyticsSettingItem(
                    onClick = { viewModel.toggleAllowCollectAnalytics() }
                )
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
                    createBackupFilename = viewModel::createBackupFilename,
                    createBackup = { uri ->
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
                    onFontSelected = { font ->
                        viewModel.setFont(font.asDomain())
                        (context as? Activity)?.recreate()
                    }
                )
            }

            Setting.ChangeLanguage -> {
                ChangeLanguageSettingItem(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    shape = ContainerShapeDefaults.topShape
                )
            }

            Setting.ClearCache -> {
                ClearCacheSettingItem(
                    value = viewModel.getReadableCacheSize(),
                    clearCache = viewModel::clearCache
                )
            }

            Setting.ColorScheme -> {
                ColorSchemeSettingItem(
                    toggleInvertColors = viewModel::toggleInvertColors,
                    setThemeStyle = viewModel::setThemeStyle,
                    updateThemeContrast = viewModel::updateThemeContrast,
                    updateColorTuple = viewModel::setColorTuple,
                    updateColorTuples = viewModel::updateColorTuples,
                    onToggleUseEmojiAsPrimaryColor = viewModel::toggleUseEmojiAsPrimaryColor
                )
            }

            Setting.Crashlytics -> {
                CrashlyticsSettingItem(
                    onClick = { viewModel.toggleAllowCollectCrashlytics() }
                )
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
                        tryGetUpdate(
                            newRequest = true,
                            installedFromMarket = context.isInstalledFromPlayStore(),
                            onNoUpdates = {
                                scope.launch {
                                    toastHostState.showToast(
                                        icon = Icons.Rounded.FileDownloadOff,
                                        message = context.getString(R.string.no_updates)
                                    )
                                }
                            }
                        )
                    }
                }

                CurrentVersionCodeSettingItem(
                    updateAvailable = updateAvailable,
                    onClick = {
                        clicks++
                    }
                )
            }

            Setting.Donate -> {
                DonateSettingItem()
            }

            Setting.DynamicColors -> {
                DynamicColorsSettingItem(
                    onClick = { viewModel.toggleDynamicColors() }
                )
            }

            Setting.Emoji -> {
                EmojiSettingItem(
                    addColorTupleFromEmoji = viewModel::addColorTupleFromEmoji,
                    selectedEmojiIndex = viewModel.settingsState.selectedEmoji ?: 0,
                    updateEmoji = viewModel::setEmoji
                )
            }

            Setting.EmojisCount -> {
                EmojisCountSettingItem(updateEmojisCount = viewModel::setEmojisCount)
            }

            Setting.FabAlignment -> {
                FabAlignmentSettingItem(updateAlignment = viewModel::setAlignment)
            }

            Setting.FilenamePrefix -> {
                FilenamePrefixSettingItem(
                    onValueChange = viewModel::setFilenamePrefix
                )
            }

            Setting.FilenameSuffix -> {
                FilenameSuffixSettingItem(
                    onValueChange = viewModel::setFilenameSuffix
                )
            }

            Setting.FontScale -> {
                FontScaleSettingItem(
                    onValueChange = {
                        viewModel.onUpdateFontScale(it)
                        (context as Activity).recreate()
                    }
                )
            }

            Setting.GroupOptions -> {
                GroupOptionsSettingItem(
                    onClick = { viewModel.toggleGroupOptionsByType() }
                )
            }

            Setting.HelpTranslate -> {
                HelpTranslateSettingItem()
            }

            Setting.ImagePickerMode -> {
                ImagePickerModeSettingItemGroup(
                    updateImagePickerMode = viewModel::setImagePickerMode
                )
            }

            Setting.IssueTracker -> {
                IssueTrackerSettingItem()
            }

            Setting.LockDrawOrientation -> {
                LockDrawOrientationSettingItem(
                    onClick = { viewModel.toggleLockDrawOrientation() }
                )
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
                RandomizeFilenameSettingItem(
                    onClick = { viewModel.toggleRandomizeFilename() }
                )
            }

            Setting.ReplaceSequenceNumber -> {
                ReplaceSequenceNumberSettingItem(onClick = viewModel::toggleAddSequenceNumber)
            }

            Setting.OverwriteFiles -> {
                OverwriteFilesSettingItem(
                    onClick = { viewModel.toggleOverwriteFiles() }
                )
            }

            Setting.Reset -> {
                ResetSettingsSettingItem(onReset = viewModel::resetSettings)
            }

            Setting.Restore -> {
                RestoreSettingItem(
                    restoreBackupFrom = {
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
                            }
                        ) {
                            scope.launch {
                                toastHostState.showError(context, it)
                            }
                        }
                    }
                )
            }

            Setting.SavingFolder -> {
                SavingFolderSettingItemGroup(
                    updateSaveFolderUri = viewModel::updateSaveFolderUri
                )
            }

            Setting.ScreenOrder -> {
                ScreenOrderSettingItem(
                    updateOrder = viewModel::updateOrder
                )
            }

            Setting.ScreenSearch -> {
                ScreenSearchSettingItem(
                    onClick = { viewModel.toggleScreenSearchEnabled() }
                )
            }

            Setting.SourceCode -> {
                SourceCodeSettingItem(
                    shape = ContainerShapeDefaults.bottomShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            }

            Setting.Telegram -> {
                TelegramSettingItem()
            }

            Setting.CheckUpdatesButton -> {
                PreferenceItem(
                    title = stringResource(R.string.check_for_updates),
                    color = MaterialTheme.colorScheme.mixedContainer,
                    contentColor = MaterialTheme.colorScheme.onMixedContainer,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    shape = ContainerShapeDefaults.bottomShape,
                    startIcon = Icons.Outlined.BrowserUpdated,
                    overrideIconShapeContentColor = true,
                    onClick = {
                        tryGetUpdate(
                            newRequest = true,
                            installedFromMarket = context.isInstalledFromPlayStore(),
                            onNoUpdates = {
                                scope.launch {
                                    toastHostState.showToast(
                                        icon = Icons.Rounded.FileDownloadOff,
                                        message = context.getString(R.string.no_updates)
                                    )
                                }
                            }
                        )
                    }
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
                AutoPinClipboardSettingItem(
                    onClick = {
                        if (it) {
                            viewModel.setCopyToClipboardMode(CopyToClipboardMode.Enabled.WithSaving)
                        } else {
                            viewModel.setCopyToClipboardMode(CopyToClipboardMode.Disabled)
                        }
                    }
                )
            }

            Setting.AutoPinClipboardOnlyClip -> {
                AutoPinClipboardOnlyClipSettingItem(
                    onClick = {
                        if (it) {
                            viewModel.setCopyToClipboardMode(CopyToClipboardMode.Enabled.WithoutSaving)
                        } else {
                            viewModel.setCopyToClipboardMode(CopyToClipboardMode.Enabled.WithSaving)
                        }
                    }
                )
            }

            Setting.VibrationStrength -> {
                VibrationStrengthSettingItem(onValueChange = viewModel::setVibrationStrength)
            }

            Setting.DefaultScaleMode -> {
                DefaultScaleModeSettingItem(
                    onValueChange = viewModel::setDefaultImageScaleMode
                )
            }

            Setting.SwitchType -> {
                SwitchTypeSettingItem(onValueChange = viewModel::setSwitchType)
            }

            Setting.Magnifier -> {
                MagnifierSettingItem(onClick = { viewModel.toggleMagnifierEnabled() })
            }

            Setting.ExifWidgetInitialState -> {
                ExifWidgetInitialStateSettingItem(onClick = { viewModel.toggleExifWidgetInitialState() })
            }

            Setting.BrightnessEnforcement -> {
                BrightnessEnforcementSettingItem(
                    updateScreens = viewModel::updateBrightnessEnforcementScreens
                )
            }

            Setting.Confetti -> {
                ConfettiSettingItem(viewModel::toggleConfettiEnabled)
            }

            Setting.SecureMode -> {
                SecureModeSettingItem(viewModel::toggleSecureMode)
            }

            Setting.UseRandomEmojis -> {
                UseRandomEmojisSettingItem(viewModel::toggleUseRandomEmojis)
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
        }
    }
}