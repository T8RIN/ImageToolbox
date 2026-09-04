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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BrushColor
import com.t8rin.imagetoolbox.core.resources.icons.Done
import com.t8rin.imagetoolbox.core.resources.icons.Gradient
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelectionRowDefaults
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.palette_selection.GradientPaletteSelector

@Composable
fun DrawColorSelector(
    modifier: Modifier = Modifier
        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    value: Color,
    onValueChange: (Color) -> Unit,
    allowGradient: Boolean = false,
    gradientPalette: GradientPalette = GradientPalette.RGB,
    onGradientPaletteChange: (GradientPalette) -> Unit = {},
    isGradientEnabled: Boolean = false,
    onGradientEnabledChange: (Boolean) -> Unit = {},
    color: Color = Color.Unspecified,
    titleText: String = stringResource(R.string.paint_color),
    defaultColors: List<Color> = ColorSelectionRowDefaults.colorList,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ColorRowSelector(
            value = value,
            onValueChange = {
                onGradientEnabledChange(false)
                onValueChange(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .container(
                    shape = ShapeDefaults.extraLarge,
                    color = color
                ),
            title = titleText,
            allowAlpha = false,
            icon = Icons.Outlined.BrushColor,
            defaultColors = defaultColors,
            isColorSelectionVisible = !allowGradient || !isGradientEnabled,
            isAdditionalItemSelected = allowGradient && isGradientEnabled,
            additionalItem = if (allowGradient) {
                {
                    GradientColorItem(
                        palette = gradientPalette,
                        selected = isGradientEnabled,
                        onClick = { onGradientEnabledChange(true) }
                    )
                }
            } else null
        )
        AnimatedVisibility(
            visible = allowGradient && isGradientEnabled,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            GradientPaletteSelector(
                value = gradientPalette,
                onValueChange = onGradientPaletteChange,
                modifier = Modifier.fillMaxWidth(),
                shape = ShapeDefaults.extraLarge,
            )
        }
    }
}

@Composable
private fun GradientColorItem(
    palette: GradientPalette,
    selected: Boolean,
    onClick: () -> Unit
) {
    val itemSize = 42.dp
    val colors = remember(palette) { palette.colors.map { it.toColor() } }
    val contentColor = colors[colors.size / 2].inverse()
    val interactionSource = remember { MutableInteractionSource() }
    val shape = shapeByInteraction(
        shape = if (selected) ShapeDefaults.small else AutoCornersShape(itemSize / 2),
        pressedShape = ShapeDefaults.pressed,
        interactionSource = interactionSource
    )

    Box(
        modifier = Modifier
            .height(itemSize)
            .aspectRatio(
                ratio = animateFloatAsState(
                    targetValue = if (selected) 1.5f else 1f,
                    animationSpec = tween(400)
                ).value,
                matchHeightConstraintsFirst = true
            )
            .container(
                shape = shape,
                color = colors.first(),
                resultPadding = 0.dp
            )
            .clip(shape)
            .background(Brush.horizontalGradient(colors))
            .hapticsClickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = selected,
            modifier = Modifier.fillMaxSize()
        ) { isSelected ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSelected) {
                        Icons.Rounded.Done
                    } else {
                        Icons.Outlined.Gradient
                    },
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(if (isSelected) 20.dp else 24.dp)
                )
            }
        }
    }
}
