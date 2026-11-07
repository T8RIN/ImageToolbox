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

package com.t8rin.imagetoolbox.collage_maker.data

import android.content.Context
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.t8rin.collages.layout.CollageLayoutStore
import com.t8rin.imagetoolbox.collage_maker.data.project.CollageProjectFile
import com.t8rin.imagetoolbox.collage_maker.data.project.toFile
import com.t8rin.imagetoolbox.collage_maker.data.project.toProject
import com.t8rin.imagetoolbox.collage_maker.domain.CollageProject
import com.t8rin.imagetoolbox.collage_maker.domain.CollageProjectRepository
import com.t8rin.imagetoolbox.core.data.utils.outputStream
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.json.JsonParser
import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.createZip
import com.t8rin.imagetoolbox.core.utils.extension
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.core.utils.putEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.inject.Inject

internal class AndroidCollageProjectRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val jsonParser: JsonParser,
    dispatchersHolder: DispatchersHolder
) : CollageProjectRepository, DispatchersHolder by dispatchersHolder {

    private val cacheDirectory = File(context.cacheDir, "collage-projects/${UUID.randomUUID()}")

    override suspend fun save(
        destination: Writeable,
        project: CollageProject
    ) = withContext(ioDispatcher) {
        val assets = linkedMapOf<String, Int>()
        val projectFile = project.toFile(assets)
        val json = requireNotNull(jsonParser.toJson(projectFile, CollageProjectFile::class.java))

        destination.outputStream().createZip { zip ->
            zip.setLevel(Deflater.NO_COMPRESSION)
            zip.putEntry(ProjectEntry) { it.write(json.encodeToByteArray()) }
            assets.forEach { (asset, index) ->
                val uri = asset.toUri()
                val extension = uri.filename(context)?.substringAfterLast('.', "")
                    ?.takeIf(ImageExtension::matches)
                    ?: uri.extension(context)?.takeIf(ImageExtension::matches)
                    ?: "img"
                zip.putNextEntry(ZipEntry("images/$index.$extension"))
                requireNotNull(context.contentResolver.openInputStream(uri)).use { input ->
                    require(input.copyToCancellable(zip) > 0L)
                }
                zip.closeEntry()
            }
        }
    }

    override suspend fun open(uri: String): CollageProject {
        val directory = File(cacheDirectory, UUID.randomUUID().toString())
        var loaded = false
        try {
            val project = withContext(ioDispatcher) {
                check(directory.mkdirs())
                readArchive(uri, directory)
            }
            loaded = true
            return project
        } finally {
            if (!loaded) withContext(NonCancellable + ioDispatcher) {
                directory.deleteRecursively()
            }
        }
    }

    override fun clearCache() {
        cacheDirectory.deleteRecursively()
    }

    private suspend fun readArchive(uri: String, directory: File): CollageProject {
        val images = mutableMapOf<Int, String>()
        var projectFile: CollageProjectFile? = null
        val input = requireNotNull(context.contentResolver.openInputStream(uri.toUri()))
        ZipInputStream(input).use { zip ->
            while (true) {
                currentCoroutineContext().ensureActive()
                val entry = zip.nextEntry ?: break
                when {
                    entry.isDirectory -> Unit
                    entry.name == ProjectEntry -> {
                        require(projectFile == null)
                        val output = ByteArrayOutputStream()
                        zip.copyToCancellable(output)
                        projectFile = requireNotNull(
                            jsonParser.fromJson<CollageProjectFile>(
                                json = output.toString(Charsets.UTF_8.name()),
                                type = CollageProjectFile::class.java
                            )
                        )
                    }

                    else -> {
                        val match = requireNotNull(ImageEntry.matchEntire(entry.name))
                        val index = match.groupValues[1].toInt()
                        require(index !in images)
                        val file = File(directory, "$index.${match.groupValues[2]}")
                        file.outputStream().use { output ->
                            require(zip.copyToCancellable(output) > 0L)
                        }
                        images[index] = FileProvider.getUriForFile(
                            context,
                            context.getString(R.string.file_provider),
                            file
                        ).toString()
                    }
                }
                zip.closeEntry()
            }
        }
        return requireNotNull(projectFile).toProject(images).also { project ->
            (project.history + project.redoHistory + project.current)
                .map { it.uris.orEmpty().size }
                .distinct()
                .forEach { CollageLayoutStore.load(it) }
        }
    }

    private suspend fun InputStream.copyToCancellable(output: OutputStream): Long {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var total = 0L
        while (true) {
            currentCoroutineContext().ensureActive()
            val count = read(buffer)
            if (count < 0) return total
            output.write(buffer, 0, count)
            total += count
        }
    }

    private companion object {
        const val ProjectEntry = "collage.json"
        val ImageExtension = Regex("[a-zA-Z0-9]{1,12}")
        val ImageEntry = Regex("images/([0-9]+)\\.([a-zA-Z0-9]{1,12})")
    }
}