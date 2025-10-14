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
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Padding
import androidx.compose.material.icons.outlined.PhotoSizeSelectLarge
import androidx.compose.material.icons.outlined.RoundedCorner
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.RoundedCorner
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.material.icons.rounded.ViewColumn
import androidx.compose.material.icons.sharp.Square
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.extendedcolors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.other.QrCodeParams
import com.t8rin.imagetoolbox.core.ui.widget.other.QrCodeParams.BallShape
import com.t8rin.imagetoolbox.core.ui.widget.other.QrCodeParams.ErrorCorrectionLevel
import com.t8rin.imagetoolbox.core.ui.widget.other.QrCodeParams.FrameShape
import com.t8rin.imagetoolbox.core.ui.widget.other.QrCodeParams.MaskPattern
import com.t8rin.imagetoolbox.core.ui.widget.other.QrCodeParams.PixelShape
import com.t8rin.imagetoolbox.core.ui.widget.other.defaultQrColors
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlin.math.roundToInt

@Composable
internal fun QrParamsSelector(
    isQrType: Boolean,
    value: QrCodeParams,
    onValueChange: (QrCodeParams) -> Unit
) {
    Column(
        modifier = Modifier
            .container(
                shape = ShapeDefaults.large,
                resultPadding = 12.dp
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TitleItem(
            text = stringResource(R.string.code_customization),
            icon = Icons.Outlined.Code,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val (bg, fg) = defaultQrColors()

            ColorRowSelector(
                value = value.foregroundColor ?: fg,
                onValueChange = {
                    onValueChange(
                        value.copy(
                            foregroundColor = it
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .container(
                        color = MaterialTheme.colorScheme.surface,
                        shape = ShapeDefaults.top
                    ),
                title = stringResource(R.string.dark_color)
            )
            ColorRowSelector(
                value = value.backgroundColor ?: bg,
                onValueChange = {
                    onValueChange(
                        value.copy(
                            backgroundColor = it
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .container(
                        color = MaterialTheme.colorScheme.surface,
                        shape = ShapeDefaults.bottom
                    ),
                title = stringResource(R.string.light_color)
            )
        }
        AnimatedVisibility(
            visible = isQrType,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val logoParamsSize = 4
                    Row(
                        modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max)
                    ) {
                        ImageSelector(
                            value = value.logo,
                            title = stringResource(R.string.logo),
                            subtitle = stringResource(R.string.qr_logo_image),
                            onValueChange = {
                                onValueChange(
                                    value.copy(
                                        logo = it
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            shape = if (value.logo == null) {
                                ShapeDefaults.default
                            } else {
                                ShapeDefaults.topStart
                            },
                            color = MaterialTheme.colorScheme.surface
                        )

                        BoxAnimatedVisibility(visible = value.logo != null) {
                            val interactionSource = remember { MutableInteractionSource() }

                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(start = 4.dp)
                                    .container(
                                        color = MaterialTheme.colorScheme.errorContainer,
                                        resultPadding = 0.dp,
                                        shape = shapeByInteraction(
                                            shape = ShapeDefaults.topEnd,
                                            pressedShape = ShapeDefaults.pressed,
                                            interactionSource = interactionSource
                                        )
                                    )
                                    .hapticsClickable(
                                        interactionSource = interactionSource,
                                        indication = LocalIndication.current
                                    ) {
                                        onValueChange(
                                            value.copy(
                                                logo = null
                                            )
                                        )
                                    }
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
                    BoxAnimatedVisibility(
                        visible = value.logo != null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            EnhancedSliderItem(
                                value = value.logoPadding,
                                title = stringResource(R.string.logo_padding),
                                valueRange = 0f..1f,
                                internalStateTransformation = { it.roundToTwoDigits() },
                                onValueChange = {
                                    onValueChange(
                                        value.copy(
                                            logoPadding = it.roundToTwoDigits()
                                        )
                                    )
                                },
                                containerColor = MaterialTheme.colorScheme.surface,
                                icon = Icons.Outlined.Padding,
                                shape = ShapeDefaults.byIndex(
                                    index = 1,
                                    size = logoParamsSize
                                )
                            )
                            EnhancedSliderItem(
                                value = value.logoSize,
                                title = stringResource(R.string.logo_size),
                                valueRange = 0f..1f,
                                internalStateTransformation = { it.roundToTwoDigits() },
                                onValueChange = {
                                    onValueChange(
                                        value.copy(
                                            logoSize = it.roundToTwoDigits()
                                        )
                                    )
                                },
                                icon = Icons.Outlined.PhotoSizeSelectLarge,
                                containerColor = MaterialTheme.colorScheme.surface,
                                shape = ShapeDefaults.byIndex(
                                    index = 2,
                                    size = logoParamsSize
                                )
                            )
                            EnhancedSliderItem(
                                value = value.logoCorners,
                                title = stringResource(R.string.logo_corners),
                                valueRange = 0f..1f,
                                internalStateTransformation = { it.roundToTwoDigits() },
                                onValueChange = {
                                    onValueChange(
                                        value.copy(
                                            logoCorners = it.roundToTwoDigits()
                                        )
                                    )
                                },
                                icon = Icons.Outlined.RoundedCorner,
                                containerColor = MaterialTheme.colorScheme.surface,
                                shape = ShapeDefaults.byIndex(
                                    index = 3,
                                    size = logoParamsSize
                                )
                            )
                        }
                    }
                }
                EnhancedButtonGroup(
                    modifier = Modifier
                        .fillMaxWidth()
                        .container(
                            shape = ShapeDefaults.default,
                            color = MaterialTheme.colorScheme.surface
                        ),
                    entries = PixelShape.entries,
                    value = value.pixelShape,
                    itemContent = { it.Content() },
                    onValueChange = {
                        onValueChange(
                            value.copy(
                                pixelShape = it
                            )
                        )
                    },
                    title = stringResource(R.string.pixel_shape),
                    inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer,
                    activeButtonColor = MaterialTheme.colorScheme.primary
                )
                EnhancedButtonGroup(
                    modifier = Modifier
                        .fillMaxWidth()
                        .container(
                            shape = ShapeDefaults.default,
                            color = MaterialTheme.colorScheme.surface
                        ),
                    entries = FrameShape.entries,
                    value = value.frameShape,
                    itemContent = { it.Content() },
                    onValueChange = {
                        onValueChange(
                            value.copy(
                                frameShape = it
                            )
                        )
                    },
                    title = stringResource(R.string.frame_shape),
                    inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer,
                    activeButtonColor = MaterialTheme.colorScheme.primary
                )
                EnhancedButtonGroup(
                    modifier = Modifier
                        .fillMaxWidth()
                        .container(
                            shape = ShapeDefaults.default,
                            color = MaterialTheme.colorScheme.surface
                        ),
                    entries = BallShape.entries,
                    value = value.ballShape,
                    itemContent = { it.Content() },
                    onValueChange = {
                        onValueChange(
                            value.copy(
                                ballShape = it
                            )
                        )
                    },
                    title = stringResource(R.string.ball_shape),
                    inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer,
                    activeButtonColor = MaterialTheme.colorScheme.primary
                )
                EnhancedButtonGroup(
                    modifier = Modifier
                        .fillMaxWidth()
                        .container(
                            shape = ShapeDefaults.default,
                            color = MaterialTheme.colorScheme.surface
                        ),
                    entries = ErrorCorrectionLevel.entries,
                    value = value.errorCorrectionLevel,
                    itemContent = {
                        Text(
                            text = when (it) {
                                ErrorCorrectionLevel.Auto -> stringResource(R.string.auto)
                                else -> it.name
                            }
                        )
                    },
                    onValueChange = {
                        onValueChange(
                            value.copy(
                                errorCorrectionLevel = it
                            )
                        )
                    },
                    title = stringResource(R.string.error_correction_level),
                    inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer,
                    activeButtonColor = MaterialTheme.colorScheme.secondaryContainer
                )
                EnhancedButtonGroup(
                    modifier = Modifier
                        .fillMaxWidth()
                        .container(
                            shape = ShapeDefaults.default,
                            color = MaterialTheme.colorScheme.surface
                        ),
                    entries = MaskPattern.entries,
                    value = value.maskPattern,
                    itemContent = {
                        Text(
                            text = when (it) {
                                MaskPattern.Auto -> stringResource(R.string.auto)
                                else -> it.name.removePrefix("P_")
                            }
                        )
                    },
                    onValueChange = {
                        onValueChange(
                            value.copy(
                                maskPattern = it
                            )
                        )
                    },
                    title = stringResource(R.string.mask_pattern),
                    inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer,
                    activeButtonColor = MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }
    }
}

@Composable
private fun PixelShape.Content() {
    when (this) {
        is PixelShape.Predefined -> {
            Icon(
                imageVector = when (this) {
                    PixelShape.Square -> Icons.Sharp.Square
                    PixelShape.RoundSquare -> Icons.Rounded.RoundedCorner
                    PixelShape.Circle -> Icons.Rounded.Circle
                    PixelShape.Vertical -> Icons.Rounded.ViewColumn
                    PixelShape.Horizontal -> Icons.Rounded.TableRows
                },
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        is PixelShape.Random -> {
            Icon(
                imageVector = Icons.Rounded.Shuffle,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        is PixelShape.Shaped -> {
            Spacer(
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        color = LocalContentColor.current,
                        shape = shape,
                    )
            )
        }
    }
}

@Composable
private fun FrameShape.Content() {
    when (this) {
        is FrameShape.RoundSquare -> {
            Spacer(
                modifier = Modifier
                    .size(20.dp)
                    .border(
                        width = 2.dp,
                        color = LocalContentColor.current,
                        shape = RoundedCornerShape(
                            percent = (percent * 100).roundToInt()
                        )
                    )
            )
        }
    }
}

@Composable
private fun BallShape.Content() {
    when (this) {
        is BallShape.Predefined -> {
            Icon(
                imageVector = when (this) {
                    BallShape.Square -> Icons.Sharp.Square
                    BallShape.RoundSquare -> Icons.Rounded.RoundedCorner
                    BallShape.Circle -> Icons.Rounded.Circle
                },
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        is BallShape.Shaped -> {
            Spacer(
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        color = LocalContentColor.current,
                        shape = shape,
                    )
            )
        }
    }
}