/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.watermarking.presentation.components.selectors

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TextRotationAngleup
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.AlphaSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.BlendingModeSelector
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.feature.watermarking.domain.WatermarkParams
import ru.tech.imageresizershrinker.feature.watermarking.domain.digitalParams
import ru.tech.imageresizershrinker.feature.watermarking.domain.isStamp
import kotlin.math.roundToInt

@Composable
internal fun CommonParamsContent(
    params: WatermarkParams,
    onValueChange: (WatermarkParams) -> Unit
) {
    val digitalParams = params.watermarkingType.digitalParams()
    val isInvisible = digitalParams?.isInvisible == true
    val isNotStampAndInvisible = !params.watermarkingType.isStamp() && !isInvisible


    AnimatedVisibility(
        visible = !params.isRepeated && isNotStampAndInvisible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column {
            EnhancedSliderItem(
                value = params.positionX,
                title = stringResource(id = R.string.offset_x),
                internalStateTransformation = {
                    it.roundToTwoDigits()
                },
                onValueChange = {
                    onValueChange(params.copy(positionX = it))
                },
                valueRange = 0f..1f,
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.height(4.dp))
            EnhancedSliderItem(
                value = params.positionY,
                title = stringResource(id = R.string.offset_y),
                internalStateTransformation = {
                    it.roundToTwoDigits()
                },
                onValueChange = {
                    onValueChange(params.copy(positionY = it))
                },
                valueRange = 0f..1f,
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }

    AnimatedVisibility(
        visible = isNotStampAndInvisible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        EnhancedSliderItem(
            value = params.rotation,
            icon = Icons.Outlined.TextRotationAngleup,
            title = stringResource(id = R.string.angle),
            valueRange = 0f..360f,
            internalStateTransformation = Float::roundToInt,
            onValueChange = {
                onValueChange(params.copy(rotation = it.roundToInt()))
            },
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        )
    }

    AnimatedVisibility(
        visible = !isInvisible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        AlphaSelector(
            value = params.alpha,
            onValueChange = {
                onValueChange(params.copy(alpha = it))
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        )
    }

    AnimatedVisibility(
        visible = isNotStampAndInvisible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column {
            PreferenceRowSwitch(
                title = stringResource(id = R.string.repeat_watermark),
                subtitle = stringResource(id = R.string.repeat_watermark_sub),
                checked = params.isRepeated,
                startIcon = Icons.Rounded.Repeat,
                onClick = {
                    onValueChange(params.copy(isRepeated = it))
                },
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface
            )
            Spacer(Modifier.height(4.dp))
            BlendingModeSelector(
                value = params.overlayMode,
                onValueChange = {
                    onValueChange(
                        params.copy(overlayMode = it)
                    )
                }
            )
        }
    }
}