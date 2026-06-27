package com.example.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

private const val TAG = "FirestoreRepo"

// ===== MODÈLES FIRESTORE =====

data class CusbuyPost(
    val id: String = UUID.randomUUID().toString(),
    val sellerId: String = "",
    val sellerName: String = "",
    val sellerAvatar: String = "",
    val videoUrl: String = "",          // URL Firebase Storage ou Cloudflare Stream
    val thumbnailUrl: String = "",       // Miniature de la vidéo
    val description: String = "",
    val hashtags: List<String> = emptyList(),
    val productName: String = "",
    val productPrice: Double = 0.0,
    val productDescription: String = "",
    val location: GeoPoint? = null,     // Géolocalisation du vendeur
    val city: String = "",
    val country: String = "",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val viewsCount: Int = 0,
    val isLive: Boolean = false,
    val isAuction: Boolean = false,
    val auctionEndTime: Long? = null,
    val currentBid: Double? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class CusbuyUser(
    val uid: String = "",
    val email: String = "",
    val nickname: String = "",
    val avatarUrl: String = "",
    val isSeller: Boolean = false,
    val sellerBusinessName: String = "",
    val sellerCity: String = "",
    val sellerLocation: String = "",
    val sellerPhone: String = "",
    val sellerCategory: String = "",
    val location: GeoPoint? = null,
    val coinsBalance: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val totalSales: Int = 0,
    val rating: Double = 0.0,
    val isVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class CusbuyOrder(
    val id: String = UUID.randomUUID().toString(),
    val buyerId: String = "",
    val sellerId: String = "",
    val postId: String = "",
    val productName: String = "",
    val amount: Double = 0.0,
    val status: String = "pending",     // pending, confirmed, shipped, delivered, cancelled
    val deliveryType: String = "pickup", // pickup, delivery
    val sellerAddress: String = "",
    val buyerAddress: String = "",
    val trackingNumber: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class CusbuyMessage(
    val id: String = UUID.randomUUID().toString(),
    val senderId: String = "",
    val receiverId: String = "",
    val chatId: String = "",
    val text: String = "",
    val imageUrl: String? = null,
    val offerAmount: Double? = null,    // Pour la négociation de prix
    val offerStatus: String? = null,    // pending, accepted, rejected
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

// ===== REPOSITORY =====

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Collections
    private val postsCol = db.collection("posts")
    private val usersCol = db.collection("users")
    private val ordersCol = db.collection("orders")
    private val chatsCol = db.collection("chats")

    // ========== POSTS / VIDÉOS ==========

    /**
     * Récupère le feed géolocalisé.
     * Priorise les vendeurs proches (filtrage côté client pour simplifier).
     */
    fun getFeedPosts(limit: Long = 20): Flow<List<CusbuyPost>> = callbackFlow {
        val listener = postsCol
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Erreur feed: ${error.message}")
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(CusbuyPost::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Récupère les posts d'une ville spécifique (géolocalisation).
     */
    fun getPostsByCity(city: String): Flow<List<CusbuyPost>> = callbackFlow {
        val listener = postsCol
            .whereEqualTo("city", city)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(30)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Erreur posts ville: ${error.message}")
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(CusbuyPost::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Publie un nouveau post.
     */
    suspend fun createPost(post: CusbuyPost): Result<String> {
        return try {
            val docRef = postsCol.document(post.id)
            docRef.set(post).await()
            Result.success(post.id)
        } catch (e: Exception) {
            Log.e(TAG, "Erreur création post: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Like/Unlike un post.
     */
    suspend fun toggleLike(postId: String, userId: String, isLiked: Boolean): Result<Unit> {
        return try {
            val likesCol = postsCol.document(postId).collection("likes")
            if (isLiked) {
                likesCol.document(userId).set(mapOf("userId" to userId)).await()
                postsCol.document(postId).update(
                    "likesCount", com.google.firebase.firestore.FieldValue.increment(1)
                ).await()
            } else {
                likesCol.document(userId).delete().await()
                postsCol.document(postId).update(
                    "likesCount", com.google.firebase.firestore.FieldValue.increment(-1)
                ).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Recherche de posts par hashtag ou mot-clé.
     */
    suspend fun searchPosts(query: String): List<CusbuyPost> {
        return try {
            // Recherche par description (à améliorer avec Algolia ou Typesense)
            val result = postsCol
                .whereGreaterThanOrEqualTo("description", query)
                .whereLessThanOrEqualTo("description", "$query\uF7FF")
                .limit(20)
                .get()
                .await()
            result.documents.mapNotNull { doc ->
                doc.toObject(CusbuyPost::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur recherche: ${e.message}")
            emptyList()
        }
    }

    // ========== UTILISATEURS ==========

    /**
     * Crée ou met à jour le profil utilisateur.
     */
    suspend fun saveUser(user: CusbuyUser): Result<Unit> {
        return try {
            usersCol.document(user.uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Récupère le profil utilisateur en temps réel.
     */
    fun observeUser(uid: String): Flow<CusbuyUser?> = callbackFlow {
        val listener = usersCol.document(uid).addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            val user = snapshot?.toObject(CusbuyUser::class.java)
            trySend(user)
        }
        awaitClose { listener.remove() }
    }

    /**
     * Mise à jour du solde de coins.
     */
    suspend fun updateCoinsBalance(uid: String, delta: Int): Result<Unit> {
        return try {
            usersCol.document(uid).update(
                "coinsBalance",
                com.google.firebase.firestore.FieldValue.increment(delta.toLong())
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== COMMANDES ==========

    /**
     * Crée une commande.
     */
    suspend fun createOrder(order: CusbuyOrder): Result<String> {
        return try {
            ordersCol.document(order.id).set(order).await()
            Result.success(order.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Met à jour le statut d'une commande.
     */
    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> {
        return try {
            ordersCol.document(orderId).update("status", status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Commandes d'un acheteur.
     */
    fun getBuyerOrders(buyerId: String): Flow<List<CusbuyOrder>> = callbackFlow {
        val listener = ordersCol
            .whereEqualTo("buyerId", buyerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val orders = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(CusbuyOrder::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(orders)
            }
        awaitClose { listener.remove() }
    }

    // ========== CHAT ==========

    /**
     * Génère l'ID du chat entre deux utilisateurs.
     */
    fun getChatId(userId1: String, userId2: String): String {
        return if (userId1 < userId2) "${userId1}_${userId2}" else "${userId2}_${userId1}"
    }

    /**
     * Envoie un message.
     */
    suspend fun sendMessage(message: CusbuyMessage): Result<Unit> {
        return try {
            chatsCol.document(message.chatId)
                .collection("messages")
                .document(message.id)
                .set(message)
                .await()
            // Mettre à jour le dernier message du chat
            chatsCol.document(message.chatId).set(
                mapOf(
                    "lastMessage" to message.text,
                    "lastMessageTime" to message.createdAt,
                    "participants" to listOf(message.senderId, message.receiverId)
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Écoute les messages d'un chat en temps réel.
     */
    fun observeMessages(chatId: String): Flow<List<CusbuyMessage>> = callbackFlow {
        val listener = chatsCol.document(chatId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val messages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(CusbuyMessage::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    // ========== UPLOAD FICHIERS ==========

    /**
     * Upload une vidéo vers Firebase Storage.
     * Pour la production, utiliser Cloudflare Stream à la place.
     */
    suspend fun uploadVideo(
        localUri: String,
        sellerId: String,
        onProgress: (Float) -> Unit = {}
    ): Result<String> {
        return try {
            val videoId = UUID.randomUUID().toString()
            val storageRef = storage.reference
                .child("videos/$sellerId/$videoId.mp4")

            val uri = android.net.Uri.parse(localUri)
            val uploadTask = storageRef.putFile(uri)

            uploadTask.addOnProgressListener { snapshot ->
                val progress = snapshot.bytesTransferred.toFloat() / snapshot.totalByteCount
                onProgress(progress)
            }.await()

            val downloadUrl = storageRef.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Erreur upload vidéo: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Upload une image de profil ou miniature.
     */
    suspend fun uploadImage(localUri: String, path: String): Result<String> {
        return try {
            val storageRef = storage.reference.child(path)
            val uri = android.net.Uri.parse(localUri)
            storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
