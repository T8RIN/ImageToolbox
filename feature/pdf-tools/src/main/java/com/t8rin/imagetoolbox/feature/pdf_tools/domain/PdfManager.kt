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

package com.t8rin.imagetoolbox.feature.pdf_tools.domain

import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.model.RectModel
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.ImagesToPdfParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PageNumbersParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfMetadata
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfSignatureParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfToImagesAction
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfWatermarkParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PrintPdfParams
import kotlinx.coroutines.flow.Flow

interface PdfManager : PdfHelper {

    fun convertPdfToImages(
        uri: String,
        pages: List<Int>?,
        preset: Preset.Percentage
    ): Flow<PdfToImagesAction>

    suspend fun convertImagesToPdf(
        imageUris: List<String>,
        onProgressChange: suspend (Int) -> Unit,
        params: ImagesToPdfParams
    ): String

    suspend fun mergePdfs(
        uris: List<String>
    ): String

    suspend fun splitPdf(
        uri: String,
        pages: List<Int>?
    ): String

    suspend fun removePdfPages(
        uri: String,
        pages: List<Int>
    ): String

    suspend fun rotatePdf(
        uri: String,
        rotations: List<Int>
    ): String

    suspend fun rearrangePdf(
        uri: String,
        newOrder: List<Int>
    ): String

    suspend fun addPageNumbers(
        uri: String,
        params: PageNumbersParams
    ): String

    suspend fun addWatermark(
        uri: String,
        watermarkText: String,
        params: PdfWatermarkParams
    ): String

    suspend fun addSignature(
        uri: String,
        signatureImage: Any,
        params: PdfSignatureParams
    ): String

    suspend fun protectPdf(
        uri: String,
        password: String
    ): String

    suspend fun unlockPdf(
        uri: String,
        password: String
    ): String

    suspend fun extractPagesFromPdf(
        uri: String
    ): List<String>

    suspend fun compressPdf(
        uri: String,
        quality: Float
    ): String

    suspend fun convertToGrayscale(
        uri: String
    ): String

    suspend fun repairPdf(
        uri: String
    ): String

    suspend fun changePdfMetadata(
        uri: String,
        metadata: PdfMetadata?
    ): String

    suspend fun getPdfMetadata(
        uri: String
    ): PdfMetadata

    suspend fun stripText(
        uri: String
    ): List<String>

    suspend fun cropPdf(
        uri: String,
        pages: List<Int>?,
        rect: RectModel
    ): String

    suspend fun flattenPdf(
        uri: String,
        quality: Float
    ): String

    suspend fun detectPdfAutoRotations(
        uri: String
    ): List<Int>

    suspend fun extractImagesFromPdf(
        uri: String
    ): String?

    suspend fun convertToZip(
        uri: String,
        interval: Int
    ): String

    suspend fun printPdf(
        uri: String,
        quality: Float,
        params: PrintPdfParams
    ): String

}