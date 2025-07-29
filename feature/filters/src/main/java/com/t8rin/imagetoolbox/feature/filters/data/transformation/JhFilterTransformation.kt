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

package com.t8rin.imagetoolbox.feature.filters.data.transformation

import android.graphics.Bitmap
import coil3.size.Size
import com.jhlabs.JhFilter
import com.t8rin.imagetoolbox.core.data.utils.asCoil
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.feature.filters.data.utils.flexible
import coil3.transform.Transformation as CoilTransformation

internal abstract class JhFilterTransformation : CoilTransformation(), Transformation<Bitmap> {

    abstract fun createFilter(): JhFilter

    open fun createFilter(image: Bitmap): JhFilter = createFilter()

    override suspend fun transform(
        input: Bitmap,
        size: Size
    ): Bitmap = input.flexible(size).let {
        createFilter(it).filter(it)
    }

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = transform(input, size.asCoil())

}