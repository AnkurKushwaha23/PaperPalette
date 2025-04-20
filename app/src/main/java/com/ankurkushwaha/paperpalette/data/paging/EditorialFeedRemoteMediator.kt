package com.ankurkushwaha.paperpalette.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ankurkushwaha.paperpalette.data.local.PaperPaletteDataBase
import com.ankurkushwaha.paperpalette.data.local.entity.UnsplashImageEntity
import com.ankurkushwaha.paperpalette.data.local.entity.UnsplashRemoteKeyEntity
import com.ankurkushwaha.paperpalette.data.mapper.toEntityList
import com.ankurkushwaha.paperpalette.data.remote.UnsplashApiService
import com.ankurkushwaha.paperpalette.data.util.Constants.ITEMS_PER_PAGE

@OptIn(ExperimentalPagingApi::class)
class EditorialFeedRemoteMediator(
    private val apiService: UnsplashApiService,
    private val dataBase: PaperPaletteDataBase
) : RemoteMediator<Int, UnsplashImageEntity>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

    private val editorialFeedDao = dataBase.editorialFeedDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UnsplashImageEntity>
    ): MediatorResult {
        try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextPage?.minus(1) ?: STARTING_PAGE_INDEX
                }

                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    Log.d("ucc", "remoteKeysPrev: ${remoteKeys?.prevPage}")
                    val prevPage = remoteKeys?.prevPage
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    prevPage
                }

                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    Log.d("ucc", "remoteKeysNext: ${remoteKeys?.nextPage}")
                    val nextPage = remoteKeys?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextPage
                }
            }

            val response = apiService.getEditorialFeedImages(
                page = currentPage,
                perPage = ITEMS_PER_PAGE
            )
            val endOfPaginationReached = response.isEmpty()

            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            dataBase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    editorialFeedDao.deleteAllEditorialFeed()
                    editorialFeedDao.deleteAllRemoteKeys()
                }

                val remoteKeys = response.map { unsplashImageDto ->
                    UnsplashRemoteKeyEntity(
                        id = unsplashImageDto.id,
                        prevPage = prevPage,
                        nextPage = nextPage
                    )
                }

                editorialFeedDao.insertRemoteKeys(remoteKeys)
                editorialFeedDao.insertEditorialFeedImages(response.toEntityList())
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, UnsplashImageEntity>
    ): UnsplashRemoteKeyEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                editorialFeedDao.getRemoteKeys(id = id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, UnsplashImageEntity>
    ): UnsplashRemoteKeyEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { unsplashImage ->
                editorialFeedDao.getRemoteKeys(id = unsplashImage.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, UnsplashImageEntity>
    ): UnsplashRemoteKeyEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { unsplashImage ->
                editorialFeedDao.getRemoteKeys(id = unsplashImage.id)
            }
    }
}