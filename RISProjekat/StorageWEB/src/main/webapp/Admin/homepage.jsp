<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="/Storage/Resources/Css/mainCss.css">
<meta charset="UTF-8">
<title>Admin</title>
<link rel = "icon" href =  
"/Storage/Resources/Images/box.png" 
        type = "image/x-icon"> 
</head>
<body>
	<h2> Admin homepage for ${admin.firstName} , ${admin.lastName} </h2>
	<table>
		<tr>
			<td><a href="/Storage/user/logout">Log out</a></td>
			<td><a href="/Storage/Admin/mail.jsp"> Mail </a></td>
			<td><a href="/Storage/Admin/options.jsp"> Settings </a></td>
		</tr>
	</table>
	<br>
	<strong>Lots</strong>

	<c:if test="${! empty lots}">
		<table id="tabela">
			<tr id="tabela">
				<td id="tabela"><a class = "klasaProba" href = "/Storage/lot/sortByLotName">Lot name</a></td>
				<td id="tabela"><a class = "klasaProba" href = "/Storage/lot/sortByStoragePrice">Storage price</a></td>
				<td id="tabela"></td>
				<td id="tabela"></td>
			</tr>
			<c:forEach items="${lots}" var="l">
				<tr id="tabela">
					<td id="tabela">${l.lotName}</td>
					<td id="tabela">${l.storagePrice}</td>
					<td id="tabela"><a
						href="/Storage/lot/checkLot?lotId=${l.lotId}">Check lot</a></td>
					<td id="tabela"><a
						href="/Storage/lot/modifyRouter?lotId=${l.lotId}">Update </a></td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
	<br>
	<br>
	<hr>
	<br>
	<strong>Add lot</strong>

	<form action="/Storage/lot/saveLot" method="POST" >
		<table>
			<tr>
				<td>Lot name</td>
				<td><input type="text" name="lotName"></td>
				<td style = "color:red;"> ${lotNameErrorMsg} </td>
			</tr>
			<tr>
				<td>Number of storage units</td>
				<td><input type="text" name="numberOfStorageUnits"></td>
				<td style = "color:red;"> ${numberOfStorageUnitsErrorMsg} </td>
			</tr>
			<tr>
				<td>Lot storage price</td>
				<td><input type="text" name="storagePrice"></td>
				<td style = "color:red;"> ${storagePriceErrorMsg} </td>
			</tr>
		</table>

		<input type="Submit" value="Save lot">
	</form>
	<p style = "color:red;"> ${saveLotErrorMsg} </p>

	<br>
	<br>
	<hr>
	<br>

	<strong> Categories </strong>

	<c:if test="${! empty categories}">
		<table id="tabela">
			<tr>
				<td id="tabela"><a class = "klasaProba" href = "/Storage/category/sortByCatName">Category name</a></td>
				<td id="tabela"><a class = "klasaProba" href = "/Storage/category/sortByCatMultiplicator">Multiplicator</a></td>
				<td id="tabela"></td>
			</tr>
			<c:forEach items="${categories}" var="c">
				<tr id="tabela">
					<td id="tabela">${c.catName}</td>
					<td id="tabela">${c.catMultiplicator}</td>
					<td id="tabela"><a
						href="/Storage/category/modifyRouter?catId=${c.catId}"> Update
					</a>
				</tr>
			</c:forEach>
		</table>
	</c:if>

	<br>
	<br>
	<hr>
	<br>Add category


	<form action="/Storage/category/saveCategory" method="POST">
		<table>
			<tr>
				<td>Category name</td>
				<td><input type="text" name="catName"></td>
				<td style = "color:red;"> ${catNameErrorMsg} </td>
			</tr>
			<tr>
				<td>Multiplicator</td>
				<td><input type="text" name="catMultiplicator"></td>
				<td style = "color:red;"> ${catMultiplicatorErrorMsg} </td>
			</tr>
		</table>

		<input type="Submit" value="Save category">
	</form>

</body>
</html>