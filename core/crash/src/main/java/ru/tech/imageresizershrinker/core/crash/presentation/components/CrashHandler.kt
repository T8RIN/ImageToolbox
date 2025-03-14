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

package ru.tech.imageresizershrinker.core.crash.presentation.components

import android.content.Intent
import android.os.Build
import android.util.Log
import ru.tech.imageresizershrinker.core.resources.BuildConfig
import ru.tech.imageresizershrinker.core.ui.utils.helper.AppVersion

interface CrashHandler {

    fun getIntent(): Intent

    private val intent: Intent get() = getIntent()

    private fun getCrashReason(): String = intent.getStringExtra(EXCEPTION_EXTRA) ?: ""

    fun getCrashInfo(): CrashInfo {
        val crashReason = getCrashReason()
        val exceptionName = crashReason.split("\n\n")[0].trim()
        val stackTrace = crashReason.split("\n\n").drop(1).joinToString("\n\n")

        val title = "[Bug] App Crash: $exceptionName"
        val deviceInfo =
            "Device: ${Build.MODEL} (${Build.BRAND} - ${Build.DEVICE}), SDK: ${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE}), App: $AppVersion (${BuildConfig.VERSION_CODE})\n\n"
        val body = "$deviceInfo$stackTrace"

        return CrashInfo(
            title = title,
            body = body,
            exceptionName = exceptionName,
            stackTrace = stackTrace
        )
    }

    companion object {
        internal const val EXCEPTION_EXTRA = "EXCEPTION_EXTRA"

        fun getCrashInfoAsExtra(
            throwable: Throwable
        ): String = "${throwable::class.java.simpleName}\n\n${Log.getStackTraceString(throwable)}"
    }
}

data class CrashInfo(
    val title: String,
    val body: String,
    val exceptionName: String,
    val stackTrace: String
)