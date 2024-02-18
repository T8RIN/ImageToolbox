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

package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileDownloadOff
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.domain.model.CopyToClipboardMode
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.feature.main.presentation.viewModel.MainViewModel

@Composable
internal fun SettingItem(
    setting: Setting,
    viewModel: MainViewModel
) {
    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHostState.current
    val scope = rememberCoroutineScope()
    val confettiController = LocalConfettiController.current

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
                    onClick = { viewModel.toggleAllowBetas(context.isInstalledFromPlayStore()) }
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
            AutoCacheClearSettingItem(
                onClick = { viewModel.toggleClearCacheOnLaunch() }
            )
        }

        Setting.AutoCheckUpdates -> {
            AutoCheckUpdatesSettingItem(
                onClick = { viewModel.toggleShowUpdateDialog() }
            )
        }

        Setting.Backup -> {
            BackupSettingItem(
                createBackupFilename = viewModel::createBackupFilename,
                createBackup = {
                    viewModel.createBackup(
                        outputStream = context.contentResolver.openOutputStream(
                            it,
                            "rw"
                        ),
                        onSuccess = {
                            scope.launch {
                                confettiController.showEmpty()
                            }
                            scope.launch {
                                toastHostState.showToast(
                                    context.getString(
                                        R.string.saved_to_without_filename,
                                        ""
                                    ),
                                    Icons.Rounded.Save
                                )
                            }
                        }
                    )
                }
            )
        }

        Setting.BorderThickness -> {
            BorderThicknessSettingItem(updateBorderWidth = viewModel::setBorderWidth)
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
                updateColorTuples = viewModel::updateColorTuples
            )
        }

        Setting.Crashlytics -> {
            CrashlyticsSettingItem(
                onClick = { viewModel.toggleAllowCollectCrashlytics() }
            )
        }

        Setting.CurrentVersionCode -> {
            CurrentVersionCodeSettingItem(
                updateAvailable = viewModel.updateAvailable,
                onTryGetUpdate = {
                    viewModel.tryGetUpdate(
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
            ReplaceSequenceNumberSettingItem(
                onClick = { viewModel.toggleAddSequenceNumber() }
            )
        }

        Setting.OverwriteFiles -> {
            OverwriteFilesSettingItem(
                onClick = { viewModel.toggleOverwriteFiles() }
            )
        }

        Setting.Reset -> {
            ResetSettingsSettingItem(
                onReset = viewModel::resetSettings
            )
        }

        Setting.Restore -> {
            RestoreSettingItem(
                restoreBackupFrom = {
                    viewModel.restoreBackupFrom(
                        uri = it,
                        onSuccess = {
                            scope.launch {
                                confettiController.showEmpty()
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                EnhancedButton(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    onClick = {
                        viewModel.tryGetUpdate(
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
                ) {
                    AutoSizeText(text = stringResource(R.string.check_for_updates), maxLines = 1)
                }
            }
        }

        Setting.ContainerShadows -> {
            ContainerShadowsSettingItem(onClick = { viewModel.toggleDrawContainerShadows() })
        }

        Setting.ButtonShadows -> {
            ButtonShadowsSettingItem(onClick = { viewModel.toggleDrawButtonShadows() })
        }

        Setting.FABShadows -> {
            FabShadowsSettingItem(onClick = { viewModel.toggleDrawFabShadows() })
        }

        Setting.SliderShadows -> {
            SliderShadowsSettingItem(onClick = { viewModel.toggleDrawSliderShadows() })
        }

        Setting.SwitchShadows -> {
            SwitchShadowsSettingItem(onClick = { viewModel.toggleDrawSwitchShadows() })
        }

        Setting.AppBarShadows -> {
            AppBarShadowsSettingItem(onClick = { viewModel.toggleDrawAppBarShadows() })
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

        Setting.UsePixelSwitch -> {
            UsePixelSwitchSettingItem(onClick = { viewModel.toggleUsePixelSwitch() })
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
    }
}