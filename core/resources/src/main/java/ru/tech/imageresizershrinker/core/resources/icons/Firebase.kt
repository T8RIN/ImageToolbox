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
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.TwoTone.Firebase: ImageVector by lazy {
    Builder(
        name = "Firebase", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFFFF9100)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(8.4f, 23.3f)
            curveToRelative(1.0f, 0.4f, 2.1f, 0.6f, 3.2f, 0.7f)
            curveToRelative(1.5f, 0.1f, 3.0f, -0.3f, 4.3f, -0.9f)
            curveToRelative(-1.6f, -0.6f, -3.0f, -1.5f, -4.2f, -2.7f)
            curveTo(11.0f, 21.7f, 9.8f, 22.7f, 8.4f, 23.3f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFFFFC400)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(11.8f, 20.5f)
            curveTo(9.0f, 17.9f, 7.3f, 14.2f, 7.4f, 10.1f)
            curveToRelative(0.0f, -0.1f, 0.0f, -0.3f, 0.0f, -0.4f)
            curveTo(7.0f, 9.5f, 6.4f, 9.5f, 5.9f, 9.5f)
            curveToRelative(-0.8f, 0.0f, -1.5f, 0.1f, -2.2f, 0.3f)
            curveTo(3.0f, 11.0f, 2.5f, 12.5f, 2.5f, 14.1f)
            curveToRelative(-0.1f, 4.1f, 2.4f, 7.7f, 6.0f, 9.2f)
            curveTo(9.8f, 22.7f, 11.0f, 21.7f, 11.8f, 20.5f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFFFF9100)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(11.8f, 20.5f)
            curveToRelative(0.6f, -1.0f, 1.0f, -2.3f, 1.1f, -3.6f)
            curveToRelative(0.1f, -3.4f, -2.2f, -6.4f, -5.4f, -7.2f)
            curveToRelative(0.0f, 0.1f, 0.0f, 0.3f, 0.0f, 0.4f)
            curveTo(7.3f, 14.2f, 9.0f, 17.9f, 11.8f, 20.5f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFFDD2C00)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(12.5f, 0.0f)
            curveToRelative(-1.8f, 1.5f, -3.3f, 3.4f, -4.1f, 5.6f)
            curveTo(7.9f, 6.9f, 7.6f, 8.2f, 7.5f, 9.7f)
            curveToRelative(3.2f, 0.8f, 5.5f, 3.8f, 5.4f, 7.2f)
            curveToRelative(0.0f, 1.3f, -0.4f, 2.5f, -1.1f, 3.6f)
            curveToRelative(1.2f, 1.1f, 2.6f, 2.0f, 4.2f, 2.7f)
            curveToRelative(3.2f, -1.5f, 5.4f, -4.6f, 5.5f, -8.3f)
            curveToRelative(0.1f, -2.4f, -0.8f, -4.6f, -2.2f, -6.4f)
            curveTo(18.0f, 6.5f, 12.5f, 0.0f, 12.5f, 0.0f)
            close()
        }
    }.build()
}

val Icons.Outlined.Firebase: ImageVector by lazy {
    Builder(
        name = "Outlined.Firebase",
        defaultWidth = 600.dp,
        defaultHeight = 599.dp,
        viewportWidth = 600f,
        viewportHeight = 599f
    ).apply {
        group(
            clipPathData = PathData {
                moveTo(78.99f, 21f)
                horizontalLineToRelative(443.89f)
                verticalLineToRelative(555.21f)
                horizontalLineToRelative(-443.89f)
                close()
            }
        ) {
            path(fill = SolidColor(Color(0xFF212121))) {
                moveTo(473.41f, 214.61f)
                curveTo(460.94f, 197.31f, 432.27f, 161.74f, 388.24f, 108.9f)
                curveTo(369.12f, 85.98f, 352.73f, 66.53f, 344.65f, 56.98f)
                curveTo(340.2f, 51.71f, 336.38f, 47.2f, 333.37f, 43.64f)
                lineTo(328.57f, 37.97f)
                lineTo(325.96f, 34.9f)
                lineTo(325.45f, 34.16f)
                lineTo(325.23f, 34.06f)
                lineTo(314.11f, 21f)
                lineTo(300.03f, 32.3f)
                curveTo(264.06f, 61.1f, 234.61f, 98.24f, 214.87f, 139.62f)
                curveTo(202.77f, 164.25f, 194.87f, 188.32f, 190.73f, 213.12f)
                curveTo(189.64f, 218.69f, 188.71f, 224.43f, 187.94f, 230.17f)
                curveTo(183.11f, 229.78f, 178.19f, 229.53f, 173.32f, 229.41f)
                curveTo(172.91f, 229.38f, 172.5f, 229.37f, 171.95f, 229.36f)
                curveTo(154.1f, 228.74f, 136.35f, 230.86f, 119.21f, 235.7f)
                lineTo(111.87f, 237.75f)
                lineTo(108.1f, 244.37f)
                curveTo(90.4f, 275.41f, 80.39f, 310.74f, 79.13f, 346.53f)
                curveTo(77.51f, 393.02f, 90.18f, 438.01f, 115.76f, 476.65f)
                curveTo(140.79f, 514.44f, 176.15f, 543.34f, 218.01f, 560.2f)
                lineTo(223.46f, 562.4f)
                lineTo(225.11f, 562.97f)
                lineTo(225.18f, 562.95f)
                curveTo(246.99f, 570.86f, 269.88f, 575.28f, 293.22f, 576.09f)
                curveTo(295.87f, 576.19f, 298.51f, 576.23f, 301.14f, 576.23f)
                curveTo(330.59f, 576.23f, 359.27f, 570.47f, 386.49f, 559.07f)
                lineTo(386.69f, 559.16f)
                lineTo(393.93f, 555.8f)
                curveTo(430.64f, 538.83f, 462.05f, 512.15f, 484.77f, 478.64f)
                curveTo(508.14f, 444.17f, 521.27f, 403.83f, 522.74f, 362.01f)
                curveTo(524.48f, 312.02f, 507.88f, 262.43f, 473.39f, 214.62f)
                lineTo(473.41f, 214.61f)
                close()
                moveTo(309.54f, 369.51f)
                curveTo(317.13f, 398.29f, 315.63f, 425.94f, 305.13f, 451.81f)
                curveTo(278.87f, 425.89f, 259.61f, 397.4f, 247.86f, 367.03f)
                curveTo(235.28f, 334.54f, 227.72f, 303.64f, 225.32f, 275.05f)
                curveTo(236.44f, 278.7f, 246.65f, 283.52f, 255.75f, 289.45f)
                curveTo(281.92f, 306.51f, 300.03f, 333.44f, 309.54f, 369.52f)
                verticalLineTo(369.51f)
                close()
                moveTo(314.32f, 508.82f)
                curveTo(325.48f, 517.32f, 337.21f, 525.28f, 349.32f, 532.6f)
                curveTo(331.5f, 537.45f, 313.11f, 539.58f, 294.52f, 538.91f)
                curveTo(291.64f, 538.81f, 288.77f, 538.65f, 285.88f, 538.41f)
                curveTo(296.48f, 529.28f, 306f, 519.37f, 314.31f, 508.82f)
                horizontalLineTo(314.32f)
                close()
                moveTo(345.48f, 360.02f)
                curveTo(333.54f, 314.75f, 310.17f, 280.52f, 276.04f, 258.29f)
                curveTo(261.06f, 248.54f, 244f, 241.15f, 225.27f, 236.32f)
                curveTo(225.54f, 233.2f, 225.87f, 230.1f, 226.27f, 227.06f)
                curveTo(226.61f, 224.46f, 226.96f, 222.06f, 227.34f, 219.82f)
                curveTo(230.42f, 203.89f, 235.02f, 188.13f, 240.96f, 172.98f)
                curveTo(243.24f, 167.18f, 245.74f, 161.42f, 248.38f, 155.85f)
                lineTo(248.48f, 155.65f)
                curveTo(252.57f, 147.39f, 257.17f, 139.03f, 262.6f, 130.1f)
                lineTo(264.73f, 126.59f)
                lineTo(264.65f, 126.55f)
                curveTo(277.25f, 106.87f, 292.33f, 88.93f, 309.59f, 73.11f)
                lineTo(316.25f, 80.98f)
                curveTo(331.79f, 99.39f, 346.4f, 116.8f, 359.68f, 132.74f)
                curveTo(389.56f, 168.57f, 428.3f, 215.64f, 443.25f, 236.39f)
                curveTo(472.8f, 277.35f, 487.04f, 319.17f, 485.59f, 360.73f)
                curveTo(484.46f, 393f, 474.91f, 424.47f, 457.95f, 451.72f)
                curveTo(441.89f, 477.54f, 419.52f, 499.15f, 393.17f, 514.36f)
                curveTo(378.46f, 507.02f, 357.11f, 494.95f, 334.43f, 477.38f)
                curveTo(352.71f, 440.94f, 356.44f, 401.48f, 345.5f, 360.02f)
                horizontalLineTo(345.48f)
                close()
                moveTo(285.91f, 484.89f)
                curveTo(269.15f, 506.61f, 249.2f, 520.74f, 237.57f, 527.84f)
                curveTo(235.68f, 527.15f, 233.79f, 526.43f, 231.93f, 525.67f)
                lineTo(230.44f, 525.08f)
                curveTo(195.97f, 510.84f, 166.89f, 486.76f, 146.35f, 455.43f)
                curveTo(125.36f, 423.42f, 114.97f, 386.2f, 116.31f, 347.8f)
                curveTo(117.26f, 320.27f, 124.13f, 293.97f, 136.72f, 269.62f)
                curveTo(144.03f, 268f, 151.48f, 266.98f, 158.93f, 266.6f)
                lineTo(160.86f, 266.56f)
                curveTo(164.63f, 266.48f, 168.37f, 266.48f, 171.94f, 266.56f)
                curveTo(177.18f, 266.8f, 182.43f, 267.35f, 187.6f, 268.18f)
                curveTo(189.29f, 303.17f, 197.88f, 340.92f, 213.17f, 380.42f)
                curveTo(227.89f, 418.44f, 252.34f, 453.56f, 285.9f, 484.88f)
                lineTo(285.91f, 484.89f)
                close()
            }
        }
    }.build()
}