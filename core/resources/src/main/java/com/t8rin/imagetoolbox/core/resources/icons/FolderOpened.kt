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

package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.FolderOpened: ImageVector by lazy {
    Builder(
        name = "Folder Opened", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 960.0f, viewportHeight = 960.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(160.0f, 800.0f)
            quadToRelative(-33.0f, 0.0f, -56.5f, -23.5f)
            reflectiveQuadTo(80.0f, 720.0f)
            verticalLineToRelative(-480.0f)
            quadToRelative(0.0f, -33.0f, 23.5f, -56.5f)
            reflectiveQuadTo(160.0f, 160.0f)
            horizontalLineToRelative(240.0f)
            lineToRelative(80.0f, 80.0f)
            horizontalLineToRelative(320.0f)
            quadToRelative(33.0f, 0.0f, 56.5f, 23.5f)
            reflectiveQuadTo(880.0f, 320.0f)
            lineTo(160.0f, 320.0f)
            verticalLineToRelative(400.0f)
            lineToRelative(96.0f, -320.0f)
            horizontalLineToRelative(684.0f)
            lineTo(837.0f, 743.0f)
            quadToRelative(-8.0f, 26.0f, -29.5f, 41.5f)
            reflectiveQuadTo(760.0f, 800.0f)
            lineTo(160.0f, 800.0f)
            close()
        }
    }
        .build()
}

val Icons.Outlined.FolderOpened: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    Builder(
        name = "Outlined.FolderOpened",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(160f, 800f)
            quadTo(127f, 800f, 103.5f, 776.5f)
            quadTo(80f, 753f, 80f, 720f)
            lineTo(80f, 240f)
            quadTo(80f, 207f, 103.5f, 183.5f)
            quadTo(127f, 160f, 160f, 160f)
            lineTo(400f, 160f)
            lineTo(480f, 240f)
            lineTo(800f, 240f)
            quadTo(833f, 240f, 856.5f, 263.5f)
            quadTo(880f, 287f, 880f, 320f)
            lineTo(447f, 320f)
            lineTo(367f, 240f)
            lineTo(160f, 240f)
            quadTo(160f, 240f, 160f, 240f)
            quadTo(160f, 240f, 160f, 240f)
            lineTo(160f, 720f)
            quadTo(160f, 720f, 160f, 720f)
            quadTo(160f, 720f, 160f, 720f)
            lineTo(256f, 400f)
            lineTo(940f, 400f)
            lineTo(837f, 743f)
            quadTo(829f, 769f, 807.5f, 784.5f)
            quadTo(786f, 800f, 760f, 800f)
            lineTo(160f, 800f)
            close()
            moveTo(244f, 720f)
            lineTo(760f, 720f)
            lineTo(832f, 480f)
            lineTo(316f, 480f)
            lineTo(244f, 720f)
            close()
            moveTo(244f, 720f)
            lineTo(316f, 480f)
            lineTo(316f, 480f)
            lineTo(244f, 720f)
            close()
            moveTo(160f, 320f)
            lineTo(160f, 240f)
            quadTo(160f, 240f, 160f, 240f)
            quadTo(160f, 240f, 160f, 240f)
            lineTo(160f, 240f)
            lineTo(160f, 320f)
            close()
        }
    }.build()
}