package com.ankurkushwaha.paperpalette.presentation.favorites_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.ankurkushwaha.paperpalette.R
import com.ankurkushwaha.paperpalette.domain.model.UnsplashImage
import com.ankurkushwaha.paperpalette.presentation.components.ImagesVerticalGrid
import com.ankurkushwaha.paperpalette.presentation.components.PaperPaletteTopAppBar
import com.ankurkushwaha.paperpalette.presentation.components.ZoomedImageCard
import com.ankurkushwaha.paperpalette.presentation.search_screen.SearchScreen
import com.ankurkushwaha.paperpalette.presentation.util.SnackbarEvent
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    snackbarHostState: SnackbarHostState,
    favoriteImages: LazyPagingItems<UnsplashImage>,
    snackbarEvent: Flow<SnackbarEvent>,
    favoriteImageIds: List<String>,
    onImageClick: (String) -> Unit,
    onBackClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    onSearchClick: () -> Unit = {},
    onToggleFavorite: (UnsplashImage) -> Unit
) {

    var showImagePreview by remember { mutableStateOf(false) }

    var activeImage by remember { mutableStateOf<UnsplashImage?>(null) }

    LaunchedEffect(key1 = true) {
        snackbarEvent.collect { event ->
            snackbarHostState.showSnackbar(
                message = event.message,
                duration = event.duration
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PaperPaletteTopAppBar(
                title = "Favorite Images",
                navigationIcon = {
                    IconButton(
                        onClick = { onBackClick() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "GoBack"
                        )
                    }
                },
                scrollBehaviour = scrollBehavior,
                onSearchClick = {
                    onSearchClick()
                }
            )

            ImagesVerticalGrid(
                images = favoriteImages,
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

        ZoomedImageCard(
            modifier = Modifier.padding(20.dp),
            isVisible = showImagePreview,
            image = activeImage
        )

        if (favoriteImages.itemCount == 0){
            EmptyState(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(R.drawable.undraw_empty),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "No Saved Images",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style =MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Images you save will be stored here",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style =MaterialTheme.typography.bodyMedium
        )
    }
}