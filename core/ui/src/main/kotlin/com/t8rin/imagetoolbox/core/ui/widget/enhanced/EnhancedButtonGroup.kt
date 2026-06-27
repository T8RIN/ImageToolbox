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

package com.t8rin.imagetoolbox.core.ui.widget.enhanced

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.t8rin.imagetoolbox.core.resources.utils.compositeOverSafe
import com.t8rin.imagetoolbox.core.settings.domain.model.ShapeType
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.DisabledAlpha
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.animation.lessSpringySpec
import com.t8rin.imagetoolbox.core.ui.utils.helper.ProvidesValue
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCircleShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun EnhancedButtonGroup(
    modifier: Modifier = defaultModifier,
    enabled: Boolean = true,
    items: List<String>,
    selectedIndex: Int,
    title: String? = null,
    onIndexChange: (Int) -> Unit,
    isScrollable: Boolean = true,
    contentPadding: PaddingValues = DefaultContentPadding,
    inactiveButtonColor: Color = MaterialTheme.colorScheme.surface,
    activeButtonColor: Color = MaterialTheme.colorScheme.secondary,
) {
    EnhancedButtonGroup(
        enabled = enabled,
        items = items,
        selectedIndex = selectedIndex,
        onIndexChange = onIndexChange,
        modifier = modifier,
        title = {
            title?.let {
                Text(
                    text = it,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        },
        isScrollable = isScrollable,
        contentPadding = contentPadding,
        inactiveButtonColor = inactiveButtonColor,
        activeButtonColor = activeButtonColor,
    )
}

@Composable
fun EnhancedButtonGroup(
    modifier: Modifier = defaultModifier,
    enabled: Boolean,
    items: List<String>,
    selectedIndex: Int,
    title: @Composable RowScope.() -> Unit = {},
    onIndexChange: (Int) -> Unit,
    isScrollable: Boolean = true,
    contentPadding: PaddingValues = DefaultContentPadding,
    inactiveButtonColor: Color = MaterialTheme.colorScheme.surface,
    activeButtonColor: Color = MaterialTheme.colorScheme.secondary,
) {
    EnhancedButtonGroup(
        modifier = modifier,
        enabled = enabled,
        itemCount = items.size,
        selectedIndex = selectedIndex,
        itemContent = {
            AutoSizeText(
                text = items[it],
                style = LocalTextStyle.current.copy(
                    fontSize = 13.sp
                ),
                maxLines = 1
            )
        },
        onIndexChange = onIndexChange,
        title = title,
        isScrollable = isScrollable,
        contentPadding = contentPadding,
        inactiveButtonColor = inactiveButtonColor,
        activeButtonColor = activeButtonColor,
    )
}

@Composable
fun <T : Any> EnhancedButtonGroup(
    modifier: Modifier = defaultModifier,
    enabled: Boolean = true,
    entries: List<T>,
    value: T,
    itemContent: @Composable (item: T) -> Unit,
    title: String?,
    onValueChange: (T) -> Unit,
    inactiveButtonColor: Color = MaterialTheme.colorScheme.surface,
    activeButtonColor: Color = MaterialTheme.colorScheme.secondary,
    isScrollable: Boolean = true,
    contentPadding: PaddingValues = DefaultContentPadding,
    useClassFinding: Boolean = false
) {
    EnhancedButtonGroup(
        modifier = modifier,
        enabled = enabled,
        itemCount = entries.size,
        selectedIndex = if (useClassFinding) {
            entries.indexOfFirst { it::class.isInstance(value) }
        } else {
            entries.indexOf(value)
        },
        itemContent = {
            itemContent(entries[it])
        },
        onIndexChange = {
            onValueChange(
                if (useClassFinding && value::class.isInstance(entries[it])) {
                    value
                } else {
                    entries[it]
                }
            )
        },
        title = {
            title?.let {
                Text(
                    text = title,
                    style = PreferenceItemDefaults.TitleFontStyleCentered,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )
            }
        },
        inactiveButtonColor = inactiveButtonColor,
        activeButtonColor = activeButtonColor,
        isScrollable = isScrollable,
        contentPadding = contentPadding
    )
}

@Composable
fun EnhancedButtonGroup(
    modifier: Modifier = defaultModifier,
    enabled: Boolean = true,
    itemCount: Int,
    selectedIndex: Int,
    itemContent: @Composable (item: Int) -> Unit,
    title: @Composable RowScope.() -> Unit = {},
    onIndexChange: (Int) -> Unit,
    inactiveButtonColor: Color = MaterialTheme.colorScheme.surface,
    activeButtonColor: Color = MaterialTheme.colorScheme.secondary,
    isScrollable: Boolean = true,
    contentPadding: PaddingValues = DefaultContentPadding
) {
    EnhancedButtonGroup(
        modifier = modifier,
        enabled = enabled,
        itemCount = itemCount,
        selectedIndices = setOf(selectedIndex),
        itemContent = itemContent,
        title = title,
        onIndexChange = onIndexChange,
        inactiveButtonColor = inactiveButtonColor,
        activeButtonColor = activeButtonColor,
        isScrollable = isScrollable,
        contentPadding = contentPadding
    )
}

@Composable
fun <T> EnhancedButtonGroup(
    modifier: Modifier = defaultModifier,
    enabled: Boolean = true,
    entries: List<T>,
    values: List<T>,
    itemContent: @Composable (item: T) -> Unit,
    title: String?,
    onValueChange: (T) -> Unit,
    inactiveButtonColor: Color = MaterialTheme.colorScheme.surface,
    activeButtonColor: Color = MaterialTheme.colorScheme.secondary,
    isScrollable: Boolean = true,
    contentPadding: PaddingValues = DefaultContentPadding
) {
    val selectedIndices by remember(values, entries) {
        derivedStateOf {
            values.mapTo(mutableSetOf()) { entries.indexOf(it) }
        }
    }

    EnhancedButtonGroup(
        modifier = modifier,
        enabled = enabled,
        itemCount = entries.size,
        selectedIndices = selectedIndices,
        itemContent = {
            itemContent(entries[it])
        },
        onIndexChange = {
            onValueChange(
                entries[it]
            )
        },
        title = {
            title?.let {
                Text(
                    text = title,
                    style = PreferenceItemDefaults.TitleFontStyleCentered,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )
            }
        },
        inactiveButtonColor = inactiveButtonColor,
        activeButtonColor = activeButtonColor,
        isScrollable = isScrollable,
        contentPadding = contentPadding
    )
}

@Composable
fun EnhancedButtonGroup(
    modifier: Modifier = defaultModifier,
    enabled: Boolean = true,
    itemCount: Int,
    selectedIndices: Set<Int>,
    itemContent: @Composable (item: Int) -> Unit,
    title: @Composable RowScope.() -> Unit = {},
    onIndexChange: (Int) -> Unit,
    inactiveButtonColor: Color = MaterialTheme.colorScheme.surface,
    activeButtonColor: Color = MaterialTheme.colorScheme.secondary,
    isScrollable: Boolean = true,
    contentPadding: PaddingValues = DefaultContentPadding
) {
    val settingsState = LocalSettingsState.current

    val disabledColor = MaterialTheme.colorScheme.onSurface
        .copy(alpha = DisabledAlpha)
        .compositeOverSafe(MaterialTheme.colorScheme.surface)

    ProvideTextStyle(
        value = LocalTextStyle.current.copy(
            color = if (!enabled) disabledColor
            else Color.Unspecified
        )
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                content = title
            )
            val scrollState = rememberScrollState()

            LocalMinimumInteractiveComponentSize.ProvidesValue(Dp.Unspecified) {
                val animationSpec = remember { lessSpringySpec<Float>() }

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Max)
                        .then(
                            if (isScrollable) {
                                Modifier
                                    .fadingEdges(scrollState)
                                    .enhancedHorizontalScroll(scrollState)
                            } else {
                                Modifier.fillMaxWidth()
                            }
                        )
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                ) {
                    repeat(itemCount) { index ->
                        val interactionSource = remember { MutableInteractionSource() }

                        val activeContainerColor = if (enabled) {
                            activeButtonColor
                        } else {
                            MaterialTheme.colorScheme.surfaceContainer
                        }

                        val selected = index in selectedIndices

                        val disableSmoothness =
                            !selected && index == 0 || index == itemCount - 1

                        LocalSettingsState.ProvidesValue(
                            if (disableSmoothness && settingsState.shapesType is ShapeType.Smooth) {
                                settingsState.copy(
                                    shapesType = ShapeType.Rounded()
                                )
                            } else {
                                settingsState
                            }
                        ) {
                            EnhancedToggleButton(
                                enabled = enabled,
                                onCheckedChange = {
                                    onIndexChange(index)
                                },
                                border = BorderStroke(
                                    width = settingsState.borderWidth,
                                    color = MaterialTheme.colorScheme.outlineVariant(
                                        onTopOf = if (selected) activeContainerColor
                                        else inactiveButtonColor
                                    )
                                ),
                                colors = ToggleButtonDefaults.toggleButtonColors(
                                    containerColor = inactiveButtonColor,
                                    contentColor = contentColorFor(inactiveButtonColor),
                                    checkedContainerColor = activeContainerColor,
                                    checkedContentColor = contentColorFor(activeContainerColor)
                                ),
                                checked = selected,
                                shapes = when (index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes(
                                        shape = AutoCornersShape(
                                            topStart = CornerFull,
                                            bottomStart = CornerFull,
                                            topEnd = CornerValueSmall,
                                            bottomEnd = CornerValueSmall,
                                        ),
                                        pressedShape = ButtonDefaults.pressedShape,
                                        checkedShape = AutoCircleShape()
                                    )

                                    itemCount - 1 -> ButtonGroupDefaults.connectedTrailingButtonShapes(
                                        shape = AutoCornersShape(
                                            topEnd = CornerFull,
                                            bottomEnd = CornerFull,
                                            topStart = CornerValueSmall,
                                            bottomStart = CornerValueSmall,
                                        ),
                                        pressedShape = ButtonDefaults.pressedShape,
                                        checkedShape = AutoCircleShape()
                                    )

                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes(
                                        shape = ShapeDefaults.mini,
                                        pressedShape = ButtonDefaults.pressedShape,
                                        checkedShape = AutoCircleShape()
                                    )
                                },
                                modifier = Modifier
                                    .enlargeOnPress(
                                        isScrollable = isScrollable,
                                        interactionSource = interactionSource,
                                        animationSpec = animationSpec,
                                        rowScope = this
                                    )
                                    .zIndex(if (selected) 1f else 0f),
                                interactionSource = interactionSource
                            ) {
                                if (!isScrollable) {
                                    Row(
                                        modifier = Modifier.marquee()
                                    ) {
                                        itemContent(index)
                                    }
                                } else {
                                    itemContent(index)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Modifier.enlargeOnPress(
    isScrollable: Boolean,
    interactionSource: MutableInteractionSource,
    animationSpec: AnimationSpec<Float>,
    rowScope: RowScope,
    factor: Float = 0.2f
): Modifier {
    val pressedAnimatable = remember { Animatable(0f) }

    LaunchedEffect(interactionSource) {
        val pressInteractions = mutableListOf<PressInteraction.Press>()

        interactionSource.interactions
            .map { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> pressInteractions.add(interaction)
                    is PressInteraction.Release -> pressInteractions.remove(interaction.press)
                    is PressInteraction.Cancel -> pressInteractions.remove(interaction.press)
                }
                pressInteractions.isNotEmpty()
            }
            .distinctUntilChanged()
            .collectLatest { pressed ->
                if (pressed) {
                    launch { pressedAnimatable.animateTo(1f, animationSpec) }
                } else {
                    delay(150)

                    val initialTimeMillis = withFrameMillis { it }
                    while (!(pressedAnimatable.value > 0.75f)) {
                        val timeMillis = withFrameMillis { it }
                        if (timeMillis - initialTimeMillis > 1_000L) break
                    }
                    pressedAnimatable.animateTo(0f, animationSpec)
                }
            }
    }

    return this then if (isScrollable) {
        var buttonWidth by remember { mutableIntStateOf(0) }

        Modifier
            .onSizeChanged {
                if (buttonWidth == 0) buttonWidth = it.width
            }
            .layout { measurable, constraints ->
                val mod =
                    (buttonWidth * pressedAnimatable.value * factor).roundToInt()
                        .coerceAtLeast(0)

                val placeable = measurable.measure(
                    if (buttonWidth == 0) {
                        constraints
                    } else {
                        constraints.copy(
                            minWidth = buttonWidth + mod,
                            maxWidth = buttonWidth + mod
                        )
                    }
                )

                layout(
                    placeable.measuredWidth,
                    placeable.measuredHeight
                ) {
                    placeable.place(0, 0)
                }
            }
    } else {
        with(rowScope) {
            Modifier.weight(1f + pressedAnimatable.value * factor)
        }
    }
}

private val defaultModifier = Modifier
    .fillMaxWidth()
    .padding(8.dp)

private val DefaultContentPadding = PaddingValues(
    start = 6.dp,
    end = 6.dp,
    bottom = 6.dp,
    top = 8.dp
)

private val CornerFull: CornerSize = CornerSize(50)
private val CornerValueSmall = CornerSize(8.0.dp)
