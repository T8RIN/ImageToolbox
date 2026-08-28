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
import android.system.Os
import android.system.OsConstants
import me.zhanghai.android.libarchive.Archive
import me.zhanghai.android.libarchive.ArchiveEntry
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel
import java.nio.charset.StandardCharsets

object ArchiveEngine {

    fun create(
        format: ArchiveFormat,
        sources: List<ArchiveSource>,
        outputStream: OutputStream? = null,
        outputChannel: SeekableByteChannel? = null,
        zipCompressionMethod: ZipCompressionMethod = ZipCompressionMethod.Deflate,
        sevenZipCompressionMethod: SevenZipCompressionMethod = SevenZipCompressionMethod.Lzma2,
        compressionLevel: ArchiveCompressionLevel = ArchiveCompressionLevel.Normal,
        passphrase: String? = null,
        onChunk: () -> Unit = {},
        onProgress: () -> Unit = {}
    ) {
        require(sources.isNotEmpty()) { "No files selected" }
        require(format.supportsMultipleFiles || sources.size == 1) {
            "${format.title} supports exactly one file"
        }
        val supportsEncryption = when (format) {
            ArchiveFormat.Zip -> zipCompressionMethod.supportsEncryption
            ArchiveFormat.SevenZip -> sevenZipCompressionMethod.supportsEncryption
            else -> false
        }
        require(passphrase.isNullOrEmpty() || supportsEncryption) {
            "${format.title} does not support encryption with the selected method"
        }
        if (format == ArchiveFormat.SevenZip && !passphrase.isNullOrEmpty()) {
            SevenZipEngine.createEncrypted(
                sources = sources,
                outputChannel = requireNotNull(outputChannel) {
                    "Encrypted 7Z output must be seekable"
                },
                compressionMethod = sevenZipCompressionMethod,
                compressionLevel = compressionLevel,
                passphrase = passphrase,
                onChunk = onChunk,
                onProgress = onProgress
            )
            return
        }

        val archive = Archive.writeNew()
        val destination = requireNotNull(outputStream) { "Output stream is required" }
        val brotliOutput = if (format.isBrotli()) BrotliOutputStream(destination) else null
        val output = BufferedOutputStream(brotliOutput ?: destination, BUFFER_SIZE)
        try {
            configureWriter(
                archive = archive,
                format = format,
                zipCompressionMethod = zipCompressionMethod,
                sevenZipCompressionMethod = sevenZipCompressionMethod
            )
            if (
                format.supportsCompressionLevel(
                    zipCompressionMethod = zipCompressionMethod,
                    sevenZipCompressionMethod = sevenZipCompressionMethod
                )
            ) {
                configureCompressionLevel(
                    archive = archive,
                    format = format,
                    compressionLevel = compressionLevel
                )
            }
            passphrase
                ?.takeIf(String::isNotEmpty)
                ?.let {
                    Archive.writeSetPassphrase(archive, it.toByteArray(StandardCharsets.UTF_8))
                    if (format == ArchiveFormat.Zip) {
                        Archive.writeSetFormatOption(
                            archive,
                            "zip".toByteArray(StandardCharsets.UTF_8),
                            "encryption".toByteArray(StandardCharsets.UTF_8),
                            "aes256".toByteArray(StandardCharsets.UTF_8)
                        )
                    }
                }
            Archive.writeSetBytesInLastBlock(archive, 1)
            Archive.writeOpen(
                archive,
                output,
                null,
                { _, stream, buffer ->
                    stream.writeRemaining(buffer)
                },
                null
            )

            sources.forEach { source ->
                require(source.size >= 0L) { "Unknown file size: ${source.name}" }
                val safeName = ArchivePath.safeSegments(source.name)
                    ?.joinToString("/")
                    ?: error("Unsafe archive entry name: ${source.name}")
                val entry = ArchiveEntry.new2(archive)
                try {
                    ArchiveEntry.setPathnameUtf8(entry, safeName)
                    ArchiveEntry.setFiletype(entry, ArchiveEntry.AE_IFREG)
                    ArchiveEntry.setPerm(entry, FILE_PERMISSIONS)
                    ArchiveEntry.setSize(entry, source.size)
                    Archive.writeHeader(archive, entry)

                    source.openStream().use { rawInput ->
                        val input = BufferedInputStream(rawInput, BUFFER_SIZE)
                        val bytes = ByteArray(BUFFER_SIZE)
                        val buffer = ByteBuffer.allocateDirect(BUFFER_SIZE)
                        while (true) {
                            val count = input.read(bytes)
                            if (count < 0) break
                            buffer.clear()
                            buffer.put(bytes, 0, count)
                            buffer.flip()
                            Archive.writeData(archive, buffer)
                            onChunk()
                        }
                    }
                    Archive.writeFinishEntry(archive)
                    onProgress()
                } finally {
                    ArchiveEntry.free(entry)
                }
            }

            Archive.writeClose(archive)
        } catch (throwable: Throwable) {
            runCatching { Archive.writeFail(archive) }
            throw throwable
        } finally {
            try {
                Archive.writeFree(archive)
            } finally {
                output.flush()
            }
        }
    }

    fun extract(
        inputFileDescriptor: Int,
        passphrase: String? = null,
        preferSevenZip: Boolean = false,
        preferRar: Boolean = false,
        forceRawFormat: Boolean = false,
        forceBrotli: Boolean = false,
        limits: ExtractionLimits = ExtractionLimits(),
        onEntry: (ArchiveEntryInfo, writeData: (OutputStream) -> Unit) -> Unit,
        onChunk: () -> Unit = {},
        onProgress: () -> Unit = {}
    ): Int {
        if (preferRar) {
            return RarEngine.extract(
                inputFileDescriptor = inputFileDescriptor,
                passphrase = passphrase,
                limits = limits,
                onEntry = onEntry,
                onChunk = onChunk,
                onProgress = onProgress
            )
        }
        if (preferSevenZip && !passphrase.isNullOrEmpty()) {
            return SevenZipEngine.extract(
                inputFileDescriptor = inputFileDescriptor,
                passphrase = passphrase,
                limits = limits,
                onEntry = onEntry,
                onChunk = onChunk,
                onProgress = onProgress
            )
        }
        val archive = Archive.readNew()
        var extractedEntries = 0
        var declaredSize = 0L
        var actualSize = 0L
        try {
            Archive.setCharset(archive, StandardCharsets.UTF_8.name().toByteArray())
            Archive.readSupportFilterAll(archive)
            if (forceRawFormat) {
                Archive.readSupportFormatRaw(archive)
            } else {
                Archive.readSupportFormatAll(archive)
            }
            passphrase
                ?.takeIf(String::isNotEmpty)
                ?.let { Archive.readAddPassphrase(archive, it.toByteArray()) }
            openReader(
                archive = archive,
                inputFileDescriptor = inputFileDescriptor,
                forceBrotli = forceBrotli
            )

            var entry = Archive.readNextHeader(archive)
            while (entry != 0L) {
                check(extractedEntries < limits.maxEntries) {
                    "Archive contains too many entries"
                }

                val size = ArchiveEntry.size(entry).coerceAtLeast(0L)
                check(size <= limits.maxEntrySizeBytes) {
                    "Archive entry is too large"
                }
                declaredSize = Math.addExact(declaredSize, size)
                check(declaredSize <= limits.maxTotalSizeBytes) {
                    "Unpacked archive is too large"
                }

                val type = ArchiveEntry.filetype(entry)
                val isDirectory = type == ArchiveEntry.AE_IFDIR
                val isRegularFile = type == ArchiveEntry.AE_IFREG || type == 0
                val path = ArchiveEntry.pathnameUtf8(entry)
                    ?: ArchiveEntry.pathname(entry)?.toString(StandardCharsets.UTF_8)

                if (path != null && (isDirectory || isRegularFile)) {
                    var consumed = false
                    onEntry(
                        ArchiveEntryInfo(
                            path = path,
                            size = size,
                            isDirectory = isDirectory
                        )
                    ) { outputStream ->
                        check(isRegularFile && !consumed) { "Archive entry data already consumed" }
                        val output = BufferedOutputStream(outputStream, BUFFER_SIZE)
                        val buffer = ByteBuffer.allocateDirect(BUFFER_SIZE)
                        var actualEntrySize = 0L
                        while (true) {
                            buffer.clear()
                            Archive.readData(archive, buffer)
                            val count = buffer.position()
                            if (count == 0) break

                            actualEntrySize = Math.addExact(actualEntrySize, count.toLong())
                            actualSize = Math.addExact(actualSize, count.toLong())
                            check(actualEntrySize <= limits.maxEntrySizeBytes) {
                                "Archive entry is too large"
                            }
                            check(actualSize <= limits.maxTotalSizeBytes) {
                                "Unpacked archive is too large"
                            }

                            buffer.flip()
                            output.writeRemaining(buffer)
                            onChunk()
                        }
                        output.flush()
                        consumed = true
                    }
                    if (!consumed) Archive.readDataSkip(archive)
                } else {
                    Archive.readDataSkip(archive)
                }

                extractedEntries++
                onProgress()
                entry = Archive.readNextHeader(archive)
            }
        } finally {
            Archive.readFree(archive)
        }
        return extractedEntries
    }

    fun listEntries(
        inputFileDescriptor: Int,
        passphrase: String? = null,
        preferSevenZip: Boolean = false,
        preferRar: Boolean = false,
        forceRawFormat: Boolean = false,
        forceBrotli: Boolean = false,
        limits: ExtractionLimits = ExtractionLimits()
    ): List<ArchiveEntryInfo> {
        if (preferRar) {
            return RarEngine.listEntries(
                inputFileDescriptor = inputFileDescriptor,
                passphrase = passphrase,
                limits = limits
            )
        }
        if (preferSevenZip) {
            return SevenZipEngine.listEntries(
                inputFileDescriptor = inputFileDescriptor,
                passphrase = passphrase,
                limits = limits
            )
        }

        val archive = Archive.readNew()
        val entries = mutableListOf<ArchiveEntryInfo>()
        var entryCount = 0
        try {
            configureReader(archive, forceRawFormat)
            passphrase
                ?.takeIf(String::isNotEmpty)
                ?.let { Archive.readAddPassphrase(archive, it.toByteArray()) }
            openReader(
                archive = archive,
                inputFileDescriptor = inputFileDescriptor,
                forceBrotli = forceBrotli
            )

            var entry = Archive.readNextHeader(archive)
            while (entry != 0L) {
                check(entryCount < limits.maxEntries) {
                    "Archive contains too many entries"
                }
                entryCount++
                val type = ArchiveEntry.filetype(entry)
                val isDirectory = type == ArchiveEntry.AE_IFDIR
                val isRegularFile = type == ArchiveEntry.AE_IFREG || type == 0
                val path = ArchiveEntry.pathnameUtf8(entry)
                    ?: ArchiveEntry.pathname(entry)?.toString(StandardCharsets.UTF_8)
                if (path != null && (isDirectory || isRegularFile)) {
                    entries += ArchiveEntryInfo(
                        path = path,
                        size = ArchiveEntry.size(entry).coerceAtLeast(0L),
                        isDirectory = isDirectory
                    )
                }
                Archive.readDataSkip(archive)
                entry = Archive.readNextHeader(archive)
            }
        } finally {
            Archive.readFree(archive)
        }
        return entries
    }

    fun encryptionStatus(
        inputFileDescriptor: Int,
        preferSevenZip: Boolean = false,
        preferRar: Boolean = false,
        forceRawFormat: Boolean = false,
        forceBrotli: Boolean = false
    ): ArchiveEncryptionStatus {
        if (preferRar) {
            return RarEngine.encryptionStatus(inputFileDescriptor)
        }
        if (preferSevenZip) {
            try {
                return SevenZipEngine.encryptionStatus(inputFileDescriptor)
            } catch (exception: Exception) {
                if (exception.indicatesEncryption()) {
                    return ArchiveEncryptionStatus.PasswordRequired
                }
                runCatching {
                    ParcelFileDescriptor.fromFd(inputFileDescriptor).use { descriptor ->
                        Os.lseek(descriptor.fileDescriptor, 0L, OsConstants.SEEK_SET)
                    }
                }
            }
        }
        val archive = Archive.readNew()
        var passphraseRequested = false
        try {
            configureReader(archive, forceRawFormat)
            Archive.readSetPassphraseCallback(archive, Unit) { _, _ ->
                passphraseRequested = true
                ByteArray(0)
            }
            openReader(
                archive = archive,
                inputFileDescriptor = inputFileDescriptor,
                forceBrotli = forceBrotli
            )

            var entry = Archive.readNextHeader(archive)
            while (entry != 0L) {
                if (
                    ArchiveEntry.isEncrypted(entry) ||
                    ArchiveEntry.isDataEncrypted(entry) ||
                    ArchiveEntry.isMetadataEncrypted(entry) ||
                    Archive.readHasEncryptedEntries(archive) > 0
                ) {
                    return if (preferSevenZip) {
                        ArchiveEncryptionStatus.PasswordRequired
                    } else {
                        archive.encryptionStatus()
                    }
                }
                Archive.readDataSkip(archive)
                entry = Archive.readNextHeader(archive)
            }
            return if (
                Archive.readHasEncryptedEntries(archive) > 0 || passphraseRequested
            ) {
                ArchiveEncryptionStatus.PasswordRequired
            } else {
                ArchiveEncryptionStatus.None
            }
        } catch (throwable: Throwable) {
            if (
                preferSevenZip &&
                (passphraseRequested || throwable.indicatesEncryption())
            ) {
                return ArchiveEncryptionStatus.PasswordRequired
            }
            if (throwable.indicatesUnsupportedEncryption()) {
                return ArchiveEncryptionStatus.Unsupported
            }
            if (passphraseRequested || throwable.indicatesEncryption()) {
                return ArchiveEncryptionStatus.PasswordRequired
            }
            throw throwable
        } finally {
            Archive.readFree(archive)
        }
    }

    fun verifyPassphrase(
        inputFileDescriptor: Int,
        passphrase: String,
        preferSevenZip: Boolean = false,
        preferRar: Boolean = false,
        forceRawFormat: Boolean = false,
        forceBrotli: Boolean = false
    ): Boolean {
        if (passphrase.isEmpty()) return false

        var decryptedDataRead = false
        return try {
            extract(
                inputFileDescriptor = inputFileDescriptor,
                passphrase = passphrase,
                preferSevenZip = preferSevenZip,
                preferRar = preferRar,
                forceRawFormat = forceRawFormat,
                forceBrotli = forceBrotli,
                onEntry = { entry, writeData ->
                    if (!entry.isDirectory && !decryptedDataRead) {
                        writeData(
                            PasswordProbeOutputStream {
                                decryptedDataRead = true
                            }
                        )
                    }
                }
            )
            true
        } catch (_: PasswordProbeCompletedException) {
            true
        } catch (throwable: Throwable) {
            decryptedDataRead || throwable.hasPasswordProbeCompletionCause()
        }
    }

    private fun configureWriter(
        archive: Long,
        format: ArchiveFormat,
        zipCompressionMethod: ZipCompressionMethod,
        sevenZipCompressionMethod: SevenZipCompressionMethod
    ) {
        when (format) {
            ArchiveFormat.Zip -> {
                Archive.writeSetFormatZip(archive)
                when (zipCompressionMethod) {
                    ZipCompressionMethod.Store -> Archive.writeZipSetCompressionStore(archive)
                    ZipCompressionMethod.Deflate -> Archive.writeZipSetCompressionDeflate(archive)
                    ZipCompressionMethod.Bzip2 -> Archive.writeZipSetCompressionBzip2(archive)
                    ZipCompressionMethod.Lzma -> Archive.writeZipSetCompressionLzma(archive)
                    ZipCompressionMethod.Xz -> Archive.writeZipSetCompressionXz(archive)
                    ZipCompressionMethod.Zstd -> Archive.writeZipSetCompressionZstd(archive)
                }
            }

            ArchiveFormat.SevenZip -> {
                Archive.writeSetFormat7zip(archive)
                Archive.writeSetFormatOption(
                    archive,
                    "7zip".toByteArray(StandardCharsets.UTF_8),
                    "compression".toByteArray(StandardCharsets.UTF_8),
                    sevenZipCompressionMethod.option.toByteArray(StandardCharsets.UTF_8)
                )
            }

            ArchiveFormat.Tar -> Archive.writeSetFormatPaxRestricted(archive)
            ArchiveFormat.PaxTar -> Archive.writeSetFormatPax(archive)
            ArchiveFormat.GnuTar -> Archive.writeSetFormatGnutar(archive)
            ArchiveFormat.Ustar -> Archive.writeSetFormatUstar(archive)
            ArchiveFormat.V7Tar -> Archive.writeSetFormatV7tar(archive)
            ArchiveFormat.TarGzip -> {
                Archive.writeAddFilterGzip(archive)
                Archive.writeSetFormatPaxRestricted(archive)
            }

            ArchiveFormat.TarCompress -> {
                Archive.writeAddFilterCompress(archive)
                Archive.writeSetFormatPaxRestricted(archive)
            }

            ArchiveFormat.TarBzip2 -> {
                Archive.writeAddFilterBzip2(archive)
                Archive.writeSetFormatPaxRestricted(archive)
            }

            ArchiveFormat.TarXz -> {
                Archive.writeAddFilterXz(archive)
                Archive.writeSetFormatPaxRestricted(archive)
            }

            ArchiveFormat.TarZstd -> {
                Archive.writeAddFilterZstd(archive)
                Archive.writeSetFormatPaxRestricted(archive)
            }

            ArchiveFormat.TarLz4 -> {
                Archive.writeAddFilterLz4(archive)
                Archive.writeSetFormatPaxRestricted(archive)
            }

            ArchiveFormat.TarLzip -> {
                Archive.writeAddFilterLzip(archive)
                Archive.writeSetFormatPaxRestricted(archive)
            }

            ArchiveFormat.TarLzma -> {
                Archive.writeAddFilterLzma(archive)
                Archive.writeSetFormatPaxRestricted(archive)
            }

            ArchiveFormat.TarBrotli -> Archive.writeSetFormatPaxRestricted(archive)

            ArchiveFormat.Cpio -> Archive.writeSetFormatCpioNewc(archive)
            ArchiveFormat.CpioOdc -> Archive.writeSetFormatCpioOdc(archive)
            ArchiveFormat.CpioBinary -> Archive.writeSetFormatCpioBin(archive)
            ArchiveFormat.CpioPwb -> Archive.writeSetFormatCpioPwb(archive)
            ArchiveFormat.ArBsd -> Archive.writeSetFormatArBsd(archive)
            ArchiveFormat.ArGnu -> Archive.writeSetFormatArSvr4(archive)
            ArchiveFormat.Iso -> Archive.writeSetFormatIso9660(archive)
            ArchiveFormat.Gzip -> {
                Archive.writeAddFilterGzip(archive)
                Archive.writeSetFormatRaw(archive)
            }

            ArchiveFormat.Bzip2 -> {
                Archive.writeAddFilterBzip2(archive)
                Archive.writeSetFormatRaw(archive)
            }

            ArchiveFormat.Xz -> {
                Archive.writeAddFilterXz(archive)
                Archive.writeSetFormatRaw(archive)
            }

            ArchiveFormat.Zstd -> {
                Archive.writeAddFilterZstd(archive)
                Archive.writeSetFormatRaw(archive)
            }

            ArchiveFormat.Lz4 -> {
                Archive.writeAddFilterLz4(archive)
                Archive.writeSetFormatRaw(archive)
            }

            ArchiveFormat.Lzip -> {
                Archive.writeAddFilterLzip(archive)
                Archive.writeSetFormatRaw(archive)
            }

            ArchiveFormat.Lzma -> {
                Archive.writeAddFilterLzma(archive)
                Archive.writeSetFormatRaw(archive)
            }

            ArchiveFormat.Brotli -> Archive.writeSetFormatRaw(archive)

            ArchiveFormat.Compress -> {
                Archive.writeAddFilterCompress(archive)
                Archive.writeSetFormatRaw(archive)
            }
        }
    }

    private fun configureCompressionLevel(
        archive: Long,
        format: ArchiveFormat,
        compressionLevel: ArchiveCompressionLevel
    ) {
        val level = when (format) {
            ArchiveFormat.TarZstd, ArchiveFormat.Zstd -> when (compressionLevel) {
                ArchiveCompressionLevel.Fast -> 1
                ArchiveCompressionLevel.Normal -> 6
                ArchiveCompressionLevel.Maximum -> 19
            }

            else -> compressionLevel.value
        }.toString().toByteArray(StandardCharsets.UTF_8)
        val option = "compression-level".toByteArray(StandardCharsets.UTF_8)

        when (format) {
            ArchiveFormat.Zip -> Archive.writeSetFormatOption(
                archive,
                "zip".toByteArray(StandardCharsets.UTF_8),
                option,
                level
            )

            ArchiveFormat.SevenZip -> Archive.writeSetFormatOption(
                archive,
                "7zip".toByteArray(StandardCharsets.UTF_8),
                option,
                level
            )

            ArchiveFormat.TarGzip, ArchiveFormat.Gzip -> Archive.writeSetFilterOption(
                archive,
                "gzip".toByteArray(StandardCharsets.UTF_8),
                option,
                level
            )

            ArchiveFormat.TarBzip2, ArchiveFormat.Bzip2 -> Archive.writeSetFilterOption(
                archive,
                "bzip2".toByteArray(StandardCharsets.UTF_8),
                option,
                level
            )

            ArchiveFormat.TarXz, ArchiveFormat.Xz -> Archive.writeSetFilterOption(
                archive,
                "xz".toByteArray(StandardCharsets.UTF_8),
                option,
                level
            )

            ArchiveFormat.TarZstd, ArchiveFormat.Zstd -> Archive.writeSetFilterOption(
                archive,
                "zstd".toByteArray(StandardCharsets.UTF_8),
                option,
                level
            )

            ArchiveFormat.TarLz4, ArchiveFormat.Lz4 -> Archive.writeSetFilterOption(
                archive,
                "lz4".toByteArray(StandardCharsets.UTF_8),
                option,
                level
            )

            else -> Unit
        }
    }

    private fun configureReader(
        archive: Long,
        forceRawFormat: Boolean
    ) {
        Archive.setCharset(archive, StandardCharsets.UTF_8.name().toByteArray())
        Archive.readSupportFilterAll(archive)
        if (forceRawFormat) {
            Archive.readSupportFormatRaw(archive)
        } else {
            Archive.readSupportFormatAll(archive)
        }
    }

    private fun openReader(
        archive: Long,
        inputFileDescriptor: Int,
        forceBrotli: Boolean
    ) {
        if (!forceBrotli) {
            Archive.readOpenFd(archive, inputFileDescriptor, BUFFER_SIZE.toLong())
            return
        }
        val descriptor = ParcelFileDescriptor.fromFd(inputFileDescriptor)
        val input = ArchiveCallbackInput(
            BrotliInputStream(ParcelFileDescriptor.AutoCloseInputStream(descriptor))
        )
        Archive.readOpen(
            archive,
            input,
            null,
            { _, source -> source.read() },
            { _, source -> source.close() }
        )
    }

    private fun OutputStream.writeRemaining(buffer: ByteBuffer) {
        val bytes = ByteArray(minOf(BUFFER_SIZE, buffer.remaining()))
        while (buffer.hasRemaining()) {
            val count = minOf(bytes.size, buffer.remaining())
            buffer.get(bytes, 0, count)
            write(bytes, 0, count)
        }
    }

    private const val BUFFER_SIZE = 64 * 1024
    private const val FILE_PERMISSIONS = 420
}

private class PasswordProbeOutputStream(
    private val onDataRead: () -> Unit
) : OutputStream() {
    override fun write(value: Int) {
        onDataRead()
        throw PasswordProbeCompletedException()
    }

    override fun write(buffer: ByteArray, offset: Int, length: Int) {
        if (length == 0) return
        onDataRead()
        throw PasswordProbeCompletedException()
    }
}

private class PasswordProbeCompletedException : RuntimeException()

private fun Throwable.hasPasswordProbeCompletionCause(): Boolean =
    generateSequence(this) { it.cause }
        .any { it is PasswordProbeCompletedException }

private fun ArchiveFormat.isBrotli(): Boolean =
    this == ArchiveFormat.Brotli || this == ArchiveFormat.TarBrotli

private class ArchiveCallbackInput(
    source: InputStream
) {
    private val input = BufferedInputStream(source, BufferSize)
    private val bytes = ByteArray(BufferSize)
    private val buffer = ByteBuffer.allocateDirect(BufferSize)

    fun read(): ByteBuffer {
        val count = input.read(bytes)
        buffer.clear()
        if (count > 0) {
            buffer.put(bytes, 0, count)
        }
        buffer.flip()
        return buffer
    }

    fun close() = input.close()
}

private fun Long.encryptionStatus(): ArchiveEncryptionStatus {
    val format = Archive.format(this) and Archive.FORMAT_BASE_MASK
    return if (format == Archive.FORMAT_ZIP) {
        ArchiveEncryptionStatus.PasswordRequired
    } else {
        ArchiveEncryptionStatus.Unsupported
    }
}

private fun Throwable.indicatesUnsupportedEncryption(): Boolean =
    generateSequence(this) { it.cause }
        .mapNotNull(Throwable::message)
        .any { message ->
            message.contains("encrypted", ignoreCase = true) &&
                    message.contains("not supported", ignoreCase = true)
        }

private fun Throwable.indicatesEncryption(): Boolean =
    generateSequence(this) { it.cause }
        .mapNotNull(Throwable::message)
        .any { message ->
            message.contains("encrypted", ignoreCase = true) ||
                    message.contains("passphrase", ignoreCase = true) ||
                    message.contains("password", ignoreCase = true)
        }

data class ExtractionLimits(
    val maxEntries: Int = 100_000,
    val maxEntrySizeBytes: Long = 64L * 1024 * 1024 * 1024,
    val maxTotalSizeBytes: Long = 128L * 1024 * 1024 * 1024
)

private const val BufferSize = 64 * 1024
