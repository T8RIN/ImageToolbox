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

package com.t8rin.imagetoolbox.feature.root.presentation.screenLogic

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.items
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.t8rin.imagetoolbox.core.domain.APP_RELEASES
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.model.ExtraDataType
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.model.PerformanceClass
import com.t8rin.imagetoolbox.core.domain.remote.AnalyticsManager
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.filters.domain.FilterParamsInteractor
import com.t8rin.imagetoolbox.core.resources.BuildConfig
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.handleDeeplinks
import com.t8rin.imagetoolbox.core.ui.utils.helper.toImageModel
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.ui.widget.other.ToastDuration
import com.t8rin.imagetoolbox.core.ui.widget.other.ToastHostState
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.ChildProvider
import com.t8rin.imagetoolbox.feature.root.presentation.components.navigation.NavigationChild
import com.t8rin.imagetoolbox.feature.root.presentation.components.utils.BackEventObserver
import com.t8rin.imagetoolbox.feature.settings.presentation.screenLogic.SettingsComponent
import com.t8rin.logger.makeLog
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.w3c.dom.Element
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.time.Duration.Companion.seconds

class RootComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    private val settingsManager: SettingsManager,
    private val childProvider: ChildProvider,
    private val analyticsManager: AnalyticsManager,
    filterParamsInteractor: FilterParamsInteractor,
    fileController: FileController,
    dispatchersHolder: DispatchersHolder,
    settingsComponentFactory: SettingsComponent.Factory,
    resourceManager: ResourceManager,
) : BaseComponent(dispatchersHolder, componentContext), ResourceManager by resourceManager {

    private var updatesJob: Job? by smartJob()

    private val _backupRestoredEvents: Channel<Boolean> = Channel(Channel.BUFFERED)
    val backupRestoredEvents: Flow<Boolean> = _backupRestoredEvents.receiveAsFlow()

    private val _isUpdateAvailable: MutableValue<Boolean> = MutableValue(false)
    val isUpdateAvailable: Value<Boolean> = _isUpdateAvailable

    private val _concealBackdropChannel: Channel<Boolean> = Channel(Channel.BUFFERED)
    val concealBackdropFlow: Flow<Boolean> = _concealBackdropChannel.receiveAsFlow()

    val settingsComponent = settingsComponentFactory(
        componentContext = childContext("rootSettings"),
        onTryGetUpdate = ::tryGetUpdate,
        onNavigate = ::navigateTo,
        isUpdateAvailable = isUpdateAvailable,
        onGoBack = {
            _concealBackdropChannel.trySend(true)
        },
        initialSearchQuery = ""
    )

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

    private val _extraDataType = mutableStateOf<ExtraDataType?>(null)
    val extraDataType: ExtraDataType? by _extraDataType

    private val _showSelectDialog = mutableStateOf(false)
    val showSelectDialog by _showSelectDialog

    private val _showUpdateDialog = mutableStateOf(false)
    val showUpdateDialog by _showUpdateDialog

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

    private val _filterPreviewModel: MutableState<ImageModel> =
        mutableStateOf(R.drawable.filter_preview_source.toImageModel())
    val filterPreviewModel by _filterPreviewModel

    private val _canSetDynamicFilterPreview: MutableState<Boolean> =
        mutableStateOf(false)
    val canSetDynamicFilterPreview by _canSetDynamicFilterPreview

    val toastHostState = ToastHostState()

    init {
        runBlocking {
            _settingsState.value = settingsManager.getSettingsState()

            if (settingsState.clearCacheOnLaunch && fileController.getCacheSize() > 20 * 1024 * 1024) {
                fileController.clearCache()
            }
        }
        settingsManager
            .settingsState
            .onEach { state ->
                _showTelegramGroupDialog.update {
                    state.appOpenCount % 6 == 0 && state.appOpenCount != 0 && !state.isTelegramGroupOpened
                }
                _settingsState.value = state
            }
            .launchIn(componentScope)

        if (settingsState.screenList.size != Screen.entries.size) {
            componentScope.launch {
                val currentList = settingsState.screenList
                val neededList = Screen.entries.filter { it.id !in currentList }.map { it.id }
                settingsManager.setScreenOrder(
                    (currentList + neededList).joinToString("/")
                )
            }
        }

        filterParamsInteractor
            .getFilterPreviewModel().onEach { data ->
                _filterPreviewModel.update { data }
            }.launchIn(componentScope)

        filterParamsInteractor
            .getCanSetDynamicFilterPreview().onEach { value ->
                _canSetDynamicFilterPreview.update { value }
            }.launchIn(componentScope)
    }

    fun toggleShowUpdateDialog() {
        componentScope.launch {
            settingsManager.toggleShowUpdateDialogOnStartup()
        }
    }

    fun setPresets(newPresets: List<Int>) {
        componentScope.launch {
            settingsManager.setPresets(newPresets)
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
                updatesJob = componentScope.launch {
                    checkForUpdates(
                        showDialog = showDialog,
                        onNoUpdates = onNoUpdates
                    )
                }
            }
        }
    }

    private suspend fun checkForUpdates(
        showDialog: Boolean,
        onNoUpdates: () -> Unit
    ) = withContext(defaultDispatcher) {
        "start updates check".makeLog("checkForUpdates")
        runCatching {
            withTimeoutOrNull(30.seconds) {
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
            }

            val isNeedUpdate = isNeedUpdate(
                currentName = BuildConfig.VERSION_NAME,
                updateName = tag
            )

            "isNeedUpdate = $isNeedUpdate".makeLog("checkForUpdates")

            if (isNeedUpdate) {
                _isUpdateAvailable.value = true
                if (showDialog) {
                    _showUpdateDialog.value = true
                }
            } else {
                onNoUpdates()
            }
        }.onFailure {
            it.makeLog("checkForUpdates")
            onNoUpdates()
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

        if (uris.isNotEmpty() || extraDataType != null) {
            _showSelectDialog.value = true
        }
    }

    fun updateExtraDataType(type: ExtraDataType?) {
        if (type is ExtraDataType.Backup) {
            componentScope.launch {
                settingsManager.restoreFromBackupFile(
                    backupFileUri = type.uri.trim(),
                    onSuccess = {
                        _backupRestoredEvents.trySend(true)
                    },
                    onFailure = { throwable ->
                        showToast(
                            message = getString(
                                R.string.smth_went_wrong,
                                throwable.localizedMessage ?: ""
                            ),
                            icon = Icons.Rounded.ErrorOutline,
                            duration = ToastDuration.Long
                        )
                        _backupRestoredEvents.trySend(false)
                    }
                )
            }
            return
        }

        _extraDataType.update { null }
        _extraDataType.update { type }
    }

    fun showToast(
        message: String,
        icon: ImageVector? = null,
        duration: ToastDuration = ToastDuration.Short
    ) {
        componentScope.launch {
            toastHostState.showToast(
                message = message,
                icon = icon,
                duration = duration
            )
        }
    }

    fun cancelShowingExitDialog() {
        _shouldShowExitDialog.update { false }
    }

    fun toggleAllowBetas() {
        componentScope.launch {
            settingsManager.toggleAllowBetas()
        }
    }

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
            screen.simpleName.makeLog("Navigator").also(analyticsManager::registerScreenOpen)
            navController.pushNew(screen)
        }
    }

    fun navigateToNew(screen: Screen) {
        if (childStack.items.lastOrNull()?.configuration != Screen.Main) {
            navigateBack()
        }
        screen.simpleName.makeLog("Navigator").also(analyticsManager::registerScreenOpen)
        navController.pushNew(screen)
    }

    private val backEventsObservers: MutableList<BackEventObserver> = mutableListOf()

    fun navigateBack() {
        val closedScreen = childStack.items.lastOrNull()?.configuration
        backEventsObservers.forEach { observer ->
            observer.onBack(closedScreen)
        }
        hideSelectDialog()
        "Pop ${closedScreen?.simpleName}".makeLog("Navigator")
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

    fun handleDeeplinks(intent: Intent?) {
        intent.handleDeeplinks(
            onStart = ::hideSelectDialog,
            onHasExtraDataType = ::updateExtraDataType,
            onColdStart = ::cancelShowingExitDialog,
            onGetUris = ::updateUris,
            onShowToast = ::showToast,
            onNavigate = ::navigateTo,
            isHasUris = !uris.isNullOrEmpty(),
            onWantGithubReview = ::onWantGithubReview,
            isOpenEditInsteadOfPreview = settingsState.openEditInsteadOfPreview
        )
    }


    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): RootComponent
    }

}