<%@ page
	contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%><ano:define id="box" toScope="page" name="__box" scope="request" type="net.anotheria.anosite.content.bean.BoxBean"
/>
<ano:notEqual name="box" property="parameter1" value="">
	<h2><ano:write name="box" property="parameter1"/></h2>
</ano:notEqual>
<p><ano:write name="box" property="content" filter="false"/></p>
<p>&nbsp;</p>