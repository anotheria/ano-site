<%@ page contentType="text/html;charset=UTF-8" session="true"%>
<%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"%>
<%@ taglib uri="/WEB-INF/tlds/anosite.tld" prefix="as"%>
<%@ page isELIgnored ="false" %>

<!DOCTYPE html>

<html>

<head>
  <meta charset="utf-8">
  <title>Login page</title>

  <link rel="stylesheet" type="text/css" href="/cms_static/css/newadmin.css">
</head>
<body class="login_page">
  <div class="login_box">
    <form name="ChangePass" method="POST" action="changePass">
      <div class="login_box_header">
        <a href="/cms/index" class="login_asg_logo"></a>
        <h2 class="change_password_header_title">Change password</h2>
      </div>
      <div class="login_hint_box">
        <ano:write name="Message"/>
      </div>

      <div class="login_input_box">
        <label class="change_password_label" for="change_password_login">Your login:</label>
        <input id="change_password_login" type="text" name="Login" disabled="disabled" value="<ano:write name="currentUserId"/>">
      </div>

      <div class="login_input_box">
        <label class="change_password_label" for="current_password">Current password:</label>
        <input id="current_password" type="password" name="pOldPass" value="">
      </div>

      <div class="login_input_box">
        <label class="change_password_label" for="new_password">New password:</label>
        <input id="new_password" type="password" name="pNewPass1" value="">
      </div>

      <div class="login_input_box">
        <label class="change_password_label" for="new_password_confirm">New password (confirm):</label>
        <input id="new_password_confirm" type="password" name="pNewPass2" value="">
      </div>

      <input class='login_button' type="submit" value="Change password">

      <input type="hidden" name="pIsSubmit" value="true">
    </form>
  </div>
</body>
</html>
