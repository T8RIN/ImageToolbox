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

package ru.tech.imageresizershrinker.feature.filters.data.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.domain.model.ImageModel
import ru.tech.imageresizershrinker.core.domain.transformation.ChainTransformation
import ru.tech.imageresizershrinker.core.domain.transformation.Transformation
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.resources.R

internal class EdgyAmberFilter(
    override val value: Float = 1f,
    private val lut512x512FilterFactory: LUT512x512Filter.Factory,
) : ChainTransformation<Bitmap>, Filter.EdgyAmber {

    override fun getTransformations(): List<Transformation<Bitmap>> = listOf(
        lut512x512FilterFactory(value to ImageModel(R.drawable.lookup_edgy_amber))
    )

    override val cacheKey: String
        get() = value.hashCode().toString()
}