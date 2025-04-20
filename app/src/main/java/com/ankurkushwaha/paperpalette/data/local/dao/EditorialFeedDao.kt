package com.ankurkushwaha.paperpalette.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ankurkushwaha.paperpalette.data.local.entity.UnsplashImageEntity
import com.ankurkushwaha.paperpalette.data.local.entity.UnsplashRemoteKeyEntity

@Dao
interface EditorialFeedDao {
    @Upsert
    suspend fun insertEditorialFeedImages(images: List<UnsplashImageEntity>)

    @Query("DELETE FROM unsplash_image_table")
    suspend fun deleteAllEditorialFeed()

    @Query("SELECT * FROM unsplash_image_table")
    fun getAllEditorialFeedImages():PagingSource<Int,UnsplashImageEntity>

    @Upsert
    suspend fun insertRemoteKeys(remoteKeys: List<UnsplashRemoteKeyEntity>)

    @Query("DELETE FROM remote_keys_table")
    suspend fun deleteAllRemoteKeys()

    @Query("SELECT * FROM remote_keys_table WHERE id= :id")
    suspend fun getRemoteKeys(id:String):UnsplashRemoteKeyEntity
}