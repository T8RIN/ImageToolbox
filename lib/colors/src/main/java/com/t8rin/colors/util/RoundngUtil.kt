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

@file:Suppress("unused")

package com.t8rin.colors.util

import kotlin.math.roundToInt

/**
 * Converts alpha, red, green or blue values from range of [0f-1f] to [0-255].
 */
fun Float.fractionToRGBRange() = (this * 255.0f).toInt()

/**
 * Converts alpha, red, green or blue values from range of [0f-1f] to [0-255] and returns
 * it as [String].
 */
fun Float.fractionToRGBString() = this.fractionToRGBRange().toString()

/**
 * Rounds this [Float] to another with 2 significant numbers
 * 0.1234 is rounded to 0.12
 * 0.127 is rounded to 0.13
 */
fun Float.roundToTwoDigits() = (this * 100.0f).roundToInt() / 100.0f

/**
 * Rounds this [Float] to closest int.
 */
fun Float.round() = this.roundToInt()

/**
 * Converts **HSV** or **HSL** colors that are in range of [0f-1f] to [0-100] range in [Integer]
 * with [Float.roundToInt]
 */
fun Float.fractionToPercent() = (this * 100.0f).roundToInt()

/**
 * Converts **HSV** or **HSL** colors that are in range of [0f-1f] to [0-100] range in [Integer]
 * with [Float.toInt]
 */
fun Float.fractionToIntPercent() = (this * 100.0f).toInt()
