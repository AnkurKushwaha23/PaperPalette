package com.ankurkushwaha.paperpalette.data.remote

import com.ankurkushwaha.paperpalette.data.remote.dto.UnsplashImageDto
import com.ankurkushwaha.paperpalette.data.remote.dto.UnsplashImagesSearchResult
import com.ankurkushwaha.paperpalette.data.util.Constants.API_KEY
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashApiService {

    @Headers("Authorization: Client-ID $API_KEY")
    @GET("/photos")
    suspend fun getEditorialFeedImages(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ):List<UnsplashImageDto>

    @Headers("Authorization: Client-ID $API_KEY")
    @GET("/search/photos")
    suspend fun searchImages(
        @Query("query") query:String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ):UnsplashImagesSearchResult

    @Headers("Authorization: Client-ID $API_KEY")
    @GET("/photos/{id}")
    suspend fun getImage(
        @Path("id") imageId: String
    ):UnsplashImageDto
}