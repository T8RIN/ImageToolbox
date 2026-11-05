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

package com.t8rin.colors.parser

import androidx.compose.ui.graphics.Color
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorNameParserTest {

    @Test
    fun reloadingDoesNotMutatePreviouslyPublishedNames() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        ColorNameParser.init(context)
        val names = ColorNameParser.colorNames
        val black = names.getValue("#FF000000")
        assertTrue(names.size > 30_000)

        ColorNameParser.init(context)

        assertNotSame(names, ColorNameParser.colorNames)
        assertSame(black, names.getValue("#FF000000"))
        assertEquals(names, ColorNameParser.colorNames)
        assertEquals("Black", ColorNameParser.parseColorName(Color.Black))
        assertEquals(Color.Black, ColorNameParser.parseColorFromNameSingle("black"))
    }

}