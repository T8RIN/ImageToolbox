/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.ascii.Gradient
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AsciiParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiAsciiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.resources.icons.Delete
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
    value: AsciiParams,
    filter: UiFilter<AsciiParams>,
    onFilterChange: (value: AsciiParams) -> Unit,
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
        onFilterChange(
            AsciiParams(
                gradient = gradientState.value,
                fontSize = fontSize.value,
                backgroundColor = backgroundColor.value,
                isGrayscale = isGrayscale,
                font = font.type
            )
        )
    }

    Column(
        modifier = Modifier.padding(if (isForText) 0.dp else 8.dp),
        verticalArrangement = Arrangement.spacedBy(
            if (isForText) 4.dp else 8.dp
        )
    ) {
        filter.paramsInfo.take(
            if (isForText) 2 else 5
        ).forEachIndexed { index, (title, valueRange, roundTo) ->
            when (index) {
                0 -> {
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
                            listOf(
                                Gradient.NORMAL,
                                Gradient.NORMAL2,
                                Gradient.ARROWS,
                                Gradient.OLD,
                                Gradient.EXTENDED_HIGH,
                                Gradient.MINIMAL,
                                Gradient.MATH,
                                Gradient.NUMERICAL
                            ).map { it.value }
                        }
                        val customEntries = settings.customAsciiGradients - defaultItems

                        val items = defaultItems + customEntries

                        RoundedTextField(
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
                            inactiveButtonColor = Color.Unspecified
                        )
                    }
                }

                1 -> {
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

                2 -> {
                    FontSelector(
                        value = font,
                        onValueChange = { font = it },
                        behaveAsContainer = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                3 -> {
                    ColorRowSelector(
                        title = stringResource(title!!),
                        value = backgroundColor.value.toColor(),
                        onValueChange = {
                            backgroundColor.value = it.toModel()
                        },
                        allowScroll = !previewOnly,
                        icon = null,
                        defaultColors = ColorSelectionRowDefaults.colorList,
                        contentHorizontalPadding = 16.dp,
                    )
                }

                4 -> {
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
                        resultModifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                        enabled = !previewOnly
                    )
                }
            }
        }
    }
}

@Composable
fun AsciiParamsSelector(
    value: AsciiParams,
    onValueChange: (value: AsciiParams) -> Unit,
    itemShapes: @Composable (Int) -> Shape = {
        ShapeDefaults.byIndex(
            index = it,
            size = 4
        )
    }
) {
    val filter by remember(value) {
        derivedStateOf {
            UiAsciiFilter(value)
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