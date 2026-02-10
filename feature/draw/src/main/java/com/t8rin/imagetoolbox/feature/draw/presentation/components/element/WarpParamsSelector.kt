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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.element

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.extendedcolors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.domain.utils.safeCast
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.WarpMode

@Composable
internal fun WarpParamsSelector(
    value: DrawMode,
    onValueChange: (DrawMode) -> Unit
) {
    AnimatedVisibility(
        visible = value is DrawMode.Warp,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column {
            EnhancedSliderItem(
                value = value.safeCast<DrawMode.Warp>()?.strength ?: 0f,
                title = stringResource(R.string.strength),
                valueRange = 0f..1f,
                internalStateTransformation = {
                    it.roundToTwoDigits()
                },
                onValueChange = {
                    onValueChange(
                        value.safeCast<DrawMode.Warp>()?.copy(
                            strength = it.roundToTwoDigits()
                        ) ?: value
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = ShapeDefaults.top
            )
            Spacer(modifier = Modifier.height(4.dp))
            EnhancedSliderItem(
                value = value.safeCast<DrawMode.Warp>()?.hardness ?: 0f,
                title = stringResource(R.string.hardness),
                valueRange = 0f..1f,
                internalStateTransformation = {
                    it.roundToTwoDigits()
                },
                onValueChange = {
                    onValueChange(
                        value.safeCast<DrawMode.Warp>()?.copy(
                            hardness = it.roundToTwoDigits()
                        ) ?: value
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = ShapeDefaults.center
            )
            Spacer(modifier = Modifier.height(4.dp))
            EnhancedButtonGroup(
                enabled = true,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .container(
                        shape = ShapeDefaults.bottom,
                        color = MaterialTheme.colorScheme.surface
                    ),
                itemCount = WarpMode.entries.size,
                title = {
                    Text(
                        text = stringResource(R.string.warp_mode),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                },
                selectedIndex = WarpMode.entries.indexOfFirst {
                    value.safeCast<DrawMode.Warp>()?.warpMode == it
                },
                itemContent = {
                    Text(
                        text = stringResource(WarpMode.entries[it].title())
                    )
                },
                onIndexChange = {
                    onValueChange(
                        value.safeCast<DrawMode.Warp>()?.copy(
                            warpMode = WarpMode.entries[it]
                        ) ?: value
                    )
                },
                inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private fun WarpMode.title(): Int = when (this) {
    WarpMode.MOVE -> R.string.warp_mode_move
    WarpMode.GROW -> R.string.warp_mode_grow
    WarpMode.SHRINK -> R.string.warp_mode_shrink
    WarpMode.SWIRL_CW -> R.string.warp_mode_swirl_cw
    WarpMode.SWIRL_CCW -> R.string.warp_mode_swirl_ccw
    WarpMode.MIXING -> R.string.mix
}

@Composable
@Preview
private fun Preview() = ImageToolboxThemeForPreview(true) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        WarpParamsSelector(
            value = DrawMode.Warp(),
            onValueChange = {}
        )
    }
}