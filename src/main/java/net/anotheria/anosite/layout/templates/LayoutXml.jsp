<%@ page
	contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="logic"
%>
<ano:define id="__boxlist" toScope="request" name="page" property="column1"/>
<jsp:include page="ListIterator.jsp" flush="false"/>
<ano:define id="__boxlist" toScope="request" name="page" property="column2"/>
<jsp:include page="ListIterator.jsp" flush="false"/>



