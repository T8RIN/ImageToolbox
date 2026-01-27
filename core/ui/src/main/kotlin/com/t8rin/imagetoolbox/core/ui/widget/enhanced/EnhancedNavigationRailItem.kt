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
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.HorizontalRuler
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.VerticalRuler
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFirstOrNull
import com.t8rin.imagetoolbox.core.ui.theme.DisabledAlpha
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt

@Composable
fun EnhancedNavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
    colors: NavigationRailItemColors = NavigationRailItemDefaults.colors(),
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
                val style = NavigationRailVerticalItemTokens.LabelTextFont
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
            .defaultMinSize(minHeight = NavigationRailItemHeight)
            .widthIn(min = NavigationRailItemWidth),
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
                val itemWidth = NavigationRailItemWidth.roundToPx()
                val indicatorWidth =
                    NavigationRailVerticalItemTokens.ActiveIndicatorWidth.roundToPx()
                Offset((itemWidth - indicatorWidth).toFloat() / 2, 0f)
            }
        }
        val offsetInteractionSource =
            remember(interactionSource, calculateDeltaOffset) {
                MappedInteractionSource(interactionSource, calculateDeltaOffset)
            }

        val indicatorShape = shapeByInteraction(
            shape = NavigationRailBaselineItemTokens.ActiveIndicatorShape,
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
                        .background(color = colors.indicatorColor, shape = indicatorShape)
                )
            }

        NavigationRailItemLayout(
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

object NavigationRailItemDefaults {
    @Composable
    fun colors(
        selectedIconColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        selectedTextColor: Color = MaterialTheme.colorScheme.secondary,
        indicatorColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        unselectedIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = DisabledAlpha),
        disabledTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = DisabledAlpha),
    ): NavigationRailItemColors = NavigationRailItemColors(
        selectedIconColor = selectedIconColor,
        selectedTextColor = selectedTextColor,
        selectedIndicatorColor = indicatorColor,
        unselectedIconColor = unselectedIconColor,
        unselectedTextColor = unselectedTextColor,
        disabledIconColor = disabledIconColor,
        disabledTextColor = disabledTextColor,
    )

}

@Immutable
class NavigationRailItemColors(
    val selectedIconColor: Color,
    val selectedTextColor: Color,
    val selectedIndicatorColor: Color,
    val unselectedIconColor: Color,
    val unselectedTextColor: Color,
    val disabledIconColor: Color,
    val disabledTextColor: Color,
) {
    /**
     * Returns a copy of this NavigationRailItemColors, optionally overriding some of the values.
     * This uses the Color.Unspecified to mean “use the value from the source”
     */
    fun copy(
        selectedIconColor: Color = this.selectedIconColor,
        selectedTextColor: Color = this.selectedTextColor,
        selectedIndicatorColor: Color = this.selectedIndicatorColor,
        unselectedIconColor: Color = this.unselectedIconColor,
        unselectedTextColor: Color = this.unselectedTextColor,
        disabledIconColor: Color = this.disabledIconColor,
        disabledTextColor: Color = this.disabledTextColor,
    ) =
        NavigationRailItemColors(
            selectedIconColor.takeOrElse { this.selectedIconColor },
            selectedTextColor.takeOrElse { this.selectedTextColor },
            selectedIndicatorColor.takeOrElse { this.selectedIndicatorColor },
            unselectedIconColor.takeOrElse { this.unselectedIconColor },
            unselectedTextColor.takeOrElse { this.unselectedTextColor },
            disabledIconColor.takeOrElse { this.disabledIconColor },
            disabledTextColor.takeOrElse { this.disabledTextColor },
        )

    /**
     * Represents the icon color for this item, depending on whether it is [selected].
     *
     * @param selected whether the item is selected
     * @param enabled whether the item is enabled
     */
    @Stable
    internal fun iconColor(selected: Boolean, enabled: Boolean): Color =
        when {
            !enabled -> disabledIconColor
            selected -> selectedIconColor
            else -> unselectedIconColor
        }

    /**
     * Represents the text color for this item, depending on whether it is [selected].
     *
     * @param selected whether the item is selected
     * @param enabled whether the item is enabled
     */
    @Stable
    internal fun textColor(selected: Boolean, enabled: Boolean): Color =
        when {
            !enabled -> disabledTextColor
            selected -> selectedTextColor
            else -> unselectedTextColor
        }

    /** Represents the color of the indicator used for selected items. */
    internal val indicatorColor: Color
        get() = selectedIndicatorColor

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is NavigationRailItemColors) return false

        if (selectedIconColor != other.selectedIconColor) return false
        if (unselectedIconColor != other.unselectedIconColor) return false
        if (selectedTextColor != other.selectedTextColor) return false
        if (unselectedTextColor != other.unselectedTextColor) return false
        if (selectedIndicatorColor != other.selectedIndicatorColor) return false
        if (disabledIconColor != other.disabledIconColor) return false
        if (disabledTextColor != other.disabledTextColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = selectedIconColor.hashCode()
        result = 31 * result + unselectedIconColor.hashCode()
        result = 31 * result + selectedTextColor.hashCode()
        result = 31 * result + unselectedTextColor.hashCode()
        result = 31 * result + selectedIndicatorColor.hashCode()
        result = 31 * result + disabledIconColor.hashCode()
        result = 31 * result + disabledTextColor.hashCode()

        return result
    }
}

@Composable
private fun NavigationRailItemLayout(
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
        val indicatorVerticalPadding =
            if (label == null) {
                IndicatorVerticalPaddingNoLabel
            } else {
                IndicatorVerticalPaddingWithLabel
            }
        val indicatorHeight = iconPlaceable.height + (indicatorVerticalPadding * 2).roundToPx()

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
        constraints.constrainWidth(
            maxOf(
                iconPlaceable.width,
                indicatorRipplePlaceable.width,
                indicatorPlaceable?.width ?: 0,
            )
        )
    val height = constraints.constrainHeight(NavigationRailItemHeight.roundToPx())

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
                IndicatorVerticalPaddingWithLabel.toPx() +
                NavigationRailItemVerticalPadding.toPx() +
                labelPlaceable.height
    val contentVerticalPadding =
        ((constraints.minHeight - contentHeight) / 2).coerceAtLeast(
            IndicatorVerticalPaddingWithLabel.toPx()
        )
    val height = contentHeight + contentVerticalPadding * 2

    // Icon (when selected) should be `contentVerticalPadding` from the top
    val selectedIconY = contentVerticalPadding
    val unselectedIconY =
        if (alwaysShowLabel) selectedIconY else (height - iconPlaceable.height) / 2

    // How far the icon needs to move between unselected and selected states
    val iconDistance = unselectedIconY - selectedIconY

    // The interpolated fraction of iconDistance that all placeables need to move based on
    // animationProgress, since the icon is higher in the selected state.
    val offset = iconDistance * (1 - animationProgress)

    // Label should be fixed padding below icon
    val labelY =
        selectedIconY +
                iconPlaceable.height +
                IndicatorVerticalPaddingWithLabel.toPx() +
                NavigationRailItemVerticalPadding.toPx()

    val width =
        constraints.constrainWidth(
            maxOf(iconPlaceable.width, labelPlaceable.width, indicatorPlaceable?.width ?: 0)
        )
    val labelX = (width - labelPlaceable.width) / 2
    val iconX = (width - iconPlaceable.width) / 2
    val rippleX = (width - indicatorRipplePlaceable.width) / 2
    val rippleY = selectedIconY - IndicatorVerticalPaddingWithLabel.toPx()

    return layout(width, height.roundToInt()) {
        indicatorPlaceable?.let {
            val indicatorX = (width - it.width) / 2
            val indicatorY = selectedIconY - IndicatorVerticalPaddingWithLabel.toPx()
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

internal val NavigationRailItemWidth: Dp = NavigationRailCollapsedTokens.NarrowContainerWidth

internal val NavigationRailItemHeight: Dp = NavigationRailVerticalItemTokens.ActiveIndicatorWidth

internal val NavigationRailItemVerticalPadding: Dp = 4.dp

private val IndicatorHorizontalPadding: Dp =
    (NavigationRailVerticalItemTokens.ActiveIndicatorWidth -
            NavigationRailBaselineItemTokens.IconSize) / 2

private val IndicatorVerticalPaddingWithLabel: Dp =
    (NavigationRailVerticalItemTokens.ActiveIndicatorHeight -
            NavigationRailBaselineItemTokens.IconSize) / 2

private val IndicatorVerticalPaddingNoLabel: Dp =
    (NavigationRailVerticalItemTokens.ActiveIndicatorWidth -
            NavigationRailBaselineItemTokens.IconSize) / 2

internal val BadgeTopRuler = HorizontalRuler()
internal val BadgeEndRuler = VerticalRuler()

internal fun Modifier.badgeBounds() =
    this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(
            width = placeable.width,
            height = placeable.height,
            rulers = {
                // use provides instead of provideRelative cause we will place relative
                // in the badge code
                BadgeEndRuler provides coordinates.size.width.toFloat()
                BadgeTopRuler provides 0f
            },
        ) {
            placeable.place(0, 0)
        }
    }

internal object NavigationRailVerticalItemTokens {
    val ActiveIndicatorHeight = 32.0.dp
    val ActiveIndicatorWidth = 56.0.dp
    val LabelTextFont @Composable get() = MaterialTheme.typography.labelMedium
}

internal object NavigationRailBaselineItemTokens {
    val ActiveIndicatorShape @Composable get() = AutoCornersShape(16.dp)
    val IconSize = 24.0.dp
}

internal object NavigationRailCollapsedTokens {
    val NarrowContainerWidth = 80.0.dp
}

internal class MappedInteractionSource(
    underlyingInteractionSource: InteractionSource,
    private val calculateDelta: () -> Offset,
) : InteractionSource {
    private val mappedPresses = mutableMapOf<PressInteraction.Press, PressInteraction.Press>()

    override val interactions =
        underlyingInteractionSource.interactions.map { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    val mappedPress = mapPress(interaction)
                    mappedPresses[interaction] = mappedPress
                    mappedPress
                }

                is PressInteraction.Cancel -> {
                    val mappedPress = mappedPresses.remove(interaction.press)
                    if (mappedPress == null) {
                        interaction
                    } else {
                        PressInteraction.Cancel(mappedPress)
                    }
                }

                is PressInteraction.Release -> {
                    val mappedPress = mappedPresses.remove(interaction.press)
                    if (mappedPress == null) {
                        interaction
                    } else {
                        PressInteraction.Release(mappedPress)
                    }
                }

                else -> interaction
            }
        }

    private fun mapPress(press: PressInteraction.Press): PressInteraction.Press =
        PressInteraction.Press(press.pressPosition - calculateDelta())
}