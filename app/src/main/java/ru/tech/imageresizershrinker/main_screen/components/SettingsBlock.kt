package ru.tech.imageresizershrinker.main_screen.components

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
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.FileDownloadOff
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.ImageSearch
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.ModeNight
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhotoSizeSelectSmall
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.SettingsSystemDaydream
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.dynamic.theme.ColorTupleItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.common.AUTHOR_AVATAR
import ru.tech.imageresizershrinker.common.DONATE
import ru.tech.imageresizershrinker.common.ISSUE_TRACKER
import ru.tech.imageresizershrinker.common.WEBLATE_LINK
import ru.tech.imageresizershrinker.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.theme.CreateAlt
import ru.tech.imageresizershrinker.theme.Emoji
import ru.tech.imageresizershrinker.theme.EmojiItem
import ru.tech.imageresizershrinker.theme.FileSettings
import ru.tech.imageresizershrinker.theme.Lamp
import ru.tech.imageresizershrinker.theme.allIcons
import ru.tech.imageresizershrinker.theme.blend
import ru.tech.imageresizershrinker.theme.inverse
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.helper.BitmapInfo
import ru.tech.imageresizershrinker.utils.modifier.block
import ru.tech.imageresizershrinker.utils.modifier.pulsate
import ru.tech.imageresizershrinker.utils.modifier.scaleOnTap
import ru.tech.imageresizershrinker.utils.storage.LocalFileController
import ru.tech.imageresizershrinker.utils.storage.SaveTarget
import ru.tech.imageresizershrinker.utils.storage.constructFilename
import ru.tech.imageresizershrinker.utils.storage.toUiPath
import ru.tech.imageresizershrinker.widget.PreferenceRow
import ru.tech.imageresizershrinker.widget.PreferenceRowSwitch
import ru.tech.imageresizershrinker.widget.TitleItem
import ru.tech.imageresizershrinker.widget.ToastHostState
import ru.tech.imageresizershrinker.widget.image.Picture
import ru.tech.imageresizershrinker.widget.utils.SettingsState
import ru.tech.imageresizershrinker.widget.utils.isNightMode
import ru.tech.imageresizershrinker.widget.utils.toAlignment
import kotlin.math.roundToInt

fun LazyListScope.SettingsBlock(
    onEditPresets: () -> Unit,
    onEditArrangement: () -> Unit,
    onEditFilename: () -> Unit,
    onEditEmoji: () -> Unit,
    onEditColorScheme: () -> Unit,
    onShowAuthor: () -> Unit,
    settingsState: SettingsState,
    currentFolderUri: Uri?,
    toastHost: ToastHostState,
    scope: CoroutineScope,
    context: Context,
    viewModel: MainViewModel
) {
    item {
        // Night mode
        Column {
            TitleItem(
                icon = Icons.Rounded.Lamp,
                text = stringResource(R.string.night_mode),
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    stringResource(R.string.dark) to Icons.Rounded.ModeNight,
                    stringResource(R.string.light) to Icons.Rounded.WbSunny,
                    stringResource(R.string.system) to Icons.Rounded.SettingsSystemDaydream
                ).forEachIndexed { index, (title, icon) ->
                    val selected = index == viewModel.nightMode
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
                            .padding(horizontal = 16.dp)
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
            Spacer(Modifier.height(16.dp))
        }
        Divider()
    }
    item {
        // Customization
        Column {
            TitleItem(
                icon = Icons.Rounded.Palette,
                text = stringResource(R.string.customization),
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ChangeLanguagePreference()
                PreferenceRow(
                    title = stringResource(R.string.emoji),
                    subtitle = stringResource(R.string.emoji_sub),
                    onClick = {
                        onEditEmoji()
                    },
                    endContent = {
                        val emoji =
                            Emoji.allIcons.getOrNull(viewModel.selectedEmoji)
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
                    title = stringResource(R.string.dynamic_colors),
                    subtitle = stringResource(R.string.dynamic_colors_sub),
                    checked = viewModel.dynamicColors,
                    onClick = { viewModel.updateDynamicColors() }
                )
                val enabled = !viewModel.dynamicColors
                PreferenceRow(
                    modifier = Modifier.alpha(
                        animateFloatAsState(
                            if (enabled) 1f
                            else 0.5f
                        ).value
                    ),
                    title = stringResource(R.string.color_scheme),
                    subtitle = stringResource(R.string.pick_accent_color),
                    onClick = {
                        if (enabled) onEditColorScheme()
                        else scope.launch {
                            toastHost.showToast(
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
                            colorTuple = viewModel.appColorTuple,
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
                                            viewModel.appColorTuple.primary.inverse(
                                                fraction = {
                                                    if (it) 0.8f
                                                    else 0.5f
                                                },
                                                darkMode = viewModel.appColorTuple.primary.luminance() < 0.3f
                                            )
                                        ).value,
                                        CircleShape
                                    )
                            )
                            Icon(
                                imageVector = Icons.Rounded.CreateAlt,
                                contentDescription = null,
                                tint = viewModel.appColorTuple.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                )
                PreferenceRowSwitch(
                    title = stringResource(R.string.allow_image_monet),
                    subtitle = stringResource(R.string.allow_image_monet_sub),
                    checked = viewModel.allowImageMonet,
                    onClick = { viewModel.updateAllowImageMonet() }
                )
                PreferenceRowSwitch(
                    title = stringResource(R.string.amoled_mode),
                    subtitle = stringResource(R.string.amoled_mode_sub),
                    checked = viewModel.amoledMode,
                    onClick = { viewModel.updateAmoledMode() }
                )
                Column(
                    Modifier
                        .padding(horizontal = 16.dp)
                        .block(
                            color = MaterialTheme
                                .colorScheme
                                .secondaryContainer
                                .copy(alpha = 0.2f)
                        )
                        .animateContentSize()
                ) {
                    var sliderValue by remember {
                        mutableStateOf(
                            viewModel.emojisCount.coerceAtLeast(1)
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
                        modifier = Modifier.padding(horizontal = 16.dp),
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
                        .padding(horizontal = 16.dp)
                        .block(
                            color = MaterialTheme
                                .colorScheme
                                .secondaryContainer
                                .copy(alpha = 0.2f)
                        )
                        .animateContentSize()
                ) {
                    var sliderValue by remember {
                        mutableStateOf(
                            viewModel.borderWidth.coerceAtLeast(0f)
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
                        modifier = Modifier.padding(horizontal = 16.dp),
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
                        .padding(horizontal = 16.dp)
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
                            mutableStateOf(viewModel.alignment.toFloat())
                        }
                        Column(
                            Modifier
                                .weight(1f)
                                .height(115.dp)
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
                                modifier = Modifier.padding(horizontal = 12.dp),
                                value = animateFloatAsState(sliderValue).value,
                                onValueChange = {
                                    sliderValue = it
                                },
                                onValueChangeFinished = {
                                    viewModel.setAlignment(sliderValue)
                                },
                                valueRange = 0f..2f,
                                steps = 1
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        FabPreview(
                            alignment = sliderValue.roundToInt().toAlignment(),
                            modifier = Modifier.width(64.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        Divider()
    }
    item {
        // Arrangement
        Column {
            TitleItem(
                icon = Icons.Rounded.TableRows,
                text = stringResource(R.string.options_arrangement),
            )
            PreferenceItem(
                onClick = { onEditArrangement() },
                title = stringResource(R.string.order),
                subtitle = stringResource(R.string.order_sub),
                color = MaterialTheme
                    .colorScheme
                    .secondaryContainer
                    .copy(alpha = 0.2f),
                endIcon = Icons.Rounded.CreateAlt,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(16.dp))
        }
        Divider()
    }
    item {
        // Presets
        Column {
            TitleItem(
                icon = Icons.Rounded.PhotoSizeSelectSmall,
                text = stringResource(R.string.presets),
            )
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
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(16.dp))
        }
        Divider()
    }
    item {
        // Folder
        Column {
            val launcher = rememberLauncherForActivityResult(
                contract = object :
                    ActivityResultContracts.OpenDocumentTree() {
                    override fun createIntent(
                        context: Context,
                        input: Uri?
                    ): Intent {
                        val intent = super.createIntent(context, input)
                        intent.addFlags(
                            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                        return intent
                    }
                },
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
            TitleItem(
                icon = Icons.Rounded.Folder,
                text = stringResource(R.string.folder),
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
                    .padding(horizontal = 16.dp)
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
                onClick = { launcher.launch(currentFolderUri) },
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
                    .padding(horizontal = 16.dp)
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
            Spacer(Modifier.height(16.dp))
        }
        Divider()
    }
    item {
        // File
        Column {
            val fileController = LocalFileController.current
            TitleItem(
                icon = Icons.Rounded.FileSettings,
                text = stringResource(R.string.filename),
            )
            PreferenceItem(
                onClick = { onEditFilename() },
                title = stringResource(R.string.prefix),
                subtitle = remember(
                    viewModel.filenamePrefix,
                    viewModel.addSizeInFilename,
                    viewModel.addOriginalFilename,
                    viewModel.addSequenceNumber
                ) {
                    derivedStateOf {
                        constructFilename(
                            context = context,
                            fileParams = fileController.fileParams,
                            saveTarget = SaveTarget(BitmapInfo(), Uri.EMPTY, null)
                        ).split(".")[0].let {
                            if (viewModel.imagePickerModeInt == 0) {
                                it.replace("_" + context.getString(R.string.original_filename), "")
                            } else it
                        } + ".img"
                    }
                }.value,
                color = MaterialTheme
                    .colorScheme
                    .secondaryContainer
                    .copy(alpha = 0.2f),
                endIcon = Icons.Rounded.CreateAlt,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(8.dp))
            PreferenceRowSwitch(
                onClick = { viewModel.updateAddFileSize() },
                title = stringResource(R.string.add_file_size),
                subtitle = stringResource(R.string.add_file_size_sub),
                checked = viewModel.addSizeInFilename
            )
            Spacer(Modifier.height(8.dp))
            val enabled = viewModel.imagePickerModeInt != 0
            PreferenceRowSwitch(
                modifier = Modifier.alpha(
                    animateFloatAsState(
                        if (enabled) 1f
                        else 0.5f
                    ).value
                ),
                onClick = {
                    if (enabled) viewModel.updateAddOriginalFilename()
                    else scope.launch {
                        toastHost.showToast(
                            message = context.getString(R.string.filename_not_work_with_photopicker),
                            icon = Icons.Outlined.ErrorOutline
                        )
                    }
                },
                title = stringResource(R.string.add_original_filename),
                subtitle = stringResource(R.string.add_original_filename_sub),
                checked = viewModel.addOriginalFilename && enabled
            )
            Spacer(Modifier.height(8.dp))
            PreferenceRowSwitch(
                onClick = { viewModel.updateAddSequenceNumber() },
                title = stringResource(R.string.replace_sequence_number),
                subtitle = stringResource(R.string.replace_sequence_number_sub),
                checked = viewModel.addSequenceNumber
            )
            Spacer(Modifier.height(16.dp))
        }
        Divider()
    }
    item {
        // Source
        Column {
            TitleItem(
                icon = Icons.Rounded.ImageSearch,
                text = stringResource(R.string.image_source),
            )
            PreferenceItem(
                onClick = { viewModel.updateImagePickerMode(0) },
                title = stringResource(R.string.photo_picker),
                icon = Icons.Outlined.BurstMode,
                subtitle = stringResource(R.string.photo_picker_sub),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                    alpha = animateFloatAsState(
                        if (viewModel.imagePickerModeInt == 0) 0.7f
                        else 0.2f
                    ).value
                ),
                endIcon = if (viewModel.imagePickerModeInt == 0) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(
                        width = settingsState.borderWidth,
                        color = animateColorAsState(
                            if (viewModel.imagePickerModeInt == 0) MaterialTheme.colorScheme.onSecondaryContainer.copy(
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
                        if (viewModel.imagePickerModeInt == 1) 0.7f
                        else 0.2f
                    ).value
                ),
                endIcon = if (viewModel.imagePickerModeInt == 1) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(
                        width = settingsState.borderWidth,
                        color = animateColorAsState(
                            if (viewModel.imagePickerModeInt == 1) MaterialTheme.colorScheme.onSecondaryContainer.copy(
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
                        if (viewModel.imagePickerModeInt == 2) 0.7f
                        else 0.2f
                    ).value
                ),
                endIcon = if (viewModel.imagePickerModeInt == 2) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(
                        width = settingsState.borderWidth,
                        color = animateColorAsState(
                            if (viewModel.imagePickerModeInt == 2) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                alpha = 0.5f
                            )
                            else Color.Transparent
                        ).value,
                        shape = RoundedCornerShape(16.dp)
                    )
            )
            Spacer(Modifier.height(16.dp))
        }
        Divider()
    }
    item {
        // About app
        Column {
            TitleItem(
                icon = Icons.Rounded.Info,
                text = stringResource(R.string.about_app)
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PreferenceRow(
                    modifier = Modifier.pulsate(
                        enabled = viewModel.updateAvailable,
                        range = 0.98f..1.02f
                    ),
                    title = stringResource(R.string.version),
                    subtitle = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    endContent = {
                        Icon(
                            painterResource(R.drawable.ic_launcher_monochrome),
                            null,
                            tint = animateColorAsState(
                                if (viewModel.nightMode.isNightMode()) {
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
                                        if (viewModel.nightMode.isNightMode()) {
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
                                    toastHost.showToast(
                                        icon = Icons.Rounded.FileDownloadOff,
                                        message = context.getString(R.string.no_updates)
                                    )
                                }
                            }
                        )
                    }
                )
                PreferenceRowSwitch(
                    title = stringResource(R.string.check_updates),
                    subtitle = stringResource(R.string.check_updates_sub),
                    checked = viewModel.showDialogOnStartUp,
                    onClick = viewModel::updateShowDialog
                )
                PreferenceRow(
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
                SourceCodePreference(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}