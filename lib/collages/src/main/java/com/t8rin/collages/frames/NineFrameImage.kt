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
 * All points of polygon must be ordered by clockwise along<br></br>
 * Created by admin on 7/3/2016.
 */
internal object NineFrameImage {
    internal fun collage_9_11(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_9_11")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.2666f, 0.3333f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.7519f, 0f))
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
        photoItem.bound.set(0.2f, 0f, 0.8f, 0.3333f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.8889f, 1f))
        photoItem.pointList.add(PointF(0.1111f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 8
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.7334f, 0f, 1f, 0.3333f)
        photoItem.pointList.add(PointF(0.2481f, 0f))
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
        //fourth frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.3333f, 0.3333f, 0.6666f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.8f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //fifth frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.2666f, 0.3333f, 0.7334f, 0.6666f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.8572f, 1f))
        photoItem.pointList.add(PointF(0.1428f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //sixth frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.6666f, 0.3333f, 1f, 0.6666f)
        photoItem.pointList.add(PointF(0.2f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //seventh frame
        photoItem = PhotoItem()
        photoItem.index = 5
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.6666f, 0.4f, 1f)
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
        //eighth frame
        photoItem = PhotoItem()
        photoItem.index = 6
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.3333f, 0.6666f, 0.6666f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.8f, 1f))
        photoItem.pointList.add(PointF(0.2f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //ninth frame
        photoItem = PhotoItem()
        photoItem.index = 7
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.6f, 0.6666f, 1f, 1f)
        photoItem.pointList.add(PointF(0.1666f, 0f))
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

    internal fun collage_9_10(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_9_10")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.39645f, 0.39645f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.73881f, 0f))
        photoItem.pointList.add(PointF(1f, 0.6306f))
        photoItem.pointList.add(PointF(0.6306f, 1f))
        photoItem.pointList.add(PointF(0f, 0.73881f))
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
        photoItem.bound.set(0.2929f, 0f, 0.7071f, 0.25f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.75f, 1f))
        photoItem.pointList.add(PointF(0.25f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 8
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.60355f, 0f, 1f, 0.39645f)
        photoItem.pointList.add(PointF(0.26119f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 0.73881f))
        photoItem.pointList.add(PointF(0.3694f, 1f))
        photoItem.pointList.add(PointF(0f, 0.6306f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //fourth frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.75f, 0.2929f, 1f, 0.7071f)
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 0.75f))
        photoItem.pointList.add(PointF(0f, 0.25f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //fifth frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.60355f, 0.60355f, 1f, 1f)
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0.26199f, 1f))
        photoItem.pointList.add(PointF(0f, 0.3694f))
        photoItem.pointList.add(PointF(0.3694f, 0f))
        photoItem.pointList.add(PointF(1f, 0.26199f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //sixth frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.2929f, 0.75f, 0.7071f, 1f)
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.pointList.add(PointF(0.25f, 0f))
        photoItem.pointList.add(PointF(0.75f, 0f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //seventh frame
        photoItem = PhotoItem()
        photoItem.index = 5
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.60355f, 0.39645f, 1f)
        photoItem.pointList.add(PointF(0.6306f, 0f))
        photoItem.pointList.add(PointF(1f, 0.3694f))
        photoItem.pointList.add(PointF(0.73881f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.pointList.add(PointF(0f, 0.26199f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //eighth frame
        photoItem = PhotoItem()
        photoItem.index = 6
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.2929f, 0.25f, 0.7071f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0.25f))
        photoItem.pointList.add(PointF(1f, 0.75f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //ninth frame
        photoItem = PhotoItem()
        photoItem.index = 7
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.25f, 0.25f, 0.75f, 0.75f)
        photoItem.pointList.add(PointF(0.2929f, 0f))
        photoItem.pointList.add(PointF(0.7071f, 0f))
        photoItem.pointList.add(PointF(1f, 0.2929f))
        photoItem.pointList.add(PointF(1f, 0.7071f))
        photoItem.pointList.add(PointF(0.7071f, 1f))
        photoItem.pointList.add(PointF(0.2929f, 1f))
        photoItem.pointList.add(PointF(0f, 0.7071f))
        photoItem.pointList.add(PointF(0f, 0.2929f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[5]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[6]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[7]] = PointF(1f, 1f)
        photoItemList.add(photoItem)

        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_9_9(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_9_9")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.3f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0.6f))
        photoItem.pointList.add(PointF(1f, 1f))
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
        photoItem.bound.set(0f, 0f, 0.5f, 0.3f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0.6f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 8
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0f, 1f, 0.3f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.4f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //fourth frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.7f, 0f, 1f, 0.5f)
        photoItem.pointList.add(PointF(0f, 0.6f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //fifth frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.7f, 0.5f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 0.4f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //sixth frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0.7f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.4f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //seventh frame
        photoItem = PhotoItem()
        photoItem.index = 5
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.7f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0.6f, 0f))
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
        //eighth frame
        photoItem = PhotoItem()
        photoItem.index = 6
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.5f, 0.3f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 0.4f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItemList.add(photoItem)
        //ninth frame
        photoItem = PhotoItem()
        photoItem.index = 7
        photoItem.bound.set(0.3f, 0.3f, 0.7f, 0.7f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItemList.add(photoItem)

        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_9_8(): CollageLayout {
        return CollageLayoutFactory.collage("collage_9_8") {
            val x1 = param(0.25f)
            val x2 = param(0.5f)
            val x3 = param(0.75f)
            val x4 = param(0.3333f)
            val x5 = param(0.6666f)
            val y1 = param(0.3333f)
            val y2 = param(0.6666f)

            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, 0f, vs[x1], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x1], 0f, vs[x2], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x2, x3),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x2], 0f, vs[x3], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x3),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x3], 0f, 1f, vs[y1]) }
            )

            addBoxedItem(
                xParams = listOf(x4),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(0f, vs[y1], vs[x4], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x4, x5),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[x4], vs[y1], vs[x5], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x5),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[x5], vs[y1], 1f, vs[y2]) }
            )

            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(0f, vs[y2], vs[x2], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[x2], vs[y2], 1f, 1f) }
            )
        }
    }

    internal fun collage_9_7(): CollageLayout {
        return CollageLayoutFactory.collage("collage_9_7") {
            val x1 = param(0.25f)
            val x2 = param(0.5f)
            val x3 = param(0.75f)
            val x4 = param(0.3333f)
            val x5 = param(0.6666f)
            val y1 = param(0.3333f)
            val y2 = param(0.6666f)

            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, 0f, vs[x1], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x1], 0f, vs[x2], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x2, x3),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x2], 0f, vs[x3], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x3),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x3], 0f, 1f, vs[y1]) }
            )

            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(0f, vs[y1], vs[x2], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[x2], vs[y1], 1f, vs[y2]) }
            )

            addBoxedItem(
                xParams = listOf(x4),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(0f, vs[y2], vs[x4], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x4, x5),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[x4], vs[y2], vs[x5], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x5),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[x5], vs[y2], 1f, 1f) }
            )
        }
    }

    internal fun collage_9_6(): CollageLayout {
        return CollageLayoutFactory.collage("collage_9_6") {
            val x1 = param(0.2f)
            val x2 = param(0.4f)
            val x3 = param(0.6f)
            val x4 = param(0.8f)
            val y1 = param(0.5f)

            addBoxedItem(
                xParams = listOf(x1),
                boxParams = { vs -> RectF(0f, 0f, vs[x1], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x1], 0f, vs[x2], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x2, x3),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x2], 0f, vs[x3], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x3, x4),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x3], 0f, vs[x4], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x4),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x4], 0f, 1f, vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x1], vs[y1], vs[x2], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x2, x3),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x2], vs[y1], vs[x3], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x3, x4),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x3], vs[y1], vs[x4], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x4),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x4], vs[y1], 1f, 1f) }
            )
        }
    }

    internal fun collage_9_5(): CollageLayout {
        return CollageLayoutFactory.collage("collage_9_5") {
            val x1 = param(0.3333f)
            val x2 = param(0.6666f)
            val y1 = param(0.25f)
            val y2 = param(0.5f)
            val y3 = param(0.75f)

            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, 0f, vs[x1], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x1], 0f, vs[x2], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x2], 0f, 1f, vs[y1]) }
            )

            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(0f, vs[y1], vs[x1], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[x1], vs[y1], vs[x2], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[x2], vs[y1], 1f, vs[y2]) }
            )

            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(0f, vs[y2], vs[x2], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y2, y3),
                boxParams = { vs -> RectF(vs[x2], vs[y2], 1f, vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y3),
                boxParams = { vs -> RectF(vs[x2], vs[y3], 1f, 1f) }
            )
        }
    }

    internal fun collage_9_4(): CollageLayout {
        return CollageLayoutFactory.collage("collage_9_4") {
            val xL = param(0.3333f)
            val xQ1 = param(0.25f)
            val xQ2 = param(0.5f)
            val xQ3 = param(0.75f)
            val y1 = param(0.3333f)
            val y2 = param(0.6666f)

            addBoxedItem(
                xParams = listOf(xL),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, 0f, vs[xL], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(xL),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[xL], 0f, 1f, vs[y1]) }
            )

            addBoxedItem(
                xParams = listOf(xQ1),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, vs[y1], vs[xQ1], 1f) }
            )
            addBoxedItem(
                xParams = listOf(xQ1, xQ2),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[xQ1], vs[y1], vs[xQ2], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(xQ2, xQ3),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[xQ2], vs[y1], vs[xQ3], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(xQ3),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[xQ3], vs[y1], 1f, vs[y2]) }
            )

            addBoxedItem(
                xParams = listOf(xQ1, xQ2),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[xQ1], vs[y2], vs[xQ2], 1f) }
            )
            addBoxedItem(
                xParams = listOf(xQ2, xQ3),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[xQ2], vs[y2], vs[xQ3], 1f) }
            )
            addBoxedItem(
                xParams = listOf(xQ3),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[xQ3], vs[y2], 1f, 1f) }
            )
        }
    }

    internal fun collage_9_3(): CollageLayout {
        return CollageLayoutFactory.collage("collage_9_3") {
            val x1 = param(0.2f)
            val x2 = param(0.4f)
            val x3 = param(0.6f)
            val x4 = param(0.8f)
            val y1 = param(0.2f)
            val y2 = param(0.4f)
            val y3 = param(0.6f)
            val y4 = param(0.8f)

            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(0f, 0f, vs[x1], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y2, y4),
                boxParams = { vs -> RectF(0f, vs[y2], vs[x1], vs[y4]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y4),
                boxParams = { vs -> RectF(0f, vs[y4], vs[x2], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x2, x4),
                yParams = listOf(y4),
                boxParams = { vs -> RectF(vs[x2], vs[y4], vs[x4], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x4),
                yParams = listOf(y3),
                boxParams = { vs -> RectF(vs[x4], vs[y3], 1f, 1f) }
            )
            addBoxedItem(
                xParams = listOf(x4),
                yParams = listOf(y1, y3),
                boxParams = { vs -> RectF(vs[x4], vs[y1], 1f, vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x3),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x1], 0f, vs[x3], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x3),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x3], 0f, 1f, vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x4),
                yParams = listOf(y1, y4),
                boxParams = { vs -> RectF(vs[x1], vs[y1], vs[x4], vs[y4]) }
            )
        }
    }

    internal fun collage_9_2(): CollageLayout {
        return CollageLayoutFactory.collage("collage_9_2") {
            val xM = param(0.5f)
            val x1 = param(0.3333f)
            val x2 = param(0.6666f)
            val y1 = param(0.25f)
            val y2 = param(0.5f)
            val y3 = param(0.75f)

            addBoxedItem(
                xParams = listOf(xM),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, 0f, vs[xM], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(xM),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[xM], 0f, 1f, vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(xM),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(0f, vs[y1], vs[xM], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(xM),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[xM], vs[y1], 1f, vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(xM),
                yParams = listOf(y2, y3),
                boxParams = { vs -> RectF(0f, vs[y2], vs[xM], vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(xM),
                yParams = listOf(y2, y3),
                boxParams = { vs -> RectF(vs[xM], vs[y2], 1f, vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y3),
                boxParams = { vs -> RectF(0f, vs[y3], vs[x1], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y3),
                boxParams = { vs -> RectF(vs[x1], vs[y3], vs[x2], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y3),
                boxParams = { vs -> RectF(vs[x2], vs[y3], 1f, 1f) }
            )
        }
    }

    internal fun collage_9_1(): CollageLayout {
        return CollageLayoutFactory.collage("collage_9_1") {
            val x1 = param(0.3333f)
            val x2 = param(0.6666f)
            val y1 = param(0.3333f)
            val y2 = param(0.6666f)

            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, 0f, vs[x1], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x1], 0f, vs[x2], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x2], 0f, 1f, vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(0f, vs[y1], vs[x1], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[x1], vs[y1], vs[x2], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[x2], vs[y1], 1f, vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(0f, vs[y2], vs[x1], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[x1], vs[y2], vs[x2], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[x2], vs[y2], 1f, 1f) }
            )
        }
    }

    internal fun collage_9_0(): CollageLayout {
        return CollageLayoutFactory.collage("collage_9_0") {
            val x1 = param(0.25f)
            val x2 = param(0.75f)
            val y1 = param(0.25f)
            val y2 = param(0.75f)

            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, 0f, vs[x1], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x1], 0f, vs[x2], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x2], 0f, 1f, vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(0f, vs[y1], vs[x1], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[x1], vs[y1], vs[x2], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[x2], vs[y1], 1f, vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(0f, vs[y2], vs[x1], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[x1], vs[y2], vs[x2], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[x2], vs[y2], 1f, 1f) }
            )
        }
    }
}