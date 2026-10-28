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

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.yield
import kotlin.math.sqrt

internal object FaceClusterer {

    const val VERSION = 1
    const val MIN_FACE_QUALITY = 0.002f
    private const val NEIGHBORS = 8

    class Face(
        val id: Long,
        val mediaId: Long,
        val embedding: FloatArray,
        val left: Float,
        val top: Float,
        val right: Float,
        val bottom: Float,
        val confidence: Float,
        val personId: String?
    )

    class PersonSeed(
        val id: String,
        val curated: Boolean,
        val faceCount: Int,
        val threshold: Float = 0.55f
    )

    class FaceAssertion(
        val mediaId: Long,
        val left: Float,
        val top: Float,
        val right: Float,
        val bottom: Float,
        val personId: String,
        val include: Boolean
    )

    class MediaAssertion(val mediaId: Long, val personId: String)

    class Component(val faceIds: List<Long>)

    class Result(
        val resolved: List<Pair<String, Component>>,
        val fresh: List<Component>,
        val unassignedFaceIds: List<Long>
    )

    private class Neighbor(val index: Int, val similarity: Float)
    private class Edge(val first: Int, val second: Int, val similarity: Float)

    suspend fun cluster(
        faces: List<Face>,
        persons: List<PersonSeed>,
        faceAssertions: List<FaceAssertion>,
        mediaAssertions: List<MediaAssertion>
    ): Result {
        val rejected = hashSetOf<Long>()
        val usable = faces.sortedBy(Face::id).filter { face ->
            val quality = (face.right - face.left).coerceAtLeast(0f) *
                    (face.bottom - face.top).coerceAtLeast(0f) * face.confidence
            val excludedPhoto = mediaAssertions.any {
                it.mediaId == face.mediaId && it.personId == face.personId
            }
            val excludedFace = faceAssertions.any {
                !it.include && it.personId == face.personId && it.mediaId == face.mediaId &&
                        overlap(face, it.left, it.top, it.right, it.bottom) >= 0.5f
            }
            val valid = face.embedding.size == 512 && face.embedding.all(Float::isFinite) &&
                    quality >= MIN_FACE_QUALITY && !excludedPhoto && !excludedFace
            if (!valid) rejected += face.id
            valid
        }
        val known = persons.associateBy(PersonSeed::id)
        val pins = Array(usable.size) { hashSetOf<String>() }
        val excluded = Array(usable.size) { hashSetOf<String>() }
        val claims = Array(usable.size) { hashSetOf<String>() }
        usable.forEachIndexed { index, face ->
            mediaAssertions.filter { it.mediaId == face.mediaId }
                .forEach { excluded[index] += it.personId }
            faceAssertions.filter {
                it.mediaId == face.mediaId && overlap(
                    face,
                    it.left,
                    it.top,
                    it.right,
                    it.bottom
                ) >= 0.5f
            }.forEach {
                if (it.include) pins[index] += it.personId else excluded[index] += it.personId
            }
            pins[index].removeAll(excluded[index])
            claims[index] += pins[index]
            face.personId?.takeIf { it !in excluded[index] }?.let { claims[index] += it }
        }
        val thresholds = FloatArray(usable.size) { index ->
            claims[index].maxOfOrNull { known[it]?.threshold ?: 0.55f } ?: 0.55f
        }
        val parent = IntArray(usable.size) { it }
        val members = Array(usable.size) { mutableListOf(it) }
        fun root(index: Int): Int {
            var current = index
            while (parent[current] != current) {
                parent[current] = parent[parent[current]]
                current = parent[current]
            }
            return current
        }

        fun canJoin(first: List<Int>, second: List<Int>): Boolean {
            val firstPins = first.flatMapTo(hashSetOf()) { pins[it] }
            val sharesPin = second.any { pins[it].any(firstPins::contains) }
            if (!sharesPin) {
                val mediaIds = first.mapTo(hashSetOf()) { usable[it].mediaId }
                if (second.any { usable[it].mediaId in mediaIds }) return false
            }
            val firstClaims = first.flatMapTo(hashSetOf()) { claims[it] }
            val secondClaims = second.flatMapTo(hashSetOf()) { claims[it] }
            if (first.any { excluded[it].any(secondClaims::contains) } ||
                second.any { excluded[it].any(firstClaims::contains) }
            ) {
                return false
            }
            val firstNames = firstClaims.filterTo(hashSetOf()) { known[it]?.curated == true }
            val secondNames = secondClaims.filterTo(hashSetOf()) { known[it]?.curated == true }
            return firstNames.isEmpty() || secondNames.isEmpty() || firstNames.any(secondNames::contains)
        }

        fun join(first: Int, second: Int) {
            var a = root(first)
            var b = root(second)
            if (a == b || !canJoin(members[a], members[b])) return
            if (members[a].size < members[b].size) {
                val previous = a
                a = b
                b = previous
            }
            parent[b] = a
            members[a].addAll(members[b])
            members[b].clear()
        }

        val pinned = hashMapOf<String, Int>()
        pins.forEachIndexed { index, ids ->
            ids.forEach { id -> pinned.putIfAbsent(id, index)?.let { join(index, it) } }
        }
        val nearest = Array(usable.size) { mutableListOf<Neighbor>() }
        fun addNeighbor(index: Int, other: Int, score: Float) {
            val list = nearest[index]
            if (list.size < NEIGHBORS) {
                list += Neighbor(other, score)
            } else {
                val worst = list.indices.minBy { list[it].similarity }
                if (score > list[worst].similarity) list[worst] = Neighbor(other, score)
            }
        }
        for (first in usable.indices) {
            for (second in first + 1 until usable.size) {
                if (usable[first].mediaId == usable[second].mediaId) continue
                val required = when {
                    claims[first].isEmpty() -> thresholds[second]
                    claims[second].isEmpty() -> thresholds[first]
                    else -> maxOf(thresholds[first], thresholds[second])
                }
                val score = cosine(usable[first].embedding, usable[second].embedding)
                if (score >= required) {
                    addNeighbor(first, second, score)
                    addNeighbor(second, first, score)
                }
            }
            if (first % 64 == 0) {
                currentCoroutineContext().ensureActive()
                yield()
            }
        }
        val seen = hashSetOf<Long>()
        val edges = nearest.flatMapIndexed { first, neighbors ->
            neighbors.mapNotNull { other ->
                val a = minOf(first, other.index)
                val b = maxOf(first, other.index)
                val key = (a.toLong() shl 32) or b.toLong()
                val mutual = nearest[other.index].any { it.index == first }
                if ((other.similarity >= 0.62f || mutual) && seen.add(key)) {
                    Edge(a, b, other.similarity)
                } else {
                    null
                }
            }
        }.sortedWith(
            compareByDescending<Edge> { it.similarity }.thenBy { it.first }.thenBy { it.second }
        )
        edges.forEach { join(it.first, it.second) }
        fun centroid(indices: List<Int>): FloatArray {
            val vector = FloatArray(512)
            indices.forEach { index ->
                for (dimension in vector.indices) vector[dimension] += usable[index].embedding[dimension]
            }
            val norm = sqrt(vector.sumOf { it.toDouble() * it }).toFloat().coerceAtLeast(1e-10f)
            return FloatArray(vector.size) { vector[it] / norm }
        }

        val components = mutableListOf<MutableList<Int>>()
        members.filter { it.isNotEmpty() }.forEach { group ->
            val center = centroid(group)
            val stable = group.filter {
                pins[it].isNotEmpty() || cosine(
                    usable[it].embedding,
                    center
                ) >= 0.4f
            }
            if (stable.isNotEmpty()) components += stable.toMutableList()
            group.filterNot(stable::contains).forEach { components += mutableListOf(it) }
        }
        val representatives = components.filter { it.size > 1 }.map { group ->
            Triple(group, centroid(group), group.maxOf { thresholds[it] })
        }
        val attachments = components.filter { it.size == 1 }.mapIndexedNotNull { position, single ->
            if (position % 64 == 0) {
                currentCoroutineContext().ensureActive()
                yield()
            }
            val index = single.single()
            val matches = representatives.mapNotNull candidate@{ (group, center, threshold) ->
                if (!canJoin(single, group)) return@candidate null
                Triple(
                    group,
                    cosine(usable[index].embedding, center),
                    maxOf(thresholds[index], threshold)
                )
            }.sortedByDescending { it.second }
            val best = matches.firstOrNull() ?: return@mapIndexedNotNull null
            if (best.second < best.third ||
                (matches.size > 1 && best.second - matches[1].second < 0.05f)
            ) {
                return@mapIndexedNotNull null
            }
            val support = best.first.asSequence().filter {
                cosine(usable[index].embedding, usable[it].embedding) >= best.third
            }.map { usable[it].mediaId }.distinct().take(2).count()
            if (support < 2) null else Triple(single, best.first, best.second)
        }.sortedWith(
            compareByDescending<Triple<MutableList<Int>, MutableList<Int>, Float>> { it.third }
                .thenBy { usable[it.first.single()].id }
        )
        attachments.forEach { (single, group) ->
            if (canJoin(single, group)) {
                group += single
                single.clear()
            }
        }
        val used = hashSetOf<String>()
        val resolved = mutableListOf<Pair<String, Component>>()
        val fresh = mutableListOf<Component>()
        components.filter { it.isNotEmpty() }.sortedWith(
            compareByDescending<List<Int>> { group -> group.count { pins[it].isNotEmpty() } }
                .thenByDescending { it.size }.thenBy { usable[it.first()].id }
        ).forEach { group ->
            val blocked = group.flatMapTo(hashSetOf()) { excluded[it] }
            val forced = group.flatMap { pins[it] }.groupingBy { it }.eachCount()
            val previous = group.mapNotNull { usable[it].personId }.groupingBy { it }.eachCount()
            val candidates =
                (forced.keys + previous.keys).filter { it in known && it !in used && it !in blocked }
            val target = candidates.maxWithOrNull(
                compareBy<String> { forced[it] ?: 0 }
                    .thenBy { known[it]?.curated == true }.thenBy { previous[it] ?: 0 }
                    .thenBy { known[it]?.faceCount ?: 0 }.thenByDescending { it }
            )
            val component = Component(group.map { usable[it].id })
            if (target == null) {
                fresh += component
            } else {
                used += target
                resolved += target to component
            }
        }
        currentCoroutineContext().ensureActive()
        return Result(resolved, fresh, rejected.sorted())
    }

    private fun cosine(first: FloatArray, second: FloatArray): Float {
        var sum = 0f
        for (index in first.indices) sum += first[index] * second[index]
        return sum
    }

    private fun overlap(face: Face, left: Float, top: Float, right: Float, bottom: Float): Float {
        val intersection = (minOf(face.right, right) - maxOf(face.left, left)).coerceAtLeast(0f) *
                (minOf(face.bottom, bottom) - maxOf(face.top, top)).coerceAtLeast(0f)
        val area = (face.right - face.left) * (face.bottom - face.top)
        return intersection / (area + (right - left) * (bottom - top) - intersection).coerceAtLeast(
            1e-10f
        )
    }
}