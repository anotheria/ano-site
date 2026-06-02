<%@ page
	contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%>
        <!-- Login -->
<h1><ano:write name="res.logout.title"/></h1>
<div class="loginform">
  <form method="post" action="<ano:write name="logout.action" ignore="true"/>"> 
   	<fieldset>
   		<p><input type="submit" name="logout" class="button" value="<ano:write name="res.logout.submit"/>"  /></p>
 	</fieldset>
  </form>
</div>