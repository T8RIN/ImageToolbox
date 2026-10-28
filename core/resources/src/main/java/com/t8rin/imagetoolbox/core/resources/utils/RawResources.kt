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

package com.t8rin.imagetoolbox.core.resources.utils

import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.RawRes
import org.tukaani.xz.XZInputStream
import java.io.InputStream
import java.util.zip.GZIPInputStream

internal fun Resources.openDecodedRawResource(
    @RawRes id: Int
): InputStream {
    val value = TypedValue().also { getValue(id, it, true) }
    val input = openRawResource(id).buffered()
    return when (value.string.toString().substringAfterLast('.')) {
        "xz" -> XZInputStream(input, 4 * 1024)
        "gz" -> GZIPInputStream(input)
        else -> input
    }
}