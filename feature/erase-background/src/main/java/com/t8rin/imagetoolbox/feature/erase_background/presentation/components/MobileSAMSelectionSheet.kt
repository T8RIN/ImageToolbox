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

package com.t8rin.imagetoolbox.feature.erase_background.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AutoFixHigh
import com.t8rin.imagetoolbox.core.resources.icons.Undo
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.core.ui.widget.other.ZoomBadge
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.erase_background.domain.model.BgModelType
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
internal fun MobileSAMSelectionSheet(
    state: MobileSAMSelectionState,
    visible: Boolean,
    onDismiss: () -> Unit,
    onHidden: () -> Unit,
    onApply: (Bitmap) -> Unit
) {
    var applyRequested by remember(state) { mutableStateOf(false) }

    LaunchedEffect(state, visible) {
        if (visible) {
            state.prepare()
            onDismiss()
        }
    }
    LaunchedEffect(state, visible, state.isPrepared, state.points) {
        if (visible) state.updatePreview()
    }
    LaunchedEffect(state, visible, applyRequested) {
        if (visible && applyRequested) {
            try {
                state.applySelection()?.let(onApply)
            } finally {
                applyRequested = false
            }
        }
    }

    EnhancedModalBottomSheet(
        nestedScrollEnabled = false,
        enableBottomContentWeight = true,
        visible = visible,
        onDismiss = { if (!it) onDismiss() },
        title = {
            TitleItem(
                text = BgModelType.MobileSAM.title,
                icon = Icons.Rounded.AutoFixHigh
            )
        },
        confirmButton = {
            EnhancedButton(
                enabled = visible && state.isPrepared && state.canApply &&
                        !state.isLoading && !applyRequested,
                onClick = { applyRequested = true }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.apply),
                        modifier = Modifier.alpha(if (state.isApplying) 0f else 1f)
                    )
                    if (state.isApplying) {
                        EnhancedCircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }
    ) {
        DisposableEffect(state) {
            onDispose { onHidden() }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MobileSAMSelectionPreview(
                state = state,
                visible = visible,
                modifier = Modifier
                    .weight(1f, false)
                    .fillMaxWidth()
                    .heightIn(min = 160.dp, max = 440.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .container(resultPadding = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EnhancedButtonGroup(
                    enabled = visible && state.isPrepared && !state.isApplying,
                    title = null,
                    items = listOf(
                        stringResource(R.string.sam_keep),
                        stringResource(R.string.sam_remove)
                    ),
                    selectedIndex = if (state.positive) 0 else 1,
                    onIndexChange = { state.setPositive(it == 0) },
                    isScrollable = false,
                    modifier = Modifier.weight(1f)
                )
                EnhancedIconButton(
                    enabled = visible && state.isPrepared &&
                            state.points.isNotEmpty() && !state.isApplying,
                    onClick = state::undoPoint
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Undo,
                        contentDescription = stringResource(R.string.sam_undo_point)
                    )
                }
            }
            InfoContainer(
                text = stringResource(R.string.sam_points_hint),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(0.5f),
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.85f),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MobileSAMSelectionPreview(
    state: MobileSAMSelectionState,
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    val positiveColor = MaterialTheme.colorScheme.primary
    val negativeColor = MaterialTheme.colorScheme.error
    val outlineColor = MaterialTheme.colorScheme.surface
    val source = state.previewSource
    val zoomState = rememberZoomState(
        maxScale = 30f,
        contentSize = Size(source.width.toFloat(), source.height.toFloat()),
        key = state
    )
    var previewSize by remember(state) { mutableStateOf(IntSize.Zero) }
    val addPoint by rememberUpdatedState<(Offset) -> Unit> { offset ->
        if (visible && state.isPrepared && !state.isApplying) {
            val scale = minOf(
                previewSize.width / source.width.toFloat(),
                previewSize.height / source.height.toFloat()
            )
            if (scale > 0f) {
                val center = Offset(previewSize.width / 2f, previewSize.height / 2f)
                val pan = Offset(zoomState.offsetX, zoomState.offsetY)
                val position = (offset - center - pan) / zoomState.scale
                val x = position.x / (source.width * scale) + 0.5f
                val y = position.y / (source.height * scale) + 0.5f
                if (x in 0f..1f && y in 0f..1f) {
                    state.addPoint(x, y, state.positive)
                }
            }
        }
    }

    Box(
        modifier = modifier
            .container(shape = ShapeDefaults.default, resultPadding = 0.dp)
            .transparencyChecker(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .zoomable(
                    zoomState = zoomState,
                    zoomEnabled = visible && !state.isApplying,
                    onTap = { addPoint(it) }
                )
                .onSizeChanged { previewSize = it }
        ) {
            Image(
                bitmap = remember(state.previewSource) {
                    state.previewSource.asImageBitmap()
                },
                contentDescription = null,
                contentScale = ContentScale.Fit,
                alpha = 0.2f,
                modifier = Modifier.fillMaxSize()
            )
            Image(
                bitmap = remember(state.preview) {
                    state.preview.asImageBitmap()
                },
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
            Canvas(Modifier.fillMaxSize()) {
                val scale = minOf(size.width / source.width, size.height / source.height)
                val origin = Offset(
                    x = (size.width - source.width * scale) / 2,
                    y = (size.height - source.height * scale) / 2
                )
                state.points.forEach { point ->
                    val position = origin + Offset(
                        x = point.x * source.width * scale,
                        y = point.y * source.height * scale
                    )
                    drawCircle(outlineColor, 7.dp.toPx() / zoomState.scale, position)
                    drawCircle(
                        color = if (point.positive) positiveColor else negativeColor,
                        radius = 5.dp.toPx() / zoomState.scale,
                        center = position
                    )
                }
            }
        }
        ZoomBadge(
            zoomLevel = zoomState.scale,
            modifier = Modifier.align(Alignment.TopStart),
            horizontalPadding = 8.dp
        )
        if (visible && state.isLoading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(0.5f))
            )
            EnhancedLoadingIndicator()
        }
    }
}