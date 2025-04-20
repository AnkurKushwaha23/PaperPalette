package com.ankurkushwaha.paperpalette.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("bio")
    val bio: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("instagram_username")
    val instagramUsername: String? = null,
    @SerialName("location")
    val location: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("portfolio_url")
    val portfolioUrl: String? = null,
    @SerialName("profile_image")
    val profileImage: ProfileImage = ProfileImage(),
    @SerialName("total_collections")
    val totalCollections: Int = 0,
    @SerialName("total_likes")
    val totalLikes: Int = 0,
    @SerialName("total_photos")
    val totalPhotos: Int = 0,
    @SerialName("twitter_username")
    val twitterUsername: String? = null,
    @SerialName("username")
    val username: String? = null,
    @SerialName("links")
    val links: UserLinksDto = UserLinksDto()
)


@Serializable
data class ProfileImage(
    @SerialName("large")
    val large: String = "",
    @SerialName("medium")
    val medium: String = "",
    @SerialName("small")
    val small: String = ""
)

@Serializable
data class UserLinksDto(
    @SerialName("html")
    val html: String = "",
    @SerialName("likes")
    val likes: String = "",
    @SerialName("photos")
    val photos: String = "",
    @SerialName("portfolio")
    val portfolio: String = "",
    @SerialName("self")
    val self: String = ""
)