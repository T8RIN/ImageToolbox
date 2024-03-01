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

package ru.tech.imageresizershrinker.feature.zip.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.feature.zip.domain.ZipManager
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

internal class ZipManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ZipManager {

    override suspend fun zip(
        files: List<String>,
        onProgress: () -> Unit
    ): ByteArray = withContext(Dispatchers.IO) {
        ByteArrayOutputStream().apply {
            use { out ->
                ZipOutputStream(BufferedOutputStream(out)).use { output ->
                    files.forEach { file ->
                        withContext(Dispatchers.IO) {
                            context.contentResolver.openInputStream(file.toUri()).use { input ->
                                BufferedInputStream(input).use { origin ->
                                    val entry = ZipEntry(file.toUri().getFilename())
                                    output.putNextEntry(entry)
                                    origin.copyTo(output, 1024)
                                }
                            }
                        }
                        onProgress()
                    }
                }
            }
        }.toByteArray()
    }

    private fun Uri.getFilename(): String = DocumentFile.fromSingleUri(context, this)?.name ?: ""

}