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
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.model.BackupProgress
import com.t8rin.imagetoolbox.core.utils.appContextOrNull
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.io.File
import java.io.FilterInputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

internal object SettingsBackup {

    const val SETTINGS_PATH = "datastore/$GLOBAL_STORAGE_NAME.preferences_pb"

    fun settingsFile(filesDir: File): File = File(filesDir, SETTINGS_PATH)

    fun reset(filesDir: File) {
        settingsFile(filesDir).apply {
            delete()
            createNewFile()
        }
    }

    suspend fun size(filesDir: File): Long {
        var size = 0L
        backupFiles(filesDir).filter(File::isFile).forEach {
            currentCoroutineContext().ensureActive()
            size += it.length()
        }
        return size
    }

    suspend fun write(
        filesDir: File,
        output: OutputStream,
        fullBackup: Boolean,
        onProgress: (BackupProgress) -> Unit = {}
    ) = coroutineScope {
        onProgress(BackupProgress())
        if (!fullBackup) {
            val file = settingsFile(filesDir)
            val reporter = ProgressReporter(file.length(), onProgress)
            output.buffered().use { destination ->
                file.inputStream().use { it.copyBackupTo(destination, reporter::add) }
            }
            reporter.finish()
            return@coroutineScope
        }

        val root = filesDir.canonicalFile
        val files = mutableListOf<File>()
        backupFiles(root).forEach {
            currentCoroutineContext().ensureActive()
            files.add(it)
        }
        val reporter = ProgressReporter(
            totalBytes = files.filter(File::isFile).sumOf(File::length),
            onProgress = onProgress
        )
        ZipOutputStream(output.buffered()).use { zip ->
            zip.setLevel(Deflater.BEST_SPEED)
            files.forEach { file ->
                currentCoroutineContext().ensureActive()
                val name = file.relativeTo(root).invariantSeparatorsPath
                zip.putNextEntry(ZipEntry(if (file.isDirectory) "$name/" else name))
                if (file.isFile) {
                    file.inputStream().use { it.copyBackupTo(zip, reporter::add) }
                }
                zip.closeEntry()
            }
        }
        reporter.finish()
    }

    suspend fun read(
        input: InputStream,
        directory: File,
        totalBytes: Long? = null,
        onProgress: (BackupProgress) -> Unit = {}
    ): File = coroutineScope {
        val reporter = ProgressReporter(totalBytes, onProgress)
        val source = object : FilterInputStream(input) {
            override fun read(): Int = `in`.read().also {
                if (it >= 0) reporter.add(1)
            }

            override fun read(bytes: ByteArray, offset: Int, length: Int): Int =
                `in`.read(bytes, offset, length).also {
                    if (it > 0) reporter.add(it.toLong())
                }
        }.buffered()
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
            val file = settingsFile(directory)
            check(file.parentFile!!.mkdirs() || file.parentFile!!.isDirectory)
            file.outputStream().use { source.copyBackupTo(it) }
        }

        return@coroutineScope settingsFile(directory).also {
            if (!it.isFile) {
                throw IllegalArgumentException(
                    appContextOrNull?.getString(R.string.corrupted_file_or_not_a_backup)
                        ?: "Backup does not contain settings"
                )
            }

            reporter.finish()
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

    private fun backupFiles(filesDir: File): Sequence<File> {
        val root = filesDir.canonicalFile
        return root.walkTopDown()
            .onEnter { it.canonicalFile == it.absoluteFile }
            .onFail { _, error -> throw error }
            .filter { it != root && it.canonicalFile == it.absoluteFile }
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
        output: OutputStream,
        onCopied: (Long) -> Unit = {}
    ) = coroutineScope {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (true) {
            currentCoroutineContext().ensureActive()
            val count = read(buffer)
            if (count < 0) break
            output.write(buffer, 0, count)
            onCopied(count.toLong())
        }
    }

    private class ProgressReporter(
        private val totalBytes: Long?,
        private val onProgress: (BackupProgress) -> Unit
    ) {
        private var completedBytes = 0L
        private var lastUpdate = System.nanoTime()

        init {
            report()
        }

        fun add(bytes: Long) {
            completedBytes += bytes
            val now = System.nanoTime()
            if (now - lastUpdate >= 100_000_000L) {
                lastUpdate = now
                report()
            }
        }

        fun finish() {
            completedBytes = maxOf(completedBytes, totalBytes ?: 0L)
            report()
        }

        private fun report() {
            onProgress(
                BackupProgress(
                    completedBytes = completedBytes,
                    totalBytes = totalBytes?.let { maxOf(it, completedBytes) }
                )
            )
        }
    }
}