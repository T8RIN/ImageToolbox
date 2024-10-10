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

package ru.tech.imageresizershrinker.core.settings.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Architecture
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Face6
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.IntegrationInstructions
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material.icons.rounded.ShieldMoon
import androidx.compose.material.icons.rounded.SortByAlpha
import androidx.compose.material.icons.rounded.SystemSecurityUpdate
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.twotone.Palette
import androidx.compose.ui.graphics.vector.ImageVector
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.Draw
import ru.tech.imageresizershrinker.core.resources.icons.Exif
import ru.tech.imageresizershrinker.core.resources.icons.Firebase
import ru.tech.imageresizershrinker.core.resources.icons.FolderOpened
import ru.tech.imageresizershrinker.core.resources.icons.ImageSearch
import ru.tech.imageresizershrinker.core.resources.icons.LabelPercent
import ru.tech.imageresizershrinker.core.resources.icons.Shadow
import ru.tech.imageresizershrinker.core.resources.icons.Stacks

sealed class SettingsGroup(
    val titleId: Int,
    val icon: ImageVector,
    val settingsList: List<Setting>,
    val initialState: Boolean,
) {
    data object ContactMe : SettingsGroup(
        icon = Icons.Rounded.PersonSearch,
        titleId = R.string.contact_me,
        settingsList = listOf(
            Setting.Author,
            Setting.Donate
        ),
        initialState = true
    )

    data object PrimaryCustomization : SettingsGroup(
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
        icon = Icons.TwoTone.Palette,
        titleId = R.string.secondary_customization,
        settingsList = listOf(
            Setting.ColorBlindScheme,
            Setting.BorderThickness,
            Setting.SwitchType,
            Setting.DragHandleWidth,
            Setting.UseCompactSelectors,
            Setting.FabAlignment
        ),
        initialState = false
    )

    data object NightMode : SettingsGroup(
        icon = Icons.Rounded.ShieldMoon,
        titleId = R.string.night_mode,
        settingsList = listOf(
            Setting.NightMode
        ),
        initialState = false
    )

    data object Shadows : SettingsGroup(
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
        icon = Icons.Rounded.LabelPercent,
        titleId = R.string.presets,
        settingsList = listOf(
            Setting.Presets,
            Setting.CanEnterPresetsByTextField
        ),
        initialState = false
    )

    data object DefaultValues : SettingsGroup(
        icon = Icons.Outlined.Architecture,
        titleId = R.string.default_values,
        settingsList = listOf(
            Setting.DefaultScaleMode,
            Setting.DefaultResizeType
        ),
        initialState = false
    )

    data object Draw : SettingsGroup(
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
        icon = Icons.Rounded.Exif,
        titleId = R.string.exif,
        settingsList = listOf(
            Setting.ExifWidgetInitialState
        ),
        initialState = false
    )

    data object Folder : SettingsGroup(
        icon = Icons.Rounded.FolderOpened,
        titleId = R.string.folder,
        settingsList = listOf(
            Setting.SavingFolder,
            Setting.OneTimeSaveLocation
        ),
        initialState = false
    )

    data object Filename : SettingsGroup(
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
            Setting.RandomizeFilename
        ),
        initialState = false
    )

    data object Cache : SettingsGroup(
        icon = Icons.Rounded.Cached,
        titleId = R.string.cache,
        settingsList = listOf(
            Setting.ClearCache,
            Setting.AutoCacheClear
        ),
        initialState = false
    )

    data object ImageSource : SettingsGroup(
        icon = Icons.Rounded.ImageSearch,
        titleId = R.string.image_source,
        settingsList = listOf(
            Setting.ImagePickerMode
        ),
        initialState = false
    )

    data object BackupRestore : SettingsGroup(
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
        icon = Icons.Rounded.Firebase,
        titleId = R.string.firebase,
        settingsList = listOf(
            Setting.Crashlytics,
            Setting.Analytics
        ),
        initialState = false
    )

    data object Updates : SettingsGroup(
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
        icon = Icons.Rounded.Info,
        titleId = R.string.about_app,
        settingsList = listOf(
            Setting.CurrentVersionCode,
            Setting.HelpTranslate,
            Setting.IssueTracker,
            Setting.TelegramChannel,
            Setting.TelegramGroup,
            Setting.SourceCode
        ),
        initialState = true
    )

    data object Clipboard : SettingsGroup(
        icon = Icons.Rounded.IntegrationInstructions,
        titleId = R.string.clipboard,
        settingsList = listOf(
            Setting.AutoPinClipboard,
            Setting.AutoPinClipboardOnlyClip,
            Setting.AllowAutoClipboardPaste
        ),
        initialState = false
    )

    data object Haptics : SettingsGroup(
        icon = Icons.Rounded.Vibration,
        titleId = R.string.vibration,
        settingsList = listOf(
            Setting.VibrationStrength
        ),
        initialState = false
    )

    data object Screen : SettingsGroup(
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
        icon = Icons.Rounded.Explore,
        titleId = R.string.behavior,
        settingsList = listOf(
            Setting.SkipFilePicking,
            Setting.ShowSettingsInLandscape,
            Setting.UseFullscreenSettings,
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
            )
        }
    }
}