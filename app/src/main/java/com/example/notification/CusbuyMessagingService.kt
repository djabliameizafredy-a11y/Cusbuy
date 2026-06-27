package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "CusbuyFCM"

// IDs des canaux de notification
object NotificationChannels {
    const val LIVE      = "cusbuy_live"
    const val ORDERS    = "cusbuy_orders"
    const val MESSAGES  = "cusbuy_messages"
    const val AUCTIONS  = "cusbuy_auctions"
    const val GENERAL   = "cusbuy_general"
}

/**
 * Service Firebase Cloud Messaging pour les notifications Cusbuy.
 * Gère tous les types de notifications :
 * - 🔴 Live démarré
 * - 📦 Commande mise à jour
 * - 💬 Nouveau message
 * - 🏆 Enchère (surenchéri, gagné)
 * - ❤️ Like sur ta vidéo
 */
class CusbuyMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Nouveau FCM Token: $token")
        // Enregistrer le token dans Firestore pour l'utilisateur connecté
        saveFcmToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message reçu: ${remoteMessage.data}")

        val data = remoteMessage.data
        val type = data["type"] ?: "general"

        when (type) {
            "live_started"   -> showLiveNotification(data)
            "new_order"      -> showOrderNotification(data)
            "order_update"   -> showOrderUpdateNotification(data)
            "new_message"    -> showMessageNotification(data)
            "auction_outbid" -> showAuctionOutbidNotification(data)
            "auction_won"    -> showAuctionWonNotification(data)
            "new_like"       -> showLikeNotification(data)
            "new_follower"   -> showFollowerNotification(data)
            else             -> showGeneralNotification(remoteMessage.notification)
        }
    }

    // ========== NOTIFICATIONS LIVE ==========

    private fun showLiveNotification(data: Map<String, String>) {
        val sellerName = data["sellerName"] ?: "Un vendeur"
        showNotification(
            channelId = NotificationChannels.LIVE,
            title = "🔴 $sellerName est en LIVE !",
            body = "Rejoins le live maintenant pour découvrir ses produits",
            notificationId = 1001
        )
    }

    // ========== NOTIFICATIONS COMMANDES ==========

    private fun showOrderNotification(data: Map<String, String>) {
        val productName = data["productName"] ?: "un produit"
        showNotification(
            channelId = NotificationChannels.ORDERS,
            title = "📦 Nouvelle commande !",
            body = "Quelqu'un veut acheter : $productName",
            notificationId = 1002
        )
    }

    private fun showOrderUpdateNotification(data: Map<String, String>) {
        val status = data["status"] ?: "mis à jour"
        val productName = data["productName"] ?: "ta commande"
        val statusText = when (status) {
            "confirmed"  -> "confirmée ✅"
            "shipped"    -> "en route 🚚"
            "delivered"  -> "livrée ! 🎉"
            "cancelled"  -> "annulée ❌"
            else         -> status
        }
        showNotification(
            channelId = NotificationChannels.ORDERS,
            title = "📦 Commande $statusText",
            body = productName,
            notificationId = 1003
        )
    }

    // ========== NOTIFICATIONS MESSAGES ==========

    private fun showMessageNotification(data: Map<String, String>) {
        val senderName = data["senderName"] ?: "Quelqu'un"
        val message = data["message"] ?: "t'a envoyé un message"
        showNotification(
            channelId = NotificationChannels.MESSAGES,
            title = "💬 $senderName",
            body = message,
            notificationId = 1004
        )
    }

    // ========== NOTIFICATIONS ENCHÈRES ==========

    private fun showAuctionOutbidNotification(data: Map<String, String>) {
        val productName = data["productName"] ?: "ton enchère"
        val newBid = data["newBid"] ?: ""
        showNotification(
            channelId = NotificationChannels.AUCTIONS,
            title = "🏆 Tu as été surenchéri !",
            body = "$productName - Nouvelle mise : $newBid FCFA",
            notificationId = 1005
        )
    }

    private fun showAuctionWonNotification(data: Map<String, String>) {
        val productName = data["productName"] ?: "un produit"
        showNotification(
            channelId = NotificationChannels.AUCTIONS,
            title = "🎉 Tu as GAGNÉ l'enchère !",
            body = "Félicitations ! Tu as remporté : $productName",
            notificationId = 1006
        )
    }

    // ========== NOTIFICATIONS ENGAGEMENT ==========

    private fun showLikeNotification(data: Map<String, String>) {
        val count = data["count"] ?: "1"
        showNotification(
            channelId = NotificationChannels.GENERAL,
            title = "❤️ $count personnes ont liké ta vidéo",
            body = "Ton produit devient populaire !",
            notificationId = 1007
        )
    }

    private fun showFollowerNotification(data: Map<String, String>) {
        val followerName = data["followerName"] ?: "Quelqu'un"
        showNotification(
            channelId = NotificationChannels.GENERAL,
            title = "👤 $followerName te suit maintenant",
            body = "Clique pour voir son profil",
            notificationId = 1008
        )
    }

    private fun showGeneralNotification(notification: RemoteMessage.Notification?) {
        notification ?: return
        showNotification(
            channelId = NotificationChannels.GENERAL,
            title = notification.title ?: "Cusbuy",
            body = notification.body ?: "",
            notificationId = 1000
        )
    }

    // ========== HELPER ==========

    private fun showNotification(
        channelId: String,
        title: String,
        body: String,
        notificationId: Int
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Créer le canal si nécessaire (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels(notificationManager)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannels(manager: NotificationManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        listOf(
            Triple(NotificationChannels.LIVE, "Lives Cusbuy", "Alertes quand un vendeur est en live"),
            Triple(NotificationChannels.ORDERS, "Commandes", "Mises à jour de tes commandes"),
            Triple(NotificationChannels.MESSAGES, "Messages", "Nouveaux messages"),
            Triple(NotificationChannels.AUCTIONS, "Enchères", "Alertes enchères"),
            Triple(NotificationChannels.GENERAL, "Général", "Notifications générales")
        ).forEach { (id, name, desc) ->
            NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).apply {
                description = desc
                manager.createNotificationChannel(this)
            }
        }
    }

    private fun saveFcmToken(token: String) {
        // Sera implémenté après Firebase Auth
        // val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        // FirebaseFirestore.getInstance().collection("users").document(userId)
        //     .update("fcmToken", token)
        Log.d(TAG, "Token FCM à sauvegarder: $token")
    }
}
