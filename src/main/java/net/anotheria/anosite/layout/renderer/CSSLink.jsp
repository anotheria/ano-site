<%@ page
	contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%>
<ano:define id="box" toScope="page" name="__box" scope="request" type="net.anotheria.anosite.content.bean.BoxBean"/>
<script type="text/javascript" src="<ano:write name="box" property="parameter1" filter="false"/>" >
<ano:notEmpty name="box" property="content">
	<script type="text/javascript">
	<ano:write name="box" property="content" filter="false"/>
	</script>
</ano:notEmpty>

<%--
<ano:define id="__boxlist" toScope="request" name="box" property="subboxes"/>
<jsp:include page="../templates/ListIterator.jsp" flush="false"/>
--%>