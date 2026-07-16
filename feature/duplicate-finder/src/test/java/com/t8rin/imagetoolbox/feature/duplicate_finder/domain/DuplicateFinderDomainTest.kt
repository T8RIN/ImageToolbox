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

package com.t8rin.imagetoolbox.feature.duplicate_finder.domain

import com.t8rin.imagetoolbox.core.data.saving.io.ByteArrayReadable
import com.t8rin.imagetoolbox.core.data.saving.io.StreamReadable
import com.t8rin.imagetoolbox.core.data.utils.computeFromReadable
import com.t8rin.imagetoolbox.core.domain.model.HashingType
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.helper.DHash
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.helper.DuplicateGrouping
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateItem
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateKeepStrategy
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream

class DuplicateFinderDomainTest {

    @Test
    fun identicalImagesHaveIdenticalDHash() {
        val pixels = ascendingPixels()

        assertEquals(DHash.calculate(pixels), DHash.calculate(pixels.copyOf()))
    }

    @Test
    fun smallChangeHasSmallHammingDistance() {
        val original = ascendingPixels()
        val changed = original.copyOf().apply {
            this[0] = 2
            this[1] = 1
        }

        assertEquals(1, DHash.hammingDistance(DHash.calculate(original), DHash.calculate(changed)))
    }

    @Test
    fun completelyDifferentImagesAreNotGroupedAtDefaultSensitivity() {
        val first = item(uri = "first", sha256 = "a", dHash = 0L)
        val second = item(uri = "second", sha256 = "b", dHash = -1L)

        assertTrue(DuplicateGrouping.regroup(listOf(first, second)).isEmpty())
    }

    @Test
    fun exactCopiesAreDeterminedBySha256() = runBlocking {
        val bytes = "same image bytes".encodeToByteArray()
        val firstHash = HashingType.SHA_256.computeFromReadable(ByteArrayReadable(bytes))
        val secondHash = HashingType.SHA_256.computeFromReadable(ByteArrayReadable(bytes.copyOf()))
        val differentHash =
            HashingType.SHA_256.computeFromReadable(StreamReadable(ByteArrayInputStream("different".encodeToByteArray())))

        assertEquals(firstHash, secondHash)
        assertNotEquals(firstHash, differentHash)
        val groups = DuplicateGrouping.regroup(
            listOf(
                item(uri = "first", sha256 = firstHash, dHash = 0L),
                item(uri = "second", sha256 = secondHash, dHash = -1L)
            )
        )
        assertEquals(DuplicateType.Exact, groups.single().type)
    }

    @Test
    fun recommendedItemIsDeterministic() {
        val older = item(
            uri = "older",
            sha256 = "a",
            dHash = 0L,
            width = 200,
            height = 200,
            sizeBytes = 2_000,
            lastModified = 100,
            sourceIndex = 0
        )
        val newer = older.copy(uri = "newer", sha256 = "b", lastModified = 200, sourceIndex = 1)
        val sameAsNewerButLater = newer.copy(uri = "later", sha256 = "c", sourceIndex = 2)

        assertEquals(
            "newer",
            DuplicateGrouping.recommendedItem(listOf(sameAsNewerButLater, older, newer)).uri
        )
    }

    @Test
    fun keepStrategiesChooseExpectedItems() {
        val first = item(
            uri = "first",
            sha256 = "a",
            dHash = 0L,
            width = 100,
            height = 100,
            sizeBytes = 2_000,
            lastModified = 200,
            sourceIndex = 0
        )
        val smallestNewest = item(
            uri = "smallest-newest",
            sha256 = "b",
            dHash = 0L,
            width = 200,
            height = 200,
            sizeBytes = 1_000,
            lastModified = 300,
            sourceIndex = 1
        )
        val bestOldest = item(
            uri = "best-oldest",
            sha256 = "c",
            dHash = 0L,
            width = 300,
            height = 300,
            sizeBytes = 3_000,
            lastModified = 100,
            sourceIndex = 2
        )
        val items = listOf(bestOldest, smallestNewest, first)

        assertEquals(
            "best-oldest",
            DuplicateGrouping.recommendedItem(
                items,
                DuplicateKeepStrategy.BestQuality
            ).uri
        )
        assertEquals(
            "smallest-newest",
            DuplicateGrouping.recommendedItem(
                items,
                DuplicateKeepStrategy.SmallestFile
            ).uri
        )
        assertEquals(
            "smallest-newest",
            DuplicateGrouping.recommendedItem(items, DuplicateKeepStrategy.Newest).uri
        )
        assertEquals(
            "best-oldest",
            DuplicateGrouping.recommendedItem(items, DuplicateKeepStrategy.Oldest).uri
        )
        assertEquals(
            "first",
            DuplicateGrouping.recommendedItem(
                items,
                DuplicateKeepStrategy.FirstSelected
            ).uri
        )
    }

    @Test
    fun regroupAppliesKeepStrategyToGroups() {
        val group = DuplicateGrouping.regroup(
            items = listOf(
                item(uri = "large", sha256 = "same", dHash = 0L, sizeBytes = 2_000),
                item(uri = "small", sha256 = "same", dHash = 0L, sizeBytes = 1_000)
            ),
            keepStrategy = DuplicateKeepStrategy.SmallestFile
        ).single()

        assertEquals("small", group.recommendedUri)
    }

    @Test
    fun selectAllNeverSelectsRecommendedItem() {
        val group = DuplicateGrouping.regroup(
            listOf(
                item(uri = "best", sha256 = "same", dHash = 0L, sourceIndex = 0),
                item(uri = "copy", sha256 = "same", dHash = 0L, sourceIndex = 1)
            )
        ).single()

        val selection = DuplicateGrouping.selectAllDuplicates(listOf(group))

        assertFalse(group.recommendedUri in selection)
        assertEquals(setOf("copy"), selection)
    }

    @Test
    fun sensitivityIsClampedAndInclusiveAtBoundary() {
        assertEquals(0, DuplicateGrouping.normalizeSensitivity(-1))
        assertEquals(
            DuplicateGrouping.MAX_SENSITIVITY,
            DuplicateGrouping.normalizeSensitivity(99)
        )

        val first = item(uri = "first", sha256 = "a", dHash = 0L)
        val second = item(
            uri = "second",
            sha256 = "b",
            dHash = (1L shl DuplicateGrouping.MAX_SENSITIVITY) - 1
        )
        assertTrue(
            DuplicateGrouping.regroup(
                listOf(first, second),
                sensitivity = DuplicateGrouping.MAX_SENSITIVITY - 1
            ).isEmpty()
        )
        assertEquals(
            DuplicateType.Similar,
            DuplicateGrouping.regroup(
                listOf(first, second),
                sensitivity = DuplicateGrouping.MAX_SENSITIVITY
            ).single().type
        )
    }

    @Test
    fun sensitivityZeroDoesNotTurnVisualMatchIntoExactCopy() {
        val groups = DuplicateGrouping.regroup(
            listOf(
                item(uri = "first", sha256 = "a", dHash = 42L),
                item(uri = "second", sha256 = "b", dHash = 42L)
            ),
            sensitivity = 0
        )

        assertEquals(DuplicateType.Similar, groups.single().type)
    }

    @Test
    fun completeLinkGroupingPreventsTransitiveMerge() {
        val first = item(uri = "a", sha256 = "a", dHash = 0L)
        val bridge = item(uri = "b", sha256 = "b", dHash = 0b11_1111L)
        val distant = item(uri = "c", sha256 = "c", dHash = 0b11_1111_1111_11L)

        val groups = DuplicateGrouping.regroup(
            items = listOf(first, bridge, distant),
            sensitivity = DuplicateGrouping.DEFAULT_SENSITIVITY
        )

        assertEquals(setOf("a", "b"), groups.single().items.mapTo(mutableSetOf()) { it.uri })
        assertFalse(groups.single().items.any { it.uri == "c" })
    }

    private fun ascendingPixels(): IntArray = IntArray(DHash.PIXEL_COUNT) { index ->
        index % DHash.WIDTH
    }

    private fun item(
        uri: String,
        sha256: String,
        dHash: Long,
        width: Int = 100,
        height: Int = 100,
        sizeBytes: Long = 1_000,
        lastModified: Long? = 100,
        sourceIndex: Int = 0
    ) = DuplicateItem(
        uri = uri,
        name = "$uri.png",
        width = width,
        height = height,
        sizeBytes = sizeBytes,
        format = "PNG",
        lastModified = lastModified,
        sourceIndex = sourceIndex,
        sha256 = sha256,
        dHash = dHash
    )
}
