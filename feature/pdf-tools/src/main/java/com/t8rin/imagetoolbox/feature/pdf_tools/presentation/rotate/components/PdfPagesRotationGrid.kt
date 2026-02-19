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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.rotate.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.RotateRight
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.utils.animation.lessSpringySpec
import com.t8rin.imagetoolbox.core.ui.utils.helper.EnPreview
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker

@Composable
internal fun PdfPagesRotationGrid(
    pages: List<Any>,
    modifier: Modifier = Modifier
        .container(
            shape = ShapeDefaults.extraLarge,
            clip = false
        ),
    onRotateAll: () -> Unit,
    onClearAll: () -> Unit,
    rotations: List<Int>,
    onRotateAt: (Int) -> Unit,
    title: String = stringResource(R.string.pages),
    coerceHeight: Boolean = true
) {
    Column(
        modifier = modifier
            .then(
                if (coerceHeight) {
                    Modifier
                        .heightIn(
                            max = LocalWindowInfo.current.containerDpSize.height * 0.7f
                        )
                } else {
                    Modifier
                }
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    bottom = 8.dp,
                    start = 16.dp,
                    end = 8.dp
                )
        ) {
            Text(
                fontWeight = FontWeight.Medium,
                text = title,
                modifier = Modifier.weight(1f),
                fontSize = 18.sp,
            )
            Spacer(Modifier.width(16.dp))
            EnhancedButton(
                onClick = onRotateAll,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 12.dp
                ),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .height(30.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.RotateRight,
                        contentDescription = "Rotate 90",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        stringResource(R.string.all)
                    )
                }
            }

            EnhancedButton(
                onClick = onClearAll,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 12.dp
                ),
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .height(30.dp),
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Rounded.Restore,
                        contentDescription = "Restore",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        stringResource(R.string.reset)
                    )
                }
            }

        }
        Box(
            modifier = Modifier.weight(1f, false)
        ) {
            val listState = rememberLazyGridState()
            LazyVerticalGrid(
                state = listState,
                modifier = Modifier
                    .fadingEdges(
                        scrollableState = listState,
                        isVertical = true
                    )
                    .animateContentSizeNoClip(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                flingBehavior = enhancedFlingBehavior(),
                columns = GridCells.Adaptive(minSize = 150.dp)
            ) {
                itemsIndexed(
                    items = pages,
                    key = { _, uri -> uri.toString() + uri.hashCode() }
                ) { index, uri ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .container(
                                color = MaterialTheme.colorScheme.surface,
                                resultPadding = 8.dp
                            )
                            .container(
                                shape = ShapeDefaults.small,
                                color = Color.Transparent,
                                resultPadding = 0.dp
                            )
                            .hapticsClickable {
                                onRotateAt(index)
                            }
                    ) {
                        Picture(
                            model = uri,
                            modifier = Modifier
                                .fillMaxSize()
                                .transparencyChecker()
                                .rotate(
                                    animateFloatAsState(
                                        targetValue = rotations.getOrNull(index)?.toFloat() ?: 0f,
                                        animationSpec = lessSpringySpec()
                                    ).value
                                ),
                            showTransparencyChecker = false,
                            shape = RectangleShape,
                            contentScale = ContentScale.Inside
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainer.copy(0.6f)
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
                }
            }
        }
    }
}

@Composable
@EnPreview
private fun Preview() = ImageToolboxThemeForPreview(true) {
    var files by remember {
        mutableStateOf(
            List(15) {
                "file:///uri_$it.pdf".toUri()
            }
        )
    }

    var rotations by remember(files) {
        mutableStateOf(
            List(files.size) { 0 }
        )
    }
    LazyColumn {
        item {
            PdfPagesRotationGrid(
                pages = files,
                rotations = rotations,
                onRotateAll = {
                    rotations = rotations.map { (it + 90) % 360 }
                },
                onClearAll = {
                    rotations = rotations.map { 0 }
                },
                onRotateAt = {
                    rotations = rotations.toMutableList().apply {
                        this[it] = (this[it] + 90) % 360
                    }
                }
            )
        }

        items(30) {
            Text("TEST $it")
        }
    }
}