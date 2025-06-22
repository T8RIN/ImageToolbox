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

package com.t8rin.imagetoolbox.core.ui.widget.controls

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.restrict
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.CalculatorDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField

@Composable
fun ResizeImageField(
    imageInfo: ImageInfo,
    originalSize: IntegerSize?,
    onWidthChange: (Int) -> Unit,
    onHeightChange: (Int) -> Unit,
    showWarning: Boolean = false
) {
    Column(
        modifier = Modifier
            .container(shape = ShapeDefaults.extraLarge)
            .padding(8.dp)
            .animateContentSizeNoClip()
    ) {
        Row {
            val widthField: @Composable RowScope.() -> Unit = {
                ResizeImageFieldImpl(
                    value = imageInfo.width.takeIf { it > 0 }
                        .let { it ?: "" }
                        .toString(),
                    onValueChange = { value ->
                        val maxValue = if (imageInfo.height > 0) {
                            (ImageUtils.Dimens.MAX_IMAGE_SIZE / imageInfo.height).coerceAtMost(32768)
                        } else 32768

                        onWidthChange(
                            value
                                .restrict(maxValue)
                                .toIntOrNull() ?: 0
                        )
                    },
                    shape = ShapeDefaults.smallStart,
                    label = {
                        AutoSizeText(
                            stringResource(
                                R.string.width,
                                originalSize?.width?.toString() ?: ""
                            )
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            val heightField: @Composable RowScope.() -> Unit = {
                ResizeImageFieldImpl(
                    value = imageInfo.height.takeIf { it > 0 }
                        .let { it ?: "" }
                        .toString(),
                    onValueChange = { value ->
                        val maxValue = if (imageInfo.width > 0) {
                            (ImageUtils.Dimens.MAX_IMAGE_SIZE / imageInfo.width).coerceAtMost(32768)
                        } else 32768

                        onHeightChange(
                            value
                                .restrict(maxValue)
                                .toIntOrNull() ?: 0
                        )
                    },
                    shape = ShapeDefaults.smallEnd,
                    label = {
                        AutoSizeText(
                            stringResource(
                                R.string.height,
                                originalSize?.height?.toString()
                                    ?: ""
                            )
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            widthField()
            Spacer(modifier = Modifier.width(4.dp))
            heightField()
        }
        IcoSizeWarning(
            visible = imageInfo.run {
                imageFormat == ImageFormat.Ico && (width > 256 || height > 256)
            }
        )
        OOMWarning(visible = showWarning)
    }
}

@Composable
internal fun ResizeImageFieldImpl(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    shape: Shape,
    modifier: Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    var showCalculator by rememberSaveable {
        mutableStateOf(false)
    }

    RoundedTextField(
        value = value,
        onValueChange = onValueChange,
        shape = shape,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        label = label,
        endIcon = {
            AnimatedVisibility(
                visible = isFocused,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                EnhancedIconButton(
                    onClick = {
                        showCalculator = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Calculate,
                        contentDescription = null
                    )
                }
            }
        },
        modifier = modifier,
        interactionSource = interactionSource
    )

    CalculatorDialog(
        visible = showCalculator,
        onDismiss = { showCalculator = false },
        initialValue = value.toBigDecimalOrNull(),
        onValueChange = { onValueChange(it.toInt().toString()) }
    )
}