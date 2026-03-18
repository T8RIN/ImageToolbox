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
import android.graphics.PointF
import androidx.core.net.toUri
import com.t8rin.collages.model.TemplateItem
import com.t8rin.collages.view.PhotoItem
import java.io.IOException

/**
 * Created by admin on 5/6/2016.
 */
internal object FrameImageUtils {
    private const val FRAME_FOLDER = "frame"

    internal fun collage(
        frameName: String
    ): TemplateItem = TemplateItem(
        preview = ("file:///android_asset/$FRAME_FOLDER/$frameName").toUri(),
        title = frameName
    )

    private fun collage_1_0(): TemplateItem {
        val item = collage("collage_1_0.png")
        val photoItem = PhotoItem()
        photoItem.bound.set(0f, 0f, 1f, 1f)
        photoItem.index = 0
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        return item.copy(photoItemList = listOf(photoItem))
    }

    fun buildParamsCollage(
        imageName: String,
        setup: ParamsManagerBuilder.() -> Unit
    ): TemplateItem {
        val item = collage(imageName)
        val builder = ParamsManagerBuilder()
        builder.setup()
        val (paramsManager, photoItemList) = builder.build()
        return item.copy(paramsManager = paramsManager, photoItemList = photoItemList)
    }

    fun createHeartItem(top: Float, size: Float): Path {
        val path = Path()
        path.moveTo(top, top + size / 4)
        path.quadTo(top, top, top + size / 4, top)
        path.quadTo(top + size / 2, top, top + size / 2, top + size / 4)
        path.quadTo(top + size / 2, top, top + size * 3 / 4, top)
        path.quadTo(top + size, top, top + size, top + size / 4)
        path.quadTo(top + size, top + size / 2, top + size * 3 / 4, top + size * 3 / 4)
        path.lineTo(top + size / 2, top + size)
        path.lineTo(top + size / 4, top + size * 3 / 4)
        path.quadTo(top, top + size / 2, top, top + size / 4)
        return path
    }

    fun loadFrameImages(context: Context): ArrayList<TemplateItem> {
        val templateItemList = ArrayList<TemplateItem>()
        val am = context.assets
        try {
            val frameNames = am.list(FRAME_FOLDER)
            templateItemList.clear()
            if (!frameNames.isNullOrEmpty()) {
                for (str in frameNames) {
                    val item = createTemplateItems(str)
                    if (item != null)
                        templateItemList.add(item)
                }

                templateItemList.sortWith { lhs, rhs -> lhs.photoItemList.size - rhs.photoItemList.size }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return templateItemList
    }

    internal fun createTemplateItems(frameName: String): TemplateItem? {
        return when (frameName) {
            "collage_1_0.png" -> collage_1_0()
            "collage_2_0.png" -> TwoFrameImage.collage_2_0()
            "collage_2_1.png" -> TwoFrameImage.collage_2_1()
            "collage_2_2.png" -> TwoFrameImage.collage_2_2()
            "collage_2_3.png" -> TwoFrameImage.collage_2_3()
            "collage_2_4.png" -> TwoFrameImage.collage_2_4()
            "collage_2_5.png" -> TwoFrameImage.collage_2_5()
            "collage_2_6.png" -> TwoFrameImage.collage_2_6()
            "collage_2_7.png" -> TwoFrameImage.collage_2_7()
            "collage_2_8.png" -> TwoFrameImage.collage_2_8()
            "collage_2_9.png" -> TwoFrameImage.collage_2_9()
            "collage_2_10.png" -> TwoFrameImage.collage_2_10()
            "collage_2_11.png" -> TwoFrameImage.collage_2_11()
            "collage_3_0.png" -> ThreeFrameImage.collage_3_0()
            "collage_3_1.png" -> ThreeFrameImage.collage_3_1()
            "collage_3_3.png" -> ThreeFrameImage.collage_3_3()
            "collage_3_4.png" -> ThreeFrameImage.collage_3_4()
            "collage_3_5.png" -> ThreeFrameImage.collage_3_5()
            "collage_3_6.png" -> ThreeFrameImage.collage_3_6()
            "collage_3_7.png" -> ThreeFrameImage.collage_3_7()
            "collage_3_8.png" -> ThreeFrameImage.collage_3_8()
            "collage_3_9.png" -> ThreeFrameImage.collage_3_9()
            "collage_3_10.png" -> ThreeFrameImage.collage_3_10()
            "collage_3_11.png" -> ThreeFrameImage.collage_3_11()
            "collage_3_12.png" -> ThreeFrameImage.collage_3_12()
            "collage_3_13.png" -> ThreeFrameImage.collage_3_13()
            "collage_3_14.png" -> ThreeFrameImage.collage_3_14()
            "collage_3_15.png" -> ThreeFrameImage.collage_3_15()
            "collage_3_16.png" -> ThreeFrameImage.collage_3_16()
            "collage_3_17.png" -> ThreeFrameImage.collage_3_17()
            "collage_3_18.png" -> ThreeFrameImage.collage_3_18()
            "collage_3_19.png" -> ThreeFrameImage.collage_3_19()
            "collage_3_20.png" -> ThreeFrameImage.collage_3_20()
            "collage_3_21.png" -> ThreeFrameImage.collage_3_21()
            "collage_3_22.png" -> ThreeFrameImage.collage_3_22()
            "collage_3_23.png" -> ThreeFrameImage.collage_3_23()
            "collage_3_24.png" -> ThreeFrameImage.collage_3_24()
            "collage_3_25.png" -> ThreeFrameImage.collage_3_25()
            "collage_3_26.png" -> ThreeFrameImage.collage_3_26()
            "collage_3_27.png" -> ThreeFrameImage.collage_3_27()
            "collage_3_28.png" -> ThreeFrameImage.collage_3_28()
            "collage_3_29.png" -> ThreeFrameImage.collage_3_29()
            "collage_3_30.png" -> ThreeFrameImage.collage_3_30()
            "collage_3_31.png" -> ThreeFrameImage.collage_3_31()
            "collage_3_32.png" -> ThreeFrameImage.collage_3_32()
            "collage_3_33.png" -> ThreeFrameImage.collage_3_33()
            "collage_3_34.png" -> ThreeFrameImage.collage_3_34()
            "collage_3_35.png" -> ThreeFrameImage.collage_3_35()
            "collage_3_36.png" -> ThreeFrameImage.collage_3_36()
            "collage_3_37.png" -> ThreeFrameImage.collage_3_37()
            "collage_3_38.png" -> ThreeFrameImage.collage_3_38()
            "collage_3_39.png" -> ThreeFrameImage.collage_3_39()
            "collage_3_40.png" -> ThreeFrameImage.collage_3_40()
            "collage_3_41.png" -> ThreeFrameImage.collage_3_41()
            "collage_3_42.png" -> ThreeFrameImage.collage_3_42()
            "collage_3_43.png" -> ThreeFrameImage.collage_3_43()
            "collage_3_44.png" -> ThreeFrameImage.collage_3_44()
            "collage_3_45.png" -> ThreeFrameImage.collage_3_45()
            "collage_3_46.png" -> ThreeFrameImage.collage_3_46()
            "collage_3_47.png" -> ThreeFrameImage.collage_3_47()
            "collage_4_0.png" -> FourFrameImage.collage_4_0()
            "collage_4_1.png" -> FourFrameImage.collage_4_1()
            "collage_4_2.png" -> FourFrameImage.collage_4_2()
            "collage_4_4.png" -> FourFrameImage.collage_4_4()
            "collage_4_5.png" -> FourFrameImage.collage_4_5()
            "collage_4_6.png" -> FourFrameImage.collage_4_6()
            "collage_4_7.png" -> FourFrameImage.collage_4_7()
            "collage_4_8.png" -> FourFrameImage.collage_4_8()
            "collage_4_9.png" -> FourFrameImage.collage_4_9()
            "collage_4_10.png" -> FourFrameImage.collage_4_10()
            "collage_4_11.png" -> FourFrameImage.collage_4_11()
            "collage_4_12.png" -> FourFrameImage.collage_4_12()
            "collage_4_13.png" -> FourFrameImage.collage_4_13()
            "collage_4_14.png" -> FourFrameImage.collage_4_14()
            "collage_4_15.png" -> FourFrameImage.collage_4_15()
            "collage_4_16.png" -> FourFrameImage.collage_4_16()
            "collage_4_17.png" -> FourFrameImage.collage_4_17()
            "collage_4_18.png" -> FourFrameImage.collage_4_18()
            "collage_4_19.png" -> FourFrameImage.collage_4_19()
            "collage_4_20.png" -> FourFrameImage.collage_4_20()
            "collage_4_21.png" -> FourFrameImage.collage_4_21()
            "collage_4_22.png" -> FourFrameImage.collage_4_22()
            "collage_4_23.png" -> FourFrameImage.collage_4_23()
            "collage_4_24.png" -> FourFrameImage.collage_4_24()
            "collage_4_25.png" -> FourFrameImage.collage_4_25()
            "collage_5_0.png" -> FiveFrameImage.collage_5_0()
            "collage_5_1.png" -> FiveFrameImage.collage_5_1()
            "collage_5_2.png" -> FiveFrameImage.collage_5_2()
            "collage_5_3.png" -> FiveFrameImage.collage_5_3()
            "collage_5_4.png" -> FiveFrameImage.collage_5_4()
            "collage_5_5.png" -> FiveFrameImage.collage_5_5()
            "collage_5_6.png" -> FiveFrameImage.collage_5_6()
            "collage_5_7.png" -> FiveFrameImage.collage_5_7()
            "collage_5_8.png" -> FiveFrameImage.collage_5_8()
            "collage_5_9.png" -> FiveFrameImage.collage_5_9()
            "collage_5_10.png" -> FiveFrameImage.collage_5_10()
            "collage_5_11.png" -> FiveFrameImage.collage_5_11()
            "collage_5_12.png" -> FiveFrameImage.collage_5_12()
            "collage_5_13.png" -> FiveFrameImage.collage_5_13()
            "collage_5_14.png" -> FiveFrameImage.collage_5_14()
            "collage_5_15.png" -> FiveFrameImage.collage_5_15()
            "collage_5_16.png" -> FiveFrameImage.collage_5_16()
            "collage_5_17.png" -> FiveFrameImage.collage_5_17()
            "collage_5_18.png" -> FiveFrameImage.collage_5_18()
            "collage_5_19.png" -> FiveFrameImage.collage_5_19()
            "collage_5_20.png" -> FiveFrameImage.collage_5_20()
            "collage_5_21.png" -> FiveFrameImage.collage_5_21()
            "collage_5_22.png" -> FiveFrameImage.collage_5_22()
            "collage_5_23.png" -> FiveFrameImage.collage_5_23()
            "collage_5_24.png" -> FiveFrameImage.collage_5_24()
            "collage_5_25.png" -> FiveFrameImage.collage_5_25()
            "collage_5_26.png" -> FiveFrameImage.collage_5_26()
            "collage_5_27.png" -> FiveFrameImage.collage_5_27()
            "collage_5_28.png" -> FiveFrameImage.collage_5_28()
            "collage_5_29.png" -> FiveFrameImage.collage_5_29()
            "collage_5_30.png" -> FiveFrameImage.collage_5_30()
            "collage_5_31.png" -> FiveFrameImage.collage_5_31()
            "collage_6_0.png" -> SixFrameImage.collage_6_0()
            "collage_6_1.png" -> SixFrameImage.collage_6_1()
            "collage_6_2.png" -> SixFrameImage.collage_6_2()
            "collage_6_3.png" -> SixFrameImage.collage_6_3()
            "collage_6_4.png" -> SixFrameImage.collage_6_4()
            "collage_6_5.png" -> SixFrameImage.collage_6_5()
            "collage_6_6.png" -> SixFrameImage.collage_6_6()
            "collage_6_7.png" -> SixFrameImage.collage_6_7()
            "collage_6_8.png" -> SixFrameImage.collage_6_8()
            "collage_6_9.png" -> SixFrameImage.collage_6_9()
            "collage_6_10.png" -> SixFrameImage.collage_6_10()
            "collage_6_11.png" -> SixFrameImage.collage_6_11()
            "collage_6_12.png" -> SixFrameImage.collage_6_12()
            "collage_6_13.png" -> SixFrameImage.collage_6_13()
            "collage_6_14.png" -> SixFrameImage.collage_6_14()
            "collage_7_0.png" -> SevenFrameImage.collage_7_0()
            "collage_7_1.png" -> SevenFrameImage.collage_7_1()
            "collage_7_2.png" -> SevenFrameImage.collage_7_2()
            "collage_7_3.png" -> SevenFrameImage.collage_7_3()
            "collage_7_4.png" -> SevenFrameImage.collage_7_4()
            "collage_7_5.png" -> SevenFrameImage.collage_7_5()
            "collage_7_6.png" -> SevenFrameImage.collage_7_6()
            "collage_7_7.png" -> SevenFrameImage.collage_7_7()
            "collage_7_8.png" -> SevenFrameImage.collage_7_8()
            "collage_7_9.png" -> SevenFrameImage.collage_7_9()
            "collage_7_10.png" -> SevenFrameImage.collage_7_10()
            "collage_8_0.png" -> EightFrameImage.collage_8_0()
            "collage_8_1.png" -> EightFrameImage.collage_8_1()
            "collage_8_2.png" -> EightFrameImage.collage_8_2()
            "collage_8_3.png" -> EightFrameImage.collage_8_3()
            "collage_8_4.png" -> EightFrameImage.collage_8_4()
            "collage_8_5.png" -> EightFrameImage.collage_8_5()
            "collage_8_6.png" -> EightFrameImage.collage_8_6()
            "collage_8_7.png" -> EightFrameImage.collage_8_7()
            "collage_8_8.png" -> EightFrameImage.collage_8_8()
            "collage_8_9.png" -> EightFrameImage.collage_8_9()
            "collage_8_10.png" -> EightFrameImage.collage_8_10()
            "collage_8_11.png" -> EightFrameImage.collage_8_11()
            "collage_8_12.png" -> EightFrameImage.collage_8_12()
            "collage_8_13.png" -> EightFrameImage.collage_8_13()
            "collage_8_14.png" -> EightFrameImage.collage_8_14()
            "collage_8_15.png" -> EightFrameImage.collage_8_15()
            "collage_8_16.png" -> EightFrameImage.collage_8_16()
            "collage_9_0.png" -> NineFrameImage.collage_9_0()
            "collage_9_1.png" -> NineFrameImage.collage_9_1()
            "collage_9_2.png" -> NineFrameImage.collage_9_2()
            "collage_9_3.png" -> NineFrameImage.collage_9_3()
            "collage_9_4.png" -> NineFrameImage.collage_9_4()
            "collage_9_5.png" -> NineFrameImage.collage_9_5()
            "collage_9_6.png" -> NineFrameImage.collage_9_6()
            "collage_9_7.png" -> NineFrameImage.collage_9_7()
            "collage_9_8.png" -> NineFrameImage.collage_9_8()
            "collage_9_9.png" -> NineFrameImage.collage_9_9()
            "collage_9_10.png" -> NineFrameImage.collage_9_10()
            "collage_9_11.png" -> NineFrameImage.collage_9_11()
            "collage_10_0.png" -> TenFrameImage.collage_10_0()
            "collage_10_1.png" -> TenFrameImage.collage_10_1()
            "collage_10_2.png" -> TenFrameImage.collage_10_2()
            "collage_10_3.png" -> TenFrameImage.collage_10_3()
            "collage_10_4.png" -> TenFrameImage.collage_10_4()
            "collage_10_5.png" -> TenFrameImage.collage_10_5()
            "collage_10_6.png" -> TenFrameImage.collage_10_6()
            "collage_10_7.png" -> TenFrameImage.collage_10_7()
            "collage_10_8.png" -> TenFrameImage.collage_10_8()
            else -> null
        }
    }
}
