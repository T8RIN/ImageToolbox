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

import org.junit.Assert.assertEquals
import org.junit.Test

class StringUtilsTest {

    @Test
    fun testSlice() {
        val s = "0123456789"
        assertEquals("0123456789", s.slice(null, null))
        assertEquals("01234", s.slice(0, 5))
        assertEquals("1234", s.slice(1, 5))
        assertEquals("56789", s.slice(5, null))
        assertEquals("01234", s.slice(null, 5))
        assertEquals("789", s.slice(-3, null))
        assertEquals("78", s.slice(-3, -1))
        assertEquals("012", s.slice(null, -7))
        assertEquals("", s.slice(5, 2))
        assertEquals("", s.slice(10, 15))
        assertEquals("9", s.slice(-1, null))
        assertEquals("", s.slice(-1, -1))
    }
}
