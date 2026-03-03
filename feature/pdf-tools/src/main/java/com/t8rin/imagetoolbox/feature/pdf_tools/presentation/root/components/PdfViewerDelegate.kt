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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.components

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresExtension
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.pdf.ExperimentalPdfApi
import androidx.pdf.PdfDocument
import androidx.pdf.R
import androidx.pdf.view.PdfView
import androidx.pdf.view.ToolBoxView
import androidx.pdf.viewer.fragment.PdfViewerFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.t8rin.imagetoolbox.core.domain.utils.safeCast
import com.t8rin.imagetoolbox.core.ui.utils.ComposeActivity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalPdfApi::class)
@SuppressLint("RestrictedApi", "VisibleForTests", "PrivateResource")
@RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
internal class PdfViewerDelegate : PdfViewerFragment() {
    private val _loadingState = MutableStateFlow<Boolean?>(true)
    val loadingState: StateFlow<Boolean?> = _loadingState

    override fun onLoadDocumentSuccess(document: PdfDocument) {
        super.onLoadDocumentSuccess(document)
        _loadingState.value = false
    }

    override fun onPdfViewCreated(pdfView: PdfView) {
        super.onPdfViewCreated(pdfView)
        pdfContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            leftMargin = 0
            rightMargin = 0
        }
    }

    override fun onLoadDocumentError(error: Throwable) {
        super.onLoadDocumentError(error)
        _loadingState.value = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().safeCast<ComposeActivity>()?.let { activity ->
            activity.applyDynamicColors()
            lifecycleScope.launch {
                activity.applyGlobalNightMode()
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun ToolBoxView.getEditFab(): FloatingActionButton? {
        return try {
            val field = ToolBoxView::class.java.getDeclaredField("editButton")
            field.isAccessible = true
            field.get(this) as? FloatingActionButton
        } catch (_: Throwable) {
            null
        }
    }

    fun setScheme(colorScheme: ColorScheme) {
        toolboxView.getEditFab()?.let { fab ->
            fab.backgroundTintList = ColorStateList.valueOf(colorScheme.tertiaryContainer.toArgb())
            fab.imageTintList = ColorStateList.valueOf(colorScheme.onTertiaryContainer.toArgb())
            fab.invalidate()
        }

        pdfSearchView.apply {
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadii = floatArrayOf(
                    36f.dp, 36f.dp,
                    36f.dp, 36f.dp,
                    0f, 0f,
                    0f, 0f
                )
                setColor(colorScheme.surfaceContainer.toArgb())
            }
        }

        pdfSearchView.findViewById<View>(R.id.searchViewContainer).apply {
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 28f.dp
                setColor(colorScheme.surfaceBright.toArgb())
            }
            invalidate()
        }

        pdfSearchView.searchQueryBox.apply {
            setTextColor(colorScheme.onSurface.toArgb())
            setHintTextColor(colorScheme.onSurfaceVariant.toArgb())
            highlightColor = colorScheme.primary.copy(alpha = 0.3f).toArgb()
            invalidate()
        }

        pdfSearchView.matchStatusTextView.apply {
            setTextColor(
                colorScheme.onSurfaceVariant.toArgb()
            )
            invalidate()
        }

        val iconTint = ColorStateList.valueOf(
            colorScheme.onSurfaceVariant.toArgb()
        )
        val buttonBgTint = ColorStateList.valueOf(
            Color.Transparent.toArgb()
        )

        listOf(
            pdfSearchView.findPrevButton,
            pdfSearchView.findNextButton,
            pdfSearchView.closeButton
        ).forEach { button ->
            ImageViewCompat.setImageTintList(button, iconTint)
            button.backgroundTintList = buttonBgTint
            button.invalidate()
        }

        pdfSearchView.invalidate()

        ContextCompat.getDrawable(requireContext(), R.drawable.page_indicator_background)
            ?.mutate()?.let { pageIndicatorDrawable ->
                pageIndicatorDrawable.setTint(colorScheme.surfaceContainer.toArgb())
                pdfView.fastScrollPageIndicatorBackgroundDrawable = pageIndicatorDrawable
            }

        pdfView.invalidate()
    }

    private val Float.dp: Float
        get() = this * requireContext().resources.displayMetrics.density

    companion object {
        private val _searchToggle: Channel<Unit> = Channel(Channel.BUFFERED)
        val searchToggle: Flow<Unit> = _searchToggle.receiveAsFlow()

        fun toggleSearch() {
            _searchToggle.trySend(Unit)
        }
    }
}