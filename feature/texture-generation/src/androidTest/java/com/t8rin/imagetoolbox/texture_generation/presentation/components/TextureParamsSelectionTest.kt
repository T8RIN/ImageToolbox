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

package com.t8rin.imagetoolbox.texture_generation.presentation.components

import android.content.Intent
import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureFilterType
import com.t8rin.imagetoolbox.texture_generation.domain.model.withDefaultsFor
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TextureParamsSelectionTest {
    @Test
    fun switchingBetweenPatternsAndOtherTexturesKeepsAnimatedPanelsConsistent() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val activity = instrumentation.startActivitySync(
            Intent(instrumentation.targetContext, TextureParamsTestActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ) as TextureParamsTestActivity
        try {
            val types = listOf(
                TextureFilterType.HatchPattern,
                TextureFilterType.BrushedMetal,
                TextureFilterType.RosettePattern,
                TextureFilterType.Sphere3D,
                TextureFilterType.CrossStitchPattern,
                TextureFilterType.Brick,
                TextureFilterType.HatchPattern
            )
            for (type in types) {
                instrumentation.runOnMainSync {
                    activity.params = activity.params.withDefaultsFor(type)
                }
                val deadline = SystemClock.uptimeMillis() + 5000
                while (activity.displayedType != type && SystemClock.uptimeMillis() < deadline) {
                    SystemClock.sleep(16)
                }
                assertEquals(type, activity.displayedType)
                SystemClock.sleep(100)
            }
            SystemClock.sleep(500)
            instrumentation.waitForIdleSync()
            assertEquals(TextureFilterType.HatchPattern, activity.displayedType)
        } finally {
            instrumentation.runOnMainSync { activity.finish() }
        }
    }
}
