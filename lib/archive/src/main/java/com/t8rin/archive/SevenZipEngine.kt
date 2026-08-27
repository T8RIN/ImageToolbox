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

package com.t8rin.archive

import android.os.ParcelFileDescriptor
import org.apache.commons.compress.PasswordRequiredException
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import org.apache.commons.compress.archivers.sevenz.SevenZMethod
import org.apache.commons.compress.archivers.sevenz.SevenZMethodConfiguration
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile
import org.tukaani.xz.LZMA2Options
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.OutputStream
import java.nio.channels.SeekableByteChannel

internal object SevenZipEngine {

    fun createEncrypted(
        sources: List<ArchiveSource>,
        outputChannel: SeekableByteChannel,
        compressionMethod: SevenZipCompressionMethod,
        passphrase: String,
        onChunk: () -> Unit,
        onProgress: () -> Unit
    ) {
        require(compressionMethod.supportsEncryption) {
            "7Z with ${compressionMethod.title} does not support encryption"
        }
        SevenZOutputFile(
            CloseShieldSeekableByteChannel(outputChannel),
            passphrase.toCharArray()
        ).use { archive ->
            archive.setContentMethods(listOf(compressionMethod.toSevenZConfiguration()))
            sources.forEach { source ->
                require(source.size >= 0L) { "Unknown file size: ${source.name}" }
                val safeName = ArchivePath.safeSegments(source.name)
                    ?.joinToString("/")
                    ?: error("Unsafe archive entry name: ${source.name}")
                val entry = SevenZArchiveEntry().apply {
                    name = safeName
                    size = source.size
                }
                archive.putArchiveEntry(entry)
                source.openStream().use { rawInput ->
                    val input = BufferedInputStream(rawInput, BufferSize)
                    val buffer = ByteArray(BufferSize)
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        archive.write(buffer, 0, count)
                        onChunk()
                    }
                }
                archive.closeArchiveEntry()
                onProgress()
            }
        }
    }

    fun extract(
        inputFileDescriptor: Int,
        passphrase: String,
        limits: ExtractionLimits,
        onEntry: (ArchiveEntryInfo, writeData: (OutputStream) -> Unit) -> Unit,
        onChunk: () -> Unit,
        onProgress: () -> Unit
    ): Int {
        var extractedEntries = 0
        var declaredSize = 0L
        var actualSize = 0L
        open(inputFileDescriptor, passphrase).use { archive ->
            var entry = archive.nextEntry
            while (entry != null) {
                check(extractedEntries < limits.maxEntries) {
                    "Archive contains too many entries"
                }
                val size = entry.size.coerceAtLeast(0L)
                check(size <= limits.maxEntrySizeBytes) { "Archive entry is too large" }
                declaredSize = Math.addExact(declaredSize, size)
                check(declaredSize <= limits.maxTotalSizeBytes) {
                    "Unpacked archive is too large"
                }

                var consumed = false
                onEntry(
                    ArchiveEntryInfo(
                        path = entry.name,
                        size = size,
                        isDirectory = entry.isDirectory
                    )
                ) { outputStream ->
                    check(!entry.isDirectory && !consumed) {
                        "Archive entry data already consumed"
                    }
                    consumed = true
                    val output = BufferedOutputStream(outputStream, BufferSize)
                    actualSize = copyCurrentEntry(
                        archive = archive,
                        output = output,
                        entrySize = size,
                        actualTotalSize = actualSize,
                        limits = limits,
                        onChunk = onChunk
                    )
                    output.flush()
                }
                if (!entry.isDirectory && !consumed) {
                    actualSize = copyCurrentEntry(
                        archive = archive,
                        output = NullOutputStream,
                        entrySize = size,
                        actualTotalSize = actualSize,
                        limits = limits,
                        onChunk = onChunk
                    )
                }
                extractedEntries++
                onProgress()
                entry = archive.nextEntry
            }
        }
        return extractedEntries
    }

    fun encryptionStatus(inputFileDescriptor: Int): ArchiveEncryptionStatus = try {
        open(inputFileDescriptor, passphrase = null).use { archive ->
            val probe = ByteArray(1)
            var encrypted = false
            var entry = archive.nextEntry
            while (entry != null) {
                if (entry.contentMethods?.any {
                        it.method == SevenZMethod.AES256SHA256
                    } == true
                ) {
                    encrypted = true
                    break
                }
                if (!entry.isDirectory) {
                    archive.read(probe)
                }
                entry = archive.nextEntry
            }
            if (encrypted) {
                ArchiveEncryptionStatus.PasswordRequired
            } else {
                ArchiveEncryptionStatus.None
            }
        }
    } catch (_: PasswordRequiredException) {
        ArchiveEncryptionStatus.PasswordRequired
    }

    private fun open(inputFileDescriptor: Int, passphrase: String?): SevenZFile {
        val channel = ParcelFileDescriptor.AutoCloseInputStream(
            ParcelFileDescriptor.fromFd(inputFileDescriptor)
        ).channel
        channel.position(0)
        return SevenZFile.builder()
            .setSeekableByteChannel(channel)
            .setMaxMemoryLimitKiB(MaxSevenZipMemoryKiB)
            .apply {
                passphrase?.let(::setPassword)
            }
            .get()
    }

    private fun copyCurrentEntry(
        archive: SevenZFile,
        output: OutputStream,
        entrySize: Long,
        actualTotalSize: Long,
        limits: ExtractionLimits,
        onChunk: () -> Unit
    ): Long {
        val buffer = ByteArray(BufferSize)
        var actualEntrySize = 0L
        var totalSize = actualTotalSize
        while (true) {
            val count = archive.read(buffer)
            if (count < 0) break
            actualEntrySize = Math.addExact(actualEntrySize, count.toLong())
            check(actualEntrySize <= limits.maxEntrySizeBytes) {
                "Archive entry is too large"
            }
            totalSize = Math.addExact(totalSize, count.toLong())
            check(totalSize <= limits.maxTotalSizeBytes) {
                "Unpacked archive is too large"
            }
            output.write(buffer, 0, count)
            onChunk()
        }
        if (entrySize > 0L) {
            check(actualEntrySize <= entrySize) { "Archive entry exceeds declared size" }
        }
        return totalSize
    }
}

private fun SevenZipCompressionMethod.toSevenZConfiguration(): SevenZMethodConfiguration =
    when (this) {
        SevenZipCompressionMethod.Copy -> SevenZMethodConfiguration(SevenZMethod.COPY)
        SevenZipCompressionMethod.Deflate -> SevenZMethodConfiguration(SevenZMethod.DEFLATE)
        SevenZipCompressionMethod.Bzip2 -> SevenZMethodConfiguration(SevenZMethod.BZIP2)
        SevenZipCompressionMethod.Lzma2 -> SevenZMethodConfiguration(
            SevenZMethod.LZMA2,
            LZMA2Options().apply {
                dictSize = Lzma2DictionarySize
                mode = LZMA2Options.MODE_FAST
                matchFinder = LZMA2Options.MF_HC4
            }
        )

        SevenZipCompressionMethod.Lzma1,
        SevenZipCompressionMethod.Ppmd -> error("Unsupported encrypted 7Z method: $title")
    }

private class CloseShieldSeekableByteChannel(
    private val delegate: SeekableByteChannel
) : SeekableByteChannel by delegate {
    override fun close() = Unit
}

private object NullOutputStream : OutputStream() {
    override fun write(b: Int) = Unit
    override fun write(b: ByteArray, off: Int, len: Int) = Unit
}

private const val BufferSize = 64 * 1024
private const val MaxSevenZipMemoryKiB = 64 * 1024
private const val Lzma2DictionarySize = 1024 * 1024
