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

package com.t8rin.collages.geometry

import android.graphics.PathMeasure
import android.graphics.PointF
import android.graphics.RectF
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.collages.layout.generator.ProceduralCollageLayouts
import com.t8rin.collages.layout.toLayout
import com.t8rin.collages.model.CollagePoint
import com.t8rin.collages.model.PhotoItem
import com.t8rin.collages.view.FrameImageView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CollageSpacingTest {
    @Test
    fun slantedEdgeDoesNotLeaveAFragmentAtTheCanvasBoundary() {
        val item = PhotoItem(
            bound = RectF(0f, 0f, 1f, 1f),
            shape = listOf(
                CollagePoint(0f, .4f), CollagePoint(.8f, 0f),
                CollagePoint(1f, .2f), CollagePoint(.5f, .8f), CollagePoint(0f, 1f)
            )
        )
        for (spacing in listOf(10f, 40f, 100f)) {
            val path = FrameImageView.shapePath(item, 1000f, 1000f, spacing)
            val measure = PathMeasure(path, true)
            assertTrue(measure.length > 0f)
            assertFalse(measure.nextContour())
        }
    }

    @Test
    fun tangentCurveIsNotTreatedAsAStraightCanvasEdge() {
        val bounds = RectF(0f, 0f, 1000f, 1000f)
        assertFalse(isCollageEdge(PointF(0f, 500f), PointF(.5f, 520f), bounds))
        assertTrue(isCollageEdge(PointF(0f, 500f), PointF(0f, 520f), bounds))
    }

    @Test
    fun narrowLegacySlotRemainsVisibleAtMaximumSpacing() {
        val item = PhotoItem(
            bound = RectF(.2f, .2f, .4f, .4f),
            pointList = arrayListOf(PointF(0f, 0f), PointF(1f, 0f), PointF(1f, 1f), PointF(0f, 1f))
        )
        val bounds = RectF()
        FrameImageView.shapePath(item, 200f, 200f, 100f).computeBounds(bounds, true)
        assertEquals(60f, bounds.width(), .01f)
        assertEquals(60f, bounds.height(), .01f)
    }

    @Test
    fun closelySpacedRingVerticesDoNotEraseTheSlot() {
        val layout = ProceduralCollageLayouts.create(97, 5).toLayout("ring")
        val item = layout.photoItemList[28]
        for (spacing in listOf(10f, 40f, 100f)) {
            val path = FrameImageView.shapePath(
                item, 1000 * item.bound.width(), 1000 * item.bound.height(), spacing
            )
            assertFalse(path.isEmpty)
            assertFalse(PathMeasure(path, true).nextContour())
        }
    }
}