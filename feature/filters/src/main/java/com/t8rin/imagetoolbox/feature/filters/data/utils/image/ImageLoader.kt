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

package com.t8rin.imagetoolbox.feature.filters.data.utils.image

import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.utils.appContext

internal suspend fun Any.loadBitmap(size: Int? = null) = appContext.imageLoader.execute(
    ImageRequest.Builder(appContext)
        .data(this)
        .apply {
            if (size != null) size(size)
        }
        .build()
).image?.toBitmap()