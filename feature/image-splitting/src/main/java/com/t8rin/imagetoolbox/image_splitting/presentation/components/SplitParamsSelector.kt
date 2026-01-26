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

package com.t8rin.imagetoolbox.image_splitting.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.material.icons.rounded.ViewColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.extendedcolors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.image_splitting.domain.SplitParams
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
internal fun SplitParamsSelector(
    value: SplitParams,
    onValueChange: (SplitParams) -> Unit
) {
    var recomputeTrigger by remember {
        mutableIntStateOf(0)
    }
    var rowsCount by remember(recomputeTrigger) {
        mutableIntStateOf(value.rowsCount)
    }
    var columnsCount by remember(recomputeTrigger) {
        mutableIntStateOf(value.columnsCount)
    }

    Column(
        modifier = Modifier.container(
            shape = ShapeDefaults.top
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TitleItem(
                text = stringResource(R.string.compute_percents),
                icon = Icons.Rounded.Percent,
                modifier = Modifier.padding(
                    start = 12.dp,
                    top = 8.dp,
                    bottom = 4.dp,
                    end = 12.dp
                )
            )
        }

        var aspectRatio by rememberSaveable {
            mutableStateOf("")
        }
        val focus = LocalFocusManager.current

        val computed by remember(aspectRatio) {
            derivedStateOf {
                aspectRatio
                    .replace(',', '.')
                    .filter { it.isDigit() || it == '.' }
                    .takeIf { it.isNotEmpty() }
                    ?.toFloatOrNull() ?: 0f
            }
        }
        val scope = rememberCoroutineScope()

        RoundedTextField(
            value = aspectRatio,
            onValueChange = { text ->
                aspectRatio = text
            },
            hint = {
                Text(1f.toString())
            },
            label = {
                Text(stringResource(R.string.aspect_ratio))
            },
            endIcon = {
                AnimatedVisibility(
                    visible = computed > 0f,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    EnhancedIconButton(
                        onClick = {
                            onValueChange(
                                value.withAspectRatio(computed)
                            )
                            aspectRatio = ""
                            scope.launch {
                                delay(200)
                                recomputeTrigger++
                                focus.clearFocus()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null
                        )
                    }
                }
            },
            modifier = Modifier.padding(12.dp)
        )
    }
    Spacer(Modifier.height(4.dp))
    EnhancedSliderItem(
        value = rowsCount,
        title = stringResource(R.string.rows_count),
        sliderModifier = Modifier
            .padding(
                top = 14.dp,
                start = 12.dp,
                end = 12.dp,
                bottom = 10.dp
            ),
        icon = Icons.Rounded.TableRows,
        valueRange = 1f..20f,
        steps = 18,
        internalStateTransformation = {
            it.roundToInt()
        },
        onValueChangeFinished = {
            onValueChange(
                value.copy(rowsCount = it.roundToInt())
            )
            rowsCount = it.roundToInt()
        },
        onValueChange = {},
        shape = ShapeDefaults.center,
        additionalContent = if (rowsCount > 1) {
            {
                PercentagesField(
                    totalSize = rowsCount,
                    percentageValues = value.rowPercentages,
                    onValueChange = {
                        onValueChange(
                            value.copy(
                                rowPercentages = it
                            )
                        )
                    }
                )
            }
        } else null
    )
    Spacer(Modifier.height(4.dp))
    EnhancedSliderItem(
        value = columnsCount,
        title = stringResource(R.string.columns_count),
        sliderModifier = Modifier
            .padding(
                top = 14.dp,
                start = 12.dp,
                end = 12.dp,
                bottom = 10.dp
            ),
        steps = 18,
        icon = Icons.Rounded.ViewColumn,
        valueRange = 1f..20f,
        internalStateTransformation = {
            it.roundToInt()
        },
        onValueChangeFinished = {
            onValueChange(
                value.copy(columnsCount = it.roundToInt())
            )
            columnsCount = it.roundToInt()
        },
        onValueChange = {},
        shape = ShapeDefaults.bottom,
        additionalContent = if (columnsCount > 1) {
            {
                PercentagesField(
                    totalSize = columnsCount,
                    percentageValues = value.columnPercentages,
                    onValueChange = {
                        onValueChange(
                            value.copy(
                                columnPercentages = it
                            )
                        )
                    }
                )
            }
        } else null
    )
    if (value.imageFormat.canChangeCompressionValue) {
        Spacer(Modifier.height(8.dp))
    }
    QualitySelector(
        imageFormat = value.imageFormat,
        quality = value.quality,
        onQualityChange = {
            onValueChange(
                value.copy(quality = it)
            )
        }
    )
    Spacer(Modifier.height(8.dp))
    ImageFormatSelector(
        value = value.imageFormat,
        onValueChange = {
            onValueChange(
                value.copy(imageFormat = it)
            )
        },
        quality = value.quality,
    )
}

@Composable
private fun PercentagesField(
    totalSize: Int,
    percentageValues: List<Float>,
    onValueChange: (List<Float>) -> Unit
) {
    val default by remember(totalSize) {
        derivedStateOf {
            List(totalSize) { 1f / totalSize }.joinToString("/") {
                it.roundToTwoDigits().toString()
            }
        }
    }
    var percentages by remember {
        mutableStateOf(
            percentageValues.joinToString("/") {
                it.roundToTwoDigits().toString()
            }
        )
    }

    LaunchedEffect(percentageValues, totalSize) {
        if (percentageValues.size > totalSize) {
            percentages = percentageValues.take(totalSize).joinToString("/") {
                it.roundToTwoDigits().toString()
            }
        }
    }

    LaunchedEffect(percentages) {
        onValueChange(
            percentages.split("/").mapNotNull { it.toFloatOrNull() }
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        TitleItem(
            text = stringResource(R.string.part_percents),
            icon = Icons.Rounded.Percent,
            modifier = Modifier.padding(
                start = 12.dp,
                top = 8.dp,
                bottom = 4.dp,
                end = 12.dp
            )
        )
    }

    RoundedTextField(
        value = percentages,
        onValueChange = {
            percentages = it
        },
        hint = {
            Text(text = default)
        },
        modifier = Modifier.padding(12.dp)
    )
}