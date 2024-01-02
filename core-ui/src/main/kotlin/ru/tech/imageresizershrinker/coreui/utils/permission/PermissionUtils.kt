package ru.tech.imageresizershrinker.coreui.utils.permission

import android.app.Activity
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

    fun Activity.checkPermissions(
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

    private fun Activity.hasPermissionAllowed(permission: String): Boolean {
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

    private fun <T> get(key: Preferences.Key<T>, default: T): T {
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