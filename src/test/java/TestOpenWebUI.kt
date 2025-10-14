import easy.io.EHttpClient
import easy.util.OpenWebUI
import org.json.JSONArray
import org.json.JSONObject


object TestOpenWebUI {
	@JvmStatic
	fun main(args: Array<String>) {
		val client = EHttpClient()
		client.setConnectionTimeout(180000)

		val openwebui = OpenWebUI(
			"__OPENWEBUI_API_URL__",
			"__REDACTED_OPENWEBUI_TOKEN__",
			client)

		val messages = JSONArray()
		val userMessage = JSONObject()
		userMessage.put("role", "user")
		userMessage.put("content", "你可以做什么")
		messages.put(userMessage)

		val completion = openwebui.getChatCompletionsFromOllama(
			model = "gpt-oss:20b-cloud",
			messages = messages
		                                                 )
		println(completion.toString(2))
	}
}
