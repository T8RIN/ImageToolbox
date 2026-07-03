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

package com.t8rin.imagetoolbox.core.data.coil

import coil3.map.Mapper
import coil3.request.Options
import coil3.toUri
import com.t8rin.imagetoolbox.core.utils.UriReplacements

internal object OriginalUriMapper : Mapper<Any, Any> {
    override fun map(
        data: Any,
        options: Options
    ): Any? = when (data) {
        is String -> UriReplacements.resolve(data).takeIf { it != data }
        is android.net.Uri -> UriReplacements.resolve(data).takeIf { it != data }
        is coil3.Uri -> UriReplacements.resolve(data.toString())
            .takeIf { it != data.toString() }
            ?.toUri()

        else -> null
    }
}