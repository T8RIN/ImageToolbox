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

package ru.tech.imageresizershrinker.core.resources.icons

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

val Icons.Outlined.ImageEdit: ImageVector by lazy {
    Builder(
        name = "Image Edit", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(11.0182f, 18.6079f)
            horizontalLineTo(5.393f)
            verticalLineTo(5.4684f)
            horizontalLineToRelative(13.214f)
            verticalLineToRelative(3.5806f)
            verticalLineToRelative(1.5046f)
            lineToRelative(0.035f, -0.0348f)
            curveToRelative(0.178f, -0.178f, 0.3758f, -0.3066f, 0.5934f, -0.3857f)
            curveToRelative(0.2175f, -0.0791f, 0.4351f, -0.1187f, 0.6527f, -0.1187f)
            curveToRelative(0.2104f, 0.0f, 0.4118f, 0.0408f, 0.6066f, 0.1107f)
            verticalLineTo(9.5792f)
            verticalLineTo(9.0491f)
            verticalLineTo(5.4684f)
            curveToRelative(0.0f, -1.0324f, -0.84f, -1.8771f, -1.8877f, -1.8771f)
            horizontalLineTo(5.393f)
            curveToRelative(-1.0382f, 0.0f, -1.8877f, 0.8447f, -1.8877f, 1.8771f)
            verticalLineToRelative(13.1395f)
            curveToRelative(0.0f, 1.0417f, 0.8495f, 1.8771f, 1.8877f, 1.8771f)
            horizontalLineToRelative(5.6252f)
            horizontalLineToRelative(0.0379f)
            horizontalLineToRelative(0.7311f)
            verticalLineToRelative(-1.8771f)
            horizontalLineToRelative(-0.6839f)
            horizontalLineTo(11.0182f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(22.4588f, 13.8637f)
            curveToRelative(-0.075f, -0.1833f, -0.1792f, -0.35f, -0.3125f, -0.5f)
            lineToRelative(-0.925f, -0.925f)
            curveToRelative(-0.15f, -0.15f, -0.3167f, -0.2625f, -0.5f, -0.3375f)
            curveToRelative(-0.1833f, -0.075f, -0.375f, -0.1125f, -0.575f, -0.1125f)
            curveToRelative(-0.1833f, 0.0f, -0.3667f, 0.0333f, -0.55f, 0.1f)
            curveToRelative(-0.1833f, 0.0667f, -0.35f, 0.175f, -0.5f, 0.325f)
            lineToRelative(-5.525f, 5.5f)
            verticalLineToRelative(3.075f)
            horizontalLineToRelative(3.075f)
            lineToRelative(5.5f, -5.5f)
            curveToRelative(0.15f, -0.15f, 0.2583f, -0.3209f, 0.325f, -0.5125f)
            curveToRelative(0.0667f, -0.1917f, 0.1f, -0.3792f, 0.1f, -0.5625f)
            reflectiveCurveTo(22.5338f, 14.0469f, 22.4588f, 13.8637f)
            close()
            moveTo(16.0213f, 19.4887f)
            horizontalLineToRelative(-0.95f)
            verticalLineToRelative(-0.95f)
            lineToRelative(3.05f, -3.025f)
            lineToRelative(0.475f, 0.45f)
            lineToRelative(0.45f, 0.475f)
            lineTo(16.0213f, 19.4887f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(15.4244f, 13.7219f)
            lineToRelative(-1.4644f, -1.9469f)
            lineToRelative(-2.75f, 3.54f)
            lineToRelative(-1.96f, -2.36f)
            lineToRelative(-2.75f, 3.53f)
            lineToRelative(6.1487f, 0.0f)
            close()
        }
    }.build()
}