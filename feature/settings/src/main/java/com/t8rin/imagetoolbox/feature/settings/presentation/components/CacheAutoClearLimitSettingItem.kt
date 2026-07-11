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

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.humanFileSize
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.DataSaverOff
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import kotlinx.collections.immutable.toPersistentMap
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun CacheAutoClearLimitSettingItem(
    onValueChange: (Long) -> Unit,
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    var selectedIndex by remember(settingsState.cacheAutoClearLimitBytes) {
        mutableIntStateOf(settingsState.cacheAutoClearLimitBytes.closestCacheLimitIndex())
    }

    val valuesPreviewMapping = remember {
        CacheLimits.mapIndexed { index, bytes ->
            index.toFloat() to humanFileSize(bytes)
        }.toMap().toPersistentMap()
    }

    EnhancedSliderItem(
        value = selectedIndex,
        title = stringResource(R.string.cache_auto_clear_limit),
        modifier = modifier,
        icon = Icons.Rounded.DataSaverOff,
        valueRange = 0f..CacheLimits.lastIndex.toFloat(),
        onValueChange = {
            selectedIndex = it.toCacheLimitIndex()
        },
        onValueChangeFinished = {
            onValueChange(CacheLimits[selectedIndex])
        },
        steps = CacheLimits.size - 2,
        internalStateTransformation = Float::toCacheLimitIndex,
        shape = shape,
        valueTextTapEnabled = false,
        valuesPreviewMapping = valuesPreviewMapping,
        enabled = settingsState.clearCacheOnLaunch,
        canInputValue = false
    )
}

private fun Long.closestCacheLimitIndex(): Int = CacheLimits.indices.minByOrNull { index ->
    abs(CacheLimits[index] - this)
} ?: 0

private fun Float.toCacheLimitIndex(): Int = roundToInt().coerceIn(CacheLimits.indices)

private val CacheLimits = listOf(
    20L * 1024 * 1024,
    50L * 1024 * 1024,
    100L * 1024 * 1024,
    250L * 1024 * 1024,
    500L * 1024 * 1024,
    1024L * 1024 * 1024,
    2L * 1024 * 1024 * 1024
)