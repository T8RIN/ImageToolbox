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

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
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
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.CheckCircle
import com.t8rin.imagetoolbox.core.resources.icons.Error
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.White
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.shareUris
import com.t8rin.imagetoolbox.core.ui.utils.helper.PredictiveBackObserver
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.widget.buttons.MediaCheckBox
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.image.HistogramChart
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageViewerError
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageViewerTitle
import com.t8rin.imagetoolbox.core.ui.widget.image.MetadataPreviewButton
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.toShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.withLayoutCorners
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaAction
import com.t8rin.modalsheet.FullscreenPopup
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import net.engawapg.lib.zoomable.ScrollGesturePropagation
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import kotlin.math.roundToInt

@Composable
internal fun MediaImagePager(
    imagePreviewUri: String?,
    onDismiss: () -> Unit,
    media: List<Media>,
    selectedMedia: SnapshotStateList<Media>,
    onMediaClick: (Media) -> Unit,
    onMediaAction: ((MediaAction, Media) -> Unit)? = null
) {
    val visible = imagePreviewUri != null
    val visibility = remember { MutableTransitionState(false) }
    visibility.targetState = visible
    if (!visible && visibility.isIdle && !visibility.currentState) return

    val context = LocalContext.current
    val useOpaqueBackground = LocalSettingsState.current.useOpaqueImageViewerBackground

    FullscreenPopup {
        var predictiveBackProgress by remember {
            mutableFloatStateOf(0f)
        }
        val animatedPredictiveBackProgress by animateFloatAsState(predictiveBackProgress)

        LaunchedEffect(predictiveBackProgress, visible) {
            if (!visible && predictiveBackProgress != 0f) {
                delay(600)
                predictiveBackProgress = 0f
            }
        }

        AnimatedVisibility(
            visibleState = visibility,
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

            LaunchedEffect(media.isEmpty()) {
                if (media.isEmpty()) onDismiss()
            }
            val initialPage = remember(imagePreviewUri, media) {
                media.indexOfFirst { it.uri == imagePreviewUri }.coerceAtLeast(0)
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
            val backgroundColor = if (useOpaqueBackground) Color.Black
            else MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .withLayoutCorners { corners ->
                        graphicsLayer {
                            val scale = (1f - animatedPredictiveBackProgress).coerceAtLeast(0.75f)
                            scaleX = scale
                            scaleY = scale
                            shape = corners.toShape(animatedPredictiveBackProgress)
                            clip = true
                        }
                    }
                    .drawBehind { drawRect(backgroundColor, alpha = progress) }
            ) {
                val currentPageMedia by remember(pagerState, media) {
                    derivedStateOf { media.getOrNull(pagerState.currentPage) }
                }
                val imageErrorPages = remember {
                    mutableStateListOf<String>()
                }
                var hideControls by remember {
                    mutableStateOf(false)
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondViewportPageCount = 1,
                    key = { media[it].uri },
                    pageSpacing = if (pagerState.pageCount > 1) 16.dp
                    else 0.dp
                ) { page ->
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val model = media.getOrNull(page)?.uri
                        var isImageLoaded by remember(model) { mutableStateOf(false) }
                        val zoomState = rememberZoomState()
                        LaunchedEffect(isImageLoaded) {
                            if (!isImageLoaded) zoomState.reset()
                        }
                        Picture(
                            showTransparencyChecker = false,
                            model = model,
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
                                    zoomEnabled = isImageLoaded,
                                    zoomState = zoomState,
                                    scrollGesturePropagation = ScrollGesturePropagation.NotZoomed,
                                    onTap = {
                                        hideControls = !hideControls
                                    }
                                ),
                            enableUltraHDRSupport = true,
                            enableUltraHDRSupportTransformation = false,
                            contentScale = ContentScale.Fit,
                            shape = RectangleShape,
                            onLoading = {
                                isImageLoaded = false
                                model?.let(imageErrorPages::remove)
                            },
                            onSuccess = {
                                isImageLoaded = true
                                model?.let(imageErrorPages::remove)
                            },
                            onError = {
                                isImageLoaded = false
                                if (model != null && model !in imageErrorPages) imageErrorPages.add(
                                    model
                                )
                            },
                            error = { ImageViewerError() }
                        )
                    }
                }
                val showControls by remember(draggableState) {
                    derivedStateOf {
                        draggableState.offset == 0f && !hideControls
                    }
                }

                AnimatedVisibility(
                    visible = showControls,
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
                            val currentMedia = currentPageMedia
                            MetadataPreviewButton(
                                uri = currentMedia?.uri?.toUri(),
                                onShare = { context.shareUris(listOf(it)) },
                                dateModified = { currentMedia?.timestamp?.times(1000) },
                                path = { currentMedia?.path },
                                name = { currentMedia?.label },
                                fileSize = { currentMedia?.humanFileSize?.takeIf { currentMedia.fileSize > 0 } },
                                imageSize = { currentMedia?.imageSize },
                                mimeType = { currentMedia?.mimeType },
                                isFavorite = currentMedia?.isFavorite == true,
                                isTrashed = currentMedia?.isTrashed == true,
                                onFavoriteChange = onMediaAction?.takeIf { currentMedia?.isTrashed == false }
                                    ?.let { action ->
                                        { uri, favorite ->
                                            media.firstOrNull { it.uri == uri.toString() }?.let {
                                                action(
                                                    if (favorite) MediaAction.Favorite else MediaAction.Unfavorite,
                                                    it
                                                )
                                            }
                                        }
                                    },
                                onTrash = onMediaAction?.let { action ->
                                    { uri ->
                                        media.firstOrNull { it.uri == uri.toString() }?.let {
                                            action(
                                                if (it.isTrashed) MediaAction.Restore else MediaAction.Trash,
                                                it
                                            )
                                        }
                                    }
                                },
                                onDelete = onMediaAction?.takeIf { currentMedia?.isTrashed == true }
                                    ?.let { action ->
                                        { uri ->
                                            media.firstOrNull { it.uri == uri.toString() }?.let {
                                                action(MediaAction.Delete, it)
                                            }
                                        }
                                    }
                            )
                        },
                        actions = {
                            val currentMedia = currentPageMedia
                            val isImageError = imageErrorPages.contains(currentMedia?.uri)
                            AnimatedVisibility(
                                visible = currentMedia != null,
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                val isChecked = selectedMedia.any { it.uri == currentMedia?.uri }
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
                                        Icons.Rounded.Error
                                    } else {
                                        Icons.Rounded.CheckCircle
                                    },
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
                                        imageVector = Icons.Rounded.ArrowBack,
                                        contentDescription = stringResource(R.string.exit),
                                        tint = White
                                    )
                                }
                            }
                        },
                    )
                }

                AnimatedVisibility(
                    visible = showControls,
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
                        val currentMedia = currentPageMedia
                        Column(Modifier.weight(1f)) {
                            ImageViewerTitle(
                                uri = currentMedia?.uri?.toUri(),
                                name = currentMedia?.label ?: stringResource(R.string.image),
                                dateMillis = currentMedia?.takenTimestamp?.takeIf { it > 0 }
                                    ?: currentMedia?.timestamp?.takeIf { it > 0 }?.times(1000),
                                page = pagerState.currentPage,
                                pageCount = pagerState.pageCount
                            )
                            currentMedia?.expiryTimestamp?.takeIf { currentMedia.isTrashed && it > 0 }
                                ?.let { expiry ->
                                    Text(
                                        text = stringResource(
                                            R.string.gallery_trash_expires,
                                            DateUtils.formatDateTime(
                                                context,
                                                expiry * 1000,
                                                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
                                            )
                                        ),
                                        color = White,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                        }
                        var histogramPage by remember(pagerState) {
                            mutableIntStateOf(pagerState.settledPage)
                        }
                        LaunchedEffect(pagerState) {
                            snapshotFlow {
                                pagerState.settledPage.takeUnless { pagerState.isScrollInProgress }
                            }.collectLatest { page ->
                                if (page != null && page != histogramPage) {
                                    delay(180L)
                                    histogramPage = page
                                }
                            }
                        }
                        val histogramUri = media.getOrNull(histogramPage)?.uri
                        if (histogramUri != null && histogramUri !in imageErrorPages) {
                            Spacer(Modifier.width(16.dp))
                            HistogramChart(
                                model = histogramUri,
                                modifier = Modifier
                                    .height(50.dp)
                                    .width(90.dp)
                                    .graphicsLayer {
                                        compositingStrategy = CompositingStrategy.Offscreen
                                    },
                                bordersColor = MaterialTheme.colorScheme.primaryFixed
                                    .blend(White, 0.5f)
                            )
                        } else {
                            Spacer(Modifier.height(50.dp))
                        }
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