package easy.util

import easy.io.EHttpClient
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URI

// https://docs.openwebui.com/getting-started/api-endpoints
@Suppress("unused")
class OpenWebUI(private val apiurl: String, private val token: String, private val client: EHttpClient = EHttpClient()) {

	@Suppress("MemberVisibilityCanBePrivate")
	fun api(func: String, postjson: JSONObject? = null): JSONObject {
		val url = "$apiurl/$func"
		val head = mutableMapOf<String, String>()
		head["Content-Type"] = "application/json"
		head["Authorization"] = "Bearer $token"

		val response = if (postjson != null) {
			val posthead = mutableMapOf<String, String>()
			// 这里的键可以根据实际需要调整
			posthead[""] = postjson.toString()
			client.postToString(url, posthead, head)
		} else {
			client.get(url, head, null)
		}

		return JSONObject.fromObject(response)
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
		return JSONObject.fromObject(response)
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
		postjson["model"] = model
		postjson["messages"] = messages

		// 将可变参数传入的多个 (key,value) 写入 postjson
		for ((key, value) in params) {
			postjson[key] = value
		}

		return api("/chat/completions", postjson)
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