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

package com.t8rin.collages.public

import coil3.request.ImageRequest
import com.t8rin.collages.AdditionalCollages
import com.t8rin.collages.ArtisticCollages
import com.t8rin.collages.CreativeCollages
import com.t8rin.collages.DecorativeCollages
import com.t8rin.collages.PatternCollages
import com.t8rin.collages.utils.CollageLayoutFactory

object CollageConstants {
    const val MAX_IMAGE_COUNT: Int = 100

    val layoutCount: Int get() = CollageLayoutFactory.COLLAGE_MAP.size + CreativeCollages.Family.entries.size * 20 + AdditionalCollages.count + ArtisticCollages.count + DecorativeCollages.count + PatternCollages.count

    internal var requestMapper: ImageRequest.Builder.() -> ImageRequest.Builder = { this }

    fun requestMapper(mapper: ImageRequest.Builder.() -> ImageRequest.Builder) {
        this.requestMapper = mapper
    }
}