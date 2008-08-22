<%@ page
	contentType="text/html;charset=windows-1251" session="true"
%><%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"
%><%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"
%><%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic"
%><%@ taglib uri="/WEB-INF/tld/anoweb.tld" prefix="ano"
%>		
<%
	String pageName = (String)request.getAttribute("page");
%>
<html>
<head>
<logic:notPresent name="application.customTitle">
	<title><bean:message key="application.title"/></title>
</logic:notPresent>
<logic:present name="application.customTitle">
	<title><bean:message key="application.customTitle"/></title>
</logic:present>
<META http-equiv=Content-Type content="text/html; charset=iso-8859-1">
<bean:message key="styles.common.link"/>
<bean:message key="styles.links.link"/>
<bean:message key="javascript.common.link"/>
</head>

<body >
<table width="790" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td valign="top" >
		<table width=100% border=0 cellpadding=3 cellspacing=0>
 		  <tr width=100% class="taborange">
		    <td valign="middle" colspan=2>
				<strong>ERROR</strong>
		    </td>
		  </tr>  
		  <tr width=100% class="logintext">
		   <td colspan=2><img src="<bean:message key="emptyimage"/>" height=10 width=1></td>
		  </tr>
		  <tr class="logintextq">
		  	<td colspan="2" align="center"><br>
		      Die von Ihnen ausgef&uuml;hrte Aktion hat folgenden Fehler verursacht:<br> <br>
		      <font color=red><bean:write name="error" property="message"/></font>
		      <br><br>
		      Falls Sie meinen, dass der Fehler nicht aus einer falschen Eingabe resultiert,
		      benachrichtigen Sie uns bitte &uuml;ber diesen Fehler unter <a href="mailto:support@anotheria.net?subject=ERROR IK:<bean:write name="error" property="message"/>">support@anotheria.net</a>.
		  	</td>
		  </tr>
		  <tr class="logintext">
		  	<td colspan="2">&nbsp;</td>
		  </tr>
		  <tr class="logintextq">
		  	<td width=35% align="right">
		      &nbsp;
		  	</td>
		  	<td width="65%" align="left" class="logintextq">
		      <a href="#" class="more" onClick="history.back(); return false">&raquo;&nbsp;Zur&uuml;ck&nbsp;</a>
		  	</td>
		  </tr>
		  <tr width=100% class="logintextq">
		    <td colspan=2><img src="<bean:message key="emptyimage"/>" height=10 width=1></td>
		  </tr>
		  </form>
		</table>
		<p><font color="#FFFFFF"><bean:write name="error" property="stackTrace"/></font></p>
		  
    </td>
    <td width="21" valign="top" background="<bean:message key="images" arg0="trenner_v.gif"/>"><img src="<bean:message key="emptyimage"/>" width="21" height="1"></td>
  </tr>
  <tr>
    <td colspan="3">&nbsp;</td>
  </tr>
</table>
<jsp:include page="Footer.jsp"/>
</body>
</html>