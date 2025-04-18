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

package ru.tech.imageresizershrinker.feature.filters.data.model

import android.content.Context
import android.graphics.Bitmap
import com.t8rin.opencv_tools.red_eye.RedEyeRemover
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.transformation.Transformation
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter

internal class AutoRemoveRedEyesFilter(
    private val context: Context,
    override val value: Float = 150f,
) : Transformation<Bitmap>, Filter.AutoRemoveRedEyes {

    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = RedEyeRemover.removeRedEyes(
        bitmap = input,
        context = context,
        minEyeSize = 35.0,
        redThreshold = value.toDouble()
    )

}