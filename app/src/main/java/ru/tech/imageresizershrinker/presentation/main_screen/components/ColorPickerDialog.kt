package ru.tech.imageresizershrinker.presentation.main_screen.components


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.smarttoolfactory.colordetector.util.ColorUtil
import com.smarttoolfactory.colorpicker.selector.SelectorRectSaturationValueHSV
import com.smarttoolfactory.colorpicker.slider.SliderAlphaHSL
import com.smarttoolfactory.colorpicker.slider.SliderHueHSV
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.ColorTupleItem
import com.t8rin.dynamic.theme.calculateSecondaryColor
import com.t8rin.dynamic.theme.calculateSurfaceColor
import com.t8rin.dynamic.theme.calculateTertiaryColor
import com.t8rin.dynamic.theme.getAppColorTuple
import com.t8rin.dynamic.theme.rememberColorScheme
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.theme.defaultColorTuple
import ru.tech.imageresizershrinker.presentation.root.theme.icons.CreateAlt
import ru.tech.imageresizershrinker.presentation.root.theme.icons.PaletteSwatch
import ru.tech.imageresizershrinker.presentation.root.theme.inverse
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ListUtils.nearestFor
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.alertDialog
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import kotlin.random.Random

@ExperimentalMaterial3Api
@Composable
fun ColorPickerDialog(
    visible: MutableState<Boolean>,
    colorTuple: ColorTuple,
    borderWidth: Dp = LocalSettingsState.current.borderWidth,
    title: String = stringResource(R.string.color_scheme),
    onColorChange: (ColorTuple) -> Unit
) {
    var primary by rememberSaveable(colorTuple) { mutableIntStateOf(colorTuple.primary.toArgb()) }
    var secondary by rememberSaveable(colorTuple) {
        mutableIntStateOf(
            colorTuple.secondary?.toArgb() ?: colorTuple.primary.calculateSecondaryColor()
        )
    }
    var tertiary by rememberSaveable(colorTuple) {
        mutableIntStateOf(
            colorTuple.tertiary?.toArgb() ?: colorTuple.primary.calculateTertiaryColor()
        )
    }

    var surface by rememberSaveable(colorTuple) {
        mutableIntStateOf(
            colorTuple.surface?.toArgb() ?: colorTuple.primary.calculateSurfaceColor()
        )
    }

    val appColorTuple = getAppColorTuple(
        defaultColorTuple = LocalSettingsState.current.appColorTuple,
        dynamicColor = true,
        darkTheme = true
    )

    val scheme = rememberColorScheme(
        amoledMode = false,
        isDarkTheme = true,
        colorTuple = appColorTuple
    )

    LaunchedEffect(visible.value) {
        if (!visible.value) {
            delay(2500)
            primary = colorTuple.primary.toArgb()
            secondary = colorTuple.secondary?.toArgb()
                ?: colorTuple.primary.calculateSecondaryColor()
            tertiary =
                colorTuple.tertiary?.toArgb()
                    ?: colorTuple.primary.calculateTertiaryColor()
            surface =
                colorTuple.surface?.toArgb()
                    ?: colorTuple.primary.calculateSurfaceColor()
        }
    }

    SimpleSheet(
        visible = visible,
        title = { TitleItem(text = title, icon = Icons.Outlined.Palette, modifier = Modifier) },
        endConfirmButtonPadding = 0.dp,
        sheetContent = {
            HorizontalDivider()
            Box {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(260.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item(
                        span = {
                            GridItemSpan(maxCurrentLineSpan)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .block(RoundedCornerShape(24.dp))
                                .padding(16.dp)
                        ) {
                            Icon(Icons.Rounded.PaletteSwatch, null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.monet_colors))
                            Spacer(Modifier.width(8.dp))
                            Spacer(Modifier.weight(1f))
                            OutlinedButton(
                                onClick = {
                                    scheme.apply {
                                        primary = this.primary.toArgb()
                                        secondary = this.secondary.toArgb()
                                        tertiary = this.tertiary.toArgb()
                                        surface = this.surface.toArgb()
                                    }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                border = BorderStroke(
                                    borderWidth,
                                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                                )
                            ) {
                                Icon(Icons.Rounded.ContentPaste, null)
                            }
                        }
                    }
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .block(RoundedCornerShape(24.dp))
                                .padding(horizontal = 20.dp)
                        ) {
                            TitleItem(text = stringResource(R.string.primary))
                            ColorSelection(
                                color = primary,
                                onColorChange = {
                                    if (primary != it) {
                                        secondary = Color(it).calculateSecondaryColor()
                                        tertiary = Color(it).calculateTertiaryColor()
                                        surface = Color(it).calculateSurfaceColor()
                                    }
                                    primary = it
                                }
                            )
                        }
                    }
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .block(RoundedCornerShape(24.dp))
                                .padding(horizontal = 20.dp)
                        ) {
                            TitleItem(text = stringResource(R.string.secondary))
                            ColorSelection(
                                color = secondary,
                                onColorChange = {
                                    secondary = it
                                }
                            )
                        }
                    }
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .block(RoundedCornerShape(24.dp))
                                .padding(horizontal = 20.dp)
                        ) {
                            TitleItem(text = stringResource(R.string.tertiary))
                            ColorSelection(
                                color = tertiary,
                                onColorChange = {
                                    tertiary = it
                                }
                            )
                        }
                    }
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .block(RoundedCornerShape(24.dp))
                                .padding(horizontal = 20.dp)
                        ) {
                            TitleItem(text = stringResource(R.string.surface))
                            ColorSelection(
                                color = surface,
                                onColorChange = {
                                    surface = it
                                }
                            )
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
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                border = BorderStroke(
                    borderWidth,
                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                ),
                onClick = {
                    onColorChange(
                        ColorTuple(
                            Color(primary),
                            Color(secondary),
                            Color(tertiary),
                            Color(surface)
                        )
                    )
                    visible.value = false
                }
            ) {
                AutoSizeText(stringResource(R.string.save))
            }
        },
    )
}

@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun AvailableColorTuplesDialog(
    visible: MutableState<Boolean>,
    colorTupleList: List<ColorTuple>,
    currentColorTuple: ColorTuple,
    openColorPicker: () -> Unit,
    borderWidth: Dp = LocalSettingsState.current.borderWidth,
    colorPicker: @Composable (onUpdateColorTuples: (List<ColorTuple>) -> Unit) -> Unit,
    onPickTheme: (ColorTuple) -> Unit,
    onUpdateColorTuples: (List<ColorTuple>) -> Unit,
) {
    val showEditColorPicker = rememberSaveable { mutableStateOf(false) }

    SimpleSheet(
        visible = visible,
        endConfirmButtonPadding = 0.dp,
        title = {
            var showConfirmDeleteDialog by remember { mutableStateOf(false) }
            val settingsState = LocalSettingsState.current

            if (showConfirmDeleteDialog) {
                AlertDialog(
                    modifier = Modifier.alertDialog(),
                    onDismissRequest = { showConfirmDeleteDialog = false },
                    confirmButton = {
                        OutlinedButton(
                            colors = ButtonDefaults.buttonColors(),
                            border = BorderStroke(
                                settingsState.borderWidth,
                                MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                            ),
                            onClick = { showConfirmDeleteDialog = false }
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            colors = ButtonDefaults.filledTonalButtonColors(),
                            border = BorderStroke(
                                settingsState.borderWidth,
                                MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                            ),
                            onClick = {
                                showConfirmDeleteDialog = false
                                if ((colorTupleList - currentColorTuple).isEmpty()) {
                                    onPickTheme(defaultColorTuple)
                                } else {
                                    colorTupleList.nearestFor(currentColorTuple)
                                        ?.let { onPickTheme(it) }
                                }
                                onUpdateColorTuples(colorTupleList - currentColorTuple)
                            }
                        ) {
                            Text(stringResource(R.string.delete))
                        }
                    },
                    title = {
                        Text(stringResource(R.string.delete_color_scheme_title))
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null
                        )
                    },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            ColorTupleItem(
                                colorTuple = currentColorTuple,
                                modifier = Modifier
                                    .padding(2.dp)
                                    .size(64.dp)
                                    .border(
                                        borderWidth,
                                        MaterialTheme.colorScheme.outlineVariant(
                                            0.2f
                                        ),
                                        MaterialTheme.shapes.medium
                                    )
                                    .clip(MaterialTheme.shapes.medium),
                                backgroundColor = rememberColorScheme(
                                    LocalSettingsState.current.isNightMode,
                                    LocalSettingsState.current.isDynamicColors,
                                    currentColorTuple
                                ).surfaceVariant.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stringResource(R.string.delete_color_scheme_warn))
                        }
                    }
                )
            }
            Row {
                OutlinedButton(
                    onClick = {
                        showConfirmDeleteDialog = true
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    ),
                    border = BorderStroke(
                        borderWidth,
                        MaterialTheme.colorScheme.outlineVariant(
                            onTopOf = MaterialTheme.colorScheme.errorContainer
                        )
                    ),
                ) {
                    Icon(Icons.Rounded.Delete, null)
                }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(
                    onClick = {
                        showEditColorPicker.value = true
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                    border = BorderStroke(
                        borderWidth,
                        MaterialTheme.colorScheme.outlineVariant(
                            onTopOf = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ),
                ) {
                    Icon(Icons.Rounded.CreateAlt, null)
                }
            }
        },
        sheetContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TitleItem(
                    text = stringResource(R.string.color_scheme),
                    icon = Icons.Rounded.PaletteSwatch
                )
            }
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                HorizontalDivider(
                    Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(100f)
                )
                FlowRow(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 16.dp, horizontal = 2.dp)
                ) {
                    colorTupleList.forEach { colorTuple ->
                        ColorTupleItem(
                            colorTuple = colorTuple, modifier = Modifier
                                .padding(2.dp)
                                .size(64.dp)
                                .border(
                                    borderWidth,
                                    MaterialTheme.colorScheme.outlineVariant(
                                        0.2f
                                    ),
                                    MaterialTheme.shapes.medium
                                )
                                .clip(MaterialTheme.shapes.medium)
                                .combinedClickable(
                                    onClick = {
                                        onPickTheme(colorTuple)
                                    },
                                ),
                            backgroundColor = rememberColorScheme(
                                LocalSettingsState.current.isNightMode,
                                LocalSettingsState.current.isDynamicColors,
                                colorTuple
                            ).surfaceVariant.copy(alpha = 0.8f)
                        ) {
                            AnimatedContent(
                                targetState = colorTuple == currentColorTuple
                            ) { selected ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    if (selected) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(
                                                    animateColorAsState(
                                                        colorTuple.primary.inverse(
                                                            fraction = { cond ->
                                                                if (cond) 0.8f
                                                                else 0.5f
                                                            },
                                                            darkMode = colorTuple.primary.luminance() < 0.3f
                                                        )
                                                    ).value,
                                                    CircleShape
                                                ),
                                        )
                                        Icon(
                                            imageVector = Icons.Rounded.Done,
                                            contentDescription = null,
                                            tint = colorTuple.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    ColorTupleItem(
                        colorTuple = ColorTuple(
                            primary = MaterialTheme.colorScheme.secondary,
                            secondary = MaterialTheme.colorScheme.secondary,
                            tertiary = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier
                            .padding(2.dp)
                            .size(64.dp)
                            .border(
                                borderWidth,
                                MaterialTheme.colorScheme.outlineVariant(
                                    0.2f
                                ),
                                MaterialTheme.shapes.medium
                            )
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { openColorPicker() },
                        backgroundColor = MaterialTheme
                            .colorScheme
                            .surfaceVariant
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddCircleOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(24.dp)
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
                onClick = {
                    visible.value = false
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                border = BorderStroke(
                    borderWidth,
                    MaterialTheme.colorScheme.outlineVariant(
                        onTopOf = MaterialTheme.colorScheme.primary
                    )
                ),
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
    )
    ColorPickerDialog(
        visible = showEditColorPicker,
        colorTuple = currentColorTuple,
        onColorChange = {
            onUpdateColorTuples(colorTupleList + it - currentColorTuple)
            onPickTheme(it)
        }
    )
    colorPicker(onUpdateColorTuples)
}

@Composable
fun ColorSelection(
    color: Int,
    onColorChange: (Int) -> Unit,
) {
    val color = Color(color)
    val hsv = ColorUtil.colorToHSV(color)
    var hue by remember { mutableFloatStateOf(hsv[0]) }
    val saturation = hsv[1]
    val value = hsv[2]

    Column {
        ColorInfo(
            color = color.toArgb(),
            onColorChange = onColorChange,
        )
        Spacer(Modifier.height(16.dp))
        SelectorRectSaturationValueHSV(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .block(RoundedCornerShape(2.dp), applyResultPadding = false)
                .clip(RoundedCornerShape(3.dp)),
            hue = hue,
            saturation = saturation,
            value = value
        ) { s, v ->
            onColorChange(
                Color.hsv(hue, s, v).toArgb()
            )
        }
        Spacer(Modifier.height(16.dp))
        SliderHueHSV(
            hue = hue,
            saturation = saturation,
            value = value,
            onValueChange = { h ->
                hue = h
                onColorChange(
                    Color.hsv(hue, saturation, value).toArgb()
                )
            },
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                )
                .height(40.dp)
                .border(
                    width = LocalSettingsState.current.borderWidth,
                    color = MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.surfaceVariant),
                    shape = CircleShape
                )
                .padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LaunchedEffect(color) {
            if (hue != hsv[0]) hue = hsv[0]
        }
    }
}

@Composable
fun AlphaColorSelection(
    color: Int,
    onColorChange: (Int) -> Unit,
) {
    val color = Color(color)
    val hsv = ColorUtil.colorToHSV(color)
    var hue by remember { mutableFloatStateOf(hsv[0]) }
    var alpha by remember { mutableFloatStateOf(color.alpha) }
    val saturation = hsv[1]
    val value = hsv[2]

    Column {
        ColorInfo(
            color = color.toArgb(),
            onColorChange = {
                onColorChange(Color(it).toArgb())
            },
        )
        Spacer(Modifier.height(16.dp))
        SelectorRectSaturationValueHSV(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .block(RoundedCornerShape(2.dp), applyResultPadding = false)
                .clip(RoundedCornerShape(3.dp)),
            hue = hue,
            saturation = saturation,
            value = value
        ) { s, v ->
            val c = Color.hsv(hue, s, v, alpha).toArgb()
            onColorChange(c)
        }
        Spacer(Modifier.height(16.dp))
        SliderHueHSV(
            hue = hue,
            saturation = saturation,
            value = value,
            onValueChange = { h ->
                hue = h
                val c = Color.hsv(h, saturation, value, alpha).toArgb()
                onColorChange(c)
            },
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                )
                .height(40.dp)
                .border(
                    width = LocalSettingsState.current.borderWidth,
                    color = MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.surfaceVariant),
                    shape = CircleShape
                )
                .padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        SliderAlphaHSL(
            hue = hue,
            alpha = alpha,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                )
                .height(40.dp)
                .border(
                    width = LocalSettingsState.current.borderWidth,
                    color = MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.surfaceVariant),
                    shape = CircleShape
                )
                .padding(horizontal = 10.dp),
            onValueChange = {
                alpha = it
                val c = Color.hsv(hue, saturation, value, alpha).toArgb()
                onColorChange(c)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        LaunchedEffect(color) {
            if (hue != hsv[0]) hue = hsv[0]
            if (alpha != color.alpha) alpha = color.alpha
        }
    }
}

/** Save a text into the clipboard. */
fun Context.copyColorIntoClipboard(label: String, value: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, value)
    clipboard.setPrimaryClip(clip)
}

/** Receive the clipboard data. */
fun Context.pasteColorFromClipboard(
    onPastedColor: (Int) -> Unit,
    onPastedColorFailure: (String) -> Unit,
) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val item = clipboard.primaryClip?.getItemAt(0)
    val text = item?.text?.toString()
    text?.let {
        runCatching {
            onPastedColor(android.graphics.Color.parseColor(it))
        }.getOrElse {
            onPastedColorFailure(getString(R.string.clipboard_paste_invalid_color_code))
        }
    } ?: run {
        onPastedColorFailure(getString(R.string.clipboard_paste_invalid_empty))
    }
}

/** Receive the clipboard data. */
fun getFormattedColor(color: Int): String =
    String.format("#%08X", (0xFFFFFFFF and color.toLong())).replace("#FF", "#")


@Composable
private fun ColorInfo(
    color: Int,
    borderWidth: Dp = LocalSettingsState.current.borderWidth,
    onColorChange: (Int) -> Unit,
) {
    val context = LocalContext.current
    val colorPasteError = rememberSaveable { mutableStateOf<String?>(null) }
    val onCopyCustomColor = {
        context.copyColorIntoClipboard(
            label = context.getString(R.string.color),
            value = getFormattedColor(color)
        )
    }
    val onPasteCustomColor = {
        context.pasteColorFromClipboard(
            onPastedColor = { onColorChange(it) },
            onPastedColorFailure = { colorPasteError.value = it },
        )
    }
    LaunchedEffect(colorPasteError.value) {
        delay(1500)
        colorPasteError.value = null
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Card(
            modifier = Modifier
                .size(56.dp)
                .border(
                    BorderStroke(
                        borderWidth,
                        MaterialTheme.colorScheme.outlineVariant(onTopOf = Color(color))
                    ),
                    MaterialTheme.shapes.medium,
                ),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.elevatedCardColors(containerColor = Color(color)),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        onColorChange(
                            Color(Random.nextInt()).copy(alpha = Color(color).alpha).toArgb()
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shuffle,
                        contentDescription = null,
                        tint = animateColorAsState(
                            Color(color).inverse(
                                fraction = { cond ->
                                    if (cond) 0.8f
                                    else 0.5f
                                },
                                darkMode = Color(color).luminance() < 0.3f
                            )
                        ).value
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
                .padding(start = 16.dp)
                .border(
                    BorderStroke(
                        borderWidth,
                        MaterialTheme.colorScheme.outlineVariant()
                    ),
                    MaterialTheme.shapes.medium,
                )
        ) {
            AnimatedContent(
                colorPasteError.value != null
            ) { error ->
                var expanded by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 8.dp)
                        .padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (error) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = colorPasteError.value ?: "",
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Row(modifier = Modifier.weight(1f)) {
                            AutoSizeText(
                                text = getFormattedColor(color),
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        expanded = true
                                    }
                                    .padding(4.dp)
                            )
                        }
                        Row(Modifier.width(80.dp)) {
                            IconButton(onClick = onCopyCustomColor) {
                                Icon(Icons.Rounded.ContentCopy, null)
                            }
                            IconButton(onClick = onPasteCustomColor) {
                                Icon(Icons.Rounded.ContentPaste, null)
                            }
                        }
                    }
                }
                if (expanded) {
                    var value by remember { mutableStateOf(getFormattedColor(color)) }
                    AlertDialog(
                        modifier = Modifier.alertDialog(),
                        onDismissRequest = { expanded = false },
                        icon = {
                            Icon(Icons.Outlined.Palette, null)
                        },
                        title = {
                            Text(stringResource(R.string.color))
                        },
                        text = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                OutlinedTextField(
                                    shape = RoundedCornerShape(16.dp),
                                    value = value,
                                    textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
                                    maxLines = 1,
                                    onValueChange = { colorString ->
                                        val newValue =
                                            (colorString + "0" * (7 - colorString.length)).take(7)
                                        if (newValue.matches(Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"))) {
                                            value = newValue
                                        }
                                    }
                                )
                            }
                        },
                        confirmButton = {
                            OutlinedButton(
                                onClick = {
                                    val hexColor =
                                        value.removePrefix("#").toLong(16) or 0x00000000FF000000
                                    onColorChange(Color(hexColor).toArgb())
                                    expanded = false
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                ),
                                border = BorderStroke(
                                    borderWidth,
                                    MaterialTheme.colorScheme.outlineVariant(
                                        onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ),
                            ) {
                                Text(stringResource(R.string.ok))
                            }
                        }
                    )
                }
            }
        }
    }
}

private operator fun String.times(i: Int): String {
    var s = ""
    repeat(i) {
        s += this
    }
    return s
}