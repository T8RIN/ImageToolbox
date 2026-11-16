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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.t8rin.imagetoolbox.feature.maze_generation.domain.*
import kotlin.math.min
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

object MazeBitmapRenderer {
    suspend fun render(scene: MazeScene, width: Int, height: Int): Bitmap {
        require(width in 1..8192 && height in 1..8192)
        require(
            scene.minX.isFinite() && scene.minY.isFinite() &&
                scene.width.isFinite() && scene.width > 0f &&
                scene.height.isFinite() && scene.height > 0f
        ) { "Maze scene has invalid bounds" }
        currentCoroutineContext().ensureActive()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        try {
            val canvas = Canvas(bitmap)
            val s = scene.style
            if (!s.transparent) canvas.drawColor(s.backgroundColor)
            val scale = min(width / scene.width, height / scene.height)
            canvas.translate((width - scene.width * scale) / 2, (height - scene.height * scale) / 2)
            canvas.scale(scale, scale)
            canvas.translate(-scene.minX, -scene.minY)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            fun polygon(points: List<MazePoint>): Path =
                Path().apply {
                    points.forEachIndexed { i, p ->
                        if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                    }
                    close()
                }
            scene.fills.forEach { (points, distance) ->
                currentCoroutineContext().ensureActive()
                paint.style = Paint.Style.FILL
                paint.color = Color.HSVToColor(100, floatArrayOf(240f * (1 - distance), 0.8f, 1f))
                canvas.drawPath(polygon(points), paint)
            }
            paint.style = Paint.Style.STROKE
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeCap =
                when (s.cap) {
                    MazeCap.Butt -> Paint.Cap.BUTT
                    MazeCap.Round -> Paint.Cap.ROUND
                    MazeCap.Square -> Paint.Cap.SQUARE
                }
            paint.color = s.wallColor
            paint.strokeWidth = s.wallWidth
            scene.walls.forEach { wall ->
                currentCoroutineContext().ensureActive()
                when (wall) {
                    is MazeWall.Line ->
                        canvas.drawLine(wall.from.x, wall.from.y, wall.to.x, wall.to.y, paint)
                    is MazeWall.Arc ->
                        canvas.drawArc(
                            RectF(
                                wall.center.x - wall.radius,
                                wall.center.y - wall.radius,
                                wall.center.x + wall.radius,
                                wall.center.y + wall.radius,
                            ),
                            wall.start,
                            wall.sweep,
                            false,
                            paint,
                        )
                }
            }
            if (scene.solution.isNotEmpty()) {
                currentCoroutineContext().ensureActive()
                paint.color = s.solutionColor
                paint.strokeWidth = s.solutionWidth
                val path = Path()
                scene.solution.forEachIndexed { i, point ->
                    if (i == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
                }
                canvas.drawPath(path, paint)
            }
            paint.style = Paint.Style.FILL
            scene.start?.let {
                paint.color = s.startColor
                canvas.drawCircle(it.x, it.y, s.markerSize, paint)
            }
            scene.end?.let {
                paint.color = s.endColor
                canvas.drawCircle(it.x, it.y, s.markerSize, paint)
            }
            return bitmap
        } catch (t: Throwable) {
            bitmap.recycle()
            throw t
        }
    }
}
