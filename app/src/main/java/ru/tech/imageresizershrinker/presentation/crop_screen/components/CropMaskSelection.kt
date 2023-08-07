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
import androidx.compose.ui.graphics.Path
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
                id = 6,
                title = "Octagon",
                polygonProperties = PolygonProperties(sides = 8, 0f)
            )
        ),
        CropOutlineProperty(
            OutlineType.Custom,
            object : CropShape {
                override val shape: Shape
                    get() = PentagonShape
                override val id: Int
                    get() = 4
                override val title: String
                    get() = "Pentagon"

            }
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
            object : CropShape {
                override val shape: Shape
                    get() = DavidStarShape
                override val id: Int
                    get() = 12
                override val title: String
                    get() = "DavidStar"

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

        val path = Path().apply {
            moveTo(12f, 100f)
            cubicTo(12f, 76f, 0f, 77.6142f, 0f, 50f)
            cubicTo(0f, 22.3858f, 22.3858f, 0f, 50f, 0f)
            cubicTo(77.6142f, 0f, 76f, 12f, 100f, 12f)
            cubicTo(124f, 12f, 122.3858f, 0f, 150f, 0f)
            cubicTo(177.6142f, 0f, 200f, 22.3858f, 200f, 50f)
            cubicTo(200f, 77.6142f, 188f, 76f, 188f, 100f)
            cubicTo(188f, 124f, 200f, 122.3858f, 200f, 150f)
            cubicTo(200f, 177.6142f, 177.6142f, 200f, 150f, 200f)
            cubicTo(122.3858f, 200f, 124f, 188f, 100f, 188f)
            cubicTo(76f, 188f, 77.6142f, 200f, 50f, 200f)
            cubicTo(22.3858f, 200f, 0f, 177.6142f, 0f, 150f)
            cubicTo(0f, 122.3858f, 12f, 124f, 12f, 100f)
            close()
        }

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


val DavidStarShape: Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val baseWidth = 865.0807f
        val baseHeight = 865.0807f

        val path = Path().apply {
            moveTo(403.3913f, 8.7356f)
            cubicTo(421.0787f, -2.9119f, 444.002f, -2.9119f, 461.6894f, 8.7356f)
            lineTo(518.743f, 46.3066f)
            cubicTo(528.2839f, 52.5895f, 539.5995f, 55.6215f, 551.0036f, 54.9508f)
            lineTo(619.1989f, 50.9402f)
            cubicTo(640.3404f, 49.6968f, 660.1926f, 61.1585f, 669.6865f, 80.0892f)
            lineTo(700.3109f, 141.1534f)
            cubicTo(705.4321f, 151.365f, 713.7157f, 159.6486f, 723.9273f, 164.7699f)
            lineTo(784.9915f, 195.3942f)
            cubicTo(803.9222f, 204.8881f, 815.3839f, 224.7403f, 814.1406f, 245.8818f)
            lineTo(810.1299f, 314.0771f)
            cubicTo(809.4593f, 325.4812f, 812.4913f, 336.7969f, 818.7742f, 346.3378f)
            lineTo(856.3451f, 403.3913f)
            cubicTo(867.9926f, 421.0787f, 867.9927f, 444.002f, 856.3452f, 461.6894f)
            lineTo(818.7742f, 518.743f)
            cubicTo(812.4913f, 528.2839f, 809.4593f, 539.5995f, 810.1299f, 551.0036f)
            lineTo(814.1406f, 619.1989f)
            cubicTo(815.3839f, 640.3404f, 803.9223f, 660.1926f, 784.9916f, 669.6865f)
            lineTo(723.9274f, 700.3109f)
            cubicTo(713.7158f, 705.4321f, 705.4321f, 713.7157f, 700.3109f, 723.9273f)
            lineTo(669.6866f, 784.9915f)
            cubicTo(660.1926f, 803.9222f, 640.3404f, 815.3839f, 619.1989f, 814.1406f)
            lineTo(551.0036f, 810.1299f)
            cubicTo(539.5995f, 809.4593f, 528.2839f, 812.4913f, 518.743f, 818.7742f)
            lineTo(461.6894f, 856.3451f)
            cubicTo(444.0021f, 867.9926f, 421.0787f, 867.9927f, 403.3914f, 856.3452f)
            lineTo(346.3378f, 818.7742f)
            cubicTo(336.7969f, 812.4913f, 325.4812f, 809.4593f, 314.0771f, 810.1299f)
            lineTo(245.8818f, 814.1406f)
            cubicTo(224.7404f, 815.3839f, 204.8882f, 803.9223f, 195.3942f, 784.9916f)
            lineTo(164.7699f, 723.9274f)
            cubicTo(159.6486f, 713.7158f, 151.365f, 705.4321f, 141.1534f, 700.3109f)
            lineTo(80.0892f, 669.6866f)
            cubicTo(61.1585f, 660.1926f, 49.6968f, 640.3404f, 50.9402f, 619.199f)
            lineTo(54.9508f, 551.0036f)
            cubicTo(55.6215f, 539.5995f, 52.5895f, 528.2839f, 46.3066f, 518.743f)
            lineTo(8.7356f, 461.6894f)
            cubicTo(-2.9119f, 444.0021f, -2.9119f, 421.0787f, 8.7356f, 403.3914f)
            lineTo(46.3066f, 346.3378f)
            cubicTo(52.5895f, 336.7969f, 55.6215f, 325.4813f, 54.9508f, 314.0771f)
            lineTo(50.9402f, 245.8818f)
            cubicTo(49.6968f, 224.7404f, 61.1585f, 204.8882f, 80.0892f, 195.3942f)
            lineTo(141.1534f, 164.7699f)
            cubicTo(151.365f, 159.6486f, 159.6486f, 151.365f, 164.7699f, 141.1534f)
            lineTo(195.3942f, 80.0892f)
            cubicTo(204.8882f, 61.1585f, 224.7403f, 49.6968f, 245.8818f, 50.9402f)
            lineTo(314.0771f, 54.9508f)
            cubicTo(325.4813f, 55.6215f, 336.7969f, 52.5895f, 346.3378f, 46.3066f)
            lineTo(403.3913f, 8.7356f)
            close()
        }

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


val PentagonShape: Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val baseWidth = 1224.1858f
        val baseHeight = 1137.3882f

        val path = Path().apply {
            moveTo(521.133f, 28.588f)
            cubicTo(575.7829f, -9.5293f, 648.4028f, -9.5293f, 703.0528f, 28.588f)
            lineTo(1156.1332f, 344.6029f)
            cubicTo(1214.148f, 385.0671f, 1238.4571f, 458.9902f, 1215.7808f, 525.9892f)
            lineTo(1045.41f, 1029.3625f)
            cubicTo(1023.5555f, 1093.9334f, 962.9716f, 1137.3882f, 894.8026f, 1137.3882f)
            lineTo(329.3832f, 1137.3882f)
            cubicTo(261.2142f, 1137.3882f, 200.6302f, 1093.9334f, 178.7757f, 1029.3625f)
            lineTo(8.405f, 525.9893f)
            cubicTo(-14.2714f, 458.9903f, 10.0377f, 385.0671f, 68.0526f, 344.6029f)
            lineTo(521.133f, 28.588f)
            close()
        }

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