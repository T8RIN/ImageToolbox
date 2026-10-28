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
import java.time.Year
import java.time.YearMonth

@Serializable
internal data class AssistantPhotoPlan(
    val resetFilters: Boolean = false,
    val withinResults: Boolean = false,
    val scene: String? = null,
    val people: Map<String, PersonRole> = emptyMap(),
    val from: String? = null,
    val to: String? = null,
    val album: String? = null,
    val filename: String? = null,
    val clear: Set<Filter> = emptySet()
) {
    @Serializable
    enum class PersonRole {
        @SerialName("include")
        Include,
        @SerialName("exclude")
        Exclude,
        @SerialName("remove")
        Remove
    }

    @Serializable
    enum class Filter { Scene, People, Date, Album, Filename }

    fun mode(current: AssistantGalleryQuery?): AssistantGallerySearch.Mode = when {
        withinResults -> AssistantGallerySearch.Mode.Narrow
        resetFilters || current == null -> AssistantGallerySearch.Mode.New
        else -> AssistantGallerySearch.Mode.Update
    }

    fun gallery(current: AssistantGalleryQuery?, originalQuestion: String): AssistantGalleryQuery {
        val included = people.filterValues { it == PersonRole.Include }.keys.toList()
        val excluded = people.filterValues { it == PersonRole.Exclude }.keys.toList()
        val removed = people.filterValues { it == PersonRole.Remove }.keys
        val changes = AssistantGalleryQuery(
            people = included,
            from = from,
            to = to,
            album = album,
            filename = filename,
            excludedPeople = excluded
        ).normalized()
        val scene = scene?.takeIf(String::isNotBlank)
            ?.let { originalQuestion.trim().takeIf(String::isNotEmpty) }
        if (mode(current) == AssistantGallerySearch.Mode.New) return changes.copy(semantic = scene)
            .normalized()
        val previous = requireNotNull(current)
        val datesChanged = from != null || to != null || Filter.Date in clear
        fun identities(
            old: List<String>,
            supplied: List<String>,
            opposite: List<String>
        ): List<String> {
            val base = old.takeUnless { Filter.People in clear }.orEmpty().filterNot { name ->
                removed.any { it.equals(name, true) }
            }
            val combined = (base + supplied).distinct()
            return combined.filterNot { name -> opposite.any { it.equals(name, true) } }
        }
        return previous.copy(
            semantic = scene ?: previous.semantic.takeUnless { Filter.Scene in clear },
            people = identities(previous.people, included, excluded),
            excludedPeople = identities(previous.excludedPeople, excluded, included),
            from = if (datesChanged) from else previous.from,
            to = if (datesChanged) to else previous.to,
            album = album ?: previous.album.takeUnless { Filter.Album in clear },
            filename = filename ?: previous.filename.takeUnless { Filter.Filename in clear }
        ).normalized()
    }

    fun normalized(): AssistantPhotoPlan {
        fun date(value: String?, upper: Boolean): String? = value?.takeIf(String::isNotBlank)?.let {
            runCatching { LocalDate.parse(it) }.getOrElse {
                val month = runCatching { YearMonth.parse(value) }.getOrElse {
                    Year.parse(value).atMonth(if (upper) 12 else 1)
                }
                if (upper) month.atEndOfMonth() else month.atDay(1)
            }.toString()
        }
        return copy(from = date(from, false), to = date(to, true)).also {
            AssistantGalleryQuery(
                people = it.people.filterValues { role -> role == PersonRole.Include }.keys.toList(),
                from = it.from,
                to = it.to,
                album = it.album,
                filename = it.filename,
                excludedPeople = it.people.filterValues { role -> role == PersonRole.Exclude }.keys.toList()
            ).normalized()
            require(!it.resetFilters || !it.withinResults)
        }
    }

    companion object {
        fun parse(output: String): AssistantPhotoPlan {
            require(output.length <= 6000)
            return Json.decodeFromString<AssistantPhotoPlan>(
                output.trim().removePrefix("```json").removeSuffix("```").trim()
            ).normalized()
        }
    }
}
