package ru.tech.imageresizershrinker.domain.model

import ru.tech.imageresizershrinker.domain.Domain

data class SettingsState(
    val nightMode: NightMode,
    val isDynamicColors: Boolean,
    val allowChangeColorByImage: Boolean,
    val emojisCount: Int,
    val isAmoledMode: Boolean,
    val appColorTuple: String,
    val borderWidth: Float,
    val presets: List<Preset>,
    val fabAlignment: Int,
    val selectedEmoji: Int?,
    val imagePickerModeInt: Int,
    val clearCacheOnLaunch: Boolean,
    val showDialogOnStartup: Boolean,
    val groupOptionsByTypes: Boolean,
    val screenList: List<Int>,
    val colorTupleList: String?,
    val addSequenceNumber: Boolean,
    val saveFolderUri: String?,
    val filenamePrefix: String,
    val addSizeInFilename: Boolean,
    val addOriginalFilename: Boolean,
    val randomizeFilename: Boolean,
    val font: Int,
    val fontScale: Float?
) : Domain {
    companion object {
        fun Default() = SettingsState(
            nightMode = NightMode.System,
            isDynamicColors = true,
            allowChangeColorByImage = true,
            emojisCount = 1,
            isAmoledMode = false,
            appColorTuple = "",
            borderWidth = 1f,
            presets = emptyList(),
            fabAlignment = 1,
            selectedEmoji = 0,
            imagePickerModeInt = 0,
            clearCacheOnLaunch = true,
            showDialogOnStartup = true,
            groupOptionsByTypes = true,
            screenList = emptyList(),
            colorTupleList = null,
            addSequenceNumber = true,
            saveFolderUri = null,
            filenamePrefix = "",
            addSizeInFilename = true,
            addOriginalFilename = false,
            randomizeFilename = false,
            font = 0,
            fontScale = 1f
        )
    }
}