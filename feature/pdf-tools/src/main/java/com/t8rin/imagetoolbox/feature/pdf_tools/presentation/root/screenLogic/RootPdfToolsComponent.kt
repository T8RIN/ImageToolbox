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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.screenLogic

import android.net.Uri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class RootPdfToolsComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    fun navigate(screen: Screen, tempSelectionUri: Uri?) {
        onNavigate(
            when (screen) {
                is Screen.PdfTools.ExtractPages -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.Merge -> screen.copy(uris = tempSelectionUri?.let(::listOf))
                is Screen.PdfTools.RemovePages -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.Split -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.Rotate -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.Rearrange -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.Crop -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.PageNumbers -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.Watermark -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.Signature -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.Compress -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.Flatten -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.Print -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.Grayscale -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.Repair -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.Protect -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.Unlock -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.Metadata -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.ExtractImages -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.OCR -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.ZipConvert -> screen.copy(uri = tempSelectionUri)
                is Screen.PdfTools.Preview -> screen.copy(uri = tempSelectionUri)
                else -> screen
            }
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): RootPdfToolsComponent
    }

}