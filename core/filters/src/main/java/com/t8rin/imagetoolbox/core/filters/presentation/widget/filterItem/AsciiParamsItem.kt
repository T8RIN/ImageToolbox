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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.ascii.Gradient
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Ascii.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.copy
import com.t8rin.imagetoolbox.core.filters.presentation.model.createUiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.parameterInfo
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Save
import com.t8rin.imagetoolbox.core.settings.presentation.model.asUi
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.utils.helper.toModel
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelectionRowDefaults
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.FontSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import kotlinx.coroutines.launch

@Composable
internal fun AsciiParamsItem(
    value: Value,
    filter: Filter<Value>,
    onFilterChange: (value: Value) -> Unit,
    previewOnly: Boolean,
    itemShape: @Composable (Int) -> Shape = {
        ShapeDefaults.byIndex(
            index = it,
            size = 4
        )
    },
    isForText: Boolean = false
) {
    val gradientState: MutableState<String> =
        remember(value) { mutableStateOf(value.gradient) }
    val fontSize: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.fontSize) }
    val backgroundColor: MutableState<ColorModel> =
        remember(value) { mutableStateOf(value.backgroundColor) }
    var isGrayscale by remember(value) { mutableStateOf(value.isGrayscale) }

    val settings = LocalSettingsState.current
    val currentFont = settings.font

    var font by remember(value) {
        mutableStateOf(
            value.font?.asUi() ?: currentFont
        )
    }

    LaunchedEffect(
        gradientState.value,
        fontSize.value,
        backgroundColor.value,
        isGrayscale,
        font
    ) {
        val hasChanges = value.gradient != gradientState.value ||
                value.fontSize != fontSize.value ||
                value.backgroundColor != backgroundColor.value ||
                value.isGrayscale != isGrayscale ||
                value.font != font.type

        if (!previewOnly && hasChanges) {
            onFilterChange(
                value.copy(
                    gradient = gradientState.value,
                    fontSize = fontSize.value,
                    backgroundColor = backgroundColor.value,
                    isGrayscale = isGrayscale,
                    font = font.type
                )
            )
        }
    }

    Column(
        modifier = Modifier.padding(if (isForText) 0.dp else 8.dp),
        verticalArrangement = Arrangement.spacedBy(
            if (isForText) 4.dp else 8.dp
        )
    ) {
        val properties = listOf(
            Value::gradient,
            Value::fontSize,
            Value::font,
            Value::backgroundColor,
            Value::isGrayscale
        )
        properties.take(if (isForText) 2 else properties.size).forEachIndexed { index, property ->
            val (title, valueRange, roundTo) = filter.parameterInfo(property)
            when (property) {
                Value::gradient -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = if (isForText) {
                            Modifier.container(
                                color = MaterialTheme.colorScheme.surface,
                                shape = itemShape(index)
                            )
                        } else Modifier
                    ) {
                        val scope = rememberCoroutineScope()
                        val interactor = LocalSimpleSettingsInteractor.current
                        val defaultItems = remember {
                            Gradient.entries.map { it.value }
                        }

                        val customEntries = remember(settings.customAsciiGradients, defaultItems) {
                            settings.customAsciiGradients - defaultItems.toSet()
                        }

                        val items = remember(defaultItems, customEntries) {
                            defaultItems + customEntries
                        }

                        RoundedTextField(
                            enabled = !previewOnly,
                            value = gradientState.value,
                            onValueChange = { value ->
                                gradientState.value =
                                    value.toList().distinct().filter { !it.isWhitespace() }
                                        .joinToString("")
                            },
                            endIcon = {
                                AnimatedContent(
                                    targetState = Triple(
                                        gradientState.value,
                                        defaultItems,
                                        customEntries
                                    ),
                                    transitionSpec = {
                                        slideInHorizontally { it / 2 } + fadeIn() togetherWith slideOutHorizontally { it / 2 } + fadeOut()
                                    },
                                    contentKey = { (g, d, c) -> (g in d) to (g in c) },
                                ) { (gradient, default, custom) ->
                                    if (gradient.length > 1 && gradient !in default) {
                                        val saved = gradient in custom

                                        EnhancedIconButton(
                                            enabled = !previewOnly,
                                            onClick = {
                                                scope.launch {
                                                    interactor.toggleCustomAsciiGradient(gradient)
                                                }
                                            },
                                            modifier = Modifier.padding(end = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (saved) Icons.Outlined.Delete else Icons.Outlined.Save,
                                                contentDescription = if (saved) "delete" else "add"
                                            )
                                        }
                                    } else {
                                        Spacer(Modifier.height(40.dp))
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(if (isForText) Modifier.padding(top = 4.dp) else Modifier)
                                .padding(
                                    horizontal = 4.dp
                                ),
                            label = stringResource(title!!)
                        )

                        EnhancedButtonGroup(
                            title = null,
                            enabled = !previewOnly,
                            items = items,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 4.dp
                                ),
                            selectedIndex = items.indexOf(gradientState.value),
                            onIndexChange = {
                                gradientState.value = items[it]
                            },
                            inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    }
                }

                Value::fontSize -> {
                    EnhancedSliderItem(
                        enabled = !previewOnly,
                        value = fontSize.value,
                        title = stringResource(title!!),
                        valueRange = valueRange,
                        steps = if (valueRange == 0f..3f) 2 else 0,
                        onValueChange = {
                            fontSize.value = it
                        },
                        internalStateTransformation = {
                            it.roundTo(roundTo)
                        },
                        behaveAsContainer = isForText,
                        containerColor = MaterialTheme.colorScheme.surface,
                        shape = itemShape(index)
                    )
                }

                Value::font -> {
                    FontSelector(
                        value = font,
                        onValueChange = { if (!previewOnly) font = it },
                        behaveAsContainer = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Value::backgroundColor -> {
                    ColorRowSelector(
                        title = stringResource(title!!),
                        value = backgroundColor.value.toColor(),
                        onValueChange = {
                            if (!previewOnly) backgroundColor.value = it.toModel()
                        },
                        allowCustom = !previewOnly,
                        allowScroll = !previewOnly,
                        icon = null,
                        defaultColors = ColorSelectionRowDefaults.colorList,
                        contentHorizontalPadding = 16.dp,
                    )
                }

                Value::isGrayscale -> {
                    PreferenceRowSwitch(
                        title = stringResource(id = title!!),
                        checked = isGrayscale,
                        onClick = {
                            isGrayscale = it
                        },
                        modifier = Modifier.padding(
                            top = 8.dp,
                            start = 4.dp,
                            end = 4.dp
                        ),
                        applyHorizontalPadding = false,
                        startContent = {},
                        resultModifier = Modifier.padding(16.dp),
                        enabled = !previewOnly
                    )
                }
            }
        }
    }
}

@Composable
fun AsciiParamsSelector(
    value: Value,
    onValueChange: (value: Value) -> Unit,
    itemShapes: @Composable (Int) -> Shape = {
        ShapeDefaults.byIndex(
            index = it,
            size = 4
        )
    }
) {
    val filter by remember(value) {
        derivedStateOf {
            createUiFilter(Filter.Ascii::class, value)
        }
    }

    AsciiParamsItem(
        value = value,
        filter = filter,
        onFilterChange = onValueChange,
        previewOnly = false,
        isForText = true,
        itemShape = itemShapes
    )
}