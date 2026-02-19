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

package com.t8rin.imagetoolbox.app.presentation.components.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context.ACTIVITY_SERVICE
import android.os.Build.VERSION.SDK_INT
import com.t8rin.logger.makeLog

internal fun Application.isMain(): Boolean =
    getProcessName().makeLog("Current Process") == packageName.makeLog("Current packageName")


@SuppressLint("PrivateApi")
internal fun Application.getProcessName(): String? {
    if (SDK_INT >= 28) {
        return Application.getProcessName()
    }

    // Try using ActivityThread to determine the current process name.
    try {
        val activityThread = Class.forName(
            "android.app.ActivityThread",
            false,
            this::class.java.getClassLoader()
        )
        val packageName: Any?
        val currentProcessName = activityThread.getDeclaredMethod("currentProcessName")
        currentProcessName.isAccessible = true
        packageName = currentProcessName.invoke(null)
        if (packageName is String) {
            return packageName
        }
    } catch (exception: Throwable) {
        exception.makeLog()
    }

    // Fallback to the most expensive way
    val pid: Int = android.os.Process.myPid()
    val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager

    val processes = am.runningAppProcesses
    if (processes != null && processes.isNotEmpty()) {
        for (process in processes) {
            if (process.pid == pid) {
                return process.processName
            }
        }
    }

    return null
}