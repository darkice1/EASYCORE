/**
 * AJAX请求对象.
 * @athor sjx
 * @param arg_method 			请求方法，以字符串形式进行设置，例如GET、POST、HEAD等.
 * @param arg_url 				请求地址，以字符串方式进行设置，例如http://www.baidu.com或index.jsp.
 * @param arg_synchronized 		是否同步，以BOOLEAN方式进行设置，例如true或false.
 * @param arg_filer 			请求条件，以数组方式进行设置，例如{"a=1","b=c"}.
 * @param arg_onReadyMethod 	请求完成时触发的事件方法，该参数为一个方法指针.
 * 将XMLHttpRequest对象进行的封装.
 */
function AjaxRequest(arg_method,arg_url,arg_synchronized,arg_filer,arg_onReadyMethod) {
// 验证浏览器
	var xmlhttprequest = false;
	if(window.XMLHttpRequest) {
		xmlhttprequest = new XMLHttpRequest();
	}
	else {
		if(window.ActiveXObject){
			try {
				xmlhttprequest = new ActiveXObject("msxml2.XMLHTTP");
			}
			catch(e) {
				xmlhttprequest = new ActiveXObject("Microsoft.XMLHTTP");
			}
		}
		else {
			alert("Error! 您的浏览器暂时不支持AJAX技术.")
		}
	}
	
	// 配置AJAX请求对象
	this.method = arg_method == null ? "post" : arg_method;
	this.url = arg_url;
	this.synchronized = arg_synchronized == null ? true : arg_synchronized;
	this.responseMethod = arg_onReadyMethod;
	this.filer = arg_filer;
	
	//设置|获取请求方法
	this.setMethod = function(arg) {this.method = arg;};
	this.getMethod = function() {return this.method;};
	//设置|获取URL路径
	this.setUrl = function(arg) {this.url = arg;};
	this.getUrl = function() {return this.url;};
	//设置|获取请求条件
	this.setFiler = function(arg) {this.filer = arg;};
	this.getFiler = function() {return this.filer;};
	//设置|获取同步
	this.setSynchronized = function(arg) {this.synchronized = arg;};
	this.getSynchronized = function() {return this.synchronized;};
	
	this.request = function(arg_filer) {
		if(this.method.toLowerCase == "POST".toLowerCase) {
			this.filer = arg_filer;
			var query = "";
			if(this.filer)
				for(var i = 0;i < this.filer.length;i ++) {
					if(i == this.filer.length - 1)
						query + this.filer[i];
					else 
						query + this.filer[i] + "&";
				}
			xmlhttprequest.open(this.method,this.url,this.synchronized);
			xmlhttprequest.setrequestheader("Content-Type","application/x-www-form-urlencoded"); 
			xmlhttprequest.send(escape(query));
		}
		else {
			this.filer = arg_filer;
			var query = "";
			if(this.filer) {
				if(this.filer != null && this.filer.length > 0) query += "?";
				for(var i = 0;i < this.filer.length;i ++) {
					if(i == this.filer.length - 1)
						query + this.filer[i];
					else 
						query + this.filer[i] + "&";
				}
			}
			xmlhttprequest.open(this.method,this.url + escape(query),this.synchronized);
			xmlhttprequest.send(null);
		}
	}
	//以文本模式获取响应内容
	this.getResponseText = function() {
		try {
			return xmlhttprequest.responseText;
		}
		catch(e) {
			return "";
		}
	};
	//以DOM模式获取响应内容
	this.getResponseXml = function() {
		try {
			return xmlhttprequest.responseXml;
		}
		catch(e) {
			return "";
		}
	};
	//设置响应处理方法
	this.setResponseMethod = function(arg_method) {
		xmlhttprequest.onreadystatechange = function() {
			if(xmlhttprequest.readyState == 4 && xmlhttprequest.status == 200) {
				this.responseText = xmlhttprequest.responseText;
				this.responseXml = xmlhttprequest.responseXml;
				arg_method();
			}
		};
	};
}