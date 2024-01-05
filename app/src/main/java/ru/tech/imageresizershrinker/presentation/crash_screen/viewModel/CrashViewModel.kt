package ru.tech.imageresizershrinker.presentation.crash_screen.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import ru.tech.imageresizershrinker.core.domain.model.SettingsState
import ru.tech.imageresizershrinker.core.domain.use_case.get_settings_state.GetSettingsStateFlowUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.get_settings_state.GetSettingsStateUseCase
import javax.inject.Inject

@HiltViewModel
class CrashViewModel @Inject constructor(
    private val getSettingsStateUseCase: GetSettingsStateUseCase,
    getSettingsStateFlowUseCase: GetSettingsStateFlowUseCase,
) : ViewModel() {

    private val _settingsState = mutableStateOf(SettingsState.Default())
    val settingsState: SettingsState by _settingsState

    init {
        runBlocking {
            _settingsState.value = getSettingsStateUseCase()
        }
        getSettingsStateFlowUseCase().onEach {
            _settingsState.value = it
        }.launchIn(viewModelScope)
    }
}