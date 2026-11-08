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
        ::BubblePopRun, ::FishingRun, ::HexagonRun
    )

    @Test
    fun collectionContainsTwentyDistinctGamesAndTheOriginalEight() {
        assertEquals(20, ArcadeGame.playable.size)
        assertEquals(20, ArcadeGame.playable.distinct().size)
        assertEquals(
            listOf(
                ArcadeGame.Dino, ArcadeGame.Doodle, ArcadeGame.Bird, ArcadeGame.Stack,
                ArcadeGame.Pong, ArcadeGame.`2048`, ArcadeGame.Snake, ArcadeGame.Tetris
            ),
            ArcadeGame.playable.take(8)
        )
        assertEquals(20, ArcadeGame.playable.map { it.title }.distinct().size)
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
            assertBundlesEqual(run.javaClass.simpleName, saved, Bundle().also(restored::save))

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
                    assertTrue("${run.javaClass.simpleName}: $key", values.all(Float::isFinite))
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
        repeat(120 * 25) { if (run.phase == ArcadePhase.Running) run.advance(1f / 120f) }
        assertEquals(ArcadePhase.Over, run.phase)
        run.start()
        run.board.fill(-1)
        run.rush()
        repeat(120) { run.advance(1f / 120f) }
        assertEquals(ArcadePhase.Running, run.phase)
        assertTrue(run.board.take(run.columns).all { it >= 0 })
        assertTrue((0 until run.columns - 1).any { run.board[it] == run.board[it + 1] })
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

                is IntArray -> assertTrue("$label/$key", first.contentEquals(second as IntArray))
                else -> assertEquals("$label/$key", first, second)
            }
        }
    }
}