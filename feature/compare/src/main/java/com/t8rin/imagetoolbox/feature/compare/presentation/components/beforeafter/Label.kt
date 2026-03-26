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

package com.t8rin.imagetoolbox.feature.compare.presentation.components.beforeafter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Black transparent label to display before or after text
 */
@Composable
internal fun Label(
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    fontWeight: FontWeight = FontWeight.Bold,
    fontSize: TextUnit = 14.sp,
    text: String
) {
    Text(
        text = text,
        color = textColor,
        fontSize = fontSize,
        fontWeight = fontWeight,
        modifier = modifier

    )
}

internal val labelModifier =
    Modifier
        .background(Color.Black.copy(alpha = .5f), RoundedCornerShape(50))
        .padding(horizontal = 12.dp, vertical = 8.dp)

@Composable
internal fun BoxScope.BeforeLabel(
    text: String = "Before",
    textColor: Color = Color.White,
    fontWeight: FontWeight = FontWeight.Bold,
    fontSize: TextUnit = 14.sp,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter
) {
    Label(
        text = text,
        textColor = textColor,
        fontWeight = fontWeight,
        fontSize = fontSize,
        modifier = Modifier
            .padding(8.dp)
            .align(
                if (contentOrder == ContentOrder.BeforeAfter)
                    Alignment.TopStart else Alignment.TopEnd
            )
            .then(labelModifier)
    )
}

@Composable
internal fun BoxScope.AfterLabel(
    text: String = "After",
    textColor: Color = Color.White,
    fontWeight: FontWeight = FontWeight.Bold,
    fontSize: TextUnit = 14.sp,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter
) {
    Label(
        text = text,
        textColor = textColor,
        fontWeight = fontWeight,
        fontSize = fontSize,

        modifier = Modifier
            .padding(8.dp)
            .align(
                if (contentOrder == ContentOrder.BeforeAfter)
                    Alignment.TopEnd else Alignment.TopStart
            )
            .then(labelModifier)
    )
}
