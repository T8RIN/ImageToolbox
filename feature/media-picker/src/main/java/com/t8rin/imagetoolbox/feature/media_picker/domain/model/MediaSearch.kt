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

internal fun List<Media>.searchByName(query: String): List<Media> =
    if (query.isBlank()) this else filter { it.label.matchesSearchQuery(query) }

internal fun List<Media>.searchByPeople(
    query: String,
    categories: List<SemanticSearchCategoryResult>
): List<Media> {
    if (query.isBlank()) return emptyList()
    val uris = categories.asSequence().filter {
        val person = it.category as? SemanticSearchCategory.Person
        person != null && !person.hidden && person.name.isNotBlank() && person.name.matchesSearchQuery(
            query
        )
    }.flatMap { it.media.asSequence() }.mapTo(hashSetOf(), Media::uri)
    return filter { it.uri in uris }.distinctBy(Media::uri)
}

private fun String.matchesSearchQuery(query: String): Boolean = when {
    query.startsWith("*") -> endsWith(query.drop(1), true)
    query.endsWith("*") -> startsWith(query.dropLast(1), true)
    else -> contains(query, true)
}

internal fun mergeMediaSearchResults(
    nameMatches: List<Media>,
    contentMatches: List<Media>
): List<Media> = (nameMatches + contentMatches).distinctBy(Media::uri)