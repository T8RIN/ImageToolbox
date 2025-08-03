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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.trimTrailingZero
import com.t8rin.imagetoolbox.core.filters.domain.model.params.CropOrPerspectiveParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.FloatPair
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
internal fun CropOrPerspectiveParamsItem(
    value: CropOrPerspectiveParams,
    filter: UiFilter<CropOrPerspectiveParams>,
    onFilterChange: (value: CropOrPerspectiveParams) -> Unit,
    previewOnly: Boolean
) {
    val topLeft: MutableState<FloatPair> =
        remember(value) { mutableStateOf(value.topLeft) }
    val topRight: MutableState<FloatPair> =
        remember(value) { mutableStateOf(value.topRight) }
    val bottomLeft: MutableState<FloatPair> =
        remember(value) { mutableStateOf(value.bottomLeft) }
    val bottomRight: MutableState<FloatPair> =
        remember(value) { mutableStateOf(value.bottomRight) }
    val isAbsolute: MutableState<Boolean> = remember(value) { mutableStateOf(value.isAbsolute) }

    LaunchedEffect(
        topLeft.value,
        topRight.value,
        bottomLeft.value,
        bottomRight.value,
        isAbsolute.value,
    ) {
        onFilterChange(
            CropOrPerspectiveParams(
                topLeft = topLeft.value,
                topRight = topRight.value,
                bottomLeft = bottomLeft.value,
                bottomRight = bottomRight.value,
                isAbsolute = isAbsolute.value
            )
        )
    }

    fun stateByIndex(index: Int) = when (index) {
        0 -> topLeft
        1 -> topRight
        2 -> bottomLeft
        else -> bottomRight
    }

    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filter.paramsInfo.forEachIndexed { index, (title) ->
            when (index) {
                0, 1, 2, 3 -> {
                    val state = stateByIndex(index)
                    var x by remember {
                        mutableStateOf(
                            state.value.first.toString().trimTrailingZero()
                        )
                    }
                    var y by remember {
                        mutableStateOf(
                            state.value.second.toString().trimTrailingZero()
                        )
                    }

                    LaunchedEffect(x, y) {
                        state.update {
                            it.copy(
                                first = x.toFloatOrNull() ?: it.first,
                                second = y.toFloatOrNull() ?: it.second,
                            )
                        }
                    }

                    Column {
                        TitleItem(
                            text = stringResource(title!!),
                            modifier = Modifier.padding(
                                horizontal = 8.dp
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        Row {
                            RoundedTextField(
                                value = x,
                                onValueChange = { x = it },
                                shape = ShapeDefaults.smallStart,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                label = {
                                    Text("X")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(
                                        start = 8.dp,
                                        top = 8.dp,
                                        bottom = 8.dp,
                                        end = 2.dp
                                    )
                            )
                            RoundedTextField(
                                value = y,
                                onValueChange = { y = it },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                shape = ShapeDefaults.smallEnd,
                                label = {
                                    Text("Y")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(
                                        start = 2.dp,
                                        top = 8.dp,
                                        bottom = 8.dp,
                                        end = 8.dp
                                    ),
                            )
                        }
                    }
                }

                4 -> {
                    PreferenceRowSwitch(
                        title = stringResource(id = title!!),
                        checked = isAbsolute.value,
                        onClick = {
                            isAbsolute.value = it
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