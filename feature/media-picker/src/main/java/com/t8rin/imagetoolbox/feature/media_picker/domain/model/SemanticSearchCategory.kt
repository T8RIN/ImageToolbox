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

package com.t8rin.imagetoolbox.feature.media_picker.domain.model

sealed interface SemanticSearchCategory {

    val id: String

    enum class BuiltIn(
        internal val query: String
    ) : SemanticSearchCategory {
        People("a photo of a person or a group of people"),
        Animals("a photo of an animal, a pet or a bird"),
        Nature("a photo of nature, a landscape, plants or flowers"),
        Food("a photo of food or a meal"),
        Documents("a photo of a document, a receipt or printed text"),
        Screenshots("a screenshot of an app or a website"),
        Vehicles("a photo of a car, a motorcycle, a train or a boat"),
        Travel("a travel photo of a tourist landmark or a vacation"),
        Architecture("a photo of architecture, buildings or a city skyline"),
        Art("a photo of a painting, a sculpture or a work of art"),
        Sports("a photo of sports, exercise or an athletic competition"),
        Events("a photo of a party, a wedding, a concert or a celebration"),
        Night("a photo taken at night with a dark sky and lights"),
        Beach("a photo of a sandy beach, the ocean or the sea"),
        Mountains("a photo of mountains, peaks or hiking trails"),
        Sunset("a photo of a sunset or sunrise with a colorful sky");

        override val id: String get() = name

        val defaultThreshold: Float get() = if (this == People) 0.25f else 0.26f
    }

    data class Person(
        val personId: Long,
        val name: String,
        val thumbnail: String,
        val threshold: Float = 0.55f,
        val excludedPhotos: Int = 0,
        val coverUri: String? = null,
        val hidden: Boolean = false,
        val similarPeople: Map<Long, Float> = emptyMap(),
        val faces: List<PersonFace> = emptyList(),
        val coverFaceId: String? = null
    ) : SemanticSearchCategory {
        override val id: String get() = "person:$personId"
    }

    data class PersonFace(val id: String, val uri: String, val thumbnail: String)

    data class Custom(
        override val id: String,
        val name: String,
        val description: String
    ) : SemanticSearchCategory
}

data class SemanticSearchCategoryResult(
    val category: SemanticSearchCategory,
    val media: List<Media>
)