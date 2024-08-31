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

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.modalsheet.FullscreenPopup
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.BrokenImageVariant
import ru.tech.imageresizershrinker.core.ui.theme.White
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.utils.animation.PageCloseTransition
import ru.tech.imageresizershrinker.core.ui.utils.animation.PageOpenTransition
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.Media
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.MediaItem
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.MediaState
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.isHeaderKey

@Composable
fun MediaPickerGrid(
    state: MediaState,
    selectedMedia: SnapshotStateList<Media>,
    allowSelection: Boolean,
    isButtonVisible: Boolean,
    onRequestManagePermission: () -> Unit,
    isManagePermissionAllowed: Boolean
) {
    val scope = rememberCoroutineScope()
    val stringToday = stringResource(id = R.string.header_today)
    val stringYesterday = stringResource(id = R.string.header_yesterday)
    val gridState = rememberLazyGridState()
    val isCheckVisible = rememberSaveable { mutableStateOf(allowSelection) }
    val hapticFeedback = LocalHapticFeedback.current

    LaunchedEffect(state.media) {
        gridState.scrollToItem(0)
    }

    var imagePreviewUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val onMediaClick: (Media) -> Unit = {
        if (allowSelection) {
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
    LazyVerticalGrid(
        state = gridState,
        modifier = Modifier.fillMaxSize(),
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
                    if (allowSelection) {
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
                        modifier = Modifier.animateItem()
                    ) {
                        if (allowSelection) {
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
                }

                is MediaItem.MediaViewItem -> {
                    val selectionState = remember { mutableStateOf(true) }

                    MediaImage(
                        modifier = Modifier.animateItem(),
                        media = item.media,
                        selectionState = selectionState,
                        canClick = true,
                        onItemClick = {
                            hapticFeedback.performHapticFeedback(
                                HapticFeedbackType.TextHandleMove
                            )
                            onMediaClick(it)
                        },
                        isSelected = remember(item, selectedMedia) {
                            derivedStateOf {
                                selectedMedia.contains(item.media)
                            }
                        }.value,
                        onItemLongClick = {
                            imagePreviewUri = it.uri
                        }
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f))
                    .pointerInput(Unit) {
                        detectTapGestures { }
                    }
            ) {
                val imageErrorPages = remember {
                    mutableStateListOf<Int>()
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondViewportPageCount = 5,
                    pageSpacing = 16.dp
                ) { page ->
                    val media = state.media.getOrNull(page)
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Picture(
                            showTransparencyChecker = false,
                            model = media?.uri,
                            modifier = Modifier
                                .fillMaxSize()
                                .clipToBounds()
                                .zoomable(rememberZoomState(8f))
                                .systemBarsPadding()
                                .displayCutoutPadding(),
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
                val currentMedia = state.media.getOrNull(pagerState.currentPage)
                EnhancedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    type = EnhancedTopAppBarType.Center,
                    modifier = Modifier,
                    title = {
                        state.media.size.takeIf { it > 1 }?.let {
                            Text(
                                text = "${pagerState.currentPage + 1}/$it",
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                                    .padding(vertical = 4.dp, horizontal = 12.dp),
                                color = White
                            )
                        }
                    },
                    actions = {
                        val isImageError = imageErrorPages.contains(pagerState.currentPage)
                        AnimatedVisibility(
                            visible = state.media.isNotEmpty(),
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            MediaCheckBox(
                                modifier = Modifier.background(
                                    color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                                    shape = CircleShape
                                ),
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
                            Box(
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .size(48.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        imagePreviewUri = null
                                    },
                                contentAlignment = Alignment.Center
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
                currentMedia?.label?.let {
                    Text(
                        text = it,
                        modifier = Modifier
                            .animateContentSize()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .navigationBarsPadding()
                            .background(
                                color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = White
                    )
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