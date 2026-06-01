<%@ page contentType="text/html;charset=UTF-8" session="true"%>
<!DOCTYPE html>

<html>

<head>
	<meta charset="utf-8">
	<title>Login page</title>

	<link rel="stylesheet" type="text/css" href="/cms_static/css/newadmin.css">
</head>
<body class="login_page">
	<div class="login_box">
		<form name="Login" method="POST" action="login">
			<div class="login_box_header">
				<a href="" class="login_asg_logo"></a>
				<h2 class="login_box_header_title">Login</h2>
			</div>
			<div class="login_hint_box">
				This page is not public. Please login to access this page.
			</div>

			<div class="login_input_box">
				<label class="input_box_label" for="login_username">Username:</label>
				<input id="login_username" type="text" name="pUserLogin">
			</div>

			<div class="login_input_box">
				<label class="input_box_label" for="login_password">Password:</label>
				<input id="login_password" type="password" name="pPassword">
			</div>

			<input class='login_button' type="submit" value="Login">
		</form>
	</div>
</body>
</html>
