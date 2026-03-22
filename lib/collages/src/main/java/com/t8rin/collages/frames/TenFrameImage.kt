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

import android.graphics.RectF
import com.t8rin.collages.model.CollageLayout
import com.t8rin.collages.utils.CollageLayoutFactory

/**
 * Created by admin on 7/4/2016.
 */
internal object TenFrameImage {
    internal fun collage_10_8(): CollageLayout {
        return CollageLayoutFactory.collage("collage_10_8") {
            val x1 = param(0.2f)
            val x2 = param(0.5f)
            val x3 = param(0.8f)
            val y1 = param(0.2f)
            val y2 = param(0.5f)
            val y3 = param(0.8f)

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
                xParams = listOf(x1, x2),
                yParams = listOf(y1, y3),
                boxParams = { vs -> RectF(vs[x1], vs[y1], vs[x2], vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(x2, x3),
                yParams = listOf(y1, y3),
                boxParams = { vs -> RectF(vs[x2], vs[y1], vs[x3], vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y2, y3),
                boxParams = { vs -> RectF(0f, vs[y2], vs[x1], vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(x3),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[x3], vs[y1], 1f, vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x3),
                yParams = listOf(y2, y3),
                boxParams = { vs -> RectF(vs[x3], vs[y2], 1f, vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(x3),
                yParams = listOf(y3),
                boxParams = { vs -> RectF(0f, vs[y3], vs[x3], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x3),
                yParams = listOf(y3),
                boxParams = { vs -> RectF(vs[x3], vs[y3], 1f, 1f) }
            )
        }
    }

    internal fun collage_10_7(): CollageLayout {
        return CollageLayoutFactory.collage("collage_10_7") {
            val x1 = param(0.2f)
            val x2 = param(0.6f)
            val y1 = param(0.2f)
            val y2 = param(0.5f)
            val y3 = param(0.8f)

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
                yParams = listOf(y2, y3),
                boxParams = { vs -> RectF(0f, vs[y2], vs[x1], vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y2, y3),
                boxParams = { vs -> RectF(vs[x1], vs[y2], vs[x2], vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y2, y3),
                boxParams = { vs -> RectF(vs[x2], vs[y2], 1f, vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y3),
                boxParams = { vs -> RectF(0f, vs[y3], vs[x2], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y3),
                boxParams = { vs -> RectF(vs[x2], vs[y3], 1f, 1f) }
            )
        }
    }

    internal fun collage_10_6(): CollageLayout {
        return CollageLayoutFactory.collage("collage_10_6") {
            val x1 = param(0.25f)
            val x2 = param(0.5f)
            val x3 = param(0.75f)
            val y1 = param(0.25f)
            val y2 = param(0.5f)
            val y3 = param(0.75f)

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
                yParams = listOf(y1, y3),
                boxParams = { vs -> RectF(0f, vs[y1], vs[x1], vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[x1], vs[y1], vs[x2], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x2, x3),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[x2], vs[y1], vs[x3], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y2, y3),
                boxParams = { vs -> RectF(vs[x1], vs[y2], vs[x2], vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(x2, x3),
                yParams = listOf(y2, y3),
                boxParams = { vs -> RectF(vs[x2], vs[y2], vs[x3], vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(x3),
                yParams = listOf(y1, y3),
                boxParams = { vs -> RectF(vs[x3], vs[y1], 1f, vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y3),
                boxParams = { vs -> RectF(0f, vs[y3], vs[x2], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y3),
                boxParams = { vs -> RectF(vs[x2], vs[y3], 1f, 1f) }
            )
        }
    }

    internal fun collage_10_5(): CollageLayout {
        return CollageLayoutFactory.collage("collage_10_5") {
            val xL = param(0.2f)
            val xM = param(0.5f)
            val xR = param(0.8f)
            val yT = param(0.25f)
            val yM = param(0.5f)
            val yB = param(0.75f)

            addBoxedItem(
                xParams = listOf(xM),
                yParams = listOf(yT),
                boxParams = { vs -> RectF(0f, 0f, vs[xM], vs[yT]) }
            )
            addBoxedItem(
                xParams = listOf(xM),
                yParams = listOf(yT),
                boxParams = { vs -> RectF(vs[xM], 0f, 1f, vs[yT]) }
            )
            addBoxedItem(
                xParams = listOf(xL),
                yParams = listOf(yT, yM),
                boxParams = { vs -> RectF(0f, vs[yT], vs[xL], vs[yM]) }
            )
            addBoxedItem(
                xParams = listOf(xL, xR),
                yParams = listOf(yT, yM),
                boxParams = { vs -> RectF(vs[xL], vs[yT], vs[xR], vs[yM]) }
            )
            addBoxedItem(
                xParams = listOf(xR),
                yParams = listOf(yT, yM),
                boxParams = { vs -> RectF(vs[xR], vs[yT], 1f, vs[yM]) }
            )
            addBoxedItem(
                xParams = listOf(xL),
                yParams = listOf(yM, yB),
                boxParams = { vs -> RectF(0f, vs[yM], vs[xL], vs[yB]) }
            )
            addBoxedItem(
                xParams = listOf(xL, xR),
                yParams = listOf(yM, yB),
                boxParams = { vs -> RectF(vs[xL], vs[yM], vs[xR], vs[yB]) }
            )
            addBoxedItem(
                xParams = listOf(xR),
                yParams = listOf(yM, yB),
                boxParams = { vs -> RectF(vs[xR], vs[yM], 1f, vs[yB]) }
            )
            addBoxedItem(
                xParams = listOf(xM),
                yParams = listOf(yB),
                boxParams = { vs -> RectF(0f, vs[yB], vs[xM], 1f) }
            )
            addBoxedItem(
                xParams = listOf(xM),
                yParams = listOf(yB),
                boxParams = { vs -> RectF(vs[xM], vs[yB], 1f, 1f) }
            )
        }
    }

    internal fun collage_10_4(): CollageLayout {
        return CollageLayoutFactory.collage("collage_10_4") {
            val xM = param(0.5f)
            val xR = param(0.8f)
            val y1 = param(0.2f)
            val y2 = param(0.5f)
            val y3 = param(0.8f)

            addBoxedItem(
                xParams = listOf(xM),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(0f, 0f, vs[xM], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(xM),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(0f, vs[y2], vs[xM], 1f) }
            )
            addBoxedItem(
                xParams = listOf(xM, xR),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[xM], 0f, vs[xR], vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(xR),
                yParams = listOf(y1),
                boxParams = { vs -> RectF(vs[xR], 0f, 1f, vs[y1]) }
            )
            addBoxedItem(
                xParams = listOf(xM, xR),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[xM], vs[y1], vs[xR], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(xR),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[xR], vs[y1], 1f, vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(xM, xR),
                yParams = listOf(y2, y3),
                boxParams = { vs -> RectF(vs[xM], vs[y2], vs[xR], vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(xR),
                yParams = listOf(y2, y3),
                boxParams = { vs -> RectF(vs[xR], vs[y2], 1f, vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(xM, xR),
                yParams = listOf(y3),
                boxParams = { vs -> RectF(vs[xM], vs[y3], vs[xR], 1f) }
            )
            addBoxedItem(
                xParams = listOf(xR),
                yParams = listOf(y3),
                boxParams = { vs -> RectF(vs[xR], vs[y3], 1f, 1f) }
            )
        }
    }

    internal fun collage_10_3(): CollageLayout {
        return CollageLayoutFactory.collage("collage_10_3") {
            val xL = param(0.7f)
            val xR = param(0.9f)
            val xS = param(0.3f)
            val y1 = param(0.3f)
            val y2 = param(0.7f)
            val y3 = param(0.9f)

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
                xParams = listOf(xL),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(0f, vs[y1], vs[xL], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(xL, xR),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[xL], vs[y1], vs[xR], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(xR),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[xR], vs[y1], 1f, vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(xS),
                yParams = listOf(y2, y3),
                boxParams = { vs -> RectF(0f, vs[y2], vs[xS], vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(xS),
                yParams = listOf(y3),
                boxParams = { vs -> RectF(0f, vs[y3], vs[xS], 1f) }
            )
            addBoxedItem(
                xParams = listOf(xS, xL),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[xS], vs[y2], vs[xL], 1f) }
            )
            addBoxedItem(
                xParams = listOf(xL),
                yParams = listOf(y2, y3),
                boxParams = { vs -> RectF(vs[xL], vs[y2], 1f, vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(xL),
                yParams = listOf(y3),
                boxParams = { vs -> RectF(vs[xL], vs[y3], 1f, 1f) }
            )
        }
    }

    internal fun collage_10_2(): CollageLayout {
        return CollageLayoutFactory.collage("collage_10_2") {
            val xM = param(0.5f)
            val xL = param(0.2f)
            val xR = param(0.8f)
            val yM = param(0.5f)
            val yB = param(0.8f)

            addBoxedItem(
                xParams = listOf(xM),
                yParams = listOf(yM),
                boxParams = { vs -> RectF(0f, 0f, vs[xM], vs[yM]) }
            )
            addBoxedItem(
                xParams = listOf(xM),
                yParams = listOf(yM),
                boxParams = { vs -> RectF(vs[xM], 0f, 1f, vs[yM]) }
            )
            addBoxedItem(
                xParams = listOf(xL),
                yParams = listOf(yM, yB),
                boxParams = { vs -> RectF(0f, vs[yM], vs[xL], vs[yB]) }
            )
            addBoxedItem(
                xParams = listOf(xL, xM),
                yParams = listOf(yM, yB),
                boxParams = { vs -> RectF(vs[xL], vs[yM], vs[xM], vs[yB]) }
            )
            addBoxedItem(
                xParams = listOf(xM, xR),
                yParams = listOf(yM, yB),
                boxParams = { vs -> RectF(vs[xM], vs[yM], vs[xR], vs[yB]) }
            )
            addBoxedItem(
                xParams = listOf(xR),
                yParams = listOf(yM, yB),
                boxParams = { vs -> RectF(vs[xR], vs[yM], 1f, vs[yB]) }
            )
            addBoxedItem(
                xParams = listOf(xL),
                yParams = listOf(yB),
                boxParams = { vs -> RectF(0f, vs[yB], vs[xL], 1f) }
            )
            addBoxedItem(
                xParams = listOf(xL, xM),
                yParams = listOf(yB),
                boxParams = { vs -> RectF(vs[xL], vs[yB], vs[xM], 1f) }
            )
            addBoxedItem(
                xParams = listOf(xM, xR),
                yParams = listOf(yB),
                boxParams = { vs -> RectF(vs[xM], vs[yB], vs[xR], 1f) }
            )
            addBoxedItem(
                xParams = listOf(xR),
                yParams = listOf(yB),
                boxParams = { vs -> RectF(vs[xR], vs[yB], 1f, 1f) }
            )
        }
    }

    internal fun collage_10_1(): CollageLayout {
        return CollageLayoutFactory.collage("collage_10_1") {
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
                xParams = listOf(x2, x3),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[x2], vs[y1], vs[x3], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x3),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[x3], vs[y1], 1f, vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x1),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(0f, vs[y2], vs[x1], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x1, x3),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[x1], vs[y2], vs[x3], 1f) }
            )
            addBoxedItem(
                xParams = listOf(x3),
                yParams = listOf(y2),
                boxParams = { vs -> RectF(vs[x3], vs[y2], 1f, 1f) }
            )
        }
    }

    internal fun collage_10_0(): CollageLayout {
        return CollageLayoutFactory.collage("collage_10_0") {
            val x1 = param(0.2f)
            val x2 = param(0.8f)
            val y1 = param(0.2f)
            val y2 = param(0.5f)
            val y3 = param(0.8f)

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
                yParams = listOf(y1, y3),
                boxParams = { vs -> RectF(0f, vs[y1], vs[x1], vs[y3]) }
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
            addBoxedItem(
                xParams = listOf(x2),
                yParams = listOf(y1, y3),
                boxParams = { vs -> RectF(vs[x2], vs[y1], 1f, vs[y3]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y1, y2),
                boxParams = { vs -> RectF(vs[x1], vs[y1], vs[x2], vs[y2]) }
            )
            addBoxedItem(
                xParams = listOf(x1, x2),
                yParams = listOf(y2, y3),
                boxParams = { vs -> RectF(vs[x1], vs[y2], vs[x2], vs[y3]) }
            )
        }
    }
}