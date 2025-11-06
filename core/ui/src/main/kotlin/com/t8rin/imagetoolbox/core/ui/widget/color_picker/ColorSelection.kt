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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

@Composable
fun ColorSelection(
    color: Int,
    onColorChange: (Int) -> Unit,
    withAlpha: Boolean = false,
    infoContainerColor: Color = Color.Unspecified,
) {
    val colorMapped = Color(color)

    Column {
        ColorInfo(
            color = colorMapped.let {
                if (withAlpha) it else it.copy(1f)
            }.toArgb(),
            onColorChange = onColorChange,
            infoContainerColor = infoContainerColor
        )
        Spacer(Modifier.height(16.dp))
        ColorPicker(
            onColorSelected = { onColorChange(it.toArgb()) },
            selectedColor = colorMapped,
            containerColor = infoContainerColor,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            hueSliderConfig = HueSliderThumbConfig(
                withAlpha = withAlpha
            )
        )
        Spacer(Modifier.height(16.dp))
    }
}