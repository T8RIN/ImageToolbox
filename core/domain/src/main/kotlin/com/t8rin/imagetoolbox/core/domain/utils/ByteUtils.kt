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

package com.t8rin.imagetoolbox.core.domain.utils

import java.text.CharacterIterator
import java.text.StringCharacterIterator
import java.util.Locale


fun humanFileSize(
    bytes: Long,
    precision: Int = 1
): String {
    var tempBytes = bytes
    if (-1024 < tempBytes && tempBytes < 1024) {
        return "$tempBytes B"
    }
    val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
    while (tempBytes <= -999950 || tempBytes >= 999950) {
        tempBytes /= 1024
        ci.next()
    }
    return java.lang.String.format(
        Locale.getDefault(),
        "%.${precision}f %cB",
        tempBytes / 1024.0,
        ci.current()
    ).replace(",0", "")
}