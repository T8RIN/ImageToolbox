package ru.tech.imageresizershrinker.presentation.root.utils.helper

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Save
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.saving.SaveResult
import ru.tech.imageresizershrinker.presentation.root.widget.other.ToastHostState
import ru.tech.imageresizershrinker.presentation.root.widget.other.showError

fun parseSaveResult(
    saveResult: SaveResult,
    onSuccess: () -> Unit,
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
                        context.getString(
                            R.string.saved_to,
                            saveResult.savingPath,
                            saveResult.filename
                        ),
                        Icons.Rounded.Save
                    )
                }
                onSuccess()
            }
        }

        SaveResult.Error.MissingPermissions -> Unit //Requesting permissions does FileController
    }
}