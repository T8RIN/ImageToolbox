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

package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.FinanceMode: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.FinanceMode",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(320f, 470f)
            verticalLineToRelative(-170f)
            quadToRelative(0f, -25f, 17.5f, -42.5f)
            reflectiveQuadTo(380f, 240f)
            quadToRelative(25f, 0f, 42.5f, 17.5f)
            reflectiveQuadTo(440f, 300f)
            verticalLineToRelative(170f)
            quadToRelative(0f, 25f, -17.5f, 42.5f)
            reflectiveQuadTo(380f, 530f)
            quadToRelative(-25f, 0f, -42.5f, -17.5f)
            reflectiveQuadTo(320f, 470f)
            close()
            moveTo(520f, 461f)
            verticalLineToRelative(-321f)
            quadToRelative(0f, -25f, 17.5f, -42.5f)
            reflectiveQuadTo(580f, 80f)
            quadToRelative(25f, 0f, 42.5f, 17.5f)
            reflectiveQuadTo(640f, 140f)
            verticalLineToRelative(321f)
            quadToRelative(0f, 30f, -18.5f, 45f)
            reflectiveQuadTo(580f, 521f)
            quadToRelative(-23f, 0f, -41.5f, -15f)
            reflectiveQuadTo(520f, 461f)
            close()
            moveTo(120f, 599f)
            verticalLineToRelative(-139f)
            quadToRelative(0f, -25f, 17.5f, -42.5f)
            reflectiveQuadTo(180f, 400f)
            quadToRelative(25f, 0f, 42.5f, 17.5f)
            reflectiveQuadTo(240f, 460f)
            verticalLineToRelative(139f)
            quadToRelative(0f, 30f, -18.5f, 45f)
            reflectiveQuadTo(180f, 659f)
            quadToRelative(-23f, 0f, -41.5f, -15f)
            reflectiveQuadTo(120f, 599f)
            close()
            moveTo(216f, 842f)
            quadToRelative(-26f, 0f, -36.5f, -24.5f)
            reflectiveQuadTo(188f, 774f)
            lineToRelative(164f, -164f)
            quadToRelative(11f, -11f, 26.5f, -12f)
            reflectiveQuadToRelative(27.5f, 10f)
            lineToRelative(114f, 98f)
            lineToRelative(224f, -224f)
            horizontalLineToRelative(-24f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(680f, 442f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(720f, 402f)
            horizontalLineToRelative(120f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(880f, 442f)
            verticalLineToRelative(120f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(840f, 602f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(800f, 562f)
            verticalLineToRelative(-24f)
            lineTo(550f, 788f)
            quadToRelative(-11f, 11f, -26.5f, 12f)
            reflectiveQuadTo(496f, 790f)
            lineToRelative(-114f, -98f)
            lineToRelative(-138f, 138f)
            quadToRelative(-5f, 5f, -12.5f, 8.5f)
            reflectiveQuadTo(216f, 842f)
            close()
        }
    }.build()
}