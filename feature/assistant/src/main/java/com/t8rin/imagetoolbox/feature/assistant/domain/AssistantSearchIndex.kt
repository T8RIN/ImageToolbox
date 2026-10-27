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

import java.util.Locale
import kotlin.math.ln

class AssistantSearchIndex<T>(
    documents: List<Document<T>>
) {
    data class Document<T>(
        val value: T,
        val title: String,
        val text: String
    )

    data class Match<T>(val value: T, val score: Double)

    private data class Indexed<T>(
        val value: T,
        val title: String,
        val text: String,
        val titleTokens: Set<String>,
        val frequencies: Map<String, Int>,
        val length: Int
    )

    private val indexed = documents.map { document ->
        val frequencies = tokens(document.text).groupingBy { it }.eachCount()
        Indexed(
            value = document.value,
            title = normalize(document.title),
            text = normalize(document.text),
            titleTokens = tokens(document.title).toSet(),
            frequencies = frequencies,
            length = frequencies.values.sum()
        )
    }

    private val averageLength = indexed.map { it.length }.average()
        .takeIf { it > 0.0 } ?: 1.0

    fun search(
        query: String,
        limit: Int = 5,
        predicate: ((T) -> Boolean)? = null
    ): List<Match<T>> = rank(query, limit, predicate, requireCoverage = true)

    fun candidates(query: String, limit: Int = 6): List<Match<T>> =
        rank(query, limit, predicate = null, requireCoverage = false)

    private fun rank(
        query: String,
        limit: Int,
        predicate: ((T) -> Boolean)?,
        requireCoverage: Boolean
    ): List<Match<T>> {
        val terms = tokens(query).distinct().take(MAX_QUERY_TERMS)
        if (terms.isEmpty()) return emptyList()
        val candidates = if (predicate == null) indexed else indexed.filter { predicate(it.value) }
        val averageLength = if (predicate == null) averageLength else {
            candidates.map { it.length }.average().takeIf { it > 0.0 } ?: 1.0
        }
        val phrase = normalize(query)
        val inverseFrequencies = terms.associateWith { term ->
            val frequency = candidates.count { document ->
                document.frequencies.keys.any { matches(term, it) } ||
                        document.titleTokens.any { matches(term, it) }
            }
            ln(1.0 + (candidates.size - frequency + 0.5) / (frequency + 0.5))
        }
        val queryWeight = inverseFrequencies.values.sum()
        return candidates.mapNotNull { document ->
            var matched = 0
            var matchedWeight = 0.0
            var score = 0.0
            terms.forEach { term ->
                val hits = document.frequencies.entries.filter { matches(term, it.key) }
                val titleHit = document.titleTokens.any { matches(term, it) }
                if (hits.isNotEmpty() || titleHit) {
                    matched++
                    val inverseFrequency = inverseFrequencies.getValue(term)
                    matchedWeight += inverseFrequency
                    val frequency = hits.sumOf { it.value }.coerceAtLeast(1).toDouble()
                    val length = document.length / averageLength
                    val saturation = frequency * (TERM_SATURATION + 1) /
                            (frequency + TERM_SATURATION * (1 - LENGTH_WEIGHT + LENGTH_WEIGHT * length))
                    score += inverseFrequency * (if (titleHit) TITLE_WEIGHT else 1.0) * saturation
                }
            }
            if (matched == 0 || requireCoverage && (matched * 2 < terms.size || matchedWeight * 2 < queryWeight)) return@mapNotNull null
            score *= matched.toDouble() / terms.size
            if (phrase.length >= MIN_PHRASE_LENGTH && document.title.contains(phrase)) score += TITLE_PHRASE_BONUS
            if (phrase.length >= MIN_PHRASE_LENGTH && document.text.contains(phrase)) score += TEXT_PHRASE_BONUS
            Match(document.value, score)
        }.sortedByDescending { it.score }.take(limit.coerceIn(1, MAX_RESULTS))
    }

    companion object {
        private const val MAX_QUERY_TERMS = 24
        private const val MAX_RESULTS = 20
        private const val TITLE_WEIGHT = 4.0
        private const val TERM_SATURATION = 1.2
        private const val LENGTH_WEIGHT = 0.75
        private const val MIN_PHRASE_LENGTH = 3
        private const val TITLE_PHRASE_BONUS = 8.0
        private const val TEXT_PHRASE_BONUS = 2.0
        private const val MIN_PREFIX_LENGTH = 4

        private val wordSeparator = Regex("[^\\p{L}\\p{N}]+")
        private val russianEnding =
            Regex("(?:яйте|айте|ить|ять|ать|иями|ями|ами|ого|ему|ому|ими|ыми|ая|яя|ое|ее|ые|ие|ую|юю|ой|ей|ам|ям|ах|ях|ом|ем|ов|ев|их|ых|а|я|ы|и|у|ю|е)$")

        fun normalize(text: String): String = text.lowercase(Locale.ROOT)
            .replace('ё', 'е')
            .replace(wordSeparator, " ")
            .trim()

        fun tokens(text: String): List<String> = normalize(text).split(' ')
            .filter { it.length > 1 && it.any(Char::isLetter) && it !in stopWords }
            .map { word ->
                if (word.all { it in 'а'..'я' }) {
                    word.replace(russianEnding, "").takeIf { it.length >= 3 } ?: word
                } else if (word.all { it in 'a'..'z' }) englishRoot(word) else word
            }

        private fun englishRoot(word: String): String {
            var root = when {
                word.endsWith("ies") -> word.dropLast(3) + "y"
                listOf("ches", "shes", "xes", "zes", "sses").any(word::endsWith) -> word.dropLast(2)
                word.endsWith('s') && !word.endsWith("ss") -> word.dropLast(1)
                else -> word
            }
            root = when {
                root == "conversion" -> "convert"
                root.endsWith("ion") -> root.dropLast(3)
                root.endsWith("ing") || root.endsWith("ed") -> {
                    val base = root.dropLast(if (root.endsWith("ing")) 3 else 2)
                    if (base.length > 2 && base.last() in "bdfgmnpt" && base.last() == base[base.lastIndex - 1]) {
                        base.dropLast(1)
                    } else base
                }

                else -> root
            }
            return root.removeSuffix("e").takeIf { it.length >= 3 } ?: word
        }

        private fun matches(query: String, word: String): Boolean =
            query == word || minOf(query.length, word.length) >= MIN_PREFIX_LENGTH &&
                    (query.any { it !in 'a'..'z' } || word.any { it !in 'a'..'z' }) && word.startsWith(query)

        private val stopWords = setOf(
            "a",
            "an",
            "the",
            "and",
            "or",
            "but",
            "for",
            "with",
            "from",
            "into",
            "of",
            "to",
            "in",
            "on",
            "at",
            "by",
            "as",
            "is",
            "are",
            "was",
            "were",
            "be",
            "been",
            "do",
            "does",
            "did",
            "can",
            "could",
            "will",
            "would",
            "should",
            "me",
            "my",
            "our",
            "you",
            "your",
            "it",
            "its",
            "this",
            "that",
            "these",
            "those",
            "how",
            "what",
            "which",
            "where",
            "why",
            "when",
            "и",
            "или",
            "но",
            "для",
            "со",
            "из",
            "во",
            "на",
            "по",
            "до",
            "за",
            "при",
            "под",
            "над",
            "ли",
            "не",
            "об",
            "от",
            "что",
            "как",
            "где",
            "почему",
            "зачем",
            "чтобы",
            "это",
            "этот",
            "эта",
            "эту",
            "этим",
            "этом",
            "эти",
            "те",
            "мы",
            "вы",
            "ты",
            "мне",
            "мой",
            "моих",
            "наш",
            "ваш",
            "его",
            "ее",
            "их"
        )
    }
}
