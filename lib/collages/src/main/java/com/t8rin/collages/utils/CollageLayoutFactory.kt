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

import android.content.Context
import android.graphics.Path
import android.graphics.RectF
import androidx.core.net.toUri
import com.t8rin.collages.frames.EightFrameImage
import com.t8rin.collages.frames.ExtendedFrameImage
import com.t8rin.collages.frames.FiveFrameImage
import com.t8rin.collages.frames.FourFrameImage
import com.t8rin.collages.frames.NineFrameImage
import com.t8rin.collages.frames.SevenFrameImage
import com.t8rin.collages.frames.SixFrameImage
import com.t8rin.collages.frames.TenFrameImage
import com.t8rin.collages.frames.ThreeFrameImage
import com.t8rin.collages.frames.TwoFrameImage
import com.t8rin.collages.model.CollageLayout

internal object CollageLayoutFactory {
    private const val FRAME_FOLDER = "frame"

    fun collage(
        frameName: String
    ): CollageLayout = CollageLayout(
        preview = ("file:///android_asset/$FRAME_FOLDER/$frameName.webp").toUri(),
        title = frameName
    )

    fun collage(
        frameName: String,
        setup: ParamsManagerBuilder.() -> Unit
    ): CollageLayout {
        val (paramsManager, photoItemList) = ParamsManagerBuilder().apply(setup).build()
        return collage(frameName).copy(paramsManager = paramsManager, photoItemList = photoItemList)
    }

    fun createHeartItem(top: Float, size: Float): Path {
        return Path().apply {
            moveTo(top, top + size / 4)
            quadTo(top, top, top + size / 4, top)
            quadTo(top + size / 2, top, top + size / 2, top + size / 4)
            quadTo(top + size / 2, top, top + size * 3 / 4, top)
            quadTo(top + size, top, top + size, top + size / 4)
            quadTo(top + size, top + size / 2, top + size * 3 / 4, top + size * 3 / 4)
            lineTo(top + size / 2, top + size)
            lineTo(top + size / 4, top + size * 3 / 4)
            quadTo(top, top + size / 2, top, top + size / 4)
        }
    }

    fun loadFrameImages(context: Context): List<CollageLayout> {
        return runCatching {
            context.assets.list(FRAME_FOLDER).orEmpty().mapNotNull {
                createCollageLayouts(it.substringBeforeLast('.'))
            }.sortedWith { lhs, rhs -> lhs.photoItemList.size - rhs.photoItemList.size }
        }.onFailure {
            it.printStackTrace()
        }.getOrNull().orEmpty()
    }

    internal fun collageLinear(
        name: String,
        count: Int,
        isHorizontal: Boolean,
    ): CollageLayout {
        return collage(name) {
            if (count <= 0) return@collage

            val params = (1 until count).map { i ->
                param(i.toFloat() / count)
            }

            repeat(count) { index ->
                addBoxedItem(
                    xParams = if (isHorizontal) params else emptyList(),
                    yParams = if (!isHorizontal) params else emptyList(),
                    boxParams = { vs ->
                        if (isHorizontal) {
                            val left = if (index == 0) 0f else vs[params[index - 1]]
                            val right = if (index == count - 1) 1f else vs[params[index]]
                            RectF(left, 0f, right, 1f)
                        } else {
                            val top = if (index == 0) 0f else vs[params[index - 1]]
                            val bottom = if (index == count - 1) 1f else vs[params[index]]
                            RectF(0f, top, 1f, bottom)
                        }
                    }
                )
            }
        }
    }

    internal fun collageParametricGrid(
        name: String,
        rows: Int,
        cols: Int,
    ): CollageLayout {
        return collage(name) {
            if (rows <= 0 || cols <= 0) return@collage
            val xColParams = (1 until cols).map { c -> param(c.toFloat() / cols) }
            val yRowParams = (1 until rows).map { r -> param(r.toFloat() / rows) }
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val xp = buildList {
                        if (c > 0) add(xColParams[c - 1])
                        if (c < cols - 1) add(xColParams[c])
                    }.distinct()
                    val yp = buildList {
                        if (r > 0) add(yRowParams[r - 1])
                        if (r < rows - 1) add(yRowParams[r])
                    }.distinct()
                    addBoxedItem(
                        xParams = xp,
                        yParams = yp,
                        boxParams = { vs ->
                            RectF(
                                if (c == 0) 0f else vs[xColParams[c - 1]],
                                if (r == 0) 0f else vs[yRowParams[r - 1]],
                                if (c == cols - 1) 1f else vs[xColParams[c]],
                                if (r == rows - 1) 1f else vs[yRowParams[r]],
                            )
                        },
                    )
                }
            }
        }
    }

    internal fun createCollageLayouts(frameName: String): CollageLayout? {
        return COLLAGE_MAP[frameName]?.invoke()
    }

    internal val COLLAGE_MAP: Map<String, () -> CollageLayout> = mapOf(
        "collage_1_0" to { ExtendedFrameImage.collage_1_0() },
        "collage_2_0" to { TwoFrameImage.collage_2_0() },
        "collage_2_1" to { TwoFrameImage.collage_2_1() },
        "collage_2_2" to { TwoFrameImage.collage_2_2() },
        "collage_2_3" to { TwoFrameImage.collage_2_3() },
        "collage_2_4" to { TwoFrameImage.collage_2_4() },
        "collage_2_5" to { TwoFrameImage.collage_2_5() },
        "collage_2_6" to { TwoFrameImage.collage_2_6() },
        "collage_2_7" to { TwoFrameImage.collage_2_7() },
        "collage_2_8" to { TwoFrameImage.collage_2_8() },
        "collage_2_9" to { TwoFrameImage.collage_2_9() },
        "collage_2_10" to { TwoFrameImage.collage_2_10() },
        "collage_2_11" to { TwoFrameImage.collage_2_11() },
        "collage_3_0" to { ThreeFrameImage.collage_3_0() },
        "collage_3_1" to { ThreeFrameImage.collage_3_1() },
        "collage_3_3" to { ThreeFrameImage.collage_3_3() },
        "collage_3_4" to { ThreeFrameImage.collage_3_4() },
        "collage_3_5" to { ThreeFrameImage.collage_3_5() },
        "collage_3_6" to { ThreeFrameImage.collage_3_6() },
        "collage_3_7" to { ThreeFrameImage.collage_3_7() },
        "collage_3_8" to { ThreeFrameImage.collage_3_8() },
        "collage_3_9" to { ThreeFrameImage.collage_3_9() },
        "collage_3_10" to { ThreeFrameImage.collage_3_10() },
        "collage_3_11" to { ThreeFrameImage.collage_3_11() },
        "collage_3_12" to { ThreeFrameImage.collage_3_12() },
        "collage_3_13" to { ThreeFrameImage.collage_3_13() },
        "collage_3_14" to { ThreeFrameImage.collage_3_14() },
        "collage_3_15" to { ThreeFrameImage.collage_3_15() },
        "collage_3_16" to { ThreeFrameImage.collage_3_16() },
        "collage_3_17" to { ThreeFrameImage.collage_3_17() },
        "collage_3_18" to { ThreeFrameImage.collage_3_18() },
        "collage_3_19" to { ThreeFrameImage.collage_3_19() },
        "collage_3_20" to { ThreeFrameImage.collage_3_20() },
        "collage_3_21" to { ThreeFrameImage.collage_3_21() },
        "collage_3_22" to { ThreeFrameImage.collage_3_22() },
        "collage_3_23" to { ThreeFrameImage.collage_3_23() },
        "collage_3_24" to { ThreeFrameImage.collage_3_24() },
        "collage_3_25" to { ThreeFrameImage.collage_3_25() },
        "collage_3_26" to { ThreeFrameImage.collage_3_26() },
        "collage_3_27" to { ThreeFrameImage.collage_3_27() },
        "collage_3_28" to { ThreeFrameImage.collage_3_28() },
        "collage_3_29" to { ThreeFrameImage.collage_3_29() },
        "collage_3_30" to { ThreeFrameImage.collage_3_30() },
        "collage_3_31" to { ThreeFrameImage.collage_3_31() },
        "collage_3_32" to { ThreeFrameImage.collage_3_32() },
        "collage_3_33" to { ThreeFrameImage.collage_3_33() },
        "collage_3_34" to { ThreeFrameImage.collage_3_34() },
        "collage_3_35" to { ThreeFrameImage.collage_3_35() },
        "collage_3_36" to { ThreeFrameImage.collage_3_36() },
        "collage_3_37" to { ThreeFrameImage.collage_3_37() },
        "collage_3_38" to { ThreeFrameImage.collage_3_38() },
        "collage_3_39" to { ThreeFrameImage.collage_3_39() },
        "collage_3_40" to { ThreeFrameImage.collage_3_40() },
        "collage_3_41" to { ThreeFrameImage.collage_3_41() },
        "collage_3_42" to { ThreeFrameImage.collage_3_42() },
        "collage_3_43" to { ThreeFrameImage.collage_3_43() },
        "collage_3_44" to { ThreeFrameImage.collage_3_44() },
        "collage_3_45" to { ThreeFrameImage.collage_3_45() },
        "collage_3_46" to { ThreeFrameImage.collage_3_46() },
        "collage_3_47" to { ThreeFrameImage.collage_3_47() },
        "collage_4_0" to { FourFrameImage.collage_4_0() },
        "collage_4_1" to { FourFrameImage.collage_4_1() },
        "collage_4_1_1" to { FourFrameImage.collage_4_1_1() },
        "collage_4_2" to { FourFrameImage.collage_4_2() },
        "collage_4_4" to { FourFrameImage.collage_4_4() },
        "collage_4_5" to { FourFrameImage.collage_4_5() },
        "collage_4_6" to { FourFrameImage.collage_4_6() },
        "collage_4_7" to { FourFrameImage.collage_4_7() },
        "collage_4_8" to { FourFrameImage.collage_4_8() },
        "collage_4_9" to { FourFrameImage.collage_4_9() },
        "collage_4_10" to { FourFrameImage.collage_4_10() },
        "collage_4_11" to { FourFrameImage.collage_4_11() },
        "collage_4_12" to { FourFrameImage.collage_4_12() },
        "collage_4_13" to { FourFrameImage.collage_4_13() },
        "collage_4_14" to { FourFrameImage.collage_4_14() },
        "collage_4_15" to { FourFrameImage.collage_4_15() },
        "collage_4_16" to { FourFrameImage.collage_4_16() },
        "collage_4_17" to { FourFrameImage.collage_4_17() },
        "collage_4_18" to { FourFrameImage.collage_4_18() },
        "collage_4_19" to { FourFrameImage.collage_4_19() },
        "collage_4_20" to { FourFrameImage.collage_4_20() },
        "collage_4_21" to { FourFrameImage.collage_4_21() },
        "collage_4_22" to { FourFrameImage.collage_4_22() },
        "collage_4_23" to { FourFrameImage.collage_4_23() },
        "collage_4_24" to { FourFrameImage.collage_4_24() },
        "collage_4_25" to { FourFrameImage.collage_4_25() },
        "collage_5_1_1" to { FiveFrameImage.collage_5_1_1() },
        "collage_5_1_2" to { FiveFrameImage.collage_5_1_2() },
        "collage_5_0" to { FiveFrameImage.collage_5_0() },
        "collage_5_1" to { FiveFrameImage.collage_5_1() },
        "collage_5_2" to { FiveFrameImage.collage_5_2() },
        "collage_5_3" to { FiveFrameImage.collage_5_3() },
        "collage_5_4" to { FiveFrameImage.collage_5_4() },
        "collage_5_5" to { FiveFrameImage.collage_5_5() },
        "collage_5_6" to { FiveFrameImage.collage_5_6() },
        "collage_5_7" to { FiveFrameImage.collage_5_7() },
        "collage_5_8" to { FiveFrameImage.collage_5_8() },
        "collage_5_9" to { FiveFrameImage.collage_5_9() },
        "collage_5_10" to { FiveFrameImage.collage_5_10() },
        "collage_5_11" to { FiveFrameImage.collage_5_11() },
        "collage_5_12" to { FiveFrameImage.collage_5_12() },
        "collage_5_13" to { FiveFrameImage.collage_5_13() },
        "collage_5_14" to { FiveFrameImage.collage_5_14() },
        "collage_5_15" to { FiveFrameImage.collage_5_15() },
        "collage_5_16" to { FiveFrameImage.collage_5_16() },
        "collage_5_17" to { FiveFrameImage.collage_5_17() },
        "collage_5_18" to { FiveFrameImage.collage_5_18() },
        "collage_5_19" to { FiveFrameImage.collage_5_19() },
        "collage_5_20" to { FiveFrameImage.collage_5_20() },
        "collage_5_21" to { FiveFrameImage.collage_5_21() },
        "collage_5_22" to { FiveFrameImage.collage_5_22() },
        "collage_5_23" to { FiveFrameImage.collage_5_23() },
        "collage_5_24" to { FiveFrameImage.collage_5_24() },
        "collage_5_25" to { FiveFrameImage.collage_5_25() },
        "collage_5_26" to { FiveFrameImage.collage_5_26() },
        "collage_5_27" to { FiveFrameImage.collage_5_27() },
        "collage_5_28" to { FiveFrameImage.collage_5_28() },
        "collage_5_29" to { FiveFrameImage.collage_5_29() },
        "collage_5_30" to { FiveFrameImage.collage_5_30() },
        "collage_5_31" to { FiveFrameImage.collage_5_31() },
        "collage_6_1_1" to { SixFrameImage.collage_6_1_1() },
        "collage_6_1_2" to { SixFrameImage.collage_6_1_2() },
        "collage_6_0" to { SixFrameImage.collage_6_0() },
        "collage_6_1" to { SixFrameImage.collage_6_1() },
        "collage_6_2" to { SixFrameImage.collage_6_2() },
        "collage_6_3" to { SixFrameImage.collage_6_3() },
        "collage_6_4" to { SixFrameImage.collage_6_4() },
        "collage_6_5" to { SixFrameImage.collage_6_5() },
        "collage_6_6" to { SixFrameImage.collage_6_6() },
        "collage_6_7" to { SixFrameImage.collage_6_7() },
        "collage_6_8" to { SixFrameImage.collage_6_8() },
        "collage_6_9" to { SixFrameImage.collage_6_9() },
        "collage_6_10" to { SixFrameImage.collage_6_10() },
        "collage_6_11" to { SixFrameImage.collage_6_11() },
        "collage_6_12" to { SixFrameImage.collage_6_12() },
        "collage_6_13" to { SixFrameImage.collage_6_13() },
        "collage_6_14" to { SixFrameImage.collage_6_14() },
        "collage_7_1_1" to { SevenFrameImage.collage_7_1_1() },
        "collage_7_1_2" to { SevenFrameImage.collage_7_1_2() },
        "collage_7_0" to { SevenFrameImage.collage_7_0() },
        "collage_7_1" to { SevenFrameImage.collage_7_1() },
        "collage_7_2" to { SevenFrameImage.collage_7_2() },
        "collage_7_3" to { SevenFrameImage.collage_7_3() },
        "collage_7_4" to { SevenFrameImage.collage_7_4() },
        "collage_7_5" to { SevenFrameImage.collage_7_5() },
        "collage_7_6" to { SevenFrameImage.collage_7_6() },
        "collage_7_7" to { SevenFrameImage.collage_7_7() },
        "collage_7_8" to { SevenFrameImage.collage_7_8() },
        "collage_7_9" to { SevenFrameImage.collage_7_9() },
        "collage_7_10" to { SevenFrameImage.collage_7_10() },
        "collage_8_0" to { EightFrameImage.collage_8_0() },
        "collage_8_1" to { EightFrameImage.collage_8_1() },
        "collage_8_2" to { EightFrameImage.collage_8_2() },
        "collage_8_3" to { EightFrameImage.collage_8_3() },
        "collage_8_4" to { EightFrameImage.collage_8_4() },
        "collage_8_5" to { EightFrameImage.collage_8_5() },
        "collage_8_6" to { EightFrameImage.collage_8_6() },
        "collage_8_7" to { EightFrameImage.collage_8_7() },
        "collage_8_8" to { EightFrameImage.collage_8_8() },
        "collage_8_9" to { EightFrameImage.collage_8_9() },
        "collage_8_10" to { EightFrameImage.collage_8_10() },
        "collage_8_11" to { EightFrameImage.collage_8_11() },
        "collage_8_12" to { EightFrameImage.collage_8_12() },
        "collage_8_13" to { EightFrameImage.collage_8_13() },
        "collage_8_14" to { EightFrameImage.collage_8_14() },
        "collage_8_15" to { EightFrameImage.collage_8_15() },
        "collage_8_16" to { EightFrameImage.collage_8_16() },
        "collage_9_0" to { NineFrameImage.collage_9_0() },
        "collage_9_1" to { NineFrameImage.collage_9_1() },
        "collage_9_2" to { NineFrameImage.collage_9_2() },
        "collage_9_3" to { NineFrameImage.collage_9_3() },
        "collage_9_4" to { NineFrameImage.collage_9_4() },
        "collage_9_5" to { NineFrameImage.collage_9_5() },
        "collage_9_6" to { NineFrameImage.collage_9_6() },
        "collage_9_7" to { NineFrameImage.collage_9_7() },
        "collage_9_8" to { NineFrameImage.collage_9_8() },
        "collage_9_9" to { NineFrameImage.collage_9_9() },
        "collage_9_10" to { NineFrameImage.collage_9_10() },
        "collage_9_11" to { NineFrameImage.collage_9_11() },
        "collage_10_0" to { TenFrameImage.collage_10_0() },
        "collage_10_1" to { TenFrameImage.collage_10_1() },
        "collage_10_2" to { TenFrameImage.collage_10_2() },
        "collage_10_3" to { TenFrameImage.collage_10_3() },
        "collage_10_4" to { TenFrameImage.collage_10_4() },
        "collage_10_5" to { TenFrameImage.collage_10_5() },
        "collage_10_6" to { TenFrameImage.collage_10_6() },
        "collage_10_7" to { TenFrameImage.collage_10_7() },
        "collage_10_8" to { TenFrameImage.collage_10_8() },
        "collage_10_9" to { ExtendedFrameImage.collage_10_9() },
        "collage_10_10" to { ExtendedFrameImage.collage_10_10() },
        "collage_2_12" to { ExtendedFrameImage.collage_2_12() },
        "collage_2_13" to { ExtendedFrameImage.collage_2_13() },
        "collage_3_48" to { ExtendedFrameImage.collage_3_48() },
        "collage_4_26" to { ExtendedFrameImage.collage_4_26() },
        "collage_4_27" to { ExtendedFrameImage.collage_4_27() },
        "collage_5_32" to { ExtendedFrameImage.collage_5_32() },
        "collage_5_33" to { ExtendedFrameImage.collage_5_33() },
        "collage_6_15" to { ExtendedFrameImage.collage_6_15() },
        "collage_6_16" to { ExtendedFrameImage.collage_6_16() },
        "collage_6_17" to { ExtendedFrameImage.collage_6_17() },
        "collage_7_11" to { ExtendedFrameImage.collage_7_11() },
        "collage_7_12" to { ExtendedFrameImage.collage_7_12() },
        "collage_8_17" to { ExtendedFrameImage.collage_8_17() },
        "collage_8_18" to { ExtendedFrameImage.collage_8_18() },
        "collage_9_12" to { ExtendedFrameImage.collage_9_12() },
        "collage_9_13" to { ExtendedFrameImage.collage_9_13() },
        "collage_11_0" to { ExtendedFrameImage.collage_11_0() },
        "collage_11_1" to { ExtendedFrameImage.collage_11_1() },
        "collage_11_2" to { ExtendedFrameImage.collage_11_2() },
        "collage_11_3" to { ExtendedFrameImage.collage_11_3() },
        "collage_11_4" to { ExtendedFrameImage.collage_11_4() },
        "collage_11_5" to { ExtendedFrameImage.collage_11_5() },
        "collage_11_6" to { ExtendedFrameImage.collage_11_6() },
        "collage_11_7" to { ExtendedFrameImage.collage_11_7() },
        "collage_11_8" to { ExtendedFrameImage.collage_11_8() },
        "collage_11_9" to { ExtendedFrameImage.collage_11_9() },
        "collage_11_10" to { ExtendedFrameImage.collage_11_10() },
        "collage_12_0" to { ExtendedFrameImage.collage_12_0() },
        "collage_12_1" to { ExtendedFrameImage.collage_12_1() },
        "collage_12_2" to { ExtendedFrameImage.collage_12_2() },
        "collage_12_3" to { ExtendedFrameImage.collage_12_3() },
        "collage_12_4" to { ExtendedFrameImage.collage_12_4() },
        "collage_12_5" to { ExtendedFrameImage.collage_12_5() },
        "collage_12_6" to { ExtendedFrameImage.collage_12_6() },
        "collage_12_7" to { ExtendedFrameImage.collage_12_7() },
        "collage_12_8" to { ExtendedFrameImage.collage_12_8() },
        "collage_12_9" to { ExtendedFrameImage.collage_12_9() },
        "collage_12_10" to { ExtendedFrameImage.collage_12_10() },
        "collage_13_0" to { ExtendedFrameImage.collage_13_0() },
        "collage_13_1" to { ExtendedFrameImage.collage_13_1() },
        "collage_13_2" to { ExtendedFrameImage.collage_13_2() },
        "collage_13_3" to { ExtendedFrameImage.collage_13_3() },
        "collage_13_4" to { ExtendedFrameImage.collage_13_4() },
        "collage_13_5" to { ExtendedFrameImage.collage_13_5() },
        "collage_13_6" to { ExtendedFrameImage.collage_13_6() },
        "collage_13_7" to { ExtendedFrameImage.collage_13_7() },
        "collage_13_8" to { ExtendedFrameImage.collage_13_8() },
        "collage_13_9" to { ExtendedFrameImage.collage_13_9() },
        "collage_13_10" to { ExtendedFrameImage.collage_13_10() },
        "collage_14_0" to { ExtendedFrameImage.collage_14_0() },
        "collage_14_1" to { ExtendedFrameImage.collage_14_1() },
        "collage_14_2" to { ExtendedFrameImage.collage_14_2() },
        "collage_14_3" to { ExtendedFrameImage.collage_14_3() },
        "collage_14_4" to { ExtendedFrameImage.collage_14_4() },
        "collage_14_5" to { ExtendedFrameImage.collage_14_5() },
        "collage_14_6" to { ExtendedFrameImage.collage_14_6() },
        "collage_14_7" to { ExtendedFrameImage.collage_14_7() },
        "collage_14_8" to { ExtendedFrameImage.collage_14_8() },
        "collage_14_9" to { ExtendedFrameImage.collage_14_9() },
        "collage_14_10" to { ExtendedFrameImage.collage_14_10() },
        "collage_15_0" to { ExtendedFrameImage.collage_15_0() },
        "collage_15_1" to { ExtendedFrameImage.collage_15_1() },
        "collage_15_2" to { ExtendedFrameImage.collage_15_2() },
        "collage_15_3" to { ExtendedFrameImage.collage_15_3() },
        "collage_15_4" to { ExtendedFrameImage.collage_15_4() },
        "collage_15_5" to { ExtendedFrameImage.collage_15_5() },
        "collage_15_6" to { ExtendedFrameImage.collage_15_6() },
        "collage_15_7" to { ExtendedFrameImage.collage_15_7() },
        "collage_15_8" to { ExtendedFrameImage.collage_15_8() },
        "collage_15_9" to { ExtendedFrameImage.collage_15_9() },
        "collage_15_10" to { ExtendedFrameImage.collage_15_10() },
        "collage_16_0" to { ExtendedFrameImage.collage_16_0() },
        "collage_16_1" to { ExtendedFrameImage.collage_16_1() },
        "collage_16_2" to { ExtendedFrameImage.collage_16_2() },
        "collage_16_3" to { ExtendedFrameImage.collage_16_3() },
        "collage_16_4" to { ExtendedFrameImage.collage_16_4() },
        "collage_16_5" to { ExtendedFrameImage.collage_16_5() },
        "collage_16_6" to { ExtendedFrameImage.collage_16_6() },
        "collage_16_7" to { ExtendedFrameImage.collage_16_7() },
        "collage_16_8" to { ExtendedFrameImage.collage_16_8() },
        "collage_16_9" to { ExtendedFrameImage.collage_16_9() },
        "collage_16_10" to { ExtendedFrameImage.collage_16_10() },
        "collage_17_0" to { ExtendedFrameImage.collage_17_0() },
        "collage_17_1" to { ExtendedFrameImage.collage_17_1() },
        "collage_17_2" to { ExtendedFrameImage.collage_17_2() },
        "collage_17_3" to { ExtendedFrameImage.collage_17_3() },
        "collage_17_4" to { ExtendedFrameImage.collage_17_4() },
        "collage_17_5" to { ExtendedFrameImage.collage_17_5() },
        "collage_17_6" to { ExtendedFrameImage.collage_17_6() },
        "collage_17_7" to { ExtendedFrameImage.collage_17_7() },
        "collage_17_8" to { ExtendedFrameImage.collage_17_8() },
        "collage_17_9" to { ExtendedFrameImage.collage_17_9() },
        "collage_17_10" to { ExtendedFrameImage.collage_17_10() },
        "collage_18_0" to { ExtendedFrameImage.collage_18_0() },
        "collage_18_1" to { ExtendedFrameImage.collage_18_1() },
        "collage_18_2" to { ExtendedFrameImage.collage_18_2() },
        "collage_18_3" to { ExtendedFrameImage.collage_18_3() },
        "collage_18_4" to { ExtendedFrameImage.collage_18_4() },
        "collage_18_5" to { ExtendedFrameImage.collage_18_5() },
        "collage_18_6" to { ExtendedFrameImage.collage_18_6() },
        "collage_18_7" to { ExtendedFrameImage.collage_18_7() },
        "collage_18_8" to { ExtendedFrameImage.collage_18_8() },
        "collage_18_9" to { ExtendedFrameImage.collage_18_9() },
        "collage_18_10" to { ExtendedFrameImage.collage_18_10() },
        "collage_19_0" to { ExtendedFrameImage.collage_19_0() },
        "collage_19_1" to { ExtendedFrameImage.collage_19_1() },
        "collage_19_2" to { ExtendedFrameImage.collage_19_2() },
        "collage_19_3" to { ExtendedFrameImage.collage_19_3() },
        "collage_19_4" to { ExtendedFrameImage.collage_19_4() },
        "collage_19_5" to { ExtendedFrameImage.collage_19_5() },
        "collage_19_6" to { ExtendedFrameImage.collage_19_6() },
        "collage_19_7" to { ExtendedFrameImage.collage_19_7() },
        "collage_19_8" to { ExtendedFrameImage.collage_19_8() },
        "collage_19_9" to { ExtendedFrameImage.collage_19_9() },
        "collage_19_10" to { ExtendedFrameImage.collage_19_10() },
        "collage_20_0" to { ExtendedFrameImage.collage_20_0() },
        "collage_20_1" to { ExtendedFrameImage.collage_20_1() },
        "collage_20_2" to { ExtendedFrameImage.collage_20_2() },
        "collage_20_3" to { ExtendedFrameImage.collage_20_3() },
        "collage_20_4" to { ExtendedFrameImage.collage_20_4() },
        "collage_20_5" to { ExtendedFrameImage.collage_20_5() },
        "collage_20_6" to { ExtendedFrameImage.collage_20_6() },
        "collage_20_7" to { ExtendedFrameImage.collage_20_7() },
        "collage_20_8" to { ExtendedFrameImage.collage_20_8() },
        "collage_20_9" to { ExtendedFrameImage.collage_20_9() },
        "collage_20_10" to { ExtendedFrameImage.collage_20_10() },
    )

}
