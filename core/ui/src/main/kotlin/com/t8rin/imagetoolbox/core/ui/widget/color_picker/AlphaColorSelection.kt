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

package com.t8rin.imagetoolbox.core.ui.widget.color_picker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil
import com.smarttoolfactory.colorpicker.selector.SelectorRectSaturationValueHSV
import com.smarttoolfactory.colorpicker.slider.SliderAlphaHSL
import com.smarttoolfactory.colorpicker.slider.SliderHueHSV
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

@Composable
fun AlphaColorSelection(
    color: Int,
    onColorChange: (Int) -> Unit,
) {
    val color1 = Color(color)
    val hsv = ColorUtil.colorToHSV(color1)
    var hue by remember { mutableFloatStateOf(hsv[0]) }
    var alpha by remember { mutableFloatStateOf(color1.alpha) }
    val saturation = hsv[1]
    val value = hsv[2]

    Column {
        ColorInfo(
            color = color1.toArgb(),
            onColorChange = {
                onColorChange(Color(it).toArgb())
            },
        )
        Spacer(Modifier.height(16.dp))
        SelectorRectSaturationValueHSV(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .container(
                    shape = ShapeDefaults.pressed,
                    resultPadding = 0.dp
                )
                .clip(ShapeDefaults.pressed),
            hue = hue,
            saturation = saturation,
            value = value
        ) { s, v ->
            val c = Color.hsv(hue, s, v, alpha).toArgb()
            onColorChange(c)
        }
        Spacer(Modifier.height(16.dp))
        SliderHueHSV(
            hue = hue,
            saturation = saturation,
            value = value,
            onValueChange = { h ->
                hue = h
                val c = Color.hsv(h, saturation, value, alpha).toArgb()
                onColorChange(c)
            },
            trackHeight = 16.dp,
            modifier = Modifier
                .container(
                    shape = CircleShape,
                    resultPadding = 0.dp,
                    clip = false,
                    isShadowClip = true,
                    autoShadowElevation = if (LocalSettingsState.current.drawSliderShadows) 1.dp
                    else 0.dp
                )
                .padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        SliderAlphaHSL(
            hue = hue,
            alpha = alpha,
            trackHeight = 16.dp,
            modifier = Modifier
                .container(
                    shape = CircleShape,
                    resultPadding = 0.dp,
                    clip = false,
                    isShadowClip = true
                )
                .padding(horizontal = 10.dp),
            onValueChange = {
                alpha = it
                val c = Color.hsv(hue, saturation, value, alpha).toArgb()
                onColorChange(c)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        LaunchedEffect(color1) {
            if (hue != hsv[0]) hue = hsv[0]
            if (alpha != color1.alpha) alpha = color1.alpha
        }
    }
}