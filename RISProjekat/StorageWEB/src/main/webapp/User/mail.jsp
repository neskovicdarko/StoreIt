<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="/Storage/Resources/Css/mainCss.css">
<meta charset="UTF-8">
<title>Mail</title>
<link rel="icon" href="/Storage/Resources/Images/box.png"
	type="image/x-icon">
</head>
<body>
	User mail for ${user.firstName}, ${user.lastName}
	<br>
	<br>
	<a href="/Storage/user/logout">Log out</a>
	<br>
	<a href="/Storage/User/homepage.jsp"> Home </a>
	<br>
	<hr>

	<c:if test="${! empty messages }">
		<table id="tabela">
			<tr id="tabela">
				<td id="tabela">Sender</td>
				<td id="tabela">Reciever</td>
				<td id="tabela">Subject</td>
				<td id="tabela">Sent</td>
				<td id="tabela"></td>
				<td id="tabela"></td>
			</tr>
			<c:forEach items="${ messages }" var="msg">
				<tr id="tabela">
					<c:if test="${msg.sentByUser == 1}">
						<td id="tabela">Me</td>
						<td id="tabela">Admin: ${msg.admin.firstName},
							${msg.admin.lastName}</td>
					</c:if>
					<c:if test="${msg.sentByUser == 0}">
						<td id="tabela">Admin: ${msg.admin.firstName},
							${msg.admin.lastName}</td>
						<td id="tabela">Me</td>
					</c:if>

					<td id="tabela">${msg.msgSubject}</td>
					<td id="tabela">${msg.sentDate}</td>
					<td id="tabela"><a
						href="/Storage/message/routerReadByUser?msgId=${msg.msgId}">
							Read </a></td>
					<td id="tabela"><c:if test="${msg.sentByUser == 1 }">
							<a href="/Storage/message/deleteMessage?msgId=${msg.msgId}">
								Delete </a>
						</c:if></td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
	<c:if test="${empty messages}">
		You have no sent or recieved mail.
	 </c:if>
	<br>
	<a href="/Storage/message/refreshMessages">Refresh</a>
	<hr>
	<br> Send mail to admins
	<br>
	<form action="/Storage/message/sendMessageToAdmins" method="post"
		id="mailForm">
		<table>
			<tr>
				<td>Subject</td>
				<td><input type="text" name="msgSubject"></td>
			</tr>
			<tr>
				<td>Content</td>
			</tr>
		</table>
		<textarea rows="4" cols="50" name="msgContent" form="mailForm"></textarea>

		<input type="Submit" value="Send">
	</form>
	<p style="color: red;">${errorMsg}</p>
	<br>
</body>
</html>