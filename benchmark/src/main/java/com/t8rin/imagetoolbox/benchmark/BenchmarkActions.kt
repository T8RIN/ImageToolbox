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

package com.t8rin.imagetoolbox.benchmark

import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

internal const val PACKAGE_NAME = "ru.tech.imageresizershrinker"
internal const val TIMEOUT = 10_000L

internal fun appString(name: String): String {
    val resources = InstrumentationRegistry.getInstrumentation()
        .context.packageManager.getResourcesForApplication(PACKAGE_NAME)
    return resources.getString(resources.getIdentifier(name, "string", PACKAGE_NAME))
}

private var isAppPrepared = false

internal fun UiDevice.prepareBenchmarkApp() {
    val permissions = buildList {
        if (Build.VERSION.SDK_INT >= 33) {
            add("android.permission.READ_MEDIA_IMAGES")
            add("android.permission.POST_NOTIFICATIONS")
        } else {
            add("android.permission.READ_EXTERNAL_STORAGE")
            add("android.permission.WRITE_EXTERNAL_STORAGE")
        }
        if (Build.VERSION.SDK_INT >= 29) add("android.permission.ACCESS_MEDIA_LOCATION")
    }
    permissions.forEach { permission ->
        executeShellCommand("pm grant $PACKAGE_NAME $permission")
    }
    // Visit recurring prompts before measuring and dismiss them permanently.
    for (attempt in 0 until if (isAppPrepared) 1 else 40) {
        executeShellCommand("am force-stop $PACKAGE_NAME")
        executeShellCommand(
            "am start -W -n $PACKAGE_NAME/com.t8rin.imagetoolbox.app.presentation.AppActivity"
        )
        wait(Until.findObject(By.text(appString("ok"))), if (attempt == 0) 1_000 else 100)
            ?.click()
        waitForIdle()
        dismissPrompts()
    }
    check(wait(Until.hasObject(By.text(appString("single_edit"))), TIMEOUT)) {
        "Main screen is not visible"
    }
    isAppPrepared = true
    waitForIdle()
    pressHome()
}

internal fun UiDevice.scrollList(swipeCount: Int = 4) {
    val list = findObjects(By.scrollable(true))
        .maxByOrNull { it.visibleBounds.height() }
    checkNotNull(list) { "Scrollable list is not visible" }
    val bounds = list.visibleBounds
    val x = bounds.centerX()
    val top = bounds.top + bounds.height() / 5
    val bottom = bounds.bottom - bounds.height() / 5

    repeat(swipeCount) {
        swipe(x, bottom, x, top, 20)
        waitForIdle()
    }
    repeat(swipeCount) {
        swipe(x, top, x, bottom, 20)
        waitForIdle()
    }
}

internal fun UiDevice.openSingleEditor() {
    var card = checkNotNull(findObject(By.text(appString("single_edit_sub"))))
    while (!card.isClickable) {
        card = checkNotNull(card.parent)
    }
    card.click()
}

internal fun UiDevice.dismissInvitation(): Boolean {
    var dialog = wait(
        Until.findObject(By.text(appString("image_toolbox_in_telegram"))), 500
    ) ?: return false
    while (dialog.findObject(By.text(appString("group"))) == null) {
        dialog = checkNotNull(dialog.parent)
    }
    // Scope the icon-only dismiss button to the invitation, not the screen behind it.
    dialog.findObjects(By.clickable(true)).single {
        it.childCount == 0 && it.text.isNullOrEmpty() && it.contentDescription.isNullOrEmpty()
    }.click()
    check(wait(Until.gone(By.text(appString("image_toolbox_in_telegram"))), TIMEOUT))
    waitForIdle()
    return true
}

internal fun UiDevice.dismissPrompts() {
    dismissInvitation()
    for (title in listOf("Weblate", appString("donation"))) {
        if (hasObject(By.text(title))) {
            val dismiss = wait(
                Until.findObject(By.desc(appString("dismiss_forever"))), 1_500
            ) ?: findObject(By.text(appString("dismiss_forever")))
            ?: findObject(By.text(appString("understood")))
            ?: findObject(By.text(appString("close")))
            checkNotNull(dismiss) { "Cannot dismiss $title" }.click()
            check(wait(Until.gone(By.text(title)), TIMEOUT))
            waitForIdle()
        }
    }
}

internal fun UiDevice.openSettings() {
    checkNotNull(findObject(By.desc(appString("settings")))).click()
    check(wait(Until.hasObject(By.text(appString("customization"))), TIMEOUT))
    waitForIdle()
}