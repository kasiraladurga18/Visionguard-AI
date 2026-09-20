package com.example.data.remote.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class SupabaseUser(
    val id: String,
    val email: String,
    val fullName: String,
    val accessToken: String? = null
)

sealed class AuthResult {
    data class Success(val user: SupabaseUser, val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class SupabaseAuthService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
) {
    companion object {
        const val SUPABASE_URL = "https://hefwtrdgpsyxueatpbfz.supabase.co"
        const val PROJECT_ID = "hefwtrdgpsyxueatpbfz"
        const val PUBLISHABLE_KEY = "sb_publishable_Nuw2dtpMUU0RAR_ggTYzTw_G4csZeg0"
        const val ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhlZnd0cmRncHN5eHVlYXRwYmZ6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODg2ODYzNjgsImV4cCI6MjEwNDI2MjM2OH0.sMpKsD4LGBq0yrVAYTc9Hl_44nutyQKc9rL73cAiQqw"
        const val SERVICE_ROLE_SECRET = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhlZnd0cmRncHN5eHVlYXRwYmZ6Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc4ODY4NjM2OCwiZXhwIjoyMTA0MjYyMzY4fQ.rLs5Xgn7NECGF2PJpgNblGofJ0hc92P9avXlH_WGAtM"
        
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }

    /**
     * Signs in an existing user with email and password via Supabase GoTrue token endpoint.
     */
    suspend fun signIn(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            val jsonBody = JSONObject().apply {
                put("email", email.trim())
                put("password", password)
            }.toString()

            val request = Request.Builder()
                .url("$SUPABASE_URL/auth/v1/token?grant_type=password")
                .header("apikey", ANON_KEY)
                .header("Authorization", "Bearer $ANON_KEY")
                .header("Content-Type", "application/json")
                .post(jsonBody.toRequestBody(JSON_MEDIA_TYPE))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val json = JSONObject(responseBody)
                val accessToken = json.optString("access_token")
                val userObj = json.optJSONObject("user")
                val id = userObj?.optString("id") ?: ""
                val userEmail = userObj?.optString("email") ?: email
                val metadata = userObj?.optJSONObject("user_metadata")
                val fullName = metadata?.optString("full_name")?.ifBlank { null }
                    ?: userEmail.substringBefore("@").replace(".", " ").capitalizeWords()

                AuthResult.Success(
                    user = SupabaseUser(
                        id = id,
                        email = userEmail,
                        fullName = fullName,
                        accessToken = accessToken
                    ),
                    message = "Welcome back, $fullName!"
                )
            } else {
                val errorMsg = parseErrorMessage(responseBody, "Login failed (${response.code})")
                AuthResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Network error connecting to Supabase")
        }
    }

    /**
     * Registers a new user with Supabase.
     * Uses the provided service role secret to auto-confirm the email so users can access immediately.
     */
    suspend fun signUp(fullName: String, email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            val signupJson = JSONObject().apply {
                put("email", email.trim())
                put("password", password)
                put("data", JSONObject().apply {
                    put("full_name", fullName.trim())
                })
            }.toString()

            val signupRequest = Request.Builder()
                .url("$SUPABASE_URL/auth/v1/signup")
                .header("apikey", ANON_KEY)
                .header("Authorization", "Bearer $ANON_KEY")
                .header("Content-Type", "application/json")
                .post(signupJson.toRequestBody(JSON_MEDIA_TYPE))
                .build()

            val signupResponse = client.newCall(signupRequest).execute()
            val signupBody = signupResponse.body?.string() ?: ""

            if (!signupResponse.isSuccessful) {
                val errorMsg = parseErrorMessage(signupBody, "Registration failed (${signupResponse.code})")
                return@withContext AuthResult.Error(errorMsg)
            }

            val userObj = JSONObject(signupBody)
            val userId = userObj.optString("id")
            val userEmail = userObj.optString("email", email)

            // Auto-confirm user via Admin API using provided service role key
            if (userId.isNotBlank()) {
                try {
                    val confirmBody = JSONObject().apply {
                        put("email_confirm", true)
                    }.toString()

                    val confirmRequest = Request.Builder()
                        .url("$SUPABASE_URL/auth/v1/admin/users/$userId")
                        .header("apikey", SERVICE_ROLE_SECRET)
                        .header("Authorization", "Bearer $SERVICE_ROLE_SECRET")
                        .header("Content-Type", "application/json")
                        .put(confirmBody.toRequestBody(JSON_MEDIA_TYPE))
                        .build()

                    client.newCall(confirmRequest).execute().close()
                } catch (_: Exception) {
                    // Non-fatal if confirmation endpoint fails
                }
            }

            // Automatically sign in to obtain access token
            val loginResult = signIn(email, password)
            if (loginResult is AuthResult.Success) {
                AuthResult.Success(
                    user = loginResult.user.copy(fullName = fullName.trim()),
                    message = "Account created and verified! Welcome, ${fullName.trim()}."
                )
            } else {
                AuthResult.Success(
                    user = SupabaseUser(
                        id = userId,
                        email = userEmail,
                        fullName = fullName.trim()
                    ),
                    message = "Account created successfully for $userEmail!"
                )
            }
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Failed to connect to Supabase auth service")
        }
    }

    private fun parseErrorMessage(responseBody: String, fallback: String): String {
        return try {
            val json = JSONObject(responseBody)
            when {
                json.has("msg") -> json.getString("msg")
                json.has("message") -> json.getString("message")
                json.has("error_description") -> json.getString("error_description")
                else -> fallback
            }
        } catch (_: Exception) {
            fallback
        }
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}
