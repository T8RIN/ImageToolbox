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

package com.t8rin.collages

import android.util.LruCache
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.withTransform
import com.t8rin.collages.model.CollageLayout
import com.t8rin.collages.utils.CollagePreviewSize
import com.t8rin.collages.utils.createCollagePreviewPath

@Composable
internal fun CollagePreview(
    path: Path,
    color: Color,
    modifier: Modifier
) {
    Canvas(modifier) {
        withTransform({
            scale(
                scaleX = size.width / CollagePreviewSize,
                scaleY = size.height / CollagePreviewSize,
                pivot = Offset.Zero
            )
        }) {
            drawPath(path, color)
        }
    }
}

internal object CollagePreviewCache {
    private val paths = LruCache<String, Path>(192)

    fun get(layout: CollageLayout): Path = synchronized(paths) {
        paths[layout.title] ?: createCollagePreviewPath(layout).asComposePath().also {
            paths.put(layout.title, it)
        }
    }
}