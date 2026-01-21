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

package com.t8rin.imagetoolbox.feature.image_stitch.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.image_stitch.domain.StitchFadeSide

@Composable
fun ImageFadingEdgesSelector(
    modifier: Modifier = Modifier,
    value: StitchFadeSide,
    onValueChange: (StitchFadeSide) -> Unit
) {
    EnhancedButtonGroup(
        modifier = modifier
            .container(shape = ShapeDefaults.extraLarge),
        title = stringResource(id = R.string.fading_edges),
        entries = StitchFadeSide.entries,
        value = value,
        onValueChange = onValueChange,
        itemContent = {
            Text(it.title())
        }
    )
}

@Composable
private fun StitchFadeSide.title() = when (this) {
    StitchFadeSide.None -> stringResource(R.string.disabled)
    StitchFadeSide.Start -> stringResource(R.string.start)
    StitchFadeSide.End -> stringResource(R.string.end)
    StitchFadeSide.Both -> stringResource(R.string.both)
}