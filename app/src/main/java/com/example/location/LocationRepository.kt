package com.example.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.math.*

private const val TAG = "LocationRepo"

data class CusbuyLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val city: String = "",
    val country: String = "",
    val geoPoint: GeoPoint = GeoPoint(0.0, 0.0)
)

/**
 * Gestion de la géolocalisation pour l'algorithme de feed Cusbuy.
 * Priorise les vendeurs proches de l'utilisateur.
 */
class LocationRepository(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val geocoder = Geocoder(context, Locale.getDefault())

    private val _userLocation = MutableStateFlow<CusbuyLocation?>(null)
    val userLocation: StateFlow<CusbuyLocation?> = _userLocation

    /**
     * Récupère la position actuelle une seule fois.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): CusbuyLocation? {
        return suspendCancellableCoroutine { continuation ->
            val request = CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

            fusedLocationClient.getCurrentLocation(request, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val cusbuyLocation = resolveLocation(location.latitude, location.longitude)
                        _userLocation.value = cusbuyLocation
                        continuation.resume(cusbuyLocation)
                    } else {
                        continuation.resume(null)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Erreur localisation: ${e.message}")
                    continuation.resume(null)
                }
        }
    }

    /**
     * Écoute la position en temps réel (pour mise à jour du feed).
     */
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(onUpdate: (CusbuyLocation) -> Unit) {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            60_000L // Toutes les 60 secondes
        ).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    val cusbuyLocation = resolveLocation(location.latitude, location.longitude)
                    _userLocation.value = cusbuyLocation
                    onUpdate(cusbuyLocation)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        )
    }

    /**
     * Convertit les coordonnées GPS en ville/pays (Geocoding).
     */
    private fun resolveLocation(lat: Double, lng: Double): CusbuyLocation {
        return try {
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            val address = addresses?.firstOrNull()

            CusbuyLocation(
                latitude = lat,
                longitude = lng,
                city = address?.locality ?: address?.subAdminArea ?: "",
                country = address?.countryName ?: "",
                geoPoint = GeoPoint(lat, lng)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Erreur geocoding: ${e.message}")
            CusbuyLocation(latitude = lat, longitude = lng, geoPoint = GeoPoint(lat, lng))
        }
    }

    /**
     * Algorithme géolocalisé de Cusbuy.
     * Trie les posts par priorité géographique.
     *
     * Logique :
     * 1. Même ville      → priorité 1 (score +100)
     * 2. Même pays       → priorité 2 (score +60)
     * 3. Même continent  → priorité 3 (score +30)
     * 4. International   → score de base
     */
    fun <T> sortByProximity(
        items: List<T>,
        userLocation: CusbuyLocation,
        getItemCity: (T) -> String,
        getItemCountry: (T) -> String,
        getItemLocation: (T) -> GeoPoint?
    ): List<T> {
        return items.sortedByDescending { item ->
            val itemCity = getItemCity(item)
            val itemCountry = getItemCountry(item)
            val itemGeoPoint = getItemLocation(item)

            var score = 0.0

            // Bonus même ville
            if (itemCity.isNotEmpty() && itemCity.equals(userLocation.city, ignoreCase = true)) {
                score += 100.0
            }
            // Bonus même pays
            else if (itemCountry.isNotEmpty() && itemCountry.equals(userLocation.country, ignoreCase = true)) {
                score += 60.0
            }

            // Score basé sur la distance GPS (si disponible)
            if (itemGeoPoint != null && userLocation.latitude != 0.0) {
                val distance = calculateDistance(
                    userLocation.latitude, userLocation.longitude,
                    itemGeoPoint.latitude, itemGeoPoint.longitude
                )
                // Plus proche = score plus élevé (max 40 points bonus)
                score += (40.0 * (1.0 - minOf(distance / 5000.0, 1.0)))
            }

            score
        }
    }

    /**
     * Calcule la distance en km entre deux points GPS (formule Haversine).
     */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        return earthRadius * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    /**
     * Filtre les posts dans un rayon donné (en km).
     */
    fun <T> filterByRadius(
        items: List<T>,
        userLocation: CusbuyLocation,
        radiusKm: Double,
        getItemLocation: (T) -> GeoPoint?
    ): List<T> {
        if (userLocation.latitude == 0.0) return items
        return items.filter { item ->
            val geoPoint = getItemLocation(item) ?: return@filter false
            val distance = calculateDistance(
                userLocation.latitude, userLocation.longitude,
                geoPoint.latitude, geoPoint.longitude
            )
            distance <= radiusKm
        }
    }
}
