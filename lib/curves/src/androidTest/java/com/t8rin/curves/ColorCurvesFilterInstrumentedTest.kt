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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class ColorCurvesFilterInstrumentedTest {

    @Test
    fun everyCurveTypeCompilesAndRenders() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val source = Bitmap.createBitmap(
            intArrayOf(
                Color.RED,
                Color.GREEN,
                Color.BLUE,
                Color.WHITE
            ),
            2,
            2,
            Bitmap.Config.ARGB_8888
        )

        ImageCurvesEditorType.entries.forEach { type ->
            val state = ImageCurvesEditorState.Default
            val curve = state.curvesToolValue.curvesFor(type).first()
            curve.addPoint(
                x = 0.5f,
                y = if (type.centeredCurve) 0.65f else 0.4f
            )

            val result = GPUImage(context).run {
                setImage(source)
                setFilter(state.buildFilter())
                bitmapWithFilterApplied
            }

            assertEquals(type.title, 2, result.width)
            assertEquals(type.title, 2, result.height)
            result.recycle()
        }

        source.recycle()
    }

    @Test
    fun relationCurvesAdjustExpectedColorComponents() {
        val sourceColor = Color.rgb(204, 102, 68)
        val sourceHsv = sourceColor.toHsv()

        listOf(
            ImageCurvesEditorType.HueVsSat,
            ImageCurvesEditorType.LumaVsSat,
            ImageCurvesEditorType.SatVsSat
        ).forEach { type ->
            val resultHsv = renderFlatCurve(type, 0.25f, sourceColor).toHsv()

            assertTrue(type.title, resultHsv[1] < sourceHsv[1])
            assertTrue(type.title, hueDistance(resultHsv[0], sourceHsv[0]) < 2f)
        }

        val mutedColor = Color.rgb(180, 150, 140)
        val mutedSaturation = mutedColor.toHsv()[1]
        listOf(
            ImageCurvesEditorType.HueVsSat,
            ImageCurvesEditorType.LumaVsSat,
            ImageCurvesEditorType.SatVsSat
        ).forEach { type ->
            val boostedSaturation = renderFlatCurve(type, 1f, mutedColor).toHsv()[1]

            assertTrue(type.title, boostedSaturation > mutedSaturation)
            assertTrue(type.title, boostedSaturation < 0.85f)
        }

        val lumaResult = renderFlatCurve(
            type = ImageCurvesEditorType.HueVsLuma,
            value = 0.6f,
            sourceColor = sourceColor
        )
        assertTrue(
            ImageCurvesEditorType.HueVsLuma.title,
            lumaResult.luma() > sourceColor.luma()
        )
        assertTrue(
            "${ImageCurvesEditorType.HueVsLuma.title}: " +
                    "${lumaResult.toHsv().contentToString()} vs ${sourceHsv.contentToString()}",
            hueDistance(lumaResult.toHsv()[0], sourceHsv[0]) < 6f
        )

        val darkenedValue = renderFlatCurve(
            type = ImageCurvesEditorType.HueVsLuma,
            value = 0f,
            sourceColor = sourceColor
        ).toHsv()[2]
        assertTrue(
            ImageCurvesEditorType.HueVsLuma.title,
            darkenedValue < 0.02f
        )

        val neutralGray = Color.rgb(160, 160, 160)
        val unchangedGray = renderFlatCurve(
            type = ImageCurvesEditorType.HueVsLuma,
            value = 0f,
            sourceColor = neutralGray
        )
        assertTrue(abs(Color.red(neutralGray) - Color.red(unchangedGray)) <= 2)
        assertTrue(abs(Color.green(neutralGray) - Color.green(unchangedGray)) <= 2)
        assertTrue(abs(Color.blue(neutralGray) - Color.blue(unchangedGray)) <= 2)

        val saturatedRed = Color.rgb(230, 20, 10)
        val darkenedRed = renderFlatCurve(
            type = ImageCurvesEditorType.HueVsLuma,
            value = 0.3f,
            sourceColor = saturatedRed
        )
        assertTrue(darkenedRed.luma() < 0.02f)

        val darkColoredNoise = Color.rgb(16, 24, 28)
        val preservedDarkDetail = renderFlatCurve(
            type = ImageCurvesEditorType.HueVsLuma,
            value = 1f,
            sourceColor = darkColoredNoise
        )
        assertTrue(
            abs(preservedDarkDetail.luma() - darkColoredNoise.luma()) < 0.03f
        )

        val rotatedHue = renderFlatCurve(
            type = ImageCurvesEditorType.HueVsHue,
            value = 0.75f,
            sourceColor = Color.RED
        ).toHsv()[0]
        assertTrue(
            "${ImageCurvesEditorType.HueVsHue.title}: $rotatedHue",
            rotatedHue in 85f..95f
        )
    }

    @Test
    fun referenceStyleCurvesUseHsvValueAndAdditiveAdjustments() {
        val brightRed = Color.rgb(230, 20, 10)
        val darkRed = Color.rgb(64, 6, 3)
        val descendingCurve = listOf(
            PointF(0f, 0.75f),
            PointF(0.35f, 0.75f),
            PointF(0.65f, 0f),
            PointF(1f, 0f)
        )
        val ascendingCurve = listOf(
            PointF(0f, 0.25f),
            PointF(0.35f, 0.25f),
            PointF(0.65f, 0.75f),
            PointF(1f, 0.75f)
        )
        val lumaVsSatState = ImageCurvesEditorState.Default
        val lumaVsSatCurve = lumaVsSatState.curvesToolValue
            .curvesFor(ImageCurvesEditorType.LumaVsSat)
            .first()
        lumaVsSatCurve.replacePoints(descendingCurve)
        assertTrue(!lumaVsSatCurve.isDefault)
        assertTrue(lumaVsSatCurve.toLut()[230] < 0.05f)
        val desaturatedBrightRed = renderCurve(
            type = ImageCurvesEditorType.LumaVsSat,
            points = descendingCurve,
            sourceColor = brightRed
        ).toHsv()
        assertTrue(
            "Luma vs Sat: ${desaturatedBrightRed.contentToString()}",
            desaturatedBrightRed[1] < 0.05f
        )
        assertTrue(desaturatedBrightRed[2] > 0.85f)

        val brightHue = renderCurve(
            type = ImageCurvesEditorType.LumaVsHue,
            points = ascendingCurve,
            sourceColor = brightRed
        ).toHsv()[0]
        val darkHue = renderCurve(
            type = ImageCurvesEditorType.LumaVsHue,
            points = ascendingCurve,
            sourceColor = darkRed
        ).toHsv()[0]
        assertTrue(brightHue in 60f..120f)
        assertTrue(darkHue in 240f..320f)

        val tintedGray = renderFlatCurve(
            type = ImageCurvesEditorType.HueVsSat,
            value = 1f,
            sourceColor = Color.rgb(160, 160, 160)
        ).toHsv()
        assertTrue(tintedGray[1] in 0.25f..0.4f)

        val reducedRedSaturation = renderCurve(
            type = ImageCurvesEditorType.SatVsSat,
            points = listOf(
                PointF(0f, 0.8f),
                PointF(0.3f, 0.8f),
                PointF(0.75f, 0.2f),
                PointF(1f, 0.2f)
            ),
            sourceColor = brightRed
        ).toHsv()[1]
        assertTrue(reducedRedSaturation in 0.25f..0.5f)
    }

    @Test
    fun centeredCurvePreservesControlPointAtBoundary() {
        val curve = ImageCurvesEditorState.Default.curvesToolValue
            .curvesFor(ImageCurvesEditorType.SatVsSat)
            .first()
        curve.replacePoints(
            listOf(
                PointF(0f, 0.7f),
                PointF(0f, 0.7f),
                PointF(1f, 0.7f)
            )
        )

        val copiedPoints = curve.copy().points

        assertEquals(3, copiedPoints.size)
        assertEquals(0f, copiedPoints[1].x)
    }

    @Test
    fun hueCurveIsContinuousAcrossRedBoundary() {
        val curve = ImageCurvesEditorState.Default.curvesToolValue
            .curvesFor(ImageCurvesEditorType.HueVsLuma)
            .first()
        curve.replacePoints(
            listOf(
                PointF(0f, 0.5f),
                PointF(0.3f, 0.1f),
                PointF(0.65f, 0.9f),
                PointF(1f, 0.5f)
            )
        )

        val interpolated = curve.interpolateCurve()
        val lut = curve.toLut()

        assertTrue(abs(lut.first() - lut.last()) < 0.01f)
        assertTrue(
            interpolated
                .toList()
                .chunked(2)
                .zipWithNext()
                .all { (first, second) -> first[0] < second[0] }
        )
        assertTrue(
            interpolated
                .toList()
                .chunked(2)
                .all { point -> point[1] in 0f..1f }
        )
    }

    @Test
    fun linearInputCurveExtendsNearestControlPointToEdges() {
        val curve = ImageCurvesEditorState.Default.curvesToolValue
            .curvesFor(ImageCurvesEditorType.LumaVsSat)
            .first()
        curve.replacePoints(
            listOf(
                PointF(0f, 0.5f),
                PointF(0.35f, 0.9f),
                PointF(0.65f, 0.1f),
                PointF(1f, 0.5f)
            )
        )

        val interpolated = curve.interpolateCurve()
        val lut = curve.toLut()

        assertEquals(0.9f, interpolated[1], 0.00001f)
        assertEquals(0.1f, interpolated[interpolated.lastIndex], 0.00001f)
        assertEquals(0.9f, lut[(0.3f * lut.lastIndex).toInt()], 0.00001f)
        assertEquals(0.1f, lut[(0.7f * lut.lastIndex).toInt()], 0.00001f)
        assertTrue(
            interpolated
                .toList()
                .chunked(2)
                .zipWithNext()
                .all { (first, second) -> first[0] < second[0] }
        )
        assertTrue(
            interpolated
                .toList()
                .chunked(2)
                .all { point -> point[1] in 0f..1f }
        )
    }

    private fun renderFlatCurve(
        type: ImageCurvesEditorType,
        value: Float,
        sourceColor: Int
    ): Int = renderCurve(
        type = type,
        points = if (type.centeredCurve) {
            listOf(
                PointF(0f, value),
                PointF(0.5f, value),
                PointF(1f, value)
            )
        } else {
            listOf(
                PointF(0f, value),
                PointF(1f, value)
            )
        },
        sourceColor = sourceColor
    )

    private fun renderCurve(
        type: ImageCurvesEditorType,
        points: List<PointF>,
        sourceColor: Int
    ): Int {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val state = ImageCurvesEditorState.Default
        state.curvesToolValue.curvesFor(type).first().replacePoints(points)
        val source = Bitmap.createBitmap(
            IntArray(16) { sourceColor },
            4,
            4,
            Bitmap.Config.ARGB_8888
        )
        val result = GPUImage(context).run {
            setImage(source)
            val filter = state.buildFilter()
            setFilter(filter)
            bitmapWithFilterApplied.also {
                assertProgramsCompiled(type, filter)
            }
        }
        return result.getPixel(0, 0).also {
            source.recycle()
            result.recycle()
        }
    }

    private fun Int.toHsv(): FloatArray = FloatArray(3).also {
        Color.colorToHSV(this, it)
    }

    private fun Int.luma(): Float {
        return (
                Color.red(this) * 0.2126f +
                        Color.green(this) * 0.7152f +
                        Color.blue(this) * 0.0722f
                ) / 255f
    }

    private fun hueDistance(first: Float, second: Float): Float {
        val distance = abs(first - second)
        return minOf(distance, 360f - distance)
    }

    private fun assertProgramsCompiled(
        type: ImageCurvesEditorType,
        filter: GPUImageFilter
    ) {
        assertTrue("${type.title} shader did not compile", filter.program != 0)
        if (filter is GPUImageFilterGroup) {
            filter.mergedFilters.forEach { child ->
                assertProgramsCompiled(type, child)
            }
        }
    }

}
