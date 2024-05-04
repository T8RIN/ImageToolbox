/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.watermarking.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageSelector
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.feature.watermarking.domain.WatermarkParams
import ru.tech.imageresizershrinker.feature.watermarking.domain.WatermarkingType

@Composable
fun WatermarkDataSelector(
    value: WatermarkParams,
    onValueChange: (WatermarkParams) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = value.watermarkingType is WatermarkingType.Text,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut()
    ) {
        val type = value.watermarkingType as? WatermarkingType.Text
            ?: return@AnimatedVisibility

        RoundedTextField(
            modifier = modifier
                .container(
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(8.dp),
            value = type.text,
            singleLine = false,
            onValueChange = {
                onValueChange(
                    value.copy(
                        watermarkingType = type.copy(text = it)
                    )
                )
            },
            label = {
                Text(stringResource(R.string.text))
            }
        )
    }
    AnimatedVisibility(
        visible = value.watermarkingType is WatermarkingType.Image,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut()
    ) {
        val type = value.watermarkingType as? WatermarkingType.Image
            ?: return@AnimatedVisibility

        ImageSelector(
            value = type.imageData,
            subtitle = stringResource(id = R.string.watermarking_image_sub),
            onValueChange = {
                onValueChange(
                    value.copy(
                        watermarkingType = type.copy(imageData = it)
                    )
                )
            },
            modifier = modifier.fillMaxWidth()
        )
    }
}