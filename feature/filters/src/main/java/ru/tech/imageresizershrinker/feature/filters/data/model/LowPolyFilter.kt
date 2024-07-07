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
import io.github.xyzxqs.xlowpoly.LowPoly
import oupson.apng.utils.Utils.flexibleResize
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.transformation.Transformation
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import kotlin.math.max

internal class LowPolyFilter(
    override val value: Pair<Int, Boolean> = 1000 to true
) : Transformation<Bitmap>, Filter.LowPoly {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = LowPoly.lowPoly(
        input = input.let {
            if (it.height > 3000 || it.width > 3000) it.flexibleResize(3000)
            else it
        },
        alphaOrPointCount = value.first.toFloat(),
        fill = value.second
    ).let {
        if (input.height > 3000 || input.width > 3000) it.flexibleResize(
            max(
                input.width,
                input.height
            )
        )
        else it
    }

}