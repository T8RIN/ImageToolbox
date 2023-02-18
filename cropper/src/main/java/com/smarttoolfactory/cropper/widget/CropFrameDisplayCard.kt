package com.smarttoolfactory.cropper.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.cropper.model.CropOutline
import com.smarttoolfactory.cropper.model.CropPath
import com.smarttoolfactory.cropper.model.CropShape
import com.smarttoolfactory.cropper.util.scale

@Composable
fun CropFrameDisplayCard(
    modifier: Modifier = Modifier,
    editable: Boolean,
    scale: Float,
    contentColor: Color = MaterialTheme.colorScheme.surface,
    outlineColor: Color,
    editButtonBackgroundColor: Color = MaterialTheme.colorScheme.tertiary,
    editButtonContentColor: Color = MaterialTheme.colorScheme.onTertiary,
    fontSize: TextUnit = 12.sp,
    title: String,
    cropOutline: CropOutline,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .background(contentColor)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CropFrameDisplay(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .aspectRatio(1f),
                cropOutline = cropOutline,
                color = outlineColor
            ) {

                if (editable) {
                    Icon(
                        modifier = Modifier
                            .graphicsLayer {
                                val iconScale =
                                    scale(
                                        start1 = .9f,
                                        end1 = 1f,
                                        pos = scale.coerceAtLeast(.9f),
                                        start2 = 0f,
                                        end2 = 1f
                                    )
                                scaleX = iconScale
                                scaleY = iconScale

                                val translation = this.density.run { 12.dp.toPx() }
                                translationX = translation
                                translationY = -translation
                                clip = true
                                shape = CircleShape

                                this.density
                            }
                            .clickable {
                                onClick()
                            }
                            .padding(8.dp)
                            .background(editButtonBackgroundColor, CircleShape)
                            .size(20.dp)
                            .padding(4.dp),
                        imageVector = Icons.Default.Edit,
                        tint = editButtonContentColor,
                        contentDescription = "Edit"
                    )
                }
            }


            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    color = outlineColor,
                    fontSize = fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun CropFrameDisplay(
    modifier: Modifier,
    cropOutline: CropOutline,
    color: Color,
    content: @Composable () -> Unit
) {

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    when (cropOutline) {

        is CropShape -> {
            val shape = remember { cropOutline.shape }

            Box(
                modifier.drawWithCache {

                    val outline = shape.createOutline(
                        size = size,
                        layoutDirection = layoutDirection,
                        density = density
                    )

                    onDrawWithContent {
                        val width = size.width
                        val height = size.height
                        val outlineWidth = outline.bounds.width
                        val outlineHeight = outline.bounds.height

                        translate(
                            left = (width - outlineWidth) / 2,
                            top = (height - outlineHeight) / 2
                        ) {
                            drawOutline(
                                outline = outline,
                                color = color,
                                style = Stroke(4.dp.toPx())
                            )
                        }
                        drawContent()
                    }
                },
                contentAlignment = Alignment.TopEnd
            ) {
                content()
            }
        }
        is CropPath -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.TopEnd
            ) {
                Icon(
                    modifier = Modifier.matchParentSize(),
                    imageVector = Icons.Outlined.FavoriteBorder,
                    tint = color,
                    contentDescription = "Crop with Path"
                )

                content()
            }
        }
        else -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.TopEnd
            ) {
                Icon(
                    modifier = Modifier.matchParentSize(),
                    imageVector = Icons.Outlined.Image,
                    tint = color,
                    contentDescription = "Crop with Image Mask"
                )

                content()
            }
        }
    }
}
