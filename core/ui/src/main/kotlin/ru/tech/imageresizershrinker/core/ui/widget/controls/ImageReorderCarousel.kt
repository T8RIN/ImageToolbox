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

package ru.tech.imageresizershrinker.core.ui.widget.controls

import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.domain.model.SortType
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.sortedByType
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalBottomSheet
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.hapticsClickable
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun ImageReorderCarousel(
    images: List<Uri>?,
    onReorder: (List<Uri>) -> Unit,
    modifier: Modifier = Modifier
        .container(RoundedCornerShape(24.dp)),
    onNeedToAddImage: () -> Unit,
    onNeedToRemoveImageAt: (Int) -> Unit
) {
    val data = remember { mutableStateOf(images ?: emptyList()) }

    val listState = rememberLazyListState()
    val state = rememberReorderableLazyListState(
        lazyListState = listState,
        onMove = { from, to ->
            data.value = data.value.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
        }
    )

    LaunchedEffect(images) {
        if (data.value.sorted() != images?.sorted()) {
            data.value = images ?: emptyList()
            listState.animateScrollToItem(data.value.lastIndex.coerceAtLeast(0))
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        ) {
            Text(
                fontWeight = FontWeight.Medium,
                text = stringResource(R.string.images_order),
                modifier = Modifier.padding(start = 8.dp),
                fontSize = 18.sp
            )
            EnhancedIconButton(
                onClick = onNeedToAddImage,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                forceMinimumInteractiveComponentSize = false,
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .size(30.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.add),
                    modifier = Modifier.size(20.dp)
                )
            }

            var showSortTypeSelection by rememberSaveable {
                mutableStateOf(false)
            }

            EnhancedIconButton(
                onClick = {
                    showSortTypeSelection = true
                },
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                forceMinimumInteractiveComponentSize = false,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(30.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.FilterAlt,
                    contentDescription = stringResource(R.string.filter),
                    modifier = Modifier.size(20.dp)
                )
            }
            EnhancedModalBottomSheet(
                visible = showSortTypeSelection,
                onDismiss = { showSortTypeSelection = it },
                title = {
                    TitleItem(
                        text = stringResource(R.string.sorting),
                        icon = Icons.Rounded.FilterAlt
                    )
                },
                confirmButton = {
                    EnhancedButton(
                        onClick = {
                            showSortTypeSelection = false
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(stringResource(R.string.close))
                    }
                }
            ) {
                val context = LocalContext.current
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val items = remember {
                        SortType.entries
                    }

                    items.forEachIndexed { index, item ->
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .container(
                                    shape = ContainerShapeDefaults.shapeForIndex(
                                        index,
                                        items.size
                                    ),
                                    resultPadding = 0.dp
                                )
                                .hapticsClickable {
                                    val newValue = images
                                        ?.sortedByType(
                                            sortType = item,
                                            context = context
                                        )
                                        ?.reversed() ?: emptyList()
                                    data.value = newValue
                                    onReorder(newValue)
                                    showSortTypeSelection = false
                                }
                        ) {
                            TitleItem(text = item.title)
                        }
                    }
                }
            }
        }
        Box {
            LazyRow(
                state = listState,
                modifier = Modifier.animateContentSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = data.value,
                    key = { _, uri -> uri.toString() + uri.hashCode() }
                ) { index, uri ->
                    ReorderableItem(
                        state = state,
                        key = uri.toString() + uri.hashCode()
                    ) { isDragging ->
                        val alpha by animateFloatAsState(if (isDragging) 0.3f else 0.6f)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .longPressDraggableHandle {
                                        onReorder(data.value)
                                    }
                                    .scale(
                                        animateFloatAsState(
                                            if (isDragging) 1.05f
                                            else 1f
                                        ).value
                                    )
                                    .container(
                                        shape = RoundedCornerShape(16.dp),
                                        color = Color.Transparent,
                                        resultPadding = 0.dp
                                    )
                            ) {
                                Picture(
                                    model = uri,
                                    modifier = Modifier.fillMaxSize(),
                                    shape = RectangleShape,
                                    contentScale = ContentScale.Fit
                                )
                                Box(
                                    Modifier
                                        .size(120.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceContainer.copy(
                                                alpha = alpha
                                            ),
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            BoxAnimatedVisibility(
                                visible = (images?.size ?: 0) > 2 && !state.isAnyItemDragging,
                                enter = scaleIn() + fadeIn(),
                                exit = scaleOut() + fadeOut()
                            ) {
                                EnhancedButton(
                                    contentPadding = PaddingValues(),
                                    onClick = { onNeedToRemoveImageAt(index) },
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                        .height(30.dp)
                                ) {
                                    Text(stringResource(R.string.remove), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
            val edgeHeight by animateDpAsState(
                120.dp + if (!state.isAnyItemDragging && (images?.size
                        ?: 0) > 2
                ) 50.dp else 0.dp
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(12.dp)
                    .height(edgeHeight)
                    .background(
                        brush = Brush.horizontalGradient(
                            0f to MaterialTheme
                                .colorScheme
                                .surfaceContainerLow,
                            1f to Color.Transparent
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(12.dp)
                    .height(edgeHeight)
                    .background(
                        brush = Brush.horizontalGradient(
                            0f to Color.Transparent,
                            1f to MaterialTheme
                                .colorScheme
                                .surfaceContainerLow
                        )
                    )
            )
        }
    }
}

private val SortType.title: String
    @Composable
    get() = when (this) {
        SortType.Date -> stringResource(R.string.sort_by_date)
        SortType.DateReversed -> stringResource(R.string.sort_by_date_reversed)
        SortType.Name -> stringResource(R.string.sort_by_name)
        SortType.NameReversed -> stringResource(R.string.sort_by_name_reversed)
    }