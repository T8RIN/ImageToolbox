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

package com.t8rin.imagetoolbox.collage_maker.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.domain.model.GradientFill
import com.t8rin.imagetoolbox.core.domain.model.GradientGeometry
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorAndGradientSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.saver.GradientGeometrySaver

@Composable
internal fun CollageBorderSelector(
    color: Color,
    gradient: GradientFill?,
    onColorChange: (Color) -> Unit,
    onGradientChange: (GradientFill) -> Unit,
    shape: Shape = ShapeDefaults.extraLarge,
    selectionKey: Any? = Unit
) {
    var geometry by rememberSaveable(selectionKey, stateSaver = GradientGeometrySaver) {
        mutableStateOf(gradient?.geometry ?: GradientGeometry())
    }
    LaunchedEffect(gradient) {
        gradient?.let { geometry = it.geometry }
    }
    ColorAndGradientSelector(
        value = color,
        gradientPalette = gradient?.palette,
        onValueChange = onColorChange,
        onGradientPaletteChange = { onGradientChange(geometry.withPalette(it)) },
        gradientGeometry = geometry,
        onGradientGeometryChange = {
            geometry = it
            onGradientChange(it.withPalette(gradient?.palette ?: GradientPalette.SoftRainbow))
        },
        title = stringResource(R.string.border_color),
        shape = shape,
        selectionKey = selectionKey
    )
}