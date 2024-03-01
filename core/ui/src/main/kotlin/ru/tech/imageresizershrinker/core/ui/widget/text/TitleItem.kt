/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.widget.text

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.ui.shapes.IconShapeContainer
import ru.tech.imageresizershrinker.core.ui.theme.suggestContainerColorBy

@Composable
fun TitleItem(
    icon: ImageVector? = null,
    text: String,
    endContent: @Composable RowScope.() -> Unit,
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier.padding(16.dp),
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let { icon ->
            IconShapeContainer(
                enabled = true,
                underlyingColor = MaterialTheme.colorScheme.suggestContainerColorBy(
                    LocalContentColor.current
                ),
                content = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(text = text, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        endContent.let {
            Spacer(Modifier.width(8.dp))
            it()
        }
    }
}


@Composable
fun TitleItem(
    icon: ImageVector? = null,
    text: String,
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier.padding(16.dp),
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let { icon ->
            IconShapeContainer(
                enabled = true,
                underlyingColor = MaterialTheme.colorScheme.suggestContainerColorBy(
                    LocalContentColor.current
                ),
                content = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}