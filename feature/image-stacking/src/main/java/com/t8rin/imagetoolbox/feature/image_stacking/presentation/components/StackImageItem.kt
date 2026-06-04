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

package com.t8rin.imagetoolbox.feature.image_stacking.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.colors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.domain.model.Position
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.KeyboardArrowDown
import com.t8rin.imagetoolbox.core.resources.icons.Percent
import com.t8rin.imagetoolbox.core.resources.icons.PhotoSizeSelectLarge
import com.t8rin.imagetoolbox.core.resources.icons.Place
import com.t8rin.imagetoolbox.core.resources.icons.RemoveCircle
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.AlphaSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.BlendingModeSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.translatedName
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.image_stacking.domain.StackImage

@Composable
fun StackImageItem(
    stackImage: StackImage,
    index: Int,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    onStackImageChange: (StackImage) -> Unit,
    isRemoveVisible: Boolean,
    backgroundColor: Color = Color.Unspecified,
    shape: Shape = MaterialTheme.shapes.extraLarge
) {
    var isControlsExpanded by rememberSaveable {
        mutableStateOf(true)
    }

    Row(
        modifier = modifier
            .container(color = backgroundColor, shape = shape)
            .animateContentSizeNoClip(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isRemoveVisible) {
                    EnhancedIconButton(
                        onClick = onRemove
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.RemoveCircle,
                            contentDescription = stringResource(R.string.remove)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            top = 8.dp,
                            end = 8.dp,
                            start = 16.dp,
                            bottom = 8.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.clip(MaterialTheme.shapes.small)
                    ) {
                        Picture(
                            model = stackImage.uri,
                            shape = RectangleShape,
                            modifier = Modifier
                                .height(56.dp)
                                .width(112.dp)
                        )
                        Box(
                            Modifier
                                .matchParentSize()
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainer.copy(
                                        alpha = 0.3f
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                }
                EnhancedIconButton(
                    onClick = {
                        isControlsExpanded = !isControlsExpanded
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Expand",
                        modifier = Modifier.rotate(
                            animateFloatAsState(
                                if (isControlsExpanded) 180f
                                else 0f
                            ).value
                        )
                    )
                }
            }
            AnimatedVisibility(
                visible = isControlsExpanded
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    AlphaSelector(
                        value = stackImage.alpha,
                        onValueChange = {
                            onStackImageChange(
                                stackImage.copy(alpha = it)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = ShapeDefaults.top
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    BlendingModeSelector(
                        value = stackImage.blendingMode,
                        onValueChange = {
                            onStackImageChange(
                                stackImage.copy(blendingMode = it)
                            )
                        },
                        color = Color.Unspecified,
                        shape = ShapeDefaults.center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    DataSelector(
                        value = stackImage.positionOption,
                        onValueChange = { positionOption ->
                            onStackImageChange(
                                when (positionOption) {
                                    StackPosition.Custom -> stackImage.copy(position = null)
                                    is StackPosition.Preset -> stackImage.copy(
                                        position = positionOption.position
                                    )
                                }
                            )
                        },
                        entries = rememberStackPositions(),
                        spanCount = 2,
                        title = stringResource(R.string.position),
                        titleIcon = Icons.Outlined.Place,
                        itemContentText = {
                            when (it) {
                                StackPosition.Custom -> stringResource(R.string.custom)
                                is StackPosition.Preset -> it.position.translatedName
                            }
                        },
                        containerColor = Color.Unspecified,
                        shape = ShapeDefaults.center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedVisibility(visible = stackImage.position == null) {
                        Column {
                            EnhancedSliderItem(
                                value = stackImage.positionX,
                                title = stringResource(R.string.offset_x),
                                icon = Icons.Rounded.Percent,
                                valueRange = 0f..1f,
                                internalStateTransformation = {
                                    it.roundToTwoDigits()
                                },
                                onValueChange = {
                                    onStackImageChange(
                                        stackImage.copy(
                                            positionX = it.roundToTwoDigits()
                                        )
                                    )
                                },
                                containerColor = Color.Unspecified,
                                shape = ShapeDefaults.center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            EnhancedSliderItem(
                                value = stackImage.positionY,
                                title = stringResource(R.string.offset_y),
                                icon = Icons.Rounded.Percent,
                                valueRange = 0f..1f,
                                internalStateTransformation = {
                                    it.roundToTwoDigits()
                                },
                                onValueChange = {
                                    onStackImageChange(
                                        stackImage.copy(
                                            positionY = it.roundToTwoDigits()
                                        )
                                    )
                                },
                                containerColor = Color.Unspecified,
                                shape = ShapeDefaults.center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                    DataSelector(
                        value = stackImage.scale,
                        onValueChange = {
                            onStackImageChange(
                                stackImage.copy(scale = it)
                            )
                        },
                        entries = StackImage.Scale.entries,
                        spanCount = 1,
                        title = stringResource(R.string.scale),
                        titleIcon = Icons.Outlined.PhotoSizeSelectLarge,
                        itemContentText = {
                            it.title()
                        },
                        containerColor = Color.Unspecified,
                        shape = ShapeDefaults.bottom
                    )
                }
            }
        }
    }
}

private val StackImage.positionOption: StackPosition
    get() = position?.let(StackPosition::Preset) ?: StackPosition.Custom

@Composable
private fun rememberStackPositions(): List<StackPosition> = remember {
    Position.entries.map(StackPosition::Preset) + StackPosition.Custom
}

private sealed class StackPosition {
    data class Preset(val position: Position) : StackPosition()

    data object Custom : StackPosition()
}

@Composable
private fun StackImage.Scale.title(): String = when (this) {
    StackImage.Scale.None -> stringResource(R.string.none)
    StackImage.Scale.Fill -> stringResource(R.string.fill)
    StackImage.Scale.Fit -> stringResource(R.string.fit)
    StackImage.Scale.FitWidth -> stringResource(R.string.fit_width)
    StackImage.Scale.FitHeight -> stringResource(R.string.fit_height)
    StackImage.Scale.Crop -> stringResource(R.string.crop)
}