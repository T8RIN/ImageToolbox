package ru.tech.imageresizershrinker.presentation.root.utils.helper

import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Save
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ContextUtils.requestStoragePermission
import ru.tech.imageresizershrinker.presentation.root.widget.other.ToastDuration
import ru.tech.imageresizershrinker.presentation.root.widget.other.ToastHostState

fun Activity.failedToSaveImages(
    scope: CoroutineScope,
    failed: Int,
    done: Int,
    toastHostState: ToastHostState,
    savingPathString: String,
    showConfetti: () -> Unit
) {
    if (failed == -1) requestStoragePermission()
    else if (failed == 0) {
        scope.launch {
            toastHostState.showToast(
                getString(
                    R.string.saved_to_without_filename,
                    savingPathString
                ),
                Icons.Rounded.Save
            )
        }
        showReview(this)
        showConfetti()
    } else if (failed < done) {
        scope.launch {
            showConfetti()
            toastHostState.showToast(
                getString(
                    R.string.saved_to_without_filename,
                    savingPathString
                ),
                Icons.Rounded.Save
            )
            toastHostState.showToast(
                getString(R.string.failed_to_save, failed),
                Icons.Rounded.ErrorOutline,
                ToastDuration.Long
            )
        }
    } else {
        scope.launch {
            toastHostState.showToast(
                getString(R.string.failed_to_save, failed),
                Icons.Rounded.ErrorOutline,
                ToastDuration.Long
            )
        }
    }
}