package com.example

import java.io.Serializable

data class UserProfile(
    val isRegistered: Boolean = false,
    val email: String = "djabliameizafredy@gmail.com",
    val nickname: String = "FredyD",
    val defaultMode: String = "Acheteur",
    val currentMode: String = "Acheteur",
    
    // Seller-specific registration info
    val sellerBusinessName: String = "",
    val sellerCity: String = "",
    val sellerLocation: String = "",
    val sellerGoogleAccount: String = "",
    val sellerCategory: String = "",
    val sellerPhone: String = "",
    val isSellerRegistered: Boolean = false
) : Serializable

enum class UserMode {
    ACHETEUR, VENDEUR
}

enum class PostType {
    VIDEO, IMAGE, CAROUSEL
}

data class PostComment(
    val id: Int,
    val author: String,
    val authorAvatar: String,
    val text: String,
    val timestamp: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val isPinned: Boolean = false,
    val replyVideoTitle: String? = null // Video response if any
)

data class ProductInfo(
    val name: String,
    val priceUSD: Double,
    val location: String,
    val description: String,
    val coinsRequired: Int = 100
)

data class VideoPost(
    val id: Int,
    val sellerName: String,
    val sellerAvatarUrl: String,
    val description: String,
    val hashtags: List<String>,
    val type: PostType = PostType.VIDEO,
    
    val videoSimulatedProgress: Float = 0f,
    val durationSeconds: Int = 30,
    val isLiked: Boolean = false,
    val likesCount: Int = 1540,
    val sharesCount: Int = 89,
    val comments: List<PostComment> = emptyList(),
    
    val soundTitle: String = "Original Sound - @${sellerName}",
    val originalSoundSeller: String? = null,
    val voiceEffect: String = "Normal",
    val filterName: String = "None",
    
    val photos: List<String> = emptyList(), // For carousel if type CAROUSEL
    val productInfo: ProductInfo? = null,
    val flashSaleCountdownSeconds: Int? = null
)

data class OrderAction(
    val id: Int,
    val type: ActionType, // BUY vs SELL
    val itemTitle: String,
    val description: String,
    val amountUSD: Double = 0.0,
    val amountCoins: Int = 0,
    val status: String, // "Livrée", "En attente", "Expédiée", "Payée"
    val timestamp: String,
    val trackingNumber: String? = null
)

enum class ActionType {
    BUY, SELL
}

data class SellerStory(
    val id: Int,
    val sellerName: String,
    val sellerAvatar: String,
    val type: StoryWidgetType = StoryWidgetType.NONE,
    val imageOrBg: String,
    val title: String,
    val pollQuestion: String? = null,
    val pollOption1: String? = null,
    val pollOption2: String? = null,
    var pollVotes1: Int = 0,
    var pollVotes2: Int = 0,
    val qnaQuestion: String? = null,
    val countdownMinutes: Int? = null,
    val productLinkName: String? = null
)

enum class StoryWidgetType {
    NONE, POLL, QNA, COUNTDOWN, LINK
}

data class LiveGift(
    val name: String,
    val icon: String,
    val coinsCost: Int
)

data class LiveStreamSim(
    val id: Int,
    val sellerName: String,
    val sellerAvatar: String,
    val viewerCount: Int,
    val title: String,
    val activeProduct: ProductInfo?,
    val isLiveNow: Boolean = true
)

data class TopViewer(
    val nickname: String,
    val rank: Int,
    val coinsSent: Int
)
