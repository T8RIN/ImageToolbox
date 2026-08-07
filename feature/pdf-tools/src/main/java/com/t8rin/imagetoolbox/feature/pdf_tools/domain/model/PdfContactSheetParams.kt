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

package com.t8rin.imagetoolbox.feature.pdf_tools.domain.model

data class PdfContactSheetParams(
    val columns: Int = 3,
    val rows: Int = 4,
    val margin: Float = 24f,
    val spacing: Float = 8f,
    val captionFields: Set<PdfContactSheetCaptionField> = setOf(
        PdfContactSheetCaptionField.SequenceNumber,
        PdfContactSheetCaptionField.FileName,
        PdfContactSheetCaptionField.ImageDimensions,
        PdfContactSheetCaptionField.FileSize
    ),
    val customCaptionText: String = "",
    val quality: Float = 0.85f
)

enum class PdfContactSheetCaptionField {
    FileName,
    FileNameWithoutExtension,
    SequenceNumber,
    ImageDimensions,
    FileSize,
    DateTaken,
    CameraModel,
    Lens,
    ExposureSettings,
    ParentFolder,
    CustomText;

    companion object {
        val defaultFields by lazy {
            setOf(
                SequenceNumber,
                FileName,
                ImageDimensions,
                FileSize
            )
        }
    }
}
