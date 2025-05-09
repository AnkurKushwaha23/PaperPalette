package com.ankurkushwaha.paperpalette.presentation.home_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.ankurkushwaha.paperpalette.R
import com.ankurkushwaha.paperpalette.domain.model.UnsplashImage
import com.ankurkushwaha.paperpalette.presentation.components.ImagesVerticalGrid
import com.ankurkushwaha.paperpalette.presentation.components.PaperPaletteTopAppBar
import com.ankurkushwaha.paperpalette.presentation.components.ZoomedImageCard
import com.ankurkushwaha.paperpalette.presentation.util.SnackbarEvent
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    snackbarEvent: Flow<SnackbarEvent>,
    scrollBehavior: TopAppBarScrollBehavior,
    images:LazyPagingItems<UnsplashImage>,
    favoriteImageIds: List<String>,
    onImageClick: (String) -> Unit,
    onSearchClick: ()->Unit={},
    onFABClick:()->Unit,
    onToggleFavorite: (UnsplashImage) -> Unit
) {
    var showImagePreview by remember { mutableStateOf(false) }
    var activeImage by remember { mutableStateOf<UnsplashImage?>(null) }

    LaunchedEffect (key1 = true){
        snackbarEvent.collect{event->
            snackbarHostState.showSnackbar(
                message = event.message,
                duration = event.duration
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            PaperPaletteTopAppBar(
                scrollBehaviour = scrollBehavior,
                onSearchClick = {
                    onSearchClick()
                }
            )

            ImagesVerticalGrid(
                images = images,
                onImageClick = onImageClick,
                favoriteImageIds = favoriteImageIds,
                onDragStart = { image ->
                    activeImage = image
                    showImagePreview = true
                },
                onDragEnd = { showImagePreview = false },
                onToggleFavoriteStatus = { onToggleFavorite(it) }
            )
        }
        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            onClick = {
            onFABClick()
        }) {
            Icon(
                painter = painterResource(R.drawable.ic_save_24),
                contentDescription = "Favorites",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        ZoomedImageCard(
            modifier = Modifier.padding(20.dp),
            isVisible = showImagePreview,
            image = activeImage
        )
    }
}