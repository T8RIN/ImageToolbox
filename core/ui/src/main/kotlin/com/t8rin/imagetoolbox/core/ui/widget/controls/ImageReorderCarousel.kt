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

package com.t8rin.imagetoolbox.core.ui.widget.controls

import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.shareUris
import com.t8rin.imagetoolbox.core.ui.utils.helper.sortedByType
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.press
import com.t8rin.imagetoolbox.core.ui.widget.image.ImagePager
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun ImageReorderCarousel(
    images: List<Uri>?,
    onReorder: (List<Uri>) -> Unit,
    modifier: Modifier = Modifier
        .container(ShapeDefaults.extraLarge),
    onNeedToAddImage: () -> Unit,
    onNeedToRemoveImageAt: (Int) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val data = remember { mutableStateOf(images ?: emptyList()) }

    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val listState = rememberLazyListState()
    val state = rememberReorderableLazyListState(
        lazyListState = listState,
        onMove = { from, to ->
            haptics.press()
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

    var previewUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
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

            val scope = rememberCoroutineScope()
            SortButton(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(30.dp),
                onSortTypeSelected = { sortType ->
                    scope.launch(Dispatchers.Default) {
                        val newValue = images
                            .orEmpty()
                            .sortedByType(
                                sortType = sortType
                            )

                        withContext(Dispatchers.Main.immediate) {
                            data.value = newValue
                            onReorder(newValue)
                        }
                    }
                }
            )
        }
        Box {
            val showButton = (images?.size ?: 0) > 2 && !state.isAnyItemDragging
            LazyRow(
                state = listState,
                modifier = Modifier
                    .fadingEdges(scrollableState = listState)
                    .animateContentSizeNoClip(),
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
                        val shape = animateShape(
                            if (showButton) ShapeDefaults.top
                            else ShapeDefaults.default
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .scale(
                                        animateFloatAsState(
                                            if (isDragging) 1.05f
                                            else 1f
                                        ).value
                                    )
                                    .container(
                                        shape = shape,
                                        color = Color.Transparent,
                                        resultPadding = 0.dp
                                    )
                                    .hapticsClickable { previewUri = uri }
                                    .longPressDraggableHandle(
                                        onDragStarted = {
                                            haptics.longPress()
                                        },
                                        onDragStopped = {
                                            onReorder(data.value)
                                        }
                                    )
                            ) {
                                Picture(
                                    model = uri,
                                    modifier = Modifier.fillMaxSize(),
                                    shape = RectangleShape,
                                    contentScale = ContentScale.Fit
                                )
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .background(
                                            MaterialTheme.colorScheme
                                                .surfaceContainer
                                                .copy(alpha)
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
                                visible = showButton,
                                enter = expandVertically(tween(300)) + fadeIn(),
                                exit = shrinkVertically(tween(300)) + fadeOut(),
                                modifier = Modifier.width(120.dp)
                            ) {
                                EnhancedButton(
                                    contentPadding = PaddingValues(),
                                    onClick = { onNeedToRemoveImageAt(index) },
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                        0.5f
                                    ),
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    shape = ShapeDefaults.bottom,
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .height(30.dp)
                                        .width(120.dp)
                                ) {
                                    Text(stringResource(R.string.remove), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    ImagePager(
        visible = previewUri != null,
        selectedUri = previewUri,
        uris = images,
        onNavigate = onNavigate,
        onUriSelected = { previewUri = it },
        onShare = { context.shareUris(listOf(it)) },
        onDismiss = { previewUri = null }
    )
}