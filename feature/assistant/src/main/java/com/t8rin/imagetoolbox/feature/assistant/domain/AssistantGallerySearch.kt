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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDate

@Serializable
data class AssistantGalleryQuery(
    val semantic: String? = null,
    val people: List<String> = emptyList(),
    val from: String? = null,
    val to: String? = null,
    val album: String? = null,
    val filename: String? = null
) {
    fun normalized(): AssistantGalleryQuery {
        val query = copy(
            semantic = semantic?.trim(),
            people = people.map(String::trim).distinct(),
            from = from?.trim(),
            to = to?.trim(),
            album = album?.trim(),
            filename = filename?.trim()
        )
        val strings = listOfNotNull(
            query.semantic,
            query.from,
            query.to,
            query.album,
            query.filename
        ) + query.people
        require(strings.all { it.length <= 300 && it.isNotBlank() })
        require(query.people.size <= 10)
        val start = query.from?.let(LocalDate::parse)
        val end = query.to?.let(LocalDate::parse)
        require(start == null || end == null || start <= end)
        return query
    }

    fun refine(previous: AssistantGalleryQuery): AssistantGalleryQuery = copy(
        people = (previous.people + people).distinct(),
        from = from ?: previous.from,
        to = to ?: previous.to,
        album = album ?: previous.album,
        filename = filename ?: previous.filename,
        semantic = semantic ?: previous.semantic
    ).normalized()
}

@Serializable
internal data class AssistantGalleryCommand(
    val action: Action,
    val semantic: String? = null,
    val people: List<String> = emptyList(),
    val from: String? = null,
    val to: String? = null,
    val album: String? = null,
    val filename: String? = null
) {
    val query: AssistantGalleryQuery?
        get() = if (action == Action.Search || action == Action.Refine) {
            AssistantGalleryQuery(semantic, people, from, to, album, filename).normalized()
        } else null

    fun isRefinement(question: String): Boolean {
        if (action == Action.Refine) return true
        if (action != Action.Search) return false
        val text = question.trimStart()
        return refinementPrefixes.any { text.startsWith(it, ignoreCase = true) }
    }

    @Serializable
    enum class Action {
        @SerialName("help")
        Help,

        @SerialName("search")
        Search,

        @SerialName("refine")
        Refine,

        @SerialName("more")
        More,

        @SerialName("clarify")
        Clarify
    }

    companion object {
        private val refinementPrefixes = listOf(
            "из этих", "из них", "среди найденных",
            "а где он", "а где она", "а где они",
            "of these", "from these", "among these",
            "and where is he", "and where is she", "and where are they"
        )

        fun parse(output: String): AssistantGalleryCommand {
            require(output.length <= 4_000)
            val text = output.trim().removePrefix("```json").removePrefix("```")
                .removeSuffix("```").trim()
            return Json.decodeFromString<AssistantGalleryCommand>(text).also {
                val query = it.query
                if (query == null) {
                    require(
                        it.semantic == null && it.people.isEmpty() && it.from == null &&
                                it.to == null && it.album == null && it.filename == null
                    )
                } else {
                    require(
                        query.semantic != null || query.people.isNotEmpty() || query.from != null ||
                                query.to != null || query.album != null || query.filename != null
                    )
                }
            }
        }
    }
}

@Serializable
data class AssistantGalleryPhoto(val uri: String, val name: String)

@Serializable
data class AssistantGalleryPerson(val id: Long, val name: String, val thumbnail: String)

@Serializable
data class AssistantGalleryResult(
    val sessionId: Long,
    val query: AssistantGalleryQuery,
    val photos: List<AssistantGalleryPhoto> = emptyList(),
    val total: Int = 0,
    val people: List<AssistantGalleryPerson> = emptyList()
) {
    companion object {
        const val PREVIEW_COUNT = 5
    }
}

interface AssistantGallerySearch {
    suspend fun capabilities(): String
    suspend fun search(
        query: AssistantGalleryQuery,
        refine: Boolean,
        sessionId: Long
    ): AssistantAnswer

    suspend fun retry(sessionId: Long): AssistantAnswer
    suspend fun more(sessionId: Long): AssistantAnswer
    suspend fun choosePerson(sessionId: Long, personId: Long): AssistantAnswer
    fun currentQuery(): AssistantGalleryQuery?
    fun isCurrent(sessionId: Long): Boolean
    fun clear()
}
