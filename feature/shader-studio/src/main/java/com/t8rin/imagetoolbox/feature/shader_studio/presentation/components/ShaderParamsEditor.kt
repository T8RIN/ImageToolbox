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

package com.t8rin.imagetoolbox.feature.shader_studio.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParam
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParamType
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderValue
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddCircle
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.HashTag
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelectionRow
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.feature.shader_studio.presentation.screenLogic.ShaderStudioComponent

@Composable
internal fun ShaderParamsEditor(component: ShaderStudioComponent) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        PreferenceItemOverload(
            title = stringResource(R.string.params),
            subtitle = if (component.params.isEmpty()) {
                stringResource(R.string.shader_no_parameters)
            } else {
                stringResource(R.string.shader_params_count, component.params.size)
            },
            startIcon = {
                Icon(
                    imageVector = Icons.Rounded.HashTag,
                    contentDescription = null
                )
            },
            endIcon = {
                EnhancedIconButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = { component.addParam() }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AddCircle,
                        contentDescription = stringResource(R.string.add)
                    )
                }
            },
            shape = if (component.params.isEmpty()) {
                ShapeDefaults.large
            } else {
                ShapeDefaults.top
            },
            onClick = { component.addParam() },
            modifier = Modifier.fillMaxWidth()
        )
        component.params.forEachIndexed { index, param ->
            ShaderParamEditor(
                param = param,
                shape = ShapeDefaults.byIndex(index + 1, component.params.size + 1),
                onParamChange = { component.updateParam(index, it) },
                onRemove = { component.removeParam(index) }
            )
        }
    }
}

@Composable
private fun ShaderParamEditor(
    param: ShaderParam,
    shape: Shape,
    onParamChange: (ShaderParam) -> Unit,
    onRemove: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .container(shape)
            .padding(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RoundedTextField(
                value = param.name,
                onValueChange = { onParamChange(param.copy(name = it)) },
                label = stringResource(R.string.name),
                shape = ShapeDefaults.large,
                modifier = Modifier.weight(1f)
            )
            EnhancedIconButton(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                onClick = onRemove
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = stringResource(R.string.delete)
                )
            }
        }
        EnhancedButtonGroup(
            items = ShaderParamType.entries.map { it.serialName },
            selectedIndex = ShaderParamType.entries.indexOf(param.type),
            onIndexChange = { index ->
                val type = ShaderParamType.entries[index]
                onParamChange(
                    param.copy(
                        type = type,
                        defaultValue = type.defaultValue(),
                        minValue = null,
                        maxValue = null
                    )
                )
            },
            isScrollable = false,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(),
            inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.animateContentSizeNoClip()
        ) {
            val hasBounds = param.type.supportsBounds()
            val settingsCount = if (hasBounds) 3 else 1

            ShaderValueEditor(
                title = stringResource(R.string.default_value),
                value = param.defaultValue,
                shape = ShapeDefaults.byIndex(0, settingsCount),
                onValueChange = { onParamChange(param.copy(defaultValue = it)) }
            )
            if (hasBounds) {
                ShaderOptionalBoundsEditor(
                    param = param,
                    totalSize = settingsCount,
                    onParamChange = onParamChange
                )
            }
        }
    }
}

@Composable
private fun ShaderOptionalBoundsEditor(
    param: ShaderParam,
    totalSize: Int,
    onParamChange: (ShaderParam) -> Unit
) {
    ShaderValueEditor(
        title = stringResource(R.string.min),
        value = param.minValue ?: param.type.defaultValue(),
        enabled = param.minValue != null,
        shape = ShapeDefaults.byIndex(1, totalSize),
        onEnabledChange = {
            onParamChange(param.copy(minValue = if (it) param.type.defaultValue() else null))
        },
        onValueChange = { onParamChange(param.copy(minValue = it)) }
    )
    ShaderValueEditor(
        title = stringResource(R.string.max),
        value = param.maxValue ?: param.type.defaultValue(),
        enabled = param.maxValue != null,
        shape = ShapeDefaults.byIndex(2, totalSize),
        onEnabledChange = {
            onParamChange(param.copy(maxValue = if (it) param.type.defaultValue() else null))
        },
        onValueChange = { onParamChange(param.copy(maxValue = it)) }
    )
}

@Composable
private fun ShaderValueEditor(
    title: String,
    value: ShaderValue,
    enabled: Boolean = true,
    shape: Shape,
    onEnabledChange: ((Boolean) -> Unit)? = null,
    onValueChange: (ShaderValue) -> Unit
) {
    if (onEnabledChange != null) {
        PreferenceRowSwitch(
            title = title,
            checked = enabled,
            shape = shape,
            containerColor = MaterialTheme.colorScheme.surface,
            applyHorizontalPadding = false,
            modifier = Modifier.fillMaxWidth(),
            onClick = onEnabledChange,
            resultModifier = Modifier.padding(16.dp),
            additionalContent = {
                AnimatedVisibility(
                    visible = enabled,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ShaderValueContent(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        )
    } else if (value is ShaderValue.BoolValue) {
        PreferenceRowSwitch(
            title = title,
            checked = value.value,
            shape = shape,
            containerColor = MaterialTheme.colorScheme.surface,
            applyHorizontalPadding = false,
            modifier = Modifier.fillMaxWidth(),
            onClick = { onValueChange(ShaderValue.BoolValue(it)) }
        )
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .container(
                    shape = shape,
                    color = MaterialTheme.colorScheme.surface,
                    autoShadowElevation = 0.dp,
                    isStandaloneContainer = false
                )
                .then(
                    if (value is ShaderValue.ColorValue) {
                        Modifier.padding(
                            top = 12.dp,
                            start = 12.dp,
                            end = 12.dp,
                            bottom = 8.dp
                        )
                    } else {
                        Modifier.padding(12.dp)
                    }
                )
        ) {
            Text(
                text = title,
                style = PreferenceItemDefaults.TitleFontStyle
            )
            ShaderValueContent(
                value = value,
                onValueChange = onValueChange
            )
        }
    }
}

@Composable
private fun ShaderValueContent(
    value: ShaderValue,
    modifier: Modifier = Modifier,
    onValueChange: (ShaderValue) -> Unit
) {
    AnimatedContent(
        targetState = value,
        contentKey = { it::class.simpleName },
        modifier = Modifier.fillMaxWidth()
    ) { state ->
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (state) {
                is ShaderValue.FloatValue -> FloatValueField(
                    value = state,
                    onValueChange = onValueChange
                )

                is ShaderValue.IntValue -> IntValueField(
                    value = state,
                    onValueChange = onValueChange
                )

                is ShaderValue.ColorValue -> ColorSelectionRow(
                    value = state.toComposeColor(),
                    allowAlpha = true,
                    contentPadding = PaddingValues(0.dp),
                    onValueChange = { onValueChange(it.toShaderColorValue()) }
                )

                is ShaderValue.Vec2Value -> Vec2ValueFields(
                    value = state,
                    onValueChange = onValueChange
                )

                is ShaderValue.BoolValue -> Unit
            }
        }
    }
}

@Composable
private fun FloatValueField(
    value: ShaderValue.FloatValue,
    onValueChange: (ShaderValue) -> Unit
) {
    var text by remember(value.value) { mutableStateOf(value.value.toString()) }
    RoundedTextField(
        value = text,
        onValueChange = {
            text = it
            it.toFloatOrNull()?.let { parsed -> onValueChange(ShaderValue.FloatValue(parsed)) }
        },
        label = "",
        shape = ShapeDefaults.large,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun IntValueField(
    value: ShaderValue.IntValue,
    onValueChange: (ShaderValue) -> Unit
) {
    var text by remember(value.value) { mutableStateOf(value.value.toString()) }
    RoundedTextField(
        value = text,
        onValueChange = {
            text = it
            it.toIntOrNull()?.let { parsed -> onValueChange(ShaderValue.IntValue(parsed)) }
        },
        label = "",
        shape = ShapeDefaults.large,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun Vec2ValueFields(
    value: ShaderValue.Vec2Value,
    onValueChange: (ShaderValue) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        var x by remember(value.x) { mutableStateOf(value.x.toString()) }
        var y by remember(value.y) { mutableStateOf(value.y.toString()) }
        RoundedTextField(
            value = x,
            onValueChange = {
                x = it
                it.toFloatOrNull()
                    ?.let { parsed -> onValueChange(ShaderValue.Vec2Value(parsed, value.y)) }
            },
            label = "X",
            shape = ShapeDefaults.large,
            modifier = Modifier.weight(1f)
        )
        RoundedTextField(
            value = y,
            onValueChange = {
                y = it
                it.toFloatOrNull()
                    ?.let { parsed -> onValueChange(ShaderValue.Vec2Value(value.x, parsed)) }
            },
            label = "Y",
            shape = ShapeDefaults.large,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun ShaderParamType.defaultValue(): ShaderValue = when (this) {
    ShaderParamType.Float -> ShaderValue.FloatValue(0f)
    ShaderParamType.Int -> ShaderValue.IntValue(0)
    ShaderParamType.Bool -> ShaderValue.BoolValue(false)
    ShaderParamType.Color -> ShaderValue.ColorValue(255, 255, 255, 255)
    ShaderParamType.Vec2 -> ShaderValue.Vec2Value(0f, 0f)
}

private fun ShaderParamType.supportsBounds(): Boolean =
    this != ShaderParamType.Bool && this != ShaderParamType.Color

private fun ShaderValue.ColorValue.toComposeColor(): Color =
    Color(red = red, green = green, blue = blue, alpha = alpha)

private fun Color.toShaderColorValue(): ShaderValue.ColorValue {
    val argb = toArgb()
    return ShaderValue.ColorValue(
        red = (argb shr 16) and 0xFF,
        green = (argb shr 8) and 0xFF,
        blue = argb and 0xFF,
        alpha = (argb shr 24) and 0xFF
    )
}
