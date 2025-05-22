package ru.tech.imageresizershrinker.feature.draw.presentation.components.utils

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.HelperGridParams
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHelperGrid
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker

@Composable
fun BoxScope.BitmapDrawerPreview(
    preview: ImageBitmap,
    globalTouchPointersCount: Int,
    onReceiveMotionEvent: (MotionEvent) -> Unit,
    onInvalidate: () -> Unit,
    onUpdateCurrentDrawPosition: (Offset) -> Unit,
    onUpdateDrawDownPosition: (Offset) -> Unit,
    drawEnabled: Boolean,
    helperGridParams: HelperGridParams,
    beforeHelperGridModifier: Modifier = Modifier,
) {
    Picture(
        model = preview,
        modifier = Modifier
            .matchParentSize()
            .pointerDrawHandler(
                globalTouchPointersCount = globalTouchPointersCount,
                onReceiveMotionEvent = onReceiveMotionEvent,
                onInvalidate = onInvalidate,
                onUpdateCurrentDrawPosition = onUpdateCurrentDrawPosition,
                onUpdateDrawDownPosition = onUpdateDrawDownPosition,
                enabled = drawEnabled
            )
            .clip(RoundedCornerShape(2.dp))
            .transparencyChecker()
            .then(beforeHelperGridModifier)
            .drawHelperGrid(helperGridParams)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant(),
                shape = RoundedCornerShape(2.dp)
            ),
        contentDescription = null,
        contentScale = ContentScale.FillBounds
    )
}