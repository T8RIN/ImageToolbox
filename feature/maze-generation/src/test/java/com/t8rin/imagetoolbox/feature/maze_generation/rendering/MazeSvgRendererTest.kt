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
import javax.xml.parsers.DocumentBuilderFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class MazeSvgRendererTest {
    @Test
    fun svgIsXmlAndUsesTheSameWallsAndOutputSizeForEveryGrid() = runBlocking {
        for (topology in MazeTopology.entries) {
            val recipe =
                MazeRecipe(
                    topology = topology,
                    rows = 6,
                    columns = 7,
                    style = MazeStyle(showSolution = true, heatmap = true, transparent = true),
                )
            val maze = MazeGenerator.generate(recipe)
            val solution = MazeSolver.solve(maze, recipe)
            val scene = MazeSceneBuilder.build(maze, solution, recipe)
            val encoded = Json.encodeToString(recipe)
            val svg = MazeSvgRenderer.render(scene, 1200, 800, encoded)
            assertFalse(svg.contains("NaN"))
            assertFalse(svg.contains("Infinity"))
            val document =
                DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(svg.byteInputStream())
            assertEquals("0 0 1200 800", document.documentElement.getAttribute("viewBox"))
            assertEquals(scene.walls.size, document.getElementsByTagName("path").length)
            assertEquals(1, document.getElementsByTagName("polyline").length)
            val wallGroupStart = svg.indexOf("<g stroke=\"#161616\"")
            val wallGroupEnd = svg.indexOf("</g>", wallGroupStart)
            val routeIndex = svg.indexOf("<polyline")
            assertTrue(wallGroupStart >= 0 && wallGroupEnd >= 0 && routeIndex > wallGroupEnd)
            assertEquals(0, document.getElementsByTagName("rect").length)
            assertEquals(
                recipe,
                Json.decodeFromString<MazeRecipe>(
                    document.getElementsByTagName("metadata").item(0).textContent
                ),
            )
        }
    }


    @Test
    fun svgOmitsTheSolutionPolylineWhenTheOptionIsDisabled() = runBlocking {
        val recipe = MazeRecipe(style = MazeStyle(showSolution = false))
        val maze = MazeGenerator.generate(recipe)
        val scene = MazeSceneBuilder.build(maze, MazeSolver.solve(maze, recipe), recipe)
        val svg = MazeSvgRenderer.render(scene, 800, 800, "{}")
        assertFalse(svg.contains("<polyline"))
    }

    @Test
    fun colorAndSolutionChangesDoNotChangeTheGraphKey() {
        val recipe = MazeRecipe()
        assertEquals(
            recipe.graphKey(),
            recipe
                .copy(
                    style = recipe.style.copy(wallColor = 123, showSolution = true),
                    entrance = MazeEntrance.Manual,
                    startCell = 23,
                    endCell = 52,
                    openEntrances = false,
                )
                .graphKey(),
        )
    }

    @Test
    fun svgNumbersDoNotDependOnDeviceLocale() = runBlocking {
        val old = Locale.getDefault()
        try {
            val recipe = MazeRecipe(topology = MazeTopology.Polar, rows = 4)
            val maze = MazeGenerator.generate(recipe)
            val scene = MazeSceneBuilder.build(maze, MazeSolver.solve(maze, recipe), recipe)
            Locale.setDefault(Locale.US)
            val us = MazeSvgRenderer.render(scene, 1000, 1000, "{}")
            Locale.setDefault(Locale.GERMANY)
            val de = MazeSvgRenderer.render(scene, 1000, 1000, "{}")
            assertEquals(us, de)
        } finally {
            Locale.setDefault(old)
        }
    }
}
