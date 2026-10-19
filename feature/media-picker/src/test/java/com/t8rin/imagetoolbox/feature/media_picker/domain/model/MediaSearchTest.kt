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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaSearchTest {

    @Test
    fun blankQueryReturnsOriginalList() {
        val media = listOf(media(1, "one.jpg"))

        assertSame(media, media.searchByName(""))
        assertSame(media, media.searchByName(" \t\n"))
    }

    @Test
    fun nameSearchIgnoresCaseAndPreservesSourceOrder() {
        val media = listOf(
            media(3, "my_CAT.jpg"),
            media(2, "dog.jpg"),
            media(1, "cathedral.png")
        )

        assertEquals(listOf(media[0], media[2]), media.searchByName("CaT"))
        assertTrue(media.searchByName(" cat ").isEmpty())
    }

    @Test
    fun leadingWildcardMatchesOnlyNameEnd() {
        val media = listOf(media(1, "cat.JPG"), media(2, "cat.jpg.backup"))

        assertEquals(listOf(media[0]), media.searchByName("*.jpg"))
    }

    @Test
    fun trailingWildcardMatchesOnlyNameStart() {
        val media = listOf(media(1, "CAT.jpg"), media(2, "my_cat.jpg"))

        assertEquals(listOf(media[0]), media.searchByName("cat*"))
    }

    @Test
    fun wildcardBehaviorKeepsLeadingWildcardPrecedence() {
        val media = listOf(media(1, "my_cat*"), media(2, "cat.jpg"))

        assertEquals(listOf(media[0]), media.searchByName("*cat*"))
        assertEquals(media, media.searchByName("*"))
    }

    @Test
    fun nameMatchesComeFirstAndKeepTheirMediaWhenContentAlsoMatches() {
        val firstName = media(3, "cat.jpg")
        val secondName = media(1, "cathedral.jpg")
        val content = media(2, "IMG_001.jpg")
        val sameUri = firstName.copy(label = "old_name.jpg", timestamp = 1)

        val results = mergeMediaSearchResults(
            listOf(firstName, secondName),
            listOf(content, sameUri, secondName)
        )

        assertEquals(listOf(firstName, secondName, content), results)
        assertSame(firstName, results.first())
    }

    @Test
    fun mergingRemovesDuplicateUrisWithoutReorderingContentMatches() {
        val name = media(1, "cat.jpg")
        val firstContent = media(4, "z.jpg")
        val secondContent = media(2, "a.jpg")
        val thirdContent = media(3, "b.jpg")

        assertEquals(
            listOf(name, firstContent, secondContent, thirdContent),
            mergeMediaSearchResults(
                listOf(name, name.copy(label = "duplicate.jpg")),
                listOf(firstContent, secondContent, firstContent, name, thirdContent, secondContent)
            )
        )
    }

    @Test
    fun eitherSourceCanSupplyAllSearchResults() {
        val media = listOf(media(2, "b.jpg"), media(1, "a.jpg"))

        assertEquals(media, mergeMediaSearchResults(media, emptyList()))
        assertEquals(media, mergeMediaSearchResults(emptyList(), media))
        assertTrue(mergeMediaSearchResults(emptyList(), emptyList()).isEmpty())
    }

    @Test
    fun peopleSearchMatchesCyrillicNamesIgnoringCaseAndPreservesSourceOrder() {
        val media = listOf(media(3, "third.jpg"), media(1, "first.jpg"), media(2, "second.jpg"))
        val categories = listOf(person("АННА Иванова", listOf(media[1], media[0])))

        assertEquals(listOf(media[0], media[1]), media.searchByPeople("на и", categories))
    }

    @Test
    fun peopleSearchSkipsBlankQueriesUnnamedPeopleAndHiddenGroups() {
        val media = listOf(media(1, "first.jpg"), media(2, "second.jpg"), media(3, "third.jpg"))
        val categories = listOf(
            person("Alice", listOf(media[0]), hidden = true),
            person(" ", listOf(media[1])),
            person("Alice", listOf(media[2])),
            SemanticSearchCategoryResult(
                SemanticSearchCategory.Custom("custom", "Alice", "Alice"),
                listOf(media[1])
            )
        )

        assertTrue(media.searchByPeople("", categories).isEmpty())
        assertTrue(media.searchByPeople(" \t\n", categories).isEmpty())
        assertEquals(listOf(media[2]), media.searchByPeople("ali", categories))
        assertEquals(listOf(media[2]), media.searchByPeople("*", categories))
    }

    @Test
    fun peopleSearchOnlyReturnsCurrentAlbumOrCategoryMediaWithCurrentMetadata() {
        val current = media(1, "current.jpg")
        val outside = media(2, "outside.jpg")
        val old = current.copy(label = "old.jpg", timestamp = 1)
        val categories = listOf(person("Alice", listOf(outside, old, old)))
        val results = listOf(current).searchByPeople("Alice", categories)

        assertEquals(listOf(current), results)
        assertSame(current, results.single())
        assertTrue(emptyList<Media>().searchByPeople("Alice", categories).isEmpty())
    }

    @Test
    fun peopleSearchUsesTheSameWildcardRulesAsFilenames() {
        val media = listOf(media(1, "first.jpg"), media(2, "second.jpg"))
        val categories = listOf(
            person("Alice Smith", listOf(media[0])),
            person("Smith Alice", listOf(media[1]))
        )

        assertEquals(listOf(media[0]), media.searchByPeople("alice*", categories))
        assertEquals(listOf(media[1]), media.searchByPeople("*alice", categories))
        assertTrue(media.searchByPeople(" Alice ", categories).isEmpty())
    }

    @Test
    fun peopleComeBeforeFilenamesAndContentWithoutDuplicatingPhotos() {
        val filename = media(4, "alex-file.jpg")
        val portrait = media(3, "IMG_003.jpg")
        val shared = media(1, "alex.jpg")
        val content = media(2, "IMG_002.jpg")
        val media = listOf(filename, portrait, shared, content)
        val categories = listOf(person("Alex", listOf(shared, portrait, portrait)))
        val names = mergeMediaSearchResults(
            media.searchByPeople("alex", categories),
            media.searchByName("alex")
        )

        assertEquals(
            listOf(portrait, shared, filename, content),
            mergeMediaSearchResults(names, listOf(content, portrait, shared, filename))
        )
    }

    private fun person(
        name: String,
        media: List<Media>,
        hidden: Boolean = false
    ) = SemanticSearchCategoryResult(
        category = SemanticSearchCategory.Person(
            personId = media.firstOrNull()?.id ?: 0,
            name = name,
            thumbnail = "",
            hidden = hidden
        ),
        media = media
    )

    private fun media(id: Long, label: String) = Media(
        id = id,
        label = label,
        uri = "content://media/$id",
        path = "/Pictures/$label",
        relativePath = "Pictures/",
        albumID = 1,
        albumLabel = "Pictures",
        timestamp = 0,
        mimeType = "image/jpeg"
    )
}
