package ru.tech.imageresizershrinker.coreui.utils.helper

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Save
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coredomain.saving.SaveResult
import ru.tech.imageresizershrinker.coreui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.coreui.widget.other.ToastHostState
import ru.tech.imageresizershrinker.coreui.widget.other.showError

fun parseSaveResult(
    saveResult: SaveResult,
    onSuccess: suspend () -> Unit,
    toastHostState: ToastHostState,
    context: Context,
    scope: CoroutineScope
) {
    when (saveResult) {
        is SaveResult.Error.Exception -> {
            scope.launch {
                toastHostState.showError(context, saveResult.throwable)
            }
        }

        is SaveResult.Success -> {
            if (saveResult.savingPath.isNotEmpty() && saveResult.filename.isNotEmpty()) {
                scope.launch {
                    toastHostState.showToast(
                        message = context.getString(
                            R.string.saved_to,
                            saveResult.savingPath,
                            saveResult.filename
                        ),
                        icon = Icons.Rounded.Save,
                        duration = ToastDuration.Long
                    )
                }
                scope.launch { onSuccess() }
                showReview(context)
            }
        }

        SaveResult.Error.MissingPermissions -> Unit //Requesting permissions does FileController
    }
}