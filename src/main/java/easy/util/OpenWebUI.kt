package easy.util

import easy.io.EHttpClient
import net.sf.json.JSONArray
import net.sf.json.JSONObject

@Suppress("unused")
class OpenWebUI(private val apiurl: String, private val token: String,private val client:EHttpClient=EHttpClient()) {

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