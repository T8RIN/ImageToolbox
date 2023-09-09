@file:Suppress("KotlinConstantConditions")

package ru.tech.imageresizershrinker.presentation.main_screen.components

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BorderStyle
import androidx.compose.material.icons.outlined.BurstMode
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.FileDownloadOff
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.FolderSpecial
import androidx.compose.material.icons.rounded.FontDownload
import androidx.compose.material.icons.rounded.ImageSearch
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.ModeNight
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.material.icons.rounded.PhotoSizeSelectSmall
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material.icons.rounded.SettingsSystemDaydream
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.TextFormat
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.twotone.Palette
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
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
import ru.tech.imageresizershrinker.domain.model.NightMode
import ru.tech.imageresizershrinker.presentation.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.presentation.root.icons.emoji.EmojiItem
import ru.tech.imageresizershrinker.presentation.root.icons.material.Analytics
import ru.tech.imageresizershrinker.presentation.root.icons.material.Crashlytics
import ru.tech.imageresizershrinker.presentation.root.icons.material.CreateAlt
import ru.tech.imageresizershrinker.presentation.root.icons.material.DownloadFile
import ru.tech.imageresizershrinker.presentation.root.icons.material.FileSettings
import ru.tech.imageresizershrinker.presentation.root.icons.material.Firebase
import ru.tech.imageresizershrinker.presentation.root.icons.material.Lamp
import ru.tech.imageresizershrinker.presentation.root.icons.material.Telegram
import ru.tech.imageresizershrinker.presentation.root.model.UiFontFam
import ru.tech.imageresizershrinker.presentation.root.model.UiSettingsState
import ru.tech.imageresizershrinker.presentation.root.shapes.CloverShape
import ru.tech.imageresizershrinker.presentation.root.shapes.DavidStarShape
import ru.tech.imageresizershrinker.presentation.root.theme.blend
import ru.tech.imageresizershrinker.presentation.root.theme.inverse
import ru.tech.imageresizershrinker.presentation.root.theme.mixedColor
import ru.tech.imageresizershrinker.presentation.root.theme.onMixedColor
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ContextUtils.cacheSize
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ContextUtils.clearCache
import ru.tech.imageresizershrinker.presentation.root.utils.helper.toUiPath
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.container
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.pulsate
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.scaleOnTap
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedSlider
import ru.tech.imageresizershrinker.presentation.root.widget.dialogs.ResetDialog
import ru.tech.imageresizershrinker.presentation.root.widget.image.Picture
import ru.tech.imageresizershrinker.presentation.root.widget.other.ToastDuration
import ru.tech.imageresizershrinker.presentation.root.widget.other.ToastHostState
import ru.tech.imageresizershrinker.presentation.root.widget.other.showError
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceRow
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.SourceCodePreference
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SourceLockedOrientationActivity")
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
    item { Spacer(Modifier.height(8.dp)) }
    item {
        // Night mode
        SettingItem(
            icon = Icons.Rounded.Lamp,
            text = stringResource(R.string.night_mode),
            initialState = false
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple(
                        stringResource(R.string.dark),
                        Icons.Rounded.ModeNight,
                        NightMode.Dark
                    ),
                    Triple(
                        stringResource(R.string.light),
                        Icons.Rounded.WbSunny,
                        NightMode.Light
                    ),
                    Triple(
                        stringResource(R.string.system),
                        Icons.Rounded.SettingsSystemDaydream,
                        NightMode.System
                    ),
                ).forEach { (title, icon, nightMode) ->
                    val selected = nightMode == viewModel.settingsState.nightMode
                    PreferenceItem(
                        onClick = { viewModel.setNightMode(nightMode) },
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
            Column {
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
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
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
                                .size(72.dp)
                                .offset(7.dp)
                                .container(
                                    shape = DavidStarShape,
                                    color = MaterialTheme
                                        .colorScheme
                                        .surfaceVariant
                                        .copy(alpha = 0.5f),
                                    borderColor = MaterialTheme.colorScheme.outlineVariant(0.2f),
                                    resultPadding = 5.dp
                                ),
                            colorTuple = settingsState.appColorTuple,
                            backgroundColor = Color.Transparent
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
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    applyHorPadding = false,
                    title = stringResource(R.string.dynamic_colors),
                    subtitle = stringResource(R.string.dynamic_colors_sub),
                    checked = settingsState.isDynamicColors,
                    onClick = { viewModel.toggleDynamicColors() }
                )
                PreferenceRowSwitch(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    applyHorPadding = false,
                    title = stringResource(R.string.amoled_mode),
                    subtitle = stringResource(R.string.amoled_mode_sub),
                    checked = settingsState.isAmoledMode,
                    onClick = {
                        viewModel.updateAmoledMode()
                    }
                )
                PreferenceRow(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    applyHorPadding = false,
                    title = stringResource(R.string.emoji),
                    subtitle = stringResource(R.string.emoji_sub),
                    onClick = onEditEmoji,
                    endContent = {
                        val emoji = LocalSettingsState.current.selectedEmoji
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .offset(x = 7.dp)
                                .container(
                                    shape = CloverShape,
                                    color = MaterialTheme
                                        .colorScheme
                                        .surfaceVariant
                                        .copy(alpha = 0.5f),
                                    borderColor = MaterialTheme.colorScheme.outlineVariant(0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            EmojiItem(
                                emoji = emoji,
                                modifier = Modifier.then(
                                    if (emoji != null) Modifier.scaleOnTap(onRelease = {})
                                    else Modifier
                                ),
                                fontScale = 1f,
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
            }
        }
    }
    item {
        // Secondary Customization
        SettingItem(
            icon = Icons.TwoTone.Palette,
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
                        .container(
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
                        Icon(
                            imageVector = Icons.Outlined.EmojiEmotions,
                            contentDescription = null,
                            modifier = Modifier.padding(
                                top = 16.dp,
                                start = 12.dp
                            )
                        )
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
                    EnhancedSlider(
                        modifier = Modifier
                            .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                            .offset(y = (-2).dp),
                        value = sliderValue,
                        onValueChange = {
                            sliderValue = it.toInt()
                        },
                        onValueChangeFinished = {
                            viewModel.updateEmojisCount(sliderValue)
                        },
                        valueRange = 1f..5f,
                        steps = 3
                        backgroundShape = RoundedCornerShape(12.dp),
                    )
                }
                Column(
                    Modifier
                        .padding(horizontal = 8.dp)
                        .container(
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
                        Icon(
                            imageVector = Icons.Outlined.BorderStyle,
                            contentDescription = null,
                            modifier = Modifier.padding(
                                top = 16.dp,
                                start = 12.dp
                            )
                        )
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
                    EnhancedSlider(
                        modifier = Modifier
                            .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                            .offset(y = (-2).dp),
                        value = sliderValue,
                        onValueChange = {
                            sliderValue = it.roundToTwoDigits()
                        },
                        onValueChangeFinished = {
                            viewModel.setBorderWidth(sliderValue)
                        },
                        backgroundShape = RoundedCornerShape(12.dp),
                        valueRange = 0f..1.5f,
                        steps = 14
                    )
                }
                Box(
                    Modifier
                        .padding(horizontal = 8.dp)
                        .container(
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
                            EnhancedSlider(
                                modifier = Modifier
                                    .padding(
                                        start = 12.dp,
                                        end = 12.dp,
                                        bottom = 4.dp,
                                        top = 4.dp
                                    )
                                    .offset(y = (-2).dp),
                                value = sliderValue,
                                onValueChange = {
                                    sliderValue = it
                                    viewModel.setAlignment(sliderValue)
                                },
                                valueRange = 0f..2f,
                                steps = 1,
                                backgroundShape = RoundedCornerShape(12.dp),
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
        // Font
        val showFontSheet = rememberSaveable { mutableStateOf(false) }
        val showFontScaleSheet = rememberSaveable { mutableStateOf(false) }
        SettingItem(
            icon = Icons.Rounded.TextFormat,
            text = stringResource(R.string.text),
        ) {
            ChangeLanguagePreference(Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp))
            PreferenceItem(
                onClick = { showFontSheet.value = true },
                title = stringResource(R.string.font),
                subtitle = settingsState.font.name ?: stringResource(R.string.system),
                color = MaterialTheme
                    .colorScheme
                    .secondaryContainer
                    .copy(alpha = 0.2f),
                endIcon = Icons.Rounded.FontDownload,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
            Spacer(Modifier.height(8.dp))
            PreferenceItem(
                onClick = { showFontScaleSheet.value = true },
                title = stringResource(R.string.font_scale),
                subtitle = settingsState.fontScale?.takeIf { it > 0 }?.toString() ?: stringResource(
                    R.string.defaultt
                ),
                color = MaterialTheme
                    .colorScheme
                    .secondaryContainer
                    .copy(alpha = 0.2f),
                endIcon = Icons.Rounded.TextFields,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
        SimpleSheet(
            visible = showFontSheet,
            sheetContent = {
                Box {
                    HorizontalDivider(
                        Modifier.zIndex(100f)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .verticalScroll(
                                rememberScrollState()
                            )
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                    ) {
                        UiFontFam.entries.forEach { font ->
                            FontSelectionItem(
                                font = font,
                                onClick = {
                                    viewModel.setFont(font.asDomain())
                                    (context as? Activity)?.recreate()
                                }
                            )
                        }
                    }
                    HorizontalDivider(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .zIndex(100f)
                    )
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = { showFontSheet.value = false },
                    colors = ButtonDefaults.buttonColors(),
                    border = BorderStroke(
                        settingsState.borderWidth,
                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                    )
                ) {
                    AutoSizeText(stringResource(R.string.close))
                }
            },
            title = {
                TitleItem(
                    icon = Icons.Rounded.FontDownload,
                    text = stringResource(R.string.font),
                )
            }
        )

        SimpleSheet(
            visible = showFontScaleSheet,
            sheetContent = {
                val list = remember {
                    List(19) { (0.6f + it / 20f).roundToTwoDigits() }
                }
                Box {
                    HorizontalDivider(
                        Modifier.zIndex(100f)
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(64.dp),
                        verticalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterVertically
                        ),
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterHorizontally
                        ),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = 8.dp
                        )
                    ) {
                        item {
                            val selected = settingsState.fontScale == null
                            Box(
                                modifier = Modifier
                                    .height(58.dp)
                                    .background(
                                        color = if (selected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f),
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .border(
                                        width = LocalSettingsState.current.borderWidth,
                                        color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                            0.7f
                                        )
                                        else MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                            alpha = 0.1f
                                        ),
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable {
                                        viewModel.onUpdateFontScale(0f)
                                        (context as Activity).recreate()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                AutoSizeText(
                                    text = stringResource(id = R.string.system),
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                        list.forEach { scale ->
                            item {
                                val selected = scale == settingsState.fontScale
                                Box(
                                    modifier = Modifier
                                        .size(58.dp)
                                        .background(
                                            color = if (selected) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.secondaryContainer.copy(
                                                alpha = 0.2f
                                            ),
                                            shape = MaterialTheme.shapes.medium
                                        )
                                        .border(
                                            width = LocalSettingsState.current.borderWidth,
                                            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                0.7f
                                            )
                                            else MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                                alpha = 0.1f
                                            ),
                                            shape = MaterialTheme.shapes.medium
                                        )
                                        .clip(MaterialTheme.shapes.medium)
                                        .clickable {
                                            viewModel.onUpdateFontScale(scale)
                                            (context as Activity).recreate()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = scale.toString(),
                                        fontSize = with(LocalDensity.current) {
                                            val textSize = 16 / fontScale * scale
                                            textSize.sp
                                        }
                                    )
                                }
                            }
                        }
                        item(
                            span = {
                                GridItemSpan(maxCurrentLineSpan)
                            }
                        ) {
                            androidx.compose.animation.AnimatedVisibility(
                                visible = context.resources.configuration.fontScale > 1.2f,
                                enter = fadeIn() + expandIn(expandFrom = Alignment.TopCenter),
                                exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.TopCenter)
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                            alpha = 0.7f
                                        ),
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                                    ),
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .border(
                                            settingsState.borderWidth,
                                            MaterialTheme.colorScheme.onErrorContainer.copy(
                                                0.4f
                                            ),
                                            RoundedCornerShape(24.dp)
                                        ),
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.using_large_fonts_warn),
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(8.dp),
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.SemiBold,
                                        lineHeight = 14.sp,
                                        color = LocalContentColor.current.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                    HorizontalDivider(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .zIndex(100f)
                    )
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = { showFontScaleSheet.value = false },
                    colors = ButtonDefaults.buttonColors(),
                    border = BorderStroke(
                        settingsState.borderWidth,
                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                    )
                ) {
                    AutoSizeText(stringResource(R.string.close))
                }
            },
            title = {
                TitleItem(
                    icon = Icons.Rounded.TextFields,
                    text = stringResource(R.string.font_scale),
                )
            }
        )
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
                    .alpha(
                        animateFloatAsState(
                            if (enabled) 1f
                            else 0.5f
                        ).value
                    )
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
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
                endIcon = if (currentFolderUri != null) Icons.Outlined.FolderSpecial else Icons.Rounded.FolderSpecial,
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
                            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                    )
                    PreferenceRowSwitch(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                        applyHorPadding = false,
                        onClick = { viewModel.toggleAddFileSize() },
                        title = stringResource(R.string.add_file_size),
                        subtitle = stringResource(R.string.add_file_size_sub),
                        checked = settingsState.addSizeInFilename
                    )
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
                            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
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
                    PreferenceRowSwitch(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                        applyHorPadding = false,
                        onClick = { viewModel.toggleAddSequenceNumber() },
                        title = stringResource(R.string.replace_sequence_number),
                        subtitle = stringResource(R.string.replace_sequence_number_sub),
                        checked = settingsState.addSequenceNumber
                    )
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

        var showResetDialog by remember { mutableStateOf(false) }
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
                    backupSavingLauncher.launch("*/*#${viewModel.createBackupFilename()}")
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
            Spacer(modifier = Modifier.height(8.dp))
            PreferenceItem(
                onClick = {
                    showResetDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                color = MaterialTheme
                    .colorScheme
                    .errorContainer
                    .copy(alpha = 0.8f),
                title = stringResource(R.string.reset),
                subtitle = stringResource(R.string.reset_settings_sub),
                endIcon = Icons.Rounded.RestartAlt
            )
        }

        ResetDialog(
            visible = showResetDialog,
            onDismiss = {
                showResetDialog = false
            },
            onReset = {
                showResetDialog = false
                viewModel.resetSettings()
            },
            title = stringResource(R.string.reset),
            text = stringResource(R.string.reset_settings_sub)
        )
    }
    if (BuildConfig.FLAVOR != "foss") {
        item {
            // Firebase
            SettingItem(
                icon = Icons.Rounded.Firebase,
                text = stringResource(R.string.firebase),
                initialState = false
            ) {
                PreferenceRowSwitch(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    applyHorPadding = false,
                    resultModifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    title = stringResource(R.string.crashlytics),
                    subtitle = stringResource(id = R.string.crashlytics_sub),
                    startContent = {
                        Icon(
                            Icons.Rounded.Crashlytics,
                            null,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    },
                    checked = settingsState.allowCollectCrashlytics,
                    onClick = {
                        viewModel.toggleAllowCollectCrashlytics()
                    }
                )
                Spacer(Modifier.height(8.dp))
                PreferenceRowSwitch(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    applyHorPadding = false,
                    resultModifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    title = stringResource(R.string.analytics),
                    subtitle = stringResource(id = R.string.analytics_sub),
                    startContent = {
                        Icon(
                            imageVector = Icons.Rounded.Analytics,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    },
                    checked = settingsState.allowCollectAnalytics,
                    onClick = {
                        viewModel.toggleAllowCollectAnalytics()
                    }
                )
            }
        }
    }
    item {
        // Contact me
        SettingItem(
            icon = Icons.Rounded.PersonSearch,
            text = stringResource(R.string.contact_me),
            initialState = false
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
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.9f),
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
                    onClick = onShowAuthor
                )
            }
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
                            painter = painterResource(R.drawable.ic_launcher_monochrome),
                            contentDescription = null,
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
                                .container(
                                    resultPadding = 0.dp,
                                    color = animateColorAsState(
                                        if (settingsState.isNightMode) {
                                            MaterialTheme.colorScheme.background.blend(
                                                Color.White,
                                                0.1f
                                            )
                                        } else {
                                            MaterialTheme.colorScheme.primaryContainer
                                        }
                                    ).value,
                                    borderColor = MaterialTheme.colorScheme.outlineVariant(),
                                    shape = DavidStarShape
                                )
                                .scale(1.25f)
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
                    color = MaterialTheme.colorScheme.mixedColor.copy(alpha = 0.7f),
                    contentColor = MaterialTheme.colorScheme.onMixedColor.copy(alpha = 0.9f),
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
        Spacer(modifier = Modifier.height(8.dp))
    }
    item { Spacer(Modifier.height(8.dp)) }
}