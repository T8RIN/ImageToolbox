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

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.domain.model.GradientFill
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.icons.BackgroundColor

@Composable
fun BackgroundColorSelector(
    value: Color,
    gradient: GradientFill?,
    onValueChange: (Color) -> Unit,
    onGradientChange: (GradientFill) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = Icons.Outlined.BackgroundColor
) {
    ColorAndGradientSelector(
        value = value,
        gradientPalette = gradient?.palette,
        onValueChange = onValueChange,
        onGradientPaletteChange = {
            onGradientChange(
                (gradient ?: GradientFill()).copy(palette = it)
            )
        },
        gradientAngle = gradient?.angle ?: 0f,
        onGradientAngleChange = { onGradientChange((gradient ?: GradientFill()).copy(angle = it)) },
        modifier = modifier,
        icon = icon
    )
}