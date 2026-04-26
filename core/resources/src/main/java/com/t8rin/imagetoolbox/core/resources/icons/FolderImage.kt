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

val Icons.Outlined.FolderImage: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.FolderImage",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(6.667f, 15.733f)
            lineToRelative(10.667f, 0f)
            lineToRelative(-3.68f, -4.8f)
            lineToRelative(-2.453f, 3.2f)
            lineToRelative(-1.653f, -2.133f)
            lineToRelative(-2.88f, 3.733f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(20.973f, 6.76f)
            curveToRelative(-0.418f, -0.418f, -0.92f, -0.627f, -1.507f, -0.627f)
            horizontalLineToRelative(-7.467f)
            lineToRelative(-2.133f, -2.133f)
            horizontalLineToRelative(-5.333f)
            curveToRelative(-0.587f, 0f, -1.089f, 0.209f, -1.507f, 0.627f)
            reflectiveCurveToRelative(-0.627f, 0.92f, -0.627f, 1.507f)
            verticalLineToRelative(11.733f)
            curveToRelative(0f, 0.587f, 0.209f, 1.089f, 0.627f, 1.507f)
            reflectiveCurveToRelative(0.92f, 0.627f, 1.507f, 0.627f)
            horizontalLineToRelative(14.933f)
            curveToRelative(0.587f, 0f, 1.089f, -0.209f, 1.507f, -0.627f)
            reflectiveCurveToRelative(0.627f, -0.92f, 0.627f, -1.507f)
            verticalLineToRelative(-9.6f)
            curveToRelative(0f, -0.587f, -0.209f, -1.089f, -0.627f, -1.507f)
            close()
            moveTo(19.467f, 17.867f)
            horizontalLineTo(4.533f)
            verticalLineTo(6.133f)
            horizontalLineToRelative(4.453f)
            lineToRelative(2.133f, 2.133f)
            horizontalLineToRelative(8.347f)
            verticalLineToRelative(9.6f)
            close()
        }
    }.build()
}

val Icons.Rounded.FolderImage: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.FolderImage",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(20.973f, 6.76f)
            curveToRelative(-0.418f, -0.418f, -0.92f, -0.627f, -1.507f, -0.627f)
            horizontalLineToRelative(-7.467f)
            lineToRelative(-2.133f, -2.133f)
            horizontalLineToRelative(-5.333f)
            curveToRelative(-0.587f, 0f, -1.089f, 0.209f, -1.507f, 0.627f)
            curveToRelative(-0.418f, 0.418f, -0.627f, 0.92f, -0.627f, 1.507f)
            verticalLineToRelative(11.733f)
            curveToRelative(0f, 0.587f, 0.209f, 1.089f, 0.627f, 1.507f)
            curveToRelative(0.418f, 0.418f, 0.92f, 0.627f, 1.507f, 0.627f)
            horizontalLineToRelative(14.933f)
            curveToRelative(0.587f, 0f, 1.089f, -0.209f, 1.507f, -0.627f)
            curveToRelative(0.418f, -0.418f, 0.627f, -0.92f, 0.627f, -1.507f)
            verticalLineToRelative(-9.6f)
            curveToRelative(0f, -0.587f, -0.209f, -1.089f, -0.627f, -1.507f)
            close()
            moveTo(6.667f, 15.733f)
            lineToRelative(2.88f, -3.733f)
            lineToRelative(1.653f, 2.133f)
            lineToRelative(2.453f, -3.2f)
            lineToRelative(3.68f, 4.8f)
            horizontalLineTo(6.667f)
            close()
        }
    }.build()
}