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

package com.t8rin.imagetoolbox.texture_generation.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.t8rin.colors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.provider.ProvideContainerDefaults
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.texture_generation.domain.model.CellularGridType
import com.t8rin.imagetoolbox.texture_generation.domain.model.FbmBasisType
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureFilterType
import kotlin.math.roundToInt

@Composable
internal fun ParamColumn(
    content: @Composable () -> Unit
) {
    ProvideContainerDefaults(
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            content()
        }
    }
}

@Composable
internal fun FloatParam(
    value: Float,
    title: String,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    shape: Shape = ShapeDefaults.center
) {
    EnhancedSliderItem(
        value = value,
        title = title,
        valueRange = range,
        internalStateTransformation = {
            it.roundToTwoDigits()
        },
        onValueChange = onValueChange,
        shape = shape
    )
}

@Composable
internal fun IntParam(
    value: Int,
    title: String,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Int) -> Unit,
    shape: Shape = ShapeDefaults.center
) {
    EnhancedSliderItem(
        value = value,
        title = title,
        valueRange = range,
        internalStateTransformation = {
            it.roundToInt()
        },
        onValueChange = {
            onValueChange(it.roundToInt())
        },
        shape = shape
    )
}

@Composable
internal fun ColorParam(
    title: String,
    value: Color,
    onValueChange: (Color) -> Unit,
    shape: Shape = ShapeDefaults.center
) {
    ColorRowSelector(
        value = value,
        onValueChange = onValueChange,
        title = title,
        modifier = Modifier.container(
            shape = shape
        )
    )
}

internal fun TextureFilterType.titleRes(): Int = when (this) {
    TextureFilterType.BrushedMetal -> R.string.texture_brushed_metal
    TextureFilterType.Caustics -> R.string.texture_caustics
    TextureFilterType.Cellular -> R.string.texture_cellular
    TextureFilterType.Check -> R.string.texture_checkerboard
    TextureFilterType.FBM -> R.string.texture_fbm
    TextureFilterType.Marble -> R.string.texture_marble
    TextureFilterType.Plasma -> R.string.texture_plasma
    TextureFilterType.Quilt -> R.string.texture_quilt
    TextureFilterType.Wood -> R.string.texture_wood
}

internal fun CellularGridType.titleRes(): Int = when (this) {
    CellularGridType.Random -> R.string.grid_random
    CellularGridType.Square -> R.string.grid_square
    CellularGridType.Hexagonal -> R.string.grid_hexagonal
    CellularGridType.Octagonal -> R.string.grid_octagonal
    CellularGridType.Triangular -> R.string.grid_triangular
}

internal fun FbmBasisType.titleRes(): Int = when (this) {
    FbmBasisType.Noise -> R.string.noise
    FbmBasisType.Ridged -> R.string.fbm_ridged
    FbmBasisType.VlNoise -> R.string.fbm_vl_noise
    FbmBasisType.ScNoise -> R.string.fbm_sc_noise
    FbmBasisType.Cellular -> R.string.texture_cellular
}