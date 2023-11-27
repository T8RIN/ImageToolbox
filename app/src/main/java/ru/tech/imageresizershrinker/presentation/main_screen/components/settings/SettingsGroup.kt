package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.ImageSearch
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.material.icons.rounded.PhotoSizeSelectSmall
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material.icons.rounded.ShieldMoon
import androidx.compose.material.icons.rounded.SystemSecurityUpdate
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.material.icons.rounded.TextFormat
import androidx.compose.material.icons.twotone.Palette
import androidx.compose.ui.graphics.vector.ImageVector
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.icons.material.FileSettings
import ru.tech.imageresizershrinker.presentation.root.icons.material.Firebase
import ru.tech.imageresizershrinker.presentation.root.icons.material.Shadow

sealed class SettingsGroup(
    val titleId: Int,
    val icon: ImageVector,
    val settingsList: List<Setting>,
    val initialState: Boolean
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
            Setting.Emoji
        ),
        initialState = true
    )

    data object SecondaryCustomization : SettingsGroup(
        icon = Icons.TwoTone.Palette,
        titleId = R.string.secondary_customization,
        settingsList = listOf(
            Setting.AllowImageMonet,
            Setting.EmojisCount,
            Setting.BorderThickness,
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
            Setting.ButtonShadows,
            Setting.FABShadows,
            Setting.SwitchShadows,
            Setting.SliderShadows
        ),
        initialState = false
    )

    data object Font : SettingsGroup(
        icon = Icons.Rounded.TextFormat,
        titleId = R.string.text,
        settingsList = listOf(
            Setting.ChangeLanguage,
            Setting.ChangeFont,
            Setting.FontScale
        ),
        initialState = false
    )

    data object OptionsArrangement : SettingsGroup(
        icon = Icons.Rounded.TableRows,
        titleId = R.string.options_arrangement,
        settingsList = listOf(
            Setting.ScreenOrder,
            Setting.ScreenSearch,
            Setting.GroupOptions
        ),
        initialState = false
    )

    data object Presets : SettingsGroup(
        icon = Icons.Rounded.PhotoSizeSelectSmall,
        titleId = R.string.presets,
        settingsList = listOf(
            Setting.Presets
        ),
        initialState = false
    )

    data object Draw : SettingsGroup(
        icon = Icons.Rounded.Draw,
        titleId = R.string.draw,
        settingsList = listOf(
            Setting.LockDrawOrientation
        ),
        initialState = false
    )

    data object Folder : SettingsGroup(
        icon = Icons.Rounded.Folder,
        titleId = R.string.folder,
        settingsList = listOf(
            Setting.SavingFolder
        ),
        initialState = false
    )

    data object Filename : SettingsGroup(
        icon = Icons.Rounded.FileSettings,
        titleId = R.string.filename,
        settingsList = listOf(
            Setting.FilenamePrefix,
            Setting.AddFileSize,
            Setting.AddOriginalFilename,
            Setting.ReplaceSequenceNumber,
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
            Setting.Telegram,
            Setting.SourceCode
        ),
        initialState = true
    )

    companion object {
        val entries: List<SettingsGroup> by lazy {
            listOf(
                ContactMe,
                PrimaryCustomization,
                SecondaryCustomization,
                NightMode,
                Shadows,
                Font,
                OptionsArrangement,
                Presets,
                Draw,
                Folder,
                Filename,
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