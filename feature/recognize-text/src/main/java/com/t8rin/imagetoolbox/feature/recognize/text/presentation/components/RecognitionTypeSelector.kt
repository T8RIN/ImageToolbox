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

package com.t8rin.imagetoolbox.feature.recognize.text.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.recognize.text.domain.RecognitionType

@Composable
fun RecognitionTypeSelector(
    value: RecognitionType,
    onValueChange: (RecognitionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .container(shape = ShapeDefaults.extraLarge)
            .animateContentSizeNoClip(),
        contentAlignment = Alignment.Center
    ) {
        EnhancedButtonGroup(
            modifier = Modifier.padding(8.dp),
            enabled = true,
            items = RecognitionType.entries.map { it.translatedName },
            selectedIndex = RecognitionType.entries.indexOf(value),
            title = stringResource(id = R.string.recognition_type),
            onIndexChange = {
                onValueChange(RecognitionType.entries[it])
            }
        )
    }
}

private val RecognitionType.translatedName: String
    @Composable
    get() = when (this) {
        RecognitionType.Best -> stringResource(id = R.string.best)
        RecognitionType.Fast -> stringResource(id = R.string.fast)
        RecognitionType.Standard -> stringResource(id = R.string.standard)
    }