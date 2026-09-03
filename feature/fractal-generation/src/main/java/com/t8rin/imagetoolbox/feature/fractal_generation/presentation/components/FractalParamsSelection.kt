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

package com.t8rin.imagetoolbox.feature.fractal_generation.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BackgroundColor
import com.t8rin.imagetoolbox.core.resources.icons.Build
import com.t8rin.imagetoolbox.core.resources.icons.Palette
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.utils.helper.toModel
import com.t8rin.imagetoolbox.core.ui.utils.provider.ProvideContainerDefaults
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalCamera
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalCoefficientLabel
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalCoefficientSpec
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalColoring
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalComplex
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalFormula
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalIterationPolicy
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalPalette
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalParams
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalQuaternion
import kotlin.math.roundToInt

@Composable
fun FractalParamsSelection(
    value: FractalParams,
    supportedFormulas: List<FractalFormula>,
    onValueChange: (FractalParams) -> Unit,
    onFormulaChange: (FractalFormula) -> Unit,
    modifier: Modifier = Modifier
) {
    val powerRange = FractalParams.MIN_POWER.toFloat().rangeTo(
        FractalParams.MAX_POWER.toFloat()
    )
    val iterationRange = FractalIterationPolicy.MIN_ITERATIONS.toFloat().rangeTo(
        FractalIterationPolicy.MAX_ITERATIONS.toFloat()
    )
    val supersamplingRange = FractalParams.MIN_SUPERSAMPLING.toFloat().rangeTo(
        FractalParams.MAX_SUPERSAMPLING.toFloat()
    )

    Column(
        modifier = modifier.container(
            shape = ShapeDefaults.large,
            resultPadding = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TitleItem(
            text = stringResource(R.string.params),
            icon = Icons.Rounded.Build,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ProvideContainerDefaults(color = MaterialTheme.colorScheme.surface) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DataSelector(
                    value = value.formula,
                    onValueChange = onFormulaChange,
                    entries = supportedFormulas,
                    title = stringResource(R.string.fractal_type),
                    titleIcon = null,
                    itemContentText = { it.label() },
                    spanCount = 2,
                    containerColor = Color.Unspecified,
                    shape = ShapeDefaults.top,
                    key = FractalFormula::stableKey
                )

                AnimatedVisibility(
                    visible = value.formula.usesPower,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    EnhancedSliderItem(
                        value = value.power,
                        title = stringResource(R.string.fractal_power),
                        valueRange = powerRange,
                        internalStateTransformation = { it.roundTo(2) },
                        onValueChange = {
                            onValueChange(value.copy(power = it.roundTo(2).toDouble()))
                        },
                        shape = ShapeDefaults.center
                    )
                }

                CameraParams(
                    value = value,
                    onValueChange = onValueChange
                )

                FormulaSpecificParams(
                    value = value,
                    onValueChange = onValueChange
                )

                AnimatedVisibility(
                    visible = value.formula.usesIterationControls,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        EnhancedSliderItem(
                            value = value.iterations,
                            title = stringResource(R.string.iterations),
                            valueRange = iterationRange,
                            internalStateTransformation = { it.roundToInt() },
                            onValueChange = {
                                onValueChange(value.copy(iterations = it.roundToInt()))
                            },
                            shape = ShapeDefaults.center
                        )

                        DataSelector(
                            value = value.iterationPolicy,
                            onValueChange = {
                                onValueChange(value.copy(iterationPolicy = it))
                            },
                            entries = FractalIterationPolicy.entries,
                            title = stringResource(R.string.fractal_iteration_policy),
                            titleIcon = null,
                            itemContentText = { it.label() },
                            spanCount = 1,
                            containerColor = Color.Unspecified,
                            shape = ShapeDefaults.center,
                            key = FractalIterationPolicy::stableKey
                        )
                    }
                }

                AnimatedVisibility(
                    visible = value.formula.usesBailout,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    EnhancedSliderItem(
                        value = value.bailout,
                        title = stringResource(R.string.fractal_bailout),
                        valueRange = 2f..128f,
                        internalStateTransformation = { it.roundTo(2) },
                        onValueChange = {
                            onValueChange(value.copy(bailout = it.roundTo(2).toDouble()))
                        },
                        shape = ShapeDefaults.center
                    )
                }

                AnimatedVisibility(
                    visible = !value.formula.isThreeDimensional &&
                            !value.formula.isGeometric &&
                            !value.formula.isDensityVisualization,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DataSelector(
                        value = value.coloring,
                        onValueChange = {
                            onValueChange(value.copy(coloring = it))
                        },
                        entries = FractalColoring.entries,
                        title = stringResource(R.string.fractal_coloring),
                        titleIcon = null,
                        itemContentText = { it.label() },
                        spanCount = 2,
                        containerColor = Color.Unspecified,
                        shape = ShapeDefaults.center,
                        key = FractalColoring::stableKey
                    )
                }

                FractalPaletteSelector(
                    value = value.palette,
                    onValueChange = {
                        onValueChange(value.copy(palette = it))
                    }
                )

                EnhancedSliderItem(
                    value = value.paletteCycles,
                    title = stringResource(R.string.fractal_palette_cycles),
                    valueRange = FractalParams.MIN_PALETTE_CYCLES.toFloat()..16f,
                    internalStateTransformation = { it.roundTo(2) },
                    onValueChange = {
                        onValueChange(value.copy(paletteCycles = it.roundTo(2).toDouble()))
                    },
                    shape = ShapeDefaults.center
                )

                EnhancedSliderItem(
                    value = value.paletteOffset,
                    title = stringResource(R.string.fractal_palette_offset),
                    valueRange = 0f..0.99f,
                    internalStateTransformation = { it.roundTo(2) },
                    onValueChange = {
                        onValueChange(value.copy(paletteOffset = it.roundTo(2).toDouble()))
                    },
                    shape = ShapeDefaults.center
                )

                val isBackgroundColor =
                    value.formula.isThreeDimensional ||
                            value.formula.isGeometric ||
                            value.formula.isDensityVisualization
                key(value.palette) {
                    ColorRowSelector(
                        value = value.insideColor.toColor(),
                        onValueChange = {
                            onValueChange(value.copy(insideColor = it.toModel()))
                        },
                        title = stringResource(
                            if (isBackgroundColor) R.string.background_color
                            else R.string.fractal_inside_color
                        ),
                        icon = if (isBackgroundColor) {
                            Icons.Outlined.BackgroundColor
                        } else {
                            Icons.Outlined.Palette
                        },
                        allowAlpha = false,
                        defaultColors = remember(value.palette) {
                            value.palette.suggestedColors
                                .map { it.toColor() }
                                .distinctBy { it.toArgb() }
                        },
                        modifier = Modifier.container(
                            shape = ShapeDefaults.center
                        )
                    )
                }

                EnhancedSliderItem(
                    value = value.supersampling,
                    title = stringResource(
                        if (value.formula.isDensityVisualization) {
                            R.string.fractal_sampling_density
                        } else {
                            R.string.fractal_supersampling
                        }
                    ),
                    valueRange = supersamplingRange,
                    steps = FractalParams.MAX_SUPERSAMPLING -
                            FractalParams.MIN_SUPERSAMPLING - 1,
                    internalStateTransformation = { it.roundToInt() },
                    onValueChange = {
                        onValueChange(value.copy(supersampling = it.roundToInt()))
                    },
                    shape = ShapeDefaults.bottom
                )
            }
        }
    }
}

@Composable
private fun CameraParams(
    value: FractalParams,
    onValueChange: (FractalParams) -> Unit
) {
    AnimatedVisibility(
        visible = value.formula.usesCamera,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            EnhancedSliderItem(
                value = value.camera.yaw,
                title = stringResource(R.string.fractal_camera_yaw),
                valueRange = FractalCamera.MIN_YAW.toFloat()..FractalCamera.MAX_YAW.toFloat(),
                valueSuffix = "°",
                internalStateTransformation = { it.roundTo(1) },
                onValueChange = {
                    onValueChange(
                        value.copy(camera = value.camera.copy(yaw = it.roundTo(1).toDouble()))
                    )
                },
                shape = ShapeDefaults.center
            )
            EnhancedSliderItem(
                value = value.camera.pitch,
                title = stringResource(R.string.fractal_camera_pitch),
                valueRange = FractalCamera.MIN_PITCH.toFloat()..FractalCamera.MAX_PITCH.toFloat(),
                valueSuffix = "°",
                internalStateTransformation = { it.roundTo(1) },
                onValueChange = {
                    onValueChange(
                        value.copy(camera = value.camera.copy(pitch = it.roundTo(1).toDouble()))
                    )
                },
                shape = ShapeDefaults.center
            )
            EnhancedSliderItem(
                value = value.camera.distance,
                title = stringResource(R.string.fractal_camera_distance),
                valueRange = FractalCamera.MIN_DISTANCE.toFloat().rangeTo(
                    FractalCamera.MAX_DISTANCE.toFloat()
                ),
                internalStateTransformation = { it.roundTo(2) },
                onValueChange = {
                    onValueChange(
                        value.copy(camera = value.camera.copy(distance = it.roundTo(2).toDouble()))
                    )
                },
                shape = ShapeDefaults.center
            )
        }
    }
}

@Composable
private fun FormulaSpecificParams(
    value: FractalParams,
    onValueChange: (FractalParams) -> Unit
) {
    CoefficientParams(
        value = value,
        onValueChange = onValueChange
    )

    AnimatedVisibility(
        visible = value.formula.usesJuliaConstant,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        ComplexParams(
            value = value.juliaConstant,
            realTitle = stringResource(R.string.fractal_julia_real),
            imaginaryTitle = stringResource(R.string.fractal_julia_imaginary),
            onValueChange = {
                onValueChange(value.copy(juliaConstant = it))
            }
        )
    }

    AnimatedVisibility(
        visible = value.formula.usesPhoenixConstant,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        ComplexParams(
            value = value.phoenixConstant,
            realTitle = stringResource(R.string.fractal_phoenix_real),
            imaginaryTitle = stringResource(R.string.fractal_phoenix_imaginary),
            onValueChange = {
                onValueChange(value.copy(phoenixConstant = it))
            }
        )
    }

    AnimatedVisibility(
        visible = value.formula.usesNovaRelaxation,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        EnhancedSliderItem(
            value = value.novaRelaxation,
            title = stringResource(R.string.fractal_nova_relaxation),
            valueRange = FractalParams.MIN_NOVA_RELAXATION.toFloat().rangeTo(
                FractalParams.MAX_NOVA_RELAXATION.toFloat()
            ),
            internalStateTransformation = { it.roundTo(2) },
            onValueChange = {
                onValueChange(value.copy(novaRelaxation = it.roundTo(2).toDouble()))
            },
            shape = ShapeDefaults.center
        )
    }

    AnimatedVisibility(
        visible = value.formula.usesQuaternionConstant,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        QuaternionParams(
            value = value.quaternionConstant,
            onValueChange = {
                onValueChange(value.copy(quaternionConstant = it))
            }
        )
    }
}

@Composable
private fun CoefficientParams(
    value: FractalParams,
    onValueChange: (FractalParams) -> Unit
) {
    val specs = value.formula.coefficientSpecs

    AnimatedVisibility(
        visible = value.formula.usesCoefficients,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            specs.a?.let { spec ->
                CoefficientSlider(
                    value = value.coefficients.a,
                    spec = spec,
                    onValueChange = {
                        onValueChange(
                            value.copy(coefficients = value.coefficients.copy(a = it))
                        )
                    }
                )
            }
            specs.b?.let { spec ->
                CoefficientSlider(
                    value = value.coefficients.b,
                    spec = spec,
                    onValueChange = {
                        onValueChange(
                            value.copy(coefficients = value.coefficients.copy(b = it))
                        )
                    }
                )
            }
            specs.c?.let { spec ->
                CoefficientSlider(
                    value = value.coefficients.c,
                    spec = spec,
                    onValueChange = {
                        onValueChange(
                            value.copy(coefficients = value.coefficients.copy(c = it))
                        )
                    }
                )
            }
            specs.d?.let { spec ->
                CoefficientSlider(
                    value = value.coefficients.d,
                    spec = spec,
                    onValueChange = {
                        onValueChange(
                            value.copy(coefficients = value.coefficients.copy(d = it))
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun CoefficientSlider(
    value: Double,
    spec: FractalCoefficientSpec,
    onValueChange: (Double) -> Unit
) {
    EnhancedSliderItem(
        value = value,
        title = spec.label.label(),
        valueRange = spec.valueRange.start.toFloat()..spec.valueRange.endInclusive.toFloat(),
        internalStateTransformation = { it.roundTo(3) },
        onValueChange = { onValueChange(it.roundTo(3).toDouble()) },
        shape = ShapeDefaults.center
    )
}

@Composable
private fun FractalCoefficientLabel.label(): String = when (this) {
    FractalCoefficientLabel.A -> "A"
    FractalCoefficientLabel.B -> "B"
    FractalCoefficientLabel.C -> "C"
    FractalCoefficientLabel.D -> "D"
    FractalCoefficientLabel.Sigma -> "σ"
    FractalCoefficientLabel.Rho -> "ρ"
    FractalCoefficientLabel.Beta -> "β"
    FractalCoefficientLabel.Scale -> stringResource(R.string.scale)
    FractalCoefficientLabel.Fold -> stringResource(R.string.fractal_fold)
    FractalCoefficientLabel.MinimumRadius -> stringResource(R.string.fractal_minimum_radius)
}

@Composable
private fun QuaternionParams(
    value: FractalQuaternion,
    onValueChange: (FractalQuaternion) -> Unit
) {
    val range = FractalQuaternion.MIN_COMPONENT.toFloat().rangeTo(
        FractalQuaternion.MAX_COMPONENT.toFloat()
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        EnhancedSliderItem(
            value = value.x,
            title = stringResource(R.string.fractal_quaternion_x),
            valueRange = range,
            internalStateTransformation = { it.roundTo(2) },
            onValueChange = { onValueChange(value.copy(x = it.roundTo(2).toDouble())) },
            shape = ShapeDefaults.center
        )
        EnhancedSliderItem(
            value = value.y,
            title = stringResource(R.string.fractal_quaternion_y),
            valueRange = range,
            internalStateTransformation = { it.roundTo(2) },
            onValueChange = { onValueChange(value.copy(y = it.roundTo(2).toDouble())) },
            shape = ShapeDefaults.center
        )
        EnhancedSliderItem(
            value = value.z,
            title = stringResource(R.string.fractal_quaternion_z),
            valueRange = range,
            internalStateTransformation = { it.roundTo(2) },
            onValueChange = { onValueChange(value.copy(z = it.roundTo(2).toDouble())) },
            shape = ShapeDefaults.center
        )
        EnhancedSliderItem(
            value = value.w,
            title = stringResource(R.string.fractal_quaternion_w),
            valueRange = range,
            internalStateTransformation = { it.roundTo(2) },
            onValueChange = { onValueChange(value.copy(w = it.roundTo(2).toDouble())) },
            shape = ShapeDefaults.center
        )
    }
}

@Composable
private fun ComplexParams(
    value: FractalComplex,
    realTitle: String,
    imaginaryTitle: String,
    onValueChange: (FractalComplex) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        EnhancedSliderItem(
            value = value.real,
            title = realTitle,
            valueRange = -2f..2f,
            internalStateTransformation = { it.roundTo(3) },
            onValueChange = {
                onValueChange(value.copy(real = it.roundTo(3).toDouble()))
            },
            shape = ShapeDefaults.center
        )
        EnhancedSliderItem(
            value = value.imaginary,
            title = imaginaryTitle,
            valueRange = -2f..2f,
            internalStateTransformation = { it.roundTo(3) },
            onValueChange = {
                onValueChange(value.copy(imaginary = it.roundTo(3).toDouble()))
            },
            shape = ShapeDefaults.center
        )
    }
}

@Composable
private fun FractalPaletteSelector(
    value: FractalPalette,
    onValueChange: (FractalPalette) -> Unit
) {
    Column(
        modifier = Modifier.container(
            shape = ShapeDefaults.center,
            color = Color.Unspecified,
            resultPadding = 8.dp
        )
    ) {
        TitleItem(
            text = stringResource(R.string.palette),
            icon = Icons.Rounded.Palette,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        val state = rememberLazyListState()
        LazyRow(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .fadingEdges(state),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp),
            flingBehavior = enhancedFlingBehavior()
        ) {
            items(
                items = FractalPalette.entries,
                key = FractalPalette::stableKey
            ) { palette ->
                EnhancedChip(
                    selected = palette == value,
                    onClick = { onValueChange(palette) },
                    selectedColor = MaterialTheme.colorScheme.tertiaryContainer,
                    selectedContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val colors = remember(palette) {
                            palette.colors.map { it.toColor() }
                        }
                        Box(
                            modifier = Modifier
                                .width(64.dp)
                                .height(18.dp)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(Brush.horizontalGradient(colors))
                        )
                        Text(
                            text = palette.label(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
