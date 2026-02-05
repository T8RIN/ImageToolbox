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

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelectionRow
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelectionRowDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsCombinedClickable
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.IconShapeContainer
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.coroutines.launch

@Composable
fun ColorRowSelector(
    value: Color?,
    onValueChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.background_color),
    icon: ImageVector? = Icons.Outlined.Palette,
    allowAlpha: Boolean = true,
    allowScroll: Boolean = true,
    defaultColors: List<Color> = ColorSelectionRowDefaults.colorListVariant,
    topEndIcon: (@Composable () -> Unit)? = null,
    contentHorizontalPadding: Dp = 12.dp,
    onNullClick: (() -> Unit)? = null
) {
    val isCompactLayout = LocalSettingsState.current.isCompactSelectorsLayout
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.then(
            if (isCompactLayout && icon != null) {
                Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            scope.launch {
                                tooltipState.show()
                            }
                        }
                    )
                }
            } else Modifier
        ),
    ) {
        if (!isCompactLayout) {
            TitleItem(
                icon = icon,
                text = title,
                iconEndPadding = 14.dp,
                modifier = Modifier.padding(
                    top = if (topEndIcon == null) {
                        12.dp
                    } else {
                        6.dp
                    },
                    start = contentHorizontalPadding,
                    end = if (topEndIcon == null) {
                        contentHorizontalPadding
                    } else {
                        (contentHorizontalPadding - 8.dp).coerceAtLeast(0.dp)
                    }
                ),
                endContent = topEndIcon?.let {
                    { it() }
                }
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isCompactLayout) {
                Box {
                    AnimatedContent(icon) { icon ->
                        if (icon != null) {
                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                    TooltipAnchorPosition.Above
                                ),
                                tooltip = {
                                    RichTooltip(
                                        colors = TooltipDefaults.richTooltipColors(
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(
                                                0.5f
                                            ),
                                            titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                        ),
                                        title = { Text(title) },
                                        text = {
                                            if (value != null) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .container(
                                                            shape = ShapeDefaults.circle,
                                                            color = value,
                                                            resultPadding = 0.dp
                                                        )
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Rounded.Block,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .container(
                                                            shape = ShapeDefaults.circle,
                                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                                            resultPadding = 0.dp
                                                        ),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    )
                                },
                                state = tooltipState,
                                content = {
                                    IconShapeContainer(
                                        enabled = true,
                                        content = {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null
                                            )
                                        },
                                        modifier = Modifier
                                            .padding(
                                                start = contentHorizontalPadding
                                            )
                                            .clip(
                                                LocalSettingsState.current.iconShape?.shape
                                                    ?: ShapeDefaults.circle
                                            )
                                            .hapticsCombinedClickable(
                                                onLongClick = {
                                                    scope.launch { tooltipState.show() }
                                                },
                                                onClick = {
                                                    scope.launch { tooltipState.show() }
                                                }
                                            )
                                    )
                                }
                            )
                        }
                    }
                    BoxAnimatedVisibility(icon == null) {
                        Text(
                            text = title,
                            modifier = Modifier
                                .padding(
                                    start = contentHorizontalPadding
                                )
                                .widthIn(max = 100.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 16.sp
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
            }
            ColorSelectionRow(
                defaultColors = defaultColors,
                allowAlpha = allowAlpha,
                contentPadding = PaddingValues(
                    start = if (isCompactLayout) 0.dp else contentHorizontalPadding,
                    end = contentHorizontalPadding
                ),
                allowScroll = allowScroll,
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                onNullClick = onNullClick
            )
            if (isCompactLayout && topEndIcon != null) {
                topEndIcon()
            }
        }
    }
}

@Composable
@Preview
private fun Preview() = ImageToolboxThemeForPreview(false) {
    var color by remember {
        mutableStateOf<Color?>(null)
    }

    CompositionLocalProvider(
        LocalSettingsState provides LocalSettingsState.current.copy(isCompactSelectorsLayout = true)
    ) {
        ColorRowSelector(
            value = color,
            onNullClick = {
                color = null
            },
            onValueChange = { color = it },
            modifier = Modifier
                .padding(20.dp)
                .padding(vertical = 100.dp)
                .fillMaxWidth()
                .container(
                    shape = ShapeDefaults.large
                ),
            icon = Icons.Rounded.Palette,
            title = stringResource(R.string.selected_color),
            topEndIcon = {
                EnhancedIconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PushPin,
                        contentDescription = null
                    )
                }
            }
        )
    }
}