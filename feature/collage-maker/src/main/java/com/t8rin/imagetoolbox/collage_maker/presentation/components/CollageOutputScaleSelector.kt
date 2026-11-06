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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.colors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.PhotoSizeSelectSmall
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

@Composable
internal fun CollageOutputScaleSelector(
    value: Float,
    onValueChange: (Float) -> Unit,
    outputSize: Pair<Int, Int>?
) {
    EnhancedSliderItem(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        title = stringResource(R.string.output_image_scale),
        valueRange = 0.1f..1f,
        internalStateTransformation = {
            it.roundToTwoDigits()
        },
        onValueChange = onValueChange,
        sliderModifier = Modifier
            .padding(
                top = 14.dp,
                start = 12.dp,
                end = 12.dp,
                bottom = 10.dp
            ),
        icon = Icons.Outlined.PhotoSizeSelectSmall,
        shape = ShapeDefaults.extraLarge
    ) {
        if (outputSize != null) {
            val (width, height) = outputSize
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .container(
                        shape = ShapeDefaults.large,
                        color = MaterialTheme.colorScheme.surface
                    )
                    .padding(4.dp)
            ) {
                listOf(
                    stringResource(R.string.width, "") to width,
                    stringResource(R.string.height, "") to height
                ).forEachIndexed { index, (title, value) ->
                    if (index > 0) Spacer(Modifier.width(8.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .container(
                                autoShadowElevation = 0.2.dp,
                                color = MaterialTheme.colorScheme.surfaceContainerLow
                            )
                            .padding(4.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(title)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = value.toString(),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = LocalContentColor.current.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        } else {
            Text(
                text = stringResource(R.string.loading),
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .container(
                        shape = ShapeDefaults.large,
                        color = MaterialTheme.colorScheme.surface
                    )
                    .padding(6.dp),
                color = LocalContentColor.current.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}