<%@ page
	contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%><ano:define id="box" toScope="page" name="__box" scope="request" type="net.anotheria.anosite.content.bean.BoxBean"
/><div class="column1-unit">
<ano:notEqual name="box" property="content" value="">
	<ano:write name="box" property="content" filter="false"/>
</ano:notEqual>
</div>
<hr class="clear-contentunit" />