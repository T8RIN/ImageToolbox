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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BrokenImageAlt
import com.t8rin.imagetoolbox.core.ui.theme.White
import com.t8rin.imagetoolbox.core.ui.theme.onPrimaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.theme.primaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.helper.PredictiveBackObserver
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.widget.buttons.MediaCheckBox
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.image.HistogramChart
import com.t8rin.imagetoolbox.core.ui.widget.image.MetadataPreviewButton
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.toShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.withLayoutCorners
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.modalsheet.FullscreenPopup
import kotlinx.coroutines.delay
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.toggleScale
import net.engawapg.lib.zoomable.zoomable
import kotlin.math.roundToInt

@Composable
internal fun MediaImagePager(
    imagePreviewUri: String?,
    onDismiss: () -> Unit,
    media: List<Media>,
    selectedMedia: SnapshotStateList<Media>,
    onMediaClick: (Media) -> Unit
) {
    val visible = imagePreviewUri != null

    FullscreenPopup {
        var predictiveBackProgress by remember {
            mutableFloatStateOf(0f)
        }
        val animatedPredictiveBackProgress by animateFloatAsState(predictiveBackProgress)
        val scale = (1f - animatedPredictiveBackProgress).coerceAtLeast(0.75f)

        LaunchedEffect(predictiveBackProgress, visible) {
            if (!visible && predictiveBackProgress != 0f) {
                delay(600)
                predictiveBackProgress = 0f
            }
        }

        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(tween(500)),
            exit = fadeOut(tween(500))
        ) {
            val density = LocalDensity.current
            val screenHeight =
                LocalScreenSize.current.height + WindowInsets.systemBars.asPaddingValues()
                    .let { it.calculateTopPadding() + it.calculateBottomPadding() }
            val anchors = with(density) {
                DraggableAnchors {
                    true at 0f
                    false at -screenHeight.toPx()
                }
            }

            val draggableState = remember(anchors) {
                AnchoredDraggableState(
                    initialValue = true,
                    anchors = anchors
                )
            }

            LaunchedEffect(draggableState.settledValue) {
                if (!draggableState.settledValue) {
                    onDismiss()
                    delay(600)
                    draggableState.snapTo(true)
                }
            }

            val initialPage by remember(imagePreviewUri, media) {
                derivedStateOf {
                    imagePreviewUri?.let {
                        media.indexOfFirst { it.uri == imagePreviewUri }
                    }?.takeIf { it >= 0 } ?: 0
                }
            }
            val pagerState = rememberPagerState(
                initialPage = initialPage,
                pageCount = {
                    media.size
                }
            )
            val progress by remember(draggableState) {
                derivedStateOf {
                    draggableState.progress(
                        from = false,
                        to = true
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .withLayoutCorners { corners ->
                        graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            shape = corners.toShape(animatedPredictiveBackProgress)
                            clip = true
                        }
                    }
                    .background(
                        MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f * progress)
                    )
            ) {
                val currentMedia = media.getOrNull(pagerState.currentPage)
                val imageErrorPages = remember {
                    mutableStateListOf<Int>()
                }
                var hideControls by remember(animatedPredictiveBackProgress) {
                    mutableStateOf(false)
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondViewportPageCount = 5,
                    pageSpacing = if (pagerState.pageCount > 1) 16.dp
                    else 0.dp
                ) { page ->
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val zoomState = rememberZoomState(10f)
                        Picture(
                            showTransparencyChecker = false,
                            model = media.getOrNull(page)?.uri,
                            modifier = Modifier
                                .fillMaxSize()
                                .clipToBounds()
                                .systemBarsPadding()
                                .displayCutoutPadding()
                                .offset {
                                    IntOffset(
                                        x = 0,
                                        y = -draggableState
                                            .requireOffset()
                                            .roundToInt(),
                                    )
                                }
                                .anchoredDraggable(
                                    state = draggableState,
                                    enabled = zoomState.scale < 1.01f && !pagerState.isScrollInProgress,
                                    orientation = Orientation.Vertical,
                                    reverseDirection = true,
                                    flingBehavior = AnchoredDraggableDefaults.flingBehavior(
                                        animationSpec = tween(500),
                                        state = draggableState
                                    )
                                )
                                .zoomable(
                                    zoomEnabled = !imageErrorPages.contains(page),
                                    zoomState = zoomState,
                                    onTap = {
                                        hideControls = !hideControls
                                    },
                                    onDoubleTap = {
                                        zoomState.toggleScale(
                                            targetScale = 5f,
                                            position = it
                                        )
                                    }
                                ),
                            enableUltraHDRSupport = true,
                            contentScale = ContentScale.Fit,
                            shape = RectangleShape,
                            onSuccess = {
                                imageErrorPages.remove(page)
                            },
                            onError = {
                                imageErrorPages.add(page)
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
                }
                val showTopBar by remember(hideControls, draggableState) {
                    derivedStateOf {
                        draggableState.offset == 0f && !hideControls
                    }
                }
                val showBottomHist = pagerState.currentPage !in imageErrorPages
                val showBottomBar by remember(draggableState, showBottomHist, hideControls) {
                    derivedStateOf {
                        draggableState.offset == 0f && showBottomHist && !hideControls
                    }
                }

                AnimatedVisibility(
                    visible = showTopBar,
                    modifier = Modifier.fillMaxWidth(),
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    EnhancedTopAppBar(
                        colors = EnhancedTopAppBarDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)
                        ),
                        type = EnhancedTopAppBarType.Center,
                        drawHorizontalStroke = false,
                        title = {
                            media.size.takeIf { it > 1 }?.let {
                                Text(
                                    text = "${pagerState.currentPage + 1}/$it",
                                    modifier = Modifier
                                        .padding(vertical = 4.dp, horizontal = 12.dp),
                                    color = White
                                )
                            }
                        },
                        actions = {
                            val isImageError = imageErrorPages.contains(pagerState.currentPage)
                            AnimatedVisibility(
                                visible = media.isNotEmpty(),
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                val isChecked = selectedMedia.contains(currentMedia)
                                MediaCheckBox(
                                    isChecked = isChecked,
                                    onCheck = {
                                        currentMedia?.let(onMediaClick)
                                    },
                                    uncheckedColor = White,
                                    checkedColor = if (isImageError) {
                                        MaterialTheme.colorScheme.error
                                    } else MaterialTheme.colorScheme.primary,
                                    checkedIcon = if (isImageError) {
                                        Icons.Filled.Error
                                    } else Icons.Filled.CheckCircle,
                                    addContainer = isChecked
                                )
                            }
                        },
                        navigationIcon = {
                            AnimatedVisibility(media.isNotEmpty()) {
                                EnhancedIconButton(
                                    onClick = onDismiss
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = stringResource(R.string.exit),
                                        tint = White
                                    )
                                }
                            }
                        },
                    )
                }

                AnimatedVisibility(
                    visible = showBottomBar,
                    modifier = Modifier.align(Alignment.BottomEnd),
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut() + slideOutVertically { it / 2 }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.scrim.copy(0.5f))
                            .navigationBarsPadding()
                            .padding(
                                WindowInsets.displayCutout
                                    .only(
                                        WindowInsetsSides.Horizontal
                                    )
                                    .asPaddingValues()
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            currentMedia?.label?.let {
                                Text(
                                    text = it,
                                    modifier = Modifier
                                        .animateContentSizeNoClip()
                                        .weight(1f, false),
                                    color = White,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontSize = 13.sp
                                )
                            }
                            currentMedia?.humanFileSize
                                ?.takeIf { currentMedia.fileSize > 0 }
                                ?.let { size ->
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = size,
                                        modifier = Modifier
                                            .animateContentSizeNoClip()
                                            .background(
                                                color = MaterialTheme.colorScheme.primaryContainerFixed,
                                                shape = CircleShape
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = MaterialTheme.colorScheme.onPrimaryContainerFixed,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            MetadataPreviewButton(
                                uri = currentMedia?.uri?.toUri()
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        HistogramChart(
                            model = currentMedia?.uri ?: Uri.EMPTY,
                            modifier = Modifier
                                .height(50.dp)
                                .width(90.dp),
                            bordersColor = Color.White
                        )
                    }
                }
            }

            PredictiveBackObserver(
                onProgress = {
                    predictiveBackProgress = it / 6f
                },
                onClean = { isCompleted ->
                    if (isCompleted) {
                        onDismiss()
                        delay(400)
                    }
                    predictiveBackProgress = 0f
                },
                enabled = visible
            )
        }
    }
}