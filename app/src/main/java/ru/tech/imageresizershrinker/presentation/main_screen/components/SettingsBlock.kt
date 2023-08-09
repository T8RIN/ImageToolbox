package ru.tech.imageresizershrinker.presentation.main_screen.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BurstMode
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.FileDownloadOff
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.ImageSearch
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.ModeNight
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.material.icons.rounded.PhotoSizeSelectSmall
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material.icons.rounded.SettingsSystemDaydream
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.dynamic.theme.ColorTupleItem
import com.t8rin.dynamic.theme.observeAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.core.AUTHOR_AVATAR
import ru.tech.imageresizershrinker.core.CHAT_LINK
import ru.tech.imageresizershrinker.core.DONATE
import ru.tech.imageresizershrinker.core.ISSUE_TRACKER
import ru.tech.imageresizershrinker.core.WEBLATE_LINK
import ru.tech.imageresizershrinker.presentation.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.presentation.root.model.UiSettingsState
import ru.tech.imageresizershrinker.presentation.root.theme.EmojiItem
import ru.tech.imageresizershrinker.presentation.root.theme.blend
import ru.tech.imageresizershrinker.presentation.root.theme.icons.CreateAlt
import ru.tech.imageresizershrinker.presentation.root.theme.icons.DownloadFile
import ru.tech.imageresizershrinker.presentation.root.theme.icons.FileSettings
import ru.tech.imageresizershrinker.presentation.root.theme.icons.Lamp
import ru.tech.imageresizershrinker.presentation.root.theme.icons.Telegram
import ru.tech.imageresizershrinker.presentation.root.theme.inverse
import ru.tech.imageresizershrinker.presentation.root.theme.mixedColor
import ru.tech.imageresizershrinker.presentation.root.theme.onMixedColor
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ContextUtils.cacheSize
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ContextUtils.clearCache
import ru.tech.imageresizershrinker.presentation.root.utils.helper.toUiPath
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.pulsate
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.scaleOnTap
import ru.tech.imageresizershrinker.presentation.root.widget.image.Picture
import ru.tech.imageresizershrinker.presentation.root.widget.other.ToastDuration
import ru.tech.imageresizershrinker.presentation.root.widget.other.ToastHostState
import ru.tech.imageresizershrinker.presentation.root.widget.other.showError
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceRow
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.SourceCodePreference
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import kotlin.math.roundToInt

fun LazyListScope.settingsBlock(
    onEditPresets: () -> Unit,
    onEditArrangement: () -> Unit,
    onEditFilename: () -> Unit,
    onEditEmoji: () -> Unit,
    onEditColorScheme: () -> Unit,
    onShowAuthor: () -> Unit,
    settingsState: UiSettingsState,
    toastHostState: ToastHostState,
    scope: CoroutineScope,
    context: Context,
    viewModel: MainViewModel
) {
    item {
        // Night mode
        SettingItem(
            icon = Icons.Rounded.Lamp,
            text = stringResource(R.string.night_mode),
            initialState = true
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    stringResource(R.string.dark) to Icons.Rounded.ModeNight,
                    stringResource(R.string.light) to Icons.Rounded.WbSunny,
                    stringResource(R.string.system) to Icons.Rounded.SettingsSystemDaydream
                ).forEachIndexed { index, (title, icon) ->
                    val selected = index == viewModel.settingsState.nightMode
                    PreferenceItem(
                        onClick = { viewModel.setNightMode(index) },
                        title = title,
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(
                            alpha = animateFloatAsState(
                                if (selected) 0.7f
                                else 0.2f
                            ).value
                        ),
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .border(
                                width = settingsState.borderWidth,
                                color = animateColorAsState(
                                    if (selected) MaterialTheme
                                        .colorScheme
                                        .onSecondaryContainer
                                        .copy(alpha = 0.5f)
                                    else Color.Transparent
                                ).value,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        endIcon = if (selected) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked
                    )
                }
            }
        }
    }
    item {
        // Primary Customization
        SettingItem(
            icon = Icons.Rounded.Palette,
            text = stringResource(R.string.customization),
            initialState = true
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ChangeLanguagePreference()
                PreferenceRow(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    applyHorPadding = false,
                    title = stringResource(R.string.emoji),
                    subtitle = stringResource(R.string.emoji_sub),
                    onClick = {
                        onEditEmoji()
                    },
                    endContent = {
                        val emoji = LocalSettingsState.current.selectedEmoji
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .offset(x = 7.dp)
                                .background(
                                    MaterialTheme
                                        .colorScheme
                                        .surfaceVariant
                                        .copy(alpha = 0.5f),
                                    MaterialTheme.shapes.medium
                                )
                                .border(
                                    settingsState.borderWidth,
                                    MaterialTheme.colorScheme.outlineVariant(
                                        0.2f
                                    ),
                                    MaterialTheme.shapes.medium
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            EmojiItem(
                                emoji = emoji,
                                modifier = Modifier.then(
                                    if (emoji != null) Modifier.scaleOnTap(onRelease = {})
                                    else Modifier
                                ),
                                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                                onNoEmoji = { size ->
                                    Icon(
                                        imageVector = Icons.Rounded.Block,
                                        contentDescription = null,
                                        modifier = Modifier.size(size)
                                    )
                                }
                            )
                        }
                    }
                )
                PreferenceRowSwitch(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    applyHorPadding = false,
                    title = stringResource(R.string.dynamic_colors),
                    subtitle = stringResource(R.string.dynamic_colors_sub),
                    checked = settingsState.isDynamicColors,
                    onClick = { viewModel.toggleDynamicColors() }
                )
                val enabled = !settingsState.isDynamicColors
                PreferenceRow(
                    applyHorPadding = false,
                    modifier = Modifier
                        .alpha(
                            animateFloatAsState(
                                if (enabled) 1f
                                else 0.5f
                            ).value
                        )
                        .padding(horizontal = 8.dp),
                    title = stringResource(R.string.color_scheme),
                    subtitle = stringResource(R.string.pick_accent_color),
                    onClick = {
                        if (enabled) onEditColorScheme()
                        else scope.launch {
                            toastHostState.showToast(
                                icon = Icons.Rounded.Palette,
                                message = context.getString(R.string.cannot_change_palette_while_dynamic_colors_applied)
                            )
                        }
                    },
                    endContent = {
                        ColorTupleItem(
                            modifier = Modifier
                                .size(64.dp)
                                .offset(7.dp)
                                .border(
                                    settingsState.borderWidth,
                                    MaterialTheme.colorScheme.outlineVariant(
                                        0.2f
                                    ),
                                    MaterialTheme.shapes.medium
                                ),
                            colorTuple = settingsState.appColorTuple,
                            backgroundColor = MaterialTheme
                                .colorScheme
                                .surfaceVariant
                                .copy(alpha = 0.5f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(
                                        animateColorAsState(
                                            settingsState.appColorTuple.primary.inverse(
                                                fraction = {
                                                    if (it) 0.8f
                                                    else 0.5f
                                                },
                                                darkMode = settingsState.appColorTuple.primary.luminance() < 0.3f
                                            )
                                        ).value,
                                        CircleShape
                                    )
                            )
                            Icon(
                                imageVector = Icons.Rounded.CreateAlt,
                                contentDescription = null,
                                tint = settingsState.appColorTuple.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                )
                PreferenceRowSwitch(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    applyHorPadding = false,
                    title = stringResource(R.string.amoled_mode),
                    subtitle = stringResource(R.string.amoled_mode_sub),
                    checked = settingsState.isAmoledMode,
                    onClick = { viewModel.updateAmoledMode() }
                )
            }
        }
    }
    item {
        // Secondary Customization
        SettingItem(
            icon = Icons.Outlined.Palette,
            text = stringResource(R.string.secondary_customization),
            initialState = false
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PreferenceRowSwitch(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    applyHorPadding = false,
                    title = stringResource(R.string.allow_image_monet),
                    subtitle = stringResource(R.string.allow_image_monet_sub),
                    checked = settingsState.allowChangeColorByImage,
                    onClick = { viewModel.updateAllowImageMonet() }
                )
                Column(
                    Modifier
                        .padding(horizontal = 8.dp)
                        .block(
                            color = MaterialTheme
                                .colorScheme
                                .secondaryContainer
                                .copy(alpha = 0.2f)
                        )
                        .animateContentSize()
                ) {
                    var sliderValue by remember {
                        mutableIntStateOf(
                            settingsState.emojisCount.coerceAtLeast(1)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.emojis_count),
                            modifier = Modifier
                                .padding(
                                    top = 16.dp,
                                    end = 16.dp,
                                    start = 16.dp
                                )
                                .weight(1f)
                        )
                        AnimatedContent(
                            targetState = sliderValue,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            }
                        ) { value ->
                            Text(
                                text = "$value",
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.5f
                                ),
                                modifier = Modifier.padding(top = 16.dp),
                                lineHeight = 18.sp
                            )
                        }
                        Spacer(
                            modifier = Modifier.padding(
                                start = 4.dp,
                                top = 16.dp,
                                end = 20.dp
                            )
                        )
                    }
                    Slider(
                        modifier = Modifier
                            .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                            .offset(y = (-2).dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = CircleShape
                            )
                            .height(40.dp)
                            .border(
                                width = settingsState.borderWidth,
                                color = MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer),
                                shape = CircleShape
                            )
                            .padding(horizontal = 10.dp),
                        colors = SliderDefaults.colors(
                            inactiveTrackColor =
                            MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                        ),
                        value = animateFloatAsState(sliderValue.toFloat()).value,
                        onValueChange = {
                            sliderValue = it.toInt()
                        },
                        onValueChangeFinished = {
                            viewModel.updateEmojisCount(sliderValue)
                        },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                }
                Column(
                    Modifier
                        .padding(horizontal = 8.dp)
                        .block(
                            color = MaterialTheme
                                .colorScheme
                                .secondaryContainer
                                .copy(alpha = 0.2f)
                        )
                        .animateContentSize()
                ) {
                    var sliderValue by remember {
                        mutableFloatStateOf(
                            viewModel.settingsState.borderWidth.coerceAtLeast(0f)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.border_thickness),
                            modifier = Modifier
                                .padding(
                                    top = 16.dp,
                                    end = 16.dp,
                                    start = 16.dp
                                )
                                .weight(1f)
                        )
                        AnimatedContent(
                            targetState = sliderValue,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            }
                        ) { value ->
                            Text(
                                text = "$value",
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.5f
                                ),
                                modifier = Modifier.padding(top = 16.dp),
                                lineHeight = 18.sp
                            )
                        }
                        Text(
                            maxLines = 1,
                            text = "Dp",
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.5f
                            ),
                            modifier = Modifier.padding(
                                start = 4.dp,
                                top = 16.dp,
                                end = 16.dp
                            )
                        )
                    }
                    Slider(
                        modifier = Modifier
                            .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                            .offset(y = (-2).dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = CircleShape
                            )
                            .height(40.dp)
                            .border(
                                width = settingsState.borderWidth,
                                color = MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer),
                                shape = CircleShape
                            )
                            .padding(horizontal = 10.dp),
                        colors = SliderDefaults.colors(
                            inactiveTrackColor =
                            MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                        ),
                        value = animateFloatAsState(sliderValue).value,
                        onValueChange = {
                            sliderValue = it
                        },
                        onValueChangeFinished = {
                            viewModel.setBorderWidth(if (sliderValue > 0) sliderValue else -1f)
                        },
                        valueRange = 0f..4f,
                        steps = 15
                    )
                }
                Box(
                    Modifier
                        .padding(horizontal = 8.dp)
                        .block(
                            color = MaterialTheme
                                .colorScheme
                                .secondaryContainer
                                .copy(alpha = 0.2f)
                        )
                        .animateContentSize()
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 4.dp,
                                top = 4.dp,
                                bottom = 4.dp,
                                end = 8.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var sliderValue by remember {
                            mutableFloatStateOf(viewModel.settingsState.fabAlignment.toFloat())
                        }
                        Column(
                            Modifier
                                .weight(1f)
                                .height(136.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.fab_alignment),
                                modifier = Modifier
                                    .padding(
                                        top = 12.dp,
                                        end = 12.dp,
                                        start = 12.dp
                                    ),
                                lineHeight = 18.sp
                            )
                            AnimatedContent(
                                targetState = sliderValue,
                                transitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                }
                            ) { value ->
                                Text(
                                    text = stringArrayResource(R.array.fab_alignment_sub)[value.roundToInt()],
                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.5f
                                    ),
                                    modifier = Modifier.padding(
                                        top = 8.dp,
                                        start = 12.dp,
                                        bottom = 8.dp,
                                        end = 12.dp
                                    ),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 14.sp,
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Slider(
                                modifier = Modifier
                                    .padding(
                                        start = 12.dp,
                                        end = 12.dp,
                                        bottom = 4.dp,
                                        top = 4.dp
                                    )
                                    .offset(y = (-2).dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = CircleShape
                                    )
                                    .height(40.dp)
                                    .border(
                                        width = settingsState.borderWidth,
                                        color = MaterialTheme.colorScheme.outlineVariant(
                                            onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                        ),
                                        shape = CircleShape
                                    )
                                    .padding(horizontal = 10.dp),
                                colors = SliderDefaults.colors(
                                    inactiveTrackColor =
                                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                                ),
                                value = animateFloatAsState(sliderValue).value,
                                onValueChange = {
                                    sliderValue = it
                                    viewModel.setAlignment(sliderValue)
                                },
                                valueRange = 0f..2f,
                                steps = 1
                            )
                        }
                        FabPreview(
                            alignment = settingsState.fabAlignment,
                            modifier = Modifier.width(74.dp)
                        )
                    }
                }
            }
        }
    }
    item {
        // Arrangement
        SettingItem(
            icon = Icons.Rounded.TableRows,
            text = stringResource(R.string.options_arrangement),
        ) {
            val enabled = !settingsState.groupOptionsByTypes
            PreferenceItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .alpha(
                        animateFloatAsState(
                            if (enabled) 1f
                            else 0.5f
                        ).value
                    ),
                onClick = {
                    if (enabled) {
                        onEditArrangement()
                    } else scope.launch {
                        toastHostState.showToast(
                            icon = Icons.Rounded.TableRows,
                            message = context.getString(R.string.cannot_change_arrangement_while_options_grouping_enabled)
                        )
                    }
                },
                title = stringResource(R.string.order),
                subtitle = stringResource(R.string.order_sub),
                color = MaterialTheme
                    .colorScheme
                    .secondaryContainer
                    .copy(alpha = 0.2f),
                endIcon = Icons.Rounded.CreateAlt,
            )
            Spacer(Modifier.height(8.dp))
            PreferenceRowSwitch(
                modifier = Modifier.padding(horizontal = 8.dp),
                applyHorPadding = false,
                title = stringResource(R.string.group_options_by_type),
                subtitle = stringResource(R.string.group_options_by_type_sub),
                checked = settingsState.groupOptionsByTypes,
                onClick = {
                    viewModel.updateGroupOptionsByTypes()
                }
            )
        }
    }
    item {
        // Presets
        SettingItem(
            icon = Icons.Rounded.PhotoSizeSelectSmall,
            text = stringResource(R.string.presets),
        ) {
            PreferenceItem(
                onClick = { onEditPresets() },
                title = stringResource(R.string.values),
                subtitle = settingsState.presets.joinToString(", "),
                color = MaterialTheme
                    .colorScheme
                    .secondaryContainer
                    .copy(alpha = 0.2f),
                endIcon = Icons.Rounded.CreateAlt,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }
    item {
        // Folder
        SettingItem(
            icon = Icons.Rounded.Folder,
            text = stringResource(R.string.folder),
        ) {
            val currentFolderUri = settingsState.saveFolderUri
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocumentTree(),
                onResult = { uri ->
                    uri?.let {
                        viewModel.updateSaveFolderUri(it)
                        context.contentResolver.takePersistableUriPermission(
                            it,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                    }
                }
            )
            PreferenceItem(
                onClick = { viewModel.updateSaveFolderUri(null) },
                title = stringResource(R.string.def),
                subtitle = stringResource(R.string.default_folder),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                    alpha = animateFloatAsState(
                        if (currentFolderUri == null) 0.7f
                        else 0.2f
                    ).value
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .border(
                        width = settingsState.borderWidth,
                        color = animateColorAsState(
                            if (currentFolderUri == null) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                alpha = 0.5f
                            )
                            else Color.Transparent
                        ).value,
                        shape = RoundedCornerShape(16.dp)
                    )
            )
            Spacer(modifier = Modifier.height(8.dp))
            PreferenceItem(
                onClick = {
                    kotlin.runCatching {
                        launcher.launch(currentFolderUri)
                    }.getOrNull() ?: scope.launch {
                        toastHostState.showToast(
                            context.getString(R.string.activate_files),
                            icon = Icons.Outlined.FolderOff,
                            duration = ToastDuration.Long
                        )
                    }
                },
                title = stringResource(R.string.custom),
                subtitle = currentFolderUri.toUiPath(
                    context = LocalContext.current,
                    default = stringResource(R.string.unspecified)
                ),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                    alpha = animateFloatAsState(
                        if (currentFolderUri != null) 0.7f
                        else 0.2f
                    ).value
                ),
                endIcon = if (currentFolderUri != null) Icons.Rounded.CreateAlt else Icons.Rounded.AddCircleOutline,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .border(
                        width = settingsState.borderWidth,
                        color = animateColorAsState(
                            if (currentFolderUri != null) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                alpha = 0.5f
                            )
                            else Color.Transparent
                        ).value,
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        }
    }
    item {
        // Cache
        SettingItem(
            icon = Icons.Rounded.Cached,
            text = stringResource(R.string.cache),
        ) {
            var cache by remember(
                context,
                LocalLifecycleOwner.current.lifecycle.observeAsState().value
            ) { mutableStateOf(context.cacheSize()) }

            PreferenceItem(
                onClick = {
                    context.clearCache { cache = it }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                color = MaterialTheme
                    .colorScheme
                    .secondaryContainer
                    .copy(alpha = 0.2f),
                title = stringResource(R.string.cache_size),
                subtitle = stringResource(R.string.found_s, cache),
                endIcon = Icons.Rounded.DeleteOutline
            )
            Spacer(modifier = Modifier.height(8.dp))
            PreferenceRowSwitch(
                modifier = Modifier.padding(horizontal = 8.dp),
                applyHorPadding = false,
                title = stringResource(R.string.auto_cache_clearing),
                subtitle = stringResource(R.string.auto_cache_clearing_sub),
                checked = settingsState.clearCacheOnLaunch,
                onClick = {
                    viewModel.updateClearCacheOnLaunch()
                }
            )
        }
    }
    item {
        // File
        SettingItem(
            icon = Icons.Rounded.FileSettings,
            text = stringResource(R.string.filename),
        ) {
            Box {
                Column(Modifier.alpha(animateFloatAsState(if (!settingsState.randomizeFilename) 1f else 0.5f).value)) {
                    PreferenceItem(
                        onClick = { onEditFilename() },
                        title = stringResource(R.string.prefix),
                        subtitle = (settingsState.filenamePrefix.takeIf { it.isNotEmpty() }
                            ?: stringResource(R.string.default_prefix)),
                        color = MaterialTheme
                            .colorScheme
                            .secondaryContainer
                            .copy(alpha = 0.2f),
                        endIcon = Icons.Rounded.CreateAlt,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    PreferenceRowSwitch(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        applyHorPadding = false,
                        onClick = { viewModel.toggleAddFileSize() },
                        title = stringResource(R.string.add_file_size),
                        subtitle = stringResource(R.string.add_file_size_sub),
                        checked = settingsState.addSizeInFilename
                    )
                    Spacer(Modifier.height(8.dp))
                    val enabled = settingsState.imagePickerModeInt != 0
                    PreferenceRowSwitch(
                        applyHorPadding = false,
                        modifier = Modifier
                            .alpha(
                                animateFloatAsState(
                                    if (enabled) 1f
                                    else 0.5f
                                ).value
                            )
                            .padding(horizontal = 8.dp),
                        onClick = {
                            if (enabled) viewModel.toggleAddOriginalFilename()
                            else scope.launch {
                                toastHostState.showToast(
                                    message = context.getString(R.string.filename_not_work_with_photopicker),
                                    icon = Icons.Outlined.ErrorOutline
                                )
                            }
                        },
                        title = stringResource(R.string.add_original_filename),
                        subtitle = stringResource(R.string.add_original_filename_sub),
                        checked = settingsState.addOriginalFilename && enabled
                    )
                    Spacer(Modifier.height(8.dp))
                    PreferenceRowSwitch(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        applyHorPadding = false,
                        onClick = { viewModel.toggleAddSequenceNumber() },
                        title = stringResource(R.string.replace_sequence_number),
                        subtitle = stringResource(R.string.replace_sequence_number_sub),
                        checked = settingsState.addSequenceNumber
                    )
                    Spacer(Modifier.height(8.dp))
                }
                if (settingsState.randomizeFilename) {
                    Surface(modifier = Modifier.matchParentSize(), color = Color.Transparent) {}
                }
            }
            PreferenceRowSwitch(
                modifier = Modifier.padding(horizontal = 8.dp),
                applyHorPadding = false,
                onClick = { viewModel.toggleRandomizeFilename() },
                title = stringResource(R.string.randomize_filename),
                subtitle = stringResource(R.string.randomize_filename_sub),
                checked = settingsState.randomizeFilename
            )
        }
    }
    item {
        // Source
        SettingItem(
            icon = Icons.Rounded.ImageSearch,
            text = stringResource(R.string.image_source),
        ) {
            PreferenceItem(
                onClick = { viewModel.updateImagePickerMode(0) },
                title = stringResource(R.string.photo_picker),
                icon = Icons.Outlined.BurstMode,
                subtitle = stringResource(R.string.photo_picker_sub),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                    alpha = animateFloatAsState(
                        if (settingsState.imagePickerModeInt == 0) 0.7f
                        else 0.2f
                    ).value
                ),
                endIcon = if (settingsState.imagePickerModeInt == 0) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .border(
                        width = settingsState.borderWidth,
                        color = animateColorAsState(
                            if (settingsState.imagePickerModeInt == 0) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                alpha = 0.5f
                            )
                            else Color.Transparent
                        ).value,
                        shape = RoundedCornerShape(16.dp)
                    )
            )
            Spacer(modifier = Modifier.height(8.dp))
            PreferenceItem(
                onClick = { viewModel.updateImagePickerMode(1) },
                title = stringResource(R.string.gallery_picker),
                icon = Icons.Outlined.Image,
                subtitle = stringResource(R.string.gallery_picker_sub),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                    alpha = animateFloatAsState(
                        if (settingsState.imagePickerModeInt == 1) 0.7f
                        else 0.2f
                    ).value
                ),
                endIcon = if (settingsState.imagePickerModeInt == 1) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .border(
                        width = settingsState.borderWidth,
                        color = animateColorAsState(
                            if (settingsState.imagePickerModeInt == 1) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                alpha = 0.5f
                            )
                            else Color.Transparent
                        ).value,
                        shape = RoundedCornerShape(16.dp)
                    )
            )
            Spacer(modifier = Modifier.height(8.dp))
            PreferenceItem(
                onClick = { viewModel.updateImagePickerMode(2) },
                title = stringResource(R.string.file_explorer_picker),
                subtitle = stringResource(R.string.file_explorer_picker_sub),
                icon = Icons.Rounded.FolderOpen,
                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                    alpha = animateFloatAsState(
                        if (settingsState.imagePickerModeInt == 2) 0.7f
                        else 0.2f
                    ).value
                ),
                endIcon = if (settingsState.imagePickerModeInt == 2) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .border(
                        width = settingsState.borderWidth,
                        color = animateColorAsState(
                            if (settingsState.imagePickerModeInt == 2) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                alpha = 0.5f
                            )
                            else Color.Transparent
                        ).value,
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        }
    }
    item {
        // Backup and restore
        SettingItem(
            icon = Icons.Rounded.SettingsBackupRestore,
            text = stringResource(R.string.backup_and_restore),
            initialState = false
        ) {
            val confettiController = LocalConfettiController.current
            val backupSavingLauncher = rememberLauncherForActivityResult(
                contract = object : ActivityResultContracts.CreateDocument("*/*") {
                    override fun createIntent(context: Context, input: String): Intent {
                        return super.createIntent(
                            context = context,
                            input = input.split("#")[0]
                        ).putExtra(Intent.EXTRA_TITLE, input.split("#")[1])
                    }
                },
                onResult = {
                    it?.let { uri ->
                        viewModel.createBackup(
                            context.contentResolver.openOutputStream(
                                uri,
                                "rw"
                            )
                        ) {
                            scope.launch {
                                confettiController.showEmpty()
                            }
                            scope.launch {
                                toastHostState.showToast(
                                    context.getString(
                                        R.string.saved_to_without_filename,
                                        ""
                                    ),
                                    Icons.Rounded.Save
                                )
                            }
                        }
                    }
                }
            )
            val filePicker = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent(),
                onResult = { uri ->
                    uri?.let {
                        viewModel.restoreBackupFrom(
                            uri = it,
                            onSuccess = {
                                scope.launch {
                                    confettiController.showEmpty()
                                }
                                scope.launch {
                                    toastHostState.showToast(
                                        context.getString(R.string.settings_restored),
                                        Icons.Rounded.Save
                                    )
                                }
                            },
                            onFailure = {
                                scope.launch {
                                    toastHostState.showError(context, it)
                                }
                            }
                        )
                    }
                }
            )
            PreferenceItem(
                onClick = {
                    backupSavingLauncher.launch("*/*#image_toolbox_settings_backup.imtbx_backup")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                color = MaterialTheme
                    .colorScheme
                    .secondaryContainer
                    .copy(alpha = 0.2f),
                title = stringResource(R.string.backup),
                subtitle = stringResource(R.string.backup_sub),
                endIcon = Icons.Rounded.UploadFile
            )
            Spacer(modifier = Modifier.height(8.dp))
            PreferenceItem(
                onClick = {
                    filePicker.launch("*/*")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                color = MaterialTheme
                    .colorScheme
                    .secondaryContainer
                    .copy(alpha = 0.2f),
                title = stringResource(R.string.restore),
                subtitle = stringResource(R.string.restore_sub),
                endIcon = Icons.Rounded.DownloadFile
            )
        }
    }
    item {
        // About app
        SettingItem(
            icon = Icons.Rounded.Info,
            text = stringResource(R.string.about_app),
            initialState = true
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PreferenceRow(
                    applyHorPadding = false,
                    modifier = Modifier
                        .pulsate(
                            enabled = viewModel.updateAvailable,
                            range = 0.98f..1.02f
                        )
                        .padding(horizontal = 8.dp),
                    title = stringResource(R.string.version),
                    subtitle = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    endContent = {
                        Icon(
                            painterResource(R.drawable.ic_launcher_monochrome),
                            null,
                            tint = animateColorAsState(
                                if (settingsState.isNightMode) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onPrimaryContainer.blend(
                                        Color.White
                                    )
                                }
                            ).value,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(64.dp)
                                .offset(7.dp)
                                .background(
                                    animateColorAsState(
                                        if (settingsState.isNightMode) {
                                            MaterialTheme.colorScheme.background.blend(
                                                Color.White,
                                                0.1f
                                            )
                                        } else {
                                            MaterialTheme.colorScheme.primaryContainer
                                        }
                                    ).value,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .border(
                                    settingsState.borderWidth,
                                    MaterialTheme.colorScheme.outlineVariant(),
                                    shape = MaterialTheme.shapes.medium
                                )
                                .scale(1.4f)
                        )
                    },
                    onClick = {
                        viewModel.tryGetUpdate(
                            newRequest = true,
                            onNoUpdates = {
                                scope.launch {
                                    toastHostState.showToast(
                                        icon = Icons.Rounded.FileDownloadOff,
                                        message = context.getString(R.string.no_updates)
                                    )
                                }
                            }
                        )
                    }
                )
                PreferenceRowSwitch(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    applyHorPadding = false,
                    title = stringResource(R.string.check_updates),
                    subtitle = stringResource(R.string.check_updates_sub),
                    checked = viewModel.settingsState.showDialogOnStartup,
                    onClick = {
                        viewModel.toggleShowDialog()
                    }
                )
                PreferenceRow(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    applyHorPadding = false,
                    title = stringResource(R.string.help_translate),
                    subtitle = stringResource(R.string.help_translate_sub),
                    endContent = {
                        Icon(Icons.Rounded.Translate, null)
                    },
                    onClick = {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(WEBLATE_LINK)
                            )
                        )
                    }
                )
                PreferenceRow(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    applyHorPadding = false,
                    title = stringResource(R.string.issue_tracker),
                    subtitle = stringResource(R.string.issue_tracker_sub),
                    endContent = {
                        Icon(Icons.Rounded.BugReport, null)
                    },
                    onClick = {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(ISSUE_TRACKER)
                            )
                        )
                    }
                )
                PreferenceRow(
                    applyHorPadding = false,
                    onClick = {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(CHAT_LINK)
                            )
                        )
                    },
                    startContent = {
                        Icon(
                            Icons.Rounded.Telegram,
                            null,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                    },
                    title = stringResource(R.string.tg_chat),
                    subtitle = stringResource(R.string.tg_chat_sub),
                    color = MaterialTheme.colorScheme.mixedColor,
                    contentColor = MaterialTheme.colorScheme.onMixedColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                )
                SourceCodePreference(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
    item {
        // Contact me
        SettingItem(
            icon = Icons.Rounded.PersonSearch,
            text = stringResource(R.string.contact_me),
            initialState = true
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PreferenceRow(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    applyHorPadding = false,
                    title = stringResource(R.string.buy_me_a_coffee),
                    subtitle = stringResource(R.string.buy_me_a_coffee_sub),
                    endContent = {
                        Icon(Icons.Rounded.Coffee, null)
                    },
                    onClick = {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(DONATE)
                            )
                        )
                    }
                )
                PreferenceRow(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    applyHorPadding = false,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    title = stringResource(R.string.app_developer),
                    subtitle = stringResource(R.string.app_developer_nick),
                    startContent = {
                        Picture(
                            model = AUTHOR_AVATAR,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(48.dp)
                                .border(
                                    settingsState.borderWidth,
                                    MaterialTheme.colorScheme.outlineVariant(),
                                    MaterialTheme.shapes.medium
                                ),
                            shape = MaterialTheme.shapes.medium
                        )
                    },
                    onClick = {
                        onShowAuthor()
                    }
                )
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}