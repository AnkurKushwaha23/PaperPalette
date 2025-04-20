package com.ankurkushwaha.paperpalette.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.ankurkushwaha.paperpalette.data.local.PaperPaletteDataBase
import com.ankurkushwaha.paperpalette.data.mapper.toDomainModel
import com.ankurkushwaha.paperpalette.data.mapper.toFavoriteImageEntity
import com.ankurkushwaha.paperpalette.data.paging.EditorialFeedRemoteMediator
import com.ankurkushwaha.paperpalette.data.paging.SearchPagingSource
import com.ankurkushwaha.paperpalette.data.remote.UnsplashApiService
import com.ankurkushwaha.paperpalette.domain.model.UnsplashImage
import com.ankurkushwaha.paperpalette.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
class ImageRepositoryImpl(
    private val apiService: UnsplashApiService,
    private val dataBase: PaperPaletteDataBase
) : ImageRepository {

    private val favoriteImageDao = dataBase.favouriteImageDao()
    private val editorialImageDao = dataBase.editorialFeedDao()

    override fun getEditorialFeedImages(): Flow<PagingData<UnsplashImage>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = EditorialFeedRemoteMediator(apiService, dataBase),
            pagingSourceFactory = { editorialImageDao.getAllEditorialFeedImages() }
        ).flow
            .map { pagingData ->
                pagingData.map { it.toDomainModel() }
            }
    }

    override fun searchImages(query: String): Flow<PagingData<UnsplashImage>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { SearchPagingSource(query, apiService) }
        ).flow
    }

    override suspend fun toggleFavoriteStatus(image: UnsplashImage) {
        val isFavourite = favoriteImageDao.isImageFavourite(image.id)
        val favImage = image.toFavoriteImageEntity()
        if (isFavourite) {
            favoriteImageDao.deleteFavouriteImage(favImage)
        } else {
            favoriteImageDao.insertFavouriteImage(favImage)
        }
    }

    override fun getFavoriteImagesIds(): Flow<List<String>> {
        return favoriteImageDao.getFavouriteImageIds()
    }

    override fun getAllFavoriteImages(): Flow<PagingData<UnsplashImage>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { favoriteImageDao.getAllFavouriteImages() }
        ).flow
            .map { pagingData ->
                pagingData.map { it.toDomainModel() }
            }
    }

    override suspend fun getImage(imageId: String): UnsplashImage {
        return apiService.getImage(imageId = imageId).toDomainModel()
    }
}