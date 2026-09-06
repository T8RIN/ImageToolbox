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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.utils

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.createScaledBitmap
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun rememberDrawPathEffect(
    drawMode: DrawMode.PathEffect,
    canvasSize: IntegerSize,
    imageWidth: Int,
    imageHeight: Int,
    outputImage: ImageBitmap,
    onRequestFiltering: suspend (Bitmap, List<Filter<*>>) -> Bitmap?,
    onInvalidate: () -> Unit
): State<ImageBitmap?> {
    val shaderBitmap = remember(outputImage, drawMode, canvasSize) {
        mutableStateOf<ImageBitmap?>(null)
    }

    LaunchedEffect(outputImage, drawMode, canvasSize) {
        shaderBitmap.value = withContext(Dispatchers.Default) {
            onRequestFiltering(
                outputImage.asAndroidBitmap(),
                transformationsForMode(drawMode, canvasSize)
            )
                ?.createScaledBitmap(width = imageWidth, height = imageHeight)?.asImageBitmap()
        }
        onInvalidate()
    }

    return shaderBitmap
}