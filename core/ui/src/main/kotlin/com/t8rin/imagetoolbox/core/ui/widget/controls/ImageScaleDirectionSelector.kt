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

package com.t8rin.imagetoolbox.core.ui.widget.controls

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleDirection
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.state.derivedValueOf
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

@Composable
fun ImageScaleDirectionSelector(
    modifier: Modifier = Modifier,
    value: ImageScaleDirection,
    onValueChange: (ImageScaleDirection) -> Unit
) {
    Column(
        modifier = modifier
            .container(shape = ShapeDefaults.extraLarge)
    ) {
        EnhancedButtonGroup(
            modifier = Modifier.padding(start = 3.dp, end = 2.dp),
            enabled = true,
            title = {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(id = R.string.scaling))
                    Spacer(modifier = Modifier.height(8.dp))
                }
            },
            onIndexChange = {
                onValueChange(ImageScaleDirection.entries[it])
            },
            selectedIndex = derivedValueOf(value) {
                ImageScaleDirection.entries.indexOfFirst { it == value }
            },
            itemCount = ImageScaleDirection.entries.size,
            itemContent = {
                val small = stringResource(R.string.small)
                val large = stringResource(R.string.large)
                Text(
                    when (ImageScaleDirection.entries[it]) {
                        ImageScaleDirection.None -> stringResource(R.string.none)
                        ImageScaleDirection.Up -> "$small -> $large"
                        ImageScaleDirection.Down -> "$large -> $small"
                    }
                )
            },
        )
    }
}