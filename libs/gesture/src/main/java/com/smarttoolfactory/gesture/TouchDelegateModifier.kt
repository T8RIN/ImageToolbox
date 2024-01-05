package com.smarttoolfactory.gesture

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 * Modifier to handle situations where you want a view to have a larger touch area than
 * its actual Composable bounds. [dpRect] increases when values are positive and
 * decreases touch area by negative values
 * entered for any side
 * @param dpRect rectangle that contains left, right, top, and bottom offets to increase or
 * decrease touch area
 * @param enabled Controls the enabled state. When `false`, [onClick], and this modifier will
 * appear disabled for accessibility services
 * @param onClickLabel semantic / accessibility label for the [onClick] action
 * @param role the type of user interface element. Accessibility services might use this
 * to describe the element or do customizations
 * @param onClick will be called when user clicks on the element
 */
fun Modifier.touchDelegate(
    dpRect: DelegateRect = DelegateRect.Zero,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
) =
    composed(
        inspectorInfo = {
            name = "touchDelegate"
            properties["dpRect"] = dpRect
            properties["enabled"] = enabled
            properties["onClickLabel"] = onClickLabel
            properties["role"] = role
            properties["onClick"] = onClick
        },
        factory = {

            Modifier.touchDelegate(
                dpRect = dpRect,
                enabled = enabled,
                onClickLabel = onClickLabel,
                onClick = onClick,
                role = role,
                indication = LocalIndication.current,
                interactionSource = remember { MutableInteractionSource() }
            )
        }
    )

/**
 * Modifier to handle situations where you want a view to have a larger touch area than
 * its actual Composable bounds. [dpRect] increases when values are positive and
 * decreases touch area by negative values
 * entered for any side
 * @param dpRect rectangle that contains left, right, top, and bottom offets to increase or
 * decrease touch area
 * @param interactionSource [MutableInteractionSource] that will be used to dispatch
 * [PressInteraction.Press] when this clickable is pressed. Only the initial (first) press will be
 * recorded and dispatched with [MutableInteractionSource].
 * @param indication indication to be shown when modified element is pressed. Be default,
 * indication from [LocalIndication] will be used. Pass `null` to show no indication, or
 * current value from [LocalIndication] to show theme default
 * @param enabled Controls the enabled state. When `false`, [onClick], and this modifier will
 * appear disabled for accessibility services
 * @param onClickLabel semantic / accessibility label for the [onClick] action
 * @param role the type of user interface element. Accessibility services might use this
 * to describe the element or do customizations
 * @param onClick will be called when user clicks on the element
 */
fun Modifier.touchDelegate(
    dpRect: DelegateRect = DelegateRect.Zero,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource,
    indication: Indication?,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
) =
    composed(
        inspectorInfo = {
            name = "touchDelegate"
            properties["dpRect"] = dpRect
            properties["enabled"] = enabled
            properties["onClickLabel"] = onClickLabel
            properties["role"] = role
            properties["onClick"] = onClick
        },
        factory = {

            val density = LocalDensity.current

            var initialSize by remember {
                mutableStateOf(IntSize.Zero)
            }

            val updatedRect = remember(dpRect) {
                with(density) {
                    RectF(
                        left = dpRect.left.toPx(),
                        top = dpRect.top.toPx(),
                        right = dpRect.right.toPx(),
                        bottom = dpRect.bottom.toPx(),
                    )
                }
            }

            val scale = remember(initialSize, updatedRect) {
                getScale(initialSize, updatedRect)
            }


            Modifier
                .graphicsLayer {
                    scaleX = scale.x
                    scaleY = scale.y
                    this.translationX = -updatedRect.left
                    this.translationY = -updatedRect.top

                    transformOrigin = TransformOrigin(0f, 0f)
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = indication,
                    enabled = enabled,
                    onClickLabel = onClickLabel,
                    role = role,
                    onClick = onClick
                )
                .graphicsLayer {
                    val scaleX = if (scale.x == 0f) 1f else 1 / scale.x
                    val scaleY = if (scale.y == 0f) 1f else 1 / scale.y
                    this.scaleX = scaleX
                    this.scaleY = scaleY
                    this.translationX = (updatedRect.left) * scaleX
                    this.translationY = (updatedRect.top) * scaleY
                    transformOrigin = TransformOrigin(0f, 0f)
                }
                .onSizeChanged {
                    initialSize = it
                }
        }
    )

private fun getScale(initialSize: IntSize, updatedRect: RectF): Offset =
    if (initialSize.width == 0 ||
        initialSize.height == 0
    ) {
        Offset(1f, 1f)
    } else {
        val initialWidth = initialSize.width
        val initialHeight = initialSize.height
        val scaleX =
            ((updatedRect.left + updatedRect.right + initialWidth) / initialWidth)
                .coerceAtLeast(0f)
        val scaleY =
            ((updatedRect.top + updatedRect.bottom + initialHeight) / initialHeight)
                .coerceAtLeast(0f)
        Offset(scaleX, scaleY)
    }


@Immutable
data class DelegateRect(
    val left: Dp = 0.dp,
    val top: Dp = 0.dp,
    val right: Dp = 0.dp,
    val bottom: Dp = 0.dp
) {
    companion object {
        val Zero = DelegateRect()
    }
}

@Immutable
data class RectF(
    val left: Float = 0f,
    val top: Float = 0f,
    val right: Float = 0f,
    val bottom: Float = 0f
)
