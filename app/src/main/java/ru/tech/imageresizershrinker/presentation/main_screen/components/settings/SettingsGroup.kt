package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

import android.content.Context
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

sealed class SettingsGroup(
    val titleId: Int,
    val icon: ImageVector,
    val settingsList: List<SettingItem>,
    val initialState: Boolean
) {
    data object ContactMe : SettingsGroup(
        icon = Icons.Rounded.PersonSearch,
        titleId = R.string.contact_me,
        settingsList = listOf(
            SettingItem.Author,
            SettingItem.Donate
        ),
        initialState = true
    )

    data object PrimaryCustomization : SettingsGroup(
        icon = Icons.Rounded.Palette,
        titleId = R.string.customization,
        settingsList = listOf(
            SettingItem.ColorScheme,
            SettingItem.DynamicColors,
            SettingItem.AmoledMode,
            SettingItem.Emoji
        ),
        initialState = true
    )

    data object SecondaryCustomization : SettingsGroup(
        icon = Icons.TwoTone.Palette,
        titleId = R.string.secondary_customization,
        settingsList = listOf(
            SettingItem.AllowImageMonet,
            SettingItem.EmojisCount,
            SettingItem.BorderThickness,
            SettingItem.EnableShadows,
            SettingItem.FabAlignment
        ),
        initialState = false
    )

    data object NightMode : SettingsGroup(
        icon = Icons.Rounded.ShieldMoon,
        titleId = R.string.night_mode,
        settingsList = listOf(
            SettingItem.NightMode
        ),
        initialState = false
    )

    data object Font : SettingsGroup(
        icon = Icons.Rounded.TextFormat,
        titleId = R.string.text,
        settingsList = listOf(
            SettingItem.ChangeLanguage,
            SettingItem.ChangeFont,
            SettingItem.FontScale
        ),
        initialState = false
    )

    data object OptionsArrangement : SettingsGroup(
        icon = Icons.Rounded.TableRows,
        titleId = R.string.options_arrangement,
        settingsList = listOf(
            SettingItem.ScreenOrder,
            SettingItem.ScreenSearch,
            SettingItem.GroupOptions
        ),
        initialState = false
    )

    data object Presets : SettingsGroup(
        icon = Icons.Rounded.PhotoSizeSelectSmall,
        titleId = R.string.presets,
        settingsList = listOf(
            SettingItem.Presets
        ),
        initialState = false
    )

    data object Draw : SettingsGroup(
        icon = Icons.Rounded.Draw,
        titleId = R.string.draw,
        settingsList = listOf(
            SettingItem.LockDrawOrientation
        ),
        initialState = false
    )

    data object Folder : SettingsGroup(
        icon = Icons.Rounded.Folder,
        titleId = R.string.folder,
        settingsList = listOf(
            SettingItem.SavingFolder
        ),
        initialState = false
    )

    data object Filename : SettingsGroup(
        icon = Icons.Rounded.FileSettings,
        titleId = R.string.filename,
        settingsList = listOf(
            SettingItem.FilenamePrefix,
            SettingItem.AddFileSize,
            SettingItem.AddOriginalFilename,
            SettingItem.ReplaceSequenceNumber,
            SettingItem.RandomizeFilename
        ),
        initialState = false
    )

    data object Cache : SettingsGroup(
        icon = Icons.Rounded.Cached,
        titleId = R.string.cache,
        settingsList = listOf(
            SettingItem.ClearCache,
            SettingItem.AutoCacheClear
        ),
        initialState = false
    )

    data object ImageSource : SettingsGroup(
        icon = Icons.Rounded.ImageSearch,
        titleId = R.string.image_source,
        settingsList = listOf(
            SettingItem.ImagePickerMode
        ),
        initialState = false
    )

    data object BackupRestore : SettingsGroup(
        icon = Icons.Rounded.SettingsBackupRestore,
        titleId = R.string.backup_and_restore,
        settingsList = listOf(
            SettingItem.Backup,
            SettingItem.Restore,
            SettingItem.Reset
        ),
        initialState = false
    )

    data object Firebase : SettingsGroup(
        icon = Icons.Rounded.Firebase,
        titleId = R.string.firebase,
        settingsList = listOf(
            SettingItem.Crashlytics,
            SettingItem.Analytics
        ),
        initialState = false
    )

    data object Updates : SettingsGroup(
        icon = Icons.Rounded.SystemSecurityUpdate,
        titleId = R.string.updates,
        settingsList = listOf(
            SettingItem.AutoCheckUpdates,
            SettingItem.AllowBetas,
            SettingItem.CheckUpdatesButton
        ),
        initialState = false
    )

    data object AboutApp : SettingsGroup(
        icon = Icons.Rounded.Info,
        titleId = R.string.about_app,
        settingsList = listOf(
            SettingItem.CurrentVersionCode,
            SettingItem.HelpTranslate,
            SettingItem.IssueTracker,
            SettingItem.Telegram,
            SettingItem.SourceCode
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

sealed interface SettingItem {
    data object AddFileSize : SettingItem
    data object AddOriginalFilename : SettingItem
    data object AllowBetas : SettingItem
    data object AllowImageMonet : SettingItem
    data object AmoledMode : SettingItem
    data object Analytics : SettingItem
    data object Author : SettingItem
    data object AutoCacheClear : SettingItem
    data object AutoCheckUpdates : SettingItem
    data object Backup : SettingItem
    data object BorderThickness : SettingItem
    data object ChangeFont : SettingItem
    data object ChangeLanguage : SettingItem
    data object CheckUpdatesButton : SettingItem
    data object ClearCache : SettingItem
    data object ColorScheme : SettingItem
    data object Crashlytics : SettingItem
    data object CurrentVersionCode : SettingItem
    data object Donate : SettingItem
    data object DynamicColors : SettingItem
    data object EmojisCount : SettingItem
    data object Emoji : SettingItem
    data object EnableShadows : SettingItem
    data object FabAlignment : SettingItem
    data object FilenamePrefix : SettingItem
    data object FontScale : SettingItem
    data object GroupOptions : SettingItem
    data object HelpTranslate : SettingItem
    data object ImagePickerMode : SettingItem
    data object IssueTracker : SettingItem
    data object LockDrawOrientation : SettingItem
    data object NightMode : SettingItem
    data object Presets : SettingItem
    data object RandomizeFilename : SettingItem
    data object ReplaceSequenceNumber : SettingItem
    data object Reset : SettingItem
    data object Restore : SettingItem
    data object SavingFolder : SettingItem
    data object ScreenOrder : SettingItem
    data object ScreenSearch : SettingItem
    data object SourceCode : SettingItem
    data object Telegram : SettingItem
}

fun SettingItem.getSubtitle(context: Context): String {
    return context.getString(
        when (this) {
            SettingItem.AddFileSize -> R.string.add_file_size
            SettingItem.AddOriginalFilename -> R.string.add_original_filename
            SettingItem.AllowBetas -> R.string.allow_betas
            SettingItem.AllowImageMonet -> R.string.allow_image_monet
            SettingItem.AmoledMode -> R.string.amoled_mode
            SettingItem.Analytics -> R.string.analytics
            SettingItem.Author -> R.string.app_developer
            SettingItem.AutoCacheClear -> R.string.auto_cache_clearing
            SettingItem.AutoCheckUpdates -> R.string.check_updates
            SettingItem.Backup -> R.string.backup
            SettingItem.BorderThickness -> R.string.border_thickness
            SettingItem.ChangeFont -> R.string.font
            SettingItem.ChangeLanguage -> R.string.language
            SettingItem.CheckUpdatesButton -> R.string.check_updates
            SettingItem.ClearCache -> R.string.cache
            SettingItem.ColorScheme -> R.string.color_scheme
            SettingItem.Crashlytics -> R.string.crashlytics
            SettingItem.CurrentVersionCode -> R.string.app_name
            SettingItem.Donate -> R.string.donation
            SettingItem.DynamicColors -> R.string.dynamic_colors
            SettingItem.Emoji -> R.string.emoji
            SettingItem.EmojisCount -> R.string.emojis_count
            SettingItem.EnableShadows -> R.string.shadows
            SettingItem.FabAlignment -> R.string.fab_alignment
            SettingItem.FilenamePrefix -> R.string.prefix
            SettingItem.FontScale -> R.string.font_scale
            SettingItem.GroupOptions -> R.string.group_options_by_type
            SettingItem.HelpTranslate -> R.string.help_translate
            SettingItem.ImagePickerMode -> R.string.photo_picker
            SettingItem.IssueTracker -> R.string.issue_tracker
            SettingItem.LockDrawOrientation -> R.string.lock_draw_orientation
            SettingItem.NightMode -> R.string.night_mode
            SettingItem.Presets -> R.string.presets
            SettingItem.RandomizeFilename -> R.string.randomize_filename
            SettingItem.ReplaceSequenceNumber -> R.string.replace_sequence_number
            SettingItem.Reset -> R.string.reset
            SettingItem.Restore -> R.string.restore
            SettingItem.SavingFolder -> R.string.folder
            SettingItem.ScreenOrder -> R.string.order
            SettingItem.ScreenSearch -> R.string.search_option
            SettingItem.SourceCode -> R.string.check_source_code
            SettingItem.Telegram -> R.string.telegram
        }
    )
}

fun SettingItem.getTitle(context: Context): String {
    return context.getString(
        when (this) {
            SettingItem.AddFileSize -> R.string.add_file_size_sub
            SettingItem.AddOriginalFilename -> R.string.add_original_filename_sub
            SettingItem.AllowBetas -> R.string.allow_betas_sub
            SettingItem.AllowImageMonet -> R.string.allow_image_monet_sub
            SettingItem.AmoledMode -> R.string.amoled_mode_sub
            SettingItem.Analytics -> R.string.analytics_sub
            SettingItem.Author -> R.string.app_developer
            SettingItem.AutoCacheClear -> R.string.auto_cache_clearing_sub
            SettingItem.AutoCheckUpdates -> R.string.check_updates_sub
            SettingItem.Backup -> R.string.backup_sub
            SettingItem.BorderThickness -> R.string.border_thickness
            SettingItem.ChangeFont -> R.string.font
            SettingItem.ChangeLanguage -> R.string.language
            SettingItem.CheckUpdatesButton -> R.string.check_updates_sub
            SettingItem.ClearCache -> R.string.cache_size
            SettingItem.ColorScheme -> R.string.color_scheme
            SettingItem.Crashlytics -> R.string.crashlytics_sub
            SettingItem.CurrentVersionCode -> R.string.app_name
            SettingItem.Donate -> R.string.donation_sub
            SettingItem.DynamicColors -> R.string.dynamic_colors_sub
            SettingItem.Emoji -> R.string.emoji_sub
            SettingItem.EmojisCount -> R.string.emojis_count
            SettingItem.EnableShadows -> R.string.shadows_setting_sub
            SettingItem.FabAlignment -> R.string.fab_alignment
            SettingItem.FilenamePrefix -> R.string.default_prefix
            SettingItem.FontScale -> R.string.font_scale
            SettingItem.GroupOptions -> R.string.group_options_by_type_sub
            SettingItem.HelpTranslate -> R.string.help_translate_sub
            SettingItem.ImagePickerMode -> R.string.photo_picker_sub
            SettingItem.IssueTracker -> R.string.issue_tracker_sub
            SettingItem.LockDrawOrientation -> R.string.lock_draw_orientation_sub
            SettingItem.NightMode -> R.string.night_mode
            SettingItem.Presets -> R.string.presets_sub
            SettingItem.RandomizeFilename -> R.string.randomize_filename_sub
            SettingItem.ReplaceSequenceNumber -> R.string.replace_sequence_number_sub
            SettingItem.Reset -> R.string.reset_settings_sub
            SettingItem.Restore -> R.string.restore_sub
            SettingItem.SavingFolder -> R.string.folder
            SettingItem.ScreenOrder -> R.string.order_sub
            SettingItem.ScreenSearch -> R.string.search_option_sub
            SettingItem.SourceCode -> R.string.check_source_code_sub
            SettingItem.Telegram -> R.string.app_developer_nick
        }
    )
}