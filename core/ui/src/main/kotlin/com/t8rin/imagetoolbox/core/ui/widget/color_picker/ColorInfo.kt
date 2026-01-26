/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.ui.widget.color_picker

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.pasteColorFromClipboard
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun ColorInfo(
    color: Color,
    onColorChange: (Color) -> Unit,
    onSupportButtonClick: () -> Unit = {
        onColorChange(
            Color(Random.nextInt()).copy(alpha = color.alpha)
        )
    },
    supportButtonIcon: ImageVector = Icons.Rounded.Shuffle,
    modifier: Modifier = Modifier,
    infoContainerColor: Color = Color.Unspecified,
) {
    val context = LocalContext.current
    val colorPasteError = rememberSaveable { mutableStateOf<String?>(null) }
    val essentials = rememberLocalEssentials()
    val onCopyCustomColor = {
        essentials.copyToClipboard(
            text = getFormattedColor(color),
            message = R.string.color_copied
        )
    }
    val onPasteCustomColor = {
        context.pasteColorFromClipboard(
            onPastedColor = onColorChange,
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
                    color = color,
                    resultPadding = 0.dp
                )
                .transparencyChecker()
                .background(
                    color = color,
                    shape = MaterialTheme.shapes.medium
                ),
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
                    onClick = onSupportButtonClick
                ) {
                    Icon(
                        imageVector = supportButtonIcon,
                        contentDescription = stringResource(R.string.edit),
                        tint = animateColorAsState(
                            color.inverse(
                                fraction = { cond ->
                                    if (cond) 0.8f
                                    else 0.5f
                                },
                                darkMode = color.luminance() < 0.3f
                            )
                        ).value,
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = color.copy(alpha = 1f),
                                shape = ShapeDefaults.mini
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
                    color = infoContainerColor
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
                                    .clip(ShapeDefaults.pressed)
                                    .hapticsClickable {
                                        expanded = true
                                    }
                                    .padding(4.dp)
                            )
                        }
                        Row(Modifier.width(80.dp)) {
                            EnhancedIconButton(
                                onClick = onCopyCustomColor
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ContentCopy,
                                    contentDescription = stringResource(R.string.copy)
                                )
                            }
                            EnhancedIconButton(
                                onClick = onPasteCustomColor
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ContentPaste,
                                    contentDescription = stringResource(R.string.pastel)
                                )
                            }
                        }
                    }
                }
                var value by remember(expanded) { mutableStateOf(getFormattedColor(color)) }
                EnhancedAlertDialog(
                    visible = expanded,
                    onDismissRequest = { expanded = false },
                    icon = {
                        val hexColorInt by remember(value) {
                            derivedStateOf {
                                if (hexWithAlphaRegex.matches(value)) {
                                    HexUtil.hexToColor(value).toArgb()
                                } else null
                            }
                        }
                        AnimatedContent(hexColorInt) { colorFromHex ->
                            if (colorFromHex != null) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .container(
                                            shape = ShapeDefaults.circle,
                                            color = Color(colorFromHex),
                                            resultPadding = 0.dp
                                        )
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.Palette,
                                    contentDescription = null
                                )
                            }
                        }

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
                            val style =
                                MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center)
                            OutlinedTextField(
                                shape = ShapeDefaults.default,
                                textStyle = style,
                                maxLines = 1,
                                value = value.removePrefix("#"),
                                visualTransformation = HexVisualTransformation(true),
                                onValueChange = {
                                    val hex = it.replace("#", "")

                                    if (hex.length <= 8) {
                                        var validHex = true

                                        for (index in hex.indices) {
                                            validHex =
                                                hexRegexSingleChar.matches(hex[index].toString())
                                            if (!validHex) break
                                        }

                                        if (validHex) {
                                            value = "#${hex.uppercase()}"
                                        }
                                    }
                                },
                                placeholder = {
                                    Text(
                                        text = "#AARRGGBB",
                                        style = style,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            )
                        }
                    },
                    confirmButton = {
                        EnhancedButton(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = {
                                if (hexWithAlphaRegex.matches(value)) {
                                    onColorChange(HexUtil.hexToColor(value))
                                }
                                expanded = false
                            }
                        ) {
                            Text(stringResource(R.string.apply))
                        }
                    }
                )
            }
        }
    }
}

/** Receive the clipboard data. */
private fun getFormattedColor(color: Color): String {
    return if (color.alpha == 1f) {
        colorToHex(color)
    } else {
        colorToHexAlpha(color)
    }.uppercase()
}