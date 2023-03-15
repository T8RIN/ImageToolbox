package ru.tech.imageresizershrinker.main_screen.components


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.R

@ExperimentalMaterial3Api
@Composable
fun ColorDialog(
    color: Color?,
    title: String = stringResource(R.string.color_scheme),
    onColorChange: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    var _color by rememberSaveable { mutableStateOf(color?.toArgb() ?: Color.Red.toArgb()) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        icon = { Icon(Icons.Outlined.Palette, null) },
        text = {
            Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                MaterialTheme(shapes = Shapes()) {
                    ColorCustomComponent(
                        color = _color,
                        onColorChange = { _color = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onColorChange(_color)
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            FilledTonalButton(onClick = onDismissRequest) {
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

        ElevatedCard(
            modifier = Modifier
                .size(56.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.elevatedCardColors(containerColor = Color(color)),
        ) {}

        ElevatedCard(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp)
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.padding(start = 0.dp)) {
                    if (colorPasteError.value != null) {
                        Text(
                            modifier = Modifier.wrapContentWidth(),
                            text = colorPasteError.value ?: "",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.argb),
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1
                        )
                        Text(
                            modifier = Modifier.padding(top = 2.dp),
                            text = getFormattedColor(color),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1
                        )
                    }
                }
                if (colorPasteError.value == null) {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onCopyCustomColor) {
                        Icon(Icons.Rounded.ContentCopy, null)
                    }
                    IconButton(onClick = onPasteCustomColor) {
                        Icon(Icons.Rounded.ContentCopy, null)
                    }
                }
            }
        }
    }
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
            Modifier.width(LocalDensity.current.run { it.toDp() })
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
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
        )

        Text(
            modifier = wrapOrFixedModifier(colorValueLabelWidth),
            text = value.toString(),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.End,
        )
    }
}