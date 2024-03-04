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

package ru.tech.imageresizershrinker.feature.jxl_tools.data

import android.content.Context
import androidx.core.net.toUri
import com.awxkee.jxlcoder.JxlCoder
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.tech.imageresizershrinker.feature.jxl_tools.domain.JxlTranscoder
import javax.inject.Inject


internal class AndroidJxlTranscoder @Inject constructor(
    @ApplicationContext private val context: Context
) : JxlTranscoder {

    override suspend fun jpegToJxl(
        jpegUris: List<String>,
        onProgress: suspend (String, ByteArray) -> Unit
    ) = jpegUris.forEach { uri ->
        uri.jxl?.let { onProgress(uri, it) }
    }

    override suspend fun jxlToJpeg(
        jxlUris: List<String>,
        onProgress: suspend (String, ByteArray) -> Unit
    ) = jxlUris.forEach { uri ->
        uri.jpeg?.let { onProgress(uri, it) }
    }

    private val String.jxl: ByteArray?
        get() = bytes?.let { JxlCoder.construct(it) }

    private val String.jpeg: ByteArray?
        get() = bytes?.let { JxlCoder.reconstructJPEG(it) }

    private val String.bytes: ByteArray?
        get() = context
            .contentResolver
            .openInputStream(toUri())?.use {
                it.readBytes()
            }

}