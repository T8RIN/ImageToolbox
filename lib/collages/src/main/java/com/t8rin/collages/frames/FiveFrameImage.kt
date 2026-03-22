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

import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import com.t8rin.collages.model.CollageLayout
import com.t8rin.collages.utils.CollageLayoutFactory
import com.t8rin.collages.view.PhotoItem

/**
 * Created by admin on 6/24/2016.
 */
internal object FiveFrameImage {
    internal fun collage_5_31(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_5_31")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.3333f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0.3333f))
        photoItem.pointList.add(PointF(1f, 0.6666f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 1f, 0.3333f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.6666f, 1f))
        photoItem.pointList.add(PointF(0.3333f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.6666f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0.3333f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 0.6666f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //fourth frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.6666f, 1f, 1f)
        photoItem.pointList.add(PointF(0.3333f, 0f))
        photoItem.pointList.add(PointF(0.6666f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //fifth frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.bound.set(0.3333f, 0.3333f, 0.6666f, 0.6666f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_5_30(): CollageLayout {
        return CollageLayoutFactory.collage("collage_5_30") {
            val wallTopY = param(0.3333f)
            val wallBottomY = param(0.6666f)
            val wallLeftX = param(0.3333f)
            val wallRightX = param(0.6666f)

            addBoxedItem(
                yParams = listOf(wallTopY),
                boxParams = { vs -> RectF(0f, 0f, 1f, vs[wallTopY]) }
            )
            addBoxedItem(
                xParams = listOf(wallLeftX),
                yParams = listOf(wallTopY, wallBottomY),
                boxParams = { vs -> RectF(0f, vs[wallTopY], vs[wallLeftX], vs[wallBottomY]) }
            )
            addBoxedItem(
                xParams = listOf(wallLeftX, wallRightX),
                yParams = listOf(wallTopY, wallBottomY),
                boxParams = { vs ->
                    RectF(
                        vs[wallLeftX],
                        vs[wallTopY],
                        vs[wallRightX],
                        vs[wallBottomY]
                    )
                }
            )
            addBoxedItem(
                xParams = listOf(wallRightX),
                yParams = listOf(wallTopY, wallBottomY),
                boxParams = { vs -> RectF(vs[wallRightX], vs[wallTopY], 1f, vs[wallBottomY]) }
            )
            addBoxedItem(
                yParams = listOf(wallBottomY),
                boxParams = { vs -> RectF(0f, vs[wallBottomY], 1f, 1f) }
            )
        }
    }

    internal fun collage_5_29(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_5_29")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.4444f, 0.3333f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.75f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.3333f, 0f, 1f, 0.3333f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0.1666f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.bound.set(0f, 0.3333f, 1f, 0.6666f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem)
        //fourth frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.6666f, 0.6667f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.8333f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //fifth frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5556f, 0.6666f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0.25f, 1f))
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_5_28(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_5_28")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 1f, 0.4f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.6f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.6667f, 0.2f, 1f, 0.8f)
        photoItem.pointList.add(PointF(0f, 0.1111f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 0.8888f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //fourth frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.3333f, 0.2667f, 0.6667f, 0.7333f)
        photoItem.pointList.add(PointF(0f, 0.1428f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 0.8571f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //fifth frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.3333f, 0.3333f, 0.6667f)
        photoItem.pointList.add(PointF(0f, 0.2f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 0.8f))
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_5_27(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_5_27")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 1f, 0.4f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.6f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0.5f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.2f, 0.3333f, 0.8f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0.1111f))
        photoItem.pointList.add(PointF(1f, 0.8888f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //fourth frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.3333f, 0.2667f, 0.6667f, 0.7333f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0.1428f))
        photoItem.pointList.add(PointF(1f, 0.8571f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //fifth frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.6666f, 0.3333f, 1f, 0.6667f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0.2f))
        photoItem.pointList.add(PointF(1f, 0.8f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_5_26(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_5_26")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.6f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.8333f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0f, 1f, 0.5f)
        photoItem.pointList.add(PointF(0.2f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.5f, 0.3333f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.75f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //fourth frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.25f, 0.5f, 0.75f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.8333f, 1f))
        photoItem.pointList.add(PointF(0.1666f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //fifth frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.bound.set(0.6667f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(0.25f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_5_25(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_5_25")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.5f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0f, 1f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //fourth frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.5f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //fifth frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.bound.set(0.25f, 0f, 0.75f, 1f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_5_24(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_5_24")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.75f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.3333f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0f, 1f, 0.75f)
        photoItem.pointList.add(PointF(0f, 0.3333f))
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.25f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.pointList.add(PointF(0.6667f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(1f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //fourth frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.25f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0.6667f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //fifth frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.bound.set(0.25f, 0.25f, 0.75f, 0.75f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_5_23(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_5_23")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 1f, 0.3333f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.6667f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.6667f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0.3333f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.3333f, 0.3333f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //fourth frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.6667f, 0.6667f, 1f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //fifth frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.bound.set(0.3333f, 0.3333f, 0.6667f, 0.6667f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_5_22(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_5_22")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.5f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0f, 1f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //fourth frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.5f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //fifth frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.25f, 0.25f, 0.75f, 0.75f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_5_21(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_5_21")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.5f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0f, 1f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //fourth frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.5f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //fifth frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_5_20(): CollageLayout {
        return collage_5_19(
            name = "collage_5_20",
            x1 = 0.5f,
            x2 = 0.5f,
            y1 = 0.3333f,
            y2 = 0.6666f
        )
    }

    internal fun collage_5_19(
        name: String = "collage_5_19",
        x1: Float = 0.6f,
        x2: Float = 0.4f,
        y1: Float = 0.3333f,
        y2: Float = 0.6666f
    ): CollageLayout {
        return CollageLayoutFactory.collage(name) {
            val wallTopSplitX = param(x1)
            val wallBottomSplitX = param(x2)
            val wallTopY = param(y1)
            val wallBottomY = param(y2)

            addBoxedItem(
                xParams = listOf(wallTopSplitX),
                yParams = listOf(wallTopY),
                boxParams = { vs -> RectF(0f, 0f, vs[wallTopSplitX], vs[wallTopY]) }
            )
            addBoxedItem(
                xParams = listOf(wallTopSplitX),
                yParams = listOf(wallTopY),
                boxParams = { vs -> RectF(vs[wallTopSplitX], 0f, 1f, vs[wallTopY]) }
            )
            addBoxedItem(
                yParams = listOf(wallTopY, wallBottomY),
                boxParams = { vs -> RectF(0f, vs[wallTopY], 1f, vs[wallBottomY]) }
            )
            addBoxedItem(
                xParams = listOf(wallBottomSplitX),
                yParams = listOf(wallBottomY),
                boxParams = { vs -> RectF(0f, vs[wallBottomY], vs[wallBottomSplitX], 1f) }
            )
            addBoxedItem(
                xParams = listOf(wallBottomSplitX),
                yParams = listOf(wallBottomY),
                boxParams = { vs -> RectF(vs[wallBottomSplitX], vs[wallBottomY], 1f, 1f) }
            )
        }
    }

    internal fun collage_5_18(): CollageLayout {
        return CollageLayoutFactory.collage("collage_5_18") {
            val wallTopSplitX = param(0.6f)
            val wallMidSplitX = param(0.4f)
            val wallTopY = param(0.3333f)
            val wallBottomY = param(0.6666f)

            addBoxedItem(
                xParams = listOf(wallTopSplitX),
                yParams = listOf(wallTopY),
                boxParams = { vs -> RectF(0f, 0f, vs[wallTopSplitX], vs[wallTopY]) }
            )
            addBoxedItem(
                xParams = listOf(wallTopSplitX),
                yParams = listOf(wallTopY),
                boxParams = { vs -> RectF(vs[wallTopSplitX], 0f, 1f, vs[wallTopY]) }
            )
            addBoxedItem(
                xParams = listOf(wallMidSplitX),
                yParams = listOf(wallTopY, wallBottomY),
                boxParams = { vs -> RectF(0f, vs[wallTopY], vs[wallMidSplitX], vs[wallBottomY]) }
            )
            addBoxedItem(
                xParams = listOf(wallMidSplitX),
                yParams = listOf(wallTopY, wallBottomY),
                boxParams = { vs -> RectF(vs[wallMidSplitX], vs[wallTopY], 1f, vs[wallBottomY]) }
            )
            addBoxedItem(
                yParams = listOf(wallBottomY),
                boxParams = { vs -> RectF(0f, vs[wallBottomY], 1f, 1f) }
            )
        }
    }

    internal fun collage_5_17(): CollageLayout {
        return CollageLayoutFactory.collage("collage_5_17") {
            val wallMidX = param(0.5f)
            val wallTopY = param(0.3333f)
            val wallBottomY = param(0.6666f)

            addBoxedItem(
                xParams = listOf(wallMidX),
                yParams = listOf(wallTopY),
                boxParams = { vs -> RectF(0f, 0f, vs[wallMidX], vs[wallTopY]) }
            )
            addBoxedItem(
                xParams = listOf(wallMidX),
                yParams = listOf(wallTopY),
                boxParams = { vs -> RectF(vs[wallMidX], 0f, 1f, vs[wallTopY]) }
            )
            addBoxedItem(
                xParams = listOf(wallMidX),
                yParams = listOf(wallTopY, wallBottomY),
                boxParams = { vs -> RectF(0f, vs[wallTopY], vs[wallMidX], vs[wallBottomY]) }
            )
            addBoxedItem(
                xParams = listOf(wallMidX),
                yParams = listOf(wallTopY, wallBottomY),
                boxParams = { vs -> RectF(vs[wallMidX], vs[wallTopY], 1f, vs[wallBottomY]) }
            )
            addBoxedItem(
                yParams = listOf(wallBottomY),
                boxParams = { vs -> RectF(0f, vs[wallBottomY], 1f, 1f) }
            )
        }
    }

    internal fun collage_5_16(): CollageLayout {
        return CollageLayoutFactory.collage("collage_5_16") {
            val wallTopY = param(0.3333f)
            val wallBottomY = param(0.6666f)
            val wallMidX = param(0.5f)

            addBoxedItem(
                yParams = listOf(wallTopY),
                boxParams = { vs -> RectF(0f, 0f, 1f, vs[wallTopY]) }
            )
            addBoxedItem(
                xParams = listOf(wallMidX),
                yParams = listOf(wallTopY, wallBottomY),
                boxParams = { vs -> RectF(0f, vs[wallTopY], vs[wallMidX], vs[wallBottomY]) }
            )
            addBoxedItem(
                xParams = listOf(wallMidX),
                yParams = listOf(wallTopY, wallBottomY),
                boxParams = { vs -> RectF(vs[wallMidX], vs[wallTopY], 1f, vs[wallBottomY]) }
            )
            addBoxedItem(
                xParams = listOf(wallMidX),
                yParams = listOf(wallBottomY),
                boxParams = { vs -> RectF(0f, vs[wallBottomY], vs[wallMidX], 1f) }
            )
            addBoxedItem(
                xParams = listOf(wallMidX),
                yParams = listOf(wallBottomY),
                boxParams = { vs -> RectF(vs[wallMidX], vs[wallBottomY], 1f, 1f) }
            )
        }
    }

    internal fun collage_5_15(): CollageLayout {
        return CollageLayoutFactory.collage("collage_5_15") {
            val wallLeftX = param(0.6f)
            val wallY1 = param(0.25f)
            val wallY2 = param(0.5f)
            val wallY3 = param(0.75f)

            addBoxedItem(
                xParams = listOf(wallLeftX),
                boxParams = { vs -> RectF(0f, 0f, vs[wallLeftX], 1f) }
            )
            addBoxedItem(
                xParams = listOf(wallLeftX),
                yParams = listOf(wallY1),
                boxParams = { vs -> RectF(vs[wallLeftX], 0f, 1f, vs[wallY1]) }
            )
            addBoxedItem(
                xParams = listOf(wallLeftX),
                yParams = listOf(wallY1, wallY2),
                boxParams = { vs -> RectF(vs[wallLeftX], vs[wallY1], 1f, vs[wallY2]) }
            )
            addBoxedItem(
                xParams = listOf(wallLeftX),
                yParams = listOf(wallY2, wallY3),
                boxParams = { vs -> RectF(vs[wallLeftX], vs[wallY2], 1f, vs[wallY3]) }
            )
            addBoxedItem(
                xParams = listOf(wallLeftX),
                yParams = listOf(wallY3),
                boxParams = { vs -> RectF(vs[wallLeftX], vs[wallY3], 1f, 1f) }
            )
        }
    }

    internal fun collage_5_14(): CollageLayout {
        return CollageLayoutFactory.collage("collage_5_14") {
            val wallX1 = param(0.3333f)
            val wallX2 = param(0.6666f)
            val wallY = param(0.4f)

            addBoxedItem(
                xParams = listOf(wallX1),
                yParams = listOf(wallY),
                boxParams = { vs -> RectF(0f, 0f, vs[wallX1], vs[wallY]) }
            )
            addBoxedItem(
                xParams = listOf(wallX1, wallX2),
                yParams = listOf(wallY),
                boxParams = { vs -> RectF(vs[wallX1], 0f, vs[wallX2], vs[wallY]) }
            )
            addBoxedItem(
                xParams = listOf(wallX2),
                yParams = listOf(wallY),
                boxParams = { vs -> RectF(vs[wallX2], 0f, 1f, vs[wallY]) }
            )
            addBoxedItem(
                xParams = listOf(wallX2),
                yParams = listOf(wallY),
                boxParams = { vs -> RectF(0f, vs[wallY], vs[wallX2], 1f) }
            )
            addBoxedItem(
                xParams = listOf(wallX2),
                yParams = listOf(wallY),
                boxParams = { vs -> RectF(vs[wallX2], vs[wallY], 1f, 1f) }
            )
        }
    }

    internal fun collage_5_13(): CollageLayout {
        return CollageLayoutFactory.collage("collage_5_13") {
            val wallX1 = param(0.3333f)
            val wallX2 = param(0.6666f)
            val wallY = param(0.5f)

            addBoxedItem(
                xParams = listOf(wallX1),
                boxParams = { vs -> RectF(0f, 0f, vs[wallX1], 1f) }
            )
            addBoxedItem(
                xParams = listOf(wallX1, wallX2),
                yParams = listOf(wallY),
                boxParams = { vs -> RectF(vs[wallX1], 0f, vs[wallX2], vs[wallY]) }
            )
            addBoxedItem(
                xParams = listOf(wallX2),
                yParams = listOf(wallY),
                boxParams = { vs -> RectF(vs[wallX2], 0f, 1f, vs[wallY]) }
            )
            addBoxedItem(
                xParams = listOf(wallX1, wallX2),
                yParams = listOf(wallY),
                boxParams = { vs -> RectF(vs[wallX1], vs[wallY], vs[wallX2], 1f) }
            )
            addBoxedItem(
                xParams = listOf(wallX2),
                yParams = listOf(wallY),
                boxParams = { vs -> RectF(vs[wallX2], vs[wallY], 1f, 1f) }
            )
        }
    }

    internal fun collage_5_12(): CollageLayout {
        return CollageLayoutFactory.collage("collage_5_12") {
            val wallTopSplitX = param(0.4f)
            val wallMidSplitX = param(0.6f)
            val wallTopY = param(0.3333f)
            val wallBottomY = param(0.6666f)

            addBoxedItem(
                xParams = listOf(wallTopSplitX),
                yParams = listOf(wallTopY),
                boxParams = { vs -> RectF(0f, 0f, vs[wallTopSplitX], vs[wallTopY]) }
            )
            addBoxedItem(
                xParams = listOf(wallTopSplitX),
                yParams = listOf(wallTopY),
                boxParams = { vs -> RectF(vs[wallTopSplitX], 0f, 1f, vs[wallTopY]) }
            )
            addBoxedItem(
                xParams = listOf(wallMidSplitX),
                yParams = listOf(wallTopY),
                boxParams = { vs -> RectF(0f, vs[wallTopY], vs[wallMidSplitX], 1f) }
            )
            addBoxedItem(
                xParams = listOf(wallMidSplitX),
                yParams = listOf(wallTopY, wallBottomY),
                boxParams = { vs -> RectF(vs[wallMidSplitX], vs[wallTopY], 1f, vs[wallBottomY]) }
            )
            addBoxedItem(
                xParams = listOf(wallMidSplitX),
                yParams = listOf(wallBottomY),
                boxParams = { vs -> RectF(vs[wallMidSplitX], vs[wallBottomY], 1f, 1f) }
            )
        }
    }

    internal fun collage_5_11(): CollageLayout {
        return collage_5_10(name = "collage_5_11", y1 = 0.3333f)
    }

    internal fun collage_5_10(
        name: String = "collage_5_10",
        x1: Float = 0.3333f,
        x2: Float = 0.6667f,
        y1: Float = 0.5f,
        y2: Float = 0.6667f
    ): CollageLayout {
        return CollageLayoutFactory.collage(name) {
            val wallX1 = param(x1)
            val wallX2 = param(x2)
            val wallY1 = param(y1)
            val wallY2 = param(y2)

            addBoxedItem(
                xParams = listOf(wallX1),
                yParams = listOf(wallY1),
                boxParams = { vs -> RectF(0f, 0f, vs[wallX1], vs[wallY1]) }
            )
            addBoxedItem(
                xParams = listOf(wallX1),
                yParams = listOf(wallY1),
                boxParams = { vs -> RectF(0f, vs[wallY1], vs[wallX1], 1f) }
            )
            addBoxedItem(
                xParams = listOf(wallX1),
                yParams = listOf(wallY2),
                boxParams = { vs -> RectF(vs[wallX1], 0f, 1f, vs[wallY2]) }
            )
            addBoxedItem(
                xParams = listOf(wallX1, wallX2),
                yParams = listOf(wallY2),
                boxParams = { vs -> RectF(vs[wallX1], vs[wallY2], vs[wallX2], 1f) }
            )
            addBoxedItem(
                xParams = listOf(wallX2),
                yParams = listOf(wallY2),
                boxParams = { vs -> RectF(vs[wallX2], vs[wallY2], 1f, 1f) }
            )
        }
    }

    internal fun collage_5_9(): CollageLayout {
        return collage_5_8(name = "collage_5_9", x1 = 0.3333f, x2 = 0.6667f)
    }

    internal fun collage_5_8(
        name: String = "collage_5_8",
        x1: Float = 0.6667f,
        x2: Float = 0.3333f,
        y1: Float = 0.3333f,
        y2: Float = 0.6666f
    ): CollageLayout {
        return CollageLayoutFactory.collage(name) {
            val wallTopSplitX = param(x1)
            val wallBottomSplitX = param(x2)
            val wallY1 = param(y1)
            val wallY2 = param(y2)

            addBoxedItem(
                xParams = listOf(wallTopSplitX),
                yParams = listOf(wallY1),
                boxParams = { vs -> RectF(0f, 0f, vs[wallTopSplitX], vs[wallY1]) }
            )
            addBoxedItem(
                xParams = listOf(wallTopSplitX),
                yParams = listOf(wallY1),
                boxParams = { vs -> RectF(vs[wallTopSplitX], 0f, 1f, vs[wallY1]) }
            )
            addBoxedItem(
                yParams = listOf(wallY1, wallY2),
                boxParams = { vs -> RectF(0f, vs[wallY1], 1f, vs[wallY2]) }
            )
            addBoxedItem(
                xParams = listOf(wallBottomSplitX),
                yParams = listOf(wallY2),
                boxParams = { vs -> RectF(0f, vs[wallY2], vs[wallBottomSplitX], 1f) }
            )
            addBoxedItem(
                xParams = listOf(wallBottomSplitX),
                yParams = listOf(wallY2),
                boxParams = { vs -> RectF(vs[wallBottomSplitX], vs[wallY2], 1f, 1f) }
            )
        }
    }

    internal fun collage_5_6(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_5_6")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.5f, 0.5f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0f, 1f, 0.5f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //four frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.5f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //five frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.25f, 0.25f, 0.75f, 0.75f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_5_7(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_5_7")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 0.5f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.clearPath = Path()
        photoItem.clearPath!!.addCircle(256f, 256f, 256f, Path.Direction.CCW)
        photoItem.clearPathRatioBound = RectF(0.5f, 0.5f, 1.5f, 1.5f)
        photoItem.centerInClearBound = true
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0.5f, 0f, 1f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.clearPath = Path()
        photoItem.clearPath!!.addCircle(256f, 256f, 256f, Path.Direction.CCW)
        photoItem.clearPathRatioBound = RectF(-0.5f, 0.5f, 0.5f, 1.5f)
        photoItem.centerInClearBound = true
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.bound.set(0.5f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.clearPath = Path()
        photoItem.clearPath!!.addCircle(256f, 256f, 256f, Path.Direction.CCW)
        photoItem.clearPathRatioBound = RectF(-0.5f, -0.5f, 0.5f, 0.5f)
        photoItem.centerInClearBound = true
        photoItemList.add(photoItem)
        //four frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.bound.set(0f, 0.5f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.clearPath = Path()
        photoItem.clearPath!!.addCircle(256f, 256f, 256f, Path.Direction.CCW)
        photoItem.clearPathRatioBound = RectF(0.5f, -0.5f, 1.5f, 0.5f)
        photoItem.centerInClearBound = true
        photoItemList.add(photoItem)
        //five frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.bound.set(0.25f, 0.25f, 0.75f, 0.75f)
        photoItem.path = Path()
        photoItem.path!!.addCircle(256f, 256f, 256f, Path.Direction.CCW)
        photoItem.pathRatioBound = RectF(0f, 0f, 1f, 1f)
        photoItem.pathInCenterHorizontal = true
        photoItem.pathInCenterVertical = true
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_5_5(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_5_5")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 0.5f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.clearPath = CollageLayoutFactory.createHeartItem(0f, 512f)
        photoItem.clearPathRatioBound = RectF(0.5f, 0.5f, 1.5f, 1.5f)
        photoItem.centerInClearBound = true
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0.5f, 0f, 1f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.clearPath = CollageLayoutFactory.createHeartItem(0f, 512f)
        photoItem.clearPathRatioBound = RectF(-0.5f, 0.5f, 0.5f, 1.5f)
        photoItem.centerInClearBound = true
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.bound.set(0.5f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.clearPath = CollageLayoutFactory.createHeartItem(0f, 512f)
        photoItem.clearPathRatioBound = RectF(-0.5f, -0.5f, 0.5f, 0.5f)
        photoItem.centerInClearBound = true
        photoItemList.add(photoItem)
        //four frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.bound.set(0f, 0.5f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.clearPath = CollageLayoutFactory.createHeartItem(0f, 512f)
        photoItem.clearPathRatioBound = RectF(0.5f, -0.5f, 1.5f, 0.5f)
        photoItem.centerInClearBound = true
        photoItemList.add(photoItem)
        //five frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.bound.set(0.25f, 0.25f, 0.75f, 0.75f)
        photoItem.path = CollageLayoutFactory.createHeartItem(0f, 512f)
        photoItem.pathRatioBound = RectF(0f, 0f, 1f, 1f)
        photoItem.pathInCenterHorizontal = true
        photoItem.pathInCenterVertical = true
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_5_4(): CollageLayout {
        return CollageLayoutFactory.collage("collage_5_4") {
            val wallX1 = param(0.3333f)
            val wallX2 = param(0.6666f)
            val wallX3 = param(0.5f)
            val wallY = param(0.5f)

            addBoxedItem(
                xParams = listOf(wallX1),
                yParams = listOf(wallY),
                boxParams = { vs -> RectF(0f, 0f, vs[wallX1], vs[wallY]) }
            )
            addBoxedItem(
                xParams = listOf(wallX1, wallX2),
                yParams = listOf(wallY),
                boxParams = { vs -> RectF(vs[wallX1], 0f, vs[wallX2], vs[wallY]) }
            )
            addBoxedItem(
                xParams = listOf(wallX2),
                yParams = listOf(wallY),
                boxParams = { vs -> RectF(vs[wallX2], 0f, 1f, vs[wallY]) }
            )
            addBoxedItem(
                xParams = listOf(wallX3),
                yParams = listOf(wallY),
                boxParams = { vs -> RectF(0f, vs[wallY], vs[wallX3], 1f) }
            )
            addBoxedItem(
                xParams = listOf(wallX3),
                yParams = listOf(wallY),
                boxParams = { vs -> RectF(vs[wallX3], vs[wallY], 1f, 1f) }
            )
        }
    }

    internal fun collage_5_3(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_5_3")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.5f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0.5f, 0.5f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[5]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0f, 1f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0.5f, 0.5f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[5]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        photoItem.pointList.add(PointF(0.5f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[5]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //four frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.5f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(0.5f, 0.5f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[5]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //five frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.bound.set(0.25f, 0.25f, 0.75f, 0.75f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_5_2(): CollageLayout {
        return CollageLayoutFactory.collage("collage_5_2") {
            val wallMidX = param(0.5f)
            val wallLeftY = param(0.5f)
            val wallRightY1 = param(0.3333f)
            val wallRightY2 = param(0.6666f)

            addBoxedItem(
                xParams = listOf(wallMidX),
                yParams = listOf(wallLeftY),
                boxParams = { vs -> RectF(0f, 0f, vs[wallMidX], vs[wallLeftY]) }
            )
            addBoxedItem(
                xParams = listOf(wallMidX),
                yParams = listOf(wallLeftY),
                boxParams = { vs -> RectF(0f, vs[wallLeftY], vs[wallMidX], 1f) }
            )
            addBoxedItem(
                xParams = listOf(wallMidX),
                yParams = listOf(wallRightY1),
                boxParams = { vs -> RectF(vs[wallMidX], 0f, 1f, vs[wallRightY1]) }
            )
            addBoxedItem(
                xParams = listOf(wallMidX),
                yParams = listOf(wallRightY1, wallRightY2),
                boxParams = { vs -> RectF(vs[wallMidX], vs[wallRightY1], 1f, vs[wallRightY2]) }
            )
            addBoxedItem(
                xParams = listOf(wallMidX),
                yParams = listOf(wallRightY2),
                boxParams = { vs -> RectF(vs[wallMidX], vs[wallRightY2], 1f, 1f) }
            )
        }
    }

    internal fun collage_5_1(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_5_1")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.5f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0f, 1f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //four frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.5f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //five frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.disableShrink = true
        photoItem.bound.set(0.25f, 0.25f, 0.75f, 0.75f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(0.625f, 0.375f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0.625f, 0.625f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0.375f, 0.625f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        photoItem.pointList.add(PointF(0.375f, 0.375f))
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_5_0(): CollageLayout {
        return CollageLayoutFactory.collage("collage_5_0") {
            val wall1X = param(0.25f)
            val wall2X = param(0.75f)
            val wall3Y = param(0.25f)
            val wall4Y = param(0.75f)
            addBoxedItem(
                xParams = listOf(wall1X),
                yParams = listOf(wall4Y),
                boxParams = { vs ->
                    RectF(0f, 0f, vs[wall1X], vs[wall4Y])
                }
            )
            addBoxedItem(
                xParams = listOf(wall1X),
                yParams = listOf(wall3Y),
                boxParams = { vs ->
                    RectF(vs[wall1X], 0f, 1f, vs[wall3Y])
                }
            )
            addBoxedItem(
                xParams = listOf(wall1X, wall2X),
                yParams = listOf(wall3Y, wall4Y),
                boxParams = { vs ->
                    RectF(vs[wall1X], vs[wall3Y], vs[wall2X], vs[wall4Y])
                }
            )
            addBoxedItem(
                xParams = listOf(wall2X),
                yParams = listOf(wall3Y),
                boxParams = { vs ->
                    RectF(vs[wall2X], vs[wall3Y], 1f, 1f)
                }
            )
            addBoxedItem(
                xParams = listOf(wall2X),
                yParams = listOf(wall4Y),
                boxParams = { vs ->
                    RectF(0f, vs[wall4Y], vs[wall2X], 1f)
                }
            )
        }
    }

    internal fun collage_5_1_1(): CollageLayout {
        return CollageLayoutFactory.collageLinear(
            name = "collage_5_1_1",
            count = 5,
            isHorizontal = true
        )
    }

    internal fun collage_5_1_2(): CollageLayout {
        return CollageLayoutFactory.collageLinear(
            name = "collage_5_1_2",
            count = 5,
            isHorizontal = false
        )
    }
}