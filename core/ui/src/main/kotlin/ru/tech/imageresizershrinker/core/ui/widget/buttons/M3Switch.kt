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

package ru.tech.imageresizershrinker.core.ui.widget.buttons

import android.content.res.ColorStateList
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.materialswitch.MaterialSwitch


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
    var view by remember {
        mutableStateOf<MaterialSwitch?>(null)
    }

    DisposableEffect(view, checked) {
        view?.isChecked = checked
        view?.setOnCheckedChangeListener { _, value ->
            onCheckedChange?.let { onCheckedChange(value) }
        }

        onDispose {
            view?.setOnCheckedChangeListener(null)
        }
    }

    Box {
        Switch(
            checked = false,
            onCheckedChange = {},
            modifier = internalModifier,
            colors = SwitchColors(
                Transparent,
                Transparent,
                Transparent,
                Transparent,
                Transparent,
                Transparent,
                Transparent,
                Transparent,
                Transparent,
                Transparent,
                Transparent,
                Transparent,
                Transparent,
                Transparent,
                Transparent,
                Transparent
            )
        )
        AndroidView(
            modifier = modifier,
            factory = {
                MaterialSwitch(it).apply {
                    view = this
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