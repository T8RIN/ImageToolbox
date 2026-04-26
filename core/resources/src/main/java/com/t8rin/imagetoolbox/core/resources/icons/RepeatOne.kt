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

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.RepeatOne: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.RepeatOne",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(460f, 420f)
            horizontalLineToRelative(-30f)
            quadToRelative(-13f, 0f, -21.5f, -8.5f)
            reflectiveQuadTo(400f, 390f)
            quadToRelative(0f, -13f, 8.5f, -21.5f)
            reflectiveQuadTo(430f, 360f)
            horizontalLineToRelative(50f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(520f, 400f)
            verticalLineToRelative(170f)
            quadToRelative(0f, 13f, -8.5f, 21.5f)
            reflectiveQuadTo(490f, 600f)
            quadToRelative(-13f, 0f, -21.5f, -8.5f)
            reflectiveQuadTo(460f, 570f)
            verticalLineToRelative(-150f)
            close()
            moveTo(274f, 760f)
            lineToRelative(34f, 34f)
            quadToRelative(12f, 12f, 11.5f, 28f)
            reflectiveQuadTo(308f, 850f)
            quadToRelative(-12f, 12f, -28.5f, 12.5f)
            reflectiveQuadTo(251f, 851f)
            lineTo(148f, 748f)
            quadToRelative(-6f, -6f, -8.5f, -13f)
            reflectiveQuadToRelative(-2.5f, -15f)
            quadToRelative(0f, -8f, 2.5f, -15f)
            reflectiveQuadToRelative(8.5f, -13f)
            lineToRelative(103f, -103f)
            quadToRelative(12f, -12f, 28.5f, -11.5f)
            reflectiveQuadTo(308f, 590f)
            quadToRelative(11f, 12f, 11.5f, 28f)
            reflectiveQuadTo(308f, 646f)
            lineToRelative(-34f, 34f)
            horizontalLineToRelative(406f)
            verticalLineToRelative(-120f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(720f, 520f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(760f, 560f)
            verticalLineToRelative(120f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(680f, 760f)
            lineTo(274f, 760f)
            close()
            moveTo(686f, 280f)
            lineTo(280f, 280f)
            verticalLineToRelative(120f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(240f, 440f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(200f, 400f)
            verticalLineToRelative(-120f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(280f, 200f)
            horizontalLineToRelative(406f)
            lineToRelative(-34f, -34f)
            quadToRelative(-12f, -12f, -11.5f, -28f)
            reflectiveQuadToRelative(11.5f, -28f)
            quadToRelative(12f, -12f, 28.5f, -12.5f)
            reflectiveQuadTo(709f, 109f)
            lineToRelative(103f, 103f)
            quadToRelative(6f, 6f, 8.5f, 13f)
            reflectiveQuadToRelative(2.5f, 15f)
            quadToRelative(0f, 8f, -2.5f, 15f)
            reflectiveQuadToRelative(-8.5f, 13f)
            lineTo(709f, 371f)
            quadToRelative(-12f, 12f, -28.5f, 11.5f)
            reflectiveQuadTo(652f, 370f)
            quadToRelative(-11f, -12f, -11.5f, -28f)
            reflectiveQuadToRelative(11.5f, -28f)
            lineToRelative(34f, -34f)
            close()
        }
    }.build()
}
