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

package ru.tech.imageresizershrinker.feature.apng_tools.presentation.components

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.dragHandler
import ru.tech.imageresizershrinker.core.ui.widget.other.Loading
import ru.tech.imageresizershrinker.feature.apng_tools.domain.ApngFrames

@Composable
fun ApngConvertedImagesPreview(
    imageUris: List<String>,
    apngFrames: ApngFrames,
    onApngFramesChange: (ApngFrames) -> Unit,
    isPortrait: Boolean,
    isLoadingApngImages: Boolean,
    spacing: Dp = 8.dp
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
        val indexes = apngFrames
            .getFramePositions(imageUris.size)
            .map { it - 1 }
        imageUris.mapIndexedNotNull { index, _ ->
            if (index in indexes) index + 1
            else null
        }.toSet()
    }
    val selectedItems by remember(imageUris, apngFrames) {
        mutableStateOf(getUris())
    }
    val privateSelectedItems = remember {
        mutableStateOf(selectedItems)
    }

    LaunchedEffect(apngFrames, selectedItems) {
        if (apngFrames !is ApngFrames.ManualSelection || selectedItems.isEmpty()) {
            privateSelectedItems.value = selectedItems
        }
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val modifier = if (isPortrait) {
        Modifier.height(
            (130.dp * imageUris.size).coerceAtMost(420.dp)
        )
    } else {
        Modifier
    }.layout { measurable, constraints ->
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

    Box(modifier = modifier) {
        if (isPortrait) {
            LazyHorizontalGrid(
                rows = GridCells.Adaptive(120.dp),
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
                            onApngFramesChange(ApngFrames.ManualSelection(it.toList()))
                        },
                        autoScrollSpeed = autoScrollSpeed,
                        autoScrollThreshold = with(LocalDensity.current) { 40.dp.toPx() }
                    ),
                verticalArrangement = Arrangement.spacedBy(
                    space = spacing,
                    alignment = Alignment.CenterVertically
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    space = spacing,
                    alignment = Alignment.CenterHorizontally
                ),
                contentPadding = PaddingValues(12.dp),
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
                        uri = uri
                    )
                }
                item {
                    AnimatedVisibility(isLoadingApngImages) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Loading()
                        }
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(90.dp),
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
                            onApngFramesChange(ApngFrames.ManualSelection(it.toList()))
                        },
                        autoScrollSpeed = autoScrollSpeed,
                        autoScrollThreshold = with(LocalDensity.current) { 40.dp.toPx() }
                    ),
                verticalArrangement = Arrangement.spacedBy(
                    space = spacing,
                    alignment = Alignment.CenterVertically
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    space = spacing,
                    alignment = Alignment.CenterHorizontally
                ),
                contentPadding = PaddingValues(12.dp),
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
                        uri = uri
                    )
                }
                item {
                    AnimatedVisibility(isLoadingApngImages) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Loading()
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
    selected: Boolean
) {
    val transition = updateTransition(selected)
    val padding by transition.animateDp { s ->
        if (s) 10.dp else 0.dp
    }
    val corners by transition.animateDp { s ->
        if (s) 16.dp else 0.dp
    }
    val bgColor = MaterialTheme.colorScheme.secondaryContainer

    Box(
        modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
    ) {
        Picture(
            modifier = Modifier
                .matchParentSize()
                .padding(padding)
                .clip(RoundedCornerShape(corners))
                .background(Color.White),
            shape = RectangleShape,
            model = uri
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .clip(RoundedCornerShape(corners))
                .background(Color.Black.copy(0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = (index + 1).toString(),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }
        AnimatedContent(
            targetState = selected,
            transitionSpec = {
                fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
            }
        ) { selected ->
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .border(2.dp, bgColor, CircleShape)
                        .clip(CircleShape)
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