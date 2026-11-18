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

package com.t8rin.imagetoolbox.core.crash.data

import android.system.ErrnoException
import android.system.OsConstants
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics.Event
import com.google.firebase.analytics.FirebaseAnalytics.Param
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.crashlytics.crashlytics
import com.t8rin.imagetoolbox.core.domain.remote.AnalyticsManager
import com.t8rin.imagetoolbox.core.domain.saving.io.InputFileNotFoundException
import com.t8rin.imagetoolbox.core.ui.utils.helper.DeviceInfo.Companion.get
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import java.io.IOException
import java.net.ProtocolException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

internal object AnalyticsManagerImpl : AnalyticsManager {

    override var allowCollectCrashlytics: Boolean = false

    override var allowCollectAnalytics: Boolean = false

    override fun updateAnalyticsCollectionEnabled(value: Boolean) {
        analytics.setAnalyticsCollectionEnabled(value)
        allowCollectAnalytics = value
    }

    override fun updateAllowCollectCrashlytics(value: Boolean) {
        crashlytics.isCrashlyticsCollectionEnabled = value
        allowCollectCrashlytics = value

        if (value) {
            crashlytics.sendUnsentReports()
        }
    }

    override fun sendReport(throwable: Throwable) {
        if (allowCollectCrashlytics && !throwable.isExpectedFailure()) {
            crashlytics.apply {
                recordException(throwable)
                sendUnsentReports()
            }
        }
    }

    override fun registerScreenOpen(screenName: String) {
        if (allowCollectAnalytics) {
            analytics.apply {
                logEvent(Event.SELECT_CONTENT) { param(Param.CONTENT_TYPE, screenName) }
                logEvent(screenName) { param(Param.CONTENT, deviceInfo()) }
            }
        }
    }

    override fun pushMetric(
        tag: String,
        metric: String
    ) {
        if (allowCollectAnalytics) {
            analytics.logEvent(tag) { param(Param.CONTENT, metric) }
        }
    }

    private fun deviceInfo(): String {
        val info = get()

        return listOf(
            "Device: ${info.device}",
            "App Version: ${info.appVersion}"
        ).joinToString(",")
    }

    private val analytics get() = Firebase.analytics
    private val crashlytics get() = Firebase.crashlytics
}

private fun Throwable.isExpectedFailure(): Boolean {
    val visited = mutableSetOf<Throwable>()
    return generateSequence(this) { it.cause }.takeWhile(visited::add).any {
        when (it) {
            is InputFileNotFoundException,
            is UnknownHostException,
            is SocketException,
            is SocketTimeoutException,
            is ConnectTimeoutException,
            is HttpRequestTimeoutException,
            is SSLException,
            is ProtocolException,
            is ResponseException -> true

            is ErrnoException -> when (it.errno) {
                OsConstants.ECONNABORTED,
                OsConstants.ECONNREFUSED,
                OsConstants.ECONNRESET,
                OsConstants.EHOSTUNREACH,
                OsConstants.ENETDOWN,
                OsConstants.ENETRESET,
                OsConstants.ENETUNREACH,
                OsConstants.ENOTCONN,
                OsConstants.ETIMEDOUT -> true

                else -> false
            }

            is IOException -> it.message == "Required SETTINGS preface not received"
            else -> it.javaClass.name == "android.system.GaiException"
        }
    }
}
