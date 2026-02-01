/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Info
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.domain.utils.Flavor
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ClipboardFile
import com.t8rin.imagetoolbox.core.resources.icons.Cool
import com.t8rin.imagetoolbox.core.resources.icons.Database
import com.t8rin.imagetoolbox.core.resources.icons.DesignServices
import com.t8rin.imagetoolbox.core.resources.icons.Draw
import com.t8rin.imagetoolbox.core.resources.icons.Exif
import com.t8rin.imagetoolbox.core.resources.icons.Firebase
import com.t8rin.imagetoolbox.core.resources.icons.FolderOpened
import com.t8rin.imagetoolbox.core.resources.icons.Glyphs
import com.t8rin.imagetoolbox.core.resources.icons.HardDrive
import com.t8rin.imagetoolbox.core.resources.icons.ImageSearch
import com.t8rin.imagetoolbox.core.resources.icons.LabelPercent
import com.t8rin.imagetoolbox.core.resources.icons.Mobile
import com.t8rin.imagetoolbox.core.resources.icons.MobileArrowDown
import com.t8rin.imagetoolbox.core.resources.icons.MobileCast
import com.t8rin.imagetoolbox.core.resources.icons.MobileLayout
import com.t8rin.imagetoolbox.core.resources.icons.MobileVibrate
import com.t8rin.imagetoolbox.core.resources.icons.Psychology
import com.t8rin.imagetoolbox.core.resources.icons.ResponsiveLayout
import com.t8rin.imagetoolbox.core.resources.icons.Routine
import com.t8rin.imagetoolbox.core.resources.icons.SquareFoot
import com.t8rin.imagetoolbox.core.resources.icons.Tonality

sealed class SettingsGroup(
    val id: Int,
    val titleId: Int,
    val icon: ImageVector,
    val settingsList: List<Setting>,
    val initialState: Boolean,
) {
    data object ContactMe : SettingsGroup(
        id = 0,
        icon = Icons.Rounded.MobileCast,
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
        icon = Icons.Rounded.DesignServices,
        titleId = R.string.customization,
        settingsList = listOf(
            Setting.ColorScheme,
            Setting.DynamicColors,
            Setting.AmoledMode,
            Setting.IconShape
        ),
        initialState = true
    )

    data object SecondaryCustomization : SettingsGroup(
        id = 2,
        icon = Icons.TwoTone.DesignServices,
        titleId = R.string.secondary_customization,
        settingsList = listOf(
            Setting.ColorBlindScheme,
            Setting.AllowImageMonet,
            Setting.BorderThickness,
            Setting.MainScreenTitle
        ),
        initialState = false
    )

    data object Layout : SettingsGroup(
        id = 3,
        icon = Icons.Rounded.MobileLayout,
        titleId = R.string.layout,
        settingsList = listOf(
            Setting.SwitchType,
            Setting.SliderType,
            Setting.ShapeType,
            Setting.CornersSize,
            Setting.UseCompactSelectors,
            Setting.DragHandleWidth,
            Setting.CenterAlignDialogButtons,
            Setting.FabAlignment
        ),
        initialState = false
    )

    data object NightMode : SettingsGroup(
        id = 4,
        icon = Icons.Rounded.Routine,
        titleId = R.string.night_mode,
        settingsList = listOf(
            Setting.NightMode
        ),
        initialState = false
    )

    data object Shadows : SettingsGroup(
        id = 5,
        icon = Icons.Rounded.Tonality,
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
        icon = Icons.Outlined.Glyphs,
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
        icon = Icons.Rounded.ResponsiveLayout,
        titleId = R.string.tools_arrangement,
        settingsList = listOf(
            Setting.ScreenOrder,
            Setting.ScreenSearch,
            Setting.EnableLauncherMode,
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
        icon = Icons.Rounded.SquareFoot,
        titleId = R.string.default_values,
        settingsList = listOf(
            Setting.DefaultScaleMode,
            Setting.DefaultColorSpace,
            Setting.DefaultImageFormat,
            Setting.DefaultQuality,
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
            Setting.FilenamePattern,
            Setting.AddFileSize,
            Setting.AddOriginalFilename,
            Setting.ReplaceSequenceNumber,
            Setting.AddTimestampToFilename,
            Setting.UseFormattedFilenameTimestamp,
            Setting.AddPresetToFilename,
            Setting.AddImageScaleModeToFilename,
            Setting.OverwriteFiles,
            Setting.ChecksumAsFilename,
            Setting.RandomizeFilename
        ),
        initialState = false
    )

    data object Cache : SettingsGroup(
        id = 14,
        icon = Icons.Rounded.Database,
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
        icon = Icons.Rounded.HardDrive,
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
        icon = Icons.Rounded.MobileArrowDown,
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
            Setting.TelegramGroup,
            Setting.TelegramChannel,
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
        icon = Icons.Rounded.MobileVibrate,
        titleId = R.string.vibration,
        settingsList = listOf(
            Setting.VibrationStrength
        ),
        initialState = false
    )

    data object Screen : SettingsGroup(
        id = 22,
        icon = Icons.Rounded.Mobile,
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
        icon = Icons.Rounded.Cool,
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
        icon = Icons.Rounded.Psychology,
        titleId = R.string.behavior,
        settingsList = listOf(
            Setting.SkipFilePicking,
            Setting.AllowSkipIfLarger,
            Setting.EnableToolExitConfirmation,
            Setting.ShowSettingsInLandscape,
            Setting.UseFullscreenSettings,
            Setting.FastSettingsSide,
            Setting.OpenEditInsteadOfPreview,
            Setting.SnowfallMode,
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
                !(it is Firebase && Flavor.isFoss())
            }
        }
    }
}