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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.size.Precision
import com.t8rin.imagetoolbox.core.resources.icons.BrokenImageAlt
import com.t8rin.imagetoolbox.core.ui.theme.White
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.widget.buttons.MediaCheckBox
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsCombinedClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateShape
import com.t8rin.imagetoolbox.core.utils.appContext
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
    val transition = updateTransition(isSelected)

    val selectedSize = transition.animateDp {
        if (it) 12.dp else 0.dp
    }
    val scale = transition.animateFloat {
        if (it) 0.5f else 1f
    }
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
        val shape = animateShape(
            if (isSelected) {
                AutoCornersShape(16.dp)
            } else {
                AutoCornersShape(4.dp)
            }
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .aspectRatio(1f)
                .padding(selectedSize.value)
                .clip(shape)
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    shape = shape,
                    color = strokeColor
                )
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = shape
                )
        ) {
            Picture(
                modifier = Modifier.fillMaxSize(),
                model = remember(media.uri) {
                    ImageRequest.Builder(appContext)
                        .data(media.uri)
                        .size(384)
                        .precision(Precision.INEXACT)
                        .memoryCacheKey(media.uri)
                        .diskCacheKey(media.uri)
                        .allowHardware(true)
                        .build()
                },
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
                },
                filterQuality = FilterQuality.High
            )
        }

        Box(
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            if (media.duration != null) {
                MediaVideoDurationHeader(
                    modifier = Modifier
                        .padding(selectedSize.value / 2)
                        .graphicsLayer {
                            scaleX = scale.value
                            scaleY = scale.value
                        },
                    media = media,
                )
            } else {
                MediaExtensionHeader(
                    modifier = Modifier
                        .padding(selectedSize.value / 2)
                        .graphicsLayer {
                            scaleX = scale.value
                            scaleY = scale.value
                        },
                    media = media
                )
            }
        }

        if (media.fileSize > 0) {
            MediaSizeFooter(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(selectedSize.value / 2)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        transformOrigin = TransformOrigin(0.3f, 0.5f)
                    },
                media = media,
            )
        }

        if (isInSelection) {
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
                        .clip(ShapeDefaults.circle)
                        .background(
                            transition.animateColor {
                                if (it) MaterialTheme.colorScheme.surfaceContainer
                                else Color.Transparent
                            }.value
                        )
                )
            }
        }
    }
}
