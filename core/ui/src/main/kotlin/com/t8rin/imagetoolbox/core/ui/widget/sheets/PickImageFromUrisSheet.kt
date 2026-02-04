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

package com.t8rin.imagetoolbox.core.ui.widget.sheets

import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.transform.Transformation
import com.t8rin.imagetoolbox.core.domain.utils.notNullAnd
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
fun PickImageFromUrisSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    transformations: List<Transformation>? = null,
    uris: List<Uri>?,
    selectedUri: Uri?,
    onUriRemoved: (Uri) -> Unit,
    columns: Int,
    onUriPicked: (Uri) -> Unit
) {
    val hasUris = uris.notNullAnd { it.size >= 2 }
    if (!hasUris) onDismiss()

    EnhancedModalBottomSheet(
        sheetContent = {
            val gridState = rememberLazyGridState()
            LaunchedEffect(Unit) {
                gridState.scrollToItem(
                    uris?.indexOf(selectedUri) ?: 0
                )
            }
            Box {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(8.dp),
                    state = gridState,
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    ),
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    uris?.let { uris ->
                        items(
                            items = uris,
                            key = { it.toString() + it.hashCode() }
                        ) { uri ->
                            val selected = selectedUri == uri
                            val color by animateColorAsState(
                                if (selected) MaterialTheme.colorScheme.surface
                                else MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                            val padding by animateDpAsState(if (selected) 12.dp else 4.dp)
                            val pictureShape = animateShape(
                                if (selected) ShapeDefaults.large
                                else ShapeDefaults.mini
                            )
                            val borderWidth by animateDpAsState(if (selected) 1.5.dp else (-1).dp)
                            val borderColor by animateColorAsState(
                                if (selected) MaterialTheme.colorScheme.primaryContainer
                                else Color.Transparent
                            )
                            Box(
                                modifier = Modifier
                                    .container(
                                        shape = ShapeDefaults.mini,
                                        resultPadding = 0.dp,
                                        color = color
                                    )
                                    .animateItem()
                            ) {
                                Picture(
                                    transformations = transformations,
                                    model = uri,
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(pictureShape)
                                        .padding(padding)
                                        .clip(pictureShape)
                                        .hapticsClickable {
                                            onUriPicked(uri)
                                            onDismiss()
                                        }
                                        .border(
                                            width = borderWidth,
                                            color = borderColor,
                                            shape = pictureShape
                                        ),
                                    shape = RectangleShape,
                                    contentScale = ContentScale.Fit
                                )
                                BoxAnimatedVisibility(
                                    visible = selected,
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Box {
                                        Box(
                                            modifier = Modifier
                                                .padding(1.dp)
                                                .size(36.dp)
                                                .clip(
                                                    AutoCornersShape(
                                                        bottomStartPercent = 50
                                                    )
                                                )
                                                .background(MaterialTheme.colorScheme.primaryContainer)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .width(38.dp)
                                                .height(padding)
                                                .background(color)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .width(padding)
                                                .height(38.dp)
                                                .background(color)
                                        )
                                    }
                                }
                                EnhancedIconButton(
                                    onClick = {
                                        onUriRemoved(uri)
                                    },
                                    forceMinimumInteractiveComponentSize = false,
                                    containerColor = color,
                                    enabled = hasUris,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .align(Alignment.TopEnd),
                                    enableAutoShadowAndBorder = false,
                                    shape = AutoCornersShape(
                                        bottomStartPercent = 50
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.RemoveCircleOutline,
                                        contentDescription = stringResource(R.string.remove)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onDismiss,
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
        title = {
            TitleItem(
                text = stringResource(R.string.change_preview),
                icon = Icons.Rounded.PhotoLibrary
            )
        },
        visible = visible,
        onDismiss = {
            if (!it) onDismiss()
        }
    )
}