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
import ru.tech.imageresizershrinker.theme.defaultColorTuple
import ru.tech.imageresizershrinker.utils.AMOLED_MODE
import ru.tech.imageresizershrinker.utils.APP_COLOR
import ru.tech.imageresizershrinker.utils.BORDER_WIDTH
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

    private val _appColorTuple = mutableStateOf(defaultColorTuple)
    val appColorTuple by _appColorTuple

    private val _borderWidth = mutableStateOf(0f)
    val borderWidth by _borderWidth

    init {
        runBlocking {
            dataStore.edit { prefs ->
                _nightMode.value = prefs[NIGHT_MODE] ?: 2
                _dynamicColors.value = prefs[DYNAMIC_COLORS] ?: true
                _amoledMode.value = prefs[AMOLED_MODE] ?: false
                _appColorTuple.value = (prefs[APP_COLOR]?.let { tuple ->
                    val colorTuple = tuple.split("*")
                    ColorTuple(
                        primary = colorTuple.getOrNull(0)?.toIntOrNull()?.let { Color(it) }
                            ?: defaultColorTuple.primary,
                        secondary = colorTuple.getOrNull(1)?.toIntOrNull()?.let { Color(it) },
                        tertiary = colorTuple.getOrNull(2)?.toIntOrNull()?.let { Color(it) },
                        surface = colorTuple.getOrNull(3)?.toIntOrNull()?.let { Color(it) },
                    )
                }) ?: defaultColorTuple
                _borderWidth.value = prefs[BORDER_WIDTH] ?: 1f
            }
        }
        dataStore.data.onEach { prefs ->
            _nightMode.value = prefs[NIGHT_MODE] ?: 2
            _dynamicColors.value = prefs[DYNAMIC_COLORS] ?: true
            _amoledMode.value = prefs[AMOLED_MODE] ?: false
            _appColorTuple.value = (prefs[APP_COLOR]?.let { tuple ->
                val colorTuple = tuple.split("*")
                ColorTuple(
                    primary = colorTuple.getOrNull(0)?.toIntOrNull()?.let { Color(it) }
                        ?: defaultColorTuple.primary,
                    secondary = colorTuple.getOrNull(1)?.toIntOrNull()?.let { Color(it) },
                    tertiary = colorTuple.getOrNull(2)?.toIntOrNull()?.let { Color(it) },
                    surface = colorTuple.getOrNull(3)?.toIntOrNull()?.let { Color(it) },
                )
            }) ?: defaultColorTuple
            _borderWidth.value = prefs[BORDER_WIDTH] ?: 1f
        }.launchIn(viewModelScope)
    }
}