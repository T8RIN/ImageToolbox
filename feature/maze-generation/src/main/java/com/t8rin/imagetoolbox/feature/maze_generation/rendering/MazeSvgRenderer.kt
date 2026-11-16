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

package com.t8rin.imagetoolbox.feature.maze_generation.rendering

import com.t8rin.imagetoolbox.feature.maze_generation.domain.*
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

object MazeSvgRenderer {
    fun render(scene: MazeScene, width: Int, height: Int, recipeJson: String): String {
        require(width in 1..8192 && height in 1..8192 && width.toLong() * height <= 32_000_000)
        require(
            scene.minX.isFinite() && scene.minY.isFinite() &&
                scene.width.isFinite() && scene.width > 0f &&
                scene.height.isFinite() && scene.height > 0f
        ) { "Maze scene has invalid bounds" }
        require(recipeJson.length <= 500_000) { "Maze recipe metadata exceeds 500 KB" }
        return buildString {
            fun f(x: Float) = String.format(Locale.ROOT, "%.5f", x)
            fun color(c: Int) = "#%06x".format(Locale.ROOT, c and 0xffffff)
            fun opacity(c: Int) = f((c ushr 24) / 255f)
            fun point(p: MazePoint) = "${f(p.x)},${f(p.y)}"
            val s = scene.style
            append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            append(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"$width\" height=\"$height\" viewBox=\"0 0 $width $height\">\n"
            )
            append("<metadata>")
            append(recipeJson.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"))
            append("</metadata>\n")
            if (!s.transparent)
                append(
                    "<rect width=\"$width\" height=\"$height\" fill=\"${color(s.backgroundColor)}\" fill-opacity=\"${opacity(s.backgroundColor)}\"/>\n"
                )
            val scale = minOf(width / scene.width, height / scene.height)
            val dx = (width - scene.width * scale) / 2 - scene.minX * scale
            val dy = (height - scene.height * scale) / 2 - scene.minY * scale
            append("<g transform=\"translate(${f(dx)} ${f(dy)}) scale(${f(scale)})\">\n")
            scene.fills.forEach { (points, d) ->
                val hue = 240f * (1 - d)
                val x = 1 - kotlin.math.abs((hue / 60) % 2 - 1)
                val rgb =
                    when {
                        hue < 60 -> floatArrayOf(1f, x, 0f)
                        hue < 120 -> floatArrayOf(x, 1f, 0f)
                        hue < 180 -> floatArrayOf(0f, 1f, x)
                        hue < 240 -> floatArrayOf(0f, x, 1f)
                        else -> floatArrayOf(x, 0f, 1f)
                    }
                val c = rgb.map { ((it * 0.8f + 0.2f) * 255).toInt() }
                append(
                    "<polygon points=\"${points.joinToString(" ",transform=::point)}\" fill=\"rgb(${c[0]},${c[1]},${c[2]})\" fill-opacity=\"${f(100/255f)}\"/>\n"
                )
            }
            append(
                "<g stroke-linecap=\"${s.cap.name.lowercase(Locale.ROOT)}\" stroke-linejoin=\"round\" fill=\"none\">\n"
            )
            append(
                "<g stroke=\"${color(s.wallColor)}\" stroke-opacity=\"${opacity(s.wallColor)}\" stroke-width=\"${f(s.wallWidth)}\">\n"
            )
            scene.walls.forEach { wall ->
                when (wall) {
                    is MazeWall.Line ->
                        append("<path d=\"M${point(wall.from)} L${point(wall.to)}\"/>\n")
                    is MazeWall.Arc -> {
                        fun at(a: Float): MazePoint {
                            val r = Math.toRadians(a.toDouble())
                            return MazePoint(
                                wall.center.x + wall.radius * cos(r).toFloat(),
                                wall.center.y + wall.radius * sin(r).toFloat(),
                            )
                        }
                        // Split full circles, which SVG cannot represent with a single arc command.
                        val pieces = if (wall.sweep >= 359.99f) 2 else 1
                        append("<path d=\"M${point(at(wall.start))}")
                        repeat(pieces) { i ->
                            val sweep = wall.sweep / pieces
                            append(
                                " A${f(wall.radius)},${f(wall.radius)} 0 ${if(sweep>180)1 else 0} 1 ${point(at(wall.start+sweep*(i+1)))}"
                            )
                        }
                        append("\"/>\n")
                    }
                }
            }
            append("</g>\n")
            if (scene.solution.isNotEmpty())
                append(
                    "<polyline points=\"${scene.solution.joinToString(" ",transform=::point)}\" stroke=\"${color(s.solutionColor)}\" stroke-opacity=\"${opacity(s.solutionColor)}\" stroke-width=\"${f(s.solutionWidth)}\"/>\n"
                )
            append("</g>\n")
            fun marker(p: MazePoint?, c: Int) {
                p?.let {
                    append(
                        "<circle cx=\"${f(it.x)}\" cy=\"${f(it.y)}\" r=\"${f(s.markerSize)}\" fill=\"${color(c)}\" fill-opacity=\"${opacity(c)}\"/>\n"
                    )
                }
            }
            marker(scene.start, s.startColor)
            marker(scene.end, s.endColor)
            append("</g></svg>")
        }
    }
}
