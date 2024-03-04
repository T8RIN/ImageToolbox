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
        onProgress: suspend (originalUri: String, data: ByteArray) -> Unit
    ) {
        jpegUris.forEach { uri ->
            val bytes = context.contentResolver.openInputStream(uri.toUri())?.use {
                it.readBytes()
            } ?: return

            onProgress(uri, JxlCoder.construct(bytes))
        }
    }

    override suspend fun jxlToJpeg(
        jxlUris: List<String>,
        onProgress: suspend (originalUri: String, data: ByteArray) -> Unit
    ) {
        jxlUris.forEach { uri ->
            val bytes = context.contentResolver.openInputStream(uri.toUri())?.use {
                it.readBytes()
            } ?: return

            onProgress(uri, JxlCoder.reconstructJPEG(bytes))
        }
    }


}