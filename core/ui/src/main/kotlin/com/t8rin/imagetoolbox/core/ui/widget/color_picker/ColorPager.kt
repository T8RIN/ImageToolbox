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

package com.t8rin.imagetoolbox.core.ui.widget.color_picker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.colors.parser.ColorWithName
import com.t8rin.colors.util.ColorUtil
import com.t8rin.colors.util.ColorUtil.hex
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.BookmarkRemove
import com.t8rin.imagetoolbox.core.resources.icons.ContentCopy
import com.t8rin.imagetoolbox.core.resources.icons.Info
import com.t8rin.imagetoolbox.core.resources.icons.Save
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.White
import com.t8rin.imagetoolbox.core.ui.theme.Yellow
import com.t8rin.imagetoolbox.core.ui.theme.onPrimaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.theme.onSecondaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.theme.onTertiaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.theme.primaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.theme.rememberCustomColorScheme
import com.t8rin.imagetoolbox.core.ui.theme.secondaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.theme.tertiaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.utils.helper.PredictiveBackObserver
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ColorCopyFormatSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ColorImageExportDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.toShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.modifier.withLayoutCorners
import com.t8rin.modalsheet.FullscreenPopup
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun ColorPager(
    visible: Boolean,
    colors: List<ColorWithName>,
    initialPage: Int,
    onDismiss: () -> Unit,
    favoriteColors: Set<String> = emptySet(),
    onToggleFavorite: ((ColorWithName) -> Unit)? = null
) {
    if (colors.isEmpty()) return

    val visibility = remember { MutableTransitionState(false) }
    visibility.targetState = visible

    val useOpaqueBackground = LocalSettingsState.current.useOpaqueImageViewerBackground

    FullscreenPopup {
        var predictiveBackProgress by remember { mutableFloatStateOf(0f) }
        val animatedPredictiveBackProgress by animateFloatAsState(predictiveBackProgress)
        val scale = (1f - animatedPredictiveBackProgress).coerceAtLeast(0.75f)

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

            val pagerState = rememberPagerState(
                initialPage = initialPage.coerceIn(colors.indices),
                pageCount = { colors.size }
            )
            val currentColor = colors[pagerState.currentPage.coerceIn(colors.indices)]
            var colorCopyTarget by remember { mutableStateOf<ColorWithName?>(null) }
            var showColorInfo by remember { mutableStateOf(false) }
            var showExportDialog by remember { mutableStateOf(false) }
            var exportTarget by remember { mutableStateOf(currentColor.color) }
            var hideControls by remember(animatedPredictiveBackProgress) { mutableStateOf(false) }

            LaunchedEffect(visible, initialPage, colors) {
                if (visible) {
                    pagerState.scrollToPage(initialPage.coerceIn(colors.indices))
                    draggableState.snapTo(true)
                    hideControls = false
                }
            }

            val progress by remember(draggableState) {
                derivedStateOf {
                    draggableState.progress(
                        from = false,
                        to = true
                    )
                }
            }
            val showControls by remember(draggableState, hideControls) {
                derivedStateOf {
                    draggableState.offset == 0f && !hideControls
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
                        if (useOpaqueBackground) Color.Black.copy(alpha = progress)
                        else MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f * progress)
                    )
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondViewportPageCount = 5,
                    pageSpacing = if (pagerState.pageCount > 1) 16.dp else 0.dp
                ) { page ->
                    val color = colors[page].color

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset {
                                IntOffset(
                                    x = 0,
                                    y = -draggableState.requireOffset().roundToInt()
                                )
                            }
                            .anchoredDraggable(
                                state = draggableState,
                                enabled = !pagerState.isScrollInProgress,
                                orientation = Orientation.Vertical,
                                reverseDirection = true,
                                flingBehavior = AnchoredDraggableDefaults.flingBehavior(
                                    animationSpec = tween(500),
                                    state = draggableState
                                )
                            )
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    hideControls = !hideControls
                                }
                            }
                            .then(
                                if (color.alpha < 1f) Modifier.transparencyChecker()
                                else Modifier
                            )
                            .background(color)
                    )
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
                            Row(
                                modifier = Modifier.height(32.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                EnhancedIconButton(
                                    onClick = { showColorInfo = true },
                                    modifier = Modifier.size(32.dp),
                                    shape = ShapeDefaults.smallStart,
                                    forceMinimumInteractiveComponentSize = false,
                                    containerColor = MaterialTheme.colorScheme.secondaryContainerFixed,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainerFixed
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                EnhancedIconButton(
                                    onClick = { colorCopyTarget = currentColor },
                                    modifier = Modifier.size(32.dp),
                                    shape = ShapeDefaults.smallEnd,
                                    forceMinimumInteractiveComponentSize = false,
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainerFixed,
                                    contentColor = MaterialTheme.colorScheme.onTertiaryContainerFixed
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ContentCopy,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                if (onToggleFavorite != null) {
                                    Spacer(Modifier.width(1.dp))
                                    val favoriteColorScheme = rememberCustomColorScheme(Yellow)

                                    EnhancedIconButton(
                                        onClick = { onToggleFavorite(currentColor) },
                                        modifier = Modifier.size(32.dp),
                                        shape = ShapeDefaults.small,
                                        forceMinimumInteractiveComponentSize = false,
                                        containerColor = favoriteColorScheme.primaryContainerFixed,
                                        contentColor = favoriteColorScheme.onPrimaryContainerFixed
                                    ) {
                                        Icon(
                                            imageVector = if (currentColor.name in favoriteColors) {
                                                Icons.Rounded.BookmarkRemove
                                            } else {
                                                Icons.Outlined.Bookmark
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        },
                        actions = {
                            EnhancedIconButton(
                                onClick = {
                                    exportTarget = currentColor.color
                                    showExportDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Save,
                                    contentDescription = null,
                                    tint = White
                                )
                            }
                        },
                        navigationIcon = {
                            EnhancedIconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowBack,
                                    contentDescription = null,
                                    tint = White
                                )
                            }
                        }
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
                            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
                            .navigationBarsPadding()
                            .padding(
                                WindowInsets.displayCutout
                                    .only(WindowInsetsSides.Horizontal)
                                    .asPaddingValues()
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = currentColor.name,
                                color = White,
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                modifier = Modifier.padding(top = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = currentColor.color.hex(),
                                    color = White.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp,
                                    maxLines = 1
                                )
                                if (pagerState.pageCount > 1) {
                                    Text(
                                        text = "${pagerState.currentPage + 1} / ${pagerState.pageCount}",
                                        color = White.copy(alpha = 0.55f),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontSize = 11.sp,
                                        lineHeight = 14.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        val colorInfo = remember(currentColor.color) {
                            val rgb = ColorUtil.colorToRGBArray(currentColor.color)
                            val hsl = ColorUtil.colorToHSL(currentColor.color)
                            "RGB  ${rgb.joinToString(", ")}\n" +
                                    "HSL  ${hsl[0].roundToInt()}°, " +
                                    "${(hsl[1] * 100).roundToInt()}%, " +
                                    "${(hsl[2] * 100).roundToInt()}%"
                        }
                        Text(
                            text = colorInfo,
                            color = White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }

            PredictiveBackObserver(
                onProgress = { predictiveBackProgress = it / 6f },
                onClean = { isCompleted ->
                    if (isCompleted) {
                        onDismiss()
                        delay(400)
                    }
                    predictiveBackProgress = 0f
                },
                enabled = visible && colorCopyTarget == null &&
                        !showColorInfo && !showExportDialog
            )

            ColorInfoSheet(
                visible = showColorInfo,
                color = currentColor,
                onDismiss = { showColorInfo = false }
            )

            ColorCopyFormatSelectionDialog(
                target = colorCopyTarget,
                onDismiss = { colorCopyTarget = null }
            )

            ColorImageExportDialog(
                visible = showExportDialog,
                onDismiss = { showExportDialog = false },
                color = exportTarget
            )
        }
    }
}