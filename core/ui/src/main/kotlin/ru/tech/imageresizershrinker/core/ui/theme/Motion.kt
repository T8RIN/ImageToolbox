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

@file:Suppress("unused", "UNCHECKED_CAST")

package ru.tech.imageresizershrinker.core.ui.theme

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.material3.MotionScheme
import ru.tech.imageresizershrinker.core.domain.utils.cast
import ru.tech.imageresizershrinker.core.ui.utils.animation.FancyTransitionEasing


internal val CustomMotionScheme: MotionScheme = object : MotionScheme {
    val SpringDefaultSpatialDamping = 0.8f
    val SpringDefaultSpatialStiffness = 380.0f
    val SpringDefaultEffectsDamping = 1.0f
    val SpringDefaultEffectsStiffness = 1600.0f
    val SpringFastSpatialDamping = 0.6f
    val SpringFastSpatialStiffness = 800.0f
    val SpringFastEffectsDamping = 1.0f
    val SpringFastEffectsStiffness = 3800.0f
    val SpringSlowSpatialDamping = 0.8f
    val SpringSlowSpatialStiffness = 200.0f
    val SpringSlowEffectsDamping = 1.0f
    val SpringSlowEffectsStiffness = 800.0f

    private val defaultSpatialSpec =
        tween<Any>(
            durationMillis = 400,
            easing = FancyTransitionEasing
        )

    private val fastSpatialSpec =
        spring<Any>(
            dampingRatio = SpringFastSpatialDamping,
            stiffness = SpringFastSpatialStiffness
        )

    private val slowSpatialSpec =
        spring<Any>(
            dampingRatio = SpringSlowSpatialDamping,
            stiffness = SpringSlowSpatialStiffness
        )

    private val defaultEffectsSpec =
        spring<Any>(
            dampingRatio = SpringDefaultEffectsDamping,
            stiffness = SpringDefaultEffectsStiffness
        )

    private val fastEffectsSpec =
        tween<Any>(
            durationMillis = 300,
            easing = FancyTransitionEasing
        )

    private val slowEffectsSpec =
        tween<Any>(
            durationMillis = 500,
            easing = FancyTransitionEasing
        )

    override fun <T> defaultSpatialSpec(): FiniteAnimationSpec<T> = defaultSpatialSpec.cast()

    override fun <T> fastSpatialSpec(): FiniteAnimationSpec<T> = fastSpatialSpec.cast()

    override fun <T> slowSpatialSpec(): FiniteAnimationSpec<T> = slowSpatialSpec.cast()

    override fun <T> defaultEffectsSpec(): FiniteAnimationSpec<T> = defaultEffectsSpec.cast()

    override fun <T> fastEffectsSpec(): FiniteAnimationSpec<T> = fastEffectsSpec.cast()

    override fun <T> slowEffectsSpec(): FiniteAnimationSpec<T> = slowEffectsSpec.cast()
}