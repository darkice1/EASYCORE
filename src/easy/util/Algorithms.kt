package easy.util

/**
 *
 * *Copyright: youhow.net(c) 2005-2011*
 *
 * 常用算法类
 *
 * @version 1.0 (*2011-5-27 neo(starneo@gmail.com)*)
 */
object Algorithms {
	/**
	 * 二分法 放入数组需要先按顺序排训
	 * @param <T>
	 * @param list
	 * @param o
	 * @return
	</T> */
	@JvmStatic
	fun <T : Comparable<T>?> dichotomy(list: Array<T>, o: T): T? {
		val len = list.size - 1
		var idx = len / 2
		var high = len
		var low = 0
		while (low <= high) {
			val t = list[idx]
			val re = t!!.compareTo(o)
			when {
				re < 0 -> {
					low = idx + 1
				}
				re > 0 -> {
					high = idx - 1
				}
				else -> {
					return t
				}
			}
			idx = (low + high) / 2
		}
		return null
	}
}