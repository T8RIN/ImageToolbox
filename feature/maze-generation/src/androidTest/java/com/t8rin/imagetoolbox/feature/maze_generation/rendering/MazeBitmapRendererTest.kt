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

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.imagetoolbox.feature.maze_generation.domain.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MazeBitmapRendererTest {
    @Test
    fun everyTopologyRendersAtTheRequestedSizeWithTransparentLetterboxing() = runBlocking {
        for (topology in MazeTopology.entries) {
            val recipe =
                MazeRecipe(
                    topology = topology,
                    rows = 6,
                    columns = 7,
                    style = MazeStyle(transparent = true),
                )
            val result = MazeGenerator.generate(recipe)
            val scene = MazeSceneBuilder.build(result, MazeSolver.solve(result, recipe), recipe)
            val bitmap = MazeBitmapRenderer.render(scene, 640, 480)
            try {
                assertEquals(640, bitmap.width)
                assertEquals(480, bitmap.height)
                assertEquals(0, Color.alpha(bitmap.getPixel(0, 0)))
            } finally {
                bitmap.recycle()
            }
        }
    }


    @Test
    fun solutionRouteIsAnOptionalOverlayAboveWalls() = runBlocking {
        val style =
            MazeStyle(
                wallColor = Color.BLACK,
                backgroundColor = Color.WHITE,
                solutionColor = Color.RED,
                wallWidth = 0.5f,
                solutionWidth = 0.8f,
                markerSize = 0.1f,
                margin = 0.1f,
            )
        val wall = MazeWall.Line(MazePoint(1f, 5f), MazePoint(9f, 5f))
        val route = listOf(MazePoint(1f, 5f), MazePoint(9f, 5f))
        val scene =
            MazeScene(
                minX = 0f,
                minY = 0f,
                width = 10f,
                height = 10f,
                walls = listOf(wall),
                solution = route,
                fills = emptyList(),
                start = null,
                end = null,
                style = style,
            )
        val visible = MazeBitmapRenderer.render(scene, 100, 100)
        try {
            assertEquals(Color.RED, visible.getPixel(50, 50))
        } finally {
            visible.recycle()
        }

        val hidden = MazeBitmapRenderer.render(scene.copy(solution = emptyList()), 100, 100)
        try {
            assertEquals(Color.BLACK, hidden.getPixel(50, 50))
        } finally {
            hidden.recycle()
        }
    }

    @Test
    fun opaqueBackgroundIncludesLetterboxing() = runBlocking {
        val recipe = MazeRecipe(style = MazeStyle(backgroundColor = Color.MAGENTA))
        val result = MazeGenerator.generate(recipe)
        val bitmap =
            MazeBitmapRenderer.render(
                MazeSceneBuilder.build(result, MazeSolver.solve(result, recipe), recipe),
                640,
                240,
            )
        try {
            assertEquals(Color.MAGENTA, bitmap.getPixel(0, 0))
        } finally {
            bitmap.recycle()
        }
    }
}
