package com.ankurkushwaha.paperpalette.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys_table")
data class UnsplashRemoteKeyEntity(
    @PrimaryKey
    val id :String,
    val prevPage : Int?,
    val nextPage : Int?
)
