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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.colors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Shadow
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

@Composable
internal fun CollageShadowSelector(
    radius: Float,
    color: Color,
    onRadiusChange: (Float) -> Unit,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = ShapeDefaults.extraLarge
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        EnhancedSliderItem(
            value = radius,
            title = stringResource(R.string.collage_shadow),
            icon = Icons.Outlined.Shadow,
            valueRange = 0f..20f,
            internalStateTransformation = { it.roundToTwoDigits() },
            onValueChange = onRadiusChange,
            modifier = Modifier.fillMaxWidth(),
            shape = if (radius > 0f) ShapeDefaults.top else shape
        )
        AnimatedVisibility(radius > 0f) {
            ColorRowSelector(
                value = color,
                title = stringResource(R.string.shadow_color),
                onValueChange = onColorChange,
                modifier = Modifier.container(shape = ShapeDefaults.bottom)
            )
        }
    }
}