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

package com.t8rin.imagetoolbox.app.presentation.components

import com.t8rin.imagetoolbox.app.presentation.components.functions.attachLogWriter
import com.t8rin.imagetoolbox.app.presentation.components.functions.initAI
import com.t8rin.imagetoolbox.app.presentation.components.functions.initColorNames
import com.t8rin.imagetoolbox.app.presentation.components.functions.initOpenCV
import com.t8rin.imagetoolbox.app.presentation.components.functions.initPdfBox
import com.t8rin.imagetoolbox.app.presentation.components.functions.initQrScanner
import com.t8rin.imagetoolbox.app.presentation.components.functions.injectBaseComponent
import com.t8rin.imagetoolbox.app.presentation.components.functions.registerSecurityProviders
import com.t8rin.imagetoolbox.app.presentation.components.functions.setupFlags
import com.t8rin.imagetoolbox.app.presentation.components.utils.isMain
import com.t8rin.imagetoolbox.core.crash.presentation.components.applyGlobalExceptionHandler
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.core.ui.utils.ComposeApplication
import com.t8rin.imagetoolbox.core.utils.initAppContext
import dagger.hilt.android.HiltAndroidApp
import io.ktor.client.HttpClient
import javax.inject.Inject


@HiltAndroidApp
class ImageToolboxApplication : ComposeApplication() {

    @Inject
    lateinit var keepAliveService: KeepAliveService

    @Inject
    lateinit var httpClient: HttpClient

    private var isSetupCompleted: Boolean = false

    override fun onCreate() {
        super.onCreate()
        runSetup()
    }

    override fun runSetup() {
        if (isSetupCompleted) return

        if (isMain()) {
            setupFlags()
            initAppContext()
            initOpenCV()
            initAI()
            initColorNames()
            initQrScanner()
            attachLogWriter()
            applyGlobalExceptionHandler()
            registerSecurityProviders()
            initPdfBox()
            injectBaseComponent()

            isSetupCompleted = true
        }
    }

}