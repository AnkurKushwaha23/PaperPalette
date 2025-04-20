package com.ankurkushwaha.paperpalette.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ankurkushwaha.paperpalette.data.mapper.toDomainModelList
import com.ankurkushwaha.paperpalette.data.remote.UnsplashApiService
import com.ankurkushwaha.paperpalette.domain.model.UnsplashImage

class SearchPagingSource(
    private val query: String,
    private val unsplashApiService: UnsplashApiService
) : PagingSource<Int, UnsplashImage>() {
    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, UnsplashImage>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashImage> {
        val currentPage = params.key ?: STARTING_PAGE_INDEX
        return try {
            val response = unsplashApiService.searchImages(
                query = query,
                page = currentPage,
                perPage = params.loadSize
            )
            val endOfPaginationReached = response.images.isEmpty()
            LoadResult.Page(
                data = response.images.toDomainModelList(),
                prevKey = if (currentPage == STARTING_PAGE_INDEX) null else currentPage - 1,
                nextKey = if (endOfPaginationReached) null else currentPage + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}