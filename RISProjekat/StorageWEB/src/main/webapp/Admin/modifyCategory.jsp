<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="/Storage/Resources/Css/mainCss.css">
<meta charset="UTF-8">
<title>Update category data</title>
<link rel = "icon" href =  
"/Storage/Resources/Images/box.png" 
        type = "image/x-icon"> 
</head>
<body>
	Category data for ${category.catName}
	<br>
	<br>
	<a href="/Storage/Admin/homepage.jsp">Home</a>
	<br>
	<a href="/Storage/user/logout">Log out</a>
	<br>
	<hr>

	<form action="/Storage/category/modifyCategory" method="post">
		<table>
			<tr>
				<td>Category Name</td>
				<td><input type="text" name="newName"
					value="${category.catName}"></td>
			</tr>
			<tr>
				<td>Multiplicator</td>
				<td><input type="text" name="newMultiplicator"
					value="${category.catMultiplicator}"></td>
			</tr>
		</table>
		<input type="Submit" value="Update">
	</form>
	<br>
	<hr>


</body>
</html>