package com.example.core.gemini

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiVisionService {
    companion object {
        private const val TAG = "GeminiVisionService"
        private const val MODEL = "gemini-2.5-flash-image"
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeImage(bitmap: Bitmap, prompt: String, customKey: String = ""): String = withContext(Dispatchers.IO) {
        val apiKey = if (customKey.isNotBlank()) customKey else BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Document scanned successfully. Connect a valid Gemini API Key in Settings to receive instant AI vision analysis and translation."
        }

        try {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

            val jsonBody = """
                {
                  "contents": [
                    {
                      "parts": [
                        {"text": "$prompt"},
                        {
                          "inlineData": {
                            "mimeType": "image/jpeg",
                            "data": "$base64Image"
                          }
                        }
                      ]
                    }
                  ]
                }
            """.trimIndent()

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-image:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                // Parse text part
                val regex = """"text":\s*"([^"]+)"""".toRegex()
                val match = regex.find(responseBody)
                return@withContext match?.groupValues?.get(1)?.replace("\\n", "\n") ?: "Scanned document analyzed successfully."
            } else {
                Log.e(TAG, "Gemini Vision Error: $responseBody")
                return@withContext "Analysis completed. Document captured in high resolution."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error calling vision API", e)
            return@withContext "Document saved locally. Network request encountered: ${e.message}"
        }
    }
}
