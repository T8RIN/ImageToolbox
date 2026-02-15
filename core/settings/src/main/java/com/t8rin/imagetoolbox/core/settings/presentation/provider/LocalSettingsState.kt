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

package com.t8rin.imagetoolbox.core.settings.presentation.provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.rememberAppColorTuple
import com.t8rin.imagetoolbox.core.settings.domain.SimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.settings.presentation.model.EditPresetsController
import com.t8rin.imagetoolbox.core.settings.presentation.model.UiSettingsState

val LocalSettingsState =
    compositionLocalOf<UiSettingsState> { error("UiSettingsState not present") }

val LocalSimpleSettingsInteractor =
    compositionLocalOf<SimpleSettingsInteractor> { error("SimpleSettingInteractor not present") }

val LocalEditPresetsController =
    compositionLocalOf<EditPresetsController> { error("EditPresetsController not present") }

@Composable
fun rememberAppColorTuple(
    settingsState: UiSettingsState = LocalSettingsState.current
): ColorTuple = rememberAppColorTuple(
    defaultColorTuple = settingsState.appColorTuple,
    dynamicColor = settingsState.isDynamicColors,
    darkTheme = settingsState.isNightMode
)

@Composable
fun rememberEditPresetsController(
    initialVisibility: Boolean = false
) = rememberSaveable(initialVisibility, saver = EditPresetsController.Saver) {
    EditPresetsController(initialVisibility)
}