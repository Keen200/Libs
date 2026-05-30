package com.example.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    
    // OkHttpClient with generous timeouts as required by the Gemini API design guidelines
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val SYSTEM_INSTRUCTION = """You are an expert AutoCAD AutoLISP and macro developer. Your role is to convert natural language descriptions (written in Arabic, English, or any other language) into high-quality, clean, well-commented, and ready-to-run AutoLISP (.lsp) code.

Rules:
1. ALWAYS generate the main AutoLISP code inside a Markdown code block (e.g. ```lisp ... ```).
2. Define drawing or modification commands in the 'c:NAME' format (e.g. (defun c:DrawSquare ...)) so they can be run directly from the AutoCAD command bar.
3. Write clear comments in the script (matching the user's language, such as Arabic or English) explaining how the lines function.
4. Keep the code straightforward, standard, and compatible with modern AutoCAD versions.
5. Provide a short description (2-3 lines) below the code block in the user's language explaining:
   - What the command name is (e.g. DrawSquare).
   - Any brief tip on how to paste and load it.
6. Do NOT include annoying greeting texts or redundant introductions before the code block. Start writing the code block directly."""

    /**
     * Call generative model to convert CAD descriptions into AutoLISP
     */
    suspend fun generateAutoLisp(prompt: String): GenerateResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key is not configured.")
            return@withContext GenerateResult.Error("API Key is not configured. Please use the Secrets Panel in AI Studio to add your GEMINI_API_KEY.")
        }

        // We use gemini-3.5-flash as default, very competent and extremely fast.
        val model = "gemini-3.5-flash"
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        // Build request body manually for reliable parsing & speed
        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", SYSTEM_INSTRUCTION)
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.5) // Lower temp for precise code generation
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = requestJson.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBodyStr = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val errMsg = try {
                        JSONObject(responseBodyStr).getJSONObject("error").getString("message")
                    } catch (e: Exception) {
                        "Server returned code ${response.code}"
                    }
                    Log.e(TAG, "Request failed: $errMsg")
                    return@withContext GenerateResult.Error(errMsg)
                }

                if (responseBodyStr.trim().isEmpty()) {
                    return@withContext GenerateResult.Error("Received empty response from server.")
                }

                val jsonResponse = JSONObject(responseBodyStr)
                val candidatesArray = jsonResponse.optJSONArray("candidates")
                if (candidatesArray == null || candidatesArray.length() == 0) {
                    return@withContext GenerateResult.Error("No code generated. Please try a different prompt.")
                }

                val firstCandidate = candidatesArray.getJSONObject(0)
                val responseContent = firstCandidate.optJSONObject("content")
                if (responseContent == null) {
                    return@withContext GenerateResult.Error("Response structure is missing content key.")
                }

                val partsArray = responseContent.optJSONArray("parts")
                if (partsArray == null || partsArray.length() == 0) {
                    return@withContext GenerateResult.Error("Response structure is missing parts key.")
                }

                val text = partsArray.getJSONObject(0).optString("text", "")
                if (text.trim().isEmpty()) {
                    return@withContext GenerateResult.Error("Generated script is empty.")
                }

                // Match and extract code block and description
                val extracted = parseResponse(text)
                GenerateResult.Success(
                    code = extracted.code,
                    description = extracted.explanation,
                    fullResponse = text
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in generateAutoLisp: ${e.localizedMessage}", e)
            GenerateResult.Error(e.localizedMessage ?: "Check your internet connection.")
        }
    }

    private fun parseResponse(rawText: String): ExtractedContent {
        val lispRegex = "```(?:lisp|cl|lsp)?\\s*([\\s\\S]*?)\\s*```".toRegex(RegexOption.IGNORE_CASE)
        val matchResult = lispRegex.find(rawText)

        return if (matchResult != null) {
            val code = matchResult.groupValues[1].trim()
            val explanation = rawText.replace(matchResult.value, "").trim()
            ExtractedContent(code, explanation)
        } else {
            // Fallback if no markdown block, assume text contains code itself
            ExtractedContent(rawText.trim(), "")
        }
    }

    data class ExtractedContent(val code: String, val explanation: String)
}

sealed interface GenerateResult {
    data class Success(val code: String, val description: String, val fullResponse: String) : GenerateResult
    data class Error(val message: String) : GenerateResult
}
