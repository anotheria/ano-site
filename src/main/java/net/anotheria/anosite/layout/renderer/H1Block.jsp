<%@ page
	contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%><ano:define id="box" toScope="page" name="__box" scope="request" type="net.anotheria.anosite.content.bean.BoxBean"
/><ano:notEqual name="box" property="content" value="">
	<h1 class="block"><ano:write name="box" property="content" filter="false"/></h1>
</ano:notEqual>