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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Tune
import com.t8rin.imagetoolbox.core.settings.domain.model.SemanticSearchSettings
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.media_picker.presentation.screenLogic.MediaPickerComponent
import kotlin.math.roundToInt

@Composable
internal fun MediaSemanticSearchAccuracySheet(
    visible: Boolean,
    onDismiss: (Boolean) -> Unit,
    component: MediaPickerComponent
) {
    val settings = component.settingsState.semanticSearchSettings
    var textThreshold by remember(settings.textThreshold, visible) {
        mutableFloatStateOf(settings.textThreshold)
    }
    var imageThreshold by remember(settings.imageThreshold, visible) {
        mutableFloatStateOf(settings.imageThreshold)
    }

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        title = {
            TitleItem(
                text = stringResource(R.string.gallery_search_accuracy),
                icon = Icons.Rounded.Tune
            )
        },
        confirmButton = {
            EnhancedButton(
                onClick = { onDismiss(false) },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .enhancedVerticalScroll(rememberScrollState())
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            EnhancedSliderItem(
                value = (textThreshold * 100).roundToInt(),
                title = stringResource(R.string.gallery_search_text_threshold),
                valueRange = SemanticSearchSettings.TextThresholdRange.let {
                    val start = (it.start * 100).roundToInt().toFloat()
                    val end = (it.endInclusive * 100).roundToInt().toFloat()
                    start..end
                },
                onValueChange = { textThreshold = it.roundToInt() / 100f },
                onValueChangeFinished = { value ->
                    val threshold = value.roundToInt() / 100f
                    component.updateSemanticSearchSettings {
                        it.copy(textThreshold = threshold)
                    }
                },
                internalStateTransformation = Float::roundToInt,
                valueSuffix = "%",
                shape = ShapeDefaults.top,
                modifier = Modifier.fillMaxWidth()
            )
            EnhancedSliderItem(
                value = (imageThreshold * 100).roundToInt(),
                title = stringResource(R.string.gallery_search_image_threshold),
                valueRange = SemanticSearchSettings.ImageThresholdRange.let {
                    val start = (it.start * 100).roundToInt().toFloat()
                    val end = (it.endInclusive * 100).roundToInt().toFloat()
                    start..end
                },
                onValueChange = { imageThreshold = it.roundToInt() / 100f },
                onValueChangeFinished = { value ->
                    val threshold = value.roundToInt() / 100f
                    component.updateSemanticSearchSettings {
                        it.copy(imageThreshold = threshold)
                    }
                },
                internalStateTransformation = Float::roundToInt,
                valueSuffix = "%",
                shape = ShapeDefaults.bottom,
                modifier = Modifier.fillMaxWidth()
            )
            InfoContainer(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(0.5f),
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.85f),
                text = stringResource(R.string.gallery_search_threshold_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
    }
}