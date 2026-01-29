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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.model.ShapeType
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop

@Composable
fun CornersSizeSettingItem(
    onValueChange: (ShapeType) -> Unit,
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier
        .padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    val settingsInteractor = LocalSimpleSettingsInteractor.current

    var value by remember {
        mutableFloatStateOf(settingsState.shapesType.strength)
    }

    var isSwapped by rememberSaveable {
        mutableStateOf(false)
    }
    var trigger by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(settingsState.shapesType) {
        snapshotFlow { trigger }
            .drop(1)
            .collectLatest {
                if (settingsState.shapesType is ShapeType.Smooth && settingsState.drawContainerShadows && value > 1f) {
                    settingsInteractor.setBorderWidth(0.2f)
                    isSwapped = true
                } else if (isSwapped && settingsState.borderWidth.value == 0.2f) {
                    settingsInteractor.setBorderWidth(0f)
                    isSwapped = false
                }
                onValueChange(settingsState.shapesType.copy(value))
            }
    }

    EnhancedSliderItem(
        modifier = modifier,
        shape = shape,
        value = value,
        title = stringResource(R.string.corners_size),
        icon = Icons.Outlined.Percent,
        onValueChange = {
            value = it.roundToTwoDigits()
        },
        onValueChangeFinished = {
            trigger++
        },
        internalStateTransformation = {
            it.roundToTwoDigits()
        },
        valueRange = 0f..1.35f
    )
}