package com.ankurkushwaha.paperpalette.presentation.full_image_screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.animateZoomBy
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.ankurkushwaha.paperpalette.R
import com.ankurkushwaha.paperpalette.domain.model.UnsplashImage
import com.ankurkushwaha.paperpalette.presentation.components.DownloadOptionBottomSheet
import com.ankurkushwaha.paperpalette.presentation.components.FullImageViewTopBar
import com.ankurkushwaha.paperpalette.presentation.components.ImageDownloadOptions
import com.ankurkushwaha.paperpalette.presentation.components.PaperPaletteLoadingBar
import com.ankurkushwaha.paperpalette.presentation.util.SnackbarEvent
import com.ankurkushwaha.paperpalette.presentation.util.rememberWindowInsetsController
import com.ankurkushwaha.paperpalette.presentation.util.toggleStatusBar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.math.max

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FullImageScreen(
    snackbarHostState: SnackbarHostState,
    snackbarEvent: Flow<SnackbarEvent>,
    image: UnsplashImage?,
    onBackClick: () -> Unit,
    onPhotographerNameClick: (String) -> Unit,
    onImageDownloadClick:(String,String?)->Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showBars by rememberSaveable { mutableStateOf(false) }
    val windowInsetsController = rememberWindowInsetsController()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isDownloadBottomSheetOpen by remember { mutableStateOf(false) }

    LaunchedEffect (key1 = true){
        snackbarEvent.collect{event->
            snackbarHostState.showSnackbar(
                message = event.message,
                duration = event.duration
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        windowInsetsController.toggleStatusBar(show = showBars)
    }

    BackHandler (enabled = !showBars){
        windowInsetsController.toggleStatusBar(show = true)
        onBackClick()
    }

    DownloadOptionBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {isDownloadBottomSheetOpen = false},
        isOpen = isDownloadBottomSheetOpen,
        onOptionClick = {option->
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) isDownloadBottomSheetOpen = false
            }
            val url = when(option){
                ImageDownloadOptions.SMALL -> image?.imageUrlSmall
                ImageDownloadOptions.MEDIUM -> image?.imageUrlRegular
                ImageDownloadOptions.ORIGINAL -> image?.imageUrlRaw
            }
            url?.let{
                onImageDownloadClick(it,image?.description?.take(20))
                Toast.makeText(context,"Downloading...",Toast.LENGTH_SHORT).show()
            }
        }
    )

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            var scale by remember { mutableFloatStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }
            val isImageZoomed: Boolean by remember { derivedStateOf { scale != 1f } }
            val transformableState =
                rememberTransformableState { zoomChange, offSetChange, rotationChange ->
                    scale = max(scale * zoomChange, 1f)
                    val maxX = (constraints.maxWidth * (scale - 1)) / 2
                    val maxY = (constraints.maxHeight * (scale - 1)) / 2
                    offset = Offset(
                        x = (offset.x + offSetChange.x).coerceIn(-maxX, maxX),
                        y = (offset.y + offSetChange.y).coerceIn(-maxY, maxY)
                    )
                }
            var isLoading by remember { mutableStateOf(true) }
            var isError by remember { mutableStateOf(false) }

            val imageLoader = rememberAsyncImagePainter(
                model = image?.imageUrlRaw,
                onState = { imageState ->
                    isLoading = imageState is AsyncImagePainter.State.Loading
                    isError = imageState is AsyncImagePainter.State.Error
                }
            )

            if (isLoading) {
//            CircularProgressIndicator()
                PaperPaletteLoadingBar()
            }

            Image(
                painter = if (isError.not()) imageLoader else painterResource(R.drawable.ic_error_outline),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .transformable(transformableState)
                    .combinedClickable(
                        onDoubleClick = {
                            if (isImageZoomed) {
                                scale = 1f
                                offset = Offset.Zero
                            } else {
                                scope.launch {
                                    transformableState.animateZoomBy(zoomFactor = 3f)
                                }
                            }
                        },
                        onClick = {
                            showBars = !showBars
                            windowInsetsController.toggleStatusBar(show = showBars)
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
            )
        }
        FullImageViewTopBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 40.dp),
            image = image,
            isVisible = showBars,
            onBackClick = onBackClick,
            onPhotographerNameClick = onPhotographerNameClick,
            onDownloadImageClick = {isDownloadBottomSheetOpen = true}
        )
    }
}