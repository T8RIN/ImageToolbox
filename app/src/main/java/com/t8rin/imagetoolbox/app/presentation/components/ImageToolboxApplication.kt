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

package com.t8rin.imagetoolbox.app.presentation.components

import android.app.Application
import androidx.compose.foundation.ComposeFoundationFlags
import com.arkivanov.decompose.DecomposeExperimentFlags
import com.t8rin.imagetoolbox.app.presentation.components.utils.attachLogWriter
import com.t8rin.imagetoolbox.app.presentation.components.utils.initOpenCV
import com.t8rin.imagetoolbox.app.presentation.components.utils.registerSecurityProviders
import com.t8rin.imagetoolbox.core.crash.presentation.components.applyGlobalExceptionHandler
import com.t8rin.imagetoolbox.core.ui.utils.initAppContext
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class ImageToolboxApplication : Application() {

    init {
        DecomposeExperimentFlags.duplicateConfigurationsEnabled = true
        ComposeFoundationFlags.isPausableCompositionInPrefetchEnabled = true
        registerSecurityProviders()
    }

    override fun onCreate() {
        super.onCreate()
        initAppContext()
        initOpenCV()
        attachLogWriter()
        applyGlobalExceptionHandler()
    }

}