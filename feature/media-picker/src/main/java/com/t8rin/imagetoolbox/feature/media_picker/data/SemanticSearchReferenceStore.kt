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

package com.t8rin.imagetoolbox.feature.media_picker.data

import android.content.Context
import android.graphics.Bitmap
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.model.SemanticSearchSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import kotlin.math.roundToInt

internal class SemanticSearchReferenceStore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder {

    private val directory = File(context.filesDir, "semantic-search-references").canonicalFile
    private val restoredDirectory = Regex(
        "/data/(?:user/[0-9]+|data)/${Regex.escape(context.packageName)}/files/${directory.name}"
    )

    suspend fun prepare(
        settings: SemanticSearchSettings
    ): SemanticSearchSettings = withContext(ioDispatcher) {
        val created = mutableListOf<File>()
        val imported = mutableMapOf<String, String>()
        try {
            settings.copy(
                customCategories = settings.customCategories.map { category ->
                    category.copy(
                        referenceImages = category.referenceImages.map { uri ->
                            currentCoroutineContext().ensureActive()
                            imported.getOrPut(uri) { importReference(uri, created) }
                        }
                    )
                }
            )
        } catch (exception: Exception) {
            created.forEach(File::delete)
            throw exception
        }
    }

    suspend fun cleanup(settings: SemanticSearchSettings) = withContext(ioDispatcher) {
        val retained = settings.customCategories.flatMap { it.referenceImages }
            .mapNotNull(::ownedFile)
            .toSet()
        directory.listFiles().orEmpty().forEach { file ->
            if (file.isFile && fileName.matches(file.name.removeSuffix(".tmp")) &&
                file.canonicalFile !in retained
            ) {
                file.delete()
            }
        }
    }

    private suspend fun importReference(uri: String, created: MutableList<File>): String {
        ownedFile(uri)?.let { file ->
            check(file.isFile && file.length() > 0) {
                context.getString(R.string.gallery_search_reference_read_error)
            }
            return file.toUri().toString()
        }
        var readError: Throwable? = null
        val image = imageGetter.getImageData(
            uri = uri,
            size = MAX_SIZE,
            onFailure = { readError = it }
        )?.image ?: throw IllegalStateException(
            context.getString(R.string.gallery_search_reference_read_error),
            readError
        )
        currentCoroutineContext().ensureActive()
        val ratio = (MAX_SIZE.toFloat() / maxOf(image.width, image.height)).coerceAtMost(1f)
        val scaled = if (ratio < 1f) {
            imageScaler.scaleImage(
                image = image,
                width = (image.width * ratio).roundToInt().coerceAtLeast(1),
                height = (image.height * ratio).roundToInt().coerceAtLeast(1)
            )
        } else image
        val bytes = try {
            check(maxOf(scaled.width, scaled.height) <= MAX_SIZE) {
                context.getString(R.string.gallery_search_reference_save_error)
            }
            imageCompressor.compress(
                image = scaled,
                imageFormat = ImageFormat.Webp.Lossy,
                quality = Quality.Base(90)
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw IllegalStateException(
                context.getString(R.string.gallery_search_reference_save_error),
                exception
            )
        } finally {
            if (scaled !== image) scaled.recycle()
        }
        currentCoroutineContext().ensureActive()
        check(bytes.isNotEmpty() && (directory.isDirectory || directory.mkdirs())) {
            context.getString(R.string.gallery_search_reference_save_error)
        }
        val destination = File(directory, "${UUID.randomUUID()}.webp")
        val temporary = File(directory, "${destination.name}.tmp")
        created.add(destination)
        try {
            withContext(ioDispatcher) {
                FileOutputStream(temporary).use { stream ->
                    stream.write(bytes)
                    stream.fd.sync()
                }
            }
            currentCoroutineContext().ensureActive()
            check(temporary.renameTo(destination)) {
                context.getString(R.string.gallery_search_reference_save_error)
            }
            return destination.toUri().toString()
        } catch (exception: IOException) {
            throw IllegalStateException(
                context.getString(R.string.gallery_search_reference_save_error),
                exception
            )
        } finally {
            temporary.delete()
        }
    }

    private fun ownedFile(value: String): File? {
        val uri = value.toUri()
        if (uri.scheme != "file") return null
        val file = uri.path?.let(::File) ?: return null
        if (!fileName.matches(file.name)) return null
        if (restoredDirectory.matches(file.parent.orEmpty())) {
            File(directory, file.name).takeIf(File::isFile)?.let { return it }
        }
        return file.canonicalFile.takeIf {
            it.parentFile == directory
        }
    }

    private companion object {
        const val MAX_SIZE = 512
        val fileName = Regex(
            "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}\\.webp"
        )
    }

}