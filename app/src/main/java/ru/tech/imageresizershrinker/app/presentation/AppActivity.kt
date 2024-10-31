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

package ru.tech.imageresizershrinker.app.presentation

import android.content.Intent
import android.os.Bundle
import com.arkivanov.decompose.retainedComponent
import com.arkivanov.decompose.router.stack.push
import dagger.hilt.android.AndroidEntryPoint
import ru.tech.imageresizershrinker.core.crash.components.GlobalExceptionHandler
import ru.tech.imageresizershrinker.core.crash.components.M3Activity
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.parseImageFromIntent
import ru.tech.imageresizershrinker.core.ui.utils.provider.setContentWithWindowSizeClass
import ru.tech.imageresizershrinker.feature.root.presentation.RootContent
import ru.tech.imageresizershrinker.feature.root.presentation.viewModel.RootViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : M3Activity() {

    @Inject
    lateinit var viewModelFactory: RootViewModel.Factory

    private val viewModel: RootViewModel by lazy {
        retainedComponent(factory = viewModelFactory::invoke)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parseImage(intent)

        setContentWithWindowSizeClass {
            RootContent(viewModel = viewModel)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        parseImage(intent)
    }

    private fun parseImage(intent: Intent?) {
        parseImageFromIntent(
            intent = intent,
            onStart = viewModel::hideSelectDialog,
            onHasExtraImageType = viewModel::updateExtraImageType,
            onColdStart = viewModel::cancelShowingExitDialog,
            onGetUris = viewModel::updateUris,
            onShowToast = viewModel::showToast,
            onNavigate = { screen ->
                GlobalExceptionHandler.registerScreenOpen(screen)
                viewModel.navController.push(screen)
            },
            isHasUris = !viewModel.uris.isNullOrEmpty(),
            onWantGithubReview = viewModel::onWantGithubReview,
            isOpenEditInsteadOfPreview = viewModel.settingsState.openEditInsteadOfPreview
        )
    }
}