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

package com.t8rin.imagetoolbox.feature.shader_studio.presentation

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.data.utils.safeAspectRatio
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParam
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParamType
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderPreset
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderValue
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddCircle
import com.t8rin.imagetoolbox.core.resources.icons.Code
import com.t8rin.imagetoolbox.core.resources.icons.ContentCopy
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.DownloadFile
import com.t8rin.imagetoolbox.core.resources.icons.EditAlt
import com.t8rin.imagetoolbox.core.resources.icons.FileOpen
import com.t8rin.imagetoolbox.core.resources.icons.HashTag
import com.t8rin.imagetoolbox.core.resources.icons.IosShare
import com.t8rin.imagetoolbox.core.resources.icons.MoreVert
import com.t8rin.imagetoolbox.core.resources.icons.Save
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelectionRow
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedDropdownMenu
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.shader_studio.presentation.screenLogic.ShaderStudioComponent

@Composable
fun ShaderStudioContent(
    component: ShaderStudioComponent
) {
    var exportingPreset by remember { mutableStateOf<ShaderPreset?>(null) }
    var showShaderLibrary by rememberSaveable { mutableStateOf(false) }

    val importPicker = rememberFilePicker(
        mimeType = MimeType.All,
        onSuccess = component::importPreset
    )
    val exportPicker = rememberFileCreator(
        mimeType = MimeType.Txt,
        onSuccess = { uri: Uri ->
            exportingPreset?.let { component.exportPreset(it, uri) }
            exportingPreset = null
        }
    )

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = true,
        title = {
            Text(
                text = stringResource(R.string.shader_studio),
                textAlign = TextAlign.Center,
                modifier = Modifier.marquee()
            )
        },
        onGoBack = component.onGoBack,
        actions = {},
        topAppBarPersistentActions = {
            TopAppBarEmoji()
        },
        imagePreview = {
            ShaderPreview(component)
        },
        controls = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ShaderPreviewSourceSelector(component)
                ShaderPresetEditor(component)
                ShaderParamsEditor(component)
                val presets by component.presets.collectAsStateWithLifecycle()

                if (presets.isEmpty()) {
                    PreferenceItemOverload(
                        title = stringResource(R.string.saved_shaders),
                        subtitle = stringResource(R.string.shader_library_sub),
                        startIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Code,
                                contentDescription = null
                            )
                        },
                        onClick = { showShaderLibrary = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        buttons = { actions ->
            ShaderStudioButtons(
                actions = actions,
                onImport = importPicker::pickFile,
                onSave = component::savePreset,
                canSave = component.canSave
            )
        },
        canShowScreenData = true
    )

    ShaderLibrarySheet(
        visible = showShaderLibrary,
        component = component,
        onDismiss = { showShaderLibrary = false },
        onExportPreset = {
            exportingPreset = it
            exportPicker.make("${it.name}.itshader")
        }
    )
}

@Composable
private fun ShaderPreview(component: ShaderStudioComponent) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        var aspectRatio by remember {
            mutableFloatStateOf(1f)
        }

        Picture(
            model = component.previewModel.data,
            modifier = Modifier
                .container(MaterialTheme.shapes.medium)
                .aspectRatio(aspectRatio),
            onSuccess = {
                aspectRatio = it.result.image.toBitmap().safeAspectRatio
            },
            isLoadingFromDifferentPlace = component.isImageLoading,
            shape = MaterialTheme.shapes.medium,
            contentScale = ContentScale.FillBounds,
            transformations = remember(component.shaderSource, component.params) {
                component.getPreviewTransformations()
            },
        )
        if (component.isImageLoading) EnhancedLoadingIndicator()
    }
}

@Composable
private fun ShaderPreviewSourceSelector(component: ShaderStudioComponent) {
    val previewModel = component.previewModel

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Max)
    ) {
        ImageSelector(
            value = previewModel.data,
            onValueChange = { component.setFilterPreviewModel(it.toString()) },
            title = stringResource(R.string.filter_preview_image),
            subtitle = stringResource(R.string.filter_preview_image_sub),
            contentScale = ContentScale.Crop,
            color = Color.Unspecified,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            shape = ShapeDefaults.start
        )
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(2) { index ->
                val shape = if (index == 0) {
                    ShapeDefaults.topEnd
                } else {
                    ShapeDefaults.bottomEnd
                }
                val containerColor = takeColorFromScheme {
                    when (previewModel.data) {
                        R.drawable.filter_preview_source if index == 0 -> secondary
                        R.drawable.filter_preview_source_3 if index == 1 -> secondary
                        else -> secondaryContainer
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(shape)
                        .hapticsClickable {
                            component.setFilterPreviewModel(index.toString())
                        }
                        .container(
                            color = containerColor,
                            shape = shape,
                            resultPadding = 0.dp
                        )
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AutoSizeText(
                        text = (index + 1).toString(),
                        color = contentColorFor(containerColor)
                    )
                }
            }
        }
    }
}

@Composable
private fun ShaderLibrarySheet(
    visible: Boolean,
    component: ShaderStudioComponent,
    onDismiss: () -> Unit,
    onExportPreset: (ShaderPreset) -> Unit
) {
    val presets by component.presets.collectAsStateWithLifecycle()

    LaunchedEffect(presets) {
        if (presets.isEmpty()) onDismiss()
    }

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = { if (!it) onDismiss() },
        confirmButton = {
            EnhancedButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.close))
            }
        },
        title = {
            TitleItem(
                text = stringResource(R.string.saved_shaders),
                icon = Icons.Rounded.Code
            )
        },
    ) {
        ShaderLibrary(
            presets = presets,
            onEdit = {
                component.editPreset(it)
                onDismiss()
            },
            onDuplicate = component::duplicatePreset,
            onDelete = component::deletePreset,
            onShare = component::sharePreset,
            onExport = onExportPreset
        )
    }
}

@Composable
private fun ShaderPresetEditor(component: ShaderStudioComponent) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .container(MaterialTheme.shapes.extraLarge)
            .padding(16.dp)
    ) {
        TitleItem(
            text = stringResource(R.string.shader),
            icon = Icons.Rounded.EditAlt,
            modifier = Modifier.padding(
                bottom = 8.dp
            )
        )
        RoundedTextField(
            value = component.name,
            onValueChange = component::updateName,
            label = stringResource(R.string.name),
            shape = ShapeDefaults.large,
            modifier = Modifier.fillMaxWidth()
        )
        RoundedTextField(
            value = component.shaderSource,
            onValueChange = component::updateShaderSource,
            hint = stringResource(R.string.shader_main_body_info),
            shape = ShapeDefaults.large,
            singleLine = false,
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Default
            ),
            modifier = Modifier.fillMaxWidth(),
            supportingText = component.validationErrors.takeIf { it.isNotEmpty() }?.let {
                {
                    Text(
                        text = component.validationErrors.joinToString("\n"),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }
}

@Composable
private fun ShaderParamsEditor(component: ShaderStudioComponent) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        PreferenceItemOverload(
            title = stringResource(R.string.params),
            subtitle = stringResource(R.string.shader_no_parameters),
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
        Row {
            RoundedTextField(
                value = param.name,
                onValueChange = { onParamChange(param.copy(name = it)) },
                label = stringResource(R.string.name),
                shape = ShapeDefaults.large,
                modifier = Modifier.weight(1f)
            )
            EnhancedIconButton(onClick = onRemove) {
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
            }
        )
        ShaderValueEditor(
            title = stringResource(R.string.default_value),
            value = param.defaultValue,
            onValueChange = { onParamChange(param.copy(defaultValue = it)) }
        )
        if (param.type != ShaderParamType.Bool) {
            ShaderOptionalBoundsEditor(
                param = param,
                onParamChange = onParamChange
            )
        }
    }
}

@Composable
private fun ShaderOptionalBoundsEditor(
    param: ShaderParam,
    onParamChange: (ShaderParam) -> Unit
) {
    ShaderValueEditor(
        title = stringResource(R.string.min),
        value = param.minValue ?: param.type.defaultValue(),
        enabled = param.minValue != null,
        onEnabledChange = {
            onParamChange(param.copy(minValue = if (it) param.type.defaultValue() else null))
        },
        onValueChange = { onParamChange(param.copy(minValue = it)) }
    )
    ShaderValueEditor(
        title = stringResource(R.string.max),
        value = param.maxValue ?: param.type.defaultValue(),
        enabled = param.maxValue != null,
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
    onEnabledChange: ((Boolean) -> Unit)? = null,
    onValueChange: (ShaderValue) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (onEnabledChange != null) {
            PreferenceRowSwitch(
                title = title,
                checked = enabled,
                drawContainer = false,
                onClick = onEnabledChange
            )
        } else {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge
            )
        }
        if (enabled) {
            when (value) {
                is ShaderValue.FloatValue -> FloatValueField(value, title, onValueChange)
                is ShaderValue.IntValue -> IntValueField(value, title, onValueChange)
                is ShaderValue.BoolValue -> PreferenceRowSwitch(
                    title = title,
                    checked = value.value,
                    drawContainer = false,
                    onClick = { onValueChange(ShaderValue.BoolValue(it)) }
                )

                is ShaderValue.ColorValue -> ColorSelectionRow(
                    value = value.toComposeColor(),
                    allowAlpha = true,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    onValueChange = { onValueChange(it.toShaderColorValue()) }
                )

                is ShaderValue.Vec2Value -> Vec2ValueFields(value, title, onValueChange)
            }
        }
    }
}

@Composable
private fun FloatValueField(
    value: ShaderValue.FloatValue,
    title: String,
    onValueChange: (ShaderValue) -> Unit
) {
    var text by remember(value.value) { mutableStateOf(value.value.toString()) }
    RoundedTextField(
        value = text,
        onValueChange = {
            text = it
            it.toFloatOrNull()?.let { parsed -> onValueChange(ShaderValue.FloatValue(parsed)) }
        },
        label = title,
        shape = ShapeDefaults.large,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun IntValueField(
    value: ShaderValue.IntValue,
    title: String,
    onValueChange: (ShaderValue) -> Unit
) {
    var text by remember(value.value) { mutableStateOf(value.value.toString()) }
    RoundedTextField(
        value = text,
        onValueChange = {
            text = it
            it.toIntOrNull()?.let { parsed -> onValueChange(ShaderValue.IntValue(parsed)) }
        },
        label = title,
        shape = ShapeDefaults.large,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun Vec2ValueFields(
    value: ShaderValue.Vec2Value,
    title: String,
    onValueChange: (ShaderValue) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        var x by remember(value.x) { mutableStateOf(value.x.toString()) }
        var y by remember(value.y) { mutableStateOf(value.y.toString()) }
        RoundedTextField(
            value = x,
            onValueChange = {
                x = it
                it.toFloatOrNull()
                    ?.let { parsed -> onValueChange(ShaderValue.Vec2Value(parsed, value.y)) }
            },
            label = "$title X",
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
            label = "$title Y",
            shape = ShapeDefaults.large,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ShaderLibrary(
    presets: List<ShaderPreset>,
    onEdit: (ShaderPreset) -> Unit,
    onDuplicate: (ShaderPreset) -> Unit,
    onDelete: (ShaderPreset) -> Unit,
    onShare: (ShaderPreset) -> Unit,
    onExport: (ShaderPreset) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .enhancedVerticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        presets.forEachIndexed { index, preset ->
            var showMenu by rememberSaveable(preset.name) { mutableStateOf(false) }

            PreferenceItemOverload(
                title = preset.name,
                subtitle = stringResource(R.string.shader_params_count, preset.params.size),
                endIcon = {
                    Box {
                        EnhancedIconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = null
                            )
                        }
                        EnhancedDropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            ShaderMenuAction(R.string.edit, Icons.Rounded.EditAlt) {
                                showMenu = false
                                onEdit(preset)
                            }
                            ShaderMenuAction(R.string.duplicate, Icons.Rounded.ContentCopy) {
                                showMenu = false
                                onDuplicate(preset)
                            }
                            ShaderMenuAction(R.string.export, Icons.Outlined.DownloadFile) {
                                showMenu = false
                                onExport(preset)
                            }
                            ShaderMenuAction(R.string.share, Icons.Rounded.IosShare) {
                                showMenu = false
                                onShare(preset)
                            }
                            ShaderMenuAction(R.string.delete, Icons.Rounded.Delete) {
                                showMenu = false
                                onDelete(preset)
                            }
                        }
                    }
                },
                onClick = { onEdit(preset) },
                shape = ShapeDefaults.byIndex(index, presets.size),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ShaderMenuAction(
    title: Int,
    icon: ImageVector,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(stringResource(title)) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        onClick = onClick
    )
}

@Composable
private fun ShaderStudioButtons(
    actions: @Composable RowScope.() -> Unit,
    onImport: () -> Unit,
    onSave: () -> Unit,
    canSave: Boolean
) {
    val isPortrait by isPortraitOrientationAsState()
    BottomButtonsBlock(
        isNoData = false,
        onSecondaryButtonClick = onImport,
        secondaryButtonIcon = Icons.Rounded.FileOpen,
        secondaryButtonText = stringResource(R.string.import_word),
        onPrimaryButtonClick = onSave,
        primaryButtonIcon = Icons.Rounded.Save,
        isPrimaryButtonVisible = canSave,
        actions = {
            if (isPortrait) actions()
        }
    )
}

private fun ShaderParamType.defaultValue(): ShaderValue = when (this) {
    ShaderParamType.Float -> ShaderValue.FloatValue(0f)
    ShaderParamType.Int -> ShaderValue.IntValue(0)
    ShaderParamType.Bool -> ShaderValue.BoolValue(false)
    ShaderParamType.Color -> ShaderValue.ColorValue(255, 255, 255, 255)
    ShaderParamType.Vec2 -> ShaderValue.Vec2Value(0f, 0f)
}

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
