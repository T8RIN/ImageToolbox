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

package com.t8rin.imagetoolbox.feature.root.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.ImageToolboxCompositionLocals
import com.t8rin.imagetoolbox.feature.root.presentation.components.RootDialogs
import com.t8rin.imagetoolbox.feature.root.presentation.components.ScreenSelector
import com.t8rin.imagetoolbox.feature.root.presentation.components.utils.uiSettingsState
import com.t8rin.imagetoolbox.feature.root.presentation.screenLogic.RootComponent

@Composable
fun RootContent(
    component: RootComponent
) {
    val stack by component.childStack.subscribeAsState()

    ImageToolboxCompositionLocals(
        settingsState = component.uiSettingsState(),
        toastHostState = component.toastHostState,
        filterPreviewModel = component.filterPreviewModel,
        canSetDynamicFilterPreview = component.canSetDynamicFilterPreview,
        currentScreen = stack.items.lastOrNull()?.configuration
    ) {
        ScreenSelector(component)

        RootDialogs(component)
    }
}