package ru.tech.imageresizershrinker.presentation.crop_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.CropAspectRatio
import com.smarttoolfactory.cropper.util.createRectShape
import ru.tech.imageresizershrinker.R

@Composable
fun aspectRatios(
    original: String = stringResource(R.string.original)
) = remember(original) {
    listOf(
        CropAspectRatio(
            title = original,
            shape = createRectShape(AspectRatio.Original),
            aspectRatio = AspectRatio.Original
        ),
        CropAspectRatio(
            title = "1:1",
            shape = createRectShape(AspectRatio(1 / 1f)),
            aspectRatio = AspectRatio(1 / 1f)
        ),
        CropAspectRatio(
            title = "10:16",
            shape = createRectShape(AspectRatio(10 / 16f)),
            aspectRatio = AspectRatio(10 / 16f)
        ),
        CropAspectRatio(
            title = "9:16",
            shape = createRectShape(AspectRatio(9 / 16f)),
            aspectRatio = AspectRatio(9 / 16f)
        ),
        CropAspectRatio(
            title = "9:18.5",
            shape = createRectShape(AspectRatio(9f / 18.5f)),
            aspectRatio = AspectRatio(9f / 18.5f)
        ),
        CropAspectRatio(
            title = "9:21",
            shape = createRectShape(AspectRatio(9 / 21f)),
            aspectRatio = AspectRatio(9 / 21f)
        ),
        CropAspectRatio(
            title = "1:1.91",
            shape = createRectShape(AspectRatio(1 / 1.91f)),
            aspectRatio = AspectRatio(1f / 1.91f)
        ),
        CropAspectRatio(
            title = "2:3",
            shape = createRectShape(AspectRatio(2 / 3f)),
            aspectRatio = AspectRatio(2 / 3f)
        ),
        CropAspectRatio(
            title = "1:2",
            shape = createRectShape(AspectRatio(1 / 2f)),
            aspectRatio = AspectRatio(1 / 2f)
        ),
        CropAspectRatio(
            title = "5:3",
            shape = createRectShape(AspectRatio(5 / 3f)),
            aspectRatio = AspectRatio(5 / 3f)
        ),
        CropAspectRatio(
            title = "4:3",
            shape = createRectShape(AspectRatio(4 / 3f)),
            aspectRatio = AspectRatio(4 / 3f)
        ),
        CropAspectRatio(
            title = "21:9",
            shape = createRectShape(AspectRatio(21 / 9f)),
            aspectRatio = AspectRatio(21 / 9f)
        ),
        CropAspectRatio(
            title = "18.5:9",
            shape = createRectShape(AspectRatio(18.5f / 9f)),
            aspectRatio = AspectRatio(18.5f / 9f)
        ),
        CropAspectRatio(
            title = "16:9",
            shape = createRectShape(AspectRatio(16 / 9f)),
            aspectRatio = AspectRatio(16 / 9f)
        ),
        CropAspectRatio(
            title = "16:10",
            shape = createRectShape(AspectRatio(16 / 10f)),
            aspectRatio = AspectRatio(16 / 10f)
        ),
        CropAspectRatio(
            title = "1.91:1",
            shape = createRectShape(AspectRatio(1.91f / 1f)),
            aspectRatio = AspectRatio(1.91f / 1f)
        ),
        CropAspectRatio(
            title = "3:2",
            shape = createRectShape(AspectRatio(3 / 2f)),
            aspectRatio = AspectRatio(3 / 2f)
        ),
        CropAspectRatio(
            title = "3:4",
            shape = createRectShape(AspectRatio(3 / 4f)),
            aspectRatio = AspectRatio(3 / 4f)
        ),
        CropAspectRatio(
            title = "3:5",
            shape = createRectShape(AspectRatio(3 / 5f)),
            aspectRatio = AspectRatio(3 / 5f)
        ),
        CropAspectRatio(
            title = "2:1",
            shape = createRectShape(AspectRatio(2 / 1f)),
            aspectRatio = AspectRatio(2 / 1f)
        ),
    )
}