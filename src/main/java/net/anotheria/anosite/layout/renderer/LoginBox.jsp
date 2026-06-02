<%@ page
	contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%>
        <!-- Login -->

<h1><ano:write name="res.login.title"/></h1>
<div class="loginform">
  <form method="post" action="<ano:write name="login.action" ignore="true"/>"> 
    <fieldset>
      <p><label for="username" class="top"><ano:write name="res.login.username"/>:</label><br />
        <input type="text" name="username" id="username" tabindex="1" class="field" /></p>
   <p><label for="password" class="top"><ano:write name="res.login.password"/>:</label><br />
        <input type="password" name="password" id="password" tabindex="2" class="field" onkeypress="return webLoginEnter(document.loginfrm.cmdweblogin);" value="" /></p>
   <p><input type="checkbox" id="rememberme" name="rememberme" value="true" class="checkbox" tabindex="3" size="1" /><label for="rememberme" class="right"><ano:write name="res.login.rememberme"/></label></p>
   <p><input type="submit" name="login" class="button" value="<ano:write name="res.login.submit"/>"  /></p>
   <p><ano:write name="login.invalid" ignore="true" /></p>
   <p><a href="#" id="password_forgotten"><ano:write name="res.login.password_forgotten"/></a></p>
 </fieldset>
  </form>
</div>