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

package com.t8rin.collages.model

import android.graphics.Matrix
import android.graphics.Path
import androidx.core.graphics.PathParser
import com.t8rin.collages.geometry.flattenMask
import org.json.JSONObject

internal class CollageMask(private val definition: JSONObject) {
    val preserveShape: Boolean = definition.optBoolean("preserveShape")

    val path: Path by lazy {
        checkNotNull(
            PathParser.createPathFromPathData(definition.getString("path"))
        ).apply {
            if (definition.optBoolean("evenOdd")) fillType = Path.FillType.EVEN_ODD
            transform(
                Matrix().apply {
                    setScale(
                        1f / definition.getDouble("width").toFloat(),
                        1f / definition.getDouble("height").toFloat()
                    )
                }
            )
        }
    }

    val insetPath: Path by lazy {
        Path(path).apply {
            transform(Matrix().apply { setScale(1000f, 1000f) })
        }.flattenMask().apply {
            transform(Matrix().apply { setScale(.001f, .001f) })
        }
    }
}