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

package com.t8rin.imagetoolbox.core.ui.widget.image

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFrames
import com.t8rin.imagetoolbox.core.resources.icons.BrokenImageAlt
import com.t8rin.imagetoolbox.core.ui.theme.White
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.rememberFileExtension
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberHumanFileSize
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.advancedShadow
import com.t8rin.imagetoolbox.core.ui.widget.modifier.dragHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun ImagesPreviewWithSelection(
    imageUris: List<String>,
    imageFrames: ImageFrames,
    onFrameSelectionChange: (ImageFrames) -> Unit,
    isPortrait: Boolean,
    isLoadingImages: Boolean,
    spacing: Dp = 8.dp,
    onError: (String) -> Unit = {},
    isAutoExpandLayout: Boolean = true,
    verticalCellSize: Dp = 90.dp,
    horizontalCellSize: Dp = 120.dp,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    isSelectionMode: Boolean = true,
    isAboveImageScrimEnabled: Boolean = true,
    onItemClick: (Int) -> Unit = {},
    onItemLongClick: (Int) -> Unit = {},
    aboveImageContent: @Composable BoxScope.(index: Int) -> Unit = { index ->
        Text(
            text = (index + 1).toString(),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )
    },
    showExtension: Boolean = true,
    endAdditionalItem: (@Composable LazyGridItemScope.() -> Unit)? = null,
    isContentAlignToCenter: Boolean = true,
    contentScale: ContentScale = ContentScale.Crop,
    modifier: Modifier? = null
) {
    val state = rememberLazyGridState()
    val autoScrollSpeed: MutableState<Float> = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(autoScrollSpeed.value) {
        if (autoScrollSpeed.value != 0f) {
            while (isActive) {
                state.scrollBy(autoScrollSpeed.value)
                delay(10)
            }
        }
    }
    val getUris: () -> Set<Int> = {
        val indexes = imageFrames
            .getFramePositions(imageUris.size)
            .map { it - 1 }
        imageUris.mapIndexedNotNull { index, _ ->
            if (index in indexes) index + 1
            else null
        }.toSet()
    }
    val selectedItems by remember(imageUris, imageFrames) {
        mutableStateOf(getUris())
    }
    val privateSelectedItems = remember {
        mutableStateOf(selectedItems)
    }

    LaunchedEffect(imageFrames, selectedItems) {
        if (imageFrames !is ImageFrames.ManualSelection || selectedItems.isEmpty()) {
            privateSelectedItems.value = selectedItems
        }
    }

    val screenWidth = LocalScreenSize.current.width
    val gridModifier = modifier ?: if (isPortrait) {
        Modifier.height(
            (130.dp * imageUris.size).coerceAtMost(420.dp)
        )
    } else {
        Modifier
    }.then(
        if (isAutoExpandLayout) {
            Modifier.layout { measurable, constraints ->
                val result =
                    measurable.measure(
                        if (isPortrait) {
                            constraints.copy(
                                maxWidth = screenWidth.roundToPx()
                            )
                        } else {
                            constraints.copy(
                                maxHeight = constraints.maxHeight + 48.dp.roundToPx()
                            )
                        }
                    )
                layout(result.measuredWidth, result.measuredHeight) {
                    result.place(0, 0)
                }
            }
        } else Modifier
    )

    Box(modifier = gridModifier) {
        if (isPortrait) {
            LazyHorizontalGrid(
                rows = GridCells.Adaptive(horizontalCellSize),
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .dragHandler(
                        key = null,
                        lazyGridState = state,
                        isVertical = false,
                        haptics = LocalHapticFeedback.current,
                        selectedItems = privateSelectedItems,
                        onSelectionChange = {
                            onFrameSelectionChange(ImageFrames.ManualSelection(it.toList()))
                        },
                        autoScrollSpeed = autoScrollSpeed,
                        autoScrollThreshold = with(LocalDensity.current) { 40.dp.toPx() },
                        tapEnabled = isSelectionMode,
                        onTap = onItemClick,
                        onLongTap = onItemLongClick
                    ),
                verticalArrangement = Arrangement.spacedBy(
                    space = spacing,
                    alignment = if (isContentAlignToCenter) Alignment.CenterVertically
                    else Alignment.Top
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    space = spacing,
                    alignment = if (isContentAlignToCenter) Alignment.CenterHorizontally
                    else Alignment.Start
                ),
                contentPadding = contentPadding,
                flingBehavior = enhancedFlingBehavior()
            ) {
                itemsIndexed(
                    items = imageUris,
                    key = { index, uri -> "$uri-${index + 1}" }
                ) { index, uri ->
                    val selected by remember(index, privateSelectedItems.value) {
                        derivedStateOf {
                            index + 1 in privateSelectedItems.value
                        }
                    }
                    ImageItem(
                        selected = selected,
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(1f),
                        index = index,
                        uri = uri,
                        onError = onError,
                        isAboveImageScrimEnabled = isAboveImageScrimEnabled,
                        isSelectionMode = isSelectionMode,
                        aboveImageContent = aboveImageContent,
                        showExtension = showExtension,
                        contentScale = contentScale
                    )
                }
                endAdditionalItem?.let {
                    item {
                        endAdditionalItem()
                    }
                }
                item {
                    AnimatedVisibility(isLoadingImages) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            EnhancedLoadingIndicator()
                        }
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(verticalCellSize),
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .dragHandler(
                        key = null,
                        lazyGridState = state,
                        isVertical = true,
                        haptics = LocalHapticFeedback.current,
                        selectedItems = privateSelectedItems,
                        onSelectionChange = {
                            onFrameSelectionChange(ImageFrames.ManualSelection(it.toList()))
                        },
                        autoScrollSpeed = autoScrollSpeed,
                        autoScrollThreshold = with(LocalDensity.current) { 40.dp.toPx() },
                        tapEnabled = isSelectionMode,
                        onTap = onItemClick,
                        onLongTap = onItemLongClick
                    ),
                verticalArrangement = Arrangement.spacedBy(
                    space = spacing,
                    alignment = if (isContentAlignToCenter) Alignment.CenterVertically
                    else Alignment.Top
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    space = spacing,
                    alignment = if (isContentAlignToCenter) Alignment.CenterHorizontally
                    else Alignment.Start
                ),
                contentPadding = contentPadding,
                flingBehavior = enhancedFlingBehavior()
            ) {
                itemsIndexed(
                    items = imageUris,
                    key = { index, uri -> "$uri-${index + 1}" }
                ) { index, uri ->
                    val selected by remember(index, privateSelectedItems.value) {
                        derivedStateOf {
                            index + 1 in privateSelectedItems.value
                        }
                    }
                    ImageItem(
                        selected = selected,
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(1f),
                        index = index,
                        uri = uri,
                        onError = onError,
                        aboveImageContent = aboveImageContent,
                        isSelectionMode = isSelectionMode,
                        isAboveImageScrimEnabled = isAboveImageScrimEnabled,
                        showExtension = showExtension,
                        contentScale = contentScale
                    )
                }
                endAdditionalItem?.let {
                    item {
                        endAdditionalItem()
                    }
                }
                item {
                    AnimatedVisibility(isLoadingImages) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(1f),
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

@Composable
private fun ImageItem(
    modifier: Modifier,
    uri: String,
    index: Int,
    onError: (String) -> Unit,
    selected: Boolean,
    isSelectionMode: Boolean,
    isAboveImageScrimEnabled: Boolean,
    showExtension: Boolean,
    aboveImageContent: @Composable BoxScope.(index: Int) -> Unit,
    contentScale: ContentScale
) {
    val transition = updateTransition(selected)
    val padding by transition.animateDp { s ->
        if (s) 10.dp else 0.dp
    }
    val corners by transition.animateDp { s ->
        if (s) 16.dp else 0.dp
    }
    val bgColor = MaterialTheme.colorScheme.secondaryContainer

    val shape = AutoCornersShape(corners)

    Box(
        modifier
            .clip(ShapeDefaults.extraSmall)
            .background(bgColor)
    ) {
        Picture(
            modifier = Modifier
                .matchParentSize()
                .padding(padding)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surface),
            onError = {
                onError(uri)
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
            filterQuality = FilterQuality.High,
            shape = RectangleShape,
            model = uri,
            contentScale = contentScale
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .clip(shape)
                .then(
                    if (isAboveImageScrimEnabled) {
                        Modifier.background(MaterialTheme.colorScheme.scrim.copy(0.32f))
                    } else Modifier
                ),
            contentAlignment = Alignment.Center,
            content = {
                aboveImageContent(index)

                if (showExtension) {
                    val extension = rememberFileExtension(uri.toUri())?.uppercase()
                    val humanFileSize = rememberHumanFileSize(uri.toUri())

                    extension?.let {
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .padding(vertical = 2.dp)
                                .advancedShadow(
                                    cornersRadius = 4.dp,
                                    shadowBlurRadius = 6.dp,
                                    alpha = 0.4f
                                )
                                .padding(horizontal = 2.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier,
                                text = it,
                                style = MaterialTheme.typography.labelMedium,
                                color = White
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .padding(vertical = 2.dp)
                            .advancedShadow(
                                cornersRadius = 4.dp,
                                shadowBlurRadius = 6.dp,
                                alpha = 0.4f
                            )
                            .padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier,
                            text = humanFileSize,
                            style = MaterialTheme.typography.labelMedium,
                            color = White
                        )
                    }
                }
            }
        )
        AnimatedContent(
            targetState = selected to isSelectionMode,
            transitionSpec = {
                fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
            }
        ) { (selected, isSelectionMode) ->
            if (isSelectionMode) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                            .border(2.dp, bgColor, ShapeDefaults.circle)
                            .clip(ShapeDefaults.circle)
                            .background(bgColor)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.RadioButtonUnchecked,
                        tint = Color.White.copy(alpha = 0.7f),
                        contentDescription = null,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }
    }
}