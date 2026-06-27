package com.example.search

import android.graphics.Bitmap
import android.util.Log
import com.example.data.CusbuyPost
import com.example.location.CusbuyLocation
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "GeminiSearch"

data class SearchResult(
    val query: String = "",
    val interpretation: String = "",   // Ce que Gemini a compris
    val suggestedKeywords: List<String> = emptyList(),
    val matchingPosts: List<CusbuyPost> = emptyList(),
    val suggestedCategories: List<String> = emptyList()
)

/**
 * Recherche IA intelligente avec Gemini pour Cusbuy.
 * Comprend le langage naturel + recherche visuelle par photo.
 */
class GeminiSearchRepository {

    // Modèle Gemini via Firebase AI
    private val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(
            modelName = "gemini-2.0-flash",
            generationConfig = generationConfig {
                temperature = 0.3f
                maxOutputTokens = 500
            }
        )

    /**
     * Recherche en langage naturel.
     * Ex: "Je cherche une robe en wax rouge pour un mariage"
     *     → extrait: produit=robe, couleur=rouge, tissu=wax, occasion=mariage
     */
    suspend fun searchByText(
        userQuery: String,
        allPosts: List<CusbuyPost>,
        userLocation: CusbuyLocation? = null
    ): SearchResult = withContext(Dispatchers.IO) {
        try {
            val locationContext = if (userLocation != null)
                "L'utilisateur est à ${userLocation.city}, ${userLocation.country}."
            else ""

            val prompt = """
                Tu es un assistant de recherche e-commerce pour l'app Cusbuy.
                
                $locationContext
                
                L'utilisateur cherche: "$userQuery"
                
                Analyse cette recherche et réponds UNIQUEMENT en JSON avec ce format exact:
                {
                  "interpretation": "Ce que l'utilisateur cherche vraiment en 1 phrase",
                  "keywords": ["mot1", "mot2", "mot3", "mot4", "mot5"],
                  "categories": ["Catégorie1", "Catégorie2"],
                  "priceRange": {"min": 0, "max": 0}
                }
                
                Catégories disponibles: Mode & Vêtements, Électronique, Alimentation, 
                Maison & Décoration, Beauté & Soins, Sport, Artisanat, Enfants, Services, Autre
                
                Réponds uniquement avec le JSON, sans explications.
            """.trimIndent()

            val response = model.generateContent(prompt)
            val responseText = response.text ?: return@withContext basicTextSearch(userQuery, allPosts)

            // Parser la réponse JSON
            val jsonStart = responseText.indexOf("{")
            val jsonEnd = responseText.lastIndexOf("}") + 1
            if (jsonStart == -1 || jsonEnd == 0) return@withContext basicTextSearch(userQuery, allPosts)

            val json = responseText.substring(jsonStart, jsonEnd)

            // Extraire les keywords (parsing manuel simple)
            val keywords = extractKeywordsFromJson(json)
            val interpretation = extractValueFromJson(json, "interpretation")
            val categories = extractArrayFromJson(json, "categories")

            // Filtrer les posts avec les keywords extraits
            val matchingPosts = filterPostsByKeywords(allPosts, keywords + listOf(userQuery))

            SearchResult(
                query = userQuery,
                interpretation = interpretation,
                suggestedKeywords = keywords,
                matchingPosts = matchingPosts,
                suggestedCategories = categories
            )

        } catch (e: Exception) {
            Log.e(TAG, "Erreur Gemini search: ${e.message}")
            basicTextSearch(userQuery, allPosts)
        }
    }

    /**
     * Recherche visuelle par photo.
     * L'utilisateur prend une photo → Gemini identifie le produit.
     */
    suspend fun searchByImage(
        bitmap: Bitmap,
        allPosts: List<CusbuyPost>
    ): SearchResult = withContext(Dispatchers.IO) {
        try {
            val prompt = content {
                image(bitmap)
                text("""
                    Tu es un assistant de recherche produit pour l'app Cusbuy (e-commerce).
                    
                    Identifie le produit dans cette image et réponds en JSON:
                    {
                      "productName": "nom du produit identifié",
                      "keywords": ["mot1", "mot2", "mot3", "mot4"],
                      "categories": ["Catégorie principale"],
                      "description": "description courte du produit en 1 phrase"
                    }
                    
                    Réponds uniquement avec le JSON.
                """.trimIndent())
            }

            val response = model.generateContent(prompt)
            val responseText = response.text ?: return@withContext SearchResult(query = "image search")

            val jsonStart = responseText.indexOf("{")
            val jsonEnd = responseText.lastIndexOf("}") + 1
            if (jsonStart == -1 || jsonEnd == 0) return@withContext SearchResult(query = "image search")

            val json = responseText.substring(jsonStart, jsonEnd)
            val keywords = extractKeywordsFromJson(json)
            val productName = extractValueFromJson(json, "productName")
            val description = extractValueFromJson(json, "description")
            val categories = extractArrayFromJson(json, "categories")

            val matchingPosts = filterPostsByKeywords(allPosts, keywords + listOf(productName))

            SearchResult(
                query = "📸 $productName",
                interpretation = description,
                suggestedKeywords = keywords,
                matchingPosts = matchingPosts,
                suggestedCategories = categories
            )

        } catch (e: Exception) {
            Log.e(TAG, "Erreur Gemini image search: ${e.message}")
            SearchResult(query = "image search")
        }
    }

    /**
     * Suggestions pendant la frappe (autocomplete intelligent).
     */
    suspend fun getSearchSuggestions(partialQuery: String): List<String> {
        if (partialQuery.length < 2) return emptyList()

        return try {
            val prompt = """
                Pour l'app e-commerce Cusbuy, génère 4 suggestions de recherche pour:
                "$partialQuery"
                
                Réponds avec une liste JSON: ["suggestion1", "suggestion2", "suggestion3", "suggestion4"]
                Uniquement le JSON, sans texte autour.
            """.trimIndent()

            val response = model.generateContent(prompt)
            val text = response.text ?: return emptyList()

            val start = text.indexOf("[")
            val end = text.lastIndexOf("]") + 1
            if (start == -1 || end == 0) return emptyList()

            // Extraction simple des suggestions
            text.substring(start + 1, end - 1)
                .split(",")
                .map { it.trim().removeSurrounding("\"") }
                .filter { it.isNotEmpty() }
                .take(4)

        } catch (e: Exception) {
            Log.e(TAG, "Erreur suggestions: ${e.message}")
            emptyList()
        }
    }

    // ========== HELPERS ==========

    private fun basicTextSearch(query: String, posts: List<CusbuyPost>): SearchResult {
        val keywords = query.lowercase().split(" ").filter { it.length > 2 }
        return SearchResult(
            query = query,
            interpretation = "Recherche : $query",
            matchingPosts = filterPostsByKeywords(posts, keywords)
        )
    }

    private fun filterPostsByKeywords(posts: List<CusbuyPost>, keywords: List<String>): List<CusbuyPost> {
        if (keywords.isEmpty()) return posts
        return posts.filter { post ->
            val searchText = (
                post.description + " " +
                post.productName + " " +
                post.productDescription + " " +
                post.hashtags.joinToString(" ")
            ).lowercase()
            keywords.any { keyword -> searchText.contains(keyword.lowercase()) }
        }
    }

    private fun extractValueFromJson(json: String, key: String): String {
        val pattern = """"$key"\s*:\s*"([^"]+)"""".toRegex()
        return pattern.find(json)?.groupValues?.get(1) ?: ""
    }

    private fun extractKeywordsFromJson(json: String): List<String> {
        return extractArrayFromJson(json, "keywords")
    }

    private fun extractArrayFromJson(json: String, key: String): List<String> {
        val pattern = """"$key"\s*:\s*\[([^\]]+)\]""".toRegex()
        val match = pattern.find(json)?.groupValues?.get(1) ?: return emptyList()
        return match.split(",").map { it.trim().removeSurrounding("\"") }.filter { it.isNotEmpty() }
    }
}
