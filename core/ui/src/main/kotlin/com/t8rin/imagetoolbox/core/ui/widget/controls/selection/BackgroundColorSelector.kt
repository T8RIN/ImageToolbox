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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.domain.model.GradientFill
import com.t8rin.imagetoolbox.core.domain.model.GradientGeometry
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.icons.BackgroundColor
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.saver.GradientGeometrySaver

@Composable
fun BackgroundColorSelector(
    value: Color,
    gradient: GradientFill?,
    onValueChange: (Color) -> Unit,
    onGradientChange: (GradientFill) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = Icons.Outlined.BackgroundColor,
    shape: Shape = ShapeDefaults.default,
    containerColor: Color = Color.Unspecified,
    nestedContainerColor: Color = Color.Unspecified
) {
    var lastGeometry by rememberSaveable(stateSaver = GradientGeometrySaver) {
        mutableStateOf(gradient?.geometry ?: GradientGeometry())
    }
    LaunchedEffect(gradient) {
        gradient?.let { lastGeometry = it.geometry }
    }

    ColorAndGradientSelector(
        value = value,
        gradientPalette = gradient?.palette,
        onValueChange = onValueChange,
        onGradientPaletteChange = {
            onGradientChange((gradient?.geometry ?: lastGeometry).withPalette(it))
        },
        gradientGeometry = gradient?.geometry ?: lastGeometry,
        onGradientGeometryChange = {
            onGradientChange(it.withPalette(gradient?.palette ?: GradientPalette.SoftRainbow))
        },
        modifier = modifier,
        icon = icon,
        shape = shape,
        containerColor = containerColor,
        nestedContainerColor = nestedContainerColor
    )
}