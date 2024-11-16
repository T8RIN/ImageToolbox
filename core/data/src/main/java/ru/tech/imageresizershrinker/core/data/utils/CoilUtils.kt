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

package ru.tech.imageresizershrinker.core.data.utils

import android.graphics.Bitmap
import coil3.size.Size
import coil3.size.pxOrElse
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.transformation.Transformation
import coil3.transform.Transformation as CoilTransformation

fun Size.asDomain(): IntegerSize = if (this == Size.ORIGINAL) IntegerSize.Undefined
else IntegerSize(width.pxOrElse { 1 }, height.pxOrElse { 1 })

fun IntegerSize.asCoil(): Size = if (this == IntegerSize.Undefined) Size.ORIGINAL
else Size(width, height)

fun Transformation<Bitmap>.toCoil(): CoilTransformation = object : CoilTransformation() {
    override fun toString(): String = this@toCoil::class.simpleName.toString()

    override val cacheKey: String
        get() = this@toCoil.cacheKey

    override suspend fun transform(
        input: Bitmap,
        size: Size
    ): Bitmap = this@toCoil.transform(input, size.asDomain())

}

fun CoilTransformation.asDomain(): Transformation<Bitmap> = object : Transformation<Bitmap> {
    override val cacheKey: String
        get() = this@asDomain.cacheKey

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = this@asDomain.transform(input, size.asCoil())
}