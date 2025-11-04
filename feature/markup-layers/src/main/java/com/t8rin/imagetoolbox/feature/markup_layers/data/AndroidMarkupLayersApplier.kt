/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.markup_layers.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.graphics.applyCanvas
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayersApplier
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class AndroidMarkupLayersApplier @Inject constructor(
    @ApplicationContext private val context: Context,
    dispatchersHolder: DispatchersHolder
) : MarkupLayersApplier<Bitmap>, DispatchersHolder by dispatchersHolder {

    override suspend fun applyToImage(
        image: Bitmap,
        layers: List<MarkupLayer>
    ): Bitmap = image.copy(Bitmap.Config.ARGB_8888, true).applyCanvas {
        layers.forEach { layer ->
            drawLayer(layer)
        }
    }

    private suspend fun Canvas.drawLayer(layer: MarkupLayer) {
//        val (type, initialPosition) = layer
//
//        val canvasSize = IntegerSize(
//            width = width,
//            height = height
//        )
//
//        val position = initialPosition.adjustByCanvasSize(canvasSize)
    }

}