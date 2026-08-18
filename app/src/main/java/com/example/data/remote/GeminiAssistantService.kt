package com.example.data.remote

import com.example.BuildConfig
import com.example.data.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAssistantRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        return try {
            val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
            field.get(null) as? String ?: ""
        } catch (e: Exception) {
            try {
                val field = BuildConfig::class.java.getField("API_KEY")
                field.get(null) as? String ?: ""
            } catch (ex: Exception) {
                ""
            }
        }
    }

    suspend fun queryAssistant(
        userQuery: String,
        availableProducts: List<Product>,
        cartProducts: List<String>,
        clippedCoupons: List<String>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext generateOfflineAssistantFallback(userQuery, availableProducts)
        }

        val systemPrompt = """
            You are the Albertsons AI Assistant.
            You help customers with recipe planning, for U® deals, meal budget calculations, locating store aisles, DriveUp & Go™ orders, and organizing weekly groceries.
            
            Context of Current Store Catalogue Sample:
            ${availableProducts.take(25).joinToString("\n") { "- ${it.name} (${it.brand}): $${it.price} [${it.aisle}] - Deal: ${it.dealTag ?: "None"}" }}
            
            Rules:
            1. Keep responses clear, helpful, and formatted with bullet points where appropriate.
            2. If the user asks for meal plans or taco ingredients, give exact item recommendations and cost estimates based on catalog.
            3. Highlight Albertsons brands (O Organics®, Signature SELECT®, Lucerne®, Open Nature®, Primo Taglio®) and for U® savings when relevant.
            4. Be friendly and customer-centric.
        """.trimIndent()

        try {
            // Build Gemini REST request JSON via org.json.JSONObject
            val requestJson = JSONObject().apply {
                val contentsArr = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArr = JSONArray().apply {
                            put(JSONObject().put("text", userQuery))
                        }
                        put("parts", partsArr)
                    }
                    put(contentObj)
                }
                put("contents", contentsArr)

                val sysInstructionObj = JSONObject().apply {
                    val partsArr = JSONArray().apply {
                        put(JSONObject().put("text", systemPrompt))
                    }
                    put("parts", partsArr)
                }
                put("systemInstruction", sysInstructionObj)
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

            val httpRequest = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(httpRequest).execute()
            val responseString = response.body?.string() ?: ""

            if (response.isSuccessful && responseString.isNotBlank()) {
                val rootObj = JSONObject(responseString)
                val candidates = rootObj.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCand = candidates.getJSONObject(0)
                    val content = firstCand.optJSONObject("content")
                    if (content != null) {
                        val parts = content.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val text = parts.getJSONObject(0).optString("text")
                            if (text.isNotBlank()) return@withContext text
                        }
                    }
                }
            }
            generateOfflineAssistantFallback(userQuery, availableProducts)
        } catch (e: Exception) {
            generateOfflineAssistantFallback(userQuery, availableProducts)
        }
    }

    private fun generateOfflineAssistantFallback(query: String, products: List<Product>): String {
        val q = query.lowercase()
        return when {
            "taco" in q || "tacos" in q -> {
                "🌮 **Albertsons Taco Night Bundle (~$15.80):**\n" +
                "• Signature SELECT® Ground Beef (80/20) - $5.49/lb [Aisle 5]\n" +
                "• O Organics® Yellow Onions & Cilantro - $1.49 [Produce]\n" +
                "• Signature SELECT® Taco Shells 12ct - $2.49 [Aisle 6]\n" +
                "• Lucerne® Shredded Mexican Blend Cheese 8oz - $2.79 [Aisle 4]\n" +
                "• Signature SELECT® Mild Salsa 16oz - $2.99 [Aisle 6]\n\n" +
                "💡 *Tip: Clip your $1.00 off Lucerne® Cheese for U® coupon in Deals!*"
            }
            "cheap" in q || "budget" in q || "dinners" in q || "plan" in q -> {
                "💡 **Albertsons Smart Budget Dinner Plan (Under $25 for 4):**\n" +
                "1. **Spaghetti & Beef Marinara** ($8.20 using Signature SELECT®)\n" +
                "2. **Signature SELECT® Rotisserie Chicken & Roasted Potatoes** ($9.99)\n" +
                "3. **Vegetable Stir Fry Rice** ($5.50)\n\n" +
                "You have 3 clipped for U® coupons matching these items! Would you like me to add these ingredients to your Shopping List?"
            }
            "milk" in q -> {
                "🥛 **Lucerne® & O Organics® Milk Location:**\n" +
                "Whole Milk and Organic 2% Milk are in **Aisle 4 (Dairy)**.\n" +
                "• Lucerne® Whole Milk 1 gal: **$3.99** (for U® Member Price $3.49)"
            }
            "protein" in q || "snack" in q -> {
                "💪 **High Protein Snacks on Sale:**\n" +
                "• Chobani Greek Yogurt 5.3oz - $1.25 (Buy 4 Get 1 Free for U®)\n" +
                "• Signature SELECT® Beef Jerky 3.25oz - $5.49\n" +
                "• Lucerne® Cottage Cheese 16oz - $2.49"
            }
            else -> {
                "👋 I'm your Albertsons Assistant! I can help you locate items in store, find for U® coupons, estimate recipe costs, or coordinate DriveUp & Go™. Try asking: *'What do I need for tacos?'* or *'Where is Lucerne milk?'*"
            }
        }
    }
}
