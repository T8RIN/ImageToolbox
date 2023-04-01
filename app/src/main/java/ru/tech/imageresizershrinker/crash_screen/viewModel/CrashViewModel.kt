package ru.tech.imageresizershrinker.crash_screen.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import ru.tech.imageresizershrinker.theme.md_theme_dark_primary
import ru.tech.imageresizershrinker.utils.AMOLED_MODE
import ru.tech.imageresizershrinker.utils.APP_COLOR
import ru.tech.imageresizershrinker.utils.DYNAMIC_COLORS
import ru.tech.imageresizershrinker.utils.NIGHT_MODE
import javax.inject.Inject

@HiltViewModel
class CrashViewModel @Inject constructor(
    dataStore: DataStore<Preferences>,
) : ViewModel() {

    private val _nightMode = mutableStateOf(2)
    val nightMode by _nightMode

    private val _dynamicColors = mutableStateOf(true)
    val dynamicColors by _dynamicColors

    private val _amoledMode = mutableStateOf(false)
    val amoledMode by _amoledMode

    private val _appPrimaryColor = mutableStateOf(md_theme_dark_primary)
    val appPrimaryColor by _appPrimaryColor

    init {
        runBlocking {
            dataStore.edit {
                _nightMode.value = it[NIGHT_MODE] ?: 2
                _dynamicColors.value = it[DYNAMIC_COLORS] ?: true
                _amoledMode.value = it[AMOLED_MODE] ?: false
                _appPrimaryColor.value = (it[APP_COLOR]?.let { Color(it) }) ?: md_theme_dark_primary
            }
        }
        dataStore.data.onEach {
            _nightMode.value = it[NIGHT_MODE] ?: 2
            _dynamicColors.value = it[DYNAMIC_COLORS] ?: true
            _amoledMode.value = it[AMOLED_MODE] ?: false
            _appPrimaryColor.value = (it[APP_COLOR]?.let { Color(it) }) ?: md_theme_dark_primary
        }.launchIn(viewModelScope)
    }
}