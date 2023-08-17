package ru.tech.imageresizershrinker.presentation.root.model

import android.net.Uri
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.t8rin.dynamic.theme.ColorTuple
import ru.tech.imageresizershrinker.domain.model.FontFam
import ru.tech.imageresizershrinker.domain.model.NightMode
import ru.tech.imageresizershrinker.domain.model.SettingsState
import ru.tech.imageresizershrinker.presentation.root.theme.Emoji
import ru.tech.imageresizershrinker.presentation.root.theme.allIcons
import ru.tech.imageresizershrinker.presentation.root.theme.defaultColorTuple
import ru.tech.imageresizershrinker.presentation.root.theme.toColor
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.Screen

data class UiSettingsState(
    val isNightMode: Boolean = false,
    val isDynamicColors: Boolean = true,
    val allowChangeColorByImage: Boolean = true,
    val emojisCount: Int = 1,
    val isAmoledMode: Boolean = false,
    val appColorTuple: ColorTuple = ColorTuple(Color.Unspecified),
    val borderWidth: Dp = 1.dp,
    val presets: List<Int> = listOf(),
    val fabAlignment: Alignment = Alignment.BottomCenter,
    val showDialogOnStartup: Boolean = true,
    val selectedEmoji: Uri?,
    val imagePickerModeInt: Int = 0,
    val clearCacheOnLaunch: Boolean = true,
    val groupOptionsByTypes: Boolean = true,
    val screenList: List<Screen> = listOf(),
    val colorTupleList: List<ColorTuple> = listOf(),
    val addSequenceNumber: Boolean = true,
    val saveFolderUri: Uri? = null,
    val filenamePrefix: String = "",
    val addSizeInFilename: Boolean = false,
    val addOriginalFilename: Boolean = false,
    val randomizeFilename: Boolean = false,
    val font: UiFontFam,
    val fontScale: Float?
)

@Composable
fun SettingsState.toUiState() = UiSettingsState(
    isNightMode = nightMode.isNightMode(),
    isDynamicColors = isDynamicColors,
    allowChangeColorByImage = allowChangeColorByImage,
    emojisCount = emojisCount,
    isAmoledMode = isAmoledMode,
    appColorTuple = appColorTuple.asColorTuple(),
    borderWidth = animateDpAsState(borderWidth.dp).value,
    presets = presets.mapNotNull { it.value() },
    fabAlignment = fabAlignment.toAlignment(),
    showDialogOnStartup = showDialogOnStartup,
    selectedEmoji = selectedEmoji?.takeIf { it != -1 }?.let { Emoji.allIcons[it] },
    imagePickerModeInt = imagePickerModeInt,
    clearCacheOnLaunch = clearCacheOnLaunch,
    groupOptionsByTypes = groupOptionsByTypes,
    screenList = screenList.map { Screen.entries[it] }.takeIf { it.isNotEmpty() } ?: Screen.entries,
    colorTupleList = colorTupleList.toColorTupleList(),
    addSequenceNumber = addSequenceNumber,
    saveFolderUri = saveFolderUri?.toUri()?.takeIf { it != Uri.EMPTY },
    filenamePrefix = filenamePrefix,
    addSizeInFilename = addSizeInFilename,
    addOriginalFilename = addOriginalFilename,
    randomizeFilename = randomizeFilename,
    font = font.toUiFont(),
    fontScale = fontScale?.takeIf { it > 0 }
)

private fun FontFam.toUiFont(): UiFontFam {
    return when (this) {
        FontFam.Caveat -> UiFontFam.Caveat
        FontFam.Comfortaa -> UiFontFam.Comfortaa
        FontFam.System -> UiFontFam.System
        FontFam.Handjet -> UiFontFam.Handjet
        FontFam.Jura -> UiFontFam.Jura
        FontFam.Montserrat -> UiFontFam.Montserrat
        FontFam.Podkova -> UiFontFam.Podkova
        FontFam.Tektur -> UiFontFam.Tektur
        FontFam.YsabeauSC -> UiFontFam.YsabeauSC
    }
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