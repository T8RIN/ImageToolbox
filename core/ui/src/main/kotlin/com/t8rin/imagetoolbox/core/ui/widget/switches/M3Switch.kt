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

package com.t8rin.imagetoolbox.core.ui.widget.switches

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.MotionEvent
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.materialswitch.MaterialSwitch
import kotlinx.coroutines.launch


@SuppressLint("ClickableViewAccessibility")
@Composable
fun M3Switch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    internalModifier: Modifier = modifier,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val realInteractionSource = interactionSource ?: remember {
        MutableInteractionSource()
    }
    val isPressed by realInteractionSource.collectIsPressedAsState()
    var view by remember {
        mutableStateOf<MaterialSwitch?>(null)
    }

    val scope = rememberCoroutineScope()

    DisposableEffect(view, checked, onCheckedChange) {
        view?.isChecked = checked
        view?.setOnCheckedChangeListener { _, value ->
            onCheckedChange?.let {
                onCheckedChange(value)
            }
        }

        onDispose {
            view?.setOnCheckedChangeListener(null)
        }
    }
    LaunchedEffect(view, isPressed) {
        view?.isPressed = isPressed
    }

    Box {
        Switch(
            checked = false,
            onCheckedChange = {},
            modifier = internalModifier,
            colors = SwitchDefaults.transparentColors()
        )

        var press by remember {
            mutableStateOf<PressInteraction.Press?>(null)
        }
        var drag by remember {
            mutableStateOf<DragInteraction.Start?>(null)
        }

        AndroidView(
            modifier = modifier,
            factory = { context ->
                MaterialSwitch(context).apply {
                    view = this
                    isHapticFeedbackEnabled = false
                    setOnTouchListener { _, event ->
                        when (event.actionMasked) {
                            MotionEvent.ACTION_DOWN -> {
                                press = PressInteraction.Press(
                                    Offset(event.x, event.y)
                                ).also {
                                    scope.launch {
                                        realInteractionSource.emit(it)
                                    }
                                }
                            }

                            MotionEvent.ACTION_MOVE -> {
                                drag = DragInteraction.Start().also {
                                    scope.launch {
                                        realInteractionSource.emit(it)
                                    }
                                }
                            }

                            MotionEvent.ACTION_UP -> {
                                scope.launch {
                                    press?.let {
                                        realInteractionSource.emit(PressInteraction.Release(it))
                                    }
                                    drag?.let {
                                        realInteractionSource.emit(DragInteraction.Stop(it))
                                    }
                                    press = null
                                    drag = null
                                }
                            }

                            MotionEvent.ACTION_CANCEL -> {
                                scope.launch {
                                    press?.let {
                                        realInteractionSource.emit(PressInteraction.Cancel(it))
                                    }
                                    drag?.let {
                                        realInteractionSource.emit(DragInteraction.Cancel(it))
                                    }
                                    press = null
                                    drag = null
                                }
                            }

                            else -> Unit
                        }

                        false
                    }
                }
            },
            update = {
                it.isEnabled = enabled
                val states = arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf(android.R.attr.state_checked)
                )
                val trackColors = intArrayOf(
                    colors.uncheckedTrackColor.toArgb(),
                    if (checked) {
                        colors.disabledCheckedTrackColor
                    } else {
                        colors.disabledUncheckedTrackColor
                    }.toArgb(),
                    colors.checkedTrackColor.toArgb()
                )
                it.trackTintList = ColorStateList(states, trackColors)

                val thumbColors = intArrayOf(
                    colors.uncheckedThumbColor.toArgb(),
                    if (checked) {
                        colors.disabledCheckedThumbColor
                    } else {
                        colors.disabledUncheckedThumbColor
                    }.toArgb(),
                    colors.checkedThumbColor.toArgb()
                )
                it.thumbTintList = ColorStateList(states, thumbColors)

                val borderColors = intArrayOf(
                    colors.uncheckedBorderColor.toArgb(),
                    if (checked) {
                        colors.disabledCheckedBorderColor
                    } else {
                        colors.disabledUncheckedBorderColor
                    }.toArgb(),
                    colors.checkedBorderColor.toArgb()
                )
                it.trackDecorationTintList = ColorStateList(states, borderColors)
            }
        )
    }
}

@Suppress("UnusedReceiverParameter")
private fun SwitchDefaults.transparentColors() = SwitchColors(
    checkedThumbColor = Color.Transparent,
    checkedTrackColor = Color.Transparent,
    checkedBorderColor = Color.Transparent,
    checkedIconColor = Color.Transparent,
    uncheckedThumbColor = Color.Transparent,
    uncheckedTrackColor = Color.Transparent,
    uncheckedBorderColor = Color.Transparent,
    uncheckedIconColor = Color.Transparent,
    disabledCheckedThumbColor = Color.Transparent,
    disabledCheckedTrackColor = Color.Transparent,
    disabledCheckedBorderColor = Color.Transparent,
    disabledCheckedIconColor = Color.Transparent,
    disabledUncheckedThumbColor = Color.Transparent,
    disabledUncheckedTrackColor = Color.Transparent,
    disabledUncheckedBorderColor = Color.Transparent,
    disabledUncheckedIconColor = Color.Transparent
)