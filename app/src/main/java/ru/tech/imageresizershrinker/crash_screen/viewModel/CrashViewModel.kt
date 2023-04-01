package ru.tech.imageresizershrinker.crash_screen.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t8rin.dynamic.theme.ColorTuple
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

    private val _appColorTuple = mutableStateOf(ColorTuple(md_theme_dark_primary))
    val appColorTuple by _appColorTuple

    init {
        runBlocking {
            dataStore.edit { prefs ->
                _nightMode.value = prefs[NIGHT_MODE] ?: 2
                _dynamicColors.value = prefs[DYNAMIC_COLORS] ?: true
                _amoledMode.value = prefs[AMOLED_MODE] ?: false
                _appColorTuple.value = (prefs[APP_COLOR]?.let { tuple ->
                    val colorTuple = tuple.split("*")
                    ColorTuple(
                        primary = colorTuple[0].toIntOrNull()?.let { Color(it) }
                            ?: md_theme_dark_primary,
                        secondary = colorTuple[1].toIntOrNull()?.let { Color(it) },
                        tertiary = colorTuple[2].toIntOrNull()?.let { Color(it) }
                    )
                }) ?: ColorTuple(md_theme_dark_primary)
            }
        }
        dataStore.data.onEach { prefs ->
            _nightMode.value = prefs[NIGHT_MODE] ?: 2
            _dynamicColors.value = prefs[DYNAMIC_COLORS] ?: true
            _amoledMode.value = prefs[AMOLED_MODE] ?: false
            _appColorTuple.value = (prefs[APP_COLOR]?.let {tuple ->
                val colorTuple = tuple.split("*")
                ColorTuple(
                    primary = colorTuple[0].toIntOrNull()?.let { Color(it) }
                        ?: md_theme_dark_primary,
                    secondary = colorTuple[1].toIntOrNull()?.let { Color(it) },
                    tertiary = colorTuple[2].toIntOrNull()?.let { Color(it) }
                )
            }) ?: ColorTuple(md_theme_dark_primary)
        }.launchIn(viewModelScope)
    }
}