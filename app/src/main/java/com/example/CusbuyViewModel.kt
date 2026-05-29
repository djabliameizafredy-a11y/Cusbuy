package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CusbuyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = CusbuyDatabase.getDatabase(application)
    private val dao = database.dao()

    // Database backed Profile
    val profileState: StateFlow<ProfileEntity> = dao.getProfile()
        .map { it ?: ProfileEntity() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileEntity()
        )

    // Database backed Buy/Sell Trackers
    val allActions: StateFlow<List<ActionEntity>> = dao.getAllActions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // In-memory list of active posts (Videos, Ken Burns Images, Carousel)
    private val _posts = MutableStateFlow<List<VideoPost>>(emptyList())
    val posts: StateFlow<List<VideoPost>> = _posts.asStateFlow()

    // In-memory list of active stories
    private val _stories = MutableStateFlow<List<SellerStory>>(emptyList())
    val stories: StateFlow<List<SellerStory>> = _stories.asStateFlow()

    // Previews of current navigation screens
    // "ONBOARDING_SPLASH", "ONBOARDING_QUESTIONS", "ONBOARDING_CHOICE", "MAIN_APP", "PUBLISH_CAM", "LIVE_SCREEN"
    private val _currentScreen = MutableStateFlow("ONBOARDING_SPLASH")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Mode state for instant transitions
    private val _userMode = MutableStateFlow(UserMode.ACHETEUR)
    val userMode: StateFlow<UserMode> = _userMode.asStateFlow()

    // Active live stream sim
    private val _activeLive = MutableStateFlow<LiveStreamSim?>(null)
    val activeLive: StateFlow<LiveStreamSim?> = _activeLive.asStateFlow()

    // Temporary registration fields holds details before saving
    var regEmail = "djabliameizafredy@gmail.com"
    var regNickname = "FredyD"
    var regBusinessName = "Cusbuy Boutique"
    var regCity = "Douala"
    var regLocation = "Avenue De Gaulle, Immeuble Blue"
    var regGoogleAccount = "djabliameiz@gmail.com"
    var regCategory = "Mode & Confection"
    var regPhone = "+237 671234567"

    // Simulation states
    val activePostIndex = MutableStateFlow(0)
    val activeStoryIndex = MutableStateFlow(-1) // -1 means none
    val isVideoPlaying = MutableStateFlow(true)
    val liveGiftFlyers = MutableStateFlow<List<Pair<String, Long>>>(emptyList()) // Pair of Gift Emoji & Timestamps
    val liveViewers = MutableStateFlow<List<TopViewer>>(emptyList())

    init {
        // Initialize default mock posts and stories
        loadMockContent()
        
        // Listen to profile updates to sync mode on launch
        viewModelScope.launch {
            profileState.collect { prof ->
                if (prof.isRegistered) {
                    val target = if (prof.currentMode == "Vendeur") UserMode.VENDEUR else UserMode.ACHETEUR
                    _userMode.value = target
                }
            }
        }

        // Simulating the looping timeline countdown and video progress animations
        viewModelScope.launch {
            while (true) {
                delay(100)
                if (isVideoPlaying.value) {
                    _posts.update { list ->
                        list.mapIndexed { idx, post ->
                            if (idx == activePostIndex.value && post.type == PostType.VIDEO) {
                                val nextProgress = (post.videoSimulatedProgress + 0.012f)
                                post.copy(videoSimulatedProgress = if (nextProgress > 1f) 0f else nextProgress)
                            } else {
                                post
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadMockContent() {
        val commentsPost1 = listOf(
            PostComment(1, "Aline S.", "👩‍💼", "Est-ce qu'il y a une garantie de 2 ans ?", "Il y a 2h", 24, true, true),
            PostComment(2, "Boutique Officielle", "🏪", "Oui Aline ! Garantie constructeur de 24 mois incluse 😊", "Il y a 1h", 12, false, false, replyVideoTitle = "Réponse vidéo: Détails Garantie constructeur"),
            PostComment(3, "Jean-Claude", "👨", "Livraison超 rapide sur Douala ! Reçu en 45 minutes chrono 🔥", "Il y a 30m", 5, false, false),
            PostComment(4, "ToxikGamer", "💀", "[Commentaire masqué automatiquement par l'IA de filtrage pour non-respect des règles de courtoisie]", "Il y a 10m", 0, false, false)
        )

        val commentsPost2 = listOf(
            PostComment(10, "Mireille T.", "👩", "Superbe matière ! C'est du cuir véritable ?", "Il y a 5h", 4, false, false),
            PostComment(11, "Boutique Officielle", "🏪", "Absolument Mireille, 100% cuir de vachette local !", "Il y a 4h", 2, false, false)
        )

        val items = listOf(
            VideoPost(
                id = 1,
                sellerName = "Cusbuy Tech Hub",
                sellerAvatarUrl = "⚡",
                description = "Le tout nouveau smartphone ultra fluide pour vos créations de vidéos et gaming lourd. Retrait immédiat en boutique !",
                hashtags = listOf("Tech", "CusbuyShopping", "Smartphone", "Vitesse"),
                type = PostType.VIDEO,
                durationSeconds = 45,
                likesCount = 2840,
                sharesCount = 120,
                comments = commentsPost1,
                soundTitle = "Chill Beats for Shopping Vibe (Trending)",
                productInfo = ProductInfo("Sonic S26 Ultra 5G", 699.0, "Douala, Cameroun", "Smartphone Premium, 512Go RAM, Caméra 200Mpx", 7000)
            ),
            VideoPost(
                id = 2,
                sellerName = "Atelier Sensation",
                sellerAvatarUrl = "👞",
                description = "Nos mocassins de luxe faits main avec passion et finitions impeccables. Zoom sur le grain du cuir !",
                hashtags = listOf("Artisanat", "Luxe", "Chaussures", "AfriqueStyle"),
                type = PostType.IMAGE, // image avec Ken Burns effect
                durationSeconds = 10,
                likesCount = 890,
                sharesCount = 42,
                comments = commentsPost2,
                soundTitle = "Atmospheric Crafting Sound - Soft Harp",
                productInfo = ProductInfo("Mocassins Prestige Cuir", 120.0, "Yaoundé, Cameroun", "100% Cuir Véritable, Cousu main standard haut de gamme", 1200)
            ),
            VideoPost(
                id = 3,
                sellerName = "K-Beauty Cameroon",
                sellerAvatarUrl = "🧴",
                description = "Découvrez le trio sérum hydratant + gommage éclat pour une peau rayonnante dès la première semaine. Swipez pour voir les 3 étapes !",
                hashtags = listOf("SkinCare", "Eclat", "SelfCare", "BeautyCaroussel"),
                type = PostType.CAROUSEL, // Carrousel de 3 images
                durationSeconds = 15,
                likesCount = 4500,
                sharesCount = 310,
                comments = emptyList(),
                soundTitle = "Upbeat Pop - K-Beauty Glow Theme",
                photos = listOf("https://images.unsplash.com/photo-1620916566398-39f1143ab7be", "https://images.unsplash.com/photo-1556228720-195a672e8a03", "https://images.unsplash.com/photo-1512496015851-a90fb38ba796"),
                productInfo = ProductInfo("Pack SkinGlow Pro", 45.0, "Limbe, Cameroun", "Kit complet 3 phases hypoallergénique, formule bio active", 450)
            )
        )
        _posts.value = items

        _stories.value = listOf(
            SellerStory(
                id = 101,
                sellerName = "Alpha Electrics",
                sellerAvatar = "🔌",
                imageOrBg = "DarkSlate",
                title = "🎁 SONDAGE EXCLUSIF",
                type = StoryWidgetType.POLL,
                pollQuestion = "Quelle couleur préférez-vous pour la coque magsafe ?",
                pollOption1 = "🟢 Vert Emeraude",
                pollOption2 = "🛡️ Gris Titane",
                pollVotes1 = 24,
                pollVotes2 = 38
            ),
            SellerStory(
                id = 102,
                sellerName = "K-Beauty Cameroon",
                sellerAvatar = "🧴",
                imageOrBg = "RoseDust",
                title = "💡 Posez vos questions !",
                type = StoryWidgetType.QNA,
                qnaQuestion = "Avez-vous des questions sur l'application de notre masque éclat ?"
            ),
            SellerStory(
                id = 103,
                sellerName = "Cusbuy Tech Hub",
                sellerAvatar = "⚡",
                imageOrBg = "SunsetOrange",
                title = "⏳ VENTE EN DIRECT",
                type = StoryWidgetType.COUNTDOWN,
                countdownMinutes = 15,
                productLinkName = "Sonic S26 Ultra 5G"
            )
        )

        // Mock Live viewers & stats
        liveViewers.value = listOf(
            TopViewer("FredyD (Moi)", 1, 450),
            TopViewer("Amélie_V", 2, 250),
            TopViewer("Alex_98", 3, 110)
        )
    }

    // Setup & Onboarding flow actions
    fun proceedToRegistration() {
        _currentScreen.value = "ONBOARDING_REGISTRATION"
    }

    fun skipRegistrationAndContinue() {
        viewModelScope.launch {
            val unsignedProfile = ProfileEntity(
                isRegistered = false,
                nickname = "Visiteur",
                currentMode = "Acheteur",
                defaultMode = "Acheteur"
            )
            dao.saveProfile(unsignedProfile)
            _userMode.value = UserMode.ACHETEUR
            _currentScreen.value = "MAIN_APP"
        }
    }

    fun submitRegistration(isSellerPreferred: Boolean) {
        viewModelScope.launch {
            val prof = ProfileEntity(
                isRegistered = true,
                email = regEmail,
                nickname = regNickname,
                sellerBusinessName = if (isSellerPreferred) regBusinessName else "",
                sellerCity = if (isSellerPreferred) regCity else "",
                sellerLocation = if (isSellerPreferred) regLocation else "",
                sellerGoogleAccount = if (isSellerPreferred) regGoogleAccount else "",
                sellerCategory = if (isSellerPreferred) regCategory else "",
                sellerPhone = if (isSellerPreferred) regPhone else "",
                isSellerRegistered = isSellerPreferred,
                defaultMode = if (isSellerPreferred) "Vendeur" else "Acheteur",
                currentMode = if (isSellerPreferred) "Vendeur" else "Acheteur"
            )
            dao.saveProfile(prof)
            
            // Insert initial welcome trackers to verify history instantly
            if (isSellerPreferred) {
                dao.insertAction(ActionEntity(
                    type = "SELL",
                    itemTitle = "Inscription Vendeur Activée",
                    description = "Boutique ${regBusinessName} enregistrée à ${regCity} avec succès.",
                    amountUSD = 0.0,
                    amountCoins = 0,
                    status = "Actif",
                    timestamp = "À l'instant",
                    trackingNumber = "REG-VEN-${System.currentTimeMillis() % 10000}"
                ))
            }
            dao.insertAction(ActionEntity(
                type = "BUY",
                itemTitle = "Bienvenue sur Cusbuy !",
                description = "Compte acheteur créé avec succès. Bonus inscription de 600 Coins offerts.",
                amountUSD = 0.0,
                amountCoins = 600,
                status = "Terminé",
                timestamp = "À l'instant",
                trackingNumber = "WELCOME-BONUS"
            ))

            _userMode.value = if (isSellerPreferred) UserMode.VENDEUR else UserMode.ACHETEUR
            _currentScreen.value = "MAIN_APP"
        }
    }

    // Onboarding Mode Selection choice
    fun completeOnboardingChoice(preferred: UserMode) {
        viewModelScope.launch {
            val currentProfile = profileState.value
            val isSeller = preferred == UserMode.VENDEUR
            
            if (isSeller && !currentProfile.isSellerRegistered) {
                // User wants seller but hasn't completed seller onboarding info, send them to onboarding questions screen
                _currentScreen.value = "ONBOARDING_QUESTIONS"
            } else {
                val updated = currentProfile.copy(
                    currentMode = if (isSeller) "Vendeur" else "Acheteur",
                    defaultMode = if (isSeller) "Vendeur" else "Acheteur"
                )
                dao.saveProfile(updated)
                _userMode.value = preferred
                _currentScreen.value = "MAIN_APP"
            }
        }
    }

    fun saveCompleteSellerOnboarding() {
        viewModelScope.launch {
            val current = profileState.value
            val updated = current.copy(
                isSellerRegistered = true,
                sellerBusinessName = regBusinessName,
                sellerCity = regCity,
                sellerLocation = regLocation,
                sellerGoogleAccount = regGoogleAccount,
                sellerCategory = regCategory,
                sellerPhone = regPhone,
                currentMode = "Vendeur",
                defaultMode = "Vendeur"
            )
            dao.saveProfile(updated)
            
            // Log Sell Tracker action
            dao.insertAction(ActionEntity(
                type = "SELL",
                itemTitle = "Onboarding Vendeur",
                description = "Spécifications de la boutique ${regBusinessName} validées.",
                amountUSD = 0.0,
                amountCoins = 0,
                status = "Actif",
                timestamp = "Félicitations",
                trackingNumber = "ONBD-VEN"
            ))

            _userMode.value = UserMode.VENDEUR
            _currentScreen.value = "MAIN_APP"
        }
    }

    // Dynamic Switch Mode 
    fun switchUserMode(targetMode: UserMode) {
        viewModelScope.launch {
            val currentProfile = profileState.value
            if (targetMode == UserMode.VENDEUR && !currentProfile.isSellerRegistered) {
                // Send to onboarding questions so they define business details first
                _currentScreen.value = "ONBOARDING_QUESTIONS"
            } else {
                val updated = currentProfile.copy(
                    currentMode = if (targetMode == UserMode.VENDEUR) "Vendeur" else "Acheteur"
                )
                dao.saveProfile(updated)
                _userMode.value = targetMode
            }
        }
    }

    // Interactions
    fun toggleLikePost(postId: Int) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val nextLiked = !post.isLiked
                    val nextCount = if (nextLiked) post.likesCount + 1 else post.likesCount - 1
                    post.copy(isLiked = nextLiked, likesCount = nextCount)
                } else {
                    post
                }
            }
        }
    }

    // Add Comment
    fun addCommentToPost(postId: Int, text: String) {
        if (text.trim().isEmpty()) return
        
        // Simulating IA toxicity check: if comment contains toxic words, censor automatically
        val hasToxicWords = text.lowercase().contains("fake") || 
                             text.lowercase().contains("arnaque") || 
                             text.lowercase().contains("nul") ||
                             text.lowercase().contains("scam")
        
        val commentText = if (hasToxicWords) {
            "[Commentaire masqué automatiquement par l'IA de filtrage pour non-respect des règles de courtoisie]"
        } else {
            text
        }

        val newComment = PostComment(
            id = (System.currentTimeMillis() % 100000).toInt(),
            author = profileState.value.nickname,
            authorAvatar = "📱",
            text = commentText,
            timestamp = "À l'instant",
            likesCount = 0,
            isLiked = false
        )

        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    post.copy(comments = post.comments + newComment)
                } else {
                    post
                }
            }
        }
    }

    // Use Sound of another video
    fun useAnotherVideoSound(soundName: String, originalSeller: String) {
        // Simulates using sound in publish draft
        viewModelScope.launch {
            // Toast or store in state
            // Let the user know the draft now carries this sound
        }
    }

    // Poll story click
    fun voteInStoryPoll(storyId: Int, isOption1Selected: Boolean) {
        _stories.update { list ->
            list.map { story ->
                if (story.id == storyId) {
                    if (isOption1Selected) {
                        story.copy(pollVotes1 = story.pollVotes1 + 1)
                    } else {
                        story.copy(pollVotes2 = story.pollVotes2 + 1)
                    }
                } else {
                    story
                }
            }
        }
    }

    // Gifting Live stream
    fun sendLiveGift(gift: LiveGift) {
        viewModelScope.launch {
            val current = profileState.value
            if (current.walletCoins >= gift.coinsCost) {
                // Deduct coins from user
                val profileUpdated = current.copy(
                    walletCoins = current.walletCoins - gift.coinsCost
                )
                dao.saveProfile(profileUpdated)

                // Add Buy Action
                dao.insertAction(ActionEntity(
                    type = "BUY",
                    itemTitle = "Cadeau envoyé en Live",
                    description = "Envoi du cadeau ${gift.name} (${gift.icon}) au diffuseur.",
                    amountUSD = 0.0,
                    amountCoins = gift.coinsCost,
                    status = "Envoyé",
                    timestamp = "À l'instant",
                    trackingNumber = "GIFT-${System.currentTimeMillis() % 10000}"
                ))

                // Log Sell Action as if received by seller (simulated revenue: original coins * 70% due to 30% commission)
                val sellerEarnedCoins = (gift.coinsCost * 0.70).toInt()
                dao.insertAction(ActionEntity(
                    type = "SELL",
                    itemTitle = "Cadeau reçu en Live",
                    description = "Reçu ${gift.name} de ${current.nickname} (-30% Commission Cusbuy)",
                    amountUSD = 0.0,
                    amountCoins = sellerEarnedCoins,
                    status = "Crédité",
                    timestamp = "À l'instant",
                    trackingNumber = "RCV-GIFT-${System.currentTimeMillis() % 10000}"
                ))

                // Visual effects: append a flyer animation
                val listForFlyers = liveGiftFlyers.value.toMutableList()
                listForFlyers.add(Pair("${gift.icon} ${current.nickname} a offert un ${gift.name} !", System.currentTimeMillis()))
                liveGiftFlyers.value = listForFlyers

                // Update live leaderboard
                liveViewers.update { list ->
                    var found = false
                    val updated = list.map { viewer ->
                        if (viewer.nickname.contains("Moi")) {
                            found = true
                            viewer.copy(coinsSent = viewer.coinsSent + gift.coinsCost)
                        } else {
                            viewer
                        }
                    }.sortedByDescending { it.coinsSent }
                    if (!found) {
                        (updated + TopViewer("${current.nickname} (Moi)", 1, gift.coinsCost)).sortedByDescending { it.coinsSent }
                    } else {
                        updated
                    }
                }
            }
        }
    }

    // Buying coin packs
    fun buyCoinPack(coinsAmount: Int, costUSD: Double) {
        viewModelScope.launch {
            val current = profileState.value
            val updated = current.copy(
                walletCoins = current.walletCoins + coinsAmount,
                walletCashUSD = if (current.walletCashUSD >= costUSD) current.walletCashUSD - costUSD else current.walletCashUSD
            )
            dao.saveProfile(updated)

            dao.insertAction(ActionEntity(
                type = "BUY",
                itemTitle = "Recharge Cusbuy Coins",
                description = "Pack de +$coinsAmount Coins acheté avec succès.",
                amountUSD = costUSD,
                amountCoins = coinsAmount,
                status = "Validé",
                timestamp = "À l'instant",
                trackingNumber = "COIN-PACK-${System.currentTimeMillis() % 10000}"
            ))
        }
    }

    // Purchase product from feed
    fun orderProduct(post: VideoPost) {
        val product = post.productInfo ?: return
        viewModelScope.launch {
            val current = profileState.value
            
            // Deduct funds or record
            val coinsPackPrice = product.coinsRequired
            val hasEnoughCoins = current.walletCoins >= coinsPackPrice
            
            if (hasEnoughCoins) {
                val updatedProfile = current.copy(
                    walletCoins = current.walletCoins - coinsPackPrice
                )
                dao.saveProfile(updatedProfile)
                
                // Track Purchase
                dao.insertAction(ActionEntity(
                    type = "BUY",
                    itemTitle = "Commande: ${product.name}",
                    description = "Acheté à ${post.sellerName} via feed vidéo. Livraison locale en cours.",
                    amountUSD = product.priceUSD,
                    amountCoins = coinsPackPrice,
                    status = "En préparation",
                    timestamp = "À l'instant",
                    trackingNumber = "BUY-${System.currentTimeMillis() % 100000}"
                ))

                // Track reverse Sale for seller
                dao.insertAction(ActionEntity(
                    type = "SELL",
                    itemTitle = "Vente: ${product.name}",
                    description = "Vendu à ${current.nickname} pour ${product.priceUSD} USD.",
                    amountUSD = product.priceUSD * 0.7, // Commission 30% or simulation
                    amountCoins = (coinsPackPrice * 0.7).toInt(),
                    status = "En cours d'expédition",
                    timestamp = "À l'instant",
                    trackingNumber = "SELL-${System.currentTimeMillis() % 100000}"
                ))
            } else {
                // Use normal checkout path if coins not selected or lacking
                dao.insertAction(ActionEntity(
                    type = "BUY",
                    itemTitle = "Commande par cash: ${product.name}",
                    description = "Achat direct de ${product.name} par paiement mobile/carte. En cours d'expédition.",
                    amountUSD = product.priceUSD,
                    amountCoins = 0,
                    status = "En préparation",
                    timestamp = "À l'instant",
                    trackingNumber = "CASH-BUY-${System.currentTimeMillis() % 100000}"
                ))

                // Insert matching sell action
                dao.insertAction(ActionEntity(
                    type = "SELL",
                    itemTitle = "Vente par cash: ${product.name}",
                    description = "Vendu à ${current.nickname} via vidéo. En attente de livraison.",
                    amountUSD = product.priceUSD * 0.7,
                    amountCoins = 0,
                    status = "À expédier",
                    timestamp = "À l'instant",
                    trackingNumber = "CASH-SELL-${System.currentTimeMillis() % 100000}"
                ))
            }
        }
    }

    // Publish Custom Content (Video, single image or carousel)
    fun createPublishContent(
        type: PostType,
        title: String,
        desc: String,
        price: Double,
        duration: Int = 30,
        filterName: String = "None",
        voiceEffect: String = "Normal",
        photosCount: Int = 1
    ) {
        viewModelScope.launch {
            val sellerProfile = profileState.value
            val idVal = (System.currentTimeMillis() % 100000).toInt()
            
            val photosList = if (type == PostType.CAROUSEL) {
                List(photosCount) { idx ->
                    when (idx) {
                        0 -> "https://images.unsplash.com/photo-1620916566398-39f1143ab7be"
                        1 -> "https://images.unsplash.com/photo-1556228720-195a672e8a03"
                        else -> "https://images.unsplash.com/photo-1512496015851-a90fb38ba796"
                    }
                }
            } else if (type == PostType.IMAGE) {
                listOf("https://images.unsplash.com/photo-1556228720-195a672e8a03")
            } else emptyList()

            val newPost = VideoPost(
                id = idVal,
                sellerName = if (sellerProfile.isSellerRegistered) sellerProfile.sellerBusinessName else "Boutique @${sellerProfile.nickname}",
                sellerAvatarUrl = "🏪",
                description = desc,
                hashtags = listOf("Nouveau", "CusbuyBoutique", "PromoFlash"),
                type = type,
                durationSeconds = duration,
                likesCount = 0,
                sharesCount = 0,
                comments = emptyList(),
                soundTitle = "Son Original par @${sellerProfile.nickname}",
                filterName = filterName,
                voiceEffect = voiceEffect,
                photos = photosList,
                productInfo = ProductInfo(title, price, sellerProfile.sellerCity.ifEmpty { "Douala" }, desc, (price * 10).toInt())
            )

            // Insert at the top of the feed
            _posts.update { listOf(newPost) + it }
            activePostIndex.value = 0

            // Log Sale action tracker
            dao.insertAction(ActionEntity(
                type = "SELL",
                itemTitle = "Publication: ${title}",
                description = "Nouveau contenu ${type.name} partagé avec succès dans le feed.",
                amountUSD = 0.0,
                amountCoins = 0,
                status = "Publié",
                timestamp = "À l'instant",
                trackingNumber = "PUB-${idVal}"
            ))

            // Switch to main feed to see the published content
            _currentScreen.value = "MAIN_APP"
        }
    }

    // Clear actions list
    fun clearHistories() {
        viewModelScope.launch {
            dao.clearActions()
        }
    }

    // Set layout screen
    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    // Join Live Stream simulated
    fun enterLive(liveState: LiveStreamSim) {
        _activeLive.value = liveState
        _currentScreen.value = "LIVE_SCREEN"
        
        // Populate system feedback
        viewModelScope.launch {
            delay(1000)
            val flyers = liveGiftFlyers.value.toMutableList()
            flyers.add(Pair("👀 Bienvenue sur le Live de ${liveState.sellerName}", System.currentTimeMillis()))
            liveGiftFlyers.value = flyers
        }
    }

    fun exitLive() {
        _activeLive.value = null
        _currentScreen.value = "MAIN_APP"
    }
}
