package ru.tech.imageresizershrinker.presentation.crop_screen.components

import android.graphics.Matrix
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.cropper.model.CornerRadiusProperties
import com.smarttoolfactory.cropper.model.CropShape
import com.smarttoolfactory.cropper.model.CustomPathOutline
import com.smarttoolfactory.cropper.model.CutCornerCropShape
import com.smarttoolfactory.cropper.model.ImageMaskOutline
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.PolygonCropShape
import com.smarttoolfactory.cropper.model.PolygonProperties
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.model.RoundedCornerCropShape
import com.smarttoolfactory.cropper.settings.CropOutlineProperty
import com.smarttoolfactory.cropper.settings.Paths
import com.smarttoolfactory.cropper.util.createPolygonShape
import com.smarttoolfactory.cropper.widget.CropFrameDisplayCard
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.helper.Picker
import ru.tech.imageresizershrinker.presentation.root.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.presentation.root.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import kotlin.math.roundToInt

@Composable
fun CropMaskSelection(
    modifier: Modifier = Modifier,
    selectedItem: CropOutlineProperty,
    loadImage: suspend (Uri) -> ImageBitmap?,
    onCropMaskChange: (CropOutlineProperty) -> Unit
) {
    val settingsState = LocalSettingsState.current

    var cornerRadius by rememberSaveable { mutableIntStateOf(20) }

    val outlineProperties = outlineProperties()

    val scope = rememberCoroutineScope()

    val maskLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Single)
        ) { uris ->
            uris.takeIf { it.isNotEmpty() }?.firstOrNull()?.let {
                scope.launch {
                    loadImage(it)?.let {
                        onCropMaskChange(
                            outlineProperties.last().run {
                                copy(
                                    cropOutline = (cropOutline as ImageMaskOutline).copy(image = it)
                                )
                            }
                        )
                    }
                }
            }
        }


    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(id = R.string.crop_mask),
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 16.dp),
            fontWeight = FontWeight.Medium
        )
        LazyRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 4.dp,
                bottom = 4.dp,
                end = 16.dp + WindowInsets
                    .navigationBars
                    .asPaddingValues()
                    .calculateEndPadding(LocalLayoutDirection.current)
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(outlineProperties) { _, item ->
                CropFrameDisplayCard(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .height(100.dp)
                        .block(
                            applyEndPadding = false,
                            color = animateColorAsState(
                                targetValue = if (selectedItem.cropOutline.id == item.cropOutline.id) {
                                    MaterialTheme.colorScheme.surfaceColorAtElevation(20.dp)
                                } else MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                            ).value
                        )
                        .clickable {
                            if (item.cropOutline is ImageMaskOutline) {
                                maskLauncher.pickImage()
                            } else {
                                onCropMaskChange(item)
                            }
                            cornerRadius = 20
                        }
                        .padding(16.dp),
                    editable = false,
                    scale = 1f,
                    outlineColor = MaterialTheme.colorScheme.secondary,
                    title = "",
                    cropOutline = item.cropOutline
                )
            }
        }

        AnimatedVisibility(selectedItem.cropOutline.id == 1 || selectedItem.cropOutline.id == 2) {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .block(shape = RoundedCornerShape(24.dp)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.radius),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${cornerRadius}%",
                        color = LocalContentColor.current.copy(alpha = 0.7f)
                    )
                }
                Slider(
                    modifier = Modifier
                        .padding(horizontal = 3.dp, vertical = 3.dp)
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                            CircleShape
                        )
                        .border(
                            settingsState.borderWidth,
                            MaterialTheme.colorScheme.outlineVariant(
                                onTopOf = MaterialTheme.colorScheme.secondaryContainer.copy(
                                    alpha = 0.4f
                                )
                            ),
                            CircleShape
                        )
                        .padding(horizontal = 12.dp),
                    colors = SliderDefaults.colors(
                        inactiveTrackColor =
                        MaterialTheme.colorScheme.outlineVariant(
                            onTopOf = MaterialTheme.colorScheme.secondaryContainer.copy(
                                alpha = 0.4f
                            )
                        )
                    ),
                    value = animateIntAsState(cornerRadius).value.toFloat(),
                    onValueChange = {
                        cornerRadius = it.roundToInt()
                        if (selectedItem.cropOutline is CutCornerCropShape) {
                            onCropMaskChange(
                                selectedItem.copy(
                                    cropOutline = CutCornerCropShape(
                                        selectedItem.cropOutline.id,
                                        selectedItem.cropOutline.title,
                                        CornerRadiusProperties(
                                            topStartPercent = cornerRadius,
                                            topEndPercent = cornerRadius,
                                            bottomStartPercent = cornerRadius,
                                            bottomEndPercent = cornerRadius
                                        )
                                    )
                                )
                            )
                        } else if (selectedItem.cropOutline is RoundedCornerCropShape) {
                            onCropMaskChange(
                                selectedItem.copy(
                                    cropOutline = RoundedCornerCropShape(
                                        selectedItem.cropOutline.id,
                                        selectedItem.cropOutline.title,
                                        CornerRadiusProperties(
                                            topStartPercent = cornerRadius,
                                            topEndPercent = cornerRadius,
                                            bottomStartPercent = cornerRadius,
                                            bottomEndPercent = cornerRadius
                                        )
                                    )
                                )
                            )
                        }
                    },
                    valueRange = 0f..50f,
                    steps = 50
                )
            }
        }
        AnimatedVisibility(selectedItem.cropOutline.title == OutlineType.ImageMask.name) {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .block(shape = RoundedCornerShape(24.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.image_crop_mask_sub),
                    textAlign = TextAlign.Center,
                    color = LocalContentColor.current.copy(0.5f),
                    fontSize = 14.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun outlineProperties(): List<CropOutlineProperty> = remember {
    listOf(
        CropOutlineProperty(
            OutlineType.Rect,
            RectCropShape(
                id = 0,
                title = OutlineType.Rect.name
            )
        ),
        CropOutlineProperty(
            OutlineType.RoundedRect,
            RoundedCornerCropShape(
                id = 1,
                title = OutlineType.RoundedRect.name,
                cornerRadius = CornerRadiusProperties()
            )
        ),
        CropOutlineProperty(
            OutlineType.CutCorner,
            CutCornerCropShape(
                id = 2,
                title = OutlineType.CutCorner.name,
                cornerRadius = CornerRadiusProperties()
            )
        ),
        CropOutlineProperty(
            OutlineType.Polygon,
            PolygonCropShape(
                id = 3,
                title = "Polygon"
            )
        ),
        CropOutlineProperty(
            OutlineType.Polygon,
            PolygonCropShape(
                id = 4,
                title = "Pentagon",
                polygonProperties = PolygonProperties(sides = 5, 0f),
                shape = createPolygonShape(5, 0f)
            )
        ),
        CropOutlineProperty(
            OutlineType.Polygon,
            PolygonCropShape(
                id = 5,
                title = "Heptagon",
                polygonProperties = PolygonProperties(sides = 7, 0f),
                shape = createPolygonShape(7, 0f)
            )
        ),
        CropOutlineProperty(
            OutlineType.Polygon,
            PolygonCropShape(
                id = 6,
                title = "Octagon",
                polygonProperties = PolygonProperties(sides = 8, 0f),
                shape = createPolygonShape(8, 0f)
            )
        ),
        CropOutlineProperty(
            OutlineType.Custom,
            object : CropShape {
                override val shape: Shape
                    get() = CloverShape
                override val id: Int
                    get() = 7
                override val title: String
                    get() = "Clover"

            }
        ),
        CropOutlineProperty(
            OutlineType.Custom,
            CustomPathOutline(
                id = 8,
                title = "Heart",
                path = Paths.Favorite
            )
        ),
        CropOutlineProperty(
            OutlineType.Custom,
            CustomPathOutline(
                id = 9,
                title = "Star",
                path = Paths.Star
            )
        ),
        CropOutlineProperty(
            OutlineType.ImageMask,
            ImageMaskOutline(
                id = 10,
                title = OutlineType.ImageMask.name,
                image = ImageBitmap(1, 1)
            )
        )
    )
}

val CloverShape: Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val baseWidth = 200f
        val baseHeight = 200f

        val path = Paths.Clover

        return Outline.Generic(
            path
                .asAndroidPath()
                .apply {
                    transform(Matrix().apply {
                        setScale(size.width / baseWidth, size.height / baseHeight)
                    })
                }
                .asComposePath()
        )
    }
}