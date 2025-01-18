import easy.io.EHttpClient
import easy.util.OpenWebUI
import net.sf.json.JSONArray
import net.sf.json.JSONObject

object TestOpenWebUI {
	@JvmStatic
	fun main(args: Array<String>) {
		val client = EHttpClient()
		client.setConnectionTimeout(10000)

		val openwebui = OpenWebUI(
			"http://101.226.173.140:7432",
			"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6ImQ2OTYyNWU1LWRlNzMtNGI4ZS1hODhkLWMyZTYyNTRhMjg1MiJ9.Rmn_O-o5ZMOuhJOXUFcqKREwWp7dCwMFrAy9AG5uREg",
			client)

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
//			"temperature" to 0.1,
//			"top_p" to 0.9,
//			"max_tokens" to 1000
		                                         )
		println(result)
	}
}