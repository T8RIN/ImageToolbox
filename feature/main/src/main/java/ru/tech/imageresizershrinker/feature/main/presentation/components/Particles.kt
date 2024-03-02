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

package ru.tech.imageresizershrinker.feature.main.presentation.components

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import ru.tech.imageresizershrinker.core.ui.theme.blend
import java.util.concurrent.TimeUnit
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
).map { it.toArgb() }

@Stable
class Particles(primary: Color) {

    private val data = mapOf(
        Type.Default to default(primary),
        Type.Festive to festiveBottom(primary),
        Type.Explode to explode(primary),
        Type.Rain to rain(primary),
        Type.Side to side(primary)
    )

    fun forType(type: Type): List<Party> = data[type]!!

    companion object {
        fun default(primary: Color) = listOf(
            Party(
                speed = 0f,
                maxSpeed = 15f,
                damping = 0.9f,
                angle = Angle.BOTTOM,
                spread = Spread.ROUND,
                colors = defaultColors.map { it.blend(primary, 0.5f) },
                emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
                position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0))
            ),
            Party(
                speed = 10f,
                maxSpeed = 30f,
                damping = 0.9f,
                angle = Angle.RIGHT - 45,
                spread = 60,
                colors = defaultColors.map { it.blend(primary, 0.5f) },
                emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
                position = Position.Relative(0.0, 1.0)
            ),
            Party(
                speed = 10f,
                maxSpeed = 30f,
                damping = 0.9f,
                angle = Angle.RIGHT - 135,
                spread = 60,
                colors = defaultColors.map { it.blend(primary, 0.5f) },
                emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
                position = Position.Relative(1.0, 1.0)
            )
        )

        private fun festive(
            primary: Color,
            xPos: Double = 0.5,
            yPos: Double = 1.0,
            angle: Int = Angle.TOP,
            duration: Long = 500
        ): List<Party> {
            val party = Party(
                speed = 30f,
                maxSpeed = 50f,
                damping = 0.9f,
                angle = angle,
                spread = 45,
                timeToLive = 3000L,
                colors = defaultColors.map { it.blend(primary, 0.5f) },
                emitter = Emitter(duration = duration, TimeUnit.MILLISECONDS).max(30),
                position = Position.Relative(xPos, yPos)
            )

            return listOf(
                party,
                party.copy(
                    speed = 55f,
                    maxSpeed = 65f,
                    spread = 10,
                    emitter = Emitter(duration = duration, TimeUnit.MILLISECONDS).max(10),
                ),
                party.copy(
                    speed = 50f,
                    maxSpeed = 60f,
                    spread = 120,
                    emitter = Emitter(duration = duration, TimeUnit.MILLISECONDS).max(40),
                ),
                party.copy(
                    speed = 65f,
                    maxSpeed = 80f,
                    spread = 10,
                    emitter = Emitter(duration = duration, TimeUnit.MILLISECONDS).max(10),
                )
            )
        }

        fun festiveBottom(
            primary: Color
        ) = festive(primary, 0.2)
            .plus(festive(primary, 0.8))

        fun explode(primary: Color): List<Party> {
            val party = Party(
                speed = 0f,
                maxSpeed = 30f,
                damping = 0.9f,
                spread = 360,
                timeToLive = 3000,
                colors = defaultColors.map { it.blend(primary, 0.5f) },
                emitter = Emitter(duration = 200, TimeUnit.MILLISECONDS).max(100)
            )
            val (x1, y1) = Random.nextDouble(0.0, 0.3) to Random.nextDouble(0.0, 0.5)
            val (x2, y2) = Random.nextDouble(0.0, 0.3) to Random.nextDouble(0.5, 1.0)
            val (x3, y3) = Random.nextDouble(0.3, 0.7) to Random.nextDouble(0.0, 1.0)
            val (x4, y4) = Random.nextDouble(0.7, 1.0) to Random.nextDouble(0.0, 0.5)
            val (x5, y5) = Random.nextDouble(0.7, 1.0) to Random.nextDouble(0.5, 1.0)

            return listOf(
                party.copy(
                    position = Position.Relative(x1, y1)
                ),
                party.copy(
                    position = Position.Relative(x2, y2)
                ),
                party.copy(
                    position = Position.Relative(x3, y3)
                ),
                party.copy(
                    position = Position.Relative(x4, y4)
                ),
                party.copy(
                    position = Position.Relative(x5, y5)
                )
            )
        }

        fun rain(primary: Color): List<Party> {
            val party = Party(
                speed = 10f,
                maxSpeed = 30f,
                damping = 0.9f,
                colors = defaultColors.map { it.blend(primary, 0.5f) },
                emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
            )
            return listOf(
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
            festive(primary, 1.0, 0.33, Angle.LEFT, 1000),
            festive(primary, 0.0, 0.66, Angle.RIGHT, 1000),
            festive(primary, 1.0, 1.0, Angle.LEFT, 1000),
        ).flatten()

    }

    enum class Type {
        Default, Festive, Explode, Rain, Side
    }
}