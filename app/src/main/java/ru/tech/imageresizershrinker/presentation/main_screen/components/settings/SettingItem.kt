package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileDownloadOff
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.presentation.root.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ContextUtils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.other.showError
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun SettingItem(setting: SettingItem, viewModel: MainViewModel) {
    val context = LocalContext.current
    val toastHostState = LocalToastHost.current
    val scope = rememberCoroutineScope()
    val confettiController = LocalConfettiController.current
    val settingsState = LocalSettingsState.current

    when (setting) {
        SettingItem.AddFileSize -> {
            AddFileSizeSettingItem(
                onClick = { viewModel.toggleAddFileSize() }
            )
        }

        SettingItem.AddOriginalFilename -> {
            AddOriginalFilenameSettingItem(
                onClick = { viewModel.toggleAddOriginalFilename() }
            )
        }

        SettingItem.AllowBetas -> {
            if (!context.isInstalledFromPlayStore()) {
                AllowBetasSettingItem(
                    onClick = { viewModel.toggleAllowBetas(context.isInstalledFromPlayStore()) }
                )
            }
        }

        SettingItem.AllowImageMonet -> {
            AllowImageMonetSettingItem(onClick = { viewModel.toggleAllowImageMonet() })
        }

        SettingItem.AmoledMode -> {
            AmoledModeSettingItem(
                onClick = { viewModel.toggleAmoledMode() }
            )
        }

        SettingItem.Analytics -> {
            AnalyticsSettingItem(
                onClick = { viewModel.toggleAllowCollectAnalytics() }
            )
        }

        SettingItem.Author -> {
            AuthorSettingItem()
        }

        SettingItem.AutoCacheClear -> {
            AutoCacheClearSettingItem(
                onClick = { viewModel.toggleClearCacheOnLaunch() }
            )
        }

        SettingItem.AutoCheckUpdates -> {
            AutoCheckUpdatesSettingItem(
                onClick = { viewModel.toggleShowUpdateDialog() }
            )
        }

        SettingItem.Backup -> {
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

        SettingItem.BorderThickness -> {
            BorderThicknessSettingItem(updateBorderWidth = viewModel::setBorderWidth)
        }

        SettingItem.ChangeFont -> {
            ChangeFontSettingItem(
                onFontSelected = { font ->
                    viewModel.setFont(font.asDomain())
                    (context as? Activity)?.recreate()
                }
            )
        }

        SettingItem.ChangeLanguage -> {
            ChangeLanguageSettingItem(
                modifier = Modifier.padding(horizontal = 8.dp),
                shape = SettingsShapeDefaults.topShape
            )
        }

        SettingItem.ClearCache -> {
            ClearCacheSettingItem(
                value = viewModel.getReadableCacheSize(),
                clearCache = viewModel::clearCache
            )
        }

        SettingItem.ColorScheme -> {
            ColorSchemeSettingItem(
                toggleInvertColors = viewModel::toggleInvertColors,
                setThemeStyle = viewModel::setThemeStyle,
                updateThemeContrast = viewModel::updateThemeContrast,
                updateColorTuple = viewModel::updateColorTuple,
                updateColorTuples = viewModel::updateColorTuples
            )
        }

        SettingItem.Crashlytics -> {
            CrashlyticsSettingItem(
                onClick = { viewModel.toggleAllowCollectCrashlytics() }
            )
        }

        SettingItem.CurrentVersionCode -> {
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

        SettingItem.Donate -> {
            DonateSettingItem()
        }

        SettingItem.DynamicColors -> {
            DynamicColorsSettingItem(
                onClick = { viewModel.toggleDynamicColors() }
            )
        }

        SettingItem.Emoji -> {
            EmojiSettingItem(
                addColorTupleFromEmoji = viewModel::addColorTupleFromEmoji,
                selectedEmojiIndex = viewModel.settingsState.selectedEmoji ?: 0,
                updateEmoji = viewModel::updateEmoji
            )
        }

        SettingItem.EmojisCount -> {
            EmojisCountSettingItem(updateEmojisCount = viewModel::updateEmojisCount)
        }

        SettingItem.EnableShadows -> {
            AnimatedVisibility(visible = settingsState.borderWidth <= 0.dp) {
                EnableShadowsSettingItem(onClick = { viewModel.toggleAllowShowingShadowsInsteadOfBorders() })
            }
        }

        SettingItem.FabAlignment -> {
            FabAlignmentSettingItem(updateAlignment = viewModel::setAlignment)
        }

        SettingItem.FilenamePrefix -> {
            FilenamePrefixSettingItem(
                updateFilenamePrefix = viewModel::updateFilename
            )
        }

        SettingItem.FontScale -> {
            FontScaleSettingItem(
                onValueChange = viewModel::onUpdateFontScale
            )
        }

        SettingItem.GroupOptions -> {
            GroupOptionsSettingItem(
                onClick = { viewModel.toggleGroupOptionsByType() }
            )
        }

        SettingItem.HelpTranslate -> {
            HelpTranslateSettingItem()
        }

        SettingItem.ImagePickerMode -> {
            ImagePickerModeSettingItemGroup(
                updateImagePickerMode = viewModel::updateImagePickerMode
            )
        }

        SettingItem.IssueTracker -> {
            IssueTrackerSettingItem()
        }

        SettingItem.LockDrawOrientation -> {
            LockDrawOrientationSettingItem(
                onClick = { viewModel.toggleLockDrawOrientation() }
            )
        }

        SettingItem.NightMode -> {
            NightModeSettingItemGroup(
                value = viewModel.settingsState.nightMode,
                onValueChange = viewModel::setNightMode
            )
        }

        SettingItem.Presets -> {
            PresetsSettingItem()
        }

        SettingItem.RandomizeFilename -> {
            RandomizeFilenameSettingItem(
                onClick = { viewModel.toggleRandomizeFilename() }
            )
        }

        SettingItem.ReplaceSequenceNumber -> {
            ReplaceSequenceNumberSettingItem(
                onClick = { viewModel.toggleAddSequenceNumber() }
            )
        }

        SettingItem.Reset -> {
            ResetSettingsSettingItem(
                onReset = viewModel::resetSettings
            )
        }

        SettingItem.Restore -> {
            RestoreSettingItem(
                restoreBackupFrom = {
                    viewModel.restoreBackupFrom(
                        uri = it,
                        onSuccess = {
                            scope.launch {
                                confettiController.showEmpty()
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

        SettingItem.SavingFolder -> {
            SavingFolderSettingItemGroup(
                updateSaveFolderUri = viewModel::updateSaveFolderUri
            )
        }

        SettingItem.ScreenOrder -> {
            ScreenOrderSettingItem(
                updateOrder = viewModel::updateOrder
            )
        }

        SettingItem.ScreenSearch -> {
            ScreenSearchSettingItem(
                onClick = { viewModel.toggleScreenSearchEnabled() }
            )
        }

        SettingItem.SourceCode -> {
            SourceCodeSettingItem(
                shape = SettingsShapeDefaults.bottomShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }

        SettingItem.Telegram -> {
            TelegramSettingItem()
        }

        SettingItem.CheckUpdatesButton -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                EnhancedButton(
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
                    Text(stringResource(R.string.check_for_updates))
                }
            }
        }
    }
}