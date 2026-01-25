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

package com.t8rin.imagetoolbox.core.ui.utils.confetti

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.utils.appContext
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.xml.image.DrawableImage
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.random.Random


private val Color1 = Color(0xfffce18a)
private val Color2 = Color(0xFF009688)
private val Color3 = Color(0xfff4306d)
private val Color4 = Color(0xffb48def)
private val Color5 = Color(0xFF95FF82)
private val Color6 = Color(0xFF82ECFF)
private val Color7 = Color(0xFFFF9800)
private val Color8 = Color(0xFF0E008A)

private val defaultColors = listOf(
    Color1, Color2, Color3, Color4, Color5, Color6, Color7, Color8
)

private fun List<Color>.mapToPrimary(primary: Color): List<Int> = map {
    it.blend(primary.copy(1f), primary.alpha).toArgb()
}

private val defaultShapes by lazy {
    listOf(Shape.Square, Shape.Circle, Shape.Rectangle(0.2f))
}

private val confettiCache = mutableMapOf<Color, MutableMap<Particles.Type, List<Party>>>()


@Stable
class Particles(
    private val harmonizer: Color
) {
    fun build(
        type: Type
    ): List<Party> = confettiCache[harmonizer]?.get(type) ?: when (type) {
        Type.Default -> default(harmonizer)
        Type.Festive -> festiveBottom(harmonizer)
        Type.Explode -> explode(harmonizer)
        Type.Rain -> rain(harmonizer)
        Type.Side -> side(harmonizer)
        Type.Corners -> festiveCorners(harmonizer)
        Type.Toolbox -> toolbox(harmonizer)
    }.also {
        if (confettiCache[harmonizer]?.put(type, it) == null) {
            confettiCache[harmonizer] = mutableMapOf(type to it)
        }
    }

    companion object {

        private fun festive(
            primary: Color,
            xPos: Double = 0.5,
            yPos: Double = 1.0,
            angle: Int = Angle.TOP,
            duration: Long = 500,
            delay: Int = 0,
            spread: Int = 45
        ): List<Party> = Party(
            speed = 30f,
            maxSpeed = 50f,
            damping = 0.9f,
            angle = angle,
            spread = spread,
            shapes = defaultShapes,
            delay = delay,
            timeToLive = 3000L,
            colors = defaultColors.mapToPrimary(primary),
            emitter = Emitter(duration = duration, TimeUnit.MILLISECONDS).max(30),
            position = Position.Relative(xPos, yPos)
        ).let { party ->
            listOf(
                party,
                party.copy(
                    speed = 55f,
                    maxSpeed = 65f,
                    spread = (spread * 0.22f).roundToInt(),
                    emitter = Emitter(duration = duration, TimeUnit.MILLISECONDS).max(10),
                ),
                party.copy(
                    speed = 50f,
                    maxSpeed = 60f,
                    spread = (spread * 2.67f).roundToInt(),
                    emitter = Emitter(duration = duration, TimeUnit.MILLISECONDS).max(40),
                ),
                party.copy(
                    speed = 65f,
                    maxSpeed = 80f,
                    spread = (spread * 0.22f).roundToInt(),
                    emitter = Emitter(duration = duration, TimeUnit.MILLISECONDS).max(10),
                )
            )
        }

        fun default(
            primary: Color
        ): List<Party> = listOf(
            Party(
                speed = 0f,
                maxSpeed = 15f,
                damping = 0.9f,
                angle = Angle.BOTTOM,
                spread = Spread.ROUND,
                colors = defaultColors.mapToPrimary(primary),
                shapes = defaultShapes,
                emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
                position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0))
            ),
            Party(
                speed = 10f,
                maxSpeed = 30f,
                damping = 0.9f,
                angle = Angle.RIGHT - 45,
                spread = 60,
                colors = defaultColors.mapToPrimary(primary),
                shapes = defaultShapes,
                emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
                position = Position.Relative(0.0, 1.0)
            ),
            Party(
                speed = 10f,
                maxSpeed = 30f,
                damping = 0.9f,
                angle = Angle.RIGHT - 135,
                spread = 60,
                colors = defaultColors.mapToPrimary(primary),
                shapes = defaultShapes,
                emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
                position = Position.Relative(1.0, 1.0)
            )
        )

        fun festiveBottom(
            primary: Color
        ): List<Party> = festive(primary, 0.2)
            .plus(festive(primary, 0.8))

        fun explode(
            primary: Color,
            shape: Shape? = null,
            initialDelay: Int = 0
        ): List<Party> = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            shapes = shape?.let { listOf(it) } ?: defaultShapes,
            timeToLive = 3000,
            colors = defaultColors.mapToPrimary(primary),
            emitter = Emitter(duration = 200, TimeUnit.MILLISECONDS).max(100)
        ).let { party ->
            val (x1, y1) = Random.nextDouble(0.0, 0.3) to Random.nextDouble(0.0, 0.5)
            val (x2, y2) = Random.nextDouble(0.0, 0.3) to Random.nextDouble(0.5, 1.0)
            val (x3, y3) = Random.nextDouble(0.3, 0.7) to Random.nextDouble(0.0, 1.0)
            val (x4, y4) = Random.nextDouble(0.7, 1.0) to Random.nextDouble(0.0, 0.5)
            val (x5, y5) = Random.nextDouble(0.7, 1.0) to Random.nextDouble(0.5, 1.0)

            listOf(
                party.copy(
                    position = Position.Relative(x1, y1),
                    delay = initialDelay
                ),
                party.copy(
                    position = Position.Relative(x2, y2),
                    delay = initialDelay + 200
                ),
                party.copy(
                    position = Position.Relative(x3, y3),
                    delay = initialDelay + 400
                ),
                party.copy(
                    position = Position.Relative(x4, y4),
                    delay = initialDelay + 600
                ),
                party.copy(
                    position = Position.Relative(x5, y5),
                    delay = initialDelay + 800
                )
            )
        }

        fun rain(
            primary: Color
        ): List<Party> = Party(
            speed = 10f,
            maxSpeed = 30f,
            damping = 0.9f,
            shapes = defaultShapes,
            colors = defaultColors.mapToPrimary(primary),
            emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
        ).let { party ->
            listOf(
                party.copy(
                    angle = 45,
                    position = Position.Relative(0.0, 0.0),
                    spread = 90,
                ),
                party.copy(
                    angle = 90,
                    position = Position.Relative(0.5, 0.0),
                    spread = 360,
                ),
                party.copy(
                    angle = 135,
                    position = Position.Relative(1.0, 0.0),
                    spread = 90,
                )
            )
        }

        fun side(
            primary: Color
        ): List<Party> = listOf(
            festive(primary, 0.0, 0.0, Angle.RIGHT, 1000),
            festive(primary, 1.0, 0.33, Angle.LEFT, 1000, 150),
            festive(primary, 0.0, 0.66, Angle.RIGHT, 1000, 300),
            festive(primary, 1.0, 1.0, Angle.LEFT, 1000, 450),
        ).flatten()

        fun festiveCorners(
            primary: Color
        ): List<Party> = listOf(
            festive(primary, 0.0, 0.0, 45, 1000, 0, 25),
            festive(primary, 1.0, 0.0, 135, 1000, 150, 25),
            festive(primary, 0.0, 1.0, -45, 1000, 300, 25),
            festive(primary, 1.0, 1.0, 225, 1000, 450, 25),
        ).flatten()

        fun toolbox(
            primary: Color
        ): List<Party> = Shape.DrawableShape(
            AppCompatResources.getDrawable(
                appContext,
                R.drawable.ic_launcher_monochrome_24
            )!!.let {
                DrawableImage(
                    drawable = it,
                    width = it.intrinsicWidth,
                    height = it.intrinsicHeight
                )
            }
        ).let { shape ->
            val delay = 400
            explode(primary, shape) + explode(primary, shape, delay)
        }

    }

    enum class Type {
        Default, Festive, Explode, Rain, Side, Corners, Toolbox
    }
}