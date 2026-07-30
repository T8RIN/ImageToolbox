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

package com.t8rin.imagetoolbox.feature.curves.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.t8rin.curves.ImageCurvesEditorState
import com.t8rin.curves.ImageCurvesPreview
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker

@Composable
internal fun CurvesLivePreview(
    bitmap: Bitmap?,
    state: ImageCurvesEditorState,
    imageInside: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.then(
                if (!imageInside) {
                    Modifier.padding(
                        bottom = WindowInsets
                            .navigationBars
                            .asPaddingValues()
                            .calculateBottomPadding()
                    )
                } else {
                    Modifier
                }
            ),
            contentAlignment = Alignment.Center
        ) {
            bitmap?.let { image ->
                Box(
                    modifier = Modifier.container(),
                    contentAlignment = Alignment.Center
                ) {
                    ImageCurvesPreview(
                        bitmap = image,
                        state = state,
                        modifier = Modifier
                            .aspectRatio(image.safeAspectRatio)
                            .clip(MaterialTheme.shapes.medium)
                            .transparencyChecker()
                    )
                }
            }
            if (isLoading) {
                EnhancedLoadingIndicator()
            }
        }
    }
}
