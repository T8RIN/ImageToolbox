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

package com.t8rin.imagetoolbox.feature.filters.data.model

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.core.ui.utils.helper.toModel
import com.t8rin.trickle.Trickle

@FilterInject
internal class PolkaDotFilter(
    override val value: Triple<Int, Int, ColorModel> = Triple(
        first = 10,
        second = 8,
        third = Color.Black.toModel()
    )
) : Transformation<Bitmap>, Filter.PolkaDot {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = if (value.third.colorInt == Color.Transparent.toArgb()) {
        Trickle.polkaDot(
            input = input,
            dotRadius = value.first,
            spacing = value.second
        )
    } else {
        Trickle.drawColorBehind(
            input = Trickle.polkaDot(
                input = input,
                dotRadius = value.first,
                spacing = value.second
            ),
            color = value.third.colorInt
        )
    }

}