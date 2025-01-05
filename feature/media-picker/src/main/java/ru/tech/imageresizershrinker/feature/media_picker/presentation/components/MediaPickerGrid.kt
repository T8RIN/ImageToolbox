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
package ru.tech.imageresizershrinker.feature.media_picker.presentation.components

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.t8rin.histogram.ImageHistogram
import com.t8rin.modalsheet.FullscreenPopup
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.toggleScale
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.BrokenImageVariant
import ru.tech.imageresizershrinker.core.ui.theme.White
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.utils.animation.PageCloseTransition
import ru.tech.imageresizershrinker.core.ui.utils.animation.PageOpenTransition
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.dragHandler
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.Media
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.MediaItem
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.MediaState
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.isHeaderKey
import kotlin.math.roundToInt

@Composable
internal fun MediaPickerGrid(
    state: MediaState,
    isSelectionOfAll: Boolean,
    selectedMedia: SnapshotStateList<Media>,
    allowMultiple: Boolean,
    isButtonVisible: Boolean,
    onRequestManagePermission: () -> Unit,
    isManagePermissionAllowed: Boolean
) {
    val scope = rememberCoroutineScope()
    val stringToday = stringResource(id = R.string.header_today)
    val stringYesterday = stringResource(id = R.string.header_yesterday)
    val gridState = rememberLazyGridState()
    val isCheckVisible = rememberSaveable { mutableStateOf(allowMultiple) }
    val hapticFeedback = LocalHapticFeedback.current

    LaunchedEffect(state.media) {
        gridState.scrollToItem(0)
    }

    var imagePreviewUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val onMediaClick: (Media) -> Unit = {
        if (allowMultiple) {
            if (selectedMedia.contains(it)) selectedMedia.remove(it)
            else selectedMedia.add(it)
        } else {
            if (selectedMedia.contains(it)) selectedMedia.remove(it)
            else {
                if (selectedMedia.isNotEmpty()) selectedMedia[0] = it
                else selectedMedia.add(it)
            }
        }
    }

    val layoutDirection = LocalLayoutDirection.current
    val autoScrollSpeed: MutableState<Float> = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(autoScrollSpeed.value) {
        if (autoScrollSpeed.value != 0f) {
            while (isActive) {
                gridState.scrollBy(autoScrollSpeed.value)
                delay(10)
            }
        }
    }
    val privateSelection = remember {
        mutableStateOf(emptySet<Int>())
    }

    LaunchedEffect(state.mappedMedia, isSelectionOfAll, selectedMedia.size) {
        if (isSelectionOfAll) {
            privateSelection.value = state.mappedMedia.mapIndexedNotNull { index, item ->
                if (item is MediaItem.MediaViewItem && item.media in selectedMedia) {
                    index
                } else null
            }.toSet()
        }
    }

    LaunchedEffect(selectedMedia.size) {
        if (selectedMedia.isEmpty() && isSelectionOfAll) {
            privateSelection.value = emptySet()
        }
    }

    LazyVerticalGrid(
        state = gridState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .dragHandler(
                enabled = isSelectionOfAll && allowMultiple,
                key = state.mappedMedia,
                lazyGridState = gridState,
                isVertical = true,
                haptics = LocalHapticFeedback.current,
                selectedItems = privateSelection,
                onSelectionChange = {
                    val data = state.mappedMedia.mapIndexedNotNull { index, mediaItem ->
                        if (index in it && mediaItem is MediaItem.MediaViewItem) mediaItem.media
                        else null
                    }
                    selectedMedia.clear()
                    selectedMedia.addAll(data)
                },
                autoScrollSpeed = autoScrollSpeed,
                autoScrollThreshold = with(LocalDensity.current) { 40.dp.toPx() },
                onLongTap = {
                    if (selectedMedia.isEmpty()) {
                        imagePreviewUri =
                            (state.mappedMedia[it + 1] as? MediaItem.MediaViewItem)?.media?.uri
                    }
                },
                shouldHandleLongTap = selectedMedia.isNotEmpty()
            ),
        columns = GridCells.Adaptive(100.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        contentPadding = PaddingValues(
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().plus(
                if (isButtonVisible) 80.dp
                else 0.dp
            ).plus(
                if (selectedMedia.isNotEmpty()) 52.dp
                else 0.dp
            ),
            start = WindowInsets.displayCutout.asPaddingValues()
                .calculateStartPadding(layoutDirection),
            end = WindowInsets.displayCutout.asPaddingValues().calculateEndPadding(layoutDirection)
        )
    ) {
        if (!isManagePermissionAllowed) {
            item(
                span = {
                    GridItemSpan(maxLineSpan)
                }
            ) {
                Row(
                    modifier = Modifier
                        .container(
                            color = MaterialTheme.colorScheme.errorContainer,
                            resultPadding = 0.dp,
                            shape = RectangleShape
                        )
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = 16.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.WarningAmber,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.manage_storage_extra_types),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = 18.sp,
                                modifier = Modifier.weight(1f, false)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.manage_storage_extra_types_sub),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    EnhancedButton(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                        onClick = onRequestManagePermission
                    ) {
                        Text(text = stringResource(R.string.request))
                    }
                }
            }
        }
        items(
            items = state.mappedMedia,
            key = {
                val first = if (it is MediaItem.MediaViewItem) it.media.toString() else it.key
                "$first-${state.mappedMedia.indexOf(it)}"
            },
            contentType = { it.key.startsWith("media_") },
            span = { item ->
                GridItemSpan(if (item.key.isHeaderKey) maxLineSpan else 1)
            }
        ) { item ->
            when (item) {
                is MediaItem.Header -> {
                    val isChecked = rememberSaveable { mutableStateOf(false) }
                    if (allowMultiple) {
                        LaunchedEffect(selectedMedia.size) {
                            // Partial check of media items should not check the header
                            isChecked.value = selectedMedia.containsAll(item.data)
                        }
                    }
                    val title = item.text
                        .replace("Today", stringToday)
                        .replace("Yesterday", stringYesterday)
                    MediaStickyHeader(
                        date = title,
                        showAsBig = item.key.contains("big"),
                        isCheckVisible = isCheckVisible,
                        isChecked = isChecked,
                        onChecked = {
                            if (allowMultiple) {
                                hapticFeedback.performHapticFeedback(
                                    HapticFeedbackType.LongPress
                                )
                                scope.launch {
                                    isChecked.value = !isChecked.value
                                    if (isChecked.value) {
                                        val toAdd = item.data.toMutableList().apply {
                                            // Avoid media from being added twice to selection
                                            removeIf { selectedMedia.contains(it) }
                                        }
                                        selectedMedia.addAll(toAdd)
                                    } else selectedMedia.removeAll(item.data)
                                }
                            }
                        }
                    )
                }

                is MediaItem.MediaViewItem -> {
                    MediaImage(
                        media = item.media,
                        canClick = !isSelectionOfAll || !allowMultiple,
                        onItemClick = {
                            hapticFeedback.performHapticFeedback(
                                HapticFeedbackType.TextHandleMove
                            )
                            onMediaClick(it)
                        },
                        onItemLongClick = {
                            imagePreviewUri = it.uri
                        },
                        selectionIndex = remember(selectedMedia, item.media) {
                            derivedStateOf {
                                if (selectedMedia.size > 1) {
                                    selectedMedia.indexOf(item.media)
                                } else -1
                            }
                        }.value,
                        isSelected = remember(item, selectedMedia) {
                            derivedStateOf {
                                selectedMedia.contains(item.media)
                            }
                        }.value
                    )
                }
            }
        }
    }

    FullscreenPopup {
        AnimatedVisibility(
            visible = imagePreviewUri != null,
            modifier = Modifier.fillMaxSize(),
            enter = PageOpenTransition,
            exit = PageCloseTransition
        ) {
            val density = LocalDensity.current
            val screenHeight =
                LocalConfiguration.current.screenHeightDp.dp + WindowInsets.systemBars.asPaddingValues()
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
                    imagePreviewUri = null
                    delay(600)
                    draggableState.snapTo(true)
                }
            }

            val initialPage by remember(imagePreviewUri, state.media) {
                derivedStateOf {
                    imagePreviewUri?.let {
                        state.media.indexOfFirst { it.uri == imagePreviewUri }
                    }?.takeIf { it >= 0 } ?: 0
                }
            }
            val pagerState = rememberPagerState(
                initialPage = initialPage,
                pageCount = {
                    state.media.size
                }
            )
            val progress = draggableState.progress(
                from = false,
                to = true
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f * progress)
                    )
            ) {
                val moreThanOneUri = state.media.size > 1
                val currentMedia = state.media.getOrNull(pagerState.currentPage)
                val histogram: @Composable () -> Unit = {
                    ImageHistogram(
                        imageUri = currentMedia?.uri?.toUri() ?: Uri.EMPTY,
                        modifier = Modifier
                            .height(50.dp)
                            .width(90.dp),
                        bordersColor = Color.White
                    )
                }
                val imageErrorPages = remember {
                    mutableStateListOf<Int>()
                }
                var hideControls by remember {
                    mutableStateOf(false)
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondViewportPageCount = 5,
                    pageSpacing = if (pagerState.pageCount > 1) 16.dp
                    else 0.dp
                ) { page ->
                    val media = state.media.getOrNull(page)
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val zoomState = rememberZoomState(10f)
                        Picture(
                            showTransparencyChecker = false,
                            model = media?.uri,
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
                                        imageVector = Icons.Rounded.BrokenImageVariant,
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
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)
                        ),
                        type = EnhancedTopAppBarType.Center,
                        modifier = Modifier,
                        title = {
                            state.media.size.takeIf { it > 1 }?.let {
                                Text(
                                    text = "${pagerState.currentPage + 1}/$it",
                                    modifier = Modifier
                                        .padding(vertical = 4.dp, horizontal = 12.dp),
                                    color = White
                                )
                            } ?: histogram()
                        },
                        actions = {
                            val isImageError = imageErrorPages.contains(pagerState.currentPage)
                            AnimatedVisibility(
                                visible = state.media.isNotEmpty(),
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                MediaCheckBox(
                                    isChecked = selectedMedia.contains(currentMedia),
                                    onCheck = {
                                        currentMedia?.let(onMediaClick)
                                    },
                                    uncheckedColor = White,
                                    checkedColor = if (isImageError) {
                                        MaterialTheme.colorScheme.error
                                    } else MaterialTheme.colorScheme.primary,
                                    checkedIcon = if (isImageError) {
                                        Icons.Filled.Error
                                    } else Icons.Filled.CheckCircle
                                )
                            }
                        },
                        navigationIcon = {
                            AnimatedVisibility(state.media.isNotEmpty()) {
                                EnhancedIconButton(
                                    onClick = {
                                        imagePreviewUri = null
                                    }
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

                val showBottomHist = pagerState.currentPage !in imageErrorPages && moreThanOneUri

                AnimatedVisibility(
                    visible = draggableState.offset == 0f && !currentMedia?.label.isNullOrEmpty() && (!moreThanOneUri || !showBottomHist) && !hideControls,
                    modifier = Modifier.fillMaxWidth(),
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        currentMedia?.label?.let {
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
                        currentMedia?.label?.let {
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

            if (imagePreviewUri != null) {
                BackHandler {
                    imagePreviewUri = null
                }
            }
        }
    }
}