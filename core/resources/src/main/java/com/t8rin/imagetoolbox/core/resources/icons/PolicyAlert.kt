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

val Icons.Rounded.PolicyAlert: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.PolicyAlert",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(160f, 444f)
            verticalLineToRelative(-189f)
            quadToRelative(0f, -25f, 14.5f, -45f)
            reflectiveQuadToRelative(37.5f, -29f)
            lineToRelative(240f, -90f)
            quadToRelative(14f, -5f, 28f, -5f)
            reflectiveQuadToRelative(28f, 5f)
            lineToRelative(240f, 90f)
            quadToRelative(23f, 9f, 37.5f, 29f)
            reflectiveQuadToRelative(14.5f, 45f)
            verticalLineToRelative(145f)
            quadToRelative(-33f, 0f, -56.5f, 23.5f)
            reflectiveQuadTo(720f, 480f)
            verticalLineToRelative(184f)
            lineTo(618f, 562f)
            quadToRelative(11f, -19f, 16.5f, -39.5f)
            reflectiveQuadTo(640f, 480f)
            quadToRelative(0f, -66f, -47f, -113f)
            reflectiveQuadToRelative(-113f, -47f)
            quadToRelative(-66f, 0f, -113f, 47f)
            reflectiveQuadToRelative(-47f, 113f)
            quadToRelative(0f, 66f, 47f, 113f)
            reflectiveQuadToRelative(113f, 47f)
            quadToRelative(21f, 0f, 41.5f, -5.5f)
            reflectiveQuadTo(560f, 618f)
            lineToRelative(128f, 128f)
            quadToRelative(-39f, 43f, -79f, 73.5f)
            reflectiveQuadTo(516f, 869f)
            quadToRelative(-9f, 3f, -18f, 4.5f)
            reflectiveQuadTo(480f, 875f)
            quadToRelative(-9f, 0f, -18.5f, -1.5f)
            reflectiveQuadTo(443f, 869f)
            quadToRelative(-125f, -45f, -204f, -164f)
            reflectiveQuadToRelative(-79f, -261f)
            close()
            moveTo(536.5f, 536.5f)
            quadTo(560f, 513f, 560f, 480f)
            reflectiveQuadToRelative(-23.5f, -56.5f)
            quadTo(513f, 400f, 480f, 400f)
            reflectiveQuadToRelative(-56.5f, 23.5f)
            quadTo(400f, 447f, 400f, 480f)
            reflectiveQuadToRelative(23.5f, 56.5f)
            quadTo(447f, 560f, 480f, 560f)
            reflectiveQuadToRelative(56.5f, -23.5f)
            close()
            moveTo(811.5f, 868.5f)
            quadTo(800f, 857f, 800f, 840f)
            reflectiveQuadToRelative(11.5f, -28.5f)
            quadTo(823f, 800f, 840f, 800f)
            reflectiveQuadToRelative(28.5f, 11.5f)
            quadTo(880f, 823f, 880f, 840f)
            reflectiveQuadToRelative(-11.5f, 28.5f)
            quadTo(857f, 880f, 840f, 880f)
            reflectiveQuadToRelative(-28.5f, -11.5f)
            close()
            moveTo(800f, 680f)
            verticalLineToRelative(-160f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(840f, 480f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(880f, 520f)
            verticalLineToRelative(160f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(840f, 720f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(800f, 680f)
            close()
        }
    }.build()
}
