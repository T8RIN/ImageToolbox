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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.model.toUiFont
import com.t8rin.imagetoolbox.core.ui.theme.toColor
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.FontSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.watermarking.domain.WatermarkParams
import com.t8rin.imagetoolbox.feature.watermarking.domain.WatermarkingType
import com.t8rin.imagetoolbox.feature.watermarking.domain.digitalParams

@Composable
internal fun TextParamsContent(
    params: WatermarkParams,
    onValueChange: (WatermarkParams) -> Unit
) {
    val digitalParams = params.watermarkingType.digitalParams()
    val isInvisible = digitalParams?.isInvisible == true

    AnimatedVisibility(
        visible = params.watermarkingType is WatermarkingType.Text && !isInvisible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        val type = params.watermarkingType as? WatermarkingType.Text
            ?: return@AnimatedVisibility

        Column {
            FontSelector(
                value = type.params.font.toUiFont(),
                onValueChange = {
                    onValueChange(
                        params.copy(
                            watermarkingType = type.copy(
                                params = type.params.copy(font = it.type)
                            )
                        )
                    )
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            EnhancedSliderItem(
                value = type.params.size,
                title = stringResource(R.string.watermark_size),
                internalStateTransformation = {
                    it.roundToTwoDigits()
                },
                onValueChange = {
                    onValueChange(
                        params.copy(
                            watermarkingType = type.copy(
                                params = type.params.copy(size = it)
                            )
                        )
                    )
                },
                valueRange = 0.01f..1f,
                shape = ShapeDefaults.large,
                containerColor = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.height(4.dp))
            ColorRowSelector(
                value = type.params.color.toColor(),
                onValueChange = {
                    onValueChange(
                        params.copy(
                            watermarkingType = type.copy(
                                params = type.params.copy(color = it.toArgb())
                            )
                        )
                    )
                },
                title = stringResource(R.string.text_color),
                modifier = Modifier.container(
                    shape = ShapeDefaults.large,
                    color = MaterialTheme.colorScheme.surface
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            ColorRowSelector(
                value = type.params.backgroundColor.toColor(),
                onValueChange = {
                    onValueChange(
                        params.copy(
                            watermarkingType = type.copy(
                                params = type.params.copy(backgroundColor = it.toArgb())
                            )
                        )
                    )
                },
                title = stringResource(R.string.background_color),
                modifier = Modifier.container(
                    shape = ShapeDefaults.large,
                    color = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}