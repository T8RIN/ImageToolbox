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

package ru.tech.imageresizershrinker.core.ui.widget.controls

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.material.CropSmall
import ru.tech.imageresizershrinker.core.ui.icons.material.Exif
import ru.tech.imageresizershrinker.core.ui.icons.material.Transparency
import ru.tech.imageresizershrinker.core.ui.theme.mixedContainer
import ru.tech.imageresizershrinker.core.ui.theme.onMixedContainer
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
fun ImageTransformBar(
    onEditExif: () -> Unit = {},
    imageFormat: ImageFormat? = null,
    onRotateLeft: () -> Unit,
    onFlip: () -> Unit,
    onRotateRight: () -> Unit,
    trailingContent: @Composable () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .container(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainer
            )
            .animateContentSize()
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

            trailingContent()

            EnhancedIconButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onRotateLeft
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.RotateLeft,
                    contentDescription = "Rotate Left"
                )
            }

            EnhancedIconButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onFlip
            ) {
                Icon(
                    imageVector = Icons.Default.Flip,
                    contentDescription = "Flip"
                )
            }

            EnhancedIconButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onRotateRight
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.RotateRight,
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
    onEraseBackground: () -> Unit
) {
    Row(Modifier.container(shape = CircleShape)) {
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
                imageVector = Icons.Filled.Transparency,
                contentDescription = stringResource(R.string.erase_background)
            )
        }
    }
}