function trim(str) 
{
   return str.replace(/(^\s*)|(\s*$)/g, "");
}

String.prototype.replaceAll = function(src,desc)
{
	var tmp = this;
	var re=new RegExp(src,"g");
	tmp = tmp.replace(re,desc);
	return tmp;
}
String.prototype.trim = function()
{
	return this.replace(/(^\s*)|(\s*$)/g, "");
}

String.prototype.ltrim = function()
{
	return this.replace(/(^\s*)/g, "");
}

String.prototype.rtrim = function()
{
	return this.replace(/(\s*$)/g, "");
}

function isNumber(anum1) 
{
    var numstr = "0123456789";
    for(var i=0; i < anum1.length; i++)
    {
        if(numstr.indexOf(anum1.charAt(i)) == -1)
        {
            return false;
        }
    }   
    return true;
}

function isEmail(email)
{
    var bad_email_chars = "`\"\\ /(){}[]|<>/,&+=*'%?!~#$^:;";

    if (email == "" || email==null)
    {
       return false;
    }
	if (email.length < 5) 
	{
	    return false;
    }

	if (check_string(email, bad_email_chars) > -1) 
	{
	    return false;
    }
	//Check for a String "####@.com".
	var at_sign =email.indexOf("@.");
	if (at_sign>=0) 
	{
	    return false;
    }
	//Check for a String "####.@com".
	var at_sign =email.indexOf(".@");
	if (at_sign>=0) 
	{
        return false;
	}
	//Check for that there are more two "@" in email string.
	var at_sign  =email.indexOf("@");
	var at_sign2 =email.indexOf("@", at_sign+1);
	if (at_sign2>=0) 
	{
	    return false;
    }
	// Check for an @ sign
	var at_sign =email.indexOf("@");
	if (at_sign<=0) 
	{
	    return false;
    }
	// Check for a domain
	var dot = email.indexOf(".");
	if (dot < 1) 
	{
	    return false;
    }

    return true;
}

function returnfalse(p_input) 
{	
	try
	{
		p_input.focus();
		p_input.select();
	}
	catch (e)
	{	
	}
	event.returnValue=false;
}



Date.prototype.setDateStr = function(str)
{
	try
	{
		str += "";
		date=str.split(" ");
		date1=date[0].split("-");	
		date2=date[1].split(":");
		this.setFullYear(date1[0]);
		this.setMonth(date1[1]-1);
		this.setDate(date1[2]);
		this.setHours(date2[0]);
		this.setMinutes(date2[1]);
		this.setSeconds(date2[2]);
		return true;
	}
	catch (ex)
	{
		//alert (ex);
		return false;
	}
}

Date.prototype.getDateStr = function()
{
	var str ="";
	/*
	if (this.getMonth() < 9)
	{
		str += "0";
	}
	str += (this.getMonth()+1)+"��";
	
	if (this.getDate() < 10)
	{
		str +="0";
	}
	str += this.getDate()+"��";	
	*/
	if (this.getHours() < 10)
	{
		str +="0";
	}
	str += (this.getHours())+":";	
		
	if (this.getMinutes()< 10)
	{
		str +="0";
	}
	str += this.getMinutes();
	return str;
}

function setCookie(cookieName,cookievalue,path,domainstr,expires)
{
	document.cookie=cookieName+"="+escape(cookievalue);
	if (path!="" && path!=null)
	{
		document.cookie +=";path="+path;
	}
	if (domainstr!="" && domainstr!=null)
	{
		document.cookie +=";domain="+domainstr;
	}
	if (expires!="" && expires!=null)
	{
		document.cookie +=";expires="+expires;
	}
}

function getCookie(cookieName)
{
	var cookieString = document.cookie;
	var start = cookieString.indexOf(cookieName + '=');
	if (start == -1)
	{
		return null;
	}
	else
	{
		start += cookieName.length + 1;
		var end = cookieString.indexOf(';', start);
		if (end == -1) 
		{
			return unescape(cookieString.substring(start));
		}
		else
		{
			return unescape(cookieString.substring(start, end));
		}
	}
}

function loadXML(url, handler,args,starhardly,endhardly,asynchronous)
{
	url = encodeURI(url);

	if (asynchronous == null)
	{
		asynchronous = true;
	}
	if (window.ActiveXObject && !window.XMLHttpRequest)
	{
		window.XMLHttpRequest = function() 
		{
			return new ActiveXObject((navigator.userAgent.toLowerCase().indexOf('msie 5') != -1) ? 'Microsoft.XMLHTTP' : 'Msxml2.XMLHTTP');
		};
	}
	
	if (!window.ActiveXObject && window.XMLHttpRequest)
	{
		window.ActiveXObject = function(type) 
		{
			switch (type.toLowerCase())
			{
				case 'microsoft.xmlhttp':
				case 'msxml2.xmlhttp':
				return new XMLHttpRequest();
			}
			return null;
		};
	}
		
	var req = new XMLHttpRequest();
	if (req)
	{
		if (starhardly != null)
		{
			starhardly(this);
		}
		req.onreadystatechange = function()
		{
			if (asynchronous && req.readyState == 4 && req.status == 200)
			{
				//alert(req.readyState)
				handler(req.responseXML,args);
				if (endhardly != null)
				{
					endhardly(this);
				}
			}
		};
		req.open('GET', url,asynchronous);
		req.send(null);
		if (asynchronous==false)
		{
			handler(req.responseXML,args);
			if (endhardly != null)
			{
				endhardly(this);
			}
		}
	}
}
function initRowList(xmldoc,arg)
{
	arg[0] = xmldoc.getElementsByTagName('r');
}

function getRowList(url)
{
	var r = new Array();
	loadXML(url, initRowList,r,null,null,false);
	return r[0];
}

function reportError(msg,url,line) {
	var str = "You have found an error as below: \n\n";
	str += "Err: " + msg + " on line: " + line;
	alert(str);
	return true;
}

function startload()
{
	if (document.getElementById("bgDiv")==null)
	{
		var str = "<img src=\"/common/loading.gif\" alt=\"loading\"  align=\"absmiddle\" style=\"padding:0px;margin:0px;float:none;width:16px;height:16px;display:inline;\" />Loading...";
		var msgw,msgh,bordercolor;
		msgw=400;//提示窗口的宽度
		msgh=100;//提示窗口的高度
		titleheight=25 //提示窗口标题高度
		bordercolor="#336699";//提示窗口的边框颜色
		titlecolor="#99CCFF";//提示窗口的标题颜色
		
		var sWidth,sHeight;
		sWidth=document.documentElement.offsetWidth;
		sHeight=document.documentElement.offsetHeight;
		if (sHeight<screen.height)
		{
			sHeight=screen.height;
		}
	
		var bgObj=document.createElement("div");
		bgObj.setAttribute('id','bgDiv');
		bgObj.style.position="absolute";
		bgObj.style.top="0";
		bgObj.style.background="#777";
		bgObj.style.filter="progid:DXImageTransform.Microsoft.Alpha(style=3,opacity=25,finishOpacity=75";
		bgObj.style.opacity="0.6";
		bgObj.style.left="0";
		bgObj.style.width=sWidth + "px";
		bgObj.style.height=sHeight + "px";
		bgObj.style.zIndex = "10000";
		document.body.appendChild(bgObj);
				
		var msgObj=document.createElement("div")
		msgObj.setAttribute("id","msgDiv");
		msgObj.setAttribute("align","center");
		msgObj.style.background="white";
		msgObj.style.border="1px solid " + bordercolor;
		msgObj.style.position = "absolute";
		msgObj.style.left = "50%";
		msgObj.style.top = "50%";
		msgObj.style.font="12px/1.6em Verdana, Geneva, Arial, Helvetica, sans-serif";
		msgObj.style.marginLeft = "-225px" ;
		msgObj.style.marginTop = -75+document.body.scrollTop+"px";
		msgObj.style.width = msgw + "px";
		msgObj.style.height =msgh + "px";
		msgObj.style.textAlign = "center";
		msgObj.style.lineHeight = (msgh-titleheight) + "px";
		msgObj.style.zIndex = "10001";
	   
		var title=document.createElement("h4");
		title.setAttribute("id","msgTitle");
		title.setAttribute("align","right");
		title.style.margin="0";
		title.style.padding="3px";
		title.style.background=bordercolor;
		title.style.filter="progid:DXImageTransform.Microsoft.Alpha(startX=20, startY=20, finishX=100, finishY=100,style=1,opacity=75,finishOpacity=100);";
		title.style.opacity="0.75";
		title.style.border="1px solid " + bordercolor;
		title.style.height="18px";
		title.style.font="12px Verdana, Geneva, Arial, Helvetica, sans-serif";
		title.style.color="white";
		title.style.cursor="pointer";
	
		document.body.appendChild(msgObj);
		document.getElementById("msgDiv").appendChild(title);
		var txt=document.createElement("p");
		txt.style.margin="1em 0"
		txt.setAttribute("id","msgTxt");
		txt.innerHTML=str;
		document.getElementById("msgDiv").appendChild(txt);
	}
}
function endload()
{
	try
	{
		if (document.getElementById("bgDiv")!=null)
		{
			document.body.removeChild(document.getElementById("bgDiv"));
			document.getElementById("msgDiv").removeChild(document.getElementById("msgTitle"));
			document.body.removeChild(document.getElementById("msgDiv"));
		}
	}
	catch(e)
	{
		alert(e)
	}
}

function slog(url) 
{
	var n = "t_" + (new Date()).getTime() + Math.random() * 9999;
	var c = window[n] = new Image(1, 1);
	c.onload = (c.onerror = function() {
		window[n] = null
	});
	c.src = url;
	url = null;
	n = null;
	c = null
}
//window.onerror = reportError;