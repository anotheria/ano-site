<%@ page contentType="text/html;charset=UTF-8" session="true"
%><%@ page isELIgnored ="false" 
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%><%@ taglib uri="/WEB-INF/tlds/anosite.tld" prefix="as"
%>
<ano:iterate name="page" property="scripts" type="net.anotheria.anosite.content.bean.ScriptBean" id="script">
	<ano:equal name="anosite.verbose" value="true"><!-- AS Script: Script id: <ano:write name="script" property="id"/>, name: <ano:write name="script" property="name"/> --></ano:equal>
	<ano:notEmpty name="script" property="link">
		<script type="text/javascript" src="${pageContext.request.contextPath}/<ano:write name="script" property="link" filter="false"/><ano:match name="script" property="link" value="?">&</ano:match><ano:notMatch name="script" property="link" value="?">?</ano:notMatch><as:random/>"></script></ano:notEmpty>
	<ano:notEmpty name="script" property="content"><script type="text/javascript"><ano:write name="script" property="content" filter="false"/></script></ano:notEmpty>
</ano:iterate>