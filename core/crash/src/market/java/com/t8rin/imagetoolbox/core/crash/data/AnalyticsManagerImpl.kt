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

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.t8rin.imagetoolbox.core.domain.remote.AnalyticsManager
import com.t8rin.imagetoolbox.core.ui.utils.helper.DeviceInfo.Companion.get

internal object AnalyticsManagerImpl : AnalyticsManager {

    override var allowCollectCrashlytics: Boolean = false

    override var allowCollectAnalytics: Boolean = false

    override fun updateAnalyticsCollectionEnabled(value: Boolean) {
        Firebase.analytics.setAnalyticsCollectionEnabled(value)
        allowCollectAnalytics = value

        if (value) {
            Firebase.crashlytics.sendUnsentReports()
        }
    }

    override fun updateAllowCollectCrashlytics(value: Boolean) {
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = value
        allowCollectCrashlytics = value
    }

    override fun sendReport(throwable: Throwable) {
        if (allowCollectCrashlytics) {
            Firebase.crashlytics.recordException(throwable)
            Firebase.crashlytics.sendUnsentReports()
        }
    }

    override fun registerScreenOpen(screenName: String) {
        if (allowCollectAnalytics) {
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
                param(FirebaseAnalytics.Param.CONTENT_TYPE, screenName)
            }
            Firebase.analytics.logEvent(screenName) {
                param(FirebaseAnalytics.Param.CONTENT, deviceInfo())
            }
        }
    }

    private fun deviceInfo(): String {
        val info = get()

        return listOf(
            "Device: ${info.device}",
            "App Version: ${info.appVersion}"
        ).joinToString(",")
    }

}