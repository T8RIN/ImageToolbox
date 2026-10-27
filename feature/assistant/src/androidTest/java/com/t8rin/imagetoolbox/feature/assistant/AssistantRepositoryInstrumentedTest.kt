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

package com.t8rin.imagetoolbox.feature.assistant

import android.app.Notification
import android.app.NotificationManager
import android.content.ContextWrapper
import android.content.Intent
import android.os.PowerManager
import androidx.core.content.getSystemService
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.t8rin.imagetoolbox.core.di.entryPoint
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.model.AssistantModel
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.di.SettingsStateEntryPoint
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.settings.presentation.model.Setting
import com.t8rin.imagetoolbox.core.settings.presentation.model.key
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.assistant.data.AndroidAssistantConversationStore
import com.t8rin.imagetoolbox.feature.assistant.data.AndroidAssistantRepository
import com.t8rin.imagetoolbox.feature.assistant.di.AssistantEntryPoint
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantAnswer
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryQuery
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryResult
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGallerySearch
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantKnowledge
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantMessage
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRequest
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantSearchIndex
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantScreenContext
import com.t8rin.imagetoolbox.feature.assistant.presentation.screenLogic.AssistantComponent
import com.t8rin.neural_tools.assistant.AssistantModels
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AssistantRepositoryInstrumentedTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val app = instrumentation.targetContext
    private val services = app.entryPoint<AssistantEntryPoint>()
    private val directory = File(app.cacheDir, "assistant_repository_${System.nanoTime()}")
        .apply { check(mkdirs()) }
    private val context = object : ContextWrapper(app) {
        override fun getFilesDir(): File = directory
    }
    private val store = AndroidAssistantConversationStore(context)
    private val appSettings = app.entryPoint<SettingsStateEntryPoint>().settingsManager
    private val settingsState = MutableStateFlow(
        appSettings.settingsState.value.copy(
            isAiAssistantEnabled = true,
            isAiAssistantSetupComplete = true,
            aiAssistantModel = AssistantModel.Lite
        )
    )
    private val settings = object : SettingsManager by appSettings {
        override val settingsState = this@AssistantRepositoryInstrumentedTest.settingsState
        override suspend fun getSettingsState() = settingsState.value
    }
    private val scope = object : AppScope {
        override val coroutineContext = Dispatchers.Main.immediate + SupervisorJob()
    }
    private val dispatchers = object : DispatchersHolder {
        override val uiDispatcher = Dispatchers.Main.immediate
        override val ioDispatcher = Dispatchers.IO
        override val defaultDispatcher = Dispatchers.Default
        override val decodingDispatcher = Dispatchers.Default
        override val encodingDispatcher = Dispatchers.Default
    }
    private val retrievedQueries = mutableListOf<List<String>>()
    private val started = CompletableDeferred<Unit>()
    private val reply = CompletableDeferred<AssistantAnswer>()
    private val gallery = AssistantGalleryResult(1, AssistantGalleryQuery(filename = "photo.png"))
    private val fixture = AssistantMessage(
        1, "Найди photo.png", AssistantAnswer("Фото", gallery = gallery), AssistantScreenContext()
    )
    private var active = false
    private var serviceStarts = 0
    private var serviceStops = 0
    private var repository: AndroidAssistantRepository? = null
    private val lifecycles = mutableListOf<LifecycleRegistry>()

    private suspend fun create(realService: Boolean = false): AndroidAssistantRepository {
        store.load()
        store.update { listOf(fixture) }
        store.save()
        val service = if (realService) services.keepAliveService else KeepAliveService.EMPTY
        val keepAlive = object : KeepAliveService by service {
            override fun updateOrStart(title: String, description: String, progress: Float) {
                active = true
                serviceStarts++
                service.updateOrStart(title, description, progress)
            }

            override fun stop(removeNotification: Boolean) {
                active = false
                serviceStops++
                service.stop(removeNotification)
            }
        }
        return withContext(Dispatchers.Main) {
            AndroidAssistantRepository(
                knowledge = object : AssistantKnowledge by services.knowledge {
                    override suspend fun retrieve(
                        request: AssistantRequest,
                        queries: List<String>,
                        sources: List<String>
                    ): AssistantAnswer {
                        retrievedQueries.add(queries)
                        return services.knowledge.retrieve(request, queries, sources)
                    }
                },
                store = store,
                gallerySearch = object : AssistantGallerySearch by services.gallerySearch {
                    override fun isCurrent(sessionId: Long) = true
                    override fun clear() = Unit
                    override suspend fun retry(sessionId: Long): AssistantAnswer {
                        started.complete(Unit)
                        return reply.await()
                    }
                },
                settings = settings,
                resources = services.resources,
                imageGetter = services.assistantImageGetter,
                keepAliveService = keepAlive,
                appScope = scope,
                dispatchers = dispatchers
            ).also { repository = it }
        }.also { withTimeout(10_000) { it.isLoading.first { loading -> !loading } } }
    }

    private suspend fun component(repository: AndroidAssistantRepository): AssistantComponent =
        withContext(Dispatchers.Main) {
            val lifecycle = LifecycleRegistry().also(lifecycles::add)
            AssistantComponent(
                DefaultComponentContext(lifecycle), Screen.Assistant(), {}, {},
                services.knowledge, repository, settings, scope, dispatchers
            )
        }

    @After
    fun tearDown() = runBlocking {
        withContext(Dispatchers.Main) {
            lifecycles.forEach { it.destroy() }
            repository?.stop()
        }
        repository?.let { withTimeout(10_000) { it.isGenerating.first { busy -> !busy } } }
        scope.cancel()
        shell("input keyevent KEYCODE_WAKEUP")
        directory.deleteRecursively()
        Unit
    }

    @Test
    fun keepsGeneratingWithNotificationAfterClosingTheChatAndLockingTheScreen() = runBlocking {
        app.startActivity(
            checkNotNull(app.packageManager.getLaunchIntentForPackage(app.packageName))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        val repository = create(realService = true)
        val first = component(repository)
        withContext(Dispatchers.Main) { first.regenerate() }
        withTimeout(5_000) { started.await() }
        val manager = checkNotNull(app.getSystemService<NotificationManager>())
        val title = services.resources.getString(R.string.ai_assistant)
        waitUntil("Assistant foreground notification was not shown") {
            manager.activeNotifications.any {
                it.notification.extras.getString(Notification.EXTRA_TITLE) == title &&
                        it.notification.flags and Notification.FLAG_FOREGROUND_SERVICE != 0
            }
        }
        withContext(Dispatchers.Main) { lifecycles.first().destroy() }
        shell("input keyevent KEYCODE_HOME")
        shell("input keyevent KEYCODE_SLEEP")
        val power = checkNotNull(app.getSystemService<PowerManager>())
        waitUntil("Emulator screen did not turn off") { !power.isInteractive }
        assertTrue(repository.isGenerating.value)
        assertTrue(active)
        val reopened = component(repository)
        assertTrue(reopened.isGenerating)
        withContext(Dispatchers.Main) {
            reopened.newConversation()
            assertFalse(reopened.send("Еще один запрос"))
        }
        assertEquals(1, repository.messages.value.size)
        reply.complete(AssistantAnswer("Готово", gallery = gallery))
        withTimeout(10_000) { repository.isGenerating.first { busy -> !busy } }
        withContext(Dispatchers.Main) {
            waitUntil("Reopened chat did not receive generation completion") { !reopened.isGenerating }
            assertEquals("Готово", reopened.messages.single().answer.text)
        }
        assertFalse(active)
        assertEquals(1, serviceStarts)
        assertEquals(1, serviceStops)
        assertEquals(repository.messages.value, AndroidAssistantConversationStore(context).load())
        waitUntil("Assistant foreground notification was not removed") {
            manager.activeNotifications.none {
                it.notification.extras.getString(Notification.EXTRA_TITLE) == title
            }
        }
    }

    @Test
    fun unrelatedQuestionsFinishQuicklyAndDoNotBreakTheNextAppQuestion() = runBlocking {
        app.startActivity(
            checkNotNull(app.packageManager.getLaunchIntentForPackage(app.packageName))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        instrumentation.waitForIdleSync()
        val modelFilter = InstrumentationRegistry.getArguments().getString("helpModels")?.split(',')
        val installed = AssistantModels().installations(verifyFiles = false).filter {
            it.installed && !it.variant.supportsVision && (modelFilter == null || it.variant.id in modelFilter)
        }
        assumeTrue("Install a text model before testing replies", installed.isNotEmpty())
        val report = JSONArray()
        val failures = mutableListOf<String>()
        try {
            installed.forEach { state ->
                settingsState.value = settingsState.value.copy(aiAssistantModel = state.variant)
                val repository = create()
                val component = component(repository)
                withContext(Dispatchers.Main) { component.newConversation() }
                val selectedCases =
                    InstrumentationRegistry.getArguments().getString("recoveryCases")?.split(',')
                        ?.map(String::toInt)
                val questions = listOf(
                    "Напиши стихотворение про осень.",
                    "Write a recipe for chocolate cake.",
                    "как мне замазать лица"
                )
                questions.forEachIndexed { index, question ->
                    if (selectedCases != null && index !in selectedCases) return@forEachIndexed
                    val started = System.nanoTime()
                    withContext(Dispatchers.Main) { assertTrue(component.send(question)) }
                    withTimeout(125_000) {
                        repository.messages.first { messages ->
                            messages.lastOrNull()?.let {
                                it.answer.text.isNotBlank() || it.error != null
                            } == true
                        }
                    }
                    val firstTextMs = (System.nanoTime() - started) / 1_000_000
                    withTimeout(125_000) { repository.isGenerating.first { !it } }
                    val message = repository.messages.value.last()
                    val totalMs = (System.nanoTime() - started) / 1_000_000
                    val record =
                        JSONObject().put("model", state.variant.id).put("question", question)
                            .put("firstTextMs", firstTextMs).put("totalMs", totalMs)
                            .put("answer", message.answer.text).put("error", message.error)
                            .put("sources", JSONArray(message.answer.references.map { it.tipId }))
                    report.put(record)
                    println("AssistantRecovery $record")
                    File(app.cacheDir, "assistant-recovery-quality.json").writeText(
                        report.toString(
                            2
                        )
                    )
                    val text = message.answer.text
                    val valid = message.error == null && text.isNotBlank() && text.length < 1600 &&
                            !Regex("(.{8,80})\\1{3,}").containsMatchIn(text) &&
                            if (index != questions.lastIndex) {
                                text.length < 450 &&
                                        totalMs < if (state.variant == AssistantModel.Max) 120_000 else 60_000
                            } else {
                                message.answer.references.any { it.tipId == "face-privacy" } &&
                                        Regex(
                                            "рисован|размыт|пиксел",
                                            RegexOption.IGNORE_CASE
                                        ).containsMatchIn(text) &&
                                        !text.contains("Что вы хотите сделать", true) &&
                                        totalMs < if (state.variant == AssistantModel.Max) 120_000 else 90_000
                            }
                    if (!valid) failures.add(record.toString())
                }
                withContext(Dispatchers.Main) { lifecycles.last().destroy() }
            }
            assertTrue(failures.joinToString("\n"), failures.isEmpty())
        } finally {
            File(app.cacheDir, "assistant-recovery-quality.json").writeText(report.toString(2))
        }
    }

    @Test
    fun generalAppQuestionsAndFollowUpsDoNotBecomeScopeNotices() = runBlocking {
        app.startActivity(
            checkNotNull(app.packageManager.getLaunchIntentForPackage(app.packageName)).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )
        )
        instrumentation.waitForIdleSync()
        val modelFilter = InstrumentationRegistry.getArguments().getString("helpModels")?.split(',')
        val installed = AssistantModels().installations(verifyFiles = false).filter {
            it.installed && !it.variant.supportsVision && (modelFilter == null || it.variant.id in modelFilter)
        }
        assumeTrue("Install a text model before testing replies", installed.isNotEmpty())
        val report = JSONArray()
        val failures = mutableListOf<String>()
        try {
            installed.forEach { state ->
                settingsState.value = settingsState.value.copy(aiAssistantModel = state.variant)
                val repository = create()
                val component = component(repository)
                withContext(Dispatchers.Main) {
                    store.update {
                        listOf(
                            AssistantMessage(
                                1, "Какие форматы поддерживает приложение?",
                                AssistantAnswer(
                                    services.resources.getStringLocalized(
                                        R.string.assistant_scope,
                                        "ru"
                                    )
                                ), AssistantScreenContext()
                            )
                        )
                    }
                }
                val questions = listOf(
                    "А какие форматы поддерживает тулбокс?",
                    "А какие из них сохраняют прозрачность?",
                    "А что ещё умеет это приложение?"
                )
                val selected =
                    InstrumentationRegistry.getArguments().getString("capabilityCases")?.split(',')
                        ?.map(String::toInt)
                questions.forEachIndexed { index, question ->
                    if (selected != null && index !in selected) return@forEachIndexed
                    val started = System.nanoTime()
                    withContext(Dispatchers.Main) { assertTrue(component.send(question)) }
                    var firstVisibleMs: Long? = null
                    val visibility = launch {
                        repository.messages.first { it.lastOrNull()?.answer?.text?.isNotBlank() == true }
                        firstVisibleMs = (System.nanoTime() - started) / 1_000_000
                    }
                    withTimeout(125_000) { repository.isGenerating.first { !it } }
                    visibility.cancel()
                    val message = repository.messages.value.last()
                    val record =
                        JSONObject().put("model", state.variant.id).put("question", question)
                            .put("answer", message.answer.text).put("error", message.error)
                            .put("sources", JSONArray(message.answer.references.map { it.tipId }))
                            .put("scopeNotice", message.answer.isScopeNotice)
                            .put("firstVisibleMs", firstVisibleMs)
                            .put("totalMs", (System.nanoTime() - started) / 1_000_000)
                    report.put(record)
                    File(
                        app.cacheDir,
                        "assistant-capabilities-quality.json"
                    ).writeText(report.toString(2))
                    val text = message.answer.text
                    val terms = AssistantSearchIndex.tokens(text).toSet()
                    val documentedFeature =
                        (message.answer.tools.map { it.title } + message.answer.references.map { it.title })
                            .any {
                                AssistantSearchIndex.tokens(it).distinct()
                                    .count(terms::contains) >= 2
                            }
                    val useful = when (index) {
                        0 -> listOf("PNG", "JPEG|JPG", "WebP").count {
                            Regex(
                                it,
                                RegexOption.IGNORE_CASE
                            ).containsMatchIn(text)
                        } >= 2

                        1 -> Regex(
                            "PNG|WebP|AVIF",
                            RegexOption.IGNORE_CASE
                        ).containsMatchIn(text) && Regex(
                            "прозрач|alpha",
                            RegexOption.IGNORE_CASE
                        ).containsMatchIn(text)

                        else -> Regex(
                            "обрез|размер|фильтр|конвер|формат|сжат|resize|crop|convert",
                            RegexOption.IGNORE_CASE
                        ).containsMatchIn(text) ||
                                documentedFeature
                    }
                    if (message.error != null || message.answer.isScopeNotice || !useful || text.isBlank()) failures.add(
                        record.toString()
                    )
                }
                withContext(Dispatchers.Main) { lifecycles.last().destroy() }
            }
            assertTrue(failures.joinToString("\n"), failures.isEmpty())
        } finally {
            File(app.cacheDir, "assistant-capabilities-quality.json").writeText(report.toString(2))
        }
    }

    @Test
    fun understandsEditingRequestsAndFollowUpsWithoutTopicAliases() = runBlocking {
        app.startActivity(
            checkNotNull(app.packageManager.getLaunchIntentForPackage(app.packageName))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        instrumentation.waitForIdleSync()
        val modelFilter = InstrumentationRegistry.getArguments().getString("helpModels")?.split(',')
        val installed = AssistantModels().installations(verifyFiles = false)
            .filter {
                it.installed && it.variant in setOf(
                    AssistantModel.Lite,
                    AssistantModel.Advanced,
                    AssistantModel.Max
                ) && (modelFilter == null || it.variant.id in modelFilter)
            }
        assumeTrue("Install a text model before testing help replies", installed.isNotEmpty())
        val requests = listOf(
            "Хочу скрыть лица на снимке" to setOf(Screen.Draw().simpleName),
            "Подскажи, фотка слишком тяжёлая для отправки, что сделать?" to setOf(
                Screen.WeightResize().simpleName, Screen.CompressionLab().simpleName
            ),
            "Нужно скопировать текст со скана в заметки" to setOf(Screen.RecognizeText().simpleName),
            "Как сделать PDF с поиском по тексту из сканов?" to setOf("searchable-pdf"),
            "Хочу чтобы фон стал прозрачным" to setOf(Screen.EraseBackground().simpleName),
            "Из фоток сделать анимашку" to setOf(
                Screen.GifTools().simpleName,
                Screen.ApngTools().simpleName,
                Screen.WebpTools().simpleName
            ),
            "Как настроить автоматическую очистку кэша?" to setOf(Setting.AutoCacheClear.key()),
            "Надо стереть из фотки данные о месте съёмки" to setOf(
                Screen.DeleteExif().simpleName,
                Screen.EditExif().simpleName
            ),
            "А это можно сделать с несколькими файлами сразу?" to setOf(
                Screen.DeleteExif().simpleName,
                Screen.EditExif().simpleName
            )
        )
        val brief = InstrumentationRegistry.getArguments().getString("helpBrief") == "true"
        val selectedCases = InstrumentationRegistry.getArguments().getString("helpCases")
            ?.split(',')?.map { it.toInt() }?.toSet()
        require(selectedCases == null || selectedCases.any { it in requests.indices })
        val report = JSONArray()
        val failures = mutableListOf<String>()
        try {
            installed.forEach { state ->
                settingsState.value = settingsState.value.copy(aiAssistantModel = state.variant)
                val repository = create()
                val component = component(repository)
                requests.forEachIndexed { index, (question, tools) ->
                    if (selectedCases != null && index !in selectedCases) return@forEachIndexed
                    val started = System.nanoTime()
                    withContext(Dispatchers.Main) {
                        if (index != requests.lastIndex) component.newConversation()
                        assertTrue(
                            question,
                            component.send(if (brief) "$question Ответь кратко, до трёх предложений." else question)
                        )
                        assertTrue(repository.isGenerating.value)
                        assertEquals(null, repository.error.value)
                    }
                    withTimeout(240_000) { repository.isGenerating.first { busy -> !busy } }
                    val message = repository.messages.value.last()
                    report.put(
                        JSONObject().put("model", state.variant.id).put("question", question)
                            .put("totalMs", (System.nanoTime() - started) / 1_000_000)
                            .put("answer", message.answer.text).put("error", message.error)
                            .put("queries", JSONArray(retrievedQueries.lastOrNull().orEmpty()))
                            .put("sources", JSONArray(message.answer.references.map { it.tipId }))
                    )
                    File(
                        app.cacheDir,
                        "assistant-help-usability.json"
                    ).writeText(report.toString(2))
                    val plainTextRecognition = index != 2 ||
                            message.answer.tools.mapNotNull(services.knowledge::resolveTool).any {
                                it is Screen.RecognizeText && it.type !is Screen.RecognizeText.Type.WriteToSearchablePdf &&
                                        it.type !is Screen.RecognizeText.Type.WriteToMetadata
                            } && Regex("OCR|распозн", RegexOption.IGNORE_CASE).containsMatchIn(
                        message.answer.text
                    )
                    if (!plainTextRecognition || message.error != null || message.answer.text.isBlank() ||
                        message.answer.text.contains(
                            "Что вы хотите сделать с изображением",
                            true
                        ) ||
                        (message.answer.tools.map { it.key } + message.answer.settings.map { it.key } +
                                message.answer.references.map { it.tipId }).none { it in tools }
                    ) {
                        failures.add(message.toString())
                    }
                }
                withContext(Dispatchers.Main) { lifecycles.last().destroy() }
            }
            assertTrue(failures.joinToString("\n"), failures.isEmpty())
        } finally {
            File(app.cacheDir, "assistant-help-usability.json").writeText(report.toString(2))
        }
    }

    @Test
    fun multilingualAnswersUseTheRightToolsAndKeepFollowUps() = runBlocking {
        val args = InstrumentationRegistry.getArguments()
        val selected = args.getString("helpModels")?.split(',')
        val cases = args.getString("multilingualCases")?.split(',')?.map(String::toInt)
        val installed = AssistantModels().installations(verifyFiles = false).filter {
            it.installed && !it.variant.supportsVision && (selected == null || it.variant.id in selected)
        }
        assumeTrue(installed.isNotEmpty())
        val questions = listOf(
            "¿Cómo puedo ocultar las caras en una foto?",
            "Wie verkleinere ich ein Foto auf 500 KB?",
            "画像の背景を透明にするには？",
            "¿Qué formatos de imagen admite ImageToolbox?",
            "¿Y cuáles de ellos conservan la transparencia?",
            "كيف أحول الصورة إلى PNG؟"
        )
        val report = JSONArray()
        val failures = mutableListOf<String>()
        try {
            installed.forEach { state ->
                settingsState.value = settingsState.value.copy(aiAssistantModel = state.variant)
                val repository = create()
                val component = component(repository)
                repository.prepareModel(state.variant)
                questions.forEachIndexed { index, question ->
                    if (cases != null && index !in cases) return@forEachIndexed
                    if (index != 4) withContext(Dispatchers.Main) { component.newConversation() }
                    val startedAt = System.nanoTime()
                    withContext(Dispatchers.Main) { assertTrue(component.send(question)) }
                    withTimeout(125_000) { repository.isGenerating.first { !it } }
                    val message = repository.messages.value.last()
                    val text = message.answer.text
                    val ids = message.answer.references.map { it.tipId }
                    val useful = when (index) {
                        0 -> "face-privacy" in ids
                        1 -> "resize-by-file-size" in ids && "500" in text
                        2 -> "background-remover" in ids && text.any {
                            Character.UnicodeScript.of(it.code) in setOf(
                                Character.UnicodeScript.HIRAGANA,
                                Character.UnicodeScript.KATAKANA,
                                Character.UnicodeScript.HAN
                            )
                        }

                        3 -> listOf("PNG", "JPEG", "JPG", "WebP").count {
                            text.contains(
                                it,
                                true
                            )
                        } >= 2

                        4 -> text.contains("PNG", true) || text.contains("WebP", true)
                        else -> text.contains("PNG", true) && text.any {
                            Character.UnicodeScript.of(
                                it.code
                            ) == Character.UnicodeScript.ARABIC
                        }
                    }
                    val record =
                        JSONObject().put("model", state.variant.id).put("question", question)
                            .put("answer", text).put("error", message.error)
                            .put("references", JSONArray(ids))
                            .put("totalMs", (System.nanoTime() - startedAt) / 1_000_000)
                            .put("passed", useful && message.error == null && text.isNotBlank())
                    report.put(record)
                    File(
                        app.cacheDir,
                        "assistant-multilingual-answers.json"
                    ).writeText(report.toString(2))
                    if (!record.getBoolean("passed")) failures.add(record.toString())
                }
                withContext(Dispatchers.Main) { lifecycles.last().destroy() }
            }
        } finally {
            File(app.cacheDir, "assistant-multilingual-answers.json").writeText(report.toString(2))
        }
        assertTrue(failures.joinToString("\n"), failures.isEmpty())
    }

    @Test
    fun stopKeepsTheMessageWithoutAnErrorAndReleasesTheService() = runBlocking {
        val repository = create()
        withContext(Dispatchers.Main) { repository.regenerate() }
        withTimeout(5_000) { started.await() }
        withContext(Dispatchers.Main) { repository.stop() }
        withTimeout(10_000) { repository.isGenerating.first { busy -> !busy } }
        assertEquals(null, repository.messages.value.single().error)
        assertEquals(repository.messages.value, AndroidAssistantConversationStore(context).load())
        assertFalse(active)
        assertEquals(1, serviceStops)
    }

    @Test
    fun disablingAssistantWhileTheChatIsClosedStopsGeneration() = runBlocking {
        val repository = create()
        val component = component(repository)
        withContext(Dispatchers.Main) { component.regenerate() }
        withTimeout(5_000) { started.await() }
        withContext(Dispatchers.Main) {
            lifecycles.single().destroy()
            settingsState.value = settingsState.value.copy(isAiAssistantEnabled = false)
        }
        withTimeout(10_000) { repository.isGenerating.first { busy -> !busy } }
        assertEquals(null, repository.messages.value.single().error)
        assertFalse(active)
    }

    @Test
    fun failedGenerationSavesTheErrorAndReleasesTheService() = runBlocking {
        val repository = create()
        withContext(Dispatchers.Main) { repository.regenerate() }
        withTimeout(5_000) { started.await() }
        reply.completeExceptionally(IllegalStateException("Search failed"))
        withTimeout(10_000) { repository.isGenerating.first { busy -> !busy } }
        assertEquals(
            services.resources.getString(R.string.assistant_generation_error),
            repository.messages.value.single().error
        )
        assertEquals(repository.messages.value, AndroidAssistantConversationStore(context).load())
        assertFalse(active)
        assertEquals(1, serviceStops)
    }

    private suspend fun waitUntil(description: String, condition: () -> Boolean) {
        assertTrue(description, withTimeoutOrNull(5_000) {
            while (!condition()) delay(20)
            true
        } == true)
    }

    private fun shell(command: String): String =
        android.os.ParcelFileDescriptor.AutoCloseInputStream(
            instrumentation.uiAutomation.executeShellCommand(command)
        ).bufferedReader().use { it.readText() }
}