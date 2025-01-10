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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
                modifier = Modifier.weight(1f)
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
                modifier = Modifier.weight(1f)
            )
        }
        IcoSizeWarning(
            visible = imageInfo.run {
                imageFormat == ImageFormat.Ico && (width > 256 || height > 256)
            }
        )
        OOMWarning(visible = showWarning)
    }

    CalculatorKeyboard()
}

@Composable
internal fun CalculatorKeyboard(

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
            var text by remember {
                mutableStateOf("")
            }

            val onKey: (Key) -> Unit = {
                when (it) {
                    Key.Backspace -> text = text.dropLast(1)
                    Key.Done -> Unit
                    is Key.Symbol -> text += it.char
                }
            }

            Surface {
                val height = minOf(this.maxHeight / 2, 300.dp)

                LaunchedEffect(height) {
                    keyboardHeight = height
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height)
                        .background(
                            MaterialTheme.colorScheme.surfaceContainer
                        )
                        .navigationBarsPadding()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
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
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
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
                        BackspaceKey(
                            modifier = Modifier.weight(1f),
                            onKey = onKey
                        )
                        SymbolKey(
                            modifier = Modifier.weight(1f),
                            letter = '0',
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

private sealed interface Key {
    data object Backspace : Key
    data object Done : Key
    data class Symbol(val char: Char) : Key
}

@Composable
private fun SymbolKey(
    modifier: Modifier,
    letter: Char,
    onKey: (Key.Symbol) -> Unit
) {
    Key(
        modifier = modifier,
        onClick = {
            onKey(Key.Symbol(letter))
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        content = {
            AutoSizeText(
                text = letter.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Normal
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
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Row(
        modifier = modifier
            .fillMaxHeight()
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
            .padding(ButtonDefaults.ContentPadding),
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