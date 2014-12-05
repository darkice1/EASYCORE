<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<script src="../../js/c/util.js"></script>
<script src="../../js/c/table.js"></script>
<style type="text/css" media="all">
<!--
@import url("../../css/c/main.css");
-->
</style>

<script>
		var url = "/command.do?action=list.TestListXml";
		var otherUrl = "";
		
		function load()
		{
			tt.loadXML(url + getOtherUrl());
		}
		
		function getOtherUrl()
		{
			otherUrl = "&srw_l_name=" + srw_l_name.value + "&srw_sex=" + srw_sex.value + "&list_page_num=5";
			return otherUrl;
		}
	</script>
</head>
姓名：
<input name='srw_l_name'>
性别：
<select name='srw_sex'>
	<option value='m'>男
	<option value='f'>女
</select>
<input name='' type='button' value='查询' onclick="javascript:load();">
<span id="content"></span>
<script>
var tt = new Etable();

var c1 = new Ecol("c1","name");
var c2 = new Ecol("c2","sex");
var h = new Erow("h");
h.addcol (c1);
h.addcol (c2);
tt.addhead(h);
load();
</script>