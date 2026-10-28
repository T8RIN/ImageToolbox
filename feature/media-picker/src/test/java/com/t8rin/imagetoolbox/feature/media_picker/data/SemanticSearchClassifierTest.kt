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

package com.t8rin.imagetoolbox.feature.media_picker.data

import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sqrt

class SemanticSearchClassifierTest {

    @Test
    fun aStrongBackgroundMatchRejectsFalsePeopleAndAnimals() {
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                SemanticSearchCategory.BuiltIn.People to listOf(description(0.42f)),
                SemanticSearchCategory.BuiltIn.Animals to listOf(description(0.45f))
            ),
            backgroundEmbeddings = listOf(description(-0.1f), description(0.65f))
        )

        assertTrue(classifier.classify(axis(0)).isEmpty())
    }

    @Test
    fun oneRelevantDescriptionRetainsTheSubjectWithoutAveragingItAway() {
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                SemanticSearchCategory.BuiltIn.People to listOf(
                    description(0.01f),
                    description(0.41f)
                ),
                SemanticSearchCategory.BuiltIn.Animals to listOf(
                    description(0.01f),
                    description(0.43f)
                )
            ),
            backgroundEmbeddings = listOf(description(0.18f))
        )

        assertEquals(
            setOf(SemanticSearchCategory.BuiltIn.People, SemanticSearchCategory.BuiltIn.Animals),
            classifier.classify(axis(0)).toSet()
        )
    }

    @Test
    fun ambiguousForegroundAndBackgroundScoresDoNotCreateSubjectCategories() {
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                SemanticSearchCategory.BuiltIn.People to listOf(description(0.45f)),
                SemanticSearchCategory.BuiltIn.Animals to listOf(description(0.45f))
            ),
            backgroundEmbeddings = listOf(description(0.445f))
        )

        assertTrue(classifier.classify(axis(0)).isEmpty())
    }

    @Test
    fun aPersonWithAPetCanBelongToBothCategories() {
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                SemanticSearchCategory.BuiltIn.People to listOf(axis(0)),
                SemanticSearchCategory.BuiltIn.Animals to listOf(axis(1))
            ),
            backgroundEmbeddings = listOf(axis(2))
        )
        val image = FloatArray(512).apply {
            this[0] = 0.6f
            this[1] = 0.8f
        }

        assertEquals(
            setOf(SemanticSearchCategory.BuiltIn.People, SemanticSearchCategory.BuiltIn.Animals),
            classifier.classify(image).toSet()
        )
    }

    @Test
    fun aWeakHumanMatchDoesNotMislabelAnAnimalFaceAsPeople() {
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                SemanticSearchCategory.BuiltIn.People to listOf(description(0.26f)),
                SemanticSearchCategory.BuiltIn.Animals to listOf(description(0.31f))
            ),
            backgroundEmbeddings = listOf(description(0.1f))
        )

        assertEquals(listOf(SemanticSearchCategory.BuiltIn.Animals), classifier.classify(axis(0)))
    }

    @Test
    fun similarlySupportedPeopleAndAnimalsRemainIndependentMatches() {
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                SemanticSearchCategory.BuiltIn.People to listOf(description(0.26f)),
                SemanticSearchCategory.BuiltIn.Animals to listOf(description(0.27f))
            ),
            backgroundEmbeddings = listOf(description(0.1f))
        )

        assertEquals(
            setOf(SemanticSearchCategory.BuiltIn.People, SemanticSearchCategory.BuiltIn.Animals),
            classifier.classify(axis(0)).toSet()
        )
    }

    @Test
    fun abstractArtworkCanStillBelongToArtWhileSubjectCategoriesAreRejected() {
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                SemanticSearchCategory.BuiltIn.People to listOf(description(0.4f)),
                SemanticSearchCategory.BuiltIn.Art to listOf(description(0.45f))
            ),
            backgroundEmbeddings = listOf(description(0.9f))
        )

        assertEquals(listOf(SemanticSearchCategory.BuiltIn.Art), classifier.classify(axis(0)))
    }

    @Test
    fun beatingAnUnrelatedBackgroundDoesNotMakeAWeakSubjectMatchRelevant() {
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                SemanticSearchCategory.BuiltIn.People to listOf(description(0.1f)),
                SemanticSearchCategory.BuiltIn.Animals to listOf(description(0.1f))
            ),
            backgroundEmbeddings = listOf(description(-0.2f))
        )

        assertTrue(classifier.classify(axis(0)).isEmpty())
    }

    @Test
    fun categoryThresholdsChangeMatchesWithoutChangingOtherCategories() {
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                SemanticSearchCategory.BuiltIn.Nature to listOf(description(0.22f)),
                SemanticSearchCategory.BuiltIn.Food to listOf(description(0.35f)),
                SemanticSearchCategory.BuiltIn.Art to listOf(description(0.3f))
            ),
            backgroundEmbeddings = listOf(description(0.1f)),
            thresholds = mapOf(
                SemanticSearchCategory.BuiltIn.Nature.id to 0.2f,
                SemanticSearchCategory.BuiltIn.Food.id to 0.4f
            )
        )

        assertEquals(
            listOf(SemanticSearchCategory.BuiltIn.Nature, SemanticSearchCategory.BuiltIn.Art),
            classifier.classify(axis(0))
        )
    }

    @Test
    fun relaxedSubjectThresholdsStillRejectAbstractBackgrounds() {
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                SemanticSearchCategory.BuiltIn.People to listOf(description(0.23f)),
                SemanticSearchCategory.BuiltIn.Animals to listOf(description(0.24f))
            ),
            backgroundEmbeddings = listOf(description(0.5f)),
            thresholds = mapOf(
                SemanticSearchCategory.BuiltIn.People.id to 0.15f,
                SemanticSearchCategory.BuiltIn.Animals.id to 0.15f
            )
        )

        assertTrue(classifier.classify(axis(0)).isEmpty())
    }

    @Test
    fun customCategoriesUseTheirOwnThresholdAndMayDescribeAbstractImages() {
        val matching = SemanticSearchCategory.Custom("custom-1", "Patterns", "colorful patterns")
        val strict = matching.copy(id = "custom-2", name = "Strict patterns")
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                matching to listOf(description(0.3f)),
                strict to listOf(description(0.3f))
            ),
            backgroundEmbeddings = listOf(description(0.9f)),
            thresholds = mapOf(matching.id to 0.25f, strict.id to 0.4f)
        )

        assertEquals(listOf(matching), classifier.classify(axis(0)))
    }

    @Test
    fun invalidThresholdsFallBackToCategoryDefaults() {
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                SemanticSearchCategory.BuiltIn.People to listOf(description(0.255f)),
                SemanticSearchCategory.BuiltIn.Nature to listOf(description(0.255f))
            ),
            backgroundEmbeddings = listOf(description(0.1f)),
            thresholds = mapOf(
                SemanticSearchCategory.BuiltIn.People.id to Float.NaN,
                SemanticSearchCategory.BuiltIn.Nature.id to Float.POSITIVE_INFINITY
            )
        )

        assertEquals(listOf(SemanticSearchCategory.BuiltIn.People), classifier.classify(axis(0)))
    }

    @Test
    fun referenceOnlyCategoriesMatchAnyPhotoWithoutAveragingDifferentSubjects() {
        val category = SemanticSearchCategory.Custom("photos", "Photos", "")
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(category to emptyList()),
            backgroundEmbeddings = emptyList(),
            referenceEmbeddings = mapOf(category.id to listOf(axis(0), axis(1)))
        )

        assertEquals(listOf(category), classifier.classify(axis(0)))
        assertEquals(listOf(category), classifier.classify(axis(1)))
        assertTrue(classifier.classify(axis(2)).isEmpty())
    }

    @Test
    fun textAndPhotoReferencesMatchIndependentlyWithDifferentThresholds() {
        val category = SemanticSearchCategory.Custom("mixed", "Mixed", "a cat")
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(category to listOf(description(0.3f))),
            backgroundEmbeddings = emptyList(),
            thresholds = mapOf(category.id to 0.26f),
            referenceEmbeddings = mapOf(category.id to listOf(axis(1))),
            referenceThresholds = mapOf(category.id to 0.8f)
        )

        assertEquals(listOf(category), classifier.classify(axis(0)))
        assertEquals(listOf(category), classifier.classify(axis(1)))
        assertTrue(classifier.classify(FloatArray(512).apply { this[1] = 0.79f }).isEmpty())
    }

    @Test
    fun missingReferencesDoNotBreakTextOrCreateReferenceOnlyMatches() {
        val text = SemanticSearchCategory.Custom("text", "Text", "a cat")
        val photo = SemanticSearchCategory.Custom("photo", "Photo", "")
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(text to listOf(description(0.4f)), photo to emptyList()),
            backgroundEmbeddings = emptyList(),
            referenceEmbeddings = mapOf(text.id to emptyList(), photo.id to emptyList())
        )

        assertEquals(listOf(text), classifier.classify(axis(0)))
    }

    @Test
    fun photoReferencesDoNotUseTheLowerTextThreshold() {
        val category = SemanticSearchCategory.Custom("photo", "Photo", "")
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(category to emptyList()),
            backgroundEmbeddings = emptyList(),
            thresholds = mapOf(category.id to 0.15f),
            referenceEmbeddings = mapOf(category.id to listOf(description(0.5f))),
            referenceThresholds = mapOf(category.id to Float.NaN)
        )

        assertTrue(classifier.classify(axis(0)).isEmpty())
    }

    private fun description(similarity: Float) = FloatArray(512).apply {
        this[0] = similarity
        this[511] = sqrt(1f - similarity * similarity)
    }

    private fun axis(index: Int) = FloatArray(512).apply { this[index] = 1f }
}