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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.icons.BrokenImageAlt
import com.t8rin.imagetoolbox.core.ui.theme.Red
import com.t8rin.imagetoolbox.core.ui.theme.White
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.widget.buttons.MediaCheckBox
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsCombinedClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media

@Composable
fun MediaImage(
    modifier: Modifier = Modifier,
    media: Media,
    isInSelection: Boolean = true,
    isSelected: Boolean,
    selectionIndex: Int,
    canClick: Boolean,
    onItemClick: (Media) -> Unit,
    onItemLongClick: (Media) -> Unit,
) {
    val selectedSize by animateDpAsState(
        if (isSelected) 12.dp else 0.dp
    )
    val scale by animateFloatAsState(
        if (isSelected) 0.5f else 1f
    )
    val selectedShapeSize by animateDpAsState(
        if (isSelected) 16.dp else 4.dp
    )
    val strokeSize by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 0.dp
    )
    var isImageError by remember {
        mutableStateOf(false)
    }
    val strokeColor = takeColorFromScheme {
        if (isSelected) {
            if (isImageError) errorContainer
            else primaryContainer
        } else Color.Transparent
    }

    Box(
        modifier = modifier
            .clip(ShapeDefaults.extraSmall)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .then(
                if (canClick) {
                    Modifier.hapticsCombinedClickable(
                        onClick = {
                            onItemClick(media)
                        },
                        onLongClick = {
                            onItemLongClick(media)
                        },
                    )
                } else Modifier
            )
            .aspectRatio(1f)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .aspectRatio(1f)
                .padding(selectedSize)
                .clip(RoundedCornerShape(selectedShapeSize))
                .border(
                    width = strokeSize,
                    shape = RoundedCornerShape(selectedShapeSize),
                    color = strokeColor
                )
                .then(
                    if (isSelected) {
                        Modifier.clip(
                            RoundedCornerShape(selectedShapeSize + 2.dp)
                        )
                    } else Modifier
                )
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(selectedShapeSize)
                )
        ) {
            Picture(
                modifier = Modifier
                    .fillMaxSize(),
                model = media.uri,
                contentDescription = media.label,
                contentScale = ContentScale.Crop,
                onSuccess = {
                    isImageError = false
                },
                onError = {
                    isImageError = true
                },
                error = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.background(
                            takeColorFromScheme { isNightMode ->
                                errorContainer.copy(
                                    if (isNightMode) 0.25f
                                    else 1f
                                ).compositeOver(surface)
                            }
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.BrokenImageAlt,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(0.5f),
                            tint = MaterialTheme.colorScheme.onErrorContainer.copy(0.8f)
                        )
                    }
                }
            )
        }

        AnimatedContent(
            targetState = media.duration != null,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.align(Alignment.TopEnd)
        ) { haveDuration ->
            if (haveDuration) {
                MediaVideoDurationHeader(
                    modifier = Modifier
                        .padding(selectedSize / 2)
                        .scale(scale),
                    media = media
                )
            } else {
                MediaExtensionHeader(
                    modifier = Modifier
                        .padding(selectedSize / 2)
                        .scale(scale),
                    media = media
                )
            }
        }

        AnimatedVisibility(
            visible = media.fileSize > 0,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            MediaSizeFooter(
                modifier = Modifier
                    .padding(selectedSize / 2)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = TransformOrigin(0.3f, 0.5f)
                    },
                media = media
            )
        }

        AnimatedVisibility(
            visible = media.isFavorite,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                modifier = Modifier
                    .padding(selectedSize / 2)
                    .scale(scale)
                    .padding(8.dp)
                    .size(16.dp),
                imageVector = Icons.Filled.Favorite,
                tint = Red,
                contentDescription = null
            )
        }

        AnimatedVisibility(
            visible = isInSelection,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                MediaCheckBox(
                    isChecked = isSelected,
                    uncheckedColor = White.copy(0.8f),
                    checkedColor = if (isImageError) {
                        MaterialTheme.colorScheme.error
                    } else MaterialTheme.colorScheme.primary,
                    checkedIcon = if (isImageError) {
                        Icons.Filled.Error
                    } else Icons.Filled.CheckCircle,
                    selectionIndex = selectionIndex,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            animateColorAsState(
                                if (isSelected) MaterialTheme.colorScheme.surfaceContainer
                                else Color.Transparent
                            ).value
                        )
                )
            }
        }
    }
}
