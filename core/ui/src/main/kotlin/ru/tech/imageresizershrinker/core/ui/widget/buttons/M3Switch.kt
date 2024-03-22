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

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.materialswitch.MaterialSwitch

@Composable
fun M3Switch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val trackColor by animateColorAsState(trackColor(enabled, checked, colors))
    val thumbColor by animateColorAsState(thumbColor(enabled, checked, colors))
    val borderColor by animateColorAsState(borderColor(enabled, checked, colors))

    AndroidView(
        modifier = modifier,
        factory = {
            MaterialSwitch(it).apply {
                setOnCheckedChangeListener { _, value ->
                    onCheckedChange?.let { onCheckedChange(value) }
                }
            }
        },
        update = {
            it.isEnabled = enabled
            it.isChecked = checked
//            it.trackTintList = ColorStateList.valueOf(trackColor.toArgb())
//            it.thumbTintList = ColorStateList.valueOf(thumbColor.toArgb())
//            it.trackDecorationTintList = ColorStateList.valueOf(borderColor.toArgb())
        }
    )
}

@Stable
private fun trackColor(
    enabled: Boolean,
    checked: Boolean,
    colors: SwitchColors
): Color =
    if (enabled) {
        if (checked) colors.checkedTrackColor else colors.uncheckedTrackColor
    } else {
        if (checked) colors.disabledCheckedTrackColor else colors.disabledUncheckedTrackColor
    }

@Stable
private fun thumbColor(
    enabled: Boolean,
    checked: Boolean,
    colors: SwitchColors
): Color =
    if (enabled) {
        if (checked) colors.checkedThumbColor else colors.uncheckedThumbColor
    } else {
        if (checked) colors.disabledCheckedThumbColor else colors.disabledUncheckedThumbColor
    }

@Stable
private fun borderColor(
    enabled: Boolean,
    checked: Boolean,
    colors: SwitchColors
): Color =
    if (enabled) {
        if (checked) colors.checkedBorderColor else colors.uncheckedBorderColor
    } else {
        if (checked) colors.disabledCheckedBorderColor else colors.disabledUncheckedBorderColor
    }