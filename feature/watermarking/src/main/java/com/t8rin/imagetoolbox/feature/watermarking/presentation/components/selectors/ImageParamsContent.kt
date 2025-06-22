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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.feature.watermarking.domain.WatermarkParams
import com.t8rin.imagetoolbox.feature.watermarking.domain.WatermarkingType
import com.t8rin.imagetoolbox.feature.watermarking.domain.digitalParams

@Composable
internal fun ImageParamsContent(
    params: WatermarkParams,
    onValueChange: (WatermarkParams) -> Unit
) {
    val digitalParams = params.watermarkingType.digitalParams()
    val isInvisible = digitalParams?.isInvisible == true

    AnimatedVisibility(
        visible = params.watermarkingType is WatermarkingType.Image && !isInvisible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        val type = params.watermarkingType as? WatermarkingType.Image
            ?: return@AnimatedVisibility

        EnhancedSliderItem(
            value = type.size,
            title = stringResource(R.string.watermark_size),
            internalStateTransformation = {
                it.roundToTwoDigits()
            },
            onValueChange = {
                onValueChange(
                    params.copy(
                        watermarkingType = type.copy(size = it)
                    )
                )
            },
            valueRange = 0.01f..1f,
            shape = ShapeDefaults.large,
            color = MaterialTheme.colorScheme.surface
        )
    }
}