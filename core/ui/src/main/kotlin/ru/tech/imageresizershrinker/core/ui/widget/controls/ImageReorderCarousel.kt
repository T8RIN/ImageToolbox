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

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
fun ImageReorderCarousel(
    images: List<Uri>?,
    onReorder: (List<Uri>?) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
        .container(RoundedCornerShape(24.dp)),
    onNeedToAddImage: () -> Unit,
    onNeedToRemoveImageAt: (Int) -> Unit
) {
    val data = remember { mutableStateOf(images ?: emptyList()) }

    val state = rememberReorderableLazyListState(
        onMove = { from, to ->
            data.value = data.value.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
        },
        onDragEnd = { _, _ ->
            onReorder(data.value)
        }
    )
    val listState = state.listState

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
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                forceMinimumInteractiveComponentSize = false,
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .size(30.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Box {
            LazyRow(
                state = listState,
                modifier = Modifier
                    .reorderable(state)
                    .detectReorderAfterLongPress(state)
                    .animateContentSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(data.value, key = { _, uri -> uri.hashCode() }) { index, uri ->
                    ReorderableItem(
                        reorderableState = state,
                        key = uri.hashCode()
                    ) { isDragging ->
                        val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)
                        val alpha by animateFloatAsState(if (isDragging) 0.3f else 0.6f)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                Modifier
                                    .size(120.dp)
                                    .shadow(elevation, RoundedCornerShape(16.dp))
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
                            androidx.compose.animation.AnimatedVisibility(
                                visible = (images?.size ?: 0) > 2 && state.draggingItemKey == null,
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
                120.dp + if (state.draggingItemKey == null && (images?.size
                        ?: 0) > 2
                ) 50.dp else 0.dp
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(12.dp)
                    .height(edgeHeight)
                    .background(
                        brush = Brush.Companion.horizontalGradient(
                            0f to MaterialTheme
                                .colorScheme
                                .surfaceColorAtElevation(
                                    1.dp
                                ),
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
                        brush = Brush.Companion.horizontalGradient(
                            0f to Color.Transparent,
                            1f to MaterialTheme
                                .colorScheme
                                .surfaceColorAtElevation(
                                    1.dp
                                )
                        )
                    )
            )
        }
    }
}