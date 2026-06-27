package com.example.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

/**
 * Gestion de l'authentification Firebase pour Cusbuy.
 * Supporte : Google Sign-In + Email/Mot de passe
 */
class AuthRepository(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()

    // WEB_CLIENT_ID à récupérer dans firebase console > Authentication > Sign-in method > Google
    // Remplacer par ton vrai Web Client ID Firebase
    companion object {
        const val WEB_CLIENT_ID = "VOTRE_WEB_CLIENT_ID_FIREBASE"
    }

    // Etat de l'utilisateur connecté
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: Flow<FirebaseUser?> = _currentUser.asStateFlow()

    val isLoggedIn: Boolean get() = auth.currentUser != null
    val userId: String? get() = auth.currentUser?.uid
    val userEmail: String? get() = auth.currentUser?.email
    val userName: String? get() = auth.currentUser?.displayName

    // ========== GOOGLE SIGN-IN ==========

    fun getGoogleSignInIntent(): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        return googleSignInClient.signInIntent
    }

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user!!
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== EMAIL/MOT DE PASSE ==========

    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user!!
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createAccountWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== DÉCONNEXION ==========

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }

    // ========== ÉCOUTER LES CHANGEMENTS ==========

    fun observeAuthState(onChange: (FirebaseUser?) -> Unit) {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
            onChange(firebaseAuth.currentUser)
        }
    }
}
