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

import org.tartarus.snowball.SnowballStemmer
import java.util.Locale
import kotlin.math.ln

class AssistantSearchIndex<T>(
    documents: List<Document<T>>,
    language: String? = null
) {
    data class Document<T>(
        val value: T,
        val title: String,
        val text: String
    )

    data class Match<T>(val value: T, val score: Double, val matchedTerms: Int)

    private data class Indexed<T>(
        val value: T,
        val title: String,
        val text: String,
        val titleTokens: Set<String>,
        val titleFields: List<Set<String>>,
        val frequencies: Map<String, Int>,
        val length: Int
    )

    private val stemmerConstructor = language?.let { tag ->
        runCatching {
            val name =
                Locale.forLanguageTag(tag).getDisplayLanguage(Locale.ENGLISH).lowercase(Locale.ROOT)
            Class.forName("org.tartarus.snowball.ext.${name}Stemmer")
                .asSubclass(SnowballStemmer::class.java).getDeclaredConstructor()
        }.getOrNull()
    }
    private val stemmers = object : ThreadLocal<SnowballStemmer?>() {
        override fun initialValue(): SnowballStemmer? = stemmerConstructor?.newInstance()
    }

    private fun analyze(text: String): List<String> {
        val stemmer = stemmers.get() ?: return tokens(text)
        return tokens(text).map { term ->
            stemmer.current = term
            stemmer.stem()
            stemmer.current
        }
    }

    fun containsTitle(text: String, title: String): Boolean {
        val words = analyze(text)
        val terms = analyze(title)
        return terms.isNotEmpty() && words.windowed(terms.size).any { phrase ->
            phrase.zip(terms).all { (word, term) ->
                word == term || stemmerConstructor == null && matches(word, term)
            }
        }
    }

    private val indexed = documents.map { document ->
        val frequencies = analyze(document.text).groupingBy { it }.eachCount()
        Indexed(
            value = document.value,
            title = analyze(document.title).joinToString(" "),
            text = analyze(document.text).joinToString(" "),
            titleTokens = analyze(document.title).toSet(),
            titleFields = document.title.lineSequence().map { analyze(it).toSet() }
                .filter { it.isNotEmpty() }.toList(),
            frequencies = frequencies,
            length = frequencies.values.sum()
        )
    }

    private val vocabulary = indexed.flatMap { it.frequencies.keys + it.titleTokens }.toHashSet()

    private val averageLength = indexed.map { it.length }.average()
        .takeIf { it > 0.0 } ?: 1.0

    fun search(
        query: String,
        limit: Int = 5,
        minimumCoverage: Double = 1.0 / 3,
        predicate: ((T) -> Boolean)? = null
    ): List<Match<T>> = rank(query, limit, predicate, minimumCoverage)

    fun candidates(
        query: String,
        limit: Int = 6,
        predicate: ((T) -> Boolean)? = null
    ): List<Match<T>> =
        rank(query, limit, predicate, minimumCoverage = 0.0)

    private fun rank(
        query: String,
        limit: Int,
        predicate: ((T) -> Boolean)?,
        minimumCoverage: Double
    ): List<Match<T>> {
        val candidates = if (predicate == null) indexed else indexed.filter { predicate(it.value) }
        val terms = analyze(query).distinct().filter { it.length > 1 || it.all(Char::isDigit) }
            .take(MAX_QUERY_TERMS)
        if (terms.isEmpty()) return emptyList()
        val allowTypos = terms.any { it in vocabulary && !it.all(Char::isDigit) }
        fun matchesTerm(term: String, word: String): Boolean =
            if (stemmerConstructor == null) matches(
                term,
                word,
                allowTypos = allowTypos && term !in vocabulary
            )
            else term == word || allowTypos && term !in vocabulary && oneEditApart(term, word)

        fun matchesTitle(term: String, word: String): Boolean =
            if (stemmerConstructor == null) matches(
                term,
                word,
                minimumCoverage = 0.6,
                allowTypos = allowTypos && term !in vocabulary
            )
            else matchesTerm(term, word)
        val averageLength = if (predicate == null) averageLength else {
            candidates.map { it.length }.average().takeIf { it > 0.0 } ?: 1.0
        }
        val phrase = analyze(query).joinToString(" ")
        val documentFrequencies = terms.associateWith { term ->
            candidates.count { document ->
                document.frequencies.keys.any { matchesTerm(term, it) } ||
                        document.titleTokens.any { matchesTitle(term, it) }
            }
        }
        val inverseFrequencies = terms.associateWith { term ->
            val frequency = documentFrequencies.getValue(term)
            if (term.all(Char::isDigit)) 0.0
            else ln(1.0 + (candidates.size - frequency + 0.5) / (frequency + 0.5))
        }
        val queryWeight = inverseFrequencies.values.sum()
        if (queryWeight == 0.0) return emptyList()
        return candidates.mapNotNull { document ->
            var matched = 0
            var matchedWeight = 0.0
            var score = 0.0
            terms.forEach { term ->
                val hits = document.frequencies.entries.filter { matchesTerm(term, it.key) }
                val titleHit = document.titleTokens.any { matchesTitle(term, it) }
                if (hits.isNotEmpty() || titleHit) {
                    matched++
                    val inverseFrequency = inverseFrequencies.getValue(term)
                    matchedWeight += inverseFrequency
                    val frequency = hits.sumOf { it.value }.coerceAtLeast(1).toDouble()
                    val length = document.length / averageLength
                    val saturation = frequency * (TERM_SATURATION + 1) /
                            (frequency + TERM_SATURATION * (1 - LENGTH_WEIGHT + LENGTH_WEIGHT * length))
                    if (hits.isNotEmpty()) score += inverseFrequency * saturation
                    if (titleHit) {
                        score += inverseFrequency * TITLE_WEIGHT
                    }
                }
            }
            val anchorWeight = terms.filter { it in document.titleTokens }
                .maxOfOrNull { inverseFrequencies.getValue(it) } ?: 0.0
            val effectiveQueryWeight = if (anchorWeight >= ln(2.0)) terms.sumOf {
                val weight = inverseFrequencies.getValue(it)
                if (documentFrequencies.getValue(it) == 0) minOf(weight, anchorWeight) else weight
            } else queryWeight
            if (matched == 0 || matchedWeight < effectiveQueryWeight * minimumCoverage) return@mapNotNull null
            score *= (matchedWeight / effectiveQueryWeight).coerceAtMost(1.0).let { it * it }
            terms.zipWithNext().forEach { (first, second) ->
                val pair = "$first $second"
                val weight =
                    inverseFrequencies.getValue(first) + inverseFrequencies.getValue(second)
                if (document.title.contains(pair)) score += weight
                else if (document.text.contains(pair)) score += weight * 0.5
            }
            if (phrase.length >= MIN_PHRASE_LENGTH && (terms.size > 1 && document.title.contains(
                    phrase
                ) || document.titleFields.any { it.isNotEmpty() && terms.toSet().containsAll(it) })
            ) score += TITLE_PHRASE_BONUS
            if (phrase.length >= MIN_PHRASE_LENGTH && document.text.contains(phrase)) score += TEXT_PHRASE_BONUS
            Match(document.value, score, matched)
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
        private const val MIN_PREFIX_LENGTH = 3

        fun normalize(text: String): String = buildString {
            java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFKD)
                .lowercase(Locale.ROOT).forEach { character ->
                    when {
                        character.isLetterOrDigit() -> append(character)
                        Character.getType(character) == Character.NON_SPACING_MARK.toInt() -> Unit
                        isNotEmpty() && last() != ' ' -> append(' ')
                    }
                }
        }.trim()

        fun tokens(text: String): List<String> {
            val normalized = normalize(text)
            val words =
                java.text.BreakIterator.getWordInstance(Locale.ROOT).apply { setText(normalized) }
            val result = mutableListOf<String>()
            var start = words.first()
            var end = words.next()
            while (end != java.text.BreakIterator.DONE) {
                normalized.substring(start, end).takeIf { it.any(Char::isLetterOrDigit) }
                    ?.let { word ->
                        var part = 0
                        for (index in 1..word.length) {
                            if (index == word.length || word[index].isDigit() != word[index - 1].isDigit()) {
                                result.add(word.substring(part, index))
                                part = index
                            }
                        }
                    }
                start = end
                end = words.next()
            }
            return result
        }

        internal fun matches(
            query: String,
            word: String,
            minimumPrefix: Int = MIN_PREFIX_LENGTH,
            minimumCoverage: Double = 0.75,
            allowTypos: Boolean = true
        ): Boolean {
            if (query == word) return true
            if (query.all(Char::isDigit) || word.all(Char::isDigit)) return false
            if (minOf(query.length, word.length) < 4) return false
            var prefix = 0
            while (prefix < minOf(
                    query.length,
                    word.length
                ) && query[prefix] == word[prefix]
            ) prefix++
            return prefix >= minimumPrefix && (prefix >= 5 || prefix == minOf(
                query.length,
                word.length
            )) && prefix.toDouble() / maxOf(
                query.length,
                word.length
            ) >= minimumCoverage || allowTypos && oneEditApart(query, word)
        }

        private fun oneEditApart(query: String, word: String): Boolean {
            if (minOf(
                    query.length,
                    word.length
                ) < 5 || kotlin.math.abs(query.length - word.length) > 1
            ) return false
            var left = 0
            var right = 0
            var edits = 0
            while (left < query.length && right < word.length) {
                if (query[left] == word[right]) {
                    left++
                    right++
                } else {
                    if (++edits > 1) return false
                    if (query.length >= word.length) left++
                    if (word.length >= query.length) right++
                }
            }
            return edits + query.length - left + word.length - right <= 1
        }
    }
}
