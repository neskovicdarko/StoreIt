<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="/Storage/Resources/Css/mainCss.css">
<meta charset="UTF-8">
<title>Registration</title>
<link rel = "icon" href =  
"/Storage/Resources/Images/box.png" 
        type = "image/x-icon"> 
</head>
<body>
	<form action="/Storage/user/register" method="post">
		<table>
			<tr>
				<td>First name</td>
				<td><input type="text" name="firstName" value="${firstNamePH}"></td>
				<td style = "color:red;">${firstNameErrorMessage}</td>
			</tr>
			<tr>
				<td>Last name</td>
				<td><input type="text" name="lastName" value="${lastNamePH}"></td>
				<td style = "color:red;">${lastNameErrorMessage}</td>
			</tr>
			<tr>
				<td>Email</td>
				<td><input type="email" name="gmail" value="${emailPH}"></td>
				<td style = "color:red;">${emailErrorMessage}</td>
			</tr>
			<tr>
				<td>Password</td>
				<td><input type="password" name="password"></td>
				<td style = "color:red;">${passwordErrorMessage}</td>
			</tr>
			<tr>
				<td><input type="submit" value="Register"></td>
			</tr>

		</table>
	</form>
	<br>
	<hr>
	<br>
	<a href="/Storage/index.jsp">Log in</a>

</body>
</html>