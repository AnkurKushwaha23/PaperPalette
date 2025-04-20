package com.ankurkushwaha.paperpalette.presentation.favorites_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ankurkushwaha.paperpalette.domain.model.UnsplashImage
import com.ankurkushwaha.paperpalette.domain.repository.ImageRepository
import com.ankurkushwaha.paperpalette.presentation.util.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {
    private val _snackbarEvent = Channel<SnackbarEvent>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    val favoriteImageIds: StateFlow<List<String>> = imageRepository.getFavoriteImagesIds()
        .catch {
            _snackbarEvent.send(
                SnackbarEvent(message = "Something went wrong. ${it.message}")
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteImages: StateFlow<PagingData<UnsplashImage>> =
        imageRepository.getAllFavoriteImages()
            .catch {
                _snackbarEvent.send(
                    SnackbarEvent(message = "Something went wrong. ${it.message}")
                )
            }
            .cachedIn(viewModelScope)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = PagingData.empty()
            )

    fun toggleFavoriteStatus(image: UnsplashImage) {
        viewModelScope.launch {
            try {
                imageRepository.toggleFavoriteStatus(image)
            } catch (e: Exception) {
                _snackbarEvent.send(
                    SnackbarEvent(message = "Something went wrong. ${e.message}")
                )
            }
        }
    }
}