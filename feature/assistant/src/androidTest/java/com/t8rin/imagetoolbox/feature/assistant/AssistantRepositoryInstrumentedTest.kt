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
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.assistant.data.AndroidAssistantConversationStore
import com.t8rin.imagetoolbox.feature.assistant.data.AndroidAssistantRepository
import com.t8rin.imagetoolbox.feature.assistant.di.AssistantEntryPoint
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantAnswer
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryQuery
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryResult
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGallerySearch
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantMessage
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantScreenContext
import com.t8rin.imagetoolbox.feature.assistant.presentation.screenLogic.AssistantComponent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
                context = context,
                knowledge = services.knowledge,
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
        assertTrue(holdsAssistantWakeLock())
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
        assertFalse(holdsAssistantWakeLock())
    }

    @Test
    fun stopSavesCancellationAndReleasesTheService() = runBlocking {
        val repository = create()
        withContext(Dispatchers.Main) { repository.regenerate() }
        withTimeout(5_000) { started.await() }
        withContext(Dispatchers.Main) { repository.stop() }
        withTimeout(10_000) { repository.isGenerating.first { busy -> !busy } }
        assertEquals(
            services.resources.getString(R.string.assistant_cancelled),
            repository.messages.value.single().error
        )
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
        assertEquals(
            services.resources.getString(R.string.assistant_cancelled),
            repository.messages.value.single().error
        )
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

    private fun holdsAssistantWakeLock(): Boolean = shell("dumpsys power")
        .lineSequence()
        .any { it.contains("PARTIAL_WAKE_LOCK") && it.contains("ImageToolbox:Assistant") }

    private fun shell(command: String): String =
        android.os.ParcelFileDescriptor.AutoCloseInputStream(
            instrumentation.uiAutomation.executeShellCommand(command)
        ).bufferedReader().use { it.readText() }
}