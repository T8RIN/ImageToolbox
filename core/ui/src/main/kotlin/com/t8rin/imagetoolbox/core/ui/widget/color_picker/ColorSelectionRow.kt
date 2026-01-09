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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.pasteColorFromClipboard
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalContainerColor
import com.t8rin.imagetoolbox.core.ui.utils.provider.ProvideContainerDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsCombinedClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.other.LocalToastHostState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ColorSelectionRow(
    modifier: Modifier = Modifier,
    defaultColors: List<Color> = ColorSelectionRowDefaults.colorList,
    allowAlpha: Boolean = false,
    allowScroll: Boolean = true,
    value: Color,
    onValueChange: (Color) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val scope = rememberCoroutineScope()
    val toastHostState = LocalToastHostState.current
    val context = LocalContext.current
    var customColor by remember { mutableStateOf<Color?>(null) }
    var showColorPicker by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(value) {
        if (value !in defaultColors) {
            customColor = value
        }
    }

    LaunchedEffect(Unit) {
        delay(250)
        if (value == customColor) {
            listState.scrollToItem(0)
        } else if (value in defaultColors) {
            listState.scrollToItem(defaultColors.indexOf(value))
        }
    }

    val itemSize = 42.dp

    ProvideContainerDefaults(
        color = LocalContainerColor.current
    ) {
        LazyRow(
            state = listState,
            modifier = modifier
                .fillMaxWidth()
                .height(64.dp)
                .fadingEdges(listState),
            userScrollEnabled = allowScroll,
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                val background = customColor ?: MaterialTheme.colorScheme.primary
                val isSelected = customColor != null
                val interactionSource = remember { MutableInteractionSource() }
                val shape = shapeByInteraction(
                    shape = if (isSelected) ShapeDefaults.small else RoundedCornerShape(itemSize / 2),
                    pressedShape = ShapeDefaults.pressed,
                    interactionSource = interactionSource
                )

                Box(
                    modifier = Modifier
                        .height(itemSize)
                        .aspectRatio(
                            ratio = animateFloatAsState(
                                targetValue = if (isSelected) 1.5f else 1f,
                                animationSpec = tween(400)
                            ).value,
                            matchHeightConstraintsFirst = true
                        )
                        .container(
                            shape = shape,
                            color = background,
                            resultPadding = 0.dp
                        )
                        .transparencyChecker()
                        .background(background, shape)
                        .hapticsCombinedClickable(
                            indication = LocalIndication.current,
                            interactionSource = interactionSource,
                            onLongClick = {
                                context.pasteColorFromClipboard(
                                    onPastedColor = {
                                        val color = if (allowAlpha) it else it.copy(1f)

                                        onValueChange(color)
                                        customColor = color
                                    },
                                    onPastedColorFailure = { message ->
                                        scope.launch {
                                            toastHostState.showToast(
                                                message = message,
                                                icon = Icons.Outlined.Error
                                            )
                                        }
                                    }
                                )
                            },
                            onClick = {
                                showColorPicker = true
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Palette,
                        contentDescription = null,
                        tint = background.inverse(
                            fraction = {
                                if (it) 0.8f
                                else 0.5f
                            },
                            darkMode = background.luminance() < 0.3f
                        ),
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = background.copy(alpha = 1f),
                                shape = shape
                            )
                            .padding(4.dp)
                    )
                }
            }
            items(
                items = defaultColors,
                key = { it.toArgb() }
            ) { color ->
                val isSelected = value == color && customColor == null
                val interactionSource = remember { MutableInteractionSource() }
                val shape = shapeByInteraction(
                    shape = if (isSelected) ShapeDefaults.small else RoundedCornerShape(itemSize / 2),
                    pressedShape = ShapeDefaults.pressed,
                    interactionSource = interactionSource
                )

                Box(
                    modifier = Modifier
                        .height(itemSize)
                        .aspectRatio(
                            ratio = animateFloatAsState(
                                targetValue = if (isSelected) 1.5f else 1f,
                                animationSpec = tween(400)
                            ).value,
                            matchHeightConstraintsFirst = true
                        )
                        .container(
                            shape = shape,
                            color = color,
                            resultPadding = 0.dp
                        )
                        .transparencyChecker()
                        .background(color, shape)
                        .hapticsClickable(
                            interactionSource = interactionSource,
                            indication = LocalIndication.current,
                            onClick = {
                                onValueChange(color.copy(if (allowAlpha) color.alpha else 1f))
                                customColor = null
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedVisibility(
                        visible = isSelected,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DoneAll,
                                contentDescription = null,
                                tint = color.inverse(
                                    fraction = {
                                        if (it) 0.8f
                                        else 0.5f
                                    },
                                    darkMode = color.luminance() < 0.3f
                                ),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    ColorPickerSheet(
        visible = showColorPicker,
        onDismiss = { showColorPicker = false },
        color = customColor,
        onColorSelected = {
            val color = it.copy(if (allowAlpha) it.alpha else 1f)
            onValueChange(color)
            customColor = color
        },
        allowAlpha = allowAlpha
    )
}


object ColorSelectionRowDefaults {
    val colorList by lazy {
        listOf(
            Color(0xFF7a000b),
            Color(0xFF8a000b),
            Color(0xFF97000c),
            Color(0xFFa5000d),
            Color(0xFFb2070d),
            Color(0xFFbf100e),
            Color(0xFFca120e),
            Color(0xFFd8150e),
            Color(0xFFf8130d),
            Color(0xFFff3306),
            Color(0xFFff4f05),
            Color(0xFFff6b02),
            Color(0xFFff7900),
            Color(0xFFf89c08),
            Color(0xFFf8b40c),
            Color(0xFFfcf721),
            Color(0xFFe0e11c),
            Color(0xFFc9e11a),
            Color(0xFFc0dc18),
            Color(0xFFaee020),
            Color(0xFF96df20),
            Color(0xFF88dd20),
            Color(0xFF50cbb5),
            Color(0xFF36c0b3),
            Color(0xFF1eb0b0),
            Color(0xFF01a0a3),
            Color(0xFF05bfc0),
            Color(0xFF07cfd3),
            Color(0xFF59cbf0),
            Color(0xFF3b9df0),
            Color(0xFF1c7fff),
            Color(0xFF005FFF),
            Color(0xFF034087),
            Color(0xFF022b6d),
            Color(0xFF2d1d9c),
            Color(0xFF3f1f9a),
            Color(0xFF642bec),
            Color(0xFF7b2bec),
            Color(0xFF8e36f4),
            Color(0xFF9836f5),
            Color(0xFFaa3ef8),
            Color(0xFFb035f8),
            Color(0xFFc78afe),
            Color(0xFFd294fe),
            Color(0xFFdb94fe),
            Color(0xFFe76cd9),
            Color(0xFFfa64e1),
            Color(0xFFfc50a6),
            Color(0xFFe10076),
            Color(0xFFd7036a),
            Color(0xFFFFFFFF),
            Color(0xFF768484),
            Color(0xFF333333),
            Color(0xFF000000),
        )
    }

    val colorListVariant by lazy {
        colorList.takeLast(4) + colorList.dropLast(4)
    }
}