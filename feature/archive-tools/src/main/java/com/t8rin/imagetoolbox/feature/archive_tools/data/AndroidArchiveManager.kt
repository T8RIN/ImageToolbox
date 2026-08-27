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

package com.t8rin.imagetoolbox.feature.archive_tools.data

import android.content.Context
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.t8rin.archive.ArchiveEngine
import com.t8rin.archive.ArchiveFormat
import com.t8rin.archive.ArchivePath
import com.t8rin.archive.ArchiveSource
import com.t8rin.archive.SevenZipCompressionMethod
import com.t8rin.archive.ZipCompressionMethod
import com.t8rin.imagetoolbox.core.data.saving.io.SeekableWriteable
import com.t8rin.imagetoolbox.core.data.saving.io.UriReadable
import com.t8rin.imagetoolbox.core.data.utils.outputStream
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.fileSize
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.feature.archive_tools.domain.ArchiveManager
import com.t8rin.imagetoolbox.feature.archive_tools.domain.model.ArchiveExtractionOptions
import com.t8rin.imagetoolbox.feature.archive_tools.domain.model.SupportedArchiveExtensions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.OutputStream
import javax.inject.Inject
import kotlin.random.Random

internal class AndroidArchiveManager @Inject constructor(
    @ApplicationContext private val context: Context,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, ArchiveManager {

    override suspend fun archive(
        files: List<String>,
        destination: Writeable,
        format: ArchiveFormat,
        zipCompressionMethod: ZipCompressionMethod,
        sevenZipCompressionMethod: SevenZipCompressionMethod,
        passphrase: String?,
        onProgress: () -> Unit
    ) = withContext(defaultDispatcher) {
        val operationContext = coroutineContext
        val temporaryFiles = mutableListOf<File>()
        try {
            val usedNames = mutableSetOf<String>()
            val sources = files.mapNotNull { value ->
                val uri = value.toUri()
                val name = uniqueName(
                    name = uri.filename(context) ?: return@mapNotNull null,
                    usedNames = usedNames
                )
                val size = uri.fileSize()
                if (size != null && size >= 0L) {
                    ArchiveSource(
                        name = name,
                        size = size,
                        openStream = { UriReadable(uri, context).stream }
                    )
                } else {
                    val temporaryFile = File.createTempFile(
                        "archive_source_",
                        ".tmp",
                        context.cacheDir
                    ).also(temporaryFiles::add)
                    UriReadable(uri, context).stream.use { input ->
                        temporaryFile.outputStream().buffered().use { output ->
                            val buffer = ByteArray(CopyBufferSize)
                            while (true) {
                                val count = input.read(buffer)
                                if (count < 0) break
                                output.write(buffer, 0, count)
                                operationContext.ensureActive()
                            }
                        }
                    }
                    ArchiveSource(
                        name = name,
                        size = temporaryFile.length(),
                        openStream = temporaryFile::inputStream
                    )
                }
            }
            ArchiveEngine.create(
                format = format,
                sources = sources,
                outputStream = if (format == ArchiveFormat.SevenZip && !passphrase.isNullOrEmpty()) {
                    null
                } else {
                    destination.outputStream()
                },
                outputChannel = (destination as? SeekableWriteable)?.channel,
                zipCompressionMethod = zipCompressionMethod,
                sevenZipCompressionMethod = sevenZipCompressionMethod,
                passphrase = passphrase,
                onChunk = operationContext::ensureActive,
                onProgress = onProgress
            )
        } finally {
            temporaryFiles.forEach(File::delete)
        }
    }

    override suspend fun getArchiveEncryptionStatus(archive: String) =
        withContext(defaultDispatcher) {
            val uri = archive.toUri()
            val sourceName = uri.filename(context).orEmpty()
            val preferSevenZip = sourceName.hasSevenZipExtension()
            val preferRar = sourceName.hasRarExtension()
            val forceRawFormat = sourceName.shouldForceRawFormat()
            val forceBrotli = sourceName.hasBrotliExtension()
            context.contentResolver.openFileDescriptor(uri, "r")?.use { input ->
                ArchiveEngine.encryptionStatus(
                    inputFileDescriptor = input.fd,
                    preferSevenZip = preferSevenZip,
                    preferRar = preferRar,
                    forceRawFormat = forceRawFormat,
                    forceBrotli = forceBrotli
                )
            } ?: error("Cannot open archive")
        }

    override suspend fun extract(
        archive: String,
        destinationFolder: String,
        passphrase: String?,
        options: ArchiveExtractionOptions,
        onProgress: () -> Unit
    ): Int = withContext(defaultDispatcher) {
        val operationContext = coroutineContext
        val destination = DocumentFile.fromTreeUri(context, destinationFolder.toUri())
            ?: error("Cannot access destination folder")
        val createdDocuments = mutableListOf<DocumentFile>()
        val archiveName = archive.toUri().filename(context).orEmpty().archiveName()
        val outputFolder = if (options.createSubfolder) {
            destination.createUniqueDirectory(archiveName)
        } else {
            destination
        }

        try {
            extractEntries(
                archive = archive,
                passphrase = passphrase,
                options = options,
                onEntry = { segments, isDirectory, writeData ->
                    if (isDirectory) {
                        if (options.preserveDirectoryStructure) {
                            outputFolder.resolveDirectories(
                                segments = segments,
                                createdDocuments = createdDocuments
                            )
                        }
                    } else {
                        val parent = outputFolder.resolveDirectories(
                            segments = segments.dropLast(1),
                            createdDocuments = createdDocuments
                        )
                        val file = parent.createUniqueFile(segments.last())
                            .also(createdDocuments::add)
                        context.contentResolver.openOutputStream(file.uri, "w")?.use(writeData)
                            ?: error("Cannot create ${segments.last()}")
                    }
                },
                onChunk = operationContext::ensureActive,
                onProgress = onProgress
            )
        } catch (throwable: Throwable) {
            if (options.createSubfolder) {
                outputFolder.delete()
            } else {
                createdDocuments.asReversed().forEach(DocumentFile::delete)
            }
            throw throwable
        }
    }

    override suspend fun extractToCache(
        archive: String,
        passphrase: String?,
        options: ArchiveExtractionOptions,
        onProgress: () -> Unit
    ): List<String> = withContext(defaultDispatcher) {
        val operationContext = coroutineContext
        val cacheFolder = File(
            context.cacheDir,
            "cache/${Random.nextInt()}"
        ).apply { mkdirs() }
        val archiveName = archive.toUri().filename(context).orEmpty().archiveName()
        val outputFolder = if (options.createSubfolder) {
            File(cacheFolder, archiveName).apply { mkdirs() }
        } else {
            cacheFolder
        }
        val extractedFiles = mutableListOf<File>()

        try {
            extractEntries(
                archive = archive,
                passphrase = passphrase,
                options = options,
                onEntry = { segments, isDirectory, writeData ->
                    if (isDirectory) {
                        if (options.preserveDirectoryStructure) {
                            outputFolder.resolveDirectories(segments)
                        }
                    } else {
                        val parent = outputFolder.resolveDirectories(segments.dropLast(1))
                        val file = parent.createUniqueFile(segments.last())
                            .also(extractedFiles::add)
                        file.outputStream().use(writeData)
                    }
                },
                onChunk = operationContext::ensureActive,
                onProgress = onProgress
            )

            if (extractedFiles.isEmpty()) {
                cacheFolder.deleteRecursively()
                emptyList()
            } else {
                extractedFiles.map { file ->
                    FileProvider.getUriForFile(
                        context,
                        context.getString(R.string.file_provider),
                        file
                    ).toString()
                }
            }
        } catch (throwable: Throwable) {
            cacheFolder.deleteRecursively()
            throw throwable
        }
    }

    private fun extractEntries(
        archive: String,
        passphrase: String?,
        options: ArchiveExtractionOptions,
        onEntry: (
            segments: List<String>,
            isDirectory: Boolean,
            writeData: (OutputStream) -> Unit
        ) -> Unit,
        onChunk: () -> Unit,
        onProgress: () -> Unit
    ): Int {
        val sourceName = archive.toUri().filename(context).orEmpty()
        val preferSevenZip = sourceName.hasSevenZipExtension()
        val preferRar = sourceName.hasRarExtension()
        val forceRawFormat = sourceName.shouldForceRawFormat()
        val forceBrotli = sourceName.hasBrotliExtension()
        val archiveName = sourceName.archiveName()

        return context.contentResolver.openFileDescriptor(archive.toUri(), "r")?.use { input ->
            ArchiveEngine.extract(
                inputFileDescriptor = input.fd,
                passphrase = passphrase,
                preferSevenZip = preferSevenZip,
                preferRar = preferRar,
                forceRawFormat = forceRawFormat,
                forceBrotli = forceBrotli,
                onChunk = onChunk,
                onEntry = entry@{ entry, writeData ->
                    val entryPath = if (forceRawFormat && !entry.isDirectory) {
                        archiveName
                    } else {
                        entry.path
                    }
                    val segments = ArchivePath.safeSegments(entryPath)
                        ?: error("Unsafe archive entry path: $entryPath")
                    if (options.skipHiddenFiles && segments.any { it.startsWith('.') }) {
                        return@entry
                    }
                    val outputSegments = if (options.preserveDirectoryStructure) {
                        segments
                    } else {
                        listOf(segments.last())
                    }
                    onEntry(outputSegments, entry.isDirectory, writeData)
                },
                onProgress = onProgress
            )
        } ?: error("Cannot open archive")
    }

    private fun uniqueName(
        name: String,
        usedNames: MutableSet<String>
    ): String {
        if (usedNames.add(name.lowercase())) return name
        val base = name.substringBeforeLast('.', name)
        val extension = name.substringAfterLast('.', "").let { if (it.isEmpty()) "" else ".$it" }
        var index = 2
        while (true) {
            val candidate = "$base ($index)$extension"
            if (usedNames.add(candidate.lowercase())) return candidate
            index++
        }
    }
}

private fun String.archiveName(): String = ArchivePath.safeSegments(
    removeArchiveExtension().ifBlank { "Archive" }
)?.lastOrNull() ?: "Archive"

private fun String.removeArchiveExtension(): String {
    val lower = lowercase()
    val extension = SupportedArchiveExtensions.firstOrNull(lower::endsWith)
        ?: return substringBeforeLast('.')
    return dropLast(extension.length)
}

private fun DocumentFile.resolveDirectories(
    segments: List<String>,
    createdDocuments: MutableList<DocumentFile>
): DocumentFile =
    segments.fold(this) { parent, segment ->
        parent.findFile(segment)?.takeIf(DocumentFile::isDirectory)
            ?: parent.createDirectory(segment)
                ?.also(createdDocuments::add)
            ?: error("Cannot create directory: $segment")
    }

private fun DocumentFile.createUniqueDirectory(name: String): DocumentFile {
    var candidate = name
    var index = 2
    while (findFile(candidate) != null) {
        candidate = "$name ($index)"
        index++
    }
    return createDirectory(candidate) ?: error("Cannot create output directory")
}

private fun DocumentFile.createUniqueFile(name: String): DocumentFile {
    val base = name.substringBeforeLast('.', name)
    val extension = name.substringAfterLast('.', "")
    var candidate = name
    var index = 2
    while (findFile(candidate) != null) {
        candidate = "$base ($index)${if (extension.isEmpty()) "" else ".$extension"}"
        index++
    }
    val mimeType = MimeTypeMap.getSingleton()
        .getMimeTypeFromExtension(extension.lowercase())
        ?: "application/octet-stream"
    return createFile(mimeType, candidate) ?: error("Cannot create file: $candidate")
}

private fun File.resolveDirectories(segments: List<String>): File =
    segments.fold(this) { parent, segment ->
        File(parent, segment).also { directory ->
            check(directory.isDirectory || directory.mkdir()) {
                "Cannot create directory: $segment"
            }
        }
    }

private fun File.createUniqueFile(name: String): File {
    val base = name.substringBeforeLast('.', name)
    val extension = name.substringAfterLast('.', "")
    var candidate = name
    var index = 2
    while (File(this, candidate).exists()) {
        candidate = "$base ($index)${if (extension.isEmpty()) "" else ".$extension"}"
        index++
    }
    return File(this, candidate)
}

private val RawArchiveExtensions = listOf(
    ".gz", ".bz2", ".xz", ".zst", ".zstd", ".z", ".lz4", ".lz", ".lzip",
    ".lzma", ".br"
)

private val CompressedTarExtensions = listOf(
    ".tar.gz", ".tar.bz2", ".tar.xz", ".tar.zst", ".tar.zstd", ".tar.z",
    ".tar.lz4", ".tar.lz", ".tar.lzip", ".tar.lzma", ".tar.br", ".warc.gz",
    ".tgz", ".tbz", ".tbz2", ".txz", ".tzst", ".taz", ".tlz", ".tbr"
)

private fun String.shouldForceRawFormat(): Boolean {
    val lower = lowercase()
    return CompressedTarExtensions.none(lower::endsWith) &&
            RawArchiveExtensions.any(lower::endsWith)
}

private fun String.hasSevenZipExtension(): Boolean {
    val lower = lowercase()
    return lower.endsWith(".7z") || lower.endsWith(".cb7")
}

private fun String.hasRarExtension(): Boolean {
    val lower = lowercase()
    return lower.endsWith(".rar") || lower.endsWith(".cbr")
}

private fun String.hasBrotliExtension(): Boolean {
    val lower = lowercase()
    return lower.endsWith(".br") || lower.endsWith(".tbr")
}

private const val CopyBufferSize = 64 * 1024
