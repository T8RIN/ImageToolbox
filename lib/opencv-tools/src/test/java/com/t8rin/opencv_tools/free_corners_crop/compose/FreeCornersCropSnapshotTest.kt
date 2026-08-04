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

package com.t8rin.opencv_tools.free_corners_crop.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FreeCornersCropSnapshotTest {

    @Test
    fun newAttachmentCapturesOutgoingCropperBeforeReplacingCallbacks() {
        val initial = snapshotAt(0.1f)
        val outgoing = snapshotAt(0.3f)
        val incomingInitial = snapshotAt(0.8f)
        val state = FreeCornersCropperState()
        val firstAttachmentKey = Any()
        val secondAttachmentKey = Any()
        var renderedSnapshot = initial
        var restoredSnapshot: FreeCornersCropSnapshot? = null

        state.attach(
            attachmentKey = firstAttachmentKey,
            imageKey = "image",
            captureSnapshot = { renderedSnapshot },
            restoreSnapshot = {}
        )
        state.onCropperReady()
        state.beginTransformation()
        renderedSnapshot = outgoing

        state.attach(
            attachmentKey = secondAttachmentKey,
            imageKey = "image",
            captureSnapshot = { incomingInitial },
            restoreSnapshot = { restoredSnapshot = it }
        )
        state.prepareForReattachment(firstAttachmentKey)
        state.detach(firstAttachmentKey)
        state.onCropperReady()

        assertEquals(outgoing, restoredSnapshot)
    }

    @Test
    fun committedSnapshotIsNotReplacedByTemporaryLayoutGeometry() {
        val initial = snapshotAt(0.1f)
        val edited = snapshotAt(0.3f)
        val temporaryLayout = snapshotAt(0.8f)
        val state = FreeCornersCropperState()
        val firstAttachmentKey = Any()
        val secondAttachmentKey = Any()
        var renderedSnapshot = initial
        var restoredSnapshot: FreeCornersCropSnapshot? = null

        state.attach(
            attachmentKey = firstAttachmentKey,
            imageKey = "image",
            captureSnapshot = { renderedSnapshot },
            restoreSnapshot = {}
        )
        state.onCropperReady()
        state.beginTransformation()
        renderedSnapshot = edited
        state.endTransformation()
        renderedSnapshot = temporaryLayout

        state.attach(
            attachmentKey = secondAttachmentKey,
            imageKey = "image",
            captureSnapshot = { temporaryLayout },
            restoreSnapshot = { restoredSnapshot = it }
        )
        state.onCropperReady()

        assertEquals(edited, restoredSnapshot)
    }

    @Test
    fun layoutChangeUsesCommittedSnapshotInsteadOfReinitializedGeometry() {
        val initial = snapshotAt(0.1f)
        val edited = snapshotAt(0.3f)
        val reinitialized = snapshotAt(0.8f)
        val state = FreeCornersCropperState()
        var renderedSnapshot = initial

        state.attach(
            attachmentKey = Any(),
            imageKey = "image",
            captureSnapshot = { renderedSnapshot },
            restoreSnapshot = {}
        )
        state.onCropperReady()
        state.beginTransformation()
        renderedSnapshot = edited
        state.endTransformation()

        renderedSnapshot = reinitialized

        assertEquals(edited, state.snapshotForLayoutChange())
    }

    @Test
    fun snapshotKeepsImageCoordinatesAcrossOrientationChange() {
        val portraitBounds = Rect(140f, 320f, 940f, 920f)
        val landscapeBounds = Rect(460f, 90f, 1460f, 840f)
        val portraitViewport = Rect(16f, 16f, 1064f, 1904f)
        val landscapeViewport = Rect(16f, 16f, 1904f, 1064f)
        val initialSnapshot = FreeCornersCropSnapshot(
            normalizedPoints = listOf(
                Offset(0.1f, 0.2f),
                Offset(0.8f, 0.15f),
                Offset(0.9f, 0.85f),
                Offset(0.2f, 0.9f)
            ),
            normalizedViewportCenter = Offset(0.42f, 0.58f),
            viewportFillFraction = 0.76f
        )

        val portrait = requireNotNull(
            restoreFreeCornersCropSnapshot(
                snapshot = initialSnapshot,
                imageBounds = portraitBounds,
                viewportBounds = portraitViewport
            )
        )
        val capturedInPortrait = requireNotNull(
            captureFreeCornersCropSnapshot(
                points = portrait.points,
                imageBounds = portraitBounds,
                imageScale = portrait.imageScale,
                imageTranslation = portrait.imageTranslation,
                viewportBounds = portraitViewport
            )
        )
        val landscape = requireNotNull(
            restoreFreeCornersCropSnapshot(
                snapshot = capturedInPortrait,
                imageBounds = landscapeBounds,
                viewportBounds = landscapeViewport
            )
        )
        val capturedInLandscape = requireNotNull(
            captureFreeCornersCropSnapshot(
                points = landscape.points,
                imageBounds = landscapeBounds,
                imageScale = landscape.imageScale,
                imageTranslation = landscape.imageTranslation,
                viewportBounds = landscapeViewport
            )
        )

        assertEquals(
            initialSnapshot.viewportFillFraction,
            capturedInLandscape.viewportFillFraction,
            0.0001f
        )
        initialSnapshot.normalizedPoints.zip(capturedInLandscape.normalizedPoints)
            .forEach { (expected, actual) -> assertOffsetEquals(expected, actual) }
        assertTrue(landscape.points.all { isInside(landscapeViewport, it) })
    }

    @Test
    fun repeatedOrientationChangesKeepLogicalPointsAndFrameInsideViewport() {
        val expectedPoints = listOf(
            Offset(0.02f, 0.08f),
            Offset(0.72f, 0.03f),
            Offset(0.98f, 0.91f),
            Offset(0.11f, 0.97f)
        )
        var snapshot = FreeCornersCropSnapshot(
            normalizedPoints = expectedPoints,
            normalizedViewportCenter = Offset(0.75f, 0.25f),
            viewportFillFraction = 0.92f
        )
        val layouts = listOf(
            Rect(140f, 320f, 940f, 920f) to Rect(16f, 16f, 1064f, 1904f),
            Rect(460f, 90f, 1460f, 840f) to Rect(16f, 16f, 1904f, 1064f),
            Rect(140f, 320f, 940f, 920f) to Rect(16f, 16f, 1064f, 1904f),
            Rect(460f, 90f, 1460f, 840f) to Rect(16f, 16f, 1904f, 1064f)
        )

        layouts.forEach { (imageBounds, viewportBounds) ->
            val restored = requireNotNull(
                restoreFreeCornersCropSnapshot(
                    snapshot = snapshot,
                    imageBounds = imageBounds,
                    viewportBounds = viewportBounds
                )
            )
            assertTrue(restored.points.all { isInside(viewportBounds, it) })
            snapshot = requireNotNull(
                captureFreeCornersCropSnapshot(
                    points = restored.points,
                    imageBounds = imageBounds,
                    imageScale = restored.imageScale,
                    imageTranslation = restored.imageTranslation,
                    viewportBounds = viewportBounds
                )
            )
            expectedPoints.zip(snapshot.normalizedPoints).forEach { (expected, actual) ->
                assertOffsetEquals(expected, actual)
            }
        }
    }

    private fun assertOffsetEquals(expected: Offset, actual: Offset) {
        assertEquals(expected.x, actual.x, 0.0001f)
        assertEquals(expected.y, actual.y, 0.0001f)
    }

    private fun isInside(rect: Rect, point: Offset): Boolean =
        point.x >= rect.left - 0.001f &&
                point.x <= rect.right + 0.001f &&
                point.y >= rect.top - 0.001f &&
                point.y <= rect.bottom + 0.001f

    private fun snapshotAt(offset: Float) = FreeCornersCropSnapshot(
        normalizedPoints = listOf(
            Offset(offset, offset),
            Offset(offset + 0.1f, offset),
            Offset(offset + 0.1f, offset + 0.1f),
            Offset(offset, offset + 0.1f)
        ),
        normalizedViewportCenter = Offset(0.5f, 0.5f),
        viewportFillFraction = 0.5f
    )
}
