package com.ankurkushwaha.paperpalette.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.ankurkushwaha.paperpalette.data.local.entity.FavoriteImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteImageDao {
    @Query("SELECT * FROM fav_image_table")
    fun getAllFavouriteImages(): PagingSource<Int, FavoriteImageEntity>

    @Upsert
    suspend fun insertFavouriteImage(image: FavoriteImageEntity)

    @Delete
    suspend fun deleteFavouriteImage(image: FavoriteImageEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM fav_image_table WHERE id= :id)")
    suspend fun isImageFavourite(id: String): Boolean

    @Query("SELECT id FROM fav_image_table")
    fun getFavouriteImageIds(): Flow<List<String>>
}