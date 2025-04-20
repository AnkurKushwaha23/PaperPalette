package com.ankurkushwaha.paperpalette.presentation.home_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {
    private val _snackbarEvent = Channel<SnackbarEvent>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    val images: StateFlow<PagingData<UnsplashImage>> =
        imageRepository.getEditorialFeedImages()
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