/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.crash.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import kotlin.system.exitProcess


class GlobalExceptionHandler<T : CrashHandler> private constructor(
    private val applicationContext: Context,
    private val defaultHandler: Thread.UncaughtExceptionHandler?,
    private val activityToBeLaunched: Class<T>
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(
        p0: Thread,
        p1: Throwable
    ) {
        if (allowCollectCrashlytics) {
            Firebase.crashlytics.recordException(p1)
            Firebase.crashlytics.sendUnsentReports()
        }
        runCatching {
            Log.e(this.toString(), p1.stackTraceToString())
            applicationContext.launchActivity(activityToBeLaunched, p1)
            exitProcess(0)
        }.getOrElse {
            defaultHandler?.uncaughtException(p0, p1)
        }
    }

    private fun <T : Activity> Context.launchActivity(
        activity: Class<T>,
        exception: Throwable
    ) = applicationContext.startActivity(
        Intent(applicationContext, activity).putExtra(
            INTENT_DATA_NAME,
            "${exception::class.java.simpleName}\n\n${Log.getStackTraceString(exception)}"
        ).addFlags(defFlags)
    )

    companion object {

        var allowCollectCrashlytics: Boolean = false
            private set

        var allowCollectAnalytics: Boolean = false
            private set

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

        fun setAnalyticsCollectionEnabled(value: Boolean) {
            Firebase.analytics.setAnalyticsCollectionEnabled(value)
            allowCollectAnalytics = value
        }

        fun setAllowCollectCrashlytics(value: Boolean) {
            Firebase.crashlytics.isCrashlyticsCollectionEnabled = value
            allowCollectCrashlytics = value
        }

        fun registerScreenOpen(screen: Screen) {
            val screenName = screen.simpleName
            if (allowCollectAnalytics && screenName != null) {
                Log.d("SCREEN_OPEN", screenName)
                Firebase.analytics.logEvent("SCREEN_OPEN") {
                    param(FirebaseAnalytics.Param.CONTENT_TYPE, screenName)
                }
            }
        }

    }
}

private const val INTENT_DATA_NAME = "GlobalExceptionHandler"
private const val defFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
        Intent.FLAG_ACTIVITY_NEW_TASK or
        Intent.FLAG_ACTIVITY_CLEAR_TASK

abstract class CrashHandler : M3Activity() {
    fun getCrashReason(): String = intent.getStringExtra(INTENT_DATA_NAME) ?: ""
}