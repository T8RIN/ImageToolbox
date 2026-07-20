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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Highlight
import com.t8rin.imagetoolbox.core.settings.domain.model.RawHighlightRecovery
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import kotlin.math.roundToInt

@Composable
fun RawHighlightRecoverySettingItem(
    onValueChange: (RawHighlightRecovery) -> Unit,
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp),
) {
    val recovery = LocalSettingsState.current.rawDevelopSettings.highlightRecovery
    val reconstruct = recovery as? RawHighlightRecovery.Reconstruct
        ?: RawHighlightRecovery.Reconstruct()
    val entries = remember(reconstruct) {
        listOf(
            RawHighlightRecovery.Clip,
            RawHighlightRecovery.Unclip,
            RawHighlightRecovery.Blend,
            reconstruct
        )
    }
    var level by remember(reconstruct.level) { mutableFloatStateOf(reconstruct.level.toFloat()) }

    Column(
        modifier = modifier.container(shape = shape)
    ) {
        DataSelector(
            value = recovery,
            onValueChange = onValueChange,
            entries = entries,
            spanCount = 1,
            title = stringResource(R.string.raw_highlight_recovery),
            titleIcon = Icons.Outlined.Highlight,
            itemContentText = { stringResource(it.title) },
            behaveAsContainer = false,
            modifier = Modifier.fillMaxWidth(),
            selectedItemColor = MaterialTheme.colorScheme.secondary
        )

        AnimatedVisibility(visible = recovery is RawHighlightRecovery.Reconstruct) {
            EnhancedSliderItem(
                modifier = Modifier.padding(8.dp),
                shape = ShapeDefaults.default,
                value = level,
                title = stringResource(R.string.raw_highlight_reconstruction_level),
                onValueChange = { level = it.roundToInt().toFloat() },
                onValueChangeFinished = {
                    onValueChange(RawHighlightRecovery.Reconstruct(level.roundToInt()))
                },
                internalStateTransformation = Float::roundToInt,
                valueRange = 3f..9f,
                steps = 5,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        }
    }
}

private val RawHighlightRecovery.title: Int
    get() = when (this) {
        RawHighlightRecovery.Clip -> R.string.raw_highlight_recovery_clip
        RawHighlightRecovery.Unclip -> R.string.raw_highlight_recovery_unclip
        RawHighlightRecovery.Blend -> R.string.raw_highlight_recovery_blend
        is RawHighlightRecovery.Reconstruct -> R.string.raw_highlight_recovery_reconstruct
    }
