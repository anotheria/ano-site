<%@ page
	contentType="text/html;charset=UTF-8" session="true"%><%@ taglib 
	uri="http://www.anotheria.net/ano-tags" prefix="ano"
%><ano:define id="box" toScope="page" name="__box" scope="request" type="net.anotheria.anosite.content.bean.BoxBean"/><ano:notEmpty 
	name="box" property="cssClass"><div id="box<ano:write name="box" property="id"/>" class="<ano:write name="box" property="cssClass"/>"></ano:notEmpty><ano:notEqual 
	name="box" property="content" value=""><ano:write name="box" property="content" filter="false"/></ano:notEqual><ano:define 
	id="__boxlist" toScope="request" name="box" property="subboxes"/><jsp:include page="../templates/ListIterator.jsp" flush="false"/><ano:notEmpty name="box" property="cssClass"></div></ano:notEmpty>