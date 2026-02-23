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

package com.t8rin.imagetoolbox.feature.pdf_tools.data.utils

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.data.saving.io.UriReadable
import com.t8rin.imagetoolbox.core.data.utils.outputStream
import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import com.t8rin.imagetoolbox.core.domain.utils.applyUse
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfMetadata
import com.t8rin.logger.makeLog
import com.tom_roush.harmony.awt.AWTColor
import com.tom_roush.pdfbox.io.MemoryUsageSetting
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDDocumentInformation
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.PDResources
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission
import com.tom_roush.pdfbox.pdmodel.encryption.InvalidPasswordException
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font
import com.tom_roush.pdfbox.pdmodel.graphics.form.PDFormXObject
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import com.tom_roush.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState
import java.util.Calendar
import kotlin.math.roundToInt

internal fun PDDocument.save(
    writeable: Writeable,
    password: String? = null
) {
    if (password.isNullOrBlank()) {
        isAllSecurityToBeRemoved = true
    } else {
        protect(
            StandardProtectionPolicy(password, password, AccessPermission()).apply {
                encryptionKeyLength = 128
            }
        )
    }
    save(writeable.outputStream())
}

internal var PDDocument.metadata: PdfMetadata
    get() = documentInformation.run {
        PdfMetadata(
            title = title,
            author = author,
            subject = subject,
            keywords = keywords,
            creator = creator,
            producer = producer
        )
    }
    set(value) {
        documentInformation.apply {
            title = value.title ?: title
            author = value.author ?: author
            subject = value.subject ?: subject
            keywords = value.keywords ?: keywords
            creator = value.creator ?: creator
            producer = value.producer ?: producer
        }
    }

@JvmName("setMetadataNullable")
internal fun PDDocument.setMetadata(value: PdfMetadata?) {
    if (value == null) {
        documentInformation = PDDocumentInformation().apply {
            creationDate = Calendar.getInstance()
            modificationDate = Calendar.getInstance()
        }
    } else {
        metadata = value
    }
}

internal val PDDocument.baseFont
    get() = PDType0Font.load(
        this, appContext.resources.openRawResource(R.raw.roboto_bold)
    )

internal fun PDDocument.getPageSafe(index: Int): PDPage = getPage(
    index.coerceIn(
        minimumValue = 0,
        maximumValue = numberOfPages - 1
    )
)

internal fun PDPageContentStream.setAlpha(alpha: Float) {
    val gs = PDExtendedGraphicsState().apply {
        nonStrokingAlphaConstant = alpha
    }

    setGraphicsStateParameters(gs)
}

internal fun PDPageContentStream.setColor(color: Color) {
    if (color.alpha < 1f) {
        setAlpha(color.alpha)
    }
    setNonStrokingColor(
        AWTColor(
            (color.red * 255).roundToInt(),
            (color.green * 255).roundToInt(),
            (color.blue * 255).roundToInt(),
            (color.alpha * 255).roundToInt()
        )
    )
}

internal fun <T> PDDocument.createPage(
    page: PDPage,
    graphics: PDPageContentStream.() -> T
) {
    addPage(page)
    writePage(
        page = page,
        overwrite = true,
        graphics = graphics
    )
}

internal fun <T> PDDocument.writePage(
    page: PDPage,
    overwrite: Boolean = false,
    graphics: PDPageContentStream.() -> T
) = if (overwrite) {
    PDPageContentStream(this, page)
} else {
    PDPageContentStream(
        this,
        page,
        PDPageContentStream.AppendMode.APPEND,
        true
    )
}.applyUse(graphics)

internal fun safeOpenPdf(
    uri: String,
    password: String?
): PDDocument {
    val stream = UriReadable(uri.toUri(), appContext).stream

    return try {
        PDDocument.load(
            stream,
            password.orEmpty()
        )
    } catch (t: Throwable) {
        if (t is InvalidPasswordException) throw t

        "failed to open pdf from $uri - trying again".makeLog("openPdf")

        PDDocument.load(
            stream,
            password.orEmpty(),
            MemoryUsageSetting.setupTempFileOnly()
        )
    }
}

internal fun Bitmap.asXObject(
    document: PDDocument,
    quality: Float
): PDImageXObject = if (quality >= 1f) {
    LosslessFactory.createFromImage(document, this)
} else {
    JPEGFactory.createFromImage(document, this, quality.coerceAtLeast(0f))
}

internal fun PDDocument.getAllImages(): List<PDImageXObject> =
    pages.flatMap { it.getResources().getImages() }

private fun PDResources.getImages(): List<PDImageXObject> {
    val images: MutableList<PDImageXObject> = mutableListOf()

    for (xObjectName in xObjectNames) {
        val xObject = getXObject(xObjectName)

        if (xObject is PDFormXObject) {
            images.addAll(xObject.getResources().getImages())
        } else if (xObject is PDImageXObject) {
            images.add(xObject)
        }
    }

    return images
}