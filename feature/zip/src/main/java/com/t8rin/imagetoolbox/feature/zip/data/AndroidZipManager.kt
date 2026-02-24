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

package com.t8rin.imagetoolbox.feature.zip.data

import android.content.Context
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.data.saving.io.UriReadable
import com.t8rin.imagetoolbox.core.data.utils.outputStream
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.utils.createZip
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.core.utils.putEntry
import com.t8rin.imagetoolbox.feature.zip.domain.ZipManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class AndroidZipManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shareProvider: ShareProvider,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, ZipManager {

    override suspend fun zip(
        files: List<String>,
        onProgress: () -> Unit
    ): String = withContext(defaultDispatcher) {
        shareProvider.cacheData(
            writeData = { writeable ->
                writeable.outputStream().createZip { output ->
                    for (file in files) {
                        output.putEntry(
                            name = file.toUri().filename(context) ?: continue,
                            input = UriReadable(file.toUri(), context).stream
                        )
                        onProgress()
                    }
                }
            },
            filename = files.firstOrNull()?.toUri()?.filename() ?: "temp.zip"
        ) ?: throw IllegalArgumentException("Cached to null file")
    }

}