/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.widget.haptics

import android.content.Context
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.accessibility.AccessibilityManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalView
import androidx.core.content.getSystemService

private fun View.vibrate() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    reallyPerformHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
} else {
    reallyPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
}

private fun View.vibrateStrong() = reallyPerformHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

private fun View.reallyPerformHapticFeedback(feedbackConstant: Int) {
    if (context.isTouchExplorationEnabled()) return

    isHapticFeedbackEnabled = true

    performHapticFeedback(feedbackConstant, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
}

private fun Context.isTouchExplorationEnabled(): Boolean {
    val accessibilityManager = getSystemService<AccessibilityManager>()

    return accessibilityManager?.isTouchExplorationEnabled ?: false
}


@Composable
fun rememberCustomHapticFeedback(hapticsStrength: Int): HapticFeedback {
    val view = LocalView.current

    val haptics by remember(hapticsStrength) {
        derivedStateOf {
            if (hapticsStrength == 0) EmptyHaptics
            else object : HapticFeedback {
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
                    }
                }
            }
        }
    }

    return haptics
}

data object EmptyHaptics : HapticFeedback {
    override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) = Unit
}
