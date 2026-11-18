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

package com.t8rin.imagetoolbox.core.games.presentation.components.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import com.t8rin.imagetoolbox.core.games.presentation.components.utils.ArcadeDefaults.LevelTransitionDuration
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
internal abstract class ArcadeRun {
    var phase by mutableStateOf(ArcadePhase.Ready)
    var gameOverReported = false
    var statsRoundStarted = false
    var score by mutableIntStateOf(0)
    var frame by mutableIntStateOf(0)
        private set
    var isAnimating by mutableStateOf(false)
    var levelTransition by mutableFloatStateOf(0f)
    var prepared by mutableStateOf(false)
    var previousBest = -1

    @Transient
    var startWhenReady = false
    var topInset = 44f
        private set
    var width = 360f
        private set
    var height = 240f
        private set

    fun prepare() {
        stopInput()
        score = 0
        isAnimating = false
        levelTransition = 0f
        gameOverReported = false
        statsRoundStarted = false
        phase = ArcadePhase.Ready
        reset()
        prepared = true
        redraw()
    }

    fun start() {
        if (!prepared || phase == ArcadePhase.Over || phase == ArcadePhase.Running) prepare()
        phase = ArcadePhase.Running
        redraw()
    }

    fun pause() {
        if (phase != ArcadePhase.Running) return
        phase = ArcadePhase.Paused
        stopInput()
    }

    fun finish() {
        stopInput()
        phase = ArcadePhase.Over
        redraw()
    }

    fun animateLevel() {
        if (phase == ArcadePhase.Running) {
            stopInput()
            levelTransition = LevelTransitionDuration
        }
    }

    fun redraw() {
        frame++
    }

    fun resize(
        width: Float,
        height: Float,
        topInset: Float = this.topInset
    ) {
        if (prepared && width == this.width && height == this.height &&
            topInset == this.topInset
        ) return
        this.topInset = topInset
        val previous = Size(width = this.width, height = this.height)
        this.width = width
        this.height = height
        if (prepared) onResize(previous)
        else prepare()
        redraw()
    }

    abstract fun reset()

    open fun isValid(): Boolean = width.isFinite() && height.isFinite() &&
            topInset.isFinite() &&
            width >= 120f && height > topInset + 60f

    open fun onRestore() = Unit
    open fun onResize(previous: Size) = Unit
    open fun stopInput() = Unit
    open fun advance(seconds: Float) = Unit
}