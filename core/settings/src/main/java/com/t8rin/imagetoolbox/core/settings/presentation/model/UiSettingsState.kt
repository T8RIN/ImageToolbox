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

package com.t8rin.imagetoolbox.core.settings.presentation.model

import android.net.Uri
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import com.t8rin.dynamic.theme.ColorBlindType
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.PaletteStyle
import com.t8rin.dynamic.theme.extractPrimaryColor
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.domain.model.HashingType
import com.t8rin.imagetoolbox.core.domain.model.SystemBarsVisibility
import com.t8rin.imagetoolbox.core.resources.BuildConfig
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.emoji.Emoji
import com.t8rin.imagetoolbox.core.settings.domain.model.ColorHarmonizer
import com.t8rin.imagetoolbox.core.settings.domain.model.CopyToClipboardMode
import com.t8rin.imagetoolbox.core.settings.domain.model.FastSettingsSide
import com.t8rin.imagetoolbox.core.settings.domain.model.NightMode
import com.t8rin.imagetoolbox.core.settings.domain.model.OneTimeSaveLocation
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.settings.domain.model.SliderType
import com.t8rin.imagetoolbox.core.settings.domain.model.SwitchType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@Stable
data class UiSettingsState(
    val isNightMode: Boolean,
    val isDynamicColors: Boolean,
    val allowChangeColorByImage: Boolean,
    val emojisCount: Int,
    val isAmoledMode: Boolean,
    val appColorTuple: ColorTuple,
    val borderWidth: Dp,
    val presets: List<Int>,
    val fabAlignment: Alignment,
    val showUpdateDialogOnStartup: Boolean,
    val selectedEmoji: Uri?,
    val picturePickerMode: PicturePickerMode,
    val clearCacheOnLaunch: Boolean,
    val groupOptionsByTypes: Boolean,
    val screenList: List<Int>,
    val colorTupleList: List<ColorTuple>,
    val addSequenceNumber: Boolean,
    val saveFolderUri: Uri?,
    val filenamePrefix: String,
    val addSizeInFilename: Boolean,
    val addOriginalFilename: Boolean,
    val randomizeFilename: Boolean,
    val font: UiFontFamily,
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
    val aspectRatios: List<DomainAspectRatio>,
    val lockDrawOrientation: Boolean,
    val themeContrastLevel: Double,
    val themeStyle: PaletteStyle,
    val isInvertThemeColors: Boolean,
    val screensSearchEnabled: Boolean,
    val copyToClipboardMode: CopyToClipboardMode,
    val hapticsStrength: Int,
    val overwriteFiles: Boolean,
    val filenameSuffix: String,
    val defaultImageScaleMode: ImageScaleMode,
    val magnifierEnabled: Boolean,
    val exifWidgetInitialState: Boolean,
    val screenListWithMaxBrightnessEnforcement: List<Int>,
    val isConfettiEnabled: Boolean,
    val isSecureMode: Boolean,
    val useRandomEmojis: Boolean,
    val iconShape: IconShape?,
    val useEmojiAsPrimaryColor: Boolean,
    val dragHandleWidth: Dp,
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
    val donateDialogOpenCount: Int?,
    val colorBlindType: ColorBlindType?,
    val favoriteScreenList: List<Int>,
    val isLinkPreviewEnabled: Boolean,
    val defaultDrawColor: Color,
    val defaultDrawPathMode: Int,
    val addTimestampToFilename: Boolean,
    val useFormattedFilenameTimestamp: Boolean,
    val favoriteColors: List<Color>,
    val defaultResizeType: ResizeType,
    val systemBarsVisibility: SystemBarsVisibility,
    val isSystemBarsVisibleBySwipe: Boolean,
    val isCompactSelectorsLayout: Boolean,
    val mainScreenTitle: String,
    val sliderType: SliderType,
    val isCenterAlignDialogButtons: Boolean,
    val fastSettingsSide: FastSettingsSide,
    val settingGroupsInitialVisibility: Map<Int, Boolean>,
    val hashingTypeForFilename: HashingType?,
    val customFonts: List<UiFontFamily.Custom>,
    val enableToolExitConfirmation: Boolean,
    val recentColors: List<Color>,
    val backgroundForNoAlphaImageFormats: Color,
    val addPresetInfoToFilename: Boolean,
    val addImageScaleModeInfoToFilename: Boolean,
    val allowSkipIfLarger: Boolean,
    val customAsciiGradients: Set<String>,
    val isScreenSelectionLauncherMode: Boolean,
    val spotHealMode: Int
)

fun UiSettingsState.isFirstLaunch(
    approximate: Boolean = true,
) = if (approximate) {
    appOpenCount <= 3
} else appOpenCount < 1

@Composable
fun SettingsState.toUiState(
    randomEmojiKey: Any? = null,
): UiSettingsState {
    val allEmojis = Emoji.allIcons()
    val allIconShapes: ImmutableList<IconShape> = IconShape.entries

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isNightMode = nightMode.isNightMode()

    val selectedEmojiIndex by remember(selectedEmoji, useRandomEmojis, randomEmojiKey) {
        derivedStateOf {
            selectedEmoji?.takeIf { it != -1 }?.let {
                if (useRandomEmojis) allEmojis.indices.random()
                else it
            }
        }
    }

    var emojiColorTuple: ColorTuple? by remember {
        mutableStateOf(null)
    }

    val appColorTupleComposed by remember(
        allEmojis,
        selectedEmojiIndex,
        appColorTuple,
        useEmojiAsPrimaryColor
    ) {
        derivedStateOf {
            if (useEmojiAsPrimaryColor) {
                selectedEmojiIndex?.let { index ->
                    scope.launch {
                        context.imageLoader.execute(
                            ImageRequest.Builder(context)
                                .target {
                                    emojiColorTuple =
                                        ColorTuple(it.toBitmap().extractPrimaryColor())
                                }
                                .data(allEmojis[index].toString())
                                .build()
                        )
                    }
                }
            } else {
                emojiColorTuple = null
            }
            appColorTuple.asColorTuple()
        }
    }

    val appColorTuple by remember(appColorTupleComposed, appColorTuple) {
        derivedStateOf {
            emojiColorTuple ?: appColorTupleComposed
        }
    }

    val borderWidth by animateDpAsState(borderWidth.dp)

    val presets by remember(presets) {
        derivedStateOf {
            presets.mapNotNull(Preset::value)
        }
    }

    val selectedEmoji by remember(selectedEmojiIndex, allEmojis) {
        derivedStateOf {
            selectedEmojiIndex?.let(allEmojis::getOrNull)
        }
    }

    val colorTupleList by remember(colorTupleList) {
        derivedStateOf {
            colorTupleList.toColorTupleList()
        }
    }

    val saveFolderUri by remember(saveFolderUri) {
        derivedStateOf {
            saveFolderUri?.toUri()?.takeIf { it != Uri.EMPTY }
        }
    }

    val font by remember(font) {
        derivedStateOf {
            font.toUiFont()
        }
    }

    val themeStyle by remember(themeStyle) {
        derivedStateOf {
            PaletteStyle
                .entries
                .getOrNull(themeStyle) ?: PaletteStyle.TonalSpot
        }
    }

    val iconShape by remember(iconShape) {
        derivedStateOf {
            iconShape?.let(allIconShapes::getOrNull)
        }
    }

    val dragHandleWidth by animateDpAsState(dragHandleWidth.dp)

    val colorBlindType by remember(colorBlindType) {
        derivedStateOf {
            colorBlindType?.let {
                ColorBlindType.entries.getOrNull(it)
            }
        }
    }

    val favoriteColors by remember(favoriteColors) {
        derivedStateOf {
            favoriteColors.map { Color(it.colorInt) }
        }
    }

    val recentColors by remember(recentColors) {
        derivedStateOf {
            recentColors.map { Color(it.colorInt) }
        }
    }

    val mainScreenTitle = mainScreenTitle.ifEmpty { stringResource(R.string.app_name) }

    val customFonts by remember(customFonts) {
        derivedStateOf {
            customFonts.map {
                it.toUiFont() as UiFontFamily.Custom
            }
        }
    }

    return remember(this, selectedEmoji) {
        derivedStateOf {
            UiSettingsState(
                isNightMode = isNightMode,
                isDynamicColors = isDynamicColors,
                allowChangeColorByImage = allowChangeColorByImage,
                emojisCount = emojisCount,
                isAmoledMode = isAmoledMode,
                appColorTuple = appColorTuple,
                borderWidth = borderWidth,
                presets = presets,
                fabAlignment = fabAlignment.toAlignment(),
                showUpdateDialogOnStartup = showUpdateDialogOnStartup,
                selectedEmoji = selectedEmoji,
                picturePickerMode = PicturePickerMode.fromInt(picturePickerModeInt),
                clearCacheOnLaunch = clearCacheOnLaunch,
                groupOptionsByTypes = groupOptionsByTypes,
                screenList = screenList,
                colorTupleList = colorTupleList,
                addSequenceNumber = addSequenceNumber,
                saveFolderUri = saveFolderUri,
                filenamePrefix = filenamePrefix,
                addSizeInFilename = addSizeInFilename,
                addOriginalFilename = addOriginalFilename,
                randomizeFilename = randomizeFilename,
                font = font,
                fontScale = fontScale?.takeIf { it > 0 },
                allowCollectCrashlytics = allowCollectCrashlytics,
                allowCollectAnalytics = allowCollectAnalytics,
                allowBetas = allowBetas,
                drawContainerShadows = drawContainerShadows,
                drawButtonShadows = drawButtonShadows,
                drawFabShadows = drawFabShadows,
                drawSliderShadows = drawSliderShadows,
                drawSwitchShadows = drawSwitchShadows,
                drawAppBarShadows = drawAppBarShadows,
                appOpenCount = appOpenCount,
                aspectRatios = aspectRatios,
                lockDrawOrientation = lockDrawOrientation,
                themeContrastLevel = themeContrastLevel,
                themeStyle = themeStyle,
                isInvertThemeColors = isInvertThemeColors,
                screensSearchEnabled = screensSearchEnabled,
                copyToClipboardMode = copyToClipboardMode,
                hapticsStrength = hapticsStrength,
                overwriteFiles = overwriteFiles,
                filenameSuffix = filenameSuffix,
                defaultImageScaleMode = defaultImageScaleMode,
                magnifierEnabled = magnifierEnabled,
                exifWidgetInitialState = exifWidgetInitialState,
                screenListWithMaxBrightnessEnforcement = screenListWithMaxBrightnessEnforcement,
                isConfettiEnabled = isConfettiEnabled,
                isSecureMode = isSecureMode,
                useRandomEmojis = useRandomEmojis,
                iconShape = iconShape,
                useEmojiAsPrimaryColor = useEmojiAsPrimaryColor,
                dragHandleWidth = dragHandleWidth,
                confettiType = confettiType,
                allowAutoClipboardPaste = allowAutoClipboardPaste,
                confettiColorHarmonizer = confettiColorHarmonizer,
                confettiHarmonizationLevel = confettiHarmonizationLevel,
                skipImagePicking = skipImagePicking,
                generatePreviews = generatePreviews,
                showSettingsInLandscape = showSettingsInLandscape,
                useFullscreenSettings = useFullscreenSettings,
                switchType = switchType,
                defaultDrawLineWidth = defaultDrawLineWidth,
                oneTimeSaveLocations = oneTimeSaveLocations,
                openEditInsteadOfPreview = openEditInsteadOfPreview,
                canEnterPresetsByTextField = canEnterPresetsByTextField,
                donateDialogOpenCount = donateDialogOpenCount.takeIf { it >= 0 },
                colorBlindType = colorBlindType,
                favoriteScreenList = favoriteScreenList,
                isLinkPreviewEnabled = isLinkPreviewEnabled,
                defaultDrawColor = Color(defaultDrawColor.colorInt),
                defaultDrawPathMode = defaultDrawPathMode,
                addTimestampToFilename = addTimestampToFilename,
                useFormattedFilenameTimestamp = useFormattedFilenameTimestamp,
                favoriteColors = favoriteColors,
                defaultResizeType = defaultResizeType,
                systemBarsVisibility = systemBarsVisibility,
                isSystemBarsVisibleBySwipe = isSystemBarsVisibleBySwipe,
                isCompactSelectorsLayout = isCompactSelectorsLayout,
                mainScreenTitle = mainScreenTitle,
                sliderType = sliderType,
                isCenterAlignDialogButtons = isCenterAlignDialogButtons,
                fastSettingsSide = fastSettingsSide,
                settingGroupsInitialVisibility = settingGroupsInitialVisibility,
                hashingTypeForFilename = hashingTypeForFilename,
                customFonts = customFonts,
                enableToolExitConfirmation = enableToolExitConfirmation,
                recentColors = recentColors,
                backgroundForNoAlphaImageFormats = Color(backgroundForNoAlphaImageFormats.colorInt),
                addPresetInfoToFilename = addPresetInfoToFilename,
                addImageScaleModeInfoToFilename = addImageScaleModeInfoToFilename,
                allowSkipIfLarger = allowSkipIfLarger,
                customAsciiGradients = customAsciiGradients,
                isScreenSelectionLauncherMode = isScreenSelectionLauncherMode,
                spotHealMode = spotHealMode
            )
        }
    }.value
}

private fun String?.toColorTupleList(): List<ColorTuple> {
    val list = mutableListOf<ColorTuple>()
    this?.split("*")?.forEach { colorTuple ->
        val temp = colorTuple.split("/")
        temp.getOrNull(0)?.toIntOrNull()?.toColor()?.let {
            list.add(
                ColorTuple(
                    primary = it,
                    secondary = temp.getOrNull(1)?.toIntOrNull()?.toColor(),
                    tertiary = temp.getOrNull(2)?.toIntOrNull()?.toColor(),
                    surface = temp.getOrNull(3)?.toIntOrNull()?.toColor()
                )
            )
        }
    }
    if (list.isEmpty()) {
        list.add(defaultColorTuple)
    }
    return list.toHashSet().toList()
}

private fun Int.toColor() = Color(this)

fun String.asColorTuple(): ColorTuple {
    val colorTuple = split("*")
    return ColorTuple(
        primary = colorTuple.getOrNull(0)?.toIntOrNull()?.let { Color(it) }
            ?: defaultColorTuple.primary,
        secondary = colorTuple.getOrNull(1)?.toIntOrNull()?.let { Color(it) },
        tertiary = colorTuple.getOrNull(2)?.toIntOrNull()?.let { Color(it) },
        surface = colorTuple.getOrNull(3)?.toIntOrNull()?.let { Color(it) },
    )
}

private fun Int.toAlignment() = when (this) {
    0 -> Alignment.BottomStart
    1 -> Alignment.BottomCenter
    else -> Alignment.BottomEnd
}

@Composable
private fun NightMode.isNightMode(): Boolean = when (this) {
    NightMode.System -> isSystemInDarkTheme()
    else -> this is NightMode.Dark
}

val defaultColorTuple = ColorTuple(
    if (BuildConfig.DEBUG) Color(0xFF3ADBD6)
    else Color(0xFF8FDB3A)
)