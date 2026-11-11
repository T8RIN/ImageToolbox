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

package com.t8rin.imagetoolbox.core.games.utils

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.pixelRect

internal fun DrawScope.arcadeCircuit(style: ArcadeStyle, width: Float, height: Float, top: Float) {
    val color = style.detailColor.copy(alpha = 0.19f)
    for (side in 0..1) {
        val edge = if (side == 0) 7f else width - 7f
        val direction = if (side == 0) 1f else -1f
        repeat(6) { index ->
            val y = top + (height - top) * (index + 0.4f) / 6f
            val reach = (11f + index % 3 * 5f) * direction
            drawLine(color, Offset(edge, y), Offset(edge + reach, y), 1f)
            drawLine(color, Offset(edge + reach, y), Offset(edge + reach, y + 8f), 1f)
            pixelRect(color, edge + reach - 1f, y + 8f, 2f, 2f)
            pixelRect(color.copy(alpha = 0.1f), edge - direction * 2f, y + 17f, 4f, 1f)
        }
    }
}

internal fun DrawScope.arcadeBlocks(style: ArcadeStyle, width: Float, height: Float, top: Float) {
    val shapes = listOf(
        listOf(0 to 0, 0 to 1, 1 to 1, 2 to 1),
        listOf(1 to 0, 2 to 0, 0 to 1, 1 to 1),
        listOf(0 to 0, 1 to 0, 2 to 0, 1 to 1)
    )
    for (side in 0..1) repeat(5) { index ->
        val left = if (side == 0) 6f else width - 21f
        val y = top + (height - top) * (index + 0.5f) / 5f
        for ((x, row) in shapes[(index + side) % shapes.size]) {
            drawRect(
                style.detailColor.copy(alpha = 0.12f),
                Offset(left + x * 5f, y + row * 5f),
                Size(4f, 4f),
                style = Stroke(0.7f)
            )
        }
    }
}

internal fun DrawScope.arcadeTiles(style: ArcadeStyle, width: Float, height: Float, top: Float) {
    for (side in 0..1) repeat(5) { index ->
        val x = if (side == 0) 7f else width - 20f
        val y = top + (height - top) * (index + 0.3f) / 5f
        val color = style.detailColor.copy(alpha = 0.12f)
        drawRoundRect(
            color,
            Offset(x + 3f, y + 4f),
            Size(10f, 10f),
            CornerRadius(1f),
            style = Stroke(0.7f)
        )
        drawRoundRect(color, Offset(x, y), Size(10f, 10f), CornerRadius(1f), style = Stroke(0.7f))
    }
}

internal fun DrawScope.arcadeSurvey(style: ArcadeStyle, width: Float, height: Float, top: Float) {
    val ink = style.detailColor.copy(alpha = 0.14f)
    for (side in 0..1) {
        val x = if (side == 0) 6f else width - 6f
        pixelRect(ink, x, top + 7f, 0.7f, height - top - 17f)
        var y = top + 12f
        var tick = 0
        while (y < height - 10f) {
            val length = if (tick++ % 5 == 0) 6f else 3f
            pixelRect(ink, if (side == 0) x else x - length, y, length, 0.7f)
            y += 7f
        }
    }
}

internal fun DrawScope.arcadeRadar(style: ArcadeStyle, width: Float, height: Float, top: Float) {
    val center = Offset(width * 0.08f, top + (height - top) * 0.67f)
    val radius = minOf(width, height - top) * 0.4f
    val color = style.detailColor.copy(alpha = 0.08f)
    for (ring in 1..3) drawCircle(color, radius * ring / 3f, center, style = Stroke(0.7f))
    drawLine(color, center - Offset(radius, 0f), center + Offset(radius, 0f), 0.7f)
    drawLine(color, center - Offset(0f, radius), center + Offset(0f, radius), 0.7f)
    val coast = Path().apply {
        moveTo(width, height * 0.42f)
        lineTo(width * 0.92f, height * 0.49f)
        lineTo(width * 0.96f, height * 0.61f)
        lineTo(width * 0.88f, height * 0.72f)
        lineTo(width * 0.91f, height)
    }
    drawPath(coast, color, style = Stroke(1f))
}

internal fun DrawScope.arcadeBamboo(style: ArcadeStyle, width: Float, height: Float, top: Float) {
    val color = style.detailColor.copy(alpha = 0.12f)
    for (side in 0..1) {
        val x = if (side == 0) 10f else width - 11f
        val direction = if (side == 0) 1f else -1f
        val bottom = height - 5f
        val stemTop = top + (height - top) * if (side == 0) 0.22f else 0.4f
        pixelRect(color, x, stemTop, 2f, bottom - stemTop)
        var y = bottom - 14f
        while (y > stemTop + 8f) {
            pixelRect(color, x - 1f, y, 4f, 1f)
            drawLine(color, Offset(x, y), Offset(x + 10f * direction, y - 7f), 2f)
            drawLine(
                color,
                Offset(x + 5f * direction, y - 3f),
                Offset(x + 14f * direction, y - 3f),
                2f
            )
            y -= 27f
        }
    }
}

internal fun DrawScope.arcadeGrid(style: ArcadeStyle, width: Float, height: Float, top: Float) {
    val color = style.accentColor.copy(alpha = 0.035f)
    var x = width / 2f % 24f
    while (x < width) {
        drawLine(color, Offset(x, top), Offset(x, height), 0.6f)
        x += 24f
    }
    var y = top + 12f
    while (y < height) {
        drawLine(color, Offset(0f, y), Offset(width, y), 0.6f)
        y += 24f
    }
}

internal fun DrawScope.arcadeTunnel(style: ArcadeStyle, width: Float, height: Float, top: Float) {
    val color = style.detailColor.copy(alpha = 0.10f)
    repeat(5) { index ->
        val x = (index + 0.5f) * width / 5f
        val inset = (height - top) * 0.18f
        drawLine(color, Offset(x - 9f, top + 3f), Offset(x, top + inset), 2f)
        drawLine(color, Offset(x, top + inset), Offset(x, height - inset), 1f)
        drawLine(color, Offset(x, height - inset), Offset(x + 9f, height - 3f), 2f)
        pixelRect(color, x - 2f, top + inset + 6f, 4f, 2f)
        pixelRect(color, x - 2f, height - inset - 8f, 4f, 2f)
    }
}

internal fun DrawScope.arcadeOrbits(style: ArcadeStyle, width: Float, height: Float, top: Float) {
    val color = style.detailColor.copy(alpha = 0.08f)
    val radius = minOf(width, height - top) * 0.36f
    val center = Offset(width * 0.92f, top + (height - top) * 0.17f)
    for (scale in listOf(0.52f, 0.76f, 1f)) {
        drawOval(
            color,
            center - Offset(radius, radius * scale),
            Size(radius * 2f, radius * scale * 2f),
            style = Stroke(0.7f)
        )
    }
    pixelRect(color, center.x - radius - 1.5f, center.y - 1.5f, 3f, 3f)
}

internal fun DrawScope.arcadePennants(style: ArcadeStyle, width: Float, top: Float) {
    val color = style.detailColor.copy(alpha = 0.14f)
    val y = top + 8f
    drawLine(color, Offset(7f, y), Offset(width - 7f, y), 0.7f)
    repeat(9) { index ->
        val x = 17f + index * (width - 34f) / 8f
        val path = Path().apply {
            moveTo(x - 3f, y)
            lineTo(x + 3f, y)
            lineTo(x + 3f, y + 5f)
            lineTo(x, y + 8f)
            lineTo(x - 3f, y + 5f)
            close()
        }
        drawPath(path, color, style = Stroke(0.7f))
    }
}

internal fun DrawScope.arcadeRoutes(
    style: ArcadeStyle,
    width: Float,
    height: Float,
    top: Float,
    origin: Offset,
    side: Float
) {
    val color = style.detailColor.copy(alpha = 0.24f)
    val margin = (origin.x - 9f).coerceIn(7f, 38f)
    for (edge in 0..1) {
        val direction = if (edge == 0) 1f else -1f
        val x = if (edge == 0) 7f else width - 7f
        repeat(3) { index ->
            val y = top + (height - top) * (index + 0.25f) / 3f
            val reach = margin * if (index % 2 == 0) 0.9f else 0.55f
            val endY = minOf(y + 27f, height - 11f)
            val path = Path().apply {
                moveTo(x, y)
                lineTo(x + reach * direction, y)
                lineTo(x + reach * direction, endY)
                lineTo(x + margin * 0.25f * direction, endY)
            }
            drawPath(
                path,
                color,
                style = Stroke(1f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            for (point in listOf(Offset(x, y), Offset(x + margin * 0.25f * direction, endY))) {
                pixelCircle(color, 2.5f, point, style = Stroke(1f))
            }
        }
    }
    for (y in listOf(origin.y - 6f, origin.y + side + 6f)) {
        pixelRect(color.copy(alpha = 0.12f), origin.x + side * 0.25f, y, side * 0.5f, 1f)
        for (x in listOf(origin.x + side * 0.25f, origin.x + side * 0.75f)) {
            pixelRect(color, x - 1f, y - 1f, 3f, 3f)
        }
    }
}

internal fun DrawScope.arcadeGarden(
    style: ArcadeStyle,
    width: Float,
    height: Float,
    top: Float,
    origin: Offset,
    side: Float
) {
    val color = style.detailColor.copy(alpha = 0.22f)
    for (edge in 0..1) {
        val x = if (edge == 0) (origin.x / 2f).coerceAtLeast(5f)
        else width - (origin.x / 2f).coerceAtLeast(5f)
        repeat(5) { index ->
            val y = top + (height - top) * (index + 0.55f) / 5f
            pixelRect(color, x, y - 4f, 1f, 9f)
            pixelRect(color, x - 3f, y - 3f, 3f, 2f)
            pixelRect(color, x + 1f, y, 3f, 2f)
            pixelRect(color.copy(alpha = 0.12f), x - 3f, y + 5f, 7f, 1f)
        }
    }
    for (x in listOf(origin.x + side * 0.22f, origin.x + side * 0.72f)) {
        val y = origin.y + side + 6f
        pixelRect(color, x, y, 7f, 1f)
        pixelRect(color, x + 2f, y - 2f, 1f, 2f)
        pixelRect(color, x + 5f, y - 3f, 1f, 3f)
    }
}

internal fun DrawScope.arcadeCabinet(style: ArcadeStyle, width: Float, height: Float, top: Float) {
    val color = style.detailColor.copy(alpha = 0.22f)
    for (side in 0..1) {
        val x = if (side == 0) 4f else width - 6f
        pixelRect(color.copy(alpha = 0.1f), x, top + 6f, 2f, height - top - 16f)
        var y = top + 10f
        while (y < height - 15f) {
            pixelRect(color, x - 1f, y, 4f, 7f)
            pixelRect(color.copy(alpha = 0.1f), x - 1f, y + 8f, 4f, 1f)
            y += 23f
        }
    }
    val center = Offset(width / 2f, top + (height - top) * 0.67f)
    for (radius in listOf(22f, 29f)) {
        drawArc(
            color.copy(alpha = 0.09f), 20f, 140f, false,
            center - Offset(radius, radius), Size(radius * 2f, radius * 2f), style = Stroke(1f)
        )
    }
    repeat(3) { index ->
        pixelRect(color.copy(alpha = 0.12f), center.x - 8f + index * 7f, center.y + 8f, 2f, 5f)
    }
}

internal fun DrawScope.arcadeBankshot(style: ArcadeStyle, width: Float, height: Float, top: Float) {
    val color = style.detailColor.copy(alpha = 0.21f)
    for (side in 0..1) {
        val x = if (side == 0) 6f else width - 6f
        val direction = if (side == 0) 1f else -1f
        drawLine(color, Offset(x, top + 8f), Offset(x, height - 15f), 1f)
        repeat(6) { index ->
            val y = top + (height - top) * (index + 0.5f) / 6f
            drawLine(color, Offset(x - direction * 2f, y - 3f), Offset(x + direction * 2f, y), 1f)
            drawLine(color, Offset(x + direction * 2f, y), Offset(x - direction * 2f, y + 3f), 1f)
        }
    }
    val center = Offset(width * 0.5f, top + (height - top) * 0.55f)
    drawCircle(color.copy(alpha = 0.06f), 22f, center, style = Stroke(1f))
    drawCircle(color.copy(alpha = 0.06f), 16f, center, style = Stroke(1f))
}

internal fun DrawScope.arcadePlanks(style: ArcadeStyle, width: Float, height: Float, top: Float) {
    val color = style.detailColor.copy(alpha = 0.13f)
    val plank = (height - top) / 4f
    repeat(4) { index ->
        val y = top + index * plank + 12f
        pixelRect(color.copy(alpha = 0.075f), 8f, y, width - 16f, 1f)
        for (x in listOf(13f, width - 14f)) {
            pixelRect(color, x, y + 5f, 2f, 2f)
            val direction = if (x < width / 2f) 1f else -1f
            pixelRect(color, x + (if (direction > 0f) 4f else -18f), y + 18f, 14f, 1f)
            pixelRect(
                color.copy(alpha = 0.08f),
                x + (if (direction > 0f) 9f else -27f),
                y + 21f,
                18f,
                1f
            )
        }
    }
}

internal fun DrawScope.arcadeNotebook(style: ArcadeStyle, width: Float, height: Float, top: Float) {
    val color = style.detailColor.copy(alpha = 0.055f)
    var y = top + 8f
    while (y < height) {
        drawLine(color, Offset(0f, y), Offset(width, y), 0.6f)
        y += 18f
    }
    var x = 17f
    while (x < width) {
        drawLine(color, Offset(x, top), Offset(x, height), 0.6f)
        x += 18f
    }
    drawLine(style.detailColor.copy(alpha = 0.16f), Offset(17f, top), Offset(17f, height), 1f)
    for (hole in 0..3) {
        pixelCircle(
            style.detailColor.copy(alpha = 0.2f), 2f,
            Offset(7f, top + (height - top) * (hole + 0.5f) / 4f), style = Stroke(0.8f)
        )
    }
}

internal fun DrawScope.arcadePrisms(style: ArcadeStyle, width: Float, height: Float, top: Float) {
    for (side in 0..1) repeat(4) { index ->
        val x = if (side == 0) width * 0.07f else width * 0.93f
        val y = top + (height - top) * (index + 0.4f) / 4f
        val span = 9f + index % 2 * 5f
        val path = Path().apply {
            moveTo(x, y - span / 2f)
            lineTo(x + span, y)
            lineTo(x, y + span / 2f)
            lineTo(x - span, y)
            close()
        }
        drawPath(path, style.detailColor.copy(alpha = 0.2f), style = Stroke(0.8f))
        drawLine(
            style.detailColor.copy(alpha = 0.12f),
            Offset(x - span, y + 4f),
            Offset(x, y + span / 2f + 4f),
            0.8f
        )
        drawLine(
            style.detailColor.copy(alpha = 0.12f),
            Offset(x, y + span / 2f + 4f),
            Offset(x + span, y + 4f),
            0.8f
        )
    }
}

internal fun DrawScope.arcadeSkyline(style: ArcadeStyle, width: Float, ground: Float, top: Float) {
    val color = style.detailColor.copy(alpha = 0.16f)
    val moon = Offset(width * 0.14f, top + (ground - top) * 0.16f)
    pixelCircle(color.copy(alpha = 0.08f), 18f, moon)
    pixelCircle(color, 2.5f, moon + Offset(-5f, -4f))
    pixelCircle(color.copy(alpha = 0.1f), 4f, moon + Offset(4f, 5f))
    var x = -5f
    var index = 0
    while (x < width) {
        val buildingWidth = 12f + index % 3 * 5f
        val buildingHeight = 16f + (index * 17 % 31)
        val y = ground - buildingHeight
        pixelRect(color.copy(alpha = 0.08f), x, y, buildingWidth, buildingHeight)
        pixelRect(color, x, y, buildingWidth, 1f)
        if (index % 3 == 0) pixelRect(color, x + buildingWidth / 2f, y - 5f, 1f, 5f)
        for (row in 0..2) for (column in 0..1) {
            pixelRect(color.copy(alpha = 0.1f), x + 3f + column * 5f, y + 5f + row * 8f, 2f, 2f)
        }
        x += buildingWidth + 3f
        index++
    }
}

internal fun DrawScope.arcadeOrchard(style: ArcadeStyle, width: Float, height: Float, top: Float) {
    val color = style.detailColor.copy(alpha = 0.16f)
    for (side in 0..1) {
        val direction = if (side == 0) 1f else -1f
        val x = if (side == 0) 2f else width - 2f
        val y = top + 24f + side * 12f
        val branch = Path().apply {
            moveTo(x, y + 40f)
            lineTo(x + direction * 9f, y + 28f)
            lineTo(x + direction * 9f, y + 14f)
            lineTo(x + direction * 20f, y)
        }
        drawPath(branch, color, style = Stroke(1.5f))
        repeat(4) { index ->
            val leafX = x + direction * (9f + index % 2 * 6f)
            val leafY = y + index * 10f
            val leaf = Path().apply {
                moveTo(leafX, leafY + 6f)
                lineTo(leafX + direction * 2f, leafY)
                lineTo(leafX + direction * 8f, leafY - 3f)
                lineTo(leafX + direction * 7f, leafY + 3f)
                close()
            }
            drawPath(leaf, color.copy(alpha = 0.1f))
            drawPath(leaf, color, style = Stroke(0.7f))
        }
        pixelRect(
            color.copy(alpha = 0.08f),
            x + (if (direction > 0f) 5f else -23f),
            height - 13f,
            18f,
            1f
        )
    }
}

internal fun DrawScope.arcadeShaft(
    style: ArcadeStyle,
    width: Float,
    height: Float,
    top: Float,
    drift: Float
) {
    val color = style.detailColor.copy(alpha = 0.15f)
    for (side in 0..1) {
        val edge = if (side == 0) 0f else width
        val direction = if (side == 0) 1f else -1f
        val inner = edge + width * 0.16f * direction
        val outer = edge + width * 0.035f * direction
        val wall = Path().apply {
            moveTo(edge, top)
            lineTo(inner, top)
            lineTo(inner - width * 0.035f * direction, height)
            lineTo(edge, height)
            close()
        }
        drawPath(wall, color.copy(alpha = 0.045f))
        drawLine(
            color.copy(alpha = 0.09f), Offset(inner, top),
            Offset(inner - width * 0.035f * direction, height), 1f
        )
        drawLine(color.copy(alpha = 0.08f), Offset(outer, top), Offset(outer, height), 1f)
        var y = top - drift % 82f - 82f
        while (y < height) {
            val x = edge + width * 0.08f * direction
            val half = width * 0.025f
            val niche = Path().apply {
                moveTo(x - half, y + 31f)
                lineTo(x - half, y + 9f)
                lineTo(x, y + 3f)
                lineTo(x + half, y + 9f)
                lineTo(x + half, y + 31f)
                close()
            }
            drawPath(niche, color.copy(alpha = 0.07f))
            drawPath(niche, color, style = Stroke(0.8f))
            pixelRect(style.accentColor.copy(alpha = 0.12f), x - 1f, y + 14f, 2f, 9f)
            drawLine(
                color.copy(alpha = 0.08f), Offset(outer, y + 54f),
                Offset(inner - width * 0.02f * direction, y + 58f), 0.8f
            )
            y += 82f
        }
    }
}