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
import kotlinx.serialization.Serializable

@Serializable
sealed class Setting(
    val title: Int,
    val subtitle: Int?,
) {
    @Serializable
    data object AddFileSize : Setting(
        title = R.string.add_file_size,
        subtitle = R.string.add_file_size_sub
    )

    @Serializable
    data object AddOriginalFilename : Setting(
        title = R.string.add_original_filename,
        subtitle = R.string.add_original_filename_sub
    )

    @Serializable
    data object AllowBetas : Setting(
        title = R.string.allow_betas,
        subtitle = R.string.allow_betas_sub
    )

    @Serializable
    data object AllowImageMonet : Setting(
        title = R.string.allow_image_monet,
        subtitle = R.string.allow_image_monet_sub
    )

    @Serializable
    data object AmoledMode : Setting(
        title = R.string.amoled_mode,
        subtitle = R.string.amoled_mode_sub
    )

    @Serializable
    data object Analytics : Setting(
        title = R.string.analytics,
        subtitle = R.string.analytics_sub
    )

    @Serializable
    data object Author : Setting(
        title = R.string.app_developer,
        subtitle = R.string.app_developer_nick
    )

    @Serializable
    data object AutoCacheClear : Setting(
        title = R.string.auto_cache_clearing,
        subtitle = R.string.auto_cache_clearing_sub
    )

    @Serializable
    data object AutoCheckUpdates : Setting(
        title = R.string.check_updates,
        subtitle = R.string.check_updates_sub
    )

    @Serializable
    data object Backup : Setting(
        title = R.string.backup,
        subtitle = R.string.backup_sub
    )

    @Serializable
    data object BorderThickness : Setting(
        title = R.string.border_thickness,
        subtitle = null
    )

    @Serializable
    data object ChangeFont : Setting(
        title = R.string.font,
        subtitle = null
    )

    @Serializable
    data object ChangeLanguage : Setting(
        title = R.string.language,
        subtitle = null
    )

    @Serializable
    data object CheckUpdatesButton : Setting(
        title = R.string.check_updates,
        subtitle = R.string.check_updates_sub
    )

    @Serializable
    data object ClearCache : Setting(
        title = R.string.cache,
        subtitle = R.string.cache_size
    )

    @Serializable
    data object ColorScheme : Setting(
        title = R.string.color_scheme,
        subtitle = R.string.color_scheme
    )

    @Serializable
    data object Crashlytics : Setting(
        title = R.string.crashlytics,
        subtitle = R.string.crashlytics_sub
    )

    @Serializable
    data object CurrentVersionCode : Setting(
        title = R.string.app_name,
        subtitle = R.string.version
    )

    @Serializable
    data object Donate : Setting(
        title = R.string.donation,
        subtitle = R.string.donation_sub
    )

    @Serializable
    data object DynamicColors : Setting(
        title = R.string.dynamic_colors,
        subtitle = R.string.dynamic_colors_sub
    )

    @Serializable
    data object EmojisCount : Setting(
        title = R.string.emojis_count,
        subtitle = null
    )

    @Serializable
    data object Emoji : Setting(
        title = R.string.emoji,
        subtitle = R.string.emoji_sub
    )

    @Serializable
    data object ContainerShadows : Setting(
        title = R.string.containers_shadow,
        subtitle = R.string.containers_shadow_sub
    )

    @Serializable
    data object AppBarShadows : Setting(
        title = R.string.app_bars_shadow,
        subtitle = R.string.app_bars_shadow_sub
    )

    @Serializable
    data object SliderShadows : Setting(
        title = R.string.sliders_shadow,
        subtitle = R.string.sliders_shadow_sub
    )

    @Serializable
    data object SwitchShadows : Setting(
        title = R.string.switches_shadow,
        subtitle = R.string.switches_shadow_sub
    )

    @Serializable
    data object FABShadows : Setting(
        title = R.string.fabs_shadow,
        subtitle = R.string.fabs_shadow_sub
    )

    @Serializable
    data object ButtonShadows : Setting(
        title = R.string.buttons_shadow,
        subtitle = R.string.buttons_shadow_sub
    )

    @Serializable
    data object FabAlignment : Setting(
        title = R.string.fab_alignment,
        subtitle = null
    )

    @Serializable
    data object FilenamePrefix : Setting(
        title = R.string.prefix,
        subtitle = null
    )

    @Serializable
    data object FontScale : Setting(
        title = R.string.font_scale,
        subtitle = null
    )

    @Serializable
    data object GroupOptions : Setting(
        title = R.string.group_tools_by_type,
        subtitle = R.string.group_tools_by_type_sub
    )

    @Serializable
    data object FavoriteToolsInGroupedMode : Setting(
        title = R.string.favorite_tools_in_grouped_mode,
        subtitle = R.string.favorite_tools_in_grouped_mode_sub
    )

    @Serializable
    data object ShowFavoriteAsLast : Setting(
        title = R.string.show_favorite_as_last,
        subtitle = R.string.show_favorite_as_last_sub
    )

    @Serializable
    data object HelpTranslate : Setting(
        title = R.string.help_translate,
        subtitle = R.string.help_translate_sub
    )

    @Serializable
    data object HelpTips : Setting(
        title = R.string.help_tips,
        subtitle = R.string.help_tips_settings_sub
    )

    @Serializable
    data object ImagePickerMode : Setting(
        title = R.string.photo_picker,
        subtitle = R.string.photo_picker_sub
    )

    @Serializable
    data object IssueTracker : Setting(
        title = R.string.issue_tracker,
        subtitle = R.string.issue_tracker_sub
    )

    @Serializable
    data object LockDrawOrientation : Setting(
        title = R.string.lock_draw_orientation,
        subtitle = R.string.lock_draw_orientation_sub
    )

    @Serializable
    data object NightMode : Setting(
        title = R.string.night_mode,
        subtitle = null
    )

    @Serializable
    data object Presets : Setting(
        title = R.string.presets,
        subtitle = R.string.presets_sub
    )

    @Serializable
    data object RandomizeFilename : Setting(
        title = R.string.randomize_filename,
        subtitle = R.string.randomize_filename_sub
    )

    @Serializable
    data object ReplaceSequenceNumber : Setting(
        title = R.string.replace_sequence_number,
        subtitle = R.string.replace_sequence_number_sub
    )

    @Serializable
    data object Reset : Setting(
        title = R.string.reset,
        subtitle = R.string.reset_settings_sub
    )

    @Serializable
    data object Restore : Setting(
        title = R.string.restore,
        subtitle = R.string.restore_sub
    )

    @Serializable
    data object SavingFolder : Setting(
        title = R.string.folder,
        subtitle = null
    )

    @Serializable
    data object SaveToOriginalFolder : Setting(
        title = R.string.save_to_original_folder,
        subtitle = R.string.save_to_original_folder_sub
    )

    @Serializable
    data object ScreenOrder : Setting(
        title = R.string.order,
        subtitle = R.string.order_sub
    )

    @Serializable
    data object ScreenSearch : Setting(
        title = R.string.search_option,
        subtitle = R.string.search_option_sub
    )

    @Serializable
    data object SourceCode : Setting(
        title = R.string.check_source_code,
        subtitle = R.string.check_source_code_sub
    )

    @Serializable
    data object TelegramGroup : Setting(
        title = R.string.tg_chat,
        subtitle = R.string.tg_chat_sub
    )

    @Serializable
    data object AutoPinClipboard : Setting(
        title = R.string.auto_pin,
        subtitle = R.string.auto_pin_sub
    )

    @Serializable
    data object AutoPinClipboardOnlyClip : Setting(
        title = R.string.only_clip,
        subtitle = R.string.only_clip_sub
    )

    @Serializable
    data object VibrationStrength : Setting(
        title = R.string.vibration_strength,
        subtitle = null
    )

    @Serializable
    data object OverwriteFiles : Setting(
        title = R.string.overwrite_files,
        subtitle = R.string.overwrite_files_sub
    )

    @Serializable
    data object FilenameSuffix : Setting(
        title = R.string.suffix,
        subtitle = null
    )

    @Serializable
    data object DefaultScaleMode : Setting(
        title = R.string.scale_mode,
        subtitle = null
    )

    @Serializable
    data object DefaultColorSpace : Setting(
        title = R.string.tag_color_space,
        subtitle = null
    )

    @Serializable
    data object SwitchType : Setting(
        title = R.string.switch_type,
        subtitle = null
    )

    @Serializable
    data object Magnifier : Setting(
        title = R.string.magnifier,
        subtitle = R.string.magnifier_sub
    )

    @Serializable
    data object DrawBitmapBorder : Setting(
        title = R.string.draw_bitmap_border,
        subtitle = R.string.draw_bitmap_border_sub
    )

    @Serializable
    data object ExifWidgetInitialState : Setting(
        title = R.string.force_exif_widget_initial_value,
        subtitle = R.string.force_exif_widget_initial_value_sub
    )

    @Serializable
    data object BrightnessEnforcement : Setting(
        title = R.string.brightness_enforcement,
        subtitle = null
    )

    @Serializable
    data object Confetti : Setting(
        title = R.string.confetti,
        subtitle = R.string.confetti_sub
    )

    @Serializable
    data object SecureMode : Setting(
        title = R.string.secure_mode,
        subtitle = R.string.secure_mode_sub
    )

    @Serializable
    data object UseRandomEmojis : Setting(
        title = R.string.random_emojis,
        subtitle = R.string.random_emojis_sub
    )

    @Serializable
    data object UseAnimatedEmojis : Setting(
        title = R.string.animated_emojis,
        subtitle = R.string.animated_emojis_sub
    )

    @Serializable
    data object IconShape : Setting(
        title = R.string.icon_shape,
        subtitle = R.string.icon_shape_sub
    )

    @Serializable
    data object DragHandleWidth : Setting(
        title = R.string.drag_handle_width,
        subtitle = null
    )

    @Serializable
    data object ShapeByInteractionThrottle : Setting(
        title = R.string.shape_by_interaction_throttle,
        subtitle = null
    )

    @Serializable
    data object ConfettiType : Setting(
        title = R.string.confetti_type,
        subtitle = null
    )

    @Serializable
    data object AllowAutoClipboardPaste : Setting(
        title = R.string.auto_paste,
        subtitle = R.string.auto_paste_sub
    )

    @Serializable
    data object ConfettiHarmonizer : Setting(
        title = R.string.harmonization_color,
        subtitle = null
    )

    @Serializable
    data object ConfettiHarmonizationLevel : Setting(
        title = R.string.harmonization_level,
        subtitle = null
    )

    @Serializable
    data object SkipFilePicking : Setting(
        title = R.string.skip_file_picking,
        subtitle = R.string.skip_file_picking_sub
    )

    @Serializable
    data object GeneratePreviews : Setting(
        title = R.string.generate_previews,
        subtitle = R.string.generate_previews_sub
    )

    @Serializable
    data object ShowSettingsInLandscape : Setting(
        title = R.string.show_settings_in_landscape,
        subtitle = R.string.show_settings_in_landscape_sub
    )

    @Serializable
    data object UseFullscreenSettings : Setting(
        title = R.string.fullscreen_settings,
        subtitle = R.string.fullscreen_settings_sub
    )

    @Serializable
    data object DefaultDrawLineWidth : Setting(
        title = R.string.default_line_width,
        subtitle = null
    )

    @Serializable
    data object OpenEditInsteadOfPreview : Setting(
        title = R.string.open_edit_instead_of_preview,
        subtitle = R.string.open_edit_instead_of_preview_sub
    )

    @Serializable
    data object CanEnterPresetsByTextField : Setting(
        title = R.string.allow_enter_by_text_field,
        subtitle = R.string.allow_enter_by_text_field_sub
    )

    @Serializable
    data object ColorBlindScheme : Setting(
        title = R.string.color_blind_scheme,
        subtitle = R.string.color_blind_scheme_sub
    )

    @Serializable
    data object EnableLinksPreview : Setting(
        title = R.string.links_preview,
        subtitle = R.string.links_preview_sub
    )

    @Serializable
    data object DefaultDrawColor : Setting(
        title = R.string.default_draw_color,
        subtitle = null
    )

    @Serializable
    data object DefaultDrawPathMode : Setting(
        title = R.string.default_draw_path_mode,
        subtitle = null
    )

    @Serializable
    data object AddTimestampToFilename : Setting(
        title = R.string.add_timestamp,
        subtitle = R.string.add_timestamp_sub
    )

    @Serializable
    data object UseFormattedFilenameTimestamp : Setting(
        title = R.string.formatted_timestamp,
        subtitle = R.string.formatted_timestamp_sub
    )

    @Serializable
    data object OneTimeSaveLocation : Setting(
        title = R.string.one_time_save_location,
        subtitle = R.string.one_time_save_location_sub
    )

    @Serializable
    data object TelegramChannel : Setting(
        title = R.string.ci_channel,
        subtitle = R.string.ci_channel_sub
    )

    @Serializable
    data object FreeSoftwarePartner : Setting(
        title = R.string.free_software_partner,
        subtitle = R.string.free_software_partner_sub
    )

    @Serializable
    data object DefaultResizeType : Setting(
        title = R.string.resize_type,
        subtitle = null
    )

    @Serializable
    data object SystemBarsVisibility : Setting(
        title = R.string.system_bars_visibility,
        subtitle = null
    )

    @Serializable
    data object ShowSystemBarsBySwipe : Setting(
        title = R.string.show_system_bars_by_swipe,
        subtitle = R.string.show_system_bars_by_swipe_sub
    )

    @Serializable
    data object UseCompactSelectors : Setting(
        title = R.string.compact_selectors,
        subtitle = R.string.compact_selectors_sub
    )

    @Serializable
    data object MainScreenTitle : Setting(
        title = R.string.main_screen_title,
        subtitle = null
    )

    @Serializable
    data object SliderType : Setting(
        title = R.string.slider_type,
        subtitle = null
    )

    @Serializable
    data object CenterAlignDialogButtons : Setting(
        title = R.string.center_align_dialog_buttons,
        subtitle = R.string.center_align_dialog_buttons_sub
    )

    @Serializable
    data object OpenSourceLicenses : Setting(
        title = R.string.open_source_licenses,
        subtitle = R.string.open_source_licenses_sub
    )

    @Serializable
    data object FastSettingsSide : Setting(
        title = R.string.fast_settings_side,
        subtitle = R.string.fast_settings_side_sub
    )

    @Serializable
    data object ChecksumAsFilename : Setting(
        title = R.string.checksum_as_filename,
        subtitle = R.string.checksum_as_filename_sub
    )

    @Serializable
    data object EnableToolExitConfirmation : Setting(
        title = R.string.tool_exit_confirmation,
        subtitle = R.string.tool_exit_confirmation_sub
    )

    @Serializable
    data object SendLogs : Setting(
        title = R.string.send_logs,
        subtitle = R.string.send_logs_sub
    )

    @Serializable
    data object AppLogs : Setting(
        title = R.string.app_logs,
        subtitle = R.string.app_logs_sub
    )

    @Serializable
    data object AppUsageStatistics : Setting(
        title = R.string.usage_statistics,
        subtitle = R.string.usage_statistics_sub
    )

    @Serializable
    data object AddPresetToFilename : Setting(
        title = R.string.add_preset_to_filename,
        subtitle = R.string.add_preset_to_filename_sub
    )

    @Serializable
    data object AddImageScaleModeToFilename : Setting(
        title = R.string.add_image_scale_mode_to_filename,
        subtitle = R.string.add_image_scale_mode_to_filename_sub
    )

    @Serializable
    data object AllowSkipIfLarger : Setting(
        title = R.string.allow_skip_if_larger,
        subtitle = R.string.allow_skip_if_larger_sub
    )

    @Serializable
    data object EnableLauncherMode : Setting(
        title = R.string.launcher_mode,
        subtitle = R.string.launcher_mode_sub
    )

    @Serializable
    data object SnowfallMode : Setting(
        title = R.string.snowfall_mode,
        subtitle = null
    )

    @Serializable
    data object DefaultImageFormat : Setting(
        title = R.string.image_format,
        subtitle = null
    )

    @Serializable
    data object DefaultQuality : Setting(
        title = R.string.quality,
        subtitle = null
    )

    @Serializable
    data object ShapeType : Setting(
        title = R.string.shapes_type,
        subtitle = null
    )

    @Serializable
    data object CornersSize : Setting(
        title = R.string.corners_size,
        subtitle = null
    )

    @Serializable
    data object FilenamePattern : Setting(
        title = R.string.filename_format,
        subtitle = null
    )

    @Serializable
    data object FlingType : Setting(
        title = R.string.fling_type,
        subtitle = null
    )

    @Serializable
    data object MotionDurationScale : Setting(
        title = R.string.motion_duration_scale,
        subtitle = R.string.motion_duration_scale_sub
    )

    @Serializable
    data object ToolsHiddenForShare : Setting(
        title = R.string.hidden_for_share,
        subtitle = null
    )

    @Serializable
    data object KeepDateTime : Setting(
        title = R.string.keep_date_time,
        subtitle = R.string.keep_date_time_sub
    )

    @Serializable
    data object AlwaysClearExif : Setting(
        title = R.string.always_clear_exif,
        subtitle = R.string.always_clear_exif_sub
    )

    @Serializable
    data object EnableBackgroundColorForAlphaFormats : Setting(
        title = R.string.background_color_for_alpha_formats,
        subtitle = R.string.background_color_for_alpha_formats_sub
    )

    @Serializable
    data object DebugMenu : Setting(
        title = R.string.debug_menu,
        subtitle = R.string.debug_menu_sub
    )

    @Serializable
    data object ShowToolsHistory : Setting(
        title = R.string.show_recent_tools,
        subtitle = R.string.show_recent_tools_sub
    )

}
