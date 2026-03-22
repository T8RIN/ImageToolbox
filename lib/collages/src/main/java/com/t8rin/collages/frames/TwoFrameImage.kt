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

package com.t8rin.collages.frames

import android.graphics.PointF
import android.graphics.RectF
import com.t8rin.collages.model.CollageLayout
import com.t8rin.collages.utils.CollageLayoutFactory
import com.t8rin.collages.view.PhotoItem

/**
 * Created by admin on 5/8/2016.
 */
internal object TwoFrameImage {
    fun collage_2_0(name: String = "collage_2_0", initialPosition: Float = 0.5f): CollageLayout {
        return CollageLayoutFactory.collage(name) {
            val wall1X = param(initialPosition)
            addBoxedItem(
                xParams = listOf(wall1X),
                boxParams = { vs ->
                    RectF(0f, 0f, vs[wall1X], 1f)
                }
            )
            addBoxedItem(
                xParams = listOf(wall1X),
                boxParams = { vs ->
                    RectF(vs[wall1X], 0f, 1f, 1f)
                }
            )
        }
    }

    fun collage_2_1(name: String = "collage_2_1", initialPosition: Float = 0.5f): CollageLayout {
        return CollageLayoutFactory.collage(name) {
            val wall1Y = param(initialPosition)
            addBoxedItem(
                yParams = listOf(wall1Y),
                boxParams = { vs ->
                    RectF(0f, 0f, 1f, vs[wall1Y])
                }
            )
            addBoxedItem(
                yParams = listOf(wall1Y),
                boxParams = { vs ->
                    RectF(0f, vs[wall1Y], 1f, 1f)
                }
            )
        }
    }

    fun collage_2_2(): CollageLayout {
        return collage_2_1("collage_2_2", 0.3333f)
    }

    fun collage_2_3(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_2_3")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        val photoItem1 = PhotoItem()
        photoItem1.index = 0
        photoItem1.bound.set(0f, 0f, 1f, 0.667f)
        photoItem1.pointList.add(PointF(0f, 0f))
        photoItem1.pointList.add(PointF(1f, 0f))
        photoItem1.pointList.add(PointF(1f, 0.5f))
        photoItem1.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem1)
        //second frame
        val photoItem2 = PhotoItem()
        photoItem2.index = 1
        photoItem2.bound.set(0f, 0.333f, 1f, 1f)
        photoItem2.pointList.add(PointF(0f, 0.5f))
        photoItem2.pointList.add(PointF(1f, 0f))
        photoItem2.pointList.add(PointF(1f, 1f))
        photoItem2.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem2)
        return item.copy(photoItemList = photoItemList)
    }

    fun collage_2_4(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_2_4")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 1f, 0.5714f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0.8333f, 0.75f))
        photoItem.pointList.add(PointF(0.6666f, 1f))
        photoItem.pointList.add(PointF(0.5f, 0.75f))
        photoItem.pointList.add(PointF(0.3333f, 1f))
        photoItem.pointList.add(PointF(0.1666f, 0.75f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0f, 0.4286f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0.25f))
        photoItem.pointList.add(PointF(0.1666f, 0f))
        photoItem.pointList.add(PointF(0.3333f, 0.25f))
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(0.6666f, 0.25f))
        photoItem.pointList.add(PointF(0.8333f, 0f))
        photoItem.pointList.add(PointF(1f, 0.25f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    fun collage_2_5(): CollageLayout {
        return collage_2_1("collage_2_5", 0.6667f)
    }

    fun collage_2_6(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_2_6")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        val photoItem1 = PhotoItem()
        photoItem1.index = 0
        photoItem1.bound.set(0f, 0f, 1f, 0.667f)
        photoItem1.pointList.add(PointF(0f, 0f))
        photoItem1.pointList.add(PointF(1f, 0f))
        photoItem1.pointList.add(PointF(1f, 1f))
        photoItem1.pointList.add(PointF(0f, 0.5f))
        photoItemList.add(photoItem1)
        //second frame
        val photoItem2 = PhotoItem()
        photoItem2.index = 1
        photoItem2.bound.set(0f, 0.333f, 1f, 1f)
        photoItem2.pointList.add(PointF(0f, 0f))
        photoItem2.pointList.add(PointF(1f, 0.5f))
        photoItem2.pointList.add(PointF(1f, 1f))
        photoItem2.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem2)
        return item.copy(photoItemList = photoItemList)
    }

    fun collage_2_7(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_2_7")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        val photoItem1 = PhotoItem()
        photoItem1.index = 0
        photoItem1.bound.set(0f, 0f, 1f, 1f)
        photoItem1.pointList.add(PointF(0f, 0f))
        photoItem1.pointList.add(PointF(1f, 0f))
        photoItem1.pointList.add(PointF(1f, 1f))
        photoItem1.pointList.add(PointF(0f, 1f))
        //clear area
        photoItem1.clearAreaPoints = ArrayList()
        photoItem1.clearAreaPoints!!.add(PointF(0.6f, 0.6f))
        photoItem1.clearAreaPoints!!.add(PointF(0.9f, 0.6f))
        photoItem1.clearAreaPoints!!.add(PointF(0.9f, 0.9f))
        photoItem1.clearAreaPoints!!.add(PointF(0.6f, 0.9f))
        photoItemList.add(photoItem1)
        //second frame
        val photoItem2 = PhotoItem()
        photoItem2.index = 1
        //photoItem2.hasBackground = true;
        photoItem2.bound.set(0.6f, 0.6f, 0.9f, 0.9f)
        photoItem2.pointList.add(PointF(0f, 0f))
        photoItem2.pointList.add(PointF(1f, 0f))
        photoItem2.pointList.add(PointF(1f, 1f))
        photoItem2.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem2)
        return item.copy(photoItemList = photoItemList)
    }

    fun collage_2_8(): CollageLayout {
        return collage_2_0("collage_2_8", 0.3333f)
    }

    fun collage_2_9(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_2_9")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        val photoItem1 = PhotoItem()
        photoItem1.index = 0
        photoItem1.bound.set(0f, 0f, 0.6667f, 1f)
        photoItem1.pointList.add(PointF(0f, 0f))
        photoItem1.pointList.add(PointF(0.5f, 0f))
        photoItem1.pointList.add(PointF(1f, 1f))
        photoItem1.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem1)
        //second frame
        val photoItem2 = PhotoItem()
        photoItem2.index = 1
        photoItem2.bound.set(0.3333f, 0f, 1f, 1f)
        photoItem2.pointList.add(PointF(0f, 0f))
        photoItem2.pointList.add(PointF(1f, 0f))
        photoItem2.pointList.add(PointF(1f, 1f))
        photoItem2.pointList.add(PointF(0.5f, 1f))
        photoItemList.add(photoItem2)
        return item.copy(photoItemList = photoItemList)
    }

    fun collage_2_10(): CollageLayout {
        return collage_2_0("collage_2_10", 0.6667f)
    }

    fun collage_2_11(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_2_11")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        val photoItem1 = PhotoItem()
        photoItem1.index = 0
        photoItem1.bound.set(0f, 0f, 0.667f, 1f)
        photoItem1.pointList.add(PointF(0f, 0f))
        photoItem1.pointList.add(PointF(1f, 0f))
        photoItem1.pointList.add(PointF(0.5f, 1f))
        photoItem1.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem1)
        //second frame
        val photoItem2 = PhotoItem()
        photoItem1.index = 1
        photoItem2.bound.set(0.333f, 0f, 1f, 1f)
        photoItem2.pointList.add(PointF(0.5f, 0f))
        photoItem2.pointList.add(PointF(1f, 0f))
        photoItem2.pointList.add(PointF(1f, 1f))
        photoItem2.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem2)
        return item.copy(photoItemList = photoItemList)
    }
}
