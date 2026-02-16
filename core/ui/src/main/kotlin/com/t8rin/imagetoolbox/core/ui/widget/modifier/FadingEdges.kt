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

package com.t8rin.imagetoolbox.core.ui.widget.modifier

import android.annotation.SuppressLint
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gigamole.composefadingedges.FadingEdgesGravity
import com.gigamole.composefadingedges.content.FadingEdgesContentType
import com.gigamole.composefadingedges.content.scrollconfig.FadingEdgesScrollConfig
import com.gigamole.composefadingedges.fill.FadingEdgesFillType
import com.gigamole.composefadingedges.horizontalFadingEdges
import com.gigamole.composefadingedges.verticalFadingEdges


@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.fadingEdges(
    scrollableState: ScrollableState? = null,
    color: Color = Color.Unspecified,
    isVertical: Boolean = false,
    spanCount: Int? = null,
    scrollFactor: Float = 1.25f,
    enabled: Boolean = true,
    length: Dp = 16.dp,
    gravity: FadingEdgesGravity = FadingEdgesGravity.All
) = this.composed {
    if (!enabled) Modifier
    else {
        val fillType = if (color.isSpecified) {
            FadingEdgesFillType.FadeColor(
                color = color
            )
        } else {
            FadingEdgesFillType.FadeClip()
        }

        val scrollConfig = FadingEdgesScrollConfig.Dynamic(
            scrollFactor = scrollFactor
        )

        when (scrollableState) {
            is ScrollState -> {
                if (isVertical) {
                    Modifier.verticalFadingEdges(
                        gravity = gravity,
                        contentType = FadingEdgesContentType.Dynamic.Scroll(
                            scrollConfig = scrollConfig,
                            state = scrollableState
                        ),
                        fillType = fillType,
                        length = length
                    )
                } else {
                    Modifier.horizontalFadingEdges(
                        gravity = gravity,
                        contentType = FadingEdgesContentType.Dynamic.Scroll(
                            scrollConfig = scrollConfig,
                            state = scrollableState
                        ),
                        fillType = fillType,
                        length = length
                    )
                }
            }

            is LazyListState -> {
                if (isVertical) {
                    Modifier.verticalFadingEdges(
                        gravity = gravity,
                        contentType = FadingEdgesContentType.Dynamic.Lazy.List(
                            scrollConfig = scrollConfig,
                            state = scrollableState
                        ),
                        fillType = fillType,
                        length = length
                    )
                } else {
                    Modifier.horizontalFadingEdges(
                        gravity = gravity,
                        contentType = FadingEdgesContentType.Dynamic.Lazy.List(
                            scrollConfig = scrollConfig,
                            state = scrollableState
                        ),
                        fillType = fillType,
                        length = length
                    )
                }
            }

            is LazyGridState -> {
                if (isVertical) {
                    Modifier.verticalFadingEdges(
                        gravity = gravity,
                        contentType = FadingEdgesContentType.Dynamic.Lazy.Grid(
                            scrollConfig = scrollConfig,
                            state = scrollableState,
                            spanCount = spanCount ?: remember(scrollableState) {
                                derivedStateOf {
                                    scrollableState.layoutInfo.maxSpan
                                }
                            }.value
                        ),
                        fillType = fillType,
                        length = length
                    )
                } else {
                    Modifier.horizontalFadingEdges(
                        gravity = gravity,
                        contentType = FadingEdgesContentType.Dynamic.Lazy.Grid(
                            scrollConfig = scrollConfig,
                            state = scrollableState,
                            spanCount = spanCount ?: remember(scrollableState) {
                                derivedStateOf {
                                    scrollableState.layoutInfo.maxSpan
                                }
                            }.value
                        ),
                        fillType = fillType,
                        length = length
                    )
                }
            }

            is LazyStaggeredGridState -> {
                require(spanCount != null)
                if (isVertical) {
                    Modifier.verticalFadingEdges(
                        gravity = gravity,
                        contentType = FadingEdgesContentType.Dynamic.Lazy.StaggeredGrid(
                            scrollConfig = scrollConfig,
                            state = scrollableState,
                            spanCount = spanCount
                        ),
                        fillType = fillType,
                        length = length
                    )
                } else {
                    Modifier.horizontalFadingEdges(
                        gravity = gravity,
                        contentType = FadingEdgesContentType.Dynamic.Lazy.StaggeredGrid(
                            scrollConfig = scrollConfig,
                            state = scrollableState,
                            spanCount = spanCount
                        ),
                        fillType = fillType,
                        length = length
                    )
                }
            }

            else -> {
                if (isVertical) {
                    Modifier.verticalFadingEdges(
                        gravity = gravity,
                        contentType = FadingEdgesContentType.Static,
                        fillType = fillType,
                        length = length
                    )
                } else {
                    Modifier.horizontalFadingEdges(
                        gravity = gravity,
                        contentType = FadingEdgesContentType.Static,
                        fillType = fillType,
                        length = length
                    )
                }
            }
        }
    }
}