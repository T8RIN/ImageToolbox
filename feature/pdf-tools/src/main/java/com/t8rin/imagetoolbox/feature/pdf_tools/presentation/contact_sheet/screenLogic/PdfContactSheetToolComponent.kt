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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.contact_sheet.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.get
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.utils.humanFileSize
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.core.utils.imageSize
import com.t8rin.imagetoolbox.core.utils.tryExtractOriginal
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfManager
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfContactSheetCaptionField
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfContactSheetParams
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.util.Locale
import kotlin.math.roundToInt

class PdfContactSheetToolComponent @AssistedInject internal constructor(
    @Assisted val initialUris: List<Uri>?,
    @Assisted componentContext: ComponentContext,
    @Assisted onGoBack: () -> Unit,
    @Assisted onNavigate: (Screen) -> Unit,
    private val pdfManager: PdfManager,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val fileController: FileController,
    dispatchersHolder: DispatchersHolder
) : BasePdfToolComponent(
    onGoBack = onGoBack,
    onNavigate = onNavigate,
    dispatchersHolder = dispatchersHolder,
    componentContext = componentContext,
    pdfManager = pdfManager
) {
    override val _haveChanges: MutableState<Boolean> = mutableStateOf(initialUris != null)
    override val haveChanges: Boolean by _haveChanges

    private val _uris: MutableState<List<Uri>?> = mutableStateOf(initialUris)
    val uris by _uris

    private val _params = mutableStateOf(PdfContactSheetParams())
    val params by _params

    fun setUris(uris: List<Uri>?) {
        if (uris == null) registerChangesCleared() else registerChanges()
        _uris.update { uris }
    }

    fun addUris(uris: List<Uri>) {
        setUris(this.uris.orEmpty().plus(uris).distinct())
    }

    fun removeAt(index: Int) {
        runCatching {
            setUris(uris?.toMutableList()?.apply { removeAt(index) })
        }
    }

    fun updateParams(params: PdfContactSheetParams) {
        _params.update { params }
        registerChanges()
    }

    private suspend fun createContactSheet(): Uri {
        val imageUris = uris.orEmpty()
        return pdfManager.createContactSheet(
            imageUris = imageUris.map(Uri::toString),
            captions = imageUris.mapIndexed { index, uri ->
                createCaptionLines(
                    uri = uri,
                    index = index,
                    totalImages = imageUris.size
                )
            },
            params = params
        ).toUri()
    }

    private suspend fun createCaptionLines(
        uri: Uri,
        index: Int,
        totalImages: Int
    ): List<String> {
        val fields = params.captionFields
        if (fields.isEmpty()) return emptyList()

        val filename = uri.filename() ?: uri.lastPathSegment.orEmpty()
        val metadataFields = setOf(
            PdfContactSheetCaptionField.DateTaken,
            PdfContactSheetCaptionField.CameraModel,
            PdfContactSheetCaptionField.Lens,
            PdfContactSheetCaptionField.ExposureSettings
        )
        val metadata = if (fields.any(metadataFields::contains)) {
            fileController.readMetadata(uri.toString())
        } else null
        val lines = mutableListOf<String>()

        listOfNotNull(
            if (PdfContactSheetCaptionField.SequenceNumber in fields) {
                (index + 1).toString().padStart(maxOf(3, totalImages.toString().length), '0')
            } else null,
            if (PdfContactSheetCaptionField.FileName in fields) filename else null,
            if (PdfContactSheetCaptionField.FileNameWithoutExtension in fields) {
                filename.substringBeforeLast('.', filename)
            } else null
        ).joinToString(" — ").takeIf(String::isNotBlank)?.let(lines::add)

        listOfNotNull(
            if (PdfContactSheetCaptionField.ImageDimensions in fields) {
                uri.imageSize()?.let { "${it.width} × ${it.height}" }
            } else null,
            if (PdfContactSheetCaptionField.FileSize in fields) {
                fileController.getSize(uri.toString())?.let(::humanFileSize)
            } else null
        ).joinToString(" • ").takeIf(String::isNotBlank)?.let(lines::add)

        listOfNotNull(
            if (PdfContactSheetCaptionField.CameraModel in fields) {
                cameraName(
                    make = metadata?.get(MetadataTag.Make),
                    model = metadata?.get(MetadataTag.Model)
                )
            } else null,
            if (PdfContactSheetCaptionField.Lens in fields) {
                metadata?.get(MetadataTag.LensModel)
            } else null
        ).joinToString(" • ").takeIf(String::isNotBlank)?.let(lines::add)

        if (PdfContactSheetCaptionField.ExposureSettings in fields) {
            val iso = metadata?.get(MetadataTag.PhotographicSensitivity)
                ?: metadata?.get(MetadataTag.IsoSpeed)
                ?: metadata?.get(MetadataTag.RecommendedExposureIndex)
            listOfNotNull(
                metadata?.get(MetadataTag.ExposureTime)?.formatExposureTime(),
                metadata?.get(MetadataTag.FNumber)?.formatFNumber(),
                iso?.let { if (it.startsWith("ISO", true)) it else "ISO $it" }
            ).joinToString(" • ").takeIf(String::isNotBlank)?.let(lines::add)
        }

        if (PdfContactSheetCaptionField.DateTaken in fields) {
            (metadata?.get(MetadataTag.DatetimeOriginal)
                ?: metadata?.get(MetadataTag.Datetime))?.let(lines::add)
        }
        if (PdfContactSheetCaptionField.ParentFolder in fields) {
            uri.parentFolderName()?.let(lines::add)
        }
        if (PdfContactSheetCaptionField.CustomText in fields) {
            params.customCaptionText
                .lineSequence()
                .map(String::trim)
                .filter(String::isNotEmpty)
                .forEach(lines::add)
        }

        return lines
    }

    override fun saveTo(uri: Uri) {
        doSaving {
            fileController.transferBytes(
                fromUri = createContactSheet().toString(),
                toUri = uri.toString()
            ).onSuccess(::registerSave)
        }
    }

    override fun performSharing(
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        prepareForSharing(
            onSuccess = {
                shareProvider.shareUris(it.map(Uri::toString))
                registerSave()
                onSuccess()
            },
            onFailure = onFailure
        )
    }

    override fun prepareForSharing(
        onSuccess: suspend (List<Uri>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        doSharing(
            action = {
                onSuccess(listOf(createContactSheet()))
                registerSave()
            },
            onFailure = onFailure
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            initialUris: List<Uri>?,
            componentContext: ComponentContext,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): PdfContactSheetToolComponent
    }
}

private fun cameraName(make: String?, model: String?): String? {
    val makeClean = make?.trim().orEmpty()
    val modelClean = model?.trim().orEmpty()
    return when {
        modelClean.isBlank() -> makeClean.takeIf(String::isNotBlank)
        makeClean.isBlank() || modelClean.contains(makeClean, true) -> modelClean
        else -> "$makeClean $modelClean"
    }
}

private fun String.formatExposureTime(): String {
    val value = parseExifNumber() ?: return if (endsWith("s")) this else "${this}s"
    if (value <= 0.5 && value > 0.0) {
        return "1/${(1.0 / value).roundToInt().coerceAtLeast(1)}s"
    }
    return "${value.toCompactString()}s"
}

private fun String.formatFNumber(): String {
    val value = parseExifNumber() ?: return if (startsWith("f/", true)) this else "f/$this"
    return "f/${value.toCompactString()}"
}

private fun String.parseExifNumber(): Double? = split('/').let { parts ->
    when (parts.size) {
        1 -> parts[0].toDoubleOrNull()
        2 -> parts[0].toDoubleOrNull()?.let { numerator ->
            parts[1].toDoubleOrNull()?.takeIf { it != 0.0 }?.let { numerator / it }
        }

        else -> null
    }
}

private fun Double.toCompactString(): String = if (this % 1.0 == 0.0) {
    toInt().toString()
} else {
    String.format(Locale.US, "%.2f", this).trimEnd('0').trimEnd('.')
}

private fun Uri.parentFolderName(): String? {
    val resolvedUri = tryExtractOriginal()
    if (resolvedUri.scheme == "file") {
        return runCatching { resolvedUri.toFile().parentFile?.name }.getOrNull()
    }

    val relativePath = runCatching {
        appContext.contentResolver.query(
            resolvedUri,
            arrayOf(MediaStore.MediaColumns.RELATIVE_PATH),
            null,
            null,
            null
        )?.use { cursor ->
            if (!cursor.moveToFirst()) return@use null
            cursor.getColumnIndex(MediaStore.MediaColumns.RELATIVE_PATH)
                .takeIf { it >= 0 && !cursor.isNull(it) }
                ?.let(cursor::getString)
        }
    }.getOrNull()
        ?.trim('/')
        ?.substringAfterLast('/')
        ?.takeIf(String::isNotBlank)
    if (relativePath != null) return relativePath

    val documentPath = runCatching {
        DocumentsContract.getDocumentId(resolvedUri)
            .substringAfter(':')
            .substringBeforeLast('/', "")
            .substringAfterLast('/')
            .takeIf(String::isNotBlank)
    }.getOrNull()
    if (documentPath != null) return Uri.decode(documentPath)

    return resolvedUri.pathSegments
        .dropLast(1)
        .lastOrNull()
        ?.takeUnless { it in setOf("document", "image", "images", "media") }
        ?.let(Uri::decode)
        ?.takeIf(String::isNotBlank)
}
