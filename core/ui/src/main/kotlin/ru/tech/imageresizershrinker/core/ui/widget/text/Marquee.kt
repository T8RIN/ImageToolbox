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

package ru.tech.imageresizershrinker.core.ui.widget.text

import androidx.compose.foundation.basicMarquee
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.gigamole.composefadingedges.fill.FadingEdgesFillType
import com.gigamole.composefadingedges.marqueeHorizontalFadingEdges


fun Modifier.marquee(
    edgesColor: Color = Color.Unspecified,
) = this.composed {
    var showMarquee by remember { mutableStateOf(false) }

    Modifier
        .clipToBounds()
        .then(
            if (showMarquee) {
                Modifier.marqueeHorizontalFadingEdges(
                    fillType = if (edgesColor.isSpecified) {
                        FadingEdgesFillType.FadeColor(
                            color = edgesColor
                        )
                    } else FadingEdgesFillType.FadeClip(),
                    length = 10.dp,
                    isMarqueeAutoLayout = false
                ) {
                    Modifier.basicMarquee(
                        iterations = Int.MAX_VALUE,
                        velocity = 48.dp,
                        repeatDelayMillis = 1500
                    )
                }
            } else Modifier
        )
        .layout { measurable, constraints ->
            val childConstraints = constraints.copy(maxWidth = Constraints.Infinity)
            val placeable = measurable.measure(childConstraints)
            val containerWidth = constraints.constrainWidth(placeable.width)
            val contentWidth = placeable.width
            if (!showMarquee) {
                showMarquee = contentWidth > containerWidth
            }
            layout(containerWidth, placeable.height) {
                placeable.placeWithLayer(x = 0, y = 0)
            }
        }
}