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

@file:Suppress("FunctionName")

package com.t8rin.collages.utils

import android.graphics.RectF
import com.t8rin.collages.model.TemplateItem

internal object ExtendedFrameImage {

    private fun ParamsManagerBuilder.fixedRect(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
    ) {
        addBoxedItem(boxParams = { RectF(left, top, right, bottom) })
    }

    private fun ParamsManagerBuilder.uniformGridInRegion(
        rows: Int,
        cols: Int,
        regionLeft: Float = 0f,
        regionTop: Float = 0f,
        regionRight: Float = 1f,
        regionBottom: Float = 1f,
    ) {
        val w = regionRight - regionLeft
        val h = regionBottom - regionTop
        val cw = w / cols
        val rh = h / rows
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                fixedRect(
                    regionLeft + c * cw,
                    regionTop + r * rh,
                    regionLeft + (c + 1) * cw,
                    regionTop + (r + 1) * rh,
                )
            }
        }
    }

    private fun ParamsManagerBuilder.rowStrip(
        rowTop: Float,
        rowBottom: Float,
        cellCount: Int,
        regionLeft: Float = 0f,
        regionRight: Float = 1f,
    ) {
        val w = (regionRight - regionLeft) / cellCount
        for (i in 0 until cellCount) {
            fixedRect(
                regionLeft + i * w,
                rowTop,
                regionLeft + (i + 1) * w,
                rowBottom,
            )
        }
    }

    private fun ParamsManagerBuilder.parametricStackedRowStrips(
        rowCounts: List<Int>,
    ) {
        val numRows = rowCounts.size
        if (numRows == 0) return
        val yRowParams = (1 until numRows).map { r -> param(r.toFloat() / numRows) }
        val xRowParamsList = rowCounts.map { k ->
            if (k <= 1) emptyList()
            else (1 until k).map { j -> param(j.toFloat() / k) }
        }
        for (r in rowCounts.indices) {
            val k = rowCounts[r]
            if (k <= 0) continue
            val xRowParams = xRowParamsList[r]
            repeat(k) { i ->
                val xp = buildList {
                    if (i > 0) add(xRowParams[i - 1])
                    if (i < k - 1) add(xRowParams[i])
                }.distinct()
                val yp = buildList {
                    if (r > 0) add(yRowParams[r - 1])
                    if (r < numRows - 1) add(yRowParams[r])
                }.distinct()
                addBoxedItem(
                    xParams = xp,
                    yParams = yp,
                    boxParams = { vs ->
                        val top = if (r == 0) 0f else vs[yRowParams[r - 1]]
                        val bottom = if (r == numRows - 1) 1f else vs[yRowParams[r]]
                        val left = if (i == 0) 0f else vs[xRowParams[i - 1]]
                        val right = if (i == k - 1) 1f else vs[xRowParams[i]]
                        RectF(left, top, right, bottom)
                    },
                )
            }
        }
    }

    internal fun collage_2_12(): TemplateItem {
        return TwoFrameImage.collage_2_0("collage_2_12", 0.38f)
    }

    internal fun collage_2_13(): TemplateItem {
        return TwoFrameImage.collage_2_1("collage_2_13", 0.42f)
    }

    internal fun collage_3_48(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_3_48") {
            val x1 = param(0.34f)
            val y1 = param(0.5f)
            addBoxedItem(
                xParams = listOf(x1),
                boxParams = { vs -> RectF(0f, 0f, vs[x1], 1f) },
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x1], 0f, 1f, vs[y1]) },
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x1], vs[y1], 1f, 1f) },
            )
        }
    }

    internal fun collage_4_26(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_4_26") {
            val y1 = param(0.55f)
            addBoxedItem(
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, 0f, 1f, vs[y1]) },
            )
            val x1 = param(0.3333f)
            val x2 = param(0.6667f)
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, vs[y1], vs[x1], 1f) },
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x1], vs[y1], vs[x2], 1f) },
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x2], vs[y1], 1f, 1f) },
            )
        }
    }

    internal fun collage_4_27(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_4_27") {
            fixedRect(0f, 0f, 0.55f, 1f)
            fixedRect(0.55f, 0f, 1f, 1f / 3f)
            fixedRect(0.55f, 1f / 3f, 1f, 2f / 3f)
            fixedRect(0.55f, 2f / 3f, 1f, 1f)
        }
    }

    internal fun collage_5_32(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_5_32") {
            val x1 = param(0.28f)
            val x2 = param(0.72f)
            val y1 = param(0.28f)
            val y2 = param(0.72f)
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, 0f, vs[x1], vs[y1]) },
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x2], 0f, 1f, vs[y1]) },
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(0f, vs[y2], vs[x1], 1f) },
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[x2], vs[y2], 1f, 1f) },
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[x1], vs[y1], vs[x2], vs[y2]) },
            )
        }
    }

    internal fun collage_5_33(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_5_33") {
            rowStrip(0f, 0.5f, 2)
            rowStrip(0.5f, 1f, 3)
        }
    }

    internal fun collage_6_15(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_6_15") {
            rowStrip(0f, 1f / 3f, 1)
            rowStrip(1f / 3f, 2f / 3f, 2)
            rowStrip(2f / 3f, 1f, 3)
        }
    }

    internal fun collage_6_16(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_6_16") {
            rowStrip(0f, 1f / 3f, 2)
            rowStrip(1f / 3f, 2f / 3f, 2)
            rowStrip(2f / 3f, 1f, 2)
        }
    }

    internal fun collage_6_17(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_6_17") {
            rowStrip(0f, 1f / 3f, 3)
            rowStrip(1f / 3f, 2f / 3f, 2)
            rowStrip(2f / 3f, 1f, 1)
        }
    }

    internal fun collage_7_11(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_7_11") {
            rowStrip(0f, 1f / 3f, 3)
            rowStrip(1f / 3f, 2f / 3f, 2)
            rowStrip(2f / 3f, 1f, 2)
        }
    }

    internal fun collage_7_12(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_7_12") {
            rowStrip(0f, 0.32f, 3)
            rowStrip(0.32f, 0.55f, 1)
            rowStrip(0.55f, 1f, 3)
        }
    }

    internal fun collage_8_17(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_8_17") {
            rowStrip(0f, 0.35f, 3)
            rowStrip(0.35f, 0.7f, 3)
            rowStrip(0.7f, 1f, 2)
        }
    }

    internal fun collage_8_18(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_8_18") {
            rowStrip(0f, 0.25f, 2)
            rowStrip(0.25f, 0.5f, 2)
            rowStrip(0.5f, 0.75f, 2)
            rowStrip(0.75f, 1f, 2)
        }
    }

    internal fun collage_9_12(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_9_12") {
            rowStrip(0f, 1f / 3f, 2)
            rowStrip(1f / 3f, 2f / 3f, 3)
            rowStrip(2f / 3f, 1f, 4)
        }
    }

    internal fun collage_9_13(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_9_13") {
            rowStrip(0f, 1f / 3f, 3)
            rowStrip(1f / 3f, 2f / 3f, 3)
            rowStrip(2f / 3f, 1f, 3)
        }
    }

    internal fun collage_10_9(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_10_9") {
            uniformGridInRegion(3, 3, 0f, 0f, 1f, 0.75f)
            fixedRect(0f, 0.75f, 1f, 1f)
        }
    }

    internal fun collage_10_10(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_10_10") {
            uniformGridInRegion(2, 5, 0f, 0f, 1f, 1f)
        }
    }

    internal fun collage_11_0(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_11_0") {
            parametricStackedRowStrips(listOf(3, 4, 4))
        }
    }

    internal fun collage_11_1(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_11_1") {
            parametricStackedRowStrips(listOf(5, 6))
        }
    }

    internal fun collage_11_2(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_11_2") {
            parametricStackedRowStrips(listOf(4, 4, 3))
        }
    }

    internal fun collage_11_3(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_11_3") {
            parametricStackedRowStrips(listOf(3, 3, 5))
        }
    }

    internal fun collage_11_4(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_11_4") {
            parametricStackedRowStrips(listOf(4, 3, 4))
        }
    }

    internal fun collage_11_5(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_11_5") {
            parametricStackedRowStrips(listOf(5, 3, 3))
        }
    }

    internal fun collage_12_0(): TemplateItem {
        return FrameImageUtils.collageParametricGrid("collage_12_0", rows = 3, cols = 4)
    }

    internal fun collage_12_1(): TemplateItem {
        return FrameImageUtils.collageParametricGrid("collage_12_1", rows = 4, cols = 3)
    }

    internal fun collage_12_2(): TemplateItem {
        return FrameImageUtils.collageParametricGrid("collage_12_2", rows = 2, cols = 6)
    }

    internal fun collage_12_3(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_12_3") {
            parametricStackedRowStrips(listOf(2, 3, 3, 4))
        }
    }

    internal fun collage_12_4(): TemplateItem {
        return FrameImageUtils.collageParametricGrid("collage_12_4", rows = 6, cols = 2)
    }

    internal fun collage_12_5(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_12_5") {
            parametricStackedRowStrips(listOf(4, 3, 3, 2))
        }
    }

    internal fun collage_13_0(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_13_0") {
            parametricStackedRowStrips(listOf(4, 4, 5))
        }
    }

    internal fun collage_13_1(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_13_1") {
            parametricStackedRowStrips(listOf(3, 3, 3, 4))
        }
    }

    internal fun collage_13_2(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_13_2") {
            parametricStackedRowStrips(listOf(5, 4, 4))
        }
    }

    internal fun collage_13_3(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_13_3") {
            parametricStackedRowStrips(listOf(3, 4, 6))
        }
    }

    internal fun collage_13_4(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_13_4") {
            parametricStackedRowStrips(listOf(2, 5, 6))
        }
    }

    internal fun collage_13_5(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_13_5") {
            parametricStackedRowStrips(listOf(4, 5, 4))
        }
    }

    internal fun collage_14_0(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_14_0") {
            parametricStackedRowStrips(listOf(7, 7))
        }
    }

    internal fun collage_14_1(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_14_1") {
            parametricStackedRowStrips(listOf(4, 5, 5))
        }
    }

    internal fun collage_14_2(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_14_2") {
            parametricStackedRowStrips(listOf(3, 4, 4, 3))
        }
    }

    internal fun collage_14_3(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_14_3") {
            parametricStackedRowStrips(listOf(5, 5, 4))
        }
    }

    internal fun collage_14_4(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_14_4") {
            parametricStackedRowStrips(listOf(6, 4, 4))
        }
    }

    internal fun collage_14_5(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_14_5") {
            parametricStackedRowStrips(listOf(4, 4, 3, 3))
        }
    }

    internal fun collage_15_0(): TemplateItem {
        return FrameImageUtils.collageParametricGrid("collage_15_0", rows = 3, cols = 5)
    }

    internal fun collage_15_1(): TemplateItem {
        return FrameImageUtils.collageParametricGrid("collage_15_1", rows = 5, cols = 3)
    }

    internal fun collage_15_2(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_15_2") {
            parametricStackedRowStrips(listOf(2, 3, 3, 4, 5))
        }
    }

    internal fun collage_15_3(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_15_3") {
            parametricStackedRowStrips(listOf(4, 5, 6))
        }
    }

    internal fun collage_15_4(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_15_4") {
            parametricStackedRowStrips(listOf(3, 5, 7))
        }
    }

    internal fun collage_15_5(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_15_5") {
            parametricStackedRowStrips(listOf(4, 4, 4, 3))
        }
    }

    internal fun collage_16_0(): TemplateItem {
        return FrameImageUtils.collageParametricGrid("collage_16_0", rows = 4, cols = 4)
    }

    internal fun collage_16_1(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_16_1") {
            parametricStackedRowStrips(listOf(5, 5, 6))
        }
    }

    internal fun collage_16_2(): TemplateItem {
        return FrameImageUtils.collageParametricGrid("collage_16_2", rows = 8, cols = 2)
    }

    internal fun collage_16_3(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_16_3") {
            parametricStackedRowStrips(listOf(3, 4, 5, 4))
        }
    }

    internal fun collage_16_4(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_16_4") {
            parametricStackedRowStrips(listOf(2, 4, 4, 6))
        }
    }

    internal fun collage_16_5(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_16_5") {
            parametricStackedRowStrips(listOf(6, 5, 5))
        }
    }

    internal fun collage_17_0(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_17_0") {
            parametricStackedRowStrips(listOf(4, 4, 4, 5))
        }
    }

    internal fun collage_17_1(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_17_1") {
            parametricStackedRowStrips(listOf(6, 6, 5))
        }
    }

    internal fun collage_17_2(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_17_2") {
            parametricStackedRowStrips(listOf(5, 6, 6))
        }
    }

    internal fun collage_17_3(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_17_3") {
            parametricStackedRowStrips(listOf(4, 4, 5, 4))
        }
    }

    internal fun collage_17_4(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_17_4") {
            parametricStackedRowStrips(listOf(7, 5, 5))
        }
    }

    internal fun collage_17_5(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_17_5") {
            parametricStackedRowStrips(listOf(3, 4, 4, 6))
        }
    }

    internal fun collage_18_0(): TemplateItem {
        return FrameImageUtils.collageParametricGrid("collage_18_0", rows = 3, cols = 6)
    }

    internal fun collage_18_1(): TemplateItem {
        return FrameImageUtils.collageParametricGrid("collage_18_1", rows = 6, cols = 3)
    }

    internal fun collage_18_2(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_18_2") {
            parametricStackedRowStrips(listOf(6, 6, 6))
        }
    }

    internal fun collage_18_3(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_18_3") {
            parametricStackedRowStrips(listOf(4, 5, 5, 4))
        }
    }

    internal fun collage_18_4(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_18_4") {
            parametricStackedRowStrips(listOf(3, 5, 4, 6))
        }
    }

    internal fun collage_18_5(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_18_5") {
            parametricStackedRowStrips(listOf(4, 4, 4, 3, 3))
        }
    }

    internal fun collage_19_0(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_19_0") {
            parametricStackedRowStrips(listOf(5, 5, 5, 4))
        }
    }

    internal fun collage_19_1(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_19_1") {
            parametricStackedRowStrips(listOf(4, 5, 5, 5))
        }
    }

    internal fun collage_19_2(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_19_2") {
            parametricStackedRowStrips(listOf(6, 6, 7))
        }
    }

    internal fun collage_19_3(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_19_3") {
            parametricStackedRowStrips(listOf(3, 4, 5, 7))
        }
    }

    internal fun collage_19_4(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_19_4") {
            parametricStackedRowStrips(listOf(2, 6, 6, 5))
        }
    }

    internal fun collage_19_5(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_19_5") {
            parametricStackedRowStrips(listOf(4, 4, 4, 4, 3))
        }
    }

    internal fun collage_20_0(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_20_0") {
            parametricStackedRowStrips(listOf(7, 6, 7))
        }
    }

    internal fun collage_20_1(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_20_1") {
            parametricStackedRowStrips(listOf(6, 5, 4, 3, 2))
        }
    }

    internal fun collage_20_2(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_20_2") {
            parametricStackedRowStrips(listOf(4, 4, 4, 4, 4))
        }
    }

    internal fun collage_20_3(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_20_3") {
            parametricStackedRowStrips(listOf(5, 5, 5, 5))
        }
    }

    internal fun collage_20_4(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_20_4") {
            parametricStackedRowStrips(listOf(3, 7, 7, 3))
        }
    }

    internal fun collage_20_5(): TemplateItem {
        return FrameImageUtils.buildParamsCollage("collage_20_5") {
            parametricStackedRowStrips(listOf(7, 6, 4, 3))
        }
    }

}
