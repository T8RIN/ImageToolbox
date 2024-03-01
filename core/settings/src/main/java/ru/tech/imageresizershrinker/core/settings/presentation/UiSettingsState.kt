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

package ru.tech.imageresizershrinker.core.settings.presentation

import android.net.Uri
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.PaletteStyle
import kotlinx.collections.immutable.ImmutableList
import ru.tech.imageresizershrinker.core.domain.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.settings.domain.model.CopyToClipboardMode
import ru.tech.imageresizershrinker.core.settings.domain.model.DomainAspectRatio
import ru.tech.imageresizershrinker.core.settings.domain.model.NightMode
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState

@Stable
data class UiSettingsState(
    val isNightMode: Boolean = false,
    val isDynamicColors: Boolean = true,
    val allowChangeColorByImage: Boolean = true,
    val emojisCount: Int = 1,
    val isAmoledMode: Boolean = false,
    val appColorTuple: ColorTuple = ColorTuple(Color.Unspecified),
    val borderWidth: Dp = (-1).dp,
    val presets: List<Int> = listOf(),
    val fabAlignment: Alignment = Alignment.BottomCenter,
    val showUpdateDialogOnStartup: Boolean = true,
    val selectedEmoji: Uri?,
    val imagePickerModeInt: Int = 0,
    val clearCacheOnLaunch: Boolean = true,
    val groupOptionsByTypes: Boolean = true,
    val screenList: List<Int> = listOf(),
    val colorTupleList: List<ColorTuple>,
    val addSequenceNumber: Boolean = true,
    val saveFolderUri: Uri? = null,
    val filenamePrefix: String = "",
    val addSizeInFilename: Boolean = false,
    val addOriginalFilename: Boolean = false,
    val randomizeFilename: Boolean = false,
    val font: UiFontFam,
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
    val usePixelSwitch: Boolean,
    val magnifierEnabled: Boolean,
    val exifWidgetInitialState: Boolean,
    val screenListWithMaxBrightnessEnforcement: List<Int>,
    val isConfettiEnabled: Boolean,
    val isSecureMode: Boolean,
    val useRandomEmojis: Boolean,
    val iconShape: IconShape?,
    val useEmojiAsPrimaryColor: Boolean
)

fun UiSettingsState.isFirstLaunch(
    approximate: Boolean = true
) = if (approximate) {
    appOpenCount <= 3f
} else appOpenCount <= 1f

@Composable
fun SettingsState.toUiState(
    allEmojis: ImmutableList<Uri>,
    allIconShapes: ImmutableList<IconShape>,
    getEmojiColorTuple: (String, (ColorTuple?) -> Unit) -> Unit = { _, _ -> },
    randomEmojiKey: Any? = null
): UiSettingsState {
    val selectedEmojiIndex by remember(selectedEmoji, useRandomEmojis, randomEmojiKey, this) {
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
                selectedEmojiIndex?.let { selectedEmoji ->
                    getEmojiColorTuple(
                        allEmojis[selectedEmoji].toString()
                    ) {
                        emojiColorTuple = it
                    }
                    null
                } ?: appColorTuple.asColorTuple()
            } else {
                emojiColorTuple = null
                appColorTuple.asColorTuple()
            }
        }
    }

    return UiSettingsState(
        isNightMode = nightMode.isNightMode(),
        isDynamicColors = isDynamicColors,
        allowChangeColorByImage = allowChangeColorByImage,
        emojisCount = emojisCount,
        isAmoledMode = isAmoledMode,
        appColorTuple = remember(appColorTupleComposed, appColorTuple) {
            derivedStateOf {
                emojiColorTuple ?: appColorTupleComposed
            }
        }.value,
        borderWidth = animateDpAsState(borderWidth.dp).value,
        presets = remember(presets) {
            derivedStateOf {
                presets.mapNotNull { it.value() }
            }
        }.value,
        fabAlignment = fabAlignment.toAlignment(),
        showUpdateDialogOnStartup = showUpdateDialogOnStartup,
        selectedEmoji = remember(selectedEmojiIndex, allEmojis) {
            derivedStateOf {
                selectedEmojiIndex?.let {
                    allEmojis.getOrNull(it)
                }
            }
        }.value,
        imagePickerModeInt = imagePickerModeInt,
        clearCacheOnLaunch = clearCacheOnLaunch,
        groupOptionsByTypes = groupOptionsByTypes,
        screenList = screenList,
        colorTupleList = remember(colorTupleList) {
            derivedStateOf {
                colorTupleList.toColorTupleList()
            }
        }.value,
        addSequenceNumber = addSequenceNumber,
        saveFolderUri = remember(saveFolderUri) {
            derivedStateOf {
                saveFolderUri?.toUri()?.takeIf { it != Uri.EMPTY }
            }
        }.value,
        filenamePrefix = filenamePrefix,
        addSizeInFilename = addSizeInFilename,
        addOriginalFilename = addOriginalFilename,
        randomizeFilename = randomizeFilename,
        font = remember(font) {
            derivedStateOf {
                font.toUiFont()
            }
        }.value,
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
        themeStyle = remember(themeStyle) {
            derivedStateOf {
                PaletteStyle
                    .entries
                    .getOrNull(themeStyle) ?: PaletteStyle.TonalSpot
            }
        }.value,
        isInvertThemeColors = isInvertThemeColors,
        screensSearchEnabled = screensSearchEnabled,
        copyToClipboardMode = copyToClipboardMode,
        hapticsStrength = hapticsStrength,
        overwriteFiles = overwriteFiles,
        filenameSuffix = filenameSuffix,
        defaultImageScaleMode = defaultImageScaleMode,
        usePixelSwitch = usePixelSwitch,
        magnifierEnabled = magnifierEnabled,
        exifWidgetInitialState = exifWidgetInitialState,
        screenListWithMaxBrightnessEnforcement = screenListWithMaxBrightnessEnforcement,
        isConfettiEnabled = isConfettiEnabled,
        isSecureMode = isSecureMode,
        useRandomEmojis = useRandomEmojis,
        iconShape = remember(iconShape) {
            derivedStateOf {
                iconShape?.let {
                    allIconShapes.getOrNull(it)
                }
            }
        }.value,
        useEmojiAsPrimaryColor = useEmojiAsPrimaryColor
    )
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

private fun String.asColorTuple(): ColorTuple {
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

val defaultColorTuple = ColorTuple(Color(0xFF8FDB3A))