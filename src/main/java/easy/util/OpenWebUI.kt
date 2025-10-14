package easy.util

import easy.io.EHttpClient
import org.json.JSONArray
import org.json.JSONObject

import java.io.File
import java.net.HttpURLConnection
import java.net.URI

// https://docs.openwebui.com/getting-started/api-endpoints
	@Suppress("unused")
class OpenWebUI(private val apiurl: String, private val token: String, private val client: EHttpClient = EHttpClient()) {

	private val apiBaseUrl = apiurl.trimEnd('/')
	private val rootUrl = apiBaseUrl.removeSuffix("/api")

	private fun request(func: String, postjson: JSONObject? = null, base: String = apiBaseUrl): String {
		val url = "${base}/${func.removePrefix("/")}"
		val head = mutableMapOf<String, String>()
		head["Content-Type"] = "application/json"
		head["Authorization"] = "Bearer $token"

		return if (postjson != null) {
			val posthead = mutableMapOf<String, String>()
			// 这里的键可以根据实际需要调整
			posthead[""] = postjson.toString()
			client.postToString(url, posthead, head)
		} else {
			client.get(url, head, null)
		}

	}

	@Suppress("MemberVisibilityCanBePrivate")
	fun api(func: String, postjson: JSONObject? = null): JSONObject {
		return JSONObject(request(func, postjson, apiBaseUrl))
	}

	fun uploadFile(filePath: String): JSONObject {
		val urlString = "$apiurl/v1/files/"
		val boundary = "----WebKitFormBoundary" + System.currentTimeMillis()
		val file = File(filePath)

		val connection = URI(urlString).toURL().openConnection() as HttpURLConnection
		connection.requestMethod = "POST"
		connection.doOutput = true
		connection.setRequestProperty("Authorization", "Bearer $token")
		connection.setRequestProperty("Accept", "application/json")
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

		connection.outputStream.use { output ->
			output.write(("--$boundary\r\n" +
					"Content-Disposition: form-data; name=\"file\"; filename=\"${file.name}\"\r\n" +
					"Content-Type: application/octet-stream\r\n\r\n").toByteArray())

			file.inputStream().copyTo(output)
			output.write("\r\n--$boundary--\r\n".toByteArray())
		}

		val response = connection.inputStream.bufferedReader().use { it.readText() }
		// 返回实例 {"id":"0a933594-41c0-424b-9dfc-67b2b402f9ab","user_id":"e8786b6b-f025-45be-b8ad-4017b60e0cbd","hash":null,"filename":"174.txt","data":{},"meta":{"name":"174.txt","content_type":"application/octet-stream","size":240,"data":{}},"created_at":1742182984,"updated_at":1742182984,"path":"/root/myenv/lib/python3.12/site-packages/open_webui/data/uploads/0a933594-41c0-424b-9dfc-67b2b402f9ab_174.txt","access_control":null,"error":"'NoneType' object has no attribute 'encode'"}
		return JSONObject(response)
	}

	fun getModels(): JSONObject {
		return api("/models")
	}

	/**
	 * 使用 vararg (可变参数) 来支持自定义参数，如 temperature, top_p 等。
	 *
	 * @param model    指定要调用的模型名称
	 * @param messages 聊天消息的 JSONArray
	 * @param params   可变参数形式的键值对，如 ("temperature" to 0.7)
	 */
	fun getChatCompletions(
		model: String,
		messages: JSONArray,
		vararg params: Pair<String, Any?>
	                      ): JSONObject {

		val postjson = JSONObject()
//		postjson["model"] = model
		postjson.put("model", model)
//		postjson["messages"] = messages
		postjson.put("messages", messages)

		// 将可变参数传入的多个 (key,value) 写入 postjson
		for ((key, value) in params) {
//			postjson[key] = value
			postjson.put(key, value)
		}

		return api("/chat/completions", postjson)
	}

	/**
	 * 调用 /ollama/api/generate 接口，返回包含响应片段、汇总文本、思考信息等的 JSON。
	 *
	 * @param model  指定模型
	 * @param prompt 提示词内容
	 * @param params 可选扩展参数，如 ("options" to JSONObject(...))
	 */
	fun generateFromOllama(
		model: String,
		prompt: String,
		vararg params: Pair<String, Any?>
	                      ): JSONObject {
		val postjson = JSONObject()
		postjson.put("model", model)
		postjson.put("prompt", prompt)

		val hasStreamParam = params.any { it.first == "stream" }
		if (!hasStreamParam) {
			postjson.put("stream", false)
		}

		for ((key, value) in params) {
			postjson.put(key, value)
		}

		val responseText = request("ollama/api/generate", postjson, rootUrl)
			val responseBuilder = StringBuilder()
			val thinkingBuilder = StringBuilder()
			val meta = JSONObject()
			var modelName: String? = null
			var done = false
		var doneReason: String? = null

			responseText.lineSequence()
				.map { it.trim() }
				.filter { it.isNotEmpty() }
				.forEach { line ->
					try {
						val chunk = JSONObject(line)

						if (chunk.has("error")) {
							val errorMessage = chunk.optString("error")
							Log.OutLog("OpenWebUI.generateFromOllama error: $errorMessage")
							throw RuntimeException(errorMessage)
						}

						if (chunk.has("model")) {
							val chunkModel = chunk.optString("model")
							if (chunkModel.isNotEmpty()) {
								modelName = chunkModel
							}
						}

						val responsePart = chunk.optString("response")
						if (responsePart.isNotEmpty()) {
							responseBuilder.append(responsePart)
						}

						val thinkingPart = chunk.optString("thinking")
						if (thinkingPart.isNotBlank()) {
							thinkingBuilder.append(thinkingPart)
						}

						if (chunk.has("done") && chunk.optBoolean("done")) {
							done = true
						}

						if (chunk.has("done_reason")) {
							val chunkDoneReason = chunk.optString("done_reason")
							if (chunkDoneReason.isNotEmpty()) {
								doneReason = chunkDoneReason
							}
						}

						val skipKeys = setOf("response", "thinking", "model", "done", "done_reason")
						val keyIterator = chunk.keys()
						while (keyIterator.hasNext()) {
							val key = keyIterator.next()
							if (!skipKeys.contains(key)) {
								meta.put(key, chunk.get(key))
							}
						}
					} catch (e: Throwable) {
						Log.OutLog("OpenWebUI.generateFromOllama parse error:[${line}]")
						throw e
					}
				}

		val result = JSONObject()
		if (modelName != null) {
			result.put("model", modelName)
		}

		result.put("response", responseBuilder.toString())
		if (thinkingBuilder.isNotEmpty()) {
			result.put("thinking", thinkingBuilder.toString())
		}
		result.put("done", done)
		if (doneReason != null) {
			result.put("done_reason", doneReason)
			}
			if (meta.length() > 0) {
				result.put("meta", meta)
			}

			return result
		}

	companion object{
		@JvmStatic
		fun getContent(jsonObject: JSONObject):String{
			return try {
				jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
			}
			catch (e:Throwable)
			{
				Log.OutLog("OpenWebUI.getContent Error:[${jsonObject}]")
				throw e
			}
		}
	}
}
