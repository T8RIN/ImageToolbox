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

@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.t8rin.imagetoolbox.core.ui.widget.enhanced

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFirstOrNull
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import kotlin.math.roundToInt

@Composable
fun RowScope.EnhancedNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
    colors: NavigationBarItemColors = NavigationBarItemDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val colorAnimationSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Color>()
    val styledIcon =
        @Composable {
            val iconColor by
            animateColorAsState(
                targetValue = colors.iconColor(selected = selected, enabled = enabled),
                animationSpec = colorAnimationSpec,
            )
            // If there's a label, don't have a11y services repeat the icon description.
            val clearSemantics = label != null && (alwaysShowLabel || selected)
            Box(modifier = if (clearSemantics) Modifier.clearAndSetSemantics {} else Modifier) {
                CompositionLocalProvider(LocalContentColor provides iconColor, content = icon)
            }
        }

    val styledLabel: @Composable (() -> Unit)? =
        label?.let {
            @Composable {
                val style = NavigationBarTokens.LabelTextFont
                val textColor by
                animateColorAsState(
                    targetValue = colors.textColor(selected = selected, enabled = enabled),
                    animationSpec = colorAnimationSpec,
                )
                ProvideContentColorTextStyle(
                    contentColor = textColor,
                    textStyle = style,
                    content = label,
                )
            }
        }

    var itemWidth by remember { mutableIntStateOf(0) }

    Box(
        modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null,
            )
            .defaultMinSize(minHeight = NavigationBarHeight)
            .weight(1f)
            .onSizeChanged { itemWidth = it.width },
        contentAlignment = Alignment.Center,
        propagateMinConstraints = true,
    ) {
        val alphaAnimationProgress: State<Float> =
            animateFloatAsState(
                targetValue = if (selected) 1f else 0f,
                animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
            )
        val sizeAnimationProgress: State<Float> =
            animateFloatAsState(
                targetValue = if (selected) 1f else 0f,
                animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
            )
        // The entire item is selectable, but only the indicator pill shows the ripple. To achieve
        // this, we re-map the coordinates of the item's InteractionSource into the coordinates of
        // the indicator.
        val density = LocalDensity.current
        val calculateDeltaOffset = {
            with(density) {
                val indicatorWidth =
                    NavigationBarVerticalItemTokens.ActiveIndicatorWidth.roundToPx()
                Offset((itemWidth - indicatorWidth).toFloat() / 2, IndicatorVerticalOffset.toPx())
            }
        }
        val offsetInteractionSource =
            remember(interactionSource, calculateDeltaOffset) {
                MappedInteractionSource(interactionSource, calculateDeltaOffset)
            }

        val indicatorShape = shapeByInteraction(
            shape = NavigationBarTokens.ItemActiveIndicatorShape,
            pressedShape = ShapeDefaults.pressed,
            interactionSource = interactionSource
        )

        // The indicator has a width-expansion animation which interferes with the timing of the
        // ripple, which is why they are separate composables
        val indicatorRipple =
            @Composable {
                Box(
                    Modifier
                        .layoutId(IndicatorRippleLayoutIdTag)
                        .clip(indicatorShape)
                        .indication(offsetInteractionSource, ripple())
                )
            }
        val indicator =
            @Composable {
                Box(
                    Modifier
                        .layoutId(IndicatorLayoutIdTag)
                        .graphicsLayer { alpha = alphaAnimationProgress.value }
                        .background(
                            color = colors.selectedIndicatorColor,
                            shape = indicatorShape
                        )
                )
            }

        NavigationBarItemLayout(
            indicatorRipple = indicatorRipple,
            indicator = indicator,
            icon = styledIcon,
            label = styledLabel,
            alwaysShowLabel = alwaysShowLabel,
            alphaAnimationProgress = { alphaAnimationProgress.value },
            sizeAnimationProgress = { sizeAnimationProgress.value },
        )
    }
}

@Composable
private fun NavigationBarItemLayout(
    indicatorRipple: @Composable () -> Unit,
    indicator: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable (() -> Unit)?,
    alwaysShowLabel: Boolean,
    alphaAnimationProgress: () -> Float,
    sizeAnimationProgress: () -> Float,
) {
    Layout(
        modifier = Modifier.badgeBounds(),
        content = {
            indicatorRipple()
            indicator()

            Box(Modifier.layoutId(IconLayoutIdTag)) { icon() }

            if (label != null) {
                Box(
                    Modifier
                        .layoutId(LabelLayoutIdTag)
                        .graphicsLayer {
                            alpha = if (alwaysShowLabel) 1f else alphaAnimationProgress()
                        }
                ) {
                    label()
                }
            }
        },
    ) { measurables, constraints ->
        @Suppress("NAME_SHADOWING")
        // Ensure that the progress is >= 0. It may be negative on bouncy springs, for example.
        val animationProgress = sizeAnimationProgress().coerceAtLeast(0f)
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val iconPlaceable =
            measurables.fastFirst { it.layoutId == IconLayoutIdTag }.measure(looseConstraints)

        val totalIndicatorWidth = iconPlaceable.width + (IndicatorHorizontalPadding * 2).roundToPx()
        val animatedIndicatorWidth = (totalIndicatorWidth * animationProgress).roundToInt()
        val indicatorHeight = iconPlaceable.height + (IndicatorVerticalPadding * 2).roundToPx()
        val indicatorRipplePlaceable =
            measurables
                .fastFirst { it.layoutId == IndicatorRippleLayoutIdTag }
                .measure(Constraints.fixed(width = totalIndicatorWidth, height = indicatorHeight))
        val indicatorPlaceable =
            measurables
                .fastFirstOrNull { it.layoutId == IndicatorLayoutIdTag }
                ?.measure(
                    Constraints.fixed(width = animatedIndicatorWidth, height = indicatorHeight)
                )

        val labelPlaceable =
            label?.let {
                measurables.fastFirst { it.layoutId == LabelLayoutIdTag }.measure(looseConstraints)
            }

        if (label == null) {
            placeIcon(iconPlaceable, indicatorRipplePlaceable, indicatorPlaceable, constraints)
        } else {
            placeLabelAndIcon(
                labelPlaceable!!,
                iconPlaceable,
                indicatorRipplePlaceable,
                indicatorPlaceable,
                constraints,
                alwaysShowLabel,
                animationProgress,
            )
        }
    }
}

/** Places the provided [Placeable]s in the center of the provided [constraints]. */
private fun MeasureScope.placeIcon(
    iconPlaceable: Placeable,
    indicatorRipplePlaceable: Placeable,
    indicatorPlaceable: Placeable?,
    constraints: Constraints,
): MeasureResult {
    val width =
        if (constraints.maxWidth == Constraints.Infinity) {
            iconPlaceable.width + NavigationBarItemToIconMinimumPadding.roundToPx() * 2
        } else {
            constraints.maxWidth
        }
    val height = constraints.constrainHeight(NavigationBarHeight.roundToPx())

    val iconX = (width - iconPlaceable.width) / 2
    val iconY = (height - iconPlaceable.height) / 2

    val rippleX = (width - indicatorRipplePlaceable.width) / 2
    val rippleY = (height - indicatorRipplePlaceable.height) / 2

    return layout(width, height) {
        indicatorPlaceable?.let {
            val indicatorX = (width - it.width) / 2
            val indicatorY = (height - it.height) / 2
            it.placeRelative(indicatorX, indicatorY)
        }
        iconPlaceable.placeRelative(iconX, iconY)
        indicatorRipplePlaceable.placeRelative(rippleX, rippleY)
    }
}

/**
 * Places the provided [Placeable]s in the correct position, depending on [alwaysShowLabel] and
 * [animationProgress].
 *
 * When [alwaysShowLabel] is true, the positions do not move. The [iconPlaceable] and
 * [labelPlaceable] will be placed together in the center with padding between them, according to
 * the spec.
 *
 * When [animationProgress] is 1 (representing the selected state), the positions will be the same
 * as above.
 *
 * Otherwise, when [animationProgress] is 0, [iconPlaceable] will be placed in the center, like in
 * [placeIcon], and [labelPlaceable] will not be shown.
 *
 * When [animationProgress] is animating between these values, [iconPlaceable] and [labelPlaceable]
 * will be placed at a corresponding interpolated position.
 *
 * [indicatorRipplePlaceable] and [indicatorPlaceable] will always be placed in such a way that to
 * share the same center as [iconPlaceable].
 *
 * @param labelPlaceable text label placeable inside this item
 * @param iconPlaceable icon placeable inside this item
 * @param indicatorRipplePlaceable indicator ripple placeable inside this item
 * @param indicatorPlaceable indicator placeable inside this item, if it exists
 * @param constraints constraints of the item
 * @param alwaysShowLabel whether to always show the label for this item. If true, icon and label
 *   positions will not change. If false, positions transition between 'centered icon with no label'
 *   and 'top aligned icon with label'.
 * @param animationProgress progress of the animation, where 0 represents the unselected state of
 *   this item and 1 represents the selected state. Values between 0 and 1 interpolate positions of
 *   the icon and label.
 */
private fun MeasureScope.placeLabelAndIcon(
    labelPlaceable: Placeable,
    iconPlaceable: Placeable,
    indicatorRipplePlaceable: Placeable,
    indicatorPlaceable: Placeable?,
    constraints: Constraints,
    alwaysShowLabel: Boolean,
    animationProgress: Float,
): MeasureResult {
    val contentHeight =
        iconPlaceable.height +
                IndicatorVerticalPadding.toPx() +
                NavigationBarIndicatorToLabelPadding.toPx() +
                labelPlaceable.height
    val contentVerticalPadding =
        ((constraints.minHeight - contentHeight) / 2).coerceAtLeast(IndicatorVerticalPadding.toPx())
    val height = contentHeight + contentVerticalPadding * 2

    // Icon (when selected) should be `contentVerticalPadding` from top
    val selectedIconY = contentVerticalPadding
    val unselectedIconY =
        if (alwaysShowLabel) selectedIconY else (height - iconPlaceable.height) / 2

    // How far the icon needs to move between unselected and selected states.
    val iconDistance = unselectedIconY - selectedIconY

    // The interpolated fraction of iconDistance that all placeables need to move based on
    // animationProgress.
    val offset = iconDistance * (1 - animationProgress)

    // Label should be fixed padding below icon
    val labelY =
        selectedIconY +
                iconPlaceable.height +
                IndicatorVerticalPadding.toPx() +
                NavigationBarIndicatorToLabelPadding.toPx()

    val containerWidth =
        if (constraints.maxWidth == Constraints.Infinity) {
            iconPlaceable.width + NavigationBarItemToIconMinimumPadding.roundToPx() * 2
        } else {
            constraints.maxWidth
        }

    val labelX = (containerWidth - labelPlaceable.width) / 2
    val iconX = (containerWidth - iconPlaceable.width) / 2

    val rippleX = (containerWidth - indicatorRipplePlaceable.width) / 2
    val rippleY = selectedIconY - IndicatorVerticalPadding.toPx()

    return layout(containerWidth, height.roundToInt()) {
        indicatorPlaceable?.let {
            val indicatorX = (containerWidth - it.width) / 2
            val indicatorY = selectedIconY - IndicatorVerticalPadding.roundToPx()
            it.placeRelative(indicatorX, (indicatorY + offset).roundToInt())
        }
        if (alwaysShowLabel || animationProgress != 0f) {
            labelPlaceable.placeRelative(labelX, (labelY + offset).roundToInt())
        }
        iconPlaceable.placeRelative(iconX, (selectedIconY + offset).roundToInt())
        indicatorRipplePlaceable.placeRelative(rippleX, (rippleY + offset).roundToInt())
    }
}

private const val IndicatorRippleLayoutIdTag: String = "indicatorRipple"

private const val IndicatorLayoutIdTag: String = "indicator"

private const val IconLayoutIdTag: String = "icon"

private const val LabelLayoutIdTag: String = "label"

private val NavigationBarHeight: Dp = NavigationBarTokens.TallContainerHeight

/*@VisibleForTesting*/
internal val NavigationBarIndicatorToLabelPadding: Dp = 4.dp

private val IndicatorHorizontalPadding: Dp =
    (NavigationBarVerticalItemTokens.ActiveIndicatorWidth -
            NavigationBarVerticalItemTokens.IconSize) / 2

/*@VisibleForTesting*/
internal val IndicatorVerticalPadding: Dp =
    (NavigationBarVerticalItemTokens.ActiveIndicatorHeight -
            NavigationBarVerticalItemTokens.IconSize) / 2

private val IndicatorVerticalOffset: Dp = 12.dp

/*@VisibleForTesting*/
internal val NavigationBarItemToIconMinimumPadding: Dp = 44.dp

internal object NavigationBarTokens {
    val ItemActiveIndicatorShape @Composable get() = AutoCornersShape(16.dp)
    val TallContainerHeight = 80.0.dp
    val LabelTextFont @Composable get() = MaterialTheme.typography.labelMedium
}

internal object NavigationBarVerticalItemTokens {
    val ActiveIndicatorHeight = 32.0.dp
    val ActiveIndicatorWidth = 56.0.dp
    val IconSize = 24.0.dp
}

@Stable
internal fun NavigationBarItemColors.iconColor(selected: Boolean, enabled: Boolean): Color =
    when {
        !enabled -> disabledIconColor
        selected -> selectedIconColor
        else -> unselectedIconColor
    }

@Stable
internal fun NavigationBarItemColors.textColor(selected: Boolean, enabled: Boolean): Color =
    when {
        !enabled -> disabledTextColor
        selected -> selectedTextColor
        else -> unselectedTextColor
    }