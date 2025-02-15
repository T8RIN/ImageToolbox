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

package ru.tech.imageresizershrinker.feature.compare.presentation.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.transform.Transformation
import com.smarttoolfactory.beforeafter.BeforeAfterImage
import kotlinx.coroutines.delay
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingIndicator

@Composable
internal fun CompareScreenContentImpl(
    compareType: CompareType,
    bitmapPair: Pair<Pair<Uri, Bitmap>?, Pair<Uri, Bitmap>?>,
    pixelByPixelCompareState: PixelByPixelCompareState,
    compareProgress: Float,
    onCompareProgressChange: (Float) -> Unit,
    isPortrait: Boolean,
    isLabelsEnabled: Boolean,
    createPixelByPixelTransformation: () -> Transformation
) {
    val modifier = Modifier
        .padding(16.dp)
        .container(RoundedCornerShape(16.dp))
        .padding(4.dp)
        .clip(RoundedCornerShape(12.dp))
        .transparencyChecker()

    AnimatedContent(targetState = compareType) { type ->
        when (type) {
            CompareType.Slide -> {
                AnimatedContent(targetState = bitmapPair) { data ->
                    data.let { (b, a) ->
                        val before = remember(data) { b?.second?.asImageBitmap() }
                        val after = remember(data) { a?.second?.asImageBitmap() }

                        if (before != null && after != null) {
                            BeforeAfterImage(
                                enableZoom = false,
                                modifier = modifier,
                                progress = animateFloatAsState(targetValue = compareProgress).value,
                                onProgressChange = onCompareProgressChange,
                                beforeImage = before,
                                afterImage = after,
                                beforeLabel = {
                                    b?.let { (uri) ->
                                        Box(
                                            modifier = Modifier.matchParentSize()
                                        ) {
                                            CompareLabel(
                                                uri = uri,
                                                alignment = Alignment.TopStart,
                                                enabled = isLabelsEnabled,
                                                shape = RoundedCornerShape(
                                                    bottomEnd = 16.dp
                                                )
                                            )
                                        }
                                    }
                                },
                                afterLabel = {
                                    a?.let { (uri) ->
                                        Box(
                                            modifier = Modifier.matchParentSize()
                                        ) {
                                            CompareLabel(
                                                uri = uri,
                                                alignment = Alignment.BottomEnd,
                                                enabled = isLabelsEnabled,
                                                shape = RoundedCornerShape(
                                                    topStart = 16.dp
                                                )
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            CompareType.SideBySide -> {
                val first = bitmapPair.first?.second
                val second = bitmapPair.second?.second

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
                                AsyncImage(
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
                                AsyncImage(
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
                            uri = bitmapPair.first?.first,
                            alignment = Alignment.TopStart,
                            enabled = isLabelsEnabled,
                            shape = RoundedCornerShape(
                                bottomEnd = 16.dp
                            )
                        )
                        CompareLabel(
                            uri = bitmapPair.second?.first,
                            alignment = Alignment.BottomStart,
                            enabled = isLabelsEnabled,
                            shape = RoundedCornerShape(
                                topEnd = 16.dp
                            )
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (first != null) {
                                AsyncImage(
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
                                AsyncImage(
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
                            uri = bitmapPair.first?.first,
                            alignment = Alignment.TopStart,
                            enabled = isLabelsEnabled,
                            shape = RoundedCornerShape(
                                bottomEnd = 16.dp
                            )
                        )
                        CompareLabel(
                            uri = bitmapPair.second?.first,
                            alignment = Alignment.TopEnd,
                            enabled = isLabelsEnabled,
                            shape = RoundedCornerShape(
                                bottomStart = 16.dp
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
                    modifier = modifier
                        .pointerInput(Unit) {
                            detectTapGestures {
                                showSecondImage = !showSecondImage
                            }
                        }
                ) {
                    val first = bitmapPair.first?.second
                    val second = bitmapPair.second?.second
                    if (!showSecondImage && first != null) {
                        AsyncImage(
                            model = first,
                            contentDescription = null,
                            contentScale = ContentScale.Inside
                        )
                    }
                    if (showSecondImage && second != null) {
                        AsyncImage(
                            model = second,
                            contentDescription = null,
                            contentScale = ContentScale.Inside
                        )
                    }
                    Box(
                        modifier = Modifier.matchParentSize()
                    ) {
                        CompareLabel(
                            uri = if (showSecondImage) bitmapPair.second?.first
                            else bitmapPair.first?.first,
                            alignment = if (showSecondImage) Alignment.BottomEnd
                            else Alignment.TopStart,
                            enabled = isLabelsEnabled,
                            shape = if (showSecondImage) {
                                RoundedCornerShape(
                                    topStart = 16.dp
                                )
                            } else {
                                RoundedCornerShape(
                                    bottomEnd = 16.dp
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
                    val first = bitmapPair.first?.second
                    val second = bitmapPair.second?.second
                    if (first != null) {
                        AsyncImage(
                            model = first,
                            contentDescription = null,
                            contentScale = ContentScale.Inside
                        )
                    }
                    if (second != null) {
                        AsyncImage(
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
                            uri = bitmapPair.first?.first,
                            alignment = Alignment.TopStart,
                            enabled = isLabelsEnabled,
                            shape = RoundedCornerShape(
                                bottomEnd = 16.dp
                            )
                        )
                    }
                    Box(
                        modifier = Modifier.matchParentSize()
                    ) {
                        CompareLabel(
                            uri = bitmapPair.second?.first,
                            modifier = Modifier.alpha(compareProgress / 100f),
                            alignment = Alignment.BottomEnd,
                            enabled = isLabelsEnabled,
                            shape = RoundedCornerShape(
                                topStart = 16.dp
                            )
                        )
                    }
                }
            }

            CompareType.PixelByPixel -> {
                var isLoading by remember {
                    mutableStateOf(false)
                }

                val first = bitmapPair.first?.second
                val second = bitmapPair.second?.second

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
                            LoadingIndicator()
                        }
                    }
                }
            }
        }
    }
}