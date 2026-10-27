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

package com.t8rin.neural_tools.assistant

internal fun repeatedAssistantSuffix(text: CharSequence): Int? {
    val size = text.length
    for (period in 1..minOf(256, size / 2)) {
        val minimum = if (period >= 16) maxOf(64, period * 2) else maxOf(48, period * 3)
        if (minimum > size || text[size - 1] != text[size - 1 - period]) continue
        var start = size - period
        while (start > 0 && text[start - 1] == text[start - 1 + period]) start--
        if (size - start >= minimum) return start + period
    }
    return null
}
