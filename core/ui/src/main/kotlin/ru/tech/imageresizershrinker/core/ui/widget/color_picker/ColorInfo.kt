/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.core.ui.widget.color_picker

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.colorToHex
import com.smarttoolfactory.colordetector.util.ColorUtil.colorToHexAlpha
import com.smarttoolfactory.colordetector.util.HexUtil
import com.smarttoolfactory.colorpicker.util.HexVisualTransformation
import com.smarttoolfactory.colorpicker.util.hexRegexSingleChar
import com.smarttoolfactory.colorpicker.util.hexWithAlphaRegex
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.inverse
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.copyToClipboard
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.pasteColorFromClipboard
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.alertDialogBorder
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import kotlin.random.Random

@Composable
fun ColorInfo(
    color: Int,
    onColorChange: (Int) -> Unit,
    onSupportButtonClick: () -> Unit = {
        onColorChange(
            Color(Random.nextInt()).copy(alpha = Color(color).alpha).toArgb()
        )
    },
    supportButtonIcon: ImageVector = Icons.Rounded.Shuffle,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colorPasteError = rememberSaveable { mutableStateOf<String?>(null) }
    val onCopyCustomColor = {
        context.copyToClipboard(
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
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .size(56.dp)
                .container(
                    shape = MaterialTheme.shapes.medium,
                    color = Color(color),
                    resultPadding = 0.dp
                )
                .transparencyChecker()
                .background(Color(color), MaterialTheme.shapes.medium),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EnhancedIconButton(
                    containerColor = Color.Transparent,
                    contentColor = LocalContentColor.current,
                    enableAutoShadowAndBorder = false,
                    onClick = onSupportButtonClick
                ) {
                    Icon(
                        imageVector = supportButtonIcon,
                        contentDescription = null,
                        tint = animateColorAsState(
                            Color(color).inverse(
                                fraction = { cond ->
                                    if (cond) 0.8f
                                    else 0.5f
                                },
                                darkMode = Color(color).luminance() < 0.3f
                            )
                        ).value,
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = Color(color).copy(alpha = 1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(2.dp)
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
                .padding(start = 16.dp)
                .container(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainer
                ),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface
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
                            EnhancedIconButton(
                                containerColor = Color.Transparent,
                                contentColor = LocalContentColor.current,
                                enableAutoShadowAndBorder = false,
                                onClick = onCopyCustomColor
                            ) {
                                Icon(Icons.Rounded.ContentCopy, null)
                            }
                            EnhancedIconButton(
                                containerColor = Color.Transparent,
                                contentColor = LocalContentColor.current,
                                enableAutoShadowAndBorder = false,
                                onClick = onPasteCustomColor
                            ) {
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
                                    textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
                                    maxLines = 1,
                                    value = value.removePrefix("#"),
                                    visualTransformation = HexVisualTransformation(true),
                                    onValueChange = {
                                        if (it.length <= 8) {
                                            var validHex = true

                                            for (index in it.indices) {
                                                validHex =
                                                    hexRegexSingleChar.matches(it[index].toString())
                                                if (!validHex) break
                                            }

                                            if (validHex) {
                                                value = "#${it.uppercase()}"
                                            }
                                        }
                                    }
                                )
                            }
                        },
                        confirmButton = {
                            EnhancedButton(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                onClick = {
                                    if (hexWithAlphaRegex.matches(value)) {
                                        onColorChange(HexUtil.hexToColor(value).toArgb())
                                    }
                                    expanded = false
                                }
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
private fun getFormattedColor(color: Int): String {
    return if (Color(color).alpha == 1f) {
        colorToHex(Color(color))
    } else {
        colorToHexAlpha(Color(color))
    }.uppercase()
}

private operator fun String.times(i: Int): String {
    var s = ""
    repeat(i) {
        s += this
    }
    return s
}