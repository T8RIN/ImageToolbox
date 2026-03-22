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
 * Created by admin on 6/26/2016.
 */
internal object SixFrameImage {
    internal fun collage_6_14(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_6_14")
        val photoItemList = mutableListOf<PhotoItem>()
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0f, 0.4f, 0.6f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(0.625f, 1f))
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
        photoItem.bound.set(0.4f, 0f, 1f, 0.3f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.3333f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
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
        photoItem.bound.set(0.6f, 0f, 1f, 0.6f)
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0.375f, 1f))
        photoItem.pointList.add(PointF(0f, 0.5f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //fourth frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.5f, 0.6f, 1f, 1f)
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
        //fifth frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.6f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(0.5f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(2f, 2f)
        photoItemList.add(photoItem)
        //sixth frame
        photoItem = PhotoItem()
        photoItem.index = 5
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.25f, 0.3f, 0.75f, 0.8f)
        photoItem.pointList.add(PointF(0.3f, 0f))
        photoItem.pointList.add(PointF(0.7f, 0f))
        photoItem.pointList.add(PointF(1f, 0.6f))
        photoItem.pointList.add(PointF(0.5f, 1f))
        photoItem.pointList.add(PointF(0f, 0.6f))
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[4]] = PointF(1f, 1f)
        photoItemList.add(photoItem)

        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_6_13(): CollageLayout {
        val item = CollageLayoutFactory.collage("collage_6_13")
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
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.35f, 0f, 1f, 0.4f)
        photoItem.pointList.add(PointF(0.2308f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(0.3846f, 1f))
        photoItem.pointList.add(PointF(0f, 0.375f))
        //shrink map
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)
        //fourth frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.15f, 0.6f, 1f)
        photoItem.pointList.add(PointF(0.5833f, 0f))
        photoItem.pointList.add(PointF(1f, 0.2941f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.pointList.add(PointF(0f, 0.4118f))
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
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0f, 0.6f, 0.65f, 1f)
        photoItem.pointList.add(PointF(0.6154f, 0f))
        photoItem.pointList.add(PointF(1f, 0.625f))
        photoItem.pointList.add(PointF(0.7692f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(2f, 1f)
        photoItemList.add(photoItem)
        //sixth frame
        photoItem = PhotoItem()
        photoItem.index = 5
        photoItem.shrinkMethod = PhotoItem.SHRINK_METHOD_COMMON
        photoItem.bound.set(0.4f, 0f, 1f, 0.85f)
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 0.5882f))
        photoItem.pointList.add(PointF(0.4166f, 1f))
        photoItem.pointList.add(PointF(0f, 0.7059f))
        photoItem.shrinkMap = HashMap()
        photoItem.shrinkMap!![photoItem.pointList[0]] = PointF(1f, 2f)
        photoItem.shrinkMap!![photoItem.pointList[1]] = PointF(2f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[2]] = PointF(1f, 1f)
        photoItem.shrinkMap!![photoItem.pointList[3]] = PointF(1f, 1f)
        photoItemList.add(photoItem)

        return item.copy(photoItemList = photoItemList)
    }

    internal fun collage_6_12(): CollageLayout {
        return CollageLayoutFactory.collage("collage_6_12") {
            val topY = param(0.2f)
            val midY = param(0.7f)
            val x1 = param(0.3333f)
            val x2 = param(0.6666f)
            val bottomSplitX = param(0.5f)

            addBoxedItem(
                yParams = listOf(topY),
                boxParams = { vs -> RectF(0f, 0f, 1f, vs[topY]) }
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(topY, midY),
                boxParams = { vs -> RectF(0f, vs[topY], vs[x1], vs[midY]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(topY, midY),
                boxParams = { vs -> RectF(vs[x1], vs[topY], vs[x2], vs[midY]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(topY, midY),
                boxParams = { vs -> RectF(vs[x2], vs[topY], 1f, vs[midY]) }
            )
            addBoxedItem(
                xParams = listOf(bottomSplitX),
                yParams = listOf(midY),
                boxParams = { vs -> RectF(0f, vs[midY], vs[bottomSplitX], 1f) }
            )
            addBoxedItem(
                xParams = listOf(bottomSplitX),
                yParams = listOf(midY),
                boxParams = { vs -> RectF(vs[bottomSplitX], vs[midY], 1f, 1f) }
            )
        }
    }

    internal fun collage_6_11(): CollageLayout {
        return CollageLayoutFactory.collage("collage_6_11") {
            val y1 = param(0.25f)
            val y2 = param(0.5f)
            val y3 = param(0.75f)
            val midX = param(0.5f)

            addBoxedItem(
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, 0f, 1f, vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(midX),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(0f, vs[y1], vs[midX], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(midX),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[midX], vs[y1], 1f, vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(midX),
                yParams = listOf(y2, y3),
                boxParams = { vs -> RectF(0f, vs[y2], vs[midX], vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(midX),
                yParams = listOf(y2, y3),
                boxParams = { vs -> RectF(vs[midX], vs[y2], 1f, vs[y3]) }
            )
            addBoxedItem(
                yParams = listOf(y3),
                boxParams = { vs -> RectF(0f, vs[y3], 1f, 1f) }
            )
        }
    }

    internal fun collage_6_10(): CollageLayout {
        return CollageLayoutFactory.collage("collage_6_10") {
            val rightX = param(0.6666f)
            val y1 = param(0.25f)
            val y2 = param(0.5f)
            val y3 = param(0.75f)

            addBoxedItem(
                xParams = listOf(rightX),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, 0f, vs[rightX], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(rightX),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(0f, vs[y1], vs[rightX], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(rightX),
                yParams = listOf(y2, y3),
                boxParams = { vs -> RectF(0f, vs[y2], vs[rightX], vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(rightX),
                yParams = listOf(y3),
                boxParams = { vs -> RectF(0f, vs[y3], vs[rightX], 1f) }
            )
            addBoxedItem(
                xParams = listOf(rightX),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[rightX], 0f, 1f, vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(rightX),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[rightX], vs[y1], 1f, 1f) }
            )
        }
    }

    internal fun collage_6_9(): CollageLayout {
        return CollageLayoutFactory.collage("collage_6_9") {
            val x1 = param(0.3333f)
            val x2 = param(0.6666f)
            val yTop = param(0.3333f)
            val yRightSplit = param(0.6666f)

            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(yTop),
                boxParams = { vs -> RectF(0f, 0f, vs[x1], vs[yTop]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(yTop),
                boxParams = { vs -> RectF(vs[x1], 0f, vs[x2], vs[yTop]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(yTop),
                boxParams = { vs -> RectF(vs[x2], 0f, 1f, vs[yTop]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(yTop),
                boxParams = { vs -> RectF(0f, vs[yTop], vs[x2], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(yTop, yRightSplit),
                boxParams = { vs -> RectF(vs[x2], vs[yTop], 1f, vs[yRightSplit]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(yRightSplit),
                boxParams = { vs -> RectF(vs[x2], vs[yRightSplit], 1f, 1f) }
            )
        }
    }

    internal fun collage_6_8(): CollageLayout {
        return CollageLayoutFactory.collage("collage_6_8") {
            val x1 = param(0.3333f)
            val x2 = param(0.6666f)
            val midY = param(0.5f)

            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(midY),
                boxParams = { vs -> RectF(0f, 0f, vs[x1], vs[midY]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(midY),
                boxParams = { vs -> RectF(vs[x1], 0f, vs[x2], vs[midY]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(midY),
                boxParams = { vs -> RectF(vs[x2], 0f, 1f, vs[midY]) }
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(midY),
                boxParams = { vs -> RectF(0f, vs[midY], vs[x1], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(midY),
                boxParams = { vs -> RectF(vs[x1], vs[midY], vs[x2], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(midY),
                boxParams = { vs -> RectF(vs[x2], vs[midY], 1f, 1f) }
            )
        }
    }

    internal fun collage_6_7(): CollageLayout {
        return CollageLayoutFactory.collage("collage_6_7") {
            val x1 = param(0.3333f)
            val x2 = param(0.6666f)
            val y1 = param(0.3333f)
            val y2 = param(0.6666f)
            val yTop = param(0.4f)

            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, 0f, vs[x1], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(0f, vs[y1], vs[x1], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(0f, vs[y2], vs[x1], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(yTop),
                boxParams = { vs -> RectF(vs[x1], vs[yTop], 1f, 1f) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(yTop),
                boxParams = { vs -> RectF(vs[x1], 0f, vs[x2], vs[yTop]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(yTop),
                boxParams = { vs -> RectF(vs[x2], 0f, 1f, vs[yTop]) }
            )
        }
    }

    internal fun collage_6_6(): CollageLayout {
        return CollageLayoutFactory.collage("collage_6_6") {
            val x1 = param(0.25f)
            val x2 = param(0.5f)
            val x3 = param(0.75f)
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
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[x2], 0f, 1f, vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, vs[y1], vs[x2], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x2, x3),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[x2], vs[y2], vs[x3], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x3),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[x3], vs[y2], 1f, 1f) }
            )
        }
    }

    internal fun collage_6_5(): CollageLayout {
        return collage_6_1(name = "collage_6_5", x1 = 0.6667f, x2 = 0.3333f, x3 = 0.6667f)
    }

    internal fun collage_6_4(): CollageLayout {
        return collage_6_3(name = "collage_6_4", initial_x1 = 0.5f)
    }

    internal fun collage_6_3(
        name: String = "collage_6_3",
        initial_x1: Float = 0.3333f,
        initial_y1: Float = 0.3333f,
        initial_y2: Float = 0.6666f
    ): CollageLayout {
        return CollageLayoutFactory.collage(name) {
            val x1 = param(initial_x1)
            val y1 = param(initial_y1)
            val y2 = param(initial_y2)

            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, 0f, vs[x1], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[x1], 0f, 1f, vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(0f, vs[y1], vs[x1], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[x1], vs[y1], 1f, vs[y2]) }
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

    internal fun collage_6_2(): CollageLayout {
        return collage_6_1(name = "collage_6_2", x3 = 0.3333f)
    }

    internal fun collage_6_1(
        name: String = "collage_6_1",
        x1: Float = 0.3333f,
        x2: Float = 0.5f,
        x3: Float = 0.6666f,
        y1: Float = 0.3333f,
        y2: Float = 0.6666f
    ): CollageLayout {
        return CollageLayoutFactory.collage(name) {
            val xTopSplit = param(x1)
            val y1 = param(y1)
            val midX = param(x2)
            val y2 = param(y2)
            val bottomSplitX = param(x3)

            addBoxedItem(
                xParams = listOf(xTopSplit),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(0f, 0f, vs[xTopSplit], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(xTopSplit),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[xTopSplit], 0f, 1f, vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(midX),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(0f, vs[y1], vs[midX], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(midX),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[midX], vs[y1], 1f, vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(bottomSplitX),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(0f, vs[y2], vs[bottomSplitX], 1f) }
            )
            addBoxedItem(
                xParams = listOf(bottomSplitX),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[bottomSplitX], vs[y2], 1f, 1f) }
            )
        }
    }

    internal fun collage_6_0(): CollageLayout {
        return CollageLayoutFactory.collage("collage_6_0") {
            val leftX = param(0.3333f)
            val y1 = param(0.3333f)
            val y2 = param(0.6667f)
            val bottomSplitX = param(0.6667f)

            addBoxedItem(
                xParams = listOf(leftX),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(0f, 0f, vs[leftX], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(leftX),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(0f, vs[y2], vs[leftX], 1f) }
            )
            addBoxedItem(
                xParams = listOf(leftX),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[leftX], 0f, 1f, vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(leftX),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[leftX], vs[y1], 1f, vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(leftX, bottomSplitX),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[leftX], vs[y2], vs[bottomSplitX], 1f) }
            )
            addBoxedItem(
                xParams = listOf(bottomSplitX),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[bottomSplitX], vs[y2], 1f, 1f) }
            )
        }
    }

    internal fun collage_6_1_1(): CollageLayout {
        return CollageLayoutFactory.collageLinear(
            name = "collage_6_1_1",
            count = 6,
            isHorizontal = true
        )
    }

    internal fun collage_6_1_2(): CollageLayout {
        return CollageLayoutFactory.collageLinear(
            name = "collage_6_1_2",
            count = 6,
            isHorizontal = false
        )
    }
}