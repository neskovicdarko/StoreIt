<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="/Storage/Resources/Css/mainCss.css">
<meta charset="UTF-8">
<title>Home</title>
<link rel = "icon" href =  
"/Storage/Resources/Images/box.png" 
        type = "image/x-icon"> 
</head>
<body>
	<h2>User homepage for ${user.firstName}, ${user.lastName}</h2>
	<table>
		<tr>
			<td><a href="/Storage/user/logout">Log out</a></td>
			<td><a href="/Storage/User/mail.jsp"> Mail </a></td>
			<td><a href="/Storage/User/options.jsp"> Settings </a></td>
		</tr>
	</table>
	<br>
	<hr>
	<br> Your reservations:

	<c:if test="${! empty reservations}">
		<table id="tabela">
			<tr id="tabela">
				<td id="tabela">Lot name</td>
				<td id="tabela">Storage number</td>
				<td id="tabela"><a class = "klasaProba" href = "/Storage/reservation/sortByInDate">In date</a></td>
				<td id="tabela"><a class = "klasaProba" href = "/Storage/reservation/sortByOutDate">Out date</a></td>
				<td id="tabela"><a class = "klasaProba" href = "/Storage/reservation/sortByEmptiedDate">Emptied on</a></td>
				<td id="tabela"><a class = "klasaProba" href = "/Storage/reservation/sortByCategory">Category</a></td>
				<td id="tabela">Description</td>
				<td id="tabela"><a class = "klasaProba" href = "/Storage/reservation/sortByPricePerDay">Price per day</a></td>
				<td id="tabela">Status</td>
			</tr>
			<c:forEach items="${reservations }" var="r">
				<tr id="tabela">
					<td id="tabela">${r.lot.lotName}</td>
					<td id="tabela">${r.storageUnit.unitNum}</td>
					<td id="tabela">${r.inDate}</td>
					<td id="tabela">${r.outDate}</td>
					<td id="tabela"><c:if test="${empty r.acctualOutDate}">Still in storage</c:if>
						<c:if test="${!empty r.acctualOutDate}">${r.acctualOutDate}</c:if>
					</td>
					<td id="tabela">${r.category.catName}</td>
					<td id="tabela">${r.description}</td>
					<td id="tabela">${r.pricePerDay}</td>
					<td id="tabela"><c:if test="${! empty r.acctualOutDate}">Concluded</c:if>
						<c:if test="${empty r.acctualOutDate}">Active</c:if></td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
	<br>
	
	<table>
	<tr>
	<td>
	<a
		href="/Storage/reservation/getActiveReservationsForUser?userId=${user.userId}">Active</a>
	</td>
	<td>
	<a
		href="/Storage/reservation/getConcludedReservationsForUser?userId=${user.userId}">Concluded</a>
	</td>
	<td>
	<a
		href="/Storage/reservation/getAllReservationsForUser?userId=${user.userId}">
		All reservations </a>
	</td>
	</tr>
	</table>
	<hr>
	<br> Make reservation:

	<form action="/Storage/reservation/saveReservation" method="POST">
		<table>
			<tr>
				<td>Lot</td>
				<td><select name="lotId">
						<c:forEach items="${lots}" var="l">
							<option value="${l.lotId}">${l.lotName}</option>
						</c:forEach>
				</select> </td>
				<td style = "color:red;"> ${saveReservationMessage} </td>
			</tr>
			<tr>
				<td>Category</td>
				<td><select name="catId">
						<c:forEach items="${categories}" var="c">
							<option value="${c.catId}">${c.catName} (${c.catMultiplicator}$)</option>
						</c:forEach>
				</select></td>
			</tr>
			<tr>
				<td>Description</td>
				<td><input type="text" name="description"></td>
				<td style = "color:red;">${descriptionError}</td>
			</tr>
			<tr>
				<td>In date</td>
				<td><input type="Date" name="inDate"></td>
				<td style = "color:red;">${inDateError}</td>
			</tr>
			<tr>
				<td>Out date</td>
				<td><input type="Date" name="outDate"></td>
				<td style = "color:red;"> ${outDateError}</td>
			</tr>
		</table>
		<input type="Submit" value="Make reservation">
	</form>
</body>
</html>