package com.example.live

import android.content.Context
import android.util.Log
import android.view.SurfaceView
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import io.agora.rtc2.*
import io.agora.rtc2.video.VideoCanvas

private const val TAG = "AgoraLive"

/**
 * Gestion du Live Streaming avec Agora.io pour Cusbuy.
 *
 * IMPORTANT: Avant d'utiliser, configure dans .env:
 * AGORA_APP_ID=ton_app_id_agora
 *
 * Créer un compte sur https://www.agora.io
 * Créer un projet → copier l'App ID
 */
class AgoraLiveManager(private val context: Context) {

    // REMPLACER PAR TON APP ID AGORA
    companion object {
        // Récupéré depuis les BuildConfig (via .env)
        private var APP_ID = ""  // sera injecté depuis BuildConfig.AGORA_APP_ID

        fun initialize(appId: String) {
            APP_ID = appId
        }
    }

    private var rtcEngine: RtcEngine? = null

    // État de la connexion
    var isJoined = false
        private set
    var remoteUsers = mutableListOf<Int>()
        private set

    /**
     * Initialise Agora RTC Engine.
     * Appeler une seule fois au démarrage de l'app.
     */
    fun initEngine(
        onUserJoined: (Int) -> Unit = {},
        onUserLeft: (Int) -> Unit = {},
        onError: (Int) -> Unit = {}
    ) {
        try {
            val config = RtcEngineConfig().apply {
                mContext = context
                mAppId = APP_ID
                mEventHandler = object : IRtcEngineEventHandler() {
                    override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
                        Log.d(TAG, "Rejoint le canal: $channel (uid: $uid)")
                        isJoined = true
                    }

                    override fun onUserJoined(uid: Int, elapsed: Int) {
                        Log.d(TAG, "Utilisateur rejoint: $uid")
                        remoteUsers.add(uid)
                        onUserJoined(uid)
                    }

                    override fun onUserOffline(uid: Int, reason: Int) {
                        Log.d(TAG, "Utilisateur parti: $uid")
                        remoteUsers.remove(uid)
                        onUserLeft(uid)
                    }

                    override fun onError(err: Int) {
                        Log.e(TAG, "Agora erreur: $err")
                        onError(err)
                    }
                }
            }
            rtcEngine = RtcEngine.create(config)
            rtcEngine?.enableVideo()
            Log.d(TAG, "Agora Engine initialisé ✅")
        } catch (e: Exception) {
            Log.e(TAG, "Erreur initialisation Agora: ${e.message}")
        }
    }

    /**
     * Démarre un Live en tant que VENDEUR (broadcaster).
     *
     * @param channelName ID unique du live (ex: "live_vendeur123")
     * @param token Token Agora (pour la prod, générer côté serveur)
     */
    fun startLiveAsBroadcaster(channelName: String, token: String? = null) {
        val engine = rtcEngine ?: return

        // Mode broadcast (vendeur = hôte)
        engine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        engine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)

        val options = ChannelMediaOptions().apply {
            publishCameraTrack = true
            publishMicrophoneTrack = true
            clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        }

        engine.joinChannel(token, channelName, 0, options)
        Log.d(TAG, "Live démarré: $channelName")
    }

    /**
     * Rejoint un Live en tant que SPECTATEUR (audience).
     *
     * @param channelName ID du live à regarder
     * @param token Token Agora
     */
    fun joinLiveAsAudience(channelName: String, token: String? = null) {
        val engine = rtcEngine ?: return

        engine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        engine.setClientRole(Constants.CLIENT_ROLE_AUDIENCE)

        val options = ChannelMediaOptions().apply {
            publishCameraTrack = false
            publishMicrophoneTrack = false
            clientRoleType = Constants.CLIENT_ROLE_AUDIENCE
        }

        engine.joinChannel(token, channelName, 0, options)
        Log.d(TAG, "Rejoint le live: $channelName")
    }

    /**
     * Quitte le live.
     */
    fun leaveLive() {
        rtcEngine?.leaveChannel()
        isJoined = false
        remoteUsers.clear()
        Log.d(TAG, "Live quitté")
    }

    /**
     * Activer/désactiver la caméra.
     */
    fun toggleCamera(enabled: Boolean) {
        rtcEngine?.muteLocalVideoStream(!enabled)
    }

    /**
     * Activer/désactiver le micro.
     */
    fun toggleMicrophone(enabled: Boolean) {
        rtcEngine?.muteLocalAudioStream(!enabled)
    }

    /**
     * Retourner la caméra (avant/arrière).
     */
    fun switchCamera() {
        rtcEngine?.switchCamera()
    }

    /**
     * Affichage de la caméra locale (vendeur).
     */
    fun setupLocalVideo(surfaceView: SurfaceView) {
        rtcEngine?.setupLocalVideo(
            VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0)
        )
        rtcEngine?.startPreview()
    }

    /**
     * Affichage de la vidéo d'un utilisateur distant.
     */
    fun setupRemoteVideo(surfaceView: SurfaceView, uid: Int) {
        rtcEngine?.setupRemoteVideo(
            VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid)
        )
    }

    /**
     * Libérer les ressources Agora.
     */
    fun destroy() {
        rtcEngine?.leaveChannel()
        RtcEngine.destroy()
        rtcEngine = null
        Log.d(TAG, "Agora Engine détruit")
    }
}

// ===== COMPOSABLES LIVE =====

/**
 * Vue caméra locale pour le vendeur en live.
 */
@Composable
fun LocalVideoView(agoraManager: AgoraLiveManager, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { ctx ->
            SurfaceView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                agoraManager.setupLocalVideo(this)
            }
        },
        modifier = modifier
    )
}

/**
 * Vue vidéo d'un spectateur distant.
 */
@Composable
fun RemoteVideoView(agoraManager: AgoraLiveManager, uid: Int, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { ctx ->
            SurfaceView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                agoraManager.setupRemoteVideo(this, uid)
            }
        },
        modifier = modifier
    )
}

/**
 * Interface Live complète pour le VENDEUR.
 */
@Composable
fun SellerLiveScreen(
    channelName: String,
    agoraManager: AgoraLiveManager,
    viewersCount: Int = 0,
    onEndLive: () -> Unit
) {
    val context = LocalContext.current
    var isCameraOn by remember { mutableStateOf(true) }
    var isMicOn by remember { mutableStateOf(true) }

    LaunchedEffect(channelName) {
        agoraManager.startLiveAsBroadcaster(channelName)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // Caméra locale plein écran
        LocalVideoView(
            agoraManager = agoraManager,
            modifier = Modifier.fillMaxSize()
        )

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 32.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge LIVE
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Red)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(7.dp).clip(CircleShape).background(Color.White)
                )
                Spacer(Modifier.width(5.dp))
                Text("LIVE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            // Compteur viewers
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.RemoveRedEye, null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(5.dp))
                Text("$viewersCount", color = Color.White, fontSize = 13.sp)
            }

            // Bouton terminer
            TextButton(
                onClick = {
                    agoraManager.leaveLive()
                    onEndLive()
                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Red.copy(alpha = 0.9f)
                ),
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            ) {
                Text("Terminer", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // Contrôles bas
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Toggle caméra
            IconButton(
                onClick = {
                    isCameraOn = !isCameraOn
                    agoraManager.toggleCamera(isCameraOn)
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCameraOn) Color.Black.copy(alpha = 0.5f)
                        else Color.Red.copy(alpha = 0.8f)
                    )
            ) {
                Icon(
                    if (isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                    null, tint = Color.White
                )
            }

            // Toggle micro
            IconButton(
                onClick = {
                    isMicOn = !isMicOn
                    agoraManager.toggleMicrophone(isMicOn)
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isMicOn) Color.Black.copy(alpha = 0.5f)
                        else Color.Red.copy(alpha = 0.8f)
                    )
            ) {
                Icon(
                    if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff,
                    null, tint = Color.White
                )
            }

            // Retourner caméra
            IconButton(
                onClick = { agoraManager.switchCamera() },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.FlipCameraAndroid, null, tint = Color.White)
            }
        }
    }
}
