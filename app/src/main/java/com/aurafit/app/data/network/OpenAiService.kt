package com.aurafit.app.data.network

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.Response

interface OpenAiService {
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: ChatCompletionRequest
    ): Response<ChatCompletionResponse>

    @POST("v1/chat/completions")
    suspend fun getVisionCompletion(
        @Header("Authorization") authorization: String,
        @Body request: VisionChatCompletionRequest
    ): Response<ChatCompletionResponse>
}

// Text Chat Completion Request
data class ChatCompletionRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<Message>,
    val temperature: Double = 0.2
)

data class Message(
    val role: String,
    val content: String
)

// Vision Chat Completion Request
data class VisionChatCompletionRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<VisionMessage>,
    val max_tokens: Int = 1000,
    val temperature: Double = 0.2
)

data class VisionMessage(
    val role: String,
    val content: List<VisionContentPart>
)

data class VisionContentPart(
    val type: String, // "text" or "image_url"
    val text: String? = null,
    val image_url: ImageUrlObject? = null
)

data class ImageUrlObject(
    val url: String // data:image/jpeg;base64,{base64_data}
)

// Response from OpenAI
data class ChatCompletionResponse(
    val id: String? = null,
    val choices: List<Choice>? = null
)

data class Choice(
    val index: Int? = null,
    val message: ResponseMessage? = null
)

data class ResponseMessage(
    val role: String? = null,
    val content: String? = null
)

// Target Parsed JSON returned by OpenAI models
data class ParsedFoodItem(
    val foodName: String,
    val portion: String = "1 serving",
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val description: String = ""
)

data class ParsedNutritionResponse(
    val items: List<ParsedFoodItem>,
    val totals: ParsedTotals,
    val summary: String = ""
)

data class ParsedTotals(
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int
)
