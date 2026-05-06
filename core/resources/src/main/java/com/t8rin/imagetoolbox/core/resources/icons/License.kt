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

val Icons.Outlined.License: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.License",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(395f, 485f)
            quadToRelative(-35f, -35f, -35f, -85f)
            reflectiveQuadToRelative(35f, -85f)
            quadToRelative(35f, -35f, 85f, -35f)
            reflectiveQuadToRelative(85f, 35f)
            quadToRelative(35f, 35f, 35f, 85f)
            reflectiveQuadToRelative(-35f, 85f)
            quadToRelative(-35f, 35f, -85f, 35f)
            reflectiveQuadToRelative(-85f, -35f)
            close()
            moveTo(480f, 840f)
            lineTo(293f, 902f)
            quadToRelative(-20f, 7f, -36.5f, -5f)
            reflectiveQuadTo(240f, 865f)
            verticalLineToRelative(-254f)
            quadToRelative(-38f, -42f, -59f, -96f)
            reflectiveQuadToRelative(-21f, -115f)
            quadToRelative(0f, -134f, 93f, -227f)
            reflectiveQuadToRelative(227f, -93f)
            quadToRelative(134f, 0f, 227f, 93f)
            reflectiveQuadToRelative(93f, 227f)
            quadToRelative(0f, 61f, -21f, 115f)
            reflectiveQuadToRelative(-59f, 96f)
            verticalLineToRelative(254f)
            quadToRelative(0f, 20f, -16.5f, 32f)
            reflectiveQuadTo(667f, 902f)
            lineToRelative(-187f, -62f)
            close()
            moveTo(650f, 570f)
            quadToRelative(70f, -70f, 70f, -170f)
            reflectiveQuadToRelative(-70f, -170f)
            quadToRelative(-70f, -70f, -170f, -70f)
            reflectiveQuadToRelative(-170f, 70f)
            quadToRelative(-70f, 70f, -70f, 170f)
            reflectiveQuadToRelative(70f, 170f)
            quadToRelative(70f, 70f, 170f, 70f)
            reflectiveQuadToRelative(170f, -70f)
            close()
            moveTo(320f, 801f)
            lineToRelative(160f, -41f)
            lineToRelative(160f, 41f)
            verticalLineToRelative(-124f)
            quadToRelative(-35f, 20f, -75.5f, 31.5f)
            reflectiveQuadTo(480f, 720f)
            quadToRelative(-44f, 0f, -84.5f, -11.5f)
            reflectiveQuadTo(320f, 677f)
            verticalLineToRelative(124f)
            close()
            moveTo(480f, 739f)
            close()
        }
    }.build()
}
