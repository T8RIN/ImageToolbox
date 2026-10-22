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

package com.t8rin.imagetoolbox.core.data.saving

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.os.SystemClock
import android.provider.DocumentsContract
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleCallback
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.t8rin.imagetoolbox.core.di.entryPoint
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.settings.di.SettingsStateEntryPoint
import com.t8rin.imagetoolbox.core.settings.domain.model.CopyToClipboardMode
import com.t8rin.imagetoolbox.core.settings.domain.model.FilenameBehavior
import com.t8rin.imagetoolbox.core.ui.utils.ComposeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class OriginalFolderSavingInstrumentedTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext

    @Test
    @SdkSuppress(minSdkVersion = 26)
    fun savesNestedImagesAndDeletesOriginalsOnlyAfterSuccessfulSave() {
        val folderName = "!ImageToolbox-saving-${System.nanoTime()}"
        val treeUri = DocumentsContract.buildTreeDocumentUri(
            "com.android.externalstorage.documents",
            "primary:$folderName"
        )
        val activity = startActivity()
        val settings = context.entryPoint<SettingsStateEntryPoint>().settingsManager
        val originalSettings = runBlocking { settings.getSettingsState() }
        val hadAllFilesAccess = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                Environment.isExternalStorageManager()
        try {
            if (hadAllFilesAccess) {
                shell("appops set --uid ${context.packageName} MANAGE_EXTERNAL_STORAGE deny")
            }
            shell("mkdir /sdcard/$folderName")
            selectFolder(activity, treeUri)
            val root = checkNotNull(DocumentFile.fromTreeUri(context, treeUri))
            val bytes = ByteArrayOutputStream().use { stream ->
                val bitmap = Bitmap.createBitmap(16, 12, Bitmap.Config.ARGB_8888)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                bitmap.recycle()
                stream.toByteArray()
            }
            runBlocking {
                settings.setChecksumTypeForFilename(null)
                if (!originalSettings.addSequenceNumber) settings.toggleAddSequenceNumber()
                if (!originalSettings.saveToOriginalFolder) settings.toggleSaveToOriginalFolder()
                if (!originalSettings.deleteOriginalsAfterSave) settings.toggleDeleteOriginalsAfterSave()
                settings.setCopyToClipboardMode(CopyToClipboardMode.Disabled)
                settings.setFilenamePattern("saved_\\c.jpg")
                settings.setSaveFolderUri(null)
                withTimeout(5_000) {
                    settings.settingsState.first {
                        it.saveToOriginalFolder && it.deleteOriginalsAfterSave && it.addSequenceNumber &&
                                it.filenameBehavior is FilenameBehavior.None &&
                                it.copyToClipboardMode == CopyToClipboardMode.Disabled &&
                                it.filenamePattern == "saved_\\c.jpg" && it.saveFolderUri.isNullOrEmpty()
                    }
                }
                repeat(3) { directoryIndex ->
                    val directory = checkNotNull(root.createDirectory("Данные - $directoryIndex"))
                    repeat(3) { imageIndex ->
                        val original = createImage(directory, "source_$imageIndex.jpg", bytes)
                        val target = ImageSaveTarget(
                            imageInfo = ImageInfo(
                                width = 16,
                                height = 12,
                                imageFormat = ImageFormat.Jpg
                            ),
                            originalUri = original.uri.toString(),
                            sequenceNumber = imageIndex + 1,
                            data = bytes
                        )
                        val result = activity.fileController.save(target, false, null)
                        assertTrue(result.toString(), result is SaveResult.Success)
                        assertFalse("Original must be deleted after saving", original.exists())
                        val saved = checkNotNull(directory.findFile("saved_${imageIndex + 1}.jpg"))
                        val savedBytes = readBytes(saved.uri)
                        val savedBitmap = checkNotNull(
                            BitmapFactory.decodeByteArray(savedBytes, 0, savedBytes.size)
                        )
                        assertEquals(16, savedBitmap.width)
                        assertEquals(12, savedBitmap.height)
                        savedBitmap.recycle()
                    }
                    assertEquals(3, directory.listFiles().size)

                    val original = createImage(directory, "collision.jpg", bytes)
                    val existing = checkNotNull(directory.findFile("saved_1.jpg"))
                    val existingBytes = readBytes(existing.uri)
                    val result = activity.fileController.save(
                        ImageSaveTarget(
                            imageInfo = ImageInfo(
                                width = 16,
                                height = 12,
                                imageFormat = ImageFormat.Jpg
                            ),
                            originalUri = original.uri.toString(),
                            sequenceNumber = 1,
                            data = bytes
                        ),
                        false,
                        null
                    )
                    assertTrue(result.toString(), result is SaveResult.Error)
                    assertTrue("Failed save must preserve the original", original.exists())
                    assertArrayEquals(bytes, readBytes(original.uri))
                    assertArrayEquals(existingBytes, readBytes(existing.uri))
                    assertEquals(4, directory.listFiles().size)
                }
                assertEquals(3, root.listFiles().size)
            }
        } finally {
            runBlocking {
                val current = settings.getSettingsState()
                if (current.addSequenceNumber != originalSettings.addSequenceNumber) {
                    settings.toggleAddSequenceNumber()
                }
                if (current.saveToOriginalFolder != originalSettings.saveToOriginalFolder) {
                    settings.toggleSaveToOriginalFolder()
                }
                if (current.deleteOriginalsAfterSave != originalSettings.deleteOriginalsAfterSave) {
                    settings.toggleDeleteOriginalsAfterSave()
                }
                when (val behavior = originalSettings.filenameBehavior) {
                    is FilenameBehavior.Overwrite -> if (current.filenameBehavior !is FilenameBehavior.Overwrite) {
                        settings.toggleOverwriteFiles()
                    }

                    is FilenameBehavior.Random -> if (current.filenameBehavior !is FilenameBehavior.Random) {
                        settings.toggleRandomizeFilename()
                    }

                    is FilenameBehavior.Checksum -> settings.setChecksumTypeForFilename(behavior.hashingType)
                    is FilenameBehavior.None -> settings.setChecksumTypeForFilename(null)
                }
                settings.setCopyToClipboardMode(originalSettings.copyToClipboardMode)
                settings.setFilenamePattern(originalSettings.filenamePattern)
                settings.setSaveFolderUri(originalSettings.saveFolderUri)
            }
            shell("rm -r /sdcard/$folderName")
            if (hadAllFilesAccess) {
                shell("appops set --uid ${context.packageName} MANAGE_EXTERNAL_STORAGE allow")
            }
            context.revokeUriPermission(
                treeUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            instrumentation.runOnMainSync { activity.finish() }
        }
    }

    private fun startActivity(): ComposeActivity {
        var activity: ComposeActivity? = null
        val resumed = CountDownLatch(1)
        val monitor = ActivityLifecycleMonitorRegistry.getInstance()
        val callback = ActivityLifecycleCallback { current, stage ->
            if (current is ComposeActivity && stage == Stage.RESUMED) {
                activity = current
                resumed.countDown()
            }
        }
        instrumentation.runOnMainSync {
            monitor.addLifecycleCallback(callback)
            activity = monitor.getActivitiesInStage(Stage.RESUMED)
                .filterIsInstance<ComposeActivity>()
                .firstOrNull()
            if (activity != null) resumed.countDown()
        }
        try {
            context.startActivity(
                Intent().setClassName(
                    context,
                    "com.t8rin.imagetoolbox.app.presentation.AppActivity"
                )
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            assertTrue("App activity did not resume", resumed.await(15, TimeUnit.SECONDS))
            return checkNotNull(activity)
        } finally {
            instrumentation.runOnMainSync { monitor.removeLifecycleCallback(callback) }
        }
    }

    private fun selectFolder(activity: ComposeActivity, treeUri: Uri) {
        val selected = CountDownLatch(1)
        var selectedUri: Uri? = null
        lateinit var launcher: ActivityResultLauncher<Uri?>
        instrumentation.runOnMainSync {
            launcher = activity.activityResultRegistry.register(
                "original_folder_saving_test",
                ActivityResultContracts.OpenDocumentTree()
            ) {
                selectedUri = it
                selected.countDown()
            }
            launcher.launch(
                DocumentsContract.buildDocumentUriUsingTree(
                    treeUri,
                    DocumentsContract.getTreeDocumentId(treeUri)
                )
            )
        }
        try {
            instrumentation.uiAutomation.serviceInfo =
                instrumentation.uiAutomation.serviceInfo.apply {
                    flags = flags or AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
                }
            clickNode("android:id/button1", "Use this folder")
            clickNode("android:id/button1", "Allow", dialog = true)
            assertTrue("Folder selection timed out", selected.await(10, TimeUnit.SECONDS))
            assertEquals(treeUri, selectedUri)
        } finally {
            instrumentation.runOnMainSync { launcher.unregister() }
        }
    }

    private fun clickNode(resourceId: String, text: String, dialog: Boolean = false) {
        val deadline = SystemClock.uptimeMillis() + 10_000
        while (SystemClock.uptimeMillis() < deadline) {
            val root = instrumentation.uiAutomation.rootInActiveWindow
            if (dialog && root?.findAccessibilityNodeInfosByViewId("android:id/button2")
                    .isNullOrEmpty()
            ) {
                SystemClock.sleep(100)
                continue
            }
            val node = root?.findAccessibilityNodeInfosByViewId(resourceId)?.firstOrNull()
                ?: root?.findAccessibilityNodeInfosByText(text)?.firstOrNull()
            if (node != null && node.isEnabled && node.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                return
            }
            SystemClock.sleep(100)
        }
        error("Could not click $text")
    }

    private fun createImage(parent: DocumentFile, name: String, bytes: ByteArray): DocumentFile =
        checkNotNull(parent.createFile("image/jpeg", name)).also { file ->
            checkNotNull(context.contentResolver.openOutputStream(file.uri)).use { it.write(bytes) }
        }

    private fun readBytes(uri: Uri): ByteArray =
        checkNotNull(context.contentResolver.openInputStream(uri)).use { it.readBytes() }

    private fun shell(command: String) {
        instrumentation.uiAutomation.executeShellCommand(command).use { descriptor ->
            ParcelFileDescriptor.AutoCloseInputStream(descriptor).use { it.readBytes() }
        }
    }
}