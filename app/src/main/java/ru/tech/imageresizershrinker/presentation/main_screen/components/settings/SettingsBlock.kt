@file:Suppress("KotlinConstantConditions")

package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.FileDownloadOff
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.ImageSearch
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.material.icons.rounded.PhotoSizeSelectSmall
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material.icons.rounded.ShieldMoon
import androidx.compose.material.icons.rounded.SystemSecurityUpdate
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.material.icons.rounded.TextFormat
import androidx.compose.material.icons.twotone.Palette
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.presentation.root.icons.material.FileSettings
import ru.tech.imageresizershrinker.presentation.root.icons.material.Firebase
import ru.tech.imageresizershrinker.presentation.root.model.isFirstLaunch
import ru.tech.imageresizershrinker.presentation.root.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ContextUtils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.presentation.root.utils.helper.plus
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.other.showError
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalEditPresetsState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState


@Composable
fun SettingsBlock(
    viewModel: MainViewModel
) {
    val settingsState = LocalSettingsState.current
    val toastHostState = LocalToastHost.current
    val scope = rememberCoroutineScope()
    val confettiController = LocalConfettiController.current
    val context = LocalContext.current
    val layoutDirection = LocalLayoutDirection.current

    LazyColumn(
        contentPadding = WindowInsets.navigationBars
            .asPaddingValues()
            .plus(
                paddingValues = WindowInsets.displayCutout
                    .asPaddingValues()
                    .run {
                        PaddingValues(
                            bottom = calculateBottomPadding(),
                            end = calculateEndPadding(layoutDirection)
                        )
                    }
            )
    ) {
        item { Spacer(Modifier.height(8.dp)) }
        item {
            // Contact me
            SettingItem(
                icon = Icons.Rounded.PersonSearch,
                text = stringResource(R.string.contact_me),
                initialState = settingsState.isFirstLaunch()
            ) {
                AuthorSettingItem()
                Spacer(Modifier.height(4.dp))
                DonateSettingItem()
            }
        }
        item {
            // Primary Customization
            SettingItem(
                icon = Icons.Rounded.Palette,
                text = stringResource(R.string.customization),
                initialState = true
            ) {
                ColorSchemeSettingItem(
                    toggleInvertColors = viewModel::toggleInvertColors,
                    setThemeStyle = viewModel::setThemeStyle,
                    updateThemeContrast = viewModel::updateThemeContrast,
                    updateColorTuple = viewModel::updateColorTuple,
                    updateColorTuples = viewModel::updateColorTuples
                )
                Spacer(Modifier.height(4.dp))
                DynamicColorsSettingItem(
                    onClick = { viewModel.toggleDynamicColors() }
                )
                Spacer(Modifier.height(4.dp))
                AmoledModeSettingItem(
                    onClick = { viewModel.toggleAmoledMode() }
                )
                Spacer(Modifier.height(4.dp))
                EmojiSettingItem(
                    addColorTupleFromEmoji = viewModel::addColorTupleFromEmoji,
                    selectedEmojiIndex = viewModel.settingsState.selectedEmoji ?: 0,
                    updateEmoji = viewModel::updateEmoji
                )
            }
        }
        item {
            // Secondary Customization
            SettingItem(
                icon = Icons.TwoTone.Palette,
                text = stringResource(R.string.secondary_customization),
                initialState = false,
            ) {
                AllowImageMonetSettingItem(onClick = { viewModel.toggleAllowImageMonet() })
                Spacer(Modifier.height(4.dp))
                EmojisCountSettingItem(updateEmojisCount = viewModel::updateEmojisCount)
                Spacer(Modifier.height(4.dp))
                BorderThicknessSettingItem(updateBorderWidth = viewModel::setBorderWidth)
                AnimatedVisibility(visible = settingsState.borderWidth <= 0.dp) {
                    Spacer(Modifier.height(4.dp))
                    EnableShadowsSettingItem(onClick = { viewModel.toggleAllowShowingShadowsInsteadOfBorders() })
                }
                Spacer(Modifier.height(4.dp))
                FabAlignmentSettingItem(updateAlignment = viewModel::setAlignment)
            }
        }
        item {
            // Night mode
            SettingItem(
                icon = Icons.Rounded.ShieldMoon,
                text = stringResource(R.string.night_mode),
                initialState = false
            ) {
                NightModeSettingItemGroup(
                    value = viewModel.settingsState.nightMode,
                    onValueChange = viewModel::setNightMode
                )
            }
        }
        item {
            // Font
            SettingItem(
                icon = Icons.Rounded.TextFormat,
                text = stringResource(R.string.text)
            ) {
                ChangeLanguageSettingItem(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    shape = SettingsShapeDefaults.topShape
                )
                Spacer(Modifier.height(4.dp))
                ChangeFontSettingItem(
                    onFontSelected = { font ->
                        viewModel.setFont(font.asDomain())
                        (context as? Activity)?.recreate()
                    }
                )
                Spacer(Modifier.height(4.dp))
                FontScaleSettingItem(
                    onValueChange = viewModel::onUpdateFontScale
                )
            }
        }
        item {
            // Options Arrangement
            SettingItem(
                icon = Icons.Rounded.TableRows,
                text = stringResource(R.string.options_arrangement)
            ) {
                ScreenOrderSettingItem(
                    updateOrder = viewModel::updateOrder
                )
                Spacer(Modifier.height(4.dp))
                ScreenSearchSettingItem(
                    onClick = { viewModel.toggleScreenSearchEnabled() }
                )
                Spacer(Modifier.height(4.dp))
                GroupOptionsSettingItem(
                    onClick = { viewModel.toggleGroupOptionsByType() }
                )
            }
        }
        item {
            // Presets
            SettingItem(
                icon = Icons.Rounded.PhotoSizeSelectSmall,
                text = stringResource(R.string.presets),
            ) {
                val editPresetsState = LocalEditPresetsState.current
                PresetsSettingItem(
                    onClick = {
                        editPresetsState.value = true
                    }
                )
            }
        }
        item {
            SettingItem(
                icon = Icons.Rounded.Draw,
                text = stringResource(R.string.draw),
            ) {
                LockDrawOrientationSettingItem(
                    onClick = { viewModel.toggleLockDrawOrientation() }
                )
            }
        }
        item {
            // Folder
            SettingItem(
                icon = Icons.Rounded.Folder,
                text = stringResource(R.string.folder),
            ) {
                SavingFolderSettingItemGroup(
                    updateSaveFolderUri = viewModel::updateSaveFolderUri
                )
            }
        }
        item {
            // File
            SettingItem(
                icon = Icons.Rounded.FileSettings,
                text = stringResource(R.string.filename)
            ) {
                Box {
                    Column(
                        modifier = Modifier
                            .alpha(animateFloatAsState(if (!settingsState.randomizeFilename) 1f else 0.5f).value),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FilenamePrefixSettingItem(
                            updateFilenamePrefix = viewModel::updateFilename
                        )
                        AddFileSizeSettingItem(
                            onClick = { viewModel.toggleAddFileSize() }
                        )
                        AddOriginalFilenameSettingItem(
                            onClick = { viewModel.toggleAddOriginalFilename() }
                        )
                        ReplaceSequenceNumberSettingItem(
                            onClick = { viewModel.toggleAddSequenceNumber() }
                        )
                    }
                    if (settingsState.randomizeFilename) {
                        Surface(
                            modifier = Modifier.matchParentSize(),
                            color = Color.Transparent
                        ) {}
                    }
                }
                Spacer(Modifier.height(4.dp))
                RandomizeFilenameSettingItem(
                    onClick = { viewModel.toggleRandomizeFilename() }
                )
            }
        }
        item {
            // Cache
            SettingItem(
                icon = Icons.Rounded.Cached,
                text = stringResource(R.string.cache)
            ) {
                ClearCacheSettingItem(
                    value = viewModel.getReadableCacheSize(),
                    clearCache = viewModel::clearCache
                )
                Spacer(Modifier.height(4.dp))
                AutoCacheClearSettingItem(
                    onClick = { viewModel.toggleClearCacheOnLaunch() }
                )
            }
        }
        item {
            // Source
            SettingItem(
                icon = Icons.Rounded.ImageSearch,
                text = stringResource(R.string.image_source),
            ) {
                ImagePickerModeSettingItemGroup(
                    updateImagePickerMode = viewModel::updateImagePickerMode
                )
            }
        }
        item {
            // Backup and restore
            SettingItem(
                icon = Icons.Rounded.SettingsBackupRestore,
                text = stringResource(R.string.backup_and_restore),
                initialState = false
            ) {
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
                Spacer(Modifier.height(4.dp))
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
                Spacer(Modifier.height(4.dp))
                ResetSettingsSettingItem(
                    onReset = viewModel::resetSettings
                )
            }
        }
        if (BuildConfig.FLAVOR != "foss") {
            item {
                // Firebase
                SettingItem(
                    icon = Icons.Rounded.Firebase,
                    text = stringResource(R.string.firebase),
                    initialState = false
                ) {
                    CrashlyticsSettingItem(
                        onClick = { viewModel.toggleAllowCollectCrashlytics() }
                    )
                    Spacer(Modifier.height(4.dp))
                    AnalyticsSettingItem(
                        onClick = { viewModel.toggleAllowCollectAnalytics() }
                    )
                }
            }
        }
        item {
            // Updates
            SettingItem(
                icon = Icons.Rounded.SystemSecurityUpdate,
                text = stringResource(R.string.updates),
                initialState = false
            ) {
                AutoCheckUpdatesSettingItem(
                    onClick = { viewModel.toggleShowUpdateDialog() }
                )
                if (!context.isInstalledFromPlayStore()) {
                    Spacer(Modifier.height(4.dp))
                    AllowBetasSettingItem(
                        onClick = { viewModel.toggleAllowBetas(context.isInstalledFromPlayStore()) }
                    )
                }
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
        item {
            // About app
            SettingItem(
                icon = Icons.Rounded.Info,
                text = stringResource(R.string.about_app),
                initialState = true,
            ) {
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
                Spacer(Modifier.height(4.dp))
                HelpTranslateSettingItem()
                Spacer(Modifier.height(4.dp))
                IssueTrackerSettingItem()
                Spacer(Modifier.height(4.dp))
                TelegramSettingItem()
                Spacer(Modifier.height(4.dp))
                SourceCodeSettingItem(
                    shape = SettingsShapeDefaults.bottomShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}