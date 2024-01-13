package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import ru.tech.imageresizershrinker.core.resources.R

sealed class Setting(
    val title: Int,
    val subtitle: Int?
) {
    data object AddFileSize : Setting(
        title = R.string.add_file_size,
        subtitle = R.string.add_file_size_sub
    )

    data object AddOriginalFilename : Setting(
        title = R.string.add_original_filename,
        subtitle = R.string.add_original_filename_sub
    )

    data object AllowBetas : Setting(
        title = R.string.allow_betas,
        subtitle = R.string.allow_betas_sub
    )

    data object AllowImageMonet : Setting(
        title = R.string.allow_image_monet,
        subtitle = R.string.allow_image_monet_sub
    )

    data object AmoledMode : Setting(
        title = R.string.amoled_mode,
        subtitle = R.string.amoled_mode_sub
    )

    data object Analytics : Setting(
        title = R.string.analytics,
        subtitle = R.string.analytics_sub
    )

    data object Author : Setting(
        title = R.string.app_developer,
        subtitle = R.string.app_developer_nick
    )

    data object AutoCacheClear : Setting(
        title = R.string.auto_cache_clearing,
        subtitle = R.string.auto_cache_clearing_sub
    )

    data object AutoCheckUpdates : Setting(
        title = R.string.check_updates,
        subtitle = R.string.check_updates_sub
    )

    data object Backup : Setting(
        title = R.string.backup,
        subtitle = R.string.backup_sub
    )

    data object BorderThickness : Setting(
        title = R.string.border_thickness,
        subtitle = null
    )

    data object ChangeFont : Setting(
        title = R.string.font,
        subtitle = null
    )

    data object ChangeLanguage : Setting(
        title = R.string.language,
        subtitle = null
    )

    data object CheckUpdatesButton : Setting(
        title = R.string.check_updates,
        subtitle = R.string.check_updates_sub
    )

    data object ClearCache : Setting(
        title = R.string.cache,
        subtitle = R.string.cache_size
    )

    data object ColorScheme : Setting(
        title = R.string.color_scheme,
        subtitle = R.string.color_scheme
    )

    data object Crashlytics : Setting(
        title = R.string.crashlytics,
        subtitle = R.string.crashlytics_sub
    )

    data object CurrentVersionCode : Setting(
        title = R.string.app_name,
        subtitle = R.string.version
    )

    data object Donate : Setting(
        title = R.string.donation,
        subtitle = R.string.donation_sub
    )

    data object DynamicColors : Setting(
        title = R.string.dynamic_colors,
        subtitle = R.string.dynamic_colors_sub
    )

    data object EmojisCount : Setting(
        title = R.string.emoji,
        subtitle = R.string.emoji_sub
    )

    data object Emoji : Setting(
        title = R.string.emojis_count,
        subtitle = null
    )

    data object ContainerShadows : Setting(
        title = R.string.containers_shadow,
        subtitle = R.string.containers_shadow_sub
    )

    data object AppBarShadows : Setting(
        title = R.string.app_bars_shadow,
        subtitle = R.string.app_bars_shadow_sub
    )

    data object SliderShadows : Setting(
        title = R.string.sliders_shadow,
        subtitle = R.string.sliders_shadow_sub
    )

    data object SwitchShadows : Setting(
        title = R.string.switches_shadow,
        subtitle = R.string.switches_shadow_sub
    )

    data object FABShadows : Setting(
        title = R.string.fabs_shadow,
        subtitle = R.string.fabs_shadow_sub
    )

    data object ButtonShadows : Setting(
        title = R.string.buttons_shadow,
        subtitle = R.string.buttons_shadow_sub
    )

    data object FabAlignment : Setting(
        title = R.string.fab_alignment,
        subtitle = null
    )

    data object FilenamePrefix : Setting(
        title = R.string.prefix,
        subtitle = null
    )

    data object FontScale : Setting(
        title = R.string.font_scale,
        subtitle = null
    )

    data object GroupOptions : Setting(
        title = R.string.group_options_by_type,
        subtitle = R.string.group_options_by_type_sub
    )

    data object HelpTranslate : Setting(
        title = R.string.help_translate,
        subtitle = R.string.help_translate_sub
    )

    data object ImagePickerMode : Setting(
        title = R.string.photo_picker,
        subtitle = R.string.photo_picker_sub
    )

    data object IssueTracker : Setting(
        title = R.string.issue_tracker,
        subtitle = R.string.issue_tracker_sub
    )

    data object LockDrawOrientation : Setting(
        title = R.string.lock_draw_orientation,
        subtitle = R.string.lock_draw_orientation_sub
    )

    data object NightMode : Setting(
        title = R.string.night_mode,
        subtitle = null
    )

    data object Presets : Setting(
        title = R.string.presets,
        subtitle = R.string.presets_sub
    )

    data object RandomizeFilename : Setting(
        title = R.string.randomize_filename,
        subtitle = R.string.randomize_filename_sub
    )

    data object ReplaceSequenceNumber : Setting(
        title = R.string.replace_sequence_number,
        subtitle = R.string.replace_sequence_number_sub
    )

    data object Reset : Setting(
        title = R.string.reset,
        subtitle = R.string.reset_settings_sub
    )

    data object Restore : Setting(
        title = R.string.restore,
        subtitle = R.string.restore_sub
    )

    data object SavingFolder : Setting(
        title = R.string.folder,
        subtitle = null
    )

    data object ScreenOrder : Setting(
        title = R.string.order,
        subtitle = R.string.order_sub
    )

    data object ScreenSearch : Setting(
        title = R.string.search_option,
        subtitle = R.string.search_option_sub
    )

    data object SourceCode : Setting(
        title = R.string.check_source_code,
        subtitle = R.string.check_source_code_sub
    )

    data object Telegram : Setting(
        title = R.string.tg_chat,
        subtitle = R.string.tg_chat_sub
    )

    data object AutoPinClipboard : Setting(
        title = R.string.auto_pin,
        subtitle = R.string.auto_pin_sub
    )

    data object AutoPinClipboardOnlyClip : Setting(
        title = R.string.only_clip,
        subtitle = R.string.only_clip_sub
    )

    data object VibrationStrength : Setting(
        title = R.string.vibration_strength,
        subtitle = null
    )

    data object OverwriteFiles : Setting(
        title = R.string.overwrite_files,
        subtitle = R.string.overwrite_files_sub
    )

    data object FilenameSuffix : Setting(
        title = R.string.suffix,
        subtitle = null
    )

    data object DefaultScaleMode : Setting(
        title = R.string.default_value,
        subtitle = null
    )

    data object UsePixelSwitch : Setting(
        title = R.string.use_pixel_switch,
        subtitle = R.string.use_pixel_switch_sub
    )

    data object Magnifier : Setting(
        title = R.string.magnifier,
        subtitle = R.string.magnifier_sub
    )

    data object ExifWidgetInitialState : Setting(
        title = R.string.force_exif_widget_inital_value,
        subtitle = R.string.force_exif_widget_inital_value_sub
    )
}