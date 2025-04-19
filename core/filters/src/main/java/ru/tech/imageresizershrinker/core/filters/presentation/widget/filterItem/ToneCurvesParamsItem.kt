package ru.tech.imageresizershrinker.core.filters.presentation.widget.filterItem

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import com.t8rin.curves.ImageCurvesEditor
import com.t8rin.curves.ImageCurvesEditorState
import ru.tech.imageresizershrinker.core.filters.domain.model.ToneCurvesParams
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.LocalFilterPreviewModel

@Composable
internal fun ToneCurvesParamsItem(
    value: ToneCurvesParams,
    filter: UiFilter<ToneCurvesParams>,
    onFilterChange: (value: ToneCurvesParams) -> Unit,
    previewOnly: Boolean
) {
    val editorState: MutableState<ImageCurvesEditorState> =
        remember { mutableStateOf(ImageCurvesEditorState(value.controlPoints)) }

    Box(
        modifier = Modifier.padding(8.dp)
    ) {
        var bitmap by remember {
            mutableStateOf<Bitmap?>(null)
        }

        val previewModel = LocalFilterPreviewModel.current

        val context = LocalContext.current

        LaunchedEffect(context, previewModel) {
            bitmap = context.imageLoader.execute(
                ImageRequest.Builder(context)
                    .data(previewModel.data)
                    .build()
            ).image?.toBitmap()
        }

        ImageCurvesEditor(
            bitmap = bitmap,
            state = editorState.value,
            curvesSelectionText = {
                Text(
                    text = when (it) {
                        0 -> stringResource(R.string.all)
                        1 -> stringResource(R.string.color_red)
                        2 -> stringResource(R.string.color_green)
                        3 -> stringResource(R.string.color_blue)
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            },
            imageObtainingTrigger = false,
            onImageObtained = { },
            //shape = RoundedCornerShape(4.dp),
            containerModifier = Modifier.fillMaxWidth(),
            onStateChange = {
                onFilterChange(
                    ToneCurvesParams(
                        controlPoints = it.controlPoints
                    )
                )
            }
        )

        if (previewOnly) {
            Surface(
                modifier = Modifier.matchParentSize(),
                color = Color.Transparent,
                content = {}
            )
        }
    }
}