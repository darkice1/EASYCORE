<%@ page import="easy.sql.CPSql"%>
<%@ page import="easy.sql.DataSet"%>
<%@ page import="easy.util.Format" %>
<%
		CPSql sql = new CPSql();
		DataSet ds = sql.executeQuery("SELECT * FROM list");
		out.print(Format.toXMLString(ds));
%>