<%@ page
	contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%>
<ano:define id="box" toScope="page" name="__box" scope="request" type="net.anotheria.anosite.content.bean.BoxBean"/>
<link id="gallery" rel="alternate" href="<ano:write name="box" property="parameter1"/>.rss" type="application/rss+xml">
<script type="text/javascript" src="http://lite.piclens.com/current/piclens.js"> </script>
