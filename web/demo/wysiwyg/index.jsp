<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<jsp:include page="../../js/wysiwyg/full.jsp" flush="true" />
</head>

<body>
<form id="form1" name="form1" method="post" action=""><label>
<textarea name="test" cols="20" rows="20"></textarea> </label> <label> <input
	type="submit" name="Submit" value="Submit" /> </label></form>

<div><%= request.getParameter("test") %></div>
</body>
</html>
