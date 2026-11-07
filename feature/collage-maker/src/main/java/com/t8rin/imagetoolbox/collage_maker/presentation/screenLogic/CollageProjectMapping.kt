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

package com.t8rin.imagetoolbox.collage_maker.presentation.screenLogic

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri
import com.t8rin.collages.layout.CollageLayoutStore
import com.t8rin.collages.model.CollageType
import com.t8rin.imagetoolbox.collage_maker.domain.CollageProjectSnapshot
import com.t8rin.imagetoolbox.collage_maker.presentation.screenLogic.CollageMakerComponent.HistorySnapshot

internal fun HistorySnapshot.toProjectSnapshot(): CollageProjectSnapshot {
    val images = uris.orEmpty()
    val layoutId = collageType.layoutId.takeIf { collageType.imageCount == images.size }
    return CollageProjectSnapshot(
        uris = uris?.map(Uri::toString),
        layoutId = layoutId,
        collageState = collageState?.takeIf { state ->
            state.layoutId == layoutId && state.images.size == images.size &&
                    state.images.all { it.uri == images.getOrNull(it.index)?.toString() }
        },
        aspectRatio = aspectRatio,
        backgroundColor = backgroundColor.toArgb(),
        backgroundGradient = backgroundGradient,
        params = params,
        imageFormat = imageFormat,
        quality = quality
    )
}

internal fun CollageProjectSnapshot.toHistorySnapshot(): HistorySnapshot {
    val layout = layoutId?.let {
        requireNotNull(
            CollageLayoutStore.getLayout(
                layoutId = it,
                layoutParams = collageState?.layoutParams,
                design = collageState?.design
            )
        ).also { type ->
            require(type.imageCount == uris.orEmpty().size)
        }
    } ?: CollageType.Empty
    return HistorySnapshot(
        uris = uris?.map(String::toUri),
        collageType = layout,
        collageState = collageState,
        aspectRatio = aspectRatio,
        backgroundColor = Color(backgroundColor),
        backgroundGradient = backgroundGradient,
        params = params,
        imageFormat = imageFormat,
        quality = quality
    )
}