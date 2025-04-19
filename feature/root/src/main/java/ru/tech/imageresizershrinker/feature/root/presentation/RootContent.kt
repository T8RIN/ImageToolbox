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
import ru.tech.imageresizershrinker.core.ui.utils.provider.ImageToolboxCompositionLocals
import ru.tech.imageresizershrinker.feature.root.presentation.components.RootDialogs
import ru.tech.imageresizershrinker.feature.root.presentation.components.ScreenSelector
import ru.tech.imageresizershrinker.feature.root.presentation.components.dialogs.AppExitDialog
import ru.tech.imageresizershrinker.feature.root.presentation.components.utils.HandleUpdateSearching
import ru.tech.imageresizershrinker.feature.root.presentation.components.utils.SuccessRestoreBackup
import ru.tech.imageresizershrinker.feature.root.presentation.components.utils.uiSettingsState
import ru.tech.imageresizershrinker.feature.root.presentation.screenLogic.RootComponent

@Composable
fun RootContent(
    component: RootComponent
) {
    ImageToolboxCompositionLocals(
        settingsState = component.uiSettingsState(),
        toastHostState = component.toastHostState,
        filterPreviewModel = component.filterPreviewModel,
        simpleSettingsInteractor = component.simpleSettingsInteractor
    ) {
        AppExitDialog(component)

        ScreenSelector(component)

        RootDialogs(component)

        SuccessRestoreBackup(component)

        HandleUpdateSearching(component)
    }
}