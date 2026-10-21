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

package com.t8rin.imagetoolbox.feature.assistant

import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.imagetoolbox.core.domain.model.ExtraDataType
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.utils.screensFor
import com.t8rin.imagetoolbox.core.ui.widget.utils.withSelectedUris
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssistantNavigationInstrumentedTest {
    private val images = listOf("file:///first.png".toUri(), "file:///second.png".toUri())
    private val pdfs = listOf("file:///first.pdf".toUri(), "file:///second.pdf".toUri())

    @Test
    fun passesSelectedPhotosToSingleAndBatchTools() {
        assertEquals(Screen.Crop(images.first()), Screen.Crop().withSelectedUris(images, null))
        assertEquals(
            Screen.ResizeAndConvert(images),
            Screen.ResizeAndConvert().withSelectedUris(images, null)
        )
        assertEquals(
            Screen.PdfTools.ImagesToPdf(images),
            Screen.PdfTools.ImagesToPdf().withSelectedUris(images, null)
        )
        assertEquals(Screen.DeleteExif(images), Screen.DeleteExif().withSelectedUris(images, null))
        assertEquals(
            Screen.PalettePdfTool(images.first()),
            Screen.PalettePdfTool().withSelectedUris(images, null)
        )
    }

    @Test
    fun preservesExplicitModesWithoutChangingPickerChoices() {
        val single = images.take(1)
        val searchablePdf = Screen.RecognizeText(Screen.RecognizeText.Type.WriteToSearchablePdf())
        assertEquals(
            Screen.RecognizeText(Screen.RecognizeText.Type.WriteToSearchablePdf(single)),
            searchablePdf.withSelectedUris(single, null)
        )
        assertEquals(
            Screen.Filter(Screen.Filter.Type.Masking(images.first())),
            Screen.Filter(Screen.Filter.Type.Masking()).withSelectedUris(images, null)
        )
        val opacity = Screen.Filter(Screen.Filter.Type.Basic(filterKey = "opacity"))
        assertEquals(
            Screen.Filter(Screen.Filter.Type.Basic(images, filterKey = "opacity")),
            opacity.withSelectedUris(images, null)
        )
        assertEquals(opacity, opacity.withSelectedUris(emptyList(), null))
        assertFalse(single.screensFor(null).any {
            it is Screen.RecognizeText && it.type is Screen.RecognizeText.Type.WriteToSearchablePdf
        })
        assertFalse(single.screensFor(null).any { it is Screen.PalettePdfTool })
    }

    @Test
    fun passesPdfInputsWithoutConfusingToolsThatShareAnId() {
        assertEquals(
            Screen.PdfTools.ComparePdf(pdfs),
            Screen.PdfTools.ComparePdf().withSelectedUris(pdfs, ExtraDataType.Pdf)
        )
        assertEquals(
            Screen.PdfTools.Split(pdfs.first()),
            Screen.PdfTools.Split().withSelectedUris(pdfs, ExtraDataType.Pdf)
        )
        assertEquals(
            Screen.PdfTools.DarkMode(pdfs.first()),
            Screen.PdfTools.DarkMode().withSelectedUris(pdfs, ExtraDataType.Pdf)
        )
        assertEquals(
            Screen.CodePreview,
            Screen.CodePreview.withSelectedUris(pdfs, ExtraDataType.Pdf)
        )
    }

    @Test
    fun leavesIncompatibleOrEmptySelectionsForTheToolPicker() {
        val crop = Screen.Crop()
        assertEquals(crop, crop.withSelectedUris(pdfs, ExtraDataType.Pdf))
        assertEquals(crop, crop.withSelectedUris(emptyList(), null))
        val mode = Screen.RecognizeText(Screen.RecognizeText.Type.WriteToSearchablePdf())
        assertEquals(mode, mode.withSelectedUris(emptyList(), null))
        val palette = "file:///colors.gpl".toUri()
        assertEquals(
            Screen.PalettePdfTool(palette),
            Screen.PalettePdfTool().withSelectedUris(listOf(palette), ExtraDataType.File)
        )
        assertEquals(
            Screen.PalettePdfTool(),
            Screen.PalettePdfTool().withSelectedUris(pdfs, ExtraDataType.Pdf)
        )
    }
}