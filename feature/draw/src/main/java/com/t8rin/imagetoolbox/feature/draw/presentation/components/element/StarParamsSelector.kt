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
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.innerRadiusRatio
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.isRegular
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.isStar
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.rotationDegrees
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.updateStar
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.vertices
import kotlin.math.roundToInt

@Composable
internal fun StarParamsSelector(
    value: DrawPathMode,
    onValueChange: (DrawPathMode) -> Unit
) {
    AnimatedVisibility(
        visible = value.isStar(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column {
            EnhancedSliderItem(
                value = value.vertices(),
                title = stringResource(R.string.vertices),
                valueRange = 3f..24f,
                steps = 20,
                internalStateTransformation = {
                    it.roundToInt()
                },
                onValueChange = {
                    onValueChange(
                        value.updateStar(vertices = it.toInt())
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
                value = value.rotationDegrees(),
                title = stringResource(R.string.angle),
                valueRange = 0f..360f,
                internalStateTransformation = {
                    it.roundToInt()
                },
                onValueChange = {
                    onValueChange(
                        value.updateStar(rotationDegrees = it.toInt())
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = ShapeDefaults.center
            )
            Spacer(modifier = Modifier.height(4.dp))
            EnhancedSliderItem(
                value = value.innerRadiusRatio(),
                title = stringResource(R.string.inner_radius_ratio),
                valueRange = 0f..1f,
                internalStateTransformation = {
                    it.roundToTwoDigits()
                },
                onValueChange = {
                    onValueChange(
                        value.updateStar(innerRadiusRatio = it)
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
                title = stringResource(R.string.draw_regular_star),
                subtitle = stringResource(R.string.draw_regular_polygon_sub),
                checked = value.isRegular(),
                onClick = {
                    onValueChange(
                        value.updateStar(isRegular = it)
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = ShapeDefaults.bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                resultModifier = Modifier.padding(16.dp),
                applyHorizontalPadding = false
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}