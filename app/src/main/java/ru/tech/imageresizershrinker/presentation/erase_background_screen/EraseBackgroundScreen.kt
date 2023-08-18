package ru.tech.imageresizershrinker.presentation.erase_background_screen

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.gesture.MotionEvent
import com.smarttoolfactory.gesture.pointerMotionEvents
import com.t8rin.drawbox.presentation.model.DrawPath
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.PathPaint
import ru.tech.imageresizershrinker.presentation.erase_background_screen.viewModel.EraseBackgroundViewModel
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.transformation.filter.SaturationFilter
import ru.tech.imageresizershrinker.presentation.root.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.root.utils.helper.Picker
import ru.tech.imageresizershrinker.presentation.root.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.presentation.root.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.navBarsLandscapePadding
import ru.tech.imageresizershrinker.presentation.root.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.presentation.root.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.root.widget.other.showError
import ru.tech.imageresizershrinker.presentation.root.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EraseBackgroundScreen(
    uriState: Uri?,
    onGoBack: () -> Unit,
    viewModel: EraseBackgroundViewModel = hiltViewModel()
) {
    val settingsState = LocalSettingsState.current
    val toastHostState = LocalToastHost.current
    val context = LocalContext.current as ComponentActivity
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val scope = rememberCoroutineScope()
    val confettiController = LocalConfettiController.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiController.showEmpty()
        }
    }

    LaunchedEffect(uriState) {
        uriState?.let {
            viewModel.setUri(it)
            viewModel.decodeBitmapByUri(
                uri = it,
                onGetMimeType = viewModel::setMime,
                onGetExif = {},
                onGetBitmap = viewModel::updateBitmap,
                onError = {
                    scope.launch {
                        toastHostState.showError(context, it)
                    }
                }
            )
        }
    }

    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let {
            if (allowChangeColor) {
                themeState.updateColorByImage(
                    SaturationFilter(context, 2f).transform(it, coil.size.Size.ORIGINAL)
                )
            }
        }
    }

    val focus = LocalFocusManager.current
    var showExitDialog by rememberSaveable { mutableStateOf(false) }


    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Single)
        ) { uris ->
            uris.takeIf { it.isNotEmpty() }?.firstOrNull()?.let {
                viewModel.setUri(it)
                viewModel.decodeBitmapByUri(
                    uri = it,
                    onGetMimeType = viewModel::setMime,
                    onGetExif = {},
                    onGetBitmap = viewModel::updateBitmap,
                    onError = {
                        scope.launch {
                            toastHostState.showError(context, it)
                        }
                    }
                )
            }
        }

    val pickImage = pickImageLauncher::pickImage

    val imageInside =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact


    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState
    )

    val onBack = {
        if (/*TODO: bitmapInfo.haveChanges(viewModel.bitmap)*/ false) showExitDialog = true
        else onGoBack()
    }

    val imageBlock = @Composable {
        viewModel.bitmap?.let { bitmap ->
            val imageBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true).asImageBitmap()

            val aspectRatio = imageBitmap.width / imageBitmap.height.toFloat()

            var strokeWidth by rememberSaveable { mutableStateOf(10f) }
            BitmapEraser(
                imageBitmap = imageBitmap,
                erasePaths = viewModel.erasePaths,
                strokeWidth = strokeWidth,
                onAddErasePath = viewModel::addErasePath,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio)
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .block()
                    .animateContentSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.line_width),
                        modifier = Modifier
                            .padding(
                                top = 16.dp,
                                end = 16.dp,
                                start = 16.dp
                            )
                            .weight(1f)
                    )
                    Text(
                        text = "$strokeWidth",
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.5f
                        ),
                        modifier = Modifier.padding(top = 16.dp),
                        lineHeight = 18.sp
                    )
                    Text(
                        maxLines = 1,
                        text = "Px",
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.5f
                        ),
                        modifier = Modifier.padding(
                            start = 4.dp,
                            top = 16.dp,
                            end = 16.dp
                        )
                    )
                }
                Slider(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                        .offset(y = (-2).dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = CircleShape
                        )
                        .height(40.dp)
                        .border(
                            width = settingsState.borderWidth,
                            color = MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer),
                            shape = CircleShape
                        )
                        .padding(horizontal = 10.dp),
                    colors = SliderDefaults.colors(
                        inactiveTrackColor =
                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                    ),
                    value = strokeWidth,
                    valueRange = 1f..100f,
                    onValueChange = {
                        strokeWidth = it
                    }
                )
            }
        }
    }

    val buttons = @Composable {
        Text(text = "BUTTONS HERE")
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focus.clearFocus()
                    }
                )
            }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Column(Modifier.fillMaxSize()) {
                LargeTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.drawHorizontalStroke(),
                    title = {
                        TopAppBarTitle(
                            title = stringResource(R.string.single_edit),
                            bitmap = viewModel.bitmap,
                            isLoading = viewModel.isImageLoading,
                            size = viewModel.sizeInBytes
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = onBack
                        ) {
                            Icon(Icons.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        if (viewModel.bitmap == null) {
                            TopAppBarEmoji()
                        }
//                        compareButton()
//                        zoomButton()
//                        if (!imageInside && viewModel.bitmap != null) actions()
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (!imageInside && viewModel.bitmap != null) {
                        Box(
                            Modifier
                                .weight(1.2f)
                                .padding(20.dp)
                        ) {
                            Box(Modifier.align(Alignment.Center)) {
                                imageBlock()
                            }
                        }
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant())
                        )
                    }
                    LazyColumn(
                        contentPadding = PaddingValues(
                            bottom = WindowInsets
                                .navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding() + WindowInsets.ime
                                .asPaddingValues()
                                .calculateBottomPadding() + (if (!imageInside && viewModel.bitmap != null) 20.dp else 100.dp),
                            top = if (viewModel.bitmap == null || !imageInside) 20.dp else 0.dp,
                            start = 20.dp,
                            end = 20.dp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clipToBounds()
                    ) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .navBarsLandscapePadding(viewModel.bitmap == null),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (viewModel.bitmap == null) ImageNotPickedWidget(onPickImage = pickImage)
                                else imageBlock()
                            }
                        }
                    }
                    if (!imageInside && viewModel.bitmap != null) {
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant())
                                .padding(start = 20.dp)
                        )
                        buttons()
                    }
                }
            }

            if (imageInside || viewModel.bitmap == null) {
                Box(
                    modifier = Modifier.align(settingsState.fabAlignment)
                ) {
                    buttons()
                }
            }


            ExitWithoutSavingDialog(
                onExit = onGoBack,
                onDismiss = { showExitDialog = false },
                visible = showExitDialog
            )

            if (viewModel.isSaving) LoadingDialog()

            BackHandler(onBack = onBack)

        }
    }
}


@Composable
fun BitmapEraser(
    imageBitmap: ImageBitmap,
    erasePaths: List<PathPaint>,
    onAddErasePath: (PathPaint) -> Unit,
    strokeWidth: Float,
    modifier: Modifier,
    onErased: (Bitmap) -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    BoxWithConstraints(modifier) {

        var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
        // This is our motion event we get from touch motion
        var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
        // This is previous motion event before next touch is saved into this current position
        var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

        val imageWidth = constraints.maxWidth
        val imageHeight = constraints.maxHeight


        val drawImageBitmap = remember {
            Bitmap.createScaledBitmap(imageBitmap.asAndroidBitmap(), imageWidth, imageHeight, false)
                .asImageBitmap()
        }

        val erasedBitmap: ImageBitmap = remember {
            Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888).asImageBitmap()
        }

        LaunchedEffect(currentPosition) {
            onErased(erasedBitmap.asAndroidBitmap())
        }

        val canvas: Canvas = remember {
            Canvas(erasedBitmap)
        }


        val paint = remember {
            Paint().apply {
                isAntiAlias = true
            }
        }

        val erasePaint = remember(strokeWidth) {
            Paint().apply {
                blendMode = BlendMode.Clear
                style = PaintingStyle.Stroke
                strokeCap = StrokeCap.Round
                this.strokeWidth = strokeWidth
                strokeJoin = StrokeJoin.Round
                isAntiAlias = true
            }
        }

        var erasePath by remember { mutableStateOf(Path()) }

        canvas.apply {
            val nativeCanvas = this.nativeCanvas
            val canvasWidth = nativeCanvas.width.toFloat()
            val canvasHeight = nativeCanvas.height.toFloat()


            when (motionEvent) {

                MotionEvent.Down -> {
                    previousPosition = currentPosition
                    erasePath.moveTo(currentPosition.x, currentPosition.y)
                }

                MotionEvent.Move -> {
                    previousPosition = currentPosition
                    erasePath.quadraticBezierTo(
                        previousPosition.x,
                        previousPosition.y,
                        (previousPosition.x + currentPosition.x) / 2,
                        (previousPosition.y + currentPosition.y) / 2
                    )
                }

                MotionEvent.Up -> {
                    erasePath.lineTo(currentPosition.x, currentPosition.y)
                    currentPosition = Offset.Unspecified
                    previousPosition = currentPosition
                    motionEvent = MotionEvent.Idle
                    onAddErasePath(
                        PathPaint(
                            erasePath, erasePaint
                        )
                    )
                    scope.launch {
                        kotlinx.coroutines.delay(100L)
                        erasePath = Path()
                    }
                }

                else -> Unit
            }

            with(canvas.nativeCanvas) {
                drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)


                drawImageRect(
                    image = drawImageBitmap,
                    dstSize = IntSize(canvasWidth.toInt(), canvasHeight.toInt()),
                    paint = paint
                )

                erasePaths.forEach { (path, paint) ->
                    this.drawPath(path.asAndroidPath(), paint.asFrameworkPaint())
                }
                this.drawPath(erasePath.asAndroidPath(), erasePaint.asFrameworkPaint())
            }
        }

        val canvasModifier = Modifier.pointerMotionEvents(
            onDown = { pointerInputChange ->
                motionEvent = MotionEvent.Down
                currentPosition = pointerInputChange.position
                pointerInputChange.consume()
            },
            onMove = { pointerInputChange ->
                motionEvent = MotionEvent.Move
                currentPosition = pointerInputChange.position
                pointerInputChange.consume()
            },
            onUp = { pointerInputChange ->
                motionEvent = MotionEvent.Up
                pointerInputChange.consume()
            },
            delayAfterDownInMillis = 20
        )

        Image(
            modifier = canvasModifier
                .clipToBounds()
                .clip(MaterialTheme.shapes.small)
                .drawBehind {
                    val width = this.size.width
                    val height = this.size.height

                    val checkerWidth = 10.dp.toPx()
                    val checkerHeight = 10.dp.toPx()

                    val horizontalSteps = (width / checkerWidth).toInt()
                    val verticalSteps = (height / checkerHeight).toInt()

                    for (y in 0..verticalSteps) {
                        for (x in 0..horizontalSteps) {
                            val isGrayTile = ((x + y) % 2 == 1)
                            drawRect(
                                color = if (isGrayTile) Color.LightGray else Color.White,
                                topLeft = Offset(x * checkerWidth, y * checkerHeight),
                                size = Size(checkerWidth, checkerHeight)
                            )
                        }
                    }
                }
                .matchParentSize()
                .block(MaterialTheme.shapes.small, Color.Transparent, false),
            bitmap = erasedBitmap,
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
    }

}