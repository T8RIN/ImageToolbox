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

package com.t8rin.imagetoolbox.feature.recognize.text.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.recognize.text.domain.RecognitionEngine

@Composable
fun RecognitionEngineSelector(
    value: RecognitionEngine,
    onValueChange: (RecognitionEngine) -> Unit
) {
    EnhancedButtonGroup(
        modifier = Modifier
            .fillMaxWidth()
            .container(shape = ShapeDefaults.extraLarge),
        items = RecognitionEngine.entries.map { stringResource(it.title) },
        selectedIndex = RecognitionEngine.entries.indexOf(value),
        title = stringResource(id = R.string.provider),
        onIndexChange = {
            onValueChange(RecognitionEngine.entries[it])
        },
        activeButtonColor = MaterialTheme.colorScheme.tertiaryContainer,
        isScrollable = false
    )
}

private inline val RecognitionEngine.title: Int
    get() = when (this) {
        RecognitionEngine.Tesseract -> R.string.tesseract
        RecognitionEngine.PaddleOCRv5 -> R.string.paddle_ocr_v5
        RecognitionEngine.PaddleOCRv6 -> R.string.paddle_ocr_v6
    }