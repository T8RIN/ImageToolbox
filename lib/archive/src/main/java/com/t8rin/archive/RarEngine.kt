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
import com.github.junrar.Archive
import com.github.junrar.ArchiveOptions
import com.github.junrar.exception.WrongPasswordException
import com.github.junrar.io.SeekableReadOnlyByteChannel
import com.github.junrar.volume.Volume
import com.github.junrar.volume.VolumeManager
import java.io.BufferedOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

internal object RarEngine {

    fun extract(
        inputFileDescriptor: Int,
        passphrase: String?,
        limits: ExtractionLimits,
        onEntry: (ArchiveEntryInfo, writeData: (OutputStream) -> Unit) -> Unit,
        onChunk: () -> Unit,
        onProgress: () -> Unit
    ): Int {
        var extractedEntries = 0
        var declaredSize = 0L
        var actualSize = 0L
        open(inputFileDescriptor, passphrase).use { archive ->
            archive.fileHeaders.forEach { entry ->
                check(extractedEntries < limits.maxEntries) {
                    "Archive contains too many entries"
                }
                val size = if (entry.isUnpSizeUnknown) {
                    0L
                } else {
                    entry.fullUnpackSize.coerceAtLeast(0L)
                }
                check(size <= limits.maxEntrySizeBytes) { "Archive entry is too large" }
                declaredSize = Math.addExact(declaredSize, size)
                check(declaredSize <= limits.maxTotalSizeBytes) {
                    "Unpacked archive is too large"
                }

                var consumed = false
                onEntry(
                    ArchiveEntryInfo(
                        path = entry.fileName,
                        size = size,
                        isDirectory = entry.isDirectory
                    )
                ) { outputStream ->
                    check(!entry.isDirectory && !consumed) {
                        "Archive entry data already consumed"
                    }
                    consumed = true
                    val output = BufferedOutputStream(outputStream, BufferSize)
                    val limitingOutput = LimitingOutputStream(
                        delegate = output,
                        entryLimit = limits.maxEntrySizeBytes,
                        totalLimit = limits.maxTotalSizeBytes,
                        initialTotal = actualSize,
                        onChunk = onChunk
                    )
                    archive.extractFile(entry, limitingOutput)
                    limitingOutput.flush()
                    actualSize = limitingOutput.totalSize
                }
                if (!entry.isDirectory && !consumed) {
                    val limitingOutput = LimitingOutputStream(
                        delegate = DiscardingOutputStream,
                        entryLimit = limits.maxEntrySizeBytes,
                        totalLimit = limits.maxTotalSizeBytes,
                        initialTotal = actualSize,
                        onChunk = onChunk
                    )
                    archive.extractFile(entry, limitingOutput)
                    actualSize = limitingOutput.totalSize
                }
                extractedEntries++
                onProgress()
            }
        }
        return extractedEntries
    }

    fun listEntries(
        inputFileDescriptor: Int,
        passphrase: String?,
        limits: ExtractionLimits
    ): List<ArchiveEntryInfo> = open(inputFileDescriptor, passphrase).use { archive ->
        archive.fileHeaders.mapIndexed { index, entry ->
            check(index < limits.maxEntries) {
                "Archive contains too many entries"
            }
            ArchiveEntryInfo(
                path = entry.fileName,
                size = if (entry.isUnpSizeUnknown) {
                    0L
                } else {
                    entry.fullUnpackSize.coerceAtLeast(0L)
                },
                isDirectory = entry.isDirectory
            )
        }
    }

    fun encryptionStatus(inputFileDescriptor: Int): ArchiveEncryptionStatus = try {
        open(inputFileDescriptor, passphrase = null).use { archive ->
            if (archive.isPasswordProtected) {
                ArchiveEncryptionStatus.PasswordRequired
            } else {
                ArchiveEncryptionStatus.None
            }
        }
    } catch (_: WrongPasswordException) {
        ArchiveEncryptionStatus.PasswordRequired
    }

    private fun open(inputFileDescriptor: Int, passphrase: String?): Archive {
        val descriptor = ParcelFileDescriptor.fromFd(inputFileDescriptor)
        val channel = ParcelFileDescriptor.AutoCloseInputStream(descriptor).channel
        val options = ArchiveOptions.builder()
            .maxDictionarySize(MaxRarDictionarySize)
            .apply { passphrase?.let { password(it.toCharArray()) } }
            .build()
        return Archive(SingleVolumeManager(channel), options)
    }
}

private class SingleVolumeManager(
    private val channel: FileChannel
) : VolumeManager {
    override fun nextVolume(archive: Archive, lastVolume: Volume?): Volume? {
        if (lastVolume != null) return null
        return object : Volume {
            override fun getChannel(): SeekableReadOnlyByteChannel =
                FileChannelAdapter(this@SingleVolumeManager.channel)

            override fun getLength(): Long = this@SingleVolumeManager.channel.size()
            override fun getArchive(): Archive = archive
        }
    }
}

private class FileChannelAdapter(
    private val channel: FileChannel
) : SeekableReadOnlyByteChannel {
    override fun getPosition(): Long = channel.position()
    override fun setPosition(pos: Long) {
        channel.position(pos)
    }

    override fun read(): Int {
        val buffer = ByteBuffer.allocate(1)
        return if (channel.read(buffer) < 0) -1 else buffer.array()[0].toInt() and 0xFF
    }

    override fun read(buffer: ByteArray, off: Int, count: Int): Int =
        channel.read(ByteBuffer.wrap(buffer, off, count))

    override fun readFully(buffer: ByteArray, count: Int): Int {
        var offset = 0
        while (offset < count) {
            val read = read(buffer, offset, count - offset)
            if (read <= 0) break
            offset += read
        }
        return offset
    }

    override fun close() = channel.close()
}

private class LimitingOutputStream(
    private val delegate: OutputStream,
    private val entryLimit: Long,
    private val totalLimit: Long,
    initialTotal: Long,
    private val onChunk: () -> Unit
) : OutputStream() {
    private var entrySize = 0L
    var totalSize = initialTotal
        private set

    override fun write(b: Int) {
        ensureCapacity(1)
        delegate.write(b)
        onChunk()
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        ensureCapacity(len)
        delegate.write(b, off, len)
        onChunk()
    }

    override fun flush() = delegate.flush()

    private fun ensureCapacity(count: Int) {
        entrySize = Math.addExact(entrySize, count.toLong())
        check(entrySize <= entryLimit) { "Archive entry is too large" }
        totalSize = Math.addExact(totalSize, count.toLong())
        check(totalSize <= totalLimit) { "Unpacked archive is too large" }
    }
}

private object DiscardingOutputStream : OutputStream() {
    override fun write(b: Int) = Unit
    override fun write(b: ByteArray, off: Int, len: Int) = Unit
}

private const val BufferSize = 64 * 1024
private const val MaxRarDictionarySize = 128L * 1024L * 1024L
