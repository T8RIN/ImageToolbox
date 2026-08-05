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

package com.t8rin.cropper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import com.t8rin.cropper.state.calculateRestoreGeometry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ImageCropperStateTest {

    @Test
    fun savedStateIsRestoredAfterImageChange() {
        val beforeCrop = snapshot(0.35f)
        val newImageInitial = snapshot(0.8f)
        val state = ImageCropperState()
        var restoredSnapshot: ImageCropSnapshot? = null

        state.attach(
            attachmentKey = Any(),
            imageKey = "before-crop",
            captureSnapshot = { beforeCrop },
            restoreSnapshot = {}
        )
        state.onCropperReady()
        val savedState = requireNotNull(state.saveState())

        state.restoreStateOnNextImageChange(savedState)
        state.attach(
            attachmentKey = Any(),
            imageKey = "after-undo",
            captureSnapshot = { newImageInitial },
            restoreSnapshot = { restoredSnapshot = it }
        )
        state.onCropperReady()

        assertEquals(beforeCrop.copy(preferExactTransform = true), restoredSnapshot)

        restoredSnapshot = null
        state.attach(
            attachmentKey = Any(),
            imageKey = "after-transition",
            captureSnapshot = { newImageInitial },
            restoreSnapshot = { restoredSnapshot = it }
        )
        state.onCropperReady()

        assertEquals(beforeCrop.copy(preferExactTransform = true), restoredSnapshot)
    }

    @Test
    fun undoRedoKeepsLogicalSnapshotsWhileRestoreIsPending() {
        val before = snapshot(10f)
        val after = snapshot(20f)
        val state = ImageCropperState()
        var renderedSnapshot = before
        var pendingRestore: ImageCropSnapshot? = null
        val attachmentKey = Any()

        state.attach(
            attachmentKey = attachmentKey,
            imageKey = "image",
            captureSnapshot = { renderedSnapshot },
            restoreSnapshot = { pendingRestore = it }
        )
        state.onCropperReady()
        state.beginTransformation()
        renderedSnapshot = after
        assertTrue(state.endTransformation())

        state.undo()
        assertEquals(before, pendingRestore)

        state.redo()
        assertEquals(after, pendingRestore)

        state.undo()
        assertEquals(before, pendingRestore)

        state.redo()
        assertEquals(after, pendingRestore)
    }

    @Test
    fun staleCropperCannotDetachCurrentCropper() {
        val before = snapshot(10f)
        val after = snapshot(20f)
        val state = ImageCropperState()
        val staleAttachmentKey = Any()
        val currentAttachmentKey = Any()
        var renderedSnapshot = before
        var restoredSnapshot: ImageCropSnapshot? = null

        state.attach(
            attachmentKey = staleAttachmentKey,
            imageKey = "image",
            captureSnapshot = { renderedSnapshot },
            restoreSnapshot = {}
        )
        state.onCropperReady()
        state.attach(
            attachmentKey = currentAttachmentKey,
            imageKey = "image",
            captureSnapshot = { renderedSnapshot },
            restoreSnapshot = { restoredSnapshot = it }
        )
        state.detach(staleAttachmentKey)

        state.beginTransformation()
        renderedSnapshot = after
        assertTrue(state.endTransformation())
        state.undo()

        assertEquals(before, restoredSnapshot)
    }

    @Test
    fun reattachmentDoesNotRestoreWithoutExplicitRequest() {
        val before = snapshot(10f)
        val after = snapshot(20f)
        val state = ImageCropperState()
        val firstAttachmentKey = Any()
        val secondAttachmentKey = Any()
        var renderedSnapshot = before
        var restoredSnapshot: ImageCropSnapshot? = null

        state.attach(
            attachmentKey = firstAttachmentKey,
            imageKey = "image",
            captureSnapshot = { renderedSnapshot },
            restoreSnapshot = {}
        )
        state.onCropperReady()
        state.detach(firstAttachmentKey)
        renderedSnapshot = after
        state.attach(
            attachmentKey = secondAttachmentKey,
            imageKey = "image",
            captureSnapshot = { renderedSnapshot },
            restoreSnapshot = { restoredSnapshot = it }
        )
        state.onCropperReady()

        assertNull(restoredSnapshot)
    }

    @Test
    fun reattachmentRestoresSnapshotAfterExplicitRequest() {
        val before = snapshot(10f)
        val after = snapshot(20f)
        val state = ImageCropperState()
        val firstAttachmentKey = Any()
        val secondAttachmentKey = Any()
        var renderedSnapshot = before
        var restoredSnapshot: ImageCropSnapshot? = null

        state.attach(
            attachmentKey = firstAttachmentKey,
            imageKey = "image",
            captureSnapshot = { renderedSnapshot },
            restoreSnapshot = {}
        )
        state.onCropperReady()
        state.prepareForReattachment()
        state.detach(firstAttachmentKey)
        renderedSnapshot = after
        state.attach(
            attachmentKey = secondAttachmentKey,
            imageKey = "image",
            captureSnapshot = { renderedSnapshot },
            restoreSnapshot = { restoredSnapshot = it }
        )
        state.onCropperReady()

        assertEquals(before, restoredSnapshot)
    }

    @Test
    fun layoutReattachmentRestoresRelativeSnapshotAutomatically() {
        val before = snapshot(0.1f)
        val state = ImageCropperState()
        val firstAttachmentKey = Any()
        val secondAttachmentKey = Any()
        var restoredSnapshot: ImageCropSnapshot? = null

        state.attach(
            attachmentKey = firstAttachmentKey,
            imageKey = "image",
            layoutKey = IntSize(1080, 1920),
            configurationKey = "same-config",
            captureSnapshot = { before },
            restoreSnapshot = {}
        )
        state.onCropperReady()
        state.detach(firstAttachmentKey)
        state.attach(
            attachmentKey = secondAttachmentKey,
            imageKey = "image",
            layoutKey = IntSize(1920, 1080),
            configurationKey = "same-config",
            captureSnapshot = { snapshot(0.2f) },
            restoreSnapshot = { restoredSnapshot = it }
        )
        state.onCropperReady()

        assertEquals(before, restoredSnapshot)
    }

    @Test
    fun configurationChangeDoesNotRestoreSnapshotOverNewCropGeometry() {
        val state = ImageCropperState()
        val firstAttachmentKey = Any()
        val secondAttachmentKey = Any()
        var restoredSnapshot: ImageCropSnapshot? = null

        state.attach(
            attachmentKey = firstAttachmentKey,
            imageKey = "image",
            layoutKey = IntSize(1080, 1920),
            configurationKey = "1:1",
            captureSnapshot = { snapshot(0.1f) },
            restoreSnapshot = {}
        )
        state.onCropperReady()
        state.detach(firstAttachmentKey)

        state.attach(
            attachmentKey = secondAttachmentKey,
            imageKey = "image",
            layoutKey = IntSize(1080, 1920),
            configurationKey = "16:9",
            captureSnapshot = { snapshot(0.2f) },
            restoreSnapshot = { restoredSnapshot = it }
        )
        state.onCropperReady()

        assertNull(restoredSnapshot)
    }

    @Test
    fun explicitReattachmentDoesNotRestoreSnapshotAfterConfigurationChange() {
        val state = ImageCropperState()
        val firstAttachmentKey = Any()
        val secondAttachmentKey = Any()
        var restoredSnapshot: ImageCropSnapshot? = null

        state.attach(
            attachmentKey = firstAttachmentKey,
            imageKey = "image",
            layoutKey = IntSize(1080, 1920),
            configurationKey = "free",
            captureSnapshot = { snapshot(0.1f) },
            restoreSnapshot = {}
        )
        state.onCropperReady()
        state.prepareForReattachment(firstAttachmentKey)
        state.detach(firstAttachmentKey)

        state.attach(
            attachmentKey = secondAttachmentKey,
            imageKey = "image",
            layoutKey = IntSize(1080, 1920),
            configurationKey = "9:16",
            captureSnapshot = { snapshot(0.2f) },
            restoreSnapshot = { restoredSnapshot = it }
        )
        state.onCropperReady()

        assertNull(restoredSnapshot)
    }

    @Test
    fun layoutRestoreDoesNotReplaceLogicalSnapshotWithConstrainedGeometry() {
        val logicalSnapshot = snapshot(0.1f)
        val constrainedLandscapeSnapshot = snapshot(0.35f)
        val state = ImageCropperState()
        val portraitAttachment = Any()
        val landscapeAttachment = Any()
        val restoredPortraitAttachment = Any()
        var restoredSnapshot: ImageCropSnapshot? = null

        state.attach(
            attachmentKey = portraitAttachment,
            imageKey = "image",
            layoutKey = IntSize(1080, 1920),
            configurationKey = "free",
            captureSnapshot = { logicalSnapshot },
            restoreSnapshot = {}
        )
        state.onCropperReady()
        state.detach(portraitAttachment)

        state.attach(
            attachmentKey = landscapeAttachment,
            imageKey = "image",
            layoutKey = IntSize(1920, 1080),
            configurationKey = "free",
            captureSnapshot = { constrainedLandscapeSnapshot },
            restoreSnapshot = {}
        )
        state.onCropperReady()
        state.captureLayoutSnapshot(
            attachmentKey = landscapeAttachment,
            snapshot = constrainedLandscapeSnapshot
        )
        state.syncSnapshot()
        state.detach(landscapeAttachment)

        state.attach(
            attachmentKey = restoredPortraitAttachment,
            imageKey = "image",
            layoutKey = IntSize(1080, 1920),
            configurationKey = "free",
            captureSnapshot = { snapshot(0.8f) },
            restoreSnapshot = { restoredSnapshot = it }
        )
        state.onCropperReady()

        assertEquals(logicalSnapshot, restoredSnapshot)
    }

    @Test
    fun sameWidthRemeasureUsesSnapshotContainerSize() {
        val snapshot = ImageCropSnapshot(
            normalizedCropRect = Rect(0.1f, 0.2f, 0.4f, 0.8f),
            containerSize = IntSize(1080, 1920),
            rotation = 0f
        )
        val state = ImageCropperState()
        val attachmentKey = Any()

        state.attach(
            attachmentKey = attachmentKey,
            imageKey = "image",
            layoutKey = IntSize(1080, 1920),
            configurationKey = "free",
            captureSnapshot = { snapshot },
            restoreSnapshot = {}
        )
        state.onCropperReady()

        assertEquals(
            IntSize(1080, 1920),
            state.preferredContainerSize("image", IntSize(1080, 1970))
        )
        assertEquals(
            IntSize(1920, 1080),
            state.preferredContainerSize("image", IntSize(1920, 1080))
        )
    }

    @Test
    fun newAttachmentCapturesOutgoingCropperBeforeReplacingCallbacks() {
        val initial = snapshot(0.1f)
        val outgoing = snapshot(0.35f)
        val incomingInitial = snapshot(0.8f)
        val state = ImageCropperState()
        val firstAttachmentKey = Any()
        val secondAttachmentKey = Any()
        var renderedSnapshot = initial
        var restoredSnapshot: ImageCropSnapshot? = null

        state.attach(
            attachmentKey = firstAttachmentKey,
            imageKey = "image",
            layoutKey = IntSize(1080, 1920),
            configurationKey = "same-config",
            captureSnapshot = { renderedSnapshot },
            restoreSnapshot = {}
        )
        state.onCropperReady()
        state.beginTransformation()
        renderedSnapshot = outgoing

        state.attach(
            attachmentKey = secondAttachmentKey,
            imageKey = "image",
            layoutKey = IntSize(1920, 1080),
            configurationKey = "same-config",
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
        val initial = snapshot(0.1f)
        val edited = snapshot(0.35f)
        val temporaryLayout = snapshot(0.8f)
        val state = ImageCropperState()
        val firstAttachmentKey = Any()
        val secondAttachmentKey = Any()
        var renderedSnapshot = initial
        var restoredSnapshot: ImageCropSnapshot? = null

        state.attach(
            attachmentKey = firstAttachmentKey,
            imageKey = "image",
            layoutKey = IntSize(1080, 1920),
            configurationKey = "same-config",
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
            layoutKey = IntSize(1920, 1080),
            configurationKey = "same-config",
            captureSnapshot = { temporaryLayout },
            restoreSnapshot = { restoredSnapshot = it }
        )
        state.onCropperReady()

        assertEquals(edited, restoredSnapshot)
    }

    @Test
    fun layoutSnapshotFromOutgoingCropperWinsOverTemporaryAttachmentGeometry() {
        val initial = snapshot(0.1f)
        val outgoing = snapshot(0.35f)
        val temporaryLayout = snapshot(0.8f)
        val state = ImageCropperState()
        val firstAttachmentKey = Any()
        val secondAttachmentKey = Any()
        var restoredSnapshot: ImageCropSnapshot? = null

        state.attach(
            attachmentKey = firstAttachmentKey,
            imageKey = "image",
            layoutKey = IntSize(1080, 1920),
            configurationKey = "same-config",
            captureSnapshot = { initial },
            restoreSnapshot = {}
        )
        state.onCropperReady()
        state.captureLayoutSnapshot(firstAttachmentKey, outgoing)

        state.attach(
            attachmentKey = secondAttachmentKey,
            imageKey = "image",
            layoutKey = IntSize(1920, 1080),
            configurationKey = "same-config",
            captureSnapshot = { temporaryLayout },
            restoreSnapshot = { restoredSnapshot = it }
        )
        state.onCropperReady()

        assertEquals(outgoing, restoredSnapshot)
    }

    @Test
    fun syncedSnapshotIsRestoredAfterAspectRatioChangeAndReattachment() {
        val beforeAspectRatioChange = snapshot(0.1f)
        val afterAspectRatioChange = snapshot(0.35f)
        val state = ImageCropperState()
        val firstAttachmentKey = Any()
        val secondAttachmentKey = Any()
        var renderedSnapshot = beforeAspectRatioChange
        var restoredSnapshot: ImageCropSnapshot? = null

        state.attach(
            attachmentKey = firstAttachmentKey,
            imageKey = "image",
            layoutKey = IntSize(1080, 1920),
            configurationKey = "same-config",
            captureSnapshot = { renderedSnapshot },
            restoreSnapshot = {}
        )
        state.onCropperReady()

        renderedSnapshot = afterAspectRatioChange
        state.syncSnapshot()
        state.detach(firstAttachmentKey)
        state.attach(
            attachmentKey = secondAttachmentKey,
            imageKey = "image",
            layoutKey = IntSize(1920, 1080),
            configurationKey = "same-config",
            captureSnapshot = { snapshot(0.8f) },
            restoreSnapshot = { restoredSnapshot = it }
        )
        state.onCropperReady()

        assertEquals(afterAspectRatioChange, restoredSnapshot)
    }

    @Test
    fun relativeSnapshotKeepsImageCropAcrossOrientationChange() {
        val snapshot = ImageCropSnapshot(
            normalizedCropRect = Rect(0.1f, 0.2f, 0.7f, 0.8f),
            rotation = 0f
        )
        val imageSize = IntSize(4000, 3000)

        val portrait = calculateRestoreGeometry(
            snapshot = snapshot,
            imageSize = imageSize,
            containerSize = IntSize(1080, 1920),
            drawAreaSize = IntSize(1080, 810)
        )
        val landscape = calculateRestoreGeometry(
            snapshot = snapshot,
            imageSize = imageSize,
            containerSize = IntSize(1920, 1080),
            drawAreaSize = IntSize(1440, 1080)
        )

        assertNormalizedCropEquals(
            expected = snapshot.normalizedCropRect,
            actual = requireNotNull(portrait).normalizedCropRect(
                containerSize = IntSize(1080, 1920),
                drawAreaSize = IntSize(1080, 810)
            )
        )
        assertNormalizedCropEquals(
            expected = snapshot.normalizedCropRect,
            actual = requireNotNull(landscape).normalizedCropRect(
                containerSize = IntSize(1920, 1080),
                drawAreaSize = IntSize(1440, 1080)
            )
        )
        assertTrue(portrait.overlayRect in IntSize(1080, 1920))
        assertTrue(landscape.overlayRect in IntSize(1920, 1080))
        assertEquals(540f, portrait.overlayRect.center.x, 0.0001f)
        assertEquals(960f, portrait.overlayRect.center.y, 0.0001f)
        assertEquals(960f, landscape.overlayRect.center.x, 0.0001f)
        assertEquals(540f, landscape.overlayRect.center.y, 0.0001f)
    }

    @Test
    fun repeatedOrientationChangesDoNotDriftCrop() {
        val expected = Rect(0.07f, 0.18f, 0.43f, 0.82f)
        val snapshot = ImageCropSnapshot(expected, rotation = 0f)
        val imageSize = IntSize(3000, 4000)
        val layouts = listOf(
            IntSize(1080, 1920) to IntSize(1080, 1440),
            IntSize(1920, 1080) to IntSize(810, 1080),
            IntSize(1080, 1920) to IntSize(1080, 1440),
            IntSize(1920, 1080) to IntSize(810, 1080)
        )

        layouts.forEach { (containerSize, drawAreaSize) ->
            val restored = requireNotNull(
                calculateRestoreGeometry(
                    snapshot = snapshot,
                    imageSize = imageSize,
                    containerSize = containerSize,
                    drawAreaSize = drawAreaSize
                )
            )
            assertNormalizedCropEquals(
                expected = expected,
                actual = restored.normalizedCropRect(containerSize, drawAreaSize)
            )
            assertTrue(restored.overlayRect in containerSize)
        }
    }

    @Test
    fun orientationChangeKeepsFrameWithinViewport() {
        val snapshot = ImageCropSnapshot(
            normalizedCropRect = Rect(0.25f, 0.25f, 0.5f, 0.5f),
            rotation = 0f
        )

        val restored = requireNotNull(
            calculateRestoreGeometry(
                snapshot = snapshot,
                imageSize = IntSize(4000, 3000),
                containerSize = IntSize(1920, 1080),
                drawAreaSize = IntSize(1440, 1080)
            )
        )

        assertTrue(restored.overlayRect in IntSize(1920, 1080))
        assertNormalizedCropEquals(
            expected = snapshot.normalizedCropRect,
            actual = restored.normalizedCropRect(
                containerSize = IntSize(1920, 1080),
                drawAreaSize = IntSize(1440, 1080)
            )
        )
    }

    @Test
    fun sameViewportRestoresExactTransform() {
        val imageSize = IntSize(1200, 900)
        val containerSize = IntSize(1080, 1600)
        val drawAreaSize = IntSize(1080, 810)
        val exactOverlayRect = Rect(56f, 700f, 1024f, 1038f)
        val exactPan = Offset(18f, -74f)
        val snapshot = ImageCropSnapshot(
            normalizedCropRect = Rect(0.12f, 0.23f, 0.81f, 0.67f),
            containerSize = containerSize,
            rotation = 0f,
            imageSize = imageSize,
            drawAreaSize = drawAreaSize,
            exactOverlayRect = exactOverlayRect,
            exactZoom = 1.73f,
            exactPan = exactPan
        )

        val restored = requireNotNull(
            calculateRestoreGeometry(
                snapshot = snapshot,
                imageSize = imageSize,
                containerSize = containerSize,
                drawAreaSize = drawAreaSize
            )
        )

        assertEquals(exactOverlayRect, restored.overlayRect)
        assertEquals(1.73f, restored.zoom, 0f)
        assertEquals(exactPan, restored.pan)
    }

    @Test
    fun exactTransformCompensatesContainerCenterChange() {
        val snapshot = ImageCropSnapshot(
            normalizedCropRect = Rect(0.12f, 0.23f, 0.81f, 0.67f),
            containerSize = IntSize(1080, 1600),
            rotation = 0f,
            drawAreaSize = IntSize(1080, 810),
            exactOverlayRect = Rect(56f, 700f, 1024f, 1038f),
            exactZoom = 1.73f,
            exactPan = Offset(18f, -74f),
            preferExactTransform = true
        )

        val restored = requireNotNull(
            calculateRestoreGeometry(
                snapshot = snapshot,
                imageSize = IntSize(1200, 900),
                containerSize = IntSize(1080, 1888),
                drawAreaSize = IntSize(1080, 810)
            )
        )

        assertEquals(Rect(56f, 844f, 1024f, 1182f), restored.overlayRect)
        assertEquals(1.73f, restored.zoom, 0f)
        assertEquals(Offset(18f, -74f), restored.pan)
    }

    @Test
    fun orientationRoundTripKeepsOverlayPlacement() {
        val snapshot = ImageCropSnapshot(
            normalizedCropRect = Rect(0.1f, 0.2f, 0.7f, 0.8f),
            normalizedOverlayCenter = Offset(0.6f, 0.6f),
            overlaySizeFraction = 0.65f,
            rotation = 0f
        )

        val restored = requireNotNull(
            calculateRestoreGeometry(
                snapshot = snapshot,
                imageSize = IntSize(4000, 3000),
                containerSize = IntSize(1080, 1920),
                drawAreaSize = IntSize(1080, 810)
            )
        )

        assertEquals(648f, restored.overlayRect.center.x, 0.0001f)
        assertEquals(1152f, restored.overlayRect.center.y, 0.0001f)
        assertEquals(702f, restored.overlayRect.width, 0.001f)
    }

    @Test
    fun sameWidthLayoutChangeKeepsAbsoluteOverlayCenter() {
        val snapshot = ImageCropSnapshot(
            normalizedCropRect = Rect(0.25f, 0.2f, 0.5f, 0.8f),
            normalizedOverlayCenter = Offset(0.5f, 0.5f),
            overlaySizeFraction = 0.5f,
            containerSize = IntSize(1080, 1920),
            rotation = 0f
        )

        val restored = requireNotNull(
            calculateRestoreGeometry(
                snapshot = snapshot,
                imageSize = IntSize(4000, 3000),
                containerSize = IntSize(1080, 1970),
                drawAreaSize = IntSize(1080, 810)
            )
        )

        assertEquals(540f, restored.overlayRect.center.x, 0.0001f)
        assertEquals(960f, restored.overlayRect.center.y, 0.0001f)
    }

    private fun snapshot(left: Float) = ImageCropSnapshot(
        normalizedCropRect = Rect(left, 0f, left + 0.1f, 0.1f),
        rotation = 0f
    )

    private fun com.t8rin.cropper.state.RestoredCropGeometry.normalizedCropRect(
        containerSize: IntSize,
        drawAreaSize: IntSize
    ): Rect {
        val drawSize = Size(drawAreaSize.width * zoom, drawAreaSize.height * zoom)
        val drawCenter = Offset(
            x = containerSize.width / 2f + pan.x,
            y = containerSize.height / 2f + pan.y
        )
        val drawRect = Rect(
            offset = drawCenter - Offset(drawSize.width / 2f, drawSize.height / 2f),
            size = drawSize
        )
        return Rect(
            left = (overlayRect.left - drawRect.left) / drawRect.width,
            top = (overlayRect.top - drawRect.top) / drawRect.height,
            right = (overlayRect.right - drawRect.left) / drawRect.width,
            bottom = (overlayRect.bottom - drawRect.top) / drawRect.height
        )
    }

    private fun assertNormalizedCropEquals(expected: Rect, actual: Rect) {
        assertEquals(expected.left, actual.left, 0.0001f)
        assertEquals(expected.top, actual.top, 0.0001f)
        assertEquals(expected.right, actual.right, 0.0001f)
        assertEquals(expected.bottom, actual.bottom, 0.0001f)
    }

    private operator fun IntSize.contains(rect: Rect): Boolean =
        rect.left >= -0.001f &&
                rect.top >= -0.001f &&
                rect.right <= width + 0.001f &&
                rect.bottom <= height + 0.001f
}
