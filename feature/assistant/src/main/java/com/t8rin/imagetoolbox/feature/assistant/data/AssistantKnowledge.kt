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

package com.t8rin.imagetoolbox.feature.assistant.data

import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.settings.presentation.model.SettingsGroup
import com.t8rin.imagetoolbox.core.settings.presentation.model.key
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.navigation.searchText
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantAnswer
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantReference
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRequest
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantScreenContext
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantSearchIndex
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantTool
import com.t8rin.imagetoolbox.feature.help.domain.HelpRepository
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class AssistantKnowledge @Inject constructor(
    private val helpRepository: HelpRepository,
    private val resources: ResourceManager,
    private val dispatchers: DispatchersHolder
) {
    private data class Index(
        val help: AssistantSearchIndex<AssistantReference>,
        val tools: AssistantSearchIndex<Screen>,
        val references: Map<String, AssistantReference>
    )

    // Documents come directly from the installed resource table, so an application update
    // rebuilds the index instead of retaining stale documentation in a second database.
    private val indices = mutableMapOf<String, Index>()
    private val strings = ConcurrentHashMap<Pair<Int, String>, String>()
    private val settings by lazy {
        SettingsGroup.entries.flatMap { it.settingsList }.distinct().associateBy { it.key() }
    }

    suspend fun retrieve(request: AssistantRequest): AssistantAnswer =
        withContext(dispatchers.defaultDispatcher) {
            require(request.query.length <= MAX_QUESTION_LENGTH) { "Question is too long" }
            val language = language(request)
            val index = index(language)
            val contextSetting = request.context.settingKey?.let(settings::get)
            val contextTool = Screen.entries.firstOrNull { it.id == request.context.toolId }
            val contextual = contextQuestion.containsMatchIn(request.query)
            val discoveredTools = mutableListOf<Screen>()
            val matches =
                request.query.split(topicSeparator).filter(String::isNotBlank).map { topic ->
                    val query = searchQuery(
                        when {
                            contextSetting != null && contextual -> topic + " " + string(
                                contextSetting.title,
                                language
                            )

                            contextTool != null && contextual -> topic + " " + string(
                                contextTool.title,
                                language
                            )

                            else -> topic
                        }
                    )
                    val reference =
                        helpAliases.firstOrNull { (_, pattern) -> pattern.containsMatchIn(query) }
                            ?.first?.let(index.references::get)
                    if (reference != null) {
                        helpRepository.getTip(reference.tipId)?.deepLink?.let(discoveredTools::add)
                        return@map listOf(reference)
                    }
                    val tools = if (contextTool != null && contextual && contextSetting == null) {
                        listOf(contextTool)
                    } else {
                        index.tools.search(query, 1).map { it.value }
                    }
                    discoveredTools.addAll(tools)
                    findReferences(index, query, tools, language)
                }
            val references = matches.flatten().distinctBy { it.tipId }.take(MAX_REFERENCES)
            val tools =
                (discoveredTools + references.mapNotNull { helpRepository.getTip(it.tipId)?.deepLink })
                    .mapNotNull { screen -> Screen.entries.firstOrNull { it.id == screen.id } }
                    .distinctBy { it.id }.take(MAX_TOOLS).map {
                        AssistantTool(it.id, toolTitle(it, language), string(it.subtitle, language))
                    }
            AssistantAnswer(text = "", references = references, tools = tools)
        }

    fun grounding(request: AssistantRequest, answer: AssistantAnswer): String = buildString {
        val language = language(request)
        appendLine(
            "You are ImageToolbox's assistant. Reply in ${if (language == "ru") "Russian" else "English"}. " +
                    "Be concise and answer the user's question. Do not claim to have performed operations."
        )
        appendContext(request.context, language)
        if (answer.references.isNotEmpty()) {
            appendLine(
                "For ImageToolbox tasks, use the documented tools and steps instead of other apps. " +
                        "For how-to questions, give brief practical steps. Keep tool names, modes and values exact. " +
                        "Do not invent settings."
            )
            appendDocuments(
                answer.references.distinctBy { reference ->
                    helpRepository.getTip(reference.tipId)?.deepLink?.id ?: reference.tipId
                },
                language
            )
        }
        if (answer.tools.isNotEmpty()) {
            appendLine("Tools: ${answer.tools.joinToString { it.title }}")
        }
    }

    private fun index(language: String): Index = synchronized(indices) {
        indices.getOrPut(language) {
            val help = helpRepository.tips.flatMap { tip ->
                tip.pages.map { page ->
                    val reference = AssistantReference(
                        title = string(tip.title, language),
                        text = (listOf(page.title, page.description) + page.steps)
                            .joinToString("\n\n") { string(it, language) },
                        tipId = tip.id,
                        categoryKey = tip.category.key
                    )
                    AssistantSearchIndex.Document(
                        value = reference,
                        title = reference.title + " " + string(page.title, language),
                        text = (
                                listOf(
                                    tip.title,
                                    tip.subtitle,
                                    tip.category.title,
                                    page.title,
                                    page.description
                                ) + page.steps
                                ).joinToString(" ") { bilingual(it, language) }
                    )
                }
            }
            val tools = Screen.entries.map { screen ->
                AssistantSearchIndex.Document(
                    value = screen,
                    title = string(screen.title, language),
                    text = screen.searchText { bilingual(it, language) }
                )
            }
            Index(
                AssistantSearchIndex(help),
                AssistantSearchIndex(tools),
                references = help.map { it.value }.distinctBy { it.tipId }.associateBy { it.tipId }
            )
        }
    }

    private fun toolTitle(screen: Screen, language: String): String = string(
        when (screen) {
            is Screen.RecognizeText -> screen.type?.title ?: screen.title
            is Screen.Filter -> screen.type?.title ?: screen.title
            else -> screen.title
        },
        language
    )

    private fun bilingual(id: Int, language: String): String =
        listOf(language, "ru", "en").distinct().joinToString(" ") { string(id, it) }

    private fun string(id: Int, language: String): String = strings.getOrPut(id to language) {
        resources.getStringLocalized(id, language)
    }

    private fun findReferences(
        index: Index,
        query: String,
        tools: List<Screen>,
        language: String
    ): List<AssistantReference> {
        val anchors = tools.mapNotNull { screen ->
            val relatedIds = helpRepository.tips.filter { it.deepLink?.id == screen.id }
                .map { it.id }.toSet()
            index.help.search(query + " " + toolTitle(screen, language), 1) {
                it.tipId in relatedIds
            }.firstOrNull()?.value ?: relatedIds.firstNotNullOfOrNull(index.references::get)
        }.distinctBy { it.tipId }
        return anchors.ifEmpty { index.help.search(query, 1).map { it.value } }.take(1)
    }

    private fun StringBuilder.appendDocuments(
        references: List<AssistantReference>,
        language: String
    ) {
        if (references.isEmpty()) return
        appendLine("Help:")
        var remainingCharacters = MAX_DOCUMENTS_LENGTH
        references.forEachIndexed { index, reference ->
            val budget = minOf(MAX_DOCUMENT_LENGTH, remainingCharacters / (references.size - index))
            val title = when (val screen = helpRepository.getTip(reference.tipId)?.deepLink) {
                is Screen.Settings -> screen.targetSetting?.let { string(it.title, language) }
                null -> null
                else -> toolTitle(screen, language)
            }
            appendLine(title ?: reference.title)
            var usedCharacters = 0
            reference.text.split("\n\n").drop(1).forEach { paragraph ->
                if (usedCharacters + paragraph.length <= budget) {
                    appendLine(paragraph)
                    usedCharacters += paragraph.length
                }
            }
            remainingCharacters -= usedCharacters
        }
    }

    private fun StringBuilder.appendContext(context: AssistantScreenContext, language: String) {
        val lines = buildList {
            Screen.entries.firstOrNull { it.id == context.toolId }?.let {
                add("Tool: ${toolTitle(it, language)}")
            }
            context.settingKey?.let(settings::get)?.let {
                add("Setting: ${string(it.title, language)}")
                it.subtitle?.let { subtitle -> add(string(subtitle, language)) }
            }
            if (context.selectedUris.isNotEmpty()) add("Images: ${context.selectedUris.size}")
            context.parameters.filterKeys { it in contextParameters }.entries.take(
                MAX_CONTEXT_PARAMETERS
            )
                .forEach { (key, value) -> add("$key: ${value.take(MAX_CONTEXT_VALUE_LENGTH)}") }
        }
        if (lines.isNotEmpty()) {
            appendLine("CONTEXT:")
            lines.forEach { appendLine(it) }
        }
    }
}

private fun language(request: AssistantRequest): String = request.language
    ?.takeIf { it in setOf("ru", "en") }
    ?: if (request.query.any { it in 'А'..'я' || it == 'ё' || it == 'Ё' }) "ru" else "en"

private fun searchQuery(query: String): String = AssistantSearchIndex.normalize(query)
    .replace(Regex("\\b(?:сжат|сжим|сожм|сжми|ужат|ужим|ужм)[а-я]*"), "сжать")
    .replace(Regex("\\bудал[а-я]*"), "удал")
    .replace(Regex("\\b[0-9]+\\b"), "")

private const val MAX_QUESTION_LENGTH = 8000
private const val MAX_REFERENCES = 3
private const val MAX_TOOLS = 4
private const val MAX_CONTEXT_PARAMETERS = 12
private const val MAX_CONTEXT_VALUE_LENGTH = 40
private const val MAX_DOCUMENTS_LENGTH = 1600
private const val MAX_DOCUMENT_LENGTH = 800

private val contextQuestion =
    Regex("настрой|setting|параметр|эт[аоу]|текущ|this|current|where|где", RegexOption.IGNORE_CASE)
private val topicSeparator = Regex("[.!?;]|\\b(?:затем|потом|then)\\b", RegexOption.IGNORE_CASE)
private val helpAliases = listOf(
    "background-remover" to Regex("(?:удал|убра|стер).*фон|фон.*(?:удал|убра)|(?:remov|eras).*background|background.*(?:remov|eras)"),
    "searchable-pdf" to Regex("(?:pdf|пдф).*(?:ocr|распозн|текст|поиск|searchable)|(?:ocr|распозн|текст|поиск|searchable).*(?:pdf|пдф)"),
    "resize-by-file-size" to Regex("^(?!.*\\b(?:pdf|пдф)\\b).*(?:\\bсжать\\b|\\bcompress(?:ing|ed)?\\b|уменьш[а-я]*\\s+(?:вес|размер\\s+файл)|reduce\\s+(?:file\\s+size|size\\s+of))"),
    "exif-removal" to Regex("(?:удал|убра|очист|стер|remove|strip|clear|delete).*(?:exif|эксиф|метадан|metadata)|(?:exif|эксиф|метадан|metadata).*(?:удал|убра|очист|стер|remove|strip|clear|delete)")
)
private val contextParameters = setOf(
    "maxBytes", "keepExif", "saveIfSmaller", "imageFormat", "quality", "mode",
    "filterCount", "selectedCount", "width", "height"
)
