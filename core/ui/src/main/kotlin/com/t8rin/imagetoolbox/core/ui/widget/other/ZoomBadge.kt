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

package com.t8rin.imagetoolbox.core.ui.widget.other

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.colors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.domain.utils.trimTrailingZero
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.onPrimaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.theme.primaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults

@Composable
fun ZoomBadge(
    zoomLevel: Float,
    modifier: Modifier,
    showAfter: Float = 1f,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainerFixed.blend(Color.Black),
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainerFixed,
    shape: Shape = ShapeDefaults.extraSmall
) {
    BoxAnimatedVisibility(
        visible = zoomLevel > showAfter,
        modifier = modifier
            .padding(
                horizontal = 24.dp,
                vertical = 8.dp
            ),
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        val level = remember(zoomLevel) {
            zoomLevel.roundToTwoDigits().toString().trimTrailingZero()
        }
        Text(
            text = stringResource(R.string.zoom) + " ${level}x",
            modifier = Modifier
                .background(
                    color = containerColor,
                    shape = shape
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelLarge,
            color = contentColor
        )
    }
}