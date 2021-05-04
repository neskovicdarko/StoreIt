<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<style>
body {
	background-image: url("/Storage/Resources/Images/yellowLogo.png");
	background-repeat: no-repeat;
}

</style>
<link rel="stylesheet" href="/Storage/Resources/Css/mainCss.css">
<meta charset="UTF-8">
<title>Storage</title>

<link rel = "icon" href =  
"/Storage/Resources/Images/box.png" 
        type = "image/x-icon"> 
</head>
<body>
	
	Login form
	<br>
	<hr>
	<form action="/Storage/user/login" method="post">
		<table>
			<tr>
				<td>Email:</td>
				<td><input type="text" name="gmail" value="${emailPH}"></td>
				<td  style = "color:red;">${emailErrorMessage}</td>
			</tr>
			<tr>
				<td>Password:</td>
				<td><input type="password" name="password"></td>
				<td  style = "color:red;">${passwordErrorMessage}</td>
			</tr>
		</table>
		<br>
		<input type="Submit" value="Log in"> <p style = "color:red;"> ${contextMessage} </p>
	</form>
	<br>
	<hr>
	<br>
	<a href="/Storage/Login/register.jsp">Register</a>
	
	<br>
	<br> 
</body>
</html>