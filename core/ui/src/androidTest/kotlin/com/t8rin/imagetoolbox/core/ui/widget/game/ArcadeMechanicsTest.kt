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

package com.t8rin.imagetoolbox.core.ui.widget.game

import android.os.Bundle
import androidx.compose.ui.geometry.Offset
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class ArcadeMechanicsTest {
    private val factories = listOf(
        ::RacerRun, ::RicochetRun, ::AsteroidsRun, ::FruitSliceRun, ::KnifeHitRun,
        ::MissileCommandRun, ::RiverHopRun, ::WhackAMoleRun, ::SimonSaysRun,
        ::BubblePopRun, ::FishingRun, ::HexagonRun,
        ::BomberRun, ::MinesweeperRun, ::MazeChaserRun,
        ::GravityFlipRun, ::ZigZagRun, ::TimbermanRun,
        ::OrbitSurvivalRun, ::LunarLanderRun, ::BrickBreakerRun, ::LightCyclesRun
    )

    @Test
    fun collectionContainsThirtyDistinctGamesAndTheOriginalEight() {
        assertEquals(30, ArcadeGame.playable.size)
        assertEquals(30, ArcadeGame.playable.distinct().size)
        assertEquals(
            listOf(
                ArcadeGame.Dino, ArcadeGame.Doodle, ArcadeGame.Bird, ArcadeGame.Stack,
                ArcadeGame.Pong, ArcadeGame.`2048`, ArcadeGame.Snake, ArcadeGame.Tetris
            ),
            ArcadeGame.playable.take(8)
        )
        assertEquals(30, ArcadeGame.playable.map { it.title }.distinct().size)
    }

    @Test
    fun everyNewGameRestoresItsLiveStateAtCompactAndTallSizes() {
        for (height in listOf(220f, 680f)) for (factory in factories) {
            val run = factory().apply {
                resize(360f, height, 52f)
                start()
            }
            repeat(90) { run.advance(1f / 120f) }
            run.stopInput()
            val saved = Bundle().also(run::save)
            val restored = factory().apply {
                reset()
                resize(run.width, run.height, run.topInset)
                score = run.score
                phase = run.phase
                restore(saved)
            }
            assertBundlesEqual((run as Any).javaClass.simpleName, saved, Bundle().also(restored::save))

        }
    }

    @Test
    fun resizingAndRestartingNeverLeavesInvalidSimulationValues() {
        for (factory in factories) {
            val run = factory().apply { start() }
            repeat(6) { cycle ->
                run.resize(
                    if (cycle % 2 == 0) 360f else 780f,
                    if (cycle % 2 == 0) 680f else 220f,
                    44f
                )
                repeat(240) { if (run.phase == ArcadePhase.Running) run.advance(1f / 120f) }
                val saved = Bundle().also(run::save)
                for (key in saved.keySet()) {
                    val values = saved.get(key) as? FloatArray ?: continue
                    assertTrue("${(run as Any).javaClass.simpleName}: $key", values.all(Float::isFinite))
                }
                run.start()
                assertEquals(0, run.score)
                assertEquals(ArcadePhase.Running, run.phase)
            }
        }
    }

    @Test
    fun racerAlwaysLeavesAReachableLaneThroughLongRuns() {
        for (height in listOf(220f, 680f)) repeat(3) {
            val run = RacerRun().apply { resize(360f, height); start() }
            repeat(120 * 150) {
                val next = run.traffic.filter { it.y < run.playerY + 60f && it.y >= -24f }
                    .maxByOrNull { it.y }
                if (next != null) {
                    val blocked = run.traffic.filter { abs(it.y - next.y) < 1f }.map { it.lane }
                    val gap = (0..2).filterNot { it in blocked }.minBy { abs(it - run.position) }
                    run.steer(Offset(run.laneX(gap.toFloat()), run.playerY))
                }
                run.advance(1f / 120f)
                assertEquals(
                    "Unreachable traffic at $height, score ${run.score}",
                    ArcadePhase.Running,
                    run.phase
                )
            }
            assertTrue(run.score > 1000)
            assertTrue(run.traffic.size < 12)
        }
    }

    @Test
    fun ricochetDamagesBlocksAndReflectsWithoutGettingStuck() {
        val run = RicochetRun().apply { start() }
        run.bricks.clear()
        val brick = RicochetBrick(3, 2, 2)
        run.bricks += brick
        val x = run.origin.x + 3.5f * run.cell
        val y = run.origin.y + 3f * run.rowHeight + run.ballRadius
        run.balls += RicochetBall(x, y, 0f, -run.cell * 17f)
        run.flying = true
        run.advance(1f / 120f)
        assertEquals(1, brick.health)
        assertTrue(run.balls.single().vy > 0f)
        var steps = 0
        while (run.flying && steps++ < 120 * 10) run.advance(1f / 120f)
        assertTrue(!run.flying)
        assertTrue(run.balls.isEmpty())
        assertEquals(3, brick.row)
        assertTrue(run.bricks.size < 49)
    }

    @Test
    fun ricochetDoesNotShootOnPauseAndCollectsExtraBalls() {
        val run = RicochetRun().apply { start() }
        run.aim(Offset(100f, 60f))
        run.stopInput()
        run.shoot()
        assertTrue(!run.flying)
        run.bricks.clear()
        run.bricks += RicochetBrick(3, 2, 0)
        run.balls += RicochetBall(
            run.origin.x + 3.5f * run.cell,
            run.origin.y + 2.5f * run.rowHeight, 0f, -100f
        )
        run.flying = true
        run.advance(1f / 120f)
        assertEquals(4, run.ballCount)
        assertTrue(run.bricks.isEmpty())
        assertTrue(run.balls.single().vy < 0f)
    }

    @Test
    fun stationaryCarsAndShipsCannotSurviveIndefinitely() {
        for (height in listOf(220f, 680f)) {
            for (lane in 0..2) {
                val racer = RacerRun().apply {
                    resize(360f, height)
                    start()
                    position = lane.toFloat()
                    targetLane = lane
                }
                repeat(120 * 20) { if (racer.phase == ArcadePhase.Running) racer.advance(1f / 120f) }
                assertEquals(ArcadePhase.Over, racer.phase)
            }
            val space = AsteroidsRun().apply { resize(360f, height); start() }
            repeat(120 * 45) { if (space.phase == ArcadePhase.Running) space.advance(1f / 120f) }
            assertEquals("Idle ship survived at $height", ArcadePhase.Over, space.phase)
        }
    }

    @Test
    fun giantAsteroidsTakeMultipleHitsBeforeBreakingIntoFragments() {
        val run = AsteroidsRun().apply { start() }
        val rock = SpaceRock(100f, 100f, 0f, 0f, 34f, 0f, 4)
        run.rocks += rock
        repeat(4) { hit ->
            run.shots += SpaceShot(100f, 100f, 0f, 0f, 1f)
            run.advance(1f / 120f)
            if (hit < 3) assertTrue(rock in run.rocks)
        }
        assertTrue(rock !in run.rocks)
        assertEquals(2, run.rocks.size)
        assertTrue(run.rocks.all { it.radius < 15f })
    }

    @Test
    fun hexagonGapsRemainPassableAtCompactAndTallSizes() {
        for (height in listOf(220f, 680f)) {
            val run = HexagonRun().apply { resize(360f, height); start() }
            repeat(120 * 30) {
                run.walls.firstOrNull { !it.passed }?.let {
                    run.angle = run.rotation + (it.gap + 0.5f) * kotlin.math.PI.toFloat() / 3f
                }
                run.advance(1f / 120f)
                assertEquals(ArcadePhase.Running, run.phase)
            }
            assertTrue(run.score > 10)
            assertTrue(run.walls.size < 8)
        }
    }

    @Test
    fun fishingRewardsACatchAndMolesDistinguishBombsFromTargets() {
        val fishing = FishingRun().apply { resize(360f, 220f); start() }
        fishing.fish.clear()
        fishing.fish += SwimmingFish(fishing.anchor.x, 0, 1, 1)
        fishing.angle = 0f
        fishing.cast()
        repeat(240) { fishing.advance(1f / 120f) }
        assertEquals(1, fishing.score)
        assertEquals(0, fishing.misses)
        val moles = WhackAMoleRun().apply { resize(360f, 220f); start() }
        moles.types[4] = 1
        moles.hit(moles.hole(4))
        assertEquals(1, moles.score)
        assertEquals(0, moles.types[4])
        repeat(3) {
            moles.types[0] = 2
            moles.hit(moles.hole(0))
        }
        assertEquals(1, moles.score)
        assertEquals(ArcadePhase.Over, moles.phase)
    }

    @Test
    fun bubbleGroupsDoNotWrapAndClearingSettlesEachColumn() {
        val run = BubblePopRun().apply { resize(360f, 680f); start() }
        run.board.fill(-1)
        run.board[5] = 0
        run.board[6] = 0
        assertEquals(1, run.group(5).size)
        run.board[4] = 0
        run.board[16] = 1
        assertEquals(2, run.group(4).size)
        run.pop(run.center(4))
        assertEquals(2, run.score)
        assertEquals(1, run.board[4])
        assertEquals(-1, run.board[16])
        assertTrue(run.fall[4] < 0f)
        repeat(60) { run.advance(1f / 120f) }
        assertEquals(0f, run.fall[4], 0f)
    }

    @Test
    fun bubblesKeepRisingAndEndWhenTheCeilingIsReached() {
        val run = BubblePopRun().apply { start() }
        repeat(120 * 45) { if (run.phase == ArcadePhase.Running) run.advance(1f / 120f) }
        assertEquals(ArcadePhase.Over, run.phase)
        run.start()
        run.board.fill(-1)
        run.board[0] = 0
        run.board[1] = 0
        run.pop(run.center(0))
        repeat(240) { run.advance(1f / 120f) }
        assertEquals(ArcadePhase.Running, run.phase)
        assertTrue(run.board.take(run.columns).all { it >= 0 })
        assertTrue((0 until run.columns - 1).any { run.board[it] == run.board[it + 1] })
    }

    @Test
    fun onlySuccessfulPopsBringTheNextRowUpSmoothly() {
        val run = BubblePopRun().apply { resize(360f, 680f); start() }
        run.board.fill(-1)
        run.board[0] = 0
        run.board[1] = 1
        run.pop(run.center(0))
        repeat(60) { run.advance(1f / 120f) }
        assertEquals(0, run.score)
        assertTrue(run.rise < 0.2f)
        assertEquals(2, run.board.count { it >= 0 })

        run.board[1] = 0
        val before = run.rise
        run.pop(run.center(0))
        assertEquals(before, run.rise, 0f)
        val next = run.next.copyOf()
        val saved = Bundle().also(run::save)
        val restored = BubblePopRun().apply {
            resize(360f, 680f)
            start()
            restore(saved)
        }
        repeat(240) { restored.advance(1f / 120f) }
        assertArrayEquals(next, restored.board.take(restored.columns).toIntArray())
        assertEquals(restored.columns, restored.board.count { it >= 0 })
        assertTrue(restored.rise < 0.5f)
    }

    @Test
    fun bubbleBoardFillsTheContainerAndKeepsBubblesWhenResized() {
        val run = BubblePopRun().apply { resize(360f, 680f); start() }
        assertEquals(run.height, run.origin.y + run.rows * run.cell, 0.01f)
        assertTrue(run.rows > 6)
        val before = run.board.filter { it >= 0 }.sorted()
        run.resize(780f, 220f)
        assertEquals(before, run.board.filter { it >= 0 }.sorted())
        assertEquals(run.height, run.origin.y + run.rows * run.cell, 0.01f)
        assertTrue(run.origin.y >= run.topInset - 0.01f)
        assertEquals(ArcadePhase.Running, run.phase)
    }

    @Test
    fun fruitWavesVaryTheirLaunchPositionsAndTravelAcrossTheBoard() {
        val run = FruitSliceRun().apply { start() }
        val positions = mutableSetOf<Int>()
        val kinds = mutableSetOf<Int>()
        var left = false
        var right = false
        repeat(120 * 30) {
            run.misses = 0
            run.advance(1f / 120f)
            for (fruit in run.fruits) if (fruit.y > run.height + 10f && fruit.vy < 0f) {
                positions += fruit.x.toInt()
                kinds += fruit.kind
                if (fruit.vx < -15f) left = true
                if (fruit.vx > 15f) right = true
            }
        }
        assertTrue(positions.size > 12)
        assertTrue(kinds.size >= 4)
        assertTrue(left && right)
    }

    @Test
    fun incomingMissilesEnterFromOutsideTheTopEdge() {
        val run = MissileCommandRun().apply { resize(360f, 680f); start() }
        var frames = 0
        while (run.missiles.isEmpty() && frames++ < 150) run.advance(1f / 120f)
        assertTrue(run.missiles.isNotEmpty())
        assertTrue(run.missiles.first().y < 0f)
    }

    @Test
    fun simonPlaysEverySymbolAndAcceptsOnlyTheCorrectSequence() {
        val run = SimonSaysRun().apply { resize(360f, 220f); start() }
        repeat(15) { round ->
            val seen = mutableListOf<Int>()
            var previous = -1
            var steps = 0
            while (!run.accepting && steps++ < 3000) {
                run.advance(1f / 120f)
                if (run.lit >= 0 && previous == -1) seen += run.lit
                previous = run.lit
            }
            assertTrue(run.accepting)
            assertEquals(run.sequence, seen)
            val sequence = run.sequence.toList()
            for (pad in sequence) {
                run.tap(
                    run.origin + Offset(
                        (pad % 2 + 0.5f) * run.side / 2,
                        (pad / 2 + 0.5f) * run.side / 2
                    )
                )
                repeat(28) { run.advance(1f / 120f) }
            }
            assertEquals(round + 1, run.score)
            // Finish the last-input flash before observing the next playback.
            repeat(30) { run.advance(1f / 120f) }
        }
        while (!run.accepting) run.advance(1f / 120f)
        val wrong = (run.sequence.first() + 1) % 4
        run.tap(
            run.origin + Offset(
                (wrong % 2 + 0.5f) * run.side / 2,
                (wrong / 2 + 0.5f) * run.side / 2
            )
        )
        assertEquals(ArcadePhase.Over, run.phase)
    }

    @Test
    fun fruitSlicesUseTheEntireSwipeAndResetComboBetweenGestures() {
        val run = FruitSliceRun().apply { start() }
        run.fruits += SliceFruit(80f, 100f, 0f, 0f, false, 0)
        run.fruits += SliceFruit(180f, 100f, 0f, 0f, false, 1)
        run.begin(Offset(40f, 100f))
        run.slice(Offset(220f, 100f))
        assertEquals(3, run.score)
        assertTrue(run.fruits.isEmpty())
        run.stopInput()
        run.fruits += SliceFruit(120f, 100f, 0f, 0f, false, 0)
        run.begin(Offset(80f, 100f))
        run.slice(Offset(150f, 100f))
        assertEquals(4, run.score)
        run.fruits += SliceFruit(240f, 100f, 0f, 0f, true, 0)
        run.slice(Offset(280f, 100f))
        assertEquals(ArcadePhase.Over, run.phase)
    }

    @Test
    fun riverPreviewFillsTheFieldAfterResizingBeforeStart() {
        val run = RiverHopRun().apply { reset() }
        run.resize(360f, 680f)
        assertEquals(ArcadePhase.Ready, run.phase)
        assertTrue(run.lanes.last().index * run.cell >= run.height - run.topInset)
    }

    @Test
    fun riverLogsCarryTheFrogAndOpenWaterEndsTheRun() {
        val run = RiverHopRun().apply { resize(360f, 220f); start() }
        run.row = 3
        run.highest = 3
        run.lanes.clear()
        run.lanes += RiverLane(3, 2, 1f, 2.8f, 4.1f, 3f)
        val initial = run.frogX
        repeat(30) { run.advance(1f / 120f) }
        assertEquals(initial + 0.25f, run.frogX, 0.001f)
        assertEquals(ArcadePhase.Running, run.phase)
        run.frogX = 6.6f
        run.advance(1f / 120f)
        assertEquals(ArcadePhase.Over, run.phase)
    }

    @Test
    fun knifeCollisionAndMissileRechargeCannotBeBypassedByRapidTaps() {
        val knife = KnifeHitRun().apply { start() }
        repeat(100) { knife.throwKnife() }
        repeat(20) { knife.advance(1f / 120f) }
        assertEquals(1, knife.score)
        assertEquals(5, knife.remaining)
        val missile = MissileCommandRun().apply { start() }
        repeat(100) { missile.fire(Offset(100f, 100f)) }
        assertEquals(4, missile.shots.size)
        assertEquals(0f, missile.charge, 0f)
        repeat(60) { missile.advance(1f / 120f) }
        assertTrue(missile.charge < 1f)
    }

    @Test
    fun bomberBlastsStopAtWallsAndCratesAndChainOtherBombs() {
        val run = BomberRun().apply { start() }
        run.board.fill(0)
        val origin = 3 * run.columns + 3
        run.board[origin + 2] = 2
        run.board[origin - run.columns] = 1
        val bomb = BomberBomb(origin, 0f, 4)
        run.bombs += bomb
        run.explode(bomb)
        assertEquals(0, run.board[origin + 2])
        assertTrue(origin + 2 in run.blasts)
        assertTrue(origin + 3 !in run.blasts)
        assertTrue(origin - run.columns !in run.blasts)
        run.blasts.clear()
        run.board.fill(0)
        run.enemies.clear()
        run.enemies += BomberEnemy(origin + 3, origin + 3)
        val first = BomberBomb(origin, 0f, 2)
        run.bombs += first
        run.bombs += BomberBomb(origin + 2, 2f, 2)
        run.explode(first)
        assertTrue(run.bombs.isEmpty())
        assertTrue(origin + 4 in run.blasts)
        run.advance(1f / 120f)
        assertTrue(run.enemies.isEmpty())
        assertTrue(run.transition > 0f)
        repeat(125) { run.advance(1f / 120f) }
        assertEquals(2, run.round)
        assertTrue(run.enemies.isNotEmpty())
        run.placeBomb()
        run.placeBomb()
        assertEquals(1, run.bombs.size)
        run.steer(Offset(30f, 0f))
        repeat(20) { run.advance(1f / 120f) }
        assertEquals(run.columns + 2, run.player)
        assertEquals(3, run.lives)
        run.start()
        run.board.fill(0)
        val start = run.player
        run.begin(Offset(100f, 100f))
        run.drag(Offset(130f, 100f))
        run.advance(1f / 120f)
        run.swipe(Offset(30f, 0f))
        run.release()
        repeat(24) { run.advance(1f / 120f) }
        assertEquals(start + 1, run.player)
        run.begin(Offset(100f, 100f))
        run.swipe(Offset(0f, 30f))
        run.release()
        repeat(24) { run.advance(1f / 120f) }
        assertEquals(start + 1 + run.columns, run.player)
    }

    @Test
    fun minesweeperFirstTapIsAnEmptyFloodAndNumbersMatchTheirNeighbors() {
        for (height in listOf(220f, 680f)) repeat(20) {
            val run = MinesweeperRun().apply { resize(360f, height); start() }
            val first = run.board.indices.random()
            run.open(first)
            assertEquals(0, run.board[first])
            assertTrue(run.neighbors(first).all { run.board[it] >= 0 })
            assertEquals(run.mineCount, run.board.count { it == -1 })
            for (index in run.board.indices) if (run.board[index] >= 0) {
                assertEquals(run.neighbors(index).count { run.board[it] == -1 }, run.board[index])
            }
            assertTrue(run.cover.count { it == 1 } > 1)
            assertEquals(ArcadePhase.Running, run.phase)
        }
    }


    @Test
    fun generatedMazesHaveReachableDotsAndEnemiesWithOpenCornerPellets() {
        for (height in listOf(220f, 680f)) repeat(30) {
            val run = MazeChaserRun().apply { resize(360f, height); start() }
            val distances = run.distances(run.player)
            for (index in run.walls.indices) {
                assertEquals(!run.walls[index], distances[index] < Int.MAX_VALUE)
                if (run.dots[index] > 0) assertTrue(!run.walls[index])
                if (!run.walls[index]) {
                    val neighbors =
                        listOf(index - 1, index + 1, index - run.columns, index + run.columns)
                    assertTrue(neighbors.count { it in run.walls.indices && !run.walls[it] } >= 2)
                }
            }
            assertTrue(run.enemies.isNotEmpty())
            assertTrue(run.enemies.all { distances[it.position] >= 5 })
            assertEquals(3, run.dots.count { it == 2 })
        }
        val moving = MazeChaserRun().apply { start(); invulnerable = 999f }
        moving.dots.fill(0)
        repeat(700) { frame ->
            if (frame == 260) moving.power = 0.4f
            val before = moving.enemies.map(moving::enemyPoint)
            moving.advance(1f / 120f)
            moving.enemies.forEachIndexed { index, enemy ->
                assertTrue((moving.enemyPoint(enemy) - before[index]).getDistance() <= moving.cell * 0.05f)
            }
        }
    }


    @Test
    fun gravityObstaclesLeaveTimeToReachTheOppositeSurface() {
        for (height in listOf(220f, 680f)) {
            val run = GravityFlipRun().apply { resize(360f, height); start() }
            var lastGate: GravityObstacle? = null
            val kinds = mutableSetOf<Int>()
            repeat(120 * 150) {
                val obstacle = run.obstacles.filter { it.x + it.width >= run.playerX - run.radius }
                    .minByOrNull { it.x }
                if (obstacle != null && obstacle.x - run.playerX < run.speed * 0.9f) {
                    kinds += obstacle.kind
                    when (obstacle.kind) {
                        0 -> {
                            val gravity = if (obstacle.ceiling) 1 else -1
                            if (run.gravity != gravity) run.flip()
                        }

                        2 -> if (lastGate !== obstacle && obstacle.x + obstacle.width / 2f - run.playerX < run.speed * 0.235f) {
                            run.flip()
                            lastGate = obstacle
                        }
                    }
                }
                run.advance(1f / 120f)
                assertEquals(
                    "Gravity: height=$height, kind=${obstacle?.kind}, x=${obstacle?.x}, y=${run.y}",
                    ArcadePhase.Running,
                    run.phase
                )
            }
            assertEquals(setOf(0, 1, 2), kinds)
            assertTrue(run.score > 100)
            assertTrue(run.obstacles.size < 8)
        }
        val shield = GravityFlipRun().apply { start(); this.shield = true }
        val obstacle = GravityObstacle(shield.playerX, 30f, 0.4f, false, false)
        shield.obstacles += obstacle
        shield.advance(1f / 120f)
        assertTrue(obstacle.broken)
        assertTrue(!shield.shield)
        assertEquals(ArcadePhase.Running, shield.phase)
        shield.obstacles += GravityObstacle(shield.playerX, 30f, 0.4f, false, false)
        shield.advance(1f / 120f)
        assertEquals(ArcadePhase.Over, shield.phase)
    }

    @Test
    fun zigzagTileEdgesAreContinuousAndGeneratedCornersRemainTraversable() {
        val run = ZigZagRun().apply { start() }
        assertTrue(run.supported(0.5f, 0f))
        assertTrue(run.supported(0.49f, 0.49f))
        assertTrue(!run.supported(0.56f, 0.56f))
        var destination = 1
        repeat(120 * 80) {
            var tile = run.tiles.first { it.x + it.y == destination }
            if (abs(run.x - tile.x) < 0.035f && abs(run.y - tile.y) < 0.035f) {
                destination++
                tile = run.tiles.first { it.x + it.y == destination }
            }
            val direction = if (abs(tile.x - run.x) > abs(tile.y - run.y)) 0 else 1
            if (run.direction != direction) run.turn()
            run.advance(1f / 120f)
            assertTrue(run.fall < 0f)
        }
        assertTrue(run.progress > 150f)
        assertTrue(run.tiles.size < 60)
        val corner = ZigZagRun().apply { start(); advance(0.8f) }
        corner.tiles.clear()
        for (x in 0..4) corner.tiles += ZigZagTile(x, 0, false)
        for (y in 1..5) corner.tiles += ZigZagTile(4, y, false)
        corner.x = 4.45f
        corner.y = 0f
        corner.direction = 0
        corner.turn()
        repeat(30) { corner.advance(1f / 120f) }
        assertTrue(corner.fall < 0f)
        assertEquals(4.45f, corner.x, 0.001f)
        corner.x = 7f
        repeat(15) { corner.advance(1f / 120f) }
        assertTrue(corner.fall >= 0f)
    }

    @Test
    fun timbermanAlwaysLeavesASafeSideAndRequiresAChoppingRhythm() {
        val run = TimbermanRun().apply { start() }
        run.resize(360f, 1400f)
        assertTrue(run.branches.size * run.segment > run.base + run.segment)
        run.resize(360f, 280f)
        run.start()
        repeat(180) {
            val side = listOf(-1, 1).first { it != run.branches[0] && it != run.branches[1] }
            run.chop(side)
            repeat(16) { run.advance(1f / 120f) }
            assertEquals(ArcadePhase.Running, run.phase)
            assertEquals(24, run.branches.size)
            assertTrue(run.chips.size <= 6)
        }
        assertTrue(run.score >= 180)
        repeat(700) { if (run.phase == ArcadePhase.Running) run.advance(1f / 120f) }
        assertEquals(ArcadePhase.Over, run.phase)
        run.start()
        run.branches[1] = 1
        run.chop(1)
        assertEquals(ArcadePhase.Over, run.phase)
    }

    @Test
    fun orbitPatternsFitBothDotsAndEitherDotCanCollide() {
        for (kind in 0..2) {
            val run = OrbitSurvivalRun().apply { start(); advance(0.01f) }
            run.gates.clear()
            run.gates += OrbitGate(-1.4f, kind)
            run.angle = when (kind) {
                0 -> 0f; 1 -> Math.PI.toFloat() / 2f; else -> Math.PI.toFloat() / 4f
            }
            repeat(230) { run.advance(1f / 120f) }
            assertEquals(ArcadePhase.Running, run.phase)
            assertEquals(1, run.score)
        }
        for (kind in 0..2) for (angle in listOf(0.7f, -1.2f, 3.14f)) {
            val run = OrbitSurvivalRun().apply { start(); advance(0.01f) }
            run.gates.clear()
            run.gates += OrbitGate(-1.4f, kind, angle = angle)
            run.angle = angle + when (kind) {
                0 -> 0f; 1 -> Math.PI.toFloat() / 2f; else -> Math.PI.toFloat() / 4f
            }
            repeat(230) { run.advance(1f / 120f) }
            assertEquals(ArcadePhase.Running, run.phase)
        }
        val variety = OrbitSurvivalRun().apply { start() }
        val kinds = mutableSetOf<Int>()
        val angles = mutableSetOf<Float>()
        repeat(2400) {
            variety.advance(0.05f)
            variety.gates.forEach { kinds += it.kind; angles += it.angle }
            variety.gates.clear()
        }
        assertEquals(5, kinds.size)
        assertTrue(angles.size >= 4)
        for (y in listOf(-1.1f, 0.9f)) {
            val run = OrbitSurvivalRun().apply { start(); advance(0.01f) }
            run.gates.clear()
            run.gates += OrbitGate(y, 0)
            run.angle = Math.PI.toFloat() / 2f
            run.advance(1f / 120f)
            assertEquals(ArcadePhase.Over, run.phase)
        }
    }

    @Test
    fun landerRewardsGentleLandingsAndRejectsHardOrOffPadImpacts() {
        for (height in listOf(220f, 680f)) {
            val run = LunarLanderRun().apply { resize(360f, height); start() }
            fun approach(speed: Float, x: Float) {
                run.beginThrust(Offset(run.width / 2f, 100f))
                run.stopInput()
                run.x = x
                run.y = run.padY - 12f * run.shipScale / run.span - 0.002f
                run.vx = 0f
                run.vy = speed
                run.angle = 0f
            }
            approach(0.07f, run.padLeft + run.padWidth / 2f)
            repeat(12) { run.advance(1f / 120f) }
            assertTrue(run.landed)
            assertTrue(run.worldWidth > run.width)
            assertTrue(run.camera.x <= 0f)
            assertTrue(run.camera.x + run.worldWidth * run.zoom >= run.width - 0.01f)
            assertEquals(3, run.lives)
            assertTrue(run.score >= 120)
            repeat(130) { run.advance(1f / 120f) }
            assertEquals(2, run.round)
            assertEquals(1f, run.fuel, 0.001f)
            approach(0.5f, run.padLeft + run.padWidth / 2f)
            repeat(8) { run.advance(1f / 120f) }
            assertTrue(run.crashed)
            assertEquals(2, run.lives)
            repeat(140) { run.advance(1f / 120f) }
            run.terrain.clear()
            run.terrain += Offset(0f, run.padY)
            run.terrain += Offset(1f, run.padY)
            approach(0.07f, run.padLeft - 0.03f)
            repeat(12) { run.advance(1f / 120f) }
            assertTrue(run.crashed)
            assertEquals(1, run.lives)
        }
        val controlled = LunarLanderRun().apply { start() }
        val finger = Offset(180f, 120f)
        controlled.beginThrust(finger)
        repeat(1800) {
            if (!controlled.landed && !controlled.crashed) {
                val clearance =
                    controlled.padY - controlled.y - 12f * controlled.shipScale / controlled.span
                val target = minOf(0.17f, maxOf(0.035f, clearance * 0.65f))
                val throttle = if (controlled.vy > target) 0.85f else 0.1f
                controlled.steer(finger + Offset(0f, (0.55f - throttle) * 90f))
                controlled.advance(1f / 120f)
            }
        }
        assertTrue(
            "Landing: y=${controlled.y}, vy=${controlled.vy}, fuel=${controlled.fuel}, crashed=${controlled.crashed}",
            controlled.landed
        )
        assertTrue(controlled.fuel > 0f)
        controlled.stopInput()
        assertTrue(!controlled.burning)
    }

    @Test
    fun breakerBouncesOffPaddleAndArmoredBricksThenStartsAnotherWave() {
        val run = BrickBreakerRun().apply { start() }
        repeat(100) { run.advance(1f / 120f) }
        run.ballX = run.paddleX
        run.ballY = run.paddleY - run.ballRadius - 0.5f
        run.vx = 0f
        run.vy = run.speed
        run.advance(1f / 120f)
        assertTrue(run.vy < 0f && abs(run.vx) > 0f)
        run.bricks.clear()
        val brick = BreakerBrick(3, 0, 2)
        run.bricks += brick
        repeat(2) {
            val rect = run.bounds(brick)
            run.ballX = rect.center.x
            run.ballY = rect.bottom + run.ballRadius + 0.1f
            run.vx = 0f
            run.vy = -run.speed
            run.advance(1f / 120f)
            assertTrue(run.vy > 0f)
        }
        assertTrue(run.bricks.isEmpty())
        val falling = BreakerGift(20f, run.topInset + 60f, 0)
        run.gifts += falling
        repeat(120) { run.advance(1f / 120f) }
        assertEquals(2, run.round)
        assertTrue(run.bricks.isNotEmpty())
        assertEquals(3, run.lives)
        assertTrue(falling in run.gifts)
        val bonus = BrickBreakerRun().apply { start() }
        repeat(100) { bonus.advance(1f / 120f) }
        bonus.vx = 0f
        bonus.vy = 0f
        bonus.ballY = bonus.paddleY - 40f
        val normal = bonus.paddleHalf
        bonus.gifts += BreakerGift(bonus.paddleX, bonus.paddleY - 5f, 0)
        repeat(30) { bonus.advance(1f / 120f) }
        assertTrue(bonus.paddleHalf > normal * 1.35f)
        assertTrue(bonus.gifts.isEmpty())
        bonus.wideTime = 0.02f
        repeat(35) { bonus.advance(1f / 120f) }
        assertEquals(normal, bonus.paddleHalf, 0.001f)
        bonus.lives = 2
        bonus.gifts += BreakerGift(bonus.paddleX, bonus.paddleY - 5f, 1)
        bonus.advance(1f / 120f)
        assertEquals(3, bonus.lives)
        val points = bonus.score
        bonus.gifts += BreakerGift(bonus.paddleX, bonus.paddleY - 5f, 1)
        bonus.advance(1f / 120f)
        assertEquals(points + 25, bonus.score)
    }

    @Test
    fun lightCyclesResolveHeadOnCrashesTogetherAndWinningStartsAFreshArena() {
        val run = LightCyclesRun().apply { start() }
        run.board.fill(0)
        val player = run.cycles[0]
        val rival = run.cycles[1]
        player.position = 5 * run.columns + 5
        player.previous = player.position
        player.direction = 0
        rival.position = player.position + 2
        rival.previous = rival.position
        rival.direction = 2
        run.board[player.position] = 1
        run.board[rival.position] = 2
        run.board[rival.position - run.columns] = -1
        run.board[rival.position + run.columns] = -1
        run.step()
        assertTrue(!player.alive && !rival.alive)
        assertEquals(ArcadePhase.Over, run.phase)
        run.start()
        val trapped = run.cycles[1].position
        for (offset in listOf(-1, 1, -run.columns, run.columns)) run.board[trapped + offset] = -1
        run.step()
        assertTrue(run.cycles[0].alive)
        assertTrue(!run.cycles[1].alive)
        repeat(125) { run.advance(1f / 120f) }
        assertEquals(2, run.round)
        assertTrue(run.cycles.all { it.alive })
        assertTrue(run.cycles.all { it.trail.size < 3 })
        run.start()
        run.board.fill(0)
        val rider = run.cycles.first()
        run.pickups.clear()
        run.pickups += rider.position + 1
        run.step()
        assertTrue(rider.charged)
        run.board[rider.position + 1] = 1
        run.step()
        assertTrue(rider.alive && !rider.charged)
        assertTrue(rider.phaseTime > 1f)
        repeat(4) {
            run.board[rider.position + 1] = 2
            run.step()
            assertTrue(rider.alive)
        }
        rider.charged = true
        run.board[rider.position + 1] = -1
        run.step()
        assertTrue(!rider.alive)
    }

    private fun assertBundlesEqual(label: String, expected: Bundle, actual: Bundle) {
        assertEquals(label, expected.keySet(), actual.keySet())
        for (key in expected.keySet()) {
            val first = expected.get(key)
            val second = actual.get(key)
            when (first) {
                is FloatArray -> {
                    assertTrue("$label/$key", second is FloatArray)
                    assertTrue("$label/$key", first.contentEquals(second as FloatArray))
                }

                is BooleanArray -> assertTrue(
                    "$label/$key",
                    first.contentEquals(second as BooleanArray)
                )
                is IntArray -> assertTrue("$label/$key", first.contentEquals(second as IntArray))
                else -> assertEquals("$label/$key", first, second)
            }
        }
    }
}