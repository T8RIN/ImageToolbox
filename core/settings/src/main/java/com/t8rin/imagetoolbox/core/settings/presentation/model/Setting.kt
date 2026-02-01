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

package com.t8rin.imagetoolbox.core.settings.presentation.model

import com.t8rin.imagetoolbox.core.resources.R

sealed class Setting(
    val title: Int,
    val subtitle: Int?,
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
        title = R.string.emojis_count,
        subtitle = null
    )

    data object Emoji : Setting(
        title = R.string.emoji,
        subtitle = R.string.emoji_sub
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
        title = R.string.group_tools_by_type,
        subtitle = R.string.group_tools_by_type_sub
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

    data object TelegramGroup : Setting(
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
        title = R.string.scale_mode,
        subtitle = null
    )

    data object DefaultColorSpace : Setting(
        title = R.string.tag_color_space,
        subtitle = null
    )

    data object SwitchType : Setting(
        title = R.string.switch_type,
        subtitle = null
    )

    data object Magnifier : Setting(
        title = R.string.magnifier,
        subtitle = R.string.magnifier_sub
    )

    data object ExifWidgetInitialState : Setting(
        title = R.string.force_exif_widget_initial_value,
        subtitle = R.string.force_exif_widget_initial_value_sub
    )

    data object BrightnessEnforcement : Setting(
        title = R.string.brightness_enforcement,
        subtitle = null
    )

    data object Confetti : Setting(
        title = R.string.confetti,
        subtitle = R.string.confetti_sub
    )

    data object SecureMode : Setting(
        title = R.string.secure_mode,
        subtitle = R.string.secure_mode_sub
    )

    data object UseRandomEmojis : Setting(
        title = R.string.random_emojis,
        subtitle = R.string.random_emojis_sub
    )

    data object IconShape : Setting(
        title = R.string.icon_shape,
        subtitle = R.string.icon_shape_sub
    )

    data object DragHandleWidth : Setting(
        title = R.string.drag_handle_width,
        subtitle = null
    )

    data object ConfettiType : Setting(
        title = R.string.confetti_type,
        subtitle = null
    )

    data object AllowAutoClipboardPaste : Setting(
        title = R.string.auto_paste,
        subtitle = R.string.auto_paste_sub
    )

    data object ConfettiHarmonizer : Setting(
        title = R.string.harmonization_color,
        subtitle = null
    )

    data object ConfettiHarmonizationLevel : Setting(
        title = R.string.harmonization_level,
        subtitle = null
    )

    data object SkipFilePicking : Setting(
        title = R.string.skip_file_picking,
        subtitle = R.string.skip_file_picking_sub
    )

    data object GeneratePreviews : Setting(
        title = R.string.generate_previews,
        subtitle = R.string.generate_previews_sub
    )

    data object ShowSettingsInLandscape : Setting(
        title = R.string.show_settings_in_landscape,
        subtitle = R.string.show_settings_in_landscape_sub
    )

    data object UseFullscreenSettings : Setting(
        title = R.string.fullscreen_settings,
        subtitle = R.string.fullscreen_settings_sub
    )

    data object DefaultDrawLineWidth : Setting(
        title = R.string.default_line_width,
        subtitle = null
    )

    data object OpenEditInsteadOfPreview : Setting(
        title = R.string.open_edit_instead_of_preview,
        subtitle = R.string.open_edit_instead_of_preview_sub
    )

    data object CanEnterPresetsByTextField : Setting(
        title = R.string.allow_enter_by_text_field,
        subtitle = R.string.allow_enter_by_text_field_sub
    )

    data object ColorBlindScheme : Setting(
        title = R.string.color_blind_scheme,
        subtitle = R.string.color_blind_scheme_sub
    )

    data object EnableLinksPreview : Setting(
        title = R.string.links_preview,
        subtitle = R.string.links_preview_sub
    )

    data object DefaultDrawColor : Setting(
        title = R.string.default_draw_color,
        subtitle = null
    )

    data object DefaultDrawPathMode : Setting(
        title = R.string.default_draw_path_mode,
        subtitle = null
    )

    data object AddTimestampToFilename : Setting(
        title = R.string.add_timestamp,
        subtitle = R.string.add_timestamp_sub
    )

    data object UseFormattedFilenameTimestamp : Setting(
        title = R.string.formatted_timestamp,
        subtitle = R.string.formatted_timestamp_sub
    )

    data object OneTimeSaveLocation : Setting(
        title = R.string.one_time_save_location,
        subtitle = R.string.one_time_save_location_sub
    )

    data object TelegramChannel : Setting(
        title = R.string.ci_channel,
        subtitle = R.string.ci_channel_sub
    )

    data object FreeSoftwarePartner : Setting(
        title = R.string.free_software_partner,
        subtitle = R.string.free_software_partner_sub
    )

    data object DefaultResizeType : Setting(
        title = R.string.resize_type,
        subtitle = null
    )

    data object SystemBarsVisibility : Setting(
        title = R.string.system_bars_visibility,
        subtitle = null
    )

    data object ShowSystemBarsBySwipe : Setting(
        title = R.string.show_system_bars_by_swipe,
        subtitle = R.string.show_system_bars_by_swipe_sub
    )

    data object UseCompactSelectors : Setting(
        title = R.string.compact_selectors,
        subtitle = R.string.compact_selectors_sub
    )

    data object MainScreenTitle : Setting(
        title = R.string.main_screen_title,
        subtitle = null
    )

    data object SliderType : Setting(
        title = R.string.slider_type,
        subtitle = null
    )

    data object CenterAlignDialogButtons : Setting(
        title = R.string.center_align_dialog_buttons,
        subtitle = R.string.center_align_dialog_buttons_sub
    )

    data object OpenSourceLicenses : Setting(
        title = R.string.open_source_licenses,
        subtitle = R.string.open_source_licenses_sub
    )

    data object FastSettingsSide : Setting(
        title = R.string.fast_settings_side,
        subtitle = R.string.fast_settings_side_sub
    )

    data object ChecksumAsFilename : Setting(
        title = R.string.checksum_as_filename,
        subtitle = R.string.checksum_as_filename_sub
    )

    data object EnableToolExitConfirmation : Setting(
        title = R.string.tool_exit_confirmation,
        subtitle = R.string.tool_exit_confirmation_sub
    )

    data object SendLogs : Setting(
        title = R.string.send_logs,
        subtitle = R.string.send_logs_sub
    )

    data object AddPresetToFilename : Setting(
        title = R.string.add_preset_to_filename,
        subtitle = R.string.add_preset_to_filename_sub
    )

    data object AddImageScaleModeToFilename : Setting(
        title = R.string.add_image_scale_mode_to_filename,
        subtitle = R.string.add_image_scale_mode_to_filename_sub
    )

    data object AllowSkipIfLarger : Setting(
        title = R.string.allow_skip_if_larger,
        subtitle = R.string.allow_skip_if_larger_sub
    )

    data object EnableLauncherMode : Setting(
        title = R.string.launcher_mode,
        subtitle = R.string.launcher_mode_sub
    )

    data object SnowfallMode : Setting(
        title = R.string.snowfall_mode,
        subtitle = null
    )

    data object DefaultImageFormat : Setting(
        title = R.string.image_format,
        subtitle = null
    )

    data object DefaultQuality : Setting(
        title = R.string.quality,
        subtitle = null
    )

    data object ShapeType : Setting(
        title = R.string.shapes_type,
        subtitle = null
    )

    data object CornersSize : Setting(
        title = R.string.corners_size,
        subtitle = null
    )

    data object FilenamePattern : Setting(
        title = R.string.filename_format,
        subtitle = null
    )

}