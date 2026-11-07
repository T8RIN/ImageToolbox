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

package com.t8rin.imagetoolbox.collage_maker.data.project

import com.t8rin.collages.model.CollageDesign
import com.t8rin.collages.model.CollageImageState
import com.t8rin.collages.model.CollagePoint
import com.t8rin.collages.model.CollageSlot
import com.t8rin.collages.model.CollageSlotStyle
import com.t8rin.collages.model.CollageState
import com.t8rin.imagetoolbox.collage_maker.domain.CollageParams
import com.t8rin.imagetoolbox.collage_maker.domain.CollageProject
import com.t8rin.imagetoolbox.collage_maker.domain.CollageProjectSnapshot
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.domain.model.GradientFill
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class CollageProjectMapperTest {

    @Test
    fun customLayoutAndPhotoStylesSurviveProjectRoundTrip() {
        val design = CollageDesign(
            slots = listOf(
                CollageSlot(
                    listOf(
                        CollagePoint(0f, 0f),
                        CollagePoint(1f, 0f),
                        CollagePoint(0f, 1f)
                    )
                ),
                CollageSlot(
                    listOf(
                        CollagePoint(1f, 0f),
                        CollagePoint(1f, 1f),
                        CollagePoint(0f, 1f)
                    )
                )
            )
        )
        val original = snapshot("first", "second")
        val current = original.copy(
            layoutId = "custom_test",
            collageState = original.collageState!!.copy(layoutId = "custom_test", design = design),
            params = CollageParams(
                outerPadding = 24f, borderWidth = 12f, shadowRadius = 8f,
                shadowColor = 0x88223344.toInt(),
                borderGradient = GradientFill(angle = 90f),
                slotBorderGradients = listOf(GradientFill(angle = 45f), null),
                slotStyles = listOf(
                    CollageSlotStyle(
                        cornerRadius = 32f,
                        borderWidth = 5f,
                        fit = true,
                        shadowColor = 0x88775533.toInt()
                    )
                )
            )
        )
        val project = CollageProject(current, listOf(original), emptyList())
        assertEquals(
            project,
            project.toFile(linkedMapOf()).toProject(mapOf(0 to "first", 1 to "second"))
        )
    }

    @Test
    fun historySharesAssetsAndRestoresTheirNewUris() {
        val current = snapshot("first", "second")
        val previous = snapshot("second", "first")
        val undone = snapshot("first", "third")
        val project = CollageProject(current, listOf(previous), listOf(undone))
        val assets = linkedMapOf<String, Int>()
        val file = project.toFile(assets)

        assertEquals(1, file.version)
        assertEquals(mapOf("first" to 0, "second" to 1, "third" to 2), assets)
        file.current.state!!.images.forEach { assertNull(it.uri) }

        val restored = file.toProject(mapOf(0 to "new-first", 1 to "new-second", 2 to "new-third"))
        assertEquals(listOf("new-first", "new-second"), restored.current.uris)
        assertEquals(listOf("new-second", "new-first"), restored.history.single().uris)
        assertEquals(listOf("new-first", "new-third"), restored.redoHistory.single().uris)
        assertEquals(
            restored.history.single().uris,
            restored.history.single().collageState!!.images.map { it.uri }
        )
        assertEquals(
            current.collageState!!.images.map { it.matrixValues },
            restored.current.collageState!!.images.map { it.matrixValues }
        )
    }

    @Test
    fun emptyAndResetHistoryStatesSurviveRoundTrip() {
        val current = snapshot("first")
        val empty = current.copy(uris = emptyList(), layoutId = null, collageState = null)
        val reset = current.copy(collageState = null)
        val project = CollageProject(current, listOf(empty, reset), emptyList())
        val restored = project.toFile(linkedMapOf()).toProject(mapOf(0 to "first"))
        assertEquals(project, restored)
    }

    @Test
    fun aspectRatiosKeepTheirTypeAndSavedFlag() {
        val ratios = listOf(
            DomainAspectRatio.Free,
            DomainAspectRatio.Original,
            DomainAspectRatio.Numeric(3f, 2f),
            DomainAspectRatio.Custom(7f, 5f, true)
        )
        ratios.forEach { ratio ->
            val current = snapshot("first").copy(aspectRatio = ratio)
            val project = CollageProject(current, emptyList(), emptyList())
            assertEquals(project, project.toFile(linkedMapOf()).toProject(mapOf(0 to "first")))
        }
    }

    @Test
    fun missingAssetsAndDuplicateImageIndicesAreRejected() {
        val file = CollageProject(snapshot("first", "second"), emptyList(), emptyList())
            .toFile(linkedMapOf())
        assertThrows(IllegalArgumentException::class.java) {
            file.toProject(mapOf(0 to "first"))
        }
        val state = file.current.state!!
        val invalid = file.copy(
            current = file.current.copy(
                state = state.copy(images = state.images.map { it.copy(index = 0) })
            )
        )
        assertThrows(IllegalArgumentException::class.java) {
            invalid.toProject(mapOf(0 to "first", 1 to "second"))
        }
    }

    @Test
    fun unsupportedVersionsAreRejected() {
        val file = CollageProject(snapshot("first"), emptyList(), emptyList())
            .toFile(linkedMapOf()).copy(version = 2)
        assertThrows(IllegalArgumentException::class.java) {
            file.toProject(mapOf(0 to "first"))
        }
    }

    private fun snapshot(vararg uris: String) = CollageProjectSnapshot(
        uris = uris.toList(),
        layoutId = "collage_${uris.size}_0",
        collageState = CollageState(
            layoutId = "collage_${uris.size}_0",
            images = uris.mapIndexed { index, uri ->
                CollageImageState(
                    index = index,
                    uri = uri,
                    matrixValues = listOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f),
                    viewWidth = 100f,
                    viewHeight = 100f,
                    userAllowedEmptySpace = false
                )
            },
            layoutParams = emptyList()
        ),
        aspectRatio = DomainAspectRatio.Numeric(1f, 1f),
        backgroundColor = -1,
        backgroundGradient = null,
        params = CollageParams(),
        imageFormat = ImageFormat.Png.Lossless,
        quality = Quality.Base()
    )
}