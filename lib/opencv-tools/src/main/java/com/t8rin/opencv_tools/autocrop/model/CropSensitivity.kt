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

package com.t8rin.opencv_tools.autocrop.model

import androidx.annotation.IntRange

/**
 * := (255 - [EDGE_CANDIDATE_THRESHOLD_MIN]) / [CROP_SENSITIVITY_MAX]
 */
private const val EDGE_CANDIDATE_THRESHOLD_PER_SENSITIVITY_STEP: Float = 20.5f
private const val EDGE_CANDIDATE_THRESHOLD_MIN: Int = 50
private const val CROP_SENSITIVITY_MAX: Int = 10

@Retention(AnnotationRetention.BINARY)
@IntRange(from = 0, to = CROP_SENSITIVITY_MAX.toLong())
annotation class CropSensitivity

@IntRange(50, 255)
internal fun edgeCandidateThreshold(@CropSensitivity cropSensitivity: Int): Int =
    ((CROP_SENSITIVITY_MAX - cropSensitivity) * EDGE_CANDIDATE_THRESHOLD_PER_SENSITIVITY_STEP).toInt() + EDGE_CANDIDATE_THRESHOLD_MIN