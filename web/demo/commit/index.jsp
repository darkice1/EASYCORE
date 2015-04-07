<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Commit Demo</title>
</head>

<body>
<form id="form1" name="form1" method="post"
    action="/c.do?action=commit.DemoCommit">
<table width="817" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td width="239" height="30"><b>democommit1 name：</b></td>
        <td width="578" height="30"><input name="cmt_democommit1_name"
            type="text" id="cmt_democommit1_name" /></td>
    </tr>
    <tr>
        <td height="30"><b>democommit1 test1：</b></td>
        <td height="30"><input name="cmt_democommit1_test1" type="text"
            id="cmt_democommit1_test1" /></td>
    </tr>
    <tr>
        <td width="239" height="30"><b>democommit1 params：</b></td>
        <td width="578" height="30"><label><input
            name="cmts_democommit1_params" type="checkbox"
            id="cmts_democommit1_params" value="1" />1</label> <label><input
            name="cmts_democommit1_params" type="checkbox"
            id="cmts_democommit1_params" value="2" />2</label> <label><input
            name="cmts_democommit1_params" type="checkbox"
            id="cmts_democommit1_params" value="3" />3</label></td>
    </tr>
    <tr>
        <td height="30"><b>democommit2 name： </b></td>
        <td height="30"><input name="cmt_democommit2_name" type="text"
            id="cmt_democommit2_name" /></td>
    </tr>
    <tr>
        <td height="30"><b>democommit2 test_field： </b></td>
        <td height="30"><input name="cmt_democommit2_test_field"
            type="text" id="cmt_democommit2_test_field" /></td>
    </tr>
    <tr>
        <td height="30"><b>democommit1 where name：</b></td>
        <td height="30"><input name="cmtw_democommit1_name" type="text"
            id="cmtw_democommit1_name" /></td>
    </tr>
    <tr>
        <td height="30"><b>democommit2 where name：</b></td>
        <td height="30"><input name="cmtw_democommit2_name" type="text"
            id="cmtw_democommit2_name" /></td>
    </tr>
    <tr>
        <td height="30"><b>cmt_isupdate</b></td>
        <td height="30"><input name="cmt_isupdate" type="checkbox"
            id="cmt_isupdate" value="true" /></td>
    </tr>
    <tr>
        <td height="30" colspan="2"><input type="submit" name="Submit"
            value="Submit" /> &nbsp;</td>
    </tr>
</table>
<p>消耗时间：${USE_TIME}</p>
</form>
</body>
</html>
