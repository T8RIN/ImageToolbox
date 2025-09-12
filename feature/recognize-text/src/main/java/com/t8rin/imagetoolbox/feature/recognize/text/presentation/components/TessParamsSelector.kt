/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.recognize.text.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.ExpandableItem
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.recognize.text.domain.TessParams
import kotlin.math.roundToInt

@Composable
fun TessParamsSelector(
    value: TessParams,
    onValueChange: (TessParams) -> Unit,
    modifier: Modifier = Modifier
) {
    ExpandableItem(
        modifier = modifier,
        visibleContent = {
            TitleItem(
                text = stringResource(R.string.tesseract_options),
                subtitle = stringResource(R.string.tesseract_options_sub),
                icon = Icons.Outlined.Tune,
                iconEndPadding = 16.dp,
                modifier = Modifier.padding(8.dp)
            )
        },
        expandableContent = {
            val params by rememberUpdatedState(value)

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                val size = params.tessParamList.size

                params.tessParamList.forEachIndexed { index, (key, paramValue) ->
                    if (paramValue is Int) {
                        EnhancedSliderItem(
                            value = paramValue,
                            onValueChange = { newValue ->
                                onValueChange(
                                    params.update(key) {
                                        newValue.roundToInt()
                                    }
                                )
                            },
                            title = key,
                            valueRange = 0f..100f,
                            steps = 98,
                            internalStateTransformation = {
                                it.roundToInt()
                            },
                            shape = ShapeDefaults.byIndex(index, size),
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    } else if (paramValue is Boolean) {
                        PreferenceRowSwitch(
                            title = key,
                            checked = paramValue,
                            onClick = { checked ->
                                onValueChange(
                                    params.update(key) { checked }
                                )
                            },
                            shape = ShapeDefaults.byIndex(index, size),
                            modifier = Modifier.fillMaxWidth(),
                            applyHorizontalPadding = false,
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    }
                }

                var tempTessParams by rememberSaveable(params.tessCustomParams) {
                    mutableStateOf(params.tessCustomParams)
                }
                Column(
                    modifier = Modifier
                        .container(
                            shape = ShapeDefaults.default,
                            color = MaterialTheme.colorScheme.surface
                        )
                        .padding(8.dp)
                ) {
                    RoundedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = tempTessParams,
                        singleLine = false,
                        onValueChange = {
                            tempTessParams = it
                        },
                        label = {
                            Text(stringResource(R.string.custom_options))
                        },
                        onLoseFocusTransformation = {
                            tempTessParams.trim().also {
                                onValueChange(
                                    params.update(newCustomParams = it)
                                )
                                tempTessParams = it
                            }
                        },
                        endIcon = {
                            AnimatedVisibility(tempTessParams.isNotEmpty()) {
                                EnhancedIconButton(
                                    onClick = {
                                        onValueChange(
                                            params.update(newCustomParams = tempTessParams)
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Done,
                                        contentDescription = "Done"
                                    )
                                }
                            }
                        },
                        shape = ShapeDefaults.small
                    )
                    Spacer(Modifier.height(8.dp))
                    InfoContainer(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        text = stringResource(R.string.custom_params_info)
                    )
                }
            }
        },
        shape = ShapeDefaults.extraLarge
    )
}