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

package com.t8rin.imagetoolbox.core.ui.widget.enhanced

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BadgeDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults

@Composable
fun EnhancedBadge(
    modifier: Modifier = Modifier,
    containerColor: Color = BadgeDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    shape: Shape = ShapeDefaults.circle,
    content: @Composable (RowScope.() -> Unit)? = null,
) {
    val size = if (content != null) 16.dp else 6.dp

    Row(
        modifier = modifier
            .defaultMinSize(minWidth = size, minHeight = size)
            .background(color = containerColor, shape = shape)
            .then(
                if (content != null)
                    Modifier.padding(horizontal = 4.dp)
                else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (content != null) {
            val mergedStyle = LocalTextStyle.current.merge(MaterialTheme.typography.labelSmall)
            CompositionLocalProvider(
                LocalContentColor provides contentColor,
                LocalTextStyle provides mergedStyle,
                content = {
                    content()
                }
            )
        }
    }
}