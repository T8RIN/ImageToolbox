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

package ru.tech.imageresizershrinker.feature.root.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.core.crash.components.GlobalExceptionHandler
import ru.tech.imageresizershrinker.core.resources.emoji.Emoji
import ru.tech.imageresizershrinker.core.settings.presentation.model.toUiState
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.IconShapeDefaults
import ru.tech.imageresizershrinker.core.ui.theme.ImageToolboxThemeSurface
import ru.tech.imageresizershrinker.core.ui.utils.confetti.ConfettiHost
import ru.tech.imageresizershrinker.core.ui.utils.provider.ImageToolboxCompositionLocals
import ru.tech.imageresizershrinker.core.ui.widget.other.SecureModeHandler
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastHost
import ru.tech.imageresizershrinker.feature.root.presentation.components.RootDialogs
import ru.tech.imageresizershrinker.feature.root.presentation.components.ScreenSelector
import ru.tech.imageresizershrinker.feature.root.presentation.components.dialogs.AppExitDialog
import ru.tech.imageresizershrinker.feature.root.presentation.screenLogic.RootComponent

@Composable
fun RootContent(
    component: RootComponent
) {
    var randomEmojiKey by remember {
        mutableIntStateOf(0)
    }

    val currentDestination = component.childStack.subscribeAsState().value.items
    LaunchedEffect(currentDestination) {
        delay(200L) // Delay for transition
        randomEmojiKey++
    }

    SideEffect(effect = component::tryGetUpdate)

    ImageToolboxCompositionLocals(
        settingsState = component.settingsState.toUiState(
            allEmojis = Emoji.allIcons(),
            allIconShapes = IconShapeDefaults.shapes,
            randomEmojiKey = randomEmojiKey,
            getEmojiColorTuple = component::getColorTupleFromEmoji
        ),
        toastHostState = component.toastHostState,
        simpleSettingsInteractor = component.simpleSettingsInteractor
    ) {
        SecureModeHandler()

        val settingsState = LocalSettingsState.current

        LaunchedEffect(settingsState) {
            GlobalExceptionHandler.setAllowCollectCrashlytics(settingsState.allowCollectCrashlytics)
            GlobalExceptionHandler.setAnalyticsCollectionEnabled(settingsState.allowCollectAnalytics)
        }

        ImageToolboxThemeSurface {
            AppExitDialog(component)

            ScreenSelector(component)

            RootDialogs(component)

            ConfettiHost()

            ToastHost()
        }
    }

}