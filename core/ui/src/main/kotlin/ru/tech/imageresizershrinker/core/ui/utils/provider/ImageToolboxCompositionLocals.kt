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

package ru.tech.imageresizershrinker.core.ui.utils.provider

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import ru.tech.imageresizershrinker.core.domain.model.ImageModel
import ru.tech.imageresizershrinker.core.settings.domain.SimpleSettingsInteractor
import ru.tech.imageresizershrinker.core.settings.presentation.model.UiSettingsState
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalEditPresetsController
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import ru.tech.imageresizershrinker.core.settings.presentation.provider.rememberEditPresetsController
import ru.tech.imageresizershrinker.core.ui.theme.ImageToolboxThemeSurface
import ru.tech.imageresizershrinker.core.ui.utils.confetti.ConfettiHost
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.rememberConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.LocalFilterPreviewModel
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.rememberEnhancedHapticFeedback
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastHost
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.rememberToastHostState

@Composable
fun ImageToolboxCompositionLocals(
    settingsState: UiSettingsState,
    toastHostState: ToastHostState = rememberToastHostState(),
    filterPreviewModel: ImageModel? = null,
    simpleSettingsInteractor: SimpleSettingsInteractor? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val editPresetsController = rememberEditPresetsController()
    val confettiHostState = rememberConfettiHostState()
    val context = LocalContext.current
    val customHapticFeedback = rememberEnhancedHapticFeedback(settingsState.hapticsStrength)
    val screenSize = rememberScreenSize()

    val values = remember(
        context,
        toastHostState,
        settingsState,
        simpleSettingsInteractor,
        editPresetsController,
        confettiHostState,
        customHapticFeedback,
        screenSize,
        filterPreviewModel
    ) {
        derivedStateOf {
            listOfNotNull(
                LocalToastHostState provides toastHostState,
                LocalSettingsState provides settingsState,
                LocalSimpleSettingsInteractor providesOrNull simpleSettingsInteractor,
                LocalEditPresetsController provides editPresetsController,
                LocalFilterPreviewModel providesOrNull filterPreviewModel,
                LocalConfettiHostState provides confettiHostState,
                LocalHapticFeedback provides customHapticFeedback,
                LocalScreenSize provides screenSize
            ).toTypedArray()
        }
    }

    CompositionLocalProvider(
        *values.value,
        content = {
            ImageToolboxThemeSurface {
                content()

                ConfettiHost()

                ToastHost()
            }
        }
    )
}

private infix fun <T : Any> ProvidableCompositionLocal<T>.providesOrNull(
    value: T?
): ProvidedValue<T>? = if (value != null) provides(value) else null