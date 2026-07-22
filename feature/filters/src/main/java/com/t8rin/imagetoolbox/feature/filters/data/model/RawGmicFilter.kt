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

package com.t8rin.imagetoolbox.feature.filters.data.model

import android.graphics.Bitmap
import coil3.size.Size
import com.t8rin.gmic.GmicFilter
import com.t8rin.gmic.model.GmicException
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation
import com.t8rin.gmic.filters.RawGmicFilter as GmicRawFilter

@FilterInject
internal class RawGmicFilter(
    override val value: String = "fx_gmicky 0",
    private val onError: (String) -> Unit = {}
) : GMICFilterTransformation(), Filter.RawGmic {

    override val cacheKey: String
        get() = value

    override fun createFilter(): GmicFilter = GmicRawFilter(value)

    override suspend fun transform(
        input: Bitmap,
        size: Size
    ): Bitmap {
        pushError("")

        return try {
            super.transform(input, size)
        } catch (exception: GmicException) {
            pushError(exception.message.orEmpty().substringAfterLast("***").trim())
            throw exception
        }
    }

    override fun pushError(error: String) = onError(error)

}
