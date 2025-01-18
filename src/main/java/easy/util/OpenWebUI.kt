package easy.util

import easy.io.EHttpClient
import net.sf.json.JSONArray
import net.sf.json.JSONObject

@Suppress("unused")
class OpenWebUI(private val apiurl: String, private val token: String) {
	private val client = EHttpClient()

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
		return api("/api/models")
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

		return api("/api/chat/completions", postjson)
	}

/*	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			val openwebui = OpenWebUI(
				"http://101.226.173.140:7432",
				"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6ImQ2OTYyNWU1LWRlNzMtNGI4ZS1hODhkLWMyZTYyNTRhMjg1MiJ9.Rmn_O-o5ZMOuhJOXUFcqKREwWp7dCwMFrAy9AG5uREg"
			                         )

			// 构造一条消息
			val messages = JSONArray()
			val userMessage = JSONObject()
			userMessage["role"] = "user"
			userMessage["content"] = "请详细解释什么是生命?"
			messages.add(userMessage)

			// 调用时使用可变参数，添加任意数量的键值对
			val result = openwebui.getChatCompletions(
				model = "arena-model",
				messages = messages,
				"temperature" to 0.1,
				"top_p" to 0.9,
				"max_tokens" to 1000
			)
			println(result)
		}
	}*/
}