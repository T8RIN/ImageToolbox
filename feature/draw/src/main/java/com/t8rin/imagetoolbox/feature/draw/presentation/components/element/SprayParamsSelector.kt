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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.element

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CropSquare
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.density
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.isSpray
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.isSquareShaped
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.pixelSize
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.updateSpray
import kotlin.math.roundToInt

@Composable
internal fun SprayParamsSelector(
    value: DrawPathMode,
    onValueChange: (DrawPathMode) -> Unit
) {
    AnimatedVisibility(
        visible = value.isSpray(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column {
            EnhancedSliderItem(
                value = value.density(),
                title = stringResource(R.string.density),
                valueRange = 1f..500f,
                internalStateTransformation = {
                    it.roundToInt()
                },
                onValueChange = {
                    onValueChange(
                        value.updateSpray(density = it.roundToInt())
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = ShapeDefaults.top
            )
            Spacer(modifier = Modifier.height(4.dp))
            EnhancedSliderItem(
                value = value.pixelSize(),
                title = stringResource(R.string.pixel_size),
                valueRange = 0.5f..20f,
                internalStateTransformation = {
                    it.roundToTwoDigits()
                },
                onValueChange = {
                    onValueChange(
                        value.updateSpray(pixelSize = it.roundToTwoDigits())
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = ShapeDefaults.center
            )
            Spacer(modifier = Modifier.height(4.dp))
            PreferenceRowSwitch(
                title = stringResource(R.string.square_particles),
                subtitle = stringResource(R.string.square_particles_sub),
                checked = value.isSquareShaped(),
                onClick = {
                    onValueChange(
                        value.updateSpray(isSquareShaped = it)
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = ShapeDefaults.bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                resultModifier = Modifier.padding(16.dp),
                startIcon = Icons.Rounded.CropSquare
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}