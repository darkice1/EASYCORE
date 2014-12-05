<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>测试上传组件</title>
</head>

<body>
<p>name:${name}</p>
<p>上传form</p>
<form action="/c.do?action=upload.TestUpload" method="post"
	enctype="multipart/form-data" name="form1" id="form1">
<p><input name="name" type="text" id="name" value="测试中文" /></p>
<p><input name="f1" type="file" id="f1" /> <input name="f2"
	type="file" id="f2" /></p>
<p><input type="submit" name="Submit" value="Submit" /> <input
	type="reset" name="Submit2" value="Reset" /></p>
</form>
<p>&nbsp;</p>
<p>一般form</p>
<form id="form2" name="form2" method="post"
	action="/c.do?action=upload.TestUpload">
<p><input name="name" type="text" id="name" value="测试中文" /></p>
<p><input type="submit" name="Submit3" value="Submit" /> <input
	type="reset" name="Submit22" value="Reset" /></p>
</form>
<p>&nbsp;</p>
</body>
</html>
