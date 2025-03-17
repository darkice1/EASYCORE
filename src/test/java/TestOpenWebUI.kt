import easy.io.EHttpClient
import easy.util.OpenWebUI
import net.sf.json.JSONArray
import net.sf.json.JSONObject

object TestOpenWebUI {
	@JvmStatic
	fun main(args: Array<String>) {
		val client = EHttpClient()
		client.setConnectionTimeout(180000)

		val openwebui = OpenWebUI(
			"__OPENWEBUI_API_URL__",
			"__REDACTED_OPENWEBUI_TOKEN__",
			client)

		// 构造一条消息
		println(openwebui.getModels())
		val messages = JSONArray()
		val userMessage = JSONObject()
		userMessage["role"] = "user"
		userMessage["content"] = "请详细解释什么是生命?"
		messages.add(userMessage)

		// 调用时使用可变参数，添加任意数量的键值对
//		val result = openwebui.getChatCompletions(
//			model = "gpt-4o",
//			messages = messages,
////			"temperature" to 0.1,
////			"top_p" to 0.9,
////			"max_tokens" to 1000
//		                                         )
//		println(result)
		println(openwebui.uploadFile("/Users/Neo/Desktop/174.txt"))
	}
}