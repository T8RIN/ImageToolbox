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
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olshevski.navigation.reimagined.navigate
import ru.tech.imageresizershrinker.app.presentation.components.AppContent
import ru.tech.imageresizershrinker.core.crash.components.M3Activity
import ru.tech.imageresizershrinker.core.filters.domain.FavoriteFiltersInteractor
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.parseImageFromIntent
import ru.tech.imageresizershrinker.core.ui.widget.utils.setContentWithWindowSizeClass
import ru.tech.imageresizershrinker.feature.main.presentation.viewModel.MainViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : M3Activity() {

    private val viewModel by viewModels<MainViewModel>()

    @Inject
    lateinit var favoriteFiltersInteractor: FavoriteFiltersInteractor<Bitmap>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parseImage(intent)

        setContentWithWindowSizeClass {
            AppContent(viewModel = viewModel)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        parseImage(intent)
    }

    private fun parseImage(intent: Intent?) {
        parseImageFromIntent(
            onStart = viewModel::hideSelectDialog,
            onHasExtraImageType = viewModel::updateExtraImageType,
            onColdStart = {
                viewModel.shouldShowExitDialog(false)
            },
            onGetUris = viewModel::updateUris,
            showToast = viewModel::showToast,
            navigate = viewModel.navController::navigate,
            notHasUris = viewModel.uris.isNullOrEmpty(),
            intent = intent,
            onWantGithubReview = viewModel::onWantGithubReview
        )
    }
}