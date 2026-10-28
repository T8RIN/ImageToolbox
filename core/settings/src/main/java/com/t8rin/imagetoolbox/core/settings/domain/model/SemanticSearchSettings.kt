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

data class SemanticSearchSettings(
    val textThreshold: Float = DefaultTextThreshold,
    val imageThreshold: Float = DefaultImageThreshold,
    val categoryThresholds: Map<String, Float> = emptyMap(),
    val customCategories: List<CustomSemanticSearchCategory> = emptyList(),
    val groupPeople: Boolean = false
) {
    fun normalized(): SemanticSearchSettings = copy(
        textThreshold = textThreshold.takeIf(Float::isFinite)
            ?.coerceIn(TextThresholdRange) ?: DefaultTextThreshold,
        imageThreshold = imageThreshold.takeIf(Float::isFinite)
            ?.coerceIn(ImageThresholdRange) ?: DefaultImageThreshold,
        categoryThresholds = categoryThresholds.filter { (id, threshold) ->
            id.isNotBlank() && threshold.isFinite()
        }.mapValues { (_, threshold) ->
            threshold.coerceIn(TextThresholdRange)
        },
        customCategories = customCategories.map {
            it.copy(
                name = it.name.trim(),
                description = it.description.trim(),
                threshold = it.threshold.takeIf(Float::isFinite)
                    ?.coerceIn(TextThresholdRange) ?: DefaultTextThreshold,
                referenceImages = it.referenceImages.map(String::trim)
                    .filter(String::isNotEmpty).distinct(),
                referenceThreshold = it.referenceThreshold.takeIf(Float::isFinite)
                    ?.coerceIn(ImageThresholdRange) ?: DefaultImageThreshold
            )
        }.filter {
            it.id.isNotBlank() && it.name.isNotBlank() &&
                    (it.description.isNotBlank() || it.referenceImages.isNotEmpty())
        }.distinctBy { it.id }
    )

    @Suppress("ConstPropertyName")
    companion object {
        const val DefaultTextThreshold = 0.26f
        const val DefaultImageThreshold = 0.7f

        val TextThresholdRange: ClosedFloatingPointRange<Float> = 0.15f..0.8f
        val ImageThresholdRange: ClosedFloatingPointRange<Float> = 0.3f..0.95f
    }
}

data class CustomSemanticSearchCategory(
    val id: String,
    val name: String,
    val description: String,
    val threshold: Float = SemanticSearchSettings.DefaultTextThreshold,
    val referenceImages: List<String> = emptyList(),
    val referenceThreshold: Float = SemanticSearchSettings.DefaultImageThreshold
)