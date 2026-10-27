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

package com.t8rin.imagetoolbox.feature.assistant.domain

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class AssistantGalleryQuery(
    val semantic: String? = null,
    val people: List<String> = emptyList(),
    val from: String? = null,
    val to: String? = null,
    val album: String? = null,
    val filename: String? = null,
    val excludedPeople: List<String> = emptyList()
) {
    fun normalized(): AssistantGalleryQuery {
        val query = copy(
            semantic = semantic?.trim(),
            people = people.map(String::trim).distinct(),
            excludedPeople = excludedPeople.map(String::trim).distinct(),
            from = from?.trim(),
            to = to?.trim(),
            album = album?.trim(),
            filename = filename?.trim()
        )
        val strings = listOfNotNull(
            query.from,
            query.to,
            query.album,
            query.filename
        ) + query.people + query.excludedPeople
        require(strings.all { it.length <= 300 && it.isNotBlank() })
        require(query.semantic == null || query.semantic.isNotBlank() && query.semantic.length <= 4000)
        require(query.people.size + query.excludedPeople.size <= 10)
        require(query.people.none { person ->
            query.excludedPeople.any {
                it.equals(
                    person,
                    true
                )
            }
        })
        val start = query.from?.let(LocalDate::parse)
        val end = query.to?.let(LocalDate::parse)
        require(start == null || end == null || start <= end)
        return query
    }

}

data class AssistantGalleryCatalog(
    val people: List<String> = emptyList(),
    val albums: List<String> = emptyList()
)

@Serializable
data class AssistantGalleryPhoto(val uri: String, val name: String)

@Serializable
data class AssistantGalleryPerson(val id: Long, val name: String, val thumbnail: String)

@Serializable
data class AssistantGalleryState(
    val sourceUris: List<String>? = null,
    val chosenPeople: Map<String, Long> = emptyMap(),
    val ambiguousName: String? = null,
    val choices: Set<Long> = emptySet(),
    val approximate: Boolean = false,
    val complete: Boolean = false
)

@Serializable
data class AssistantGalleryResult(
    val sessionId: Long,
    val query: AssistantGalleryQuery,
    val photos: List<AssistantGalleryPhoto> = emptyList(),
    val total: Int = 0,
    val people: List<AssistantGalleryPerson> = emptyList(),
    val state: AssistantGalleryState = AssistantGalleryState()
) {
    companion object {
        const val PREVIEW_COUNT = 5
    }
}

interface AssistantGallerySearch {
    @Serializable
    enum class Mode { New, Narrow, Update }

    fun restore(results: List<AssistantGalleryResult>)
    suspend fun catalog(): AssistantGalleryCatalog
    suspend fun search(
        query: AssistantGalleryQuery,
        mode: Mode,
        sessionId: Long
    ): AssistantAnswer

    suspend fun retry(sessionId: Long): AssistantAnswer
    suspend fun more(sessionId: Long): AssistantAnswer
    suspend fun choosePerson(sessionId: Long, personId: Long): AssistantAnswer
    fun currentQuery(): AssistantGalleryQuery?
    fun isCurrent(sessionId: Long): Boolean
    fun clear()
}
