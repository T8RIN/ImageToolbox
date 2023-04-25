package ru.tech.imageresizershrinker.main_screen.components


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.calculateSecondaryColor
import com.t8rin.dynamic.theme.calculateSurfaceColor
import com.t8rin.dynamic.theme.calculateTertiaryColor
import com.t8rin.dynamic.theme.getAppColorTuple
import com.t8rin.dynamic.theme.rememberColorScheme
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.theme.PaletteSwatch
import ru.tech.imageresizershrinker.theme.outlineVariant

@ExperimentalMaterial3Api
@Composable
fun ColorDialog(
    modifier: Modifier = Modifier,
    colorTuple: ColorTuple,
    title: String = stringResource(R.string.color_scheme),
    onColorChange: (ColorTuple) -> Unit,
    onDismissRequest: () -> Unit
) {
    var primary by rememberSaveable { mutableStateOf(colorTuple.primary.toArgb()) }
    var secondary by rememberSaveable {
        mutableStateOf(
            colorTuple.secondary?.toArgb() ?: colorTuple.primary.calculateSecondaryColor()
        )
    }
    var tertiary by rememberSaveable {
        mutableStateOf(
            colorTuple.tertiary?.toArgb() ?: colorTuple.primary.calculateTertiaryColor()
        )
    }

    var surface by rememberSaveable {
        mutableStateOf(
            colorTuple.surface?.toArgb() ?: colorTuple.primary.calculateSurfaceColor()
        )
    }

    val appColorTuple = getAppColorTuple(
        defaultColorTuple = LocalAppColorTuple.current,
        dynamicColor = true,
        darkTheme = true
    )

    val scheme = rememberColorScheme(
        amoledMode = false,
        isDarkTheme = true,
        colorTuple = appColorTuple
    )

    AlertDialog(
        modifier = modifier,
        onDismissRequest = {},
        title = { Text(title) },
        icon = { Icon(Icons.Outlined.Palette, null) },
        text = {
            Box {
                Divider(
                    Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(100f)
                )

                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
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
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = BorderStroke(
                                LocalBorderWidth.current,
                                MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.surfaceVariant)
                            )
                        ) {
                            Icon(Icons.Rounded.ContentPaste, null)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Divider()
                    Spacer(Modifier.height(8.dp))
                    TitleItem(text = stringResource(R.string.primary))
                    ColorCustomComponent(
                        color = primary,
                        onColorChange = {
                            primary = it
                            secondary = Color(it).calculateSecondaryColor()
                            tertiary = Color(it).calculateTertiaryColor()
                            surface = Color(it).calculateSurfaceColor()
                        }
                    )
                    Divider()
                    TitleItem(text = stringResource(R.string.secondary))
                    ColorCustomComponent(
                        color = secondary,
                        onColorChange = {
                            secondary = it
                        }
                    )
                    Divider()
                    TitleItem(text = stringResource(R.string.tertiary))
                    ColorCustomComponent(
                        color = tertiary,
                        onColorChange = {
                            tertiary = it
                        }
                    )
                    Divider()
                    TitleItem(text = stringResource(R.string.surface))
                    ColorCustomComponent(
                        color = surface,
                        onColorChange = {
                            surface = it
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                }

                Divider(
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
                    LocalBorderWidth.current,
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
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            OutlinedButton(
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                border = BorderStroke(
                    LocalBorderWidth.current,
                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                ),
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun ColorCustomComponent(
    color: Int,
    onColorChange: (Int) -> Unit,
) {
    Column {
        ColorCustomInfoComponent(
            color = color,
            onColorChange = onColorChange,
        )
        ColorCustomControlComponent(
            color = color,
            onColorChange = onColorChange
        )
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


@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ColorCustomInfoComponent(
    color: Int,
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
                        LocalBorderWidth.current,
                        MaterialTheme.colorScheme.outlineVariant(onTopOf = Color(color))
                    ),
                    MaterialTheme.shapes.medium,
                ),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.elevatedCardColors(containerColor = Color(color)),
        ) {}

        Card(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .padding(start = 16.dp)
                .border(
                    BorderStroke(
                        LocalBorderWidth.current,
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
                        .padding(start = 16.dp)
                        .padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (error) {
                        Text(
                            modifier = Modifier.wrapContentWidth(),
                            text = colorPasteError.value ?: "",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    } else {
                        Text(
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
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = onCopyCustomColor) {
                            Icon(Icons.Rounded.ContentCopy, null)
                        }
                        IconButton(onClick = onPasteCustomColor) {
                            Icon(Icons.Rounded.ContentPaste, null)
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
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                        alpha = if (LocalNightMode.current.isNightMode()) 0.5f
                                        else 1f
                                    ),
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                ),
                                border = BorderStroke(
                                    LocalBorderWidth.current,
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

@Composable
private fun ColorCustomControlComponent(
    color: Int,
    onColorChange: (Int) -> Unit
) {
    val redValue = remember(color) { mutableStateOf(color.red) }
    val greenValue = remember(color) { mutableStateOf(color.green) }
    val blueValue = remember(color) { mutableStateOf(color.blue) }

    val newColor by remember(redValue.value, greenValue.value, blueValue.value) {
        mutableStateOf(Color(redValue.value, greenValue.value, blueValue.value))
    }

    LaunchedEffect(newColor) { onColorChange.invoke(newColor.toArgb()) }

    val colorItemLabelWidth = remember { mutableStateOf<Int?>(null) }
    val colorValueLabelWidth = remember { mutableStateOf<Int?>(null) }

    val colorItems = mutableListOf(
        stringResource(R.string.color_red) to redValue,
        stringResource(R.string.color_green) to greenValue,
        stringResource(R.string.color_blue) to blueValue
    )

    Column(modifier = Modifier.padding(top = 12.dp)) {
        colorItems.forEach { entry ->
            ColorCustomControlItemComponent(
                label = entry.first,
                value = entry.second.value,
                onValueChange = { entry.second.value = it },
                colorItemLabelWidth = colorItemLabelWidth,
                colorValueLabelWidth = colorValueLabelWidth
            )
        }
    }
}

@Composable
private fun ColorCustomControlItemComponent(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    colorItemLabelWidth: MutableState<Int?>,
    colorValueLabelWidth: MutableState<Int?>,
) {
    val wrapOrFixedModifier: @Composable (MutableState<Int?>) -> Modifier = { stateWidth ->
        val defaultModifier = Modifier
            .wrapContentWidth()
            .onGloballyPositioned { coordinates ->
                if ((stateWidth.value ?: 0) < coordinates.size.width) {
                    stateWidth.value = coordinates.size.width
                }
            }
        stateWidth.value?.let {
            Modifier.width(LocalDensity.current.run { it.toDp() + 4.dp })
        } ?: defaultModifier
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = wrapOrFixedModifier(colorItemLabelWidth),
            text = label,
            style = MaterialTheme.typography.labelMedium,
        )

        Slider(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            valueRange = 0f..255f,
            value = animateFloatAsState(targetValue = value.toFloat()).value,
            onValueChange = { onValueChange(it.toInt()) },
        )

        Text(
            modifier = Modifier.width(32.dp),
            text = value.toString(),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.End,
        )
    }
}