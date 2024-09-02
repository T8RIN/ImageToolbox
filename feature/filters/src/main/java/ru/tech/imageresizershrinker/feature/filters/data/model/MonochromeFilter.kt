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
import androidx.compose.ui.graphics.Color
import com.awxkee.aire.Aire
import ru.tech.imageresizershrinker.core.data.image.utils.ColorUtils.blue
import ru.tech.imageresizershrinker.core.data.image.utils.ColorUtils.green
import ru.tech.imageresizershrinker.core.data.image.utils.ColorUtils.red
import ru.tech.imageresizershrinker.core.data.image.utils.ColorUtils.toModel
import ru.tech.imageresizershrinker.core.domain.model.ColorModel
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.transformation.Transformation
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter


internal class MonochromeFilter(
    override val value: Pair<Float, ColorModel> = 1f to Color(
        red = 0.6f,
        green = 0.45f,
        blue = 0.3f,
        alpha = 1.0f
    ).toModel(),
) : Transformation<Bitmap>, Filter.Monochrome {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Aire.monochrome(
        bitmap = input,
        color = floatArrayOf(
            value.second.red,
            value.second.green,
            value.second.blue,
            value.first
        ),
        exposure = 1f
    )

}