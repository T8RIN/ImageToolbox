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
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.Falling: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Falling",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        group(translationY = 960f) {
            path(fill = SolidColor(Color.Black)) {
                moveToRelative(492f, -248f)
                lineToRelative(-121f, -90f)
                quadToRelative(-11f, -8f, -19f, -20.5f)
                reflectiveQuadTo(341f, -385f)
                lineToRelative(-45f, -199f)
                lineToRelative(-88f, 76f)
                lineToRelative(24f, 112f)
                quadToRelative(4f, 17f, -5.5f, 31f)
                reflectiveQuadTo(200f, -348f)
                quadToRelative(-17f, 3f, -31f, -6f)
                reflectiveQuadToRelative(-17f, -26f)
                lineToRelative(-23f, -111f)
                quadToRelative(-5f, -21f, 2f, -41.5f)
                reflectiveQuadToRelative(23f, -34.5f)
                lineToRelative(144f, -128f)
                quadToRelative(23f, -20f, 54.5f, -16.5f)
                reflectiveQuadTo(413f, -696f)
                quadToRelative(32f, 14f, 66.5f, 20f)
                reflectiveQuadToRelative(69.5f, 1f)
                quadToRelative(24f, -4f, 46f, -14f)
                reflectiveQuadToRelative(42f, -24f)
                quadToRelative(14f, -10f, 30.5f, -8.5f)
                reflectiveQuadTo(695f, -707f)
                quadToRelative(11f, 13f, 8.5f, 29.5f)
                reflectiveQuadTo(687f, -651f)
                quadToRelative(-23f, 16f, -47f, 28.5f)
                reflectiveQuadTo(589f, -602f)
                quadToRelative(-33f, 9f, -66.5f, 9.5f)
                reflectiveQuadTo(456f, -600f)
                lineToRelative(28f, 124f)
                lineToRelative(115f, -22f)
                quadToRelative(16f, -3f, 31.5f, 0.5f)
                reflectiveQuadTo(660f, -484f)
                lineToRelative(147f, 104f)
                quadToRelative(14f, 10f, 16.5f, 26.5f)
                reflectiveQuadTo(815f, -323f)
                quadToRelative(-10f, 13f, -25.5f, 15.5f)
                reflectiveQuadTo(760f, -314f)
                lineToRelative(-140f, -98f)
                lineToRelative(-144f, 28f)
                lineToRelative(68f, 50f)
                quadToRelative(18f, 14f, 26.5f, 35f)
                reflectiveQuadToRelative(4.5f, 44f)
                lineToRelative(-28f, 154f)
                quadToRelative(-3f, 17f, -17f, 26.5f)
                reflectiveQuadTo(499f, -68f)
                quadToRelative(-17f, -3f, -26f, -17f)
                reflectiveQuadToRelative(-6f, -31f)
                lineToRelative(25f, -132f)
                close()
                moveTo(320f, -740f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(240f, -820f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(320f, -900f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(400f, -820f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(320f, -740f)
                close()
            }
        }
    }.build()
}