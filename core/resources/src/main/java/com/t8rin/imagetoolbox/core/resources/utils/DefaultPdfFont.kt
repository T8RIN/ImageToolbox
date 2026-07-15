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

import android.content.Context
import com.t8rin.imagetoolbox.core.resources.R
import java.io.File
import java.util.zip.GZIPInputStream

val DefaultPdfFont get() = R.raw.image_toolbox_universal_bold

val Context.defaultPdfFontFile: File
    get() = FontCache.get(applicationContext)

private object FontCache {

    @Volatile
    private var cachedFile: File? = null

    fun get(context: Context): File {
        cachedFile?.takeIf { it.isValidFont() }?.let { return it }

        return synchronized(this) {
            cachedFile?.takeIf { it.isValidFont() } ?: unpack(context).also {
                cachedFile = it
            }
        }
    }

    private fun unpack(context: Context): File {
        val directory = File(context.filesDir, "pdf-fonts")
        check(directory.isDirectory || directory.mkdirs()) {
            "Failed to create PDF font cache directory"
        }

        val target = File(
            directory,
            "$NAME-$CACHE_KEY.ttf"
        )
        if (target.isValidFont()) return target

        target.delete()
        val temporary = File.createTempFile(NAME, ".tmp", directory)

        try {
            context.resources.openRawResource(DefaultPdfFont).use { compressed ->
                GZIPInputStream(compressed.buffered()).use { input ->
                    temporary.outputStream().buffered().use { output ->
                        input.copyTo(output)
                    }
                }
            }

            check(temporary.isValidFont()) {
                "Invalid unpacked PDF font"
            }

            if (!temporary.renameTo(target)) {
                temporary.copyTo(target, overwrite = true)
            }

            check(target.isValidFont()) {
                "Failed to cache unpacked PDF font"
            }

            directory.listFiles()
                ?.filter {
                    it != target && it.extension == "ttf" && it.name.startsWith(NAME)
                }
                ?.forEach(File::delete)

            return target
        } finally {
            temporary.delete()
        }
    }

    private fun File.isValidFont(): Boolean = isFile && length() == FILE_SIZE

    private const val CACHE_KEY = "5299e3b5fc3352dd"
    private const val FILE_SIZE = 5_359_124L
    private const val NAME = "image_toolbox_universal_bold"

}