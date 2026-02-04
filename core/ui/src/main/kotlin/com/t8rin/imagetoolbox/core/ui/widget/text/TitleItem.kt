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

package com.t8rin.imagetoolbox.core.ui.widget.text

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.resources.icons.Firebase
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.IconShapeContainer
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.IconShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults

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
    modifier: Modifier = Modifier.padding(16.dp),
    subtitle: String? = null,
    iconEndPadding: Dp = 8.dp,
    endContent: (@Composable RowScope.() -> Unit)? = null,
    iconContainerColor: Color = IconShapeDefaults.containerColor,
    iconContentColor: Color = IconShapeDefaults.contentColor,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let { icon ->
            IconShapeContainer(
                enabled = true,
                content = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (icon == Icons.TwoTone.Firebase) Color.Unspecified
                        else LocalContentColor.current
                    )
                },
                containerColor = iconContainerColor,
                contentColor = iconContentColor
            )
            Spacer(Modifier.width(iconEndPadding))
        }
        Column(
            modifier = Modifier.weight(1f, endContent != null)
        ) {
            Text(
                text = text,
                style = PreferenceItemDefaults.TitleFontStyle
            )
            subtitle?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 14.sp,
                    color = LocalContentColor.current.copy(alpha = 0.5f)
                )
            }
        }
        endContent?.let {
            Spacer(Modifier.width(8.dp))
            it()
        }
    }
}