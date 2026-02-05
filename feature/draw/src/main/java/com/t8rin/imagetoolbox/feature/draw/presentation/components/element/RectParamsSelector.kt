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
import com.smarttoolfactory.extendedcolors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.cornerRadius
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.isRect
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.rotationDegrees
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.updateOutlined
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.updateRect
import kotlin.math.roundToInt

@Composable
internal fun RectParamsSelector(
    value: DrawPathMode,
    onValueChange: (DrawPathMode) -> Unit,
    canChangeFillColor: Boolean
) {
    AnimatedVisibility(
        visible = value.isRect(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column {
            AnimatedVisibility(
                visible = canChangeFillColor,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                OutlinedFillColorSelector(
                    value = value.outlinedFillColor?.toColor(),
                    onValueChange = {
                        onValueChange(value.updateOutlined(it))
                    },
                    shape = ShapeDefaults.top,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            }
            EnhancedSliderItem(
                value = value.rotationDegrees(),
                title = stringResource(R.string.angle),
                valueRange = 0f..360f,
                internalStateTransformation = {
                    it.roundToInt()
                },
                onValueChange = {
                    onValueChange(
                        value.updateRect(rotationDegrees = it.toInt())
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = if (canChangeFillColor) ShapeDefaults.center else ShapeDefaults.top
            )
            Spacer(modifier = Modifier.height(4.dp))
            EnhancedSliderItem(
                value = value.cornerRadius(),
                title = stringResource(R.string.radius),
                valueRange = 0f..0.5f,
                internalStateTransformation = { it.roundToTwoDigits() },
                onValueChange = {
                    onValueChange(
                        value.updateRect(cornerRadius = it.roundToTwoDigits())
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = ShapeDefaults.bottom
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}