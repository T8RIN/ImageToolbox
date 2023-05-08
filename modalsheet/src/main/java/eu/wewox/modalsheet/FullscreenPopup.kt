package eu.wewox.modalsheet

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.R
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewRootForInspector
import androidx.compose.ui.semantics.popup
import androidx.compose.ui.semantics.semantics
import androidx.core.view.children
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import java.util.UUID

/**
 * Opens a popup with the given content.
 * The popup is visible as long as it is part of the composition hierarchy.
 *
 * Note: This is highly reduced version of the official Popup composable with some changes:
 * * Fixes an issue with action mode (copy-paste) menu, see https://issuetracker.google.com/issues/216662636
 * * Adds the view to the decor view of the window, instead of the window itself.
 * * Do not have properties, as Popup is laid out as fullscreen.
 *
 * @param onDismiss Executes when the user clicks outside of the popup.
 * @param content The content to be displayed inside the popup.
 */
@ExperimentalSheetApi
@Composable
internal fun FullscreenPopup(
    onDismiss: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val parentComposition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)
    val popupId = rememberSaveable { UUID.randomUUID() }
    val popupLayout = remember {
        PopupLayout(
            onDismiss = onDismiss,
            composeView = view,
            popupId = popupId
        ).apply {
            setContent(parentComposition) {
                Box(Modifier.semantics { this.popup() }) {
                    currentContent()
                }
            }
        }
    }

    DisposableEffect(popupLayout) {
        popupLayout.show()
        popupLayout.updateParameters(
            onDismiss = onDismiss
        )
        onDispose {
            popupLayout.disposeComposition()
            // Remove the window
            popupLayout.dismiss()
        }
    }

    SideEffect {
        popupLayout.updateParameters(
            onDismiss = onDismiss
        )
    }
}

/**
 * The layout the popup uses to display its content.
 */
@SuppressLint("ViewConstructor")
private class PopupLayout(
    private var onDismiss: (() -> Unit)?,
    composeView: View,
    popupId: UUID
) : AbstractComposeView(composeView.context),
    ViewRootForInspector {

    private val decorView = findOwner<Activity>(composeView.context)?.window?.decorView as ViewGroup

    override val subCompositionView: AbstractComposeView get() = this

    init {
        id = android.R.id.content
        setViewTreeLifecycleOwner(composeView.findViewTreeLifecycleOwner())
        setViewTreeViewModelStoreOwner(composeView.findViewTreeViewModelStoreOwner())
        setViewTreeSavedStateRegistryOwner(composeView.findViewTreeSavedStateRegistryOwner())
        // Set unique id for AbstractComposeView. This allows state restoration for the state
        // defined inside the Popup via rememberSaveable()
        setTag(R.id.compose_view_saveable_id_tag, "Popup:$popupId")
        setTag(R.id.consume_window_insets_tag, false)
    }

    private var content: @Composable () -> Unit by mutableStateOf({})

    override var shouldCreateCompositionOnAttachedToWindow: Boolean = false
        private set

    fun show() {
        // Place popup above all current views
        z = decorView.children.maxOf { it.z } + 1
        decorView.addView(
            this,
            0,
            MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        )

        requestFocus()
    }

    fun setContent(parent: CompositionContext, content: @Composable () -> Unit) {
        setParentCompositionContext(parent)
        this.content = content
        shouldCreateCompositionOnAttachedToWindow = true
    }

    @Composable
    override fun Content() {
        content()
    }

    @Suppress("ReturnCount")
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && onDismiss != null) {
            if (keyDispatcherState == null) {
                return super.dispatchKeyEvent(event)
            }
            if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                val state = keyDispatcherState
                state?.startTracking(event, this)
                return true
            } else if (event.action == KeyEvent.ACTION_UP) {
                val state = keyDispatcherState
                if (state != null && state.isTracking(event) && !event.isCanceled) {
                    onDismiss?.invoke()
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    fun updateParameters(
        onDismiss: (() -> Unit)?
    ) {
        this.onDismiss = onDismiss
    }

    fun dismiss() {
        setViewTreeLifecycleOwner(null)
        decorView.removeView(this)
    }
}

private inline fun <reified T> findOwner(context: Context): T? {
    var innerContext = context
    while (innerContext is ContextWrapper) {
        if (innerContext is T) {
            return innerContext
        }
        innerContext = innerContext.baseContext
    }
    return null
}
