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
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
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
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.erase_background.domain.model.BgModelType
import kotlinx.coroutines.launch

@Composable
internal fun MobileSAMSelectionSheet(
    state: MobileSAMSelectionState,
    onDismiss: () -> Unit,
    onApply: (Bitmap) -> Unit
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(state) {
        state.prepare()
        onDismiss()
    }
    LaunchedEffect(state.isPrepared, state.points) {
        state.updatePreview()
    }

    EnhancedModalBottomSheet(
        nestedScrollEnabled = false,
        enableBottomContentWeight = true,
        visible = true,
        onDismiss = { if (!it) onDismiss() },
        title = {
            TitleItem(
                text = BgModelType.MobileSAM.title,
                icon = Icons.Rounded.AutoFixHigh
            )
        },
        confirmButton = {
            EnhancedButton(
                enabled = state.isPrepared && state.canApply && !state.isLoading,
                onClick = {
                    scope.launch { state.applySelection()?.let(onApply) }
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.apply),
                        modifier = Modifier.alpha(if (state.isApplying) 0f else 1f)
                    )
                    if (state.isApplying) {
                        EnhancedCircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MobileSAMSelectionPreview(
                state = state,
                positive = state.positive,
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
                    enabled = state.isPrepared && !state.isApplying,
                    title = null,
                    items = listOf(
                        stringResource(R.string.sam_keep),
                        stringResource(R.string.sam_remove)
                    ),
                    selectedIndex = if (state.positive) 0 else 1,
                    onIndexChange = { state.positive = it == 0 },
                    isScrollable = false,
                    modifier = Modifier.weight(1f)
                )
                EnhancedIconButton(
                    enabled = state.isPrepared && state.points.isNotEmpty() && !state.isApplying,
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
    positive: Boolean,
    modifier: Modifier = Modifier
) {
    val positiveColor = MaterialTheme.colorScheme.primary
    val negativeColor = MaterialTheme.colorScheme.error
    val outlineColor = MaterialTheme.colorScheme.surface
    val source = state.source

    Box(
        modifier = modifier
            .container(shape = ShapeDefaults.default, resultPadding = 0.dp)
            .transparencyChecker()
            .pointerInput(state, positive) {
                detectTapGestures { offset ->
                    val scale = minOf(
                        size.width / source.width.toFloat(),
                        size.height / source.height.toFloat()
                    )
                    if (scale <= 0f) return@detectTapGestures

                    val imageWidth = source.width * scale
                    val imageHeight = source.height * scale
                    val x = (offset.x - (size.width - imageWidth) / 2) / imageWidth
                    val y = (offset.y - (size.height - imageHeight) / 2) / imageHeight
                    if (x in 0f..1f && y in 0f..1f) {
                        state.addPoint(x, y, positive)
                    }
                }
            },
        contentAlignment = Alignment.Center
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
                drawCircle(outlineColor, 7.dp.toPx(), position)
                drawCircle(
                    color = if (point.positive) positiveColor else negativeColor,
                    radius = 5.dp.toPx(),
                    center = position
                )
            }
        }
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(0.5f))
            )
            EnhancedLoadingIndicator()
        }
    }
}