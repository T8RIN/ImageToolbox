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
import androidx.compose.foundation.layout.Arrangement
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
import com.t8rin.colors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Thermostat
import com.t8rin.imagetoolbox.core.settings.domain.model.RawWhiteBalance
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

@Composable
fun RawWhiteBalanceSettingItem(
    onValueChange: (RawWhiteBalance) -> Unit,
    shape: Shape = ShapeDefaults.top,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp),
) {
    val whiteBalance = LocalSettingsState.current.rawDevelopSettings.whiteBalance
    val custom = whiteBalance as? RawWhiteBalance.Custom ?: RawWhiteBalance.Custom()
    val entries = remember(custom) {
        listOf(
            RawWhiteBalance.Camera,
            RawWhiteBalance.Auto,
            RawWhiteBalance.Daylight,
            custom
        )
    }

    Column(
        modifier = modifier.container(shape = shape)
    ) {
        DataSelector(
            value = whiteBalance,
            onValueChange = onValueChange,
            entries = entries,
            spanCount = 1,
            title = stringResource(R.string.white_balance),
            titleIcon = Icons.Outlined.Thermostat,
            itemContentText = { stringResource(it.title) },
            behaveAsContainer = false,
            modifier = Modifier.fillMaxWidth(),
            selectedItemColor = MaterialTheme.colorScheme.secondary
        )

        AnimatedVisibility(visible = whiteBalance is RawWhiteBalance.Custom) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                RawWhiteBalanceMultiplier(
                    value = custom.redMultiplier,
                    title = R.string.raw_white_balance_red,
                    shape = ShapeDefaults.top,
                    onValueChange = { onValueChange(custom.copy(redMultiplier = it)) }
                )
                RawWhiteBalanceMultiplier(
                    value = custom.greenMultiplier,
                    title = R.string.raw_white_balance_green,
                    shape = ShapeDefaults.center,
                    onValueChange = { onValueChange(custom.copy(greenMultiplier = it)) }
                )
                RawWhiteBalanceMultiplier(
                    value = custom.blueMultiplier,
                    title = R.string.raw_white_balance_blue,
                    shape = ShapeDefaults.center,
                    onValueChange = { onValueChange(custom.copy(blueMultiplier = it)) }
                )
                RawWhiteBalanceMultiplier(
                    value = custom.secondGreenMultiplier,
                    title = R.string.raw_white_balance_second_green,
                    shape = ShapeDefaults.bottom,
                    onValueChange = { onValueChange(custom.copy(secondGreenMultiplier = it)) }
                )
            }
        }
    }
}

@Composable
private fun RawWhiteBalanceMultiplier(
    value: Float,
    title: Int,
    shape: Shape,
    onValueChange: (Float) -> Unit,
) {
    var internalValue by remember(value) { mutableFloatStateOf(value) }

    EnhancedSliderItem(
        value = internalValue,
        title = stringResource(title),
        onValueChange = { internalValue = it.roundToTwoDigits() },
        onValueChangeFinished = { onValueChange(internalValue) },
        internalStateTransformation = Float::roundToTwoDigits,
        valueRange = 0.1f..4f,
        steps = 38,
        modifier = Modifier.padding(horizontal = 8.dp),
        shape = shape,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    )
}

private val RawWhiteBalance.title: Int
    get() = when (this) {
        RawWhiteBalance.Camera -> R.string.camera
        RawWhiteBalance.Auto -> R.string.auto
        RawWhiteBalance.Daylight -> R.string.raw_white_balance_daylight
        is RawWhiteBalance.Custom -> R.string.custom
    }
