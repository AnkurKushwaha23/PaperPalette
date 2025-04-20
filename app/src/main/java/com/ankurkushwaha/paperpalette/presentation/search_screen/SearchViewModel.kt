package com.ankurkushwaha.paperpalette.presentation.search_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ankurkushwaha.paperpalette.domain.model.UnsplashImage
import com.ankurkushwaha.paperpalette.domain.repository.ImageRepository
import com.ankurkushwaha.paperpalette.presentation.util.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _snackbarEvent = Channel<SnackbarEvent>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    private val _searchImages = MutableStateFlow<PagingData<UnsplashImage>>(PagingData.empty())
    val searchImages = _searchImages

    fun searchImage(query: String) {
        viewModelScope.launch {
            try {
                imageRepository.searchImages(query)
                    .cachedIn(viewModelScope)
                    .collect {
                        _searchImages.value = it
                    }
            } catch (e: Exception) {
                _snackbarEvent.send(
                    SnackbarEvent(message = "Something went wrong. ${e.message}")
                )
            }
        }
    }

    val favoriteImageIds : StateFlow<List<String>> = imageRepository.getFavoriteImagesIds()
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

    fun toggleFavoriteStatus(image: UnsplashImage){
        viewModelScope.launch {
            try {
                imageRepository.toggleFavoriteStatus(image)
            }catch (e:Exception){
                _snackbarEvent.send(
                    SnackbarEvent(message = "Something went wrong. ${e.message}")
                )
            }
        }
    }

}