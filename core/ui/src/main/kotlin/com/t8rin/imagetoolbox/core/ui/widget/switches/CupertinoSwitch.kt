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

@file:Suppress("KDocUnresolvedReference")

package com.t8rin.imagetoolbox.core.ui.widget.switches

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.rememberCanvasBackdrop
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

/**
 * Cupertino Design Switch.
 *
 * Switches toggle the state of a single item on or off.
 *
 * @param checked whether or not this switch is checked
 * @param onCheckedChange called when this switch is clicked. If `null`, then this switch will not
 * be intractable, unless something else handles its input events and updates its state.
 * @param modifier the [Modifier] to be applied to this switch
 * @param enabled controls the enabled state of this switch. When `false`, this component will not
 * respond to user input, and it will appear visually disabled and disabled to accessibility
 * services.
 * @param colors [CupertinoSwitchColors] that will be used to resolve the colors used for this switch in
 * different states. See [CupertinoSwitchDefaults.colors].
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 * for this switch. You can create and pass in your own `remember`ed instance to observe
 * [Interaction]s and customize the appearance / behavior of this switch in different states.
 */
@Composable
fun CupertinoSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    colors: CupertinoSwitchColors = CupertinoSwitchDefaults.colors(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null
) {
    val realInteractionSource = interactionSource ?: remember { MutableInteractionSource() }

    val isPressed by realInteractionSource.collectIsPressedAsState()
    val isDragged by realInteractionSource.collectIsDraggedAsState()

    val animatedAspectRatio by animateFloatAsState(
        targetValue = if (isPressed || isDragged) 1.25f else 1f,
        animationSpec = AspectRationAnimationSpec
    )
    val animatedBackground by animateColorAsState(
        targetValue = colors.trackColor(enabled, checked).value,
        animationSpec = ColorAnimationSpec
    )

    var alignment by remember(checked) {
        mutableFloatStateOf(
            if (checked) 1f else -1f
        )
    }

    val state = rememberDraggableState {
        alignment = (alignment + it).coerceIn(-1f, 1f)
    }

    val animatedAlignment by animateFloatAsState(
        targetValue = alignment,
        animationSpec = AlignmentAnimationSpec
    )

    Column(
        modifier
            .toggleable(
                value = checked,
                onValueChange = {
                    onCheckedChange?.invoke(it)
                },
                enabled = enabled,
                role = Role.Switch,
                interactionSource = realInteractionSource,
                indication = null
            )
            .draggable(
                state = state,
                orientation = Orientation.Horizontal,
                interactionSource = realInteractionSource,
                enabled = enabled,
                onDragStopped = {
                    if (alignment < 1 / 2f) {
                        alignment = -1f
                        if (checked) onCheckedChange?.invoke(false)
                    } else {
                        alignment = 1f
                        if (!checked) onCheckedChange?.invoke(true)
                    }
                }
            )
            .wrapContentSize(Alignment.Center)
            .requiredSize(CupertinoSwitchDefaults.Width, CupertinoSwitchDefaults.height)
            .clip(CupertinoSwitchDefaults.Shape)
            .background(animatedBackground)
            .padding(2.dp),
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .aspectRatio(animatedAspectRatio)
                .align(BiasAlignment.Horizontal(animatedAlignment))
                .container(
                    shape = CupertinoSwitchDefaults.Shape,
                    resultPadding = 0.dp,
                    autoShadowElevation = animateDpAsState(
                        if (enabled && LocalSettingsState.current.drawSwitchShadows) {
                            CupertinoSwitchDefaults.EnabledThumbElevation
                        } else 0.dp
                    ).value,
                    borderColor = Color.Transparent,
                    isShadowClip = true,
                    isStandaloneContainer = false,
                    color = colors.thumbColor(enabled).value
                )
        )
    }
}

@Composable
fun LiquidGlassSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    internalModifier: Modifier = Modifier,
    colors: CupertinoSwitchColors = CupertinoSwitchDefaults.colors(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    backgroundColor: Color
) {
    val realInteractionSource = interactionSource ?: remember { MutableInteractionSource() }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            FallbackLiquidGlassSwitch(
                checked = checked,
                enabled = false,
                onCheckedChange = null,
                modifier = internalModifier,
                colors = CupertinoSwitchDefaults.transparentColors(),
                interactionSource = realInteractionSource
            )

            LiquidToggle(
                checked = { checked },
                onCheckedChange = onCheckedChange,
                backdrop = rememberCanvasBackdrop { drawRect(backgroundColor) },
                enabled = enabled,
                colors = colors,
                interactionSource = realInteractionSource,
                modifier = modifier,
            )
        }
    } else {
        FallbackLiquidGlassSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = internalModifier,
            colors = colors,
            enabled = enabled,
            interactionSource = realInteractionSource
        )
    }
}

@Composable
private fun FallbackLiquidGlassSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    colors: CupertinoSwitchColors = CupertinoSwitchDefaults.colors(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource
) {
    val isPressed by interactionSource.collectIsPressedAsState()
    val isDragged by interactionSource.collectIsDraggedAsState()

    val animatedAspectRatio by animateFloatAsState(
        targetValue = if (isPressed || isDragged) 1.8f else 1.6f,
        animationSpec = AspectRationAnimationSpec
    )
    val animatedBackground by animateColorAsState(
        targetValue = colors.trackColor(enabled, checked).value,
        animationSpec = ColorAnimationSpec
    )

    var alignment by remember(checked) {
        mutableFloatStateOf(
            if (checked) 1f else -1f
        )
    }

    val state = rememberDraggableState {
        alignment = (alignment + it).coerceIn(-1f, 1f)
    }

    val animatedAlignment by animateFloatAsState(
        targetValue = alignment,
        animationSpec = AlignmentAnimationSpec
    )

    Column(
        modifier
            .toggleable(
                value = checked,
                onValueChange = {
                    onCheckedChange?.invoke(it)
                },
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null
            )
            .draggable(
                state = state,
                orientation = Orientation.Horizontal,
                interactionSource = interactionSource,
                enabled = enabled,
                onDragStopped = {
                    if (alignment < 1 / 2f) {
                        alignment = -1f
                        if (checked) onCheckedChange?.invoke(false)
                    } else {
                        alignment = 1f
                        if (!checked) onCheckedChange?.invoke(true)
                    }
                }
            )
            .wrapContentSize(Alignment.Center)
            .requiredSize(
                width = CupertinoSwitchDefaults.LiquidWidth,
                height = CupertinoSwitchDefaults.LiquidHeight
            )
            .clip(CupertinoSwitchDefaults.Shape)
            .background(animatedBackground)
            .padding(2.dp),
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .aspectRatio(animatedAspectRatio)
                .align(BiasAlignment.Horizontal(animatedAlignment))
                .container(
                    shape = CupertinoSwitchDefaults.Shape,
                    resultPadding = 0.dp,
                    autoShadowElevation = animateDpAsState(
                        if (enabled && LocalSettingsState.current.drawSwitchShadows) {
                            CupertinoSwitchDefaults.EnabledThumbElevation
                        } else 0.dp
                    ).value,
                    borderColor = Color.Transparent,
                    isShadowClip = true,
                    isStandaloneContainer = false,
                    color = colors.thumbColor(enabled).value
                )
        )
    }
}

/**
 * Represents the colors used by a [CupertinoSwitch] in different states
 *
 * See [CupertinoSwitchDefaults.colors] for the default implementation that follows Material
 * specifications.
 */
@Immutable
class CupertinoSwitchColors internal constructor(
    private val thumbColor: Color,
    private val disabledThumbColor: Color,
    private val checkedTrackColor: Color,
    private val checkedIconColor: Color,
    private val uncheckedTrackColor: Color,
    private val uncheckedIconColor: Color,
    private val disabledCheckedTrackColor: Color,
    private val disabledCheckedIconColor: Color,
    private val disabledUncheckedTrackColor: Color,
    private val disabledUncheckedIconColor: Color
) {
    /**
     * Represents the color used for the switch's thumb, depending on [enabled] and [checked].
     *
     * @param enabled whether the Switch is enabled or not
     */
    @Composable
    internal fun thumbColor(enabled: Boolean): State<Color> {
        return animateColorAsState(
            targetValue = if (enabled) {
                thumbColor
            } else {
                disabledThumbColor
            },
            animationSpec = ColorAnimationSpec
        )
    }

    /**
     * Represents the color used for the switch's track, depending on [enabled] and [checked].
     *
     * @param enabled whether the Switch is enabled or not
     * @param checked whether the Switch is checked or not
     */
    @Composable
    internal fun trackColor(
        enabled: Boolean,
        checked: Boolean
    ): State<Color> {
        return animateColorAsState(
            targetValue = if (enabled) {
                if (checked) checkedTrackColor else uncheckedTrackColor
            } else {
                if (checked) disabledCheckedTrackColor else disabledUncheckedTrackColor
            },
            animationSpec = ColorAnimationSpec
        )
    }

    /**
     * Represents the content color passed to the icon if used
     *
     * @param enabled whether the Switch is enabled or not
     * @param checked whether the Switch is checked or not
     */
    @Composable
    internal fun iconColor(
        enabled: Boolean,
        checked: Boolean
    ): State<Color> {
        return animateColorAsState(
            targetValue = if (enabled) {
                if (checked) checkedIconColor else uncheckedIconColor
            } else {
                if (checked) disabledCheckedIconColor else disabledUncheckedIconColor
            },
            animationSpec = ColorAnimationSpec
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is CupertinoSwitchColors) return false

        if (thumbColor != other.thumbColor) return false
        if (checkedTrackColor != other.checkedTrackColor) return false
        if (checkedIconColor != other.checkedIconColor) return false
        if (uncheckedTrackColor != other.uncheckedTrackColor) return false
        if (uncheckedIconColor != other.uncheckedIconColor) return false
        if (disabledThumbColor != other.disabledThumbColor) return false
        if (disabledCheckedTrackColor != other.disabledCheckedTrackColor) return false
        if (disabledCheckedIconColor != other.disabledCheckedIconColor) return false
        if (disabledUncheckedTrackColor != other.disabledUncheckedTrackColor) return false
        if (disabledUncheckedIconColor != other.disabledUncheckedIconColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = thumbColor.hashCode()
        result = 31 * result + checkedTrackColor.hashCode()
        result = 31 * result + checkedIconColor.hashCode()
        result = 31 * result + uncheckedTrackColor.hashCode()
        result = 31 * result + uncheckedIconColor.hashCode()
        result = 31 * result + disabledThumbColor.hashCode()
        result = 31 * result + disabledCheckedTrackColor.hashCode()
        result = 31 * result + disabledCheckedIconColor.hashCode()
        result = 31 * result + disabledUncheckedTrackColor.hashCode()
        result = 31 * result + disabledUncheckedIconColor.hashCode()
        return result
    }
}

@Immutable
object CupertinoSwitchDefaults {

    internal val EnabledThumbElevation = 4.dp

    val Width: Dp = 51.dp

    val height: Dp = 31.dp

    val LiquidWidth: Dp = 64.dp

    val LiquidHeight: Dp = 28.dp

    internal val Shape: CornerBasedShape @Composable get() = ShapeDefaults.circle

    @Composable
    @ReadOnlyComposable
    fun colors(
        thumbColor: Color = if (LocalSettingsState.current.isNightMode) {
            MaterialTheme.colorScheme.onSurface
        } else MaterialTheme.colorScheme.surfaceContainerLowest,
        disabledThumbColor: Color = thumbColor,
        checkedTrackColor: Color = if (LocalSettingsState.current.isNightMode) {
            MaterialTheme.colorScheme.primary.blend(Color.Black, 0.5f)
        } else MaterialTheme.colorScheme.primary,
        checkedIconColor: Color = MaterialTheme.colorScheme.outlineVariant,
        uncheckedTrackColor: Color = MaterialTheme.colorScheme.outline.copy(
            alpha = .33f
        ),
        uncheckedIconColor: Color = checkedIconColor,
        disabledCheckedTrackColor: Color = checkedTrackColor.copy(alpha = .33f),
        disabledCheckedIconColor: Color = checkedIconColor,
        disabledUncheckedTrackColor: Color = uncheckedTrackColor,
        disabledUncheckedIconColor: Color = checkedIconColor,
    ): CupertinoSwitchColors = CupertinoSwitchColors(
        thumbColor = thumbColor,
        disabledThumbColor = disabledThumbColor,
        checkedTrackColor = checkedTrackColor,
        checkedIconColor = checkedIconColor,
        uncheckedTrackColor = uncheckedTrackColor,
        uncheckedIconColor = uncheckedIconColor,
        disabledCheckedTrackColor = disabledCheckedTrackColor,
        disabledCheckedIconColor = disabledCheckedIconColor,
        disabledUncheckedTrackColor = disabledUncheckedTrackColor,
        disabledUncheckedIconColor = disabledUncheckedIconColor
    )

    @Composable
    @ReadOnlyComposable
    fun transparentColors() = colors(
        thumbColor = Color.Transparent,
        disabledThumbColor = Color.Transparent,
        checkedTrackColor = Color.Transparent,
        checkedIconColor = Color.Transparent,
        uncheckedTrackColor = Color.Transparent,
        uncheckedIconColor = Color.Transparent,
        disabledCheckedTrackColor = Color.Transparent,
        disabledCheckedIconColor = Color.Transparent,
        disabledUncheckedTrackColor = Color.Transparent,
        disabledUncheckedIconColor = Color.Transparent
    )
}

private val AspectRationAnimationSpec = tween<Float>(durationMillis = 300)
private val ColorAnimationSpec = tween<Color>(durationMillis = 300)
private val AlignmentAnimationSpec = AspectRationAnimationSpec