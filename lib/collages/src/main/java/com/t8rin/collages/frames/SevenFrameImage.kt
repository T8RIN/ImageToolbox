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
 * Created by admin on 6/30/2016.
 */
internal object SevenFrameImage {
    internal fun collage_7_10(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_7_10")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.25f, 0.6471f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 0.7727f))
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
        photoItem.bound.set(0.25f, 0f, 0.85f, 0.3529f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0f, 1f))
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
        photoItem.bound.set(0.5f, 0f, 1f, 0.5f)
        photoItem.pointList.add(PointF(0.7f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 0.4118f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //fourth frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.75f, 0.3529f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0.2273f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //fifth frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.15f, 0.6471f, 0.75f, 1f)
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //sixth frame
        photoItem = PhotoItem()
        photoItem.index = 5
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.5f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5882f))
        photoItem.pointList.add(PointF(0.3f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //seventh frame
        photoItem = PhotoItem()
        photoItem.index = 6
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.25f, 0.2059f, 0.75f, 0.7941f)
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0.25f))
        photoItem.pointList.add(PointF(1f, 0.75f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 0.75f))
        photoItem.pointList.add(PointF(0f, 0.25f))
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[5]] = PointF(1f, 1f)
        photoItemList.add(photoItem)

        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_7_9(): CollageLayout {
        return collage_7_8(name = "collage_7_9", y = 0.25f)
    }

    internal fun collage_7_8(name: String = "collage_7_8", y: Float = 0.75f): CollageLayout {
        return CollageLayoutFactory.collage(name) {
            val x1 = param(0.3f)
            val x2 = param(0.6f)
            val yLeft = param(y)
            val yR1 = param(0.3333f)
            val yR2 = param(0.6666f)

            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(yLeft),
                boxParams = { vs -> RectF(0f, 0f, vs[x1], vs[yLeft]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(yLeft),
                boxParams = { vs -> RectF(vs[x1], 0f, vs[x2], vs[yLeft]) }
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(yLeft),
                boxParams = { vs -> RectF(0f, vs[yLeft], vs[x1], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(yLeft),
                boxParams = { vs -> RectF(vs[x1], vs[yLeft], vs[x2], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(yR1),
                boxParams = { vs -> RectF(vs[x2], 0f, 1f, vs[yR1]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(yR1, yR2),
                boxParams = { vs -> RectF(vs[x2], vs[yR1], 1f, vs[yR2]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(yR2),
                boxParams = { vs -> RectF(vs[x2], vs[yR2], 1f, 1f) }
            )
        }
    }

    internal fun collage_7_7(): CollageLayout {
        return CollageLayoutFactory.collage("collage_7_7") {
            val x1 = param(0.3333f)
            val x2 = param(0.6666f)
            val y1 = param(0.3333f)
            val y2 = param(0.6666f)

            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, 0f, vs[x2], vs[y1]) }
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
                xParams = listOf(x1),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[x1], vs[y2], 1f, 1f) }
            )
        }
    }

    internal fun collage_7_6(): CollageLayout {
        return CollageLayoutFactory.collage("collage_7_6") {
            val x = param(0.6666f)
            val y0 = param(0.3333f)
            val y1 = param(0.6666f)
            val r1 = param(0.25f)
            val r2 = param(0.5f)
            val r3 = param(0.75f)

            addBoxedItem(
                xParams = listOf(x),
                yParams = listOf(y0),
                boxParams = { vs -> RectF(0f, 0f, vs[x], vs[y0]) }
            )
            addBoxedItem(
                xParams = listOf(x),
                yParams = listOf(y0, y1),
                boxParams = { vs -> RectF(0f, vs[y0], vs[x], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, vs[y1], vs[x], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x),
                yParams = listOf(r1),
                boxParams = { vs -> RectF(vs[x], 0f, 1f, vs[r1]) }
            )
            addBoxedItem(
                xParams = listOf(x),
                yParams = listOf(r1, r2),
                boxParams = { vs -> RectF(vs[x], vs[r1], 1f, vs[r2]) }
            )
            addBoxedItem(
                xParams = listOf(x),
                yParams = listOf(r2, r3),
                boxParams = { vs -> RectF(vs[x], vs[r2], 1f, vs[r3]) }
            )
            addBoxedItem(
                xParams = listOf(x),
                yParams = listOf(r3),
                boxParams = { vs -> RectF(vs[x], vs[r3], 1f, 1f) }
            )
        }
    }

    internal fun collage_7_5(): CollageLayout {
        return CollageLayoutFactory.collage("collage_7_5") {
            val x1 = param(0.3333f)
            val x2 = param(0.6666f)
            val x3 = param(0.5f)
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
                xParams = listOf(x3),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(0f, vs[y1], vs[x3], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x3),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[x3], vs[y1], 1f, vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x3),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(0f, vs[y2], vs[x3], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x3),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[x3], vs[y2], 1f, 1f) }
            )
        }
    }

    internal fun collage_7_4(): CollageLayout {
        return CollageLayoutFactory.collage("collage_7_4") {
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

    internal fun collage_7_3(): CollageLayout {
        return CollageLayoutFactory.collage("collage_7_3") {
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
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(0f, vs[y1], 1f, vs[y2]) }
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

    internal fun collage_7_2(): CollageLayout {
        return CollageLayoutFactory.collage("collage_7_2") {
            val x1 = param(0.3333f)
            val x2 = param(0.6666f)
            val y1 = param(0.3333f)
            val y2 = param(0.6666f)

            addBoxedItem(
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, 0f, 1f, vs[y1]) }
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

    internal fun collage_7_1(): CollageLayout {
        return CollageLayoutFactory.collage("collage_7_1") {
            val x0 = param(0.25f)
            val x1 = param(0.5f)
            val x2 = param(0.75f)
            val y = param(0.5f)

            addBoxedItem(
                xParams = listOf(x0),
                boxParams = { vs -> RectF(0f, 0f, vs[x0], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x0, x1),
                yParams = listOf(y),
                boxParams = { vs -> RectF(vs[x0], 0f, vs[x1], vs[y]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y),
                boxParams = { vs -> RectF(vs[x1], 0f, vs[x2], vs[y]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y),
                boxParams = { vs -> RectF(vs[x2], 0f, 1f, vs[y]) }
            )
            addBoxedItem(
                xParams = listOf(x0, x1),
                yParams = listOf(y),
                boxParams = { vs -> RectF(vs[x0], vs[y], vs[x1], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y),
                boxParams = { vs -> RectF(vs[x1], vs[y], vs[x2], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y),
                boxParams = { vs -> RectF(vs[x2], vs[y], 1f, 1f) }
            )
        }
    }

    internal fun collage_7_0(): CollageLayout {
        return CollageLayoutFactory.collage("collage_7_0") {
            val xL = param(0.3333f)
            val xTopSplit = param(0.5555f)
            val xRightSplit = param(0.7777f)
            val y1 = param(0.3333f)
            val y2 = param(0.6666f)

            addBoxedItem(
                xParams = listOf(xL),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, 0f, vs[xL], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(xL),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(0f, vs[y1], vs[xL], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(xL),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(0f, vs[y2], vs[xL], 1f) }
            )
            addBoxedItem(
                xParams = listOf(xL, xTopSplit),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[xL], 0f, vs[xTopSplit], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(xTopSplit),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[xTopSplit], 0f, 1f, vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(xL, xRightSplit),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[xL], vs[y1], vs[xRightSplit], 1f) }
            )
            addBoxedItem(
                xParams = listOf(xRightSplit),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[xRightSplit], vs[y1], 1f, 1f) }
            )
        }
    }

    internal fun collage_7_1_1(): CollageLayout {
        return CollageLayoutFactory.collageLinear(
            name = "collage_7_1_1",
            count = 7,
            isHorizontal = true
        )
    }

    internal fun collage_7_1_2(): CollageLayout {
        return CollageLayoutFactory.collageLinear(
            name = "collage_7_1_2",
            count = 7,
            isHorizontal = false
        )
    }
}