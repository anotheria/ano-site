<%@ page
	contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%><?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<rss xmlns:media="http://search.yahoo.com/mrss" version="2.0">
<ano:define id="__boxlist" toScope="request" name="page" property="column1"/>
<jsp:include page="ListIterator.jsp" flush="false"/>
</rss>