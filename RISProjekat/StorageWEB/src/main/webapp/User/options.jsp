<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="/Storage/Resources/Css/mainCss.css">
<meta charset="UTF-8">
<title>User options</title>
<link rel = "icon" href =  
"/Storage/Resources/Images/box.png" 
        type = "image/x-icon"> 
</head>
<body>
	<form action="/Storage/user/updateUserCredentials" method="post">
		<table>
			<tr>
				<td>New firstName</td>
				<td><input type="text" name="firstName"
					value="${user.firstName}"></td>
				<td style = "color:red;">${firstNameErrorMessage}</td>
			</tr>
			<tr>
				<td>New lastName</td>
				<td><input type="text" name="lastName"
					value="${user.lastName}"></td>
				<td style = "color:red;">${lastNameErrorMessage}</td>
			</tr>
			<tr>
				<td>New email</td>
				<td><input type="email" name="gmail" value="${user.gmail}"></td>
				<td style = "color:red;">${gmailErrorMessage}</td>
			</tr>
			<tr>
				<td>New password</td>
				<td><input type="password" name="newPassword"></td>
				<td style = "color:red;">${newPasswordErrorMessage}</td>
			</tr>
		</table>
		<hr>
		<table>
			<tr>
				<td>Enter your password to authenticate changes</td>
				<td><input type="password" name="password"></td>
				<td style = "color:red;">${passErrorMsg} ${errorMsg}</td>
			</tr>
			<tr>
				<td><input type="Submit" value="Update" name="submitAction"></td>
			</tr>
			<tr>
				<td><input type="Submit" value="Delete account" name="submitAction"></td>
			</tr>
		</table>
	</form>
	<br>
	<a href="/Storage/User/homepage.jsp"> Home </a>
	<br>
	<br>
</body>
</html>