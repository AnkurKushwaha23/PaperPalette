package com.ankurkushwaha.paperpalette.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.paging.compose.collectAsLazyPagingItems
import com.ankurkushwaha.paperpalette.presentation.favorites_screen.FavoriteViewModel
import com.ankurkushwaha.paperpalette.presentation.favorites_screen.FavoritesScreen
import com.ankurkushwaha.paperpalette.presentation.full_image_screen.FullImageScreen
import com.ankurkushwaha.paperpalette.presentation.full_image_screen.FullImageViewModel
import com.ankurkushwaha.paperpalette.presentation.home_screen.HomeScreen
import com.ankurkushwaha.paperpalette.presentation.home_screen.HomeViewModel
import com.ankurkushwaha.paperpalette.presentation.profile_screen.ProfileScreen
import com.ankurkushwaha.paperpalette.presentation.search_screen.SearchScreen
import com.ankurkushwaha.paperpalette.presentation.search_screen.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraphSetup(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    snackbarHostState: SnackbarHostState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
) {
    NavHost(navController = navController, startDestination = Routes.HomeScreen) {
        composable<Routes.HomeScreen> {
            val viewModel: HomeViewModel = hiltViewModel()
            val images = viewModel.images.collectAsLazyPagingItems()
            val favImageIds by viewModel.favoriteImageIds.collectAsStateWithLifecycle()
            HomeScreen(
                snackbarHostState = snackbarHostState,
                snackbarEvent = viewModel.snackbarEvent,
                images = images,
                favoriteImageIds = favImageIds,
                onToggleFavorite = { viewModel.toggleFavoriteStatus(it) },
                scrollBehavior = scrollBehavior,
                onImageClick = { imageId ->
                    navController.navigate(Routes.FullImageScreen(imageId))
                },
                onSearchClick = {
                    navController.navigate(Routes.SearchScreen)
                },
                onFABClick = { navController.navigate((Routes.FavoritesScreen)) }
            )
        }

        composable<Routes.SearchScreen> {
            val viewModel: SearchViewModel = hiltViewModel()
            val searchImages = viewModel.searchImages.collectAsLazyPagingItems()
            val favImageIds by viewModel.favoriteImageIds.collectAsStateWithLifecycle()
            SearchScreen(
                snackbarHostState = snackbarHostState,
                snackbarEvent = viewModel.snackbarEvent,
                onBackClick = { navController.navigateUp() },
                searchImages = searchImages,
                favoriteImageIds = favImageIds,
                onSearch = { query ->
                    viewModel.searchImage(query)
                },
                onImageClick = { imageId ->
                    navController.navigate(Routes.FullImageScreen(imageId))
                },
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onToggleFavorite = { viewModel.toggleFavoriteStatus(it) }
            )
        }

        composable<Routes.FavoritesScreen> {
            val viewModel: FavoriteViewModel = hiltViewModel()
            val favImages = viewModel.favoriteImages.collectAsLazyPagingItems()
            val favImageIds by viewModel.favoriteImageIds.collectAsStateWithLifecycle()
            FavoritesScreen(
                favoriteImageIds = favImageIds,
                favoriteImages = favImages,
                snackbarHostState = snackbarHostState,
                snackbarEvent = viewModel.snackbarEvent,
                scrollBehavior = scrollBehavior,
                onToggleFavorite = { viewModel.toggleFavoriteStatus(it) },
                onImageClick = { imageId ->
                    navController.navigate(Routes.FullImageScreen(imageId))
                },
                onSearchClick = {
                    navController.navigate(Routes.SearchScreen)
                },
                onBackClick = { navController.navigateUp() }
            )
        }

        composable<Routes.FullImageScreen> {
            val fullImageViewModel: FullImageViewModel = hiltViewModel()
            FullImageScreen(
                snackbarHostState = snackbarHostState,
                snackbarEvent = fullImageViewModel.snackbarEvent,
                image = fullImageViewModel.image,
                onBackClick = { navController.navigateUp() },
                onPhotographerNameClick = { profileLink ->
                    navController.navigate(Routes.ProfileScreen(profileLink))
                },
                onImageDownloadClick = { url, title ->
                    fullImageViewModel.downloadImage(url, title)
                }
            )
        }

        composable<Routes.ProfileScreen> { backstackEntry ->
            val profileId = backstackEntry.toRoute<Routes.ProfileScreen>().profileLink
            ProfileScreen(
                onBackClick = { navController.navigateUp() },
                profileLink = profileId
            )
        }
    }
}