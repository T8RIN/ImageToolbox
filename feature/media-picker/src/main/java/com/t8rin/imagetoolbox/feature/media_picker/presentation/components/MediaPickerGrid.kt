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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.icons.Circle
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.modifier.dragHandler
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaAction
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaItem
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaState
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.isHeaderKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
internal fun BoxScope.MediaPickerGrid(
    albumId: Long,
    state: MediaState,
    gridState: LazyGridState,
    isSelectionOfAll: Boolean,
    selectedMedia: SnapshotStateList<Media>,
    allowMultiple: Boolean,
    isGalleryMode: Boolean,
    isButtonVisible: Boolean,
    useDateTaken: Boolean,
    onRequestManagePermission: () -> Unit,
    isManagePermissionAllowed: Boolean,
    onMediaAction: ((MediaAction, Media) -> Unit)? = null,
    bottomContentPadding: Dp = 0.dp
) {
    val settingsState = LocalSettingsState.current
    val useGalleryStyleSelection = isGalleryMode || settingsState.useGalleryStyleSelection
    val previewOnTap = useGalleryStyleSelection && selectedMedia.isEmpty()
    val hapticFeedback = LocalHapticFeedback.current

    var imagePreviewUri by rememberSaveable(albumId) {
        mutableStateOf<String?>(null)
    }

    val onMediaClick: (Media) -> Unit = {
        val index = selectedMedia.indexOfFirst { selected -> selected.uri == it.uri }
        if (index >= 0) {
            selectedMedia.removeAt(index)
        } else if (allowMultiple || selectedMedia.isEmpty()) {
            selectedMedia.add(it)
        } else {
            selectedMedia[0] = it
        }
    }

    val layoutDirection = LocalLayoutDirection.current
    val selection = selectedMedia.toList()
    val selectionIndices = remember(selection) {
        buildMap {
            selection.forEachIndexed { index, media ->
                putIfAbsent(media.uri, index)
            }
        }
    }
    val privateSelection = remember {
        mutableStateOf(emptySet<Int>())
    }

    LaunchedEffect(state.mappedMedia, isSelectionOfAll, selectionIndices) {
        if (isSelectionOfAll) {
            privateSelection.value = if (selectionIndices.isEmpty()) emptySet() else {
                withContext(Dispatchers.Default) {
                    buildSet {
                        state.mappedMedia.forEachIndexed { index, item ->
                            if (item is MediaItem.MediaViewItem && item.media.uri in selectionIndices) {
                                add(index)
                            }
                        }
                    }
                }
            }
        }
    }

    val cutout = WindowInsets.displayCutout.asPaddingValues()
    val navBar = WindowInsets.navigationBars.asPaddingValues()
    val bottomPadding = maxOf(
        (if (isButtonVisible) 80.dp else 0.dp) +
                (if (selectedMedia.isNotEmpty()) 52.dp else 0.dp),
        bottomContentPadding
    ) + navBar.calculateBottomPadding()

    var columns by remember {
        mutableIntStateOf(settingsState.galleryColumns)
    }
    LaunchedEffect(settingsState.galleryColumns) {
        columns = settingsState.galleryColumns
    }
    val hasHeaders = remember(state.mappedMedia) {
        state.mappedMedia.any { it is MediaItem.Header }
    }
    val adaptiveCellSize = 100.dp
    val spacing = 1.dp
    val density = LocalDensity.current
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val gridWidth = constraints.maxWidth - with(density) {
            (cutout.calculateStartPadding(layoutDirection) +
                    cutout.calculateEndPadding(layoutDirection)).roundToPx()
        }
        val spacingPx = with(density) { spacing.roundToPx() }
        val columnCount = if (columns > 0) columns else with(density) {
            ((gridWidth + spacingPx) / (adaptiveCellSize.roundToPx() + spacingPx)).coerceAtLeast(1)
        }
        val animatedColumns by animateFloatAsState(
            targetValue = columnCount.toFloat(),
            animationSpec = tween(180)
        )
        val isZooming = animatedColumns != columnCount.toFloat()
        val cellSize = with(density) {
            ((gridWidth + spacingPx) / animatedColumns - spacingPx).coerceAtLeast(0f).toDp()
        }
        val targetCellSize = with(density) {
            ((gridWidth - spacingPx * (columnCount - 1)).coerceAtLeast(0) / columnCount).toDp()
        }
        val thumbnailSize = with(density) {
            val pixels = targetCellSize.roundToPx().coerceAtLeast(1)
            val step = if (pixels <= 256) 32 else 128
            ((pixels + step - 1) / step * step).coerceAtMost(4096)
        }
        val currentColumns by rememberUpdatedState(animatedColumns)
        var pinchAnchor by remember { mutableStateOf<MediaGridAnchor?>(null) }
        var isPinching by remember { mutableStateOf(false) }
        LaunchedEffect(animatedColumns, pinchAnchor, isPinching) {
            pinchAnchor?.let { anchor ->
                gridState.requestScrollToItem(
                    anchor.index,
                    if (anchor.isHeader) anchor.offset else
                        (anchor.offset * anchor.columns / animatedColumns).roundToInt()
                )
                if (!isPinching && !isZooming) pinchAnchor = null
            }
        }
        val uncheckedIcon = rememberVectorPainter(Icons.Outlined.Circle)
        val headerModifier = Modifier
            .wrapContentWidth(Alignment.Start)
            .requiredWidth(with(density) { gridWidth.toDp() })

        LazyVerticalGrid(
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .clipToBounds()
                .padding(
                    start = cutout.calculateStartPadding(layoutDirection),
                    end = cutout.calculateEndPadding(layoutDirection)
                )
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                        val initialColumns = columns
                        pinchAnchor = null
                        var startColumns = currentColumns
                        var zoomColumns = startColumns
                        try {
                            do {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                if (event.changes.count { it.pressed } > 1) {
                                    if (!isPinching) {
                                        startColumns = currentColumns
                                        zoomColumns = startColumns
                                        val index = gridState.firstVisibleItemIndex
                                        pinchAnchor = MediaGridAnchor(
                                            index = index,
                                            offset = gridState.firstVisibleItemScrollOffset,
                                            columns = currentColumns,
                                            isHeader = gridState.layoutInfo.visibleItemsInfo
                                                .firstOrNull { it.index == index }?.key?.isHeaderKey == true
                                        )
                                        isPinching = true
                                    }
                                    zoomColumns = (zoomColumns / event.calculateZoom())
                                        .coerceIn(1f, SettingsState.MaxGalleryColumns.toFloat())
                                    val newColumns = zoomColumns.roundToInt()
                                    columns = if (
                                        initialColumns == 0 && newColumns == startColumns.roundToInt()
                                    ) 0 else newColumns
                                }
                                if (isPinching) event.changes.forEach { it.consume() }
                            } while (event.changes.any { it.pressed })
                        } finally {
                            isPinching = false
                        }
                    }
                }
                .dragHandler(
                    enabled = isSelectionOfAll && allowMultiple && !previewOnTap,
                    key = state.mappedMedia,
                    itemIndex = { item ->
                        (item.index - if (isManagePermissionAllowed) 0 else 1)
                            .takeIf { it >= 0 && !item.key.isHeaderKey }
                    },
                    lazyGridState = gridState,
                    isVertical = true,
                    selectedItems = privateSelection,
                    onSelectionChange = { indices ->
                        val media = indices.mapNotNull {
                            (state.mappedMedia.getOrNull(it) as? MediaItem.MediaViewItem)?.media
                        }
                        selectedMedia.clear()
                        selectedMedia.addAll(media)
                    },
                    onLongTap = {
                        if (selectedMedia.isEmpty()) {
                            imagePreviewUri =
                                (state.mappedMedia[it + 1] as? MediaItem.MediaViewItem)?.media?.uri
                        }
                    },
                    shouldHandleLongTap = selectedMedia.isNotEmpty()
                ),
            columns = MediaGridCells(animatedColumns),
            horizontalArrangement = MediaGridArrangement,
            verticalArrangement = Arrangement.spacedBy(spacing),
            contentPadding = PaddingValues(bottom = bottomPadding),
            flingBehavior = enhancedFlingBehavior()
        ) {
            if (!isManagePermissionAllowed) {
                item(
                    span = {
                        GridItemSpan(maxLineSpan)
                    }
                ) {
                    Box(headerModifier) {
                        ManageExternalStorageWarning(onRequestManagePermission)
                    }
                }
            }
            items(
                items = state.mappedMedia,
                key = MediaItem::key,
                contentType = { it is MediaItem.MediaViewItem },
                span = if (hasHeaders) {
                    { item -> GridItemSpan(if (item is MediaItem.Header) maxLineSpan else 1) }
                } else null
            ) { item ->
                when (item) {
                    is MediaItem.Header -> {
                        val headerScale = 0.75f + 0.25f *
                                (cellSize / adaptiveCellSize).coerceIn(0f, 1f)
                        val isChecked = allowMultiple && remember(item.data, selectionIndices) {
                            item.data.all { it.uri in selectionIndices }
                        }
                        MediaStickyHeader(
                            modifier = headerModifier.animateItem(
                                fadeInSpec = null,
                                placementSpec = if (isZooming) null else tween(180),
                                fadeOutSpec = null
                            ),
                            date = item.text,
                            scale = headerScale,
                            isChecked = isChecked,
                            onChecked = if (
                                allowMultiple && (!isGalleryMode || selectedMedia.isNotEmpty())
                            ) {
                                {
                                    hapticFeedback.longPress()
                                    if (isChecked) {
                                        val uris = item.data.mapTo(hashSetOf(), Media::uri)
                                        selectedMedia.removeAll { it.uri in uris }
                                    } else {
                                        selectedMedia.addAll(item.data.filterNot { it.uri in selectionIndices })
                                    }
                                }
                            } else null
                        )
                    }

                    is MediaItem.MediaViewItem -> {
                        val selectionIndex = selectionIndices[item.media.uri] ?: -1

                        MediaImage(
                            modifier = Modifier
                                .animateItem(
                                    fadeInSpec = null,
                                    placementSpec = if (isZooming) null else tween(180),
                                    fadeOutSpec = null
                                ),
                            media = item.media,
                            cellSize = cellSize,
                            thumbnailSize = thumbnailSize,
                            uncheckedIcon = uncheckedIcon,
                            isInSelection = !isGalleryMode || selectionIndex >= 0,
                            canClick = previewOnTap || !isSelectionOfAll || !allowMultiple,
                            onItemClick = {
                                if (previewOnTap) {
                                    imagePreviewUri = it.uri
                                } else {
                                    hapticFeedback.longPress()
                                    onMediaClick(it)
                                }
                            },
                            onItemLongClick = {
                                if (useGalleryStyleSelection) {
                                    onMediaClick(it)
                                } else {
                                    imagePreviewUri = it.uri
                                }
                            },
                            selectionIndex = if (selectedMedia.size > 1) selectionIndex else -1,
                            isSelected = selectionIndex >= 0,
                            showFileSize = settingsState.showGalleryFileSize,
                            showFileExtension = settingsState.showGalleryFileExtension
                        )
                    }
                }
            }
        }

        MediaGridFastScroller(
            state = gridState,
            media = state.mappedMedia,
            firstMediaIndex = if (isManagePermissionAllowed) 0 else 1,
            useDateTaken = useDateTaken,
            rightPadding = cutout.calculateRightPadding(layoutDirection),
            bottomPadding = bottomPadding
        )

        MediaImagePager(
            imagePreviewUri = imagePreviewUri,
            onDismiss = { imagePreviewUri = null },
            media = state.media,
            selectedMedia = selectedMedia,
            onMediaClick = onMediaClick,
            onMediaAction = onMediaAction
        )
    }
}

private data class MediaGridCells(val columns: Float) : GridCells {
    override fun Density.calculateCrossAxisCellSizes(availableSize: Int, spacing: Int): List<Int> {
        val count = ceil(columns).toInt()
        val size = ((availableSize + spacing) / columns - spacing).coerceAtLeast(0f)
        val baseSize = size.toInt()
        val remainder = ((size - baseSize) * count).roundToInt()
        return List(count) { baseSize + if (it < remainder) 1 else 0 }
    }
}

private object MediaGridArrangement : Arrangement.Horizontal {
    override val spacing = 1.dp

    override fun Density.arrange(
        totalSize: Int,
        sizes: IntArray,
        layoutDirection: LayoutDirection,
        outPositions: IntArray
    ) {
        var position = 0
        val gap = spacing.roundToPx()
        sizes.forEachIndexed { index, size ->
            outPositions[index] = if (layoutDirection == LayoutDirection.Ltr) position
            else totalSize - position - size
            position += size + gap
        }
    }
}

private data class MediaGridAnchor(
    val index: Int,
    val offset: Int,
    val columns: Float,
    val isHeader: Boolean
)