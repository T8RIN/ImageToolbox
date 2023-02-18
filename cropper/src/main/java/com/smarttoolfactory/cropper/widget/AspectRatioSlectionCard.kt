package com.smarttoolfactory.cropper.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.cropper.model.CropAspectRatio

@Composable
fun AspectRatioSelectionCard(
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.surface,
    color: Color,
    cropAspectRatio: CropAspectRatio,
    onClick: ((List<Int>) -> Unit)? = null
) {
    Box(
        modifier = modifier
            .background(contentColor)
            .padding(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val density = LocalDensity.current
            val layoutDirection = LocalLayoutDirection.current
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .aspectRatio(1f)
                .drawWithCache {

                    val outline = cropAspectRatio.shape.createOutline(
                        size = size, layoutDirection = layoutDirection, density = density
                    )

                    val width = size.width
                    val height = size.height
                    val outlineWidth = outline.bounds.width
                    val outlineHeight = outline.bounds.height

                    onDrawWithContent {

                        translate(
                            left = (width - outlineWidth) / 2, top = (height - outlineHeight) / 2
                        ) {
                            drawOutline(
                                outline = outline, color = color, style = Stroke(3.dp.toPx())
                            )
                        }
                        drawContent()
                    }
                },
                contentAlignment = Alignment.Center
            ) {
                GridImageLayout(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(5.dp),
                    thumbnails = cropAspectRatio.icons,
                    onClick = onClick
                )
            }
            if (cropAspectRatio.title.isNotEmpty()) {
                Text(text = cropAspectRatio.title, color = color, fontSize = 14.sp)
            }
        }
    }
}
