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

package com.t8rin.imagetoolbox.feature.settings.data

import com.t8rin.imagetoolbox.core.domain.GLOBAL_STORAGE_NAME
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

internal object SettingsBackup {

    const val SETTINGS_PATH = "datastore/$GLOBAL_STORAGE_NAME.preferences_pb"

    suspend fun write(
        filesDir: File,
        output: OutputStream,
        fullBackup: Boolean
    ) = coroutineScope {
        if (!fullBackup) {
            output.buffered().use { destination ->
                File(filesDir, SETTINGS_PATH).inputStream().use { it.copyBackupTo(destination) }
            }
            return@coroutineScope
        }

        val root = filesDir.canonicalFile
        ZipOutputStream(output.buffered()).use { zip ->
            zip.setLevel(Deflater.BEST_SPEED)
            root.walkTopDown()
                .onEnter { it.canonicalFile == it.absoluteFile }
                .onFail { _, error -> throw error }
                .filter { it != root && it.canonicalFile == it.absoluteFile }
                .forEach { file ->
                    currentCoroutineContext().ensureActive()
                    val name = file.relativeTo(root).invariantSeparatorsPath
                    zip.putNextEntry(ZipEntry(if (file.isDirectory) "$name/" else name))
                    if (file.isFile) {
                        file.inputStream().use { it.copyBackupTo(zip) }
                    }
                    zip.closeEntry()
                }
        }
    }

    suspend fun read(
        input: InputStream,
        directory: File
    ): File = coroutineScope {
        val source = input.buffered()
        source.mark(4)
        val isArchive = source.read() == 0x50 && source.read() == 0x4b &&
                source.read() == 0x03 && source.read() == 0x04
        source.reset()

        if (isArchive) {
            ZipInputStream(source).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    currentCoroutineContext().ensureActive()
                    val file = resolveFile(directory, entry.name)
                    if (entry.isDirectory) {
                        check(file.mkdirs() || file.isDirectory)
                    } else {
                        check(!file.exists()) { "Duplicate backup entry: ${entry.name}" }
                        check(file.parentFile!!.mkdirs() || file.parentFile!!.isDirectory)
                        file.outputStream().use { zip.copyBackupTo(it) }
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
        } else {
            val file = File(directory, SETTINGS_PATH)
            check(file.parentFile!!.mkdirs() || file.parentFile!!.isDirectory)
            file.outputStream().use { source.copyBackupTo(it) }
        }

        return@coroutineScope File(directory, SETTINGS_PATH).also {
            require(it.isFile) { "Settings are missing from the backup" }
        }
    }

    suspend fun restoreFiles(
        directory: File,
        filesDir: File
    ) {
        val files = directory.walkTopDown().filter { it != directory }.map { file ->
            val name = file.relativeTo(directory).invariantSeparatorsPath
            val destination = resolveFile(filesDir, name)
            check(!destination.exists() || file.isDirectory == destination.isDirectory) {
                "Cannot restore backup entry: $name"
            }
            file to destination
        }.toList()

        files.forEach { (file, destination) ->
            currentCoroutineContext().ensureActive()
            if (file.isDirectory) {
                check(destination.mkdirs() || destination.isDirectory)
            } else if (file.relativeTo(directory).invariantSeparatorsPath != SETTINGS_PATH) {
                if (!file.renameTo(destination)) {
                    file.inputStream().use { input ->
                        destination.outputStream().use { input.copyBackupTo(it) }
                    }
                }
            }
        }
    }

    private fun resolveFile(directory: File, name: String): File {
        require(!File(name).isAbsolute && name.split('/').none { it == ".." }) {
            "Invalid backup entry: $name"
        }
        val file = File(directory, name).canonicalFile
        require(file.path.startsWith(directory.canonicalPath + File.separator)) {
            "Invalid backup entry: $name"
        }
        return file
    }

    private suspend fun InputStream.copyBackupTo(
        output: OutputStream
    ) = coroutineScope {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (true) {
            currentCoroutineContext().ensureActive()
            val count = read(buffer)
            if (count < 0) break
            output.write(buffer, 0, count)
        }
    }
}