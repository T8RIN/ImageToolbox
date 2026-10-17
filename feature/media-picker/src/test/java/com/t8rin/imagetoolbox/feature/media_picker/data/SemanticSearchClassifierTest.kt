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
                SemanticSearchCategory.People to listOf(description(0.42f)),
                SemanticSearchCategory.Animals to listOf(description(0.45f))
            ),
            backgroundEmbeddings = listOf(description(-0.1f), description(0.65f))
        )

        assertTrue(classifier.classify(axis(0)).isEmpty())
    }

    @Test
    fun oneRelevantDescriptionRetainsTheSubjectWithoutAveragingItAway() {
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                SemanticSearchCategory.People to listOf(description(0.01f), description(0.41f)),
                SemanticSearchCategory.Animals to listOf(description(0.01f), description(0.43f))
            ),
            backgroundEmbeddings = listOf(description(0.18f))
        )

        assertEquals(
            setOf(SemanticSearchCategory.People, SemanticSearchCategory.Animals),
            classifier.classify(axis(0)).toSet()
        )
    }

    @Test
    fun ambiguousForegroundAndBackgroundScoresDoNotCreateSubjectCategories() {
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                SemanticSearchCategory.People to listOf(description(0.45f)),
                SemanticSearchCategory.Animals to listOf(description(0.45f))
            ),
            backgroundEmbeddings = listOf(description(0.445f))
        )

        assertTrue(classifier.classify(axis(0)).isEmpty())
    }

    @Test
    fun aPersonWithAPetCanBelongToBothCategories() {
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                SemanticSearchCategory.People to listOf(axis(0)),
                SemanticSearchCategory.Animals to listOf(axis(1))
            ),
            backgroundEmbeddings = listOf(axis(2))
        )
        val image = FloatArray(512).apply {
            this[0] = 0.6f
            this[1] = 0.8f
        }

        assertEquals(
            setOf(SemanticSearchCategory.People, SemanticSearchCategory.Animals),
            classifier.classify(image).toSet()
        )
    }

    @Test
    fun aWeakHumanMatchDoesNotMislabelAnAnimalFaceAsPeople() {
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                SemanticSearchCategory.People to listOf(description(0.26f)),
                SemanticSearchCategory.Animals to listOf(description(0.31f))
            ),
            backgroundEmbeddings = listOf(description(0.1f))
        )

        assertEquals(listOf(SemanticSearchCategory.Animals), classifier.classify(axis(0)))
    }

    @Test
    fun similarlySupportedPeopleAndAnimalsRemainIndependentMatches() {
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                SemanticSearchCategory.People to listOf(description(0.26f)),
                SemanticSearchCategory.Animals to listOf(description(0.27f))
            ),
            backgroundEmbeddings = listOf(description(0.1f))
        )

        assertEquals(
            setOf(SemanticSearchCategory.People, SemanticSearchCategory.Animals),
            classifier.classify(axis(0)).toSet()
        )
    }

    @Test
    fun abstractArtworkCanStillBelongToArtWhileSubjectCategoriesAreRejected() {
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                SemanticSearchCategory.People to listOf(description(0.4f)),
                SemanticSearchCategory.Art to listOf(description(0.45f))
            ),
            backgroundEmbeddings = listOf(description(0.9f))
        )

        assertEquals(listOf(SemanticSearchCategory.Art), classifier.classify(axis(0)))
    }

    @Test
    fun beatingAnUnrelatedBackgroundDoesNotMakeAWeakSubjectMatchRelevant() {
        val classifier = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(
                SemanticSearchCategory.People to listOf(description(0.1f)),
                SemanticSearchCategory.Animals to listOf(description(0.1f))
            ),
            backgroundEmbeddings = listOf(description(-0.2f))
        )

        assertTrue(classifier.classify(axis(0)).isEmpty())
    }

    private fun description(similarity: Float) = FloatArray(512).apply {
        this[0] = similarity
        this[511] = sqrt(1f - similarity * similarity)
    }

    private fun axis(index: Int) = FloatArray(512).apply { this[index] = 1f }
}