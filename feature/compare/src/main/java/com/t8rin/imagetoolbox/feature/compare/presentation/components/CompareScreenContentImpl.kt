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

package com.t8rin.imagetoolbox.feature.compare.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.transform.Transformation
import com.smarttoolfactory.beforeafter.BeforeAfterLayout
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.CornerSides
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.only
import com.t8rin.imagetoolbox.core.ui.widget.modifier.tappable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.feature.compare.presentation.components.model.CompareData
import com.t8rin.imagetoolbox.feature.compare.presentation.components.model.ifNotEmpty
import kotlinx.coroutines.delay
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
internal fun CompareScreenContentImpl(
    compareType: CompareType,
    bitmapPair: CompareData,
    pixelByPixelCompareState: PixelByPixelCompareState,
    compareProgress: Float,
    onCompareProgressChange: (Float) -> Unit,
    isPortrait: Boolean,
    isLabelsEnabled: Boolean,
    createPixelByPixelTransformation: () -> Transformation
) {
    val modifier = Modifier
        .padding(16.dp)
        .container(ShapeDefaults.default)
        .padding(4.dp)
        .clip(ShapeDefaults.small)
        .transparencyChecker()

    AnimatedContent(targetState = compareType) { type ->
        when (type) {
            CompareType.Slide -> {
                AnimatedContent(targetState = bitmapPair) { data ->
                    data.ifNotEmpty { beforeData, afterData ->
                        val before = remember(data) { beforeData.image.asImageBitmap() }
                        val after = remember(data) { afterData.image.asImageBitmap() }

                        BeforeAfterLayout(
                            modifier = modifier,
                            progress = animateFloatAsState(targetValue = compareProgress).value,
                            onProgressChange = onCompareProgressChange,
                            enableZoom = false,
                            beforeContent = {
                                Picture(
                                    model = before,
                                    modifier = Modifier.aspectRatio(before.safeAspectRatio)
                                )
                            },
                            afterContent = {
                                Picture(
                                    model = after,
                                    modifier = Modifier.aspectRatio(after.safeAspectRatio)
                                )
                            },
                            beforeLabel = {
                                Box(
                                    modifier = Modifier.matchParentSize()
                                ) {
                                    CompareLabel(
                                        uri = beforeData.uri,
                                        alignment = Alignment.TopStart,
                                        enabled = isLabelsEnabled,
                                        shape = ShapeDefaults.default.only(
                                            CornerSides.BottomEnd
                                        )
                                    )
                                }
                            },
                            afterLabel = {
                                Box(
                                    modifier = Modifier.matchParentSize()
                                ) {
                                    CompareLabel(
                                        uri = afterData.uri,
                                        alignment = Alignment.BottomEnd,
                                        enabled = isLabelsEnabled,
                                        shape = ShapeDefaults.default.only(
                                            CornerSides.TopStart
                                        )
                                    )
                                }
                            }
                        )
                    }
                }
            }

            CompareType.SideBySide -> {
                val first = bitmapPair.first?.image
                val second = bitmapPair.second?.image

                val zoomState = rememberZoomState(30f)
                val zoomModifier = Modifier
                    .clipToBounds()
                    .zoomable(
                        zoomState = zoomState
                    )


                Box(modifier) {
                    if (isPortrait) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (first != null) {
                                Picture(
                                    model = first,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .then(zoomModifier)
                                )
                                HorizontalDivider()
                            }
                            if (second != null) {
                                Picture(
                                    model = second,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .then(zoomModifier)
                                )
                            }
                        }
                        CompareLabel(
                            uri = bitmapPair.first?.uri,
                            alignment = Alignment.TopStart,
                            enabled = isLabelsEnabled,
                            shape = ShapeDefaults.default.only(
                                CornerSides.BottomEnd
                            )
                        )
                        CompareLabel(
                            uri = bitmapPair.second?.uri,
                            alignment = Alignment.BottomStart,
                            enabled = isLabelsEnabled,
                            shape = ShapeDefaults.default.only(
                                CornerSides.TopEnd
                            )
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (first != null) {
                                Picture(
                                    model = first,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1f)
                                        .then(zoomModifier)
                                )
                                VerticalDivider()
                            }
                            if (second != null) {
                                Picture(
                                    model = second,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1f)
                                        .then(zoomModifier)
                                )
                            }
                        }
                        CompareLabel(
                            uri = bitmapPair.first?.uri,
                            alignment = Alignment.TopStart,
                            enabled = isLabelsEnabled,
                            shape = ShapeDefaults.default.only(
                                CornerSides.BottomEnd
                            )
                        )
                        CompareLabel(
                            uri = bitmapPair.second?.uri,
                            alignment = Alignment.TopEnd,
                            enabled = isLabelsEnabled,
                            shape = ShapeDefaults.default.only(
                                CornerSides.BottomStart
                            )
                        )
                    }
                }
            }

            CompareType.Tap -> {
                var showSecondImage by rememberSaveable {
                    mutableStateOf(false)
                }
                Box(
                    modifier = modifier.tappable {
                        showSecondImage = !showSecondImage
                    }
                ) {
                    val first = bitmapPair.first?.image
                    val second = bitmapPair.second?.image
                    if (!showSecondImage && first != null) {
                        Picture(
                            model = first,
                            contentDescription = null,
                            contentScale = ContentScale.Inside
                        )
                    }
                    if (showSecondImage && second != null) {
                        Picture(
                            model = second,
                            contentDescription = null,
                            contentScale = ContentScale.Inside
                        )
                    }
                    Box(
                        modifier = Modifier.matchParentSize()
                    ) {
                        CompareLabel(
                            uri = if (showSecondImage) bitmapPair.second?.uri
                            else bitmapPair.first?.uri,
                            alignment = if (showSecondImage) Alignment.BottomEnd
                            else Alignment.TopStart,
                            enabled = isLabelsEnabled,
                            shape = if (showSecondImage) {
                                ShapeDefaults.default.only(
                                    CornerSides.TopStart
                                )
                            } else {
                                ShapeDefaults.default.only(
                                    CornerSides.BottomEnd
                                )
                            }
                        )
                    }
                }
            }

            CompareType.Transparency -> {
                Box(
                    modifier = modifier
                ) {
                    val first = bitmapPair.first?.image
                    val second = bitmapPair.second?.image
                    if (first != null) {
                        Picture(
                            model = first,
                            contentDescription = null,
                            contentScale = ContentScale.Inside
                        )
                    }
                    if (second != null) {
                        Picture(
                            model = second,
                            contentDescription = null,
                            contentScale = ContentScale.Inside,
                            modifier = Modifier.alpha(compareProgress / 100f)
                        )
                    }
                    Box(
                        modifier = Modifier.matchParentSize()
                    ) {
                        CompareLabel(
                            uri = bitmapPair.first?.uri,
                            alignment = Alignment.TopStart,
                            enabled = isLabelsEnabled,
                            shape = ShapeDefaults.default.only(
                                CornerSides.BottomEnd
                            )
                        )
                    }
                    Box(
                        modifier = Modifier.matchParentSize()
                    ) {
                        CompareLabel(
                            uri = bitmapPair.second?.uri,
                            modifier = Modifier.alpha(compareProgress / 100f),
                            alignment = Alignment.BottomEnd,
                            enabled = isLabelsEnabled,
                            shape = ShapeDefaults.default.only(
                                CornerSides.TopStart
                            )
                        )
                    }
                }
            }

            CompareType.PixelByPixel -> {
                var isLoading by remember {
                    mutableStateOf(false)
                }

                val first = bitmapPair.first?.image
                val second = bitmapPair.second?.image

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = modifier
                    ) {
                        if (first != null) {
                            var transformations: List<Transformation> by remember {
                                mutableStateOf(emptyList())
                            }

                            LaunchedEffect(
                                first,
                                second,
                                compareProgress,
                                pixelByPixelCompareState
                            ) {
                                delay(300)
                                transformations = listOf(
                                    createPixelByPixelTransformation()
                                )
                            }

                            Picture(
                                model = first,
                                transformations = transformations,
                                onSuccess = {
                                    isLoading = false
                                },
                                onLoading = {
                                    isLoading = true
                                },
                                contentDescription = null,
                                contentScale = ContentScale.Inside
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = isLoading && first != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            EnhancedLoadingIndicator()
                        }
                    }
                }
            }
        }
    }
}