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

val Icons.Outlined.FolderOpen: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.FolderOpen",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(160f, 800f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(80f, 720f)
            verticalLineToRelative(-480f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(160f, 160f)
            horizontalLineToRelative(207f)
            quadToRelative(16f, 0f, 30.5f, 6f)
            reflectiveQuadToRelative(25.5f, 17f)
            lineToRelative(57f, 57f)
            horizontalLineToRelative(360f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(880f, 280f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(840f, 320f)
            lineTo(447f, 320f)
            lineToRelative(-80f, -80f)
            lineTo(160f, 240f)
            verticalLineToRelative(480f)
            lineToRelative(79f, -263f)
            quadToRelative(8f, -26f, 29.5f, -41.5f)
            reflectiveQuadTo(316f, 400f)
            horizontalLineToRelative(516f)
            quadToRelative(41f, 0f, 64.5f, 32.5f)
            reflectiveQuadTo(909f, 503f)
            lineToRelative(-72f, 240f)
            quadToRelative(-8f, 26f, -29.5f, 41.5f)
            reflectiveQuadTo(760f, 800f)
            lineTo(160f, 800f)
            close()
            moveTo(244f, 720f)
            horizontalLineToRelative(516f)
            lineToRelative(72f, -240f)
            lineTo(316f, 480f)
            lineToRelative(-72f, 240f)
            close()
            moveTo(160f, 458f)
            verticalLineToRelative(-218f)
            verticalLineToRelative(218f)
            close()
            moveTo(244f, 720f)
            lineTo(316f, 480f)
            lineTo(244f, 720f)
            close()
        }
    }.build()
}

val Icons.Rounded.FolderOpen: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.FolderOpen",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(160f, 800f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(80f, 720f)
            verticalLineToRelative(-480f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(160f, 160f)
            horizontalLineToRelative(207f)
            quadToRelative(16f, 0f, 30.5f, 6f)
            reflectiveQuadToRelative(25.5f, 17f)
            lineToRelative(57f, 57f)
            horizontalLineToRelative(360f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(880f, 280f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(840f, 320f)
            lineTo(314f, 320f)
            quadToRelative(-62f, 0f, -108f, 39f)
            reflectiveQuadToRelative(-46f, 99f)
            verticalLineToRelative(262f)
            lineToRelative(79f, -263f)
            quadToRelative(8f, -26f, 29.5f, -41.5f)
            reflectiveQuadTo(316f, 400f)
            horizontalLineToRelative(516f)
            quadToRelative(41f, 0f, 64.5f, 32.5f)
            reflectiveQuadTo(909f, 503f)
            lineToRelative(-72f, 240f)
            quadToRelative(-8f, 26f, -29.5f, 41.5f)
            reflectiveQuadTo(760f, 800f)
            lineTo(160f, 800f)
            close()
        }
    }.build()
}
