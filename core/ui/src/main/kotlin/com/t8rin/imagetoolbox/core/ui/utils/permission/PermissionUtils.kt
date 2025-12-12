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

package com.t8rin.imagetoolbox.core.ui.utils.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.runBlocking

object PermissionUtils {

    fun Context.checkPermissions(
        permissions: List<String>
    ): PermissionResult {

        val permissionPreference = PermissionPreference(this)

        val permissionResult = PermissionResult()

        val permissionStatus: HashMap<String, PermissionStatus> = hashMapOf()

        permissions.forEach { permission ->
            permissionPreference.setPermissionRequested(permission)
            if (hasPermissionAllowed(permission)) {
                permissionPreference.setPermissionAllowed(permission)
                permissionStatus[permission] = PermissionStatus.ALLOWED
            } else {
                val permissionRequestCount =
                    permissionPreference.permissionRequestCount(permission)
                when {
                    permissionRequestCount > 2 -> {
                        permissionStatus[permission] = PermissionStatus.DENIED_PERMANENTLY
                    }

                    else -> {
                        permissionStatus[permission] = PermissionStatus.NOT_GIVEN
                    }
                }
            }
        }

        permissionResult.permissionStatus = permissionStatus

        val isAnyPermissionDeniedPermanently =
            permissionStatus.values.any { it == PermissionStatus.DENIED_PERMANENTLY }

        if (isAnyPermissionDeniedPermanently) {
            permissionResult.finalStatus = PermissionStatus.DENIED_PERMANENTLY
            return permissionResult
        }

        val isAnyPermissionNotGiven =
            permissionStatus.values.any { it == PermissionStatus.NOT_GIVEN }

        if (isAnyPermissionNotGiven) {
            permissionResult.finalStatus = PermissionStatus.NOT_GIVEN
            return permissionResult
        }

        permissionResult.finalStatus = PermissionStatus.ALLOWED
        return permissionResult
    }


    fun Context.askUserToRequestPermissionExplicitly() {
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    fun Context.hasPermissionAllowed(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun Context.setPermissionsAllowed(permissions: List<String>) {
        permissions.forEach { permission ->
            PermissionPreference(this).setPermissionAllowed(permission)
        }
    }

}


private val Context.dataStore by preferencesDataStore(
    name = "permissionPreference"
)

private class PermissionPreference(private val context: Context) {

    private fun <T> get(
        key: Preferences.Key<T>,
        default: T
    ): T {
        return runBlocking {
            context.dataStore.edit {}[key] ?: default
        }
    }

    fun permissionRequestCount(permission: String): Int {
        return get(intPreferencesKey(permission), 0)
    }

    fun setPermissionRequested(permission: String) {
        runBlocking {
            context.dataStore.edit {
                it[intPreferencesKey(permission)] = (it[intPreferencesKey(permission)] ?: 0) + 1
            }
        }
    }

    fun setPermissionAllowed(permission: String) {
        runBlocking {
            context.dataStore.edit {
                it[intPreferencesKey(permission)] = 0
            }
        }
    }

}