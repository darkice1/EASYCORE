package easy

import easy.util.Format

/**
 * @author starneo@gmail.com Mar 22, 2019
 */
object Test {

	@JvmStatic
	fun main(args: Array<String>) {
		val src = "{\"jyos\":2,\"deal\":\"\",\"link\":\"\",\"pid\":\"VUT7ZDORPXX5J3LS5536\",\"mtm5k\":\"7210733b7e832f78d64e7b54c505814d\",\"ua\":\"Mozilla/5.0 (iPhone; CPU iPhone OS 15_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148\",\"type\":\"N\",\"sid\":\"VG9ETVC6EUTNCO3FCNRT\",\"ds\":\"640x960\",\"repkg\":\"\",\"jymgd\":\"\",\"uk\":\"03F51BD9-0550-4AF2-8EAF-C1B40B586E80\",\"jyod\":\"\",\"cat\":\"363\",\"jymc\":\"\",\"admaxprice\":2400000,\"model\":\"iPhone11%25252525252C8\",\"jyime\":\"\",\"brand\":\"Apple\",\"jygd\":\"M\",\"jyage\":3,\"lat\":34.876675,\"islongt\":\"N\",\"ukt\":\"IDFA\",\"jynet\":1,\"c\":\"\",\"lng\":113.620819,\"ms\":\"640x960\",\"jyand\":\"\",\"ip\":\"219.156.20.3\",\"jtype\":\"nurl\",\"jyosv\":\"15.5\",\"adver_name\":\"大航海RTA2\",\"dhh_tagid\":\"1503573\",\"size\":\"5010\",\"appid\":\"com.jiaxiao.driveAPP\",\"jyumd5\":\"d3726d130551822cfaf92df5194e54d0\",\"jyaccttype\":\"M\",\"jytm\":1659413878374,\"jyifa\":\"03F51BD9-0550-4AF2-8EAF-C1B40B586E80\",\"bid\":\"MPiiJWRjJi\",\"dhh_taskid\":\"1981891941\",\"cid\":\"4E2A1DE51557391A0495\",\"jydtp\":\"R\"}"
		val enstr = Format.compressStr(src).replace("=","")
		println(enstr)


		val test = "H4sIAAAAAAAAAI1SyW7bMBD9FUFAgQSwFW5DUelJ8dI2iRPDcRb0EtAi7dDWBlsOkhS990MKFOi96Be16GeUIoUAvVU86JEzw3nvDT-F6-dqFx6TXqi0zMPjMOyFuSk3HtVGWXBzPY8_Di9n07s7OKXnVwCU22DRFNDmxQSjmNJFrAUly1goznS8AJYBAoGZsql7afMm1YvJc3kEEQoOzPShKvXbYDC9DjwOLq8CDPcQ5Gajg4nM2oO7wyCt61zf6sWZaY44gghHGIKDs_fzyXnP577T2aY6DCbVwuT6CMMIM2GbNs-1tm0vLNx5He-S0fxmwEfX84vBJR0PLmZzG1RWf8gZeko4stutrjcrL3_9XKyUh_tWKaJjwCfDpI8AUJ-lY9IXo3TcH-AThk5A8JFArqzqqjLZWEA59Zdl_lSqQj7VW5NZeoSh9rNuVkq3_nszMH5D4HUNhKs3hfYXLLaybDs4a1zI0Zw4KFc2i9optr0pi0TMeQy90Ozyqlw1nSP7TYs-DMepKyq13WJLuHsBpXUAYxpxggROLLt_PbJdyk6iqe2f4MROhUcERU5qZ3253-bej92j3WKIwMl_1Nv7Ujo1v75--_Pl--8fP2fzlLTDeHi4b-TKzQsDohBTN7-XNhkQbrvL2r_LrCqitZFPRlaR2ppHnU6nrt2-UGDjisaEK0zttLAgJFvKZULUEnDCNDDVCcmypqPr_WsKK51DwjAVsaAxc9Yv5f_Pf-HYTabGnN7O1qfmVdZu43UlAovEssDtE3FHbERSPBwBBis4wSliCTg2qmn9nYWf_wKGys7cqgMAAA=="
		val decode = Format.decompressStr(test)
		val decode2 = Format.decompressStr(enstr)
		println(decode)

		println(src == decode)
		println(src == decode2)
	}
}