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
import com.t8rin.collages.utils.GeometryUtils
import com.t8rin.collages.view.PhotoItem

/**
 * Created by admin on 5/9/2016.
 */
internal object ThreeFrameImage {
    internal fun collage_3_47(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_47")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 1f, 0.5f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.75f, 0f))
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
        photoItem.bound.set(0f, 0.5f, 1f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(0.75f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0.5f))
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_46(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_46")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.4167f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.6f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 2f)
        photoItemList.add(photoItem)

        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0.25f, 0f, 0.75f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.8333f, 1f))
        photoItem.pointList.add(PointF(0.3333f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0.6666f, 0f, 1f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0.25f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_45(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_45")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 0.4f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0.2f, 0f, 0.8f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.6667f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0.3333f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.6f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_44(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_44")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 1f, 0.4167f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 0.6f))
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
        photoItem.bound.set(0f, 0.25f, 1f, 0.75f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0.3333f))
        photoItem.pointList.add(PointF(1f, 0.8333f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.6666f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0.25f))
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
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_43(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_43")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 1f, 0.4f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
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
        photoItem.bound.set(0f, 0.2f, 1f, 0.8f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 0.6667f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.pointList.add(PointF(0f, 0.3333f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
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
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_42(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_42")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 0.6f, 0.8f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.6667f, 0f))
        photoItem.pointList.add(PointF(1f, 0.75f))
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
        photoItem.bound.set(0.4f, 0f, 1f, 0.7f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0.3333f, 0.8571f))
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
        photoItem.bound.set(0f, 0.6f, 1f, 1f)
        photoItem.pointList.add(PointF(0.6f, 0f))
        photoItem.pointList.add(PointF(1f, 0.25f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_41(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_41")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 1f, 0.6666f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
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
        photoItem.bound.set(0.5f, 0f, 1f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0.5f))
        photoItem.pointList.add(PointF(1f, 0.3333f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0.3333f, 1f))
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
        photoItem.bound.set(0f, 0f, 0.6667f, 1f)
        photoItem.pointList.add(PointF(0f, 0.6667f))
        photoItem.pointList.add(PointF(0.75f, 0.5f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_40(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_40")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 0.5f, 0.5f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
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
        photoItem.bound.set(0f, 0f, 1f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[5]] = PointF(2f, 1f)
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
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_39(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_39")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 1f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0f, 0f, 0.5f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_38(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_38")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 0.5f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0f, 0f, 1f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_37(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_37")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 0.5f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0f, 0f, 1f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_36(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_36")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 0.5f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
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
        photoItem.bound.set(0f, 0f, 1f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_35(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_35")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 1f, 0.5f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0f, 0f, 1f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_34(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_34")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 1f, 0.5f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
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
        photoItem.bound.set(0f, 0f, 1f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0.5f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_33(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_33")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0.5f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0f, 0f, 1f, 0.5f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
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
        photoItem.bound.set(0f, 0f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_32(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_32")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0.5f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0f, 0f, 1f, 0.5f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
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
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0.5f))
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
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_31(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_31")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 0.5f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0.5f, 0f, 1f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
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
        photoItem.bound.set(0f, 0.5f, 1f, 1f)
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
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_30(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_30")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0.5f, 0f, 1f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0f, 0f, 0.5f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.5f, 1f, 1f)
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
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_29(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_29")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0f, 0f, 0.5f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_28(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_28")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 1f, 1f)
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
        photoItem.bound.set(0.5f, 0f, 1f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0.5f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_27(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_27")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0f, 0f, 1f, 0.5f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0.5f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_26(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_26")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0f, 0f, 1f, 0.5f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_25(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_25")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 1f, 0.5f)
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
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0f, 0.5f, 1f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
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
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_24(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_24")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 1f, 0.5f)
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
        photoItem.bound.set(0f, 0.5f, 1f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
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
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0.5f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_23(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_23")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0.5f, 0f, 1f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0.5f))
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
        photoItem.bound.set(0f, 0f, 1f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_22(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_22")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
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
        photoItem.bound.set(0.5f, 0f, 1f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
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
        photoItem.bound.set(0f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_21(): CollageLayout {
        return collage_3_18("collage_3_21", 0.5f, 0.3333f)
    }

    internal fun collage_3_20(): CollageLayout {
        return collage_3_18("collage_3_20", 0.5f, 0.6666f)
    }

    internal fun collage_3_19(): CollageLayout {
        return collage_3_16("collage_3_19", 0.25f, 0.75f)
    }

    internal fun collage_3_18(
        name: String = "collage_3_18",
        initialX: Float = 0.5f,
        initialY: Float = 0.5f
    ): CollageLayout {
        return CollageLayoutFactory.collage(name) {
            val wall1X = param(initialX)
            val wall2Y = param(initialY)
            addBoxedItem(
                xParams = listOf(wall1X),
                yParams = listOf(wall2Y),
                boxParams = { vs ->
                    RectF(0f, 0f, vs[wall1X], vs[wall2Y])
                }
            )
            addBoxedItem(
                xParams = listOf(wall1X),
                yParams = listOf(wall2Y),
                boxParams = { vs ->
                    RectF(vs[wall1X], 0f, 1f, vs[wall2Y])
                }
            )
            addBoxedItem(
                yParams = listOf(wall2Y),
                boxParams = { vs ->
                    RectF(0f, vs[wall2Y], 1f, 1f)
                }
            )
        }
    }

    internal fun collage_3_17(): CollageLayout {
        return CollageLayoutFactory.collage("collage_3_17") {
            val wall1X = param(0.5f)
            val wall2Y = param(0.3333f)
            addBoxedItem(
                yParams = listOf(wall2Y),
                boxParams = { vs ->
                    RectF(0f, 0f, 1f, vs[wall2Y])
                }
            )
            addBoxedItem(
                xParams = listOf(wall1X),
                yParams = listOf(wall2Y),
                boxParams = { vs ->
                    RectF(0f, vs[wall2Y], vs[wall1X], 1f)
                }
            )
            addBoxedItem(
                xParams = listOf(wall1X),
                yParams = listOf(wall2Y),
                boxParams = { vs ->
                    RectF(vs[wall1X], vs[wall2Y], 1f, 1f)
                }
            )
        }
    }

    internal fun collage_3_16(
        name: String = "collage_3_16",
        initialY1: Float = 0.3333f,
        initialY2: Float = 0.6666f
    ): CollageLayout {
        return CollageLayoutFactory.collage(name) {
            val wall1Y = param(initialY1)
            val wall2Y = param(initialY2)
            addBoxedItem(
                yParams = listOf(wall1Y),
                boxParams = { vs ->
                    RectF(0f, 0f, 1f, vs[wall1Y])
                }
            )
            addBoxedItem(
                yParams = listOf(wall1Y, wall2Y),
                boxParams = { vs ->
                    RectF(0f, vs[wall1Y], 1f, vs[wall2Y])
                }
            )
            addBoxedItem(
                yParams = listOf(wall2Y),
                boxParams = { vs ->
                    RectF(0f, vs[wall2Y], 1f, 1f)
                }
            )
        }
    }

    internal fun collage_3_15(): CollageLayout {
        return collage_3_12("collage_3_15", 0.6667f, 0.5f)
    }

    internal fun collage_3_14(): CollageLayout {
        return collage_3_12("collage_3_14", 0.3333f, 0.5f)
    }

    internal fun collage_3_13(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_13")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.fitBound = true
        photoItem.clearPath = Path()
        photoItem.clearPath!!.addRect(0f, 0f, 512f, 512f, Path.Direction.CCW)
        photoItem.clearPathRatioBound = RectF(0.5f, 0.25f, 1.5f, 0.75f)
        photoItem.clearPathInCenterVertical = true
        photoItem.cornerMethod = PhotoItem.CORNER_METHOD_3_13
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0.5f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.fitBound = true
        photoItem.clearPath = Path()
        photoItem.clearPath!!.addRect(0f, 0f, 512f, 512f, Path.Direction.CCW)
        photoItem.clearPathRatioBound = RectF(-0.5f, 0.25f, 0.5f, 0.75f)
        photoItem.clearPathInCenterVertical = true
        photoItem.cornerMethod = PhotoItem.CORNER_METHOD_3_13
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.bound.set(0.25f, 0f, 0.75f, 1f)
        photoItem.path = Path()
        photoItem.path!!.addRect(0f, 0f, 512f, 512f, Path.Direction.CCW)
        photoItem.pathRatioBound = RectF(0f, 0.25f, 1f, 0.75f)
        photoItem.pathInCenterVertical = true
        photoItem.pathInCenterHorizontal = true
        photoItem.fitBound = true
        photoItem.cornerMethod = PhotoItem.CORNER_METHOD_3_13
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_12(
        name: String = "collage_3_12",
        initialX: Float = 0.5f,
        initialY: Float = 0.5f
    ): CollageLayout {
        return CollageLayoutFactory.collage(name) {
            val wall1X = param(initialX)
            val wall2Y = param(initialY)
            addBoxedItem(
                xParams = listOf(wall1X),
                boxParams = { vs ->
                    RectF(0f, 0f, vs[wall1X], 1f)
                }
            )
            addBoxedItem(
                xParams = listOf(wall1X),
                yParams = listOf(wall2Y),
                boxParams = { vs ->
                    RectF(vs[wall1X], 0f, 1f, vs[wall2Y])
                }
            )
            addBoxedItem(
                xParams = listOf(wall1X),
                yParams = listOf(wall2Y),
                boxParams = { vs ->
                    RectF(vs[wall1X], vs[wall2Y], 1f, 1f)
                }
            )
        }
    }

    internal fun collage_3_11(): CollageLayout {
        return collage_3_5("collage_3_11", 0.6667f, 0.5f)
    }

    internal fun collage_3_10(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_10")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_USING_MAP
        photoItem.bound.set(0f, 0f, 1f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0.75f, 1f))
        photoItem.pointList.add(PointF(0.75f, 0.5f))
        photoItem.pointList.add(PointF(0.25f, 0.5f))
        photoItem.pointList.add(PointF(0.25f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(-2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(-2f, -1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, -1f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(1f, -1f)
        photoItem.shrinkMap!![photoItem.pointList[5]] = PointF(-1f, -1f)
        photoItem.shrinkMap!![photoItem.pointList[6]] = PointF(-1f, -1f)
        photoItem.shrinkMap!![photoItem.pointList[7]] = PointF(2f, -1f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0f, 0.5f, 1f, 1f)
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_USING_MAP
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.25f, 0f))
        photoItem.pointList.add(PointF(0.25f, 0.5f))
        photoItem.pointList.add(PointF(0.75f, 0.5f))
        photoItem.pointList.add(PointF(0.75f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(-1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(-1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[5]] = PointF(-2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[6]] = PointF(-2f, -2f)
        photoItem.shrinkMap!![photoItem.pointList[7]] = PointF(2f, -2f)
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.bound.set(0.25f, 0.25f, 0.75f, 0.75f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_9(): CollageLayout {
        return collage_3_5("collage_3_9", 0.5f, 0.6667f)
    }

    internal fun collage_3_8(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_8")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.clearPath = Path()
        photoItem.clearPath!!.addCircle(256f, 256f, 256f, Path.Direction.CCW)
        photoItem.clearPathRatioBound = RectF(0.5f, 0.25f, 1.5f, 0.75f)
        photoItem.clearPathInCenterVertical = true
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0.5f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.clearPath = Path()
        photoItem.clearPath!!.addCircle(256f, 256f, 256f, Path.Direction.CCW)
        photoItem.clearPathRatioBound = RectF(-0.5f, 0.25f, 0.5f, 0.75f)
        photoItem.clearPathInCenterVertical = true
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_3_8
        photoItem.bound.set(0.25f, 0f, 0.75f, 1f)
        photoItem.path = Path()
        photoItem.path!!.addCircle(256f, 256f, 256f, Path.Direction.CCW)
        photoItem.pathRatioBound = RectF(0f, 0.25f, 1f, 0.75f)
        photoItem.pathInCenterVertical = true
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_7(): CollageLayout {
        return collage_3_5("collage_3_7", 0.3333f, 0.5f)
    }

    internal fun collage_3_6(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_6")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_3_6
        photoItem.cornerMethod = PhotoItem.CORNER_METHOD_3_6
        photoItem.bound.set(0f, 0f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.clearPath = Path()
        GeometryUtils.createRegularPolygonPath(photoItem.clearPath!!, 512f, 6, 0f)
        photoItem.clearPathRatioBound = RectF(0.5f, 0.25f, 1.5f, 0.75f)
        photoItem.clearPathInCenterVertical = true
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_3_6
        photoItem.cornerMethod = PhotoItem.CORNER_METHOD_3_6
        photoItem.index = 1
        photoItem.bound.set(0.5f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.clearPath = Path()
        GeometryUtils.createRegularPolygonPath(photoItem.clearPath!!, 512f, 6, 0f)
        photoItem.clearPathRatioBound = RectF(-0.5f, 0.25f, 0.5f, 0.75f)
        photoItem.clearPathInCenterVertical = true
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_3_6
        photoItem.cornerMethod = PhotoItem.CORNER_METHOD_3_6
        photoItem.bound.set(0.25f, 0f, 0.75f, 1f)
        photoItem.path = Path()
        GeometryUtils.createRegularPolygonPath(photoItem.path!!, 512f, 6, 0f)
        photoItem.pathRatioBound = RectF(0f, 0.25f, 1f, 0.75f)
        photoItem.pathInCenterVertical = true
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_5(
        name: String = "collage_3_5",
        initialX: Float = 0.5f,
        initialY: Float = 0.5f
    ): CollageLayout {
        return CollageLayoutFactory.collage(name) {
            val wall1X = param(initialX)
            val wall2Y = param(initialY)
            addBoxedItem(
                xParams = listOf(wall1X),
                yParams = listOf(wall2Y),
                boxParams = { vs ->
                    RectF(0f, 0f, vs[wall1X], vs[wall2Y])
                }
            )
            addBoxedItem(
                xParams = listOf(wall1X),
                boxParams = { vs ->
                    RectF(vs[wall1X], 0f, 1f, 1f)
                }
            )
            addBoxedItem(
                xParams = listOf(wall1X),
                yParams = listOf(wall2Y),
                boxParams = { vs ->
                    RectF(0f, vs[wall2Y], vs[wall1X], 1f)
                }
            )
        }
    }

    internal fun collage_3_4(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_4")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 1f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.clearPath = CollageLayoutFactory.createHeartItem(0f, 512f)
        photoItem.clearPathRatioBound = RectF(0.25f, 0.5f, 0.75f, 1.5f)
        photoItem.clearPathInCenterHorizontal = true
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.clearPath = CollageLayoutFactory.createHeartItem(0f, 512f)
        photoItem.clearPathRatioBound = RectF(0.25f, -0.5f, 0.75f, 0.5f)
        photoItem.clearPathInCenterHorizontal = true
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.bound.set(0f, 0.25f, 1f, 0.75f)
        photoItem.path = CollageLayoutFactory.createHeartItem(0f, 512f)
        photoItem.pathRatioBound = RectF(0f, 0f, 1f, 1f)
        photoItem.pathInCenterHorizontal = true
        photoItem.pathInCenterVertical = true
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_3(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_3")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_3_3
        photoItem.bound.set(0f, 0f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 0.25f))
        photoItem.pointList.add(PointF(0.5f, 0.5f))
        photoItem.pointList.add(PointF(1f, 0.75f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_3_3
        photoItem.bound.set(0.5f, 0f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.pointList.add(PointF(0f, 0.75f))
        photoItem.pointList.add(PointF(0.5f, 0.5f))
        photoItem.pointList.add(PointF(0f, 0.25f))
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.bound.set(0.25f, 0.25f, 0.75f, 0.75f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_1(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_3_1")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 1f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.clearPath = Path()
        photoItem.clearPath!!.addCircle(256f, 256f, 256f, Path.Direction.CCW)
        photoItem.clearPathRatioBound = RectF(0.25f, 0.5f, 0.75f, 1.5f)
        photoItem.clearPathInCenterHorizontal = true
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.clearPath = Path()
        photoItem.clearPath!!.addCircle(256f, 256f, 256f, Path.Direction.CCW)
        photoItem.clearPathRatioBound = RectF(0.25f, -0.5f, 0.75f, 0.5f)
        photoItem.clearPathInCenterHorizontal = true
        photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_3_8
        photoItem.bound.set(0f, 0.25f, 1f, 0.75f)
        photoItem.path = Path()
        photoItem.path!!.addCircle(256f, 256f, 256f, Path.Direction.CCW)
        photoItem.pathRatioBound = RectF(0.25f, 0f, 0.75f, 1f)
        photoItem.pathInCenterVertical = true
        photoItemList.add(photoItem)
        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_3_0(): CollageLayout {
        return CollageLayoutFactory.collageLinear(
            name = "collage_3_0",
            count = 3,
            isHorizontal = true
        )
    }
}