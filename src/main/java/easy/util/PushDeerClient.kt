package easy.util

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration

/**
 * PushDeer 推送工具（不包含 pushKey，调用方自行传入）。
 *
 * 支持官方接口参数：
 * - pushkey：单个或逗号分隔多个 key
 * - text：主要内容（图片模式下为图片 URL）
 * - desp：可选，附加内容
 * - type：text / markdown / image，默认 markdown
 */
object PushDeerClient {
    private const val DEFAULT_ENDPOINT = "https://api2.pushdeer.com/message/push"
    private val defaultHttpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    private val defaultExecutor = HttpRequestExecutor { request ->
        val response = defaultHttpClient.send(request, HttpResponse.BodyHandlers.ofString())
        PushDeerResponse(response.statusCode(), response.body())
    }

    /**
     * 发送单个 pushKey 的文本/Markdown/图片推送。
     */
    @JvmStatic
    @JvmOverloads
    fun pushText(
        text: String,
        pushKey: String,
        desp: String? = null,
        type: MessageType = MessageType.MARKDOWN,
        maxRetries: Int = 1,
        retryDelay: Duration = Duration.ofMillis(50),
        endpoint: String = DEFAULT_ENDPOINT,
        executor: HttpRequestExecutor = defaultExecutor
    ): Boolean = pushMessage(
        text = text,
        pushKeys = listOf(pushKey),
        desp = desp,
        type = type,
        maxRetries = maxRetries,
        retryDelay = retryDelay,
        endpoint = endpoint,
        executor = executor
    )

    /**
     * 支持多个 pushKey 的推送入口。
     */
    @JvmStatic
    @JvmOverloads
    fun pushMessage(
        text: String,
        pushKeys: Collection<String>,
        desp: String? = null,
        type: MessageType = MessageType.MARKDOWN,
        maxRetries: Int = 5,
        retryDelay: Duration = Duration.ofMillis(100),
        endpoint: String = DEFAULT_ENDPOINT,
        executor: HttpRequestExecutor = defaultExecutor
    ): Boolean {
        val normalizedKeys = pushKeys.map { it.trim() }.filter { it.isNotEmpty() }
        require(normalizedKeys.isNotEmpty()) { "pushKeys 不能为空" }
        require(text.isNotEmpty()) { "text 不能为空" }
        require(maxRetries > 0) { "maxRetries 必须大于 0" }
        require(!retryDelay.isNegative) { "retryDelay 不能为负数" }

        val builtRequest = buildRequest(
            text = text,
            pushKeys = normalizedKeys,
            desp = desp,
            type = type,
            endpoint = endpoint
        )
        var lastError: Exception? = null

        repeat(maxRetries) { attempt ->
            try {
                val response = executor.execute(builtRequest.httpRequest)
                if (response.statusCode in 200..299) {
                    if (isPushDeerSuccess(response.body)) {
                        return true
                    }
                    lastError = IllegalStateException("PushDeer 返回内容未包含成功标记：${response.body}")
                } else {
                    lastError = IllegalStateException("PushDeer 返回状态码 ${response.statusCode}")
                }
            } catch (ex: IOException) {
                lastError = ex
            } catch (ex: InterruptedException) {
                Thread.currentThread().interrupt()
                throw ex
            }

            if (attempt < maxRetries - 1 && !retryDelay.isZero) {
                Thread.sleep(retryDelay.toMillis())
            }
        }

        lastError?.let { Log.OutException(it, "PushDeer 发送失败，已重试 $maxRetries 次。") }
        return false
    }

    private fun isPushDeerSuccess(body: String): Boolean {
        try {
            val root = JSONObject(body)
            if (root.has("code")) {
	            val codeValue = when (val code = root.opt("code")) {
                    is Number -> code.toInt()
                    is String -> code.toIntOrNull()
                    else -> null
                }
                if (codeValue == 0) {
                    return true
                }
                if (codeValue != null) {
                    return false
                }
            }

            extractResultArray(root.opt("content"))?.let { results ->
                if (containsPushSuccess(results)) {
                    return true
                }
            }
            extractResultArray(root.opt("result"))?.let { results ->
                if (containsPushSuccess(results)) {
                    return true
                }
            }
        } catch (_: JSONException) {
            return false
        }
        return false
    }

    private fun extractResultArray(node: Any?): JSONArray? = when (node) {
        is JSONObject -> node.optJSONArray("result")
        is JSONArray -> node
        else -> null
    }

    private fun containsPushSuccess(results: JSONArray): Boolean {
        for (index in 0 until results.length()) {
	        val asObject = when (val element = results.opt(index)) {
                is JSONObject -> element
                is String -> try {
                    JSONObject(element)
                } catch (_: JSONException) {
                    null
                }
                else -> null
            } ?: continue

            if (asObject.optString("success").equals("ok", ignoreCase = true)) {
                return true
            }
            val counts = asObject.optInt("counts", -1)
            if (counts > 0 && !asObject.has("success")) {
                return true
            }
        }
        return false
    }

    internal fun buildRequest(
        text: String,
        pushKeys: Collection<String>,
        desp: String?,
        type: MessageType,
        endpoint: String
    ): BuiltRequest {
        val normalizedKeys = pushKeys.map { it.trim() }.filter { it.isNotEmpty() }
        require(normalizedKeys.isNotEmpty()) { "pushKeys 不能为空" }

        val form = linkedMapOf(
            "pushkey" to normalizedKeys.joinToString(","),
            "text" to text,
            "desp" to desp,
            "type" to type.apiValue
        ).filterValues { it != null }

        val encodedBody = form.entries.joinToString("&") { (key, value) ->
            val encoded = URLEncoder.encode(value, StandardCharsets.UTF_8)
            "$key=$encoded"
        }

        val uri = URI.create(endpoint)
        val request = HttpRequest.newBuilder(uri)
            .timeout(Duration.ofSeconds(10))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(encodedBody))
            .build()

        return BuiltRequest(request, encodedBody)
    }

    fun interface HttpRequestExecutor {
        @Throws(IOException::class, InterruptedException::class)
        fun execute(request: HttpRequest): PushDeerResponse
    }

    data class PushDeerResponse(val statusCode: Int, val body: String)

    internal data class BuiltRequest(val httpRequest: HttpRequest, val encodedBody: String)

    enum class MessageType(val apiValue: String) {
        TEXT("text"),
        MARKDOWN("markdown"),
        @Suppress("unused")
        IMAGE("image")
    }
}
