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

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sqrt

class FaceClustererTest {

    @Test
    fun similarFacesFormOneGroup() = runBlocking {
        val result = cluster(
            listOf(face(1, vector(1f)), face(2, vector(1f, 0.1f)), face(3, vector(0f, 1f)))
        )
        assertEquals(
            setOf(setOf(1L, 2L), setOf(3L)),
            result.fresh.map { it.faceIds.toSet() }.toSet()
        )
    }

    @Test
    fun differentNamedPeopleAreNotMergedAutomatically() = runBlocking {
        val result = cluster(
            faces = listOf(face(1, vector(1f), "1"), face(2, vector(1f, 0.1f), "2")),
            people = listOf(seed("1"), seed("2"))
        )
        assertEquals(2, result.resolved.size)
    }

    @Test
    fun exclusionsPreventNewPhotosJoiningThePerson() = runBlocking {
        val result = cluster(
            faces = listOf(face(1, vector(1f), "1"), face(2, vector(1f, 0.1f))),
            people = listOf(seed("1")),
            exclusions = listOf(FaceClusterer.MediaAssertion(2, "1"))
        )
        assertEquals(listOf(1L), result.resolved.single().second.faceIds)
        assertEquals(listOf(2L), result.fresh.single().faceIds)
    }

    @Test
    fun alreadyExcludedFacesAreNotAssignedToNewGroups() = runBlocking {
        val result = cluster(
            faces = listOf(face(1, vector(1f), "1"), face(2, vector(1f, 0.1f), "1")),
            people = listOf(seed("1")),
            exclusions = listOf(FaceClusterer.MediaAssertion(2, "1"))
        )
        assertEquals(listOf(2L), result.unassignedFaceIds)
        assertTrue(result.fresh.isEmpty())
    }

    @Test
    fun manualMergeSurvivesDifferentEmbeddings() = runBlocking {
        val result = cluster(
            faces = listOf(face(1, vector(1f)), face(2, vector(0f, 1f))),
            people = listOf(seed("1")),
            pins = listOf(1L, 2L).map {
                FaceClusterer.FaceAssertion(it, 0.1f, 0.1f, 0.5f, 0.5f, "1", true)
            }
        )
        assertEquals(setOf(1L, 2L), result.resolved.single().second.faceIds.toSet())
    }

    @Test
    fun increasingStrictnessCanSplitAnExistingGroup() = runBlocking {
        val result = cluster(
            faces = listOf(face(1, vector(1f), "1"), face(2, vector(0.8f, 0.6f), "1")),
            people = listOf(seed("1", 0.85f))
        )
        assertEquals(1, result.resolved.size)
        assertEquals(1, result.fresh.size)
    }

    @Test
    fun curatedIdentityKeepsNewMatchesAfterAManualMerge() = runBlocking {
        val result = cluster(
            faces = listOf(
                face(1, vector(1f), "1"),
                face(2, vector(0.34202015f, 0.9396926f), "1"),
                face(3, vector(-0.17364818f, 0.9848077f), "2")
            ),
            people = listOf(seed("1"), FaceClusterer.PersonSeed("2", false, 1)),
            pins = listOf(1L, 2L).map {
                FaceClusterer.FaceAssertion(it, 0.1f, 0.1f, 0.5f, 0.5f, "1", true)
            }
        )
        assertEquals("1", result.resolved.single().first)
        assertEquals(setOf(1L, 2L, 3L), result.resolved.single().second.faceIds.toSet())
        assertTrue(result.fresh.isEmpty())
    }

    @Test
    fun facesInTheSamePhotoCannotMergeThroughAnotherPhoto() = runBlocking {
        val result = cluster(
            listOf(
                face(1, vector(1f), mediaId = 1),
                face(2, vector(1f), mediaId = 1),
                face(3, vector(1f), mediaId = 2)
            )
        )
        assertEquals(2, result.fresh.size)
    }

    @Test
    fun inputOrderDoesNotChangeGroups() = runBlocking {
        val faces = listOf(face(1, vector(1f)), face(2, vector(1f, 0.1f)), face(3, vector(0f, 1f)))
        assertEquals(
            cluster(faces).fresh.map { it.faceIds },
            cluster(faces.reversed()).fresh.map { it.faceIds }
        )
    }

    private suspend fun cluster(
        faces: List<FaceClusterer.Face>,
        people: List<FaceClusterer.PersonSeed> = emptyList(),
        pins: List<FaceClusterer.FaceAssertion> = emptyList(),
        exclusions: List<FaceClusterer.MediaAssertion> = emptyList()
    ) = FaceClusterer.cluster(faces, people, pins, exclusions)

    private fun seed(id: String, threshold: Float = 0.55f) =
        FaceClusterer.PersonSeed(id, curated = true, faceCount = 1, threshold = threshold)

    private fun face(
        id: Long,
        vector: FloatArray,
        personId: String? = null,
        mediaId: Long = id
    ) = FaceClusterer.Face(id, mediaId, vector, 0.1f, 0.1f, 0.5f, 0.5f, 1f, personId)

    private fun vector(x: Float, y: Float = 0f): FloatArray {
        val norm = sqrt(x * x + y * y)
        return FloatArray(512).apply {
            this[0] = x / norm
            this[1] = y / norm
        }
    }
}