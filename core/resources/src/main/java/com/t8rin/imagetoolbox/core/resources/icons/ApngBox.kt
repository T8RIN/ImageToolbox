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
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.ApngBox: ImageVector by lazy {
    ImageVector.Builder(
        name = "Apng Box", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = null,
            strokeLineWidth = 0.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(19.0f, 3.0f)
            horizontalLineTo(5.0f)
            curveTo(3.89f, 3.0f, 3.0f, 3.89f, 3.0f, 5.0f)
            verticalLineToRelative(14.0f)
            curveToRelative(0.0f, 1.1046f, 0.8954f, 2.0f, 2.0f, 2.0f)
            horizontalLineToRelative(14.0f)
            curveToRelative(1.1046f, 0.0f, 2.0f, -0.8954f, 2.0f, -2.0f)
            verticalLineTo(5.0f)
            curveTo(21.0f, 3.89f, 20.1f, 3.0f, 19.0f, 3.0f)
            moveTo(19.0f, 5.0f)
            verticalLineToRelative(14.0f)
            horizontalLineTo(5.0f)
            verticalLineTo(5.0f)
            horizontalLineTo(19.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = null,
            strokeLineWidth = 0.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(17.9844f, 11.0121f)
            horizontalLineTo(16.3378f)
            verticalLineToRelative(1.9759f)
            horizontalLineToRelative(0.6586f)
            verticalLineToRelative(-0.9879f)
            horizontalLineToRelative(0.9879f)
            verticalLineToRelative(1.1197f)
            curveToRelative(0.0f, 0.461f, -0.3293f, 0.8562f, -0.8562f, 0.8562f)
            horizontalLineToRelative(-0.8562f)
            curveToRelative(-0.5269f, 0.0f, -0.8562f, -0.461f, -0.8562f, -0.8562f)
            verticalLineTo(10.9462f)
            curveToRelative(-0.0659f, -0.461f, 0.2635f, -0.9221f, 0.7904f, -0.9221f)
            horizontalLineToRelative(0.8562f)
            curveToRelative(0.5269f, 0.0f, 0.8562f, 0.461f, 0.8562f, 0.8562f)
            verticalLineToRelative(0.1317f)
            horizontalLineTo(17.9844f)
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = null,
            strokeLineWidth = 0.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(10.6834f, 10.0171f)
            horizontalLineTo(9.0369f)
            verticalLineToRelative(3.9518f)
            horizontalLineToRelative(0.9879f)
            verticalLineToRelative(-1.3173f)
            horizontalLineToRelative(0.6586f)
            curveToRelative(0.5269f, 0.0f, 0.9879f, -0.461f, 0.9879f, -0.9879f)
            verticalLineToRelative(-0.6586f)
            curveTo(11.6714f, 10.4782f, 11.2103f, 10.0171f, 10.6834f, 10.0171f)
            close()
            moveTo(10.6834f, 11.6637f)
            horizontalLineToRelative(-0.6586f)
            verticalLineToRelative(-0.6586f)
            horizontalLineToRelative(0.6586f)
            verticalLineTo(11.6637f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = null,
            strokeLineWidth = 0.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(7.5907f, 10.0241f)
            horizontalLineTo(6.8032f)
            curveToRelative(-0.435f, 0.0f, -0.7875f, 0.3526f, -0.7875f, 0.7876f)
            verticalLineToRelative(3.1502f)
            horizontalLineToRelative(0.7875f)
            verticalLineToRelative(-1.3103f)
            horizontalLineToRelative(0.7875f)
            verticalLineToRelative(1.3103f)
            horizontalLineToRelative(0.7875f)
            verticalLineTo(10.8117f)
            curveTo(8.3783f, 10.3767f, 8.0257f, 10.0241f, 7.5907f, 10.0241f)
            close()
            moveTo(6.8676f, 11.0121f)
            horizontalLineToRelative(0.6586f)
            verticalLineToRelative(0.6586f)
            horizontalLineTo(6.8676f)
            verticalLineTo(11.0121f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = null,
            strokeLineWidth = 0.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(14.7095f, 13.981f)
            lineToRelative(-0.9217f, 0.0f)
            lineToRelative(-0.6145f, -1.6487f)
            lineToRelative(0.0f, 1.6487f)
            lineToRelative(-0.9217f, 0.0f)
            lineToRelative(0.0f, -3.9569f)
            lineToRelative(0.9217f, 0.0f)
            lineToRelative(0.6145f, 1.6487f)
            lineToRelative(0.0f, -1.6487f)
            lineToRelative(0.9217f, 0.0f)
            lineToRelative(0.0f, 3.9569f)
        }
    }.build()
}

val Icons.TwoTone.ApngBox: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.ApngBox",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(5f, 5f)
            horizontalLineToRelative(14f)
            verticalLineToRelative(14f)
            horizontalLineToRelative(-14f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(19f, 3f)
            horizontalLineTo(5f)
            curveToRelative(-1.11f, 0f, -2f, 0.89f, -2f, 2f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 1.105f, 0.895f, 2f, 2f, 2f)
            horizontalLineToRelative(14f)
            curveToRelative(1.105f, 0f, 2f, -0.895f, 2f, -2f)
            verticalLineTo(5f)
            curveToRelative(0f, -1.11f, -0.9f, -2f, -2f, -2f)
            moveTo(19f, 5f)
            verticalLineToRelative(14f)
            horizontalLineTo(5f)
            verticalLineTo(5f)
            horizontalLineToRelative(14f)
            close()
            moveTo(17.984f, 11.012f)
            horizontalLineToRelative(-1.647f)
            verticalLineToRelative(1.976f)
            horizontalLineToRelative(0.659f)
            verticalLineToRelative(-0.988f)
            horizontalLineToRelative(0.988f)
            verticalLineToRelative(1.12f)
            curveToRelative(0f, 0.461f, -0.329f, 0.856f, -0.856f, 0.856f)
            horizontalLineToRelative(-0.856f)
            curveToRelative(-0.527f, 0f, -0.856f, -0.461f, -0.856f, -0.856f)
            verticalLineToRelative(-2.174f)
            curveToRelative(-0.066f, -0.461f, 0.264f, -0.922f, 0.79f, -0.922f)
            horizontalLineToRelative(0.856f)
            curveToRelative(0.527f, 0f, 0.856f, 0.461f, 0.856f, 0.856f)
            verticalLineToRelative(0.132f)
            horizontalLineToRelative(0.066f)
            moveTo(10.683f, 10.017f)
            horizontalLineToRelative(-1.647f)
            verticalLineToRelative(3.952f)
            horizontalLineToRelative(0.988f)
            verticalLineToRelative(-1.317f)
            horizontalLineToRelative(0.659f)
            curveToRelative(0.527f, 0f, 0.988f, -0.461f, 0.988f, -0.988f)
            verticalLineToRelative(-0.659f)
            curveToRelative(0f, -0.527f, -0.461f, -0.988f, -0.988f, -0.988f)
            close()
            moveTo(10.683f, 11.664f)
            horizontalLineToRelative(-0.659f)
            verticalLineToRelative(-0.659f)
            horizontalLineToRelative(0.659f)
            verticalLineToRelative(0.659f)
            close()
            moveTo(7.591f, 10.024f)
            horizontalLineToRelative(-0.788f)
            curveToRelative(-0.435f, 0f, -0.787f, 0.353f, -0.787f, 0.788f)
            verticalLineToRelative(3.15f)
            horizontalLineToRelative(0.787f)
            verticalLineToRelative(-1.31f)
            horizontalLineToRelative(0.787f)
            verticalLineToRelative(1.31f)
            horizontalLineToRelative(0.787f)
            verticalLineToRelative(-3.15f)
            curveToRelative(0f, -0.435f, -0.352f, -0.788f, -0.787f, -0.788f)
            close()
            moveTo(6.868f, 11.012f)
            horizontalLineToRelative(0.659f)
            verticalLineToRelative(0.659f)
            horizontalLineToRelative(-0.659f)
            verticalLineToRelative(-0.659f)
            close()
            moveTo(14.71f, 13.981f)
            horizontalLineToRelative(-0.922f)
            lineToRelative(-0.614f, -1.649f)
            verticalLineToRelative(1.649f)
            horizontalLineToRelative(-0.922f)
            verticalLineToRelative(-3.957f)
            horizontalLineToRelative(0.922f)
            lineToRelative(0.614f, 1.649f)
            verticalLineToRelative(-1.649f)
            horizontalLineToRelative(0.922f)
            verticalLineToRelative(3.957f)
        }
    }.build()
}