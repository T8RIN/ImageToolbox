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

package ru.tech.imageresizershrinker.core.ui.widget.image

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.t8rin.histogram.ImageHistogram
import com.t8rin.modalsheet.FullscreenPopup
import kotlinx.coroutines.delay
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.toggleScale
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.BrokenImageAlt
import ru.tech.imageresizershrinker.core.resources.icons.EditAlt
import ru.tech.imageresizershrinker.core.ui.theme.White
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.rememberFilename
import ru.tech.imageresizershrinker.core.ui.utils.helper.PredictiveBackObserver
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalScreenSize
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBarDefaults
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.core.ui.widget.modifier.toShape
import ru.tech.imageresizershrinker.core.ui.widget.modifier.withLayoutCorners
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import kotlin.math.roundToInt


@Composable
fun ImagePager(
    visible: Boolean,
    selectedUri: Uri?,
    uris: List<Uri>?,
    onNavigate: (Screen) -> Unit,
    onUriSelected: (Uri?) -> Unit,
    onShare: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    FullscreenPopup {
        var predictiveBackProgress by remember {
            mutableFloatStateOf(0f)
        }
        val animatedPredictiveBackProgress by animateFloatAsState(predictiveBackProgress)
        val scale = (1f - animatedPredictiveBackProgress * 1.5f).coerceAtLeast(0.75f)

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

            var wantToEdit by rememberSaveable {
                mutableStateOf(false)
            }
            val pagerState = rememberPagerState(
                initialPage = selectedUri?.let {
                    uris?.indexOf(it)
                }?.takeIf { it >= 0 } ?: 0,
                pageCount = {
                    uris?.size ?: 0
                }
            )
            LaunchedEffect(pagerState.currentPage) {
                onUriSelected(
                    uris?.getOrNull(pagerState.currentPage)
                )
            }
            val progress = draggableState.progress(
                from = false,
                to = true
            )
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
                val moreThanOneUri = (uris?.size ?: 0) > 1

                val histogram: @Composable () -> Unit = {
                    ImageHistogram(
                        imageUri = uris?.getOrNull(pagerState.currentPage) ?: Uri.EMPTY,
                        modifier = Modifier
                            .height(50.dp)
                            .width(90.dp),
                        bordersColor = Color.White
                    )
                }
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
                            model = uris?.getOrNull(page),
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
                AnimatedVisibility(
                    visible = draggableState.offset == 0f && !hideControls,
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
                            uris?.size?.takeIf { it > 1 }?.let {
                                Text(
                                    text = "${pagerState.currentPage + 1}/$it",
                                    modifier = Modifier
                                        .padding(vertical = 4.dp, horizontal = 12.dp),
                                    color = White
                                )
                            } ?: histogram()
                        },
                        actions = {
                            AnimatedVisibility(
                                visible = !uris.isNullOrEmpty(),
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    EnhancedIconButton(
                                        onClick = {
                                            selectedUri?.let { onShare(it) }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Share,
                                            contentDescription = stringResource(R.string.share),
                                            tint = White
                                        )
                                    }
                                    EnhancedIconButton(
                                        onClick = {
                                            wantToEdit = true
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.EditAlt,
                                            contentDescription = stringResource(R.string.edit),
                                            tint = White
                                        )
                                    }
                                }
                            }
                        },
                        navigationIcon = {
                            AnimatedVisibility(!uris.isNullOrEmpty()) {
                                EnhancedIconButton(
                                    onClick = {
                                        onDismiss()
                                        onUriSelected(null)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = stringResource(R.string.exit),
                                        tint = White
                                    )
                                }
                            }
                        }
                    )
                }

                val selectedUriFilename = selectedUri?.let { rememberFilename(it) }
                val showBottomHist = pagerState.currentPage !in imageErrorPages && moreThanOneUri

                AnimatedVisibility(
                    visible = draggableState.offset == 0f && !selectedUriFilename.isNullOrEmpty() && (!moreThanOneUri || !showBottomHist) && !hideControls,
                    modifier = Modifier.fillMaxWidth(),
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        selectedUriFilename?.let {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .animateContentSize()
                                    .padding(top = 64.dp)
                                    .align(Alignment.TopCenter)
                                    .padding(8.dp)
                                    .statusBarsPadding()
                                    .background(
                                        color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                color = White,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = draggableState.offset == 0f && showBottomHist && !hideControls,
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
                        selectedUriFilename?.let {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .animateContentSize()
                                    .weight(1f),
                                color = White,
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Spacer(Modifier.width(16.dp))
                        }
                        histogram()
                    }
                }
            }

            ProcessImagesPreferenceSheet(
                uris = listOfNotNull(selectedUri),
                visible = wantToEdit,
                onDismiss = {
                    wantToEdit = false
                },
                onNavigate = onNavigate
            )

            PredictiveBackObserver(
                onProgress = {
                    predictiveBackProgress = it
                },
                onClean = { isCompleted ->
                    if (isCompleted) {
                        onDismiss()
                        onUriSelected(null)
                        delay(400)
                    }
                    predictiveBackProgress = 0f
                },
                enabled = visible
            )
        }
    }
}