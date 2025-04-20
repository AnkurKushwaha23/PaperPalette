package com.ankurkushwaha.paperpalette.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ankurkushwaha.paperpalette.data.local.dao.EditorialFeedDao
import com.ankurkushwaha.paperpalette.data.local.dao.FavoriteImageDao
import com.ankurkushwaha.paperpalette.data.local.entity.FavoriteImageEntity
import com.ankurkushwaha.paperpalette.data.local.entity.UnsplashImageEntity
import com.ankurkushwaha.paperpalette.data.local.entity.UnsplashRemoteKeyEntity

@Database(
    entities = [FavoriteImageEntity::class,UnsplashImageEntity::class,UnsplashRemoteKeyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PaperPaletteDataBase :RoomDatabase() {
    abstract fun favouriteImageDao(): FavoriteImageDao
    abstract fun editorialFeedDao():EditorialFeedDao
}