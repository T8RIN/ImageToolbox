package ru.tech.imageresizershrinker.core.ui.utils.content_pickers

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import com.t8rin.logger.makeLog
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials


private data class FolderOpenerImpl(
    val openDocumentTree: ManagedActivityResultLauncher<Uri?, Uri?>,
    val onFailure: (Throwable) -> Unit
) : FolderOpener {

    override fun open(initialLocation: Uri?) {
        "Folder Open Start".makeLog()
        runCatching {
            openDocumentTree.launch(initialLocation)
        }.onFailure {
            it.makeLog("Folder Open Failure")
            onFailure(it)
        }.onSuccess {
            "Folder Open Success".makeLog()
        }
    }

}


@Stable
@Immutable
interface FolderOpener {
    fun open(initialLocation: Uri? = null)
}

@Composable
fun rememberFolderOpener(
    onFailure: () -> Unit = {},
    onSuccess: (Uri) -> Unit,
): FolderOpener {
    val openDocumentTree = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = { uri ->
            uri?.takeIf {
                it != Uri.EMPTY
            }?.let {
                onSuccess(it)
            } ?: onFailure()
        }
    )
    val essentials = rememberLocalEssentials()

    return remember(openDocumentTree) {
        derivedStateOf {
            FolderOpenerImpl(
                openDocumentTree = openDocumentTree,
                onFailure = {
                    onFailure()
                    essentials.handleFileSystemFailure(it)
                }
            )
        }
    }.value
}