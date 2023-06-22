package ru.tech.imageresizershrinker.widget.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.ColorTuple


@Stable
interface SettingsState {
    val isNightMode: Boolean
    val isDynamicColors: Boolean
    val allowChangeColorByImage: Boolean
    val emojisCount: Int
    val isAmoledMode: Boolean
    val appColorTuple: ColorTuple
    val borderWidth: Dp
    val presets: List<Int>
    val fabAlignment: Alignment
    val selectedEmoji: ImageVector?
    val imagePickerModeInt: Int
    val clearCacheOnLaunch: Boolean
    val groupOptionsByTypes: Boolean
}

@Stable
private data class SettingsStateImpl(
    override val isNightMode: Boolean,
    override val isDynamicColors: Boolean,
    override val allowChangeColorByImage: Boolean,
    override val isAmoledMode: Boolean,
    override val emojisCount: Int,
    override val appColorTuple: ColorTuple,
    override val borderWidth: Dp,
    override val presets: List<Int>,
    override val fabAlignment: Alignment,
    override val selectedEmoji: ImageVector?,
    override val imagePickerModeInt: Int,
    override val clearCacheOnLaunch: Boolean,
    override val groupOptionsByTypes: Boolean
) : SettingsState

fun SettingsState(
    isNightMode: Boolean,
    isDynamicColors: Boolean,
    allowChangeColorByImage: Boolean,
    isAmoledMode: Boolean,
    appColorTuple: ColorTuple,
    borderWidth: Dp,
    emojisCount: Int,
    presets: List<Int>,
    fabAlignment: Alignment,
    selectedEmoji: ImageVector?,
    imagePickerModeInt: Int,
    clearCacheOnLaunch: Boolean,
    groupOptionsByType: Boolean
): SettingsState = SettingsStateImpl(
    isNightMode = isNightMode,
    isDynamicColors = isDynamicColors,
    allowChangeColorByImage = allowChangeColorByImage,
    isAmoledMode = isAmoledMode,
    appColorTuple = appColorTuple,
    borderWidth = borderWidth,
    presets = presets,
    emojisCount = emojisCount,
    fabAlignment = fabAlignment,
    selectedEmoji = selectedEmoji,
    imagePickerModeInt = imagePickerModeInt,
    clearCacheOnLaunch = clearCacheOnLaunch,
    groupOptionsByTypes = groupOptionsByType
)

@Composable
fun rememberSettingsState(
    isNightMode: Boolean = isSystemInDarkTheme(),
    isDynamicColors: Boolean = false,
    allowChangeColorByImage: Boolean = false,
    isAmoledMode: Boolean = false,
    appColorTuple: ColorTuple = ColorTuple(MaterialTheme.colorScheme.primary),
    borderWidth: Dp = 1.dp,
    emojisCount: Int = 1,
    presets: List<Int> = emptyList(),
    fabAlignment: Alignment = Alignment.Center,
    selectedEmoji: ImageVector? = null,
    imagePickerModeInt: Int = 0,
    clearCacheOnLaunch: Boolean = false,
    groupOptionsByType: Boolean = true
): SettingsState = remember(
    isNightMode,
    isDynamicColors,
    allowChangeColorByImage,
    isAmoledMode,
    appColorTuple,
    emojisCount,
    borderWidth,
    presets,
    fabAlignment,
    selectedEmoji,
    imagePickerModeInt,
    clearCacheOnLaunch,
    groupOptionsByType
) {
    derivedStateOf {
        SettingsState(
            isNightMode = isNightMode,
            isDynamicColors = isDynamicColors,
            allowChangeColorByImage = allowChangeColorByImage,
            isAmoledMode = isAmoledMode,
            appColorTuple = appColorTuple,
            borderWidth = borderWidth,
            presets = presets,
            emojisCount = emojisCount,
            fabAlignment = fabAlignment,
            selectedEmoji = selectedEmoji,
            imagePickerModeInt = imagePickerModeInt,
            clearCacheOnLaunch = clearCacheOnLaunch,
            groupOptionsByType = groupOptionsByType
        )
    }
}.value

val LocalSettingsState = compositionLocalOf<SettingsState> { error("SettingsState not present") }

val LocalEditPresetsState = compositionLocalOf { mutableStateOf(false) }

fun Int.toAlignment() = when (this) {
    0 -> Alignment.BottomStart
    1 -> Alignment.BottomCenter
    else -> Alignment.BottomEnd
}

@Composable
fun Int.isNightMode(): Boolean = when (this) {
    0 -> true
    1 -> false
    else -> isSystemInDarkTheme()
}