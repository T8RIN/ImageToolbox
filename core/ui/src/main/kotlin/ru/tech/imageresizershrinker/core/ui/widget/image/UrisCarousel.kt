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

package ru.tech.imageresizershrinker.core.ui.widget.image

import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import coil3.asDrawable
import coil3.request.ImageRequest
import coil3.request.crossfade
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.safeAspectRatio
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import kotlin.math.roundToInt

@Composable
internal fun UrisCarousel(uris: List<Uri>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            4.dp,
            Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .layout { measurable, constraints ->
                val placeable = measurable.measure(
                    constraints.copy(
                        maxWidth = constraints.maxWidth + 32.dp.roundToPx(),
                        maxHeight = (constraints.maxWidth * 0.5f)
                            .coerceAtLeast(100f)
                            .coerceAtMost(constraints.maxHeight * 0.5f)
                            .roundToInt()
                    )
                )

                layout(
                    placeable.width,
                    placeable.height
                ) {
                    placeable.place(0, 0)
                }
            }
            .horizontalScroll(rememberScrollState())
            .padding(
                PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
            )
    ) {
        uris.fastForEach { uri ->
            var aspectRatio by rememberSaveable {
                mutableFloatStateOf(0.5f)
            }
            val context = LocalContext.current
            Picture(
                model = remember(context, uri) {
                    ImageRequest.Builder(context)
                        .data(uri)
                        .size(1000)
                        .crossfade(true)
                        .build()
                },
                onSuccess = {
                    aspectRatio = it.result.image.asDrawable(context.resources).safeAspectRatio
                },
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxHeight()
                    .aspectRatio(
                        ratio = aspectRatio,
                        matchHeightConstraintsFirst = true
                    )
                    .container(
                        shape = MaterialTheme.shapes.medium,
                        resultPadding = 0.dp
                    ),
                filterQuality = FilterQuality.High,
                shape = RectangleShape,
                contentScale = ContentScale.Fit
            )
        }
    }
}
