package com.ankurkushwaha.paperpalette.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "unsplash_image_table")
data class UnsplashImageEntity(
    @PrimaryKey
    val id: String,
    val imageUrlSmall: String,
    val imageUrlRegular: String,
    val imageUrlRaw: String,
    val photographerName: String?,
    val photographerUsername: String?,
    val photographerProfileImgUrl: String,
    val photographerProfileLink: String,
    val width: Int,
    val height: Int,
    val description: String?
)
