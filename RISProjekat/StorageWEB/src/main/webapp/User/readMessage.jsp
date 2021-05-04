<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="/Storage/Resources/Css/mainCss.css">
<meta charset="UTF-8">
<title>Message info</title>
<link rel="icon" href="/Storage/Resources/Images/box.png"
	type="image/x-icon">
</head>
<body>
	User: ${user.firstName}, ${user.lastName}
	<br>
	<table>
		<tr>
			<td><a href="/Storage/user/logout">Log out</a></td>
			<td><a href="/Storage/User/homepage.jsp"> Home </a></td>
			<td><a href="/Storage/User/mail.jsp">Mail</a></td>
		</tr>
	</table>
	<hr>
	<br> Message subject: ${message.msgSubject}
	<br>
	<c:if test="${message.sentByUser == 1 }">Sent by: Me <br> 
	Recieved by: Administration <br>
	</c:if>
	<c:if test="${message.sentByUser == 0 }">Sent by: ${message.admin.firstName}, ${message.admin.lastName} <br> 
	Recieved by: Me <br>
	</c:if>
	<br>
	<br> Content
	<br>
	<textarea rows="4" cols="50" readonly>${message.msgContent}</textarea>
	<hr>
	<td><c:if test="${message.sentByUser == 1 }">
			<a href="/Storage/message/deleteMessage?msgId=${message.msgId}">
				Delete message </a>
		</c:if></td>
	<c:if test="${message.sentByUser == 0}">
		<br>Respond to this message
		<br>
		<form action="/Storage/message/sendResponseToAdmin" method="post"
			id="mailForm">
			<table>
				<tr>
					<td>Message subject</td>
					<td><input type="text" name="msgSubject"></td>
				</tr>
				<tr>
					<td>Message content</td>
				</tr>
			</table>
			<textarea rows="4" cols="50" name="msgContent" form="mailForm"></textarea>
			<input type="Submit" value="Send response">
		</form>
		<br>${errorMsg}<br>
	</c:if>
	<br>
</body>
</html>