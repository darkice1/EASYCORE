var table_id = 0;

//str 为传入字符串，classes为class ,handly为处理字符串方便名称
function Ecol(str,field,classes,handly,row) 
{
	this.str = str;
	this.classes = classes;
	this.handly = handly;
	this.field = field;
	this.row = row;
}

Ecol.prototype.toString = function()
{
	if (this.field == null && this.handly != null)
	{
		return eval(this.handly+"(this.str,this.row)");
	}
	return this.str
};

//str 为传入字符串，handly为处理字符串方便名称
function Erow(id,cols,classes,mover,mout) 
{
	this.id = id;
	if (this.cols == null)
	{
		this.cols = new Array();
	}
	else
	{
		this.cols = cols;
	}
	this.classes = classes;
	this.mover = mover;
	this.mout = mout;
}

Erow.prototype.addcol = function(col)
{
	this.cols[this.cols.length] = col;
};

Erow.prototype.over = function (row)
{
	row.className = "listrowhover";
}

Erow.prototype.out = function (row)
{
	row.className = row.getAttribute("oc");
}

Erow.prototype.toString = function()
{
	var buf = "";
	buf += "<tr id=\""+this.id +"\" class=\""+this.classes+"\"";
	if (this.mover != null)
	{
		buf += " onmousemove=\""+this.mover+"\"";
	}
	if (this.mout != null)
	{
		buf += " onmouseout=\""+this.mout+"\"";
	}
	if (this.classes != null)
	{
		buf += " oc=\""+this.classes+"\"";
	}	
	buf += ">";
	
	"\">";
	for (var i=0;i<this.cols.length;i++)
	{
		buf += "<td class=\""+this.cols[i].classes+(this.cols[i].field==null?"\"":("\" field=\""+this.cols[i].field+"\"")) +(this.cols[i].handly==null?"":(" handly=\"" + this.cols[i].handly +"\""))+">";
		buf += this.cols[i];	
		buf += "</td>";
	}
	buf += "</tr>";
	return buf;
};


//创建表格
function Etable(id,outid,classes,head,rows,dispartPages,url) 
{
	eval ("etable_"+id+"=this");
	if (outid == null || document.getElementById(outid)== null)
	{
		outid = "table_span_"+table_id;
		document.writeln ("<span id=\""+outid+"\"></span>");	
		
	}
	this.outobj = document.getElementById(outid);
	
	if (id == null)
	{
		this.id = "table_"+(table_id++);
	}
	else
	{
		this.id = id;
	}

	this.classes = classes;
	
	this.head = head;
	if (this.rows == null)
	{
		this.rows = new Array();
	}
	else
	{
		this.rows = rows;
	}
	if(dispartPages != null && dispartPages.toLowerCase() == "yes")
	{
		this.isDispartPages = "yes";
	}else
	{
		this.isDispartPages = "no";
	}
	if(url != null){
		this.url = url;
	}else{
		alert("创建对象失败！请输入请求的url！");	
		//this = null;
	}
	this.where = null;
};


Etable.prototype.addhead = function(head) 
{
	this.head = head;
};

Etable.prototype.addrow = function(row) 
{
	this.rows[this.rows.length] = row;
};

Etable.prototype.initDispartPages = function(){
	var buf = "";
	//添加分页显示工具条
	buf += "<tr class='PageToolbar'><td align='center' nowrap colspan='"+ this.head.cols.length+"'>";
	buf += "<font style='font-size:10pt;'>共"
	buf += this.count==null?0:this.count;
	buf += "条记录 分";
	buf += this.pageCount==null?0:this.pageCount;
	buf += "页显示 当前第";
	buf += this.pageNum==null?0:this.pageNum;
	buf += "页";
	if(this.pageNum <= 1){
		buf += " <input type='button' class='button' value='首页' disabled>";
		buf += " <input type='button' class='button' value='上一页' disabled>";
	}else{
		buf += " <input type='button' class='button' value='首页' onclick='";
		buf += this.gotoPage("&pageNum=1");
		buf +="'>";
		buf += " <input type='button' class='button' value='上一页' onclick='";
		buf +=  this.gotoPage("&pageNum=" + (parseInt(this.pageNum) - 1));
		buf += "'>";
	}
	if(this.pageNum >= this.pageCount){
		buf += " <input type='button' class='button' value='下一页' disabled>";
		buf += " <input type='button' class='button' value='末页' disabled>";
	}else{
		buf += " <input type='button' class='button' value='下一页' onclick='";
		buf += this.gotoPage("&pageNum=" + (parseInt(this.pageNum) + 1)) ;
		buf += "'>";
		buf += " <input type='button' class='button' value='末页' onclick='";
		buf += this.gotoPage("&pageNum=" + this.pageCount);
		buf += "'>";
	}
	buf += " 跳转至<input type='text' name='pageToTurn' id='pageToTurn' size=4 value='"+this.pageNum+"'><input type=button value='Go' onclick=\"etable_"+this.id+".turnPage()\">";
	
	buf += "</font></td></tr></table>";
	
	this.dispartPages = buf;
}

Etable.prototype.toString = function()
{
	var buf = "";
	buf += "<table id=\""+this.id +"\" class=\""+this.classes+"\">";
	if (this.head != null)
	{
		buf += this.head;
	}
	for (var i=0;i<this.rows.length;i++)
	{
		buf += this.rows[i];
	}	
	if(this.dispartPages != null){
		buf += this.dispartPages;
	}
	
	buf += "</table>";
	
	return buf;	
};

Etable.prototype.toListString = function()
{
	if (this.classes == null)
	{
		this.classes = "listtable";
	}

	if (this.head != null && this.head.classes == null)
	{
		this.head.classes = "listhead";
	}
	for (var i=0;i<this.rows.length;i++)
	{
		if (this.rows[i].classes == null)
		{
			this.rows[i].classes = "listrow"+(i%2+1);
		}
		
		if (this.rows[i].mover == null)
		{
			this.rows[i].mover = "new Erow().over(this)";
		}
		if (this.rows[i].mout == null)
		{
			this.rows[i].mout = "new Erow().out(this)";
		}
	}
	return this.toString();	
};

Etable.prototype.initXML = function(xmldoc,et)
{
	if (et.head == null)
	{
		alert ("请输入表头");
	}
	else
	{
		var info = xmldoc.getElementsByTagName('rs')[0];
		
		et.count = info.getAttribute('count');
		et.pageSize = info.getAttribute('pageSize');
		et.pageNum = info.getAttribute('pageNum');
		et.pageCount = info.getAttribute("pageCount");
		if(et.isDispartPages == "yes"){
			et.initDispartPages();	
		}
		
		var list = xmldoc.getElementsByTagName('r');
		//alert (list.length);
		for (var i=0; i < list.length; i++)
		{
			var r = new Erow();
			for (var j=0; j<et.head.cols.length; j++)
			{
				//alert (et.head.cols[j].field)
				var c = new Ecol(list[i].getAttribute(et.head.cols[j].field),null,null,et.head.cols[j].handly,list[i])
				r.addcol(c);
			}
			et.addrow(r);
		}
		//alert (et.toListString());
		et.writeListTable();
	}
};

Etable.prototype.loadXML = function(url)
{
	var t = this;
	t.rows = new Array();
	loadXML(url,this.initXML,this)
	//this.writeListTable();
};

/////////////////////////////////////////////////////
Etable.prototype.initXML2 = function(xmldoc)
{
		var info = xmldoc.getElementsByTagName('rs')[0];
		
		var list = xmldoc.getElementsByTagName('r');
		
		alert (list.length);
		for (var i=0; i < list.length; i++)
		{
			var r = new Erow();
			for (var j=0; j<et.head.cols.length; j++)
			{
				alert (et.head.cols[j].field)
				var c = new Ecol(list[i].getAttribute(et.head.cols[j].field),null,null,et.head.cols[j].handly)
				r.addcol(c);
			}
			et.addrow(r);
		}
		//alert (et.toListString());
		et.writeListTable();
};

Etable.prototype.loadXML2 = function(url)
{
	var t = this;
	t.rows = new Array();
	loadXML(url,this.initXML,this)
	//this.writeListTable();
};
////////////////////////////////////////////////

Etable.prototype.writeListTable = function()
{
	this.outobj.innerHTML = this.toListString();
};

Etable.prototype.writetable = function()
{
	this.outobj.innerHTML = this.toString();
};



Etable.prototype.gotoPage = function(url){
	var buf = "etable_";
	
	buf += this.id;
	buf += ".loadXML(\"";
	buf += this.url;
	buf += url;
	if(this.where != null)
		buf += this.where;
	buf += "\")";
	//alert(buf);
	return buf;
}


Etable.prototype.checkObjNumber = function(obj,errorStr)
{
	var number = 0;
	if (obj.type=="text" || obj.type=="password" || obj.type=='textarea' || obj.type=='file')
	{
		number = obj.value;
	}
	else if (obj.type.indexOf("select") == 0)
	{
		for (var i=0,len=obj.length;i<len;i++)
		{
			if (obj.selected == true)
			{
				number = obj.text;
				break;
			}
		}
	}
	else
	{
		alert (obj.type);
		return false;
	}
	if (!isNumber(number.trim()))
	{
		if (errorStr!=null && errorStr!="")
		{
			obj.focus();
			alert(errorStr);
		}
		return false;
	}
	return true;
}

Etable.prototype.turnPage = function(){
	var o = document.getElementById('pageToTurn');
	if (!this.checkObjNumber(o,'页码不能为字符和负数')) 
		return;
	if (o.value < 1 || parseInt(o.value) > parseInt(this.pageCount)) {
		o.focus();
		alert("共" + this.pageCount + "页记录！请确认输入的页数！");
		return;
	}
	eval ("ccc=etable_"+this.id);
	ccc.loadXML(this.url + "&pageNum=" + o.value);
}

Etable.prototype.toSearch = function(form,tid){
	var inputs = form.elements;
	if(inputs != null){
		var objs = new Array();
		for(var i = inputs.length - 1; i 
			
			>= 0; i--){
			var name = inputs[i].name;
			if(name.indexOf("srw_") == 0){
				//alert(name);
				this.addSearchInfo(objs,inputs[i]);
			}else if(name.indexOf("srw_l_") == 0){
				//alert(name);
				this.addSearchInfo(objs,inputs[i]);
			}
		}
		this.addWhere(objs);
	}else{
		alert("没有查询条件");	
	}
}

Etable.prototype.addWhere = function(objs){
	this.where = "";
	for(var i = 0; i < objs.length; i++){
		if(objs[i].value != null && objs[i].value != ""){
			this.where += "&" + objs[i].name + "=" + (objs[i].value);
		}	
	}
	//alert(this.url + this.where);
	this.loadXML(this.url + this.where);
}

Etable.prototype.addSearchInfo = function(objs,obj){
	var i = objs.length;
	objs[i] = obj;
}