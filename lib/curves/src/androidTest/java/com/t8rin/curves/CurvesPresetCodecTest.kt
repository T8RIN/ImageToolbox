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

package com.t8rin.curves

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@RunWith(AndroidJUnit4::class)
class CurvesPresetCodecTest {

    @Test
    fun imageToolboxPresetRoundTripsAllCurvesAndColorWheels() {
        val points = ImageCurvesEditorState.Default.controlPoints.toMutableList().apply {
            this[0] = listOf(0f, 0.1f, 0.5f, 0.8f, 1f, 0.9f)
            this[11] = listOf(0f, 0.5f, 0.3f, 0.75f, 1f, 0.5f)
            this[17] = listOf(0.2f, -0.1f)
            this[20] = listOf(0.7f)
        }
        val original = ImageCurvesEditorState(points)

        val decoded = CurvesPresetCodec.decode(CurvesPresetCodec.encode(original))

        assertEquals(original.controlPoints, decoded.controlPoints)
    }

    @Test
    fun importsLightroomXmpRgbPointCurves() {
        val xmp = """
            <crs:ToneCurvePV2012 xmlns:crs="crs" xmlns:rdf="rdf">
              <rdf:Seq><rdf:li>0, 10</rdf:li><rdf:li>128, 200</rdf:li><rdf:li>255, 245</rdf:li></rdf:Seq>
            </crs:ToneCurvePV2012>
            <crs:ToneCurvePV2012Red xmlns:crs="crs" xmlns:rdf="rdf">
              <rdf:Seq><rdf:li>0, 0</rdf:li><rdf:li>255, 220</rdf:li></rdf:Seq>
            </crs:ToneCurvePV2012Red>
        """.trimIndent()

        val decoded = CurvesPresetCodec.decode(xmp).controlPoints

        assertEquals(200f / 255f, decoded[0][3])
        assertEquals(220f / 255f, decoded[1][3])
        assertTrue(decoded[2] == ImageCurvesEditorState.Default.controlPoints[2])
    }

    @Test
    fun importsLegacyLightroomTemplateCurve() {
        val template = """
            s = {
              ToneCurvePV2012 = { 0, 0, 64, 80, 255, 255, },
            }
        """.trimIndent()

        val decoded = CurvesPresetCodec.decode(template).controlPoints

        assertEquals(64f / 255f, decoded[0][2])
        assertEquals(80f / 255f, decoded[0][3])
    }

    @Test
    fun exportedXmpCanBeImported() {
        val points = ImageCurvesEditorState.Default.controlPoints.toMutableList().apply {
            this[2] = listOf(0f, 0f, 0.5f, 0.75f, 1f, 1f)
        }

        val decoded = CurvesPresetCodec.decode(
            CurvesPresetCodec.encodeXmp(ImageCurvesEditorState(points))
        )

        assertEquals(191f / 255f, decoded.controlPoints[2][3])
    }

    @Test
    fun importsAdobeAcvCurves() {
        val acv = byteArrayOf(
            0, 4, 0, 1,
            0, 3,
            0, 0, 0, 0,
            0, 200.toByte(), 0, 128.toByte(),
            0, 255.toByte(), 0, 255.toByte()
        )

        val decoded = CurvesPresetCodec.decode(acv)

        assertEquals(128f / 255f, decoded.controlPoints[0][2])
        assertEquals(200f / 255f, decoded.controlPoints[0][3])
    }

    @Test
    fun importsLightroomPresetFromZipArchive() {
        val xmp = """
            <crs:ToneCurvePV2012 xmlns:crs="crs" xmlns:rdf="rdf">
              <rdf:Seq><rdf:li>0, 0</rdf:li><rdf:li>128, 190</rdf:li><rdf:li>255, 255</rdf:li></rdf:Seq>
            </crs:ToneCurvePV2012>
        """.trimIndent()
        val archive = ByteArrayOutputStream().also { bytes ->
            ZipOutputStream(bytes).use { zip ->
                zip.putNextEntry(ZipEntry("Preset Pack/Seashell.xmp"))
                zip.write(xmp.toByteArray())
                zip.closeEntry()
            }
        }.toByteArray()

        val decoded = CurvesPresetCodec.decode(archive)

        assertEquals(190f / 255f, decoded.controlPoints[0][3])
    }
}
