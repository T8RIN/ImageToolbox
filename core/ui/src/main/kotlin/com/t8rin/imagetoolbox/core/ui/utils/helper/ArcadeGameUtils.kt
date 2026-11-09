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

package com.t8rin.imagetoolbox.core.ui.utils.helper

import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Alien
import com.t8rin.imagetoolbox.core.resources.icons.Balance
import com.t8rin.imagetoolbox.core.resources.icons.Bomb
import com.t8rin.imagetoolbox.core.resources.icons.Brick
import com.t8rin.imagetoolbox.core.resources.icons.BubbleChart
import com.t8rin.imagetoolbox.core.resources.icons.Dinosaur
import com.t8rin.imagetoolbox.core.resources.icons.Explore
import com.t8rin.imagetoolbox.core.resources.icons.Filter2
import com.t8rin.imagetoolbox.core.resources.icons.Flag
import com.t8rin.imagetoolbox.core.resources.icons.Flare
import com.t8rin.imagetoolbox.core.resources.icons.Flight
import com.t8rin.imagetoolbox.core.resources.icons.Forest
import com.t8rin.imagetoolbox.core.resources.icons.Hexagon
import com.t8rin.imagetoolbox.core.resources.icons.Neurology
import com.t8rin.imagetoolbox.core.resources.icons.Nutrition
import com.t8rin.imagetoolbox.core.resources.icons.Orbit
import com.t8rin.imagetoolbox.core.resources.icons.PestControlRodent
import com.t8rin.imagetoolbox.core.resources.icons.Phishing
import com.t8rin.imagetoolbox.core.resources.icons.Planet
import com.t8rin.imagetoolbox.core.resources.icons.Raven
import com.t8rin.imagetoolbox.core.resources.icons.Rocket
import com.t8rin.imagetoolbox.core.resources.icons.Route
import com.t8rin.imagetoolbox.core.resources.icons.Shield
import com.t8rin.imagetoolbox.core.resources.icons.Shuffle
import com.t8rin.imagetoolbox.core.resources.icons.Snake
import com.t8rin.imagetoolbox.core.resources.icons.SportsBasketball
import com.t8rin.imagetoolbox.core.resources.icons.SportsMotorsports
import com.t8rin.imagetoolbox.core.resources.icons.SportsTennis
import com.t8rin.imagetoolbox.core.resources.icons.Stacks
import com.t8rin.imagetoolbox.core.resources.icons.SwapVerticalCircle
import com.t8rin.imagetoolbox.core.resources.icons.TrackChanges
import com.t8rin.imagetoolbox.core.resources.icons.TwoWheeler
import com.t8rin.imagetoolbox.core.resources.icons.VectorPolyline
import com.t8rin.imagetoolbox.core.resources.icons.ViewComfy
import com.t8rin.imagetoolbox.core.resources.icons.ViewQuilt
import com.t8rin.imagetoolbox.core.resources.icons.Water
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame

val ArcadeGame.title: Int
    get() = when (this) {
        ArcadeGame.Random -> R.string.grid_random
        ArcadeGame.Dino -> R.string.arcade_dino
        ArcadeGame.Doodle -> R.string.arcade_doodle
        ArcadeGame.Bird -> R.string.arcade_bird
        ArcadeGame.Stack -> R.string.arcade_stack
        ArcadeGame.Pong -> R.string.arcade_pong
        ArcadeGame.`2048` -> R.string.arcade_2048
        ArcadeGame.Snake -> R.string.arcade_snake
        ArcadeGame.Tetris -> R.string.arcade_tetris
        ArcadeGame.Hexagon -> R.string.arcade_hexagon
        ArcadeGame.Fishing -> R.string.arcade_fishing
        ArcadeGame.BubblePop -> R.string.arcade_bubblepop
        ArcadeGame.SimonSays -> R.string.arcade_simonsays
        ArcadeGame.WhackAMole -> R.string.arcade_whackamole
        ArcadeGame.RiverHop -> R.string.arcade_riverhop
        ArcadeGame.MissileCommand -> R.string.arcade_missilecommand
        ArcadeGame.KnifeHit -> R.string.arcade_knifehit
        ArcadeGame.FruitSlice -> R.string.arcade_fruitslice
        ArcadeGame.Asteroids -> R.string.arcade_asteroids
        ArcadeGame.Ricochet -> R.string.arcade_ricochet
        ArcadeGame.Racer -> R.string.arcade_racer
        ArcadeGame.LightCycles -> R.string.arcade_lightcycles
        ArcadeGame.BrickBreaker -> R.string.arcade_brickbreaker
        ArcadeGame.LunarLander -> R.string.arcade_lunarlander
        ArcadeGame.OrbitSurvival -> R.string.arcade_orbitsurvival
        ArcadeGame.Timberman -> R.string.arcade_timberman
        ArcadeGame.ZigZag -> R.string.arcade_zigzag
        ArcadeGame.GravityFlip -> R.string.arcade_gravityflip
        ArcadeGame.MazeChaser -> R.string.arcade_mazechaser
        ArcadeGame.Minesweeper -> R.string.arcade_minesweeper
        ArcadeGame.Bomber -> R.string.arcade_bomber
        ArcadeGame.BalanceTower -> R.string.arcade_balancetower
        ArcadeGame.FlightControl -> R.string.arcade_flightcontrol
        ArcadeGame.FlowConnect -> R.string.arcade_flowconnect
        ArcadeGame.LaserMirrors -> R.string.arcade_lasermirrors
        ArcadeGame.Basketball -> R.string.arcade_basketball
    }


val ArcadeGame.description: Int
    get() = when (this) {
        ArcadeGame.Random -> R.string.arcade_games_sub
        ArcadeGame.Dino -> R.string.arcade_dino_sub
        ArcadeGame.Doodle -> R.string.arcade_doodle_sub
        ArcadeGame.Bird -> R.string.arcade_bird_sub
        ArcadeGame.Stack -> R.string.arcade_stack_sub
        ArcadeGame.Pong -> R.string.arcade_pong_sub
        ArcadeGame.`2048` -> R.string.arcade_2048_sub
        ArcadeGame.Snake -> R.string.arcade_snake_sub
        ArcadeGame.Tetris -> R.string.arcade_tetris_sub
        ArcadeGame.Hexagon -> R.string.arcade_hexagon_sub
        ArcadeGame.Fishing -> R.string.arcade_fishing_sub
        ArcadeGame.BubblePop -> R.string.arcade_bubblepop_sub
        ArcadeGame.SimonSays -> R.string.arcade_simonsays_sub
        ArcadeGame.WhackAMole -> R.string.arcade_whackamole_sub
        ArcadeGame.RiverHop -> R.string.arcade_riverhop_sub
        ArcadeGame.MissileCommand -> R.string.arcade_missilecommand_sub
        ArcadeGame.KnifeHit -> R.string.arcade_knifehit_sub
        ArcadeGame.FruitSlice -> R.string.arcade_fruitslice_sub
        ArcadeGame.Asteroids -> R.string.arcade_asteroids_sub
        ArcadeGame.Ricochet -> R.string.arcade_ricochet_sub
        ArcadeGame.Racer -> R.string.arcade_racer_sub
        ArcadeGame.LightCycles -> R.string.arcade_lightcycles_sub
        ArcadeGame.BrickBreaker -> R.string.arcade_brickbreaker_sub
        ArcadeGame.LunarLander -> R.string.arcade_lunarlander_sub
        ArcadeGame.OrbitSurvival -> R.string.arcade_orbitsurvival_sub
        ArcadeGame.Timberman -> R.string.arcade_timberman_sub
        ArcadeGame.ZigZag -> R.string.arcade_zigzag_sub
        ArcadeGame.GravityFlip -> R.string.arcade_gravityflip_sub
        ArcadeGame.MazeChaser -> R.string.arcade_mazechaser_sub
        ArcadeGame.Minesweeper -> R.string.arcade_minesweeper_sub
        ArcadeGame.Bomber -> R.string.arcade_bomber_sub
        ArcadeGame.BalanceTower -> R.string.arcade_balancetower_sub
        ArcadeGame.FlightControl -> R.string.arcade_flightcontrol_sub
        ArcadeGame.FlowConnect -> R.string.arcade_flowconnect_sub
        ArcadeGame.LaserMirrors -> R.string.arcade_lasermirrors_sub
        ArcadeGame.Basketball -> R.string.arcade_basketball_sub
    }

val ArcadeGame.icon: ImageVector
    get() = when (this) {
        ArcadeGame.Basketball -> Icons.Outlined.SportsBasketball
        ArcadeGame.LaserMirrors -> Icons.Outlined.Flare
        ArcadeGame.FlowConnect -> Icons.Outlined.Route
        ArcadeGame.FlightControl -> Icons.Outlined.Flight
        ArcadeGame.BalanceTower -> Icons.Outlined.Balance
        ArcadeGame.Bomber -> Icons.Outlined.Bomb
        ArcadeGame.Minesweeper -> Icons.Outlined.Flag
        ArcadeGame.MazeChaser -> Icons.Outlined.Explore
        ArcadeGame.GravityFlip -> Icons.Outlined.SwapVerticalCircle
        ArcadeGame.ZigZag -> Icons.Outlined.VectorPolyline
        ArcadeGame.Timberman -> Icons.Outlined.Forest
        ArcadeGame.OrbitSurvival -> Icons.Outlined.Orbit
        ArcadeGame.LunarLander -> Icons.Outlined.Rocket
        ArcadeGame.BrickBreaker -> Icons.Outlined.ViewComfy
        ArcadeGame.LightCycles -> Icons.Outlined.TwoWheeler
        ArcadeGame.Racer -> Icons.Outlined.SportsMotorsports
        ArcadeGame.Ricochet -> Icons.Outlined.Brick
        ArcadeGame.Asteroids -> Icons.Outlined.Planet
        ArcadeGame.FruitSlice -> Icons.Outlined.Nutrition
        ArcadeGame.KnifeHit -> Icons.Outlined.TrackChanges
        ArcadeGame.MissileCommand -> Icons.Outlined.Shield
        ArcadeGame.RiverHop -> Icons.Rounded.Water
        ArcadeGame.WhackAMole -> Icons.Outlined.PestControlRodent
        ArcadeGame.SimonSays -> Icons.Outlined.Neurology
        ArcadeGame.BubblePop -> Icons.Outlined.BubbleChart
        ArcadeGame.Fishing -> Icons.Outlined.Phishing
        ArcadeGame.Hexagon -> Icons.Outlined.Hexagon
        ArcadeGame.Random -> Icons.Rounded.Shuffle
        ArcadeGame.Dino -> Icons.Outlined.Dinosaur
        ArcadeGame.Doodle -> Icons.Outlined.Alien
        ArcadeGame.Bird -> Icons.Outlined.Raven
        ArcadeGame.Stack -> Icons.Outlined.Stacks
        ArcadeGame.Pong -> Icons.Outlined.SportsTennis
        ArcadeGame.`2048` -> Icons.Outlined.Filter2
        ArcadeGame.Snake -> Icons.Outlined.Snake
        ArcadeGame.Tetris -> Icons.Outlined.ViewQuilt
    }