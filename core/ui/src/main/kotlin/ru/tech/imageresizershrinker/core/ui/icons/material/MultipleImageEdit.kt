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

package ru.tech.imageresizershrinker.core.ui.icons.material

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

val Icons.Rounded.MultipleImageEdit: ImageVector by lazy {
    Builder(
        name = "Multiple Image Edit", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(22.7125f, 5.2875f)
            curveTo(22.5208f, 5.0958f, 22.2833f, 5.0f, 22.0f, 5.0f)
            horizontalLineToRelative(-4.0f)
            curveToRelative(-0.2833f, 0.0f, -0.5208f, 0.0958f, -0.7125f, 0.2875f)
            curveTo(17.0958f, 5.4792f, 17.0f, 5.7167f, 17.0f, 6.0f)
            verticalLineToRelative(3.71f)
            curveToRelative(0.1748f, 0.0261f, 0.3466f, 0.0657f, 0.5121f, 0.1337f)
            curveToRelative(0.2684f, 0.1104f, 0.5126f, 0.2758f, 0.7323f, 0.4964f)
            lineTo(18.9015f, 11.0f)
            horizontalLineTo(22.0f)
            curveToRelative(0.2833f, 0.0f, 0.5208f, -0.0958f, 0.7125f, -0.2875f)
            curveTo(22.9042f, 10.5208f, 23.0f, 10.2833f, 23.0f, 10.0f)
            verticalLineTo(6.0f)
            curveTo(23.0f, 5.7167f, 22.9042f, 5.4792f, 22.7125f, 5.2875f)
            close()
            moveTo(21.0f, 9.0f)
            horizontalLineToRelative(-2.0f)
            verticalLineTo(7.0f)
            horizontalLineToRelative(2.0f)
            verticalLineTo(9.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(19.0311f, 12.9148f)
            curveToRelative(-0.075f, -0.1833f, -0.1792f, -0.35f, -0.3125f, -0.5f)
            lineToRelative(-0.925f, -0.925f)
            curveToRelative(-0.15f, -0.15f, -0.3167f, -0.2625f, -0.5f, -0.3375f)
            curveToRelative(-0.1833f, -0.075f, -0.375f, -0.1125f, -0.575f, -0.1125f)
            curveToRelative(-0.1833f, 0.0f, -0.3667f, 0.0333f, -0.55f, 0.1f)
            curveToRelative(-0.1834f, 0.0667f, -0.35f, 0.175f, -0.5f, 0.325f)
            lineToRelative(-5.525f, 5.5f)
            verticalLineToRelative(3.075f)
            horizontalLineToRelative(3.075f)
            lineToRelative(5.5f, -5.5f)
            curveToRelative(0.15f, -0.15f, 0.2583f, -0.3209f, 0.325f, -0.5125f)
            curveToRelative(0.0667f, -0.1917f, 0.1f, -0.3792f, 0.1f, -0.5625f)
            reflectiveCurveTo(19.1061f, 13.0981f, 19.0311f, 12.9148f)
            close()
            moveTo(12.5936f, 18.5398f)
            horizontalLineToRelative(-0.95f)
            verticalLineToRelative(-0.95f)
            lineToRelative(3.05f, -3.025f)
            lineToRelative(0.475f, 0.45f)
            lineToRelative(0.45f, 0.475f)
            lineTo(12.5936f, 18.5398f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(8.7766f, 17.0f)
            horizontalLineTo(3.0f)
            verticalLineTo(7.0f)
            horizontalLineToRelative(10.0f)
            verticalLineToRelative(5.0583f)
            lineToRelative(2.0f, -2.0101f)
            verticalLineTo(7.0f)
            curveToRelative(0.0f, -0.55f, -0.1959f, -1.0208f, -0.5875f, -1.4125f)
            reflectiveCurveTo(13.55f, 5.0f, 13.0f, 5.0f)
            horizontalLineTo(3.0f)
            curveTo(2.45f, 5.0f, 1.9792f, 5.1959f, 1.5875f, 5.5875f)
            reflectiveCurveTo(1.0f, 6.45f, 1.0f, 7.0f)
            verticalLineToRelative(10.0f)
            curveToRelative(0.0f, 0.55f, 0.1959f, 1.0208f, 0.5875f, 1.4125f)
            reflectiveCurveTo(2.45f, 19.0f, 3.0f, 19.0f)
            horizontalLineToRelative(5.1489f)
            horizontalLineToRelative(0.6277f)
            verticalLineTo(17.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(11.1718f, 13.8957f)
            lineToRelative(-1.7968f, -2.3957f)
            lineToRelative(-1.875f, 2.5f)
            lineToRelative(-1.375f, -1.825f)
            lineToRelative(-2.125f, 2.825f)
            lineToRelative(6.0731f, 0.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(22.7125f, 13.2875f)
            curveTo(22.5208f, 13.0958f, 22.2833f, 13.0f, 22.0f, 13.0f)
            horizontalLineToRelative(-1.4694f)
            curveToRelative(0.042f, 0.1667f, 0.0704f, 0.3335f, 0.0704f, 0.5003f)
            curveToRelative(0.0f, 0.2697f, -0.0488f, 0.5454f, -0.1464f, 0.8273f)
            curveTo(20.3682f, 14.5771f, 20.2258f, 14.7985f, 20.0439f, 15.0f)
            horizontalLineTo(21.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(-2.0f)
            verticalLineToRelative(-0.9357f)
            lineToRelative(-1.9887f, 1.9972f)
            curveToRelative(0.0139f, 0.2544f, 0.0996f, 0.4744f, 0.2762f, 0.6511f)
            curveTo(17.4792f, 18.9042f, 17.7167f, 19.0f, 18.0f, 19.0f)
            horizontalLineToRelative(4.0f)
            curveToRelative(0.2833f, 0.0f, 0.5208f, -0.0958f, 0.7125f, -0.2875f)
            curveTo(22.9042f, 18.5208f, 23.0f, 18.2833f, 23.0f, 18.0f)
            verticalLineToRelative(-4.0f)
            curveTo(23.0f, 13.7167f, 22.9042f, 13.4792f, 22.7125f, 13.2875f)
            close()
        }
    }.build()
}