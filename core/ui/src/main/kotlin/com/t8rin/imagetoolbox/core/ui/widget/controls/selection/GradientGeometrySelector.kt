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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.colors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.domain.model.GradientGeometry
import com.t8rin.imagetoolbox.core.domain.model.GradientType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import kotlin.math.roundToInt

@Composable
fun GradientGeometrySelector(
    value: GradientGeometry,
    onValueChange: (GradientGeometry) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    inactiveButtonColor: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        EnhancedButtonGroup(
            items = GradientType.entries.map {
                stringResource(
                    when (it) {
                        GradientType.Linear -> R.string.gradient_type_linear
                        GradientType.Radial -> R.string.gradient_type_radial
                        GradientType.Sweep -> R.string.gradient_type_sweep
                    }
                )
            },
            selectedIndex = GradientType.entries.indexOf(value.type),
            onIndexChange = {
                onValueChange(value.copy(type = GradientType.entries[it]))
            },
            title = stringResource(R.string.gradient_type),
            inactiveButtonColor = inactiveButtonColor,
            modifier = Modifier
                .fillMaxWidth()
                .container(color = containerColor, shape = ShapeDefaults.center)
        )
        AnimatedVisibility(
            visible = value.type != GradientType.Linear,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                EnhancedSliderItem(
                    value = value.centerX,
                    onValueChange = { onValueChange(value.copy(centerX = it)) },
                    valueRange = 0f..1f,
                    title = stringResource(R.string.center_x),
                    internalStateTransformation = { it.roundToTwoDigits() },
                    containerColor = containerColor,
                    shape = ShapeDefaults.center
                )
                EnhancedSliderItem(
                    value = value.centerY,
                    onValueChange = { onValueChange(value.copy(centerY = it)) },
                    valueRange = 0f..1f,
                    title = stringResource(R.string.center_y),
                    internalStateTransformation = { it.roundToTwoDigits() },
                    containerColor = containerColor,
                    shape = ShapeDefaults.center
                )
            }
        }
        AnimatedVisibility(
            visible = value.type == GradientType.Radial,
            modifier = Modifier.fillMaxWidth()
        ) {
            EnhancedSliderItem(
                value = value.radius,
                onValueChange = { onValueChange(value.copy(radius = it)) },
                valueRange = 0.05f..2f,
                title = stringResource(R.string.radius),
                internalStateTransformation = { it.roundToTwoDigits() },
                containerColor = containerColor,
                shape = ShapeDefaults.bottom
            )
        }
        AnimatedVisibility(
            visible = value.type != GradientType.Radial,
            modifier = Modifier.fillMaxWidth()
        ) {
            EnhancedSliderItem(
                value = value.angle,
                onValueChange = { onValueChange(value.copy(angle = it)) },
                valueRange = 0f..360f,
                title = stringResource(R.string.angle),
                internalStateTransformation = { it.roundToInt().toFloat() },
                containerColor = containerColor,
                shape = ShapeDefaults.bottom
            )
        }
    }
}