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

package com.t8rin.imagetoolbox.benchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.filters.SdkSuppress
import org.junit.Rule
import org.junit.Test

@SdkSuppress(minSdkVersion = 28)
class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun startup() = baselineProfileRule.collect(
        packageName = PACKAGE_NAME,
        includeInStartupProfile = true,
        profileBlock = {
            startActivityAndWait()
            device.pressBack()
        }
    )

    @Test
    fun mainScreenScrolling() = baselineProfileRule.collect(
        packageName = PACKAGE_NAME,
        includeInStartupProfile = false,
        profileBlock = {
            startActivityAndWait()

            repeat(4) {
                device.swipe(
                    device.displayWidth / 2,
                    device.displayHeight * 3 / 4,
                    device.displayWidth / 2,
                    device.displayHeight / 4,
                    20
                )
                device.waitForIdle(500)
            }

            device.pressBack()
        }
    )

    private companion object {
        const val PACKAGE_NAME = "ru.tech.imageresizershrinker"
    }
}