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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ShaderParams
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParam
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParamType
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderPreset
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderValue
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Code
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelectionRow
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.FileSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRow
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.coroutines.launch

@Composable
fun ShaderParamsItem(
    value: ShaderParams,
    presets: List<ShaderPreset>,
    onValueChange: (ShaderParams) -> Unit,
    onImportPreset: (suspend (Uri) -> ShaderPreset?)? = null,
    modifier: Modifier = Modifier,
    previewOnly: Boolean = false,
    showPresetSelector: Boolean = true
) {
    Column(modifier = modifier) {
        if (showPresetSelector) {
            ShaderPresetSelector(
                selectedPreset = value.preset,
                presets = presets,
                enabled = !previewOnly,
                onPresetSelected = {
                    onValueChange(value.withPreset(it))
                },
                onImportPreset = onImportPreset
            )
        }

        Spacer(Modifier.height(4.dp))

        val preset = value.preset?.takeIf { it.params.isNotEmpty() } ?: return@Column

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            preset.params.forEachIndexed { index, param ->
                ShaderParamItem(
                    param = param,
                    value = value.resolvedValues[param.name] ?: param.defaultValue,
                    enabled = !previewOnly,
                    shape = ShapeDefaults.byIndex(index, preset.params.size),
                    onValueChange = { shaderValue ->
                        onValueChange(
                            value.copy(
                                values = value.resolvedValues + (param.name to shaderValue)
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ShaderPresetSelector(
    selectedPreset: ShaderPreset?,
    presets: List<ShaderPreset>,
    enabled: Boolean,
    onPresetSelected: (ShaderPreset?) -> Unit,
    onImportPreset: (suspend (Uri) -> ShaderPreset?)?
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val importPreset: (Uri) -> Unit = { uri ->
        scope.launch {
            onImportPreset?.invoke(uri)?.let(onPresetSelected)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Max)
        ) {
            val hasPresets = presets.isNotEmpty() && onImportPreset != null

            FileSelector(
                value = null,
                title = stringResource(R.string.shader_file),
                subtitle = selectedPreset?.name ?: stringResource(R.string.no_shader_selected),
                onValueChange = importPreset,
                shape = if (hasPresets) ShapeDefaults.start else ShapeDefaults.default,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )

            if (hasPresets) {
                EnhancedIconButton(
                    enabled = enabled,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = { showSheet = true },
                    shape = ShapeDefaults.end,
                    modifier = Modifier.fillMaxHeight(),
                    forceMinimumInteractiveComponentSize = false
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Code,
                        contentDescription = stringResource(R.string.saved_shaders)
                    )
                }
            }
        }
    }

    EnhancedModalBottomSheet(
        visible = showSheet,
        onDismiss = { showSheet = it },
        confirmButton = {
            EnhancedButton(
                onClick = { showSheet = false }
            ) {
                Text(stringResource(R.string.close))
            }
        },
        title = {
            TitleItem(
                text = stringResource(R.string.saved_shaders),
                icon = Icons.Rounded.Code
            )
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            flingBehavior = enhancedFlingBehavior(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(
                items = presets,
                key = { _, preset -> preset.name }
            ) { index, preset ->
                PreferenceItemOverload(
                    title = preset.name,
                    subtitle = stringResource(R.string.shader_params_count, preset.params.size),
                    onClick = {
                        showSheet = false
                        onPresetSelected(preset)
                    },
                    shape = ShapeDefaults.byIndex(index, presets.size),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ShaderParamItem(
    param: ShaderParam,
    value: ShaderValue,
    enabled: Boolean,
    shape: Shape,
    onValueChange: (ShaderValue) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        when (param.type) {
            ShaderParamType.Float -> FloatShaderParamItem(
                param = param,
                value = value as? ShaderValue.FloatValue
                    ?: param.defaultValue as ShaderValue.FloatValue,
                enabled = enabled,
                shape = shape,
                onValueChange = onValueChange
            )

            ShaderParamType.Int -> IntShaderParamItem(
                param = param,
                value = value as? ShaderValue.IntValue
                    ?: param.defaultValue as ShaderValue.IntValue,
                enabled = enabled,
                shape = shape,
                onValueChange = onValueChange
            )

            ShaderParamType.Bool -> BoolShaderParamItem(
                param = param,
                value = value as? ShaderValue.BoolValue
                    ?: param.defaultValue as ShaderValue.BoolValue,
                enabled = enabled,
                shape = shape,
                onValueChange = onValueChange
            )

            ShaderParamType.Color -> ColorShaderParamItem(
                param = param,
                value = value as? ShaderValue.ColorValue
                    ?: param.defaultValue as ShaderValue.ColorValue,
                enabled = enabled,
                shape = shape,
                onValueChange = onValueChange
            )

            ShaderParamType.Vec2 -> Vec2ShaderParamItem(
                param = param,
                value = value as? ShaderValue.Vec2Value
                    ?: param.defaultValue as ShaderValue.Vec2Value,
                enabled = enabled,
                shape = shape,
                onValueChange = onValueChange
            )
        }
    }
}

@Composable
private fun FloatShaderParamItem(
    param: ShaderParam,
    value: ShaderValue.FloatValue,
    enabled: Boolean,
    shape: Shape,
    onValueChange: (ShaderValue) -> Unit
) {
    var sliderState by remember(param.name, value.value) {
        mutableFloatStateOf(value.value)
    }

    EnhancedSliderItem(
        value = sliderState,
        title = param.name,
        valueRange = param.floatRange(value.value),
        enabled = enabled,
        shape = shape,
        onValueChange = {
            sliderState = it
            onValueChange(ShaderValue.FloatValue(it.roundTo(3)))
        },
        internalStateTransformation = { it.roundTo(3) }
    )
}

@Composable
private fun IntShaderParamItem(
    param: ShaderParam,
    value: ShaderValue.IntValue,
    enabled: Boolean,
    shape: Shape,
    onValueChange: (ShaderValue) -> Unit
) {
    var sliderState by remember(param.name, value.value) {
        mutableFloatStateOf(value.value.toFloat())
    }
    val range = param.intRange(value.value)

    EnhancedSliderItem(
        value = sliderState,
        title = param.name,
        valueRange = range.first.toFloat()..range.last.toFloat(),
        enabled = enabled,
        shape = shape,
        steps = (range.last - range.first - 1).coerceIn(0, MAX_INT_STEPS),
        onValueChange = {
            sliderState = it
            onValueChange(ShaderValue.IntValue(it.toInt()))
        },
        internalStateTransformation = { it.toInt() }
    )
}

@Composable
private fun BoolShaderParamItem(
    param: ShaderParam,
    value: ShaderValue.BoolValue,
    enabled: Boolean,
    shape: Shape,
    onValueChange: (ShaderValue) -> Unit
) {
    PreferenceRowSwitch(
        title = param.name,
        checked = value.value,
        enabled = enabled,
        shape = shape,
        onClick = { onValueChange(ShaderValue.BoolValue(it)) },
        applyHorizontalPadding = false,
        resultModifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun ColorShaderParamItem(
    param: ShaderParam,
    value: ShaderValue.ColorValue,
    enabled: Boolean,
    shape: Shape,
    onValueChange: (ShaderValue) -> Unit
) {
    PreferenceRow(
        title = param.name,
        enabled = enabled,
        resultModifier = Modifier.padding(
            start = 16.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = 8.dp
        ),
        shape = shape,
        onClick = null,
        applyHorizontalPadding = false,
        additionalContent = {
            ColorSelectionRow(
                value = value.toComposeColor(),
                onValueChange = { color ->
                    onValueChange(color.toShaderColorValue())
                },
                allowAlpha = true
            )
        }
    )
}

@Composable
private fun Vec2ShaderParamItem(
    param: ShaderParam,
    value: ShaderValue.Vec2Value,
    enabled: Boolean,
    shape: Shape,
    onValueChange: (ShaderValue) -> Unit
) {
    val min = param.minValue as? ShaderValue.Vec2Value
    val max = param.maxValue as? ShaderValue.Vec2Value

    var xState by remember(param.name, value.x) {
        mutableFloatStateOf(value.x)
    }
    var yState by remember(param.name, value.y) {
        mutableFloatStateOf(value.y)
    }

    Column(
        modifier = Modifier
            .container(
                shape = shape,
                resultPadding = 0.dp
            )
            .padding(
                horizontal = 8.dp,
                vertical = 4.dp
            )
    ) {
        EnhancedSliderItem(
            value = xState,
            title = "${param.name}.x",
            valueRange = rangeFor(value.x, min?.x, max?.x),
            enabled = enabled,
            onValueChange = {
                xState = it
                onValueChange(ShaderValue.Vec2Value(it.roundTo(3), yState))
            },
            internalStateTransformation = { it.roundTo(3) },
            behaveAsContainer = false,
            titleFontWeight = FontWeight.Medium
        )
        EnhancedSliderItem(
            value = yState,
            title = "${param.name}.y",
            valueRange = rangeFor(value.y, min?.y, max?.y),
            enabled = enabled,
            onValueChange = {
                yState = it
                onValueChange(ShaderValue.Vec2Value(xState, it.roundTo(3)))
            },
            internalStateTransformation = { it.roundTo(3) },
            behaveAsContainer = false,
            titleFontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ShaderParam.floatRange(
    defaultValue: Float
): ClosedFloatingPointRange<Float> {
    val min = (minValue as? ShaderValue.FloatValue)?.value
    val max = (maxValue as? ShaderValue.FloatValue)?.value

    return rangeFor(defaultValue, min, max)
}

@Composable
private fun ShaderParam.intRange(
    defaultValue: Int
): IntRange = remember(minValue, maxValue, defaultValue) {
    val min = (minValue as? ShaderValue.IntValue)?.value ?: (defaultValue - DEFAULT_INT_SPREAD)
    val max = (maxValue as? ShaderValue.IntValue)?.value ?: (defaultValue + DEFAULT_INT_SPREAD)

    min.coerceAtMost(defaultValue)..max.coerceAtLeast(defaultValue)
}

@Composable
private fun rangeFor(
    defaultValue: Float,
    minValue: Float?,
    maxValue: Float?
): ClosedFloatingPointRange<Float> = remember(minValue, maxValue, defaultValue) {
    val min = minValue ?: (defaultValue - DEFAULT_FLOAT_SPREAD)
    val max = maxValue ?: (defaultValue + DEFAULT_FLOAT_SPREAD)

    min.coerceAtMost(defaultValue)..max.coerceAtLeast(defaultValue)
}

@Composable
private fun ShaderValue.ColorValue.toComposeColor(): Color = remember(this) {
    Color(red = red, green = green, blue = blue, alpha = alpha)
}

private fun Color.toShaderColorValue(): ShaderValue.ColorValue {
    val argb = toArgb()
    return ShaderValue.ColorValue(
        red = (argb shr RED_SHIFT) and COLOR_MASK,
        green = (argb shr GREEN_SHIFT) and COLOR_MASK,
        blue = argb and COLOR_MASK,
        alpha = (argb shr ALPHA_SHIFT) and COLOR_MASK
    )
}

private const val DEFAULT_FLOAT_SPREAD = 1f
private const val DEFAULT_INT_SPREAD = 10
private const val MAX_INT_STEPS = 100
private const val COLOR_MASK = 0xFF
private const val ALPHA_SHIFT = 24
private const val RED_SHIFT = 16
private const val GREEN_SHIFT = 8
