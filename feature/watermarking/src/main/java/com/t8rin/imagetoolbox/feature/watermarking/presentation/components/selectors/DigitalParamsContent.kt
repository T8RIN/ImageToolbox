/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.watermarking.presentation.components.selectors

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DisabledVisible
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.feature.watermarking.domain.WatermarkParams
import com.t8rin.imagetoolbox.feature.watermarking.domain.copy
import com.t8rin.imagetoolbox.feature.watermarking.domain.digitalParams
import com.t8rin.imagetoolbox.feature.watermarking.domain.isStamp

@Composable
internal fun DigitalParamsContent(
    params: WatermarkParams,
    onValueChange: (WatermarkParams) -> Unit
) {
    val digitalParams = params.watermarkingType.digitalParams()
    val isInvisible = digitalParams?.isInvisible == true

    AnimatedVisibility(
        visible = !params.watermarkingType.isStamp(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column {
            PreferenceRowSwitch(
                title = stringResource(id = R.string.invisible_mode),
                subtitle = stringResource(id = R.string.invisible_mode_sub),
                checked = isInvisible,
                startIcon = Icons.Rounded.DisabledVisible,
                onClick = {
                    onValueChange(
                        params.copy(
                            digitalParams = digitalParams?.copy(
                                isInvisible = !isInvisible
                            )
                        )
                    )
                },
                shape = ShapeDefaults.large,
                color = MaterialTheme.colorScheme.surface
            )
            AnimatedVisibility(visible = isInvisible) {
                PreferenceRowSwitch(
                    title = stringResource(id = R.string.use_lsb),
                    subtitle = stringResource(id = R.string.use_lsb_sub),
                    checked = digitalParams?.isLSB ?: false,
                    startIcon = Icons.Rounded.GraphicEq,
                    onClick = {
                        onValueChange(
                            params.copy(
                                digitalParams = digitalParams?.copy(
                                    isLSB = !digitalParams.isLSB
                                )
                            )
                        )
                    },
                    shape = ShapeDefaults.large,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}