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

val Icons.Outlined.FileRename: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.DriveFileRenameOutline",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(10.692f, 20.308f)
            lineToRelative(3.19f, -3.19f)
            curveToRelative(0.076f, -0.076f, 0.179f, -0.119f, 0.287f, -0.119f)
            horizontalLineToRelative(5.832f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            curveToRelative(0.391f, 0.392f, 0.587f, 0.863f, 0.587f, 1.413f)
            reflectiveCurveToRelative(-0.196f, 1.021f, -0.587f, 1.413f)
            curveToRelative(-0.392f, 0.391f, -0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineToRelative(-9.022f)
            curveToRelative(-0.361f, 0f, -0.542f, -0.436f, -0.287f, -0.692f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(4f, 19f)
            horizontalLineToRelative(1.425f)
            lineToRelative(9.775f, -9.775f)
            lineToRelative(-1.425f, -1.425f)
            lineToRelative(-9.775f, 9.775f)
            verticalLineToRelative(1.425f)
            close()
            moveTo(3f, 21f)
            curveToRelative(-0.283f, 0f, -0.521f, -0.096f, -0.712f, -0.287f)
            curveToRelative(-0.192f, -0.192f, -0.287f, -0.429f, -0.287f, -0.712f)
            verticalLineToRelative(-2.425f)
            curveToRelative(0f, -0.267f, 0.05f, -0.521f, 0.15f, -0.762f)
            reflectiveCurveToRelative(0.242f, -0.454f, 0.425f, -0.637f)
            lineTo(15.2f, 3.575f)
            curveToRelative(0.2f, -0.183f, 0.421f, -0.325f, 0.663f, -0.425f)
            reflectiveCurveToRelative(0.496f, -0.15f, 0.762f, -0.15f)
            reflectiveCurveToRelative(0.525f, 0.05f, 0.775f, 0.15f)
            reflectiveCurveToRelative(0.467f, 0.25f, 0.65f, 0.45f)
            lineToRelative(1.375f, 1.4f)
            curveToRelative(0.2f, 0.183f, 0.346f, 0.4f, 0.438f, 0.65f)
            reflectiveCurveToRelative(0.138f, 0.5f, 0.138f, 0.75f)
            curveToRelative(0f, 0.267f, -0.046f, 0.521f, -0.138f, 0.762f)
            reflectiveCurveToRelative(-0.237f, 0.463f, -0.438f, 0.663f)
            lineToRelative(-12.6f, 12.6f)
            curveToRelative(-0.183f, 0.183f, -0.396f, 0.325f, -0.637f, 0.425f)
            reflectiveCurveToRelative(-0.496f, 0.15f, -0.762f, 0.15f)
            horizontalLineToRelative(-2.425f)
            close()
            moveTo(18f, 6.4f)
            lineToRelative(-1.4f, -1.4f)
            lineToRelative(1.4f, 1.4f)
            close()
            moveTo(14.475f, 8.525f)
            lineToRelative(-0.7f, -0.725f)
            lineToRelative(1.425f, 1.425f)
            lineToRelative(-0.725f, -0.7f)
            close()
        }
    }.build()
}
