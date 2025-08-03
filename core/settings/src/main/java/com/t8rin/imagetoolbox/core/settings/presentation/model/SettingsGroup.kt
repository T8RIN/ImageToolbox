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

@file:Suppress("KotlinConstantConditions")

package com.t8rin.imagetoolbox.core.settings.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Architecture
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Face6
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material.icons.rounded.ShieldMoon
import androidx.compose.material.icons.rounded.SortByAlpha
import androidx.compose.material.icons.rounded.SystemSecurityUpdate
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.ViewCarousel
import androidx.compose.material.icons.twotone.Palette
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.resources.BuildConfig
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ClipboardFile
import com.t8rin.imagetoolbox.core.resources.icons.Draw
import com.t8rin.imagetoolbox.core.resources.icons.Exif
import com.t8rin.imagetoolbox.core.resources.icons.Firebase
import com.t8rin.imagetoolbox.core.resources.icons.FolderOpened
import com.t8rin.imagetoolbox.core.resources.icons.ImageSearch
import com.t8rin.imagetoolbox.core.resources.icons.LabelPercent
import com.t8rin.imagetoolbox.core.resources.icons.Mop
import com.t8rin.imagetoolbox.core.resources.icons.Shadow
import com.t8rin.imagetoolbox.core.resources.icons.Stacks

sealed class SettingsGroup(
    val id: Int,
    val titleId: Int,
    val icon: ImageVector,
    val settingsList: List<Setting>,
    val initialState: Boolean,
) {
    data object ContactMe : SettingsGroup(
        id = 0,
        icon = Icons.Rounded.PersonSearch,
        titleId = R.string.contact_me,
        settingsList = listOf(
            Setting.Author,
            Setting.SendLogs,
            Setting.Donate
        ),
        initialState = true
    )

    data object PrimaryCustomization : SettingsGroup(
        id = 1,
        icon = Icons.Rounded.Palette,
        titleId = R.string.customization,
        settingsList = listOf(
            Setting.ColorScheme,
            Setting.DynamicColors,
            Setting.AmoledMode,
            Setting.AllowImageMonet,
            Setting.IconShape
        ),
        initialState = true
    )

    data object SecondaryCustomization : SettingsGroup(
        id = 2,
        icon = Icons.TwoTone.Palette,
        titleId = R.string.secondary_customization,
        settingsList = listOf(
            Setting.ColorBlindScheme,
            Setting.BorderThickness,
            Setting.MainScreenTitle
        ),
        initialState = false
    )

    data object Layout : SettingsGroup(
        id = 3,
        icon = Icons.Rounded.ViewCarousel,
        titleId = R.string.layout,
        settingsList = listOf(
            Setting.SwitchType,
            Setting.SliderType,
            Setting.UseCompactSelectors,
            Setting.DragHandleWidth,
            Setting.CenterAlignDialogButtons,
            Setting.FabAlignment
        ),
        initialState = false
    )

    data object NightMode : SettingsGroup(
        id = 4,
        icon = Icons.Rounded.ShieldMoon,
        titleId = R.string.night_mode,
        settingsList = listOf(
            Setting.NightMode
        ),
        initialState = false
    )

    data object Shadows : SettingsGroup(
        id = 5,
        icon = Icons.Filled.Shadow,
        titleId = R.string.shadows,
        settingsList = listOf(
            Setting.ContainerShadows,
            Setting.AppBarShadows,
            Setting.ButtonShadows,
            Setting.FABShadows,
            Setting.SwitchShadows,
            Setting.SliderShadows
        ),
        initialState = false
    )

    data object Font : SettingsGroup(
        id = 6,
        icon = Icons.Rounded.SortByAlpha,
        titleId = R.string.text,
        settingsList = listOf(
            Setting.ChangeLanguage,
            Setting.ChangeFont,
            Setting.FontScale
        ),
        initialState = false
    )

    data object ToolsArrangement : SettingsGroup(
        id = 7,
        icon = Icons.Rounded.Stacks,
        titleId = R.string.tools_arrangement,
        settingsList = listOf(
            Setting.ScreenOrder,
            Setting.ScreenSearch,
            Setting.GroupOptions
        ),
        initialState = false
    )

    data object Presets : SettingsGroup(
        id = 8,
        icon = Icons.Rounded.LabelPercent,
        titleId = R.string.presets,
        settingsList = listOf(
            Setting.Presets,
            Setting.CanEnterPresetsByTextField
        ),
        initialState = false
    )

    data object DefaultValues : SettingsGroup(
        id = 9,
        icon = Icons.Outlined.Architecture,
        titleId = R.string.default_values,
        settingsList = listOf(
            Setting.DefaultScaleMode,
            Setting.DefaultColorSpace,
            Setting.DefaultResizeType
        ),
        initialState = false
    )

    data object Draw : SettingsGroup(
        id = 10,
        icon = Icons.Rounded.Draw,
        titleId = R.string.draw,
        settingsList = listOf(
            Setting.LockDrawOrientation,
            Setting.DefaultDrawLineWidth,
            Setting.DefaultDrawColor,
            Setting.DefaultDrawPathMode,
            Setting.Magnifier
        ),
        initialState = false
    )

    data object Exif : SettingsGroup(
        id = 11,
        icon = Icons.Rounded.Exif,
        titleId = R.string.exif,
        settingsList = listOf(
            Setting.ExifWidgetInitialState
        ),
        initialState = false
    )

    data object Folder : SettingsGroup(
        id = 12,
        icon = Icons.Rounded.FolderOpened,
        titleId = R.string.folder,
        settingsList = listOf(
            Setting.SavingFolder,
            Setting.OneTimeSaveLocation
        ),
        initialState = false
    )

    data object Filename : SettingsGroup(
        id = 13,
        icon = Icons.Rounded.Description,
        titleId = R.string.filename,
        settingsList = listOf(
            Setting.FilenamePrefix,
            Setting.FilenameSuffix,
            Setting.AddFileSize,
            Setting.AddOriginalFilename,
            Setting.ReplaceSequenceNumber,
            Setting.AddTimestampToFilename,
            Setting.UseFormattedFilenameTimestamp,
            Setting.OverwriteFiles,
            Setting.ChecksumAsFilename,
            Setting.RandomizeFilename
        ),
        initialState = false
    )

    data object Cache : SettingsGroup(
        id = 14,
        icon = Icons.Rounded.Mop,
        titleId = R.string.cache,
        settingsList = listOf(
            Setting.ClearCache,
            Setting.AutoCacheClear
        ),
        initialState = false
    )

    data object ImageSource : SettingsGroup(
        id = 15,
        icon = Icons.Rounded.ImageSearch,
        titleId = R.string.image_source,
        settingsList = listOf(
            Setting.ImagePickerMode
        ),
        initialState = false
    )

    data object BackupRestore : SettingsGroup(
        id = 16,
        icon = Icons.Rounded.SettingsBackupRestore,
        titleId = R.string.backup_and_restore,
        settingsList = listOf(
            Setting.Backup,
            Setting.Restore,
            Setting.Reset
        ),
        initialState = false
    )

    data object Firebase : SettingsGroup(
        id = 17,
        icon = Icons.Outlined.Firebase,
        titleId = R.string.firebase,
        settingsList = listOf(
            Setting.Crashlytics,
            Setting.Analytics
        ),
        initialState = false
    )

    data object Updates : SettingsGroup(
        id = 18,
        icon = Icons.Rounded.SystemSecurityUpdate,
        titleId = R.string.updates,
        settingsList = listOf(
            Setting.AutoCheckUpdates,
            Setting.AllowBetas,
            Setting.CheckUpdatesButton
        ),
        initialState = false
    )

    data object AboutApp : SettingsGroup(
        id = 19,
        icon = Icons.Rounded.Info,
        titleId = R.string.about_app,
        settingsList = listOf(
            Setting.CurrentVersionCode,
            Setting.OpenSourceLicenses,
            Setting.HelpTranslate,
            Setting.IssueTracker,
            Setting.FreeSoftwarePartner,
            Setting.TelegramChannel,
            Setting.TelegramGroup,
            Setting.SourceCode
        ),
        initialState = true
    )

    data object Clipboard : SettingsGroup(
        id = 20,
        icon = Icons.Rounded.ClipboardFile,
        titleId = R.string.clipboard,
        settingsList = listOf(
            Setting.AutoPinClipboard,
            Setting.AutoPinClipboardOnlyClip,
            Setting.AllowAutoClipboardPaste
        ),
        initialState = false
    )

    data object Haptics : SettingsGroup(
        id = 21,
        icon = Icons.Rounded.Vibration,
        titleId = R.string.vibration,
        settingsList = listOf(
            Setting.VibrationStrength
        ),
        initialState = false
    )

    data object Screen : SettingsGroup(
        id = 22,
        icon = Icons.Rounded.PhoneAndroid,
        titleId = R.string.screen,
        settingsList = listOf(
            Setting.BrightnessEnforcement,
            Setting.SecureMode,
            Setting.SystemBarsVisibility,
            Setting.ShowSystemBarsBySwipe
        ),
        initialState = false
    )

    data object Emoji : SettingsGroup(
        id = 23,
        icon = Icons.Rounded.Face6,
        titleId = R.string.emoji,
        settingsList = listOf(
            Setting.Emoji,
            Setting.EmojisCount,
            Setting.UseRandomEmojis
        ),
        initialState = false
    )

    data object Confetti : SettingsGroup(
        id = 24,
        icon = Icons.Rounded.Celebration,
        titleId = R.string.confetti,
        settingsList = listOf(
            Setting.Confetti,
            Setting.ConfettiHarmonizer,
            Setting.ConfettiHarmonizationLevel,
            Setting.ConfettiType
        ),
        initialState = false
    )

    data object Behavior : SettingsGroup(
        id = 25,
        icon = Icons.Rounded.Explore,
        titleId = R.string.behavior,
        settingsList = listOf(
            Setting.SkipFilePicking,
            Setting.EnableToolExitConfirmation,
            Setting.ShowSettingsInLandscape,
            Setting.UseFullscreenSettings,
            Setting.FastSettingsSide,
            Setting.OpenEditInsteadOfPreview,
            Setting.EnableLinksPreview,
            Setting.GeneratePreviews
        ),
        initialState = false
    )

    companion object {
        val entries: List<SettingsGroup> by lazy {
            listOf(
                ContactMe,
                PrimaryCustomization,
                SecondaryCustomization,
                NightMode,
                Layout,
                Emoji,
                Confetti,
                Shadows,
                Haptics,
                Screen,
                Font,
                Behavior,
                ToolsArrangement,
                Presets,
                DefaultValues,
                Draw,
                Exif,
                Folder,
                Filename,
                Clipboard,
                Cache,
                ImageSource,
                BackupRestore,
                Firebase,
                Updates,
                AboutApp
            ).filter {
                !(it is Firebase && BuildConfig.FLAVOR == "foss")
            }
        }
    }
}