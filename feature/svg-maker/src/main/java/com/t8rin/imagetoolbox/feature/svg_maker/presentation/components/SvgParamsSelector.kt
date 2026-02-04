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

package com.t8rin.imagetoolbox.feature.svg_maker.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.ChangeHistory
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.FormatColorFill
import androidx.compose.material.icons.outlined.LinearScale
import androidx.compose.material.icons.outlined.RepeatOne
import androidx.compose.material.icons.outlined.Upcoming
import androidx.compose.material.icons.rounded.BlurCircular
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Eyedropper
import com.t8rin.imagetoolbox.core.resources.icons.FreeDraw
import com.t8rin.imagetoolbox.core.resources.icons.Line
import com.t8rin.imagetoolbox.core.resources.icons.PhotoSizeSelectSmall
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.feature.svg_maker.domain.SvgParams
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun SvgParamsSelector(
    value: SvgParams,
    onValueChange: (SvgParams) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .container(shape = ShapeDefaults.extraLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.presets),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(12.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                val listState = rememberLazyListState()

                LazyRow(
                    state = listState,
                    modifier = Modifier
                        .fadingEdges(listState)
                        .padding(vertical = 1.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp, Alignment.CenterHorizontally
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    items(SvgParams.presets) {
                        val selected = value == it
                        EnhancedChip(
                            selected = selected,
                            onClick = { onValueChange(it) },
                            selectedColor = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            AutoSizeText(it.name)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        PreferenceRowSwitch(
            title = stringResource(id = R.string.downscale_image),
            subtitle = stringResource(id = R.string.downscale_image_sub),
            checked = value.isImageSampled,
            onClick = {
                onValueChange(
                    value.copy(isImageSampled = it)
                )
            },
            startIcon = Icons.Outlined.PhotoSizeSelectSmall,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeDefaults.extraLarge
        )
        AnimatedVisibility(
            visible = !value.isImageSampled
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .container(
                        shape = ShapeDefaults.large,
                        borderColor = MaterialTheme.colorScheme.onErrorContainer.copy(
                            0.4f
                        ),
                        color = MaterialTheme.colorScheme.errorContainer.copy(
                            alpha = 0.7f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.svg_warning),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 14.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.5f)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        EnhancedSliderItem(
            value = value.colorsCount,
            title = stringResource(R.string.max_colors_count),
            icon = Icons.Outlined.ColorLens,
            valueRange = 2f..64f,
            steps = 61,
            internalStateTransformation = {
                it.roundToInt()
            },
            onValueChange = {
                onValueChange(
                    value.copy(
                        colorsCount = it.roundToInt()
                    )
                )
            },
            shape = ShapeDefaults.extraLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        EnhancedSliderItem(
            value = value.minColorRatio,
            title = stringResource(R.string.min_color_ratio),
            icon = Icons.Outlined.Eyedropper,
            valueRange = 0f..0.1f,
            internalStateTransformation = {
                it.roundTo(3)
            },
            onValueChange = {
                onValueChange(
                    value.copy(
                        minColorRatio = it
                    )
                )
            },
            shape = ShapeDefaults.extraLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        EnhancedSliderItem(
            value = value.quantizationCyclesCount,
            icon = Icons.Outlined.RepeatOne,
            title = stringResource(id = R.string.repeat_count),
            valueRange = 1f..10f,
            steps = 8,
            internalStateTransformation = { it.roundToInt() },
            onValueChange = {
                onValueChange(
                    value.copy(
                        quantizationCyclesCount = it.roundToInt()
                    )
                )
            },
            shape = ShapeDefaults.extraLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        PreferenceRowSwitch(
            title = stringResource(id = R.string.use_sampled_palette),
            subtitle = stringResource(id = R.string.use_sampled_palette_sub),
            checked = value.isPaletteSampled,
            onClick = {
                onValueChange(
                    value.copy(isPaletteSampled = it)
                )
            },
            startIcon = Icons.Outlined.FormatColorFill,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeDefaults.extraLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        EnhancedSliderItem(
            value = value.svgPathsScale,
            icon = Icons.Outlined.LinearScale,
            title = stringResource(R.string.path_scale),
            valueRange = 0.01f..100f,
            internalStateTransformation = {
                it.roundToTwoDigits()
            },
            onValueChange = {
                onValueChange(
                    value.copy(
                        svgPathsScale = it
                    )
                )
            },
            shape = ShapeDefaults.extraLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        EnhancedSliderItem(
            value = value.blurRadius,
            title = stringResource(R.string.blur_radius),
            icon = Icons.Rounded.BlurCircular,
            internalStateTransformation = {
                it.roundToInt()
            },
            onValueChange = {
                onValueChange(
                    value.copy(
                        blurRadius = it.roundToInt()
                    )
                )
            },
            containerColor = Color.Unspecified,
            valueRange = 0f..5f,
            steps = 4,
            shape = ShapeDefaults.extraLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        EnhancedSliderItem(
            value = value.blurDelta,
            icon = Icons.Outlined.ChangeHistory,
            title = stringResource(id = R.string.blur_size),
            valueRange = 0f..255f,
            internalStateTransformation = { it.roundToInt() },
            onValueChange = {
                onValueChange(
                    value.copy(
                        blurDelta = it.roundToInt()
                    )
                )
            },
            shape = ShapeDefaults.extraLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        EnhancedSliderItem(
            value = value.pathOmit,
            icon = Icons.Outlined.Upcoming,
            title = stringResource(id = R.string.path_omit),
            valueRange = 0f..64f,
            steps = 63,
            internalStateTransformation = { it.roundToInt() },
            onValueChange = {
                onValueChange(
                    value.copy(
                        pathOmit = it.roundToInt()
                    )
                )
            },
            shape = ShapeDefaults.extraLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        EnhancedSliderItem(
            value = value.linesThreshold,
            icon = Icons.Rounded.Line,
            title = stringResource(R.string.lines_threshold),
            valueRange = 0f..10f,
            internalStateTransformation = {
                it.roundToTwoDigits()
            },
            onValueChange = {
                onValueChange(
                    value.copy(
                        linesThreshold = it
                    )
                )
            },
            shape = ShapeDefaults.extraLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        EnhancedSliderItem(
            value = value.quadraticThreshold,
            icon = Icons.Rounded.FreeDraw,
            title = stringResource(R.string.quadratic_threshold),
            valueRange = 0f..10f,
            internalStateTransformation = {
                it.roundToTwoDigits()
            },
            onValueChange = {
                onValueChange(
                    value.copy(
                        quadraticThreshold = it
                    )
                )
            },
            shape = ShapeDefaults.extraLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        EnhancedSliderItem(
            value = value.coordinatesRoundingAmount,
            icon = Icons.Outlined.Calculate,
            title = stringResource(R.string.coordinates_rounding_tolerance),
            valueRange = 0f..8f,
            steps = 7,
            internalStateTransformation = {
                it.roundToInt()
            },
            onValueChange = {
                onValueChange(
                    value.copy(
                        coordinatesRoundingAmount = it.roundToInt()
                    )
                )
            },
            shape = ShapeDefaults.extraLarge
        )
    }
}

private fun Float.roundTo(
    digits: Int? = 2
) = digits?.let {
    (this * 10f.pow(digits)).roundToInt() / (10f.pow(digits))
} ?: this

private val SvgParams.name: String
    @Composable
    get() = when (this) {
        SvgParams.Default -> stringResource(R.string.defaultt)
        SvgParams.Detailed -> stringResource(R.string.detailed)
        SvgParams.Grayscale -> stringResource(R.string.gray_scale)
        else -> ""
    }