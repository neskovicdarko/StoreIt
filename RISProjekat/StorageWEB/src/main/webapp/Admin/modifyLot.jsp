<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="/Storage/Resources/Css/mainCss.css">
<meta charset="UTF-8">
<title>Update lot data</title>
<link rel = "icon" href =  
"/Storage/Resources/Images/box.png" 
        type = "image/x-icon"> 
</head>
<body>
	<h1> ${lot.lotName} </h1>
	<br>
	<a href="/Storage/Admin/homepage.jsp">Home</a>
	<br>
	<a href="/Storage/user/logout">Log out</a>
	<br>
	<hr>

	<form action="/Storage/lot/modifyLot" method="post">
		<table>
			<tr>
				<td>Lot Name</td>
				<td><input type="text" name="newName" value="${lot.lotName}"></td>
			</tr>
			<tr>
				<td>Number of units to add</td>
				<td><input type="text" name="numberOfNewUnits" value="0"></td>
			</tr>
			<tr>
				<td>New price</td>
				<td><input type="text" name="newPrice"
					value="${lot.storagePrice}"></td>
			</tr>
		</table>
		<input type="Submit" value="Update">
	</form>
	<br>
	<hr>


</body>
</html>