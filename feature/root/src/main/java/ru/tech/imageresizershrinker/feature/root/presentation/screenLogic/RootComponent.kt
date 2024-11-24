/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.root.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.exifinterface.media.ExifInterface
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.items
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.extractPrimaryColor
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import ru.tech.imageresizershrinker.core.domain.APP_RELEASES
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.model.PerformanceClass
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.resources.BuildConfig
import ru.tech.imageresizershrinker.core.settings.domain.SettingsManager
import ru.tech.imageresizershrinker.core.settings.domain.SimpleSettingsInteractor
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import ru.tech.imageresizershrinker.core.settings.domain.toSimpleSettingsInteractor
import ru.tech.imageresizershrinker.core.ui.utils.BaseComponent
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastHostState
import ru.tech.imageresizershrinker.feature.root.presentation.components.navigation.ChildProvider
import ru.tech.imageresizershrinker.feature.root.presentation.components.navigation.NavigationChild
import ru.tech.imageresizershrinker.feature.root.presentation.components.utils.BackEventObserver
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class RootComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val settingsManager: SettingsManager,
    private val childProvider: ChildProvider,
    fileController: FileController,
    dispatchersHolder: DispatchersHolder,
) : BaseComponent(dispatchersHolder, componentContext) {

    val simpleSettingsInteractor: SimpleSettingsInteractor =
        settingsManager.toSimpleSettingsInteractor()

    private val _settingsState = mutableStateOf(SettingsState.Default)
    val settingsState: SettingsState by _settingsState

    private val navController = StackNavigation<Screen>()

    internal val childStack: Value<ChildStack<Screen, NavigationChild>> by lazy {
        childStack(
            source = navController,
            initialConfiguration = Screen.Main,
            serializer = Screen.serializer(),
            handleBackButton = true,
            childFactory = { screen, context ->
                with(childProvider) {
                    createChild(
                        config = screen,
                        componentContext = context
                    )
                }
            }
        )
    }

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _extraImageType = mutableStateOf<String?>(null)
    val extraImageType by _extraImageType

    private val _showSelectDialog = mutableStateOf(false)
    val showSelectDialog by _showSelectDialog

    private val _showUpdateDialog = mutableStateOf(false)
    val showUpdateDialog by _showUpdateDialog

    private val _isUpdateAvailable: MutableValue<Boolean> = MutableValue(false)
    val isUpdateAvailable: Value<Boolean> = _isUpdateAvailable

    private val _isUpdateCancelled = mutableStateOf(false)

    private val _shouldShowExitDialog = mutableStateOf(true)
    val shouldShowDialog by _shouldShowExitDialog

    private val _showGithubReviewDialog = mutableStateOf(false)
    val showGithubReviewDialog by _showGithubReviewDialog

    private val _showTelegramGroupDialog = mutableStateOf(false)
    val showTelegramGroupDialog by _showTelegramGroupDialog

    private val _tag = mutableStateOf("")
    val tag by _tag

    private val _changelog = mutableStateOf("")
    val changelog by _changelog

    val toastHostState = ToastHostState()

    init {
        if (settingsState.clearCacheOnLaunch) fileController.clearCache()

        runBlocking {
            settingsManager.registerAppOpen()
            _settingsState.value = settingsManager.getSettingsState()
        }
        settingsManager
            .getSettingsStateFlow()
            .onEach {
                _settingsState.value = it
            }
            .launchIn(componentScope)

        settingsManager
            .getNeedToShowTelegramGroupDialog()
            .onEach { value ->
                _showTelegramGroupDialog.update { value }
            }
            .launchIn(componentScope)
    }

    fun toggleShowUpdateDialog() {
        componentScope.launch {
            settingsManager.toggleShowUpdateDialogOnStartup()
        }
    }

    fun setPresets(newPresets: List<Int>) {
        componentScope.launch {
            settingsManager.setPresets(
                newPresets.joinToString("*")
            )
        }
    }

    fun cancelledUpdate(showAgain: Boolean = false) {
        if (!showAgain) _isUpdateCancelled.value = true
        _showUpdateDialog.value = false
    }

    fun tryGetUpdate(
        isNewRequest: Boolean = false,
        onNoUpdates: () -> Unit = {}
    ) {
        if (settingsState.appOpenCount < 2 && !isNewRequest) return
        val isInstalledFromMarket = settingsManager.isInstalledFromPlayStore()

        val showDialog = settingsState.showUpdateDialogOnStartup
        if (isInstalledFromMarket) {
            if (showDialog) {
                _showUpdateDialog.value = isNewRequest
            }
        } else {
            if (!_isUpdateCancelled.value || isNewRequest) {
                componentScope.launch {
                    checkForUpdates(showDialog, onNoUpdates)
                }
            }
        }
    }

    private suspend fun checkForUpdates(
        showDialog: Boolean,
        onNoUpdates: () -> Unit
    ) = withContext(defaultDispatcher) {
        runCatching {
            val nodes =
                DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    URL("$APP_RELEASES.atom").openConnection().getInputStream()
                )?.getElementsByTagName("feed")

            if (nodes != null) {
                for (i in 0 until nodes.length) {
                    val element = nodes.item(i) as Element
                    val title = element.getElementsByTagName("entry")
                    val line = (title.item(0) as Element)
                    _tag.value = (line.getElementsByTagName("title")
                        .item(0) as Element).textContent
                    _changelog.value = (line.getElementsByTagName("content")
                        .item(0) as Element).textContent
                }
            }

            if (
                isNeedUpdate(
                    currentName = BuildConfig.VERSION_NAME,
                    updateName = tag
                )
            ) {
                _isUpdateAvailable.value = true
                if (showDialog) {
                    _showUpdateDialog.value = true
                }
            } else {
                onNoUpdates()
            }
        }
    }

    private fun isNeedUpdate(
        currentName: String,
        updateName: String,
        allowBetas: Boolean = settingsState.allowBetas
    ): Boolean {
        val betaList = listOf(
            "alpha", "beta", "rc"
        )

        fun String.toVersionCodeString(): String {
            return replace(
                regex = Regex("0\\d"),
                transform = {
                    it.value.replace("0", "")
                }
            ).replace("-", "")
                .replace(".", "")
                .replace("_", "")
                .let { version ->
                    if (betaList.any { it in version }) version
                    else version + "4"
                }
                .replace("alpha", "1")
                .replace("beta", "2")
                .replace("rc", "3")
                .replace("foss", "")
                .replace("jxl", "")
        }

        val currentVersionCodeString = currentName.toVersionCodeString()
        val updateVersionCodeString = updateName.toVersionCodeString()

        val maxLength = maxOf(currentVersionCodeString.length, updateVersionCodeString.length)

        val currentVersionCode = currentVersionCodeString.padEnd(maxLength, '0').toIntOrNull() ?: -1
        val updateVersionCode = updateVersionCodeString.padEnd(maxLength, '0').toIntOrNull() ?: -1

        return if (!updateName.startsWith(currentName)) {
            if (betaList.all { it !in updateName }) {
                updateVersionCode > currentVersionCode
            } else {
                if (allowBetas || betaList.any { it in currentName }) {
                    updateVersionCode > currentVersionCode
                } else false
            }
        } else false
    }

    fun hideSelectDialog() {
        _showSelectDialog.value = false
        _uris.update { null }
    }

    fun updateUris(uris: List<Uri>) {
        _uris.value = uris

        if (uris.isNotEmpty() || extraImageType != null) {
            _showSelectDialog.value = true
        }
    }

    fun updateExtraImageType(type: String?) {
        _extraImageType.update { null }
        _extraImageType.update { type }
    }

    fun showToast(
        message: String,
        icon: ImageVector? = null,
    ) {
        componentScope.launch {
            toastHostState.showToast(
                message = message,
                icon = icon
            )
        }
    }

    fun cancelShowingExitDialog() {
        _shouldShowExitDialog.update { false }
    }

    fun toggleAllowBetas() {
        componentScope.launch {
            settingsManager.toggleAllowBetas()
            tryGetUpdate(isNewRequest = true)
        }
    }

    suspend fun getColorTupleFromEmoji(
        emojiUri: String
    ): ColorTuple? = imageGetter
        .getImage(data = emojiUri)
        ?.extractPrimaryColor()
        ?.let { ColorTuple(it) }

    fun onWantGithubReview() {
        _showGithubReviewDialog.update { true }
    }

    fun hideReviewDialog() {
        _showGithubReviewDialog.update { false }
    }

    fun hideTelegramGroupDialog() {
        _showTelegramGroupDialog.update { false }
    }

    fun adjustPerformance(performanceClass: PerformanceClass) {
        componentScope.launch {
            settingsManager.adjustPerformance(performanceClass)
        }
    }

    fun registerDonateDialogOpen() {
        componentScope.launch {
            settingsManager.registerDonateDialogOpen()
        }
    }

    fun notShowDonateDialogAgain() {
        componentScope.launch {
            settingsManager.setNotShowDonateDialogAgain()
        }
    }

    fun registerTelegramGroupOpen() {
        componentScope.launch {
            settingsManager.registerTelegramGroupOpen()
        }
    }

    fun navigateTo(screen: Screen) {
        componentScope.launch {
            delay(100)
            hideSelectDialog()
            navController.pushNew(screen)
        }
    }

    fun navigateToNew(screen: Screen) {
        if (childStack.items.lastOrNull()?.configuration != Screen.Main) {
            navigateBack()
        }
        navController.pushNew(screen)
    }

    private val backEventsObservers: MutableList<BackEventObserver> = mutableListOf()

    fun navigateBack() {
        backEventsObservers.forEach { observer ->
            observer.onBack(childStack.items.lastOrNull()?.configuration)
        }
        hideSelectDialog()
        navController.pop()
    }

    fun addBackEventsObserver(
        observer: BackEventObserver
    ) {
        backEventsObservers.add(observer)
    }

    fun removeBackEventsObserver(
        observer: BackEventObserver
    ) {
        backEventsObservers.remove(observer)
    }


    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): RootComponent
    }

}