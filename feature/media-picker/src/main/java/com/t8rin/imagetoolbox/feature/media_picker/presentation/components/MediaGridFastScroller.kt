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
package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.t8rin.fastscroller.VerticalFastScroller
import com.t8rin.fastscroller.rememberFastScrollerState
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaItem
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.dateTakenSeconds
import kotlin.math.roundToInt

@Composable
internal fun BoxScope.MediaGridFastScroller(
    state: LazyGridState,
    media: List<MediaItem>,
    firstMediaIndex: Int,
    useDateTaken: Boolean,
    rightPadding: Dp,
    bottomPadding: Dp
) {
    val fastScrollerState = rememberFastScrollerState()
    VerticalFastScroller(
        state = state,
        fastScrollerState = fastScrollerState,
        thickness = 4.dp,
        minThumbHeight = 64.dp,
        thumbColor = MaterialTheme.colorScheme.primary,
        scrollByItemIndex = true,
        autoHide = true,
        thumbTouchWidth = 48.dp,
        modifier = Modifier
            .align(AbsoluteAlignment.TopRight)
            .fillMaxHeight()
            .absolutePadding(right = rightPadding, bottom = bottomPadding)
    )

    var labelHeight by remember { mutableIntStateOf(0) }
    AnimatedVisibility(
        visible = fastScrollerState.isDragging,
        enter = fadeIn(tween(150)) + scaleIn(
            initialScale = 0.9f,
            transformOrigin = TransformOrigin(1f, 0.5f)
        ),
        exit = fadeOut(tween(120)) + scaleOut(
            targetScale = 0.9f,
            transformOrigin = TransformOrigin(1f, 0.5f)
        ),
        modifier = Modifier
            .align(AbsoluteAlignment.TopRight)
            .absolutePadding(right = rightPadding + 10.dp)
            .offset {
                IntOffset(0, (fastScrollerState.draggedThumbCenter - labelHeight / 2f).roundToInt())
            }
            .onSizeChanged { labelHeight = it.height }
    ) {
        val context = LocalContext.current
        val timestamp by remember(state, media, firstMediaIndex, useDateTaken) {
            derivedStateOf {
                val index = fastScrollerState.targetItemIndex.takeIf { it >= 0 }
                    ?: if (state.canScrollForward) state.firstVisibleItemIndex
                    else state.layoutInfo.totalItemsCount - 1
                val target = media.getOrNull((index - firstMediaIndex).coerceAtLeast(0))
                val item = when (target) {
                    is MediaItem.MediaViewItem -> target.media
                    is MediaItem.Header -> target.data.firstOrNull()
                    null -> null
                }
                if (useDateTaken) item?.dateTakenSeconds else item?.timestamp
            }
        }
        val label = remember(context, timestamp) {
            timestamp?.takeIf { it > 0 }?.let {
                DateUtils.formatDateTime(
                    context,
                    it * 1000,
                    DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR or
                            DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_ABBREV_MONTH
                )
            }.orEmpty()
        }
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clip(ShapeDefaults.circle)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}