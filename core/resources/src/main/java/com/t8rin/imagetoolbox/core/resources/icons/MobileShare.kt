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

import com.t8rin.imagetoolbox.core.resources.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.MobileShare: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.MobileShare",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
        autoMirror = true
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(320f, 640f)
            lineTo(400f, 640f)
            lineTo(400f, 520f)
            quadTo(400f, 520f, 400f, 520f)
            quadTo(400f, 520f, 400f, 520f)
            lineTo(486f, 520f)
            lineTo(444f, 564f)
            lineTo(500f, 620f)
            lineTo(640f, 480f)
            lineTo(500f, 340f)
            lineTo(444f, 396f)
            lineTo(486f, 440f)
            lineTo(400f, 440f)
            quadTo(367f, 440f, 343.5f, 463.5f)
            quadTo(320f, 487f, 320f, 520f)
            lineTo(320f, 640f)
            close()
            moveTo(280f, 920f)
            quadTo(247f, 920f, 223.5f, 896.5f)
            quadTo(200f, 873f, 200f, 840f)
            lineTo(200f, 120f)
            quadTo(200f, 87f, 223.5f, 63.5f)
            quadTo(247f, 40f, 280f, 40f)
            lineTo(680f, 40f)
            quadTo(713f, 40f, 736.5f, 63.5f)
            quadTo(760f, 87f, 760f, 120f)
            lineTo(760f, 244f)
            quadTo(778f, 251f, 789f, 266f)
            quadTo(800f, 281f, 800f, 300f)
            lineTo(800f, 380f)
            quadTo(800f, 399f, 789f, 414f)
            quadTo(778f, 429f, 760f, 436f)
            lineTo(760f, 840f)
            quadTo(760f, 873f, 736.5f, 896.5f)
            quadTo(713f, 920f, 680f, 920f)
            lineTo(280f, 920f)
            close()
            moveTo(280f, 840f)
            lineTo(680f, 840f)
            quadTo(680f, 840f, 680f, 840f)
            quadTo(680f, 840f, 680f, 840f)
            lineTo(680f, 120f)
            quadTo(680f, 120f, 680f, 120f)
            quadTo(680f, 120f, 680f, 120f)
            lineTo(280f, 120f)
            quadTo(280f, 120f, 280f, 120f)
            quadTo(280f, 120f, 280f, 120f)
            lineTo(280f, 840f)
            quadTo(280f, 840f, 280f, 840f)
            quadTo(280f, 840f, 280f, 840f)
            close()
            moveTo(280f, 840f)
            quadTo(280f, 840f, 280f, 840f)
            quadTo(280f, 840f, 280f, 840f)
            lineTo(280f, 120f)
            quadTo(280f, 120f, 280f, 120f)
            quadTo(280f, 120f, 280f, 120f)
            lineTo(280f, 120f)
            quadTo(280f, 120f, 280f, 120f)
            quadTo(280f, 120f, 280f, 120f)
            lineTo(280f, 840f)
            quadTo(280f, 840f, 280f, 840f)
            quadTo(280f, 840f, 280f, 840f)
            close()
        }
    }.build()
}

val Icons.Rounded.MobileShare: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.MobileShare",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(320f, 640f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(-120f)
            horizontalLineToRelative(86f)
            lineToRelative(-42f, 44f)
            lineToRelative(56f, 56f)
            lineToRelative(140f, -140f)
            lineToRelative(-140f, -140f)
            lineToRelative(-56f, 56f)
            lineToRelative(42f, 44f)
            horizontalLineToRelative(-86f)
            quadToRelative(-33f, 0f, -56.5f, 23.5f)
            reflectiveQuadTo(320f, 520f)
            verticalLineToRelative(120f)
            close()
            moveTo(280f, 920f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(200f, 840f)
            verticalLineToRelative(-720f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(280f, 40f)
            horizontalLineToRelative(400f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(760f, 120f)
            verticalLineToRelative(124f)
            quadToRelative(18f, 7f, 29f, 22f)
            reflectiveQuadToRelative(11f, 34f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 19f, -11f, 34f)
            reflectiveQuadToRelative(-29f, 22f)
            verticalLineToRelative(404f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(680f, 920f)
            lineTo(280f, 920f)
            close()
        }
    }.build()
}
