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

package com.t8rin.imagetoolbox.feature.draw.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.extendedcolors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BoldLine
import com.t8rin.imagetoolbox.core.resources.icons.DashedLine
import com.t8rin.imagetoolbox.core.resources.icons.DotDashedLine
import com.t8rin.imagetoolbox.core.resources.icons.StampedLine
import com.t8rin.imagetoolbox.core.resources.icons.ZigzagLine
import com.t8rin.imagetoolbox.core.resources.shapes.CloverShape
import com.t8rin.imagetoolbox.core.resources.shapes.MaterialStarShape
import com.t8rin.imagetoolbox.core.settings.presentation.model.IconShape
import com.t8rin.imagetoolbox.core.ui.widget.buttons.SupportingButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.draw.domain.DrawLineStyle

@Composable
fun DrawLineStyleSelector(
    modifier: Modifier,
    value: DrawLineStyle,
    onValueChange: (DrawLineStyle) -> Unit,
    values: List<DrawLineStyle> = DrawLineStyle.entries
) {
    var isSheetVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(values, value) {
        if (values.filterIsInstance(value::class.java).isEmpty() && values.isNotEmpty()) {
            onValueChange(values.first())
        }
    }

    Column(
        modifier = modifier
            .container(ShapeDefaults.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EnhancedButtonGroup(
            enabled = true,
            itemCount = values.size,
            title = {
                Text(
                    text = stringResource(R.string.line_style),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                SupportingButton(
                    onClick = {
                        isSheetVisible = true
                    }
                )
            },
            selectedIndex = values.indexOfFirst {
                value::class.isInstance(it)
            },
            itemContent = {
                Icon(
                    imageVector = values[it].getIcon(),
                    contentDescription = null
                )
            },
            onIndexChange = {
                onValueChange(values[it])
            },
            activeButtonColor = MaterialTheme.colorScheme.secondaryContainer
        )
        var lineStyle by remember {
            mutableStateOf<DrawLineStyle?>(value)
        }

        AnimatedVisibility(
            visible = value is DrawLineStyle.Dashed,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            SideEffect {
                if (value is DrawLineStyle.Dashed) {
                    lineStyle = value
                }
            }
            DisposableEffect(Unit) {
                onDispose { lineStyle = null }
            }
            val style = lineStyle as? DrawLineStyle.Dashed ?: return@AnimatedVisibility

            Column {
                EnhancedSliderItem(
                    value = style.size.value,
                    title = stringResource(R.string.dash_size),
                    valueRange = 0f..100f,
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    onValueChange = {
                        onValueChange(
                            style.copy(
                                size = it.pt
                            )
                        )
                    },
                    valueSuffix = " Pt",
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = ShapeDefaults.top
                )
                Spacer(modifier = Modifier.height(4.dp))
                EnhancedSliderItem(
                    value = style.gap.value,
                    title = stringResource(R.string.gap_size),
                    valueRange = 0f..100f,
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    onValueChange = {
                        onValueChange(
                            style.copy(
                                gap = it.pt
                            )
                        )
                    },
                    valueSuffix = " Pt",
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = ShapeDefaults.bottom
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        AnimatedVisibility(
            visible = value is DrawLineStyle.ZigZag,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            SideEffect {
                if (value is DrawLineStyle.ZigZag) {
                    lineStyle = value
                }
            }
            DisposableEffect(Unit) {
                onDispose { lineStyle = null }
            }
            val style = lineStyle as? DrawLineStyle.ZigZag ?: return@AnimatedVisibility

            Column {
                var ratio by remember {
                    mutableFloatStateOf(style.heightRatio)
                }
                EnhancedSliderItem(
                    value = ratio,
                    title = stringResource(R.string.zigzag_ratio),
                    valueRange = 0.5f..20f,
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    onValueChange = {
                        ratio = it
                        onValueChange(
                            style.copy(
                                heightRatio = 20.5f - it
                            )
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = ShapeDefaults.default
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        AnimatedVisibility(
            visible = value is DrawLineStyle.Stamped<*>,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            SideEffect {
                if (value is DrawLineStyle.Stamped<*>) {
                    lineStyle = value
                }
            }
            DisposableEffect(Unit) {
                onDispose { lineStyle = null }
            }
            val style = lineStyle as? DrawLineStyle.Stamped<*> ?: return@AnimatedVisibility

            val shape = if (style.shape is Shape) style.shape
            else MaterialStarShape

            Column {
                val shapes = IconShape.entriesNoRandom

                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .container(
                            shape = ShapeDefaults.top,
                            color = MaterialTheme.colorScheme.surface,
                            resultPadding = 0.dp
                        )
                ) {
                    val state = rememberLazyGridState()
                    LazyHorizontalGrid(
                        state = state,
                        rows = GridCells.Adaptive(48.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .fadingEdges(
                                scrollableState = state,
                                spanCount = 4,
                                length = 32.dp
                            ),
                        verticalArrangement = Arrangement.spacedBy(
                            space = 6.dp,
                            alignment = Alignment.CenterVertically
                        ),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 6.dp,
                            alignment = Alignment.CenterHorizontally
                        ),
                        contentPadding = PaddingValues(8.dp),
                        flingBehavior = enhancedFlingBehavior()
                    ) {
                        items(shapes) { iconShape ->
                            val selected by remember(iconShape, shape) {
                                derivedStateOf {
                                    iconShape.shape == shape
                                }
                            }
                            val color by animateColorAsState(
                                if (selected) MaterialTheme.colorScheme.primaryContainer
                                else Color.Unspecified
                            )
                            val borderColor by animateColorAsState(
                                if (selected) {
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
                                } else MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                    alpha = 0.1f
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .container(
                                        color = color,
                                        shape = CloverShape,
                                        borderColor = borderColor,
                                        resultPadding = 0.dp
                                    )
                                    .hapticsClickable {
                                        onValueChange(
                                            DrawLineStyle.Stamped(
                                                shape = iconShape.shape,
                                                spacing = style.spacing
                                            )
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .container(
                                            borderWidth = 2.dp,
                                            borderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            color = Color.Transparent,
                                            shape = iconShape.shape
                                        )
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                EnhancedSliderItem(
                    value = style.spacing.value,
                    title = stringResource(R.string.spacing),
                    valueRange = 0f..100f,
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    onValueChange = {
                        onValueChange(
                            style.copy(
                                spacing = it.pt
                            )
                        )
                    },
                    valueSuffix = " Pt",
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = ShapeDefaults.bottom
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
    EnhancedModalBottomSheet(
        sheetContent = {
            Column(
                modifier = Modifier
                    .enhancedVerticalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                values.forEachIndexed { index, item ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .container(
                                shape = ShapeDefaults.byIndex(
                                    index = index,
                                    size = values.size
                                ),
                                resultPadding = 0.dp
                            )
                    ) {
                        TitleItem(
                            text = stringResource(item.getTitle()),
                            icon = item.getIcon()
                        )
                        Text(
                            text = stringResource(item.getSubtitle()),
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        visible = isSheetVisible,
        onDismiss = {
            isSheetVisible = it
        },
        title = {
            TitleItem(text = stringResource(R.string.draw_mode))
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { isSheetVisible = false }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        }
    )
}

private fun DrawLineStyle.getSubtitle(): Int = when (this) {
    is DrawLineStyle.Dashed -> R.string.dashed_sub
    DrawLineStyle.DotDashed -> R.string.dot_dashed_sub
    DrawLineStyle.None -> R.string.defaultt_sub
    is DrawLineStyle.Stamped<*> -> R.string.stamped_sub
    is DrawLineStyle.ZigZag -> R.string.zigzag_sub
}

private fun DrawLineStyle.getTitle(): Int = when (this) {
    is DrawLineStyle.Dashed -> R.string.dashed
    DrawLineStyle.DotDashed -> R.string.dot_dashed
    DrawLineStyle.None -> R.string.defaultt
    is DrawLineStyle.Stamped<*> -> R.string.stamped
    is DrawLineStyle.ZigZag -> R.string.zigzag
}

private fun DrawLineStyle.getIcon(): ImageVector = when (this) {
    is DrawLineStyle.Dashed -> Icons.Rounded.DashedLine
    DrawLineStyle.DotDashed -> Icons.Rounded.DotDashedLine
    DrawLineStyle.None -> Icons.Rounded.BoldLine
    is DrawLineStyle.Stamped<*> -> Icons.Rounded.StampedLine
    is DrawLineStyle.ZigZag -> Icons.Rounded.ZigzagLine
}