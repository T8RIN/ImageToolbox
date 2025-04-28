package ru.tech.imageresizershrinker.core.ui.utils.content_pickers

import android.content.ActivityNotFoundException
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


private data class FileMakerImpl(
    val createDocument: ManagedActivityResultLauncher<String, Uri?>,
    val onFailure: (Throwable) -> Unit
) : FileMaker {

    override fun make(name: String) {
        "File Make Start".makeLog()
        runCatching {
            createDocument.launch(name)
        }.onFailure {
            it.makeLog("File Make Failure")
            onFailure(it)
        }.onSuccess {
            "File Make Success".makeLog()
        }
    }

}


@Stable
@Immutable
interface FileMaker {
    fun make(name: String)
}

@Composable
fun rememberFileCreator(
    mimeType: String = DefaultMimeType,
    onFailure: () -> Unit = {},
    onSuccess: (Uri) -> Unit,
): FileMaker {
    val createDocument = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(mimeType),
        onResult = { uri ->
            uri?.takeIf {
                it != Uri.EMPTY
            }?.let {
                onSuccess(it)
            } ?: onFailure()
        }
    )
    val essentials = rememberLocalEssentials()

    return remember(createDocument) {
        derivedStateOf {
            FileMakerImpl(
                createDocument = createDocument,
                onFailure = {
                    onFailure()

                    when (it) {
                        is ActivityNotFoundException -> essentials.showActivateFilesToast()
                        else -> essentials.showFailureToast(it)
                    }
                }
            )
        }
    }.value
}

private const val DefaultMimeType = "*/*"