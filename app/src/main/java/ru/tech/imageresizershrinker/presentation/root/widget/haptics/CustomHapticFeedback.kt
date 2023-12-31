@file:Suppress("DEPRECATION")

package ru.tech.imageresizershrinker.presentation.root.widget.haptics

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

private fun View.vibrate() = reallyPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
private fun View.vibrateStrong() = reallyPerformHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

private fun View.reallyPerformHapticFeedback(feedbackConstant: Int) {
    if (context.isTouchExplorationEnabled()) return

    isHapticFeedbackEnabled = true

    performHapticFeedback(feedbackConstant, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
}

private fun Context.isTouchExplorationEnabled(): Boolean {
    val accessibilityManager =
        getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager?
    return accessibilityManager?.isTouchExplorationEnabled ?: false
}


@Composable
fun customHapticFeedback(hapticsStrength: Int): HapticFeedback {
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
