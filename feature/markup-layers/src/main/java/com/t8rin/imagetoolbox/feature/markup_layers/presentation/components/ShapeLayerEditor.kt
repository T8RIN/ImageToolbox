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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatColorFill
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.colors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.toColor
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.feature.markup_layers.domain.DropShadow
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.domain.ShapeMode
import com.t8rin.imagetoolbox.feature.markup_layers.domain.arrowAngle
import com.t8rin.imagetoolbox.feature.markup_layers.domain.arrowSizeScale
import com.t8rin.imagetoolbox.feature.markup_layers.domain.cornerRadius
import com.t8rin.imagetoolbox.feature.markup_layers.domain.innerRadiusRatio
import com.t8rin.imagetoolbox.feature.markup_layers.domain.isOutlinedShapeMode
import com.t8rin.imagetoolbox.feature.markup_layers.domain.isRegular
import com.t8rin.imagetoolbox.feature.markup_layers.domain.ordinal
import com.t8rin.imagetoolbox.feature.markup_layers.domain.outlinedFillColorInt
import com.t8rin.imagetoolbox.feature.markup_layers.domain.rotationDegrees
import com.t8rin.imagetoolbox.feature.markup_layers.domain.updateArrow
import com.t8rin.imagetoolbox.feature.markup_layers.domain.updatePolygon
import com.t8rin.imagetoolbox.feature.markup_layers.domain.updateRect
import com.t8rin.imagetoolbox.feature.markup_layers.domain.updateStar
import com.t8rin.imagetoolbox.feature.markup_layers.domain.usesStrokeWidth
import com.t8rin.imagetoolbox.feature.markup_layers.domain.vertices
import com.t8rin.imagetoolbox.feature.markup_layers.domain.withOutlinedFillColor
import com.t8rin.imagetoolbox.feature.markup_layers.domain.withPreferredGeometryFor
import com.t8rin.imagetoolbox.feature.markup_layers.domain.withSavedStateFrom
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.icon
import kotlin.math.roundToInt

@Composable
internal fun ShapeLayerEditorSection(
    layer: UiMarkupLayer,
    type: LayerType.Shape,
    onUpdateLayer: (UiMarkupLayer) -> Unit,
    onUpdateLayerContinuously: (UiMarkupLayer) -> Unit,
    onContinuousEditFinished: () -> Unit
) {
    Column(
        modifier = Modifier.container(
            shape = ShapeDefaults.large,
            color = MaterialTheme.colorScheme.surface,
        )
    ) {
        EnhancedButtonGroup(
            enabled = true,
            itemCount = ShapeMode.entries.size,
            title = {
                Text(
                    text = stringResource(R.string.shape),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            },
            selectedIndex = type.shapeMode.ordinal,
            activeButtonColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer,
            itemContent = {
                Icon(
                    imageVector = ShapeMode.entries[it].kind.icon,
                    contentDescription = null
                )
            },
            onIndexChange = {
                val mode = ShapeMode.entries[it]
                    .withSavedStateFrom(type.shapeMode)
                    .let { candidate ->
                        if (candidate.isOutlinedShapeMode() && candidate.outlinedFillColorInt() == null) {
                            candidate.withOutlinedFillColor(
                                color = type.shapeMode.outlinedFillColorInt()
                            )
                        } else {
                            candidate
                        }
                    }

                onUpdateLayer(
                    layer.copy(
                        cornerRadiusPercent = 0,
                        type = type.withPreferredGeometryFor(mode)
                    )
                )
            }
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
    ShapeAppearanceSection(
        layer = layer,
        type = type,
        onUpdateLayer = onUpdateLayer,
        onUpdateLayerContinuously = onUpdateLayerContinuously,
        onContinuousEditFinished = onContinuousEditFinished
    )

    Spacer(modifier = Modifier.height(8.dp))
    ShapeSizeSection(
        layer = layer,
        type = type,
        onUpdateLayerContinuously = onUpdateLayerContinuously,
        onContinuousEditFinished = onContinuousEditFinished
    )

    AnimatedContent(
        targetState = type,
        contentKey = { it.shapeMode.kind },
        modifier = Modifier.fillMaxWidth()
    ) { animatedType ->
        Column {
            ShapeSpecificControls(
                layer = layer,
                type = animatedType,
                onUpdateLayer = onUpdateLayer,
                onUpdateLayerContinuously = onUpdateLayerContinuously,
                onContinuousEditFinished = onContinuousEditFinished
            )
        }
    }
}

@Composable
private fun ShapeAppearanceSection(
    layer: UiMarkupLayer,
    type: LayerType.Shape,
    onUpdateLayer: (UiMarkupLayer) -> Unit,
    onUpdateLayerContinuously: (UiMarkupLayer) -> Unit,
    onContinuousEditFinished: () -> Unit
) {
    val mode = type.shapeMode
    val showFillColor = mode.isOutlinedShapeMode()
    val showStrokeWidth = mode.usesStrokeWidth()
    val singleItemShape = ShapeDefaults.large

    ColorRowSelector(
        value = type.color.toColor(),
        onValueChange = {
            onUpdateLayer(
                layer.copy(
                    type = type.copy(color = it.toArgb())
                )
            )
        },
        title = stringResource(R.string.color),
        modifier = Modifier.container(
            shape = when {
                showFillColor || showStrokeWidth -> ShapeDefaults.top
                else -> singleItemShape
            },
            color = MaterialTheme.colorScheme.surface
        )
    )

    AnimatedVisibility(
        visible = showFillColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Spacer(modifier = Modifier.height(4.dp))
            ColorRowSelector(
                value = mode.outlinedFillColorInt()?.toColor(),
                onValueChange = {
                    onUpdateLayer(
                        layer.copy(
                            type = type.copy(
                                shapeMode = mode.withOutlinedFillColor(it.toArgb())
                            )
                        )
                    )
                },
                onNullClick = {
                    onUpdateLayer(
                        layer.copy(
                            type = type.copy(
                                shapeMode = mode.withOutlinedFillColor(null)
                            )
                        )
                    )
                },
                title = stringResource(R.string.fill_color),
                icon = Icons.Outlined.FormatColorFill,
                allowAlpha = true,
                modifier = Modifier.container(
                    shape = if (showStrokeWidth) ShapeDefaults.center else ShapeDefaults.bottom,
                    color = MaterialTheme.colorScheme.surface
                )
            )
        }
    }

    AnimatedVisibility(
        visible = showStrokeWidth,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Spacer(modifier = Modifier.height(4.dp))
            EnhancedSliderItem(
                value = type.strokeWidth,
                title = stringResource(R.string.line_width),
                internalStateTransformation = { it.roundToTwoDigits() },
                onValueChange = {
                    onUpdateLayerContinuously(
                        layer.copy(
                            type = type.copy(strokeWidth = it)
                        )
                    )
                },
                onValueChangeFinished = { _ -> onContinuousEditFinished() },
                valueRange = 1f..48f,
                shape = if (showFillColor) ShapeDefaults.bottom else ShapeDefaults.bottom,
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Composable
private fun ShapeSizeSection(
    layer: UiMarkupLayer,
    type: LayerType.Shape,
    onUpdateLayerContinuously: (UiMarkupLayer) -> Unit,
    onContinuousEditFinished: () -> Unit
) {
    EnhancedSliderItem(
        value = type.widthRatio,
        title = stringResource(R.string.width, "").trim(),
        internalStateTransformation = { it.roundToTwoDigits() },
        onValueChange = {
            onUpdateLayerContinuously(
                layer.copy(
                    type = type.copy(widthRatio = it.roundToTwoDigits())
                )
            )
        },
        onValueChangeFinished = { _ -> onContinuousEditFinished() },
        valueRange = 0.05f..0.5f,
        shape = ShapeDefaults.top,
        containerColor = MaterialTheme.colorScheme.surface
    )
    Spacer(modifier = Modifier.height(4.dp))
    EnhancedSliderItem(
        value = type.heightRatio,
        title = stringResource(R.string.height, "").trim(),
        internalStateTransformation = { it.roundToTwoDigits() },
        onValueChange = {
            onUpdateLayerContinuously(
                layer.copy(
                    type = type.copy(heightRatio = it.roundToTwoDigits())
                )
            )
        },
        onValueChangeFinished = { _ -> onContinuousEditFinished() },
        valueRange = 0.05f..0.5f,
        shape = ShapeDefaults.bottom,
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun ShapeSpecificControls(
    layer: UiMarkupLayer,
    type: LayerType.Shape,
    onUpdateLayer: (UiMarkupLayer) -> Unit,
    onUpdateLayerContinuously: (UiMarkupLayer) -> Unit,
    onContinuousEditFinished: () -> Unit
) {
    when (val mode = type.shapeMode) {
        is ShapeMode.Arrow,
        is ShapeMode.DoubleArrow,
        is ShapeMode.LineArrow,
        is ShapeMode.DoubleLineArrow -> {
            Spacer(modifier = Modifier.height(8.dp))
            EnhancedSliderItem(
                value = mode.arrowSizeScale(),
                title = stringResource(R.string.head_length_scale),
                internalStateTransformation = { it.roundToTwoDigits() },
                onValueChange = {
                    onUpdateLayerContinuously(
                        layer.copy(
                            type = type.copy(
                                shapeMode = mode.updateArrow(sizeScale = it)
                            )
                        )
                    )
                },
                onValueChangeFinished = { _ -> onContinuousEditFinished() },
                valueRange = 0.5f..8f,
                shape = ShapeDefaults.top,
                containerColor = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.height(4.dp))
            EnhancedSliderItem(
                value = (mode.arrowAngle() - 90f).coerceAtLeast(0f),
                title = stringResource(R.string.angle),
                internalStateTransformation = { it.roundToTwoDigits() },
                onValueChange = {
                    onUpdateLayerContinuously(
                        layer.copy(
                            type = type.copy(
                                shapeMode = mode.updateArrow(angle = it + 90f)
                            )
                        )
                    )
                },
                onValueChangeFinished = { _ -> onContinuousEditFinished() },
                valueRange = 0f..90f,
                shape = ShapeDefaults.bottom,
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        is ShapeMode.Rect,
        is ShapeMode.OutlinedRect -> {
            Spacer(modifier = Modifier.height(8.dp))
            EnhancedSliderItem(
                value = mode.rotationDegrees().toFloat(),
                title = stringResource(R.string.angle),
                internalStateTransformation = { it.roundToInt() },
                onValueChange = {
                    onUpdateLayerContinuously(
                        layer.copy(
                            type = type.copy(
                                shapeMode = mode.updateRect(rotationDegrees = it.roundToInt())
                            )
                        )
                    )
                },
                onValueChangeFinished = { _ -> onContinuousEditFinished() },
                valueRange = 0f..360f,
                shape = ShapeDefaults.top,
                containerColor = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.height(4.dp))
            EnhancedSliderItem(
                value = mode.cornerRadius(),
                title = stringResource(R.string.radius),
                internalStateTransformation = { it.roundToTwoDigits() },
                onValueChange = {
                    onUpdateLayerContinuously(
                        layer.copy(
                            type = type.copy(
                                shapeMode = mode.updateRect(cornerRadius = it.roundToTwoDigits())
                            )
                        )
                    )
                },
                onValueChangeFinished = { _ -> onContinuousEditFinished() },
                valueRange = 0f..0.5f,
                shape = ShapeDefaults.bottom,
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        is ShapeMode.Polygon,
        is ShapeMode.OutlinedPolygon -> {
            Spacer(modifier = Modifier.height(8.dp))
            EnhancedSliderItem(
                value = mode.vertices().toFloat(),
                title = stringResource(R.string.vertices),
                internalStateTransformation = { it.roundToInt() },
                onValueChange = {
                    onUpdateLayerContinuously(
                        layer.copy(
                            type = type.copy(
                                shapeMode = mode.updatePolygon(vertices = it.roundToInt())
                            )
                        )
                    )
                },
                onValueChangeFinished = { _ -> onContinuousEditFinished() },
                valueRange = 3f..24f,
                steps = 20,
                shape = ShapeDefaults.top,
                containerColor = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.height(4.dp))
            EnhancedSliderItem(
                value = mode.rotationDegrees().toFloat(),
                title = stringResource(R.string.angle),
                internalStateTransformation = { it.roundToInt() },
                onValueChange = {
                    onUpdateLayerContinuously(
                        layer.copy(
                            type = type.copy(
                                shapeMode = mode.updatePolygon(rotationDegrees = it.roundToInt())
                            )
                        )
                    )
                },
                onValueChangeFinished = { _ -> onContinuousEditFinished() },
                valueRange = 0f..360f,
                shape = ShapeDefaults.center,
                containerColor = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.height(4.dp))
            PreferenceRowSwitch(
                title = stringResource(R.string.draw_regular_polygon),
                subtitle = stringResource(R.string.draw_regular_polygon_sub),
                checked = mode.isRegular(),
                onClick = {
                    onUpdateLayer(
                        layer.copy(
                            type = type.copy(
                                shapeMode = mode.updatePolygon(isRegular = it)
                            )
                        )
                    )
                },
                shape = ShapeDefaults.bottom,
                modifier = Modifier.fillMaxWidth(),
                applyHorizontalPadding = false,
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        is ShapeMode.Star,
        is ShapeMode.OutlinedStar -> {
            Spacer(modifier = Modifier.height(8.dp))
            EnhancedSliderItem(
                value = mode.vertices().toFloat(),
                title = stringResource(R.string.vertices),
                internalStateTransformation = { it.roundToInt() },
                onValueChange = {
                    onUpdateLayerContinuously(
                        layer.copy(
                            type = type.copy(
                                shapeMode = mode.updateStar(vertices = it.roundToInt())
                            )
                        )
                    )
                },
                onValueChangeFinished = { _ -> onContinuousEditFinished() },
                valueRange = 3f..24f,
                steps = 20,
                shape = ShapeDefaults.top,
                containerColor = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.height(4.dp))
            EnhancedSliderItem(
                value = mode.rotationDegrees().toFloat(),
                title = stringResource(R.string.angle),
                internalStateTransformation = { it.roundToInt() },
                onValueChange = {
                    onUpdateLayerContinuously(
                        layer.copy(
                            type = type.copy(
                                shapeMode = mode.updateStar(rotationDegrees = it.roundToInt())
                            )
                        )
                    )
                },
                onValueChangeFinished = { _ -> onContinuousEditFinished() },
                valueRange = 0f..360f,
                shape = ShapeDefaults.center,
                containerColor = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.height(4.dp))
            EnhancedSliderItem(
                value = mode.innerRadiusRatio(),
                title = stringResource(R.string.inner_radius_ratio),
                internalStateTransformation = { it.roundToTwoDigits() },
                onValueChange = {
                    onUpdateLayerContinuously(
                        layer.copy(
                            type = type.copy(
                                shapeMode = mode.updateStar(innerRadiusRatio = it.roundToTwoDigits())
                            )
                        )
                    )
                },
                onValueChangeFinished = { _ -> onContinuousEditFinished() },
                valueRange = 0f..1f,
                shape = ShapeDefaults.center,
                containerColor = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.height(4.dp))
            PreferenceRowSwitch(
                title = stringResource(R.string.draw_regular_star),
                subtitle = stringResource(R.string.draw_regular_star_sub),
                checked = mode.isRegular(),
                onClick = {
                    onUpdateLayer(
                        layer.copy(
                            type = type.copy(
                                shapeMode = mode.updateStar(isRegular = it)
                            )
                        )
                    )
                },
                shape = ShapeDefaults.bottom,
                modifier = Modifier.fillMaxWidth(),
                applyHorizontalPadding = false,
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        else -> Unit
    }
}

@Composable
internal fun ShapeShadowSection(
    layer: UiMarkupLayer,
    type: LayerType.Shape,
    onUpdateLayer: (UiMarkupLayer) -> Unit,
    onUpdateLayerContinuously: (UiMarkupLayer) -> Unit,
    onContinuousEditFinished: () -> Unit
) {
    var haveShadow by rememberSaveable {
        mutableStateOf(type.shadow != null)
    }

    LaunchedEffect(haveShadow, type.shadow) {
        val desiredShadow = if (haveShadow) {
            type.shadow ?: DropShadow(
                color = Color.Black.copy(alpha = 0.75f).toArgb(),
                offsetX = 0f,
                offsetY = 6f,
                blurRadius = 12f
            )
        } else null

        if (type.shadow != desiredShadow) {
            onUpdateLayer(
                layer.copy(
                    type = type.copy(shadow = desiredShadow)
                )
            )
        }
    }

    PreferenceRowSwitch(
        title = stringResource(R.string.add_shadow),
        subtitle = stringResource(R.string.add_shadow_sub),
        shape = ShapeDefaults.large,
        containerColor = MaterialTheme.colorScheme.surface,
        applyHorizontalPadding = false,
        resultModifier = Modifier.padding(16.dp),
        checked = haveShadow,
        onClick = { haveShadow = it },
        additionalContent = {
            AnimatedContent(
                targetState = type.shadow,
                contentKey = { it != null },
                modifier = Modifier.fillMaxWidth()
            ) { shadow ->
                shadow?.let {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        ColorRowSelector(
                            value = shadow.color.toColor(),
                            onValueChange = {
                                onUpdateLayer(
                                    layer.copy(
                                        type = type.copy(
                                            shadow = shadow.copy(
                                                color = it.toArgb()
                                            )
                                        )
                                    )
                                )
                            },
                            title = stringResource(R.string.shadow_color),
                            modifier = Modifier.container(
                                shape = ShapeDefaults.top,
                                color = MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        )
                        EnhancedSliderItem(
                            value = shadow.blurRadius,
                            title = stringResource(R.string.blur_radius),
                            internalStateTransformation = { it.roundToTwoDigits() },
                            onValueChange = {
                                onUpdateLayerContinuously(
                                    layer.copy(
                                        type = type.copy(
                                            shadow = shadow.copy(
                                                blurRadius = it
                                            )
                                        )
                                    )
                                )
                            },
                            onValueChangeFinished = { _ -> onContinuousEditFinished() },
                            valueRange = 0f..48f,
                            shape = ShapeDefaults.center,
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                        EnhancedSliderItem(
                            value = shadow.offsetX,
                            title = stringResource(R.string.offset_x),
                            internalStateTransformation = { it.roundToTwoDigits() },
                            onValueChange = {
                                onUpdateLayerContinuously(
                                    layer.copy(
                                        type = type.copy(
                                            shadow = shadow.copy(
                                                offsetX = it
                                            )
                                        )
                                    )
                                )
                            },
                            onValueChangeFinished = { _ -> onContinuousEditFinished() },
                            valueRange = -48f..48f,
                            shape = ShapeDefaults.center,
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                        EnhancedSliderItem(
                            value = shadow.offsetY,
                            title = stringResource(R.string.offset_y),
                            internalStateTransformation = { it.roundToTwoDigits() },
                            onValueChange = {
                                onUpdateLayerContinuously(
                                    layer.copy(
                                        type = type.copy(
                                            shadow = shadow.copy(
                                                offsetY = it
                                            )
                                        )
                                    )
                                )
                            },
                            onValueChangeFinished = { _ -> onContinuousEditFinished() },
                            valueRange = -48f..48f,
                            shape = ShapeDefaults.bottom,
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    }
                }
            }
        }
    )
}
