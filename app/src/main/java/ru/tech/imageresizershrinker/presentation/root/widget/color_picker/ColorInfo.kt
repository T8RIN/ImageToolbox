package ru.tech.imageresizershrinker.presentation.root.widget.color_picker

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.theme.inverse
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ContextUtils.copyColorIntoClipboard
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ContextUtils.pasteColorFromClipboard
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.alertDialogBorder
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import kotlin.random.Random

@Composable
fun ColorInfo(
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
                        modifier = Modifier.alertDialogBorder(),
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

/** Receive the clipboard data. */
private fun getFormattedColor(color: Int): String =
    String.format("#%08X", (0xFFFFFFFF and color.toLong())).replace("#FF", "#")

private operator fun String.times(i: Int): String {
    var s = ""
    repeat(i) {
        s += this
    }
    return s
}