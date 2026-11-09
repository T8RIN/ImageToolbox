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

package com.t8rin.imagetoolbox.core.games

import android.os.Bundle
import androidx.compose.runtime.saveable.SaverScope
import kotlinx.serialization.KSerializer
import androidx.compose.ui.geometry.Offset
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.imagetoolbox.core.games.catalog.DinoRun
import com.t8rin.imagetoolbox.core.games.catalog.DoodleRun
import com.t8rin.imagetoolbox.core.games.catalog.BirdRun
import com.t8rin.imagetoolbox.core.games.catalog.StackRun
import com.t8rin.imagetoolbox.core.games.catalog.PongRun
import com.t8rin.imagetoolbox.core.games.catalog.MergeRun
import com.t8rin.imagetoolbox.core.games.catalog.SnakeRun
import com.t8rin.imagetoolbox.core.games.catalog.TetrisRun
import com.t8rin.imagetoolbox.core.games.catalog.AsteroidsRun
import com.t8rin.imagetoolbox.core.games.catalog.BalanceTowerRun
import com.t8rin.imagetoolbox.core.games.catalog.BasketballRun
import com.t8rin.imagetoolbox.core.games.catalog.BomberBomb
import com.t8rin.imagetoolbox.core.games.catalog.BomberEnemy
import com.t8rin.imagetoolbox.core.games.catalog.BomberRun
import com.t8rin.imagetoolbox.core.games.catalog.BreakerBrick
import com.t8rin.imagetoolbox.core.games.catalog.BreakerGift
import com.t8rin.imagetoolbox.core.games.catalog.BrickBreakerRun
import com.t8rin.imagetoolbox.core.games.catalog.BubblePopRun
import com.t8rin.imagetoolbox.core.games.catalog.ControlledFlight
import com.t8rin.imagetoolbox.core.games.catalog.FishingRun
import com.t8rin.imagetoolbox.core.games.catalog.FlightControlRun
import com.t8rin.imagetoolbox.core.games.catalog.FlightRunway
import com.t8rin.imagetoolbox.core.games.catalog.FlowConnectRun
import com.t8rin.imagetoolbox.core.games.catalog.FruitSliceRun
import com.t8rin.imagetoolbox.core.games.catalog.GravityFlipRun
import com.t8rin.imagetoolbox.core.games.catalog.GravityObstacle
import com.t8rin.imagetoolbox.core.games.catalog.HexagonRun
import com.t8rin.imagetoolbox.core.games.catalog.KnifeHitRun
import com.t8rin.imagetoolbox.core.games.catalog.LaserMirrorsRun
import com.t8rin.imagetoolbox.core.games.catalog.LightCyclesRun
import com.t8rin.imagetoolbox.core.games.catalog.LunarLanderRun
import com.t8rin.imagetoolbox.core.games.catalog.MazeChaserRun
import com.t8rin.imagetoolbox.core.games.catalog.MinesweeperRun
import com.t8rin.imagetoolbox.core.games.catalog.MissileCommandRun
import com.t8rin.imagetoolbox.core.games.catalog.OrbitGate
import com.t8rin.imagetoolbox.core.games.catalog.OrbitSurvivalRun
import com.t8rin.imagetoolbox.core.games.catalog.RacerRun
import com.t8rin.imagetoolbox.core.games.catalog.RicochetBall
import com.t8rin.imagetoolbox.core.games.catalog.RicochetBrick
import com.t8rin.imagetoolbox.core.games.catalog.RicochetRun
import com.t8rin.imagetoolbox.core.games.catalog.RiverHopRun
import com.t8rin.imagetoolbox.core.games.catalog.RiverLane
import com.t8rin.imagetoolbox.core.games.catalog.SimonSaysRun
import com.t8rin.imagetoolbox.core.games.catalog.SliceFruit
import com.t8rin.imagetoolbox.core.games.catalog.SpaceRock
import com.t8rin.imagetoolbox.core.games.catalog.SpaceShot
import com.t8rin.imagetoolbox.core.games.catalog.SwimmingFish
import com.t8rin.imagetoolbox.core.games.catalog.TimbermanRun
import com.t8rin.imagetoolbox.core.games.catalog.TowerLoad
import com.t8rin.imagetoolbox.core.games.catalog.WhackAMoleRun
import com.t8rin.imagetoolbox.core.games.catalog.ZigZagRun
import com.t8rin.imagetoolbox.core.games.catalog.ZigZagTile
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import com.t8rin.imagetoolbox.core.ui.utils.helper.title
import kotlin.math.abs
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArcadeMechanicsTest {
    internal val factories: List<() -> ArcadeRun> = listOf(
        ::DinoRun,
        ::DoodleRun,
        ::BirdRun,
        ::StackRun,
        ::PongRun,
        ::MergeRun,
        ::SnakeRun,
        ::TetrisRun,
        ::RacerRun,
        ::RicochetRun,
        ::AsteroidsRun,
        ::FruitSliceRun,
        ::KnifeHitRun,
        ::MissileCommandRun,
        ::RiverHopRun,
        ::WhackAMoleRun,
        ::SimonSaysRun,
        ::BubblePopRun,
        ::FishingRun,
        ::HexagonRun,
        ::BomberRun,
        ::MinesweeperRun,
        ::MazeChaserRun,
        ::GravityFlipRun,
        ::ZigZagRun,
        ::TimbermanRun,
        ::OrbitSurvivalRun,
        ::LunarLanderRun,
        ::BrickBreakerRun,
        ::LightCyclesRun,
        ::FlightControlRun,
        ::BasketballRun,
        ::LaserMirrorsRun,
        ::FlowConnectRun,
        ::BalanceTowerRun
    )

    @Test
    fun collectionContainsThirtyFiveDistinctGamesAndTheOriginalEight() {
        assertEquals(35, ArcadeGame.playable.size)
        assertEquals(35, ArcadeGame.playable.distinct().size)
        assertEquals(
            listOf(
                ArcadeGame.Dino, ArcadeGame.Doodle, ArcadeGame.Bird, ArcadeGame.Stack,
                ArcadeGame.Pong, ArcadeGame.`2048`, ArcadeGame.Snake, ArcadeGame.Tetris
            ),
            ArcadeGame.playable.take(8)
        )
        assertEquals(35, ArcadeGame.playable.map { it.title }.distinct().size)
    }

    @Test
    fun everyGameRestoresItsLiveStateAtCompactWideAndTallSizes() {
        for ((width, height) in listOf(360f to 220f, 780f to 220f, 360f to 680f)) {
            for (factory in factories) {
                val run = factory().apply { resize(width, height, 28f); start() }
                repeat(90) { if (run.phase == ArcadePhase.Running) run.advance(1f / 120f) }
                run.stopInput()
                val saved = saveRun(run)
                val restored = restoreRun(run, saved)
                assertEquals(
                    run.javaClass.simpleName,
                    saved.getString("snapshot"),
                    saveRun(restored).getString("snapshot")
                )
                assertTrue(
                    "Snapshot too large: ${run.javaClass.simpleName}",
                    requireNotNull(saved.getString("snapshot")).length < 100_000
                )
            }
        }
    }

    @Test
    fun playStartsThePreparedLevelAndPauseKeepsEverySession() {
        for (factory in factories) {
            val run = factory().apply { resize(360f, 680f, 28f) }
            val before = saveRun(run)
            run.start()
            run.phase = ArcadePhase.Ready
            assertEquals(
                run.javaClass.simpleName,
                before.getString("snapshot"),
                saveRun(run).getString("snapshot")
            )
            run.start()
            repeat(15) { if (run.phase == ArcadePhase.Running) run.advance(1f / 120f) }
            run.pause()
            val paused = saveRun(run)
            val restored = restoreRun(run, paused)
            assertEquals(ArcadePhase.Paused, restored.phase)
            assertEquals(paused.getString("snapshot"), saveRun(restored).getString("snapshot"))
            restored.start()
            restored.pause()
            assertEquals(paused.getString("snapshot"), saveRun(restored).getString("snapshot"))
            restored.finish()
            restored.start()
            assertEquals(0, restored.score)
            assertEquals(ArcadePhase.Running, restored.phase)
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
                    28f
                )
                repeat(240) { if (run.phase == ArcadePhase.Running) run.advance(1f / 120f) }
                // JSON rejects NaN and infinity, including values nested in model collections.
                saveRun(run)
                assertTrue(run.isValid())
                run.start()
                assertEquals(0, run.score)
                assertEquals(ArcadePhase.Running, run.phase)
            }
        }
    }

    @Test
    fun missingOutdatedAndCorruptedSessionsFallBackToPreparation() {
        for (factory in factories) {
            val run = factory().apply { start() }
            val saver = arcadeRunSaver(runSerializer(run))
            assertEquals(null, saver.restore(Bundle()))
            val saved = requireNotNull(saveRun(run).getString("snapshot"))
            for (invalid in listOf("{", saved.replace("\"version\":1", "\"version\":999"))) {
                assertEquals(null, saver.restore(Bundle().apply { putString("snapshot", invalid) }))
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
        val restored = restoreRun(run)
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
            for ((x, y, vx, vy, _, kind) in run.fruits) if (y > run.height + 10f && vy < 0f) {
                positions += x.toInt()
                kinds += kind
                if (vx < -15f) left = true
                if (vx > 15f) right = true
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
    fun riverGeneratesCarsAndTrucksWithRoomToCross() {
        val run = RiverHopRun().apply { resize(width = 360f, height = 680f) }
        val lengths = mutableListOf<Float>()
        repeat(128) {
            run.reset()
            for ((_, kind, _, length, period) in run.lanes) {
                if (kind != 1) continue
                lengths += length
                assertTrue(period - length >= 2.4f - 0.001f)
            }
        }
        assertTrue(lengths.any { it <= 1.8f })
        assertTrue(lengths.any { it > 1.8f })
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
    fun bomberPlayerCanCrossBombsButStillHitsWallsAndBlasts() {
        val run = BomberRun().apply { start() }
        val origin = run.columns * 3 + 3
        val guard = (run.rows - 2) * run.columns + run.columns - 2
        run.board.fill(1)
        for (index in origin - 1..origin + 1) run.board[index] = 0
        run.board[guard] = 0
        run.enemies.clear()
        run.enemies += BomberEnemy(position = guard, previous = guard)
        run.player = origin
        run.previous = origin
        run.placeBomb()
        for (destination in listOf(origin + 1, origin, origin - 1)) {
            run.steer(
                delta = Offset(x = if (destination > run.player) 32f else -32f, y = 0f)
            )
            run.advance(seconds = 0.15f)
            assertEquals(destination, run.player)
        }
        assertEquals(origin, run.bombs.single().position)
        for (tile in 1..2) {
            run.board[origin - 2] = tile
            run.steer(delta = Offset(x = -32f, y = 0f))
            run.advance(seconds = 0.15f)
            assertEquals(origin - 1, run.player)
        }
        run.steer(delta = Offset(x = 32f, y = 0f))
        run.advance(seconds = 0.15f)
        run.advance(seconds = 0.15f)
        assertEquals(origin, run.player)
        run.invulnerable = 0f
        run.bombs.single().fuse = 0.01f
        run.advance(seconds = 0.02f)
        assertEquals(2, run.lives)
        assertTrue(origin in run.blasts)
    }

    @Test
    fun bomberBombsStillBlockEnemies() {
        val run = BomberRun().apply { start() }
        val origin = run.columns * 3 + 3
        run.board.fill(1)
        for (index in origin - 1..origin + 1) run.board[index] = 0
        run.player = origin - 1
        run.previous = run.player
        run.invulnerable = 10f
        run.time = 2f
        run.enemies.clear()
        run.enemies += BomberEnemy(position = origin + 1, previous = origin + 1)
        run.bombs += BomberBomb(position = origin, fuse = 10f, range = 2)
        run.advance(seconds = 0.4f)
        assertEquals(origin + 1, run.enemies.single().position)
        assertEquals(origin, run.bombs.single().position)
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
    fun minesweeperFlagLimitAllowsRemovingAndReusingFlags() {
        for (height in listOf(220f, 680f)) {
            val run = MinesweeperRun().apply {
                resize(width = 360f, height = height)
                start()
            }
            repeat(run.mineCount) { run.flag(index = it) }
            assertEquals(0, run.remainingFlags)
            run.flag(index = run.mineCount)
            assertEquals(0, run.cover[run.mineCount])
            assertEquals(run.mineCount, run.cover.count { it == 2 })
            val restored = restoreRun(run = run, state = saveRun(run))
            restored.flag(index = restored.mineCount)
            assertEquals(0, restored.cover[restored.mineCount])
            restored.flag(index = 0)
            assertEquals(1, restored.remainingFlags)
            restored.flag(index = restored.mineCount)
            assertEquals(0, restored.remainingFlags)
            assertEquals(2, restored.cover[restored.mineCount])
            assertEquals(0, restored.cover[0])
            restored.flag(index = 0)
            assertEquals(0, restored.cover[0])
        }
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
    fun gravityAcceleratesWithTimeAndRestoresItsPace() {
        val run = GravityFlipRun().apply {
            resize(width = 360f, height = 220f)
            start()
        }
        val initialSpeed = run.speed
        var previousDistance = 0f
        repeat(4) {
            val startDistance = run.distance
            val startSpeed = run.speed
            repeat(120 * 15) {
                run.obstacles.clear()
                run.beacons.clear()
                run.advance(seconds = 1f / 120f)
            }
            val traveled = run.distance - startDistance
            assertTrue(run.speed > startSpeed)
            assertTrue(traveled > previousDistance)
            previousDistance = traveled
        }
        assertEquals(0, run.score)
        assertTrue(run.speed > initialSpeed * 1.65f)
        assertTrue(run.speed < initialSpeed * 2f)
        run.pause()
        val restored = restoreRun(run = run, state = saveRun(run))
        assertEquals(run.speed, restored.speed, 0.001f)
        restored.start()
        assertEquals(run.speed, restored.speed, 0.001f)
        restored.advance(seconds = 0.1f)
        assertTrue(restored.speed > run.speed)
        restored.finish()
        restored.start()
        assertEquals(initialSpeed, restored.speed, 0.001f)
        assertEquals(0f, restored.distance, 0f)
    }

    @Test
    fun gravityObstaclesLeaveTimeToReachTheOppositeSurface() {
        for ((width, height) in listOf(360f to 220f, 780f to 220f, 360f to 680f)) {
            val run = GravityFlipRun().apply {
                resize(width = width, height = height)
                start()
            }
            var lastGate: GravityObstacle? = null
            val kinds = mutableSetOf<Int>()
            repeat(120 * 180) {
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
                    "Gravity: width=$width, height=$height, kind=${obstacle?.kind}, x=${obstacle?.x}, y=${run.y}",
                    ArcadePhase.Running,
                    run.phase
                )
            }
            assertEquals(setOf(0, 1, 2), kinds)
            assertTrue(run.score > 100)
            assertTrue(run.obstacles.size < 8)
        }
        val shield = GravityFlipRun().apply { start(); this.shield = true }
        val obstacle = GravityObstacle(shield.playerX, 30f, 0.4f, false)
        shield.obstacles += obstacle
        shield.advance(1f / 120f)
        assertTrue(obstacle.broken)
        assertTrue(!shield.shield)
        assertEquals(ArcadePhase.Running, shield.phase)
        shield.obstacles += GravityObstacle(shield.playerX, 30f, 0.4f, false)
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
            assertTrue(run.worldWidth >= run.width * 2.5f)
            assertTrue(run.span > (run.height - run.topInset) * 1.5f)
            assertTrue(run.zoom < 0.6f)
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
        repeat(3600) {
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
        assertTrue(controlled.zoom > 1.4f)
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
        player.charged = true
        rival.phaseTime = 1f
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
        run.start()
        run.board.fill(0)
        val expired = run.cycles.first()
        expired.phaseTime = 0.04f
        run.advance(0.05f)
        assertEquals(0f, expired.phaseTime, 0f)
        run.board[expired.position + 1] = 2
        run.step()
        assertTrue(!expired.alive)
    }

    @Test
    fun basketballChainsHoopsWithSlingshotShotsAndKeepsRimCollisionsPhysical() {
        for (height in listOf(220f, 680f)) {
            val run = BasketballRun().apply { resize(360f, height); start() }
            val finger = Offset(180f, height * 0.5f)
            run.begin(finger)
            run.aim(finger + Offset(-30f, 60f))
            run.stopInput()
            run.shoot()
            assertTrue(!run.airborne)
            repeat(12) { basket ->
                run.begin(finger)
                var horizontal = 0f
                var vertical = 0f
                repeat(8) {
                    val target = run.hoops[1].center
                    val rise = target.y - run.ball.y
                    vertical = kotlin.math.sqrt(2f * run.gravity * (rise + 55f))
                    val duration =
                        (vertical + kotlin.math.sqrt(vertical * vertical - 2f * run.gravity * rise)) / run.gravity
                    horizontal =
                        (run.hoops[1].positionAt(run.time + duration).x - run.ball.x) / duration
                    run.aim(finger + Offset(-horizontal, vertical) / run.power * run.unit)
                }
                assertTrue(
                    "Aim $basket at $height: ${run.hoops}, preview=${run.preview.size}",
                    run.preview.size > 10
                )
                val preview = run.preview.toList()
                val initial = run.ball
                run.shoot()
                assertEquals(horizontal, run.velocity.x, 0.01f)
                assertEquals(vertical, run.velocity.y, 0.01f)
                run.advance(0.035f)
                assertEquals(preview.first().x, run.ball.x, 0.01f)
                assertEquals(preview.first().y, run.ball.y, 0.01f)
                assertTrue(run.ball.y > initial.y)
                repeat(120 * 4) { run.advance(1f / 120f) }
                assertEquals("Basket $basket at height $height", basket + 1, run.made)
                assertTrue(!run.airborne)
                assertEquals(ArcadePhase.Running, run.phase)
                assertTrue(run.camera > 0f)
            }
            assertTrue(run.score > 12)
            val resumed = restoreRun(run)
            repeat(120) { run.advance(1f / 120f); resumed.advance(1f / 120f) }
            assertEquals(run.hoops, resumed.hoops)
            run.begin(finger)
            run.aim(finger + Offset(0f, 55f) * run.unit)
            run.shoot()
            repeat(120 * 3) { run.advance(1f / 120f) }
            assertTrue("Returning to the same hoop allows another attempt", !run.airborne)
            assertEquals(12, run.made)
            run.begin(finger)
            run.aim(finger + Offset(-60f, 0f) * run.unit)
            run.shoot()
            repeat(120 * 4) { if (run.phase == ArcadePhase.Running) run.advance(1f / 120f) }
            assertEquals(ArcadePhase.Over, run.phase)
        }
        val bank = BasketballRun().apply { start() }
        val finger = Offset(180f, 120f)
        bank.begin(finger)
        repeat(8) {
            val rise = bank.hoops[1].center.y - bank.ball.y
            val vertical = kotlin.math.sqrt(2f * bank.gravity * (rise + 90f))
            val duration =
                (vertical + kotlin.math.sqrt(vertical * vertical - 2f * bank.gravity * rise)) / bank.gravity
            val speed =
                ((bank.ball.x - bank.radius) + (bank.hoops[1].center.x - bank.radius) / 0.86f) / duration
            bank.aim(finger + Offset(speed, vertical) / bank.power * bank.unit)
        }
        bank.shoot()
        repeat(480) { bank.advance(1f / 120f) }
        assertEquals("A clean bank shot earns its bonus", 3, bank.score)
        val run = BasketballRun().apply { start() }
        val target = run.hoops[1]
        run.ball = target.center - Offset(target.halfWidth + 25f, 0f)
        run.velocity = Offset(300f, 0f)
        run.airborne = true
        repeat(12) { run.advance(1f / 120f) }
        assertTrue("The rim must rebound the ball", run.velocity.x < 0f)
        assertEquals(0, run.score)
        run.start()
        run.ball = Offset(345f, 70f)
        run.velocity = Offset(250f, 80f)
        run.airborne = true
        repeat(12) { run.advance(1f / 120f) }
        assertTrue("Side walls allow bank shots", run.velocity.x < 0f)
        run.start()
        run.ball = run.hoops[1].center - Offset(0f, 25f)
        run.velocity = Offset(0f, 200f)
        run.airborne = true
        repeat(24) { run.advance(1f / 120f) }
        assertEquals("Entering from below is not a basket", 0, run.score)
        val restored = restoreRun(run)
        repeat(20) { run.advance(1f / 120f); restored.advance(1f / 120f) }
        assertEquals(run.ball, restored.ball)
        assertEquals(run.velocity, restored.velocity)
        run.resize(780f, 220f)
        assertTrue(run.project(run.hoops[1].center).y > run.topInset)
    }

    @Test
    fun mirrorPuzzlesStartUnsolvedAndTheirBeamsReachEveryReceiverWithoutInfiniteLoops() {
        val run = LaserMirrorsRun().apply { start() }
        repeat(50) {
            assertTrue("Already solved round ${run.round}", run.lit.size < run.targets.size)
            assertTrue(run.board.count { it in 1..2 } >= 3)
            val round = run.round
            for (index in run.board.indices) {
                if (run.board[index] in 1..2 && run.board[index] != run.solution[index]) run.turn(
                    index
                )
                assertTrue(run.beam.size <= run.board.size * 4)
                if (run.lit.size == run.targets.size) break
                repeat(20) { run.advance(1f / 120f) }
            }
            assertEquals("Round $round", run.targets.size, run.lit.size)
            repeat(110) { run.advance(1f / 120f) }
            assertEquals(round + 1, run.round)
            assertTrue(!run.isAnimating)
        }
    }

    @Test
    fun flowBoardsHaveCompleteNonIntersectingSolutionsAndDraggingBackRemovesTheTail() {
        val run = FlowConnectRun().apply { start() }
        repeat(24) {
            val round = run.round
            val occupied = run.solution.flatMap { it.toList() }
            assertEquals(run.columns * run.columns, occupied.size)
            assertEquals(occupied.size, occupied.distinct().size)
            val first = run.solution.first()
            run.begin(run.center(first[0]))
            run.drag(run.center(first[1]))
            run.drag(run.center(first[2]))
            run.drag(run.center(first[1]))
            assertEquals(listOf(first[0], first[1]), run.paths.first())
            run.stopInput()
            run.restartPuzzle()
            for ((kind, path) in run.solution.withIndex()) {
                run.begin(run.center(path.first()))
                for (index in 1 until path.size) {
                    assertTrue(run.adjacent(path[index - 1], path[index]))
                    run.drag(run.center(path[index]))
                }
                run.stopInput()
                assertTrue("Round $round / pair $kind", run.connected(kind))
            }
            assertEquals(run.columns * run.columns, run.paths.sumOf { it.size })
            repeat(115) { run.advance(1f / 120f) }
            assertEquals(round + 1, run.round)
            assertTrue(!run.isAnimating)
        }
        run.begin(run.center(run.pairs[0][0]))
        val blocked = run.pairs[1][0]
        run.extend(blocked)
        assertTrue(blocked !in run.paths[0])
        run.start()
        run.pairs.clear()
        run.pairs += intArrayOf(0, 15)
        run.pairs += intArrayOf(3, 12)
        run.paths.clear()
        repeat(2) { run.paths += mutableListOf<Int>() }
        run.begin(run.center(0))
        run.drag(run.center(5))
        assertEquals(5, run.paths[0].last())
        assertTrue(run.paths[0].zipWithNext().all { run.adjacent(it.first, it.second) })
        repeat(240) { run.advance(1f / 120f) }
        assertEquals("Pausing a finger must not detach the route", 0, run.active)
        assertTrue(run.isAnimating)
        run.drag(run.center(10))
        assertEquals(10, run.paths[0].last())
        run.stopInput()
        repeat(60) { run.advance(1f / 120f) }
        assertTrue(!run.isAnimating)
        run.begin(run.center(0))
        run.drag(run.center(3))
        assertTrue(3 !in run.paths[0])
        run.drag(run.center(6))
        assertEquals("A blocked gesture must recover without another touch", 6, run.paths[0].last())
    }

    @Test
    fun flightRoutesRequireMatchingLandingZonesAndAirborneCollisionsEndTheRun() {
        val joining = FlightControlRun().apply { start(); planes.clear() }
        joining.runways[0] = FlightRunway(Offset(0f, 1f), 0f)
        val moving = ControlledFlight(100, 0, Offset(80f, 120f), 0f, true)
        joining.planes += moving
        joining.begin(moving.point)
        joining.drag(Offset(100f, 120f))
        joining.drag(Offset(240f, 120f))
        repeat(120) { joining.advance(1f / 120f) }
        joining.commit()
        repeat(60) {
            val x = moving.point.x
            joining.advance(1f / 120f)
            assertTrue(
                "Join ahead of the aircraft, without returning to old vertices",
                moving.point.x > x
            )
            assertEquals(0f, moving.heading, 0.01f)
        }
        for (kind in 0..2) {
            val run = FlightControlRun().apply { start(); planes.clear() }
            val target = run.zone(kind)
            val from = target - if (kind == 1) Offset(0f, 65f) else Offset(65f, 0f)
            val plane = ControlledFlight(
                100,
                kind,
                from,
                if (kind == 1) Math.PI.toFloat() / 2f else 0f,
                true
            )
            run.planes += plane
            run.begin(from)
            val heading = run.runways[kind].heading
            val endpoint = if (kind == 2) target else target +
                    Offset(kotlin.math.cos(heading), kotlin.math.sin(heading)) * 28f
            run.drag(endpoint)
            run.commit()
            assertTrue(plane.cleared)
            repeat(120 * 3) { run.advance(1f / 120f) }
            assertEquals(1, run.landed)
            assertTrue(run.score >= 10)
            assertEquals(ArcadePhase.Running, run.phase)
        }
        for (kind in 0..2) for (landed in listOf(0, 100)) for (heading in 0..3) {
            val guided = FlightControlRun().apply { start(); planes.clear(); this.landed = landed }
            val aircraft = ControlledFlight(
                100,
                kind,
                Offset(40f, 100f),
                heading * Math.PI.toFloat() / 2f,
                true
            )
            guided.planes += aircraft
            guided.begin(aircraft.point)
            for (point in listOf(
                Offset(160f, 100f),
                Offset(160f, 180f),
                Offset(240f, 180f),
                Offset(240f, 110f),
                guided.zone(kind)
            )) {
                guided.drag(point)
            }
            guided.commit()
            repeat(120 * 30) {
                guided.planes.removeAll { it.id != aircraft.id }
                guided.advance(1f / 120f)
            }
            assertEquals(
                "A sharp route must not trap a plane in a circle: $kind/$landed/$heading",
                landed + 1,
                guided.landed
            )
        }
        val drawing = FlightControlRun().apply { start(); planes.clear() }
        drawing.runways[0] = FlightRunway(Offset(1f, 1f), 0f)
        val guidedPlane = ControlledFlight(100, 0, Offset(60f, 120f), 0f, true)
        drawing.planes += guidedPlane
        drawing.begin(guidedPlane.point)
        repeat(100) { step ->
            drawing.drag(Offset(65f + step * 1.8f, 120f + kotlin.math.sin(step / 30f) * 25f))
            repeat(3) { drawing.advance(1f / 120f) }
        }
        assertTrue("The plane follows the route before release", guidedPlane.point.x > 110f)
        drawing.drag(drawing.zone(0))
        assertTrue(guidedPlane.cleared)
        repeat(1200) {
            drawing.planes.removeAll { it.id != guidedPlane.id }
            drawing.advance(1f / 120f)
        }
        assertEquals(
            "Drawing into the matching runway must land without another gesture",
            1,
            drawing.landed
        )
        val spawning = FlightControlRun().apply { start(); planes.clear() }
        val incoming = ControlledFlight(100, 0, Offset(-18f, 120f), 0f)
        spawning.planes += ControlledFlight(101, 1, Offset(95f, 120f), Math.PI.toFloat(), true)
        assertTrue("Future head-on conflicts must prevent spawning", !spawning.canSpawn(incoming))
        spawning.planes.clear()
        assertTrue(spawning.canSpawn(incoming))
        val layouts = mutableSetOf<List<FlightRunway>>()
        repeat(30) {
            spawning.start()
            layouts += spawning.runways.toList()
            for (kind in 0..2) {
                val zone = spawning.zone(kind)
                assertTrue(zone.x in 48f..spawning.width - 48f)
                assertTrue(zone.y in spawning.topInset + 40f..spawning.height - 40f)
                for (other in kind + 1..2) assertTrue((zone - spawning.zone(other)).getDistance() > 72f)
            }
        }
        assertTrue(layouts.size > 25)
        val run = FlightControlRun().apply { start(); planes.clear() }
        val wrong = ControlledFlight(100, 0, Offset(100f, 100f), 0f, true)
        run.planes += wrong
        run.begin(wrong.point)
        run.drag(run.zone(2))
        run.commit()
        assertTrue(!wrong.cleared)
        run.begin(wrong.point)
        run.drag(Offset(250f, 180f))
        val route = wrong.route.toList()
        run.stopInput()
        run.commit()
        assertEquals(route, wrong.route.toList())
        wrong.point = Offset(100f, 120f)
        wrong.route.clear()
        run.planes += ControlledFlight(101, 1, Offset(117f, 120f), Math.PI.toFloat(), true)
        repeat(20) { if (run.phase == ArcadePhase.Running) run.advance(1f / 120f) }
        assertEquals(ArcadePhase.Over, run.phase)
    }

    @Test
    fun balancingUsesTheMassAboveEverySupportAndStableTowersKeepGrowing() {
        val run = BalanceTowerRun().apply { start() }
        run.advance(0.4f)
        val moving = run.current.x
        assertTrue(moving > 180f)
        run.stopInput()
        assertTrue(!run.dropping)
        run.drop()
        run.advance(0.1f)
        assertTrue(run.dropping)
        assertEquals("A tap drops the block at its current position", moving, run.current.x, 0f)
        run.start()
        repeat(80) { index ->
            val center = 180f - run.current.offset
            run.current.x = center
            run.drop()
            repeat(180) { run.advance(1f / 120f) }
            assertEquals(index + 1, run.loads.size)
            assertTrue(run.stability >= 0f)
            assertEquals(ArcadePhase.Running, run.phase)
        }
        assertTrue(run.camera > 100f)
        assertTrue(run.score >= 800)
        run.start()
        run.loads += TowerLoad(210f, 0f, 100f, 20f, 0f, 0)
        run.loads += TowerLoad(240f, 20f, 100f, 20f, 0f, 0)
        run.loads += TowerLoad(270f, 40f, 100f, 20f, 0f, 0)
        run.evaluate()
        assertTrue(run.collapsing)
        assertEquals(0, run.pivot)
        repeat(110) { run.advance(1f / 120f) }
        assertEquals(ArcadePhase.Over, run.phase)
    }

}

private val runSerializers = mapOf(
    DinoRun::class to DinoRun.serializer(),
    DoodleRun::class to DoodleRun.serializer(),
    BirdRun::class to BirdRun.serializer(),
    StackRun::class to StackRun.serializer(),
    PongRun::class to PongRun.serializer(),
    MergeRun::class to MergeRun.serializer(),
    SnakeRun::class to SnakeRun.serializer(),
    TetrisRun::class to TetrisRun.serializer(),
    RacerRun::class to RacerRun.serializer(),
    RicochetRun::class to RicochetRun.serializer(),
    AsteroidsRun::class to AsteroidsRun.serializer(),
    FruitSliceRun::class to FruitSliceRun.serializer(),
    KnifeHitRun::class to KnifeHitRun.serializer(),
    MissileCommandRun::class to MissileCommandRun.serializer(),
    RiverHopRun::class to RiverHopRun.serializer(),
    WhackAMoleRun::class to WhackAMoleRun.serializer(),
    SimonSaysRun::class to SimonSaysRun.serializer(),
    BubblePopRun::class to BubblePopRun.serializer(),
    FishingRun::class to FishingRun.serializer(),
    HexagonRun::class to HexagonRun.serializer(),
    BomberRun::class to BomberRun.serializer(),
    MinesweeperRun::class to MinesweeperRun.serializer(),
    MazeChaserRun::class to MazeChaserRun.serializer(),
    GravityFlipRun::class to GravityFlipRun.serializer(),
    ZigZagRun::class to ZigZagRun.serializer(),
    TimbermanRun::class to TimbermanRun.serializer(),
    OrbitSurvivalRun::class to OrbitSurvivalRun.serializer(),
    LunarLanderRun::class to LunarLanderRun.serializer(),
    BrickBreakerRun::class to BrickBreakerRun.serializer(),
    LightCyclesRun::class to LightCyclesRun.serializer(),
    FlightControlRun::class to FlightControlRun.serializer(),
    BasketballRun::class to BasketballRun.serializer(),
    LaserMirrorsRun::class to LaserMirrorsRun.serializer(),
    FlowConnectRun::class to FlowConnectRun.serializer(),
    BalanceTowerRun::class to BalanceTowerRun.serializer()
)

@Suppress("UNCHECKED_CAST")
internal fun <T : ArcadeRun> runSerializer(run: T): KSerializer<T> =
    requireNotNull(runSerializers[run::class]) as KSerializer<T>

internal fun <T : ArcadeRun> saveRun(run: T): Bundle = with(arcadeRunSaver(runSerializer(run))) {
    with(SaverScope { true }) { save(run) } ?: error("Unprepared game")
}

internal fun <T : ArcadeRun> restoreRun(run: T, state: Bundle = saveRun(run)): T =
    requireNotNull(arcadeRunSaver(runSerializer(run)).restore(state)).apply { phase = run.phase }