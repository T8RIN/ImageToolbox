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

import android.content.Intent
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@SdkSuppress(minSdkVersion = 28)
class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Before
    fun prepareApp() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        .prepareBenchmarkApp()

    @Test
    fun startup() = baselineProfileRule.collect(
        packageName = PACKAGE_NAME,
        includeInStartupProfile = true,
        profileBlock = {
            startActivityAndWait()
            device.waitForIdle()
            device.dismissPrompts()
            check(device.wait(Until.hasObject(By.text(appString("single_edit"))), TIMEOUT))
        }
    )

    @Test
    fun mainScreenScrolling() = baselineProfileRule.collect(
        packageName = PACKAGE_NAME,
        includeInStartupProfile = false,
        profileBlock = {
            startActivityAndWait()
            device.waitForIdle()
            device.dismissPrompts()

            device.scrollList()

            check(device.wait(Until.hasObject(By.text(appString("single_edit"))), TIMEOUT))
        }
    )

    @Test
    fun singleEditor() = baselineProfileRule.collect(
        packageName = PACKAGE_NAME,
        includeInStartupProfile = false,
        profileBlock = {
            startActivityAndWait(
                Intent("shortcut")
                    .setClassName(
                        PACKAGE_NAME,
                        "com.t8rin.imagetoolbox.app.presentation.AppActivity"
                    )
                    .putExtra("screen_id", 0)
            )
            device.waitForIdle()
            device.dismissPrompts()
            check(device.wait(Until.hasObject(By.text(appString("pick_image"))), TIMEOUT))
            device.waitForIdle()
        }
    )

    @Test
    fun settingsScrolling() = baselineProfileRule.collect(
        packageName = PACKAGE_NAME,
        includeInStartupProfile = false,
        profileBlock = {
            startActivityAndWait()
            device.waitForIdle()
            device.dismissPrompts()
            device.openSettings()
            device.scrollList()
        }
    )

}