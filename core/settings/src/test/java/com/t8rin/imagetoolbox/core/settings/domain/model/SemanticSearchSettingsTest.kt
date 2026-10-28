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

package com.t8rin.imagetoolbox.core.settings.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SemanticSearchSettingsTest {

    @Test
    fun nonFiniteThresholdsRestoreDefaultsWithoutOverridingBuiltInCategories() {
        val settings = SemanticSearchSettings(
            textThreshold = Float.NaN,
            imageThreshold = Float.POSITIVE_INFINITY,
            categoryThresholds = mapOf("People" to Float.NaN, "Animals" to 0.4f),
            customCategories = listOf(
                CustomSemanticSearchCategory(
                    "cats", "Cats", "A photo of a cat", Float.NEGATIVE_INFINITY
                )
            )
        ).normalized()

        assertEquals(SemanticSearchSettings.DefaultTextThreshold, settings.textThreshold, 0f)
        assertEquals(SemanticSearchSettings.DefaultImageThreshold, settings.imageThreshold, 0f)
        assertFalse(settings.categoryThresholds.containsKey("People"))
        assertEquals(0.4f, settings.categoryThresholds.getValue("Animals"), 0f)
        assertEquals(
            SemanticSearchSettings.DefaultTextThreshold,
            settings.customCategories.single().threshold,
            0f
        )
    }

    @Test
    fun importedThresholdsStayInSupportedRanges() {
        val settings = SemanticSearchSettings(
            textThreshold = -1f,
            imageThreshold = 4f,
            categoryThresholds = mapOf("People" to 2f, "Animals" to -2f),
            customCategories = listOf(
                CustomSemanticSearchCategory("cats", "Cats", "A photo of a cat", 2f)
            )
        ).normalized()

        assertEquals(0.15f, settings.textThreshold, 0f)
        assertEquals(0.95f, settings.imageThreshold, 0f)
        assertEquals(mapOf("People" to 0.8f, "Animals" to 0.15f), settings.categoryThresholds)
        assertEquals(0.8f, settings.customCategories.single().threshold, 0f)
    }

    @Test
    fun malformedCategoriesDoNotHideValidMultilingualCategories() {
        val category = CustomSemanticSearchCategory("cats", "Кошки", "Кошка на фото", 0.3f)
        val settings = SemanticSearchSettings(
            customCategories = listOf(
                category,
                category.copy(name = "Duplicate"),
                category.copy(id = ""),
                category.copy(id = "unnamed", name = " "),
                category.copy(id = "empty", description = " ")
            )
        ).normalized()

        assertEquals(listOf(category), settings.customCategories)
        assertEquals(settings, settings.normalized())
    }

    @Test
    fun referenceOnlyCategoriesKeepDistinctTrimmedPhotosAndIndependentThresholds() {
        val category = CustomSemanticSearchCategory(
            id = "photos",
            name = "  Любимые места  ",
            description = " ",
            referenceImages = listOf(
                " content://photo/1 ",
                "",
                "content://photo/1",
                "content://photo/2"
            ),
            referenceThreshold = 2f
        )
        val settings = SemanticSearchSettings(
            customCategories = listOf(
                category,
                category.copy(id = "empty", referenceImages = listOf(" "))
            )
        ).normalized()

        assertEquals(
            listOf(
                category.copy(
                    name = "Любимые места",
                    description = "",
                    referenceImages = listOf("content://photo/1", "content://photo/2"),
                    referenceThreshold = SemanticSearchSettings.ImageThresholdRange.endInclusive
                )
            ),
            settings.customCategories
        )
        assertEquals(settings, settings.normalized())
    }

    @Test
    fun existingTextCategoriesKeepDefaultsForNewReferenceFields() {
        val category = CustomSemanticSearchCategory("cats", "Cats", "a cat")
        val settings = SemanticSearchSettings(
            customCategories = listOf(category.copy(referenceThreshold = Float.NaN))
        ).normalized()

        assertEquals(emptyList<String>(), category.referenceImages)
        assertEquals(listOf(category), settings.customCategories)
        assertEquals(SemanticSearchSettings.DefaultImageThreshold, category.referenceThreshold, 0f)
    }
}