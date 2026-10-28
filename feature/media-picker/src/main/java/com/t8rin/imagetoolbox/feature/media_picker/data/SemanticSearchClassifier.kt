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

import com.t8rin.imagetoolbox.core.settings.domain.model.SemanticSearchSettings
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategory

internal class SemanticSearchClassifier(
    private val categoryEmbeddings: Map<SemanticSearchCategory, List<FloatArray>>,
    private val backgroundEmbeddings: List<FloatArray>,
    private val thresholds: Map<String, Float> = emptyMap(),
    private val referenceEmbeddings: Map<String, List<FloatArray>> = emptyMap(),
    private val referenceThresholds: Map<String, Float> = emptyMap()
) {

    val categories: Set<SemanticSearchCategory> get() = categoryEmbeddings.keys

    fun classify(embedding: FloatArray): List<SemanticSearchCategory> {
        val backgroundScore = backgroundEmbeddings.maxOfOrNull { it.similarity(embedding) }
            ?: Float.NEGATIVE_INFINITY
        val scores = categoryEmbeddings.mapValues { (_, descriptions) ->
            descriptions.maxOfOrNull { it.similarity(embedding) } ?: Float.NEGATIVE_INFINITY
        }
        val animalScore = scores[SemanticSearchCategory.BuiltIn.Animals] ?: Float.NEGATIVE_INFINITY
        return scores.mapNotNull { (category, score) ->
            val defaultThreshold = (category as? SemanticSearchCategory.BuiltIn)?.defaultThreshold
                ?: 0.26f
            val minimum = thresholds[category.id]?.takeIf(Float::isFinite)?.coerceIn(0.15f, 0.8f)
                ?: defaultThreshold
            val needsForeground = category == SemanticSearchCategory.BuiltIn.People ||
                    category == SemanticSearchCategory.BuiltIn.Animals
            // Reject weak face matches from animal portraits without vetoing a clear person with a pet.
            val matchesSubject =
                category != SemanticSearchCategory.BuiltIn.People || score >= 0.28f ||
                    score >= animalScore - 0.02f
            val referenceMinimum = referenceThresholds[category.id]?.takeIf(Float::isFinite)
                ?.coerceIn(SemanticSearchSettings.ImageThresholdRange)
                ?: SemanticSearchSettings.DefaultImageThreshold
            val matchesReference = referenceEmbeddings[category.id].orEmpty().any {
                it.similarity(embedding) >= referenceMinimum
            }
            category.takeIf {
                matchesReference || (score >= minimum && matchesSubject &&
                        (!needsForeground || score >= backgroundScore + 0.01f))
            }
        }
    }

    private fun FloatArray.similarity(other: FloatArray): Float {
        var score = 0f
        for (index in indices) score += this[index] * other[index]
        return score
    }

    companion object {

        fun descriptions(category: SemanticSearchCategory.BuiltIn): List<String> = when (category) {
            SemanticSearchCategory.BuiltIn.People -> listOf(
                "a photograph with a person in the foreground",
                "a portrait showing a human face",
                "a photograph of people posing together"
            )

            SemanticSearchCategory.BuiltIn.Animals -> listOf(
                "a photo of an animal",
                "a photo of a dog",
                "a photo of a cat",
                "a photo of a bird"
            )

            else -> listOf(category.query)
        }

        // Compare competing descriptions instead of raising the threshold for every photo.
        val backgroundDescriptions = listOf(
            "an abstract geometric pattern",
            "a smooth color gradient background",
            "a plain solid color background",
            "an abstract painting with colorful shapes",
            "a fractal pattern",
            "a close-up texture of paper",
            "random noise texture"
        )

    }

}