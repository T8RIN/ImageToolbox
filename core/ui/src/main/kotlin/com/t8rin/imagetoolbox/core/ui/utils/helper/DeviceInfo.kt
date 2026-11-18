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

package com.t8rin.imagetoolbox.core.ui.utils.helper

import android.app.ActivityManager
import android.content.Intent
import android.content.IntentFilter
import android.os.Build.BRAND
import android.os.Build.DEVICE
import android.os.Build.MODEL
import android.os.Build.VERSION.RELEASE
import android.os.Build.VERSION.SDK_INT
import android.os.StatFs
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import com.t8rin.imagetoolbox.core.domain.utils.humanFileSize
import com.t8rin.imagetoolbox.core.resources.BuildConfig.FLAVOR
import com.t8rin.imagetoolbox.core.resources.BuildConfig.VERSION_CODE
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.getDisplayName
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.core.utils.makeLog

@ConsistentCopyVisibility
data class DeviceInfo private constructor(
    val device: String,
    val sdk: String,
    val appVersion: String,
    val flavor: String,
    val locale: String,
    val availableStorage: Long,
    val totalStorage: Long,
    val isStorageLow: Boolean,
    val availableRam: Long,
    val totalRam: Long,
    val ramThreshold: Long,
    val isRamLow: Boolean,
    val usedHeap: Long,
    val maxHeap: Long
) {
    companion object {
        @Suppress("DEPRECATION")
        fun get(): DeviceInfo {
            val memory = ActivityManager.MemoryInfo().apply {
                appContext.getSystemService<ActivityManager>()?.getMemoryInfo(this)
            }
            val storage = StatFs(appContext.filesDir.absolutePath)
            val storageState = appContext.registerReceiver(
                null,
                IntentFilter().apply {
                    addAction(Intent.ACTION_DEVICE_STORAGE_LOW)
                    addAction(Intent.ACTION_DEVICE_STORAGE_OK)
                }
            )
            val runtime = Runtime.getRuntime()

            return DeviceInfo(
                device = "$MODEL ($BRAND - $DEVICE)",
                sdk = "$SDK_INT (Android $RELEASE)",
                appVersion = "$AppVersion ($VERSION_CODE)",
                flavor = FLAVOR,
                locale = AppCompatDelegate.getApplicationLocales().getDisplayName(),
                availableStorage = storage.availableBytes,
                totalStorage = storage.totalBytes,
                isStorageLow = storageState?.action == Intent.ACTION_DEVICE_STORAGE_LOW,
                availableRam = memory.availMem,
                totalRam = memory.totalMem,
                ramThreshold = memory.threshold,
                isRamLow = memory.lowMemory,
                usedHeap = runtime.totalMemory() - runtime.freeMemory(),
                maxHeap = runtime.maxMemory()
            )
        }

        fun getAsString(): String = get().run {
            listOf(
                "Device: $device",
                "SDK: $sdk",
                "App Version: $appVersion",
                "Flavor: $flavor",
                "Locale: $locale",
                "Internal storage: available=${humanFileSize(availableStorage)}, " +
                        "total=${humanFileSize(totalStorage)}, low=$isStorageLow",
                "RAM: available=${humanFileSize(availableRam)}, total=${humanFileSize(totalRam)}, " +
                        "threshold=${humanFileSize(ramThreshold)}, low=$isRamLow",
                "App heap: used=${humanFileSize(usedHeap)}, max=${humanFileSize(maxHeap)}"
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