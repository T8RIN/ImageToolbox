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

package com.t8rin.imagetoolbox.feature.duplicate_finder.domain.helper

import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateGroup
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateItem
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateKeepStrategy
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateScanMode
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateType
import kotlin.math.abs
import kotlin.math.max

data object DuplicateGrouping {
    const val MIN_SENSITIVITY: Int = 0
    const val MAX_SENSITIVITY: Int = 16
    const val DEFAULT_SENSITIVITY: Int = 6

    private const val RATIO_TOLERANCE = 0.08

    val RecommendedItemComparator: Comparator<DuplicateItem> =
        compareByDescending<DuplicateItem> { it.pixelCount }
            .thenByDescending { it.sizeBytes }
            .thenByDescending { it.lastModified ?: Long.MIN_VALUE }
            .thenBy { it.sourceIndex }

    fun normalizeSensitivity(sensitivity: Int): Int =
        sensitivity.coerceIn(MIN_SENSITIVITY, MAX_SENSITIVITY)

    fun regroup(
        items: List<DuplicateItem>,
        sensitivity: Int = DEFAULT_SENSITIVITY,
        keepStrategy: DuplicateKeepStrategy = DuplicateKeepStrategy.BestQuality,
        scanMode: DuplicateScanMode = DuplicateScanMode.ExactAndSimilar
    ): List<DuplicateGroup> {
        val orderedItems = items.sortedWith(
            compareBy<DuplicateItem> { it.sourceIndex }.thenBy { it.uri }
        )
        val exactGroups = orderedItems
            .groupBy(DuplicateItem::sha256)
            .values
            .filter { it.size > 1 }
            .map { createGroup(DuplicateType.Exact, it, keepStrategy) }
        val exactUris = exactGroups
            .flatMapTo(mutableSetOf()) { group -> group.items.map(DuplicateItem::uri) }
        val similarGroups = if (scanMode == DuplicateScanMode.ExactAndSimilar) {
            groupSimilar(
                items = orderedItems.filterNot { it.uri in exactUris },
                sensitivity = normalizeSensitivity(sensitivity),
                keepStrategy = keepStrategy
            )
        } else emptyList()

        return exactGroups + similarGroups
    }

    fun recommendedItem(
        items: List<DuplicateItem>,
        keepStrategy: DuplicateKeepStrategy = DuplicateKeepStrategy.BestQuality
    ): DuplicateItem =
        requireNotNull(items.minWithOrNull(keepStrategy.comparator())) {
            "Cannot recommend an item from an empty list"
        }

    fun selectAllDuplicates(groups: List<DuplicateGroup>): Set<String> = buildSet {
        groups.forEach { group ->
            group.items.forEach { item ->
                if (item.uri != group.recommendedUri) add(item.uri)
            }
        }
    }

    fun selectAllExceptRecommended(groups: List<DuplicateGroup>): Set<String> =
        selectAllDuplicates(groups)

    fun clearSelection(): Set<String> = emptySet()

    private fun groupSimilar(
        items: List<DuplicateItem>,
        sensitivity: Int,
        keepStrategy: DuplicateKeepStrategy
    ): List<DuplicateGroup> {
        val clusters = mutableListOf<MutableList<DuplicateItem>>()

        items.forEach { candidate ->
            val target = clusters.firstOrNull { cluster ->
                cluster.all { existing ->
                    aspectRatiosAreCompatible(candidate, existing) &&
                            DHash.hammingDistance(candidate.dHash, existing.dHash) <= sensitivity
                }
            }
            if (target == null) {
                clusters += mutableListOf(candidate)
            } else {
                target += candidate
            }
        }

        return clusters
            .filter { it.size > 1 }
            .map { createGroup(DuplicateType.Similar, it, keepStrategy) }
    }

    private fun createGroup(
        type: DuplicateType,
        items: List<DuplicateItem>,
        keepStrategy: DuplicateKeepStrategy
    ): DuplicateGroup {
        val recommended = recommendedItem(items, keepStrategy)
        val itemsWithDistance = items.map { item ->
            item.copy(
                distance = if (type == DuplicateType.Exact) {
                    0
                } else {
                    DHash.hammingDistance(item.dHash, recommended.dHash)
                }
            )
        }
        return DuplicateGroup(
            type = type,
            items = itemsWithDistance,
            recommendedUri = recommended.uri
        )
    }

    private fun aspectRatiosAreCompatible(
        first: DuplicateItem,
        second: DuplicateItem
    ): Boolean {
        val firstRatio = first.aspectRatio
        val secondRatio = second.aspectRatio
        if (firstRatio <= 0.0 || secondRatio <= 0.0) return true

        return abs(firstRatio - secondRatio) / max(firstRatio, secondRatio) <= RATIO_TOLERANCE
    }

    private fun DuplicateKeepStrategy.comparator(): Comparator<DuplicateItem> = when (this) {
        DuplicateKeepStrategy.BestQuality -> RecommendedItemComparator
        DuplicateKeepStrategy.SmallestFile -> compareBy<DuplicateItem> { it.sizeBytes }
            .thenByDescending { it.pixelCount }
            .thenBy { it.sourceIndex }

        DuplicateKeepStrategy.Newest -> compareBy<DuplicateItem> { it.lastModified == null }
            .thenByDescending { it.lastModified ?: Long.MIN_VALUE }
            .then(RecommendedItemComparator)

        DuplicateKeepStrategy.Oldest -> compareBy<DuplicateItem> { it.lastModified == null }
            .thenBy { it.lastModified ?: Long.MAX_VALUE }
            .then(RecommendedItemComparator)

        DuplicateKeepStrategy.FirstSelected -> compareBy<DuplicateItem> { it.sourceIndex }
            .thenBy { it.uri }
    }
}
