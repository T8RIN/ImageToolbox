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

package ru.tech.imageresizershrinker.core.settings.domain.model

import ru.tech.imageresizershrinker.core.domain.image.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.model.Preset
import ru.tech.imageresizershrinker.core.domain.image.model.Preset.Percentage
import ru.tech.imageresizershrinker.core.domain.image.model.ResizeType
import ru.tech.imageresizershrinker.core.domain.model.ColorModel
import ru.tech.imageresizershrinker.core.domain.model.DomainAspectRatio
import ru.tech.imageresizershrinker.core.domain.model.SystemBarsVisibility

data class SettingsState(
    val nightMode: NightMode,
    val isDynamicColors: Boolean,
    val allowChangeColorByImage: Boolean,
    val emojisCount: Int,
    val isAmoledMode: Boolean,
    val appColorTuple: String,
    val borderWidth: Float,
    val presets: List<Preset>,
    val aspectRatios: List<DomainAspectRatio>,
    val fabAlignment: Int,
    val selectedEmoji: Int?,
    val picturePickerModeInt: Int,
    val clearCacheOnLaunch: Boolean,
    val showUpdateDialogOnStartup: Boolean,
    val groupOptionsByTypes: Boolean,
    val screenList: List<Int>,
    val colorTupleList: String?,
    val addSequenceNumber: Boolean,
    val saveFolderUri: String?,
    val filenamePrefix: String,
    val addSizeInFilename: Boolean,
    val addOriginalFilename: Boolean,
    val randomizeFilename: Boolean,
    val font: DomainFontFamily,
    val fontScale: Float?,
    val allowCollectCrashlytics: Boolean,
    val allowCollectAnalytics: Boolean,
    val allowBetas: Boolean,
    val drawContainerShadows: Boolean,
    val drawButtonShadows: Boolean,
    val drawSliderShadows: Boolean,
    val drawSwitchShadows: Boolean,
    val drawFabShadows: Boolean,
    val drawAppBarShadows: Boolean,
    val appOpenCount: Int,
    val lockDrawOrientation: Boolean,
    val themeContrastLevel: Double,
    val themeStyle: Int,
    val isInvertThemeColors: Boolean,
    val screensSearchEnabled: Boolean,
    val copyToClipboardMode: CopyToClipboardMode,
    val hapticsStrength: Int,
    val overwriteFiles: Boolean,
    val filenameSuffix: String,
    val defaultImageScaleMode: ImageScaleMode,
    val magnifierEnabled: Boolean,
    val exifWidgetInitialState: Boolean,
    val initialOcrCodes: List<String>,
    val screenListWithMaxBrightnessEnforcement: List<Int>,
    val isConfettiEnabled: Boolean,
    val isSecureMode: Boolean,
    val useRandomEmojis: Boolean,
    val iconShape: Int?,
    val useEmojiAsPrimaryColor: Boolean,
    val dragHandleWidth: Int,
    val confettiType: Int,
    val allowAutoClipboardPaste: Boolean,
    val confettiColorHarmonizer: ColorHarmonizer,
    val confettiHarmonizationLevel: Float,
    val skipImagePicking: Boolean,
    val generatePreviews: Boolean,
    val showSettingsInLandscape: Boolean,
    val useFullscreenSettings: Boolean,
    val switchType: SwitchType,
    val defaultDrawLineWidth: Float,
    val oneTimeSaveLocations: List<OneTimeSaveLocation>,
    val openEditInsteadOfPreview: Boolean,
    val canEnterPresetsByTextField: Boolean,
    val donateDialogOpenCount: Int,
    val colorBlindType: Int?,
    val favoriteScreenList: List<Int>,
    val isLinkPreviewEnabled: Boolean,
    val defaultDrawColor: ColorModel,
    val defaultDrawPathMode: Int,
    val addTimestampToFilename: Boolean,
    val useFormattedFilenameTimestamp: Boolean,
    val favoriteColors: List<ColorModel>,
    val defaultResizeType: ResizeType,
    val systemBarsVisibility: SystemBarsVisibility,
    val isSystemBarsVisibleBySwipe: Boolean
) {

    companion object {
        val Default by lazy {
            SettingsState(
                nightMode = NightMode.System,
                isDynamicColors = true,
                allowChangeColorByImage = true,
                emojisCount = 1,
                isAmoledMode = false,
                appColorTuple = "",
                borderWidth = -1f,
                presets = List(6) { Percentage(100 - it * 10) },
                fabAlignment = 1,
                selectedEmoji = 0,
                picturePickerModeInt = 0,
                clearCacheOnLaunch = false,
                showUpdateDialogOnStartup = true,
                groupOptionsByTypes = true,
                screenList = emptyList(),
                colorTupleList = null,
                addSequenceNumber = true,
                saveFolderUri = null,
                filenamePrefix = "ResizedImage",
                addSizeInFilename = false,
                addOriginalFilename = false,
                randomizeFilename = false,
                font = DomainFontFamily.System,
                fontScale = 1f,
                allowCollectCrashlytics = true,
                allowCollectAnalytics = true,
                allowBetas = true,
                drawContainerShadows = true,
                drawButtonShadows = true,
                drawSwitchShadows = true,
                drawSliderShadows = true,
                drawFabShadows = true,
                drawAppBarShadows = true,
                appOpenCount = 0,
                aspectRatios = DomainAspectRatio.defaultList,
                lockDrawOrientation = false,
                themeContrastLevel = 0.0,
                themeStyle = 0,
                isInvertThemeColors = false,
                screensSearchEnabled = false,
                hapticsStrength = 1,
                overwriteFiles = false,
                filenameSuffix = "",
                defaultImageScaleMode = ImageScaleMode.Default,
                copyToClipboardMode = CopyToClipboardMode.Disabled,
                magnifierEnabled = false,
                exifWidgetInitialState = false,
                initialOcrCodes = listOf("eng"),
                screenListWithMaxBrightnessEnforcement = emptyList(),
                isConfettiEnabled = true,
                isSecureMode = false,
                useRandomEmojis = false,
                iconShape = 0,
                useEmojiAsPrimaryColor = false,
                dragHandleWidth = 64,
                confettiType = 0,
                allowAutoClipboardPaste = false,
                confettiColorHarmonizer = ColorHarmonizer.Primary,
                confettiHarmonizationLevel = 0.5f,
                skipImagePicking = false,
                generatePreviews = true,
                showSettingsInLandscape = true,
                useFullscreenSettings = false,
                switchType = SwitchType.MaterialYou,
                defaultDrawLineWidth = 20f,
                oneTimeSaveLocations = emptyList(),
                openEditInsteadOfPreview = false,
                canEnterPresetsByTextField = false,
                donateDialogOpenCount = 0,
                colorBlindType = null,
                favoriteScreenList = emptyList(),
                isLinkPreviewEnabled = true,
                defaultDrawColor = ColorModel(-0x1000000),
                defaultDrawPathMode = 0,
                addTimestampToFilename = true,
                useFormattedFilenameTimestamp = true,
                favoriteColors = emptyList(),
                defaultResizeType = ResizeType.Explicit,
                systemBarsVisibility = SystemBarsVisibility.Auto,
                isSystemBarsVisibleBySwipe = true
            )
        }
    }

}