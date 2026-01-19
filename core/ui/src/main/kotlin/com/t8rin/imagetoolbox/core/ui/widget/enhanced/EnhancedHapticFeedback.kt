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

@file:Suppress("DEPRECATION")
@file:SuppressLint("UnnecessaryComposedModifier")

package com.t8rin.imagetoolbox.core.ui.widget.enhanced

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.core.content.getSystemService
import com.t8rin.logger.makeLog

private fun View.vibrate() =
    reallyPerformHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)

private fun View.vibrateStrong() = reallyPerformHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

private fun View.reallyPerformHapticFeedback(feedbackConstant: Int) {
    runCatching {
        if (context.isTouchExplorationEnabled()) return

        isHapticFeedbackEnabled = true

        performHapticFeedback(feedbackConstant, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
    }.onFailure { it.makeLog("reallyPerformHapticFeedback feedbackConstant = $feedbackConstant") }
}

private fun Context.isTouchExplorationEnabled(): Boolean =
    getSystemService<AccessibilityManager>()?.isTouchExplorationEnabled == true

@SuppressLint("InlinedApi")
internal data class EnhancedHapticFeedback(
    val hapticsStrength: Int,
    val view: View
) : HapticFeedback {

    override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
        when (hapticFeedbackType) {
            HapticFeedbackType.LongPress -> {
                when (hapticsStrength) {
                    1 -> view.vibrate()
                    2 -> view.vibrateStrong()
                }
            }

            HapticFeedbackType.TextHandleMove -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    view.reallyPerformHapticFeedback(HapticFeedbackConstants.TEXT_HANDLE_MOVE)
                } else view.reallyPerformHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            }

            HapticFeedbackType.Confirm ->
                view.reallyPerformHapticFeedback(HapticFeedbackConstants.CONFIRM)

            HapticFeedbackType.ContextClick ->
                view.reallyPerformHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)

            HapticFeedbackType.GestureEnd ->
                view.reallyPerformHapticFeedback(HapticFeedbackConstants.GESTURE_END)

            HapticFeedbackType.GestureThresholdActivate ->
                view.reallyPerformHapticFeedback(HapticFeedbackConstants.GESTURE_THRESHOLD_ACTIVATE)

            HapticFeedbackType.Reject -> view.reallyPerformHapticFeedback(HapticFeedbackConstants.REJECT)
            HapticFeedbackType.SegmentFrequentTick ->
                view.reallyPerformHapticFeedback(HapticFeedbackConstants.SEGMENT_FREQUENT_TICK)

            HapticFeedbackType.SegmentTick ->
                view.reallyPerformHapticFeedback(HapticFeedbackConstants.SEGMENT_TICK)

            HapticFeedbackType.ToggleOff ->
                view.reallyPerformHapticFeedback(HapticFeedbackConstants.TOGGLE_OFF)

            HapticFeedbackType.ToggleOn ->
                view.reallyPerformHapticFeedback(HapticFeedbackConstants.TOGGLE_ON)

            HapticFeedbackType.VirtualKey ->
                view.reallyPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

}

internal data object EmptyHaptics : HapticFeedback {
    override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) = Unit
}

fun HapticFeedback.press() = performHapticFeedback(HapticFeedbackType.TextHandleMove)
fun HapticFeedback.longPress() = performHapticFeedback(HapticFeedbackType.LongPress)

@Composable
fun rememberEnhancedHapticFeedback(hapticsStrength: Int): HapticFeedback {
    val view = LocalView.current

    val haptics by remember(hapticsStrength) {
        derivedStateOf {
            if (hapticsStrength == 0) EmptyHaptics
            else EnhancedHapticFeedback(
                hapticsStrength = hapticsStrength,
                view = view
            )
        }
    }

    return haptics
}

fun Modifier.hapticsClickable(
    interactionSource: MutableInteractionSource?,
    indication: Indication?,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    enableHaptics: Boolean = true,
    onClick: () -> Unit
): Modifier = this.composed {
    val haptics = LocalHapticFeedback.current

    Modifier.clickable(
        interactionSource = interactionSource,
        indication = indication,
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = {
            if (enableHaptics) haptics.longPress()

            onClick()
        }
    )
}

fun Modifier.hapticsClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier = this.composed {
    hapticsClickable(
        interactionSource = null,
        indication = LocalIndication.current,
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick
    )
}

fun Modifier.hapticsCombinedClickable(
    interactionSource: MutableInteractionSource?,
    indication: Indication?,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onLongClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClick: () -> Unit
) = this.composed {
    val haptics = LocalHapticFeedback.current

    Modifier.combinedClickable(
        interactionSource = interactionSource,
        indication = indication,
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onLongClickLabel = onLongClickLabel,
        onLongClick = if (onLongClick != null) {
            {
                haptics.longPress()
                onLongClick()
            }
        } else null,
        onDoubleClick = if (onDoubleClick != null) {
            {
                haptics.press()
                haptics.longPress()
                onDoubleClick()
            }
        } else null,
        hapticFeedbackEnabled = false,
        onClick = {
            haptics.longPress()
            onClick()
        }
    )
}

fun Modifier.hapticsCombinedClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onLongClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClick: () -> Unit
) = this.composed {
    hapticsCombinedClickable(
        interactionSource = null,
        indication = LocalIndication.current,
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onLongClickLabel = onLongClickLabel,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        onClick = onClick
    )
}