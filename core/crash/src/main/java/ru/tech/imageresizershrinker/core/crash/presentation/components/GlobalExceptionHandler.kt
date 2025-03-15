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

import android.content.Context
import android.content.Intent
import com.t8rin.logger.makeLog
import ru.tech.imageresizershrinker.core.crash.di.CrashModule
import ru.tech.imageresizershrinker.core.crash.presentation.CrashActivity
import ru.tech.imageresizershrinker.core.domain.remote.AnalyticsManager
import kotlin.system.exitProcess

private class GlobalExceptionHandler<T : CrashHandler> private constructor(
    private val applicationContext: Context,
    private val defaultHandler: Thread.UncaughtExceptionHandler?,
    private val activityToBeLaunched: Class<T>
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(
        p0: Thread,
        p1: Throwable
    ) {
        sendReport(p1)

        runCatching {
            p1.makeLog("FATAL_EXCEPTION")

            applicationContext.launchActivity(activityToBeLaunched, p1)
            exitProcess(0)
        }.getOrElse {
            defaultHandler?.uncaughtException(p0, p1)
        }
    }

    private fun Context.launchActivity(
        activity: Class<*>,
        throwable: Throwable
    ) = applicationContext.startActivity(
        Intent(applicationContext, activity).putExtra(
            CrashHandler.EXCEPTION_EXTRA,
            CrashHandler.getCrashInfoAsExtra(throwable)
        ).addFlags(defFlags)
    )

    companion object : AnalyticsManager by CrashModule.analyticsManager() {

        fun <T : CrashHandler> initialize(
            applicationContext: Context,
            activityToBeLaunched: Class<T>,
        ) = Thread.setDefaultUncaughtExceptionHandler(
            GlobalExceptionHandler(
                applicationContext = applicationContext,
                defaultHandler = Thread.getDefaultUncaughtExceptionHandler()!!,
                activityToBeLaunched = activityToBeLaunched
            )
        )

    }
}

private const val defFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
        Intent.FLAG_ACTIVITY_NEW_TASK or
        Intent.FLAG_ACTIVITY_CLEAR_TASK

fun Context.applyGlobalExceptionHandler() = GlobalExceptionHandler.initialize(
    applicationContext = applicationContext,
    activityToBeLaunched = CrashActivity::class.java,
)