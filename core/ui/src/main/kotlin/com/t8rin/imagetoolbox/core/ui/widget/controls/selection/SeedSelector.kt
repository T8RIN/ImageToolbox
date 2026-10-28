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

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.di.entryPoint
import com.t8rin.imagetoolbox.core.domain.saving.FileControllerEventEmitter
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.SettingsEthernet
import com.t8rin.imagetoolbox.core.resources.icons.Shuffle
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.theme.PreviewFocusFix
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.utils.appContext
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.random.Random

@Composable
fun SeedSelector(
    value: Number,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.seed),
    icon: ImageVector? = Icons.Rounded.SettingsEthernet,
    roundTo: Int = 0,
    shape: Shape = ShapeDefaults.default,
    enabled: Boolean = true,
    containerColor: Color = Color.Unspecified,
    behaveAsContainer: Boolean = true
) {
    val settingsState = LocalSettingsState.current
    val settingsInteractor = LocalSimpleSettingsInteractor.current
    val scope = rememberCoroutineScope()
    val currentValue = remember(value) { mutableFloatStateOf(value.toFloat()) }
    val randomizeSeed by rememberUpdatedState {
        val seed = Random.seed(
            value = currentValue.floatValue,
            valueRange = valueRange,
            roundTo = roundTo
        )
        currentValue.floatValue = seed
        onValueChange(seed)
    }

    LaunchedEffect(enabled, settingsState.randomizeSeedAfterSave) {
        if (enabled && settingsState.randomizeSeedAfterSave) {
            appContext.entryPoint<SeedSelectorEntryPoint>()
                .fileControllerEventEmitter.successfulSaveEvents.collect {
                    randomizeSeed()
                }
        }
    }

    EnhancedSliderItem(
        value = value,
        title = title,
        modifier = modifier,
        icon = icon,
        valueRange = valueRange,
        onValueChange = {
            onValueChange(it.roundTo(roundTo))
        },
        internalStateTransformation = {
            it.roundTo(roundTo)
        },
        shape = shape,
        enabled = enabled,
        containerColor = containerColor,
        behaveAsContainer = behaveAsContainer,
        additionalContent = {
            PreferenceRowSwitch(
                modifier = Modifier.padding(8.dp),
                title = stringResource(R.string.randomize_seed_after_save),
                checked = settingsState.randomizeSeedAfterSave,
                enabled = enabled,
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                applyHorizontalPadding = false,
                onClick = {
                    scope.launch {
                        settingsInteractor.toggleRandomizeSeedAfterSave()
                    }
                }
            )
        },
        endContent = {
            EnhancedIconButton(
                onClick = randomizeSeed,
                forceMinimumInteractiveComponentSize = false,
                modifier = Modifier
                    .padding(
                        top = 8.dp
                    )
                    .size(32.dp),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(0.8f),
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.9f),
                enabled = enabled
            ) {
                Icon(
                    imageVector = Icons.Rounded.Shuffle,
                    contentDescription = stringResource(R.string.shuffle),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
}

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface SeedSelectorEntryPoint {
    val fileControllerEventEmitter: FileControllerEventEmitter
}

@Composable
fun isSeedString(@StringRes title: Int?): Boolean {
    val value = stringResource(title ?: return false)
    val seed = stringResource(R.string.seed)

    return value == seed
}

internal fun Random.seed(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    roundTo: Int
): Float {
    val multiplier = 10.0.pow(roundTo)
    val min = ceil(valueRange.start * multiplier).toLong()
    val max = floor(valueRange.endInclusive * multiplier).toLong()

    if (min >= max) return (min / multiplier).toFloat()

    val current = (value * multiplier).roundToLong().coerceIn(min, max)
    val candidate = min + nextLong(max - min)
    val result = if (candidate >= current) candidate + 1 else candidate

    return (result / multiplier).toFloat()
}

@Composable
@Preview
private fun Preview() = ImageToolboxThemeForPreview(null) {
    PreviewFocusFix()
    SeedSelector(
        value = 121,
        valueRange = -10000f..10000f,
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
@Preview
private fun Preview1() = ImageToolboxThemeForPreview(
    isDarkTheme = null,
    mapSettings = { it.copy(isCompactSelectorsLayout = true) }
) {
    PreviewFocusFix()
    SeedSelector(
        value = 121,
        valueRange = -10000f..10000f,
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}