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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ShaderParams
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParam
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParamType
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderPreset
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderValidator
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderValue
import com.t8rin.imagetoolbox.core.filters.presentation.utils.localizedMessage
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddCircle
import com.t8rin.imagetoolbox.core.resources.icons.Code
import com.t8rin.imagetoolbox.core.resources.icons.HashTag
import com.t8rin.imagetoolbox.core.resources.icons.UploadFile
import com.t8rin.imagetoolbox.core.resources.icons.WarningAmber
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelectionRow
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.FileSelector
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

@Composable
fun ShaderParamsItem(
    value: ShaderParams,
    presets: List<ShaderPreset>,
    onValueChange: (ShaderParams) -> Unit,
    onImportPreset: ((Uri, (ShaderPreset) -> Unit) -> Unit)? = null,
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

        val preset = value.preset ?: return@Column
        val errors = ShaderValidator.validate(preset)

        Spacer(Modifier.height(4.dp))

        if (errors.isNotEmpty()) {
            PreferenceItemOverload(
                title = stringResource(R.string.invalid_shader),
                subtitle = errors.joinToString("\n") { it.localizedMessage() },
                startIcon = {
                    Icon(
                        imageVector = Icons.Rounded.WarningAmber,
                        contentDescription = null
                    )
                },
                shape = ShapeDefaults.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        if (preset.params.isEmpty()) return@Column

        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            PreferenceItemOverload(
                title = stringResource(R.string.params),
                subtitle = stringResource(R.string.shader_params_count, preset.params.size),
                startIcon = {
                    Icon(
                        imageVector = Icons.Rounded.HashTag,
                        contentDescription = null
                    )
                },
                shape = ShapeDefaults.top,
                modifier = Modifier.fillMaxWidth()
            )
        }

        preset.params.forEachIndexed { index, param ->
            ShaderParamItem(
                param = param,
                value = value.resolvedValues[param.name] ?: param.defaultValue,
                enabled = !previewOnly,
                shape = ShapeDefaults.byIndex(index + 1, preset.params.size + 1),
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

@Composable
private fun ShaderPresetSelector(
    selectedPreset: ShaderPreset?,
    presets: List<ShaderPreset>,
    enabled: Boolean,
    onPresetSelected: (ShaderPreset?) -> Unit,
    onImportPreset: ((Uri, (ShaderPreset) -> Unit) -> Unit)?
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    val importPicker = rememberFilePicker(
        mimeType = MimeType.All,
        onSuccess = { uri: Uri ->
            onImportPreset?.invoke(uri) { preset ->
                onPresetSelected(preset)
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row {
            if (onImportPreset != null && enabled) {
                FileSelector(
                    value = null,
                    title = stringResource(R.string.shader_file),
                    subtitle = selectedPreset?.name ?: stringResource(R.string.no_shader_selected),
                    onValueChange = { uri ->
                        onImportPreset.invoke(uri) { preset ->
                            onPresetSelected(preset)
                        }
                    },
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = ShapeDefaults.start,
                    modifier = Modifier.weight(1f)
                )
            } else {
                PreferenceRow(
                    title = stringResource(R.string.shader_file),
                    subtitle = selectedPreset?.name ?: stringResource(R.string.no_shader_selected),
                    enabled = enabled,
                    shape = if (onImportPreset != null) ShapeDefaults.start else ShapeDefaults.large,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    onClick = { showSheet = true },
                    modifier = Modifier.weight(1f)
                )
            }
            if (onImportPreset != null) {
                EnhancedIconButton(
                    enabled = enabled,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = { showSheet = true },
                    modifier = Modifier.height(64.dp)
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
        confirmButton = {},
        enableBottomContentWeight = false,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TitleItem(
                    text = stringResource(R.string.saved_shaders),
                    icon = Icons.Rounded.Code
                )
                Spacer(Modifier.weight(1f))
                if (onImportPreset != null) {
                    EnhancedIconButton(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = importPicker::pickFile
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.UploadFile,
                            contentDescription = stringResource(R.string.import_word)
                        )
                    }
                }
            }
        }
    ) {
        if (presets.isEmpty()) {
            PreferenceItemOverload(
                title = stringResource(R.string.no_shader_selected),
                subtitle = stringResource(R.string.import_word),
                startIcon = {
                    Icon(
                        imageVector = Icons.Outlined.AddCircle,
                        contentDescription = null
                    )
                },
                onClick = importPicker::pickFile,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                flingBehavior = enhancedFlingBehavior()
            ) {
                itemsIndexed(
                    items = presets,
                    key = { _, preset -> preset.name }
                ) { index, preset ->
                    PreferenceItemOverload(
                        title = preset.name,
                        subtitle = stringResource(R.string.shader_params_count, preset.params.size),
                        startIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Code,
                                contentDescription = null
                            )
                        },
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
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .container(shape = shape)
            .padding(vertical = 8.dp)
    ) {
        when (param.type) {
            ShaderParamType.Float -> FloatShaderParamItem(
                param = param,
                value = value as? ShaderValue.FloatValue
                    ?: param.defaultValue as ShaderValue.FloatValue,
                enabled = enabled,
                onValueChange = onValueChange
            )

            ShaderParamType.Int -> IntShaderParamItem(
                param = param,
                value = value as? ShaderValue.IntValue
                    ?: param.defaultValue as ShaderValue.IntValue,
                enabled = enabled,
                onValueChange = onValueChange
            )

            ShaderParamType.Bool -> BoolShaderParamItem(
                param = param,
                value = value as? ShaderValue.BoolValue
                    ?: param.defaultValue as ShaderValue.BoolValue,
                enabled = enabled,
                onValueChange = onValueChange
            )

            ShaderParamType.Color -> ColorShaderParamItem(
                param = param,
                value = value as? ShaderValue.ColorValue
                    ?: param.defaultValue as ShaderValue.ColorValue,
                enabled = enabled,
                onValueChange = onValueChange
            )

            ShaderParamType.Vec2 -> Vec2ShaderParamItem(
                param = param,
                value = value as? ShaderValue.Vec2Value
                    ?: param.defaultValue as ShaderValue.Vec2Value,
                enabled = enabled,
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
        onValueChange = {
            sliderState = it
            onValueChange(ShaderValue.FloatValue(it.roundTo(3)))
        },
        internalStateTransformation = { it.roundTo(3) },
        behaveAsContainer = false,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@Composable
private fun IntShaderParamItem(
    param: ShaderParam,
    value: ShaderValue.IntValue,
    enabled: Boolean,
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
        steps = (range.last - range.first - 1).coerceIn(0, MAX_INT_STEPS),
        onValueChange = {
            sliderState = it
            onValueChange(ShaderValue.IntValue(it.toInt()))
        },
        internalStateTransformation = { it.toInt() },
        behaveAsContainer = false,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@Composable
private fun BoolShaderParamItem(
    param: ShaderParam,
    value: ShaderValue.BoolValue,
    enabled: Boolean,
    onValueChange: (ShaderValue) -> Unit
) {
    PreferenceRowSwitch(
        title = param.name,
        checked = value.value,
        enabled = enabled,
        onClick = { onValueChange(ShaderValue.BoolValue(it)) },
        drawContainer = false,
        resultModifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun ColorShaderParamItem(
    param: ShaderParam,
    value: ShaderValue.ColorValue,
    enabled: Boolean,
    onValueChange: (ShaderValue) -> Unit
) {
    PreferenceRow(
        title = param.name,
        subtitle = "#${value.red.hex()}${value.green.hex()}${value.blue.hex()}${value.alpha.hex()}",
        enabled = enabled,
        drawContainer = false,
        onClick = null,
        additionalContent = {
            ColorSelectionRow(
                value = value.toComposeColor(),
                onValueChange = { color ->
                    onValueChange(color.toShaderColorValue())
                },
                allowAlpha = true,
                contentPadding = PaddingValues(horizontal = 16.dp)
            )
        }
    )
}

@Composable
private fun Vec2ShaderParamItem(
    param: ShaderParam,
    value: ShaderValue.Vec2Value,
    enabled: Boolean,
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
        modifier = Modifier.padding(horizontal = 8.dp)
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
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

private fun ShaderParam.floatRange(defaultValue: Float): ClosedFloatingPointRange<Float> {
    val min = (minValue as? ShaderValue.FloatValue)?.value
    val max = (maxValue as? ShaderValue.FloatValue)?.value
    return rangeFor(defaultValue, min, max)
}

private fun ShaderParam.intRange(defaultValue: Int): IntRange {
    val min = (minValue as? ShaderValue.IntValue)?.value ?: (defaultValue - DEFAULT_INT_SPREAD)
    val max = (maxValue as? ShaderValue.IntValue)?.value ?: (defaultValue + DEFAULT_INT_SPREAD)
    return min.coerceAtMost(defaultValue)..max.coerceAtLeast(defaultValue)
}

private fun rangeFor(
    defaultValue: Float,
    minValue: Float?,
    maxValue: Float?
): ClosedFloatingPointRange<Float> {
    val min = minValue ?: (defaultValue - DEFAULT_FLOAT_SPREAD)
    val max = maxValue ?: (defaultValue + DEFAULT_FLOAT_SPREAD)
    return min.coerceAtMost(defaultValue)..max.coerceAtLeast(defaultValue)
}

private fun ShaderValue.ColorValue.toComposeColor(): Color =
    Color(red = red, green = green, blue = blue, alpha = alpha)

private fun Color.toShaderColorValue(): ShaderValue.ColorValue {
    val argb = toArgb()
    return ShaderValue.ColorValue(
        red = (argb shr RED_SHIFT) and COLOR_MASK,
        green = (argb shr GREEN_SHIFT) and COLOR_MASK,
        blue = argb and COLOR_MASK,
        alpha = (argb shr ALPHA_SHIFT) and COLOR_MASK
    )
}

private fun Int.hex(): String = coerceIn(COLOR_MIN, COLOR_MAX)
    .toString(HEX_RADIX)
    .padStart(HEX_CHANNEL_LENGTH, '0')

private const val DEFAULT_FLOAT_SPREAD = 1f
private const val DEFAULT_INT_SPREAD = 10
private const val MAX_INT_STEPS = 100
private const val HEX_RADIX = 16
private const val HEX_CHANNEL_LENGTH = 2
private const val COLOR_MIN = 0
private const val COLOR_MAX = 255
private const val COLOR_MASK = 0xFF
private const val ALPHA_SHIFT = 24
private const val RED_SHIFT = 16
private const val GREEN_SHIFT = 8
