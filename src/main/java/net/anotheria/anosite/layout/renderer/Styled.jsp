<%@ page
	contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%>
<ano:define id="box" toScope="page" name="__box" scope="request" type="net.anotheria.anosite.content.bean.BoxBean"/>
<div <ano:notEmpty name="box" property="parameter1">id="<ano:write name="box" property="parameter1"/>"</ano:notEmpty> <ano:notEmpty name="box" property="parameter2">class="<ano:write name="box" property="parameter2"/>"</ano:notEmpty>>
<ano:notEqual name="box" property="content" value="">
	<ano:write name="box" property="content" filter="false"/>
</ano:notEqual>
<ano:define id="__boxlist" toScope="request" name="box" property="subboxes"/>
<jsp:include page="../templates/ListIterator.jsp" flush="false"/>
</div>