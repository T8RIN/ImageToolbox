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
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.YearMonth
import java.time.Year

@Serializable
internal data class AssistantPhotoPlan(
    val supported: Boolean,
    val clarification: String = "",
    val resetFilters: Boolean = false,
    val withinResults: Boolean = false,
    val sceneChanged: Boolean = false,
    val people: List<String> = emptyList(),
    val excludedPeople: List<String> = emptyList(),
    val from: String? = null,
    val to: String? = null,
    val album: String? = null,
    val filename: String? = null,
    val clear: Set<Filter> = emptySet()
) {
    @Serializable
    enum class Filter { Scene, People, Date, Album, Filename }

    fun mode(current: AssistantGalleryQuery?): AssistantGallerySearch.Mode = when {
        withinResults -> AssistantGallerySearch.Mode.Narrow
        resetFilters || current == null -> AssistantGallerySearch.Mode.New
        else -> AssistantGallerySearch.Mode.Update
    }

    fun gallery(current: AssistantGalleryQuery?, originalQuestion: String): AssistantGalleryQuery {
        require(supported)
        val changes = AssistantGalleryQuery(
            people = people,
            from = from,
            to = to,
            album = album,
            filename = filename,
            excludedPeople = excludedPeople
        ).normalized()
        val scene = if (sceneChanged) originalQuestion.trim().takeIf(String::isNotEmpty) else null
        if (mode(current) == AssistantGallerySearch.Mode.New) return changes.copy(semantic = scene)
            .normalized()
        val previous = requireNotNull(current)
        val datesChanged = from != null || to != null || Filter.Date in clear
        return previous.copy(
            semantic = if (Filter.Scene in clear) null else scene ?: previous.semantic,
            people = when {
                Filter.People in clear -> emptyList()
                people.isEmpty() -> previous.people
                mode(current) == AssistantGallerySearch.Mode.Narrow -> (previous.people + people).distinct()
                else -> people
            },
            excludedPeople = when {
                Filter.People in clear -> emptyList()
                excludedPeople.isEmpty() -> previous.excludedPeople
                mode(current) == AssistantGallerySearch.Mode.Narrow -> (previous.excludedPeople + excludedPeople).distinct()
                else -> excludedPeople
            },
            from = if (datesChanged) from else previous.from,
            to = if (datesChanged) to else previous.to,
            album = if (Filter.Album in clear) null else album ?: previous.album,
            filename = if (Filter.Filename in clear) null else filename ?: previous.filename
        ).normalized()
    }

    companion object {
        fun parse(output: String): AssistantPhotoPlan {
            require(output.length <= 6000)
            return Json.decodeFromString<AssistantPhotoPlan>(
                output.trim().removePrefix("```json").removeSuffix("```").trim()
            ).let { plan ->
                fun date(value: String?, upper: Boolean): String? = value?.let {
                    runCatching { LocalDate.parse(it) }.getOrElse {
                        val month = runCatching { YearMonth.parse(value) }.getOrElse {
                            Year.parse(value).atMonth(if (upper) 12 else 1)
                        }
                        if (upper) month.atEndOfMonth() else month.atDay(1)
                    }.toString()
                }
                plan.copy(from = date(plan.from, false), to = date(plan.to, true))
            }.also {
                if (it.supported) AssistantGalleryQuery(
                    people = it.people,
                    from = it.from,
                    to = it.to,
                    album = it.album,
                    filename = it.filename,
                    excludedPeople = it.excludedPeople
                ).normalized()
                else require(it.clarification.isNotBlank())
                require(!it.resetFilters || !it.withinResults)
            }
        }

    }
}
