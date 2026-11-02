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

import com.t8rin.imagetoolbox.core.settings.domain.model.BackupProgress
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class SettingsBackupTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun settingsBackupKeepsTheExistingFormat() = runBlocking {
        val filesDir = temporaryFolder.newFolder()
        val settings = byteArrayOf(10, 8, 10, 4, 116, 101, 115, 116, 18, 0)
        filesDir.write(SettingsBackup.SETTINGS_PATH, settings)
        filesDir.write("ai_models/model.onnx", byteArrayOf(1, 2, 3))
        val output = ByteArrayOutputStream()

        SettingsBackup.write(filesDir, output, fullBackup = false)

        assertArrayEquals(settings, output.toByteArray())
        val staging = temporaryFolder.newFolder()
        val restored = SettingsBackup.read(output.toByteArray().inputStream(), staging)
        assertArrayEquals(settings, restored.readBytes())
        assertFalse(File(staging, "ai_models").exists())
    }

    @Test
    fun fullBackupRestoresNestedFilesAndPreservesSettingsForDataStore() = runBlocking {
        val filesDir = temporaryFolder.newFolder()
        val contents = mapOf(
            SettingsBackup.SETTINGS_PATH to "settings".toByteArray(),
            "ai_models/assistant/model.onnx" to ByteArray(1024 * 1024) { (it % 251).toByte() },
            "onnx/model.onnx" to byteArrayOf(1, 2, 3),
            "tesseract/Русский/tessdata/rus.traineddata" to byteArrayOf(4, 5, 6),
            ".hidden" to "hidden".toByteArray(),
            "assistant/conversation.json" to "[]".toByteArray()
        )
        contents.forEach { (name, bytes) -> filesDir.write(name, bytes) }
        File(filesDir, "empty").mkdir()
        val output = ByteArrayOutputStream()
        SettingsBackup.write(filesDir, output, fullBackup = true)
        val staging = temporaryFolder.newFolder()

        SettingsBackup.read(output.toByteArray().inputStream(), staging)

        contents.forEach { (name, bytes) ->
            assertArrayEquals(name, bytes, File(staging, name).readBytes())
        }
        val destination = temporaryFolder.newFolder()
        destination.write(SettingsBackup.SETTINGS_PATH, "current settings".toByteArray())
        destination.write("onnx/model.onnx", byteArrayOf(9))
        SettingsBackup.restoreFiles(staging, destination)

        assertEquals("current settings", File(destination, SettingsBackup.SETTINGS_PATH).readText())
        contents.filterKeys { it != SettingsBackup.SETTINGS_PATH }.forEach { (name, bytes) ->
            assertArrayEquals(name, bytes, File(destination, name).readBytes())
        }
        assertTrue(File(destination, "empty").isDirectory)
    }

    @Test
    fun archivesWithoutSettingsAreRejected() {
        val archive = archive("ai_models/model.onnx" to byteArrayOf(1, 2, 3))

        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                SettingsBackup.read(archive.inputStream(), temporaryFolder.newFolder())
            }
        }
    }

    @Test
    fun archiveEntriesCannotEscapeTheStagingDirectory() {
        listOf("../outside", "models/../../outside", "/outside").forEach { name ->
            val archive = archive(
                SettingsBackup.SETTINGS_PATH to "settings".toByteArray(),
                name to byteArrayOf(1, 2, 3)
            )

            assertThrows(IllegalArgumentException::class.java) {
                runBlocking {
                    SettingsBackup.read(archive.inputStream(), temporaryFolder.newFolder())
                }
            }
        }
        assertFalse(File(temporaryFolder.root, "outside").exists())
    }

    @Test
    fun symbolicLinksCannotWriteOutsideFilesDir() = runBlocking {
        val staging = temporaryFolder.newFolder()
        staging.write(SettingsBackup.SETTINGS_PATH, "settings".toByteArray())
        staging.write("ai_models/model.onnx", byteArrayOf(1, 2, 3))
        val destination = temporaryFolder.newFolder()
        val outside = temporaryFolder.newFolder()
        Files.createSymbolicLink(File(destination, "ai_models").toPath(), outside.toPath())

        assertThrows(IllegalArgumentException::class.java) {
            runBlocking { SettingsBackup.restoreFiles(staging, destination) }
        }

        assertFalse(File(outside, "model.onnx").exists())
    }

    @Test
    fun fullBackupDoesNotFollowSymbolicLinks() = runBlocking {
        val filesDir = temporaryFolder.newFolder()
        filesDir.write(SettingsBackup.SETTINGS_PATH, "settings".toByteArray())
        val outside = temporaryFolder.newFolder()
        outside.write("private", byteArrayOf(1, 2, 3))
        Files.createSymbolicLink(File(filesDir, "link").toPath(), outside.toPath())
        Files.createSymbolicLink(File(filesDir, "loop").toPath(), filesDir.toPath())
        val output = ByteArrayOutputStream()

        SettingsBackup.write(filesDir, output, fullBackup = true)

        val names = mutableListOf<String>()
        ZipInputStream(output.toByteArray().inputStream()).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                names.add(entry.name)
                entry = zip.nextEntry
            }
        }
        assertEquals(listOf("datastore/", SettingsBackup.SETTINGS_PATH), names)
    }

    @Test
    fun truncatedModelIsRejectedBeforeRestoringFiles() {
        val archive = archive(
            SettingsBackup.SETTINGS_PATH to "settings".toByteArray(),
            "ai_models/model.onnx" to ByteArray(1024 * 1024) { (it % 251).toByte() }
        )
        val staging = temporaryFolder.newFolder()

        assertThrows(java.io.IOException::class.java) {
            runBlocking {
                SettingsBackup.read(archive.copyOf(archive.size / 2).inputStream(), staging)
            }
        }
    }

    @Test
    fun backupProgressCountsOriginalBytesAndMatchesFolderSize() = runBlocking {
        val filesDir = temporaryFolder.newFolder()
        val settings = "settings".toByteArray()
        val model = ByteArray(1024 * 1024)
        filesDir.write(SettingsBackup.SETTINGS_PATH, settings)
        filesDir.write("ai_models/model.onnx", model)
        File(filesDir, "empty").mkdir()
        val updates = mutableListOf<BackupProgress>()

        SettingsBackup.write(filesDir, ByteArrayOutputStream(), fullBackup = true) {
            updates.add(it)
        }

        val expectedSize = settings.size.toLong() + model.size
        assertEquals(expectedSize, SettingsBackup.size(filesDir))
        assertNull(updates.first().totalBytes)
        val writing = updates.filter { it.totalBytes != null }
        assertEquals(0L, writing.first().completedBytes)
        assertTrue(writing.all { it.totalBytes == expectedSize })
        assertEquals(expectedSize, writing.last().completedBytes)
        assertEquals(1f, writing.last().fraction)
        assertTrue(writing.zipWithNext().all { (before, after) ->
            before.completedBytes <= after.completedBytes
        })
    }

    @Test
    fun restorationProgressCountsTheInputArchive() = runBlocking {
        val model = ByteArray(1024 * 1024)
        val archive = archive(
            SettingsBackup.SETTINGS_PATH to "settings".toByteArray(),
            "ai_models/model.onnx" to model
        )
        val staging = temporaryFolder.newFolder()
        val updates = mutableListOf<BackupProgress>()

        SettingsBackup.read(archive.inputStream(), staging, archive.size.toLong()) {
            updates.add(it)
        }
        val destination = temporaryFolder.newFolder()
        SettingsBackup.restoreFiles(staging, destination)

        assertEquals(archive.size.toLong(), updates.last().completedBytes)
        assertEquals(archive.size.toLong(), updates.last().totalBytes)
        assertEquals(1f, updates.last().fraction)
        assertArrayEquals(model, File(destination, "ai_models/model.onnx").readBytes())
    }

    @Test
    fun unknownInputSizeReportsBytesWithoutInventingAPercentage() = runBlocking {
        val settings = "settings".toByteArray()
        val updates = mutableListOf<BackupProgress>()

        SettingsBackup.read(settings.inputStream(), temporaryFolder.newFolder()) {
            updates.add(it)
        }

        assertEquals(settings.size.toLong(), updates.last().completedBytes)
        assertNull(updates.last().totalBytes)
        assertNull(updates.last().fraction)
    }

    private fun File.write(name: String, bytes: ByteArray) {
        File(this, name).apply {
            parentFile!!.mkdirs()
            writeBytes(bytes)
        }
    }

    private fun archive(vararg files: Pair<String, ByteArray>): ByteArray =
        ByteArrayOutputStream().also { output ->
            ZipOutputStream(output).use { zip ->
                files.forEach { (name, bytes) ->
                    zip.putNextEntry(ZipEntry(name))
                    zip.write(bytes)
                    zip.closeEntry()
                }
            }
        }.toByteArray()
}