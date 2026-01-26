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

package com.t8rin.imagetoolbox.core.ui.widget.controls

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.RotateLeft
import androidx.compose.material.icons.automirrored.rounded.RotateRight
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Flip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.CropSmall
import com.t8rin.imagetoolbox.core.resources.icons.Curve
import com.t8rin.imagetoolbox.core.resources.icons.Draw
import com.t8rin.imagetoolbox.core.resources.icons.Eraser
import com.t8rin.imagetoolbox.core.resources.icons.Exif
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.theme.onMixedContainer
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

@Composable
fun ImageTransformBar(
    onEditExif: () -> Unit = {},
    imageFormat: ImageFormat? = null,
    onRotateLeft: () -> Unit,
    onFlip: () -> Unit,
    onRotateRight: () -> Unit,
    canRotate: Boolean = true,
    leadingContent: @Composable RowScope.() -> Unit = {},
) {
    val shape = AutoCornersShape(
        animateIntAsState(if (imageFormat?.canWriteExif == false) 20 else 50).value
    )
    Column(
        modifier = Modifier
            .container(shape)
            .animateContentSizeNoClip()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedVisibility(
                visible = imageFormat != null,
                modifier = Modifier.weight(1f, false)
            ) {
                Row {
                    Spacer(Modifier.width(4.dp))
                    EnhancedButton(
                        modifier = Modifier.height(40.dp),
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentPadding = PaddingValues(8.dp),
                        onClick = onEditExif
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Rounded.Exif,
                                contentDescription = stringResource(R.string.edit_exif)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.edit_exif)
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                    }
                    Spacer(
                        Modifier
                            .weight(1f)
                            .padding(4.dp)
                    )
                }
            }

            leadingContent()

            EnhancedIconButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onRotateLeft,
                enabled = canRotate
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.RotateLeft,
                    contentDescription = "Rotate Left"
                )
            }

            EnhancedIconButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onFlip
            ) {
                Icon(
                    imageVector = Icons.Rounded.Flip,
                    contentDescription = "Flip"
                )
            }

            EnhancedIconButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onRotateRight,
                enabled = canRotate
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.RotateRight,
                    contentDescription = "Rotate Right"
                )
            }
        }
        FormatExifWarning(imageFormat)
    }
}

@Composable
fun ImageExtraTransformBar(
    onCrop: () -> Unit,
    onFilter: () -> Unit,
    onDraw: () -> Unit,
    onEraseBackground: () -> Unit,
    onApplyCurves: () -> Unit
) {
    if (LocalSettingsState.current.generatePreviews) {
        Row(Modifier.container(shape = ShapeDefaults.circle)) {
            EnhancedIconButton(
                containerColor = MaterialTheme.colorScheme.mixedContainer.copy(0.6f),
                contentColor = MaterialTheme.colorScheme.onMixedContainer,
                onClick = onCrop
            ) {
                Icon(
                    imageVector = Icons.Rounded.CropSmall,
                    contentDescription = stringResource(R.string.crop)
                )
            }

            EnhancedIconButton(
                containerColor = MaterialTheme.colorScheme.mixedContainer.copy(0.6f),
                contentColor = MaterialTheme.colorScheme.onMixedContainer,
                onClick = onApplyCurves
            ) {
                Icon(
                    imageVector = Icons.Outlined.Curve,
                    contentDescription = stringResource(R.string.tone_curves)
                )
            }

            EnhancedIconButton(
                containerColor = MaterialTheme.colorScheme.mixedContainer.copy(0.6f),
                contentColor = MaterialTheme.colorScheme.onMixedContainer,
                onClick = onFilter
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoFixHigh,
                    contentDescription = stringResource(R.string.filter)
                )
            }

            EnhancedIconButton(
                containerColor = MaterialTheme.colorScheme.mixedContainer.copy(0.6f),
                contentColor = MaterialTheme.colorScheme.onMixedContainer,
                onClick = onDraw
            ) {
                Icon(
                    imageVector = Icons.Rounded.Draw,
                    contentDescription = stringResource(R.string.draw)
                )
            }

            EnhancedIconButton(
                containerColor = MaterialTheme.colorScheme.mixedContainer.copy(0.6f),
                contentColor = MaterialTheme.colorScheme.onMixedContainer,
                onClick = onEraseBackground
            ) {
                Icon(
                    imageVector = Icons.Rounded.Eraser,
                    contentDescription = stringResource(R.string.erase_background)
                )
            }
        }
    }
}