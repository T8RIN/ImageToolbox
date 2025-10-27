/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.ui.utils.helper

import android.os.Build.BRAND
import android.os.Build.DEVICE
import android.os.Build.MODEL
import android.os.Build.VERSION.RELEASE
import android.os.Build.VERSION.SDK_INT
import androidx.appcompat.app.AppCompatDelegate
import com.t8rin.imagetoolbox.core.resources.BuildConfig.FLAVOR
import com.t8rin.imagetoolbox.core.resources.BuildConfig.VERSION_CODE
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.getDisplayName
import com.t8rin.logger.makeLog

@ConsistentCopyVisibility
data class DeviceInfo private constructor(
    val device: String,
    val sdk: String,
    val appVersion: String,
    val flavor: String,
    val locale: String
) {
    companion object {
        fun get(): DeviceInfo {
            val device = "$MODEL ($BRAND - $DEVICE)"

            val sdk = "$SDK_INT (Android $RELEASE)"

            val appVersion = "$AppVersion ($VERSION_CODE)"

            val flavor = FLAVOR

            val locale = AppCompatDelegate.getApplicationLocales().getDisplayName()

            return DeviceInfo(
                device = device,
                sdk = sdk,
                appVersion = appVersion,
                flavor = flavor,
                locale = locale
            )
        }

        fun getAsString(): String {
            val (device, sdk, appVersion, flavor, locale) = get()

            return listOf(
                "Device: $device",
                "SDK: $sdk",
                "App Version: $appVersion",
                "Flavor: $flavor",
                "Locale: $locale"
            ).joinToString("\n")
        }

        fun pushLog(extra: String? = null) {
            getAsString().makeLog("DeviceInfo".plus(extra?.let { " $it" }.orEmpty()))
        }

        fun isPixel() = getAsString().contains(
            other = "google",
            ignoreCase = true
        )
    }
}