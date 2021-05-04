<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="/Storage/Resources/Css/mainCss.css">
<meta charset="UTF-8">
<title>Lot info</title>
<link rel = "icon" href =  
"/Storage/Resources/Images/box.png" 
        type = "image/x-icon"> 
</head>
<body>
	<h1> Lot: ${lot.lotName} </h1>
	<br> Total number of lots: ${lot.numberOfStorageUnits}
	<br> Storage base price: ${lot.storagePrice}
	<br>
	<table>
	<tr>
	<td>
	<a href="/Storage/Admin/homepage.jsp">Home</a>
	</td>
	<td>
	<a href="/Storage/user/logout">Log out</a>
	</td>
	</tr>
	</table>
	<hr>
	<c:if test="${! empty activeFromDate}">
		<c:if test="${! empty activeToDate}">
			Reservations from ${activeFromDate} to ${activeToDate}. <br>

		</c:if>
	</c:if>

	<br>
	<a href="/Storage/lot/refreshReservationsForLot">Refresh
		</a>
	<br>

	<c:if test="${! empty reservations}">
		<table id= "tabela">
			<tr id= "tabela">
				<td id= "tabela">Description</td>
				<td id= "tabela">Storage unit</td>
				<td id= "tabela"><a class = "klasaProba" href = "/Storage/lot/sortByCategory">Category</a></td>
				<td id= "tabela"><a class = "klasaProba" href = "/Storage/lot/sortByInDate">In Date</a></td>
				<td id= "tabela"><a class = "klasaProba" href = "/Storage/lot/sortByOutDate">Out Date</a></td>
				<td id= "tabela"><a class = "klasaProba" href = "/Storage/lot/sortByOwner">Owner</a></td>
				<td id= "tabela"><a class = "klasaProba" href = "/Storage/lot/sortByPricePerDay">Price per day</a></td>
				<td id= "tabela"><a class = "klasaProba" href = "/Storage/lot/sortByStatus">Status</a></td>

			</tr>
			<c:forEach items="${reservations}" var="r">
				<tr id= "tabela">
					<td id= "tabela">${r.description }</td>
					<td id= "tabela">${r.storageUnit.unitNum}</td>
					<td id= "tabela">${r.category.catName }</td>
					<td id= "tabela">${r.inDate}</td>
					<td id= "tabela">${r.outDate}</td>
					<td id= "tabela">${r.user.firstName},${r.user.lastName}</td>
					<td id= "tabela">${r.pricePerDay}</td>
					<td id= "tabela"><c:if test="${!empty r.acctualOutDate}"> Unloaded </c:if>
						<c:if test="${empty r.acctualOutDate}">
							<a
								href="/Storage/reservation/unloadUnitGET?reservationId=${r.reservationId}">
								Unload unit </a>
						</c:if></td>
				</tr>
			</c:forEach>
		</table>
		<br>
		<table>
		<tr>
		<td>
		<a href = "/Storage/lot/getActiveReservations?lotId=${lot.lotId}">Active</a>
		</td>
		<td>
		<a href = "/Storage/lot/getConcludedReservations?lotId=${lot.lotId}">Concluded</a>
		</td>
		<td>
		<a href="/Storage/lot/checkLot?lotId=${lot.lotId}"> All reservations </a>
		</td>
		</tr>
		</table>
		
		<c:if test="${! empty messageAboutFreeUnits}">
			${messageAboutFreeUnits} <br>
		</c:if>
		<c:if test="${! empty messageAboutOccupiedUnits}">
			${messageAboutOccupiedUnits} <br>
			<br>
		</c:if>

	</c:if>
	<c:if test="${empty reservations }">
		There are no reservations for this lot.  
	</c:if>

	<hr>
	<br> <strong> Browse reservations </strong>

	<form action="/Storage/lot/browseLots" method="POST">
		<table>
			<tr>
				<td>From</td>
				<td><input type="date" name="fromDate"></td>
			</tr>
			<tr>
				<td>To</td>
				<td><input type="date" name="toDate"></td>
			</tr>
			<tr>
				<td>Keyword(s):</td>
				<td><input type="text" name="kwds"></td>
			</tr>
		</table>
		<br>
		<input type="Submit" value="Search">
	</form>
	<br>
	
	<br> <i> If you enter "from" date and "to" date you will be given
	information about how many storage units are free at that period. </i>
</body>
</html>