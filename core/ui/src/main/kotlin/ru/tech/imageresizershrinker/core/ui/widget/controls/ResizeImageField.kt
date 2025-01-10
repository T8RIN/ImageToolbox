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

package ru.tech.imageresizershrinker.core.ui.widget.controls

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.modalsheet.FullscreenPopup
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.restrict
import ru.tech.imageresizershrinker.core.ui.utils.provider.PropagateCustomKeyboardPadding
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.hapticsClickable
import ru.tech.imageresizershrinker.core.ui.widget.modifier.animateShape
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField

@Composable
fun ResizeImageField(
    imageInfo: ImageInfo,
    originalSize: IntegerSize?,
    onWidthChange: (Int) -> Unit,
    onHeightChange: (Int) -> Unit,
    showWarning: Boolean = false
) {
    val interactionSource1 = remember { MutableInteractionSource() }
    val interactionSource2 = remember { MutableInteractionSource() }

    val isWidthFocused by interactionSource1.collectIsFocusedAsState()
    val isHeightFocused by interactionSource2.collectIsFocusedAsState()

    Column(
        modifier = Modifier
            .container(shape = RoundedCornerShape(24.dp))
            .padding(8.dp)
            .animateContentSize()
    ) {
        Row {
            RoundedTextField(
                value = imageInfo.width.takeIf { it != 0 }
                    .let { it ?: "" }
                    .toString(),
                onValueChange = { value ->
                    val maxValue = if (imageInfo.height != 0) {
                        (ImageUtils.Dimens.MAX_IMAGE_SIZE / imageInfo.height).coerceAtMost(32768)
                    } else 32768

                    onWidthChange(
                        value
                            .restrict(maxValue)
                            .toIntOrNull() ?: 0
                    )
                },
                shape = RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 6.dp,
                    bottomStart = 12.dp,
                    bottomEnd = 6.dp
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = {
                    Text(
                        stringResource(
                            R.string.width,
                            originalSize?.width?.toString() ?: ""
                        )
                    )
                },
                modifier = Modifier.weight(1f),
                interactionSource = interactionSource1
            )
            Spacer(modifier = Modifier.width(4.dp))
            RoundedTextField(
                value = imageInfo.height.takeIf { it != 0 }
                    .let { it ?: "" }
                    .toString(),
                onValueChange = { value ->
                    val maxValue = if (imageInfo.width != 0) {
                        (ImageUtils.Dimens.MAX_IMAGE_SIZE / imageInfo.width).coerceAtMost(32768)
                    } else 32768

                    onHeightChange(
                        value
                            .restrict(maxValue)
                            .toIntOrNull() ?: 0
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                shape = RoundedCornerShape(
                    topEnd = 12.dp,
                    topStart = 6.dp,
                    bottomEnd = 12.dp,
                    bottomStart = 6.dp
                ),
                label = {
                    Text(
                        stringResource(
                            R.string.height,
                            originalSize?.height?.toString()
                                ?: ""
                        )
                    )
                },
                modifier = Modifier.weight(1f),
                interactionSource = interactionSource2
            )
        }
        IcoSizeWarning(
            visible = imageInfo.run {
                imageFormat == ImageFormat.Ico && (width > 256 || height > 256)
            }
        )
        OOMWarning(visible = showWarning)
    }

    var text by remember(isWidthFocused, isHeightFocused) {
        val size = if (isWidthFocused) {
            imageInfo.width
        } else if (isHeightFocused) {
            imageInfo.height
        } else {
            0
        }

        mutableStateOf(
            size.takeIf { it != 0 }
                .let { it ?: "" }
                .toString()
        )
    }

    val focus = LocalFocusManager.current
    val isKeyboardVisible = isWidthFocused || isHeightFocused

    CalculatorKeyboard(
        visible = false,
        onKey = {
            when (it) {
                Key.Backspace -> text = text.dropLast(1)
                Key.Done -> focus.clearFocus()
                is Key.Symbol -> text += it.char
            }
        }
    )

    BackHandler(
        enabled = isKeyboardVisible,
        onBack = focus::clearFocus
    )
}

@Composable
private fun CalculatorKeyboard(
    visible: Boolean,
    onKey: (Key) -> Unit
) {
    var keyboardHeight by remember {
        mutableStateOf(0.dp)
    }

    PropagateCustomKeyboardPadding(
        keyboardHeight = keyboardHeight
    )

    FullscreenPopup {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ) {
                Surface {
                    val height = minOf(this@BoxWithConstraints.maxHeight / 2, 300.dp)

                    LaunchedEffect(height) {
                        keyboardHeight = height
                    }

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(height)
                            .background(
                                MaterialTheme.colorScheme.surfaceContainer
                            )
                            .navigationBarsPadding()
                            .padding(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(3.5f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    SymbolKey(
                                        modifier = Modifier.weight(0.5f),
                                        letter = '+',
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        onKey = onKey
                                    )
                                    SymbolKey(
                                        modifier = Modifier.weight(1f),
                                        letter = '1',
                                        onKey = onKey
                                    )
                                    SymbolKey(
                                        modifier = Modifier.weight(1f),
                                        letter = '2',
                                        onKey = onKey
                                    )
                                    SymbolKey(
                                        modifier = Modifier.weight(1f),
                                        letter = '3',
                                        onKey = onKey
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    SymbolKey(
                                        modifier = Modifier.weight(0.5f),
                                        letter = '-',
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        onKey = onKey
                                    )
                                    SymbolKey(
                                        modifier = Modifier.weight(1f),
                                        letter = '4',
                                        onKey = onKey
                                    )
                                    SymbolKey(
                                        modifier = Modifier.weight(1f),
                                        letter = '5',
                                        onKey = onKey
                                    )
                                    SymbolKey(
                                        modifier = Modifier.weight(1f),
                                        letter = '6',
                                        onKey = onKey
                                    )
                                }
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    SymbolKey(
                                        modifier = Modifier.weight(0.5f),
                                        letter = '*',
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        onKey = onKey
                                    )
                                    SymbolKey(
                                        modifier = Modifier.weight(1f),
                                        letter = '7',
                                        onKey = onKey
                                    )
                                    SymbolKey(
                                        modifier = Modifier.weight(1f),
                                        letter = '8',
                                        onKey = onKey
                                    )
                                    SymbolKey(
                                        modifier = Modifier.weight(1f),
                                        letter = '9',
                                        onKey = onKey
                                    )
                                }
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    SymbolKey(
                                        modifier = Modifier.weight(0.5f),
                                        letter = '/',
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        onKey = onKey
                                    )
                                    Spacer(
                                        modifier = Modifier.weight(1f)
                                    )
                                    SymbolKey(
                                        modifier = Modifier.weight(1f),
                                        letter = '0',
                                        onKey = onKey
                                    )
                                    Spacer(
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier.weight(0.5f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                BackspaceKey(
                                    modifier = Modifier.weight(1f),
                                    onKey = onKey
                                )
                                DoneKey(
                                    modifier = Modifier.weight(1f),
                                    onKey = onKey
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private sealed interface Key {
    data object Backspace : Key
    data object Done : Key
    data class Symbol(val char: Char) : Key
}

@Composable
private fun SymbolKey(
    modifier: Modifier,
    letter: Char,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    onKey: (Key.Symbol) -> Unit
) {
    Key(
        modifier = modifier,
        onClick = {
            onKey(Key.Symbol(letter))
        },
        containerColor = containerColor,
        content = {
            AutoSizeText(
                text = letter.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = if (letter == '*') {
                    Modifier.offset(y = 4.dp)
                } else Modifier
            )
        }
    )
}

@Composable
private fun DoneKey(
    modifier: Modifier,
    onKey: (Key.Done) -> Unit
) {
    Key(
        modifier = modifier,
        onClick = {
            onKey(Key.Done)
        },
        contentPadding = PaddingValues(),
        containerColor = MaterialTheme.colorScheme.primary,
        content = {
            Icon(
                imageVector = Icons.Outlined.Done,
                contentDescription = null
            )
        }
    )
}

@Composable
private fun BackspaceKey(
    modifier: Modifier,
    onKey: (Key.Backspace) -> Unit
) {
    Key(
        modifier = modifier,
        onClick = {
            onKey(Key.Backspace)
        },
        contentPadding = PaddingValues(),
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        content = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Backspace,
                contentDescription = null
            )
        }
    )
}

@Composable
private fun Key(
    modifier: Modifier,
    onClick: () -> Unit,
    containerColor: Color,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Row(
        modifier = modifier
            .fillMaxSize()
            .container(
                color = containerColor,
                shape = animateShape(
                    if (isPressed) RoundedCornerShape(6.dp)
                    else RoundedCornerShape(48.dp)
                ),
                resultPadding = 0.dp
            )
            .hapticsClickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColorFor(containerColor)
        ) {
            content()
        }
    }
}