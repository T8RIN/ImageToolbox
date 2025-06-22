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

package com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.InvertColors
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.extendedcolors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.FontSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.BarcodeType
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.core.ui.widget.other.LinkPreviewList
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.screenLogic.ScanQrCodeComponent
import kotlin.math.roundToInt

@Composable
internal fun ScanQrCodeControls(component: ScanQrCodeComponent) {
    val params by rememberUpdatedState(component.params)

    LinkPreviewList(
        text = params.content,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
    RoundedTextField(
        modifier = Modifier
            .container(
                shape = MaterialTheme.shapes.large,
                resultPadding = 0.dp
            )
            .padding(
                top = 8.dp,
                start = 8.dp,
                end = 8.dp,
                bottom = 6.dp
            ),
        value = params.content,
        onValueChange = {
            component.updateParams(
                params.copy(
                    content = it
                )
            )
        },
        maxSymbols = 2500,
        singleLine = false,
        label = {
            Text(stringResource(id = R.string.code_content))
        },
        keyboardOptions = KeyboardOptions(),
        endIcon = {
            AnimatedVisibility(params.content.isNotBlank()) {
                EnhancedIconButton(
                    onClick = {
                        component.updateParams(
                            params.copy(
                                content = ""
                            )
                        )
                    },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Cancel,
                        contentDescription = stringResource(R.string.cancel)
                    )
                }
            }
        }
    )
    Spacer(modifier = Modifier.height(8.dp))
    InfoContainer(
        text = stringResource(R.string.scan_qr_code_to_replace_content),
        modifier = Modifier.padding(8.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))

    AnimatedVisibility(visible = params.content.isNotEmpty()) {
        Column {
            DataSelector(
                value = params.type,
                onValueChange = {
                    component.updateParams(
                        params.copy(
                            type = it,
                            heightRatio = if (it == BarcodeType.DATA_MATRIX) 1f
                            else params.heightRatio
                        )
                    )
                },
                spanCount = 2,
                entries = BarcodeType.entries,
                title = stringResource(R.string.barcode_type),
                titleIcon = Icons.Rounded.QrCode2,
                itemContentText = {
                    remember {
                        it.name.replace("_", " ")
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            BoxAnimatedVisibility(
                visible = !params.type.isSquare || params.type == BarcodeType.DATA_MATRIX,
                modifier = Modifier.fillMaxWidth()
            ) {
                EnhancedSliderItem(
                    value = params.heightRatio,
                    title = stringResource(R.string.height_ratio),
                    valueRange = 1f..4f,
                    onValueChange = {},
                    onValueChangeFinished = {
                        component.updateParams(
                            params.copy(
                                heightRatio = it
                            )
                        )
                    },
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            PreferenceRowSwitch(
                modifier = Modifier.fillMaxWidth(),
                shape = ShapeDefaults.large,
                startIcon = Icons.Outlined.InvertColors,
                title = stringResource(R.string.enforce_bw),
                subtitle = stringResource(R.string.enforce_bw_sub),
                checked = params.enforceBlackAndWhite,
                onClick = {
                    component.updateParams(
                        params.copy(
                            enforceBlackAndWhite = it
                        )
                    )
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max)
            ) {
                ImageSelector(
                    value = params.imageUri,
                    subtitle = stringResource(id = R.string.watermarking_image_sub),
                    onValueChange = {
                        component.updateParams(
                            params.copy(
                                imageUri = it
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    shape = ShapeDefaults.extraLarge
                )
                BoxAnimatedVisibility(visible = params.imageUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(start = 8.dp)
                            .clip(ShapeDefaults.default)
                            .hapticsClickable {
                                component.updateParams(
                                    params.copy(
                                        imageUri = null
                                    )
                                )
                            }
                            .container(
                                color = MaterialTheme.colorScheme.errorContainer,
                                resultPadding = 0.dp
                            )
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            RoundedTextField(
                modifier = Modifier.container(
                    shape = animateShape(
                        if (params.description.isNotEmpty()) ShapeDefaults.top
                        else ShapeDefaults.default
                    ),
                    resultPadding = 8.dp
                ),
                value = params.description,
                onValueChange = {
                    component.updateParams(
                        params.copy(
                            description = it
                        )
                    )
                },
                singleLine = false,
                label = {
                    Text(stringResource(id = R.string.qr_description))
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            BoxAnimatedVisibility(
                visible = params.description.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                FontSelector(
                    value = params.descriptionFont,
                    onValueChange = {
                        component.updateParams(
                            params.copy(
                                descriptionFont = it
                            )
                        )
                    },
                    color = Color.Unspecified,
                    shape = ShapeDefaults.bottom,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            EnhancedSliderItem(
                value = params.cornersSize,
                title = stringResource(R.string.corners),
                valueRange = 0f..24f,
                onValueChange = {
                    component.updateParams(
                        params.copy(
                            cornersSize = it.toInt()
                        )
                    )
                },
                internalStateTransformation = {
                    it.roundToInt()
                },
                steps = 22
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}