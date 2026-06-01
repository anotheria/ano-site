<%@ page
	contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%><ano:define id="box" toScope="page" name="__box" scope="request" type="net.anotheria.anosite.content.bean.BoxBean"
/><%
	String url = request.isSecure() ? 
		"https://ssl.google-analytics.com/urchin.js" : 
		"http://www.google-analytics.com/urchin.js";
%><!-- google analytics -->
<script src="<%=url%>" type="text/javascript">
</script>
<script type="text/javascript">
_uacct = "<ano:write name="box" property="parameter1"/>";
urchinTracker();
</script>
<!-- /google analytics -->
