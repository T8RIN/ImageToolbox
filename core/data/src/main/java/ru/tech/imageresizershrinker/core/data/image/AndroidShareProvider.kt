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

package ru.tech.imageresizershrinker.core.data.image

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.t8rin.logger.makeLog
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.data.saving.io.FileWriteable
import ru.tech.imageresizershrinker.core.data.saving.io.UriReadable
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImageShareProvider
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.resource.ResourceManager
import ru.tech.imageresizershrinker.core.domain.saving.FilenameCreator
import ru.tech.imageresizershrinker.core.domain.saving.io.Writeable
import ru.tech.imageresizershrinker.core.domain.saving.io.use
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.domain.utils.runSuspendCatching
import ru.tech.imageresizershrinker.core.resources.R
import java.io.File
import javax.inject.Inject

internal class AndroidShareProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val filenameCreator: Lazy<FilenameCreator>,
    resourceManager: ResourceManager,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder,
    ResourceManager by resourceManager,
    ShareProvider, ImageShareProvider<Bitmap> {

    override suspend fun shareImages(
        uris: List<String>,
        imageLoader: suspend (String) -> Pair<Bitmap, ImageInfo>?,
        onProgressChange: (Int) -> Unit
    ) = withContext(ioDispatcher) {
        val cachedUris = uris.mapIndexedNotNull { index, uri ->
            imageLoader(uri)?.let { (image, imageInfo) ->
                cacheImage(
                    image = image,
                    imageInfo = imageInfo
                )?.also {
                    onProgressChange(index + 1)
                }
            }
        }
        onProgressChange(-1)
        shareUris(cachedUris)
    }

    override suspend fun cacheImage(
        image: Bitmap,
        imageInfo: ImageInfo,
        filename: String?
    ): String? = withContext(ioDispatcher) {
        runSuspendCatching {
            val saveTarget = ImageSaveTarget(
                imageInfo = imageInfo,
                originalUri = imageInfo.originalUri ?: "share",
                sequenceNumber = null,
                data = byteArrayOf()
            )

            val realFilename = filename ?: filenameCreator.get().constructImageFilename(saveTarget)
            val byteArray = imageCompressor.compressAndTransform(image, imageInfo)

            cacheByteArray(
                byteArray = byteArray,
                filename = realFilename
            )
        }.getOrNull()
    }

    override suspend fun shareImage(
        imageInfo: ImageInfo,
        image: Bitmap,
        onComplete: () -> Unit
    ) = withContext(ioDispatcher) {
        cacheImage(
            image = image,
            imageInfo = imageInfo
        )?.let {
            shareUri(
                uri = it,
                type = imageInfo.imageFormat.mimeType,
                onComplete = onComplete
            )
        }
        onComplete()
    }

    override suspend fun shareUri(
        uri: String,
        type: String?,
        onComplete: () -> Unit
    ) {
        withContext(defaultDispatcher) {
            runSuspendCatching {
                shareUriImpl(
                    uri = uri,
                    type = type
                )
                onComplete()
            }.onFailure {
                val newUri = cacheData(
                    writeData = {
                        it.copyFrom(
                            UriReadable(
                                uri = uri.toUri(),
                                context = context
                            )
                        )
                    },
                    filename = filenameCreator.get()
                        .constructRandomFilename(
                            extension = imageGetter.getExtension(uri) ?: ""
                        )
                )
                shareUriImpl(
                    uri = newUri ?: return@onFailure,
                    type = type
                )
                onComplete()
            }
        }
    }

    private fun shareUriImpl(
        uri: String,
        type: String?
    ) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri.toUri())
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.type = type ?: MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(
                    imageGetter.getExtension(uri)
                ) ?: "*/*"
        }
        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share))
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    override suspend fun shareUris(
        uris: List<String>
    ) = shareImageUris(uris.map { it.toUri() })

    private suspend fun shareImageUris(
        uris: List<Uri>
    ) = withContext(defaultDispatcher) {
        if (uris.isEmpty()) return@withContext

        val sendIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            type = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(
                    imageGetter.getExtension(uris.first().toString())
                ) ?: "*/*"
        }
        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share))
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    override suspend fun cacheByteArray(
        byteArray: ByteArray,
        filename: String
    ): String? = withContext(ioDispatcher) {
        cacheData(
            writeData = { it.writeBytes(byteArray) },
            filename = filename,
        )
    }

    override suspend fun shareByteArray(
        byteArray: ByteArray,
        filename: String,
        onComplete: () -> Unit
    ) = withContext(ioDispatcher) {
        shareData(
            writeData = { it.writeBytes(byteArray) },
            filename = filename,
            onComplete = onComplete
        )
        onComplete()
    }

    override suspend fun cacheData(
        writeData: suspend (Writeable) -> Unit,
        filename: String
    ): String? = withContext(ioDispatcher) {
        val imagesFolder = File(context.cacheDir, "files")

        runSuspendCatching {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, filename)
            FileWriteable(file).use {
                writeData(it)
            }

            FileProvider.getUriForFile(
                context,
                getString(R.string.file_provider),
                file
            ).also { uri ->
                runCatching {
                    context.grantUriPermission(
                        context.packageName,
                        uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
            }
        }.onFailure { it.makeLog("cacheData") }.getOrNull()?.toString()
    }

    override suspend fun shareData(
        writeData: suspend (Writeable) -> Unit,
        filename: String,
        onComplete: () -> Unit
    ) = withContext(ioDispatcher) {
        cacheData(
            writeData = writeData,
            filename = filename
        )?.let {
            shareUri(
                uri = it,
                type = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(
                        imageGetter.getExtension(it)
                    ) ?: "*/*",
                onComplete = onComplete
            )
        }

        onComplete()
    }

    override fun shareText(
        value: String,
        onComplete: () -> Unit
    ) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, value)
        }
        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share))
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)

        onComplete()
    }

}